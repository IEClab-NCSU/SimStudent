package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;
import java.util.HashMap;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

/**
 * Tell whether a regular expression accepts a test string.
 */
public class regExMatch {

	/** Cache of compiled regular expressions reusable for later matches. Key is regex string + flags*/
    private static HashMap<String,Pattern> savedPatterns = new HashMap<String,Pattern>();
    
    /**
     * <p>Tell whether a regular expression accepts a test string.</p>
	 * @param regex regular expression
	 * @param teststr test string 
	 * @return true if teststr is accepted by the regex
     */
    public boolean regExMatch(String regex, String teststr) {
        return regExMatch(regex, teststr, null);
    }

    /**
     * <p>Tell whether a regular expression accepts a test string.</p>
     *  Also stores the regex pattern p in the HashTable savedPatterns, 
     *  to minimize computational processing
	 * @param regex regular expression
	 * @param teststr test string 
	 * @param flags case-insensitive letters for flags to {@link Pattern#compile(String, int)}:
	 * <ul>
	 *     <li>'i': {@link Pattern#CASE_INSENSITIVE};</li>
	 *     <li>'m': {@link Pattern#MULTILINE};</li>
	 *     <li>'d': {@link Pattern#DOTALL};</li>
	 *     <li>'u': {@link Pattern#UNICODE_CASE};</li>
	 *     <li>'e': {@link Pattern#CANON_EQ};</li>
	 *     <li>'l': {@link Pattern#UNIX_LINES};</li>
	 *     <li>'t': {@link Pattern#LITERAL};</li>
	 *     <li>'c': {@link Pattern#COMMENTS};</li>
	 * </ul>
	 * @return true if teststr is accepted by the regex
     */
    public boolean regExMatch(String regex, String teststr, String flags) {
        Pattern p;
		synchronized(savedPatterns)
		{
			String key = makeKey(regex, flags);
			p=savedPatterns.get(key);
			if (p==null) 
			{
                int flagBits = parseFlags(regex, flags);
				p = Pattern.compile(regex, flagBits);
				savedPatterns.put(key,p);
			}
		}
        return p.matcher(teststr).matches();
    }

    /**
     * Create a key for the table {@link #savedPatterns}.
     * @param regex pattern to match
     * @param flags flag characters
     * @return regex+"_0x"+flagsAsHexInt
     */
    private String makeKey(String regex, String flags) {
        int flagBits = parseFlags(regex, flags);
		StringBuffer sb = new StringBuffer(regex);
		sb.append("_0x").append(Integer.toHexString(flagBits));
		return sb.toString();
	}

	/**
     * Convert a string of flag characters into flag bits for {@link Pattern#compile(String, int)}.
     * @param regex needed here only for debug message
     * @param flags flags argument to {@link #regExMatch(String, String, String)}
     * @return bit-encoded integer
     */
	private int parseFlags(String regex, String flags) {
		int result = 0;
		if (flags == null)
			return result;
		flags = flags.toLowerCase();
		char c = ' ';
		for (int i = 0; i < flags.length(); ++i) {
			switch (flags.charAt(i)) {
			case 'i': result |= Pattern.CASE_INSENSITIVE; break;
			case 'm': result |= Pattern.MULTILINE; break;
			case 'd': result |= Pattern.DOTALL; break;
			case 'u': result |= Pattern.UNICODE_CASE; break;
			case 'e': result |= Pattern.CANON_EQ; break;
			case 'l': result |= Pattern.UNIX_LINES; break;
			case 't': result |= Pattern.LITERAL; break;
			case 'c': result |= Pattern.COMMENTS; break;
			default:
				trace.err("regExMatch("+regex+","+flags+"): unknown flag character '"+c+"' at position "+i);
			}
		}
		return result;
	}
	
	/**
	 * Test harness. With second set of arguments, can test caching.
	 * @param args [-imdueltc] regex teststr... [[-imdueltc] regex teststr...]
	 * @return exit code 0 if all teststr args match
	 */
	public static void main(String[] args) {
		if (args.length < 2 || args[0].equalsIgnoreCase("-h")) {
			System.err.printf("Too few arguments. Usage:\n  %s -[imdueltc] regex teststr... [-[imdueltc] regex teststr...]\n",
					(new regExMatch()).getClass().getSimpleName());
			System.err.println("where--\n"+
					"  [imdueltc] are flag(s) for case-insensitive, etc.;\n"+
					"  regex is the regular expression (often you'll want to quote this);\n"+
					"  teststr... are string(s) to test for matching.\n"+
					"If you repeat the arguments after a hyphen, you can retest with the same Pattern cache.");
			System.exit(2);
		}
		int a = 0;
		int result = 0;
		result |= testOnceOrAgain(result, args, a);
		System.out.printf("\nExiting with pattern cache size %d:\n  %s\n", savedPatterns.size(), savedPatterns);
		System.exit(result);
	}
	

	/**
	 * Run with one set of arguments.
	 * @param result for tracking final result
	 * @param args command-line args
	 * @param a current index in args[]
	 * @return
	 */
	private static int testOnceOrAgain(int result, String[] args, int a) {
		regExMatch rem = new regExMatch();
		String flags = null;
		if (args[a].startsWith("-"))
			flags = args[a++].substring(1);
		System.out.printf("Flags '"+flags+"':\n");
		String regex = args[a++];
		boolean match = false;
		for ( ; a < args.length && !args[a].startsWith("-"); a++) {
			System.out.printf("  %s %-7s %s\n", regex,
					(match = rem.regExMatch(regex, args[a], flags)) ? "accepts" : "rejects", args[a]);
			if (!match)
				result |= 1;
		}
		if (a < args.length)
			result |= testOnceOrAgain(result, args, a);
		return result;
	}
}
