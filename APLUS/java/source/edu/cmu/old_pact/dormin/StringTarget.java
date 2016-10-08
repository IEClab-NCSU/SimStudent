package edu.cmu.old_pact.dormin;
/**
* Sends an event to itself as a String.
**/
public class StringTarget extends Target{
	public ExternalObject Self=null;
	
	public StringTarget(ExternalObject newSelf){
		super(newSelf.getName());
		this.Name = newSelf.getName();
		Self = newSelf;
	}

	public void transmitEvent(MessageObject inEvent){
		//trace.out (10, this, "transmitting event");
		try {
			Self.handleEvent(inEvent.toString());
		} catch (NullPointerException e){
			System.out.println("NULL POINTER EXCEPTION in transmitEvent");
		} 
	}
}