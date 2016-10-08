package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 *  Return the result of concatenating all of the arguments as strings.
 */
public class concat {
    /**
     *  <p>Returns the result of concatenating all of the arguments as strings.</p>
     *  Each value is evaluated as itself (if it is a String) or the value of calling
     *  the toString method on the corresponding Java object.
	 *  @param values strings to concatenate
	 *  @return concatenated string
     */
    public String concat(String... values) {
        StringBuffer buf = new StringBuffer();

        for (String value : values)
            buf.append(value);

        return buf.toString();
    }
}
