package SimStAlgebraV8;
import java.util.Vector;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTable.TableExpressionCell;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.SkillNameGetter;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;
public class AlgebraV8AdhocSkillNameGetter extends SkillNameGetter {
    public String /* Object */ skillNameGetter(BR_Controller brController, 
                                         String selection, String action, String input
                                         ) {
    	if(selection.equals("done"))
    	{
    		return "done";
    	}    	
		char c = selection.charAt(DORMIN_TABLE_STEM.length());
		int col = c - '1' + 1;
        int skillIdx = input.indexOf(' ');
        String skill = skillIdx > 0 ? input.substring(0,skillIdx) : input;
        if ( col == 3 ) {
        	if (skill != null) 
            {
            	setPreviousSkill(skill);    		
			}
        } else { 		/* not in last column, so the command is just type-in. if empty then return unknown-skill*/
				if (getPreviousSkill().length()==0)
        			skill="unknown_skill";
        		else
        			skill = getPreviousSkill()+"-typein";
		}
        return skill;
    }
		static final String DORMIN_TABLE_STEM = "dorminTable";
}
