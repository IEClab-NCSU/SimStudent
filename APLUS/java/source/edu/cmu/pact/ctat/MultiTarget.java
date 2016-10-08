package edu.cmu.pact.ctat;
import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class MultiTarget extends Target{
	Vector TargetArray;
	
	public MultiTarget(String name){
		super(name);
		TargetArray = new Vector();
	}
	
	public void addTarget(Target inTarget){
		//if(!(inTarget.Name.equals(this.Name))) TargetArray.addElement(inTarget);
		TargetArray.addElement(inTarget);
	}

	public void removeTarget(String inName){
		int i;
		try{
			for(i=0;i<TargetArray.size();i++){
				Target foo = (Target) TargetArray.elementAt(i);
				if(foo.Name.equals(inName)) TargetArray.removeElementAt(i);
			}
		} catch (ArrayIndexOutOfBoundsException a){};
	}			

	public Vector TargetNames(){
		Vector newVec = new Vector();
		int i;
		try{
			for(i=0;i<TargetArray.size();i++){
				Target foo = (Target) TargetArray.elementAt(i);
				newVec.addElement(foo.toString());
			}
		} catch (ArrayIndexOutOfBoundsException a){};
		return newVec;
	}
	
	public Vector getTargets() {
		return TargetArray;
	}
	
	public Target getSingleTarget(String name) {
		int s = TargetArray.size();
		Target toret = null;
		for(int i=0; i<s; i++) {
			Target t = (Target) TargetArray.elementAt(i);
			if(t.toString().equals(name)) {
				return t;
			}
		}
		return toret;
	}
	
	public void transmitEvent(MessageObject inEvent){
		//trace.out (10, this, "transmitting event");
		int i;
		try{
			for(i=0;i<TargetArray.size();i++){
				Target foo = (Target) TargetArray.elementAt(i);
				//trace.out (5, this, "target = " + foo + " target type = " + foo.getClass().getName() );
				foo.transmitEvent(inEvent);
			}
		} catch (ArrayIndexOutOfBoundsException a){
			trace.out (5, this, "Exception " + a);
		};
	}
}