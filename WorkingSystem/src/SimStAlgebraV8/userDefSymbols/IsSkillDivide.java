/**
 * Created: Dec 20, 2013 9:12:52 PM
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
public class IsSkillDivide extends AlgebraV8UserDefJessSymbol {


    public IsSkillDivide() {
    	
    	setName("is-skill-divide");
        setArity(1);
        setArgValueType(new int[] {TYPE_SKILL_OPERAND});
    }
    
    /* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {
        
		String isDivide = FALSE_VALUE;
		
        String skillOperand = (String)args.get(0);
        String skill = getSkillOperandSkill(skillOperand);
        
        if (SKILL_DIVIDE.equals(skill)) {
            isDivide = TRUE_VALUE;
        }
        return isDivide;
	}
}
