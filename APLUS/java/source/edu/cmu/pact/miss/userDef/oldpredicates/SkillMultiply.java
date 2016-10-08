package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class SkillMultiply extends EqFeaturePredicate {

    public SkillMultiply() {
	setArity( 1 );
	setName( "skill-multiply" );
    }

    public String apply(Vector args) {
	return "multiply " + (String)args.get(0);
    }

}
