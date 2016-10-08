/*
 * Created on Jul 17, 2006
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
public class ContainsSubstanceFlash extends StoFeatPredicate {

	/**
	 * 
	 */
	public ContainsSubstanceFlash() {
		setName("contains-sub-flash");
		setArity(2);
		setArgValueType(new int[] {StoFeatPredicate.TYPE_SUBSTANCE, StoFeatPredicate.TYPE_SUBSTANCE}); 
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		return containsSubstanceFlash((String)args.get(0), (String)args.get(1));
	}

}
