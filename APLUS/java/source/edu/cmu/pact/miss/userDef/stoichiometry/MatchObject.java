package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchObject extends StoFeatPredicate {

	public MatchObject() {
		setName("match-object");
		setArity(2);
		setArgValueType(new int[] {TYPE_OBJECT, TYPE_OBJECT});
	}

	public String apply(Vector args) {
		return matchObject((String)args.get(0), (String)args.get(1));
	}
}
	