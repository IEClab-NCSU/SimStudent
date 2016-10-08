package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KGtoG extends StoFeatPredicate {

	public KGtoG() {
		setName("kg-to-g");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kg", "g", "1e3",
				(String)args.get(1));
	}
}