/*The receiver class handles the reception of events*/

package edu.cmu.old_pact.dormin;

public class Receiver extends Thread {
	protected ExternalObject myObject;
	
	public Receiver(){};
	
	public Receiver(ExternalObject inObject){
		myObject = inObject;
	}		
	
	public  void receive(MessageObject inEvent){
		myObject.handleEvent(inEvent);
	}
}