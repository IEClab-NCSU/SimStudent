package edu.cmu.pact.miss.PeerLearning;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.SimStLoggingAgent.LogEntry;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;
import edu.cmu.pact.miss.minerva_3_1.StepAbstractor;

/*Class the keeps track of log messages related to hint and logs them only if it finds out they 
* are followed or not*/
public class SimStHintLogAgent extends SimStLogAgent{
	public static String PROBLEM="Problem";
	public static String PROBLEMTYPE="ProblemType";
	public static String QUIZ="Quiz";
	public static String COG_FEEDBACK="Feedback";
	public static String RESOURCE="View-Resource";
	private static final String FEEDBACK_REQUEST = "feedback-request";
	private static final String HINT_REQUEST = "hint-request";
	private static final String QUIZ_REQUEST = 	"take-quiz";
	private static final String INFORMATIVE = 	"informative";
	private static final String COG_DEMONSTRATE="DemonstrateStep";	
	private static final String PROBLEM_TYPE_REQUEST= "problem-type";
	private static final String PROBLEM_FAIL_QUIZ_TYPE_REQUEST= "quizPType";
	private static final String UOVERVIEW_REQUEST= "resource-uoverview";
	private static final String EXAMPLES_REQUEST= "resource-examples";
	
	private static SimStHintLogAgent instance = null;
	private static final String HINT_FOLLOWED="Followed";
	private static final String HINT_NOT_FOLLOWED="Not followed";
	private static final String NOT_TRACKED="Not Tracked";
	private static final String REPEATED_HINT="Repeated";
	
	/*hash map to store the values student entered on the startStateElementValues*/
	private LinkedHashMap<String,String> studentStartStateElementsValues;
	private LinkedHashMap<String,String> getStudentStartStateElementsValues(){return studentStartStateElementsValues;}
	
	/*hash map to store the values metatutor suggested for the startStateElementValues*/
	private LinkedHashMap<String,String> mtStartStateElementsValues;	
	public LinkedHashMap<String,String> getMtStartStateElementsValues(){return mtStartStateElementsValues;} 
	public void updateMtStartStateElementsValues(String selection, String input){
			if (!lockMTHash)
				getMtStartStateElementsValues().put(selection, input);
		} 
	private boolean lockMTHash;
	
	
	/*boolean to indicate if a hint was followed or not */
	boolean hintFollowed=false;
	
	
	/*boolean to indicate if we received hint so far*/
	protected boolean hintReceived=false;
	protected void setHintReceived(boolean flag){ this.hintReceived=flag;}
	protected boolean getHintReceived(){return hintReceived;}
	

	//Contructor
	public SimStHintLogAgent(BR_Controller brController) {
		super(brController);
		studentStartStateElementsValues = new LinkedHashMap<String,String>();
		mtStartStateElementsValues= new LinkedHashMap<String,String>();
	}

	public static SimStHintLogAgent getInstance(BR_Controller newBrController){
		if (instance==null){
			instance= new SimStHintLogAgent(newBrController);
		}
		return instance;
	}

	public static SimStHintLogAgent getInstance(){
		return instance;
	}
	
	/*String to store the c of the current hint*/
	private String currentHintType="none";
	private String getCurrentHintType(){ return currentHintType; }
	
	/**
	 * Set the current hint type ( MetaCognitive or Cognitive hint) . This is set based on Hint category 
	 * 
	 * 
	 */
	private void setCurrentHintType(){	
		/*if (result.contains(QUIZ_REQUEST))
			this.currentHintType=QUIZ;
		else if (result.contains(HINT_REQUEST))
			currentHintType=COG_DEMONSTRATE;
		else if (result.contains(FEEDBACK_REQUEST))
			currentHintType=COG_FEEDBACK;
		else if (result.contains(RESOURCE))
			currentHintType=RESOURCE;
		else if (result.contains(PROBLEM_TYPE_REQUEST) ||result.contains(PROBLEM_FAIL_QUIZ_TYPE_REQUEST))	
			currentHintType=PROBLEMTYPE;
		else 
			currentHintType=PROBLEM;*/
		if (getHintCategory().equals(QUIZ) || getHintCategory().equals(PROBLEM) || getHintCategory().equals(PROBLEMTYPE) || getHintCategory().equals(RESOURCE))
			currentHintType = "MetaCognitive";
		else if (getHintCategory().equals(COG_FEEDBACK) || getHintCategory().equals(COG_DEMONSTRATE))
			 currentHintType = "Cognitive";
		else 
			 currentHintType = "none";
		
		
	}
	
	/**
	 * Get the current hint type based on the result 
	 * @param result is the actual name of the MT production rule fired 
	 * @return the type
	 */
	public String getCurrentHintType(String result){	
		String currentHintType="";
		if (result.contains(QUIZ_REQUEST))
			currentHintType=QUIZ;
		else if (result.contains(HINT_REQUEST))
			currentHintType=COG_DEMONSTRATE;
		else if (result.contains(FEEDBACK_REQUEST))
			currentHintType=COG_FEEDBACK;
		else if (result.contains(RESOURCE))
			currentHintType=RESOURCE;
		else if (result.contains(PROBLEM_TYPE_REQUEST) ||result.contains(PROBLEM_FAIL_QUIZ_TYPE_REQUEST))	
			currentHintType=PROBLEMTYPE;
		else 
			currentHintType=PROBLEM;
		return currentHintType;
		
	}
		
	/*Boolean to store the suggested resource suggested by the MT*/
	private String anticipatedResource="";
	public void setAnticipatedResource(String result){
		if (result.contains(EXAMPLES_REQUEST)) 
				anticipatedResource=AplusPlatform.exampleTabTitle;
		else if (result.contains(UOVERVIEW_REQUEST))
				anticipatedResource=AplusPlatform.overviewTabTitle.replaceAll(" ", "");
	}
	public String getAnticipatedResource(){	return anticipatedResource;	}
	
	/**
	 * 
	 * 
	 * String to store the category of hint ie Quiz, ProblemType, Problem , etc, this is extracted from the production rule name
	 * 
	 * */
	public String hintCategory="";
	public void setHintCategory(String result){
		String[] part = result.split("_");
		if(part.length == 2)
			hintCategory = part[1];
	
	}
	/***
	 * get the Hint Category i.e., Quiz, ProblemType, Problem, View Resources 
	 * @return
	 */
	public String getHintCategory(){return hintCategory;};
	
	/*public String getHintCategory(String result){
		
		//return brController.getMissController().getSimSt().getHintType(result);
		if (getCurrentHintType().equals(QUIZ) || getCurrentHintType().equals(PROBLEM) || getCurrentHintType().equals(PROBLEMTYPE) || getCurrentHintType().equals(RESOURCE))
			return "MetaCognitive";
		else if (getCurrentHintType().equals(COG_FEEDBACK) || getCurrentHintType().equals(COG_DEMONSTRATE))
			return "Cognitive";
		else return "";
		
		
		
		}*/
	
	
	/**String to hold the cognitive hint given */
	public String feedbackHint="";
	public void setFeedbackHint(){
		//this method retrieves 
		String problemName=brController.getProblemName();
		ProblemNode currentNode=(ProblemNode) brController.getCurrentNode().getParents().get(0);
		Sai sai=getSimStudentSai();	// we must retrieve the student SAI since MetaTutor Hint Given transaction does not include it.
		
		String tmp=brController.getMissController().getSimSt().getRuleActivationTestMethod();
		brController.getMissController().getSimSt().setRuleActivationTestMethod(SimSt.RA_TEST_METHOD_TUTOR_SOLVERV2);
		String correctness  = brController.getMissController().getSimSt().inquiryRuleActivation(problemName, currentNode, "", sai.getS(), sai.getA(), sai.getI(), null); 
		brController.getMissController().getSimSt().setRuleActivationTestMethod(tmp);
		feedbackHint=correctness;
	}
	
	public String getFeedbackHint(){return feedbackHint;}
	
	/**String to hold the Sai metatutor suggested */
	public Sai mtSuggestedSai=null;
	public void setMTSuggestedSai(Sai sai){ mtSuggestedSai=sai;}
	public Sai getMTSuggestedSai(){return mtSuggestedSai;}
	
	
	
	
	
	/**
	 *  Method that manages a log entry only if its about a hint given.
	 *  This should return true if the log entry is either buffered or logged ("released") by the HintLogAgent
	 * @param actionType
	 * @param action
	 * @param step
	 * @param result
	 * @param resultDetails
	 * @param sai
	 * @param node
	 * @param correctness
	 * @param expSelection
	 * @param expAction
	 * @param expInput
	 * @param duration
	 * @param feedback
	 * @param opponent
	 * @param info
	 * @param myRating
	 * @param event_time
	 * @return true if the log entry is take care of (buffered or directly log), so it shouldn't continue...
	 */
	public boolean manageLogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){
		
		boolean returnValue=false;		
		
		/*we do not care to handle hints that are just informative (e.g. "You should wait until SimStudent stops thinking")*/
		if (result.contains(SimStHintLogAgent.INFORMATIVE)) return returnValue;
	
		if (action.equals(SimStLogger.METATUTOR_HINT_ACTION) && !getHintReceived()) { //new hint is given so store it...						
			setHintReceived(true);
			setHintCategory(result);
			setCurrentHintType();
			//System.out.println(" SimStLogger should NOT proceed with logging this log entry, storeHintLogEntry should either directly log this or buffer it...");
			//System.out.println(" New Hint " );
			storeHintLogEntry( actionType,  action,  step,  result,  resultDetails,  sai,  node,  correctness,  expSelection,
		    		 expAction,  expInput,  duration,  feedback,  opponent,  info,  myRating,  event_time);
			returnValue=true; //SimStLogger should NOT proceed with logging this log entry, storeHintLogEntry should either directly log this or buffer it...
		}
		else if (action.equals(SimStLogger.METATUTOR_HINT_ACTION) && getHintReceived()){	//repeated hint	
			//System.out.println("Repeated log entry....");
			setHintCategory(result);
			setCurrentHintType();
			//System.out.println(" Repeated hint !!");

			logger.simStLog( actionType,  action,  step,  result,  resultDetails,  sai,  node,  correctness,  expSelection,
		    		 expAction,  expInput,  duration,  feedback,  opponent,  info,  myRating, false, getHintCategory() + " " + getCurrentHintType(),REPEATED_HINT, event_time);
			returnValue=true; //SimStLogger should NOT proceed with logging this log entry
		}
		else if (!action.equals(SimStLogger.METATUTOR_HINT_ACTION) && getHintReceived()){  // see if log item can "release" a buffered log 
			//we have received a hint so far, either its time to log what is buffered or we have a repeated hint...		
			//System.out.println(" Releasing the log ");
			releaseHintLogEntry( actionType,  action,  step,  result,  resultDetails,  sai,  node,  correctness,  expSelection,
					expAction,  expInput,  duration,  feedback,  opponent,  info,  myRating,  event_time);
			returnValue=false; 
		}
		/*else if (action.equals(SimStLogger.METATUTOR_HINT_REQUESTED) && !getHintReceived()){
			/*store the type of hint student requested (i.e. MetaCognitive / Cognitive) so we can later use it on metatutor hint given.*/
			//setHintCategory(result);
		//}
		else{	
			//This means current log entry is NOT a hint log, nor is capable of "releasing" a hint log, so let SimStLogger log it...
			returnValue=false;
		}	

		return returnValue;		
	}
	

	
	
	/**
	 * Method that  stores (buffers) a log entry. 
	 * Note: In case of cognitive hints, it does not store them but immediately logs them, since we do not track if they followed or not.
	 * @param actionType
	 * @param action
	 * @param step
	 * @param result
	 * @param resultDetails
	 * @param sai
	 * @param node
	 * @param correctness
	 * @param expSelection
	 * @param expAction
	 * @param expInput
	 * @param duration
	 * @param feedback
	 * @param opponent
	 * @param info
	 * @param myRating
	 * @param event_time
	 */
	public void storeHintLogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){
		
			//System.out.println(" Current Hint category  : "+getHintCategory());
			if (getHintCategory().equals(QUIZ)){
				logBuffer.add(new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time));
				//System.out.println("its a quiz hint, so store it....(logBuffer size = " + logBuffer.size()+")");	
			}
			/*else if (getCurrentHintType().equals(COG_FEEDBACK) || getCurrentHintType().equals(COG_DEMONSTRATE)){
			 	// this else section is what happend for study V, where cognitive hints where not tracked
				//logger.simStLog( actionType,  action,  step,  result,  resultDetails,  sai,  node,  correctness,  expSelection,	 expAction,  expInput,  duration,  feedback,  opponent,  info,  myRating, false, getTypeOfHint() + " " + getCurrentHintType(),NOT_TRACKED,event_time);
				//setHintReceived(false); 	//false because in both cases we don't actually buffer it, we directly log it...
			}*/
			else if (getHintCategory().equals(COG_FEEDBACK)){
				setFeedbackHint();
				logBuffer.add(new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time));
				//System.out.println("Its a " + getCurrentHintType() + " type of hint, so log and store....(logBuffer size = " + logBuffer.size()+")");
			}
			else if (getHintCategory().equals(COG_DEMONSTRATE)){	
				setMTSuggestedSai(getMetatutorSai(getCurrentHintType()));
				logBuffer.add(new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time));
				//System.out.println("Its a " + getCurrentHintType() + " type of hint, so log and store....(logBuffer size = " + logBuffer.size()+")");
			}		
			else if (getHintCategory().equals(RESOURCE)){
				setAnticipatedResource(result);
				//System.out.println(" Size of log before adding : "+logBuffer.size());
				logBuffer.add(new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time));
				//System.out.println(" Size of log after adding :  "+logBuffer.size());
			}
			else if (getHintCategory().equals(PROBLEM) || getHintCategory().equals(PROBLEMTYPE) ){				
				/*lock mt hash so we keep what MT suggested*/
				lockMTHash=true;
				logBuffer.add(new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time));		
				//System.out.println("its a problem hint, so lock and store....(logBuffer size = " + logBuffer.size()+")");
			}
					
	}
	
	
	
	/**
	 * Method that "releases" a hint log entry. 
	 * @todo: log buffer must be emptied when APLUS is closed... 
	 * 
	 * @param actionType
	 * @param action
	 * @param step
	 * @param result
	 * @param resultDetails
	 * @param sai
	 * @param node
	 * @param correctness
	 * @param expSelection
	 * @param expAction
	 * @param expInput
	 * @param duration
	 * @param feedback
	 * @param opponent
	 * @param info
	 * @param myRating
	 * @param event_time
	 * @return
	 */
	public boolean releaseHintLogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time){

		if (action.equals(SimStLogger.QUIZ_BUTTON_ACTION) || action.equals(SimStLogger.UNTAKEN_QUIZ_INITIATE_ACTION )){
			hintFollowed=(getHintCategory().equals(QUIZ)) ? true : false;
			
			if (!logBuffer.isEmpty())
				logBuffer.pop().log(logger, getHintCategory() + " " + getCurrentHintType(),hintFollowedBooleanToString(hintFollowed));
			setHintReceived(false);	//reset the hintReceived flag for the next hint...
			System.out.println("quiz button has been clicked, hint followed is ..." + hintFollowed + " hintType is " + getCurrentHintType());
			
		}
		else if (action.equals(SimStLogger.PROBLEM_ENTERED_ACTION)){
	
			/*Update the student start state element hash with the values student entered */ 
			updateStudentStartStateElementsValues();
			getMtStartStateElementsValues();
			
			/*if its problem, compare the hashes, if its problem type then abstarct and compare*/
			if (getHintCategory().equals(PROBLEM))
				hintFollowed= getStudentStartStateElementsValues().equals(getMtStartStateElementsValues())? true : false;
			else
				hintFollowed= abstractHash(getStudentStartStateElementsValues()).equals(abstractHash(getMtStartStateElementsValues()))? true : false;
			
			lockMTHash=false;
			if (!logBuffer.isEmpty())
				logBuffer.pop().log(logger,getHintCategory() + " " + getCurrentHintType(),hintFollowedBooleanToString(hintFollowed));
			
			setHintReceived(false);	//reset the hintReceived flag for the next hint...
			System.out.println("a new problem has been given, hintFollowed is " + hintFollowed + " hintType is " + getHintCategory());
			
		}
		else if (action.equals(SimStLogger.INPUT_VERIFY_ACTION) || action.equals(SimStLogger.HINT_RECEIVED) ||action.equals(SimStLogger.STUDENT_STEP_ENTERED)){
		
			if ((getHintCategory().equals(COG_FEEDBACK))){		
				hintFollowed = result.equals(this.getFeedbackHint()) ? true : false;
			}
			else if ((getHintCategory().equals(COG_DEMONSTRATE))){
				hintFollowed = areSaiEqual(sai,this.getMTSuggestedSai()) ? true : false;
			}
			else hintFollowed=false;
				
			if (!logBuffer.isEmpty())
				logBuffer.pop().log(logger,getHintCategory() + " " + getCurrentHintType(),hintFollowedBooleanToString(hintFollowed));
			//System.out.println("Sai is " + sai + " and MT sai is " + this.getMTSuggestedSai());
			System.out.println("cognitive hint is provided AND hint followed is " + hintFollowed + " hintType is " + getHintCategory());
			setHintReceived(false);
		}
		else if (action.equals(SimStLogger.TAB_SWITCH_ACTION) && !getHintCategory().equals(QUIZ)){		
			
			/*first check if given hint is a resource... If not, then hint is not followed*/
			//System.out.println("current hint type is " + getCurrentHintType() + " and we wait for " + getAnticipatedResource());
			boolean resourceHint=(getHintCategory().equals(RESOURCE)) ? true : false;
			if (resourceHint && result!=null){
				result=result.replace(" ", "");
				hintFollowed=result.contains(getAnticipatedResource()) ? true: false;
			}
			else
				hintFollowed=false;
			
			//System.out.println("Preparing to log... " + hintFollowed);
			if (!logBuffer.isEmpty()) {
				System.out.println(" Release log (size) " +logBuffer.size());
				logBuffer.pop().log(logger, getHintCategory() + " " + getCurrentHintType(),hintFollowedBooleanToString(hintFollowed)); 
			}
				   			
			setHintReceived(false);	//reset the hintReceived flag for the next hint...
			System.out.println("a new tab has been clicked, hintFollowed is " + hintFollowed + " hintType is " + getHintCategory());
			
		}
		
		return hintFollowed;
	}
	
	/**
	 * Method to compare if two sai's are the same
	 * @param sai1
	 * @param sai2
	 * @return
	 */
	boolean areSaiEqual(Sai sai1, Sai sai2){	
		//return (sai1.getS().equals(sai2.getS()) && sai1.getA().equals(sai2.getA()) &&  sai1.getI().equals(sai2.getI()));
		return (sai1.getS().equals(sai2.getS()) && sai1.getA().equals(sai2.getA()) &&  brController.getMissController().getSimSt().compairInput(sai1.getI(), sai2.getI()));
	}
	
	/**
	 * Method to retrieve the MT suggested SAI. For cognitive hints, hint should be taken from parent, 
	 * for demonstration hint should be taken from current.
	 * @return
	 */
	Sai getMetatutorSai(String typeOfCognitiveHint){
		ProblemNode currentNode;
		if (typeOfCognitiveHint.equals(COG_FEEDBACK))
			currentNode=(ProblemNode) brController.getCurrentNode().getParents().get(0);
		else 
			currentNode=(ProblemNode) brController.getCurrentNode();
		
		 AskHint hint = brController.getMissController().getSimSt().askForHintQuizGradingOracle(brController,currentNode); 
		 return hint.getSai();
	}
	
	/**
	 * Method to retrieve from the brd the current Sai of simstudent.
	 * @return
	 */
	Sai getSimStudentSai(){
		 ProblemNode currentNode=(ProblemNode) brController.getCurrentNode();
		 ProblemEdge actualEdge=currentNode.getIncomingEdges().get(0);
		 Sai simStudentSai=new Sai(actualEdge.getSelection(),actualEdge.getAction(),actualEdge.getInput());
		 return simStudentSai;
	}
	
	
	
	/**
	 * Method that converts boolean to "Followed / Not Followed"
	 * @param hintFollowed
	 * @return
	 */
	private String hintFollowedBooleanToString(boolean hintFollowed){
		return hintFollowed? HINT_FOLLOWED : HINT_NOT_FOLLOWED;
	}
	
	/**
	 * Method to abstract a hash using Minerva problem abstractor. Used to determine if hint was followed
	 * when MT made a problem type suggestion.
	 * @param hash
	 * @return
	 */
	private LinkedHashMap<String,String> abstractHash(LinkedHashMap<String,String> hash){	
		StepAbstractor abstractor = new StepAbstractor();
		String abstracted;
		String abstracted1;
		/*Abstract start state elements hash*/
		Set set = hash.entrySet();
		Iterator it = set.iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();  		     
			String key= (String) entry.getKey();
			String value= (String) entry.getValue(); 

			abstracted = abstractor.signedAbstraction(value);

			hash.put(key, abstracted);
		} 
		return hash;

	}
	/**
	 * Method that updates the internal hash with the start state elements with the values student entered
	 *  
	 */
	public void updateStudentStartStateElementsValues(){
		
		 ArrayList<String> ssElements =  this.brController.getMissController().getSimStPLE().getStartStateElements();	
		  for(String s : ssElements) {  
			  //get the value of the start state element and store it to the hash
			  TableExpressionCell cell = (TableExpressionCell)brController.lookupWidgetByName( s );		  
			  this.studentStartStateElementsValues.put(s, cell.getText());
		 }
		  
	}
	
	
	
	
}