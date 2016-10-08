package interaction;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BKT {
	 /** 
	 * This class expects data sorted on Skill and then on Student in the below mentioned format
	 * num		lesson					student			skill		   cell 	right	eol
	 *	1	Z3.Three-FactorZCros2008	student102	META-DETERMINE-DXO	cell	0	eol
	 * */

	public ArrayList<String> students_ = new ArrayList<String>();						//size of this array is number of instances
	public ArrayList<String> skill_ = new ArrayList<String>();
	public ArrayList<Double> right_ = new ArrayList<Double>();
	public ArrayList<Integer> skillends_ = new ArrayList<Integer>();					//size of this array is number of skills
	public int skillnum = -1;
	public boolean lnminus1_estimation = false;
	public boolean bounded = true;
	public boolean L0Tbounded = false;
	public int N = 13; 																	//number of training examples to look at (item strat)
	public int numStudents = 10;														//number of students to look at (student strat)
	public HashMap<String, Double[]> vals = new HashMap<String, Double[]>();
	public String currSkill = "";
	public HashMap<String, ArrayList<ArrayList<Double>>> lvals = new HashMap<String, ArrayList<ArrayList<Double>>>();
	public HashMap<String, ArrayList<ArrayList<Integer>>> trials = new HashMap<String, ArrayList<ArrayList<Integer>>>();
	public boolean studentStratified = false;											//indicates item or student strat
	
//	public BKT(String filename){
		//read files
		//update vals and whatever needs to be updated
//	}
	
//	public BKT(){
//		BKT.BKT("bla.txt");
//	}
	
	public void read_in_data(String infile) {
		int actnum = 0;
		try {
			FileReader file = new FileReader(infile);
			BufferedReader scan = new BufferedReader(file);
			String currLine;
			currLine = scan.readLine();
			int index = 0;
			skillnum = -1;
			String prevskill = "FLURG";
			String currWord = "";
			while ((currLine = (scan.readLine())) != null) {
				for (int i = 0; i < currLine.length(); i++) {
					if (currLine.charAt(i) == '\t') {
						if (index == 2) {
							students_.add(currWord);
							currWord = "";
							
						}
						else if (index == 3) {
							skill_.add(currWord);
							currWord = "";
						}
						else {
							currWord = "";
						}
						index++;
					}
					else {
						currWord += currLine.charAt(i);
					}
				}
				right_.add(Integer.getInteger(currWord)*1.0);
				currWord = "";
				index = 0;
				currWord = "";
				actnum++;
				if (!skill_.get(actnum-1).equals(prevskill)) {
					prevskill = skill_.get(actnum);
					if (skillnum > -1)
						skillends_.add(actnum - 2);
					skillnum++;
				}
			}
			prevskill = skill_.get(actnum-1);
			if (skillnum > -1) {
				skillends_.add(actnum - 1);
			}
			scan.close();
		} catch (IOException e) {
			System.out.println("Problem Reading Data File");
		}
	}

	public double findGOOF(int start, int end, double Lzero, double trans,
			double G, double S) {
		double SSR = 0.0;
		String prevstudent = "FWORPLEJOHN";
		double prevL = 0.0;
		double likelihoodcorrect = 0.0;
		double prevLgivenresult = 0.0;
		double newL = 0.0;
		int consecutive = 0;
		int students = 1;
		for (int i = start; i <= end; i++) {
			// System.out.println(SSR);
			if (!students_.get(i).equals(prevstudent)) {
				students++;
				prevL = Lzero;
				prevstudent = students_.get(i);
				consecutive = 1;
			}
			else {
				consecutive++;
			}
			if (!studentStratified) numStudents = Integer.MAX_VALUE; 		//SETS NUM STUDENTS TO INT_MAX SO THAT IT IS ITEM STRATIFIED
			if (students > numStudents) break;
			if (studentStratified) N = Integer.MAX_VALUE;					//SETS N TO INT_MAX SO THAT IT IS ITEM STRATIFIED
			if (consecutive < N) {
				if (lnminus1_estimation)
					likelihoodcorrect = prevL;
				else
					likelihoodcorrect = (prevL * (1.0 - S)) + ((1.0 - prevL) * G);
				SSR += (right_.get(i) - likelihoodcorrect) * (right_.get(i) - likelihoodcorrect);
	
				if (right_.get(i) == 1.0)
					prevLgivenresult = ((prevL * (1.0 - S)) / ((prevL * (1 - S)) + ((1.0 - prevL) * (G))));
				else
					prevLgivenresult = ((prevL * (S)) / ((prevL * (S)) + ((1.0 - prevL) * (1.0 - G))));
	
				newL = prevLgivenresult + (1.0 - prevLgivenresult) * trans;
				prevL = newL;
			}
		}
		return SSR;
	}

	public void fit_skill_model(int curskill) {
		double SSR = 0.0;
		double BestSSR = 9999999.0;
		double bestLzero = 0.01;
		double besttrans = 0.01;
		double bestG = 0.01;
		double bestS = 0.01;
		double topG = 0.99;
		double topS = 0.99;
		double topL0 = 0.99;
		double topT = 0.99;
		topL0 = 0.85;
		topT = 0.3;
		topG = 0.3;
		topS = 0.1;

		int startact = 0;
		if (curskill > 0)
			startact = skillends_.get(curskill - 1) + 1;
		int endact = skillends_.get(curskill);	

		int count = 0;
		for (double Lzero = 0.01; Lzero <= topL0; Lzero = Lzero + 0.01) {
			for (double trans = 0.01; trans <= topT; trans = trans + 0.01) {
				for (double G = 0.01; G <= topG; G = G + 0.01) {
					for (double S = 0.01; S <= topS; S = S + 0.01) {
						SSR = findGOOF(startact, endact, Lzero, trans, G, S);
						/**
						 * System.out.print(Lzero); System.out.print("\t");
						 * System.out.println(trans);
						 */
						if (SSR < BestSSR) {
							BestSSR = SSR;
							bestLzero = Lzero;
							besttrans = trans;
							bestG = G;
							bestS = S;
						}
					}
				}
			}
			count ++;
			if (count == 7) {
				System.out.print(".");
				count = 0;
			}
		}
	
		count = 0;
		// for a bit more precision
		double startLzero = bestLzero;
		double starttrans = besttrans;
		double startG = bestG;
		double startS = bestS;
		for (double Lzero = startLzero - 0.009; ((Lzero <= startLzero + 0.009) && (Lzero <= topL0)); Lzero = Lzero + 0.001) {
			for (double G = startG - 0.009; ((G <= startG + 0.009) && (G <= topG)); G = G + 0.001) {
				for (double S = startS - 0.009; ((S <= startS + 0.009) && (S <= topS)); S = S + 0.001) {
					for (double trans = starttrans - 0.009; ((trans <= starttrans + 0.009) && (trans < topT)); trans = trans + 0.001) {
						SSR = findGOOF(startact, endact, Lzero, trans, G, S);
						if (SSR < BestSSR) {
							BestSSR = SSR;
							bestLzero = Lzero;
							besttrans = trans;
							bestG = G;
							bestS = S;
						}
					}
				}
			}
			count ++;
			if (count == 7) {
				System.out.print(".");
				count = 0;
			}
		}
		String currSkill = skill_.get(startact);
		vals.put(currSkill, new Double[4]);
		vals.get(currSkill)[0] = bestLzero;
		vals.get(currSkill)[1] = bestG;
		vals.get(currSkill)[2] = bestS;
		vals.get(currSkill)[3] = besttrans;
	}
	
	public void computelzerot(String infile_) {
		
		read_in_data(infile_);

		for (int curskill = 0; curskill <= skillnum; curskill++) {
			fit_skill_model(curskill);
		}
	}

	public static void main(String args[]) {
		String infile_ = args[0];
		BKT m = new BKT();
		m.computelzerot(infile_);
	}

	public double perCorrect(String skill) {
		double L = vals.get(skill)[0];
		double G = vals.get(skill)[1];
		double S = vals.get(skill)[2];
		return (L * (1.0-S)) + ((1.0-L) * G);
	}
	
	public void update(int right, String skill) {  
		double L = vals.get(skill)[0];
		double G = vals.get(skill)[1];
		double S = vals.get(skill)[2];
		double T = vals.get(skill)[3];
		double probL = predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		if (right == 1) {
			 vals.get(skill)[0] = ((1.0-S)*probL) / (((compL * G) + (probL * (1.0-S))));
		}
		else {
			 vals.get(skill)[0] = (probL * S) / ((compL * (1.0-G)) + probL * S);
		}

	}
	
	public static double predict_mastery(double L, double G, double S, double T) {
		return L + ((1.0-L)*T);
	}
	
	
/**
 * isMastered checks if all the KCs in the current KC model are mastered
 */
	boolean isMastered() {
		for (String currSkill : vals.keySet()) {
			Double L = vals.get(currSkill)[0];
			Double G = vals.get(currSkill)[1];
			Double S = vals.get(currSkill)[2];
			Double T = vals.get(currSkill)[3];
			if (!masteryThreshold.stop(L, G, S, T)) {
				return false;
			}
		}
		return true;
	}



}