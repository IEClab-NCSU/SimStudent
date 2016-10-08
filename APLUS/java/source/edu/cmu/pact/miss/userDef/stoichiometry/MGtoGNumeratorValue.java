package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MGtoGNumeratorValue extends StoFeatPredicate {

	public MGtoGNumeratorValue() {
		setName("mg-to-g-numerator-value");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_ARITH_EXP);
	}

	public String apply(Vector args) {
		return "1";
	}
}