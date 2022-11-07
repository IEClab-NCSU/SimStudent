package edu.cmu.pact.miss.minerva_3_1;

/*
 * 	Andrew Lee
 * 	May 02 2009
 * 	Wrapper class for each "step" during categorization
 * 	into Student/Problem/Step objects.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
 * 	Pretty straightforward; stores strings and has accessor methods
 */

/*
 * Header for SE CWCTC 2009 (CL)
 * 
 Sample Name
 Anon Student Id
 Session Id
 Time
 Time Zone
 Duration (sec)
 Student Response Type
 Student Response Subtype
 Tutor Response Type
 Tutor Response Subtype
 Level(Unit)
 Level(Section)
 Problem Name
 Step Name
 Attempt At Step
 Outcome
 Selection
 Action
 Input
 Feedback Text
 Feedback Classification
 Help Level
 Total # Hints
 Condition Name
 Condition Type
 KC(Default)
 KC Type
 KC Category(Default)
 KC(Single-KC)
 School
 KC Category(Single-KC)
 Class
 */

public class InputCapsule {

	Map<String, String> values = new HashMap<String, String>();
	
	public InputCapsule(String inputLine, String header) throws Exception {
		
	    //trace.out("header: " + header);
	    //trace.out("inputLine: " + inputLine);
	    
	    
		StringTokenizer headerTk = new StringTokenizer(header, "\t");
		// a inputLine might contain null strings
		StringTokenizer inputTk = new StringTokenizer(inputLine, "\t", true);

		/*
		trace.out("header has " + headerTk.countTokens() + " tokens.");
		trace.out("input  has " + inputTk.countTokens() + " tokens.");
		
		if (inputTk.countTokens() != headerTk.countTokens()) {
			throw new Exception("incompatible input: " + inputLine);
		}
		*/
		
		while (inputTk.hasMoreTokens()) {
			String label = headerTk.nextToken();
			
			String value = inputTk.nextToken();
			// inputTk might contain null ("") strings
			if ("\t".equals(value)) {
				// if so, set the value to ""
				value = "";
			} else {
				// otherwise skip the next delimiter, if any
				if (inputTk.hasMoreTokens())
					inputTk.nextToken();
			}
			// trace.out("InputCapsule reading <" + label + ", " + value + ">");
			
			values.put(label, value);
		}
	}

	public String getValue(String label) throws Exception {
		
		if (!values.containsKey(label)) {
			throw new Exception("invalid header: " + label);
		}
		
		return values.get(label);
	}

    @Override
    public String toString() {
        return "InputCapsule [values=" + values + "]";
    }
	
	
	
}
