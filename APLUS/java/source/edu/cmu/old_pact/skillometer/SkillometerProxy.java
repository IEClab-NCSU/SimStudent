package edu.cmu.old_pact.skillometer;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolProxy;

public class SkillometerProxy extends DorminToolProxy {
	
	public SkillometerProxy(ObjectProxy parent) {
		 super(parent, "Skillometer");
	}
	
	public SkillometerProxy(){
		super();
	}
	
	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("Skillometer")) {
				SkillometerFrame sf = new SkillometerFrame("Student");
				this.setRealObject(sf);
				sf.setProxyInRealObject(this);
				setRealObjectProperties((Sharable)sf, inEvent);
			} 
			else
				super.create(inEvent);
		}catch (DorminException e) { 
			throw e; 
		} 
	}
	
	
	//createSkillProxy creates an object proxy corresponding to the named skill
	private SkillProxy createSkillProxy(String skillName) {
		SkillProxy sk_pro = new SkillProxy(this, skillName);
		SkillometerFrame skm = ((SkillometerFrame)getObject());
		skm.addSkill(skillName);
		Skill skill = skm.getSkill(skillName);
		skill.setProxyInRealObject(sk_pro);
		sk_pro.setRealObject(skill);
		return sk_pro;
	}
	
	//override getContainedObjectBy so we can create a skill on fly
	public ObjectProxy getContainedObjectBy(String type, String  format, String value) {
		ObjectProxy prox = super.getContainedObjectBy(type,format,value);
		if (prox == null && type.equalsIgnoreCase("Skill") && format.equalsIgnoreCase("Name"))
			prox = createSkillProxy(value);
		return prox;
	}

	public  void delete(MessageObject mo){ 
		this.deleteProxy(); 
	}	
}