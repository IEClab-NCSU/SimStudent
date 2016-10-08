package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Returns the numerator substring in a fraction string "nnn/ddd": e.g. returns "13" from "13/687".
 */
public class getNumerator {
    /**
     *  <p>Returns the the numerator in a fraction string "\d/\d".</p>
     *  e.g. "13/687" returns "13"
     *  Used by: PieChart, FractionBar, Numberline
     */

      public String getNumerator(String fraction) {
	String num = "0";        
	if (fraction.indexOf('/')>-1)		
		num = fraction.substring(0,fraction.indexOf('/'));
        return num;
    }
}
