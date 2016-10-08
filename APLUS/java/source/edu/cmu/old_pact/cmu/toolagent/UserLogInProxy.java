//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/UserLogInProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import java.util.Vector;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.toolframe.DorminToolProxy;

public class UserLogInProxy extends DorminToolProxy {

	// Constructor
	public UserLogInProxy(ObjectProxy parent) {
		 super("Dialog", "Login",parent);
	}
	
	public void constructChildProxy(MessageObject inEvent, Vector description) { }
	
}