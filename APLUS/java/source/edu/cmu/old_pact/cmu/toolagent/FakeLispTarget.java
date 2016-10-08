//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/FakeLispTarget.java
package edu.cmu.old_pact.cmu.toolagent;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.Target;

public class FakeLispTarget extends Target{
     FakeLispJavaConnection connectTo;
	
	public FakeLispTarget(FakeLispJavaConnection connectTo){
		super("FAKELISP");
		this.connectTo = connectTo;
	}
	
	public synchronized void transmitEvent(MessageObject inEvent){
		String message = inEvent.toString();
		message = message+"\r\n";
		connectTo.sendMessage(message);
	}
}