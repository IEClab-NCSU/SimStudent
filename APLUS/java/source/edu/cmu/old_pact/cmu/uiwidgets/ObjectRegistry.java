package edu.cmu.old_pact.cmu.uiwidgets;
import edu.cmu.pact.Utilities.trace;

import java.util.Hashtable;

class ObjectRegistry {
	public static ObjectRegistry knownObjects = new ObjectRegistry();
	
	private Hashtable hashtable = new Hashtable(); 
	
	static synchronized public void registerObject (String name, Object obj) {
		knownObjects.hashtable.put(name, obj);
	}
	
	static synchronized public void unregisterObject (String name) {
		knownObjects.hashtable.remove(name);
	}
	
	static synchronized public Object getRegisteredObject (String name) {
		trace.out("getting object "+name);
		Object theObj = knownObjects.hashtable.get(name);
		trace.out("found obj");
		return theObj;
	}
}
