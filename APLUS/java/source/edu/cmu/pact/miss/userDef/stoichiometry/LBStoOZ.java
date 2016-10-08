package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LBStoOZ extends StoFeatPredicate {

	public LBStoOZ() {
		setName("lbs-to-oz");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "lbs", "oz", "1.6e1",
				(String)args.get(1));
	}
}