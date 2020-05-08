package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import mfrm.SimulationMfrm;
import utility.MyUtil;

public class MainMfrmLinking {

	private static class ICallable implements Callable<Integer> {
		private SimulationMfrm ct;
		public ICallable(SimulationMfrm ct) {
			this.ct = ct;
		}
		@Override
		public Integer call() throws Exception {
			ct.run();
			return 0;
		}
	}

	public static void main(String[] args) throws IOException {
		final int[] roop_NCR = { 1, 2, 3, 4, 5 };
		final int[] roop_NCI = { 1, 2, 3, 4, 5 };

		List<Callable<Integer>> tasks = new ArrayList<Callable<Integer>>();
		int I = 10; int R = 10; int J = 100;
		for (int nci = 0; nci < roop_NCI.length; nci++) {
			for (int ncr = 0; ncr < roop_NCR.length; ncr++) {
				// experiment 1
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], -1, -1)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], 0, -1)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], 1, -1)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], 2, -1)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], 3, -1)));
				// experiment 3
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], -1, 2)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], 0, 2)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], -1, 3)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR[ncr], 0, 3)));

			}
		}

		final int[][] roop =  { {5, 50, 5}, {5, 100, 5}, {10, 100, 5}, {5, 100, 10} };
		for (int n = 0; n < roop.length; n++) {
			for (int nci = 0; nci < roop_NCI.length; nci++) {
				for (int ncr = 0; ncr < roop_NCR.length; ncr++) {
					// experiment 2
					tasks.add(new ICallable(new SimulationMfrm(roop[n][0], roop[n][1], roop[n][2], roop_NCI[nci], roop_NCR[ncr], -1, -1)));
					tasks.add(new ICallable(new SimulationMfrm(roop[n][0], roop[n][1], roop[n][2], roop_NCI[nci], roop_NCR[ncr], 0, -1)));
					// experiment 3
					if(roop[n][0] == 10 && roop[n][1] == 100 && roop[n][2] == 5) {
						tasks.add(new ICallable(new SimulationMfrm(roop[n][0], roop[n][1], roop[n][2], roop_NCI[nci], roop_NCR[ncr], -1, 2)));
						tasks.add(new ICallable(new SimulationMfrm(roop[n][0], roop[n][1], roop[n][2], roop_NCI[nci], roop_NCR[ncr], 0, 2)));						
					}
				}
			}			
		}

		// large scale
		I = 5; R = 20; J = 1000;
		final int[] roop_NCR2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		for (int nci = 0; nci < roop_NCI.length; nci++) {
			for (int ncr = 0; ncr < roop_NCR2.length; ncr++) {
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR2[ncr], -1, 2)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR2[ncr], 0, 2)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR2[ncr], -1, 4)));
				tasks.add(new ICallable(new SimulationMfrm(I, J, R, roop_NCI[nci], roop_NCR2[ncr], 0, 4)));
			}
		}
		MyUtil.startThread(tasks, 32);				
	}
}
