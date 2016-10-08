package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class IsEquivalent extends EqFeaturePredicate {

    public IsEquivalent() {
        setArity(2);
        setName("is-equivalent");
    }

    public String apply(Vector args) {
    	String s = inputMatcher((String)args.get(0), (String)args.get(1));
    	return s;
        }
    
}
