package edu.cmu.pact.miss.PeerLearning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.minerva_3_1.StepAbstractor;
//import edu.cmu.pact.miss.PeerLearning.SimStLogger.LogEntry;

public class SimStLoggingAgent {
	public static String PROBLEM="Problem";
	public static String PROBLEMTYPE="ProblemType";
	public static String QUIZ="Quiz";
	public static String FEEDBACK="Feedback";
	public static String RESOURCE="Resource";
	private static final String FEEDBACK_REQUEST = "feedback-request";
	private static final String HINT_REQUEST = "hint-request";
	private static final String COG_HINT="Demonstrate Step";
	
	private static SimStLoggingAgent instance = null;
	private  Queue<LogEntry> logBuffer;
	private LogEntry hintLogEntry;
	private LogEntry hintLogEntryRep;
	private LogEntry hintLogEntryRequest;
	
	/*Hash map to store what is the current values of the start state elements */
	private LinkedHashMap<String,String> ssElementValues;
	
	
	/* Hash map to store the metatutor suggestion. As suggestion may change (when rule activated) 
	 * for safety reasons it is wise to store the actual suggestion when the hint was given.
	 * So two structures are used, one to store the "live" value as send by the jess function and
	 * one to store the actual value when the hint was given
	 * */
	private LinkedHashMap<String,String> mtElementValuesHashLive;	
	private LinkedHashMap<String,String> mtElementValuesHash;
	public String e="";
	private static BR_Controller brController;
	
    /* */
    class LogEntry {
    	public String actionType;
    	public String action; 
    	public String step;
    	public String result;
    	public Object resultDetails;
    	public Sai sai; 
    	public ProblemNode node;
    	public String correctness;
    	public String expSelection;
    	public String expAction;
    	public String expInput;
    	public int duration;
    	public String feedback;
    	public String opponent;
    	public String info;
    	public int myRating;
    	public String event_time;
    	
    	public LogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
  		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating){
    		this.actionType=actionType;
       	this.action=action; 
      	this.step=step;
      	this.result=result;
      	this.resultDetails=resultDetails;
      	this.sai=sai; 
      	this.node=node;
      	this.correctness=correctness;
      	this.expSelection=expSelection;
      	this.expAction=expSelection;
      	this.expInput=expInput;
      	this.duration=duration;
      	this.feedback=feedback;
      	this.opponent=opponent;
      	this.info=info;
      	this.myRating=myRating;
    		
    	}
    	
    	public LogEntry(){}
    	
    	public void fill(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
          		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){
    		this.actionType=actionType;
           	this.action=action; 
          	this.step=step;
          	this.result=result;
          	this.resultDetails=resultDetails;
          	this.sai=sai; 
          	this.node=node;
          	this.correctness=correctness;
          	this.expSelection=expSelection;
          	this.expAction=expSelection;
          	this.expInput=expInput;
          	this.duration=duration;
          	this.feedback=feedback;
          	this.opponent=opponent;
          	this.info=info;
          	this.myRating=myRating;
          	this.event_time=event_time;    	
    	}
    	
    	public void clear(){
    		this.actionType=actionType;
           	this.action=action; 
          	this.step=step;
          	this.result=result;
          	this.resultDetails=resultDetails;
          	this.sai=null; 
          	this.node=null;
          	this.correctness="";
          	this.expSelection="";
          	this.expAction="";
          	this.expInput="";
          	this.duration=0;
          	this.feedback="";
          	this.opponent="";
          	this.info="";
          	this.myRating=0;
          	this.event_time="";
    	}
    	
    	
    }
  
   
	
	
	public static SimStLoggingAgent getInstance(BR_Controller newBrController){
			if (instance==null){
						instance= new SimStLoggingAgent(newBrController);
			}
			return instance;
	}
	
	
	/*Called from GetMTProblemSuggestion SimStLoggerController will always be initialized when this is
	* called, so no need to pass the BR_Controller
	*/
	public static SimStLoggingAgent getInstance(){
		return instance;
	}
	
	

	/*This will never get called, its here just in case...*/
	public SimStLoggingAgent(){
		/*Initialize all the internal structures*/
		logBuffer = new LinkedList<LogEntry>();	
		LogEntry hintLogEntry = new LogEntry();
		LogEntry hintLogEntryRep = new LogEntry();
		LogEntry hintLogEntryRequest = new LogEntry();
		
		ssElementValues=new LinkedHashMap<String,String>();
		mtElementValuesHash=new LinkedHashMap<String,String>();
		mtElementValuesHashLive=new LinkedHashMap<String,String>();
		
	}
	
	public SimStLoggingAgent(BR_Controller newBrController){
		/*Initialize all the internal structures*/
		logBuffer = new LinkedList<LogEntry>();	
		LogEntry hintLogEntry = new LogEntry();
		LogEntry hintLogEntryRep = new LogEntry();
		LogEntry hintLogEntryRequest = new LogEntry();
		ssElementValues=new LinkedHashMap<String,String>();
		mtElementValuesHash=new LinkedHashMap<String,String>();
		mtElementValuesHashLive=new LinkedHashMap<String,String>();
	
		this.brController=newBrController;
		 
	}
	
	
	
	
	/*Used  when the meta-tutor gives a problem hint, to copy the values form the live hash to the regular hash*/
	public void copy_mtElementValuesHash(){
	
		 Set set = mtElementValuesHashLive.entrySet();
		 Iterator it = set.iterator();
		   
		 while (it.hasNext()) {
		     Map.Entry entry = (Map.Entry) it.next();  		     
		     String key= (String) entry.getKey();
		     String value= (String) entry.getValue(); 
		     mtElementValuesHash.put(key, value );
		 } 
		 
		 
	}
	
	
	/* boolean if metatutor suggests exact type of equation or not
	 * This must be replaced with working memory
	 * */
	public String resovleHint(String feedback, String result){
		
		//System.out.println(feedback + " - " + result);

		if (result.contains(HINT_REQUEST) || result.contains("quiz") || result.contains("resource")){
			if (feedback.contains("Quiz button")) return this.QUIZ;
			else if (result.contains("demonstrate")) return this.COG_HINT;
			else if (feedback.contains("similar")) return this.PROBLEMTYPE;	
			else if (result.contains("resource")) return this.RESOURCE;	
			else return this.PROBLEM; 
		}
		else{
			return this.FEEDBACK;			
		}
		
		
	}
		
	private boolean hintFollowed=false;
	
	public boolean isHintFollowed(){
		return hintFollowed;
	}
	
	private boolean isHintRepeated=false;
	
	public void setIsHintRepeated(boolean value){
		
		this.isHintRepeated=value;
	}
	
	public boolean getIsHintRepeated(){
		
		return this.isHintRepeated;
	}
	
	
	
	public String get_isHintFollowedString() {
		if (getIsHintRepeated()) return "Repeated";
		if (getHintType()==FEEDBACK ||getHintType()==COG_HINT ) return "Not Tracked";
		if (hintFollowed) return "Followed";
		else return "Not followed";		
	}
	
	
	
	public void keepTrackOfssElements(String actionType, String action, Sai sai){
		
		/*constantly update logController with start state element values*/
    	if (actionType==SimStLogger.SIM_STUDENT_METATUTOR && action==SimStLogger.METATUTOR_MODEL_TRACING_ACTION){	 		
    		if (sai.getA().equals("UpdateTable")){
    	    		this.update_ssElements(sai.getS(),sai.getI());   		 
    	    }
    	
    	}
	}
	
	
	
	
	public LogEntry getHintLogEntry(){
		/*When in repeated mode, just bring back the repeated version...*/
		if (this.getIsHintRepeated()) return hintLogEntryRep;
		return hintLogEntry;
		
	}
	
	public LogEntry getHintLogRequestEntry(){
		return this.hintLogEntryRequest;
		
	}
	

	
	public String getFeedbackSuggestion(String feedback){	
		if (feedback.contains("Yes")) return "Yes";
		else return "No";
			
	}
	
	private String feedbackSuggestion="";
	
	public String getFeedBackSuggestion(){
		return feedbackSuggestion;
	}
	
	public void setFeedbackSuggestion(String value){
		this.feedbackSuggestion=value;
	}
	
	
	private String requestType="";
	public void setRequestType(String requestType){
		
		this.requestType=requestType;
	}
	
	public String getRequestType(){
		return this.requestType;
	}
	
	
	public String get_isHintRequestFollowed(){
		return "";
	}

	public boolean handleHintRequestLogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
    		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){
	
		if (action == SimStLogger.METATUTOR_HINT_REQUESTED){
			this.setRequestType((String) resultDetails);
			System.out.println("We have a hint request of type "+resultDetails+" , just logging it...");
			if (hintLogEntryRequest==null) {  hintLogEntryRequest= new LogEntry(); }
			else hintLogEntryRequest.clear();
			this.hintLogEntryRequest.fill(actionType, action, step, result,feedback , sai, node, correctness, expSelection, expAction, expInput, duration, (String) resultDetails, opponent, info, myRating, event_time); 
			return false;
		}
	
	return true;
	}
	
	private String anticipatedResource="";
	public void setAnticipatedResource(String result){
		if (result.contains("example")) anticipatedResource=AplusPlatform.exampleTabTitle;
		else if (result.contains("overview")) anticipatedResource=AplusPlatform.overviewTabTitle.replaceAll(" ", "");
		else if (result.contains("bank")) anticipatedResource=AplusPlatform.overviewTabTitle.replaceAll(" ", "");
		else  anticipatedResource="Unknown resource";
			
	}
	public String getAnticipatedResource(){
		return anticipatedResource;
	}
	public boolean handleHintLogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
    		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){
		

		/*when a meta-tutor hint is given figure out how to handle it*/
    	if (action == SimStLogger.METATUTOR_HINT_ACTION /*&& step == "START"*/){
 	
    		if (!getHintGiven()){ 	//If no hint was given in the past
    			if (resovleHint(feedback,result) == QUIZ){   //If the hint is "Take QUIZ"
    							//System.out.println("We have a Quiz, waiting for quiz button...");
    							setHintType(QUIZ);
    							setHintGiven(true);	
    							setTimeToLogHint(false);
    							setIsHintRepeated(false);
    							if (hintLogEntry==null) {  hintLogEntry= new LogEntry(); }
    							else hintLogEntry.clear();
    							this.hintLogEntry.fill(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time); 
    			}
    			else if(resovleHint(feedback,result) == FEEDBACK){ 
    							// return TRUE; 
    							//System.out.println("We have a Feedback hint... just log it");
    							setFeedbackSuggestion(getFeedbackSuggestion(feedback));
        						setHintType(FEEDBACK);	
        						setTimeToLogHint(true);
        						setIsHintRepeated(false);
        						if (hintLogEntry==null) {  hintLogEntry= new LogEntry(); }
        						else hintLogEntry.clear();
        						this.hintLogEntry.fill(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time);
					
    			}
    			else if(resovleHint(feedback,result) == RESOURCE){	//this might not be necessary as now I have a new function to handle cognitive and metacognitive hints
					//SAI is empty so how should I now what Mr Williams wants ? 
    				setAnticipatedResource(result);
					//System.out.println("We have a resource usage hint ... waiting for "+ getAnticipatedResource() +" tab...");
					setFeedbackSuggestion(getFeedbackSuggestion(feedback));
					setHintType(RESOURCE);	
					setTimeToLogHint(false);
					setIsHintRepeated(false);
					setHintGiven(true); 	
					if (hintLogEntry==null) {  hintLogEntry= new LogEntry(); }
					else hintLogEntry.clear();
					this.hintLogEntry.fill(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time);
	
    			}
    			else if(resovleHint(feedback,result) == COG_HINT){	//this might not be necessary as now I have a new function to handle cognitive and metacognitive hints
    					//SAI is empty so how should I now what Mr Williams wants ? 
    					//System.out.println("We have a demonstration hint ... just log it");
    					setFeedbackSuggestion(getFeedbackSuggestion(feedback));
    					setHintType(COG_HINT);	
    					setTimeToLogHint(true);
    					setIsHintRepeated(false);
    					if (hintLogEntry==null) {  hintLogEntry= new LogEntry(); }
    					else hintLogEntry.clear();
    					this.hintLogEntry.fill(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time);
		
    			}
    			else{
    						//System.out.println("We have a Problem, waiting for yes button.... ");
    			
    						setHintType(resovleHint(feedback, result));  			
    						//copy_mtElementValuesHash();  	//safely store Mr Williams suggestion 
    						
    						
    						 
    						String[] fr=this.extractEquation(feedback);
    						String[] f1=fr[0].split("\\/");
    						String[] f2=fr[1].split("\\/");
    						mtElementValuesHash.put("dorminTable1_C1R1", f1[0] );
    						mtElementValuesHash.put("dorminTable1_C1R2", f1[1] );
    						mtElementValuesHash.put("dorminTable2_C1R1", f2[0] );
    						mtElementValuesHash.put("dorminTable2_C1R2", f2[1] );
    						
    						setHintGiven(true); 
    						setTimeToLogHint(false);
    						setIsHintRepeated(false);
    						if (hintLogEntry==null) { hintLogEntry= new LogEntry();	}
    						else hintLogEntry.clear();
    						this.hintLogEntry.fill(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time); 		
    			}
    			
    			return false;  //we kept the action internally and we do not want it to be logged so return false to notify the logger;
    		}
    		else{	//Student clicked again on Mr Williams so we have a repeated hint. 
    				
    				//	System.out.println("We have a Repeated hint... just log it");
						setHintType(getHintType());	
						setIsHintRepeated(true);
						setTimeToLogHint(true);
						if (hintLogEntryRep==null) {  hintLogEntryRep= new LogEntry(); }
						else hintLogEntryRep.clear();
						this.hintLogEntryRep.fill(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time);

				return false; //This should also return false because we saved the repeated hint and it will be logged instantly. If you remove this line, log will have a double entry.
    		}
    		
    	}
    	
    	
    	if (getHintGiven()){
    		//System.out.println(action);
    			/*If Student clicked yes or no button then */
    			if (isYesNoButtonClicked(actionType,action,sai)){
    				/*If student clicked yes button AND we are in problem hint mode*/
    				if ((getHintType() == PROBLEM || getHintType() == PROBLEMTYPE) && (isActionYesButton(actionType,action,sai))){
    						hintFollowed=isProblemHintFollowed();  }
    				else if (getHintType() == FEEDBACK ){
    						/*Do nothing, we do not want to track this as followed or not*/ }
    				else{  hintFollowed=false; }		
    				resetFlagsToLog();	
    				return false;
    			}
    			
    			
    			if (action.equals(SimStLogger.QUIZ_BUTTON_ACTION)||(action.equals(SimStLogger.UNTAKEN_QUIZ_INITIATE_ACTION))){	
    				if (getHintType() == QUIZ){	
    					hintFollowed=true; 	}
    				else /*if (getHintType() == PROBLEM || getHintType() == PROBLEMTYPE)*/{
    					hintFollowed=false;	}    		    			
    				resetFlagsToLog();
    				return false;
    			}
    			
    			//The "TabClicked" is hardcoded in SimStPLEActionListener, line 191
    			if (sai!=null){
    			if (sai.getA().equals("TabClicked")){	
    				if (getHintType()==RESOURCE){ //this should be activated ONLY if a resource hint is given and only then. E.g. if a quiz hint is given and student goes through the tabs, then it is not 
    											 //considered as not followed.
    					System.out.println(anticipatedResource + "=" + sai.getS());
    					if (sai.getS().contains(this.anticipatedResource)){	
    						hintFollowed=true; 	}
    					else {
    						hintFollowed=false;	
    					}    		    			
    					resetFlagsToLog();
    				return false;
    				}	
    			}
    			}
    	
    			
    	}
    	
   	
    	return true; // either not a hint log OR hint action was logged during this function call so continue to log whatever you were logging....
		
	}
	
	public void resetFlagsToLog(){
		setHintGiven(false);
		setTimeToLogHint(true);
		setIsHintRepeated(false);
		
	}
	
	public void clear_mtElementValuesHash(){
		mtElementValuesHash.clear();	
	}
	
	/** Function that takes hash tables storing a) student entered values on start state elements and b) metatutor suggestion
	 *  for start state elements, and translates them to abstract values (for when the metatutor suggests a type of problem). 
	 * */
	public void abstractHashes(){

    	StepAbstractor abstractor = new StepAbstractor();
		String abstracted;
		String abstracted1;
		/*Abstract start state elements hash*/
		 Set set = ssElementValues.entrySet();
		 Iterator it = set.iterator();
		   
		 while (it.hasNext()) {
		     Map.Entry entry = (Map.Entry) it.next();  		     
		     String key= (String) entry.getKey();
		     String value= (String) entry.getValue(); 
		     
		     abstracted = abstractor.signedAbstraction(value);
		     
		     ssElementValues.put(key, abstracted);
		 } 
	
		 
		 /*Abstract fist hash*/
		 
		 Set set1 = mtElementValuesHashLive.entrySet();
		 Iterator it1 = set1.iterator();
		   
		 while (it1.hasNext()) {
		     Map.Entry entry1 = (Map.Entry) it1.next();  		     
		     String key1= (String) entry1.getKey();
		     String value1= (String) entry1.getValue(); 
		     
		     abstracted1 = abstractor.signedAbstraction(value1);
		     //System.out.println("Abstraction:["+value1 + "] --> [" + abstracted1+"]");
		     
		     mtElementValuesHash.put(key1, abstracted1 );
		 } 
		 
		 
		 
		
		
		
		
	}
	

	public boolean isProblemHintFollowed(){
		if (this.getHintType()==PROBLEM){
				return this.mtElementValuesHash.equals(this.ssElementValues);
		}
		else {
				abstractHashes();	//convert the equations in abstractions as now we want to compare abstract types
				return this.mtElementValuesHash.equals(this.ssElementValues);
		}
	
	}
	
	private boolean timeToLogHint=false;
	
	
	
	/**Flag to keep track if a hint was given or not*/
	private boolean hintGiven=false;
	public boolean getHintGiven(){ 	return hintGiven; }
	public void setHintGiven(boolean value){ this.hintGiven=value; 	}
	
	
	/** function that reads the log entry (to be logged) and determines if the user pressed the yes button or not
	 * 
	 * */
	public boolean isActionYesButton(String actionType, String action, Sai sai){		
		if (actionType==SimStLogger.SIM_STUDENT_METATUTOR && action==SimStLogger.METATUTOR_MODEL_TRACING_ACTION){		
    		if (sai.getS()=="yes" && sai.getA()=="ButtonPressed")
    					return true;
		}
		
		
		return false;
	}
	
	public boolean isActionNoButton(String actionType, String action, Sai sai){		
		if (actionType==SimStLogger.SIM_STUDENT_METATUTOR && action==SimStLogger.METATUTOR_MODEL_TRACING_ACTION){		
    		if (sai.getS()=="no" && sai.getA()=="ButtonPressed")
    					return true;
		}
		return false;
	}
	
	
	public boolean isYesNoButtonClicked(String actionType, String action, Sai sai){		
		if (actionType==SimStLogger.SIM_STUDENT_METATUTOR && action==SimStLogger.METATUTOR_MODEL_TRACING_ACTION){		
    		if ((sai.getS()=="yes" && sai.getA()=="ButtonPressed") || (sai.getS()=="no" && sai.getA()=="ButtonPressed"))
    					return true;
		}
		return false;
	}
	
	
	
	public LinkedHashMap<String,String> get_mtElementValuesHash(){	
		return mtElementValuesHash;
	}

	public LinkedHashMap<String,String> get_mtElementValuesHashLive(){	
		return mtElementValuesHashLive;
	}
	
	public Queue<LogEntry> getLogBuffer(){
		return logBuffer;
	}
	
	/**
	 * returns true if we must temporarily store the dialog message until the yes button is clicked
	 * Note: the "Me: Yes/No" response when user clicks the yes/no button is defined in SimStPLE inside  class SimpleListener implements ActionListener 
	 * */
	public boolean handleDialogMessage(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
    		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){
		
			
			if (actionType==SimStLogger.SIM_STUDENT_DIALOGUE){   
					boolean doesFeedbackIndicateStartTopic= brController.getMissController().getSimStPLE().getConversation().isTextInTopic(SimStConversation.START_PROBLEM_TOPIC,feedback);
   
				   //if a start problem message was received and there are also messages in queue this means that student clicked new problem 
				   //so delete buffered messages (they are not needed).
				   if (doesFeedbackIndicateStartTopic && !logBuffer.isEmpty())
					   		this.logBuffer.clear();
				
					if (feedback.equals("Me: Yes") || doesFeedbackIndicateStartTopic){		
						//System.out.println("Storing:" + feedback);
				   		LogEntry logTemp=new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating);
			    		getLogBuffer().add(logTemp);
						return true;
    		 			
					}
					else return false;
    		}
			
			
			return false;
	}

	
	public void reinstateDialogMessagesFromBuffer(String actionType, String action, Sai sai, SimStLogger logger){
		
    	//System.out.println(sai);
		if (actionType==SimStLogger.SIM_STUDENT_METATUTOR && action==SimStLogger.METATUTOR_MODEL_TRACING_ACTION){
    		//If action is "yes button clicked" then log an previously stored messages first//
    		if (sai.getS()=="yes" && sai.getA()=="ButtonPressed"){
        		Iterator it=this.getLogBuffer().iterator();
						while (it.hasNext()){
								LogEntry tmp=(LogEntry) it.next();
						//		logger.simStLogBufferedEntry(tmp.actionType, tmp.action, tmp.step, tmp.result, tmp.resultDetails, tmp.sai, tmp.node, tmp.correctness, tmp.expSelection, tmp.expAction, tmp.expInput, tmp.duration, tmp.feedback, tmp.opponent, tmp.info, tmp.myRating,null,null,null);
								it.remove();
								//System.out.println("popping out "+ tmp.feedback);
						}				
    		}
    		
    	}
		
	}
	
	
	
	
	/* Function that parses metatutor hint and returns lhs and rhs
	 * Obsolete, and NOT USED as this is domain dependent code. This
	 * is here only for testing and debuging purposes. Do NOT use in actual
	 *  production code
	 * */
	public String[] extractEquation(String hint){
		String[] result={"notFound","notFound"};

		String needle="Start by putting ";
		
		int e=hint.indexOf(needle);
		if (e==-1) return result;
		
		
		hint=hint.substring(e + needle.length(),hint.length());
		
		String needle1="on the first fraction and ";
		
		int e1=hint.indexOf(needle1);
		
		String lhs=hint.substring(0,e1);
		
		hint=hint.substring(e1 + needle1.length(),hint.length());
		
		String needle2="on the second fraction ";
		
		int e2 =hint.indexOf(needle2);

		
		String rhs=hint.substring(0, e2);
		
		lhs=lhs.trim();
		rhs=rhs.trim();
		
		
		result[0]=lhs;
		result[1]=rhs;
		
		
		return result;		
	}
	
	
	private void populate_ssElements(){
		
	/*get the initial state elements and populate the hash map*/ 
		 ArrayList<String> ssElements =  this.brController.getMissController().getSimStPLE().getStartStateElements();	
		  for(String s : ssElements) {
			  	ssElementValues.put(s, "empty");  	
		 }
		  
	}
    	
	public LinkedHashMap<String, String> get_ssElementValues(){
		return ssElementValues;
	}
	
	public void update_ssElements(String selection, String input){
			if (ssElementValues.isEmpty())
					populate_ssElements();
			
			ssElementValues.put(selection, input);	
			//System.out.println(ssElementValues);
	}
	
	
	public String getHintType() {
		return hintType;
	}

	

	public void setHintType(String hintType) {
		this.hintType = hintType;
	}



	public boolean isTimeToLogHint() {
		return timeToLogHint;
	}


	public void setTimeToLogHint(boolean timeToLogHint) {
		this.timeToLogHint = timeToLogHint;
	}

	private String hintType="-1"; // -1 indicates we do not have a hint so far
	
	
}
