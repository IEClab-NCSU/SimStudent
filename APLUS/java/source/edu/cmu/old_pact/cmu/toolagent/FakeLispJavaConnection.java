//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/FakeLispJavaConnection.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Frame;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.cmu.old_pact.dormin.Communicator;


public class FakeLispJavaConnection  extends LispJavaConnection{
	static LispJavaConnection selfConnection;
	FakeLispInterface fakeLispInterface = null;
	 
	Hashtable Registry;
	Communicator toolCommunicator;
		
	public FakeLispJavaConnection(Communicator toolCommunicator) { 
		this.toolCommunicator = toolCommunicator;
		Registry = new Hashtable();
		Frame foo = new Frame("");
		fakeLispInterface = new FakeLispInterface(this, foo);
		fakeLispInterface.setVisible(true);

	}
	
	public void registrate(String name, Object val) {
    	
    	Object v = Registry.get(name);
    	if(v != null && v instanceof Frame)
    		((Frame)v).setVisible(false);
    	Registry.put(name, val);
    }
    
    public Object getObject(String key) {
    	return Registry.get(key);
    }
    
     public void unRegistrate() {
    	for (Enumeration e = Registry.keys() ; e.hasMoreElements() ;) {
			String key = (String)e.nextElement();
    		
    		Object obj = Registry.get(key);
    		if(obj instanceof Window && ((Window)obj).isVisible()) {
    			((Window)obj).setVisible(false);
    			((Window)obj).dispose();
    		}
    		obj = null;
    	}
    	Registry.clear();
    }
    
    public void unRegistrate(String key) {
		Object obj = Registry.get(key);
    	if(obj != null) {
    		if(obj instanceof Window) 
    			((Window)obj).setVisible(false);
    		Registry.remove(key);
    	}
    }
	

    public LispJavaConnection connect()  {
        return this;
    }
	
	public void messageReceived(String mess){
		//trace.out("in FakeLJC, received message "+mess);
		toolCommunicator.handleMessage(mess);
	}
	
	public void sendMessage(String mess){
	/*
		if(fakeLispInterface == null) {
			Frame foo = new Frame("");
			fakeLispInterface = new FakeLispInterface(this, foo);
		}
		if(Boolean.getBoolean("doDebug") && !fakeLispInterface.isVisible())
			fakeLispInterface.setVisible(true);
	*/		
		fakeLispInterface.receiveMessage(mess);
	}
}


