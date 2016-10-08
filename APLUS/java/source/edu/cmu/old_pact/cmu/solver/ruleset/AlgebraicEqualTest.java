package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;

//AlgebraicEqualTest makes sure that the two expression evaluate to algebraically
//equivalent expressions
public class AlgebraicEqualTest extends ComparisonTest {
	public AlgebraicEqualTest(String one,String two) {
		super(new String[] {one},new String[] {two});
	}
	
	public AlgebraicEqualTest(String[] one,String[] two){
		super(one,two);
	}

	public AlgebraicEqualTest(String one,String[] two){
		super(new String[] {one},two);
	}

	public AlgebraicEqualTest(String[] one,String two){
		super(one,new String[] {two});
	}

	boolean compare(String s1,String s2) throws BadExpressionError{
		return sm.algebraicEqual(s1,s2);
	}
	
	public String toString() {
		return "[AlgebraicEqualTest: \""+ofString(first)+"\" equals \""+ofString(second)+"\"]";
	}
}
