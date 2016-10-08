/**
 *
 */
package edu.cmu.pact.ctat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cl.utilities.Logging.Logger;
import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Log.DataShopMessageObject;
import edu.cmu.pact.SocketProxy.SocketToolProxy;
import edu.cmu.pact.Utilities.OLIMessageObject;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pslc.logging.OliDiskLogger;

/**
 * This class can accept a list of messages and play them through the
 * Behavior Recorder.
 */
public class MessagePlayer implements Runnable {

	/** The messages to play. */
	private final List<MessageObject> messageObjects;

	/** Tutor engine for playing messages. */
	private final BR_Controller controller;

	/** Outgoing msgs to the student interface. */
	private UniversalToolProxy forwardToClientProxy;

	/** Whether to send MessageObject instances to the UTP. */
	private boolean clientAcceptsComm = true;

	/** Flag to indicate {@link #run()} should stop. */
	private volatile boolean stopping = false;

	/** The listeners to MessagePlayer events. */
    private HashSet listeners;

	private OliDiskLogger logger = null;

	/**
	 * Constructor accepts BR_Controller and list of messages to play.
	 * @param controller BR_Controller to play these messages
	 * @param msgs List of {@link MessageObject}s; makes a shallow
	 *        copy of this list
	 * @param clientAcceptsComm true means send CommMessages to the client;
	 *        false means send {@link OLIMessageObject#getOriginalElementString()}
	 */
	public MessagePlayer(BR_Controller controller, List<MessageObject> messageObjects,
			boolean clientAcceptsComm) {
		this.controller = controller;
		this.messageObjects = new ArrayList(messageObjects);
		this.clientAcceptsComm = clientAcceptsComm;
        this.listeners = new HashSet();
	}

	/**
	 * Play the List {@link #messageObjects}.
	 */
	public void run() {
		if (trace.getDebugCode("mp")) trace.out("mp", "run Message Player; messageObjects = " + messageObjects);
		int totalToSend = messageObjects.size();
		for (int i = 0; i < totalToSend && !isStopping(); ++i) {
			DataShopMessageObject mo = (DataShopMessageObject) messageObjects.get(i);
			forwardToClient(mo);
			if (logger != null) logger.log(mo.getLogMsg().getMsg(), mo.getTimeStamp());
			controller.handleCommMessage(mo);

			MessagePlayerEvent evt = new MessagePlayerEvent(this, mo);
			evt.setTotalCount(totalToSend);
			evt.setSentCount(i+1);
			evt.setStopping(isStopping());
			fireMessagePlayerEvent(evt);
		}
	}

	/**
	 * Forward an InterfaceAction message to the client served by proxy
	 * {@link #forwardToClientProxy}. No-op if {@link #forwardToClientProxy} is
	 * null or message is not of that type.
	 * @param msg type object to allow subclasses to send object of any type
	 */
	protected void forwardToClient(Object msg) {
		if (forwardToClientProxy == null)
			return;
		MessageObject mo = (MessageObject) msg;
		String msgType = mo.getMessageType();
		if (!"InterfaceAction".equalsIgnoreCase(msgType))
			return;
		if (clientAcceptsComm)
			forwardToClientProxy.handleMessage(mo);
		else if (forwardToClientProxy instanceof SocketToolProxy && mo.getDataShopElementString() != null)
			((SocketToolProxy)forwardToClientProxy).sendXMLString(mo.getDataShopElementString());
	}

	/**
	 * @return the {@link #forwardToClientProxy}
	 */
	public UniversalToolProxy getForwardToClientProxy() {
		return forwardToClientProxy;
	}

	/**
	 * @param forwardToClientProxy new value for {@link #forwardToClientProxy}
	 */
	public void setForwardToClientProxy(UniversalToolProxy forwardToClientProxy) {
		this.forwardToClientProxy = forwardToClientProxy;
	}

	/**
	 * @return the {@link #stopping}
	 */
	public boolean isStopping() {
		return stopping;
	}

	/**
	 * @param stopping new value for {@link #stopping}
	 */
	public void setStopping(boolean stopping) {
		this.stopping = stopping;
	}

	/** Adds the listener passed as argument to the list of MessagePlayer listeners.
	 * @param l the listener to be added to the list of Messsage Player listeners */
    public void addMessagePlayerListener(MessagePlayerListener l) {
        listeners.add(l);
    }

	/** Removes the listener passed as argument to the list of MessagePlayer listeners.
	 * @param l the listener to be removed from the list of MessagePlayer listeners */
    public void removeMessagePlayerListener (MessagePlayerListener l) {
        listeners.remove(l);
    }

	/** Fires a MessagePlayer event.
	 *@param e the fired event */
    public void fireMessagePlayerEvent (MessagePlayerEvent e) {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            MessagePlayerListener listener = (MessagePlayerListener) i.next();
            listener.messagePlayerEventOccurred(e);
        }
    }

	public void setLogger(OliDiskLogger oli) {
		logger = oli;
	}
}
