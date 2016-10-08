package edu.cmu.old_pact.dormin;
import java.net.Socket;
import java.util.Vector;

//A ServerReceiver is a simple class to use in Applets that want to communicate with
//their server.
//This is usually paired with ServerTarget to get two-way communication between the
//Applet and server

public class ServerReceiver extends NetworkReceiver {
	private Vector verbHandlers;
	
	public ServerReceiver(Socket serverSocket) {
		super(null,serverSocket);
		verbHandlers = new Vector(10);
	}
	
	public void addVerbHandler(ExternalObject handler) {
		verbHandlers.addElement(handler);
	}
	
	//override the receive method so that we can check multiple verbHandlers
	public void receive(MessageObject inEvent) {
		ExternalObject tryObject;
		boolean handled=false;
		
		for (int i=0;i<verbHandlers.size() && !handled;++i) {
			tryObject = (ExternalObject)(verbHandlers.elementAt(i));
			tryObject.handleEvent(inEvent);
			if (inEvent.getParseError() == null)
				handled = true;
		}
	}

}
