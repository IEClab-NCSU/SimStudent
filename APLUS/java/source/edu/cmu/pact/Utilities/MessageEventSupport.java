/*
 * $Id: MessageEventSupport.java 3736 2005-03-08 21:58:14Z mpschnei $
 */
package edu.cmu.pact.Utilities;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A class that others can use to support generation of {@link MessageEvent}
 * events. The using class can include an instance of this class as a
 * member field and delegate to it the event generation-related tasks.
 *
 * @author sewall
 */
public class MessageEventSupport implements Serializable {

	/**
	 * The current set of listeners. 
	 */
	private List listeners = new LinkedList();

	/**
	 * Add an event listener. Ensures that listener is not added more than
	 * once.
	 *
	 * @param  listener to add
	 */
	public void addMessageEventListener(MessageEventListener listener) {
		listeners.remove(listener);
		listeners.add(listener);
	}

	/**
	 * Remove an event listener.
	 *
	 * @param  listener to remove
	 */
	public void removeMessageEventListener(MessageEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notify all listeners of a {@link MessageEvent}. Uses ListIterator
	 * to avoid trouble if a listener calls {@link #removeMessageEventListener}
	 * from its {@link MessageEventListener#messageEventOccurred} method.
	 *
	 * @param  messageEvent event that occurred
	 */
	public void fireMessageEvent(MessageEvent messageEvent) {
		for (ListIterator it = listeners.listIterator(); it.hasNext(); )
			((MessageEventListener) it.next()).messageEventOccurred(messageEvent);
	}

	/**
	 * Get the current count of listeners.
	 *
	 * @return size() of {@link #listeners}
	 */
	public int getListenerCount() {
		return listeners.size();
	}
}
