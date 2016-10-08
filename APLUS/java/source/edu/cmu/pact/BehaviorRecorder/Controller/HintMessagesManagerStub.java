/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.Controller;

import java.util.Vector;

import pact.CommWidgets.StudentInterfaceWrapper;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.Utilities.MessageEvent;
import edu.cmu.pact.client.HintMessagesManager;
import edu.cmu.pact.ctat.MessageObject;

/**
 * Stub for Android, where the student interface runs outside the Java VM.
 */
public class HintMessagesManagerStub implements HintMessagesManager {

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param o
	 * @see edu.cmu.pact.client.HintMessagesManager#setMessageObject(edu.cmu.pact.ctat.MessageObject)
	 */
	public void setMessageObject(MessageObject o) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#hasPreviousMessage()
	 */
	public boolean hasPreviousMessage() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#hasNextMessage()
	 */
	public boolean hasNextMessage() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getPreviousMessage()
	 */
	public String getPreviousMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getFirstMessage()
	 */
	public String getFirstMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getNextMessage()
	 */
	public String getNextMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param next
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getNextHintRequest(boolean)
	 */
	public MessageObject getNextHintRequest(boolean next) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param next
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getNextHintResponse(boolean)
	 */
	public MessageObject getNextHintResponse(boolean next) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getMessageType()
	 */
	public String getMessageType() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#resetHighlightWidgets()
	 */
	public void resetHighlightWidgets() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#cleanUpHintOnChange()
	 */
	public void cleanUpHintOnChange() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#removeWidgetsHighlight()
	 */
	public void removeWidgetsHighlight() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#setWidgetFocus()
	 */
	public void setWidgetFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getHighlightedWidgetsVector()
	 */
	public Vector getHighlightedWidgetsVector() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#dialogCloseCleanup()
	 */
	public void dialogCloseCleanup() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param messageVector
	 * @see edu.cmu.pact.client.HintMessagesManager#setMessages(java.util.Vector)
	 */
	public void setMessages(Vector messageVector) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param mType
	 * @see edu.cmu.pact.client.HintMessagesManager#setMessageType(java.lang.String)
	 */
	public void setMessageType(String mType) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param from
	 * @param fieldName
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#fieldPosition(java.util.Vector, java.lang.String)
	 */
	public int fieldPosition(Vector from, String fieldName) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @param msgEvt
	 * @see edu.cmu.pact.client.HintMessagesManager#messageEventOccurred(edu.cmu.pact.Utilities.MessageEvent)
	 */
	public void messageEventOccurred(MessageEvent msgEvt) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return
	 * @see edu.cmu.pact.client.HintMessagesManager#getHintInterface()
	 */
	public HintWindowInterface getHintInterface() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param studentInterfaceWrapper
	 * @see edu.cmu.pact.client.HintMessagesManager#setStudentInterfaceWrapper(pact.CommWidgets.StudentInterfaceWrapper)
	 */
	public void setStudentInterfaceWrapper(
			StudentInterfaceWrapper studentInterfaceWrapper) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param hintInterface
	 * @see edu.cmu.pact.client.HintMessagesManager#setHintInterface(edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface)
	 */
	public void setHintInterface(HintWindowInterface hintInterface) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#requestHint()
	 */
	public void requestHint() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @see edu.cmu.pact.client.HintMessagesManager#requestDone()
	 */
	public void requestDone() {
		// TODO Auto-generated method stub

	}

}
