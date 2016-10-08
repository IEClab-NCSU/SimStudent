/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 * Test whether dollar-and-cents number ("d.dd") is equal, to 2 decimal places, to one of a list of expressions.
 */
public class dollarEquals {
	
	/** For converting objects. */
	private static fmtDecimal fmtD = new fmtDecimal();

	/**
	 * Test harness.
	 * @param args first arg is test string; others are potential matches
	 */
	public static void main(String[] args) {
		dollarEquals dollarE = new dollarEquals();
		if (args.length < 1)
			return;
		String v0 = args[0];
		List<String> others = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
		System.out.printf("%s dollarEquals 1 of %d? %b\n",
				v0, others.size(), 0 == dollarE.dollarEqualsInternal(v0, others));
	}
	
	/**
	 * <p>Test whether dollar-and-cents number ("d.dd") is equal, to 2 decimal places, to one of a list of expressions.</p>
	 * @param str0 string to test against elements of exprList
	 * @param otherExprs 1 or more other expressions, as Strings
	 * @return true if expr0 algebraically matches any one of otherExprs
	 */
	public boolean dollarEquals(String str0, Object... otherExprs) {
		List<Object> exprList = new ArrayList<Object>();
		for (Object expr: otherExprs)
			exprList.add(expr);
		if (exprList.isEmpty())
			return false;
		int result = dollarEqualsInternal(str0, exprList);
		return (result == 0);
	}

	/**
	 * Test whether dollar-and-cents number ("d.dd") is equal, to 2 decimal places, to one of a list of expressions.
	 * @param v0 value to test against elements of exprList
	 * @param exprList 1 or more other expressions, as Strings
	 * @return 0 if exprList[0] matches at least one of the other exprList[] elements;
	 * 			1 if it matches none of them, 2 if error
	 */
	private int dollarEqualsInternal(String v0, List exprList) {
		if (v0 == null || (v0 = v0.trim()).length() < 1)
			return 2;
		String v0Int = (v0.endsWith(".00") ? v0.substring(0, v0.length()-3) : null);
		if (v0Int != null && v0Int.length() < 1)
			v0Int = "0";                         // allow ".00" as if "0.00"
		SymbolManipulator sm = new SymbolManipulator();

		for (Object exprObj: exprList) {
			if (exprObj == null)
				continue;
			try {
				Double v = CTATFunctions.toDouble(exprObj);
				if (v == null) {
					Expression expr = sm.parseCE(exprObj.toString());
					v = expr.compute();
				}
				long vLong = v.longValue();
				if (trace.getDebugCode("functions"))
					trace.out("functions", "v0 "+v0+", v0Int "+v0Int+", v "+v+", vLong "+vLong);
				if (Math.abs(v.doubleValue()-vLong) < Double.MIN_NORMAL) {
					String vStr = Long.toString(vLong);
					if (vStr.equalsIgnoreCase(v0))
						return 0;
					if (vStr.equalsIgnoreCase(v0Int))
						return 0;
				} else {
					String vStr = fmtD.fmtDecimal(v, 2);
					if (vStr.equalsIgnoreCase(v0))
						return 0;
				}
			} catch (ClassCastException cce) {
				trace.err("dollarEquals: expression not a constant \""+exprObj+"\": "+cce+
						(cce.getCause() == null ? "" : ";\n  cause "+cce.getCause().toString()));
			} catch (Throwable t) {
				trace.err("dollarEquals: cannot parse expression \""+exprObj+"\": "+t+
						(t.getCause() == null ? "" : ";\n  cause "+t.getCause().toString()));
			}
		}
		return 1;
	}
	
}
