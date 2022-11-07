package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.Utilities.trace;

import java.util.Random;

/**
 * Choose one of the argument values at random.
 */
public class chooseRandomly {
    /**
     * <p>Choose one of the argument values at random.</p>
	 * @param values list of values to choose
	 * @return one of the values
     */
    public Object chooseRandomly(Object... values) {
        if (values==null || values.length==0)
            return null;
        Random r = new Random();
        return values[r.nextInt(values.length)];
    }

    public static void main(String [] args) throws Exception {
        chooseRandomly chooser = new chooseRandomly();

        for (int i=0; i<100; i++)
            trace.out("chose " + chooser.chooseRandomly("a", "b", "c", "d"));

        for (int i=0; i<10; i++)
            trace.out("chose " + chooser.chooseRandomly("a"));

        for (int i=0; i<10; i++)
            trace.out("chose " + chooser.chooseRandomly());
    }    
}
