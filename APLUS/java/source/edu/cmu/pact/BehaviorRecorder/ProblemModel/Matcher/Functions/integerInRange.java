/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Tell whether an integer is within the range specified by the arguments.
 */
public class integerInRange {

	/**
	 * <p>Test whether an integer is within the range specified by the arguments, inclusive.</p>
	 * @param n number to test, will convert to integer
	 * @param floor left-hand end of the interval; will convert to integer; neg infinity if null
	 * @param ceiling right-hand end of the interval 
	 * @return false if n is null; true if floor <= n <= ceiling
	 */
	public boolean integerInRange(double n, double floor, double ceiling) throws Exception {
		return integerInRange(new Double(n), new Double(floor), new Double(ceiling));
	}

	/**
	 * <p>Test whether an integer is within the range specified by the arguments, inclusive.</p>
	 * @param n number to test, will convert to integer
	 * @param floor left-hand end of the interval; will convert to integer; neg infinity if null
	 * @param ceiling right-hand end of the interval 
	 * @return false if n is null; true if floor <= n <= ceiling
	 */
	public boolean integerInRange(Number n, Number floor, Number ceiling) throws Exception {
		if (n == null)
			return false;
		long nI = Math.round(n.doubleValue());
		if (floor != null) {
			long floorI = Math.round(floor.doubleValue());
			if (nI < floorI)
				return false;
		}
		if (ceiling != null) {
			long ceilingI = Math.round(ceiling.doubleValue());
			if (ceilingI < nI)
				return false;
		}
		return true;
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
				"   [java -cp classpath] "+getClass().getName()+" n floor ceiling\n"+
				"where--\n"+
				"   n        is the number to test,\n"+
				"   floor    is the number to test,\n"+
				"   ceiling  is the right-hand end of the interval.\n"+
				"Exits with status 0 if floor <= n <= ceiling, 1 if not, 2 if error.\n");
	}
	
	/**
	 * @param args
	 * @return 0 if test passes
	 */
	public static void main(String[] args) {
		integerInRange iir = new integerInRange();
		Number n = null, floor = null, ceiling = null;
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i].trim();
			Double argD = (arg.length() < 1 ? null : new Double(arg));  // "" => null
			switch (i) {
			case 0: n = argD; break;
			case 1: floor = argD; break;
			case 2: ceiling = argD; break;
			}
		}
		try {
			boolean result = iir.integerInRange(n, floor, ceiling);
			System.exit(result ? 0 : 1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
}
