package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class IsOne extends EqFeaturePredicate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IsOne() {
	setName( "is-one" );
	setArity(1);
    }
    
    public String apply(Vector args) {
	return isOne( (String)args.get(0) );
    }

}
