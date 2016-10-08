package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcUnitNumeratorLtoML extends StoFeatPredicate {

	public UcUnitNumeratorLtoML() {
		setName("uc-unit-numerator-l-to-ml");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "L";
	}
}