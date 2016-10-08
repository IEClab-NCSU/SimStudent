/**
 * Copyright 2010 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Evaluate the given expression as a desk calculator would. Uses {@link cl.utilities.sm.Expression#compute()}.
 */
public class calca {
	
	/** Mark this class as (indirectly) using the SymbolManipulator. */
	private static final SymbolManipulator marker = null;  // do not remove

	/**
	 * <p>Evaluate the given expression as a desk calculator would.</p>
	 * Uses {@link cl.utilities.sm.Expression#compute()}.
	 * Like {@link calc#calc(String)}, but calls {@link fmtNormal#fmtNormal(double)} on the result.
	 * Handles all exceptions: returns an empty string.
	 * @param expr arithmetic expression to compute
	 * @return calculated value, formatted as by {@link fmtNormal#fmtNormal(double)}
	 */
	public String calca(String expr) {
		try {
			double d = (new calc()).calc(expr);
			return (new fmtNormal()).fmtNormal(d);
		} catch (Exception e) {
			trace.err("calca(\""+expr+"\") error: "+e);
			return "";
		}
	}
	
	/**
	 * @param args expressions to compute
	 */
	public static void main(String[] args) {
		calca c = new calca();
		for (String expr : args) {
			try {
				System.out.printf("%-12s = %s\n", expr, c.calca(expr));
			} catch (Exception e) {
				;  // handled below
			}
		}
	}

}
