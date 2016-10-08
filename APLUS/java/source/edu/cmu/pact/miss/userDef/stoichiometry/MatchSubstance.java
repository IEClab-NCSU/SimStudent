package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchSubstance extends StoFeatPredicate {

	public MatchSubstance() {
		setName("match-substance");
		setArity(2);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_TUPLE, StoFeatPredicate.TYPE_TUPLE});
	}

	public String apply(Vector args) {
		return matchSubstance((String)args.get(0), (String)args.get(1));
	}
	
}
