package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class OZtoG extends StoFeatPredicate {

	public OZtoG() {
		setName("oz-to-g");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "oz", "g", "3.11e1",
				(String)args.get(1));
	}
}