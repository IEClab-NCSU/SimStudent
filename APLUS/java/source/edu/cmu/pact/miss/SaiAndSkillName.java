package edu.cmu.pact.miss;

import edu.cmu.pact.Utilities.trace;

public class SaiAndSkillName {
    public Sai sai;
    public String skillName;
    public SaiAndSkillName(Sai sai, String skillName){
    	if(trace.getDebugCode("miss"))trace.out("miss", "Creating a SaiAndSkillName object with sai: " + sai + "skillName: " + skillName);
        this.sai = sai;
        this.skillName = skillName;
    }
}

