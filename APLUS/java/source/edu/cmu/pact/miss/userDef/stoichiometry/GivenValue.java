package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class GivenValue extends ReasonOperator {

	public GivenValue() {
		setName("given-value");
		setArity(1);
		setReturnValueType(StoFeatPredicate.TYPE_REASON);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_ARITH_EXP});
	}

	public String apply(Vector args) {
//                trace.outln("rhs", "GivenValue.apply(): arguments = ["+ (String)args.get(0)+"]");
		return supplyReason((String)args.get(0), ReasonOperator.GIVEN);
	}
}
