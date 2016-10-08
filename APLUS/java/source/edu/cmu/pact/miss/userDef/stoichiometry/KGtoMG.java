package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KGtoMG extends StoFeatPredicate {

	public KGtoMG() {
		setName("kg-to-mg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kg", "mg", "1e6",
				(String)args.get(1));
	}
}