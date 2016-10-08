package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class UcValueDenominatorKLtoL extends StoFeatPredicate {

	public UcValueDenominatorKLtoL() {
		setName("uc-value-denominator-kl-to-l");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_ARITH_EXP);
	}

	public String apply(Vector args) {
		return "1000";
	}
}
