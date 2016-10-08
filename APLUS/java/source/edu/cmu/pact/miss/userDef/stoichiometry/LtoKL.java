package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class LtoKL extends StoFeatPredicate {

	public LtoKL() {
		setName("L-to-kL");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "L", "kL", "1",
				(String)args.get(1));
	}
}