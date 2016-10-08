package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Function to simulate a negation operator.
 */
public class not {
    /**
     * <p>Negation operator. Returns true if test is false, false if test is true.</p>
	 * @param test boolean to test
	 * @return not(test)
     */
    public boolean not(boolean test) {
        return !test;
    }
}
