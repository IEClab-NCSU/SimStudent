package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;

/**
 * Quote a given string.
 */
public class quote {

	/** Match a double-quote. */
	private static final Pattern QuotePattern = Pattern.compile("\"");

	/** Replacment string to escape a double-quote without generating a back-reference. */
	private static final String QuoteReplacement = Matcher.quoteReplacement("\\\"");

	/**
     * <p>Quote a given string.Returns argument, quoted. Embedded double-quotes are escaped by backslashes.</p>
     * @param arg
     * @return NULL if arg null, else arg.toString(), with quotes
     */
    public String quote(Object arg) {
    	if (arg == null)
    		return "NULL";
    	StringBuffer result = new StringBuffer("\"");
    	Matcher m = QuotePattern.matcher(CTATFunctions.stringify(arg));
    	result.append(m.replaceAll(QuoteReplacement));
    	result.append("\"");
    	return result.toString();
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		quote s = new quote();
		for (String arg : args) {
			System.out.printf("%-20s = %s\n", arg, s.quote(arg));
		}
	}
}
