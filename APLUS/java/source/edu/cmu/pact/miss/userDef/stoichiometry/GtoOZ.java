package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GtoOZ extends StoFeatPredicate {

	public GtoOZ() {
		setName("g-to-oz");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "g", "oz", "1",
				(String)args.get(1));
	}
}