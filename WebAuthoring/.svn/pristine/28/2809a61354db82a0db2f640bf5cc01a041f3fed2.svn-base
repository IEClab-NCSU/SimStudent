import java.util.ArrayList;
import java.util.HashMap;

public class tracerModel {

	public static ArrayList<HashMap<String, ArrayList<Integer>>> studyData = new ArrayList<HashMap<String,ArrayList<Integer>>>();
	public static ArrayList<String> studentIndex = new ArrayList<String>();
	public static HashMap<String,ArrayList<Double>> averages = new HashMap<String, ArrayList<Double>>();
	public static HashMap<String,ArrayList<Double>> sizes = new HashMap<String, ArrayList<Double>>();
	public static HashMap<String, Double> L0 = new HashMap<String,Double>();
	public tracerModel() {
		// TODO Auto-generated constructor stub
	}
	
	public static void printTrials(int i, String skill) {
		for (int o = 0; o < studyData.get(i).get(skill).size(); o++) {
			if (o!=studyData.get(i).get(skill).size()-1)	System.out.print(studyData.get(i).get(skill).get(o) + " ");
			else System.out.print(studyData.get(i).get(skill).get(o));
		}
	}
	
	public static int sumArr(ArrayList<Integer> arr) {
		int sum = 0;
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).equals(1)) sum++;
		}
		return sum;
	}
	
	public static Double sumDub(ArrayList<Double> arr) {
		Double sum = 0.0;
		for (int i = 0; i < arr.size(); i++) {
			sum += arr.get(i);
		}
		return sum;
	}
	
	public static Double sumWeightedDub(ArrayList<Double> arr1, ArrayList<Double> arr2) {
		Double sum = 0.0;
		for (int i = 0; i < arr1.size(); i++) {
			sum += arr1.get(i) * arr2.get(i);
		}
		return sum;
	}

	
	// Takes test data for input
	public static void main(String[] args) {
//		computeKTParams k = new computeKTParams();
//		if (args.length >1) {
//			k.N = Integer.parseInt(args[1]);
//		}
//		String infile = args[0];
//		k.computelzerot(infile);
//		System.out.println();
//		System.out.println("Finished Training");
//		for (String key : k.vals.keySet()) {
//			L0.put(key, k.vals.get(key)[0]); //INITIAL L0 VALUES THEN PRINT AT BOTTOM
//			System.out.println(key + " -\tL: " + k.vals.get(key)[0] + "\tG: " + k.vals.get(key)[1] + "\tS: " + k.vals.get(key)[2] + "\tT: " + k.vals.get(key)[3]);
//		}
////		System.out.println("Correct/Incorrect (1/0)");
//		String currStudent = "FWORPLEJOHN";
//		for (int i = 0; i < k.students_.length; i++) {
//			if (k.students_[i] == null) break;
//			if (!k.students_[i].equals(currStudent)) {
//				currStudent = k.students_[i];
//				studentIndex.add(currStudent);
//				studyData.add(new HashMap<String,ArrayList<Integer>>());
//				studyData.get(studentIndex.size()-1).put(k.skill_[i],new ArrayList<Integer>());
//			}
//			studyData.get(studentIndex.size()-1).get(k.skill_[i]).add((int)k.right_[i]);
//		}
//		for (int sk = 0; sk < k.skillnum; sk++) {
//			String skill;
//			if (sk > 0) {
//				skill = k.skill_[k.skillends_[sk - 1] + 1];
//			}
//			else {
//				skill = k.skill_[0];
//			}
//			averages.put(skill, new ArrayList<Double>());
//			sizes.put(skill, new ArrayList<Double>());
//			double L0 = k.vals.get(skill)[0].doubleValue();
//			for (int i = 0; i < studentIndex.size(); i++) {
//				k.vals.get(skill)[0] = L0;
//				double G = k.vals.get(skill)[1];
//				double S = k.vals.get(skill)[2];
//				double T = k.vals.get(skill)[3];
//				boolean message = false;
//				ArrayList<Integer> postMastery = new ArrayList<Integer>();
//				if (!studyData.get(i).containsKey(skill)) {
//					continue;
//				}
//				boolean wheelSpinning = false;
//				int sum1 = 0;
//				int sum2 = 0;
//				int sum3 = 0;
//				for (int j = 0; j < studyData.get(i).get(skill).size(); j++) {
//					int num = studyData.get(i).get(skill).get(j);
//					if (!masteryThreshold.stop(k.vals.get(skill)[0],G,S,T) && (!predictiveSimilarity.stop(k.vals.get(skill)[0],G,S,T)) && !message) { //predictive similarity and mastery threshold
//						k.update(num,skill, j==0, false);
//					}
//					else if (!masteryThreshold.stop(k.vals.get(skill)[0],G,S,T) && (predictiveSimilarity.stop(k.vals.get(skill)[0],G,S,T)) && !message && sum1 < 1 && sum2 < 2 && sum3 < 3 & !wheelSpinning) { //predictive similarity stops and mastery threshold doesn't
////						System.out.println("this just happened");
//						if (k.vals.get(skill)[0] > 0.9) {
//							sum1++;
//							k.update(num,skill,j==0, false);
//						}
//						else if (k.vals.get(skill)[0] > 0.85) {
//							sum2++;
//							k.update(num,skill, j==0, false);
//						}
//						else if (k.vals.get(skill)[0] > 0.8) {
//							sum3++;
//							k.update(num,skill, j==0, false);
//						}
//						else {
//							wheelSpinning = true;
//						}
//					}
//					else {
//						if (!message) {
//							if (!wheelSpinning) {
//								System.out.print(studentIndex.get(i).substring(0,9) + "(");
//								printTrials(i, skill);
//								System.out.print(") achieved mastery of " + skill + " in " + (j) + " q's - L: " + k.vals.get(skill)[0] + "\t");
//							}
//							else {
//								System.out.print(studentIndex.get(i).substring(0,9) + "(");
//								printTrials(i, skill);
//								System.out.print(") was stopped from wheel spinning on " + skill + ", in " + (j) + " q's - L: " + k.vals.get(skill)[0] + "\t");
//							}
//							message = true;
//						}
//						k.update(num, skill, false, true);
//						postMastery.add(num);
//					}
//				}
//				if (!message && (masteryThreshold.stop(k.vals.get(skill)[0],G,S,T) || sum1 == 1 || sum2 == 2 || sum3 == 3)) {      //mastery threshold
//					System.out.print(studentIndex.get(i).substring(0,9) + "(");
//					printTrials(i,skill);
//					System.out.print(") achieved mastery of " + skill + " in " + (studyData.get(i).get(skill).size()) + " q's - L: " + k.vals.get(skill)[0] + "\t");
//				}
//				
//				else if (!message && predictiveSimilarity.stop(k.vals.get(skill)[0],G,S,T)) { //CHANGE THIS FOR WHEEL SPINNING
//					System.out.print(studentIndex.get(i).substring(0,9) + "(");
//					printTrials(i,skill);
//					System.out.print(") was stopped from wheel spinning of " + skill + " in " + (studyData.get(i).get(skill).size()) + " q's - L: " + k.vals.get(skill)[0] + "\t");
//				}
//				else if (!message && !masteryThreshold.stop(k.vals.get(skill)[0],G,S,T)) { 			//mastery threshold
//					System.out.print(studentIndex.get(i).substring(0,9) + "(");
//					printTrials(i,skill);
//					System.out.print(") did not achieve mastery of " + skill + " - L: " + k.vals.get(skill)[0] + "\t");
//				}
//				if (postMastery.size() != 0) {
//					Double avg = (sumArr(postMastery)*1.0)/((postMastery.size()) * 1.0);
//					averages.get(skill).add(avg);
//					sizes.get(skill).add(postMastery.size() * 1.0);
//					System.out.println("Avg: (" + postMastery.size() + "): " + (avg));
//				}
//				else System.out.println();
//			}
//		}
//		
/* PRINTING THE STUDENTS AND THEIR TRIALS */
//
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		for (int sk = 0; sk < k.skillnum; sk++) {
//			String skill;
//			if (sk > 0) {
//				skill = k.skill_[k.skillends_[sk - 1] + 1];
//			}
//			else {
//				skill = k.skill_[0];
//			}
//			System.out.println();
//			System.out.println("KC: " + skill);
//			int stunum = 1;
//			for (int i = 0; i < studyData.size(); i++) {
//				if (studyData.get(i).containsKey(skill)) {
//					if (studyData.get(i).get(skill).size() >= 12) {    //only considering cases > 12
//						System.out.print(stunum++ + "\t");
//						for (int j = 0; j < studyData.get(i).get(skill).size(); j++) {
//							if (j!=studyData.get(i).get(skill).size()-1)	System.out.print(studyData.get(i).get(skill).get(j) + " ");
//							else System.out.print(studyData.get(i).get(skill).get(j) + "\n");
//						}
////						for (int j = 0; j < 37-studyData.get(i).get(skill).size();j++) {
////							System.out.println();
////						}
//					}
//				}
//			}
//		}

		
		/* Printing L values at each step */
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		ArrayList<ArrayList<Double>> trapL = k.lvals.get("decomp-trap");
//		ArrayList<ArrayList<Integer>> trapT = k.trials.get("decomp-trap");
//		int studentnum = 1;
//		for (int i = 0; i < trapL.size(); i++) {
//			if (trapT.get(i).size() >= 12) {
//				int count = 0;
//				System.out.println(studentnum + "\t" + count++ +"\t" + "\t" + L0.get("decomp-trap"));
//				for (int j = 0; j < trapT.get(i).size(); j++) {
//					if (j < trapL.get(i).size()) System.out.println(studentnum + "\t" + count++ + "\t" + trapT.get(i).get(j) + "\t" + trapL.get(i).get(j));
//					else System.out.println(studentnum + "\t" + count++ + "\t" + trapT.get(i).get(j) + "\t" + trapL.get(i).get(trapL.get(i).size()-1));
//				}
////			int count = 0;
////			System.out.println(count + "\t" + L0.get("decomp-trap"));
////			count += 1;
////			for (int j = 0; j < trapL.get(i).size(); j++) {
////				System.out.println(count + "\t" + trapL.get(i).get(j));
////				count += 1;
////			}
//			studentnum++;
//			}
//
//		}
		
		
	}
}

