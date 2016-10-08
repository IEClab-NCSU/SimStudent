/**
 * Created: Dec 20, 2013 6:19:29 PM
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
public class IsSkillAdd extends AlgebraV8UserDefJessSymbol {

    public IsSkillAdd() {
    	
    	setName("is-skill-add");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }
    
    /* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
		
		String isAdd = FALSE_VALUE;
		
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (SKILL_ADD.equals(skill)) {
        	isAdd = TRUE_VALUE;
        }
        
        return isAdd;
	}
}
