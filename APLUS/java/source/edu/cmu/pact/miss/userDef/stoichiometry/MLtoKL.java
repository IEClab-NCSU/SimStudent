package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MLtoKL extends StoFeatPredicate {

	public MLtoKL() {
		setName("mL-to-kL");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "mL", "kL", "1",
				(String)args.get(1));
	}
}