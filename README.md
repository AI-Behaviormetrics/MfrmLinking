This package includes programs to examine the MFRM-based performance test linking accuracy. The codes are written in Java.

Run "/main/MainMfrmLinking.java" to replicate our experiments. Before running the code, change the number of the threads given in "MyUtil.startThread(tasks, 32);" depending on your environment. Note that these programs take a considerably long time to complete. 

If you want to examine a specific setting, run the following code. 

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

Here, I, J, R indicate the numbers of tasks, examinees, and raters. "Dist" indicates the parameter distribution used for the new test (0:Distribution1, 1:Distribution2, 2:Distribution3, 3:Distribution4). N_R is the number of raters allocated to each performance following the judge set design (give -1 to simulate a no-missing setting).

The results will be shown in "out/current/" directory. The files in "Dist-1" folder give the absolute error values for model parameters which are used to calculate the threshold values "delta". The files in the other folders give the absolute error values for each parameter in specific settings, which are used to calculate the average RMSEs. 

Run "/main/ResultSummarizer.java" to calculate the average RMSEs and the corresponding threshold value "delta" from the files. Change the values for "I", "J", "R", "N_R", "Dist" in the program to obtain an arbitrary setting. The program outputs the results to the console. 

The "/out/paper_results" folder includes the raw data which were obtained from the simulation experiments in the paper. The data format is the same as in the "out" folder. If you want to obtain the summarized results based on the data, run "/main/ResultSummarizer.java" after "dir" statement changes from "out/current/" to "out/paper_results". 