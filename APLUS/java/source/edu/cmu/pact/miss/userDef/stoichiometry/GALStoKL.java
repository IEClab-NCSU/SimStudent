package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GALStoKL extends StoFeatPredicate {

	public GALStoKL() {
		setName("gals-to-kL");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "gals", "kL", "3.7854e-3",
							(String)args.get(1));
	}
}