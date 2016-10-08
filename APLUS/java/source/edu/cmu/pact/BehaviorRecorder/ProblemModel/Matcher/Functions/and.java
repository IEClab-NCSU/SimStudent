package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Returns true if every argument is true, false otherwise.
 */
public class and {
    /**
     * <p>Returns true if every argument is true, false otherwise.</p>
	 * @param values boolean values to test
	 * @return true if every value is true; else returns false
     */
    public boolean and(boolean... values) {
        for (boolean value: values) {
            if (!value)
                return false;
        }

        return true;
    }
}
