package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.pact.Utilities.trace;

/**
 * Test whether a given polynomials has the same terms as any of a list of others.
 */
public class polyTermsEqual {
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
		polyTermsEqual smt = new polyTermsEqual();
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
		result = polyTermsEqual(exprList.get(0), exprList.subList(1, exprList.size()));
        trace.out(result);
		return result;
	}
	
	/**
	 * <p>Test whether a given polynomials has the same terms as any of a list of others.</p>
	 * @param expr0 expression to test against elements of exprList
	 * @param otherExprs 1 or more other expressions, as Strings
	 * @return true if expr0 algebraically matches any one of otherExprs
	 */
	public boolean polyTermsEqual(String expr0, String... otherExprs) {
		List<String> exprList = new ArrayList<String>();
		for (String expr: otherExprs)
			exprList.add(expr);
		int result = polyTermsEqual(expr0, exprList);
		return (result == 0);
	}

	/**
	 * Use {@link SymbolManipulator} to test whether polynomials have the same terms.
	 * @param expr0 expression to test against elements of exprList
	 * @param exprList 1 or more other expressions, as Strings
	 * @return 0 if expr0 matches at least one of the other exprList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int polyTermsEqual(String expr0, List<String> exprList) {
		if (exprList.size() < 1)
			usageExit("polyTermsEqual requires at least 1 other expression argument");
		try {
			SymbolManipulator sm = new SymbolManipulator();
            Expression expression0 = sm.parseCE(expr0);
			Expression sortedEx0 = expression0.sort();
            String sortedStr0 = sortedEx0.toIntermediateString();
			for (String expr1: exprList) {
				Expression expression1 = sm.parseCE(expr1);
				String sortedStr1 = expression1.sort().toIntermediateString();
				boolean result = sortedStr0.equals(sortedStr1);
                if (trace.getDebugCode("si")) trace.out("si", "\"" + sortedStr0 + "\"" + (result ? "matches" : "doesn't match") + "\"" + sortedStr1 + "\"");
				if (result)
					return 0;       // matches exprList[i]
			}
            return 1;               // mismatch on all
		} catch (Exception e) {
            e.printStackTrace(System.err);
			System.err.println("Error from polyTermsEqual \"" + expr0 + "\"" + e +
                               (e.getCause() == null ? "" : "; cause " + e.getCause().toString()));
			return 2;
		}
	}
}
