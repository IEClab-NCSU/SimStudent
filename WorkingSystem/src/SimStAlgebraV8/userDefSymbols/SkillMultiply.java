/**
 * Created: Dec 20, 2013 9:10:20 PM
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
public class SkillMultiply extends AlgebraV8UserDefJessSymbol {


    public SkillMultiply() {
    	setName( "skill-multiply" );
    	setArity( 1 );
    	setReturnValueType(TYPE_SKILL_OPERAND);
    	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
	/* (non-Javadoc)
	 * @see SimStudent2.ProductionSystem.UserDefJessSymbol#apply(java.util.Vector)
	 */
	@Override
	public String apply(ArrayList<String> args) {

		return (isArithmeticExpression((String)args.get(0))) ? SKILL_MULTIPLY + " " + (String)args.get(0) : null;
	}
}
