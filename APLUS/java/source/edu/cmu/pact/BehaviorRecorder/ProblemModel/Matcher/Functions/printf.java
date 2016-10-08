/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Format a string with optional arguments inserted, using C-style "%" format specifiers.
 */
public class printf {

	/**
	 * <p>Format a string with optional arguments inserted, using C-style "%" format specifiers.</p>
	 * @param fmt format with static text and "%" format specifiers
	 * @values arguments for the "%" format specifiers
	 * @return formatted string
	 */
	public String printf(String fmt, Object... values) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.printf(fmt, values);
		return sw.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		printf p = new printf();
		double d1 = 34.298342, d2 = -304.22;
		String fmt = "%.2f %08.1f";
		System.out.println("printf(\""+fmt+"\","+d1+","+d2+")="+p.printf(fmt, d1, d2));
	}
}
