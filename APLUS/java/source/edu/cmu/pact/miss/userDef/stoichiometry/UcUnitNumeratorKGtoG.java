package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcUnitNumeratorKGtoG extends StoFeatPredicate {

	public UcUnitNumeratorKGtoG() {
		setName("uc-unit-denominator-kg-to-g");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
		return "g";
	}
}