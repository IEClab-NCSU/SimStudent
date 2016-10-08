
package edu.cmu.pact.ctat;

import java.util.Enumeration;
import java.util.Hashtable;

public class TargetRegistry{
	public static TargetRegistry targets = new TargetRegistry();
	
	private Hashtable Registry = new Hashtable();
	
	public void register(Target obj) {

		//if (obj.toString().equalsIgnoreCase ("targets"))
		//	obj = null;
		targets.Registry.put(obj.toString(), obj);
		//trace.out (5, this, "Target registered : "+obj.toString());
	 
	}
	
	public Target getTarget(String name) {
		//trace.out (5, this, "target for name " + name + " = " + (Target) targets.Registry.get(name));
		return (Target)targets.Registry.get(name);
	}
	
	public void unRegister(String name) {
		targets.Registry.remove(name);
	}
	
	public MultiTarget getTargets(String exceptMeName) {
		MultiTarget toret = new MultiTarget("Targets");
		Enumeration ts = targets.Registry.elements();
		while (ts.hasMoreElements()) {
			Target t = (Target)ts.nextElement();
			if(!t.Name.equals(exceptMeName) && !t.Name.equals("Targets"))
				toret.addTarget(t);
		}
		return toret;
	}
	public Hashtable allTargets() {
		return targets.Registry;
	}
}