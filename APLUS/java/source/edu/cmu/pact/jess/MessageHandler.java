/*
 * Created on Nov 11, 2003
 *
 */
package edu.cmu.pact.jess;

import java.util.Vector;

import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.Utilities.MessageEventListener;
import edu.cmu.pact.Utilities.MessageEventSupport;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.ctat.MessageObject;

/**
 * @author sanket
 *
 */
public abstract class MessageHandler {

	MessageObject recdMessage, returnMessage;
	Vector propertyNames, propertyValues;
	Vector selectionList, actionList, inputList;
	JessModelTracing jmt;

	String checkResult = JessModelTracing.NOTAPPLICABLE;
	private CTAT_Controller controller;

	/**
	 * Delegate for generating {@link edu.cmu.pact.Utilities.MessageEvent} events.
	 */
	private MessageEventSupport msgEvtSupport = new MessageEventSupport();
	
	/**
	 * Create a handler for the given message.
	 * @param o message to process
	 * @param jmt model tracer to process it
	 * @param c connection for sending the result
	 * @param controller controller for state data
	 */
	public MessageHandler(MessageObject o, JessModelTracing jmt, CTAT_Controller controller) {

		this.recdMessage = o;
		this.jmt = jmt;
		this.controller = controller;
		this.jmt.setSkipTree(Utils.isRuntime());
	}
	
	/**
	 * Processes a comm message ie makes a call to ModelTracing algorithm
	 */
	public abstract String processMessage();
	/**
	 * Sends back the result of processing to the Interface
	 *
	 */
	public abstract void sendMessage();

	/**
	 * Send {@link edu.cmu.pact.Utilities.MessageEvent} objects to all listeners:
	 * call this when sending a message.
	 *
	 * @param  o MessageObject to send
	 */
	protected void fireMessageEvent(MessageObject o) {
		if (msgEvtSupport.getListenerCount() <= 0)
			return;
		String msgType = (String) o.getMessageType();
		String result = (String) o.getProperty("Result");
		msgEvtSupport.fireMessageEvent(new MessageEvent(this, true, msgType,
														result, o));
	}

	/**
	 * Add a listener for {@link edu.cmu.pact.Utilities.MessageEvent} objects
	 * emitted when this class receives or sends a MessageObject.
	 *
	 * @param  listener listener instance to add
	 */
	public void addMessageEventListener(MessageEventListener listener) {
		msgEvtSupport.addMessageEventListener(listener);
	}

	/**
	 * Remove a listener for {@link edu.cmu.pact.Utilities.MessageEvent} objects
	 * emitted when this class receives or sends a MessageObject.
	 *
	 * @param  listener listener instance to remove
	 */
	public void removeMessageEventListener(MessageEventListener listener) {
		msgEvtSupport.removeMessageEventListener(listener);
	}
	
	/**
	 * @return Returns the controller.
	 */
	public CTAT_Controller getController() {
		return controller;
	}
}
