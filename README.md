This package includes programs for examining MFRM-based performance test linking accuracy. These programs are written in Java.

Run “/main/MainMfrmLinking.java” to replicate our experiments. Before running the code, set the number of threads specified in the “MyUtil.startThread(tasks, 32)” line depending on your environment. Note that these programs take a considerably long time to complete.

If you want to examine a specific setting, run the following code:
```Java:
final int[] roop_NCR = { 1, 2, 3, 4, 5 };
final int[] roop_NCI = { 1, 2, 3, 4, 5 };
for (int nci = 0; nci < roop_NCI.length; nci++) {
	for (int ncr = 0; ncr < roop_NCR.length; ncr++) {
		new SimulationMFRM(I, J, R, roop_NCI[nci], roop_NCR[ncr], -1, N_R)
		new SimulationMFRM(I, J, R, roop_NCI[nci], roop_NCR[ncr], Dist, N_R)
	}
}
```

Here, I, J, and R respectively set the numbers of tasks, examinees, and raters. “Dist” sets the parameter distribution used for the new test (0: distribution 1, 1: distribution 2, 2: distribution 3, 3: distribution 4). N_R is the number of raters allocated to each performance following the judge set design (use -1 to simulate a no-missing-data setting).

The results will be output to the “out/current/normal/” directory. The files in the “Dist-1” folder give average bias values for the model parameters used to calculate the threshold values “delta.” Files in the other folders give the average bias values for each parameter in specific settings, which are used to calculate average RMSEs. 

Run “/main/ResultSummarizer.java” to calculate the average RMSEs and the corresponding threshold value “delta” from the files. Change the values for “I,” “J,” “R,” “N_R,” and “Dist” in the program to obtain an arbitrary setting. The program outputs the results to the console.

To simulate situations where temporal changes in rater and task parameters exist, run the programs after changing a "noisy" variable to "true". The results will be output to the “out/current/noisy/” directory.  
The “/out/paper_results” folder includes the raw data obtained from the simulation experiments in the paper. The data format is the same as that in the “out/current/” folder. To obtain summarized results based on the data, run “/main/ResultSummarizer.java” after performing a “dir” command to change from the “out/current/” to the “out/paper_results” directory.
