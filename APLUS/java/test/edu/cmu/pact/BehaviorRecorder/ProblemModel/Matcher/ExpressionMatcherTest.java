package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.CTATExpressionParser;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.CTATExpressionSemantic;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;


public class ExpressionMatcherTest extends TestCase {
	
	private static String EVALUATE = "EVALUATE";
    private static String SIMPLIFY_BASIC = "SIMPLIFY_BASIC";
    private static String SIMPLIFY_BASIC_TERMS = "SIMPLIFY_BASIC_TERMS";
	private static String SIMPLIFY_BASIC_TERMS_SAME_ORDER = "SIMPLIFY_BASIC_TERMS_SAME_ORDER";
    private static String STRICT_BASIC_TERMS = "STRICT_BASIC_TERMS";
	private static String STRICT_BASIC_TERMS_SAME_ORDER = "STRICT_BASIC_TERMS_SAME_ORDER";
    private static String SIMPLIFY_COMPLEX = "SIMPLIFY_COMPLEX";
    
	VariableTable vt;
	CTATExpressionParser expressionParser;
	
    public static Test suite() {
        return new TestSuite(ExpressionMatcherTest.class);
    }

    protected void setUp() {
    	vt = new VariableTable();
    	ExpressionTreeProperties properties  = new ExpressionTreeProperties();
    	properties.variableTable = vt;
    	vt.put("x", new Double(1));
    	vt.put("y", new Double(1));
    	//vt.put("a", new Double(1));
		vt.put("z", new Integer(2));
		vt.put("d", new Double(3.0));
		vt.put("str", "abcdefg");
		if (trace.getDebugCode("functions"))
			trace.out("functions", "Starting ExpressionMatcherTest");
		
		expressionParser = new CTATExpressionParser(properties);
    }

    /**
     * Replace calls in this method to isolating single tests for debugging.
     */
    public void testNewExpressionMatcher() 
    {
//    	parseAndCompare(new String[] {"--6x", "6x"}, STRICT_BASIC_TERMS);
    	parseAndCompare(new String[] {"3 --6x", "3 + 6x"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"1x", "x"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"x*1", "x"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"x/1", "1x/1"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"-x/1", "-1x/1"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + 1)", "2(1x + 1)"}, STRICT_BASIC_TERMS_SAME_ORDER);
    }
    
    public void testRightFactor() {
		isInvalidAlg("x2");
		isInvalidAlg("x2^3");    	
		isInvalidAlg("2^3 3");    	
		isValidAlg("(x+1)2");	
		isValidAlg("3^(4*2)5");
		isValidAlg("xx^3^4");    	
		isInvalidAlg("x2^3^4");    	
    }

    /**
     * JUnit test for the new {@link CTATExpressionParser}.
     */
    public void testNewExpressionMatcherAll() 
    {		
		isValidAlg("x");
		isValidAlg("-x");
		isValidAlg("- x");
		isValidAlg("2x +2^4x");
		isValidAlg("-2x(2^4x)/7*y^2");
		isValidAlg("xy");
		isValidAlg("(x+1)2");
		isValidAlg("2(x+1)");
		
		isInvalidAlg("x2");
		isInvalidAlg("x2^3");
		isInvalidAlg("x 2");
		isInvalidAlg("-2x(2^4x)/7*y^");
		isInvalidAlg("+x");

		/*What doesn't currently parse:
			
			parseAndCompare(new String[] {"(x+1)^3", "((x+1)(x+1)(x+1))(x-3)/(x-3)"}, SIMPLIFY_COMPLEX);
			^We currently don't apply distribution to exponents... what do do if its (x+z)^100?
			
			parseAndCompare(new String[] {"",""}, SIMPLIFY_BASIC) => Fails to parse into an ExpressionSyntaxNode. 
			
			parseAndCompare(new String[] {"2/0", "x/0/x"}, SIMPLIFY_COMPLEX); => "x/0/x" parses into x*infinity. 
			x*Infinity is actually a valid simplification, however it should farther simplify to just Infinity.
			
			
			parseAndCompare(new String[] {"(1/x)*((x+1+1))", "1+2/x"}, SIMPLIFY_COMPLEX);
			^This involves distribution of divisors through products. Possible to do but ugly.
		*/
		/*Tests that involve full simplification */
		
		//parseAndCompare(new String[] {"2/0", "x/0/x"}, SIMPLIFY_COMPLEX);
		
		parseAndCompare(new String[] {"x*x", "x^2"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"1", "1+0"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"0/0", "0/0*0", "(-2)^.5"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"(x+1)*((x+1+1))", "-1+x*x+x+x+x+3"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"1.0", "1"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"(x+1)*((x+1+1))", "-1+x*x+x+x+x+3"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2+2.0", "4", "4.0"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"1/0", "5/0"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"-4x/(2x*2)", "-1"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"x^3", "x^3/1.0","x^3/(3-(2+0))"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"xyyx^2y^2y^3", "x^3y^7"},SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"-(x^3)*x^2", "(x^3)*-(x^2)", "-x^5"},SIMPLIFY_COMPLEX);
		
		//Distribution:
		parseAndCompare(new String[] {"(x+1)*((x+1+1))", "-1+x*x+x+x+x+3"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"(x+1)*((x+1+1))", "-1+x*x+x+x+x+3"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"(x)*((1/x+1+1))", "1+2x"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"ax(2x-x-x)","0*(xa+b+c)", "0*(2x-2x)"}, SIMPLIFY_COMPLEX);

		/*Tests that involve basic simplification*/		
		parseAndCompare(new String[] {"x+3","(3+x)","(x)+(3)","((3+x))","x+(3)"}, SIMPLIFY_BASIC);
		parseAndCompare(new String[] {"(a+b)^(xy)","(b+a)^(yx)"}, SIMPLIFY_BASIC);
		parseAndCompare(new String[] {"2*1/x*z*y*(((a*(b*c))))","(z*1*y*a*b*c)/x*2"}, SIMPLIFY_BASIC);

		/* New basic simplification code. */
		parseAndCompare(new String[] {"x*1", "1*x^1", "1x", "x", "x*1", "x"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"1x", "x^1", "x*1", "x", "1x", "x"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"x/1", "1x/1", "x*1/1", "x", "(x*1)/1"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"x+3","(3+x)","(x)+(3)","((3+x))","x+(3)"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"(a+b)^(xy)","(b+a)^(yx)"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"2*1/x*z*y*(((a*(b*c))))","(z*1*y*a*b*c)/x*2"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"(-3)+3x+3", "3+3x+(-3)", "3x+3-3"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"3x-3+(-0)", "3x+0-3", "(-3)+3x"}, SIMPLIFY_BASIC_TERMS, false);

		/* Stricter term matching. */
		parseAndCompare(new String[] {"x*1", "1*x"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"x*1", "1*x^1"}, STRICT_BASIC_TERMS);      //, false ?
		parseAndCompare(new String[] {"1x", "x"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"x", "x/1"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"-x", "-x/1"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"-x", "x/-1"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"x*1", "x"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"3x", "3*x"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"3x", "3(x)"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"1x", "1*x"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"1x", "1(x)"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"x*1", "1x"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"ab", "ba"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"a+ab", "ba+a"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"a+ab", "ba+a"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"3(x + 1)", "3x + 3"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"3(x) + 3(1)", "3x + 3"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"x+3","(3+x)","(x)+(3)","((3+x))","x+(3)"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"(a+b)^(xy)","(b+a)^(yx)"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"2*1/x*z*y*(((a*(b*c))))","(z*1*y*a*b*c)/x*2"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"(-3)+3x+3", "3+3x+(-3)", "3x+3-3"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"3x-3+(0)", "3x+0-3"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"3x-3+(-0)", "3x+0-3"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"3x+0-3", "(-3)+3x"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"2(x + 1) + 5", "5 + 2(1x + 1)"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + 1)", "2(1x + 1)"}, STRICT_BASIC_TERMS);
		
		/* Testing order of terms */
		parseAndCompare(new String[] {"(a+b)^(xy)","(b+a)^(yx)"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"(a+b)^(xy)","(b+a)^(x*y)"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"(a+b)^(xy)","(b+1a)^(x*y)"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"(a+b)^(xy)","(a+1b)^(x*y)"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2(x + 1)", "2(1x + 1)"}, STRICT_BASIC_TERMS_SAME_ORDER);
		
		parseAndCompare(new String[] {"(2x)(2x)", "(x+x)(x+x)", "4x^2"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2x^2+2x^2", "x(x+x)+x(x+x)"}, SIMPLIFY_COMPLEX);

		/* Examples from the user documentation for algEval */
		parseAndCompare(new String[] {"2x + x - 3 + 5", "2*x + x + (-3) + 5"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "x*2 + 1x + 5 - 3"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "3x + 2"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "2(x + 1x - 3) + 2*5"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + 1x - 3)2 + 2*5"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + x*1 - 3*1)2 + 2*5"}, SIMPLIFY_COMPLEX);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "5*2 + (x + x + (-3))2"}, SIMPLIFY_COMPLEX);

		/* Examples from the user documentation for algEquivTerms */
		parseAndCompare(new String[] {"2x + x - 3 + 5", "2*x + x + (-3) + 5"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "x*2 + 1x + 5 - 3"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "3x + 2"}, SIMPLIFY_BASIC_TERMS, false);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "2(x + 1x - 3) + 2*5"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + 1x - 3)2 + 2*5"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + x*1 - 3*1)2 + 2*5"}, SIMPLIFY_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "5*2 + (x + x + (-3))2"}, SIMPLIFY_BASIC_TERMS);

		/* Examples from the user documentation for algEquivTermsSameOrder */
		parseAndCompare(new String[] {"2x + x - 3 + 5", "2*x + x + (-3) + 5"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "x*2 + 1x + 5 - 3"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "3x + 2"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "2(x + 1x - 3) + 2*5"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + 1x - 3)2 + 2*5"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + x*1 - 3*1)2 + 2*5"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "5*2 + (x + x + (-3))2"}, SIMPLIFY_BASIC_TERMS_SAME_ORDER, false);

		/* Examples from the user documentation for algStrictTerms */
		parseAndCompare(new String[] {"2x + x - 3 + 5", "2*x + x + (-3) + 5"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "x*2 + 1x + 5 - 3"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "3x + 2"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "2(x + 1x - 3) + 2*5"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + 1x - 3)2 + 2*5"}, STRICT_BASIC_TERMS);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + x*1 - 3*1)2 + 2*5"}, STRICT_BASIC_TERMS, false);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "5*2 + (x + x + (-3))2"}, STRICT_BASIC_TERMS);

		/* Examples from the user documentation for algStrictTermsSameOrder */
		parseAndCompare(new String[] {"2x + x - 3 + 5", "2*x + x + (-3) + 5"}, STRICT_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "x*2 + 1x + 5 - 3"}, STRICT_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"2x + x - 3 + 5", "3x + 2"}, STRICT_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "2(x + 1x - 3) + 2*5"}, STRICT_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + 1x - 3)2 + 2*5"}, STRICT_BASIC_TERMS_SAME_ORDER);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "(x + x*1 - 3*1)2 + 2*5"}, STRICT_BASIC_TERMS_SAME_ORDER, false);
		parseAndCompare(new String[] {"2(x + x - 3) + 5*2", "5*2 + (x + x + (-3))2"}, STRICT_BASIC_TERMS_SAME_ORDER, false);
		
		/* Things we don't yet handle. */
//		parseAndCompare(new String[] {"4x^2", "2x^2+2x^2", "x(x+x)+x(x+x)"}, SIMPLIFY_COMPLEX);
		
		/* Old simplify code. */
//		parseAndCompare(new String[] {"x*1", "1*x^1", "1x", "x", "x*1", "x"}, SIMPLIFY_BASIC);
//		parseAndCompare(new String[] {"1x", "x^1", "x*1", "x", "1x", "x"}, SIMPLIFY_BASIC);
//		parseAndCompare(new String[] {"x+3","(3+x)","(x)+(3)","((3+x))","x+(3)"}, SIMPLIFY_BASIC);
//		parseAndCompare(new String[] {"(a+b)^(xy)","(b+a)^(yx)"}, SIMPLIFY_BASIC);
//		parseAndCompare(new String[] {"2*1/x*z*y*(((a*(b*c))))","(z*1*y*a*b*c)/x*2"}, SIMPLIFY_BASIC);
    }
    
    /**
     * @param expr expression that should parse
     * @throws assert error if fails to parse
     */
    private void isValidAlg(String expr) {
		boolean result = (expressionParser.stringToExpressionTreeNode(expr) != null);
		assertTrue(String.format("Expression \"%s\" should parse but fails", expr), result);
	}
    
    /**
     * @param expr expression that should fail to parse
     * @throws assert error if parses ok
     */
    private void isInvalidAlg(String expr) {
		boolean result = (expressionParser.stringToExpressionTreeNode(expr) != null);
		assertFalse(String.format("Expression \"%s\" should not parse but does", expr), result);
	}

	/**
     * Parses the array of expressions and makes sure they are equivalent.
     * @param inputStrings an array of 'equivalent' expressions.
     * @param type what sort of evaluation/simplification to perform.
     */
    private void parseAndCompare(String[] inputStrings, String type){
    	parseAndCompare(inputStrings, type, true);
    }
    
    /**
     * Parses the array of expressions and makes sure they are equivalent.
     * @param inputStrings an array of 'equivalent' expressions.
     * @param type what sort of evaluation/simplification to perform.
     * @param testEquals if false, change the test from test-equals to test-not-equals
     */
    private void parseAndCompare(String[] inputStrings, String type, boolean testEquals){
    	ExpressionTreeNode[] nodes = new ExpressionTreeNode[inputStrings.length];
    	Double[] evalResults = new Double[inputStrings.length];
    	String[] stringResults = new String[inputStrings.length];
    	
    	for(int i = 0; i < nodes.length;i++){
    		if(trace.getDebugCode("simpleterm"))
    			trace.out("simpleterm", String.format("ExprMatchrTest.parseAndCompare[%d](%s)",
    					i, inputStrings[i]));
    		if(type==SIMPLIFY_BASIC)
    			stringResults[i] = this.expressionParser.simplifyBasic(inputStrings[i]);
    		else if(SIMPLIFY_BASIC_TERMS.equals(type))
    			stringResults[i] = this.expressionParser.simplifyBasicTerms(inputStrings[i]);
    		else if(SIMPLIFY_BASIC_TERMS_SAME_ORDER.equals(type))
    			stringResults[i] = this.expressionParser.simplifyBasicTermsUnsorted(inputStrings[i]);
    		else if(STRICT_BASIC_TERMS.equals(type))
    			stringResults[i] = this.expressionParser.strictBasicTerms(inputStrings[i]);
    		else if(STRICT_BASIC_TERMS_SAME_ORDER.equals(type))
    			stringResults[i] = this.expressionParser.strictBasicTermsUnsorted(inputStrings[i]);
    		else if(type == SIMPLIFY_COMPLEX)
    			stringResults[i] = this.expressionParser.simplifyComplexExpr(inputStrings[i]);
    		else if(type == EVALUATE)
    			evalResults[i] = this.expressionParser.evaluate(inputStrings[i]);
    		
    		if (trace.getDebugCode("functions"))
    			trace.out("functions", inputStrings[i] + " becomes " + ((type == EVALUATE)?evalResults[i]:stringResults[i]));
    		}
    	
    	for(int i = 1; i < nodes.length; i++){
    		if(testEquals){
    			if(type==EVALUATE)
    				assertEquals(String.format("[0] %s != [%d] %s", inputStrings[0], i, inputStrings[i]),
    						evalResults[0], evalResults[i], 0);
    			else	
    				assertEquals(String.format("[0] %s != [%d] %s", inputStrings[0], i, inputStrings[i]),
    						stringResults[0], stringResults[i]);
    		} else {
       			if(type==EVALUATE)
    				assertFalse(String.format("[0] %s == [%d] %s", inputStrings[0], i, inputStrings[i]),
    						evalResults[0].equals(evalResults[i]));
    			else	
    				assertFalse(String.format("[0] %s == [%d] %s", inputStrings[0], i, inputStrings[i]),
    						stringResults[0].equals(stringResults[i]));
    		}
    	}
    }
    
}
