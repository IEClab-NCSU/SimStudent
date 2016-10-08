package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MLtoL extends StoFeatPredicate {

	public MLtoL() {
		setName("mL-to-L");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mL", "L", "1",
				(String)args.get(1));
	}
}