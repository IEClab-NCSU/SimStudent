package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 *  Format a decimal number to have 2 digits to the right of the decimal.
 */
public class fmtDollar extends fmtDecimal {
	
    /**
     *  <p>Format a decimal number to have 2 digits to the right of the decimal.</p>
     *  If the flags argument contains an "I" then return integer dollar amounts 
     *  without the trailing ".00". If flags contains a "D" then prefix a dollar sign.
     *  @param value decimal number to format
     *  @param flags for integer, dollar sign (case-insensitive)
     *  @return String format of number
     */
    public String fmtDollar(double value, String flags) {
    	StringBuffer result = new StringBuffer(fmtDecimal(value, 2));
    	if (flags == null)
    		return result.toString();
    	flags = flags.toLowerCase();
    	if (flags.contains("i") && result.indexOf(".00") == result.length()-3)
    		result.replace(result.length()-3, result.length(), "");
    	if (flags.contains("d"))
    		result.insert(0, '$');
    	return result.toString();
    }

    /**
     *  <p>Format a decimal number to have 2 digits to the right of the decimal.</p>
     *  If the flags argument contains an "I" then return integer dollar amounts 
     *  without the trailing ".00". If flags contains a "D" then prefix a dollar sign.
     *  @param value decimal number to format
     *  @param flags for integer, dollar sign (case-insensitive)
     *  @return String format of number
     */
    public String fmtDollar(Number value, String flags) {
    	return fmtDollar(value.doubleValue(), flags);
    }

    /**
     *  <p>Format a decimal number to have the given precision.</p>
     *  If the flags argument contains an "I" then return integer dollar amounts 
     *  without the trailing ".00". If flags contains a "D" then prefix a dollar sign.
     *  @param value decimal number to format
     *  @param flags for integer, dollar sign (case-insensitive)
     *  @return String format of number
     */
    public String fmtDollar(String sValue, String flags) {
    	Number value = CTATFunctions.toNumber(sValue);
    	return fmtDollar(value, flags);
    }
	
    /**
     *  <p>Format a decimal number to have 2 digits to the right of the decimal.</p>
     *  @param value decimal number to format
     *  @return String format of number
     */
    public String fmtDollar(double value) {
    	return fmtDecimal(value, 2);
    }

    /**
     *  <p>Format a decimal number to have 2 digits to the right of the decimal.</p>
     *  @param value decimal number to format
     *  @return String format of number
     */
    public String fmtDollar(Number value) {
    	return fmtDecimal(value.doubleValue(), 2);
    }

    /**
     *  <p>Format a decimal number to have 2 digits to the right of the decimal.</p>
     *  Format a decimal number to have the given precision.
     *  @param value decimal number to format
     *  @return String format of number
     */
    public String fmtDollar(String sValue) {
    	return fmtDecimal(sValue, 2);
    }

    /**
     * Print a usage message and exit.
     * @param intro optional prefix to message
     */
    public static void usageExit(String intro) {
    	if (intro != null && intro.length() > 0)
    		System.err.printf("%s. ", intro);
		System.err.println("Usage:\n"+
				fmtDollar.class.getName()+" [-i] [-d] value...\n"+
				"where--\n"+
				"  -i means strip \".00\" from whole dollar ammounts;\n"+
				"  -d means prefix a dollar (\"$\") sign;\n"+
				"  value... number(s) to format.\n");
		System.exit(2);
    }
    
	/**
	 * Test harness. Print each argument with 2 decimal places.
	 *     fmtDollar [-i] [-d] 5.2349283 42.523282 -293.2398 
	 * @param args numbers to format
	 */
	public static void main(String[] args) {
		trace.addDebugCodes(System.getProperty("DebugCodes"));
		fmtDollar t = new fmtDollar();
		StringBuffer flags = new StringBuffer();
		int i = 0;
		for (i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
			char option = args[i].charAt(1);
			switch (option) {
			case 'h': case 'H':
				usageExit("Help");  // never returns
				break;
			case 'i': case 'I':
			case 'd': case 'D':
				flags.append(option);
				break;
			default:
				usageExit("Unknown option -"+option);  // never returns
			}
		}
		if (i >= args.length)
			usageExit("Missing argument(s)");  // never returns

		if (flags.length() < 1)
			flags = null;
		for (; i < args.length; ++i) {
			String arg = args[i];
			System.out.printf("fmtDollar(%10s%s)=%10s\n", arg,
					flags == null ? "" : ", \""+flags+"\"",
					flags == null ? t.fmtDollar(arg) : t.fmtDollar(arg, flags.toString()));
		}
	}

}
