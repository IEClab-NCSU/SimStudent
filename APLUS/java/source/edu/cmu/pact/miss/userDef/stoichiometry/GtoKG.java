package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GtoKG extends StoFeatPredicate {

	public GtoKG() {
		setName("g-to-kg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "g", "kg", "1",
				(String)args.get(1));
	}
}