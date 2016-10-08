package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Returns true if any argument is true, false otherwise.
 */
public class or {

    /**
     * <p>Returns true if any argument is true, false otherwise.</p>
	 * @param values boolean values to test
	 * @return true if any value is true; else returns false
     */
    public boolean or(boolean... values) {
        for (boolean value: values) {
            if (value)
                return true;
        }

        return false;
    }
}
