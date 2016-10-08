package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MGtoKG extends StoFeatPredicate {

	public MGtoKG() {
		setName("mg-to-kg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mg", "kg", "1",
				(String)args.get(1));
	}
}