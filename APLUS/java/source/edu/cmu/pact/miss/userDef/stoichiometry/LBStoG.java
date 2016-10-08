package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LBStoG extends StoFeatPredicate {

	public LBStoG() {
		setName("lbs-to-g");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "lbs", "g", "4.536e2",
				(String)args.get(1));
	}
}