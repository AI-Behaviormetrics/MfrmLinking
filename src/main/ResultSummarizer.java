package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import utility.MyUtil;

public class ResultSummarizer {
	final int[] roop_NCI = { 1, 2, 3, 4, 5 };

	String dir = "out/current/";
	private final int max_loop = 30;

	ResultSummarizer(int I, int J, int R, int dist, int N_R) throws IOException {
		int[] roop_NCR = { 1, 2, 3, 4, 5 };
		if (J == 1000) {
			roop_NCR = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };			
		}
		int digit = 4;
		String setting = "I" + I + "_J" + J + "_R" + R;
		System.out.println(",CI=1,CI=2,CI=3,CI=4,CI=5");
		for (int ncr = 0; ncr < roop_NCR.length; ncr++) {
			String line = roop_NCR[ncr] + ", ";
			for (int nci = 0; nci < roop_NCI.length; nci++) {
				String filename = "/JudgePair" + N_R + "/NCI" + roop_NCI[nci] + "_NCR" + roop_NCR[ncr] + ".csv";
				double[] for_delta = calculate_mae(dir + "Dist-1/" + setting + filename);
				double[] for_newtest = calculate_mae(dir + "Dist" + dist + "/" + setting + filename);
				double target = for_newtest[0];
				double delta = for_delta[0] + 2 * for_delta[1];
				if (target < delta) {
					line += "{¥bf " + MyUtil.form(target, digit) + "}(" + MyUtil.form(delta, digit) + ")";
				} else {
					line += MyUtil.form(target, digit) + "(" + MyUtil.form(delta, digit) + ")";
				}
				if (nci != roop_NCI.length - 1) {
					line += ",";					
				}
			}
			System.out.println(line);
		}
	}

	double[] calculate_mae(String filename) throws IOException {
		double[] loop_rmse = new double[max_loop];
		int parameter_num = 0;

		BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
		String str = br.readLine();
		while (str != null) {
			String[] mae_for_a_parameter = str.split(",");
			int loop_idx = Integer.valueOf(mae_for_a_parameter[0].split("-")[0].replace("Loop", ""));
			if (loop_idx == 0)
				parameter_num++;
			double mae_value = Double.valueOf(mae_for_a_parameter[1]);
			loop_rmse[loop_idx] += Math.pow(mae_value, 2);
			str = br.readLine();
		}
		br.close();

		// rmse_mean, rmse_sd
		double[] results = new double[2];
		for (int n = 0; n < max_loop; n++) {
			loop_rmse[n] = Math.sqrt(loop_rmse[n] / parameter_num);
			results[0] += loop_rmse[n] / max_loop;
		}
		for (int n = 0; n < max_loop; n++) {
			results[1] += Math.pow(results[0] - loop_rmse[n], 2) / max_loop;
		}
		results[1] = Math.sqrt(results[1]);
		return results;
	}

	public static void main(String[] args) throws IOException {

		// Replicate Experiment1(different distributions)
		System.out.println("expriment1,distribution1");
		new ResultSummarizer(10, 100, 10, 0, -1);
		System.out.println("expriment1,distribution2");
		new ResultSummarizer(10, 100, 10, 1, -1);
		System.out.println("expriment1,distribution3");
		new ResultSummarizer(10, 100, 10, 2, -1);
		System.out.println("expriment1,distribution4");
		new ResultSummarizer(10, 100, 10, 3, -1);

		// Replicate Experiment2(different data sizes)
		System.out.println("expriment2,I5,J50,R5");
		new ResultSummarizer(5, 50, 5, 0, -1);
		System.out.println("expriment2,I5,J100,R5");
		new ResultSummarizer(5, 100, 5, 0, -1);
		System.out.println("expriment2,I5,J100,R10");
		new ResultSummarizer(5, 100, 10, 0, -1);
		System.out.println("expriment2,I10,J100,R5");
		new ResultSummarizer(10, 100, 5, 0, -1);

		// Replicate Experiment3(with missing data)
		System.out.println("expriment3,60% missing");
		new ResultSummarizer(10, 100, 5, 0, 2);
		System.out.println("expriment3,70% missing");
		new ResultSummarizer(10, 100, 10, 0, 3);
		System.out.println("expriment3,80% missing");
		new ResultSummarizer(10, 100, 10, 0, 2);

		// Replicate Experiment4(large scale)
		System.out.println("expriment4,90% missing");
		new ResultSummarizer(5, 1000, 20, 0, 2);
		System.out.println("expriment4,80% missing");
		new ResultSummarizer(5, 1000, 20, 0, 4);
	}
}
