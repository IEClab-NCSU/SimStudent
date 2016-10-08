package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Return true if input matches number numerically and also has the same precision.
 */
public class matchWithPrecision {
    // check to see if input specifies a number
    private static final String Digits     = "(\\p{Digit}+)";
    private static final String HexDigits  = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally 
    // signed decimal integer.
    private static final String Exp        = "[eE][+-]?"+Digits;
    private static final String fpRegex    = ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                                               "[+-]?(" + // Optional sign character
                                               "NaN|" +           // "NaN" string
                                               "Infinity|" +      // "Infinity" string

                                               // A decimal floating-point string representing a finite positive
                                               // number without a leading sign has at most five basic pieces:
                                               // Digits . Digits ExponentPart FloatTypeSuffix
                                               // 
                                               // Since this method allows integer-only strings as input
                                               // in addition to strings of floating-point literals, the
                                               // two sub-patterns below are simplifications of the grammar
                                               // productions from the Java Language Specification, 2nd 
                                               // edition, section 3.10.2.

                                               // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                                               "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                                               // . Digits ExponentPart_opt FloatTypeSuffix_opt
                                               "(\\.("+Digits+")("+Exp+")?)|"+

                                               // Hexadecimal strings
                                               "((" +
                                               // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                                               "(0[xX]" + HexDigits + "(\\.)?)|" +

                                               // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                                               "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                                               ")[pP][+-]?" + Digits + "))" +
                                               "[fFdD]?))" +
                                               "[\\x00-\\x20]*");// Optional trailing "whitespace"
    public static final Pattern fpPattern = Pattern.compile(fpRegex);

    private void printMatches(String input, String label) {
        Matcher m = fpPattern.matcher(input);

        System.out.println(input);
        if (m.matches()) {
            System.out.println(input + " double value = " + Double.valueOf(input));
            System.out.println(input + " significant digits = " + significantDigits(m));
        } else
            System.out.println("no " + label + " match found");
        System.out.println("\n\n\n");
    }

    private int significantDigits(Matcher m) {
        int count = 0;
        final int[] digitGroups = {5, 7, 12};
        boolean foundSignificant = false;

        for (int i: digitGroups) {
            String digitsGroup = m.group(i);
            if (digitsGroup!=null) {
                for (char c: digitsGroup.toCharArray()) {
                    if (c!='0')
                        foundSignificant = true;
                    if (foundSignificant)
                        count++;
                }
            }
        }

        return count;
    }
    
    /**
     * <p>Return true if input matches number numerically and also has the same precision.</p>
	 * @param input
	 * @param number
	 * @return true if values and number of significant digits match
     */
    public boolean matchWithPrecision(String input, String number) {
        Matcher inputMatcher = fpPattern.matcher(input), numberMatcher = fpPattern.matcher(number);

        return (inputMatcher.matches() && numberMatcher.matches() &&
                Double.compare(Double.valueOf(input), Double.valueOf(number))==0 &&
                significantDigits(inputMatcher)==significantDigits(numberMatcher));
    }

    /**
     * <p>Return true if input matches number numerically and also has the same precision.</p>
	 * @param input
	 * @param number
	 * @return true if values and number of significant digits match
     */
    public boolean matchWithPrecision(String input, double number) {
        return matchWithPrecision(input, Double.toString(number));
    }

    /**
     * <p>Return true if input matches number numerically and also has the same precision.</p>
	 * @param input
	 * @param number
	 * @return true if values and number of significant digits match
     */
    public boolean matchWithPrecision(double input, String number) {
        return matchWithPrecision(Double.toString(input), number);
    }

    /**
     * <p>Return true if input matches number numerically and also has the same precision.</p>
	 * @param input
	 * @param number
	 * @return true if values and number of significant digits match
     */
    public boolean matchWithPrecision(double input, double number) {
        return matchWithPrecision(Double.toString(input), Double.toString(number));
    }
        
    public void checkMatch(String input, String number) {
        boolean check = matchWithPrecision(input, number);
        System.out.println("matchWithPrecision(" + input + ", " + number + ") = " + check);
    }

    public static void main(String [] args) throws Exception {
        matchWithPrecision withPrec = new matchWithPrecision();
        withPrec.checkMatch("0.9", "0.900");
        withPrec.checkMatch("0.09", "9E-2");
        withPrec.checkMatch("0.09", "9.000E-2");
        withPrec.checkMatch("0.9", ".9");
        withPrec.checkMatch("0.9", "0.9");
        withPrec.checkMatch("9E-1", "90E-2");
    }
}
