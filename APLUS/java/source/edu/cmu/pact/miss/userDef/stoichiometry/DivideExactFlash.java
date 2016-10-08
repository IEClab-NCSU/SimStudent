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
public class DivideExactFlash extends StoFeatPredicate {

	/**
	 * 
	 */
	public DivideExactFlash() {
		setName("div-exact-flash");
		setArity(2);
		//setArgValueType(new int[]{TYPE_VALUE, TYPE_VALUE});
		//setReturnValueType(TYPE_VALUE);
	}

	/* (non-Javadoc)
	 * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
	 */
	public String apply(Vector args) {
		SFNumber num1 = new SFNumber((String)args.get(0));
		SFNumber num2 = new SFNumber((String)args.get(1));
		String result = divideExactFlash(num1,num2).toString();
		return result;
	}
}
