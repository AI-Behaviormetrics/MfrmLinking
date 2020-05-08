package utility;

import java.util.Random;

public class Data {
	public int I, J, R;
	public int[][][] U;
	private Random rand;

	public Data(int I, int J, int R, Random rand) {
		this.rand = rand;
		this.I = I;
		this.J = J;
		this.R = R;
		this.U = new int[this.J][this.I][this.R];
	}

	public Data(Data data, Random rand) {
		this.rand = rand;
		this.I = data.I;
		this.J = data.J;
		this.R = data.R;
		this.U = new int[this.J][this.I][this.R];
		for (int j = 0; j < this.J; j++) {
			for (int i = 0; i < this.I; i++) {
				for (int r = 0; r < this.R; r++) {
					this.U[j][i][r] = data.U[j][i][r];
				}
			}
		}
	}

	public void set_data(int i, int j, int r, double[] irc) {
		double[] cumulative_irc = getCumulativeProb(irc);
		this.U[j][i][r] = random_choice(cumulative_irc);
	}

	public double[] getCumulativeProb(double[] IRC) {
		double[] cumulativeIRC = new double[IRC.length];
		cumulativeIRC[0] = IRC[0];
		for (int i = 1; i < IRC.length; i++) {
			cumulativeIRC[i] = cumulativeIRC[i - 1] + IRC[i];
		}
		return cumulativeIRC;
	}

	public int random_choice(double[] cumulative_irc) {
		int cluster = 0;
		double t = rand.nextDouble();
		for (int k = 0; k < cumulative_irc.length; k++) {
			if (t < cumulative_irc[k]) {
				cluster = k;
				break;
			}
		}
		return cluster;
	}
}
