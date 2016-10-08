package edu.cmu.pact.miss.PeerLearning;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Queue;



import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStLoggingAgent.LogEntry;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.ModelTraceWorkingMemory;
/**
* Parent class that all  SimStLogAgents, i.e. agents that are able to handle a specific action or set of actions, and log them when necessary.
* @author nbarba
*
*/
public class SimStLogAgent {

	protected static BR_Controller brController;
	protected ModelTraceWorkingMemory mtwm;
	protected ModelTracer apmt;	
	SimStLogger logger;
	
	/*queue to store the logs*/
	protected  LinkedList<LogEntry> logBuffer;

	
	/**
	 * constructor, that also initializes internal structures;
	 * @param brController
	 */
	public SimStLogAgent(BR_Controller brController){
		logBuffer = new LinkedList<LogEntry>();
		logger = new SimStLogger(brController);

		this.brController=brController;

		if(brController.getMissController().getSimSt().getModelTraceWM() != null)
		{	
			mtwm = brController.getMissController().getSimSt().getModelTraceWM();

		}

	}

	/**
	 * Method to return the current date/time
	 * @return
	 */
	public String getCurrentTime(){

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss.SSS");
		return dateFormat.format(new GregorianCalendar().getTime());

	}	

		
	
		
	/**
	 * Internal class to hold a log entry.
	 * @author nbarba
	 *
	 */
	public class LogEntry {
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
		
		/**
		 * Method to log     	
		 * @param logger
		 * @param hintType
		 * @param hintFollowed
		 */
		 public void log(SimStLogger logger,String hintType, String hintFollowed ){	
			 logger.simStLog( actionType,  action,  step,  result,  resultDetails,  sai,  node,  correctness,  expSelection,
					 expAction,  expInput,  duration,  feedback,  opponent,  info,  myRating, false, hintType,hintFollowed,event_time); 
		 }

		/**
		 * Method to log a transaction that changes the duration.
		 * @param logger
		 * @param actualDuration
		 */
		public void log(SimStLogger logger,int actualDuration ){	
				 logger.simStLog( actionType,  action,  step,  result,  resultDetails,  sai,  node,  correctness,  expSelection,
						 expAction,  expInput,  actualDuration,  feedback,  opponent,  info,  myRating, false, "","",event_time); 
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
		
		
}