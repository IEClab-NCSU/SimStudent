package edu.cmu.old_pact.dormin;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class NetworkTarget extends FilterTarget{
	Socket targetSocket;
	public NetworkTarget(String newName,Socket newSocket,Translator newFilter){
		this.Name = newName;
		try{
			outputStream = new PrintStream(newSocket.getOutputStream());
			myTranslator = newFilter;
	 		targetSocket = newSocket;
		} catch (IOException e){};
	}
	
	public NetworkTarget(String newName,Socket newSocket){
//		trace.out("Creating NetworkTarget (name,socket)");
		this.Name = newName;
		try{
//			trace.out("About to create outputstream in networktarget");
			outputStream = new PrintStream(newSocket.getOutputStream());
//			trace.out("After creating outputstream in networktarget");
//			myTranslator = new Translator();
//			trace.out("After creating translator in networktarget");
	 		targetSocket = newSocket;
		} catch (IOException e){};
	}
	
	//make sure the socket is synchronized (in case some other thread is using it)
	public void transmitEvent(MessageObject inEvent) {
		//trace.out (10, this, "transmitting event");
		synchronized (targetSocket) {
			super.transmitEvent(inEvent);
		}
	}

	protected Socket getSocket() {
		return targetSocket;
	}
	
}