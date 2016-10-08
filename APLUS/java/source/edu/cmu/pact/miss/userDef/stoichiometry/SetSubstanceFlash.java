package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

import edu.cmu.pact.Utilities.trace;

public class SetSubstanceFlash extends StoFeatPredicate {

	public SetSubstanceFlash() {
		setName("set-substance-flash");
		setArity(1);
		setArgValueType(new int[]{TYPE_SUBSTANCE});		
		setReturnValueType(TYPE_SUBSTANCE);
	}

	public String apply(Vector args) {
                trace.outln("rhs", "SetSubstanceFlash.apply(): arguments = ["+ (String)args.get(0)+"]");
		return setSubstanceFlash((String)args.get(0));
	}
}
