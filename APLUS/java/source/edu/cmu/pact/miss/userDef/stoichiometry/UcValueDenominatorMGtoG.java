package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcValueDenominatorMGtoG extends StoFeatPredicate {

	public UcValueDenominatorMGtoG() {
		setName("uc-value-denominator-mg-to-g");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_ARITH_EXP);
	}

	public String apply(Vector args) {
		return "1000";
	}
}
