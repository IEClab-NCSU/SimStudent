package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GtoLBS extends StoFeatPredicate {

	public GtoLBS() {
		setName("g-to-lbs");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "g", "lbs", "1",
				(String)args.get(1));
	}
}