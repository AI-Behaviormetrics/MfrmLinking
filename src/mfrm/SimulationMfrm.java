package mfrm;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;

import utility.Data;
import utility.JudgePair;
import utility.MTRandom;
import utility.MyUtil;

public class SimulationMfrm {
	private Random rand;
	private HashMap<String, Integer> mcmcSettings;
	private final int K = 5, Loop = 30;
	private int I, J, R, NCI, NCR, FR, dist, jpnum;
	private boolean noisy;

	public SimulationMfrm(int I, int J, int R, int NCI, int NCR, int dist, int jpnum, boolean noisy) {
		this.I = I;
		this.J = J;
		this.R = R;
		this.NCI = NCI;
		this.NCR = NCR;
		this.dist = dist;
		this.jpnum = jpnum;
		this.FR = NCR;// fixed rater parameters
		this.noisy = noisy;

		rand = new MTRandom();
		mcmcSettings = new HashMap<String, Integer>();
		mcmcSettings.put("MaxMCMC", 30000);
		mcmcSettings.put("burnIn", 15000);
		mcmcSettings.put("interbal", 100);
	}

	public void run() {
		try {
			String dir = "out/current";
			if(noisy) dir += "/noisy";
			else dir += "/normal";
			MyUtil.makeDir(dir+"/Dist" + dist + "/I" + I + "_J" + J + "_R" + R + "/JudgePair" + jpnum);
			PrintWriter pw = MyUtil.Writer(dir+"/Dist" + dist + "/I" + I + "_J" + J + "_R" + R + "/JudgePair"
					+ jpnum + "/NCI" + NCI + "_NCR" + NCR + ".csv");
			for (int loop = 0; loop < Loop; loop++) {
				// generate the base test
				Mfrm baseModel = new Mfrm(I, J, R, K, rand);
				baseModel.setRandomParameters();
				baseModel.setConstraint();

				// generate the new test
				Mfrm trueModel = new Mfrm(I, J, R, K, rand);
				setDist(trueModel);
				trueModel.setRandomParameters();

				// set the parameters values for common raters and tasks
				for (int i = 0; i < NCI; i++) {
					trueModel.beta_i[i] = baseModel.beta_i[i];
					if(noisy) {
						if(i%2 == 0) {
							trueModel.beta_i[i] += rand.nextGaussian() * 0.05;
						}
					}
				}
				for (int r = 0; r < NCR; r++) {
					trueModel.beta_r[r] = baseModel.beta_r[r];
					if(noisy) {
						if(r%2 == 1) {
							trueModel.beta_r[r] += rand.nextGaussian() * 0.10;
						}
					}
				}

				// generate data for the new test
				trueModel.init_data();
				if (jpnum != -1)
					applyJudgePairDesign(I, J, R, jpnum, trueModel.getData());

				// estimate the parameters for the new test from the generated data
				Mfrm estModel = new Mfrm(I, J, R, K, rand);
				estModel.setData(trueModel.getData());
				estModel.setRandomParameters();
				for (int i = 0; i < NCI; i++)
					estModel.beta_i[i] = baseModel.beta_i[i];
				for (int r = 0; r < NCR; r++)
					estModel.beta_r[r] = baseModel.beta_r[r];
				new EstimationMfrm(estModel, rand, mcmcSettings, FR, NCI);

				// output the results
				print_mae_except_fixed_params(NCI, MyUtil.get_bias(trueModel.beta_i, estModel.beta_i),
						("Loop" + loop + "-beta_i"), pw);
				print_mae_except_fixed_params(FR, MyUtil.get_bias(trueModel.beta_r, estModel.beta_r),
						("Loop" + loop + "-beta_r"), pw);
				print_mae_except_fixed_params(1, MyUtil.get_bias(trueModel.rho_k, estModel.rho_k),
						("Loop" + loop + "-d_k"), pw);
				print_mae_except_fixed_params(0, MyUtil.get_bias(trueModel.theta, estModel.theta),
						("Loop" + loop + "-theta"), pw);
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void applyJudgePairDesign(int I, int J, int R, int parenum, Data data) {
		JudgePair JP = new JudgePair(I, J, R);
		int[][][] Z = JP.getRaterPair(parenum);
		for (int j = 0; j < J; j++) {
			for (int i = 0; i < I; i++) {
				for (int r = 0; r < R; r++) {
					if (Z[j][i][r] == 0) {
						data.U[j][i][r] = -1;
					}
				}
			}
		}
	}

	private void print_mae_except_fixed_params(int st, double[] val, String label, PrintWriter pw) {
		for (int i = st; i < val.length; i++) {
			pw.println(label + i + "," + val[i]);
		}
	}

	private void setDist(Mfrm model) {
		if (dist == 0) {
			model.theta_prior = new double[] { -0.5, 1.0 };
		} else if (dist == 1) {
			model.theta_prior = new double[] { -0.2, 1.0 };
		} else if (dist == 2) {
			model.theta_prior = new double[] { -0.5, 1.0 };
			model.betaI_prior = new double[] { 0.5, 1.0 };
		} else if (dist == 3) {
			model.theta_prior = new double[] { -0.5, 1.0 };
			model.betaR_prior = new double[] { 0.5, 1.0 };
		}
	}
}
