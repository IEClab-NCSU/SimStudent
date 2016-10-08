package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import edu.cmu.pact.Utilities.trace;

public class MatcherFactory {

    static final String[] RANGE_CLASS = {
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RangeMatcher",
        "pact.BehaviorRecorder.Matcher.RangeMatcher",
        "RangeMatcher"
    };

    static final String[] REGEX_CLASS = {
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.RegexMatcher",
        "RegexMatcher"
    };

    static final String[] WILDCARD_CLASS = {
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.WildcardMatcher",
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.SimpleRegexMatcher",
        "WildcardMatcher"
    };

    static final String[] ANY_CLASS = {
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.AnyMatcher",
        "pact.BehaviorRecorder.Matcher.AnyMatcher",
        "AnyMatcher"
    };
    
    static final String[] EXACT_CLASS = {
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExactMatcher",
        "ExactMatcher"
    };
    
    static final String[] EXPRESSION_CLASS = {
        "edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.ExpressionMatcher",
        "ExpressionMatcher"
    };
    
    private static final String[][] ALL_MATCHERS = {
        EXPRESSION_CLASS,
        EXACT_CLASS,
        ANY_CLASS,
        WILDCARD_CLASS,
        REGEX_CLASS,
        RANGE_CLASS
    };
    
    
    /**
     * Builds a matcher using reflection
     * @param matcherClassType name of the Java class we want to instantiate
     * @return a Matcher
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     */
	public static Matcher buildSingleMatcher(String matcherClassType, boolean concat, int vector) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
	    String className = getClassForName (matcherClassType);
        trace.out ("functions", " class name = " + className + " type = " + matcherClassType);
        
        Class[] args = new Class[3];
        args[0] = boolean.class;
        args[1] = int.class;
        args[2] = String.class;
        Constructor c = java.lang.Class.forName(className).getConstructor(args);
        return (Matcher)c.newInstance(concat, vector, null);
	}
	
	public static Matcher buildMatcher(String matcherClassType) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		String className = getClassForName (matcherClassType);
        trace.out ("functions", " class name = " + className + " type = " + matcherClassType);
        return (Matcher) java.lang.Class.forName(className).newInstance();
	}
	
	/**
	 * Builds a matcher for a Vector Match from the matcher type name
	 * @param matcherType - name of the matcher
	 * @param value - the matcher's toString
	 * @param concat - true if this is a concatenation Vector Match
	 * @return - a complete, functional matcher
	 */
	public static Matcher createSingleMatcher(String matcherType, boolean concat, int vector, String value)
	{
		Matcher m = null;
		
		if (trace.getDebugCode("functions")) trace.outln("functions", "createMatcher: " + matcherType);
		if (matcherType.equals (Matcher.ANY_MATCHER))
			m = new AnyMatcher(concat, vector, value);
		else if (matcherType.equals (Matcher.EXACT_MATCHER))
			m =  new ExactMatcher(concat, vector, value);
		else if (matcherType.equals (Matcher.RANGE_MATCHER))
			m =  new RangeMatcher(concat, vector, value);
		else if (matcherType.equals (Matcher.REGULAR_EXPRESSION_MATCHER))
			m = new RegexMatcher(concat, vector, value);
		else if (matcherType.equals (Matcher.WILDCARD_MATCHER))
			m = new WildcardMatcher(concat, vector, value);
		else if (matcherType.equals (Matcher.EXPRESSION_MATCHER))
			m = new ExpressionMatcher(concat, vector, value);
        else
        	if (trace.getDebugCode("functions")) trace.outln("functions", "matcher for " + matcherType + " not found.");
		
		return m;
	}

    /**
     * @param matcherType
     * @return
     */
    private static String getClassForName(String matcherType) {
        for (int i = 0; i < ALL_MATCHERS.length; i++) {
            String[] names = ALL_MATCHERS[i];
            for (int j = 0; j < names.length; j++) {
                if (names[j].equals (matcherType))
                    return names[0];
            }
        }
        return null;
    }
 
}
