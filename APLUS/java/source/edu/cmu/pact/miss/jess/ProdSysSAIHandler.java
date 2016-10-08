package edu.cmu.pact.miss.jess;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.RuleActivationNode;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;

/**
 *
 */
public class ProdSysSAIHandler extends SAIHandler {

	/**
	 *
	 */
	private static class ExecuteInstance {
		
		final String selection, action, input;
		final ModelTracer amt;
		final CTAT_Controller controller;
		
		public ExecuteInstance(String sel, String act, String inp, ModelTracer mt,
				CTAT_Controller controller) {
			selection = sel;
			action = act;
			input = inp;
			amt = mt;
			this.controller = controller;
		}
		
		/**
		 * 
		 */
		void execute(){
			//if (trace.getDebugCode("rr")) trace.out("rr","Enter in execute");
			ProdSysSAIHandler handler = null;
			handler = new ProdSysSAIHandler(selection, action, input, amt, controller);
			handler.processSAI();
			//if (trace.getDebugCode("rr")) trace.out("rr","Exit from execute");
		}
	}
	/**
	 * 
	 */
	static class Queue implements Runnable {

		private java.util.Queue<ExecuteInstance> queue = new LinkedList<ExecuteInstance>();
		
	    private static final int MAX_THREAD_COUNT = 1;
	    
		/**	Thread pool with MAX_THREAD_COUNT available threads at any point of time. */
		private static ExecutorService saiThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);

		private volatile boolean isActive = false;
		
		@Override
		public void run() {
			while(true){
				synchronized (this) {
					isActive = true;
					if(queue.peek() == null){
						isActive = false;
						//if (trace.getDebugCode("rr")) trace.out("rr","Terminating the thread " + Thread.currentThread());
						return;
					}
					ExecuteInstance ei = queue.remove();
					ei.execute();
				}
			}
		}
		
		int add(String sel, String act, String inp, ModelTracer amt, CTAT_Controller controller){
			return add(new ExecuteInstance(sel, act, inp, amt, controller));
		}
		
		int add(ExecuteInstance ei){
			synchronized (this) {
				//if (trace.getDebugCode("rr")) trace.out("rr","In add in ProdSysSAIHandler.Queue with ExecuteInstance");
				queue.add(ei);
				if(!isActive){
					//if (trace.getDebugCode("rr")) trace.out("rr","Creating a new Thread");
					Thread t = new Thread(this);
					t.start();
				}
				//if (trace.getDebugCode("rr")) trace.out("rr","Return from ProdSysSAIHandler.Queue with " + queue.size());
				return queue.size();
			}
		}
	}
	
	public ProdSysSAIHandler(String sel, String act, String inp, ModelTracer amt, CTAT_Controller controller){
		super(sel, act, inp, amt, controller);
		messages = new Vector();
	}
	
	public String processSAI(){
		try {
			if(ModelTracer.isSAIToBeModelTraced(selection, action)){

				result = amt.runModelTrace(false, false, selection,action,input,messages);
				
			} else if(action.equalsIgnoreCase("MetaTutorClicked") && ((selection.equalsIgnoreCase("hint")) || (selection.equalsIgnoreCase("activations")))){
				
				result = amt.runModelTrace(true, false, selection, "", "", messages);
				
				if(messages.size() < 1){
					messages.add("I'm sorry, no hint is available at this step");
				}
				
				String message = "";
				// TODO Construct the result of model-trace for sending the hint messages to the student tutor
				if(messages != null && messages.size() >= 1) {					
					for(int i=0; i< messages.size(); i++){
						if(returnMessage == null){
							returnMessage = new ArrayList<String>();
						}
						message += cleanMessage((String)messages.get(i))+";";
						returnMessage.add(cleanMessage((String)messages.get(i)));
					}
				}
								
				if(!selection.equalsIgnoreCase("activations"))
				{
					RuleActivationNode hint = amt.getMatchedNode();
					Sai sai = new Sai(hint.getActualSelection(), hint.getActualAction(), hint.getActualInput());
					String step = getController().getMissController().getSimSt().getProblemStepString();
					amt.getLogger().simStLog(SimStLogger.SIM_STUDENT_METATUTOR, SimStLogger.METATUTOR_HINT_ACTION, step, 
							hint.getName(), "",	sai, 0, message);
					
					sendResult();
				}

			} else if(action.equalsIgnoreCase("MetaTutorClicked") && (selection.equalsIgnoreCase("CLHint"))) {
				
				result = amt.runModelTrace(false, true, selection, "", "", messages);
				
				if(messages.size() < 1){
					messages.add("I'm sorry, no hint is available at this step");
				}
				
				String message = "";
				// TODO Construct the result of model-trace for sending the hint messages to the student tutor
				if(messages != null && messages.size() >= 1) {
					for(int i=0; i< messages.size(); i++){
						if(returnMessage == null){
							returnMessage = new ArrayList<String>();
						}
						message += cleanMessage((String)messages.get(i))+";";
						returnMessage.add(cleanMessage((String)messages.get(i)));
					}
				}
				
				if(!selection.equalsIgnoreCase("activations"))
				{
					RuleActivationNode hint = amt.getMatchedNode();
					Sai sai = new Sai(hint.getActualSelection(), hint.getActualAction(), hint.getActualInput());
					String step = getController().getMissController().getSimSt().getProblemStepString();
					amt.getLogger().simStLog(SimStLogger.SIM_STUDENT_METATUTOR, SimStLogger.METATUTOR_HINT_ACTION, step, 
							hint.getName(), "",	sai, 0, message);
					
					sendResult();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return "";
	}
	

	public static String cleanMessage(String message){
		String returnMessage=message.replace("[", "");
		returnMessage=returnMessage.replace("]", "");
		returnMessage=returnMessage.replace(";", "");	
		return returnMessage;
	}
	
	
	/**
	 * 
	 */
	public void sendResult(){
	
		CTAT_Controller ctlr = getController();
		if(ctlr == null) {
			trace.err("null CTAT_Controller in ProdSysSAIHandler.sendResult()");
			return;
		}
		
		if(returnMessage == null)
			trace.err("null returnMessage in ProdSysSAIHandler.sendResult()");
		else {
			ctlr.getMissController().getAPlusHintMessagesManager().setMessages(returnMessage);
			String message = ctlr.getMissController().getAPlusHintMessagesManager().getFirstMessage();
			if(ctlr.getMissController() != null && ctlr.getMissController().getSimStPLE() != null
					&& ctlr.getMissController().getSimStPLE().getSimStPeerTutoringPlatform() != null) {
				ctlr.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getAPlusHintDialogInterface().
					showMessage(message);
			}
		}
	}
	
	private Vector messages;
}
