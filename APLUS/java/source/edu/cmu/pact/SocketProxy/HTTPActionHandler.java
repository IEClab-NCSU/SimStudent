package edu.cmu.pact.SocketProxy;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.HTTPMessageObject;
import edu.cmu.pact.ctat.MessageObject;

/**
 * An ActionHandler that will enqueue only {@link HTTPMessageObject} instances.
 */
public class HTTPActionHandler extends ActionHandler {
	
	/**
	 * Call the superclass constructor.
	 * @param controller for {@link ActionHandler#ActionHandler(BR_Controller)}
	 */
	public HTTPActionHandler(BR_Controller controller) {
		super(controller);
	}

	/**
	 * Will enqueue ONLY objects of type {@link HTTPMessageObject}.
	 * @param mo message to enqueue; must of type HTTPMessageObject
	 * @return
	 * @see edu.cmu.pact.SocketProxy.ActionHandler#enqueue(edu.cmu.pact.ctat.MessageObject)
	 */
	public synchronized int enqueue(MessageObject mo) {
		if(mo instanceof HTTPMessageObject) {
			return super.enqueue(mo);
		}
		if(mo.isQuitMsg())
			return super.enqueue(mo);
		trace.err("HTTPActionHandler.enqueue() argument type not HTTPMessageObject:\n  "+mo.toXML());
		return size();
	}

	/**
	 * Cause the Behavior Recorder thread to exit.
	 */
	public void halt() {
		enqueue(MessageObject.makeQuitMessage());
	}
}
