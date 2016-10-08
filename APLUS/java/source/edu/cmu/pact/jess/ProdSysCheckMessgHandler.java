/**
 * 	Created on Nov 11, 2003
 * 	Start this in a new thread. This thread will be blocking but the main thread ie MT 
 * 	will be non blocking.
 *	
 *	Do not change the working memory state when buggy actions are traced or when no model 
 *	is found.
 *	The working memory should be updated only in case the correct action is traced because
 *	in tutor-mode, it is assumed that you are in the correct state when the student performs
 *	an action. ie. If the student is in state 1 and enters some number (state 2) then it is 
 *	assumed 
 *	that the working memory is in the proper state. After this if the student performs 
 *	another action then it is assumed that the student is in state 2	
 */
package edu.cmu.pact.jess;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.jdom.Element;

import jess.Fact;
import jess.Rete;

import edu.cmu.pact.BehaviorRecorder.Controller.PseudoTutorMessageBuilder;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * This class is instantiated only if the previous arcs are consistent and the working memory
 * is assumed to be in a proper state before instantiating this class.
 * @author sanket
 *
 */
public class ProdSysCheckMessgHandler extends MessageHandler {

	/**
	 * Container for the arguments needed to construct and run an instance of the enclosing class.
	 */
	private static class ExecuteInstance {
		final MessageObject msg;
		final JessModelTracing jmt;
		final CTAT_Controller controller;
		final WMEEditor wmeEditor;
		
		ExecuteInstance(MessageObject msg, JessModelTracing jmt,
				CTAT_Controller controller, WMEEditor wmeEditor) {
			this.msg = msg;
			this.jmt = jmt;
			this.controller = controller;
			this.wmeEditor = wmeEditor;
		}
		
		/**
		 * Create an instance of the enclosing class and run it on our {@link #msg}.
		 */
        void execute() {
            ProdSysCheckMessgHandler handler = new ProdSysCheckMessgHandler(msg, jmt, controller);
            if(wmeEditor != null)
            	handler.addMessageEventListener(wmeEditor);
            handler.processMessage();
        }	
	}
	
	/**
	 * Create a {@link ProdSysCheckMessgHandler.ExecuteInstance} object for the
	 * given message and execute it immediately. This mimics, in the calling thread,
	 * the action of dequeuing an ExecuteInstance from {@link ProdSysCheckMessgHandler.Queue}
	 * and running it.
	 * @param msg message to process
	 * @param jmt tracer
	 * @param controller linkage back to communications runtime
	 * @param wmeEditor display to update at author time
	 */
	static void executeSynchronously(MessageObject msg, JessModelTracing jmt,
			CTAT_Controller controller, WMEEditor wmeEditor) {
		ExecuteInstance ei = new ExecuteInstance(msg, jmt, controller, wmeEditor);
		ei.execute();
	}
	
	/**
	 * A class to run the enclosing class from a separate thread that consumes a queue. This
	 * to ensure that student actions are presented to the rule engine one-at-a-time in their
	 * original sequence.
	 */
	static class Queue implements Runnable {
		
		/** Number of milliseconds a thread in this class will wait with no steps to process. */
		private static final long MAX_IDLE_TIME = 120000;

		/** The internal queue of instances to run. */
		private java.util.Queue<ExecuteInstance> theQueue = new LinkedList<ExecuteInstance>();
		
		/** Whether an instance of this class is active in a thread. */
		private volatile boolean isActive = false;
		
		/** Process the queue until it is empty. */
		public void run() {
			while (true) {
				ExecuteInstance ei = null;
				synchronized(this) {
					isActive = true;
					long now = System.currentTimeMillis(), then = now+MAX_IDLE_TIME;
					while(theQueue.peek() == null) {
						try {
							wait(then-now);             // await notify() from add()
						} catch(Exception e) {
							now = System.currentTimeMillis();
							trace.err("ProdSysCheckMessgHandler.Queue.run() exception after waiting "+
									(now-(then-MAX_IDLE_TIME))+" ms on queue: "+e+"; cause: "+e.getCause());
						}
						if (theQueue.peek() != null)    // queue has work for us: stop waiting & go do it
							break;                
						if(then <= (now = System.currentTimeMillis())) {
							isActive = false;
							return;                     // exit thread: idle too long
						}
					}
					ei = theQueue.remove();
				}                                           // end mutex on queue
				ei.execute();
				if (trace.getDebugCode("mt")) trace.outNT("mt", "ProdSysCheckMessgHandler.Queue.run() length "+theQueue.size());
			}
		}

		/**
		 * Convenience(?) method to avoid caller having to know about
		 * {@link ProdSysCheckMessagHandler.ExecuteInstance}
		 * @param msg
		 * @param jmt
		 * @param controller
		 * @param wmeEditor
		 * @return length of {@link #theQueue}
		 */
		int add(MessageObject msg, JessModelTracing jmt,
				CTAT_Controller controller, WMEEditor wmeEditor) {
			return add(new ExecuteInstance(msg, jmt, controller, wmeEditor));
		}

		/**
		 * Enqueue an instance and start the thread if necessary.
		 * @param ei the instance
		 * @return length of {@link #theQueue}
		 */
		synchronized int add(ExecuteInstance ei) {
			theQueue.add(ei);
			if (trace.getDebugCode("mt"))
				trace.outNT("mt", "ProdSysCheckMessgHandler.Queue.add() length "+theQueue.size());
			if (!isActive) {
				Thread t = new Thread(this);
				t.start();
			}
			notify();
			return theQueue.size();
		}
	}
	
	private Vector ruleNames;
	private List<String> skillNames;
	private Integer actionLabelTagId;
	private Vector messages;
	private Vector wmImages;
	private String transactionId = null;
	private String tutorSelection = null;
	private String tutorAction = null;
	private String tutorInput = null;

	/**
	 * @param o
	 * @param jmt
	 * @param controller controller for state data
	 */
	public ProdSysCheckMessgHandler(MessageObject o, JessModelTracing jmt,
			CTAT_Controller controller) {
		super(o, jmt, controller);
		init();
	}

	public void init(){
		messages = new Vector();

		selectionList = recdMessage.getSelection();
		inputList = recdMessage.getInput();
		actionList = recdMessage.getAction();

		// get the names of the rules to be checked.
		ruleNames = (Vector) recdMessage.getProperty("RuleNames");
		actionLabelTagId = recdMessage.getPropertyAsInteger("ActionLabelTagID");
		transactionId = recdMessage.getTransactionId();
	}

	/**
	 * Execute a student attempt or hint request.
	 * @see jess.MessageHandler#processMessage()
	 */
	public String processMessage() {
		Enumeration selectionEnum;
		boolean notApplicable = false;
		
		messages.removeAllElements();

		String selItem;
		selectionEnum = selectionList.elements();
		
//		while (selectionEnum.hasMoreElements()) {
			final String sel  = (String)selectionEnum.nextElement();
			
			int selectionIndex = selectionList.indexOf(sel); // the index of the selection element in the selection vector
			// check if the action for this selection is update....
//			final String act = "";
//			if(selectionIndex < actionList.size()){		
			final String act = (String)actionList.get(selectionIndex); // get the action for the selection element
//			}
//			final String inp = "";
//			if(selectionIndex < inputList.size()){
			final String inp = (String)inputList.get(selectionIndex);
			try {
				if ( JessModelTracing.isSAIToBeModelTraced(sel, act) ) {
					
					checkResult = jmt.runModelTrace(true, false, sel, act, inp,
							messages);
					
					setTutorSAI(jmt);
                                        
					if (jmt.getWMImages() != null) {
						wmImages = new Vector();
						wmImages.addAll(jmt.getWMImages());
					}
					
					if (ruleNames != null)
						ruleNames.clear();
					else
						ruleNames = new Vector();
					ruleNames.addAll(jmt.getRuleSeq());
					
					if (skillNames != null)
						skillNames.clear();
					else
						skillNames = new ArrayList<String>();
					skillNames.addAll(jmt.getSkillSeq());
					
					if(!(JessModelTracing.SUCCESS.equals(checkResult))){
						if (getController() != null &&
								getController().updateModelOnTraceFailure()) {
							jmt.getRete().setSAIDirectly(sel, act, inp);
						}
					}

					// construct result of lisp check to send to execution space editor
					if(messages.size() > 0){
						if(!(JessModelTracing.SUCCESS.equals(checkResult)))
							returnMessage = createLispCheckResult("BuggyMsg", messages);
						else
							returnMessage = createLispCheckResult("SuccessMsg", messages);
					}else{
						returnMessage = createLispCheckResult(null, null);
					}

					// end of constructing the result
					sendMessage();
				}else if (JessModelTracing.isHintRequest(sel, act)) {

					
					
					String selectionString = "";
					String actionString = "";
					
//					if(actionList.size() < 2){
//						System.out.println("***ERROR *** There is no Previous focus. Help not available. ***");
//						return null;
//					}
					if(actionList.size() > 1) {
						String actionString2 = actionList.get(1).toString();
						if(actionString2.equalsIgnoreCase("PreviousFocus")){
							if(selectionList.size() > 1){
								selectionString = selectionList.get(1).toString();
								if (actionList.size() > 2 && actionList.get(2) != null)
									actionString = actionList.get(2).toString();
							}
						}
					}
					if (trace.getDebugCode("mt")) trace.out("mt", "to call HINT modelTrace( sel " +	sel +
							", act " + act + ") with selectionString " + selectionString);					
					messages.removeAllElements();
					
					
					
					checkResult = jmt.runModelTrace(true, true,
							selectionString, actionString, "", messages);

					
					setTutorSAI(jmt);
					
					if (ruleNames != null)
						ruleNames.clear();
					else
						ruleNames = new Vector();
					ruleNames.addAll(jmt.getRuleSeq());
					
					if (skillNames != null)
						skillNames.clear();
					else
						skillNames = new ArrayList<String>();
					skillNames.addAll(jmt.getSkillSeq());

					if(messages.size() < 1){
						messages.add("I'm sorry, no hint is available at this step");
					}
					// constructing the result of lisp check for sending to execution space editor.
					returnMessage = createLispCheckResult("HintMessages", messages);
					
					
					// end of constructing the result
					sendMessage();
				} else {
					throw new IllegalArgumentException("Neither an update nor a hint request: act="+
							act+", sel="+sel);
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
//		}
		return null;
	}

	/**
	 * Copy the tutor selection, action and input from the {@link JessModelTracing}.
	 * @param jmt
	 */
	private void setTutorSAI(JessModelTracing jmt) {
		tutorSelection = jmt.getTutorSelection();
		tutorAction = jmt.getTutorAction();
		tutorInput = jmt.getTutorInput();		
	}

	/**
	 * Create a LispCheckResult message with the output of the model trace.
	 * @param msgListName if not null, name for message
	 * @param msgList list of messages to return: FIXME only returns first
	 */
	private MessageObject createLispCheckResult(String msgListName, List msgList) {
		MessageObject mo = MessageObject.create(MsgType.LISP_CHECK_RESULT, "SetProperty");
		
        if (trace.getDebugCode("mt")) trace.out("mt", "createLispCheckResultResult "+checkResult+" "+
        		actionLabelTagId+" "+selectionList+" "+actionList+" "+inputList+
				" "+msgListName+"="+msgList);

		if (msgListName != null && msgList!= null && msgList.size() > 0) {
			if (msgListName.toLowerCase().startsWith("hint"))      // whole vector for hints
				mo.setProperty(msgListName, msgList);
			else                           // just the first string for success or buggy msg  
				mo.setProperty(msgListName, msgList.get(0));
		}

		mo.setProperty("Result", checkResult);
		mo.setSelection(tutorSelection);
		if (trace.getDebugCode("mo"))
			trace.out("mo", "PSCMH.createLispCheckResult() getSelection "+mo.getSelection()+
					", class "+(mo.getSelection() == null ? null : mo.getSelection().getClass()));
		mo.setAction(tutorAction);
		mo.setInput(tutorInput);
		mo.setProperty(PseudoTutorMessageBuilder.STUDENT_SELECTION, selectionList);
		mo.setProperty(PseudoTutorMessageBuilder.STUDENT_ACTION, actionList);
		mo.setProperty(PseudoTutorMessageBuilder.STUDENT_INPUT, inputList);
		mo.setProperty("ActionLabelTagID", actionLabelTagId);
		if (skillNames != null && skillNames.size() > 0)
			mo.setProperty("Skills", skillNames);
		if (ruleNames != null && ruleNames.size() > 0)
			mo.setProperty("ProductionList", ruleNames);
		if (wmImages != null && wmImages.size() > 0)
			mo.setProperty("WMImages", wmImages);
		if (transactionId != null && transactionId.length() > 0)
			mo.setTransactionId(transactionId);
		List<Element> customFields = getCustomFields(jmt.getRete());
		if (customFields != null)
			mo.setProperty("custom_fields", customFields);
		return mo;
	}

	/**
	 * Get any custom fields from the {@value GetCustomFieldsFact#CUSTOM_FIELDS} fact. 
	 * @param rete
	 * @return 
	 */
	private List<Element> getCustomFields(Rete rete) {
		Fact f = GetCustomFieldsFact.get(rete);
		List<Element> result = GetCustomFieldsFact.toXML(f, rete.getGlobalContext());
		if(trace.getDebugCode("mt"))
			trace.out("mt", "ProdSysMsgHandler.getCustomFields() fact, xml:\n  "+f+"\n  "+result);
		return result;
	}

	/* (non-Javadoc)
	 * @see jess.MessageHandler#sendMessage()
	 */
	public void sendMessage() {
		CTAT_Controller ctlr = getController();
		if(ctlr == null){
			if (!Utils.isRuntime())
				JOptionPane.showMessageDialog(ctlr.getActiveWindow(), "Error in the communication " +
						" between the Interface and the Production system. \nTry again after " +
						"restarting the tools.");
			trace.err("null CTAT_Controller in ProdSysCheckMessgHandler.sendMessage()");
			return;
		}
		if (returnMessage == null)
			trace.err("null returnMessage in ProdSysCheckMessgHandler.sendMessage()");
		else {
			fireMessageEvent(returnMessage);   // notify event listeners
			ctlr.handleCommMessage(returnMessage);
		}
	}
}
