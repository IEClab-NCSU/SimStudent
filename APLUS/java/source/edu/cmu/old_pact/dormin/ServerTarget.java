package edu.cmu.old_pact.dormin;
import java.applet.Applet;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

//A ServerTarget is a simple class to use in Applets that want to communicate with
//their server.
//This can be paired with ServerReceiver to get two-way communication between the
//Applet and server

//All you need to do is supply a name and a port number, and everything will be set up
//for you

public class ServerTarget extends NetworkTarget {
	
	ServerReceiver theReceiver = null;
	
	public ServerTarget(Applet theApplet,String name,int portNumber) throws IOException, UnknownHostException {
		super(name,new Socket(theApplet.getCodeBase().getHost(),portNumber));
//		System.out.println("in ServerTarget (Applet, name, portNumber) after SUPER");
		Target.publicizeTarget(this);
	}
	
	public ServerTarget(String name,Socket sock) {
		super(name,sock);
//		System.out.println("in ServerTarget (name sock) after SUPER");
		Target.publicizeTarget(this); //IS THIS ALLOWED UNDER WINDOWS??
	}
	
	public ServerReceiver makeReceiver(ExternalObject handler) {
		if (theReceiver == null) {
			theReceiver = new ServerReceiver(getSocket());
			theReceiver.start(); //start it up right away
		}
		theReceiver.addVerbHandler(handler);
		return theReceiver;
	}
	
	public static ServerTarget getOrMakeServerTarget(Applet theApplet,String name,int portNumber) throws IOException, UnknownHostException{
		Target theTarget = Target.getTarget(name);
		if (theTarget != null)
			return (ServerTarget)theTarget;
		else
			return new ServerTarget(theApplet,name,portNumber);
	}
		
	public static ServerTarget getOrMakeServerTargetFindingPort(Applet theApplet,String name) throws IOException, UnknownHostException{
		Target theTarget = Target.getTarget(name);
		if (theTarget != null)
			return (ServerTarget)theTarget;
		else {
			ServerTargetFinder portFinder = new ServerTargetFinder();
			return portFinder.getTarget(theApplet,name);
		}
	}
}
