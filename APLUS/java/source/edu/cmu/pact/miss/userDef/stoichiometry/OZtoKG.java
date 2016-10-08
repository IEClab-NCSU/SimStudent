package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class OZtoKG extends StoFeatPredicate {

	public OZtoKG() {
		setName("oz-to-kg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "oz", "kg", "1",
				(String)args.get(1));
	}
}