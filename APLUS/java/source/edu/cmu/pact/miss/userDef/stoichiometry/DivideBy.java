package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class DivideBy extends StoFeatPredicate {

	public DivideBy() {
		setName("divide-by");
		setArity(2);
	}
	
	public String apply(Vector args) {
		return divideTerms((String)args.get(0), (String)args.get(1));
	}

}
