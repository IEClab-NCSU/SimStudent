package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;

/**
 * Return the least common multiple of the 2 arguments.
 */
public class lcm {
	
	/**
	 * <p>Return the least common multiple of the 2 arguments.</p>
	 * @param a first factor
	 * @param b second factor
	 * @return lcm(a,b), always nonnegative; 0 if either argument is 0
	 */
	public Double lcm(double a, double b) {
		if (a == 0 || b == 0)
			return new Double(0);
		gcf fn = new gcf();
		Double d = fn.gcf(a,b);
		double ans = Math.abs(a*b/Double.valueOf(d));
		return new Double(ans);
	}
	
	/**
	 * <p>Return the least common multiple of the 2 arguments.</p>
	 * @param a first factor
	 * @param b second factor
	 * @return lcm(a,b), always nonnegative; 0 if either argument is 0;
	 *         null if either argument is non-numeric
	 */
	public Double lcm(Object a, Object b) {
		Double aD = CTATFunctions.toDouble(a);
		Double bD = CTATFunctions.toDouble(b);
		if (aD == null || bD == null)
			return null;
		else
			return lcm(aD.doubleValue(), bD.doubleValue());
	}	   
	/**
	 * Test harness.
	 * @param args[0] is a, args[1] is b
	 */
	public static void main(String[] args) {
		lcm fn = new lcm();
		System.out.printf("lcm(%5s,%5s) = %5f\n", args[0], args[1], fn.lcm(args[0], args[1]));
	}	
}
