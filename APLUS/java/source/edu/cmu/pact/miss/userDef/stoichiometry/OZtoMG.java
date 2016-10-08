package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class OZtoMG extends StoFeatPredicate {

	public OZtoMG() {
		setName("oz-to-mg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "oz", "mg", "3.11e4",
				(String)args.get(1));
	}
}