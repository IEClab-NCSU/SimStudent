package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LtoML extends StoFeatPredicate {

	public LtoML() {
		setName("L-to-mL");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "L", "mL", "1e3",
				(String)args.get(1));
	}
}