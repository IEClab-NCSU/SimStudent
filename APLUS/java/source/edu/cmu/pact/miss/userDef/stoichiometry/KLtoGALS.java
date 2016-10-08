package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KLtoGALS extends StoFeatPredicate {

	public KLtoGALS() {
		setName("kL-to-gals");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kL", "gals", "2.262e2",
				(String)args.get(1));
	}
}