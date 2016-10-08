/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Test whether an algebraic expression can be simplified further.
 */
public class isSimplifiedMixed {

	/**
	 * <p>Test whether an algebraic expression can be simplified further.</p>
	 * @param expr expression to test 
	 * @return false if can simplify further, else false. 
	 */
	public boolean isSimplifiedMixed (String expr) throws Exception {
		return !canSimplify.internalCanSimplify(expr, SymbolManipulator.Settings.MIXED_NUMBERS_E_AS_VAR);
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
		isSimplifiedMixed smt = new isSimplifiedMixed();
		int result = smt.exec(args);
		if (result < 0)
			System.out.println("\nErrors on "+(-result)+" expressions.");
		else if (result > 0)
			System.out.println("\nisSimplifiedMixed() false on "+result+" expressions.");
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
				boolean result = isSimplifiedMixed(expr);
				if (!result)
					++falseCount;
				System.out.printf("%-5s isSimplifiedMixed(%s)\n", result, expr);
			} catch (Exception e) {
				errorCount--;
			}
		}
		return (errorCount < 0 ? errorCount : falseCount);
	}
}
