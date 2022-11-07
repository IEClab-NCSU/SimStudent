package edu.cmu.old_pact.cl.tutors.skillometer;

import edu.cmu.pact.Utilities.trace;

import java.util.Vector;

public class SectionSkill {
	private float init_p_known;
    private float p_known;
	private String id;
	private String name;
	private boolean isDisplayed  = true;;
	private boolean isRemediated = true;
	// will be set to true for student action only;
	private boolean isUpdated = false;
	private Vector subSkills;

	public SectionSkill(){
		subSkills = new Vector();
	}
	
	public SectionSkill(String name, String id, float init_p_known){
		this();
		this.name = name;
		this.id = id;
		this.init_p_known = init_p_known;
                p_known = init_p_known;
	}
	
	public void resetInitPKnown(float init_p_known){
		this.init_p_known = init_p_known;
	}
	
	public float getInitPKnown(){
		return init_p_known;
	}
	
    public void setPKnown(float pk)
    {
        p_known = pk;
        int s = subSkills.size();
        for(int i=0;i<s;i++)
        {
            ((SubSkill)subSkills.elementAt(i)).setPKnown(p_known);
            trace.out("in skill"+getName()+" setting subskill "+((SubSkill)(subSkills.elementAt(i))).getName()+" to "+p_known);
        }
        isUpdated = true;
    }

    public float getPKnown()
    {
        return p_known;
    }

	public void setID(String id){
		this.id = id;
	}
	
	public String getID(){
		return id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setIsDisplayed(boolean b){
		isDisplayed = b;
	}
	
	public boolean getIsDisplayed(){
		return isDisplayed;
	}
	
	public void setIsRemediated(boolean b){
		isRemediated = b;
	}
	
	public boolean getIsRemediated(){
		return isRemediated;
	}
	
	public boolean getIsUpdated(){
		return isUpdated;
	}
	
	public void addSubSkill(SubSkill sk){
            trace.out(sk.getName() + " guess = " + sk.getPGuess());
		subSkills.addElement(sk);
	}
	
	public SubSkill getMatchedSubSkill(String sub_name){
		int s = subSkills.size();
		if(s == 0)
			return null;
		SubSkill currSub = null;
		for(int i=0; i<s; i++) 
                {
			currSub=(SubSkill)subSkills.elementAt(i);
			if(currSub.getName().equalsIgnoreCase(sub_name)){
                            //  currSub.setDefPKnown(init_p_known);
				return currSub;
			}
                }
		return null;
	}
}	
	
