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
public class MatchSigFigsFlash extends StoFeatPredicate {

	/**
	 * 
	 */
	public MatchSigFigsFlash() {
		setName("match-sig-figs-flash");
		setArity(2);
                setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		//SFNumber arg0 = (SFNumber)args.get(0);
		//SFNumber arg1 = (SFNumber)args.get(1);		
		String arg0 = (String)args.get(0);
		String arg1 = (String)args.get(1);		
		return hasCorrectSigFigsFlash(arg0, arg1);
	}

}
