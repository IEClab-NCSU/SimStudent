package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class OZtoLBS extends StoFeatPredicate {

	public OZtoLBS() {
		setName("oz-to-lbs");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "oz", "lbs", "1",
				(String)args.get(1));
	}
}