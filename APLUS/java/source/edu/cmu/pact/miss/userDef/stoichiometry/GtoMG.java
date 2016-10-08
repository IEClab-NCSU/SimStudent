package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GtoMG extends StoFeatPredicate {

	public GtoMG() {
		setName("g-to-mg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "g", "mg", "1e3",
				(String)args.get(1));
	}
}