package edu.cmu.pact.miss.BKT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.BKT.BKT.BKTEntry;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStSolver;
import edu.cmu.pact.miss.PeerLearning.SimStSolver.SolutionStepInfo;
import jess.JessException;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;

public class LMS {

	private final static String QUESTIONS_FILE="question_bank.txt";

	private ArrayList<ArrayList<String>> questionBank;
	
	/*Dictionary to store section$problemName -> kcs */
	private HashMap<String,ArrayList<String>> questionBankMap;
	
	ArrayList<String> logArray=null;
	
	
	/*Used to contextualize LMS with the current section*/
	private int currentSection=0;
	public void setCurrentSection(int i){
			if (i>2) i=2;
			this.currentSection=i;
			/*When setting the current section, reset the problemsGivenPerSkillHash hash.*/
			if (problemsGivenPerSkillHash!=null)
				resetProblemsPerSkill();		
	}
	private int getCurrentSection(){return currentSection;}
	
	/*Keep track how many times each skill was asked*/
	private HashMap<String, Integer> problemsGivenPerSkillHash=null;			//skill --> num times skill checked 
	
	//Variable to indicate times must a skill be doublechecked
	private int doubleCheckLimit=1; 
	public void setDoubleCheckLimit(int val){this.doubleCheckLimit=val;}
	private int getDoubleCheckLimit(){return this.doubleCheckLimit;}
	
	SimStLogger simStLogger=null;
	void setSimStLogger(SimStLogger logger){this.simStLogger=logger;}
	SimStLogger getSimStLogger(){return this.simStLogger;}
	
	SimSt simSt=null;
	public void setSimSt(SimSt ss){this.simSt=ss;}
	private SimSt getSimSt(){return this.simSt;}	
	
	/**
	 * Constructor to define the question bank filename
	 * @param questionsFile the file containing the problems. The format of each line is section$problemName,skill1,skill2, etc
	 * @param simStLogger the simStLogger object
	 */
	public LMS(String questionsFile,SimStLogger simStLogger){
		
		questionBankMap = new HashMap();
		questionBank=new ArrayList<ArrayList<String>>();
		
		readQuestionBankFromFile(questionsFile);
		
		if (simStLogger!=null)
			this.setSimStLogger(simStLogger);

	}
	
	/**
	 * Constructor to use the default question bank filename
	 */
	public LMS(SimStLogger simStLogger) {					
		this(QUESTIONS_FILE,simStLogger);			
	}
	
	
	
	
	
	/**
	 * Method to read the question bank from a file, and populate
	 * the questionBankMap Hash Map.
	 * @param parametersFilename
	 */
	private void readQuestionBankFromFile(String questionBankFileName){
    	BufferedReader reader=null;
    	try
    	{
    		File file = null;
    		if(!SimSt.WEBSTARTENABLED) 
				file = new File(questionBankFileName);
			else 
				file = new File(WebStartFileDownloader.SimStWebStartDir+questionBankFileName);
				
        	reader = new BufferedReader(new FileReader(/*questionBankFileName*/file));
    		String line = reader.readLine();
    	
    		while(line != null)
	    	{
    			
	    			String[] parts=line.split(",");
	  
	    			String problemName=parts[0];
	    			ArrayList<String> tmp=new ArrayList<String>();
	    			for (int i=1;i<parts.length;i++){
	    				tmp.add(parts[i]);
	    			}
	    			questionBankMap.put(problemName, tmp);
	    			
	    			if(line != null)
	    			{
	    				line = reader.readLine();
	    			}
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		
    	}
    	
	}
	
	
	/**
	 * Computes the amount that L goes up if the students answer the KC right.
	 * We want to calculate Pr(L=1 | correct) - L which tells us how much mastery we gained
	 * @param vals is the dictionary of KCs to L,G,S,T values
	 * @param currSkill is the KC we are computing for 
	 * @return
	 */
	public static double gainRight(Map<String,BKTEntry> vals, String currSkill) {	
		
		double L = vals.get(currSkill).getL();// [0].doubleValue();
		double G = vals.get(currSkill).getG();//[1].doubleValue();
		double S = vals.get(currSkill).getS();//[2].doubleValue();
		double T = vals.get(currSkill).getT();//[3].doubleValue();
	
		double probL = predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		double newL = ((1.0-S)*probL) / (((compL * G) + (probL * (1.0-S))));
		return newL - L;
	}
	
	/**
	 * Computes the amount that L goes down if the student answers the KC wrong
	 * We want to calculate Pr(L = 1 | incorrect) - L which tells us how much mastery lost
	 * @param vals is the dictionary of KCs to L,G,S,T values
	 * @param currSkill is the KC we are computing for
	 * @return
	 */
	public static double lossWrong(Map<String,BKTEntry> vals, String currSkill) {
		double L = vals.get(currSkill).getL();//[0].doubleValue();
		double G = vals.get(currSkill).getG();//[1].doubleValue();
		double S = vals.get(currSkill).getS();//[2].doubleValue();
		double T = vals.get(currSkill).getT();//[3].doubleValue();
		double probL = predict_mastery(L, G, S, T);
		double compL = 1.0 - probL;
		double newL = (probL * S) / ((compL * (1.0-G)) + probL * S);
		return newL - L;
	}

	/**
	 * Computes the expected change in a students L value based on answering a question with a certain list of KCs
	 * @param listKC is the list of KCs in the problem that the student is going to attempt
	 * @param vals is the dictionary of KCs to L,G,S,T values
	 * @return
	 */
	public static double expectedChange(ArrayList<String> listKC, Map<String,BKTEntry> vals) {
		double sum = 0.0;
		for (String currSkill : listKC) {
			double probC = BKT.probC(vals,currSkill);  			// prob of student with apply skill correctly
			double compC = 1.0 - probC;						// prob of student apply skill incorrectly
			double gain = gainRight(vals,currSkill);		// how much L goes up if skill is applied correctly
			double loss = lossWrong(vals,currSkill);		// how much L goes down if skill is applied incorrectly
			sum += (probC * gain) + (compC * loss);			// running sum of overall gain. empirically defined
		}
		return sum;
	}
	
	/**
	 * returns the index of the question in the ArrayList that is the next best question for the student 
	 * questionBank is an arrayList of questions where each question is represented as an arraylist of strings of the KCs for the q
	 * @param vals is the dictionary of KCs to L,G,S,T values
	 * @return
	 */
	public int nextBestQuestion(Map<String, BKTEntry> vals) {
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
	
	ArrayList<String> bestProblems=null;
	
	/**
	 * High level method that returns the next probem to give
	 * @param vals is the dictionary of KCs to L,G,S,T values
	 * @return
	 */
	public String getNextProblem(Map<String, BKTEntry> vals) {
		
		bestProblems=null;
		bestProblems=new ArrayList<String>();
		
		if (questionBankMap.size() == 0) return null;		
		
		if (logArray==null)
			logArray=new ArrayList<String>();
		else 
			logArray.clear();
			
		if (problemsGivenPerSkillHash==null){
			problemsGivenPerSkillHash = new HashMap();
			for(Map.Entry<String, BKTEntry> entry : vals.entrySet()){
				problemsGivenPerSkillHash.put(entry.getKey(), 0);
			}
		}
		
		double maxGain = 0.0;
		int maxIndex = 0;
		String bestProblem="";	
		ArrayList<String> tmpSkills=new ArrayList<String>();
		
		ArrayList<String> targetKCs=null;
		for (Map.Entry<String, ArrayList<String>> entry : questionBankMap.entrySet()) {
			String problem = entry.getKey();
		    ArrayList kcs = entry.getValue();
		    if (getSectionFromProblem(problem)==getCurrentSection()){
		    	double gain=expectedChange(kcs, vals);
		    	logArray.add(trimSectionFromProblem(problem)+","+gain);
		    	if (gain > maxGain) {
		    		targetKCs=kcs;
		    		maxGain=gain;
		    		bestProblem=problem;
		    		
		    		bestProblems.clear();
		    		bestProblems.add(problem);
		    	}	  
		    	else if (gain == maxGain){
		    		bestProblems.add(problem);
		    	}
		    }
		}
		
		
		
		 Random randomGenerator=new Random();
		 int index = randomGenerator.nextInt(bestProblems.size());
	     String pickedProblem=  bestProblem;
	     
	     if (index<bestProblems.size()) 
	    	 pickedProblem=bestProblems.get(index); 
		
		updateProblemCountPerSkill(pickedProblem);
		String problem = trimSectionFromProblem(pickedProblem);
		String actualProblemGiven=GameShowUtilities.generate(problem);
			

		/* check if problem about to be given has the necessary skills.
		 * if not generate a new one that has */
		//while(!problemContainsTargetKCs(targetKCs,actualProblemGiven)){
		//	actualProblemGiven=GameShowUtilities.generate(problem);
		//};
		
		return actualProblemGiven;
	
	}
	
	/**
	 * Method to check if the problem given actually requires for the KCs the LMS thinks.
	 * @param targetKCs
	 * @return
	 */
	public boolean problemContainsTargetKCs(ArrayList<String> targetKCs,String problem){

		boolean isProblemOK=true;	
		Vector<Sai> sol=new Vector<Sai>();
		SimStSolver solver=simSt.createSimStSolver(problem);
		try {
			solver.createStartState();
			solver.solve();

		} catch (JessException e) {
			e.printStackTrace();
		}
	
		HashSet<String> usedSkills=solver.getSkillsUsed();

		for (String kc : targetKCs){
			if (!usedSkills.contains(kc)){
				isProblemOK=false;
				break;
			}
				
		}

		return isProblemOK;
		
	}
	
	/**
	 * Method to log the gains for all the problems considered for the last problem given
	 */
	public void logGains(SimStLogger simStLogger, String lmsID){
		String logResult="";
		for (String skill:logArray){
			String[] split=skill.split(",");
			//simStLogger.simStLog(SimStLogger.LMS_PROBLEM, SimStLogger.LMS_PROBLEM_CONSIDERED, split[0],split[1] , lmsID);		
			logResult+="["+split[0]+" "+split[1]+"],";
			
		}
		System.out.println("Log Result : "+logResult);
		simStLogger.simStLog(SimStLogger.LMS_PROBLEM, SimStLogger.LMS_PROBLEM_CONSIDERED, "",logResult , lmsID);		
		
		
	}
	
	/**
	 * Method to update the map showing how many times a skills was "checked"
	 */
	void updateProblemCountPerSkill(String problem){
		ArrayList<String> kcs=questionBankMap.get(problem);
		for (int i=0;i<kcs.size();i++){		
			int curvalue=problemsGivenPerSkillHash.get(kcs.get(i));
			curvalue=curvalue+1;
			problemsGivenPerSkillHash.put(kcs.get(i), curvalue);	
		}		
	}
	
	/**
	 * Boolean function to indicate if all the skills are checked by the problems proposed by the LMS.
	 * At least all skills should be checked x number of times. (x defined by doubleCheckLimit, default value is 1)
	 * @return
	 */
	public boolean allSkillsChecked(){
		boolean returnValue=true;
		
   		for (Map.Entry<String, Integer> entry : problemsGivenPerSkillHash.entrySet()) {
   			trace.err(entry.getKey() + " checked " + entry.getValue());
   			if (entry.getValue()<getDoubleCheckLimit()){
   				returnValue=false;
   			}
   			
   		}
   		//return true;
   		return returnValue;	
	}
	
	/**
	 * Method to reset the problems given per skill (used when student goes to a new level).
	 * Note: we have the same skills across sections.
	 */
	void resetProblemsPerSkill(){
		Set<String> kcs=problemsGivenPerSkillHash.keySet();
		Iterator<String> it = kcs.iterator();
		while(it.hasNext()){
			problemsGivenPerSkillHash.put(it.next(), 0);
		}
		
	}
	
	/**
	 * Method to extract the section from the questionBankMap dictionary key (i.e. section$problemName) 
	 * @param str
	 * @return
	 */
	int getSectionFromProblem(String str){
		String[] parts=str.split("\\$");
		return Integer.parseInt(parts[0]);
	}
	
	/**
	 * Method to extract the problem name from the questionBankMap dictionary key (i.e. section$problemName) 
	 * @param str
	 * @return
	 */
	public String trimSectionFromProblem(String str){
		String[] parts=str.split("\\$");
		return parts[1];
	}
	
	/**
	 * Method to predict the mastery of a skill
	 * @param L prob. of learning
	 * @param G prob. of quessing
	 * @param S prob. of slip
	 * @param T prob. of skill transistioning from unknown to known
	 * @return
	 */
	public static double predict_mastery(double L, double G, double S, double T) {
		return L + ((1.0-L)*T);
	}
	
}

