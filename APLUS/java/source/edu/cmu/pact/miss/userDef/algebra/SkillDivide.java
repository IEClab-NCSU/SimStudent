package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

/**
 * @author mazda
 *
 * For Carnegie Algebra I Tutor 
 * Emulating student's action to select a pull down menu to identy an action to be taken
 * 
 */

public class SkillDivide extends EqFeaturePredicate {

    public SkillDivide() {
	setName( "skill-divide" );
	setArity( 1 );
	setReturnValueType(TYPE_SKILL_OPERAND);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) {

    	if (!isArithmeticExpression ((String)args.get(0)))
    		return null;
    	
	return "divide " + (String)args.get(0);
    }
}
