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
 * NO-MODEL
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Round extends StoFeatPredicate {

	/**
	 *  Round(x,y) rounds x to y sig figs
	 */
	public Round() {
		setName("round");
		setArity(2);
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		return round((String)args.get(0), (String)args.get(1));
	}

}
