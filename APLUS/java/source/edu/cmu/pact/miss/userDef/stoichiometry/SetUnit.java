package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetUnit extends StoFeatPredicate {

	public SetUnit() {
		setName("set-unit");
		setArity(3);
	}

	public String apply(Vector args) {
		return setUnit((String)args.get(0), (String)args.get(1),
				(String)args.get(2));
	}
}
