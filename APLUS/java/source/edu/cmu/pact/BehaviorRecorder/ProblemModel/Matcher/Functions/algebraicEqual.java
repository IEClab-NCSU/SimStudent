package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.pact.Utilities.trace;

/**
 * Test whether a given expression is algebraically equal to any of 
 * a list of other expressions.
 */
public class algebraicEqual {

	private static final String DEFAULT_FUNCTION = "algebraicEqual";

	/**
	 * Print a usage message and exit.
	 * @param errorMsg if not null, diagnostic message to print before usage.
	 * @return never: calls {@link System#exit(int) System.exit(3)}
	 */
	private void usageExit(String errorMsg) {
		if (errorMsg != null && errorMsg.length() > 0)
			System.err.printf("Bad argument: %s. ", errorMsg);
		System.err.print("Usage:\n"+
				"   [java -cp classpath] "+getClass().getName()+" eq1 ...\n"+
				"where--\n"+
				"   eq1   is one expression,\n"+
				"   ...   are other expressions to test.\n"+
				"Exits with status 0 if test passes, 1 if fails, 2 if error, 3 if bad argument.\n");
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		algebraicEqual smt = new algebraicEqual();
		System.exit(smt.exec(args));
	}
	
	/**
	 * Run a test.
	 * @param args command-line arguments: see {@link #usageExit(String)}
	 * @return result of individual test
	 */
	private int exec(String[] args) {
		int i;
		String testName = DEFAULT_FUNCTION;
		List<String> eqList = new ArrayList<String>();
		int result = 2;  // error status
		
		for (i = 0; i < args.length; ++i)
			eqList.add(new String(args[i]));
		if (DEFAULT_FUNCTION.equalsIgnoreCase(testName))
			trace.out("Result: "+(0 < (result = algebraicEqual(eqList))));
		else
			usageExit("unknown test name \""+testName+"\"");
		return result;
	}
	
	/**
	 * <p>Test whether a given expression is algebraically equal to any of a list of other expressions.</p>
	 * Uses {@link SymbolManipulator#algebraicEqual(String, String)}.
	 * @param eq0 expression to test against elements of eqList
	 * @param otherEqs 1 or more other expressions, as Strings
	 * @return true i eq0 algebraically matches any one of otherEqs
	 */
	public boolean algebraicEqual(String eq0, String... otherEqs) {
		List<String> eqList = new ArrayList();
		for (String eq: otherEqs)
			eqList.add(eq);
		int result = algebraicEqual(eq0, eqList);
		return (result > 0);
	}

	/**
	 * Test {@link SymbolManipulator#algebraicEqual(String, String)}
	 * @param eqList at least 2 expressions, as Strings
	 * @return 0 if eqList[0] matches at least one of the other eqList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int algebraicEqual(List<String> eqList) {
		return algebraicEqual(eqList.get(0), eqList.subList(1, eqList.size()));
	}

	/**
	 * Test {@link SymbolManipulator#algebraicEqual(String, String)}
	 * @param eq0 expression to test against elements of eqList
	 * @param eqList 1 or more other expressions, as Strings
	 * @return number of other eqList[] elements that eqList[0] matches;
	 * 			0 if it matches none of them, -1 if error
	 */
	private int algebraicEqual(String eq0, List<String> eqList) {
		String eq1 = null;
		int i = -1;
		int count = 0;
		if (eqList.size() < 1)
			usageExit("test algebraicEqual requires at least 1 other expression argument");
		try {
			SymbolManipulator sm = new SymbolManipulator();
			for (i = 0; i < eqList.size(); ++i) {
				eq1 = eqList.get(i);
				boolean result = sm.algebraicEqual(eq0, eq1);
				if (trace.getDebugCode("si")) trace.out("si", "algebraicEqual["+i+"] result "+result+
						" on \""+eq0+"\" vs \""+eq1+"\"");
				if (result)
					++count;       // matches eqList[i]
			}
			return count;
		} catch (Exception e) {
			System.err.println("Error from algebraicEqual["+i+"](\""+eq0+"\", \""+eq1+"\"): "+e+
					(e.getCause() == null ? "" : "; cause "+ e.getCause().toString()));
			return -1;
		}
	}
}
