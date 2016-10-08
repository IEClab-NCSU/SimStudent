/*
 * Created on Jul 20, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.miss.userDef.stoichiometry;

import java.util.Vector;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DivideExact extends StoFeatPredicate {

	/**
	 * 
	 */
	public DivideExact() {
		setName("div-exact");
		setArity(2);
		setReturnValueType(StoFeatPredicate.TYPE_ARITH_EXP);
		setArgValueType(new int[]{StoFeatPredicate.TYPE_ARITH_EXP, StoFeatPredicate.TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		return divideExact((String)args.get(0), (String)args.get(1));
	}

}
