package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Simulate an if-then-else statement.
 */
public class ifThen {
    /**
	 * <p>Simulate an if-then-else statement. Returns thenValue if test is true, elseValue otherwise.</p>
	 * @param test boolean value to test
	 * @param thenValue possible return value
	 * @param elseValue alternative return value
	 * @return thenValue if test is true, otherwise elseValue.
     */
    public Object ifThen(boolean test, Object thenValue, Object elseValue) {
        return test ? thenValue : elseValue;
    }

    /**
	 * <p>Simulate an if-then-else statement. Returns thenValue if test is true, elseValue otherwise.</p>
	 * @param test boolean value to test
	 * @param thenValue possible return value
	 * @param elseValue alternative return value
	 * @return thenValue if test is true, otherwise elseValue.
     */
    public Object ifThen(boolean test, double thenValue, double elseValue) {
        return test ? thenValue : elseValue;
    }
}
