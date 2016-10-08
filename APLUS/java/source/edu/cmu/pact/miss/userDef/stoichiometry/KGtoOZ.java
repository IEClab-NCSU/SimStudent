package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KGtoOZ extends StoFeatPredicate {

	public KGtoOZ() {
		setName("kg-to-oz");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kg", "oz", "3.52740e1",
				(String)args.get(1));
	}
}