package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class SkillSubtract extends EqFeaturePredicate {

    public SkillSubtract() {
	setArity( 1 );
	setName( "skill-subtract" );
    }

    public String apply(Vector args) {
	return "subtract " + (String)args.get(0);
    }

}
