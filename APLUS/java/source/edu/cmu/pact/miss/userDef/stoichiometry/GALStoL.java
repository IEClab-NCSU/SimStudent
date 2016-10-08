package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GALStoL extends StoFeatPredicate {

	public GALStoL() {
		setName("gals-to-L");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "gals", "L", "3.7854",
					(String)args.get(1));
	}
}