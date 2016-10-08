/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Simplify an algebraic expression possibly containing mixed numbers.
 */
public class simplifyMixed {
	
	/** Save state in case we can reuse. */
	private SymbolManipulator sm = null;
	
	/**
	 * No-arg constructor needed for Functions.
	 */
	public simplifyMixed() {
		sm = new SymbolManipulator(SymbolManipulator.Settings.MIXED_NUMBERS_E_AS_VAR);
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
		simplifyMixed smt = new simplifyMixed();
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
//		System.out.println(sm.getStateDescription());
		
		for (; i < exprs.length; ++i) {
			try {
				if (testName == null || testName.equalsIgnoreCase("simplify"))
					System.out.printf("%-20s = %s\n", exprs[i], simplifyMixed(exprs[i]));
				
				else if (testName.equalsIgnoreCase("mixed"))
					System.out.printf("%-20s = %s\n", exprs[i], sm.convertMixedToImproper(exprs[i]));
				
				else if (testName.equalsIgnoreCase("simplifymixed")) {
					System.out.printf("%-20s = %s\n", exprs[i],	simplifyMixed(exprs[i]));					
				}
				else {
					usageExit("unknown test name \""+testName+"\"");
					break;
				}
			} catch (Exception e) {
				System.out.printf("exception on expression[%d] \"%s\": %s\n",
						i, exprs[i], e.toString());
			}
		}
	}

	/**
	 * <p>Evaluate an algebraic expression possibly containing mixed numbers.</p>
	 * @param expr expression to simplify
	 * @return simplified result; if a fraction, will be an improper fraction in lowest terms
	 */
	public String simplifyMixed(String expr) {
		try {
			String improper = sm.convertMixedToImproper(expr);
			String result = sm.simplify(improper);
			if (trace.getDebugCode("si")) trace.out("si", "simplifyMixed result "+result+" on\n  \""+expr+"\"");
			return result;               // mismatch on all
		} catch (Exception e) {
			trace.err("Error from simplifyMixed(\""+expr+"\"): "+e+
					(e.getCause() == null ? "" : "; cause "+ e.getCause().toString()));
			return null;
		}
	}
}
