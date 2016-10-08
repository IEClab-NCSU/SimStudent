package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MultFour extends StoFeatPredicate {

	public MultFour() {
		setName("mult-four");
		setArity(4);
	}

	public String apply(Vector args) {
		return multFourTerms((String)args.get(0), (String)args.get(1), 
						(String)args.get(2), (String)args.get(3));
	}

}
