package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class UcUnitDenominatorKLtoL extends StoFeatPredicate {

	public UcUnitDenominatorKLtoL() {
		setName("uc-unit-denominator-kl-to-l");
		setArity(1);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_REASON});
		setReturnValueType(TYPE_UNIT);
	}

	public String apply(Vector args) {
            trace.outln("rhs", "UcUnitDenominatorKLtoL.apply(): arguments = ["+ (String)args.get(0)+"]");
		return "L";
	}
}