package edu.cmu.old_pact.dormin;

/**
*	TempObjectProxy supports virtual object hierarchy. Can be used to create 
* virtual object description.
**/

import java.util.Vector;

public class TempObjectProxy extends CommonObjectProxy {
	Target external_target;
	
	public TempObjectProxy(Target target, 
							String type,  
							String name, 
							ObjectProxy parent) {
		
		super(type, name, parent);
		external_target = target;
	}
	
	public TempObjectProxy(Target target) {
		super("Default", "default", null);
		external_target = target;
	}
	
	public TempObjectProxy(	Target target,
							ObjectProxy parent, 
							String type){ 
		super(parent, type);
		external_target = target;
	}
	
	
	public TempObjectProxy(Target target, 
							String type,  
							int pos, 
							ObjectProxy parent) {
		
		super(type, parent, pos);
		external_target = target;
	}
	
	public TempObjectProxy(String type,  
							String name, 
							ObjectProxy parent) {
		
		super(type, name, parent);
		external_target = parent.getTarget();
	}
	
	public TempObjectProxy(	ObjectProxy parent,
							String type){
		super(parent, type);
		external_target = parent.getTarget();
	}
	
	
	public TempObjectProxy(String type,  
							int pos, 
							ObjectProxy parent) {
		
		super(type, parent, pos);
		external_target = parent.getTarget();
	}
	
	public void send(MessageObject outEvent) {
	// depricated functionality for now: no senders or receivers are spesified
	/*
		Vector descriptionV = getDescription();
		String descriptionStr = getStrDescription();
		outEvent.addParameter("Sender", descriptionV);
		Vector objV;
		try{
			objV = outEvent.extractListValue("Receiver");
		} catch (DorminException e) { 
			objV = new Vector(1);
			outEvent.addParameter("Receiver", descriptionStr);
		}
	*/
		outEvent.send(external_target);
	}
	
	public String toString() {
		String toret = getStrDescription();
		return toret;
	}
	
	public  void constructChildProxy(MessageObject mo, Vector description){ }
	public  void treatMessage(MessageObject mo, String verb){ }
	public void constructNewChild(MessageObject mo, Vector description) throws NoSuchObjectException{ 
		super.constructNewChild(mo, description);
	} 

}
	
	