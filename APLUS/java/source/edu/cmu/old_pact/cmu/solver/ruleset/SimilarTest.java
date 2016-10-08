package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;

/*this is not a game or test
  we both have done some grievin
		-bh*/

//SimilarTest makes sure that the two expression evaluate to similar
//expressions
public class SimilarTest extends ComparisonTest {
	public SimilarTest(String[] one,String[] two){
		super(one,two);
	}

	public SimilarTest(String one,String two) {
		super(new String[] {one},new String[] {two});
	}

	public SimilarTest(String[] one,String two){
		super(one,new String[] {two});
	}

	public SimilarTest(String one,String[] two){
		super(new String[] {one},two);
	}

	boolean compare(String s1,String s2) throws BadExpressionError{
		return sm.similar(s1,s2);
	}

	public String toString() {
		return "[SimilarTest: \""+ofString(first)+"\" similar to \""+ofString(second)+"\"]";
	}
}
