package edu.cmu.pact.SocketProxy;

import java.util.LinkedList;

import pact.CommWidgets.UniversalToolProxy;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

/**
 * A queue of messages to be processed by the Behavior Recorder. Runs in its
 * own thread, so all operations on the list must be synchronized. Waits
 * whenever list is empty.
 * 
 * If you want to simulate network delays in delivery of messages to the the
 * Tutoring Service, see the commented-out code in {@link #run()}.
 */
public class ActionHandler extends LinkedList implements Runnable {

	private static final long serialVersionUID = 1L;

	/** SocketProxy we're serving. */
	private final BR_Controller controller;

	/** Outgoing msgs to the student interface. */
	private UniversalToolProxy forwardToClientProxy;
    
	public ActionHandler(BR_Controller controller) {
		super();
		this.controller = controller;
	}

	/**
	 * Process the queue of messages until QUIT_MSG is received.
	 */
	public void run() {
		MessageObject mo = null;
		
		while (!((mo = dequeue()).isQuitMsg())) {
			if (trace.getDebugCode("sp")) trace.out("sp", "\n" + Thread.currentThread().getName()
					+ " dequeued:\n" + mo + "\n New Queue Length: " + this.size() + "\n");

			// Uncomment these lines to simulate a delay in tutoring service message receipt
			
			//>
			/*
			long now = System.currentTimeMillis();
			
			if (trace.getDebugCode("delay")) trace.out("delay", "ZZZZZ sleeping 2000 ms");
			try 
			{ 
				Thread.sleep(2000); 
			}
			catch (InterruptedException ie) 
			{
				trace.err("ZZZZZ sleep interrupted after "+(System.currentTimeMillis()-now)+" ms: "+ie);
			}
			 
			if (trace.getDebugCode("delay"))
			{
				trace.out("delay", "ZZZZZ awake after 2000 ms");
			}
			*/
			//<
			
			// End of code to simulate a delay in tutoring service message receipt
			
			try {
				forwardToClient(mo);
                if (controller!=null) 
                {
                    controller.handleCommMessage(mo);
                    
                    if (controller.inTutoringServiceMode()) {
                    	if (mo.getTransactionInfo() != null)
                    		mo.getTransactionInfo().update(!(controller.getCtatModeModel().isRuleEngineTracing()));
                    }

                }
												
			} catch (Exception e) {
				System.err.println("Error processing external message");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Forward an InterfaceAction message to the client served by proxy
	 * {@link #forwardToClientProxy}. No-op if {@link #forwardToClientProxy} is null or message not of that type. 
	 * @param mo message to forward
	 */
	protected void forwardToClient(MessageObject mo) {
		return;
	}

	/**
	 * Remove and return the message at the head of the queue. Calls wait()
	 * until queue is nonempty.
	 * 
	 * @return message from head of queue
	 */
	public synchronized MessageObject dequeue() {
		while (isEmpty()) {
			try {
				wait();
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		return (MessageObject) removeFirst();
	}

	/**
	 * Enqueue a message for later processing. Calls notifyAll() after
	 * adding to wake up waiting dequeue().
	 * 
	 * @param msg
	 *            message to queue
	 * @return length of the queue after adding msg
	 */
	public synchronized int enqueue(MessageObject mo) {
		addLast(mo);
		int result = size();
		notifyAll();
		return result;
	}

	/**
	 * Set up this action handler for forwarding messages. 
	 * @param forwardToClientProxy proxy for client connection
	 */
	void setForwardToClientProxy(UniversalToolProxy stp) {
		this.forwardToClientProxy = stp;
	}
}
