/*
 * $Id: MessageEventListener.java 3736 2005-03-08 21:58:14Z mpschnei $
 */
package edu.cmu.pact.Utilities;

import java.util.EventListener;

/**
 * An interface for listeners to message events.
 */
public interface MessageEventListener extends EventListener {

	/**
	 * Method called when source notifies listener of event.
	 *
	 * @param  me {@link MessageEvent} containing the message sent
	 *             or received
	 */
	public void messageEventOccurred(MessageEvent me);
}
