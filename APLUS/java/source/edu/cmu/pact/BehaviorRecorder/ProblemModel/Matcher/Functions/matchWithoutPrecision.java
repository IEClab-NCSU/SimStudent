package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.regex.Matcher;

import edu.cmu.pact.Utilities.trace;

/**
 * Return true if input matches number exactly or matches after rounding if input is more precise than number.
 */
public class matchWithoutPrecision {

    private int afterDecimalDigits(Matcher m) {
        final int[] decimalDigitGroups = {7, 12};
        String digitsGroup;

        int afterDecimal = 0;
        for (int i: decimalDigitGroups) {
            digitsGroup = m.group(i);
            if (digitsGroup!=null)
                afterDecimal = digitsGroup.length();
        }

        return afterDecimal;
    }

    private int meaningfulDigits(Matcher m) {
        final int[] decimalDigitGroups = {7, 12};
        String digitsGroup;

        int beforeDecimal = 0;
        digitsGroup = m.group(5);
        if (digitsGroup!=null) {
            boolean foundSignificant = false;
            for (char c: digitsGroup.toCharArray()) {
                if (c!='0')
                    foundSignificant = true;
                if (foundSignificant)
                    beforeDecimal++;
            }
        }

        int afterDecimal = 0;
        for (int i: decimalDigitGroups) {
            digitsGroup = m.group(i);
            if (digitsGroup!=null)
                afterDecimal = digitsGroup.length();
        }

        return beforeDecimal + afterDecimal;
    }
    
    private int afterDecimalDigits(String input) {
        return Math.max(afterDecimalDigits(fpMatcher(input)), afterDecimalDigits(canonicalFpMatcher(input)));
    }
    
    private int meaningfulDigits(String input) {
        return Math.max(meaningfulDigits(fpMatcher(input)), meaningfulDigits(canonicalFpMatcher(input)));
    }
    
    public static double round(double val, int places) {
        long factor = (long)Math.pow(10,places);

        // Shift the decimal the correct number of places
        // to the right.
        val = val * factor;

        // Round to the nearest integer.
        long tmp = Math.round(val);

        // Shift the decimal the correct number of places
        // back to the left.
        return (double)tmp / factor;
    }

    private Matcher fpMatcher(String input) {
        Matcher m = matchWithPrecision.fpPattern.matcher(input);

        if (m.matches())
            return m;

        return null;
    }

    private Matcher canonicalFpMatcher(String input) {
        return (fpMatcher(input)!=null) ? fpMatcher(Double.toString(Double.valueOf(input))) : null;
    }

    /**
     * <p>Return true if input matches number exactly or matches after rounding if input is more precise than number.</p>
	 * @param input
	 * @param number
	 * @return true if values match
     */
    public boolean matchWithoutPrecision(String input, String number) {
        Matcher inputMatcher = canonicalFpMatcher(input), numberMatcher = canonicalFpMatcher(number);

        if (inputMatcher!=null && numberMatcher!=null &&
            inputMatcher.matches() && numberMatcher.matches()) {
            int inputDigits = meaningfulDigits(input), numberDigits = meaningfulDigits(number);
			int numberADDigits = afterDecimalDigits(number);
			if (trace.getDebugCode("functions")) trace.out("functions", "inputDigits "+inputDigits+", numberDigits "+numberDigits+
					  ", numberADDigits "+numberADDigits);

            double numberValue = Double.valueOf(number), inputValue = Double.valueOf(input);
			if (trace.getDebugCode("functions")) trace.out("functions", "inputValue "+inputValue+", numberValue "+numberValue);

			inputValue = (numberDigits<inputDigits ? round(inputValue, numberADDigits) : inputValue);
			int compareResult = Double.compare(numberValue, inputValue);
			if (trace.getDebugCode("functions")) trace.out("functions", "rounded inputValue "+inputValue+", compareResult "+compareResult);

            return compareResult==0;
        }

        return false;
    }

    /**
     * <p>Return true if input matches number exactly or matches after rounding if input is more precise than number.</p>
	 * @param input
	 * @param number
	 * @return true if values match
     */
    public boolean matchWithoutPrecision(String input, double number) {
        return matchWithoutPrecision(input, Double.toString(number));
    }

    /**
     * <p>Return true if input matches number exactly or matches after rounding if input is more precise than number.</p>
	 * @param input
	 * @param number
	 * @return true if values match
     */
    public boolean matchWithoutPrecision(double input, String number) {
        return matchWithoutPrecision(Double.toString(input), number);
    }

    /**
     * <p>Return true if input matches number exactly or matches after rounding if input is more precise than number.</p>
	 * @param input
	 * @param number
	 * @return true if values match
     */
    public boolean matchWithoutPrecision(double input, double number) {
        return matchWithoutPrecision(Double.toString(input), Double.toString(number));
    }
        
    public void checkMatch(String input, String number) {
        boolean check = matchWithoutPrecision(input, number);
        System.out.println("matchWithoutPrecision(" + input + ", " + number + ") = " + check);
    }

    public static void main(String [] args) throws Exception {
        matchWithoutPrecision withoutPrec = new matchWithoutPrecision();
        withoutPrec.checkMatch("8.81", "8.8");
        withoutPrec.checkMatch("0.881", "0.88");
        withoutPrec.checkMatch("0.911", "0.900");
        withoutPrec.checkMatch("0.911", "0.91");
        withoutPrec.checkMatch("0.915", "0.91");
        withoutPrec.checkMatch("0.916", "0.91");
        withoutPrec.checkMatch("0.09", "9E-2");
        withoutPrec.checkMatch("0.091", "9E-2");
        withoutPrec.checkMatch("0.09", "9.000E-2");
        withoutPrec.checkMatch("0.9", ".9");
        withoutPrec.checkMatch("0.9", "0.9");
        withoutPrec.checkMatch("0.89", "0.9");
        withoutPrec.checkMatch("0.84", "0.9");
        withoutPrec.checkMatch("9E-1", "90E-2");
    }
}
