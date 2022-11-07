/**
 * 
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.PrintWriter;
import java.io.StringWriter;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 * Format a number as a calculator would: as an integer if 
 * no fractional part; else with up to {@value #DEFAULT_PRECISION}
 * digits right of the decimal point, but no trailing zeros.
 */
public class fmtNormal {

	/** The default number of decimal places, for calls where not given. */
	public static final int DEFAULT_PRECISION = 6;
	
	/** The default number of decimal places, for calls where not given. */
	public int defaultPrecision = DEFAULT_PRECISION;

	/**
     * Format a decimal number as a desk calculator would: as an integer if 
	 * there are no fractional digits; else with up to {@value #DEFAULT_PRECISION}
	 * decimal digits, but no trailing zeros.
	 */
	public fmtNormal() {
		this(DEFAULT_PRECISION);
	}

	/**
	 * Constructor sets precision.
	 * @param prec
	 */
	public fmtNormal(int prec) {
		defaultPrecision = prec;
	}

	/**
     * <p>Format a decimal number as a desk calculator would: as an integer if 
	 * there are no fractional digits; else with up to the given precision
	 * (number of decimal digits), but no trailing zeros.</p>
     * If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtNormal(double value, int prec) {
    	int i; long n;
    	for (i = 0, n = 1; i < prec; ++i, n *= 10) {
    		double svd = value * n;
    		long svl = Math.round(svd);
    		if (Math.abs(svd - svl) < Double.MIN_NORMAL) {
    			StringBuffer sb = new StringBuffer(Long.toString(svl));
    			int nDigits = (svl < 0 ? sb.length()-1 : sb.length());  // adjust length for minus
    			int prefixOffset = (svl < 0 ? 1 : 0);      
    			for (int j = nDigits; j < i; ++j)            // insert zeros between digits & point 
    				sb.insert(prefixOffset, '0');
    			if (i > 0) {                             // insert decimal point at scaled position
    				int decOffset = (sb.length()-i < prefixOffset ? prefixOffset : sb.length()-i);
    				String dec = ".";
    				if (decOffset <= 0 || !Character.isDigit(sb.charAt(decOffset-1)))
    					dec = "0.";                      // ensure 1 digit to left of decimal point
    				sb.insert(decOffset, dec);
    			}
    			return stripTrailingZeros(sb);
    		}
    	}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		String fmt = "%."+prec+"f";
		pw.printf(fmt, value);
		String result = stripTrailingZeros(sw.getBuffer());    	
    	if (trace.getDebugCode("functions")) trace.outNT("functions", "fmtNormal(double "+value+", int "+prec+")->"+result);
		return result;
    }

    /**
     * Strip trailing zeros to the right of the decimal point. If no digits to the
     * right of the decimal point remain, then strip the decimal point, too. Pay
     * no attention to anyone talking about significant digits. 
     * @param sb buffer to scan
     * @return substring of sb
     */
    private static String stripTrailingZeros(StringBuffer sb) {
    	if (sb == null)
    		return null;
    	int dp = sb.lastIndexOf(".");  // position of decimal point
    	if (dp < 0)
    		return sb.toString();
    	int end = sb.length();
    	for (end--; dp < end && sb.charAt(end) == '0'; end--)
    		;
    	if (dp < end)               // stopped on nonzero digit to right of decimal
    		end++;                  // so don't strip it
    	return sb.substring(0, end);
	}

	/**
     * <p>Format a decimal number as a desk calculator would: as an integer if 
	 * there are no fractional digits; else with up to {@value #DEFAULT_PRECISION}
	 * decimal digits, but no trailing zeros.</p>
     * If precision is zero, omits the decimal point (i.e., rounds to integer).
     * @param value decimal number to format
     * @return String format of number
     */
    public String fmtNormal(double value) {
    	return fmtNormal(value, defaultPrecision);
    }

    /**
     * <p>Format a decimal number as a desk calculator would: as an integer if 
	 * there are no fractional digits; else with up to the given precision
	 * (number of decimal digits), but no trailing zeros.</p>
     * If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @param prec number of digits after the decimal point
     *  @return String format of number
     */
    public String fmtNormal(Object value, Object prec) {
    	Double dv = CTATFunctions.toDouble(value);
    	if (dv == null)
    		return "";
    	Double pv = CTATFunctions.toDouble(prec);
    	if (pv == null)
    		return fmtNormal(dv.doubleValue());
    	else
    		return fmtNormal(dv.doubleValue(), (int) Math.round(pv));
    }

    /**
     * <p>Format a decimal number as a desk calculator would: as an integer if 
	 * there are no fractional digits; else with up to {@value #DEFAULT_PRECISION}
	 * decimal digits, but no trailing zeros.</p>
     * If precision is zero, omits the decimal point (i.e., rounds to integer).
     *  @param value decimal number to format
     *  @return String format of number
     */
    public String fmtNormal(Object value) {
    	Double dv = CTATFunctions.toDouble(value);
    	if (dv == null)
    		return "";
    	return fmtNormal(dv.doubleValue());
    }

    /**
	 * Test harness. Print each argument with 2 decimal places.
	 * With <b>-p <i>prec</i></b> option, uses this precision instead. E.g.:
	 *     fmtNormal -p 4 5.2349283 42.523282 -293.2398 
	 * @param args numbers to format
	 */
	public static void main(String[] args) {
		trace.addDebugCodes(System.getProperty("DebugCodes"));
		fmtNormal t = new fmtNormal();
		int i = 0;
		int prec = 2;
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			char option = args[i].charAt(1);
			switch (option) {
			case 'p':
				prec = Integer.parseInt(args[++i]);
				t.setDefaultPrecision(prec);
				break;
			case 'D':
				Double pD = new Double(Double.parseDouble(args[++i]));
				Double vD = new Double(Double.parseDouble(args[++i]));
				trace.out("fmtNormal("+vD+", "+pD+")="+t.fmtNormal(vD, pD));
				return;
			default:
				System.err.println("unknown option -"+option);
			}
		}
		for (; i < args.length; ++i) {
			String arg = args[i];
			trace.out("fmtNormal("+arg+")="+t.fmtNormal(arg));
		}
	}
	
	/**
	 * @return the defaultPrecision
	 */
	public int getDefaultPrecision() {
		return defaultPrecision;
	}
	/**
	 * @param defaultPrecision the defaultPrecision to set
	 */
	public void setDefaultPrecision(int defaultPrecision) {
		this.defaultPrecision = defaultPrecision;
	}

}
