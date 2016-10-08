//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/CntrldLispJavaConnection.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Frame;
import java.awt.Window;
import java.util.Enumeration;
import java.util.Hashtable;

import edu.cmu.old_pact.dormin.Communicator;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;


public class CntrldLispJavaConnection  extends LispJavaConnection{
    ControlledLispInterface controlledLispInterface = null;
	 
    Hashtable Registry;
    Communicator toolCommunicator;
		
    public CntrldLispJavaConnection(String host, int port, Communicator toolCommunicator, BR_Controller controller){
        this(host,toolCommunicator, controller);
        portNum = port;
    }

    public CntrldLispJavaConnection(String host, Communicator toolCommunicator, BR_Controller controller) { 
        this.controller = controller;
        theHost = host;
        portNum = 1001;
        selfConnection = this;
        this.toolCommunicator = toolCommunicator;
        Registry = new Hashtable();

        Frame foo = new Frame("");
        controlledLispInterface = new ControlledLispInterface(this, foo, controller);
    }
	
    public void registrate(String name, Object val) {
    	
    	Object v = Registry.get(name);
    	if(v != null && v instanceof Frame)
            ((Frame)v).setVisible(false);
    	Registry.put(name, val);
    }
    
    public Communicator getToolCommunicator(){
        return toolCommunicator;
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
	if(controlledLispInterface == null) {
	    Frame foo = new Frame("");
	    controlledLispInterface = new ControlledLispInterface(this, foo, controller);
	}
	if(Boolean.getBoolean("doDebug") && !controlledLispInterface.isVisible())
	    controlledLispInterface.setVisible(true);

	controlledLispInterface.receiveMessage(mess,ControlledLispInterface.CMTOINT);

        /*handled by receiveMessage:
          if(controlledLispInterface.getAutoSend(ControlledLispInterface.CMTOINT)){
          toolCommunicator.handleMessage(mess);
          }*/
    }
	
	public void sendMessage(String mess){
		if(controlledLispInterface == null) {
		    Frame foo = new Frame("");
		    controlledLispInterface = new ControlledLispInterface(this, foo, controller);
		}
		if(Boolean.getBoolean("doDebug") && !controlledLispInterface.isVisible())
		    controlledLispInterface.setVisible(true);
		
		controlledLispInterface.receiveMessage(mess,ControlledLispInterface.INTTOCM);

	/*handled by receiveMessage:
          if(controlledLispInterface.getAutoSend(ControlledLispInterface.INTTOCM)){
          SendToLisp stl = new SendToLisp(mess);
          stl.run();
          }*/
    }
}


