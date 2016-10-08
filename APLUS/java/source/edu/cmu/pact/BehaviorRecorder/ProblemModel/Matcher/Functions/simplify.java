/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Simplify an algebraic expression.
 */
public class simplify {
	
	/** Save state in case we can reuse. */
	private SymbolManipulator sm = null;
	
	/**
	 * No-arg constructor needed for Functions.
	 */
	public simplify() {
		sm = new SymbolManipulator();
	}

	/**
	 * Print a usage message and exit.
	 * @param errorMsg if not null, diagnostic message to print before usage.
	 * @return never: calls {@link System#exit(int) System.exit(3)}
	 */
	private void usageExit(String errorMsg) {
		if (errorMsg != null && errorMsg.length() > 0)
			System.err.printf("Bad argument: %s. ", errorMsg);
		System.err.print("Usage:\n"+
				"   [java -cp classpath] "+getClass().getName()+" -test eq1 ...\n"+
				"where--\n"+
				"   test  is one of {simplify},\n"+
				"   eq1   is one expression to convert,\n"+
				"   ...   are other expressions to convert.\n");
		System.exit(3);
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		simplify smt = new simplify();
		smt.exec(args);
	}
	
	/**
	 * Run a test.
	 * @param exprs command-line arguments: see {@link #usageExit(String)}
	 * @return result of individual test
	 */
	private void exec(String[] exprs) {
		String testName = null;
		int i = 0;
		for (i = 0; i < exprs.length && exprs[i].charAt(0) == '-'; ++i) {
			testName = exprs[i].substring(1);
		}
		if (!"simplify".equalsIgnoreCase(testName))
			usageExit("unknown test name \""+testName+"\"");
		for (; i < exprs.length; ++i)
			System.out.printf("%-20s = %s\n", exprs[i], simplify(exprs[i]));
	}

	/**
	 * <p>Simplify an algebraic expression.</p>
	 * Uses {@link SymbolManipulator#simplify(String)}
	 * @param expr expression to simplify
	 * @return simplified result
	 */
	public String simplify(String expr) {
		try {
			String result = sm.simplify(expr);
			if (trace.getDebugCode("si")) trace.out("si", "simplify result "+result+" on\n  \""+expr+"\"");
			return result;               // mismatch on all
		} catch (Exception e) {
			System.err.println("Error from simplify(\""+expr+"\"): "+e+
					(e.getCause() == null ? "" : "; cause "+ e.getCause().toString()));
			return null;
		}
	}
}
