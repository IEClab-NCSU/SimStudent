package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KLtoL extends StoFeatPredicate {

	public KLtoL() {
		setName("kL-to-L");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kL", "L", "1e3",
				(String)args.get(1));
	}
}