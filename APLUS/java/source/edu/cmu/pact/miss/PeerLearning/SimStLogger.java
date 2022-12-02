package edu.cmu.pact.miss.PeerLearning;

import java.rmi.server.UID;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.swing.JOptionPane;

import edu.cmu.oli.log.client.DiskLogger;
import edu.cmu.oli.log.client.StreamLogger;
import edu.cmu.pact.Log.TutorActionLog;
import edu.cmu.pact.Log.TutorActionLogV4;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.ProgramAction;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.ProblemAssessor;
import edu.cmu.pact.miss.Rule;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.SimStInteractiveLearning;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.SimStLoggingAgent.LogEntry;
import edu.cmu.pact.miss.storage.FileZipper;
import edu.cmu.pslc.logging.ContextMessage;
import edu.cmu.pslc.logging.Message;
import edu.cmu.pslc.logging.OliDatabaseLogger;
import edu.cmu.pslc.logging.OliDiskLogger;
import edu.cmu.pslc.logging.ToolMessage;
import edu.cmu.pslc.logging.TutorMessage;
import edu.cmu.pslc.logging.element.ActionEvaluationElement;
import edu.cmu.pslc.logging.element.SkillElement;

public class SimStLogger {

	public BR_Controller brController;
	
	static boolean loggingEnabled = false;
	
	protected static String userID = "";
	
   public static final String DEFAULT_LOG_DIR = "log";
   public static final String TEST_LOG_URL = "http://pslc-qa.andrew.cmu.edu/log/server";
   public static final String DEFAULT_LOG_URL = "http://learnlab.web.cmu.edu/log/server";

   public static String log_url = DEFAULT_LOG_URL;

   public static final String SIM_STUDENT_DIALOGUE = "SIM_STUDENT_DIALOGUE";
   public static final String SIM_STUDENT_INFO_RECEIVED = "SIM_STUDENT_INFO_RECEIVED";
   public static final String HUMAN_STUDENT_INFO_RECEIVED = "HUMAN_STUDENT_INFO_RECEIVED";
   public static final String SIM_STUDENT_LEARNING = "SIM_STUDENT_LEARNING";
   public static final String SIM_STUDENT_ERROR = "SIM_STUDENT_ERROR";
   public static final String SIM_STUDENT_PLE = "SIM_STUDENT_PLE";
   public static final String SIM_STUDENT_QUIZ = "SIM_STUDENT_QUIZ";
   public static final String BKT = "BKT";

   public static final String HUMAN_STUDENT_QUIZ = "HUMAN_STUDENT_QUIZ";
   public static final String SIM_STUDENT_ACTION_LISTENER = "SIM_STUDENT_ACTION_LISTENER";
   public static final String SIM_STUDENT_STEP = "SIM_STUDENT_STEP";
   public static final String SIM_STUDENT_PROBLEM = "SIM_STUDENT_PROBLEM";
   public static final String LMS_PROBLEM = "LMS_PROBLEM";
   public static final String COG_TUTOR_PROBLEM = "COG_TUTOR_PROBLEM";
   public static final String SIM_STUDENT_METATUTOR_AL = "SIM_STUDENT_METATUTOR_ACTION_LISTENER";
   public static final String SIM_STUDENT_METATUTOR = "SIM_STUDENT_METATUTOR";
   public static final String SIM_STUDENT_SKILLOMETER = "SIM_STUDENT_SKILLOMETER_ACTION_LISTENER";
   public static final String COG_TUTOR_PLE = "COG_TUTOR_SKILLOMETER_ACTION_LISTENER";

   
   public static final String LMS_PROBLEM_CONSIDERED = "LMS Problem Considered";
   public static final String BKT_INITIAZE = "Initialize BKT KC";
   public static final String HINT_REQUEST_ACTION = "Sim Student Requesting Hint";
   public static final String ACTIVATION_RULES_ACTION = "Activation Rules Determined";
   public static final String ACTIVATION_RULE_ACTION = "Activation Rule Considered";
   public static final String LEARNING_PERFORMANCE_ACTION = "Learning Performance Determined";
   public static final String RULE_LEARN_PERFORMANCE_ACTION = "Rule Learned Performance";
   public static final String RHS_OPS_SEARCH_PERFORMANCE_ACTION = "RHS Ops Search Performance";
   public static final String WME_PATH_SEARCH_PERFORMANCE_ACTION = "WME Path Search Performance";
   public static final String CONFIRMATION_REQUEST_ACTION = "Sim Student Requesting Confirmation of Input";
   public static final String INPUT_VERIFY_ACTION = "Student Verified Correctness of Sim Student Input";
   public static final String CONFIRMATION_REQUEST_CL_ACTION = "ClAlgebra Tutor Verified Correctness of Sim Student Input";
   public static final String CONFIRMATION_REQUEST_CL_ACTION_HUMAN = "Jess Oracle Verified Correctness of Human Student Input";
   public static final String EXCEPTION_ACTION = "Sim Student has experienced an exception";
   public static final String POSITIVE_EXAMPLE_ACTION = "Positive Example Received";
   public static final String NEGATIVE_EXAMPLE_ACTION = "Negative Example Received";
   public static final String PLE_STARTED_ACTION = "Peer Learning Environment Started";
   public static final String PLE_CLOSED_ACTION = "Peer Learning Environment Closed";
   public static final String SPLASH_SCREEN_APPEARED = "Splash screen appeared";
   public static final String SPLASH_SCREEN_CLOSED = "Splash screen ";
   public static final String SKILLOMETER_OPEN = "Skillometer window opened";
   public static final String SKILLOMETER_CLOSED = "Skillometer window closed";
   public static final String PROBLEM_REQUEST_ACTION = "New Problem Requested";
   public static final String PROBLEM_ENTERED_ACTION = "New Problem Entered";
   public static final String PROBLEM_DURATION = "Problem Duration";
   public static final String PROBLEM_RESTART_ACTION = "Problem Restarted";
   public static final String COG_TUTOR_PROBLEM_GIVEN = "Problem Given by Cog Tutor";
   public static final String INTERFACE_ACTION = "Interface Action ";
   
   
   
   
   
   
   public static final String QUIZ_QUESTION_GIVEN_ACTION_HUMAN = "Human Student Started Quiz Question";
   public static final String QUIZ_QUESTION_GIVEN_ACTION = "Sim Student Given Quiz Question";
   public static final String QUIZ_QUESTION_ANSWER_ACTION = "Sim Student Answers Quiz Question";
   public static final String QUIZ_QUESTION_ANSWER_ACTION_HUMAN = "Human Student Answers Quiz Question";
   public static final String QUIZ_COMPLETED_ACTION = "Quiz Completed";
   public static final String NEXT_PROBLEM_BUTTON_ACTION = "Next Problem Button Clicked";
   public static final String PROBLEM_ENTERED_BUTTON_ACTION = "Problem Entered Button Clicked";
   public static final String QUIZ_BUTTON_ACTION = "Quiz Button Clicked";
   public static final String CURRICULUM_BROWSER_BUTTON_ACTION = "Curriculum Browser Button Clicked";
   public static final String EXAMPLES_BUTTON_ACTION = "Examples Button Clicked";
   public static final String UNDO_BUTTON_ACTION = "Undo Button Clicked";
   public static final String RESTART_BUTTON_ACTION = "Restart Problem Button Clicked";
   public static final String TAB_SWITCH_ACTION = "Switched Tab";
   public static final String HINT_RECEIVED = "Hint Received";
   public static final String STUDENT_STEP_ENTERED = "Student Entered Step";
   public static final String RETRY_RECEIVED = "Retry Received";
   public static final String STEP_STARTED_ACTION = "New Step Started";
   public static final String NOT_LEARN_ACTION = "Sim Student Did Not Learn On Input";
   public static final String NOT_LEARN_SKIP_ACTION = "Sim Student Gave Up Learning On Input";
   public static final String BAD_INPUT_RECEIVED = "Invalid Input Received";
   public static final String PROBLEM_COMPLETED_ACTION = "Problem Completed";
   public static final String PROBLEM_ANSWER_SUBMIT_ACTION = "Problem Answer Submitted";
   public static final String PROBLEM_DONE_ACTION = "Problem Reached Done State";
   public static final String PROBLEM_ABANDONED_ACTION = "Problem Abandoned";
   public static final String PROBLEM_LEFT_QUIZ_ACTION = "Incomplete Problem Left to Quiz";
   public static final String STEP_COMPLETED_ACTION = "Step Completed";
   public static final String TAB_LEFT_ACTION = "Left Tab";
   public static final String UNDO_ACTION = "Step Undone";
   public static final String AVATAR_CONFIGURE_BUTTON_ACTION = "Avatar Configure Button Clicked";
   public static final String AVATAR_SAVE_ACTION = "Avatar Image Saved";
   public static final String RANDOMIZE_ACTION =" Roll a dice Button Clicked";
   public static final String SKILLOMETER_UPDATE_ACTION = "Skillometer Updated";
   public static final String CHAT_DIALOG_ACTION = "Chat Dialog Entered";
   public static final String QUIZ_VIEW = "Quiz Solution Viewed";
   public static final String EXAMPLE_VIEW = "Example Solution Viewed";
   public static final String QUIZ_VIEW_END = "Finished Quiz Solution View";
   public static final String EXAMPLE_VIEW_END = "Finished Example Solution View";
   public static final String ON_PAPER_VIEW_END = "Closed On Paper View";
   public static final String QUIZ_VIEW_END_TAB = "Finished Quiz Solution View by Leaving Tab";
   public static final String EXAMPLE_VIEW_END_TAB = "Finished Example Solution View by Leaving Tab";
   public static final String QUIZ_VIEW_TAB = "Quiz Solution Viewed";
   public static final String EXAMPLE_VIEW_TAB = "Example Solution Viewed on New Tab";
   public static final String UNTAKEN_QUIZ_EXPAND_ACTION = "Student Tried to Expand Untaken Quiz";
   public static final String UNTAKEN_QUIZ_INITIATE_ACTION = "Student Initiated Quiz on Untaken Quiz Expand";
   public static final String INSTRUCTION_SIZE="Instruction Vector Size";	
   public static final String SOLUTION_CHECKING_FAILED="Solution Checking Failed";
   
   public static final String APLUS_INACTIVE_WINDOW_START="Aplus Inactive Window Appeared";
   public static final String APLUS_INACTIVE_WINDOW_CLOSE="Aplus Inactive Window Closed";
   public static final String APLUS_INACTIVE_WINDOW_SELECTION="Aplus Inactive Window Student Selection";
   public static final String APLUS_INACTIVE_WINDOW_RESPONSE="Aplus Inactive Window Response";
   public static final String APLUS_INACTIVE_WINDOW_QUIT="Aplus Inactive Window Closed";
   public final static String APLUS_INACTIVE_WINDOW_NO_TUTORING="Off task while no tutoring";
   public final static String APLUS_INACTIVE_WINDOW_FEEDBACK="Off task while SimStudent was waiting for feedback";
	public final static String APLUS_INACTIVE_WINDOW_HINT_TRANSFORMATION="Off task while SimStudent was waiting for transformtion hint";
	public final static String APLUS_INACTIVE_WINDOW_HINT_TYPEIN="Off task while SimStudent was waiting for type-in hint";
	public static final String QUIT_WINDOW_START="Quit Window Appeared";
	public static final String QUIT_WINDOW_CLOSE="Quit Window Closed";
   
   
   
   public static final String SSGAME = "SSGAME";
   public static final String SSGAME_MATCHUP = "SSGAME_MATCHUP";
   public static final String SSGAME_CONTEST = "SSGAME_CONTEST";
   public static final String SSGAME_REVIEW = "SSGAME_REVIEW";
   public static final String SSGAME_ERROR = "SSGAME_ERROR";

   public static final String GAMESHOW_STARTUP_ACTION = "Game Show Program Started";
   public static final String JOIN_MATCHUP_ACTION = "Joined Matchup Area";
   public static final String SUCCESSFUL_MATCHUP_ACTION = "Matchup Successful";
   public static final String VIEW_CONTESTANT_ACTION = "Contestant Details Viewed";
   public static final String CHALLENGE_CONTESTANT_ACTION = "Challenge Issued";
   public static final String CHALLENGED_ACTION = "Challenge Received";
   public static final String CHALLENGE_ACCEPT_ACTION = "Challenge Accepted";
   public static final String CHALLENGE_REFUSE_ACTION = "Challenge Refused";
   public static final String CHALLENGE_ACCEPTED_ACTION = "Challenge Acceptance Received";
   public static final String CHALLENGE_REFUSED_ACTION = "Challenge Refusal Received";
   public static final String CHAT_MESSAGE_GROUP_ACTION = "Group Chat Message Added";
   public static final String ANNOUNCE_MESSAGE_GROUP_ACTION = "Group Announcement Message Added";
   public static final String CONTEST_START_ACTION = "Contest Started";
   public static final String PROBLEM_START_ACTION = "New Problem Started";
   public static final String STEP_INPUT_ACTION = "Input for Step Attempted";
   public static final String ANSWER_SUBMIT_ACTION = "Answer Submitted";
   public static final String CHAT_MESSAGE_PRIVATE_ACTION = "Private Chat Message Added";
   public static final String ANNOUNCE_MESSAGE_PRIVATE_ACTION = "Private Announcement Message Added";
   public static final String GENERATE_PROBLEMS_BUTTON_ACTION = "Generate Problems Button Clicked";
   public static final String PROBLEM_INVALID_ACTION = "Invalid Problem Entered";
   public static final String OPPONENT_ANSWER_ACTION = "Opponent Answer Submitted";
   public static final String CONTEST_RESULT_ACTION = "Contest Problem Results Determined";
   public static final String CHALLENGE_TIMEOUT_ACTION = "Challenge Request Timed-Out";
   public static final String PROBLEM_REQUEST_TIMEOUT_ACTION = "Problem Request Timed-Out";
   public static final String PROBLEM_SUBMIT_TIMEOUT_ACTION = "Problem Submission Timed-Out";
   public static final String WIN_DECIDED_ACTION = "Winner Decided";
   public static final String WIN_STATISTICS_ACTION = "Winning Statistics Determined";
   public static final String PROBLEMS_CORRECT_STATISTIC = "Number of Problems Correct Determined";
   public static final String PROBLEM_LENGTH_STATISTIC = "Problem Lengths Determined";
   public static final String PERCENT_CORRECT_STATISTIC = "Percent of Steps Correct Determined";
   public static final String CONTINUE_BUTTON_ACTION = "Continue Button Clicked";
   public static final String RATING_CHANGED_ACTION = "Rating Changed";
   public static final String PROBLEM_SUBMIT_ACTION = "New Problem Submitted";
   public static final String GAMESHOW_CLOSED_ACTION = "Game Show Closed";
   public static final String RECONNECT_ACTION = "Reconnected to Game Show";
   public static final String OUTSTANDING_ACTION = "Challengee Already has Outstanding Challenge";
   public static final String LEADERBOARD_REQUEST_ACTION = "Leaderboard Update Requested";

   public static final String SIM_STUDENT_EXPLANATION = "SIM_STUDENT_EXPLANATION";
   public static final String HINT_EXPLAIN_ACTION = "Hint Explained";
   // Added by Tasmia.
   public static final String ON_PAPER_EXPLAIN_ACTION = "Paper Explained";
   public static final String INPUT_WRONG_EXPLAIN_ACTION = "Non-Confirmed Input Explained";
   public static final String PROBLEM_ENTERED_EXPLAIN_ACTION = "New Problem Explained";
   public static final String NO_EXPLAIN_ACTION = "Explanation Not Given";
   public static final String EXPLANATION_CATEGORIZE_ACTION = "Categorize Explanation";

   public static final String PROBLEM_ENTERED_EXPLAIN_TYPE = "ProblemEntered";
   public static final String INPUT_WRONG_EXPLAIN_TYPE = "InputWrong";
   public static final String HINT_GIVEN_EXPLAIN_TYPE = "HintGiven";
   public static final String FOLLOW_UP_EXPLAIN_SUFFIX = "-FollowUp";

   public static final String STUDENT_ATTEMPT = "Student";
   public static final String SIMSTUDENT_ATTEMPT = "SimStudent";
   public static final String STUDENT_META_ATTEMPT = "Student Meta";
   public static final String OPPONENT_ATTEMPT = "Opponent";
   public static final String METATUTOR_ATTEMPT = "MetaTutor";
   public static final String QUIZ_RESULT = "Quiz";
   public static final String LOGGING_RESULT = "Logged Only";
   public static final String CONTEST_RESULT = "Contest";
   public static final String PROBLEM_RESULT = "Problem Logged Only";

   public static final String METATUTOR_CLICK_ACTION = "Clicked Metatutor";
   public static final String METATUTOR_HINT_TRIGGER_ACTION = "Metatutor Trigger";
   public static final String METATUTOR_QUESTION_ACTION = "Metatutor Question Asked";
   public static final String METATUTOR_NEXT_HINT_ACTION = "Next Metatutor Hint Selected";
   public static final String METATUTOR_PREVIOUS_HINT_ACTION = "Previous Metatutor Hint Selected";
   public static final String METATUTOR_CLOSE_HINT_ACTION = "Metatutor Hint Closed";
   public static final String METATUTOR_LEFT_HINT_ACTION = "Metatutor Hint Left";
   public static final String METATUTOR_HINT_ACTION = "Metatutor Hint Given";
   public static final String METATUTOR_PROACTIVE_HINT_ACTION = "Metatutor Hint Given";
   public static final String METATUTOR_MODEL_TRACING_ACTION = "Metatutor Model Traced";
   public static final String METATUTOR_HINT_REQUESTED = "Metatutor Hint Requested";
   public static final String CONTEXT_MENU_QUESTION_ACTION = "Context Menu Question Asked";
   public static final String FAILURE_OF_LEARNING = "Failure of learning detected";
   public static final String FAILURE_OF_LEARNING_RESULT = "Fatal failure of learning";
   public static final String DISJUNCT_LEARNING = "Disjunct learning detected";
   public static final String ACCEPTABLE_DISJUNCT = "Acceptable disjunt ";
   public static final String NORMAL_DISJUNCT = "Normal disjunt ";
   
   public static final String HINT_FOLLOWED = "HINT_FOLLOWED";
   public static final String HINT_TYPE = "HINT_TYPE";
   public static final String HINT_SUBJECT = "HINT_SUBJECT";


   public String getCurrentTime(){
   	
   	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
   	return dateFormat.format(new GregorianCalendar().getTime());
	
   }


   public SimStLogger(BR_Controller br)
   {
   	brController = br;
   //	trace.out("Inital Dataset Basename : "+datasetBasename);
   	datasetBasename = brController.getLoggingSupport().getCourseName();
   //	trace.out("In SimStLogger : "+datasetBasename);
   }
	
   public static final String UNPAIRED_LOG_ITEM = "UNPAIRED_LOG_ITEM";
   public static final String ACTION_TYPE_PROPERTY = "ACTION_TYPE";
   public static final String ACTION_PROPERTY = "ACTION";
   public static final String RESULT_PROPERTY = "RESULT";
   public static final String LMS_ID = "LMS_ID";
   public static final String DETAILS_PROPERTY = "DETAILS";
   public static final String USERID_PROPERTY = "USERID";
   public static final String DURATION_PROPERTY = "DURATION";
   public static final String CORRECTNESS_ST_PROPERTY = "ST_CORRECTNESS";
   public static final String CORRECTNESS_SIMST_PROPERTY = "SIMST_CORRECTNESS";
   public static final String STEP_PROPERTY = "STEP";
   public static final String SAI_AGENT_PROPERTY="SAI_AGENT";
   public static final String ACTUAL_SKILL_PROPERTY = "ACTUAL_SKILL";
   public static final String STUDENT_LOG_COUNT_PROPERTY = "STUDENT_LOG_COUNT";
   public static final String OPPONENT_PROPERTY = "OPPONENT";
   public static final String INFO_PROPERTY = "INFO";
   public static final String RATING_PROPERTY = "MY_RATING";
   public static final String STATUS_PROPERTY = "STATUS";
   public static final String TIME_PROPERTY = "tool_event_time";
   public static final String DATE_PROPERTY = "date";
   public static final String ABSTRACT_PROBLEM_PROPERTY = "ABSTRACT_PROBLEM";
   public static final String PROBLEM_TYPE_PROPERTY = "PROBLEM_TYPE";
   public static final String ABSTRACT_STEP_PROPERTY = "ABSTRACT_STEP";

   public static final String TRUE = "TRUE";
   public static final String FALSE = "FALSE";
   public static final String NOT_CALCED = "NOT CALCULATED";
   public static final String SIMST_SAI = "SimStudent";

   public static final String SIMST_APPEND = "-simSt";
   public static final String STUDENT_APPEND = "-student";
   public static final String LMS_APPEND = "-lms";

   
   public static final String QUIZ_STATUS = "Quiz";
   public static final String TUTOR_STATUS = "Tutoring";
   public static final String START_STATUS = "Start";
   public static final String GAMESHOW_STATUS = "GameShow";

   public static final String PROBLEM_MODEL = "Problem";
   public static final String PROBLEM_SUBMIT_MODEL = "ProblemSubmit";
   public static final String STEP_MODEL = "Step";
   public static final String ACTUAL_MODEL = "ActualSkill";

   public static int studentLogCount = 0;

   private String datasetBasename;

   public static String status = START_STATUS;


   private SimStLoggingAgent logAgent;

   private SimStHintLogAgent hintLogAgent;
   /*log agent to for time on task*/
   private SimStProblemStartLogAgent problemStartLogAgent;
   
   public SimStLoggingAgent getLogAgent(){
   	
   	return logAgent.getInstance();
   }

  public SimStHintLogAgent getHintLogAgent(){
   	
   	return (SimStHintLogAgent) hintLogAgent.getInstance();
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, boolean logBuffering, String hintType, String hintFollowed, String tool_event_time)
   {
       	   
	//  trace.out(" Duration :  "+duration);
	// trace.out(" Gonna try logging "+action+ " hintype "+hintType+ "   resultDetails "+resultDetails);
	   
	String problemName = brController.getProblemModel().getProblemName();
	
	
   	if(!loggingEnabled)
   	{
   		if(trace.getDebugCode("ss"))trace.out("ss", "Logging not enabled when trying to log.");
   		return;
   	}
   	
	/*boolean to indicate if back to the future holded the transaction so do not proceed with logging*/
   	boolean returnNow=false; 
   	
   	/*New logging agent that can "buffer" hint messages until they are followed or not 
   	 * enabled only when logBuffering is true and metatutor mode is on*/
    if (logBuffering  && brController.getMissController().getSimSt().isSsMetaTutorMode() ){       	
   		
   		boolean logEntryBuffered=((SimStHintLogAgent) hintLogAgent.getInstance(brController)).manageLogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, tool_event_time);
   	 	//if hintLogAgent buffered / handled the transaction, then return (i.e. transaction is either already logged or buffered). 		
   		if (logEntryBuffered){
   			if(trace.getDebugCode("ss"))trace.out("ss", "Hint log agent handled a log entry.");
   			returnNow=true;
   		}
   	}
    
    
    
    if (logBuffering){  	 /*log agent to add time on task on problem entered transaction.*/ 	
		boolean logEntryBuffered=false;
		try {
			logEntryBuffered = ((SimStProblemStartLogAgent) problemStartLogAgent.getInstance(brController)).manageLogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, tool_event_time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	if (logEntryBuffered){
    		if(trace.getDebugCode("ss"))trace.out("ss", "Problem entered log agent handled a log entry.");
    		//trace.out("Problem entered log agent handled a log entry." + action);
    		returnNow=true;
    	}
    }
    /*If back to the future handled the entry, then do not proceed.*/
    if (returnNow) return;
    
    //if (logBuffering==false)//this means the HintLogAgent is logging....
    //	trace.out("Logging from " + tool_event_time + " " + actionType +" " + action + " Hint type=" + hintType + " & hintfollowed=" + hintFollowed);
   	
   	SimStInteractiveLearning ssIL = brController.getMissController().getSimSt().getSsInteractiveLearning();
   	
   	if(problemName == null || problemName.length() == 0)
   	{
   		if(ssIL != null && ssIL.getQuizGraph() != null && ssIL.getQuizGraph().getStartNode() != null)
   		{
   			problemName = ssIL.getQuizGraph().getStartNode().getName();
   		}
   	}
   	if(problemName != null)
   	{
   		problemName = SimSt.convertFromSafeProblemName(problemName);
   	}
   	if(step != null)
   	{
   		step = SimSt.convertFromSafeProblemName(step);
   	}
   	if(step == null)
   	{
   		step = brController.getMissController().getSimSt().getProblemStepString();
   	}
   	
   	if(feedback != null && feedback.length() > 0 && feedback.contains("\n"))
   	{
   		feedback = feedback.replaceAll("\n", "");
   	}
   	
 
   	correctness = checkCorrectness(action, sai, problemName, result, node, correctness);
  
	if (action.equals(LMS_PROBLEM_CONSIDERED)) {
		problemName=step;
		//trace.out("LMS problem :"+step);
	}
	
	//if (action.equals(this.LMS_PROBLEM_CONSIDERED)) {
	//if (action.equals(this.PROBLEM_ENTERED_ACTION)) {
	//	trace.out("****** step " + step);
  	//	trace.out("****** result " + result);
   	//	trace.out("****** resultDetails " + resultDetails.toString());
	//}

	

   	//trace.out(action.equals(METATUTOR_HINT_ACTION) && (feedback.equals("") || feedback.length()<1));
   	if (action.equals(METATUTOR_HINT_ACTION) && (feedback.equals("") || feedback.length()<1)){
   		//trace.out(" returning ");
   		return;
   	}
   			
   //	trace.out(" Gonna print context message");
   	
   	//get the context message
   	ContextMessage context = getContextMessage(action, problemName, resultDetails.toString());
 //   trace.out("Context  : "+context.toString());
   	ToolMessage logMessage = ToolMessage.create(context);
 //	trace.out(" Tool Message : "+logMessage.toString());
   	TutorMessage responseMessage = TutorMessage.create(logMessage);
 //	trace.out(" Tutor Message : "+responseMessage.toString());
   	// Logs the expected selection, expected action and expected input.
   	logExpected(action, correctness, result, sai, expSelection, expAction, expInput, step, resultDetails.toString(), logMessage, responseMessage);
   	if(sai != null && !action.equalsIgnoreCase(PROBLEM_ANSWER_SUBMIT_ACTION) &&
   			!action.equalsIgnoreCase(SKILLOMETER_UPDATE_ACTION) && ! action.equalsIgnoreCase(CONTEXT_MENU_QUESTION_ACTION))
   	{
   		String agent = getSkillAgent(action);
   		String skill = "";
   		if(action.equals(BAD_INPUT_RECEIVED) && result.length() > 0)
   		{
   			Sai correctedSai = new Sai(sai.getS(), sai.getA(), result);
   			skill = determineSkill(correctedSai, step, resultDetails.toString(),action);
   		}
   		else
   			skill = determineSkill(sai, step, resultDetails.toString(),action);
   		SkillElement actualSkill = new SkillElement(skill+"-"+agent, skill);
			actualSkill.addModelName(ACTUAL_MODEL);
			responseMessage.addSkill(actualSkill);
   		//addLogItem(ACTUAL_SKILL_PROPERTY, ,logMessage);
   	}
   	
   	addLogItem(ACTION_TYPE_PROPERTY, actionType,logMessage);
   	addLogItem(ACTION_PROPERTY, action,logMessage);
   	//addLogItem(STEP_PROPERTY, "'"+step,logMessage);
   //	trace.out("Result : "+result);
   	addLogItem(RESULT_PROPERTY, result,logMessage);
   	
   	if (action.equals(LMS_PROBLEM_CONSIDERED))
   			addLogItem(LMS_ID,resultDetails.toString(),logMessage);
   	
   	if(duration>0)
   	{
   		addLogItem(DURATION_PROPERTY, ""+duration,logMessage);
   	}
   	//addLogItem(DETAILS_PROPERTY, resultDetails.toString(),logMessage);
   	
   	//logAttemptType
   	setLogAttemptType(action, sai, logMessage);
   	
   	logCorrectness(correctness, action, result, logMessage, responseMessage);
   	
   	logProblemSkill(action, problemName, step, logMessage, responseMessage);
   	
   	
   	if(feedback != null && feedback.length() > 0)
   	{
   		responseMessage.addTutorAdvice(feedback);
   	}
   	
   	if(opponent != null && opponent.length() > 0)
   	{
       	addLogItem(OPPONENT_PROPERTY, opponent, logMessage);
   	}
   	
   	//OLD (will be removed) 
   	//if(hintType != null && hintType.length() > 0)
   	//	addLogItem(HINT_TYPE, hintType, logMessage);
   	
   	if(hintType == null || hintType.length()==0 || hintType.equals(" ")) hintType="empty";
	  //  addLogItem(HINT_TYPE, hintType, logMessage);
   
   	
   	String hintSubject="empty";
   	String[] parts=hintType.split(" ");

   	if (parts.length==2){
   		hintSubject=parts[0];
   		hintType=parts[1];
   	}
		
	addLogItem(HINT_TYPE, hintType, logMessage);
	addLogItem(HINT_SUBJECT, hintSubject, logMessage);
   	  	
   	
   //	if(hintFollowed == null) hintFollowed="'";
   	if(hintFollowed == null || hintFollowed.length()==0 || hintFollowed.equals(" ") ) hintFollowed="empty";
   		addLogItem(HINT_FOLLOWED, hintFollowed, logMessage);
   	 	

   	
   	if(info == null || info.length()==0 || info.equals(" ") ) info="empty";
   		addLogItem(INFO_PROPERTY, info, logMessage);
   	
   		
   	if(myRating != 0)
   	{
   		addLogItem(RATING_PROPERTY, ""+myRating, logMessage);
   	}
   	
   	addLogItem(USERID_PROPERTY, userID, logMessage);
   	addLogItem(STATUS_PROPERTY, status, logMessage);
   	//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
   	//addLogItem(TIME_PROPERTY, dateFormat.format(new GregorianCalendar().getTime()), logMessage);
   	
   	/*if arqument is passed as null, then log current time else log the passed argument time*/
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
	if (tool_event_time==null){
		addLogItem(TIME_PROPERTY, dateFormat.format(new GregorianCalendar().getTime()), logMessage);
	}
	else{
		addLogItem(TIME_PROPERTY, tool_event_time, logMessage);		
	}
	
   	dateFormat = new SimpleDateFormat("yyyy-MM-dd EEE");
   	addLogItem(DATE_PROPERTY, dateFormat.format(new GregorianCalendar().getTime()), logMessage);
   	    	
   	addLogItem(STUDENT_LOG_COUNT_PROPERTY, ""+(studentLogCount++),logMessage);

   	if(sai != null)
   		logMessage.addSai(sai.getS(), sai.getA(), "'"+sai.getI());
   	
   	//Log tool message
   //	trace.out("Final  Tool Message : "+logMessage);
   	TutorActionLogV4 toLog = new TutorActionLogV4(logMessage);
   	
   	toLog.setSourceId(SIM_STUDENT_PLE);
   	
   	brController.getLoggingSupport().oliLog(toLog);
 	
   	//Log tutor message response
   	if((responseMessage.getActionEvaluationElement() != null && !responseMessage.getActionEvaluationElement().getEvaluation().equals(ActionEvaluationElement.HINT)) 
   			|| responseMessage.getTutorAdviceList().size() > 0)
   	{
	    	toLog = new TutorActionLogV4(responseMessage);
	    	
	    	toLog.setSourceId(SIM_STUDENT_PLE);
	    	brController.getLoggingSupport().oliLog(toLog);
   	}
  	
   	
   	
   }



	/**
	 * Get a reference to the underlying OLI stream or disk logger and put this message in the log.
	 * @param msg message to log
	 */
	private void logThis(Message msg) {
		StreamLogger sl = brController.getLoggingSupport().getOLILogger();
		if (sl != null) {
			OliDatabaseLogger odl = new OliDatabaseLogger(sl, OliDatabaseLogger.ENCODING_UTF8);
			odl.log(msg);
		}
		DiskLogger dl = brController.getLoggingSupport().getDiskLogger();
		if (dl != null) {
			OliDiskLogger odl = new OliDiskLogger(dl, OliDatabaseLogger.ENCODING_UTF8);
			odl.log(msg);
		}
	}
	
	private void addLogItem(String field, String value, ToolMessage logMessage)
	{
		if(value != null && value.length() > 0 && !value.equals("'"))
			logMessage.addCustomField(field, value);
	}
	
	/*
	 * checkCorrectness - verifies the correctness of CONFIRMATION_REQUEST_ACTIONs and HINT_RECEIVEDs
	 * 	through use of the CLAlgebraTutor.  If action is not one of these two actions, if SAI is null, 
	 * 	or if the controller is not in SimStudent mode, the same correctness string is just passed back
	 * 	out unchanged.
	 * 	@param action - the string describing the action
	 *  @param sai - the SAI to verify
	 *  @param problemName - the name of the problem in which the step takes place
	 *  @param node - the node on which the step was performed, if null, the current node is used
	 *  @param correctness - any correction information passed into the logger
	 *  @return TRUE if the SAI is correct or FALSE if the SAI is incorrect or the previous value of correctness
	 */
	private String checkCorrectness(String action, Sai sai, String problemName, String result, ProblemNode node, String correctness)
	{
		
	
		if(sai != null && brController.isSimStudentMode() && (action.equals(CONFIRMATION_REQUEST_ACTION)
				|| action.equals(HINT_RECEIVED) || action.equals(INPUT_VERIFY_ACTION) || action.equals(UNDO_ACTION))
				|| action.equals(RETRY_RECEIVED) || action.equals(ACTIVATION_RULE_ACTION) || action.equals(STUDENT_STEP_ENTERED))
   	{
   		//Calculate correctness for these cases
   		correctness = FALSE;
   		if(node == null)
   		{
   			node = brController.getCurrentNode();
   		}
   		
   		if(brController.getMissController().getSimSt().verifyStep( problemName, node,sai.getS(),sai.getA(),sai.getI()).equals(EdgeData.CORRECT_ACTION)){
   			correctness = TRUE;
   		}
   		
   		if(action.equals(INPUT_VERIFY_ACTION))
   		{
   			boolean simStCorrect = correctness.equals(TRUE);
   			boolean stAnswer = result.equals(EdgeData.CORRECT_ACTION);
   			
   			if(simStCorrect == stAnswer)
   				correctness = TRUE;
   			else
   				correctness = FALSE;
   		}
   		//Undo's correctness is opposite- if the step is incorrect, it is correct to undo it.
   		else if(action.equals(UNDO_ACTION))
   		{
   			if(correctness.equals(TRUE))
   				correctness = FALSE;
   			else
   				correctness = TRUE;
   		}
   	}
		return correctness;
	}
	
	static ContextMessage simStContext; //context message for dataset with simStudent performed actions with Outcome
	static ContextMessage studentContext; //context message for dataset with student performed actions with Outcome
	static ContextMessage flowContext; //context message for dataset with all other actions, those not containing Outcome
	static ContextMessage lmsContext; //context message for dataset with LMS related actions (for debugging the LMS)

	/*
	 * getContextMessage - create a new context message if just starting the PLE or a new problem,
	 * 	otherwise, use whatever context message is already available.  If a new context message is created, log it.
	 * 	@param action - String representing the action that is being logged
	 * 	@param problemName - the name of the problem which should be listed in the context message
	 * 	@return an appropriate context message for the logged item
	 */
	private ContextMessage getContextMessage(String action, String problemName, String details)
	{
		ContextMessage context = null;
		//Determine if we need new context messages created - ie when starting a new problem
   	if (PLE_STARTED_ACTION.equalsIgnoreCase(action) || GAMESHOW_STARTUP_ACTION.equalsIgnoreCase(action))
		{
			brController.getLoggingSupport().setProblemName("START");
			status = START_STATUS;
			createContextMessages();
		}
   	else if (PROBLEM_ENTERED_ACTION.equalsIgnoreCase(action) || PROBLEM_START_ACTION.equalsIgnoreCase(action))
		{
   		//trace.out("Problem Name from br : "+brController.getLoggingSupport().getProblemName()+"  Problem Name "+problemName);
   		if(problemName != null && problemName.length() > 0)
   			brController.getLoggingSupport().setProblemName("'"+problemName);
   		status = TUTOR_STATUS;
   		createContextMessages();
		}
   	else if (QUIZ_QUESTION_GIVEN_ACTION.equalsIgnoreCase(action) || QUIZ_QUESTION_GIVEN_ACTION_HUMAN.equals(action) || WIN_DECIDED_ACTION.equalsIgnoreCase(action) 
   		|| PROBLEM_REQUEST_ACTION.equalsIgnoreCase(action) || CONTEST_START_ACTION.equalsIgnoreCase(action)	
          )
		{
   		if(QUIZ_QUESTION_GIVEN_ACTION.equalsIgnoreCase(action) || QUIZ_QUESTION_GIVEN_ACTION_HUMAN.equals(action))
   			status = QUIZ_STATUS;
   		else
   			status = GAMESHOW_STATUS;
   		if(problemName != null && problemName.length() > 0)
   			brController.getLoggingSupport().setProblemName("'"+problemName);
   		else if(details != null && details.length() > 0)
   			brController.getLoggingSupport().setProblemName("'"+details);
   		createContextMessages();
		}
   	else if (JOIN_MATCHUP_ACTION.equalsIgnoreCase(action) || RECONNECT_ACTION.equalsIgnoreCase(action))
   	{
   		status = GAMESHOW_STATUS;
   		brController.getLoggingSupport().setProblemName("MATCHUP");
   		createContextMessages();
   	}
   	else if(EXAMPLE_VIEW.equalsIgnoreCase(action) || EXAMPLE_VIEW_END.equalsIgnoreCase(action) ){
   		//trace.out(" Example Solution Viewed : "+AplusPlatform.exampleProblem);
   		brController.getLoggingSupport().setProblemName("'"+AplusPlatform.exampleProblem);
   		createContextMessages();
   	}
   	
   	//Determine which type of context message to use, depending on the dataset it should go to
 //  	trace.out(" Action : "+action);
   	if( HINT_REQUEST_ACTION.equalsIgnoreCase(action) || CONFIRMATION_REQUEST_ACTION.equalsIgnoreCase(action)
   			|| CONFIRMATION_REQUEST_CL_ACTION.equalsIgnoreCase(action) || STEP_INPUT_ACTION.equals(action)
   			|| QUIZ_QUESTION_ANSWER_ACTION.equals(action))
   	{
   	
   		context = simStContext;
   	}
   	else if( HINT_RECEIVED.equalsIgnoreCase(action) || STUDENT_STEP_ENTERED.equalsIgnoreCase(action) || INPUT_VERIFY_ACTION.equalsIgnoreCase(action)
   			|| PROBLEM_ANSWER_SUBMIT_ACTION.equals(action) || RETRY_RECEIVED.equals(action) || CONFIRMATION_REQUEST_CL_ACTION_HUMAN.equals(action))
   	{
   		context = studentContext;
   	}
   	else if (LMS_PROBLEM_CONSIDERED.equals(action)){
   		context = lmsContext;
   	}
   	else
   	{
   		context = flowContext;
   	}
   	
   	if(context == null)
   	{
			context= brController.getLoggingSupport().getContextMessage();
			trace.out("Get the current contextMessage");
   	}
 //  	trace.out(" Context :   "+context);
//   	trace.out("End of Context details");
   	return context;
	}
	
	
	/*
	 * createContextMessages - Create and log new context messages for SimStudent, Student and flow using
	 *  the current problem settings.
	 */
	private void createContextMessages()
	{
		LoggingSupport logging = brController.getLoggingSupport();
        
		logging.setDatasetName(datasetBasename+SIMST_APPEND);
		simStContext= logging.getContextMessage();
	//	trace.out("--------Logging the following in SimStudent database-----------");
		//trace.out(simStContext.toString());
		logThis(simStContext);
	//	trace.out("--------End of storing the log in SimStudent database----------");
		
		logging.setDatasetName(datasetBasename+STUDENT_APPEND);
		studentContext= logging.getContextMessage();
	//	trace.out("---------Logging the following in Student database--------");
		//trace.out(studentContext.toString());
		logThis(studentContext);
	//	trace.out("----------End of storing the log in Student database ----------");
		
		logging.setDatasetName(datasetBasename);
		flowContext= logging.getContextMessage();
	//	trace.out("-----------Logging the following in APLUS database--------------");
		//trace.out(flowContext.toString());
		logThis(flowContext);
	//	trace.out("------------End of storing the log in APLUS database -------------\n");
		
		logging.setDatasetName(datasetBasename+LMS_APPEND);
		lmsContext= logging.getContextMessage();
	//	trace.out("-------------Logging the following in LMS database----------------");
		//trace.out(lmsContext.toString());
		logThis(lmsContext);
	//	trace.out("-------------End of storing the following in the LMS database -------------");
	}
	
	
	
	
	private String determineSkill(Sai expectedSAI, String step, String details, String action)
	{
		
		String skill = "";
		if(expectedSAI.getS().equalsIgnoreCase(Rule.DONE_NAME))
		{
			skill = "done";
		}
		else if(expectedSAI.getI().indexOf(' ') > 0)
			skill = expectedSAI.getI().substring(0,expectedSAI.getI().indexOf(' '));
		else if(expectedSAI != null && expectedSAI.getA().equals("ButtonPressed"))
		{
			skill = expectedSAI.getS();
		}
		else if(step != null && (step.indexOf('[')+1) > 0 && step.indexOf(' ') > (step.indexOf('[')+1))
		{
			skill = step.substring((step.indexOf('[')+1),step.indexOf(' '))+"-typein";
		}
		else if(details != null && details.length() > 0 && details.contains("Applying the rule "))
		{
			skill = details.substring("Applying the rule ".length(), details.indexOf(':'));
		}
		else if(step.equals(SimSt.START_STEP))
		{
			skill = "problem-entry";
		}
		/*else if(action.equals(UNDO_ACTION))
		{
			
		}*/
		else
		{
			skill = "UNKNOWN";
		}
		return skill;
	}
	
	
	
	private void logCorrectness(String correctness, String action, String result, ToolMessage logMessage, TutorMessage responseMessage)
	{
		
		if(correctness.length() > 0)
   	{
			if(action.equals(CONFIRMATION_REQUEST_CL_ACTION) || action.equals(CONFIRMATION_REQUEST_CL_ACTION_HUMAN) || action.equals(QUIZ_QUESTION_ANSWER_ACTION))
			{
				if(correctness.equalsIgnoreCase(TRUE))
					responseMessage.setAsCorrectAttemptResponse(QUIZ_RESULT);
				else
				    responseMessage.setAsIncorrectAttemptResponse(QUIZ_RESULT);
			} else if(action.equals(STEP_INPUT_ACTION) ||  action.equals(ANSWER_SUBMIT_ACTION) 
				|| action.equals(OPPONENT_ANSWER_ACTION) )
			{
				if(correctness.equalsIgnoreCase(TRUE))
					responseMessage.setAsCorrectAttemptResponse(CONTEST_RESULT);
				else
				    responseMessage.setAsIncorrectAttemptResponse(CONTEST_RESULT);
   		}
			else
			{
				if(correctness.equalsIgnoreCase(TRUE))
					responseMessage.setAsCorrectAttemptResponse(LOGGING_RESULT);
				else
				    responseMessage.setAsIncorrectAttemptResponse(LOGGING_RESULT);
			}
			/*
   		//If the correctness is specified, divide it into the student's correctness and SimSt's correctness
   		//and log these to different fields
   		if(action.equalsIgnoreCase(CONFIRMATION_REQUEST_CL_ACTION)){
   			logMessage.addCustomField(CORRECTNESS_SIMST_PROPERTY, correctness);
   			
   		}
   		else if(action.equalsIgnoreCase(QUIZ_QUESTION_ANSWER_ACTION))
   			logMessage.addCustomField(CORRECTNESS_SIMST_PROPERTY, correctness);
   		else if(action.equalsIgnoreCase(HINT_RECEIVED))
   			logMessage.addCustomField(CORRECTNESS_ST_PROPERTY, correctness);
   		else if(action.equalsIgnoreCase(CONFIRMATION_REQUEST_ACTION))
   		{
   			logMessage.addCustomField(CORRECTNESS_SIMST_PROPERTY, correctness);
   			if((result.equalsIgnoreCase("Correct Action") && correctness.equals(TRUE))
   					|| (result.equalsIgnoreCase("Error Action") && correctness.equals(FALSE)))
   			{
       			logMessage.addCustomField(CORRECTNESS_ST_PROPERTY, TRUE);
   			}
   			else if((result.equalsIgnoreCase("Correct Action") && correctness.equals(FALSE))
   					|| (result.equalsIgnoreCase("Error Action") && correctness.equals(TRUE)))
   			{
       			logMessage.addCustomField(CORRECTNESS_ST_PROPERTY, FALSE);
   			}
   		}
   		else  //If we don't know what the correctness is coming from, attribute to SIMST
   		{
   			logMessage.addCustomField(CORRECTNESS_SIMST_PROPERTY, correctness);
   		}*/
   	}
		else
		{
			responseMessage.setAsHintResponse();
			if(!action.equals(METATUTOR_LEFT_HINT_ACTION) && !action.equals(HINT_REQUEST_ACTION)
					&& !action.equals(CONTEXT_MENU_QUESTION_ACTION))
				responseMessage.getActionEvaluationElement().setEvaluation("UNGRADED");
			if(action.equals(METATUTOR_LEFT_HINT_ACTION) || action.equals(METATUTOR_CLOSE_HINT_ACTION) ||
					action.equals(METATUTOR_PREVIOUS_HINT_ACTION) || action.equals(METATUTOR_NEXT_HINT_ACTION))
				responseMessage.getActionEvaluationElement().setCurrentHintNumber(result);
			if(action.equals(CONTEXT_MENU_QUESTION_ACTION))
				responseMessage.getActionEvaluationElement().setCurrentHintNumber("1");
		}
	}
	
	private void logExpected(String action, String correctness, String result, Sai sai, String expSelection, String expAction, String expInput, String step, String details, ToolMessage logMessage, TutorMessage responseMessage)
	{
		if(correctness.length() <= 0 && expSelection != null && expAction != null && expInput != null 
				&& expSelection.length() > 0 && expAction.length() > 0 && expInput.length() > 0)
   	{
			//responseMessage.addSai(expSelection, expAction, expInput);
			/*logMessage.addCustomField(EXPECTED_SELECTION_PROPERTY, expSelection);
			logMessage.addCustomField(EXPECTED_ACTION_PROPERTY, expAction);
			logMessage.addCustomField(EXPECTED_INPUT_PROPERTY, expInput);
			*/
			Sai expectedSAI = new Sai(expSelection, expAction, expInput);
			//determine skill
			String skill = determineSkill(expectedSAI, step, details, action);

			String agent = getSkillAgent(action);
			responseMessage.addSkill(new SkillElement(skill+"-"+agent, skill));
		}
   	else if(correctness.equals(FALSE) && expSelection != null && expAction != null && expInput != null){
   		if(expSelection.length() > 0 && expAction.length() > 0 && expInput.length() > 0)
   		{
   			
   			//responseMessage.addSai(expSelection, expAction, expInput);
   			/*logMessage.addCustomField(EXPECTED_SELECTION_PROPERTY, expSelection);
   			logMessage.addCustomField(EXPECTED_ACTION_PROPERTY, expAction);
   			logMessage.addCustomField(EXPECTED_INPUT_PROPERTY, expInput);
   			*/
   			Sai expectedSAI = new Sai(expSelection, expAction, expInput);
   			//determine skill
   			String skill = determineSkill(expectedSAI, step, details, action);

   			String agent = getSkillAgent(action);
   			responseMessage.addSkill(new SkillElement(skill+"-"+agent, skill));
   			///logMessage.addCustomField(EXPECTED_SKILL_PROPERTY, skill);
   		}
   	}
		else if(action.equals(INPUT_VERIFY_ACTION) && result != null && result.equals(EdgeData.CLT_ERROR_ACTION)
				&& correctness.equals(TRUE) && expSelection != null && expAction != null && expInput != null)
		{
			//If the student correctly says that SimStudent got an error, we want to log the expected Selection
			//not SimStudent's, as expected
   		if(expSelection.length() > 0 && expAction.length() > 0 && expInput.length() > 0)
   		{
   			//responseMessage.addSai(expSelection, expAction, expInput);
   			Sai expectedSAI = new Sai(expSelection, expAction, expInput);
   			//determine skill
   			String skill = determineSkill(expectedSAI, step, details, action);

   			String agent = getSkillAgent(action);
   			responseMessage.addSkill(new SkillElement(skill+"-"+agent, skill));
   		}
		}
   	else if(correctness.equals(TRUE) && sai!= null && !action.equalsIgnoreCase(PROBLEM_ANSWER_SUBMIT_ACTION)){
   		if(sai.getS().length() > 0 && sai.getA().length() > 0 && sai.getI().length() > 0)
   		{
   			//responseMessage.addSai(sai.getS(), sai.getA(), sai.getI());
   			expInput = sai.getI();
       		/*logMessage.addCustomField(EXPECTED_SELECTION_PROPERTY, sai.getS());
       		logMessage.addCustomField(EXPECTED_ACTION_PROPERTY, sai.getA());
       		logMessage.addCustomField(EXPECTED_INPUT_PROPERTY, sai.getI());
       		 */
       		//If result is correct, expected SAI is SAI provided (even if other expectations possible)
   			String skill = determineSkill(sai, step, details, action);

   			String agent = getSkillAgent(action);
   			responseMessage.addSkill(new SkillElement(skill+"-"+agent, skill));
   			
   			//logMessage.addCustomField(EXPECTED_SKILL_PROPERTY, skill);
       	}
   	}
		if(step != null && step.length() > 0)
		{
			responseMessage.addSai("'"+step, "", expInput);
		}
		else
		{
			responseMessage.addSai("", "", "");
		}
	}
	
	private String getSkillAgent(String action)
	{
		if((action.equalsIgnoreCase(HINT_RECEIVED) || action.equalsIgnoreCase(STUDENT_STEP_ENTERED) || action.equalsIgnoreCase(BAD_INPUT_RECEIVED) || action.equals(INPUT_VERIFY_ACTION) 
			|| action.equals(NOT_LEARN_ACTION)|| action.equals(UNDO_ACTION) || action.equals(VIEW_CONTESTANT_ACTION)
			|| action.equals(PROBLEM_SUBMIT_ACTION)) || action.equals(NOT_LEARN_SKIP_ACTION) || action.equals(RETRY_RECEIVED)
			|| action.equals(METATUTOR_MODEL_TRACING_ACTION) || action.equals(CONTEXT_MENU_QUESTION_ACTION) || action.equals(CONFIRMATION_REQUEST_CL_ACTION_HUMAN))
   	{
   		return STUDENT_ATTEMPT;
   	}
   	else if((action.equalsIgnoreCase(CONFIRMATION_REQUEST_ACTION) || action.equalsIgnoreCase(CONFIRMATION_REQUEST_CL_ACTION) || action.equalsIgnoreCase(HINT_REQUEST_ACTION)))
   	{
   		return SIMSTUDENT_ATTEMPT;
   	}
   	else if(action.equalsIgnoreCase(OPPONENT_ANSWER_ACTION))
   	{
   		return OPPONENT_ATTEMPT;
   	}
   	else if(action.equalsIgnoreCase(METATUTOR_HINT_ACTION))
   	{
   		return METATUTOR_ATTEMPT;
   	}
   	else //Default to SimStudent for any other agent
   	{
   		return SIMSTUDENT_ATTEMPT;
   	}
	}
	
	private void logProblemSkill(String action, String problem, String step, ToolMessage logMessage, TutorMessage responseMessage)
	{
		ProblemAssessor assess = brController.getMissController().getSimSt().getProblemAssessor();
		
		if(problem != null && problem.length() > 0 && !problem.equals("START"))
		{
			String problemPattern = assess.abstractProblem(problem);
			String problemType = assess.classifyProblem(problem);
			
			//addLogItem(ABSTRACT_PROBLEM_PROPERTY, "'"+problemPattern,logMessage);
			//addLogItem(PROBLEM_TYPE_PROPERTY, problemType,logMessage);
	
			if(action.equals(PROBLEM_ANSWER_SUBMIT_ACTION) || action.equals(QUIZ_QUESTION_ANSWER_ACTION) || action.equals(PROBLEM_ENTERED_ACTION))
			{
				SkillElement problemSkill = new SkillElement("'"+problemPattern, problemType);
				problemSkill.addModelName(PROBLEM_SUBMIT_MODEL);
				responseMessage.addSkill(problemSkill);
			}
		
			

			SkillElement problemModelSkill = new SkillElement("'"+problemPattern, problemType);
			problemModelSkill.addModelName(PROBLEM_MODEL);
			responseMessage.addSkill(problemModelSkill);
	    	
		}

		if(step != null & step.length() > 0 && !step.startsWith(QUIZ_STATUS) && !step.equals("START"))
		{
			String step1 = step.contains("[") ? step.substring(0, step.indexOf("[")) : step;
			String stepPattern = assess.abstractProblem(step1);
			String stepType = "Transformation";
			if(step.contains("["))
			{
				step1 = step.substring(step.indexOf("[")+1, step.indexOf("]"));
				if(step1.contains(" "))
				{
					String operator = step1.substring(0, step1.indexOf(" "));
					String operand = step1.substring(step1.indexOf(" ")+1);
					String operandPattern = assess.abstractProblem(operand);
					stepPattern += "["+operator+" "+operandPattern+"]";
					stepType = "Typein";
				}
			}
			//addLogItem(ABSTRACT_STEP_PROPERTY, "'"+stepPattern,logMessage);
			
			SkillElement problemSkill = new SkillElement("'"+stepPattern, stepType);
			problemSkill.addModelName(STEP_MODEL);
	    	responseMessage.addSkill(problemSkill);
		}
		
	}
	
	private void setLogAttemptType(String action, Sai sai, ToolMessage logMessage)
	{
		// Logs the source of the SAI whether from the SimStudent or the user.
   	if(sai != null && (action.equalsIgnoreCase(HINT_RECEIVED) || action.equals(RETRY_RECEIVED) || action.equals(PROBLEM_ANSWER_SUBMIT_ACTION)
   			|| action.equals(METATUTOR_MODEL_TRACING_ACTION) || action.equals(STUDENT_STEP_ENTERED) || action.equals(CONFIRMATION_REQUEST_CL_ACTION_HUMAN)))
   	{
   		//addLogItem(SAI_AGENT_PROPERTY, userID,logMessage);
       	logMessage.setAsAttempt(STUDENT_ATTEMPT);
   	}
   	else if(sai != null && (action.equalsIgnoreCase(CONFIRMATION_REQUEST_ACTION) || action.equalsIgnoreCase(CONFIRMATION_REQUEST_CL_ACTION))
   		|| action.equals(STEP_INPUT_ACTION) || action.equals(ANSWER_SUBMIT_ACTION) || action.equals(QUIZ_QUESTION_ANSWER_ACTION)
   		|| action.equals(ACTIVATION_RULE_ACTION))
   	{
   		//addLogItem(SAI_AGENT_PROPERTY, SIMST_SAI,logMessage);
       	logMessage.setAsAttempt(SIMSTUDENT_ATTEMPT);
   	}
   	else if(action.equals(OPPONENT_ANSWER_ACTION))
   	{
   		logMessage.setAsAttempt(OPPONENT_ATTEMPT);
   	}
   	//else if second part of simstudent input confirmation, student verify attempt
   	else if (sai != null && (action.equals(INPUT_VERIFY_ACTION) || action.equals(UNDO_ACTION)))
   	{
   		logMessage.setAsAttempt(STUDENT_META_ATTEMPT);
   	}
   	else if(action.equals(NOT_LEARN_ACTION) || action.equals(BAD_INPUT_RECEIVED) || action.startsWith(HINT_EXPLAIN_ACTION) || action.startsWith(ON_PAPER_EXPLAIN_ACTION)
   			|| action.startsWith(PROBLEM_ENTERED_EXPLAIN_ACTION) || action.startsWith(INPUT_WRONG_EXPLAIN_ACTION) 
   			|| action.equals(NO_EXPLAIN_ACTION) || action.equals(PROBLEM_INVALID_ACTION) || action.equals(PROBLEM_REQUEST_TIMEOUT_ACTION)
   			|| action.equals(CHALLENGE_TIMEOUT_ACTION) || action.equals(OUTSTANDING_ACTION)
   			|| action.equals(SKILLOMETER_UPDATE_ACTION) || action.equals(NOT_LEARN_SKIP_ACTION)
   			|| action.equals(CONTEXT_MENU_QUESTION_ACTION))
   	{
   		logMessage.setAsHintRequest(STUDENT_ATTEMPT);
   	}
   	else if(action.equals(HINT_REQUEST_ACTION) || action.equals(WIN_DECIDED_ACTION)  
   			|| action.equals(EXPLANATION_CATEGORIZE_ACTION))
   	{
   		logMessage.setAsHintRequest(SIMSTUDENT_ATTEMPT);
   	}
   	else if(action.equals(METATUTOR_HINT_ACTION))
   	{
   		logMessage.setAsHintRequest(METATUTOR_ATTEMPT);
   	}
   	else
   	{
       	logMessage.setAsHintRequest();
   	}
	}
	public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent, String info)
   {
		// if(action.equals(PROBLEM_DURATION))
			//   trace.out(" Problem Duration "+duration);
		simStLog(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection,expAction, expInput, duration, feedback, opponent, info,0,true,null,null,getCurrentTime());
   }
	
	public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent)
	{
		// if(action.equals(PROBLEM_DURATION))
			//   trace.out(" Problem Duration "+duration);
		simStLog(actionType,action,step,result,resultDetails, sai, node, correctness, expSelection,
			expAction, expInput, duration, feedback, opponent, "");	
	}
	

	public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback, String opponent, int rating)
	{
		simStLog(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, "", rating,true,null,null,getCurrentTime());
	}
	
	public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
   		String expAction, String expInput, int duration, String feedback)
	{
		// if(action.equals(PROBLEM_DURATION))
			//   trace.out(" Problem Duration" +duration);
		simStLog(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection,
   		expAction, expInput, duration, feedback,"");
	}


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai)
   {
	 //  if(action.equals(PROBLEM_DURATION))
		//   trace.out(" Problem Duration");
   	simStLog(actionType, action, step, result, resultDetails, sai, null, "","","","",0,"");
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, int duration)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, null, "","","","",duration,"");
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, int duration, String feedback)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, null, "","","","",duration,feedback);
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, int duration, String feedback, String opponent)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, null, "","","","",duration,feedback,opponent);
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, int duration, String feedback, String opponent, int rating)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, null, "", "", "", "", duration, feedback, opponent, "", rating,true,null,null,getCurrentTime());
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai,ProblemNode node, String expSelection, String expAction, String expInput)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, node, "", expSelection, expAction, expInput,0,"");
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai,ProblemNode node, String expSelection, String expAction, String expInput, int duration, String feedback)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, node, "", expSelection, expAction, expInput,duration,feedback);
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, boolean correctness,String expSelection, String expAction, String expInput )
   {
   	String correct = "";
   	if(correctness){
   		correct = TRUE;
       	simStLog(actionType, action, step, result, resultDetails, sai, null, correct,"","","",0,"");	
   	}
   	else {
   		correct = FALSE;
       	simStLog(actionType, action, step, result, resultDetails, sai, null, correct, expSelection, expAction, expInput,0,"");    	
   	}
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, boolean correctness,String expSelection, String expAction, String expInput, int rating )
   {
   	String correct = "";
   	if(correctness){
   		correct = TRUE;
       	simStLog(actionType, action, step, result, resultDetails, sai, null, correct, "", "", "", 0, "", "", "", rating,true,null,null,getCurrentTime());
   	}
   	else {
   		correct = FALSE;
       	simStLog(actionType, action, step, result, resultDetails, sai, null, correct, expSelection, expAction, expInput, 0, "", "", "", rating,true,null,null,getCurrentTime());
   	}
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails)
   {
	  // if(action.equals(PROBLEM_DURATION))
		//   trace.out(" Problem Duration");
      simStLog(actionType, action, step, result, resultDetails, null);
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, int duration, String feedback)
   {

   	simStLog(actionType, action, step, result, resultDetails, null, null, "","","","",duration,feedback);	
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, int duration, String feedback, int rating)
   {

   	simStLog(actionType, action, step, result, resultDetails, null, null, "", "", "", "", duration, feedback, "", "", rating,true,null,null,getCurrentTime());
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, int duration, String feedback, String opponent)
   {

   	simStLog(actionType, action, step, result, resultDetails, null, null, "","","","",duration,feedback,opponent);	
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, int duration, String feedback, String opponent, int rating)
   {
   	simStLog(actionType, action, step, result, resultDetails, null, null, "", "", "", "", duration, feedback, opponent, "", rating,true,null,null,getCurrentTime());
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, int duration)
   {
      simStLog(actionType, action, step, result, resultDetails, null, duration);
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, int duration, int myRating)
   {
      simStLog(actionType, action, step, result, resultDetails, null, null, "", "", "", "", duration, "", "", "", myRating,true,null,null,getCurrentTime());
   } 

   public void simStLog(String actionType, String action, String result, Object resultDetails)
   {
   	simStLog(actionType, action, "", result, resultDetails, null);
   }


   public void simStLog(String actionType, String action, String result, Object resultDetails, boolean correctness)
   {
   	simStLog(actionType, action, "", result, resultDetails, null, correctness,"","","");
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, boolean correctness, int duration)
   {
   	String correct = "";
   	if(correctness){
   		correct = TRUE;
   	}
   	else {
   		correct = FALSE;
   	}
   	simStLog(actionType, action, step, result, resultDetails, null, null, correct,"","","",duration,"");    	
   }


   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, boolean correctness, int duration, int rating)
   {
   	String correct = "";
   	if(correctness){
   		correct = TRUE;
   	}
   	else {
   		correct = FALSE;
   	}
   	simStLog(actionType, action, step, result, resultDetails, null, null, correct, "", "", "",
   			duration, "", "", "", rating,true,null,null,getCurrentTime());
   }

   public void simStLog(String actionType, String action, String result, Object resultDetails, Sai sai, boolean correctness)
   {
   	simStLog(actionType, action, "", result, resultDetails, sai, correctness,"","","");
   }

   public void simStLog(String actionType, String action, String step, String result, Object resultDetails, Sai sai, boolean correctness)
   {
   	simStLog(actionType, action, step, result, resultDetails, sai, correctness,"","","");
   }

   public void simStLog(String actionType, String action, Sai sai, boolean correctness)
   {
   	simStLog(actionType, action, "", "", sai, correctness);
   }

   public void simStLog(String actionType, String action, String result)
   {
   	simStLog(actionType, action, result, "");
   }

   public void simStLog(String actionType, String action, String result, int rating)
   {
   	simStLog(actionType, action, "", result, "", null, null, "", "", "", "", 0, "", "", "", rating,true,null,null,getCurrentTime());
   }

   public void simStLog(String actionType, String action, String result,boolean correctness)
   {
   	simStLog(actionType, action, result, "", correctness);
   }

   public void simStShortLog(String actionType, String action, String result, String info)
   {
   	simStLog(actionType,action,"",result,"", null, null, "", "",
   			"", "", 0, "", "", info);	
   }


   public void simStShortLog(String actionType, String action, String result, String info, int duration)
   {	
   	simStLog(actionType,action,"",result,"", null, null, "", "",
   			"", "", duration, "", "", info);
   }

   public void simStShortLog(String actionType, String action, String result, String info, int duration, int rating)
   {
   	simStLog(actionType, action, "", result, "", null, null, "", "", "", "", duration, "", "", info, rating,true,null,null,getCurrentTime());
   }

   public void simStShortLog(String actionType, String action, String result, String info, String opponent)
   {
   	simStLog(actionType,action,"",result,"", null, null, "", "",
   			"", "", 0, "", opponent, info);
   }

   public void simStShortLog(String actionType, String action, String result, String info, String opponent, int rating)
   {
   	simStLog(actionType, action, "", result, "", null, null, "", "", "", "", 
   			0, "", opponent, info, rating,true,null,null,getCurrentTime());
   }

   public void simStInfoLog(String actionType, String action, String step, String result, String info)
   {
   	simStLog(actionType,action,step,result,"", null, null, "", "",
   			"", "", 0, "", "", info);	
   }


   public void simStLogException(Exception e)
   {
   	StackTraceElement[] stack = e.getStackTrace();
   	String stackTrace = "";
   	for(StackTraceElement ste: stack)
   	{
   		stackTrace += ste+"\n";
   	}
   	simStLog(SIM_STUDENT_ERROR, EXCEPTION_ACTION,e.getMessage()+":"+e.getCause(),stackTrace);
   }

   public void simStLogException(Exception e, String message)
   {
   	StackTraceElement[] stack = e.getStackTrace();
   	String stackTrace = "";
   	for(StackTraceElement ste: stack)
   	{
   		stackTrace += ste+";";
   	}
   	simStLog(SIM_STUDENT_ERROR, EXCEPTION_ACTION,message+"-"+e.getMessage()+":"+e.getCause(),stackTrace);
   }

   public void ssGameShowException(Exception e, String message)
   {
   	StackTraceElement[] stack = e.getStackTrace();
   	String stackTrace = "";
   	for(StackTraceElement ste: stack)
   	{
   		stackTrace += ste+";";
   	}
   	simStLog(SSGAME_ERROR, EXCEPTION_ACTION,message+"-"+e.getMessage()+":"+e.getCause(),stackTrace);
   }

   public void enableLogging(boolean logRemote, boolean logLocal, String userID)
   {
   	SimStLogger.userID = userID;
       PreferencesModel pm = brController.getPreferencesModel();
       if(!(logRemote || logLocal))
   	{
       	if(trace.getDebugCode("ss"))trace.out("ss", "Logging not enabled.");
   		if(pm != null)
   		{
   			pm.setBooleanValue(BR_Controller.USE_OLI_LOGGING, false);
   			pm.setBooleanValue(BR_Controller.USE_DISK_LOGGING, false);
   		}
   		return;
   	}
       if (pm != null)
       {
       	//Enable other logging too, not just disk?
       	pm.setBooleanValue(BR_Controller.USE_OLI_LOGGING, logRemote);
       	pm.setStringValue(BR_Controller.OLI_LOGGING_URL, log_url);
       	pm.setBooleanValue(BR_Controller.USE_DISK_LOGGING, logLocal);
       	// Check if running in WebStart Mode, then set the DEFAULT_LOG_DIR
       /*	if(brController.getMissController().getSimSt().isWebStartMode())
       		pm.setStringValue(BR_Controller.DISK_LOGGING_DIR, WebStartFileDownloader.SimStWebStartDir + DEFAULT_LOG_DIR + "_" + userID + 
       				"_" + FileZipper.formattedDate());
       	else
       		pm.setStringValue(BR_Controller.DISK_LOGGING_DIR, DEFAULT_LOG_DIR);*/
       }
       //brController.getLoggingSupport().setEnableAuthorLog(true);
   	brController.getLogger().setAnonymizedStudentName(userID);

		if(brController.getMissController().getSimSt() != null)
		{
			String condition = brController.getMissController().getSimSt().getSsCondition();
			int value = condition.contains("control") ? 0 : 1;
			brController.getLogger().addStudyConditionName(condition,value);
			/*if(brController.getMissController().getSimSt().isSelfExplainMode())
			{
		    	brController.getLogger().addStudyConditionName("selfExplain Enabled", 1);
			}
			else
			{
		    	brController.getLogger().addStudyConditionName("selfExplain Disabled", 0);
			}*/
		}
   	
   	
       loggingEnabled = true;
   }

   public boolean getLoggingEnabled()
   {
   	return loggingEnabled;
   }
}