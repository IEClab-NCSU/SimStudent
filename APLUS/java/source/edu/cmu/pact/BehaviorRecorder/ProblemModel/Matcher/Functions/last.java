/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Returns its last argument, for sequential evaluation. This function can be
 * used when you want to execute some other functions for their side effects
 * but return an arbitrary value.  For example, the following formula would
 * assign the String value "fred" a variable first_name and return the student's
 * input:<pre>
      last(assign("first_name", "fred"), input)
 * </pre>
 */
public class last {

	/**
	 * <p>Returns its last argument, for sequential evaluation of arguments.
	 * This function can be
	 * used when you want to execute some other functions for their side effects
	 * but return an arbitrary value.  For example, the following formula would
	 * assign the String value "fred" a variable first_name and return the student's
	 * input:<br>
          <tt>last(assign("first_name", "fred"), input)</tt>
	 * </br></p>
	 * @param values to evaluate
	 * @return last value; null if no arguments
	 */
	public Object last(Object... values) {
		int i = values.length;
		if (i > 0)
			return values[i-1];
		else
			return null;
	}
}
