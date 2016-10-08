package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import fri.patterns.interpreter.parsergenerator.semantics.TreeBuilderSemantic.Node;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class SyntaxTreeNode extends Node{
	
	public ExpressionTreeProperties properties;
	public static final int IDENTITY = 0;
	public static final int PLUS = 1;
	public static final int MINUS =2;
	public static final int MULTIPLY = 3;//	{ "TERM", "TERM", "'*'", "NEGOPERAND" },
	public static final int DIVIDE = 4;//	{ "TERM", "TERM", "'/'", "NEGOPERAND" },
	public static final int IMPLICIT_MULTIPLY = 5;//	{ "TERM", "TERM", "EXPOPERAND"},
	public static final int EXPONENT = 6;
	public static final int VARREF = 7;
	public static final int NUMBER = 8;
	public static final int PAREN_EXPRESSION = 9;
	
	int type = IDENTITY;//i.e. PLUS for "x+x", MINUS for "x-x"
	
	
	public int UNARY_MINUS = 1;
	
	protected String myStringValue = "";

	
	public SyntaxTreeNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges);
		this.properties = properties;
	}
	
	public ExpressionTreeNode generateExpressionTree(){
		return null;
	}

	public Object getChildObject(int index){
		return this.getInputTokens().get(index);
	}

	public SyntaxTreeNode getChildNode(int index){
		return (SyntaxTreeNode)getChildObject(index);
	}
	
	public String getChildString(int index){
		return (String)getChildObject(index);
	}
	
	public String toActualString(){
		return myStringValue;
	}
}
