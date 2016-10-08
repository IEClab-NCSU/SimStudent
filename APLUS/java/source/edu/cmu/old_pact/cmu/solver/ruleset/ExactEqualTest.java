package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;

public class ExactEqualTest extends ComparisonTest {
	public ExactEqualTest(String one,String two) {
		super(new String[] {one},new String[] {two});
	}
	
	public ExactEqualTest(String[] one,String[] two){
		super(one,two);
	}

	public ExactEqualTest(String one,String[] two){
		super(new String[] {one},two);
	}

	public ExactEqualTest(String[] one,String two){
		super(one,new String[] {two});
	}

	boolean compare(String s1,String s2) throws BadExpressionError{
		return sm.exactEqual(s1,s2);
	}
	
	public String toString() {
		return "[ExactEqualTest: \""+ofString(first)+"\" equals \""+ofString(second)+"\"]";
	}
}
