package edu.cmu.old_pact.dormin;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class Target{
	protected String Name;
	static Vector allTargets = new Vector(10);
	
	public Target(){
		this.Name = "ELVIS";
	}

	public Target(String Name){
		this.Name = Name;
		TargetRegistry.targets.register(this);
	}
	
	public void transmitEvent(MessageObject inEvent){
		trace.out("Target "+Name+" is transmitting the event "+inEvent.toString());
	}
	
	public String toString(){
		return Name;
	}
	
	public static void publicizeTarget(Target newTarget) {
		allTargets.addElement(newTarget);
	}
	
	public static Target getTarget(String name) {
		Target thisTarget = null;
		boolean found = false;
		
		for (int i=0;i<allTargets.size() & !found;++i) {
			thisTarget = (Target)(allTargets.elementAt(i));
			if (name.equals(thisTarget.toString()))
				found = true;
		}
		if (found)
			return thisTarget;
		else
			return null;
	}
	
	public void close() {
	}
}