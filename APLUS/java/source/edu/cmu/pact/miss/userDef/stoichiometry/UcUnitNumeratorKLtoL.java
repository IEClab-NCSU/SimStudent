package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcUnitNumeratorKLtoL extends StoFeatPredicate {

	public UcUnitNumeratorKLtoL() {
		setName("uc-unit-numerator-kl-to-l");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "kL";
	}
}