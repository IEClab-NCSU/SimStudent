package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchSubstanceFlash extends StoFeatPredicate {

	public MatchSubstanceFlash() {
		setName("match-substance-flash");
		setArity(2);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_SUBSTANCE, StoFeatPredicate.TYPE_SUBSTANCE});
	}

	public String apply(Vector args) {
		return matchSubstanceFlash((String)args.get(0), (String)args.get(1));
	}
	
}
