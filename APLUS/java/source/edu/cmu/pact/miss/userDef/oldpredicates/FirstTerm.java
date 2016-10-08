package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class FirstTerm extends EqFeaturePredicate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FirstTerm() {
	setName( "first-term" );
	setArity( 1 );
    }

    public String apply(Vector args) {
	return firstTerm( (String)args.get(0) );
    }

}
