package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KGtoLBS extends StoFeatPredicate {

	public KGtoLBS() {
		setName("kg-to-lbs");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kg", "lbs", "2.2046",
				(String)args.get(1));
	}
}