package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

/**
 * Returns true if argument can be interpreted as a number, false otherwise.
 */
public class isNumber {
	/**
	 * Returns true if argument can be interpreted as a number, false otherwise.
	 * @param value value to test
	 * @param toStringOk if true, test whether {@link Object#toString() value.toString()}
	 *   is numeric; if false, value must be numeric
	 * @param requireInt if true also require that the valu e
	 * @return true if value is instanceof Number or can be parsed as a double
	 */
	private static boolean isNumber(Object value, boolean toStringOk, boolean requireInt) {
		try {
			if (value == null)
				return false;
		} catch (Throwable t) {
			if (trace.getDebugCode("functions")) trace.out("functions", "isNumber("+value+","+toStringOk+") returns false: "+t);
			return false;
		}
		if (value instanceof Number) {
			if (!requireInt)
				return true;
			double d = ((Number) value).doubleValue();
			return (d < 0 ? d == Math.ceil(d) : d == Math.floor(d));
		}
		if (!toStringOk)
			return false;
		try {
			double d = Double.parseDouble(value.toString());
			if (!requireInt)
				return true;
			return (d < 0 ? d == Math.ceil(d) : d == Math.floor(d));
		} catch (NumberFormatException nfe) {
			if (trace.getDebugCode("functions")) trace.out("functions", "isNumber("+value+","+toStringOk+") returns false: "+nfe);
			return false;
		}
	}

	/**
	 * <p>Returns true if argument can be interpreted as a number, false otherwise.</p>
	 * @param value value to test
	 * @param toStringOk if true, test whether {@link Object#toString() value.toString()}
	 *   is numeric; if false, value must be numeric
	 * @return true if value is instanceof Number or can be parsed as a double
	 */
	public static boolean isInteger(Object value, boolean toStringOk) {
		return isNumber(value, toStringOk, true);
	}

	/**
	 * <p>Returns true if argument can be interpreted as a number, false otherwise.</p>
	 * @param value value to test
	 * @param toStringOk if true, test whether {@link Object#toString() value.toString()}
	 *   is numeric; if false, value must be numeric
	 * @return true if value is instanceof Number or can be parsed as a double
	 */
	public boolean isNumber(Object value, boolean toStringOk) {
		return isNumber(value, toStringOk, false);
	}

	/**
	 * <p>Returns true if argument can be interpreted as a number, false otherwise.</p>
	 * @param value value to test
	 * @return true if value is instanceof Number or can be parsed as a double
	 */
	public boolean isNumber(Object value) {
		try {
			return isNumber(value, false);
		} catch (Throwable t) {
			if (trace.getDebugCode("functions")) trace.out("functions", "isNumber("+value+") returns false: "+t);
			return false;
		}
	}
}
