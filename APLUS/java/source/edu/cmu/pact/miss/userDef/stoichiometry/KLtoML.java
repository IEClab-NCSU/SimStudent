package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class KLtoML extends StoFeatPredicate {

	public KLtoML() {
		setName("kL-to-mL");
		setArity(2);
	}

	public String apply(Vector args) {
		return convertUnit((String)args.get(0), "kL", "mL", "1e6",
				(String)args.get(1));
	}
}