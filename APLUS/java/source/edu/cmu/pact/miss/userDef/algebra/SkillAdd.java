/**
 * @author mazda
 * 
 * For Carnegie Algebra I Tutor 
 * Emulating student's action to select a pull down menu to identy an action to be taken
 * 
 */

package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

public class SkillAdd extends EqFeaturePredicate {
    
    public SkillAdd() {
	setArity(1);
	setName( "skill-add" );
	
	setReturnValueType(TYPE_SKILL_OPERAND);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
	
    }

    public String apply(Vector args) {
	
    if (!isArithmeticExpression ((String)args.get(0)))
    		return null;
    	
    	
	return "add " + (String)args.get(0);
    }

}
