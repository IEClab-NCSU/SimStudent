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
//		System.out.println("Creating NetworkTarget (name,socket)");
		this.Name = newName;
		try{
//			System.out.println("About to create outputstream in networktarget");
			outputStream = new PrintStream(newSocket.getOutputStream());
//			System.out.println("After creating outputstream in networktarget");
//			myTranslator = new Translator();
//			System.out.println("After creating translator in networktarget");
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