package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetNumber extends StoFeatPredicate {

	public SetNumber() {
		setName("set-number");
		setArity(3);
	}

	public String apply(Vector args) {
		return setNumber((String)args.get(0), (String)args.get(1),
				(String)args.get(2));
	}
}
