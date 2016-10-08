package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GALStoML extends StoFeatPredicate {

	public GALStoML() {
		setName("gals-to-mL");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "gals", "mL", "3.7854e3",
				(String)args.get(1));
	}
}