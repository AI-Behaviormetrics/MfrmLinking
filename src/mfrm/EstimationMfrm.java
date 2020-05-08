package mfrm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.uncommons.maths.random.GaussianGenerator;

import utility.MyUtil;

public class EstimationMfrm {
	protected Mfrm irt;
	protected Random rand;
	protected int MaxMCMC, burnIn, interbal;

	protected final double prop_sd = 0.1;
	private GaussianGenerator gg;
	private int stR, stI;

	public ArrayList<double[]> betaI_smpl, betaR_smpl, rhoK_smpl, theta_smpl;

	public EstimationMfrm(Mfrm irt, Random rand, HashMap<String, Integer> settings, int stR, int stI) {
		this.irt = irt;
		this.stR = stR;
		this.stI = stI;
		this.rand = rand;
		this.MaxMCMC = settings.get("MaxMCMC");
		this.burnIn = settings.get("burnIn");
		this.interbal = settings.get("interbal");
		gg = new GaussianGenerator(0.0, prop_sd, rand);

		init();
		for (int i = 0; i < this.MaxMCMC; i++) {
			if ((i % (MaxMCMC / 10)) == 0)
				System.out.println(" >> roop = " + i);
			updateThetaParam();
			updateItem_Beta_I();
			updateRater_Beta_R();
			updateItem_Rho_K();
			this.addMCMCSamples();
		}
		calc_eap();
	}

	protected void init() {
		betaI_smpl = new ArrayList<double[]>();
		betaR_smpl = new ArrayList<double[]>();
		rhoK_smpl = new ArrayList<double[]>();
		theta_smpl = new ArrayList<double[]>();
	}

	protected void addMCMCSamples() {
		betaR_smpl.add(irt.beta_r.clone());
		betaI_smpl.add(irt.beta_i.clone());
		theta_smpl.add(irt.theta.clone());
		rhoK_smpl.add(irt.rho_k.clone());
	}

	protected void calc_eap() {
		irt.beta_i = MyUtil.get_averages(betaI_smpl, burnIn, interbal);
		irt.beta_r = MyUtil.get_averages(betaR_smpl, burnIn, interbal);
		irt.rho_k = MyUtil.get_averages(rhoK_smpl, burnIn, interbal);
		irt.theta = MyUtil.get_averages(theta_smpl, burnIn, interbal);
	}

	public void updateItem_Beta_I() {
		for (int i = stI; i < irt.I; i++) {
			double prevBeta = irt.beta_i[i];
			double prevLikelihood = irt.LogLikelihoodItem(i)
					+ Math.log(MyUtil.gaussian_value(irt.betaI_prior[0], irt.betaI_prior[1], irt.beta_i[i]));
			irt.beta_i[i] += gg.nextValue();
			double newLikelihood = irt.LogLikelihoodItem(i)
					+ Math.log(MyUtil.gaussian_value(irt.betaI_prior[0], irt.betaI_prior[1], irt.beta_i[i]));
			double thresh = Math.exp(newLikelihood - prevLikelihood);
			if (MyUtil.isRejected(thresh, rand) == true) {
				irt.beta_i[i] = prevBeta;
			}
		}
	}

	public void updateItem_Rho_K() {
		double[] prevRhoK = irt.rho_k.clone();
		double sum = 0.0;
		double prevLikelihood = irt.getLogLikelihood();
		for (int k = 1; k < irt.K; k++) {
			prevLikelihood += Math.log(MyUtil.gaussian_value(irt.rhoK_prior[0], irt.rhoK_prior[1], irt.rho_k[k]));
			irt.rho_k[k] += gg.nextValue();
			sum += irt.rho_k[k];
		}
		sum = sum / (irt.K - 1.0);
		double newLikelihood = 0.0;
		for (int k = 1; k < irt.K; k++) {
			irt.rho_k[k] = irt.rho_k[k] - sum;
			newLikelihood += Math.log(MyUtil.gaussian_value(irt.rhoK_prior[0], irt.rhoK_prior[1], irt.rho_k[k]));
		}
		newLikelihood += irt.getLogLikelihood();
		double thresh = Math.exp(newLikelihood - prevLikelihood);
		if (MyUtil.isRejected(thresh, rand) == true) {
			irt.rho_k = prevRhoK.clone();
		}
		prevRhoK = null;
	}

	protected void updateRater_Beta_R() {
		for (int r = stR; r < irt.R; r++) {
			double prevBetaR = irt.beta_r[r];
			double prevLikelihood = irt.LogLikelihoodRater(r)
					+ Math.log(MyUtil.gaussian_value(irt.betaR_prior[0], irt.betaR_prior[1], irt.beta_r[r]));
			irt.beta_r[r] += gg.nextValue();
			double newLikelihood = irt.LogLikelihoodRater(r)
					+ Math.log(MyUtil.gaussian_value(irt.betaR_prior[0], irt.betaR_prior[1], irt.beta_r[r]));
			double thresh = Math.exp(newLikelihood - prevLikelihood);
			if (MyUtil.isRejected(thresh, rand) == true) {
				irt.beta_r[r] = prevBetaR;
			}
		}
	}

	public void updateThetaParam() {
		for (int j = 0; j < irt.J; j++) {
			double prevLikelihood = irt.LogLikelihoodTheta(j)
					+ Math.log(MyUtil.gaussian_value(irt.theta_prior[0], irt.theta_prior[1], irt.theta[j]));
			double prevTheta = irt.theta[j];
			irt.theta[j] += gg.nextValue();
			double newLikelihood = irt.LogLikelihoodTheta(j)
					+ Math.log(MyUtil.gaussian_value(irt.theta_prior[0], irt.theta_prior[1], irt.theta[j]));
			double thresh = Math.exp(newLikelihood - prevLikelihood);
			if (MyUtil.isRejected(thresh, rand) == true) {
				irt.theta[j] = prevTheta;
			}
		}
	}
}
