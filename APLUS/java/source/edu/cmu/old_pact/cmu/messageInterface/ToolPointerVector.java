package edu.cmu.old_pact.cmu.messageInterface;

import java.util.Vector;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Range;
import edu.cmu.pact.Utilities.trace;

public class ToolPointerVector {
	UserMessage[] userMessage;
	
	public ToolPointerVector(	Vector messageV, Vector objDescV, 
								String title, String imageBase) {
		int size = messageV.size();
		userMessage = new UserMessage[size];
		if(objDescV == null) {
			for(int i=0; i<size; i++)
				userMessage[i] =  new UserMessage(null, (String)messageV.elementAt(i), title,imageBase);
		}
		else {
			Object pointTo;
			for(int i=0; i<size; i++){
				pointTo = objDescV.elementAt(i);
				Pointer[] pointers = getPointer(pointTo);
				userMessage[i] = new UserMessage(pointers,(String)messageV.elementAt(i), title,imageBase);
						
			}
		}
	}
	
	public ToolPointerVector(Vector messageV, Vector objDescV){
		this(messageV, objDescV, null, null);
	}
	
	public ToolPointerVector(Vector messageV){
		this(messageV, null, null, null);
	}
	
	public synchronized Pointer[] getPointer(Object obj){
		if(obj instanceof String && ((String)obj).equalsIgnoreCase("NULL"))
			return null;
		else if ((obj instanceof String && ((String)obj).toLowerCase().startsWith("app")) ||
				obj instanceof Range){
			ToolPointer[] pointer = new ToolPointer[1];
			pointer[0] = new ToolPointer(obj);
			return pointer;
		}
		else if (obj instanceof Vector){
			Vector pointToV = (Vector)obj;
			int s = pointToV.size();
			ToolPointer[] pointer = new ToolPointer[s];
			for(int j=0;j<s; j++) {
				pointer[j] = new ToolPointer(pointToV.elementAt(j));
			}
			return pointer;
		}
		else if (obj instanceof ObjectProxy) {
			ToolPointer[] pointer = new ToolPointer[1];
			pointer[0] = new ToolPointer(obj);
			return pointer;
		}
		else {
			trace.out("ToolPointerVector : can't create pointer from "+obj+"["+obj.getClass().getName()+"]"); //should throw an error
			return null;
		}
	}
	
	public UserMessage[] getUserMessages(){
		return userMessage;
	}
	
}