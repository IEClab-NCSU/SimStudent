package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class SetSubstance extends StoFeatPredicate {

	public SetSubstance() {
		setName("set-substance");
		setArity(3);
	}

	public String apply(Vector args) {
		return setSubstance((String)args.get(0), (String)args.get(1),
				(String)args.get(2));
	}
}
