package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Returns the denominator substring in a fraction string "nnn/ddd": e.g. returns "687" from "13/687".
 */
public class getDenominator {
    /**
     *  <p>Returns the the denominator in a fraction string "\d/\d".</p>
     *  e.g. "13/687" returns "687"
     *  Used by: PieChart, FractionBar, Numberline
	 * @param fraction
	 * @return denominator substring
     */

      public String getDenominator(String fraction) {
	String denom = "0";
	Integer endIndex = (fraction.indexOf(';')>-1)?fraction.indexOf(';'):fraction.length();  
	if (fraction.indexOf('/')>-1)		
		denom = fraction.substring(fraction.indexOf('/')+1, endIndex);
        return denom;
    }
}
