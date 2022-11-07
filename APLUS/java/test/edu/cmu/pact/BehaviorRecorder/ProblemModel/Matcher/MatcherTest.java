/*
 * Created on Apr 22, 2004
 *
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher;

import java.security.InvalidParameterException;
import java.util.Vector;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;

/**
 * @author mpschnei
 *
 */
public class MatcherTest extends TestCase {

	/** For extra console output. */
    private static boolean verbose = false;

	public static Test suite() {
    	if(verbose)
        	trace.out("CONSTRUCTING TEST SUITE...\n\n\n");
        return new TestSuite(MatcherTest.class);
    }

    
    public void testRangeMatching() {
    	RangeMatcher r;
    	r = new RangeMatcher();
    	r.setMaximum("20");
    	r.setMinimum("0");
        r.setSelection("mySelection");
        r.setAction ("myAction");
    	assertTrue (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("10")));
    	assertTrue (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("0.00")));
    	assertTrue (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("20.0000")));
    	assertTrue (r.matchForHint(makeVector("mySelection"), null, "Student"));
    	assertFalse (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("20.00001")));
    	assertFalse (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("-0.00001")));
    	assertFalse (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("32412341241234")));
    	assertFalse (r.match(makeVector("mySelection"), makeVector("myAction"), makeVector("abacsdas")));
    	assertFalse (r.matchForHint(makeVector("yourSelection"), null, "Student"));
    	r = new RangeMatcher();
    	r.setMaximum("20");
    	r.setMinimum("50000");
    	try {
    		r.match (makeVector("mySelection"), makeVector("myAction"), makeVector("abacsdas"));
            fail();
    	} catch (InvalidParameterException e) {
    	}
    } 
    
    public Element MakeParamElt(String text, int position) {
    	Element EltResult = new Element("matcherParameter");
    	EltResult.setText(text);
    	String attrName = null;
    	switch (position) {
            case	0: attrName = "Selection"; break;
            case	1: attrName = "Action";    break;
            case	2: attrName = "Input";     break;
        }
    	if (attrName != null) EltResult.setAttribute("name", attrName);
    	trace.out("jdomreader", "element name = " + EltResult.getName() + " " + position);
    	return EltResult;
    }
    	
    /**
	 * @param string
	 * @return
	 */
	public static Vector makeVector(String string) {
		Vector v = new Vector();
		v.addElement (string);
		return v;
	}

	public void testAnyMatching() {
		AnyMatcher am = new AnyMatcher();
        am.setSelection("mySelection");
        am.setAction("myInput");
        
        assertTrue (am.match(makeVector ("mySelection"), 
                             makeVector("myInput"), makeVector("balaksdlkasdf")));
        assertFalse (am.match(makeVector ("blah"), 
                              makeVector("myInput"), makeVector("balaksdlkasdf")));
        assertTrue (am.matchForHint(makeVector ("mySelection"), null, "Student"));
        assertFalse (am.matchForHint(makeVector ("yourSelection"), null, "Student"));
    }


    public void testRegexMatching() {
        regex1();
        regex2();
        regex3();
        regex4();
    }

    private void regex4() {
        RegexMatcher rm = new RegexMatcher();
        rm.setParameter(MakeParamElt("dog-dog|cat", 0), 0);
        rm.setParameter(MakeParamElt("dog|cat", 1), 1);
        rm.setParameter(MakeParamElt("dog|cat", 2), 2);
        assertTrue (rm.match (makeVector ("dog-dog"), 
                              makeVector ("dog"), 
                              makeVector ("dog")));
        assertTrue (rm.match (makeVector ("cat"), 
                              makeVector ("cat"), 
                              makeVector ("cat")));
        assertFalse (rm.match (makeVector ("dogcat"), 
                               makeVector ("dogcat"), 
                               makeVector ("dogcat")));
    }

    /**
     * 
     */
    private void regex1() {
        RegexMatcher rm = new RegexMatcher();
        //  rm.setParameter("Selection", 0);
        //  rm.setParameter("Action", 1);
        //  rm.setParameter("Input", 2);
        rm.setParameter(MakeParamElt("Selection", 0), 0);
        rm.setParameter(MakeParamElt("Action", 1), 1);
        rm.setParameter(MakeParamElt("Input", 2), 2);
        assertTrue (rm.match (makeVector ("Selection"), 
                              makeVector ("Action"), 
                              makeVector ("Input")));
        assertTrue (rm.matchForHint (makeVector ("Selection"), null, "Student")); 
                
        assertFalse (rm.match (makeVector ("baaaab"), 
                               makeVector ("aaaaba"), 
                               makeVector ("aaaabab")));
        assertFalse (rm.matchForHint (makeVector ("baaaab"), null, "Student")); 
       
    }

    /**
     * 
     */
    private void regex2() {
        RegexMatcher rm = new RegexMatcher();
        //   rm.setParameter("a*b", 0);
        //   rm.setParameter("a*b", 1);
        //    rm.setParameter("a*b", 2);
        rm.setParameter(MakeParamElt("a*b", 0), 0);
        rm.setParameter(MakeParamElt("a*b", 1), 1);
        rm.setParameter(MakeParamElt("a*b", 2), 2);
        assertTrue (rm.match (makeVector ("aaaab"), 
                              makeVector ("aaaab"), 
                              makeVector ("aaaab")));
        assertFalse (rm.match (makeVector ("baaaab"), 
                               makeVector ("aaaaba"), 
                               makeVector ("aaaabab")));
    }

    /**
     * Test regular expressions that accept only valid numeric strings.<ul>
     * <li>The selection expression matches any integer; a leading 0 is disallowed if there's more than 1 digit.</li>
     * <li>The action expression matches any decimal number in scientific notation.</li>
     * <li>The input expression matches any decimal number not in scientific notation.</li>
     * </ul>
     * For both of the decimal expressions, at least one digit to the left of the decimal place is required.
     */
    public void testNumberRegex() {
        RegexMatcher rm = new RegexMatcher();
        rm.setParameter(MakeParamElt("-?[1-9]*[0-9]", 0), 0);
        rm.setParameter(MakeParamElt("-?[0-9]*[0-9][.]?[0-9]*([eE]-?[0-9]+)", 1), 1);
        rm.setParameter(MakeParamElt("-?[0-9]*[0-9][.]?[0-9]*", 2), 2);
        assertTrue (rm.match (makeVector ("-5399"), 
        		makeVector ("0.323e-7"), 
        		makeVector ("-23.")));
        assertFalse (rm.match (makeVector ("09"),  // leading 0 in integer disallowed
                makeVector ("-2222.E77"), 
                makeVector ("0.7")));
        assertFalse (rm.match (makeVector ("9"), 
                makeVector ("2432"), 
                makeVector (".7")));
        assertTrue (rm.match (makeVector ("9"), 
                makeVector ("-2222.E77"), 
                makeVector ("0.7")));
    }

    /**
     * 
     */
    private void regex3() {
        RegexMatcher rm = new RegexMatcher();
        // rm.setParameter(".*\\.java", 0);
        //  rm.setParameter("a*b", 1);
        //  rm.setParameter("a*b", 2);
        rm.setParameter(MakeParamElt(".*\\.java", 0), 0);
        rm.setParameter(MakeParamElt("a*b", 1), 1);
        rm.setParameter(MakeParamElt("a*b", 2), 2);
        assertTrue (rm.match (makeVector ("test.java"), 
                              makeVector ("aaaab"), 
                              makeVector ("aaaab")));
        assertFalse (rm.match (makeVector ("testjava"), 
                               makeVector ("aaaaba"), 
                               makeVector ("aaaabab")));
    }

    
    public void testSimpleRegexMatching() {
    	simpleRegex1();
    	simpleRegex2();
        
    }

	/**
	 * 
	 */
	private void simpleRegex1() {
		WildcardMatcher sm = new WildcardMatcher();
        //	sm.setParameter("*.java", 0);
        //	sm.setParameter("a*bb", 1);
        //	sm.setParameter("a*b", 2);
        sm.setParameter(MakeParamElt("*.java", 0), 0);
        sm.setParameter(MakeParamElt("a*bb", 1), 1);
        sm.setParameter(MakeParamElt("a*b", 2), 2);
		assertTrue (sm.match (makeVector ("mi  ke.java"), 
                              makeVector ("aa   aabb"), 
                              makeVector ("aaaab")));
		assertFalse (sm.match (makeVector ("baaaab"), 
                               makeVector ("aaaaba"), 
                               makeVector ("aaaabab")));
	}

	/**
	 * 
	 */
	private void simpleRegex2() {
		WildcardMatcher sm = new WildcardMatcher();
        //	sm.setParameter("\\**.java", 0);
        //	sm.setParameter("a*b", 1);
        //	sm.setParameter("a*b", 2);
        sm.setParameter(MakeParamElt("\\**.java", 0), 0);
        sm.setParameter(MakeParamElt("a*b", 1), 1);
        sm.setParameter(MakeParamElt("a*b", 2), 2);
		assertTrue (sm.match (makeVector ("*mike.java"), 
                              makeVector ("aaaab"), 
                              makeVector ("aaaab")));
		assertFalse (sm.match (makeVector ("baaaab"), 
                               makeVector ("aaaaba"), 
                               makeVector ("aaaabab")));
	}

    ExpressionMatcher _expressionMatcher, _expressionMatcherNullVT;
    
    private ExpressionMatcher expressionMatcher() {
        if (_expressionMatcher==null) {
            ExpressionMatcher m = new ExpressionMatcher();
            m.setDefaultSelection("mySelection");
            m.setDefaultAction("myAction");
	
            VariableTable vt = new VariableTable();
            vt.put("x", new Double(2));
            vt.put("y", new Double(3));
            vt.put("z", new Integer(4));
            vt.put("w", new Integer(8));
            vt.put("str", "abcdefg");
            m.setExternalResources(vt, null, null);
            _expressionMatcher = m;
        }

        return _expressionMatcher;
    }
    
    private ExpressionMatcher expressionMatcherNullVT() {
        if (_expressionMatcherNullVT==null) {
            ExpressionMatcher m = new ExpressionMatcher();
            m.setDefaultSelection("mySelection");
            m.setDefaultAction("myAction");
	        m.setExternalResources(null, null, null);
            _expressionMatcherNullVT = m;
        }
        return _expressionMatcherNullVT;
    }
    
    private void testExpressionNullVT(String expression, String value) {
        expressionMatcherNullVT().setInputExpression(expression);
        assertTrue("match("+expression+", "+value+")",
        		expressionMatcherNullVT().match(makeVector("mySelection"), makeVector("myAction"), makeVector(value)));
        if(verbose)
        	trace.out(expressionMatcherNullVT().toXML());
    }
	
    private void testExpression(String expression, String value) {
        expressionMatcher().setInputExpression(expression);
        assertTrue("match("+expression+", "+value+")",
        		expressionMatcher().match(makeVector("mySelection"), makeVector("myAction"), makeVector(value)));
        if(verbose)
        	trace.out(expressionMatcher().toXML());
    }
	
    private void testExpressionSyntaxError(String expression, String errorPrefix) {
        expressionMatcher().setInputExpression(expression);
        boolean result = expressionMatcher().match(makeVector("mySelection"),
        			makeVector("myAction"), makeVector(""));
        assertFalse("bad syntax matched in \'"+expression+"\'", result);
        String mErrStr = expressionMatcher().errorString(); 
        assertNotNull("null error string on bad syntax \'"+expression+"\'", mErrStr); 
        assertTrue("wrong error string on bad syntax \'"+expression+"\'",
        		mErrStr.startsWith(errorPrefix)); 
        if(verbose)
        	trace.out(expressionMatcher().toXML());
    }
	
    private void testExpressionFalseNullVT(String expression, String value) {
        expressionMatcherNullVT().setInputExpression(expression);
        assertFalse(expressionMatcherNullVT().match(makeVector("mySelection"), makeVector("myAction"), makeVector(value)));
        if(verbose)
        	trace.out(expressionMatcherNullVT().toXML());
    }
	
    private void testExpressionFalse(String expression, String value) {
        expressionMatcher().setInputExpression(expression);
        assertFalse(expressionMatcher().match(makeVector("mySelection"), makeVector("myAction"), makeVector(value)));
        if(verbose)
        	trace.out(expressionMatcher().toXML());
    }

    /**
     * Test the given relational operator, one of {@value ExpressionMatcher#RELATIONS}.
     * Asserts input relop (expression).
     * @param input student input
     * @param relop
     * @param expression
     */
    private void testRelationalOperator(String input, String relop, String expression) {
        String oldRelop = expressionMatcher().getRelation();
        expressionMatcher().setRelation(relop);
        expressionMatcher().setInputExpression(expression);
        assertTrue("test("+input+" "+relop+" "+expression+")",
        		expressionMatcher().match(makeVector("mySelection"), makeVector("myAction"), makeVector(input)));
        if(verbose)
        	trace.out(expressionMatcher().toXML());
        expressionMatcher().setRelation(oldRelop);
    }
	
    private void interpolateExpression(String expression, String value, String expected) {
        expressionMatcher().setInputExpression(expression);
        assertTrue(expected.equals(expressionMatcher().interpolate(expression, "mySelection", "myAction", value)));
        if(verbose)
        	trace.out(expressionMatcher().toXML());
    }
    
    private void testBooleanExpressionNullVT(String expression) {
        expressionMatcherNullVT().setRelation("boolean");
        testExpressionNullVT(expression, "");
        expressionMatcherNullVT().setRelation("=");
    }
    
    private void testBooleanExpression(String expression) {
        expressionMatcher().setRelation("boolean");
        testExpression(expression, "");
        expressionMatcher().setRelation("=");
    }
    
    private void testBooleanExpressionFalseNullVT(String expression) {
        expressionMatcherNullVT().setRelation("boolean");
        testExpressionFalseNullVT(expression, "");
        expressionMatcherNullVT().setRelation("=");
    }
    
    private void testBooleanExpressionFalse(String expression) {
        expressionMatcher().setRelation("boolean");
        testExpressionFalse(expression, "");
        expressionMatcher().setRelation("=");
    }
	
    private void checkExpressionTrue(String expression) {
        expressionMatcher().setInputExpression(expression);
        assertTrue(expressionMatcher().checkExpression());
        if(verbose)
        	trace.out(expressionMatcher().toXML());
    }
	
    private void checkExpressionFalse(String expression) {
        expressionMatcher().setInputExpression(expression);
        assertFalse(expressionMatcher().checkExpression());
        if(verbose)
        	trace.out(expressionMatcher().toXML());
    }
    
    public void testNewExpressionMatching() {
        testExpression("algEval(\"4*(11+2)/2+17\")", "43");    	
        testBooleanExpression("algEquiv(\"2h(h+3)/2\", \"h^2+3h\")");
        testBooleanExpressionFalse("algEquivTerms(\"2h(h+3)/2\", \"h^2+3h\")");
        testBooleanExpression("algEquivTerms(\"3*h+h^2*2\", \"2h^2+3h\")");
    }
    
    public void testNewExpressionMatchingNullVT() {
        testExpressionNullVT("algEval(\"4*(11+2)/2+17\")", "43");    	
        testBooleanExpressionNullVT("algEquiv(\"2h(h+3)/2\", \"h^2+3h\")");
        testBooleanExpressionFalseNullVT("algEquivTerms(\"2h(h+3)/2\", \"h^2+3h\")");
        testBooleanExpressionNullVT("algEquivTerms(\"3*h+h^2*2\", \"2h^2+3h\")");
    }
    
    public void testExpressionMatching() {
    	if(verbose)
        	trace.out("TEST EXPRESSION MATCHING...\n\n\n");
        
        testExpressionSyntaxError("x +", "ERROR:");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        
        
        testExpression("x + y", "5");
        testExpression("x - y", "-1");
        testExpression("x * y", "6");
        testExpression("x / y", "0.6666666666666666");
        testExpression("y / x", "1.5");
        testExpression("1.5 + (y / x)", "3");
        testExpression("z / x", "2");
        testExpression("z / w", "0.5");
        testExpression("w / z", "2");
        testExpression("( w / z )", "2");
        testExpression("x+(y-x)+2", "5");
        testExpression("2+(3-2)+2.0", "5");
        testExpression("( 2 )", "2");
        testExpression("( x )", "2");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        testExpression("concat(str, \"h\")", "abcdefgh");

        testExpression("concat(\"a\", \"b\", \"c\", \"d\", \"e\", \"f\", \"g\", \"h\")", "abcdefgh");

        testExpression("abs(-1.0)", "1.0");
        testExpression("abs(x)", "2.0");
        testExpression("abs((x))", "2.0");
        testExpression("abs((x+2))", "4");
        testExpression("log10(abs((x+98)))", "2");
        testExpression("log10((abs((x+98))/1))", "2");
        testExpression("round(floor((370-1000*floor(370/1000))/100))", "3");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        
        
        testExpression("sum(x)", "2.0");
        testExpression("sum((x))", "2.0");
        testExpression("sum((x),2)", "4.0");

        testExpression("sum(x, y)", "5.0");

        testExpression("sum(x, y, 4)", "9.0");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        testExpression("sum(x, y, 4, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)", "64.0");

        testExpression("ifThen(lessThan(3, 4), 1.0, 2.0)", "1.0");
        testExpression("ifThen(lessThan(4, 3), 1.0, 2.0)", "2.0");
        
        // testRelationalOperator(input, relop, expression);
        testRelationalOperator("9.8", "<",  "4+5.9");
        testRelationalOperator("Z",   "<=", "\"a\"");   // all capital letters sort before all lower case
        testRelationalOperator("a",   "<",  "\"aa\"");  // shorter strings sort before longer
        testRelationalOperator("9",   ">=", "8.1/0.9");
        testRelationalOperator("9",   "<",  "8.1/0.89999");
        testRelationalOperator("5e4", ">=", "\"50000\"");
        testRelationalOperator("5e4", "<=", "\"50000\"");
        testRelationalOperator("-9",  "<=", "-(8.1/0.9)");
        testRelationalOperator("-9",  ">=", "-(8.1/0.9)");
        
        testBooleanExpression("memberOf(x, y, 1 + 1, 3.0)");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        // testBooleanExpression("memberOf(5, y, 1 + 1, 3.0, \"a\")");
        
        testBooleanExpressionFalse("equals(\"3fre\")");
        testBooleanExpression("equals(\"3fre\", \"3fre\")");
        testBooleanExpression("equals(3, 3.0)");
        testBooleanExpression("equals(3, 3.0, \"3.000\")");
        testBooleanExpression("equals(3.0, 3, \"3.000\")");
        testBooleanExpression("equals(\"3.000\", 3, 3.0)");
        testBooleanExpression("equals(\"3\", 3.00000, 3.0)");
        testBooleanExpression("equals(2.999999999999999999, 3, \"3.000\")");
        testBooleanExpression("equals(3, 2.999999999999999999, \"3.000\")");
        testBooleanExpressionFalse("equals(3, 2.9999999999999)");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        testBooleanExpression("contains(str, \"def\")");

        testBooleanExpressionFalse("matchWithoutPrecision(\"0.911\", \"0.900\")");
        testBooleanExpressionFalse("matchWithoutPrecision(0.911, \"0.900\")");
        testBooleanExpression("matchWithoutPrecision(\"0.911\", \"0.91\")");
        testBooleanExpression("matchWithoutPrecision(0.911, 0.91)");
        testBooleanExpressionFalse("matchWithoutPrecision(\"0.915\", \"0.91\")");
        testBooleanExpression("matchWithoutPrecision(\"0.09\", \"9E-2\")");
        testBooleanExpression("matchWithoutPrecision(\"0.091\", \"9E-2\")");
        testBooleanExpression("matchWithoutPrecision(\"0.09\", \"9.000E-2\")");
        testBooleanExpression("matchWithoutPrecision(\"0.9\", \".9\")");
        testBooleanExpression("matchWithoutPrecision(\"0.89\", \".9\")");
        testBooleanExpressionFalse("matchWithoutPrecision(\"0.84\", \".9\")");
        testBooleanExpression("matchWithoutPrecision(\"9E-1\", \"90E-2\")");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        testBooleanExpressionFalse("matchWithPrecision(\"0.9\", \"0.900\")");
        testBooleanExpressionFalse("matchWithPrecision(0.9, \"0.900\")");
        testBooleanExpression("matchWithPrecision(\"0.09\", \"9E-2\")");
        testBooleanExpressionFalse("matchWithPrecision(\"0.09\", \"9.000E-2\")");
        testBooleanExpression("matchWithPrecision(\"0.9\", \".9\")");
        testBooleanExpression("matchWithPrecision(0.9, .9)");
        testBooleanExpression("matchWithPrecision(\"0.9\", \"0.9\")");
        testBooleanExpressionFalse("matchWithPrecision(\"9E-1\", \"90E-2\")");
        
        testExpression("fmtDollar((x*18/12),\"i\")", "3");
        testExpression("fmtDollar((x*31/10),\"i\")", "6.20");

        testBooleanExpression("and(lessThan(3, 4), greaterThan(5, 4))");
        testBooleanExpressionFalse("and(lessThan(3, 4), greaterThan(4, 5))");
        testBooleanExpression("or(lessThan(3, 4), greaterThan(4, 5))");
        testBooleanExpressionFalse("or(lessThan(4, 3), greaterThan(4, 5))");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        testBooleanExpression("not(lessThan(4, 3))");
        testBooleanExpressionFalse("not(lessThan(3, 4))");

        testBooleanExpression("not(equals(4, 3))");
        testBooleanExpression("equals(4, 2+2)");

        interpolateExpression("sum(<%=x%>, <%=y%>) is <%=sum(x,y)%>", "foo", "sum(2.0, 3.0) is 5.0");

        interpolateExpression("<%=sum(x,y)%> is sum(<%=x%>, <%=y%>)", "foo", "5.0 is sum(2.0, 3.0)");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        

        checkExpressionTrue("concat(a, \"h\")");

        checkExpressionTrue("7 + a");

        checkExpressionFalse("7 +");

        // checkExpressionFalse("concat(a)");

        checkExpressionFalse("memberOf()");   // function not found

        testBooleanExpression("algebraicEqual(\"x+4\", \"4 + x\")");
        testBooleanExpressionFalse("algebraicEqual(\"x+4\", \"4 - x\")");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        
        
        testExpression("simplify(\"4*(x+2)/2\")", "2(x+2)");
        
        testBooleanExpressionFalse("equals(unset,\"null\")");
        testBooleanExpression("equals(unset,null)");
        testBooleanExpression("equals(unset,null)");         // old bug set dereferenced var to "null" 
        testBooleanExpressionFalse("equals(unset,\"null\")"); 
        testExpression("unset", null);
        testBooleanExpression("startsWith(unset, \"n\")");  // ERROR: this test should fail

        testBooleanExpression("algEquivTermsSameOrder(\"3*x+4\",\"3x+4\")");
        testBooleanExpression("algEquivTermsSameOrder(\"3x+4\",\"x*3+4\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"3x+4\",\"4+3x\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"3x+3-1\",\"2+3x\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"3x+3-1\",\"3+3x-1\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"3x+3-3\",\"3+3x-3\")");
        testBooleanExpression("algEquivTermsSameOrder(\"3+3x+(-3)\",\"3+3x-3\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"-3x+3x+3\",\"3+3x+-3x\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"-3+3x+3\",\"3+3x-3\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"3x-3+3\",\"3x+3-3\")");
        testBooleanExpressionFalse("algEquivTermsSameOrder(\"-2+3x+3\",\"3+3x-2\")");
        
        testBooleanExpression("algEquivTerms(\"3x+4\",\"4+3x\")");
        testBooleanExpressionFalse("algEquivTerms(\"3x+3-1\",\"2+3x\")");
        testBooleanExpression("algEquivTerms(\"3x+3-1\",\"3+3x-1\")");
        testBooleanExpression("algEquivTerms(\"3x+3-3\",\"3+3x-3\")");
        testBooleanExpression("algEquivTerms(\"(-3)+3x+3\",\"3+3x+(-3)\")");
        testBooleanExpression("algEquivTerms(\"-3x+3x+3\",\"3+3x+-3x\")");
        testBooleanExpression("algEquivTerms(\"-3+3x+3\",\"3+3x-3\")");
        testBooleanExpression("algEquivTerms(\"3x-3+3\",\"3x+3-3\")");
        testBooleanExpression("algEquivTerms(\"-2+3x+3\",\"3+3x-2\")");

        testBooleanExpression("true");
        testBooleanExpressionFalse("false");

        _expressionMatcher = null;   // deserialize again to test MemorySerializedParser        
        
        testExpression("last(assign(\"fred.george\", \"abc\"), concat(fred.george, \"d\"))", "abcd");
        testExpression("last(assign(\"grapher.labelY\",\"fred\"),ifThen(hasValue(grapher.labelY),"+
        		"concat(\"The y-axis is called \",grapher.labelY),"+
        		"\"The time is set. Depending on the time, the height changes.\"))",
        		"The y-axis is called fred");
    }

    public static void main(String [] args) throws Exception {
    	if (args.length > 0) {
    		MatcherTest mt = new MatcherTest();
    		for (int i = 0; i < args.length && args[i].charAt(0) == '-'; ++i) {
    			String test = args[i].toLowerCase();
    			try {
    				if ("-v".equals(test)) {
    					verbose = true; continue;
    				} else if ("-te".equals(test))
    					mt.testExpression(args[++i], args[++i]);
    				else if ("-tt".equals(test))
    					mt.testBooleanExpression(args[++i]);
    				else if ("-tf".equals(test))
    					mt.testBooleanExpressionFalse(args[++i]);
    				else {
    					System.err.println("unknown test "+test+"; choices are\n"+
    							" -te <expression> <result>    (success if expression equals result);\n"+
    							" -tt <booleanExpression>      (success if booleanExpression is true);\n"+
    							" -tf <falseBooleanExpression> (success if booleanExpression is false).\n");
    					continue;
    				}
    				if("-te".equals(test))
        				System.out.printf("Sucess:               %s %s equals %s\n",
        						test, args[i-1], args[i]);
    				else
    					System.out.printf("Sucess:               %s %s: %s\n",
    							test, "-tt".equals(test) ? "true" : "false", args[i]);
    			} catch(AssertionFailedError afe) {
    				System.out.printf("\n*** Assertion failed: %s %s %s\n\n",
    						test, "-te".equals(test) ? args[i-1] : "", args[i]);
    			}
    		}
    		return;
    	}
        TestResult tr = new TestResult();
        suite().run(tr);
        for (java.util.Enumeration failures = tr.failures(); failures.hasMoreElements(); )
            trace.out("failure: " + failures.nextElement());
    }
}
