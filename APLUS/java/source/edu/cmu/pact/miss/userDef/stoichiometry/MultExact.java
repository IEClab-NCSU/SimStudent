package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;
public class MultExact extends StoFeatPredicate {

	/**
	 * 
	 */
	public MultExact() {
		setName("mult-exact");
		setArity(2);
		setReturnValueType(StoFeatPredicate.TYPE_ARITH_EXP);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_ARITH_EXP, StoFeatPredicate.TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		return multExact((String)args.get(0), (String)args.get(1));
	}

}
