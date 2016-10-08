package edu.cmu.old_pact.cl.tutors.skillometer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;

public class SkillsManager  {
	private Hashtable allSkills;
	private Vector propertyNames, propertyValues;
	ObjectProxy ref_obj = null;
	private String currEquation = "";
	private String refTarget = null;
	private boolean useLMS = false;
	private Vector sectionSkills;
	
	public SkillsManager(ObjectProxy ref_obj){
		allSkills = new Hashtable();
		propertyNames = new Vector();
		propertyNames.addElement("VALUE");
		propertyNames.addElement("ISCHECKED");
		propertyNames.addElement("SKILLIMAGE");
		propertyValues = new Vector();
		sectionSkills = new Vector();
		this.ref_obj = ref_obj;
	}
	
	public SkillsManager(){
		this(null);
	}
	
	public void setRefTarget(String t){
		refTarget = t;
	}
	
	public void setReferenceProxy(ObjectProxy o){
		ref_obj = o;
	}
	
	public void setUseLMS(boolean b) {
		useLMS = b;
	}
	
	public void delete(){
		ref_obj = null;
		allSkills.clear();
		allSkills = null;
		propertyNames.removeAllElements();
		propertyNames = null;
		propertyValues.removeAllElements();
		propertyValues = null;
		currEquation = null;
		sectionSkills.removeAllElements();
		sectionSkills = null;
	}
	
		
	public void updateSkill(String skillName, String gradient){
		SkillCalculator sCalculator = getSkill(skillName);
		if(sCalculator == null)
			return;
		boolean direction; //direction to move the skill (true is up) -- hints are treated like errors
		if (gradient.equals("1"))
			direction = true;
		else
			direction = false;
		float currValue = sCalculator.updateSkill(direction);
		if(sCalculator.canDisplay())
			sendToSkillometer(sCalculator.getSkillName(), currValue);
	}
	
	// is it in use?
	public void updateSkill(String skillName, float currValue){
		SkillCalculator sCalculator = getSkill(skillName);
		sCalculator.setPKnown(currValue);
		if(sCalculator.canDisplay())
			sendToSkillometer(sCalculator.getSkillName(), currValue);
	}
	
	public void updateSkill(String skillName, String gradient, String currEq){
		if(!currEquation.equalsIgnoreCase(currEq)){
			currEquation = currEq;
			updateSkill(skillName, gradient);
		}
	}
	
	public Hashtable getAllSkills(){
		Hashtable toret = new Hashtable();
		Enumeration els = allSkills.elements();
		
		if(!useLMS){
			Enumeration keys = allSkills.keys();
			String nextVal;
			SkillCalculator nextCal;
			while (keys.hasMoreElements()){
				nextCal = (SkillCalculator)els.nextElement();
				nextVal =String.valueOf(nextCal.getPKnown());
				toret.put(keys.nextElement(), nextVal);
			}
		}
		else{
			String id;
			SectionSkill ss;
			while (els.hasMoreElements()){
				ss = (SectionSkill)els.nextElement();
				if(ss.getIsUpdated())
					toret.put(ss.getID(), new Float(ss.getPKnown()));
			}
		}
				
		return toret;
	}

    public Hashtable getAllSkillsById()
    {
        SectionSkill current_skill;
        Hashtable to_ret = new Hashtable();

        int length = sectionSkills.size();
        for(int i=0;i<length;i++)
        {
            current_skill = (SectionSkill)sectionSkills.elementAt(i);
            to_ret.put(current_skill.getID(), current_skill);
        }
        return to_ret;
    }
	
	public void addSectionSkill(SectionSkill sk){
            allSkills.put(sk.getName(), sk);
            sectionSkills.addElement(sk);
	}
	
	public void setStudentSkill(String skill_id, float cur_pKnown) throws NoSuchFieldException{
		if(!useLMS)
			return;
		int s = allSkills.size();
		if(s == 0)
			return;
		SectionSkill sec_skill;
		Enumeration els = allSkills.elements();
		boolean found = false;
		while (els.hasMoreElements()){
			sec_skill = (SectionSkill)els.nextElement();
			if(sec_skill.getID().equals(skill_id)){
				found = true;
				sec_skill.setPKnown(cur_pKnown);
				if(sec_skill.getIsDisplayed())
					sendToSkillometer(sec_skill.getName(), cur_pKnown);
				break;
			}
		}
                    // for pilot do nothing if the skill isn't found - later we will display student skills 
                    // from previous sections that are set to be displayed
                /*                		if(!found){

                    throw new NoSuchFieldException("SkillsManager: in setStudentSkill : SectionSkills don't contain the skill with id = "+skill_id);
                    }*/
	}
			
	private SkillCalculator getSkill(String skillName){
		SkillCalculator sc = null;
		if(useLMS){
			int s = allSkills.size();
			if(s == 0)
				return null;
			SectionSkill sec_skill;
			Enumeration els = allSkills.elements();
			while (els.hasMoreElements()){
				sec_skill = (SectionSkill)els.nextElement();
				sc = sec_skill.getMatchedSubSkill(skillName);
				if(sc != null){
                                    return sc;
                                }
			}
			System.out.println("SkillsManager: getSkill : SectionSkills don't have a subskill with a name : '"+skillName+"'");
		}
		else{
			sc = (SkillCalculator)allSkills.get(skillName);
			if(sc == null){
				sc = new SkillCalculator(skillName);
				allSkills.put(skillName, sc);
			}
		}
		return sc;
	}
	
	public void sendToSkillometer(String skillName, float currValue) {
		if(ref_obj != null){
			MessageObject mo = new MessageObject("SETPROPERTY");
			String objDesc = "3:S:11:Application,S:8:POSITION,I:1:1,S:11:Skillometer,S:8:POSITION,I:1:1,S:5:Skill,S:4:NAME,S:"+String.valueOf(skillName.length())+":"+skillName;
			mo.addObjectParameter("Object", objDesc);
		
			propertyValues.removeAllElements();
			propertyValues.addElement(new Float(currValue));
			if(currValue >=  0.95) {
				propertyValues.addElement(Boolean.valueOf("true"));
				propertyValues.addElement("knownSkillBar.gif");
			}
			else{
				propertyValues.addElement(Boolean.valueOf("false"));
				propertyValues.addElement("skillbar.gif");
			}
		
			mo.addParameter("PROPERTYNAMES", propertyNames);
			mo.addParameter("PROPERTYVALUES", propertyValues);
			if(refTarget == null)
				ref_obj.send(mo);
			else
				ref_obj.send(mo, refTarget);
		
			objDesc = null;
			mo = null;
		}
	}
}
