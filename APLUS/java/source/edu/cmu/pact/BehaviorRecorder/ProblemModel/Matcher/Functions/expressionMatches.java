package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.ArrayList;
import java.util.List;

/**
 * Test whether the first expression argument is algebraically the same as any of the others.
 */
public class expressionMatches {
	/**
	 * Print a usage message and exit.
	 * @param errorMsg if not null, diagnostic message to print before usage.
	 * @return never: calls {@link System#exit(int) System.exit(3)}
	 */
	private void usageExit(String errorMsg) {
		if (errorMsg != null && errorMsg.length() > 0)
			System.err.printf("Bad argument: %s. ", errorMsg);
		System.err.print("Usage:\n"+
				"   [java -cp classpath] "+getClass().getName()+" -test expr1 ...\n"+
				"where--\n"+
				"   test  is one of {expressionMatches},\n"+
				"   expr1   is one expression,\n"+
				"   ...   are other expressions to test.\n"+
				"Exits with status 0 if test passes, 1 if fails, 2 if error, 3 if bad argument.\n");
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		expressionMatches smt = new expressionMatches();
		System.exit(smt.exec(args));
	}
	
	/**
	 * Run a test.
	 * @param args command-line arguments: see {@link #usageExit(String)}
	 * @return result of individual test
	 */
	private int exec(String[] args) {
		int i;
		String testName = null;
		List<String> exprList = new ArrayList<String>();
		int result = 2;  // error status
		
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			testName = args[i].substring(1);
		}
		for (; i < args.length; ++i)
			exprList.add(new String(args[i]));
		if ("expressionMatches".equalsIgnoreCase(testName))
			result = expressionMatches(exprList);
		else
			usageExit("unknown test name \""+testName+"\"");
        System.out.println(result);
		return result;
	}
	
	/**
	 * <p>Test whether the first expression argument is algebraically the same as any of the others.</p>
	 * Uses {@link SymbolManipulator#expressionMatches(String, String)}.
	 * @param expr0 expression to test against elements of exprList
	 * @param otherExprs 1 or more other expressions, as Strings
	 * @return true i expr0 algebraically matches any one of otherExprs
	 */
	public boolean expressionMatches(String expr0, String... otherExprs) {
		List<String> exprList = new ArrayList();
		for (String expr: otherExprs)
			exprList.add(expr);
		int result = expressionMatches(expr0, exprList);
		return (result == 0);
	}

	/**
	 * Test {@link SymbolManipulator#expressionMatches(String, String)}
	 * @param exprList at least 2 expressions, as Strings
	 * @return 0 if exprList[0] matches at least one of the other exprList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int expressionMatches(List<String> exprList) {
		return expressionMatches(exprList.get(0), exprList.subList(1, exprList.size()));
	}

	/**
	 * Test {@link SymbolManipulator#expressionMatches(String, String)}
	 * @param expr0 expression to test against elements of exprList
	 * @param exprList 1 or more other expressions, as Strings
	 * @return 0 if exprList[0] matches at least one of the other exprList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int expressionMatches(String expr0, List<String> exprList) {
		if (exprList.size() < 1)
			usageExit("test expressionMatches requires at least 1 other expression argument");
		try {
			SymbolManipulator sm = new SymbolManipulator();
			return sm.expressionMatches(expr0, exprList);
		} catch (Exception e) {
            e.printStackTrace(System.err);
			System.err.println("Error from expressionMatches \"" + expr0 + "\"" + e +
                               (e.getCause() == null ? "" : "; cause " + e.getCause().toString()));
			return 2;
		}
	}

}
