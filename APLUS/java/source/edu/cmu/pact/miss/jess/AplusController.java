package edu.cmu.pact.miss.jess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.PeerLearning.AplusPlatform;
import edu.cmu.pact.miss.PeerLearning.AplusSpotlight;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPeerTutoringPlatform;
import edu.cmu.pact.miss.PeerLearning.SimStRememberBubble;
import edu.cmu.pact.miss.PeerLearning.GameShow.GameShowUtilities;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
import pact.CommWidgets.JCommTable.TableExpressionCell;
/**
 * Class responsible for controlling what goes to the model tracer for model tracing, 
 * and how the result is processed. In detail this class
 * a) fills a queue with MetaTutorModelTracingSAIHandler objects, each one of which calls the model tracer 
 * and triggers a ModelTracingEvent when model-tracing is done
 * b) Catches the ModelTracingEvent, and depending with what it contains, displays the popup or not. 
 * @author simstudent
 *
 */
public class AplusController implements ModelTracingListener{

	private CTAT_Controller controller;

	public CTAT_Controller getController() {
		return controller;
	}

	public void setController(CTAT_Controller controller) {
		this.controller = controller;
	}
	
	private ModelTracer aPlusModelTracing;

	public ModelTracer getaPlusModelTracing() {
		return aPlusModelTracing;
	}

	public void setaPlusModelTracing(ModelTracer aPlusModelTracing) {
		this.aPlusModelTracing = aPlusModelTracing;
	}

	private SimStRete ssRete;

	public SimStRete getSsRete() {
		return ssRete;
	}

	public void setSsRete(SimStRete ssRete) {
		this.ssRete = ssRete;
	}
	
	/**	Queue to keep the SAI events that arrive */
	private MetaTutorModelTracingSAIHandler.Queue prodSysSAIHandlerQ = new MetaTutorModelTracingSAIHandler.Queue();

	/*pro-active message has the form P[....] in production rule file*/
	public static final String PROACTIVE_MESSAGE_PREFIX="P[";
	/*follow up message has the form F[....] in production rule file*/
	public static final String FOLLOWUP_MESSAGE_PREFIX="F[";
	
	/*naming for the 3 types of messages we have*/
	public static int NORMAL_HINT_MESSAGE=1;
	public static int PROACTIVE_HINT_MESSAGE=2;
	public static int FOLLOWUP_HINT_MESSAGE=3;
	
	/*variable to hold the start state elements (we do not want to show proactive messages for these elements)*/
	ArrayList<String> startStateElement;
	void setStartStateElements(ArrayList<String> ssElements){this.startStateElement=ssElements;}
	ArrayList<String> getStartStateElements(){return this.startStateElement;}
	
	/*variable to keep track if a pro-active hint has just been given*/
	boolean proactiveHintGiven=false;
    public void setProactiveHintGiven(boolean flag){this.proactiveHintGiven=flag;}
    boolean getProactiveHintGiven(){return this.proactiveHintGiven;}
    
    /*hash to keep track how many times rule fired but student SAI did not match*/
	HashMap<String, Integer> firedButNotMatchedCount; 
	private void increaseFiredButNotMatchedCount(String rulename){
		if (!firedButNotMatchedCount.containsKey(rulename))
			firedButNotMatchedCount.put(rulename, new Integer(1));
		else{
			int val=firedButNotMatchedCount.get(rulename);
			firedButNotMatchedCount.put(rulename,++val);
		}
		increaseFiringCount(rulename);
	}
	public HashMap<String, Integer> getFiredButNotMatchedCount(){return this.firedButNotMatchedCount;}
	
	/*hash to store for every node, how many times a rule fired (either correctly or incorrectly)*/
	HashMap<String, Integer> totalFiringsCount; 
	private void increaseFiringCount(String rulename){
		if (!totalFiringsCount.containsKey(rulename))
			totalFiringsCount.put(rulename, new Integer(1));
		else{
			int val=totalFiringsCount.get(rulename);
			totalFiringsCount.put(rulename,++val);
		}
	}
	public HashMap<String, Integer> getFiringCount(){return this.totalFiringsCount;}
	
	/*hash to keep track rules that fired when student entered start state element (so
	 * they are marked as pending)*/
	public HashMap<String, Integer> pendingCount; 
	private void increasePendingCount(String rulename){
		if (!pendingCount.containsKey(rulename))
			pendingCount.put(rulename, new Integer(1));
		else{
			int val=pendingCount.get(rulename);
			pendingCount.put(rulename,++val);
		}
	}
	public HashMap<String, Integer> getPendingCount(){return this.pendingCount;}
	public void removePending(String rulename){pendingCount.remove(rulename);}
	
	
	
	/**
	 * Student interface action characterized as selection, action and input is put into a queue to 
	 * be model-traced
	 * @param selection
	 * @param action
	 * @param input
	 */
	public synchronized void handleInterfaceAction(String selection, String action, String input){	
	
		prodSysSAIHandlerQ.add(selection, action, input, aPlusModelTracing, controller);
	}
	
	
	public AplusController(CTAT_Controller controller) {
		
		this.controller = controller;
		ssRete = new SimStRete((BR_Controller)controller);
		//ssRete.setJmt(null);
		aPlusModelTracing = new ModelTracer(ssRete, controller);
		ssRete.setAmt(aPlusModelTracing);
		
		totalFiringsCount = new HashMap<String, Integer>();
		firedButNotMatchedCount = new HashMap<String, Integer>();
		pendingCount = new HashMap<String, Integer>(); 
		
		/*Keep the start state elements (no pro-active messages when selection is a start state element)*/
		if (controller.getMissController().isPLEon())
			 setStartStateElements(controller.getMissController().getSimStPLE().getStartStateElements());

	}

	/**
	 * Main method for controlling if the hint should be displayed or not
	 * @param mtEvent 
	 */
	@Override
	public void modelTracingController(ModelTracingEvent mtEvent) {

		
		
		if (mtEvent.modelTracingResult!=ModelTracer.NOMODEL /*&& !mtEvent.node.getName().contains("BUG")*/){
			
			increaseFiringCount(mtEvent.node.getName());
			//int typeOfHint=getProactiveHintGiven() ? FOLLOWUP_HINT_MESSAGE: NORMAL_HINT_MESSAGE;
			int typeOfHint=NORMAL_HINT_MESSAGE;
			if (mtEvent.action.equals("MetaTutorClicked")){
				displayMessage(mtEvent.selection,typeOfHint,mtEvent.message, mtEvent.node);
				if (!mtEvent.selection.equals("activations")) setProactiveHintGiven(false);
			}
		}
		else{ 
		/*	RuleActivationNode rk=null;
			if (mtEvent.node.getName().contains("BUG"))
				rk=this.getaPlusModelTracing().noMatchedMetaCogNode;
			else rk=mtEvent.node;
			
			System.out.println("FAILED FOR " + rk.getName());
			
			if (!isOkToGive(mtEvent.selection,mtEvent.action)) return;	
			boolean inc=increaseNotMatchedCountForRule(mtEvent.selection, rk.getName());
			if (showProactiveMessage(mtEvent.node.getName())){	
					displayMessage(mtEvent.selection,PROACTIVE_HINT_MESSAGE, rk.getHintMessages(), rk);	
				setProactiveHintGiven(true);
			}
			*/	
			
		
			
			/*In CogTutor mode do not show proactive messages*/
			if (controller.getMissController().getSimSt().isSsCogTutorMode() && !controller.getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
				return;
					
			
			
			if (mtEvent.node.getName()!=null  && mtEvent.selection!=null && (mtEvent.selection.equals("dorminTable1_C1R1") || mtEvent.selection.equals("dorminTable2_C1R1") ) && (mtEvent.node.getName().contains("simst-quiz-fail-enter-lhs-failed-quiz-problem"))){				
				return; 
			}
				
				
			
			
			if (!isOkToGive(mtEvent.selection,mtEvent.action)) return;	
			boolean inc=increaseNotMatchedCountForRule(mtEvent.selection,mtEvent.node.getName());
						if (mtEvent.node.getName()!=null && mtEvent.node.getName().startsWith("MAIN::BUG")){ 
									increaseFiredButNotMatchedCount(mtEvent.node.getName());
									inc=true;
						}
						if (mtEvent.node.getName()!=null && mtEvent.node.getName().equals("MAIN::simst-enter-correct-sai")){ 
							increaseFiredButNotMatchedCount(mtEvent.node.getName());
							inc=true;
						}			
			if (inc && showProactiveMessage(mtEvent.node.getName())){	
					displayMessage(mtEvent.selection,PROACTIVE_HINT_MESSAGE, mtEvent.message, mtEvent.node);	
					setProactiveHintGiven(true);
			}
		}

	}
	

	
	
	/**
	 * Method to increase the number of times rule fired but SAI did not match. 
	 * When selection is about start state elements, then this is marked as "pending"
	 * and increases count when all start elements have been entered.  
	 * @param selection
	 * @param rulename
	 */
	private boolean increaseNotMatchedCountForRule(String selection, String rulename){
		boolean returnValue=false;
		if (startStateElement.contains(selection)){
			increasePendingCount(rulename);
			//System.out.println("increaseing for " + rulename + " now its " + getPendingCount().get(rulename) + " out of " + 2*startStateElement.size() );
			if (getPendingCount().get(rulename)==2*startStateElement.size()){
				removePending(rulename);
				//System.out.println("OK for " + rulename);
				increaseFiredButNotMatchedCount(rulename);
				returnValue=true;
			}
				
		}
		else{	/*if its not about start state element, increase count*/
				increaseFiredButNotMatchedCount(rulename);
				returnValue=true;
		}
		return returnValue;
	}

	private boolean criticalNotMatch(String str)	{
		//if (str.contains("simst-hint-update-request"){
		//}		
				return false;
	}
	private boolean isOkToGive(String selection, String action){
		return ((action.indexOf("implicit") == -1) && !selection.equalsIgnoreCase("dorminTable1_C1R1") && !selection.equalsIgnoreCase("dorminTable2_C1R1") &&  !selection.equalsIgnoreCase("Yes")  &&  !selection.equalsIgnoreCase("No")  && !selection.equalsIgnoreCase("quiz"));
	}
	
	/*utility method to retrieve the type of the message*/
	public int getMessageType(String msg){
		int returnVal=-1;	
		if (msg.startsWith(FOLLOWUP_MESSAGE_PREFIX))
			returnVal=FOLLOWUP_HINT_MESSAGE;
		else if (msg.startsWith(PROACTIVE_MESSAGE_PREFIX))
			returnVal=PROACTIVE_HINT_MESSAGE;
		else returnVal=NORMAL_HINT_MESSAGE;
		
		return returnVal;
	}
	

	/*utility method to clean up string. */
	public static String cleanMessage(String message){
		String returnMessage;
		returnMessage=message.replace(PROACTIVE_MESSAGE_PREFIX , "");
		returnMessage=returnMessage.replace(FOLLOWUP_MESSAGE_PREFIX, "");
		returnMessage=returnMessage.replace("[", "");
		returnMessage=returnMessage.replace("]", "");
		returnMessage=returnMessage.replace(";", "");	
		return returnMessage;
	}
	
	
	
	
	/**
	 * Method that determines if the pro-active message must be shown
	 * 1st time do not show message - 2nd time show message - after that its based on accuracy.
	 * @param rulename
	 * @return
	 */
	private boolean showProactiveMessage(String rulename){
		boolean returnValue=false;

		int timesOccuredSoFar;
	
		if (getFiredButNotMatchedCount().get(rulename)!=null){
			timesOccuredSoFar=getFiredButNotMatchedCount().get(rulename);
			//System.out.println("timesOccuredSoFar for " + rulename + " is " + timesOccuredSoFar);
			if (timesOccuredSoFar<2)
				returnValue=false;
			else if (timesOccuredSoFar==2){
				returnValue=true;
			}
			else{
				if (Math.random() < predictRuleAccuracy(rulename))
					returnValue = true;
			}
		}


		return returnValue;
	}
	

	/**
	 * Method to estimate the threshold for determining whether to display a pro-active 
	 * message or not.Starts with 50% chance of showing message, and as student becomes better, 
	 * proactive messages become scarce.
	 * @param rulename
	 * @return
	 */
	float predictRuleAccuracy(String rulename){
		float returnValue=0.5f;
		
		int totalFirings=(getFiringCount().get(rulename)!=null) ? getFiringCount().get(rulename): 0;
		int incorrectFirings= (getFiredButNotMatchedCount().get(rulename)!=null) ? getFiredButNotMatchedCount().get(rulename): 0;
		
		float accuracy=(totalFirings - incorrectFirings)/totalFirings;
		
		if (accuracy >= 0.95)
			returnValue=0;	//i.e. do not show any proactive messages if accuracy above 95%
		else if (accuracy > 0.8 )
			returnValue=0.3f;
		else returnValue=0.4f;
		
		return returnValue;
	}


	/**
	 * Method that parses the message returned by the model tracer, 
	 * and constructs the actual message that will be displayed in the
	 * pop up window
	 * @param msg
	 * @return
	 */
	public void displayMessage(String selection, int type, Vector<String> messages, RuleActivationNode node){
		ArrayList<String> returnMessage=null;
		if(messages.size() == 0){
			messages.add("I'm sorry, no hint is available at this step");
		}
		

		String message = "";
		// TODO Construct the result of model-trace for sending the hint messages to the student tutor
		if(messages != null && messages.size() >= 1) {				
			for(int i=0; i< messages.size(); i++){
				if(returnMessage == null){
					returnMessage = new ArrayList<String>();
				}
				
				if (type==getMessageType((String)messages.get(i))){
					message += cleanMessage((String)messages.get(i))+";";
					returnMessage.add(cleanMessage((String)messages.get(i)));	
				}
					
					
			}
		}			
		
		if(!selection.equalsIgnoreCase("activations") && message.length()>1 && !message.equals(""))
		{
			Sai sai = new Sai(node.getActualSelection(), node.getActualAction(), node.getActualInput());
			String step = getController().getMissController().getSimSt().getProblemStepString();
			getaPlusModelTracing().getLogger().simStLog(SimStLogger.SIM_STUDENT_METATUTOR, SimStLogger.METATUTOR_HINT_ACTION, step, node.getName(), "",	sai, 0, message);
			
			boolean isCognitiveHint=node.getName().contains("demonstrate-step") ? true : false;
			sendResult(returnMessage,type,isCognitiveHint);
		}
		
	}
	
	/**
	 * Method that actually displays the message
	 * @param returnMessage
	 * @param type of message, can be either PROACTIVE_HINT_MESSAGE or NORMAL_HINT_MESSAGE or FOLLOWUP_HINT_MESSAGE
	 */
	public void sendResult(ArrayList<String>  messages, int type, boolean isCognitiveHint){
	
		CTAT_Controller ctlr = getController();
		if(ctlr == null) {
			trace.err("null CTAT_Controller in ProdSysSAIHandler.sendResult()");
			return;
		}
		
		
		/*added specifically to cut the bottom out hint for cognitive hints*/
		if (!getController().getMissController().getSimSt().getShowBottomOutHint() && isCognitiveHint)
				messages.remove(messages.size()-1);
		
		
		
		if(messages == null)
			trace.err("null returnMessage in ProdSysSAIHandler.sendResult()");
		else {
			ctlr.getMissController().getAPlusHintMessagesManager().setMessages(messages);
			String message = ctlr.getMissController().getAPlusHintMessagesManager().getFirstMessage();
			if(ctlr.getMissController() != null && ctlr.getMissController().getSimStPLE() != null
					&& ctlr.getMissController().getSimStPLE().getSimStPeerTutoringPlatform() != null) {		
				
				if (type==this.PROACTIVE_HINT_MESSAGE && message!=null){
					ctlr.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getAPlusHintDialogInterface().showThinkBubble();
				}
				ctlr.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getAPlusHintDialogInterface().showMessage(message);
			}
		}
	}
	
}
