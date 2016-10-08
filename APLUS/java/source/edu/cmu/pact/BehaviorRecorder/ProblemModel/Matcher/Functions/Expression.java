/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.VersionInformation;

/**
 * Abstraction of Expression class to hide differences between {@link cl.utilities.sm.SymbolManipulator}
 * and {@link edu.cmu.old_pact.cmu.sm.SymbolManipulator}. N.B.: we need a reference to the 
 * SymbolManipulator in here somewhere (even a comment) so that this file will be removed from the
 * non-CL versions of the CTAT jar.
 */
public class Expression {
	
	/** The underlying object, either {@link cl.utilities.sm.Expresion} or {@link edu.cmu.old_pact.cmu.sm.Expression}. */
	private final Object delegate;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/** Prevent use of default constructor. */
	private Expression() {
		delegate = null;
	}

	/**
	 * Constructor for use.
	 * @param delegate underlying object, an instance of either 
	 *                 {@link cl.utilities.sm.Expresion} or {@link edu.cmu.old_pact.cmu.sm.Expression}
	 */
	Expression(Object delegate) {
		this.delegate = delegate;
	}

	/**
	 * Access to {@link cl.utilities.sm.Expression#sort()}.
	 * @return new Expression instance returned by {@link #delegate}'s sort() method
	 * @throws Exception
	 */
	public Expression sort() throws Exception {
		try {
			if (VersionInformation.includesCL())
				return new Expression(((cl.utilities.sm.Expression) delegate).sort());
			else
				return new Expression(((edu.cmu.old_pact.cmu.sm.Expression) delegate).sort());
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * Access to {@link cl.utilities.sm.Expression#toIntermediateString()}.
	 * @return string returned by {@link #delegate}'s toIntermediateString() method
	 * @throws Exception
	 */
	public String toIntermediateString() throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.Expression) delegate).toIntermediateString();
			else
				return ((edu.cmu.old_pact.cmu.sm.Expression) delegate).toIntermediateString();
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}

	/**
	 * Access to {@link cl.utilities.sm.Expression#compute()}.
	 * @return numeric value returned by {@link #delegate}'s compute() method
	 * @throws Exception
	 */
	public double compute() throws Exception {
		try {
			if (VersionInformation.includesCL())
				return ((cl.utilities.sm.Expression) delegate).compute();
			else {
				edu.cmu.old_pact.cmu.sm.Expression expr =
					((edu.cmu.old_pact.cmu.sm.Expression) delegate).simplify();
				String result = expr.getStringValue();
				return Double.parseDouble(result);
			}
		} catch (Throwable t) {
			throw new Exception(t.getLocalizedMessage(), t.getCause() == null ? t : t.getCause());
		}
	}
}
