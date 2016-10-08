package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcUnitDenominatorGtoMG extends StoFeatPredicate {

	public UcUnitDenominatorGtoMG() {
		setName("uc-unit-denominator-g-to-mg");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "mg";
	}
}