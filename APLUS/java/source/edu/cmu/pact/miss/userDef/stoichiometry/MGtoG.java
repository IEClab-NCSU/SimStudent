package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MGtoG extends StoFeatPredicate {

	public MGtoG() {
		setName("mg-to-g");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mg", "g", "1",
				(String)args.get(1));
	}
}