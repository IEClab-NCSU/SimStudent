package edu.cmu.pact.miss.SimStAlgebraV8;

import java.util.Vector;

import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.SkillNameGetter;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

/**
 * @author jinyul
 */
public class AlgebraV8AdhocSkillNameGetter extends SkillNameGetter {

    /**
     * Task specific ad-hoc method to identify skill name based on current selection
     * This is for enumerating the Algebra I tutor with a 3-column table 
     * 
     * @param selection
     * @param action
     * @param input
     * @return
     */
    public String /* Object */ skillNameGetter(BR_Controller brController, 
                                         String selection, String action, String input
                                         ) {
    	
    	if(selection.equals("done"))
    	{
    		trace.out("miss", "selection: "+selection+", skill name: done");
    		return "done";
    	}
    	
		char c = selection.charAt(DORMIN_TABLE_STEM.length());
		int col = c - '1' + 1;

        int skillIdx = input.indexOf(' ');
        String skill = skillIdx > 0 ? input.substring(0,skillIdx) : input;
        
				/* Test output */
				trace.out("miss", "input: [" + input + "]");
        trace.out("miss", "skill: " + skill);

        if ( col == 3 ) 
        {
            // The "selection" is "Skill Operand"
            if (skill != null) 
            {
            	setPreviousSkill(skill);    		
			}
        } else 
        { /* not in last column, so the command is just type-in */
        		skill = getPreviousSkill()+"-typein";
		}
        return skill;
    }
		
		static final String DORMIN_TABLE_STEM = "dorminTable";
}
