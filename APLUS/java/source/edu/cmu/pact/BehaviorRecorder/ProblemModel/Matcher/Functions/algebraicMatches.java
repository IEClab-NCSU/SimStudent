package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.pact.Utilities.trace;

/**
 * Test whether 2 or more equations are algebraically equivalent.
 */
public class algebraicMatches {

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
				"   test  is one of {algebraicMatches},\n"+
				"   eq1   is one equation,\n"+
				"   ...   are other equations to test.\n"+
				"Exits with status 0 if test passes, 1 if fails, 2 if error, 3 if bad argument.\n");
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		algebraicMatches smt = new algebraicMatches();
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
		List<String> eqList = new ArrayList<String>();
		int result = 2;  // error status
		
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			testName = args[i].substring(1);
		}
		for (; i < args.length; ++i)
			eqList.add(new String(args[i]));
		if ("algebraicMatches".equalsIgnoreCase(testName))
			trace.out("Result: "+(0 == (result = algebraicMatches(eqList))));
		else
			usageExit("unknown test name \""+testName+"\"");
		return result;
	}
	
	/**
	 * <p>Test whether 2 or more equations are algebraically equivalent.</p>
	 * Returns true if the equation in the first argument is
	 * algebraically equivalent to any of the following ones.
	 * Note that the arguments must be equations, not just expressions.
	 * Calls {@link SymbolManipulator#algebraicMatches(String, String)}.
	 * @param eq0 equation to test against other arguments otherEqs
	 * @param otherEqs 1 or more other equations, as Strings
	 * @return true if eq0 algebraically matches any one of otherEqs
	 */
	public boolean algebraicMatches(String eq0, String... otherEqs) {
		List<String> eqList = new ArrayList();
		for (String eq: otherEqs)
			eqList.add(eq);
		int result = algebraicMatches(eq0, eqList);
		return (result == 0);
	}

	/**
	 * Test {@link SymbolManipulator#algebraicMatches(String, String)}
	 * @param eqList at least 2 equations, as Strings
	 * @return 0 if eqList[0] matches at least one of the other eqList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int algebraicMatches(List<String> eqList) {
		return algebraicMatches(eqList.get(0), eqList.subList(1, eqList.size()));
	}

	/**
	 * Test {@link SymbolManipulator#algebraicMatches(String, String)}
	 * @param eq0 equation to test against elements of eqList
	 * @param eqList 1 or more other equations, as Strings
	 * @return 0 if eqList[0] matches at least one of the other eqList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int algebraicMatches(String eq0, List<String> eqList) {
		String eq1 = null;
		int i = -1;
		if (eqList.size() < 1)
			usageExit("test algebraicMatches requires at least 1 other equation argument");
		try {
			SymbolManipulator sm = new SymbolManipulator();
			for (i = 0; i < eqList.size(); ++i) {
				eq1 = eqList.get(i);
				boolean result = sm.algebraicMatches(eq0, eq1);
				if (trace.getDebugCode("si")) trace.out("si", "algebraicMatches["+i+"] result "+result+
						" on\n  \""+eq0+"\" vs\n  \""+eq1+"\"");
				if (result)
					return 0;       // matches eqList[i]
			}
			return 1;               // mismatch on all
		} catch (Exception e) {
			System.err.println("Error from algebraicMatches["+i+"](\""+eq0+"\", \""+eq1+"\"): "+e+
					(e.getCause() == null ? "" : "; cause "+ e.getCause().toString()));
			return 2;
		}
	}

}
