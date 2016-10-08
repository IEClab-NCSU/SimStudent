package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MultThree extends StoFeatPredicate {

	public MultThree() {
		setName("mult-three");
		setArity(3);
	}

	public String apply(Vector args) {
		return multThreeTerms((String)args.get(0), (String)args.get(1), (String)args.get(2));
	}

}
