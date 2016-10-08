package pact.CommWidgets;


import java.util.Enumeration;
import java.util.Vector;

import edu.cmu.old_pact.skillometer.SkillometerFrame;
import edu.cmu.pact.Utilities.trace;

//////////////////////////////////////////////////////////
/**
 * Manages the skillometer for the CommWidgets and Behavior Recorder
 */
//////////////////////////////////////////////////////////
public class SkillometerManager {
    
    private SkillometerFrame skillometerFrame;
    static private SkillometerManager instance;
    private java.util.Hashtable skillsTable = new java.util.Hashtable();
    public static final float MASTERY = (float) 0.95;
    
    
    //////////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////////
    public SkillometerManager() {
        
    }
    
    //////////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////////
    public float getSkillValue(String skill){
        float   estimates [] = (float[]) (skillsTable.get(skill));
        if (estimates == null)
            return (float) -1.0;
        else
            return estimates[0];
    }
    
    //////////////////////////////////////////////////////////
    /**
     * Find out if student has mastered this skill
     */
    //////////////////////////////////////////////////////////
    public boolean atMastery(String skill){
        return (getSkillValue(skill)>MASTERY);
    }

    //////////////////////////////////////////////////////////
    /**
        Return the names of all skills currently in skillometer
     */
    //////////////////////////////////////////////////////////
    public Vector getSkillNames () {
        Vector v = new Vector();
        for (Enumeration i = skillsTable.keys(); i.hasMoreElements();)
            v.addElement(i.nextElement());
        return v;
    }
    //////////////////////////////////////////////////////////
    /**
     * Update a skill to a specified value
     */
    //////////////////////////////////////////////////////////
    public void updateSkill (String skill, float newPKnown) {

        float estimates [] = (float[]) (skillsTable.get(skill));
        if (estimates == null) {
            trace.out(5, this, "Error: updating nonexistent skill: " + skill);
            return;
        }
        estimates[0] = newPKnown;
        trace.out (5, this, "UPDATE SKILL VALUE " + skill + "new value = " + newPKnown);
        skillsTable.put(skill, estimates);
        getSkillometerFrame().updateSkillValue(skill,(float) estimates[0]);
    }

    //////////////////////////////////////////////////////////
    /**
     * Update a skill.  Only called once per skill per problem.
     */
    //////////////////////////////////////////////////////////
    public void updateSkill(String skill, boolean success){
        float estimates [] = (float[]) (skillsTable.get(skill));
        if (estimates == null) {
            trace.out(5, this, "Error: updating nonexistent skill: " + skill);
            return;
        }
        float cur_L = estimates[0];
        float cur_T = estimates[1];
        float cur_G = estimates[2];
        float cur_S = estimates[3];
        float cur_prevL = estimates[0];
        float last = cur_prevL;
        if (success){
            cur_prevL=((cur_prevL*(1-cur_S))/((cur_prevL*(1-cur_S))+((1-cur_prevL)*cur_G)));
            cur_L = cur_prevL + (cur_T * (1-cur_prevL));
        }
        else{
            cur_prevL=((cur_prevL*cur_S)/((cur_prevL*cur_S)+((1-cur_prevL)*(1-cur_G))));
            cur_L = cur_prevL + (cur_T * (1-cur_prevL));
        }
        estimates[0] = cur_L;

        trace.out (5, this, "UPDATE SKILL " + skill + "success = " + success + " old value = " + last + " new value = " + cur_L);
        skillsTable.remove(skill);
        skillsTable.put(skill, estimates);
        getSkillometerFrame().updateSkillValue(skill,(float) estimates[0]);
    }
    
    
    //////////////////////////////////////////////////////////
    /**
     * Add a new skill to the skillometer with parameters
     */
    //////////////////////////////////////////////////////////
    public void initializeSkill(String skill, float l,
                                    float t, float g, float s){
        float params [] = new float[4];
        params[0] = l; // L
        params[1] = t; // T
        params[2] = g; // G
        params[3] = s; // S
        trace.out (5, this, "Initializing skill " + skill);
        skillsTable.put(skill, params);
        getSkillometerFrame().addSkill(skill);
        getSkillometerFrame().updateSkillValue(skill, (float)l);
    }
    
    //////////////////////////////////////////////////////////
    /**
     * Add a new skill to the skillometer with default parameters
     */
    //////////////////////////////////////////////////////////
    public void initializeSkill(String skill){
        float l = (float) 0.1; // PKnown
        float t = (float) 0.1; // PLearn
        float g = (float) 0.3; // PGuess
        float s = (float) 0.4; // PSlip
        initializeSkill (skill, l, t, g, s);
    }
    
    
    public void test(){
        initializeSkill("Murgling");
        trace.out(5, this, "Murgling = " + getSkillValue("Murgling"));
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        trace.out(5, this, "Murgling = " + getSkillValue("Murgling"));
        updateSkill("Murgling", false);
        trace.out(5, this, "Murgling = " + getSkillValue("Murgling"));
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        updateSkill("Murgling", true);
        trace.out(5, this, "Murgling = " + getSkillValue("Murgling"));
        trace.out(5, this, "Murgling mastery = " + atMastery("Murgling"));
        initializeSkill ("xxx");
        updateSkill ("xxx", true);
    }
    
    public void reset() {
        skillsTable = new java.util.Hashtable();
        
        getSkillometerFrame().clearSkills();
    }
    //////////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////////
    public void show() {
        getSkillometerFrame().show();
    }
    
    //////////////////////////////////////////////////////////
    /**
     *
     */
    //////////////////////////////////////////////////////////
    public void hide() {
        getSkillometerFrame().hide();
    }

    /**
     * @return Returns the skillometerFrame.
     */
    private SkillometerFrame getSkillometerFrame() {
        if (skillometerFrame == null)
            skillometerFrame = new SkillometerFrame("Skillometer");
        return skillometerFrame;
    }
    
}
