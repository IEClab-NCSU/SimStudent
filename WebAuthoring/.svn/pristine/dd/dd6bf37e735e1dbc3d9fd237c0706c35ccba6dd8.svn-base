package interaction;
import java.util.Map;
import java.util.ArrayList;


public class LMS {

	static ArrayList<ArrayList<String>> questionBank;
	
	public LMS(ArrayList<ArrayList<String>> questions) {
		questionBank = questions;
	}
	
	/*
	 * gainRight computes the amount that L goes up if the students answer the KC right
	 * vals is the dictionary of KCs to L,G,S,T values
	 * currSkill is the KC we are computing for 
	 * 
	 * We want to calculate Pr(L=1 | correct) - L which tells us how much mastery we gained
	 */
	
	public static double gainRight(Map<String,Double[]> vals, String currSkill) {
		double L = vals.get(currSkill)[0].doubleValue();
		double G = vals.get(currSkill)[1].doubleValue();
		double S = vals.get(currSkill)[2].doubleValue();
		double T = vals.get(currSkill)[3].doubleValue();
		double probL = predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		double newL = ((1.0-S)*probL) / (((compL * G) + (probL * (1.0-S))));
		return newL - L;
	}
	
	/*
	 * lossWrong computes the amount that L goes down if the student answers the KC wrong
	 * vals is the dictionary of KCs to L,G,S,T values
	 * currSkill is the KC we are computing for 
	 * 
	 * We want to calculate Pr(L = 1 | incorrect) - L which tells us how much mastery lost
	 */
	
	public static double lossWrong(Map<String,Double[]> vals, String currSkill) {
		double L = vals.get(currSkill)[0].doubleValue();
		double G = vals.get(currSkill)[1].doubleValue();
		double S = vals.get(currSkill)[2].doubleValue();
		double T = vals.get(currSkill)[3].doubleValue();
		double probL = predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		double newL = (probL * S) / ((compL * (1.0-G)) + probL * S);
		return newL - L;
	}
	
	/*
	 * probC computed the probability a student will answer a certain KC correctly
	 * vals is the dictionary of KCs to L,G,S,T values
	 * currSkill is the KC we are computing for 
	 * 
	 * P(C) = [L * (1-S)] + [(1-L) * G]
	 * 	mastered no slip  +   not mastered and guess 
	 */
	public static double probC(Map<String,Double[]> vals, String currSkill) {
		double L = vals.get(currSkill)[0].doubleValue();
		double G = vals.get(currSkill)[1].doubleValue();
		double notS = 1.0 - vals.get(currSkill)[2].doubleValue();
		double notL = 1.0 - L;
		return ((L*notS) + (notL*G));
	}
	
	/*
	 * expectedChange computes the expected change in a students L value based on answering a question with a certain list of KCs
	 * listKC is the list of KCs in the problem that the student is going to attempt
	 * vals is the dictionary of KCs to L,G,S,T values
	 */
	public static double expectedChange(ArrayList<String> listKC, Map<String,Double[]> vals) {
		double sum = 0.0;
		for (String currSkill : listKC) {
			double probC = probC(vals,currSkill);
			double compC = 1.0 - probC;
			double gain = gainRight(vals,currSkill);
			double loss = lossWrong(vals,currSkill);
			sum += (probC * gain) + (compC * loss);
		}
		return sum;
	}
	
	/*
	 * nextBestQuestion returns the index of the question in the ArrayList that is the next best question for the student
	 * questionBank is an arrayList of questions where each question is represented as an arraylist of strings of the KCs for the q
	 * vals is the dictionary of KCs to L,G,S,T values
	 * 
	 * the function returns -1 if the questionBank is empty
	 */
	
	public static int nextBestQuestion(Map<String, Double[]> vals) {
		if (questionBank.size() == 0) return -1;
		double maxGain = 0.0;
		int maxIndex = 0;
		for (int i = 0; i < questionBank.size(); i++) {
			if (expectedChange(questionBank.get(i), vals) > maxGain) {
				maxIndex = i;
				maxGain = expectedChange(questionBank.get(i),vals);
			}
		}
		return maxIndex;
	}
	
	public static double predict_mastery(double L, double G, double S, double T) {
		return L + ((1.0-L)*T);
	}

}
