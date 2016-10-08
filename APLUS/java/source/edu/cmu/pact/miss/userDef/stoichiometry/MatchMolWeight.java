package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MatchMolWeight extends StoFeatPredicate {

	public MatchMolWeight() {
		setName("match-mol-weight");
		setArity(1);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_REASON});
	}

	public String apply(Vector args) {
		return matchReason((String)args.get(0), ReasonOperator.MOLWEIGHT);
	}

}
