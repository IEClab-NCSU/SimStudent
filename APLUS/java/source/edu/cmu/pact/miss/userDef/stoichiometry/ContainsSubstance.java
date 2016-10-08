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
public class ContainsSubstance extends StoFeatPredicate {

	/**
	 * 
	 */
	public ContainsSubstance() {
		setName("contains-sub");
		setArity(2);
                setArgValueType(new int[] {StoFeatPredicate.TYPE_TUPLE, StoFeatPredicate.TYPE_TUPLE}); 
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		return containsSubstance((String)args.get(0), (String)args.get(1));
	}

}
