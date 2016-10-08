/**
 * Copyright 2010 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

/**
 * Tell whether each coordinate of the input point "(x,y)" is within tolerance of the target point.
 */
public class match2D {

	/** Match an (x,y) coordinate pair. Spaces permitted, "()" optional, but at least 1 digit to left of decimal point. */
	private static final Pattern Point2D =
		Pattern.compile("\\s*[(]?\\s*(-?\\d+[.]?\\d*([eE]-?[0-9]+)?)\\s*,\\s*(-?\\d+[.]?\\d*([eE]-?[0-9]+)?)\\s*[)]?\\s*");

	/** Default epsilon to accept around x and y value. Value {@value #DEFAULT_TOLERANCE}. */
	private static final double DEFAULT_TOLERANCE = 0.1;	

	/**
	 * For command line arguments, see {@link #usageExit(String)}.
	 * @param args
	 */
	public static void main(String[] args) {
		match2D m2d = new match2D();
		if (args.length < 2 || args[0].equalsIgnoreCase("-h"))
			usageExit("Too few arguments.");
		int i = 0;
		Double epsilon = (args[i].startsWith("-") ? Double.valueOf(args[i++].substring(1)) : null);
		String target = args[i++];
		System.out.printf("with x,y tolerance "+
				(epsilon == null ? "default "+DEFAULT_TOLERANCE : epsilon.toString())+":\n");
		for (; i < args.length; ++i) {
			System.out.printf("   %s: %5b %s\n", target,
					(epsilon == null ? m2d.match2D(args[i], target) : m2d.match2D(args[i], target, epsilon)),
					args[i]);
		}
	}

	/**
	 * Tell whether each coordinate of the input point "(x,y)" is within {@value #DEFAULT_TOLERANCE} of the target point.
	 * @param input point to test, in the format "(x,y)", where x and y are numeric
	 * @param target reference point to test against, in the format "(x,y)" 
	 * @return true if input point is close enough to target (that is, within the given tolerances)
	 * @throws IllegalArgumentException if can't parse target as (x,y)
	 */
	public boolean match2D(String input, String target) {
		return match2D(input, target, DEFAULT_TOLERANCE, DEFAULT_TOLERANCE);
	}

	/**
	 * Tell whether each coordinate of the input point "(x,y)" is within tolerance of the target point.
	 * @param input point to test, in the format "(x,y)", where x and y are numeric
	 * @param target reference point to test against, in the format "(x,y)" 
	 * @param tolerance maximum acceptable difference for x and y coordinates
	 * @return true if input point is close enough to target (that is, within the given tolerances)
	 * @throws IllegalArgumentException if can't parse target as (x,y)
	 */
	public boolean match2D(String input, String target, double tolerance) {
		return match2D(input, target, tolerance, tolerance);
	}

	/**
	 * Tell whether the input point "(x,y)" is within tolerance of the target point.
	 * @param input point to test, in the format "(x,y)", where x and y are numeric
	 * @param target reference point to test against, in the format "(x,y)" 
	 * @param xTolerance maximum acceptable difference for x coordinate
	 * @param yTolerance maximum acceptable difference for x coordinate
	 * @return true if input point is close enough to target (that is, within the given tolerances)
	 * @throws IllegalArgumentException if can't parse target as (x,y)
	 */
	public boolean match2D(String input, String target, double xTolerance, double yTolerance) {
		double x = 0, y = 0;
		try {
			double targetCoords[] = parse(target);
			x = targetCoords[0];
			y = targetCoords[1];
		} catch (Exception e) {
			String errMsg = "match2D() target \""+target+"\" fails to parse as (x,y)";
			trace.err(errMsg+": "+e+"; cause "+e.getCause());
			throw new IllegalArgumentException(errMsg, e);
		}
		try {
			double inputCoords[] = parse(input); 
			if (Math.abs(x - inputCoords[0]) > Math.abs(xTolerance))
				return false;
			if (Math.abs(y - inputCoords[1]) > Math.abs(yTolerance))
				return false;
			return true;
		} catch (Exception e) {
			if (trace.getDebugCode("functions")) trace.outNT("functions", "match2D(..., "+target+
					") input \""+input+"\" fails to parse as (x,y): "+e);
			return false;
		}
	}

	/**
	 * Print an error message and a usage message and call {@link System#exit(int) System.exit(2)}.
	 * @param errMsg
	 */
	private static void usageExit(String errMsg) {
		System.err.printf("%s Usage:\n  %s -[epsilon] target test...\n", errMsg,
				(new match2D()).getClass().getSimpleName());
		System.err.println("where--\n"+
				"  [epsilon] is a floating-point tolerance for both x and y;\n"+
				"  target is the (x,y) pair to match against (you'll want to quote this);\n"+
				"  test... are (x,y) pair(s) to test for matching.");
		System.exit(2);
	}

	/**
	 * Scan a coordinate pair and determine the x, y value.
	 * @param input
	 * @return
	 */
	public static double[] parse(String input) {
		Matcher m = Point2D.matcher(input);
		boolean matched = m.find();
		double[] xy = null;
		if (matched) {
			xy = new double[2]; // result
			xy[0] = Double.parseDouble(m.group(1));
			xy[1] = Double.parseDouble(m.group(3));
		}
		if (trace.getDebugCode("functions")) {
				trace.outNT("functions", "match2D.parse("+input+") match"+
						(matched ? "es ok" : " fail")+"; x="+(xy == null ? "" : Double.toString(xy[0]))+
						"; y="+(xy == null ? "" : Double.toString(xy[1]))+";");
		}
		return xy;
	}
}
