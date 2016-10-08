//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/RatioProxy.java
package edu.cmu.old_pact.cmu.toolagent;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.ToolProxy;

public class RatioProxy extends ToolProxy {
	
	public RatioProxy(ObjectProxy parent) {
		 super(parent, "RatioTool");
	}
	
	public RatioProxy(){
		super();
	}
	
	public  void create(MessageObject inEvent)  throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("RatioTool")) {
				RatioFrame ratioFrame = new RatioFrame(this);
				try{
					setRealObjectProperties((Sharable)ratioFrame, inEvent);
				}catch (DorminException e) { throw e;}
				this.setRealObject(ratioFrame);
				ratioFrame.setProxyInRealObject(this);
				//ratioFrame.setVisible(true); 
			}
			else {
				super.create(inEvent);
			}
		}catch (DorminException e) { 
			throw e; 
		}
	}
}