package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchUnit extends StoFeatPredicate {

	public MatchUnit() {
		setName("match-unit");
		setArity(2);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_TUPLE, StoFeatPredicate.TYPE_TUPLE});
	}

	public String apply(Vector args) {
		return matchUnit((String)args.get(0), (String)args.get(1));
	}
	
}
