package edu.cmu.pact.miss.BKT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.BKT.MasteryThreshold;
import edu.cmu.pact.miss.BKT.BKT.BKTEntry;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.storage.StorageClient;

	/** 
	 * This class expects data sorted on Skill and then on Student in the below mentioned format
	 * num		lesson					student			skill		   cell 	right	eol
	 *	1	Z3.Three-FactorZCros2008	student102	META-DETERMINE-DXO	cell	0	eol
	 * */

public class BKT {


		public final static String PARAMETERS_FILE_STEM="bkt_params";
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
		public HashMap<String, BKTEntry> bkt_vals = new HashMap<String, BKTEntry>();
		public HashMap<String, BKTEntry> bkt_view_vals = new HashMap<String, BKTEntry>();	// the value that will be displayed
		public HashMap<String, BKTEntry> bkt_view_vals_init = new HashMap<String, BKTEntry>();	// the initial values of the bkt (used when going to next level)

		public String currSkill = "";
		public HashMap<String, ArrayList<ArrayList<Double>>> lvals = new HashMap<String, ArrayList<ArrayList<Double>>>();
		public HashMap<String, ArrayList<ArrayList<Integer>>> trials = new HashMap<String, ArrayList<ArrayList<Integer>>>();
		public boolean studentStratified = false;											//indicates item or student strat
		
		String userID="";
		private void setUserID(String str){this.userID=str;}
		private String getUserID(){return this.getUserID();}

		/*SimStudent object necessary to make things easier when reading file */
		SimSt simSt;
		void setSimSt(SimSt simSt){this.simSt=simSt;}
		public SimSt getSimSt(){return simSt;}
		
		SimStLogger simStLogger=null;
		void setSimStLogger(SimStLogger logger){this.simStLogger=logger;}
		SimStLogger getSimStLogger(){return this.simStLogger;}
		
		private String parametersFilename=null;
		private void setParametersFilename(String str){this.parametersFilename=str;}
		private String getParametersFilename(){return this.parametersFilename;}
		
		public BKT(String parametersFilename){
			this.setParametersFilename(parametersFilename);

		}
		
	
		/**
		 * Constructor
		 * @param simSt simSt object to pass parameters necessary to log and load files.
		 */
		public BKT(SimSt simSt){

			setSimSt(simSt);
			setSimStLogger(simSt.getSimStLogger());
			
			this.setParametersFilename(PARAMETERS_FILE_STEM+"-"+simSt.getUserID()+".txt");
			initializeBKT();		
		}
		
		
		/**
		 * Method to initialize the BKT, using the default parameters filename
		 */
		public void initializeBKT(){
			initializeBKT(this.getParametersFilename());
		}
		
		/**
		 * Method to initialize the BKT, using the specified filename.
		 * @param parametersFilename
		 */
		public void initializeBKT(String parametersFilename){	
				
			File paramsFile = null;
			boolean downloaded=false;;
	
	    	if(!getSimSt().isWebStartMode()) { 
	    		paramsFile = new File(getSimSt().getProjectDir()+"/"+parametersFilename);
	    	} 
	    	else {
	    		try {
	    			downloaded = getSimSt().getMissController().getStorageClient().retrieveFile(parametersFilename, parametersFilename, WebStartFileDownloader.SimStWebStartDir );
	    		} catch (IOException e1) {
	    			e1.printStackTrace();
	    		}
	    		if(downloaded) {
	    			paramsFile = new File(WebStartFileDownloader.SimStWebStartDir + parametersFilename);
	    		}
	    		
	    	}
	    	
	    	if (paramsFile==null || !paramsFile.exists()){
	    		readBKTParametersFromFile(PARAMETERS_FILE_STEM+".txt");
	    		readInitialBKTParametersFromFile(PARAMETERS_FILE_STEM+".txt");
	    		this.saveBKTParametersFile(parametersFilename);
	    	}
	    	else{
	    		readInitialBKTParametersFromFile(PARAMETERS_FILE_STEM+".txt");
	    		readBKTParametersFromFile(parametersFilename);
	    	}
	    	
		}
		

		/**
		 * Method to dave the bkt in the default parameters file
		 */
		public void saveBKTParametersFile(){
			saveBKTParametersFile(getParametersFilename());
		}
		
		/**
		 * Method to save the bkt parameters in the specified filename
		 * @param parametersFilename
		 */
		public void saveBKTParametersFile(String parametersFilename){
			if(!SimSt.WEBSTARTENABLED) { 
			try {
				FileWriter f = new FileWriter(new File(parametersFilename));
				
				for (Map.Entry<String, BKTEntry> entry : bkt_vals.entrySet()) {
					String key = entry.getKey();
				    BKTEntry value = entry.getValue();	
				    f.write(key+","+value.getL()+","+value.getT()+","+value.getG()+","+value.getS()+","+value.getDescription()+"\n");
				}	
				f.flush();
				f.close();
								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			}
			else{
			try{
	        	File acFile = new File(WebStartFileDownloader.SimStWebStartDir+parametersFilename);
				FileWriter f = new FileWriter(acFile);
				
				for (Map.Entry<String, BKTEntry> entry : bkt_vals.entrySet()) {
					String key = entry.getKey();
				    BKTEntry value = entry.getValue();	
				    f.write(key+","+value.getL()+","+value.getT()+","+value.getG()+","+value.getS()+","+value.getDescription()+"\n");
				}	
				f.flush();
				f.close();
				StorageClient sc = new StorageClient();
	        	sc.storeFile(parametersFilename,acFile.getCanonicalPath());
								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
				
				
				
				
			}
		
		}
		
		/**
		 * Method to retrieve the p(L) for a skill
		 * @param skill
		 * @return
		 */
		public double getSkill_probL(String skill){
			if (bkt_vals.get(skill)==null) return 0.0;
			return bkt_vals.get(skill).getL();
		}
		
		
		/**
		 * Method to read the parameters filename and populate the dictionary bkt_vals		
		 * @param parametersFilename
		 */
		private void readBKTParametersFromFile(String parametersFilename){
	    	BufferedReader reader=null;
	    	try
	    	{
	    		
	    		
	    		File file = null;
	    			
	    		
	    		//File accountFile = null;
	        	boolean successful = false;

	    		
	    		// Check if the application is running locally or using Webstart
	        	if(!getSimSt().isWebStartMode()) { // Running locally
	        		file = new File(getSimSt().getProjectDir()+"/"+parametersFilename);
	        	} else {
	        		try {
	        			
	        			// Key associated when retrieving the .account file is the getSimSt().getUserID()+.account
	        			successful = getSimSt().getMissController().getStorageClient().retrieveFile(parametersFilename, parametersFilename,
	        					WebStartFileDownloader.SimStWebStartDir );
	        		} catch (IOException e1) {
	        			e1.printStackTrace();
	        		}
	        		if(successful) {
	        			file = new File(WebStartFileDownloader.SimStWebStartDir + parametersFilename);
	        		}
	        	}
	        	
	        	
	        	
	        	
	    		if (file==null){
	    			if(!SimSt.WEBSTARTENABLED) 
	    				file = new File(parametersFilename);
	    			else 
	    				file = new File(WebStartFileDownloader.SimStWebStartDir+parametersFilename);
	    		}
	        	
	        	
	    		
	    		
	    		
	        	reader = new BufferedReader(new FileReader(/*questionBankFileName*/file));
	       		String line = reader.readLine();
	    	
	    		while(line != null)
		    	{
	    			
		    			String[] parts=line.split(",");
		    			
		    			String temp_skillname=parts[0];
		    			double temp_pL=Double.valueOf(parts[1]);
		    			double temp_pT=Double.valueOf(parts[2]);
		    			double temp_pG=Double.valueOf(parts[3]);
		    			double temp_pS=Double.valueOf(parts[4]);
		    			String explanation=parts[5];
		    			
		    			vals.put(temp_skillname, new Double[4]);												//Creates Array for L,G,S,T values
		    			vals.get(temp_skillname)[0] = temp_pL;  ///vals.get(temp_skillname)[0];								//Stores L Value
		    			vals.get(temp_skillname)[1] = temp_pG;  //vals.get(temp_skillname)[1];								//Stores G Value
		    			vals.get(temp_skillname)[2] = temp_pS;  //vals.get(temp_skillname)[2];								//Stores S Value
		    			vals.get(temp_skillname)[3] = temp_pT;  // vals.get(temp_skillname)[3];								//Stores T value
		    				
		    			bkt_vals.put(temp_skillname, new BKTEntry(temp_pL,temp_pT,temp_pG,temp_pS,explanation));	    			
		    			bkt_view_vals.put(temp_skillname, new BKTEntry(temp_pL,temp_pT,temp_pG,temp_pS,explanation));

		    			if (getSimStLogger()!=null)
		    				getSimStLogger().simStLog(SimStLogger.BKT, SimStLogger.BKT_INITIAZE, temp_skillname+":L="+temp_pL+",T="+temp_pT+",G="+temp_pG+",S="+temp_pS);
		    		    
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
		 * Method to read the parameters filename and populate the dictionary bkt_view_vals_init.
		 * Used to reset the bkt hash when graduating from a level (so that bars are progressively filled).		
		 * @param parametersFilename
		 */
		private void readInitialBKTParametersFromFile(String parametersFilename){
	    	BufferedReader reader=null;
	    	try
	    	{
	    		
	    		
	    		File file = null;
	    			
	    		
	    		//File accountFile = null;
	        	boolean successful = false;

	    		
	    		// Check if the application is running locally or using Webstart
	        	if(!getSimSt().isWebStartMode()) { // Running locally
	        		file = new File(getSimSt().getProjectDir()+"/"+parametersFilename);
	        	} else {
	        		try {
	        			
	        			// Key associated when retrieving the .account file is the getSimSt().getUserID()+.account
	        			successful = getSimSt().getMissController().getStorageClient().retrieveFile(parametersFilename, parametersFilename,
	        					WebStartFileDownloader.SimStWebStartDir );
	        		} catch (IOException e1) {
	        			e1.printStackTrace();
	        		}
	        		if(successful) {
	        			file = new File(WebStartFileDownloader.SimStWebStartDir + parametersFilename);
	        		}
	        	}
        	
	    		if (file==null){
	    			if(!SimSt.WEBSTARTENABLED) 
	    				file = new File(parametersFilename);
	    			else 
	    				file = new File(WebStartFileDownloader.SimStWebStartDir+parametersFilename);
	    		}
	        	
	        	
	    		
	    		
	    		
	        	reader = new BufferedReader(new FileReader(/*questionBankFileName*/file));
	       		String line = reader.readLine();
	    	
	    		while(line != null)
		    	{
	    			
		    			String[] parts=line.split(",");
		    			
		    			String temp_skillname=parts[0];
		    			double temp_pL=Double.valueOf(parts[1]);
		    			double temp_pT=Double.valueOf(parts[2]);
		    			double temp_pG=Double.valueOf(parts[3]);
		    			double temp_pS=Double.valueOf(parts[4]);
		    			String explanation=parts[5];
		    			
		    			vals.put(temp_skillname, new Double[4]);												//Creates Array for L,G,S,T values
		    			vals.get(temp_skillname)[0] = temp_pL;  ///vals.get(temp_skillname)[0];								//Stores L Value
		    			vals.get(temp_skillname)[1] = temp_pG;  //vals.get(temp_skillname)[1];								//Stores G Value
		    			vals.get(temp_skillname)[2] = temp_pS;  //vals.get(temp_skillname)[2];								//Stores S Value
		    			vals.get(temp_skillname)[3] = temp_pT;  // vals.get(temp_skillname)[3];								//Stores T value
		    				
		    			bkt_view_vals_init.put(temp_skillname, new BKTEntry(temp_pL,temp_pT,temp_pG,temp_pS,explanation));

		    			 
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
		 * Main method, used to test the BKT
		 * @param args
		 */
		public static void main(String args[]) {

			String infile_ = "geometry_formatted.txt";//args[0];

			BKT m = new BKT("bkt_params.txt");
			//m.computelzerot(infile_);
		}

		/**
		 * Computes the probability a student will answer a certain KC correctly
		 * @param skill
		 * @return
		 */
		public double probC(String skill) {			
				return probC(bkt_vals,skill);
		}	

		
		/**
		 * Computes the probability a student will answer a certain KC correctly
		 * P(C) = [L * (1-S)] + [(1-L) * G]  ( mastered no slip  +   not mastered and guess) 
		 * @param vals is the dictionary of KCs to L,G,S,T values
		 * @param currSkill
		 * @return is the dictionary of KCs to L,G,S,T values
		 */
		public static double probC(Map<String,BKTEntry> vals, String currSkill) {
			if (vals.get(currSkill)==null) return 0.0;
			double L = vals.get(currSkill).getL();
			double G = vals.get(currSkill).getG();
			double notS = 1.0- vals.get(currSkill).getS();	
			double notL = 1.0 - L;
			return ((L*notS) + (notL*G));
		}
		
		
		/**
		 * Method to update p(L) based on the conditional probabilities analyzed in Baker et al. 2008
		 * @param correctness
		 * @param skill
		 */
		public void update(int correctness, String skill) {  
			
			Double L = ((BKTEntry) bkt_vals.get(skill)).getL();
			Double G = ((BKTEntry) bkt_vals.get(skill)).getG();
			Double S = ((BKTEntry) bkt_vals.get(skill)).getS();
			Double T = ((BKTEntry) bkt_vals.get(skill)).getT();
			Double oldL=L;
			double probL = L;	//predict_mastery(L, G, S, T);
			double compL = 1.0 - probL;
			double marginalL=0;
			trace.err("=========== ");
			trace.err("Updating for skill " + skill);
			trace.err("Correctness is " + correctness);
			trace.err("p(L)= " + L);
			trace.err("p(G)= " + G);
			trace.err("p(S)= " + S);
			trace.err("p(T)= " + T);

			/* First estimate the probability that student knew the skill before the response. 
			 * estimate the conditional probability based on whether student applied skill correctly or incorrectly*/
			if (correctness == 1) {
				
				marginalL= ((1.0-S)*probL) / (((compL * G) + (probL * (1.0-S))));
				((BKTEntry) bkt_vals.get(skill)).incNumberOfCorrectOpportunities();
				
			}
			else {
				marginalL = (probL * S) / ((compL * (1.0-G)) + probL * S);
				((BKTEntry) bkt_vals.get(skill)).incNumberOfIncorrectOpportunities();
				
			}
			trace.err("marginal P(L)= " + marginalL);

			/* estimate the probability that student learned the skill during the problem step
			 * and update in the hashmap the probability of skill mastery*/
			double newL = predict_mastery(marginalL, G, S, T);
			trace.err("new p(L)= " + newL);
			trace.err("=========== ");

			vals.get(skill)[0] =newL;
			
			//JOptionPane.showMessageDialog(null, oldL + " ---> " + newL);
			
			((BKTEntry) bkt_vals.get(skill)).setL(newL);
			((BKTEntry) bkt_view_vals.get(skill)).setL(newL);
			int correct=((BKTEntry) bkt_vals.get(skill)).numberOfCorrectOpportunities;
			int incorrect=((BKTEntry) bkt_vals.get(skill)).numberOfIncorrectOpportunities;
			trace.err("-->Skillname="+skill+", p(L)="+newL+", correctCount="+correct+", incorrectCount="+incorrect);

		}
		
		/**
		 * Method to return a string with all the L values. 
		 * Used for logging purposes
		 * @return
		 */
		public String getLValues(){
			String returnString="";
			for (Map.Entry<String, BKTEntry> entry : bkt_vals.entrySet()) {
				String key = entry.getKey();
			    BKTEntry value = entry.getValue();
			    returnString+=key+"="+value.getL()+",";
			  
			}			
			 return returnString.substring(0,returnString.length()-1);
		}
		
		/**
		 * Method to update p(L) based on Baker et al. 2008
		 * @param L
		 * @param G
		 * @param S	
		 * @param T
		 * @return
		 */
		public static double predict_mastery(double L, double G, double S, double T) {
			return L + ((1.0-L)*T);
		}
		
		public static boolean stop(double L, double G, double S, double T){
			if (!MasteryThreshold.stop(L, G, S, T)) {
				return false;
			}
			return true;
		}
		
		
		
	/**
	 * Checks if all the KCs in the current KC model are mastered
	 */
		public boolean isMastered() {
			for (String currSkill : vals.keySet()) {
								
				Double L = ((BKTEntry) bkt_vals.get(currSkill)).getL();
				Double G = ((BKTEntry) bkt_vals.get(currSkill)).getG();
				Double S = ((BKTEntry) bkt_vals.get(currSkill)).getS();
				Double T = ((BKTEntry) bkt_vals.get(currSkill)).getT();
			
				if (!MasteryThreshold.stop(L, G, S, T)) {
					return false;
				}
			}
			return true;
		}

		
		
		/**
		 * Method to get the average mastery
		 * @return
		 */
		public int getAverageMastery(){
			Double sum=0.0;
			//for (Map.Entry<String, BKTEntry> entry : bkt_vals.entrySet()) {
			for (Map.Entry<String, BKTEntry> entry : bkt_view_vals.entrySet()) {

				BKTEntry value=entry.getValue();
				sum=sum+value.getL();
			}
			Double average=sum/bkt_view_vals.size();
			average=average*100;
			return average.intValue();
			
		}
		
		/**
		 * Method used to reset the bkt_view hashmap (i.e. the hashmap linked to the progress bars). 
		 * Used when student completes a section, to reset the bkt_view hashmap to its originals values, 
		 * so the bar goes down when passing a level. (once student applies a skill the proper value of bkt appears
		 * in the skillometer and progress bar).
		 */
		public void resetBKT_view_params(){
			
			for (Map.Entry<String, BKTEntry> entry : bkt_view_vals.entrySet()) {
			
				bkt_view_vals.get(entry.getKey()).setL(bkt_view_vals_init.get(entry.getKey()).getL());
			}	
			
		}
	
		/**
		 * Method that returns for each skill what is the master so far. Used 
		 * in the skillometer
		 * @return
		 */
		public Map<String, Integer> getMasteryPerSkill(){

	   		Map<String, Integer> skills = new LinkedHashMap<String, Integer>();
		   		
	   		
	   		for (Map.Entry<String, BKTEntry> entry : bkt_view_vals.entrySet()) {
				String key = entry.getKey();
				BKTEntry value=entry.getValue();
				Double lValue=100*value.getL();
				String caption=value.getDescription().replace("\n", "");
				skills.put(caption,  lValue.intValue() );
	   		}
				
		
	   		return skills;
		}
		
		
		
		
		/**
		 * Method to find goodness of fit
		 * Code is from http://www.columbia.edu/~rsb2162/edmtools.html (Baker et al 2012)	
		 * @param start
		 * @param end
		 * @param Lzero
		 * @param trans
		 * @param G
		 * @param S
		 * @return sum of squared residuals 
	 	*/
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
				// trace.out(SSR);
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
				if (studentStratified) N = Integer.MAX_VALUE;					//SETS N TO INT_MAX SO THAT IT IS STUDENT STRATIFIED
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

		/**
		 * Brute force approach to estimate model parameters.
		 * Code is from http://www.columbia.edu/~rsb2162/edmtools.html (Baker et al 2012)	
		 * @param curskill
		 */
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
							 * trace.out(Lzero); trace.out("\t");
							 * trace.out(trans);
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
					trace.out(".");
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
					trace.out(".");
					count = 0;
				}
			}
			String currSkill = skill_.get(startact);
			vals.put(currSkill, new Double[4]);
			vals.get(currSkill)[0] = bestLzero;
			vals.get(currSkill)[1] = bestG;
			vals.get(currSkill)[2] = bestS;
			vals.get(currSkill)[3] = besttrans;
			
			bkt_vals.put(currSkill, new BKTEntry(bestLzero,bestG,bestS,besttrans,""));
			trace.out("Training params for " + curskill+ ": ["+bestLzero+" " + bestG + " " + bestS + " " + besttrans + "]");
		}
		
		
		/**
		 * Method to train the BKT, using the brute force approach by Baker et al, 2012.
		 * @param infile_
		 */
		public void computelzerot(String infile_) {
			
			read_in_data(infile_);

			for (int curskill = 0; curskill <= skillnum; curskill++) {
				fit_skill_model(curskill);
			}
		}
		
		
		/**
		 * Method to read the data file, used for training
		 * @param infile
		 */
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
					right_.add(Integer.parseInt(currWord)*1.0);
					currWord = "";
					index = 0;
					currWord = "";
					actnum++;
					if (!skill_.get(actnum-1).equals(prevskill)) {
						prevskill = skill_.get(actnum-1);
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
				trace.err("Problem Reading Data File");
			}
		}
		
		
	/**
	 * Utility class to store the four BKT params
	 * @author nbarba
	 *
	 */
	  public class BKTEntry{
		  double L;
		  double T;
		  double G;
		  double S;
		  int numberOfCorrectOpportunities=0;
		  int numberOfIncorrectOpportunities=0;

		  String description;
		  
		  public BKTEntry(double L, double T, double G, double S, String desc){
			  
			  setL(L);
			  setT(T);
			  setG(G);
			  setS(S);
			  setDescription(desc);  		  
		  }
		  
		  void setL(double val){L=val;}
		  void setT(double val){T=val;}
		  void setG(double val){G=val;}
		  void setS(double val){S=val;}
		  void setDescription(String txt){description=txt;}
		  
		  void incNumberOfCorrectOpportunities(){  numberOfCorrectOpportunities=numberOfCorrectOpportunities+1;}
		  void resetNumberOfCorrectOpportunities(){ numberOfCorrectOpportunities=0;}
		  
		  void incNumberOfIncorrectOpportunities(){  numberOfIncorrectOpportunities=numberOfIncorrectOpportunities+1;}
		  void resetNumberOfIncorrectOpportunities(){ numberOfIncorrectOpportunities=0;}
		 
		  double getL(){return L;}
		  double getT(){return T;}
		  double getG(){return G;}
		  double getS(){return S;}
		  String getDescription(){return description;}
		  
	  }

		
}
