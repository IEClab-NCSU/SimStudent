package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;

/**
 * Returns true if all arguments are equal.
 */
public class equals {
    
    /**
     * <p>Returns true if every value in values is equal to value. If value can be interpreted as a
     * double, attempts to interpret all values as doubles. Otherwise tests using {@link Object#equals(Object)}.</p>
     * {@link Object#equals(Object) value.equals(Object)}. 
     * @param value number to test others against; returns false if can't be interpreted as double
     * @param values other values to test
     * @return true only if all values are equal as described above;
     *         if value is null, returns true only if all values are null
     */
    public boolean equals(Object value, Object... values) {
    	Double dv = CTATFunctions.toDouble(value);
    	if (dv != null) {
    		for (Object compareTo: values) {
    			Double dvCompareTo = CTATFunctions.toDouble(compareTo);
    			if (dvCompareTo == null)
    				return false;
    			if (dv.doubleValue() != dvCompareTo.doubleValue())
    				return false;
    		}
    		return true;
    	}
		for (Object compareTo: values) {
			if (value == null) {
				if (compareTo != null)
					return false;
			} else if (!value.equals(compareTo))
				return false;
		}
		return true;
    }

    /**
     * For testing.
     * @param args unused
     */
    public static void main(String[] args) {
    	equals e = new equals();
    	Object a0 = new Double(args[0]);
    	Object a1 = new Double(args[1]);
    	System.out.printf("Strings: equals(%s, %s)=%s\n", args[0], args[1],
    			Boolean.toString(e.equals(args[0], args[1])));
        System.out.printf("Doubles: equals(%.2f, %.2f)=%s\n", a0, a1,
    			Boolean.toString(e.equals(a0, a1)));
    }
}
