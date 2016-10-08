package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.io.IOException;
import java.io.Serializable;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;

/**
 * This class shouldn't be static... might be settable by brd per sesson.
 * 
 * 
 * @author Administrator
 */
public class ExpressionTreeProperties implements Serializable
{
	public  boolean distributeSums = true;//implemented.(x+1)(2+x)=>x^2+2+3x?
	public  boolean cancelTerms = true;//implemented. x/x=>1
	public  boolean combineTerms = true;//Currently unused.. could avoid converting x*x->x^2
	public  boolean combineAddends = true;//Currently unused... could avoid x+x->2x
	public  boolean variablesToLowerCase = false;//Currently unused ... not sure how to handle this.
	//public	boolean 
	public  VariableTable variableTable;
}