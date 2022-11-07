/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.cmu.pact.Utilities.trace;

/**
 *  Format a decimal number to have the given precision.
 */
public class fmtDecimal {
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDecimal(double value, int prec) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		String fmt = "%."+prec+"f";
		pw.printf(fmt, value);
		String result = sw.toString();    	
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "fmtDecimal(double "+value+", int "+prec+")->"+result);
		return result;
    }
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDecimal(Number value, Double prec) {
    	String result = fmtDecimal(value.doubleValue(), (int) Math.round(prec));
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "fmtDecimal(Double "+value+", Double "+prec+")->"+result);
    	return result;
    }
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDecimal(String sValue, int prec) {
    	double value = Double.parseDouble(sValue);
    	String result = fmtDecimal(value, prec);
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "fmtDecimal(String "+sValue+", int "+prec+")->"+result);
    	return result;
    }
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDecimal(String sValue, String prec) {
    	String result = fmtDecimal(sValue, Integer.parseInt(prec));
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "fmtDecimal(String "+sValue+", String "+prec+")->"+result);
    	return result;
    }
	/**
	 * Test harness. Print each argument with 2 decimal places.
	 * With <b>-p <i>prec</i></b> option, uses this precision instead. E.g.:
	 *     fmtDecimal -p 4 5.2349283 42.523282 -293.2398 
	 * @param args numbers to format
	 */
	public static void main(String[] args) {
		trace.addDebugCodes(System.getProperty("DebugCodes"));
		fmtDecimal t = new fmtDecimal();
		int i = 0;
		int prec = 2;
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			char option = args[i].charAt(1);
			switch (option) {
			case 'p':
				prec = Integer.parseInt(args[++i]);
				break;
			case 'D':
				Double pD = new Double(Double.parseDouble(args[++i]));
				Double vD = new Double(Double.parseDouble(args[++i]));
				trace.out("fmtDecimal("+vD+", "+pD+")="+t.fmtDecimal(vD, pD));
				return;
			default:
				System.err.println("unknown option -"+option);
			}
		}
		for (; i < args.length; ++i) {
			String arg = args[i];
			trace.out("fmtDecimal("+arg+", "+prec+")="+t.fmtDecimal(arg, prec));
		}
	}

}
