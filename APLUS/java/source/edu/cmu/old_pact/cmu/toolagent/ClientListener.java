package edu.cmu.old_pact.cmu.toolagent;

import java.io.DataInputStream;
import java.io.IOException;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;


class ClientListener extends Thread {

	EventTarget target;
	LispJavaConnection connectTo;
    private BR_Controller controller;
    
	public ClientListener(LispJavaConnection c, BR_Controller controller) {
		super("help");
        connectTo = c; 
		connectTo.registrate("ClientListener", this);
        this.controller = controller;
	}
     
    public void start() {
        super.start();
    }
    
	public void run() {

		DataInputStream in = null;
		String msg;

		// sanket@cs.wpi.edu
 		if (controller.getCtatModeModel().isJessMode())
			return;

		try {
			EventTarget tar = new EventTarget(connectTo, controller);
			SemanticEvent ev = new SemanticEvent("Server", "getMessage");
			in = ev.receive(tar);
			if (in != null)
				trace.out("in stream opened");

			while (true) {

				while ((msg = in.readLine()) != null) {
					//	             	trace.out("Mess received : "+msg);
					connectTo.messageReceived(msg);
				}
			}
		} catch (IOException e) {
			System.out.println("ClientListener run " + e.toString());
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("ClientListener run " + ex.toString());
		}
	}
}
