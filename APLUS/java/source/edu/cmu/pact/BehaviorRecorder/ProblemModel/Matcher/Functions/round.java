/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 * Returns the closest integer to the argument.
 */
public class round {

	/**
	 * <p>Returns the closest integer to the argument.</p>
	 * @param o value to round
	 * @return closest integer
	 */
    public long round(Object o) {
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "round(Object "+o+")");
        if (o == null)
        	throw new IllegalArgumentException("null argument to round(Object)");
    	Double od = CTATFunctions.toDouble(o);
      	if (od != null)
      		return Math.round(od);
      	else
      		throw new IllegalArgumentException("bad argument to round(Object): "+o);
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		round r = new round();
		for (String arg : args)
			System.out.printf("round(%10s) = %d\n", arg, r.round(arg));
	}

}
