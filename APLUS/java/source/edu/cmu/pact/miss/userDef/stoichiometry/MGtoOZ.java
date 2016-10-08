package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MGtoOZ extends StoFeatPredicate {

	public MGtoOZ() {
		setName("mg-to-oz");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mg", "oz", "1",
				(String)args.get(1));
	}
}