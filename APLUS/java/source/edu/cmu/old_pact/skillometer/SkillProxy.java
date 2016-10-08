package edu.cmu.old_pact.skillometer;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.ToolProxy;

public class SkillProxy extends ToolProxy {
	
	public SkillProxy(ObjectProxy parent, String name) {
		 super("Skill", name, parent);
	}
		
	public  void delete(MessageObject mo){ 
		this.deleteProxy(); 
	}
	
}
	