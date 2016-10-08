/**
 * Copyright 2011 Carnegie Mellon University.
 */

package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Remainder function.
 */
public class modf {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double d= Double.parseDouble(args[0]), m = Double.parseDouble(args[1]);
		double r = Math.IEEEremainder(d, m);
		double ra = Math.abs(r);
		double modResult = (new modf()).modf(d, m);
		System.out.printf("%s IEEEremainder %s = %s, abs %s, modf() %s\n", args[0], args[1],
				Double.toString(r), Double.toString(ra), Double.toString(modResult));
	}
	
	/**
     * <p>Return n%m, the remainder of n divided by m.</p>
     * @param dividend
     * @param modulus
     * @return result of dividend % modulus
     */
    public double modf(double dividend, double modulus) {
        return dividend % modulus;
    }
}
