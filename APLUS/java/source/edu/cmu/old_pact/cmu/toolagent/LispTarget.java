//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/LispTarget.java
package edu.cmu.old_pact.cmu.toolagent;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.Target;
import edu.cmu.pact.Utilities.trace;

public class LispTarget extends Target{
	LispJavaConnection connectTo;
	
	public LispTarget(LispJavaConnection connectTo){
		super("LISP");
		this.connectTo = connectTo;
	}
	
	public synchronized void transmitEvent(MessageObject inEvent){
		trace.out (15,  "LispTarget.java", "Transmitting event");
		String message = inEvent.toString();
		//try  {
         	//EventTarget target = new EventTarget();
         	//SemanticEvent  ev = new SemanticEvent();
         	//ev.addMessage("", message);
         	//ev.send(target);
         	connectTo.sendMessage(message);                
     	//}catch (IOException e)  { 
     		//trace.out("LispTarget transmitEvent "+e);
     	//}
	}
}