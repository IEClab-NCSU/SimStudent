/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import cl.utilities.sm.SMParserSettings;

/**
 * Test whether an algebraic expression can be simplified further.
 */
public class canSimplify {
	/**
	 * Print a usage message and exit.
	 * @param errorMsg if not null, diagnostic message to print before usage.
	 * @return never: calls {@link System#exit(int) System.exit(3)}
	 */
	private void usageExit(String errorMsg) {
		if (errorMsg != null && errorMsg.length() > 0)
			System.err.printf("Bad argument: %s. ", errorMsg);
		System.err.print("Usage:\n"+
				"   [java -cp classpath] "+getClass().getName()+" expr1 ...\n"+
				"where--\n"+
				"   expr1   is one expression,\n"+
				"   ...   are other expressions to test.\n"+
				"Exits with status 0 if all tests pass, 1 if any fails, 2 if error.\n");
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		canSimplify smt = new canSimplify();
		int result = smt.exec(args);
		System.exit(result == 0 ? 0 : (result < 0 ? 2 : 1));
	}
	
	/**
	 * Run a test.
	 * @param args command-line arguments: see {@link #usageExit(String)}
	 * @return result of individual test
	 */
	private int exec(String[] args) {
		int falseCount = 0, errorCount = 0;

		for (String expr : args) {
			try {
				boolean result = canSimplify(expr);
				if (!result)
					++falseCount;
				System.out.printf("%-5s canSimplify(%s)\n", result, expr);
			} catch (Exception e) {
				errorCount++;
			}
		}
		return (errorCount < 0 ? errorCount : falseCount);
	}

	/**
	 * <p>Test whether an algebraic expression can be simplified further.</p>
	 * @param expr expression to test 
	 * @return true if can simplify further, else false. 
	 */
	public boolean canSimplify(String expr) throws Exception {
		return internalCanSimplify(expr);
	}

	/**
	 * Test {@link cl.utilities.sm.SymbolManipulator#canSimplify(String, String)}
	 * @param expr expression to test 
	 * @return true if can simplify further, else false. 
	 */
	static boolean internalCanSimplify(String expr) throws Exception {
		return internalCanSimplify(expr, null);
	}

	/**
	 * Test {@link cl.utilities.sm.SymbolManipulator#canSimplify(String, String)}
	 * @param expr expression to test 
	 * @param parserSettings if not null, argument for
	 *        {@link cl.utilities.sm.SymbolManipulator#setParserSettings(SMParserSettings)} 
	 * @return true if can simplify further, else false. 
	 */
	static boolean internalCanSimplify(String expr, Object parserSettings)
			throws Exception {
		if (expr == null || expr.length() < 1)
			throw new IllegalArgumentException("null or empty expression: "+expr);
		try {
			SymbolManipulator sm =
				(parserSettings != null ? new SymbolManipulator(parserSettings) : new SymbolManipulator());
			return sm.canSimplify(expr);
		} catch (Exception e) {
			String errMsg = "Error from canSimplify("+expr+"): "+e;
            e.printStackTrace(System.err);
			System.err.println(errMsg +
                               (e.getCause() == null ? "" : "; cause " + e.getCause().toString()));
			throw new RuntimeException(errMsg);
		}
	}
}
