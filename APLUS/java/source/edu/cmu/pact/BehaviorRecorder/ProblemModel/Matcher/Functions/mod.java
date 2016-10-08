/**
 * Copyright 2007 Carnegie Mellon University.
 */

package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Remainder function.
 */
public class mod {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int i= Integer.parseInt(args[0]), j = Integer.parseInt(args[1]);
		double d = Math.IEEEremainder(i, j);
		double da = Math.abs(d);
		int modResult = (new mod()).mod(i, j);
		System.out.printf("%s mod %s = %s, abs %s, mod() %d\n", args[0], args[1],
				Double.toString(d), Double.toString(da), modResult);
	}
	
	/**
     * <p>Return n%m, the remainder of n divided by m.</p>
     * @param dividend, will convert to integer via {@link Math#round(double)}
     * @param modulus, will convert to integer via {@link Math#round(double)}
     * @return remainder after n/m, as int
     */
    public int mod(double n, double m) {
        long ni = Math.round(n);
        long mi = Math.round(m);
        return (int) (ni % mi);
    }
}
