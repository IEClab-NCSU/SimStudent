package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.Arrays;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 * Return true if the first argument is equal to any of the other arguments, false otherwise.
 */
public class memberOf {
    /**
     * <p>True if the first argument is equal to any of the other arguments, false otherwise.</p>
	 * @param o value to find among other arguments
	 * @param values other arguments
	 * @return true if found
     */
    public boolean memberOf(Object o, Object... values) {
        if (o == null || values == null)
            return false;
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "memberOf(Object "+o+", Object[] "+Arrays.toString(values)+")");
    	Double od = CTATFunctions.toDouble(o);
        for (Object value : values) {
        	Double vd = CTATFunctions.toDouble(value);
        	if (od != null && vd != null && od.equals(vd))
        		return true;
            if (o.equals(value))
                return true;
        }
        return false;
    }
    
    /**
     * <p>True if the first argument is equal to any of the other arguments, false otherwise.</p>
	 * @param o value to find among other arguments
	 * @param values other arguments
	 * @return true if found
     */
    public boolean memberOf(double d, double... values) {
    	if (values == null)
    		return false;
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "memberOf(double "+d+", double[] "+Arrays.toString(values)+")");
        for (double value : values)
            if (d==value)
                return true;

        return false;
    }

    /**
     * <p>True if the first argument is equal to any of the other arguments, false otherwise.</p>
	 * @param o value to find among other arguments
	 * @param values other arguments
	 * @return true if found
     */
    public boolean memberOf(double d, Object... values) {
        return memberOf(new Double(d), values);
    }
}
