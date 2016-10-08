/**
 * Created: Dec 20, 2013 6:28:05 PM
 * @author mazda
 * 
 */
package SimStAlgebraV8.userDefSymbols;

import java.util.ArrayList;

import SimStAlgebraV8.AlgebraV8UserDefJessSymbol;

/**
 * @author mazda
 *
 */
public class IsSkillSubtract extends AlgebraV8UserDefJessSymbol {

    public IsSkillSubtract() {
    	
    	setName("is-skill-subtract");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }
    
    /* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		
        String isSubtract = FALSE_VALUE;
        
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (SKILL_SUBTRACT.equals(skill)) {
            isSubtract = TRUE_VALUE;
        }

        return isSubtract;
	}
}

