package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;
public class MultExactFlash extends StoFeatPredicate {

	public MultExactFlash() {
		setName("mult-exact-flash");
		setArity(2);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
		setReturnValueType(TYPE_ARITH_EXP);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		SFNumber num1 = new SFNumber((String)args.get(0));
		SFNumber num2 = new SFNumber((String)args.get(1));		
		return multExactFlash(num1,num2).toString();
	}

}
