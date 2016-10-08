package edu.cmu.pact.miss;

import java.util.List;
import java.util.Vector;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;

/**
 * @author jinyul
 * 
 * Purpose of this class is to give names such as add, add-typein to skills
 * demonstrated in simstudent instead of having the skills all be 
 * unlabeled skills. 
 */

public class SkillNameGetter {

	/**
     * maintains the history of calls to SkillNameGetter, i.e. skills 
     * demonstrated.
     */
    static Vector history;
    private String previousSkill = "";

    public String getPreviousSkill() {
		return previousSkill;
	}

	public void setPreviousSkill(String previousSkill) {
		this.previousSkill = previousSkill;
	}

	/**
     * must be overridden
     */
    public String /* Object */ skillNameGetter(BR_Controller brc,
            String selection, String action, String input )
    {
    	new Exception("you must override SkillNameGetter.skillNameGetter() with your domain-specific implementation.").printStackTrace();
    	return null;
    }
    
    /**
     * adds record to history. All implementations of SkillNameGetter.skillNameGetter() should call this method.
     *
     */
    public void recordQuery(String selection){
        history.add(selection);
    }
    
    /**
     * Returns the n last selections for which SkillNameGetter was used. Used for recency calculations.
     * @param n
     * @return
     */
    public static List getNLastQueries(int n){
        Vector v = new Vector();
        int startIndex = history.size() - n;
        for (int i=startIndex; i<history.size(); i++){
            v.add((String) history.get(i));
        }
        return v;
    }
    
    /**
     * returns the number of queries ago in which this selection was used.
     * @param sel
     * @return
     */
    public static int howManyBack(String sel){
        return history.size() - history.lastIndexOf(sel);
    }
    
    // Override this method if you wish any sort of initialization of the
    // FoaGetter class you are defining would have been taken place when 
    // a new start state is created
    // 
    public void init(BR_Controller brController) {
    	if(trace.getDebugCode("miss"))trace.out("miss", "You must define init(BR_Controller) in your SkillNameGetter class!");
    }
    
    
}
