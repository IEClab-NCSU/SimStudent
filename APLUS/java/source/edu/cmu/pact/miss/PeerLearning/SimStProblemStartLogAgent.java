package edu.cmu.pact.miss.PeerLearning;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStLogAgent.LogEntry;

public class SimStProblemStartLogAgent extends SimStLogAgent{

	private static SimStProblemStartLogAgent instance = null;
	
	public SimStProblemStartLogAgent(BR_Controller brController) {
		super(brController);
	}

	public static SimStProblemStartLogAgent getInstance(BR_Controller newBrController){
		if (instance==null){
			instance= new SimStProblemStartLogAgent(newBrController);
		}
		return instance;
	}

	
	/*boolean to hold if problem was entered */
	boolean problemEntered=false;
	void setProblemEntered(boolean flag){problemEntered=flag;}
	boolean isProblemEntered(){return problemEntered;}
	
	/*variable to hold the tiem problem was entered*/
	Date problemEnteredDate;
	void setProblemEnteredDate(String problemStart) throws ParseException{
		problemEnteredDate=stringToDate(problemStart);
	}
	Date getPRoblemEnteredDate(){return this.problemEnteredDate;}
		
	/*boolean to hold if done button was clicked*/
	boolean doneClicked=false;
	void setDoneClicked(boolean flag){doneClicked=flag;}
	boolean isDoneClicked(){return doneClicked;}
	
	/*variable to hold the time done button was clicked last time.*/
	Date doneClickedDate;
	void setDoneClickedDate(String doneDate) throws ParseException{
		doneClickedDate=stringToDate(doneDate);
	}
	Date getDoneClickedDate(){	return this.doneClickedDate; }
	
	
	/**
	 * Method to convert the a Date string (i.e. the even_time), to Date
	 * @param stringDate
	 * @return
	 * @throws ParseException
	 */
	private Date stringToDate(String stringDate) throws ParseException{
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");	
		return simpleDate.parse(stringDate);
		
	}
	

	
	/**
	 * Method that estimates time on task. If done button has been clicked, then time on task is computed from the time problem was entered and the time done was clicked. 
	 * If done was not clicked, then time is computed from the time problem was entered and event_time (which is the time either quiz button was clicked, or new problem was given or aplus was closed)
	 * @param 
	 * @return
	 * @throws ParseException 
	 */
	int getTimeOnTask(String event_time) throws ParseException{				 
		 return isDoneClicked() ? ((int) (getDoneClickedDate().getTime()/1000 - getPRoblemEnteredDate().getTime()/1000)) : ((int) (stringToDate(event_time).getTime()/1000 - getPRoblemEnteredDate().getTime()/1000));
	}
	
	
	
	/**
	 * Method that manages a log entry, if its about a problem started.
	 * 	- If action is problem started, log entry is buffered
	 *  - If action is either a) quiz taken b) new problem entered, c) problem done d) aplus closed
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
	 * @throws ParseException 
	 */
	public boolean manageLogEntry(String actionType, String action, String step, String result, Object resultDetails, Sai sai, ProblemNode node, String correctness, String expSelection,
	   		String expAction, String expInput, int duration, String feedback, String opponent, String info, int myRating, String event_time) throws ParseException{
			
			boolean returnValue=false;	//if false is returned, SimStLogger will continue logging. True indicates that SimStProblemStartLogAgent took action...
			/*If its problem started, hold the entry*/
			if (action.equals(SimStLogger.PROBLEM_DURATION /*SimStLogger.PROBLEM_ENTERED_ACTION*/)) { 
				//trace.out(" Duration in manageLogEntry : "+duration);
				setProblemEntered(true);
				setProblemEnteredDate(event_time);
				//trace.out(" Log Buffered  "+action+" Feedback "+feedback);
				logBuffer.add(new LogEntry(actionType, action, step, result, resultDetails, sai, node, correctness, expSelection, expAction, expInput, duration, feedback, opponent, info, myRating, event_time));
				returnValue=true;	
				
			}
			/*if problem is entered & done has been clicked, keep the time this happened*/
			else if (isProblemEntered() && action.equals(SimStLogger.PROBLEM_DONE_ACTION)){
				setDoneClicked(true);
				this.setDoneClickedDate(event_time);			
			}
			/*if problem is entered and quiz button is clicked or new problem is entered or PLE is closed then its time to log what was buffered*/
			else if (isProblemEntered() && (action.equals(SimStLogger.QUIZ_BUTTON_ACTION) || action.equals(SimStLogger.UNTAKEN_QUIZ_INITIATE_ACTION) || action.equals(SimStLogger.NEXT_PROBLEM_BUTTON_ACTION) || action.equals(SimStLogger.PROBLEM_ENTERED_BUTTON_ACTION) || action.equals(SimStLogger.PLE_CLOSED_ACTION))){				
				//trace.out(" Log size : "+logBuffer.size());
				logBuffer.pop().log(logger,getTimeOnTask(event_time));
				trace.out("****** logged time " + getTimeOnTask(event_time) );
				setProblemEntered(false);
				setDoneClicked(false);		
				returnValue=true; 
			}
			else{	
			/*This means current log entry is not problem started, nor is capable of "releasing" a buffered problem started, so let SimStLogger log it.*/
				returnValue=false;
			}	
			
			//trace.out(" The return Value is : "+returnValue);
			return returnValue;		
		}
	
	
}
