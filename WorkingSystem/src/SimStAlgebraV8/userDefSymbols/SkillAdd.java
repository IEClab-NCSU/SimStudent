/**
 * Created: Dec 20, 2013 9:09:12 PM
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
public class SkillAdd extends AlgebraV8UserDefJessSymbol {

    public SkillAdd() {
    	setName( "skill-add" );
    	setArity( 1 );
    	setReturnValueType(TYPE_SKILL_OPERAND);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {

		return (isArithmeticExpression((String)args.get(0))) ? SKILL_ADD + " " + (String)args.get(0) : null;
	}
}
