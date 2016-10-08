package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchUnitFlash extends StoFeatPredicate {

	public MatchUnitFlash() {
		setName("match-unit-flash");
		setArity(2);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_UNIT, StoFeatPredicate.TYPE_UNIT});
	}

	public String apply(Vector args) {
		return matchUnitFlash((String)args.get(0), (String)args.get(1));
	}
	
}
