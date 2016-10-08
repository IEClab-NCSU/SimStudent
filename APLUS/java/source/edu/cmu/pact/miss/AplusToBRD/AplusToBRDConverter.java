package edu.cmu.pact.miss.AplusToBRD;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

/****
 * Class that takes the aplus logs tab delimited file (as extracted from CTAT), the student logs tab delimited file (as extracted from CTAT)
 * and merges them into one file that contains only the transactions required to create a BRD file. Output file contains 
 * "New Problem Entered", "Problem Restarted", "Step Undone" transactions from aplus log, and "Student Verified Correctness of SimStudent Action", "Hint Received" from student logs.
 * Important note; Input files (i.e. aplus and student logs) must be sorted by student anonymous id by tool_event_time for this to work. This way, output file is already sorted this way.
 * @author nbarba	
 *
 */
public class AplusToBRDConverter {

	
	/********************************
	 * Variables, getters and setters
	 ********************************/
	
	/*filename of aplus log tab delimited file*/
	String aplusFilename;
	private void setAplusFilename(String filename){	this.aplusFilename=filename;	}
	private String getAplusFilename(){ return this.aplusFilename;	}
	
	/*filename of student log tab delimited file*/
	String studentFilename;
	private void setStudentFilename(String filename){	this.studentFilename=filename;	}
	private String getStudentFilename(){ return this.studentFilename;	}
	
	/*filename of output log tab delimited file*/
	String outputFilename;
	private void setOutputFilename(String filename){	this.outputFilename=filename;	}
	private String getOutputFilename(){ return this.outputFilename;	}
	
	/*CF action col id on aplus log*/
	private int actionCol_aplus=-1;
	private void setActionCol_aplus(int col) { actionCol_aplus=col; }
	private int getActionCol_aplus(){ return actionCol_aplus; } 
	
	
	/*CF tool event time col id on aplus log*/
	private int timeCol_aplus=-1;
	private void setTimeCol_aplus(int col) { timeCol_aplus=col; }
	private int  setTimeCol_aplus(){ return timeCol_aplus; } 
	
	/*CF action col id on student log*/
	private int actionCol_stud=-1;
	private void setActionCol_stud(int col) { actionCol_stud=col; }
	private int getActionCol_stud(){ return actionCol_stud; } 
		
	/*CF tool event time col id on student log*/
	private int timeCol_stud=-1;
	private void setTimeCol_stud(int col) { timeCol_stud=col; }
	private int getTimeCol_stud(){ return timeCol_stud; } 
	
	/*Anonymous User ID col on both logs*/
	private int anonIDCol=3;
	private int getAnonIDCol(){ return anonIDCol; } 
	private void setAnonIDColr(int col){ this.anonIDCol=col; } 
	
	/*Problem Name col on aplus logs*/
	private int problemCol_aplus=-1;
	private void setProblemCol_aplus(int col) { problemCol_aplus=col; }
	private int getProblemCol_aplus(){ return problemCol_aplus; } 
	
	/*Problem Name col on student logs*/
	private int problemCol_stud=-1;
	private void setProblemCol_stud(int col) { problemCol_stud=col; }
	private int getProblemCol_stud(){ return problemCol_stud; } 
	
	/*String to store the previousAnonID (to detect if student was changed)*/
	private String previousAnonID="";	
	private void setPreviousAnonID(String anonID){	previousAnonID=anonID;	}	
	private String  getPreviousAnonID(){ return previousAnonID; } 
	
	/*flag that indicates if log buffer moved to new user*/
	boolean switchUserFlag=false;
	void setSwitchUserFlag(boolean flag){ this.switchUserFlag=flag;}
	boolean getSwitchUserFlag(){return switchUserFlag;}
	
	
	/*holds the next log transaction from the student logs*/
	private String nextTransaction_stud="";
	private void setNextTransaction_stud(String line){ this.nextTransaction_stud=line; } 
	private String getNextTransaction_stud(){ return this.nextTransaction_stud; } 
	
	/*holds the next log transaction from the student logs*/
	String nextTransaction_apl="";
	private void setNextTransaction_apl(String line){ this.nextTransaction_apl=line; } 
	private String getNextTransaction_apl(){ return this.nextTransaction_apl; } 
	
	/*Produce output so its ready for SimStudentBRDWriter (the class that actually produces BRD files)*/
	boolean convertOutputForBRDWritter=false;
	public void convertOutputForBRDWritter(boolean flag){ 	this.convertOutputForBRDWritter=flag;	}
	private boolean getConvertOutputForBRDWritter(){ return this.convertOutputForBRDWritter; } 
	
	int outcomeCol=-1;
	private void setOutcomeCol(int col) { this.outcomeCol=col;}
	private int getOutcomeCol(){return outcomeCol;}
		
	int cf_resultCol=-1;
	private void setCFResultCol(int col) { this.cf_resultCol=col;}
	private int getCFResultCol(){return cf_resultCol;}
	
	int selectionCol=-1;
	private void setSelectionCol(int col) { this.selectionCol=col;}
	private int getSelectionCol(){return selectionCol;}
	
	
	int inputCol=-1;
	private void setInputCol(int col) { this.inputCol=col;}
	private int getInputCol(){return inputCol;}
	
	int actionCol=-1;
	private void setActionCol(int col) { this.actionCol=col;}
	private int getActionCol(){return actionCol;}
	
	int skillCol=-1;
	private void setSkillCol(int col) { this.skillCol=col;}
	private int getSkillCol(){return skillCol;}
	
	
	
	public static final String BRDWRITTER_HEADER = "id\tstudent_name\tproblem_name\tinterface_action\tselection\taction\tinput\tskill\toutcome\tactualOutcome\n";
	 

	String aplusLog_name="";

	/*booleans indicating if eof occurred in student logs or aplus logs*/
	boolean studEOF=false;
	boolean aplusEOF=false;
	
	/*Queues for student log transactions and aplus log transactions*/
	Queue<String> stdQueue;
	Queue<String> aplQueue;
	
	
	public static final String CF_ACTION_HEADER = "CF (ACTION)";
	public static final String CF_TIME_HEADER = "CF (tool_event_time)";
	public static final String PROBLEM_NAME_HEADER = "Problem Name";
	public static final String SELECTION_HEADER = "Selection";
	public static final String ACTION_HEADER = "Action";
	public static final String INPUT_HEADER = "Input";
	public static final String SKILL_HEADER = "KC Category (ActualSkill)";
	public static final String CF_RESULT_HEADER = "CF (RESULT)";
	public static final String OUTCOME_HEADER = "Outcome";
	public static final String NEW_PROBLEM_ENTERED_ACTION = "New Problem Entered";
	public static final String PROBLEM_RESTARTED_ACTION = "Problem Restarted";
	public static final String STEP_UNDONE_ACTION = "Step Undone";
	public static final String STUDENT_RESPONSE = "Student Verified Correctness of Sim Student Input";
	public static final String STUDENT_DEMONSTRATION = "Hint Received";
	public static final int APLUS_LOG=1;
	public static final int STUDENT_LOG=2;
	public static final String RESULT_CORRECT = "Correct Action";
	public static final String RESULT_INCORRECT = "Error Action";
	public static final String OUTCOME_CORRECT = "CORRECT";
	public static final String OUTCOME_INCORRECT = "INCORRECT";
	public static final String BRD_CORRECT = "OK";
	public static final String BRD_INCORRECT = "UNTRACEABLE_ERROR";
	private static final int ACTUAL_SKILL_ID=29;
	private static final int DEFAULT_KC_ID=31;
	private static final int PROBLEM_KC_ID=33;
	private static final int STEP_KC_ID=35;
	private static final int CF_HINT_ID=48;
	private static final int CF_HINT_TYPE=49;
	private static final int CF_INFO=50;
	private static final int UNIQUE_STEP_ID=41;
	private static final int UNIQUE_STEP_CAT_ID=42;
	
	
	
	/************************
	 * Constructors
	 ************************/
	
	public AplusToBRDConverter(String aplusFilename, String studentFilename, String outputFilename){
		setAplusFilename(aplusFilename);
		setStudentFilename(studentFilename);		
		setOutputFilename(outputFilename);
		
		stdQueue=new LinkedList<String>();
		 
		aplQueue=new LinkedList<String>();
	}
	
	
	/************************
	 * Methods
	 * @throws IOException 
	 ************************/
	
	/***
	 * Method that moves the buffer for the student log file to the next "proper" transaction (proper meaning
	 * human responded to simstudent answer and hint received)
	 * @param br buffered reader for the student log file
	 * @return the next transaction
	 * @throws IOException
	 */
	private String getNextProperTransaction_student(BufferedReader br) throws IOException{
		String line;
		String returnString="";
				
		if (!stdQueue.isEmpty()){			
			setNextTransaction_stud(stdQueue.poll());
			return nextTransaction_stud;
		}
		

	  	while ((line = br.readLine()) != null) {
	  		if (this.isHumanDemonstration(line) || this.isHumanResponse(line)){ 			
         		returnString=line;    		
         		break;
	  		}	
	  		
	  	}	  	
	  	
	  
	  	if (returnString.equals("")){
	  		studEOF=true;
	  		return null;
	  	}
	  	else{	  	
	  		setNextTransaction_stud(returnString);
	  	}
	  	
		return returnString;
	}
	
	/**
	 * SimStudentBRDWriter expects outcome column to be either "OK" for correct actions
	 * or "ERROR" for incorrect actions, or "BUG" for buggy actions. This 
	 * 
	 * @param str
	 * @return
	 */
	private String codifyCorrectness(String str){
		String returnValue="";
		if (str.equals(RESULT_CORRECT) || str.equals(OUTCOME_CORRECT))
			returnValue=BRD_CORRECT;
		else if (str.equals(RESULT_INCORRECT) || str.equals(OUTCOME_INCORRECT)){
			returnValue=BRD_INCORRECT;
		}
		return returnValue;
	}
	
	/****
	 * Returns "OK" if student demonstrated step or pressed "Yes" so taht brd writer knows to advance to next node.
	 * In any other case, returns "Error" whihc indicates student pressed "No" so brd writer must not advance to next node
	 * 
	 * @param str
	 * @param cf_action
	 * @return
	 */
	private String proceedOrNot(String str,String cf_action){
		String returnValue="";
		
		if (cf_action.equals(STUDENT_DEMONSTRATION))
			returnValue=BRD_CORRECT;
		else{
			returnValue=codifyCorrectness(str);	
		}
		
		return returnValue;
	}
	
	
	/***
	 * Method that estimates the correctness of a step from the student table, using 
	 * CF(Result) (i.e. what student responded to simstudent), and Outcome (i.e. what oracle said about correctness
	 * of student response). Logic is : 
	 * For human student answers: 
	 * - If human student said YES and oracle said CORRECT then sai is CORRECT
	 * - If human student said YES and oracle said INCORRECT then sai is INCORRECT
	 * - If human student said NO and oracle said CORRECT then sai is INCORRECT
	 * - If human student said NO and oracle said INCORRECT then sai is CORRECT. 
	 * For human student demonstrations, whatever the oracle said.
	 * @param result
	 * @param outcome
	 * @return
	 */
	private String getCorrectness(String result, String outcome, String action){
		String returnString ="";
				
		if (action.equals(this.STUDENT_RESPONSE)){
			if (result.equals(RESULT_CORRECT) && outcome.equals(OUTCOME_CORRECT))
				returnString=BRD_CORRECT;
			else if (result.equals(RESULT_CORRECT) && outcome.equals(OUTCOME_INCORRECT)){
				returnString=BRD_INCORRECT;
			}
			else if (result.equals(RESULT_INCORRECT) && outcome.equals(OUTCOME_CORRECT)){
				returnString=BRD_INCORRECT;
			}	
			else if (result.equals(RESULT_INCORRECT) && outcome.equals(OUTCOME_INCORRECT)){
				returnString=BRD_CORRECT;
			}	
				
		}
		else{
			returnString=codifyCorrectness(outcome);
		}
		
		return returnString;
	}

	/***
	 * Method that moves the buffer for the aplus log file to the next "proper" transaction (proper meaning transactions 
	 * with CF(actions) "New Problem Entered", "Problem Restarted" and "Step Undone")
	 * @param br buffered reader for the aplus log file
	 * @return the next transaction 
	 * @throws IOException
	 */
	private String getNextProperTransaction_aplus(BufferedReader br) throws IOException{
		String line;
		String returnString="";
		
		if (!aplQueue.isEmpty()){
			//nextTransaction_apl=aplQueue.poll();
			setNextTransaction_apl(aplQueue.poll());
			return getNextTransaction_apl();
		}
		
	  	while ((line = br.readLine()) != null) {

	  	  	if (this.isProblemStarted(line) || this.isProblemRestarted(line) || this.isStepUndone(line)){
	            			
         		returnString=line;    		
         		break;
	  		}	
	  		
	  	}	  	
	  	
	
	  
	  	if (returnString.equals("")){ 
	  		aplusEOF=true;
	  		return null;
	  	}
	  	else{
	  		setNextTransaction_apl(returnString);
		  	aplusLog_name=getTransactionValue(getNextTransaction_apl(),this.getAnonIDCol());
	  	}
	  	
	  	return returnString;
	}
	

	
	/***
	 * Method that returns true if the aplus log transaction (i.e. first argument), is older than the student log transaction
	 * (i.e. second argument), based on CF(tool event time) value 
	 * 
	 * @param aplusLine transaction line from the aplus logs
	 * @param studentLine transaction line from the student log
	 * @return
	 * @throws ParseException
	 */
	public boolean isTransactionOldest(String aplusLine, String studentLine) throws ParseException{
				
		String aplus_name=this.getTransactionValue(aplusLine, this.getAnonIDCol());
		String stud_name=this.getTransactionValue(studentLine, this.getAnonIDCol());
		
		/*If names in two logs don't match then student log buffer moved to new user. In that case, queue the student log
		 * transaction and return the aplus log transaction as the oldest one */
		if (!aplus_name.equals(stud_name)){			
			setSwitchUserFlag(true);
			stdQueue.add(studentLine);
			return true;				
		}
		
		String aplusTimeStamp= getTime(aplusLine,APLUS_LOG); 
		String studentTimeStamp= getTime(studentLine,STUDENT_LOG); 
		
		boolean isAplusLogOldest=isOldest(aplusTimeStamp,studentTimeStamp);
		//if aplus is oldest, add studentLog transaction on the student Queue.
		if (isAplusLogOldest) {
			stdQueue.add(studentLine);		
		}
	
		return isAplusLogOldest;
			
	}	
	
	/***
	 * Checks if aplus current anon id is different that user we processed in previous iteration 
	 * @param anonId
	 * @return
	 */
	public boolean isNewUser(String anonId){
		return !anonId.equals(getPreviousAnonID());
	}
	

	private void extractColumnIDs(BufferedReader aplus_br, BufferedReader student_br, FileWriter fw) throws IOException{
	    String line=aplus_br.readLine();
     	setColumnID(line,APLUS_LOG);	
 		fw.write(adjustLine_aplus(line,"CF(ProblemSubmitted)", "CF(ProblemSubmitted_Type)") + "\n");
 		//writeToOutput(fw,line,APLUS_LOG);
 		line=student_br.readLine();
 		setColumnID(line,STUDENT_LOG);
		
	}
	
	/***
	 *  Main function that does all the processing (i.e. keeps the necessary transactions) of merging aplus and student logs and keeping the proper transactions.
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public boolean mergeAndPreprocessLogs() throws IOException, ParseException{
		
		 File aplusLogs = new File(getAplusFilename());
	     File studentLogs = new File(getStudentFilename());
	      FileWriter fw = new FileWriter(getOutputFilename());
	 
	        
	        try {
	        	BufferedReader aplus_br = new BufferedReader(new FileReader(aplusLogs));
	        	BufferedReader student_br = new BufferedReader(new FileReader(studentLogs));
     	
	    //read header lines from both files and retrieve column id for CF(action), CF(Problem Name) and CF(tool event time)
	       extractColumnIDs(aplus_br,student_br,fw);
 		
	    String line;
     	while (getNextProperTransaction_aplus(aplus_br) != null) {
     		
     	   //when processing header line, get the action column and time column order
     		     line=getNextTransaction_apl();
		
         		String anonId=getTransactionValue(line,this.getAnonIDCol());
       	
         		/*On first iteration set the user*/
         		if (getPreviousAnonID().equals("")){
         				System.out.println("Processing user " + anonId + " ..... ");	
         				setPreviousAnonID(anonId);
         		}
         		//if we found a new user to to student log and dump any proper residual transactions for that user.
         		if (isNewUser(anonId)){
         			System.out.println("Processing user " + anonId + " ......");
         			dumpResiduals_stud(student_br,getPreviousAnonID(),fw);
         			setPreviousAnonID(anonId);      		
         		}
         		
         		/*go to student log and proceed until you find a transaction that is not oldest that aplus log transaction*/
       		while(isTransactionOldest(line,getNextProperTransaction_student(student_br))==false && getSwitchUserFlag()==false){
       			//if we are here this means student log transaction is oldest, so write it to output file.        			
       			//fw.write(adjustLine_stud(getNextTransaction_stud()) + " \n");
       			writeToOutput(fw,getNextTransaction_stud(),STUDENT_LOG);
       			
       		}
       		//if we are here this means aplus log transaction is 
       		//fw.write(adjustLine_aplus(line) + " \n");
       		writeToOutput(fw,line,APLUS_LOG);
       	
       		
				
       	//if flag is true this means student log buffer moved to new user so dump any residual proper transactions
       	//from aplus log file for this user.
       	if (getSwitchUserFlag()){
       			dumpResiduals_aplus(aplus_br,getTransactionValue(line,this.getAnonIDCol()),fw);
       			setSwitchUserFlag(false);
       		
       	}
       		

         	
     	}
     	 if (studEOF==false)
     		 dumpResiduals_stud(student_br,getPreviousAnonID(),fw);

     	aplus_br.close();
     	student_br.close();
     	fw.close();
     } catch (FileNotFoundException e) {
         e.printStackTrace();
         return false;
     }		  
     	
		return true;
	}
	

	/***
	 * Method that takes the output of the mergAndProceessLogs (which is one tab delimited file that has all the necessary actions for all users from aplus and student 
	 * tables to extract the brd's), and produces a simpler file that contains only the necessary columns the BRDWritter needs to create a graph. 
	 *  
	 * @param filename input filename
	 * @param outputFilename output filename
	 * @throws IOException
	 */
	public void convertToBRDWriterFormat(String filename,String outputFilename) throws IOException {
		
		 BufferedReader br= new BufferedReader(new FileReader(filename));
		 FileWriter fw = new FileWriter(outputFilename);
		   
		 fw.write(this.BRDWRITTER_HEADER);
		 String line= br.readLine();
		 setColumnID(line,APLUS_LOG);
		 

		 int i=0;
		 while ((line= br.readLine()) != null){
			 
			 	String[] tokens = line.split("\t");
	            String id = tokens[2];
	            String student = tokens[this.getAnonIDCol()];
	            String problemName = tokens[this.getProblemCol_aplus()].replace("'","");
	            String skill = tokens[getSkillCol()];
	            String selection = tokens[getSelectionCol()];
	            String action = tokens[getActionCol()];
	            String input = tokens[getInputCol()].replace("'","");
	            String outcome = tokens[getOutcomeCol()];
	            String result= tokens[getCFResultCol()];
	            String cf_action= tokens[this.getActionCol_aplus()];
	            String finalOutcome= getCorrectness(result, outcome, cf_action);
	            	            
	            /*final outcome actually has if the action is correct or not. NOT if we must advance or not...*/
	            
	            writeToFile(fw, id, student, problemName, cf_action, selection, action, input, skill, proceedOrNot(result,cf_action), finalOutcome );

		  }
		  br.close();
		  fw.close();
	}
	
	/***
	 * File that will be the input to the AplusToBRDWriter.
	 * @param out		filewriter
	 * @param id		transaction id
	 * @param student	student anonymous id
	 * @param problem	problem name
	 * @param cf_action	cf action (needed to detect in the BRDWriter if we have new problem, problem restart, step undone
	 * @param selection	selection
	 * @param action	action
	 * @param input		input
	 * @param skill		skill
	 * @param proceed	this indicates if graph should proceed or not ( BRDWriter proceeded to next node ONLY IF an sai is marked as correct. Now this "proceed" will determine if BRD writter should proceed to next node)
	 * @param actualOutcome	this is the actual outcome of a transaction (if its correct or not). Please note that correct does not necessarily mean advance to the next node.
	 * @throws IOException
	 */
	 private void writeToFile(FileWriter out, String id, 
	            String student, String problem, String cf_action, String selection, String action,
	            String input, String skill, String proceed, String actualOutcome) throws IOException {
	        
	        String log = id + "\t"; 
	        log += student + "\t";
	        log += problem + "\t";
	        log += cf_action + "\t";
	        log += selection + "\t";
	        log += action + "\t";
	        log += input + "\t";
	        log += skill + "\t";
	        log += proceed + "\t";
	        log += actualOutcome + "\n";
       
	        out.write(log);
	
	    }
	 
	 
	 
	 /**
	  * Method that writes to the output file.
	  * @param fw
	  * @param line
	  * @param originLog
	  * @throws IOException
	  */
	private void writeToOutput(FileWriter fw, String line, int originLog) throws IOException{	
		if (originLog==APLUS_LOG)
			fw.write(adjustLine_aplus(line) + " \n");
		else if (originLog==STUDENT_LOG)
			fw.write(adjustLine_stud(line) + " \n");
		
	}
	
	
	/***
	 *  Method that writes to output file any residual entries from the aplus logs for a specific user.
	 *  Called when student log buffer moves to a new user.
	 * @param br	buffered reader for the student logs
	 * @param anonID	user id of user we want to dump the residuals
	 * @param fw	output file file writer
	 * @throws IOException
	 */
	private void dumpResiduals_aplus(BufferedReader br,String anonID,FileWriter fw) throws IOException{

		for (int i=0;i<aplQueue.size(); i++){

			//String tm=aplQueue.poll();
			//fw.write(adjustLine_aplus(tm) + " \n");
			writeToOutput(fw,aplQueue.poll(),APLUS_LOG);

		}
	
		if (aplusEOF==false){
		while(getTransactionValue(getNextProperTransaction_aplus(br),this.getAnonIDCol()).equals(anonID)){
	        	//fw.write(adjustLine_aplus( getNextTransaction_apl() + " \n"));
	        	writeToOutput(fw,getNextTransaction_apl(),APLUS_LOG);
	        	
	        	
	    		
	    }
		
		this.setPreviousAnonID(this.aplusLog_name);
		
		aplQueue.add(getNextTransaction_apl());
		}
	}
	
	/***
	 * Method that writes to output file any residual entries from the student logs for a specific user.
	 * Called when aplus log buffer moves to a new user.
	 * @param br	buffered reader for the student logs
	 * @param anonID user id of user we want to dump the residuals
	 * @param fw	output file file writer
	 * @throws IOException
	 */
	private void dumpResiduals_stud(BufferedReader br,String anonID,FileWriter fw) throws IOException{

		for (int i=0;i<stdQueue.size(); i++){
			//fw.write(adjustLine_stud(stdQueue.poll()) + " \n");
			writeToOutput(fw,stdQueue.poll(),STUDENT_LOG);
			
		}
		
		if (studEOF==false){
			while(getTransactionValue(getNextProperTransaction_student(br),this.getAnonIDCol()).equals(anonID)){
	        	//fw.write(adjustLine_stud(getNextTransaction_stud() + " \n"));
	        	writeToOutput(fw,getNextTransaction_stud(),STUDENT_LOG);
	        
			}
		
			stdQueue.add(getNextTransaction_stud());
		}

	}
	
	

	/***
	 * Scans the first line and detect the the necessary columns
	 * @param line a transaction line
	 * @param log the log this line came from (APLUS_LOG or STUDENT_LOG)
	 */
	private void setColumnID(String line, int log){
		String[] array = line.split("\\t");

		for (int i=0;i<array.length;i++){		
			if (array[i].equals(CF_ACTION_HEADER)){
				if (log==APLUS_LOG)
					setActionCol_aplus(i);		
				else if (log==STUDENT_LOG)
					setActionCol_stud(i);
			}
			else if (array[i].equals(CF_TIME_HEADER)){
				
				if (log==APLUS_LOG)
					setTimeCol_aplus(i);
				else if (log==STUDENT_LOG)
					setTimeCol_stud(i);
			}	
			else if (array[i].equals(PROBLEM_NAME_HEADER)){
				
				if (log==APLUS_LOG)
					setProblemCol_aplus(i);
				else if (log==STUDENT_LOG)
					setProblemCol_stud(i);
			}
			else if (array[i].equals(OUTCOME_HEADER)){
					setOutcomeCol(i);
			}
			else if (array[i].equals(SELECTION_HEADER)){
				setSelectionCol(i);
			}
			else if (array[i].equals(CF_RESULT_HEADER)){
				this.setCFResultCol(i);
			}
			else if (array[i].equals(SKILL_HEADER)){
				setSkillCol(i);
			}
			else if (array[i].equals(ACTION_HEADER)){
				setActionCol(i);
			}
			else if (array[i].equals(INPUT_HEADER)){
				setInputCol(i);
			}
			
		}
	}

	
	/**check if line has the desired action*/
	private boolean isProblemStarted(String line){
		String[] array = line.split("\\t");	
		return (array[getActionCol_aplus()].equals(NEW_PROBLEM_ENTERED_ACTION) );	
	}
	
	/**check if line has the desired action*/
	private boolean isStepUndone(String line){
		String[] array = line.split("\\t");	
		return (array[getActionCol_aplus()].equals(STEP_UNDONE_ACTION) );	
	}
	
	
	/**check if line has the desired action*/
	private boolean isHumanDemonstration(String line){
		String[] array = line.split("\\t");	
		return (array[getActionCol_stud()].equals(STUDENT_DEMONSTRATION) );	
	}
	
	
	private boolean isHumanResponse(String line){
		String[] array = line.split("\\t");	
		return (array[getActionCol_stud()].equals(STUDENT_RESPONSE) );	
	}
	

	/**check if line has the desired action*/
	private boolean isProblemRestarted(String line){
		String[] array = line.split("\\t");	
		return (array[getActionCol_aplus()].equals(this.PROBLEM_RESTARTED_ACTION) );	
	}
	
	
	private String getTime(String line,int log){
		String[] array = line.split("\\t");
		int col=-1;
		if (log==APLUS_LOG)
				col=setTimeCol_aplus();
		else if (log==STUDENT_LOG)
				col=getTimeCol_stud();
		
		return (array[col]);
	}
	
	private String getTransactionValue(String line, int col){
		if (line==null) return "noTrans";
		String[] array = line.split("\\t");
		return (array[col]);
	}
	

	/**
	 *  Method that returns true if time1 string is oldest that time 2. Both strings should be in format "yyyy-MM-dd-HH:mm:ss.SSS"
	 * @param time1
	 * @param time2
	 * @return true is time1 is older than time 2
	 * @throws ParseException
	 */
	private static boolean isOldest(String time1, String time2) throws ParseException{	
		SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");	    
	    Date time1Date = s1.parse(time1);
	    Date time2Date = s1.parse(time2);   
	    return (time1Date.before(time2Date));
	}
	

	/**
	 * Adjust the line of the student logs so the output file has same number of columns
	 * @param line
	 * @return
	 */
	public static String adjustLine_stud(String line){
		String newString="";
		String[] array = line.split("\\t");	
		
		String[] newArray= new String[array.length+3];
			
		int j=0;
		for (int i=0;i<newArray.length;i++){
			

			
			
			if (i!=CF_HINT_ID && i!=CF_HINT_TYPE && i!=CF_INFO){
					newArray[i]=array[j++];	
					
			}
			else{
				newArray[i]="";	
		
			}
			
		}

		
		newString=arrayToString(newArray,"\t");
		return newString;
	}

	/**
	 * Reorders knowledge component columns of aplus logs so that output file KC columns match (they are in different order for
	 * student table).
	 * @param array
	 * @return
	 */
	public static String[] reOrderKC(String[] array){
		String[] newArray=new String[array.length];
		
		
		 System.arraycopy(array, 0, newArray, 0, array.length);


		 newArray[ACTUAL_SKILL_ID]=array[STEP_KC_ID];
		 newArray[ACTUAL_SKILL_ID+1]=array[STEP_KC_ID+1];
		 newArray[DEFAULT_KC_ID] = array[PROBLEM_KC_ID];
		 newArray[DEFAULT_KC_ID+1] = array[PROBLEM_KC_ID+1];
		 newArray[PROBLEM_KC_ID]=array[DEFAULT_KC_ID];
		 newArray[PROBLEM_KC_ID+1]=array[DEFAULT_KC_ID+1];
		 newArray[STEP_KC_ID]=array[ACTUAL_SKILL_ID];		
		 newArray[STEP_KC_ID+1]=array[ACTUAL_SKILL_ID+1];	
		 
		return newArray;
	}
	
	/**
	 * Adjust line for aplus logs, so taht output file has same number of columns. 
	 * @param line
	 * @param head1
	 * @param head2
	 * @return
	 */
	public static String adjustLine_aplus(String line, String head1, String head2){
		String newString="";
		String[] array = line.split("\\t");	
		
		String[] newArray= new String[array.length+2];
		
	
		int j=0;
		for (int i=0;i<newArray.length;i++){
			
			
			if (i!=UNIQUE_STEP_ID && i!=UNIQUE_STEP_CAT_ID){
					newArray[i]=array[j++];						
			}
			else{
				if (i==UNIQUE_STEP_ID)
					newArray[i]=head1;	
				else
					newArray[i]=head2;
			}
			
		}
		

		
		newString=arrayToString(reOrderKC(newArray),"\t");
		return newString;
	}
	
	public static String adjustLine_aplus(String line){	
		return adjustLine_aplus(line,"","");
	}
	
	
	public static String arrayToString(String[] array, String delimiter) {
	    StringBuilder arTostr = new StringBuilder();
	    if (array.length > 0) {
	        arTostr.append(array[0]);
	        for (int i=1; i<array.length; i++) {
	            arTostr.append(delimiter);
	            arTostr.append(array[i]);
	        }
	    }
	    return arTostr.toString();
	}
	
	
	
	public static void main(final String[] args) throws IOException, ParseException {
		/*Note: before running the AplusToBRDWriter function call, make sure that file StartNodeMsgTemplate.txt corresponds to the tutoring interface used to create the log files!*/

		AplusToBRDConverter logConverter = new AplusToBRDConverter("/Users/simstudent/Documents/workspace/Log2Brd/aplus_all_all1.txt","/Users/simstudent/Documents/workspace/Log2Brd/student_all.txt","/Users/simstudent/Documents/workspace/Log2Brd/output_teliki_problem1.txt");
		//logConverter.mergeAndPreprocessLogs();
		//System.out.println("Converting logs to BRDWriter format...");
		//logConverter.convertToBRDWriterFormat("/Users/simstudent/Documents/workspace/Log2Brd/output_teliki_problem1.txt","/Users/simstudent/Documents/workspace/Log2Brd/outBrd.txt");
		//System.out.println("Creating BRD files...");
		AplusToBRDWriter ssBRDTest = new AplusToBRDWriter();
		ssBRDTest.testFunc("/Users/simstudent/Documents/workspace/Log2Brd/outBrd.txt", "/Users/simstudent/Documents/workspace/Log2Brd/tmp");
		  
		
		 
    /*    if (args.length != 2) {
            System.out.println("Usage: AplusToBRDConverter <input_file> <output_file> <output_dir>");
            System.out.println("Specify the aplus table, the student table, the output file to save all the necessary transactions from these two tables");
            System.exit(-1);
        }
        
       	try{
       		AplusToBRDConverter logConverter = new AplusToBRDConverter(args[0],args[1],args[3]);
    		logConverter.mergeAndPreprocessLogs();
       	}catch(IOException e){
       	    e.printStackTrace();
       	}
		*/
		
		System.out.println("done!");
	
	}
	
	

	
}
