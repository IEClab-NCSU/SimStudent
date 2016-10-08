package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MGtoGDenominatorValue extends StoFeatPredicate {

	public MGtoGDenominatorValue() {
		setName("mg-to-g-denominator-value");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_ARITH_EXP);
	}

	public String apply(Vector args) {
		return "1000";
	}
}