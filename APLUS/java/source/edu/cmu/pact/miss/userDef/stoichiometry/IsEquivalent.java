package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class IsEquivalent extends StoFeatPredicate {

    public IsEquivalent() {
        setArity(2);
        setName("is-equivalent");
    }

    public String apply(Vector args) {
    	trace.out("boots21", "inputMatcher called!");
    	String s = inputMatcher((String)args.get(0), (String)args.get(1));
    	return s;
        }
   
}
