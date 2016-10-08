/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.ctat;

import pact.CommWidgets.UniversalToolProxy;


/**
 * Classes that want to receive Comm Messages can implement this
 * interface and register themselves with an instance of the sender.
 * At this writing (May 2007), a sender is the {@link UniversalToolProxy}.
 */
public interface MessageListener {
	
	/**
	 * Called when the message is about to be sent.
	 * @param messageObject
	 */
	public void messageSent(MessageObject messageObject);
}
