package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LBStoMG extends StoFeatPredicate {

	public LBStoMG() {
		setName("lbs-to-mg");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "lbs", "mg", "4.536e5",
				(String)args.get(1));
	}
}