package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MultTwo extends StoFeatPredicate {

	public MultTwo() {
		setName("mult-two");
		setArity(2);
	}

	public String apply(Vector args) {
		return (multTwoTerms((String)args.get(0), (String)args.get(1)));
	}

}
