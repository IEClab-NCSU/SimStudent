package edu.cmu.old_pact.dormin;

public class ObjectTarget extends Target{
	public ExternalObject Self=null;
	
	public ObjectTarget(ExternalObject newSelf){
		super(newSelf.getName());
		this.Name = newSelf.getName();
		Self = newSelf;
	}

	public void transmitEvent(MessageObject inEvent){
		//trace.out (10, this, "transmitting event");
		try {
			Self.handleEvent(inEvent);
		} catch (NullPointerException e){
			System.out.println("NULL POINTER EXCEPTION in handleEvent");
		} 
	}
}
	