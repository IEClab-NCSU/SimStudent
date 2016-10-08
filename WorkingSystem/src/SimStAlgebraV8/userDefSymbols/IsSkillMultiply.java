/**
 * Created: Dec 20, 2013 9:14:08 PM
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
public class IsSkillMultiply extends AlgebraV8UserDefJessSymbol {


    public IsSkillMultiply() {
    	
    	setName("is-skill-multiply");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }
    
    /* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {

		String isMultiply = FALSE_VALUE;
		
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (SKILL_MULTIPLY.equals(skill)) {
            isMultiply = TRUE_VALUE;
        }
        return isMultiply;
	}
}
