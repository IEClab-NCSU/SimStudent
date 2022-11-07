package edu.cmu.old_pact.dormin;
import edu.cmu.pact.Utilities.trace;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

//A ServerTargetFinder is a simple class to use in Applets that want to communicate with
//their server. This class polls a known port on the server (we typically use 2001) for a number
//which can be used as a permanent connection.

//The main way to use this class is to call the method getTarget:
// ServerTargetFinder portFinder = new ServerTargetFinder();
// ServerTarget newTarg = portFinder.getTarget(theApplet,name);

public class ServerTargetFinder {

	int myPort;
	
	public ServerTargetFinder(int usePort) {
		myPort = usePort;
	}
	
	public ServerTargetFinder() {
		myPort = 2001;
	}
	
	public ServerTarget getTarget(Applet theApplet,String name) throws IOException, UnknownHostException {
		ServerTarget returnTarget = null;
	
		Socket masterSocket = new Socket(theApplet.getCodeBase().getHost(),myPort);
		NetworkTarget masterTarget = new NetworkTarget("ServerFinder",masterSocket);
		MessageObject message = new MessageObject("GETPORT");
		message.send(masterTarget);
		
		DataInputStream instream = new DataInputStream(masterSocket.getInputStream());
		String inMessage = instream.readLine();
		instream.close();
		masterTarget.close();
		masterSocket.close();
		try {
				MessageObject portMessage = new MessageObject(inMessage,new ObjectProxy("Application"));
				int portToUse = portMessage.extractIntValue("PORT");
				trace.out("Using port "+portToUse);
				if (portToUse != -1) {
//					trace.out("about to create socket to server");
//					trace.out("host is "+theApplet.getCodeBase().getHost()+" and port is "+portToUse);
					Socket serverSocket = new Socket(theApplet.getCodeBase().getHost(),portToUse);
//					trace.out("About to create ServerTarget: "+theApplet+";"+name+";"+portToUse);
					returnTarget = new ServerTarget(name,serverSocket);
//					trace.out("After creating ServerTarget");
				}
				else
					throw new IOException("No available ports");
					
			}
		catch (DorminException d) {
			trace.out(String.valueOf(d));
		}
		return returnTarget;
	}		
}
