/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Remove any 1 coefficients from a polynomial expression.
 */
public class rm1coeff {

	/** Match at the start of the string. */
	private static final Pattern p0 = Pattern.compile("^1([a-zA-Z])");

	/** Match anywhere else, ensuring that the "1" is not part of a longer numeral. */
	private static final Pattern p = Pattern.compile("([^0-9])1([a-zA-Z])");

	/**
	 * <p>Remove any 1 coefficients from a polynomial expression..</p>
	 * @param expr expression to modify
	 * @return modified string
	 */
	public String rm1coeff(String expr) {
		if (expr == null)
			return null;
		Matcher m = p0.matcher(expr);
		String expr0 = m .replaceAll("$1");
		m = p.matcher(expr0);
		return m.replaceAll("$1$2");
	}

	/**
	 * Test harness for {@link #rm1coeff(String)}.
	 * @param args Strings to convert.
	 */
	public static void main(String[] args) {
		rm1coeff r1c = new rm1coeff();
		for (String arg : args) {
			System.out.printf("%-10s => %s ;\n", arg, r1c.rm1coeff(arg));
		}
	}
}
