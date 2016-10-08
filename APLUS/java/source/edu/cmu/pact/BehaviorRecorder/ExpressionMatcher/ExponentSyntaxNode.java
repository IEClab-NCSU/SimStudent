package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExponentNode;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class ExponentSyntaxNode extends SyntaxTreeNode {
	
	//	{ "EXPOPERAND", "OPERAND", "'^'", "NEGOPERAND"},
    //	{ "EXPOPERAND", "OPERAND"},
	
	//public String IDENTITY = "IDENTITY";
	
	public ExponentSyntaxNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		if(getInputTokens().size() == 3)
			type = EXPONENT;
		else
			type = IDENTITY;
	}

	public ExpressionTreeNode generateExpressionTree()
	{
		if(type == EXPONENT){
			
			ExpressionTreeNode left = ((SyntaxTreeNode)(getInputTokens().get(0))).generateExpressionTree();
			ExpressionTreeNode right =  ((SyntaxTreeNode)(getInputTokens().get(2))).generateExpressionTree();
			
			ArrayList<ExpressionTreeNode> temp2 = new ArrayList<ExpressionTreeNode>();
			temp2.add(left);
			temp2.add(right);
			return new ExponentNode(temp2, false, properties);
		}
		else
		{
			
			return  ((SyntaxTreeNode)getInputTokens().get(0)).generateExpressionTree();
		}
	}
	
	public String toActualString()
	{
		if(this.myStringValue == ""){
			if(type == IDENTITY)
				myStringValue = getChildNode(0).toActualString();
			else//UNARY_MINUS
				myStringValue =  getChildNode(0).toActualString() + "^" + getChildNode(2).toActualString();
		}
		return myStringValue;
	}
	
}
