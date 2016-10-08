package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcUnitDenominatorLtoML extends StoFeatPredicate {

	public UcUnitDenominatorLtoML() {
		setName("uc-unit-denominator-l-to-ml");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "mL";
	}
}