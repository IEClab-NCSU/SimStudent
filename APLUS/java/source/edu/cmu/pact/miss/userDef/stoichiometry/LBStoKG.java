package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LBStoKG extends StoFeatPredicate {

	public LBStoKG() {
		setName("lbs-to-kg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "lbs", "kg", "1",
				(String)args.get(1));
	}
}