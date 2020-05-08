package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MyUtil {

	public static double gaussian_value(double m, double s, double v) {
		double ee = -Math.pow(v - m, 2) / (2 * s * s);
		return Math.exp(ee) / (Math.sqrt(2 * Math.PI) * s);
	}

	public static double[] get_averages(ArrayList<double[]> array, int start, int thining) {
		int l = array.get(0).length;
		double[] ave = new double[l];
		int count = 0;
		for (int i = start; i < array.size(); i++) {
			if (i % thining == 0) {
				for (int j = 0; j < l; j++) {
					ave[j] += array.get(i)[j];
				}
				count++;
			}
		}
		for (int j = 0; j < l; j++) {
			ave[j] /= count;
		}
		return ave;
	}
	
	public static double[][] get_averages_matrix(ArrayList<double[][]> array, int start, int thining) {
		int l = array.get(0).length;
		int c = array.get(0)[0].length;

		double[][] ave = new double[l][c];
		int count = 0;
		for (int i = start; i < array.size(); i++) {
			if(i % thining == 0){
				for (int j = 0; j < l; j++) {
					for (int k = 0; k < c; k++) {
						ave[j][k] += array.get(i)[j][k];
					}
				}
				count++;
			}
		}

		for (int j = 0; j < l; j++) {
			for (int k = 0; k < c; k++) {
				ave[j][k] /= count;
			}
		}
		return ave;
	}

	public static double[] get_mae(double[] ave, double[] true_value) {
		int l = ave.length;
		double[] mae = new double[l];
		for (int j = 0; j < l; j++) {
			mae[j] += Math.abs(ave[j] - true_value[j]);
		}
		return mae;
	}

	public static void makeDir(String dataDir) {
		File f = new File(dataDir);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	public static boolean isRejected(double thresh, Random rand) {
		if (Double.isNaN(thresh))
			return true;
		if (thresh < 1.0) {
			double t = rand.nextDouble();
			if (t > thresh) {
				return true;
			}
		}
		return false;
	}

	public static PrintWriter Writer(String fileName) throws IOException {
		return new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
	}

	public static String form(double d, int length) {
		DecimalFormat df = new DecimalFormat();
		df.applyPattern("0");
		df.setMaximumFractionDigits(length);
		df.setMinimumFractionDigits(length);
		Double objnum = new Double(d);
		return df.format(objnum);
	}

	public static void startThread(List<Callable<Integer>> tasks, int threadNum) {
		ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
		try {
			List<Future<Integer>> futures = executorService.invokeAll(tasks);
			for (Future<Integer> future : futures) {
				try {
					future.get();
				} catch (ExecutionException ex) {
					ex.printStackTrace();
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		executorService.shutdown();
	}
}
