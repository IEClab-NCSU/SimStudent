package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MLtoGALS extends StoFeatPredicate {

	public MLtoGALS() {
		setName("mL-to-gals");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mL", "gals", "1",
				(String)args.get(1));
	}
}