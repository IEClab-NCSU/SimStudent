package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Returns true if the first argument is greater than the second, false otherwise.
 */
public class greaterThan {

    /**
	 * <p>Returns true if the first argument is greater than the second, false otherwise.
	 * If the arguments cannot both be converted to numbers, then compares lexicographically, ignoring case.</p>
	 * @param value1
	 * @param value2
     * @return true if value1 is greater than value2, false otherwise
     */
    public boolean greaterThan(String value1, String value2) {
    	if (value1 == null) {
    		trace.err("greaterThan("+value1+", "+value2+"): null 1st argument; returning false");
    		return false;
    	}
    	try {
    		double d1 = Double.parseDouble(value1);
    		return greaterThan(d1, value2);
    	} catch (NumberFormatException nfe) {
    		return value2 == null ? true : value1.compareToIgnoreCase(value2) > 0;
    	}
    }

    /**
	 * <p>Returns true if the first argument is greater than the second, false otherwise.
	 * If the arguments cannot both be converted to numbers, then compares lexicographically, ignoring case.</p>
	 * @param value1
	 * @param d2
     * @return true if value1 is greater than value2, false otherwise
     */
    public boolean greaterThan(String value1, double d2) {
    	if (value1 == null) {
    		trace.err("greaterThan("+value1+", "+d2+"): null 1st argument; returning false");
    		return false;
    	}
    	try {
    		double d1 = Double.parseDouble(value1);
    		return greaterThan(d1, d2);
    	} catch (NumberFormatException nfe) {
    		trace.err("greaterThan("+value1+", "+d2+"): non-numeric 1st argument: "+nfe);
    		return value1.compareToIgnoreCase(Double.toString(d2)) > 0;
    	}
    }

    /**
	 * <p>Returns true if the first argument is greater than the second, false otherwise.
	 * If the arguments cannot both be converted to numbers, then compares lexicographically, ignoring case.</p>
	 * @param d1
	 * @param value2
     * @return true if value1 is greater than value2; false otherwise, except true if value2 null
     */
    public boolean greaterThan(double d1, String value2) {
    	if (value2 == null) {
    		trace.err("greaterThan("+d1+", "+value2+"): null 2nd argument; returning true");
    		return true;
    	}
    	try {
    		double d2 = Double.parseDouble(value2);
    		return greaterThan(d1, d2);
    	} catch (NumberFormatException nfe) {
    		trace.err("greaterThan("+d1+", "+value2+"): non-numeric 2nd argument: "+nfe);
    		return Double.toString(d1).compareToIgnoreCase(value2) > 0;
    	}
    }

    /**
	 * <p>Returns true if the first argument is greater than the second, false otherwise.</p>
	 * @param value1
	 * @param value2
     * @return true if value1 is greater than value2, false otherwise
     */
    public boolean greaterThan(double value1, double value2) {
        return value1 > value2;
    }
    
    /**
     * Test harness.
     * @param args arg1 & arg 2 are left & right operands
     */
    public static void main(String[] args) {
    	greaterThan t = new greaterThan();
    	if (args.length < 1)
    		System.out.printf("%5b = greaterThan(null, null)\n", t.greaterThan(null, null));
    	else if (args.length < 2)
    		System.out.printf("%5b = greaterThan("+args[0]+", null)\n", t.greaterThan(args[0], null));
    	else
    		System.out.printf("%5b = greaterThan("+args[0]+", "+args[1]+")\n", t.greaterThan(args[0], args[1]));
    }
}
