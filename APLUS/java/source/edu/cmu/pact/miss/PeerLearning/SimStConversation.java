package edu.cmu.pact.miss.PeerLearning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;

public class SimStConversation {
	
	public static final String METATUTOR = "<MT>";
	public static final String QUIZ = "<Q>";
	public static final String MODEL_TRACE_ERROR = "<ERR>";
	public static final String SELECTION = "<s>";
	public static final String INPUT = "<i>";
	public static final String OPERATION = "<o>";
	public static final String NO_ACTIVATIONS = "<A0>";
	public static final String NOT_FIRST_ACTIVATION = "<A2>";
	public static final String BEHAVIOUR_DISCREPENCY = "<BD>";
	public static final String SAI = "<sai>";
	
	public static final String NEW_PROBLEM_TOPIC = "NEW_PROBLEM";
	public static final String NEW_PROBLEM_COG_TUTOR_TOPIC = "NEW_PROBLEM_COG_TUTOR";
	public static final String QUIZ_COG_TUTOR_TOPIC = "QUIZ_COG_TUTOR";
	public static final String COG_TUTOR_AFTER_PROBLEM_TOPIC = "AFTER_PROBLEM_COG_TUTOR";
	public static final String APLUS_CONTROL_QUIZ_SECTION_PASSED_TOPIC = "APLUS_CONTROL_QUIZ_SECTION_PASSED";
	public static final String COG_TUTOR_SECTION_PASSED_EXAMPLES_TOPIC = "COG_TUTOR_SECTION_PASSED_EXAMPLES";
	public static final String COG_TUTOR_SECTION_PASSED_EXAMPLES_START_TOPIC = "COG_TUTOR_SECTION_PASSED_EXAMPLES_START";
	public static final String COG_TUTOR_SECTION_PASSED_TOPIC = "COG_TUTOR_SECTION_PASSED";
	public static final String COG_TUTOR_QUIZ_CORRECT = "COG_TUTOR_QUIZ_CORRECT";
	public static final String COG_TUTOR_QUIZ_ALL_PROBLEMS_CORRECT = "COG_TUTOR_QUIZ_ALL_PROBLEMS_CORRECT";
	public static final String COG_TUTOR_PRACTICE_FIRST = "COG_TUTOR_PRACTICE_FIRST";
	public static final String START_PROBLEM_TOPIC = "START_PROBLEM";
	public static final String COG_TUTOR_START_PROBLEM_TOPIC = "COG_TUTOR_START_PROBLEM";
	public static final String THINK_TOPIC = "THINK";
	public static final String DONE_FEEDBACK_TOPIC = "DONE_FEEDBACK";
	public static final String FEEDBACK_TOPIC = "FEEDBACK";
	public static final String SOLVED_TOPIC = "SOLVED";
	public static final String TRANSFORMATION_HINT_TOPIC = "TRANSFORMATION_HINT";
	public static final String TYPEIN_HINT_TOPIC = "TYPEIN_HINT";
	public static final String TRANSFORMATION_HINT_EXPLANATION_TOPIC = "TRANSFORMATION_EXPLANATION_HINT";
	public static final String TYPEIN_HINT_EXPLANATION_TOPIC = "TYPEIN_EXPLANATION_HINT";
	public static final String FAIL_TO_LEARN_GIVE_UP_TOPIC = "FAIL_TO_LEARN_GIVE_UP";
	public static final String START_PROBLEM_NO_TOPIC = "START_PROBLEM_NO";
	public static final String STEP_CORRECT_TOPIC = "STEP_CORRECT";
	public static final String STEP_INCORRECT_TOPIC = "STEP_INCORRECT";
	public static final String CHECK_ANSWER = "CHECK_ANSWER";
	public static final String NO_VAR_VALUE_CHECK_ANS = "NO_VAR_VALUE_CHECK_ANS";
	public static final String PLUG_IN = "PLUG_IN";
	public static final String NO_VAR_TO_PLUG = "NO_VAR_TO_PLUG";
	public static final String BALANCE_CHECK_ANSWER = "BALANCE_CHECK_ANSWER";
	public static final String NO_BALANCE_CHECK_ANSWER = "NO_BALANCE_CHECK_ANSWER";
	public static final String VERIFY_WRONG = "VERIFY_WRONG";
	public static final String CONFIRM_TOPIC = "CONFIRM";
	public static final String SKIPPED_TOPIC = "SKIPPED";
	public static final String UNDO_CONFIRM_STEPS_TOPIC = "UNDO_CONFIRM_STEPS";
	public static final String NOTHING_TO_UNDO_TOPIC = "NOTHING_TO_UNDO";
	public static final String UNDO_RESUME_TOPIC = "UNDO_RESUME";
	public static final String UNDO_SHOULD_DO_TOPIC = "UNDO_SHOULD_DO";
	public static final String UNDO_REMEMBER_PREVIOUS_TOPIC = "UNDO_REMEMBER_PREVIOUS";
	public static final String NO_UNDO_TOPIC = "NO_UNDO";
	public static final String NO_UNDO_DONE_TOPIC = "NO_UNDO_DONE";
	public static final String NO_UNDO_FAIL_TO_LEARN_TOPIC = "NO_UNDO_FAIL_TO_LEARN";
	public static final String FEEDBACK_NEGATIVE_TOPIC = "FEEDBACK_NEGATIVE";
	public static final String FEEDBACK_NEGATIVE_DONE_TOPIC = "FEEDBACK_NEGATIVE_DONE";
	public static final String ALL_QUIZ_FAILED_TOPIC = "ALL_QUIZ_FAILED";
	
	
	
	public static final int ERROR_THRESHOLD = 4;
	
	private BR_Controller brController;
	
	private boolean modelTraced = false;
	private boolean metatutored = false;
	private ModelTraceWorkingMemory mtwm;
	private ModelTracer apmt;
	
	private Hashtable<String, ArrayList<String>> topics;
	
	//flag to indicate that different behaviour between quiz and tutoring was detected, so 
	//appropriate message (indicated by <FQ> in simSt-speech.txt) must be displayed.
	private boolean behaviourDiscrepency=false;
	public void setBehaviourDiscrepency(boolean flag){
		this.behaviourDiscrepency=flag;
		if (flag)
			setBehaviourDiscrepencyBroughtUp(true);	
		
	}	
	private boolean getBehaviourDiscrepency(){return false;/*this.behaviourDiscrepency;*/}
	
	/*flag to indicate if different behaviour between quiz and tutoring has been brought up
	 * in SimStudent language (SimStudent should only refer to this once while solving a problem)*/
	boolean behaviourDiscrepencyBroughtUp=false;
	public void setBehaviourDiscrepencyBroughtUp(boolean flag){ behaviourDiscrepencyBroughtUp=flag; }
	public boolean getBehaviourDiscrepencyBroughtUp(){ return behaviourDiscrepencyBroughtUp; }
	
	
	public SimStConversation(BR_Controller brController, String filename)
	{
		this.brController = brController;
		topics = new Hashtable<String, ArrayList<String>>();
		readTopics(filename);
		
		if(brController.getAmt() != null  && brController.getMissController().getSimSt().getModelTraceWM() != null)
		{
			modelTraced = true;
			mtwm = brController.getMissController().getSimSt().getModelTraceWM();
			apmt = brController.getAmt().getaPlusModelTracing();
		}
		if(brController.getMissController().getSimSt().isSsMetaTutorMode() && !brController.getMissController().getSimSt().getSsMetaTutorModeLevel().equals("Cognitive"))
			metatutored = true;
	}
		
	private void readTopics(String filename)
	{
		
			// worked like that
	    	//String file = brController.getMissController().getSimSt().getProjectDir() + "/"+filename;
			//BufferedReader reader=null;
			
		String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+filename;
			ClassLoader cl = this.getClass().getClassLoader();
			InputStream is = cl.getResourceAsStream(file);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader reader=null;
    	
	    	
	    	try
	    	{
	    		//reader=new BufferedReader(new FileReader(file));
	        	reader = new BufferedReader(isr);
	    		String line = reader.readLine();
	    		
	    		while(line != null)
	    		{
	    			String topic = line;
	    			if(topic.length() > 0)
	    			{
	    				line = reader.readLine();
	    				ArrayList<String> values = new ArrayList<String>();

	    				while(line != null && line.length() > 0)
	    				{
	    					values.add(line);
	    					line = reader.readLine();
	    				}
	    				
	    				trace.out("ss", "Added topic "+topic+" with "+values.size()+" choices.");
	    				topics.put(topic, values);
	    				
	    			}
	    			if(line != null)
	    				line = reader.readLine();
	    		}
	    		

	    	}catch(Exception e)
	    	{
	    		if(trace.getDebugCode("miss"))trace.out("miss", "Unable to read config file: "+e.getMessage());
	    		e.printStackTrace();
	    	}finally 
	    	{
	    		try{reader.close();}catch(Exception e){	}
	    	}
	}
	
   
	
	public String getMessage(String topic)
	{
		return getMessage(topic, null, null, null, null, -1);
	}
	
	public String getSimStMessage1(String topic, String selection, String input)
	{
		/*if (selection.contains("transformation"))
			selection="for the " + selection;
		else selection = "on the " + selection;
		*/
		return getMessage(topic, selection, null, input, null, -1);
	}

	public String getMessage(String topic, String operation)
	{
		return getMessage(topic, null, null, null, operation, -1);
	}

	public String getMessage(String topic, int activationNum)
	{
		return getMessage(topic, null, null, null, null, activationNum);
	}

	public String getSimStMessage(String topic, String selection, String input, int activationNum)
	{
		/*if (selection.contains("transformation"))
			selection="for the " + selection;
		else selection = "on the " + selection;
			*/	
		return getMessage(topic, selection, null, input, null, activationNum);
	}
	
	
	public String getMessage(String topic, String selection, String action, String input, String operation, int activationNum)
	{
		ArrayList<String> messages = topics.get(topic);   
	
		String message = messages.get((int)(Math.random()*messages.size()));
	
		if(!messageWorks(message, selection, input, operation, activationNum)) {
			message = getFilteredMessage(messages, selection, input, operation, activationNum);

		}
					
		return replaceVariables(message, selection, action, input, operation);
	}
	
	public String getFilteredMessage(ArrayList<String> messages,String selection, String input, String operation, int activationNum)
	{
		ArrayList<String> filtered = new ArrayList<String>();
		for(int i=0;i<messages.size();i++)
		{	
		
			String message = messages.get(i);
			if(messageWorks(message, selection, input, operation, activationNum)) {
				filtered.add(message);
			}
		}
		if(filtered.size() == 0)
			return "";
		
		
		return filtered.get((int)(Math.random()*filtered.size()));
	}
	
	public boolean isTextInTopic(String topic, String text){
		ArrayList<String> messages = topics.get(topic);

		int loc=text.indexOf(":");
		String pureText=text.substring(loc+2, text.length());
		    return messages.contains(pureText);
		
	}
	
	public boolean messageWorks(String message,String selection, String input, String operation, int activationNum)
	{

		trace.out("ss", "Checking if message works: "+message);		

		if(!modelTraced || !metatutored)
		{//!metatutor
			if(message.contains(METATUTOR))
				return false;
		}
		if (getBehaviourDiscrepency()){		//from the specific topic, if  is set selection only what is marked as BD in simSt-speech.txt.
			if (!message.contains(BEHAVIOUR_DISCREPENCY))
				return false;
		}	
		if(!modelTraced || WorkingMemoryConstants.FALSE.equals(mtwm.getQuizTaken()))
		{//!quiz
			if(message.contains(QUIZ))
				return false;
		}
		if(!metatutored || apmt.getTraceHistoryIncorrectCount() < ERROR_THRESHOLD)
		{
			if(message.contains(MODEL_TRACE_ERROR))
				return false;
		}
		if(selection == null)
		{
			if(message.contains(SELECTION))
				return false;
		}
		if(input == null)
		{
			if(message.contains(INPUT))
				return false;
		}
		if(operation == null)
		{
			if(message.contains(OPERATION))
				return false;
		}
		if(activationNum == -1)
		{
			if(message.contains(NO_ACTIVATIONS) || message.contains(NOT_FIRST_ACTIVATION))
				return false;
		}
		if(activationNum < 1)
		{
			if(message.contains(NOT_FIRST_ACTIVATION))
				return false;
		}
		if(activationNum > 0)
		{
			if(message.contains(NO_ACTIVATIONS))
				return false;
		}	
			
		return true;
	}
	
	public String replaceVariables(String message, String selection, String action, String input, String operation)
	{
		
		message = message.replaceAll(BEHAVIOUR_DISCREPENCY, "");
		message = message.replaceAll(METATUTOR, "");
		message = message.replaceAll(SELECTION, selection);
		message = message.replaceAll(INPUT, input);
		message = message.replaceAll(OPERATION, operation);
		message = message.replaceAll(NO_ACTIVATIONS, "");
		message = message.replaceAll(NOT_FIRST_ACTIVATION, "");
		message = message.replaceAll(MODEL_TRACE_ERROR, "");
		message = message.replaceAll(QUIZ, "");
		
		if (brController.getMissController().getSimStPLE()!=null)
			message=brController.getMissController().getSimStPLE().messageComposer(message,selection,action,input);
		
		return message;
	}
	
}
