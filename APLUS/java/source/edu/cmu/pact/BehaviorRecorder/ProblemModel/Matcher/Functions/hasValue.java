/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.pact.Utilities.trace;

/**
 *  Return true if all arguments are non-null.
 */
public class hasValue {
	
	/** Match the word "null" regardless of case, whether or not parenthesized. */
	private static final Pattern NullPattern =
		Pattern.compile("\\s*(\\(\\s*[nN][uU][lL][lL]\\s*\\))|([nN][uU][lL][lL])\\s*");
	
    /**
     *  Return true if all arguments are non-null. Returns false if called with no arguments at all.
     *  An argument is null if it is the null constant or its toString() representation
     *  matches the word "null" regardless of case, whether or not parenthesized.
     *  @param values any number of arguments of any type
     *  @return true if all arguments are non-null
     */
    public boolean hasValue(Object... values) {
    	if (values == null)        // happens on hasValue(null)
    		return false;
    	boolean hasArgs = false;   // whether we have any arguments at all
        for (Object value: values) {
        	hasArgs = true;
        	if (trace.getDebugCode("functions")) trace.out("functions", "hasValue("+value+")");
        	if (value == null)
        		return false;
        	Matcher m = NullPattern.matcher(value.toString());
        	if (m.matches())
        		return false;
        }
        return hasArgs;  // avoid returning true if no args
    }

    public static void main(String[] args) throws Exception {
        hasValue hv = new hasValue();
        int listLen = -1;
    	if (args.length < 1) {
    	   	System.out.printf("\"\" = %b\n", null, hv.hasValue());    		
    		return;
    	}
        try {
        	if ((listLen = Integer.parseInt(args[0])) > 0) {
        		listLen = Math.min(listLen, args.length);
        		String[] argsList = Arrays.copyOfRange(args, 0, listLen);
            	System.out.printf("\"%-20s\" = %b\n", Arrays.asList(argsList).toString(),
            			hv.hasValue((Object[])argsList));
        	} else { // test null arg
        	   	System.out.printf("\"%-20s\" = %b\n", null, hv.hasValue((Object[])null));
        	   	listLen = 0;
        	}
        }
        catch (NumberFormatException nfe) {
        	listLen = 0; // fall through to test single args
        }
        for (int i = listLen; i < args.length; ++i)
        	System.out.printf("\"%-20s\" = %b\n", args[i], hv.hasValue(args[i]));
    }    
}
