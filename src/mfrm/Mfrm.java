package mfrm;

import java.util.Arrays;
import java.util.Random;

import org.uncommons.maths.random.GaussianGenerator;

import utility.Data;

public class Mfrm {
	public int I, J, R, K;
	public double[] beta_i, beta_r, rho_k, theta;

	public double[] betaI_prior = { 0.0, 1.0 };
	public double[] betaR_prior = { 0.0, 1.0 };
	public double[] rhoK_prior = { 0.0, 1.0 };
	public double[] theta_prior = { 0.0, 1.0 };

	protected Random rand;
	protected Data data;

	public Mfrm(int item, int examinee, int rater, int categories, Random rand) {
		this.I = item;
		this.J = examinee;
		this.R = rater;
		this.K = categories;

		beta_i = new double[I];
		beta_r = new double[R];
		rho_k = new double[K];
		theta = new double[J];

		beta_r[0] = betaR_prior[0];
		rho_k[0] = 0.0;

		this.rand = rand;
		this.data = new Data(this.I, this.J, this.R, rand);
	}

	public void setRandomParameters() {
		setInitParam(theta, theta_prior, null);
		setInitParam(beta_i, betaI_prior, null);
		setInitParam(beta_r, betaR_prior, null);
		setItem_Rho_K(null);
		rho_k[0] = 0.0;
	}

	public void setConstraint() {
		beta_r[0] = betaR_prior[0];
	}

	public void setInitParam(double[] param, double[] prior, double[] defParam) {
		if (defParam != null) {
			param = defParam.clone();
		} else {
			GaussianGenerator gen = new GaussianGenerator(prior[0], prior[1], rand);
			for (int i = 0; i < param.length; i++) {
				param[i] = gen.nextValue();
			}
		}
	}

	public void setItem_Rho_K(double[] param) {
		double sumB = 0.0;
		double[] bHyp = new double[K - 1];
		for (int k = 1; k < K; k++) {
			GaussianGenerator Tau_gen = new GaussianGenerator(rhoK_prior[0], rhoK_prior[1], rand);
			bHyp[k - 1] = Tau_gen.nextValue();
			sumB += bHyp[k - 1];
		}
		Arrays.sort(bHyp);
		sumB = sumB / (K - 1.0);
		for (int k = 1; k < K; k++) {
			rho_k[k] = bHyp[k - 1] - sumB;
		}
		if (param != null) {
			rho_k = param;
		}
	}

	public double[] IRC(int i, double theta, int r) {
		double[] IRC = new double[K];
		double[] Zij = new double[K];
		double Z = 0;
		double Z_tmp = 0;
		for (int k = 0; k < K; k++) {
			Z_tmp += logit(i, theta, r, k);
			Zij[k] = Math.exp(Z_tmp);
			Z += Zij[k];
		}
		for (int k = 0; k < K; k++) {
			IRC[k] = Zij[k] / Z;
		}
		return IRC;
	}

	double logit(int i, double theta, int r, int k) {
		return theta - beta_i[i] - beta_r[r] - rho_k[k];
	}

	public double getProb(int j, int i, int r) {
		int k = this.data.U[j][i][r];
		if (k != -1) {
			return IRC(i, theta[j], r)[k];
		}
		return 1.0;
	}

	double getLogLikelihood() {
		double LogLikelihood = 0.0;
		for (int j = 0; j < J; j++) {
			LogLikelihood += LogLikelihoodTheta(j);
		}
		return LogLikelihood;
	}

	double LogLikelihoodTheta(int j) {
		double LogLikelihood = 0.0;
		for (int i = 0; i < I; i++) {
			for (int r = 0; r < R; r++) {
				LogLikelihood += Math.log(getProb(j, i, r));
			}
		}
		return LogLikelihood;
	}

	double LogLikelihoodItem(int i) {
		double LogLikelihood = 0.0;
		for (int j = 0; j < J; j++) {
			for (int r = 0; r < R; r++) {
				LogLikelihood += Math.log(getProb(j, i, r));
			}
		}
		return LogLikelihood;
	}

	public double LogLikelihoodRater(int r) {
		double LogLikelihood = 0.0;
		for (int j = 0; j < J; j++) {
			for (int i = 0; i < I; i++) {
				LogLikelihood += Math.log(getProb(j, i, r));
			}
		}
		return LogLikelihood;
	}

	// data binding
	public void init_data() {
		this.data = new Data(this.I, this.J, this.R, rand);
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				for (int r = 0; r < R; r++) {
					this.data.set_data(i, j, r, IRC(i, theta[j], r));
				}
			}
		}
	}

	public void setData(Data d) {
		this.data = new Data(d, rand);
	}

	public Data getData() {
		return this.data;
	}
}
