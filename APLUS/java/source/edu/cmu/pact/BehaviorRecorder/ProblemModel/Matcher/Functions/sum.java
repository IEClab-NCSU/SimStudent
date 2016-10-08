package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Add all values and return the result.
 */
public class sum {
    /**
     * <p>Add all values and return the result.</p>
	 * @param values
	 * @return sum
     */
    public Double sum(double... values) {
        double total = 0;

        for (double value : values)
            total += value;
	
        return new Double(total);
    }
}

