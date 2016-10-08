/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  Format a decimal number to have the given precision.
 */
public class fmtDouble {
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDouble(double value, int prec) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		String fmt = "%."+prec+"f";
		pw.printf(fmt, value);
		return sw.toString();    	
    }
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDouble(String sValue, int prec) {
    	double value = Double.parseDouble(sValue);
    	return fmtDouble(value, prec);
    }
    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtDouble(String sValue, String prec) {
    	return fmtDouble(sValue, Integer.parseInt(prec));
    }
	/**
	 * Test harness. Print each argument with 2 decimal places.
	 * With <b>-p <i>prec</i></b> option, uses this precision instead. E.g.:
	 *     fmtDouble -p 4 5.2349283 42.523282 -293.2398 
	 * @param args numbers to format
	 */
	public static void main(String[] args) {
		int i = 0;
		int prec = 2;
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			char option = args[i].charAt(1);
			switch (option) {
			case 'p':
				prec = Integer.parseInt(args[++i]);
				break;
			default:
				System.err.println("unknown option -"+option);
			}
		}
		fmtDouble t = new fmtDouble();
		for (; i < args.length; ++i) {
			String arg = args[i];
			System.out.println("toDouble("+arg+")="+t.fmtDouble(arg, prec));
		}
	}

}
