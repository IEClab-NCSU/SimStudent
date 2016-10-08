/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Returns true if argument can be interpreted as an integer, false otherwise.
 */
public class isInteger {

	/**
	 * <p>Returns true if argument can be interpreted as an integer, false otherwise.</p>
	 * @param value value to test
	 * @param toStringOk if true, test whether {@link Object#toString() value.toString()}
	 *   is numeric; if false, value must be numeric
	 * @return true if value is integer or can be parsed as an integer
	 */
	public boolean isInteger(Object value, boolean toStringOk) {
		return isNumber.isInteger(value, toStringOk);
	}

	/**
	 * <p>Returns true if argument can be interpreted as an integer, false otherwise.</p>
	 * @param value value to test
	 * @return true if value is integral or can be parsed as an integer
	 */
	public boolean isInteger(Object value) {
		try {
			return isInteger(value, true);
		} catch (Throwable t) {
			if (trace.getDebugCode("functions")) trace.out("functions", "isInteger("+value+") returns false: "+t);
			return false;
		}
	}
	
	/**
	 * Test harness.
	 * @param argv see {@link #usageExit(Exception)}
	 */
	public static void main(String[] args) {
		int i = 0;
		boolean parseToNum = false;  // whether to parse args[] as ints before calling function
		boolean toStringOk = false;
		try {
			for (boolean endSw = false; i < args.length && !endSw && args[i].charAt(0) == '-'; ++i) {
				switch(args[i].charAt(1)) {
				case 'h': case 'H':
					throw new IllegalArgumentException("Help message.");
				case 'i': case 'I':
					parseToNum = true; break;
				case 's': case 'S':
					toStringOk = true; break;
				case '-': case '_':
					endSw = true; break;
				default:
					throw new IllegalArgumentException("Undefined switch \"-"+args[i].charAt(1)+"\".");
				}
			}
		} catch (Exception e) {
			usageExit(e);
		}
		for (; i < args.length; ++i) {
			try {
				isInteger ii = new isInteger();
				if (parseToNum) {
					double argi = Double.parseDouble(args[i]);
					System.out.printf("isInteger(%6f) = %b\n", argi,
							ii.isInteger(argi));
				} else {
					System.out.printf("isInteger(%6s, %-5b) = %b\n", args[i], toStringOk,
							ii.isInteger(args[i], toStringOk));					
				}
			} catch (Exception e) {
				System.out.printf("isInteger(%6s, %-5b) = Error: %s\n", args[i], toStringOk, e.toString());
				e.printStackTrace(System.out);
			}
		}
	}

	private static void usageExit(Exception e) {
		System.err.println("Exception: "+e.getMessage()+" Usage:\n"+
				"  "+isInteger.class.getName()+" [-h] [-i] [-s] arg1 ...\n"+
				"where--\n"+
				"  -h prints this help message;\n"+
				"  -i converts each arg to int before calling isInteger(arg);\n"+
				"  -s sets the toStringOk arg to true.");
		System.exit(2);
	}
}
