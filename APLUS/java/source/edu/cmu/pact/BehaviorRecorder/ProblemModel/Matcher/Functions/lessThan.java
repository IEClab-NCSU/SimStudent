package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Returns true if the first argument is less than the second, false otherwise.
 */
public class lessThan {

	/**
	 * <p>Returns true if the first argument is less than the second, false otherwise.
	 * If the arguments cannot both be converted to numbers, then compares lexicographically, ignoring case.</p>
	 * @param value1
	 * @param value2
	 * @return true if value1 is less than value2; false otherwise,
	 *         except true if value1 null, except false if both arguments null
	 */
	public boolean lessThan(String value1, String value2) {
		if (value1 == null) {
			trace.err("lessThan("+value1+", "+value2+"): null 1st argument; returning "+(value2 != null));
			return value2 != null;
		}
		try {
			double d1 = Double.parseDouble(value1);
			return lessThan(d1, value2);
		} catch (NumberFormatException nfe) {
			return value2 == null ? false : value1.compareToIgnoreCase(value2) < 0;
		}
	}

	/**
	 * <p>Returns true if the first argument is less than the second, false otherwise.
	 * If the arguments cannot both be converted to numbers, then compares lexicographically, ignoring case.</p>
	 * @param value1
	 * @param d2
	 * @return true if value1 is less than value2; false otherwise, except true if value1 null
	 */
	public boolean lessThan(String value1, double d2) {
		if (value1 == null) {
			trace.err("lessThan("+value1+", "+d2+"): null 1st argument; returning true");
			return true;
		}
		try {
			double d1 = Double.parseDouble(value1);
			return lessThan(d1, d2);
		} catch (NumberFormatException nfe) {
			trace.err("lessThan("+value1+", "+d2+"): non-numeric 1st argument: "+nfe);
			return value1.compareToIgnoreCase(Double.toString(d2)) < 0;
		}
	}

	/**
	 * <p>Returns true if the first argument is less than the second, false otherwise.
	 * If the arguments cannot both be converted to numbers, then compares lexicographically, ignoring case.</p>
	 * @param d1
	 * @param value2
	 * @return true if value1 is less than value2; false otherwise
	 */
	public boolean lessThan(double d1, String value2) {
		if (value2 == null) {
			trace.err("lessThan("+d1+", "+value2+"): null 2nd argument; returning false");
			return false;
		}
		try {
			double d2 = Double.parseDouble(value2);
			return lessThan(d1, d2);
		} catch (NumberFormatException nfe) {
			trace.err("lessThan("+d1+", "+value2+"): non-numeric 2nd argument: "+nfe);
			return Double.toString(d1).compareToIgnoreCase(value2) < 0;
		}
	}

    /**
	 * <p>Returns true if the first argument is less than the second, false otherwise.</p>
	 * @param value1
	 * @param value2
     * @return true if value1 is less than value2, false otherwise
     */
    public boolean lessThan(double value1, double value2) {
        return value1 < value2;
    }
    
    /**
     * Test harness.
     * @param args arg1 & arg 2 are left & right operands
     */
    public static void main(String[] args) {
    	lessThan t = new lessThan();
    	if (args.length < 1)
    		System.out.printf("%5b = lessThan(null, null)\n", t.lessThan(null, null));
    	else if (args.length < 2)
    		System.out.printf("%5b = lessThan("+args[0]+", null)\n", t.lessThan(args[0], null));
    	else
    		System.out.printf("%5b = lessThan("+args[0]+", "+args[1]+")\n", t.lessThan(args[0], args[1]));
    }
}
