/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Return the left-most non-null argument.
 */
public class firstNonNull {

    /**
	 * <p>Return the left-most non-null argument.</p>
     * @param any arguments 
     * @return left-most non-null argument; if every argument is null, returns null
     */
    public String firstNonNull(Object... values) {
    	if (values == null)
    		return null;
        for (Object value: values) {
            if (null != value)
                return value.toString();
        }
        return null;
    }
}
