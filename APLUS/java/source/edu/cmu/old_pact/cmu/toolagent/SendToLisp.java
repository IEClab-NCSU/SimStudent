package edu.cmu.old_pact.cmu.toolagent;

import java.io.IOException;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;


class SendToLisp implements Runnable {

	String message;

	/**
	 * Connection through which to route all messages.
	 */
	private LispJavaConnection connection = null;

    private BR_Controller controller;

	public SendToLisp(LispJavaConnection ljc, String message, BR_Controller controller) {

		this.connection = ljc;
		this.message = message;
        this.controller = controller; 
	}

	public synchronized void run() {
		try {
			EventTarget target = new EventTarget(connection, controller);
			SemanticEvent ev = new SemanticEvent();
			ev.setMessage(message);
			ev.send(target);
		} catch (IOException e) {
			System.out.println("SendToLisp run " + e);
		}
	}
}
