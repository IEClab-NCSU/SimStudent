/**
 * Copyright 2010 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.regex.Pattern;

/**
 * Check that each number in an expression is either an integer or a dollars-and-cents (d.dd) value.
 */
public class constantsDollar {

	/** Regex for integers: optional sign then 1 or more digits. */
	private static Pattern IntegerPattern = Pattern.compile("[-+]?[0-9][0-9]*", Pattern.CASE_INSENSITIVE);

	/** Regex for integers: optional sign then 1 or more digits, then a decimal point, then 2 digits. */
	private static Pattern DollarPattern = Pattern.compile("[-+]?[0-9][0-9]*\\.[0-9][0-9]", Pattern.CASE_INSENSITIVE);
	
	/**
	 * <p>Check that each number in an expression is either an integer or a dollars-and-cents (d.dd) value.</p>
	 * @param expr arithmetic expression to test
	 * @return true if every number conforms
	 */
	public boolean constantsDollar(String expr) {
		constantsConform cc = new constantsConform();
		int result = cc.exec(expr, IntegerPattern, DollarPattern);
		return (result == 0);
	}
	
	/**
	 * @param args expressions to evaluate
	 */
	public static void main(String[] args) {
		constantsDollar cd = new constantsDollar();
		for (String expr : args)
			System.out.printf("%b <= %s\n", cd.constantsDollar(expr), expr);
	}

}
