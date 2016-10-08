package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

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
    }
    
    public String apply(Vector args) {
	
	return "divide " + (String)args.get(0);
    }
}
