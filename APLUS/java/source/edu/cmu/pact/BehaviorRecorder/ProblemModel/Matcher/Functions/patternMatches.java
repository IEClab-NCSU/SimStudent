/**
 * Copyright 2010 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cl.utilities.sm.SMParserSettings;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class patternMatches {
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
				"   expr1 is one expression,\n"+
				"   ...   are other expressions to test.\n"+
				"Exits with status 0 if test passes, 1 if fails, 2 if error, 3 if bad argument.\n");
		System.exit(3);
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		patternMatches smt = new patternMatches();
		System.exit(smt.exec(args));
	}
	
	/**
	 * Run a test.
	 * @param args command-line arguments: see {@link #usageExit(String)}
	 * @return result of individual test
	 */
	private int exec(String[] args) {
		if (args.length < 2 || args[0].toLowerCase().contains("help"))
			usageExit(null);
		List<String> exprList = Arrays.asList(args);
		int result = 2;  // error status
		result = patternMatches(exprList.get(0), exprList.subList(1, exprList.size()));
        trace.out(result);
		return result;
	}
	
	/**
	 * <p>Test whether a given polynomials has the same terms as any of a list of others.</p>
	 * @param expr0 expression to test against elements of exprList
	 * @param otherExprs 1 or more other expressions, as Strings
	 * @return true if expr0 algebraically matches any one of otherExprs
	 */
	public boolean patternMatches(String expr0, String... otherExprs) {
		List<String> exprList = new ArrayList<String>();
		for (String expr: otherExprs)
			exprList.add(expr);
		int result = patternMatches(expr0, exprList);
		return (result == 0);
	}

	/**
	 * Use {@link SymbolManipulator} to test whether polynomials have the same terms.
	 * @param expr0 expression to test against elements of exprList
	 * @param exprList 1 or more other expressions, as Strings
	 * @return 0 if expr0 matches at least one of the other exprList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int patternMatches(String expr0, List<String> exprList) {
		if (exprList.size() < 1)
			usageExit("patternMatches requires at least 1 other expression argument");
		try {
			if (VersionInformation.includesCL()) {
				cl.utilities.sm.SymbolManipulator sm =
					new cl.utilities.sm.SymbolManipulator(SMParserSettings.MIXED_NUMBERS_E_AS_CONST);
				for (String expr1: exprList) {
					if (sm.patternMatches(expr0, expr1))
						return 0;
				}
			} else {
				edu.cmu.old_pact.cmu.sm.SymbolManipulator sm =
					new edu.cmu.old_pact.cmu.sm.SymbolManipulator();
				for (String expr1: exprList) {
					if (sm.patternMatches(expr0, expr1))
						return 0;
				}
			}
			return 1;
		} catch (Exception e) {
            e.printStackTrace(System.err);
			System.err.println("Error from patternMatches \"" + expr0 + "\"" + e +
                               (e.getCause() == null ? "" : "; cause " + e.getCause().toString()));
			return 2;
		}
	}
}
