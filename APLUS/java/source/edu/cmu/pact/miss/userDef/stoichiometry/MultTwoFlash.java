package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

public class MultTwoFlash extends StoFeatPredicate {

	public MultTwoFlash() {
		setName("mult-two-flash");
		setArity(2);
		this.setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP} );
		setReturnValueType(TYPE_ARITH_EXP);
	}

	public String apply(Vector args) {
		SFNumber num1 = new SFNumber((String)args.get(0));
		SFNumber num2 = new SFNumber((String)args.get(1));		
		return multTwoTermsFlash(num1,num2).toString();
	}

}
