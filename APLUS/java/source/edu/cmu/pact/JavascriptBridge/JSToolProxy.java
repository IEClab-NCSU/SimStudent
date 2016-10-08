package edu.cmu.pact.JavascriptBridge;

import java.util.ArrayList;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import pact.CommWidgets.RemoteToolProxy;

public class JSToolProxy extends RemoteToolProxy {
	
	private ArrayList<String> buffer = new ArrayList<String>();
	
	public JSToolProxy(BR_Controller controller) {
		super();
        this.controller = controller;
    }
	
	private JSBridge bridge;

	protected synchronized void sendXMLString(String str) {
		if (trace.getDebugCode("applet")) trace.out("applet","Trying to send message: "+str);
		
		str = insertXMLPrologue(str);
		
		if (bridge != null)
			bridge.sendMessage(str);
		else
			buffer.add(str);
	}
	
	public void setBridge(JSBridge bridge) {
		this.bridge = bridge;
		
		while (buffer.size() > 0)
			bridge.sendMessage(buffer.remove(0));
	}

	/**
	 * @return #bridge
	 * @see pact.CommWidgets.RemoteToolProxy#getSocket()
	 */
	protected Object getSocket() {
		return bridge;
	}	
}
