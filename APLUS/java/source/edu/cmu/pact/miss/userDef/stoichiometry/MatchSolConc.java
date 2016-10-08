package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchSolConc extends StoFeatPredicate {

	public MatchSolConc() {
		setName("match-sol-conc");
		setArity(1);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_REASON});
	}

	public String apply(Vector args) {
		return matchReason((String)args.get(0), ReasonOperator.SOLCONC);
	}

}
