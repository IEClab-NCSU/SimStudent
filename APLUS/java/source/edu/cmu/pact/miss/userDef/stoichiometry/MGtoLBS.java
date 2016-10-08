package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MGtoLBS extends StoFeatPredicate {

	public MGtoLBS() {
		setName("mg-to-lbs");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mg", "lbs", "1",
				(String)args.get(1));
	}
}