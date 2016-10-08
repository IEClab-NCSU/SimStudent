/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;

/**
 * Set a maximum search depth for the model tracing algorithm.
 */
public class SetMaximumChainDepth implements Serializable, Userfunction {
	
	/** For {@link Serializable}, a long with digits yyyymmddHHMM from the time this class was last edited. **/
	private static final long serialVersionUID = 201308311041L;
	
	/** Function name, as known to Jess. */
	private static final String FUNCTION_NAME = "set-maximum-chain-depth";

	/**
	 * Return the name of this function as registered with Jess.
	 * @return {@value #FUNCTION_NAME}
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return FUNCTION_NAME;
	}

	/**
	 * Set a maximum search depth for the model tracing algorithm.
	 * Calls {@link MTRete#setMaxDepth(int)} with the argument.
	 * @return prior value from {@link MTRete#getMaxDepth()}; returns -1 if
	 *         rule engine is not an instance of {@link MTRete}
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		int result = -1;  // default 
		if(context.getEngine() instanceof MTRete) {
			MTRete mtr = (MTRete) context.getEngine();
			result = mtr.getMaxDepth();
			String arg = "";
			if (vv.size() > 1) {
				int newDepth = vv.get(1).resolveValue(context).intValue(context);
				mtr.setMaxDepth(newDepth);
				arg = Integer.toString(newDepth);
			}
			if(trace.getDebugCode("mt"))
				trace.out("mt", "("+FUNCTION_NAME+" "+arg+") returns "+result);
		}
		return new Value(result, RU.INTEGER);
	}
}
