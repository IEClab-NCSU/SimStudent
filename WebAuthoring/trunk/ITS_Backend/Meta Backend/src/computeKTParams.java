import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class computeKTParams {
	 /** 
	 * This class expects data sorted on Skill and then on Student in the below mentioned format
	 * num		lesson					student			skill		   cell 	right	eol
	 *	1	Z3.Three-FactorZCros2008	student102	META-DETERMINE-DXO	cell	0	eol
	 * */

	public String students_[] = new String[27600];// Number of instances
	public String skill_[] = new String[27600];
	public double right_[] = new double[27600];
	public int skillends_[] = new int[27600];//Number of Skills
	public int skillnum = -1;
	public boolean lnminus1_estimation = false;
	public boolean bounded = true;
	public boolean L0Tbounded = false;
	public int N = 13; //NUMBER OF TRAINING EXAMPLES TO LOOK AT
	public int numStudents = 10;
	public Map<String, Double[]> vals = new HashMap<String, Double[]>();
	public String currSkill = "";
	public Map<String, ArrayList<ArrayList<Double>>> lvals = new HashMap<String, ArrayList<ArrayList<Double>>>();
	public Map<String, ArrayList<ArrayList<Integer>>> trials = new HashMap<String, ArrayList<ArrayList<Integer>>>();
	public boolean studentStratified = false;
	
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
							students_[actnum] = currWord;
							currWord = "";
							
						}
						else if (index == 3) {
							skill_[actnum] = currWord;
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
				right_[actnum] = Integer.parseInt(currWord) * 1.0;
				currWord = "";
				index = 0;
				currWord = "";
				actnum++;
				if (!skill_[actnum - 1].equals(prevskill)) {
					prevskill = skill_[actnum - 1];
					if (skillnum > -1)
						skillends_[skillnum] = actnum - 2;
					skillnum++;
				}
			}
			prevskill = skill_[actnum - 1];
			if (skillnum > -1) {
				skillends_[skillnum] = actnum - 1;
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
			if (!students_[i].equals(prevstudent)) {
				students++;
				prevL = Lzero;
				prevstudent = students_[i];
				consecutive = 1;
			}
			else {
				consecutive++;
			}
			if (!studentStratified) numStudents = Integer.MAX_VALUE;
			if (students > numStudents) break;
			if (studentStratified) N = Integer.MAX_VALUE;
			if (consecutive < N) {
				if (lnminus1_estimation)
					likelihoodcorrect = prevL;
				else
					likelihoodcorrect = (prevL * (1.0 - S)) + ((1.0 - prevL) * G);
				SSR += (right_[i] - likelihoodcorrect) * (right_[i] - likelihoodcorrect);
	
				if (right_[i] == 1.0)
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
			startact = skillends_[curskill - 1] + 1;
		int endact = skillends_[curskill];

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
		String currSkill = skill_[startact];
		vals.put(currSkill, new Double[4]);
		vals.get(currSkill)[0] = bestLzero;
		vals.get(currSkill)[1] = bestG;
		vals.get(currSkill)[2] = bestS;
		vals.get(currSkill)[3] = besttrans;
	/*
		System.out.println();
		System.out.print("Skill: " + skill_[startact]);
		System.out.print("\t");
		System.out.print("L0: " + bestLzero);
		System.out.print("\t");
		System.out.print("G: " + bestG);
		System.out.print("\t");
		System.out.print("S: " + bestS);
		System.out.print("\t");
		System.out.print("T: " + besttrans);
		System.out.println();
	*/
	}
	
	public void computelzerot(String infile_) {
		
		read_in_data(infile_);

		for (int curskill = 0; curskill <= skillnum; curskill++) {
			fit_skill_model(curskill);
		}
	}

	public static void main(String args[]) {
		String infile_ = args[0];//Needs to be tab delimited
		computeKTParams m = new computeKTParams();
		m.computelzerot(infile_);
	}

	public double perCorrect(String skill) {
		double L = vals.get(skill)[0];
		double G = vals.get(skill)[1];
		double S = vals.get(skill)[2];
		return (L * (1.0-S)) + ((1.0-L) * G);
	}
	
	public void update(int right, String skill) {  //REMOVED param boolean newStudent and param boolean complete
//		if (complete)	{
//			trials.get(skill).get(trials.get(skill).size()-1).add(new Integer(right));
//			return;
//		}
		double L = vals.get(skill)[0];
		double G = vals.get(skill)[1];
		double S = vals.get(skill)[2];
		double T = vals.get(skill)[3];
		double probL = masteryThreshold.predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		if (right == 1) {
			 vals.get(skill)[0] = ((1.0-S)*probL) / (((compL * G) + (probL * (1.0-S))));
		}
		else {
			 vals.get(skill)[0] = (probL * S) / ((compL * (1.0-G)) + probL * S);
		}
//		if (!lvals.containsKey(skill)) lvals.put(skill, new ArrayList<ArrayList<Double>>());
//		if (!trials.containsKey(skill)) trials.put(skill, new ArrayList<ArrayList<Integer>>());
//		if (newStudent) {
//			lvals.get(skill).add(new ArrayList<Double>());
//			trials.get(skill).add(new ArrayList<Integer>());
//		}
//		lvals.get(skill).get(lvals.get(skill).size()-1).add(vals.get(skill)[0]);
//		trials.get(skill).get(trials.get(skill).size()-1).add(new Integer(right));
	}


}