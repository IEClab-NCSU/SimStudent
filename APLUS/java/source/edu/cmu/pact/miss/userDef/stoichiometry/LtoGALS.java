package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LtoGALS extends StoFeatPredicate {

	public LtoGALS() {
		setName("L-to-gals");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "L", "gals", "1",
				(String)args.get(1));
	}
}