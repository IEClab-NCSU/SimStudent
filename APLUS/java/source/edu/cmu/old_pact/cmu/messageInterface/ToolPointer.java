


package edu.cmu.old_pact.cmu.messageInterface;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Range;

public class ToolPointer extends edu.cmu.old_pact.cmu.messageInterface.Pointer{
	private Object objDesc = null; //could be objectproxy, range or s:4:null (we should change this so its always an objectProxy)
	
	public ToolPointer(Object objDesc) {
		this.objDesc = objDesc;
	}
	
    public  void point() throws DorminException {
    	sendToObject("TRUE");
    }

    public  void unPoint() throws DorminException {
    	sendToObject("FALSE");
    }

    public void setPointTo(Object pointTo){
    }
    
    private synchronized void sendToObject(String doHighlight) throws DorminException {
    	MessageObject mo = new MessageObject("SETPROPERTY");
    	Vector names = new Vector();
    	names.addElement("HighLight");
    	mo.addParameter("PROPERTYNAMES", names);
    	Vector values = new Vector();
    	values.addElement(doHighlight);
    	mo.addParameter("PROPERTYVALUES", values);
    	if(objDesc instanceof Range) {
    		Range rangeDesc = (Range)objDesc;
    		mo.addParameter("OBJECT", rangeDesc);
    		ObjectProxy parent = rangeDesc.getParent();
    		parent.treatMessage(mo,"SETPROPERTY");
    	}
    	else if (objDesc instanceof ObjectProxy) {
    		mo.addObjectParameter("OBJECT",(ObjectProxy)objDesc);
    		ObjectProxy.topObjectProxy.send(mo, "Application0");
    	}
    	else if (objDesc == null) {
    	} //don't worry about null destinations for pointers.
    	else {//should throw error
    		//trace.out("in sendToObject for ToolPointer, objDesc is '"+objDesc+"'");
    	}
    }
    
    public String toString(){
    	return 	"ToolPointer points to : "+objDesc.toString();
    }
    	
}