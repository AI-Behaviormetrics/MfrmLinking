package utility;

import java.util.ArrayList;
import java.util.Collections;

public class JudgePair {
	private int[][][] Z;
	private int I, J, R;

	public JudgePair(int I, int J, int R) {
		this.I = I;
		this.J = J;
		this.R = R;
		Z = new int[J][I][R];
	}

	public int[][][] getRaterPair(int NumberOfRaterPerAns) {
		ArrayList<Integer> candRaters = new ArrayList<Integer>();
		for (int r = 0; r < R; r++)
			candRaters.add(r);
		ArrayList<ArrayList<Integer>> raterSets = getSubset(candRaters, NumberOfRaterPerAns);
		Collections.shuffle(raterSets);

		int index = 0;
		for (int i = 0; i < I; i++) {
			for (int j = 0; j < J; j++) {
				ArrayList<Integer> raterSet = raterSets.get(index);
				for (int r : raterSet) {
					Z[j][i][r] = 1;
				}
				index++;
				if (raterSets.size() == index) {
					index = 0;
					Collections.shuffle(raterSets);
				}
			}
		}
		return Z;
	}

	public static ArrayList<ArrayList<Integer>> getSubset(ArrayList<Integer> S, int order) {
		ArrayList<ArrayList<Integer>> subset = new ArrayList<ArrayList<Integer>>();
		int l = S.size();
		if (l >= order) {
			if (order == 1) {
				for (int i = 0; i < l; i++) {
					ArrayList<Integer> array = new ArrayList<Integer>();
					array.add(S.get(i));
					subset.add(array);
				}
			} else {
				for (int i = 0; i < l; i++) {
					ArrayList<Integer> cs = new ArrayList<Integer>();
					for (int j = i + 1; j < l; j++) {
						cs.add(S.get(j));
					}
					ArrayList<ArrayList<Integer>> ss1 = getSubset(cs, order - 1);
					for (int j = 0; j < ss1.size(); j++) {
						ArrayList<Integer> array = new ArrayList<Integer>();
						array.add(S.get(i));
						for (int m = 0; m < ss1.get(j).size(); m++) {
							array.add(ss1.get(j).get(m));
						}
						subset.add(array);
					}
				}
			}
		}
		return subset;
	}
}
