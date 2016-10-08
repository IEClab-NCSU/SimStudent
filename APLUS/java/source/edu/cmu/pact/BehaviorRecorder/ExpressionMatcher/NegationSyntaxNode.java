package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class NegationSyntaxNode extends SyntaxTreeNode {

	//  { "NEGOPERAND", "'-'", "EXPOPERAND"},
    //  { "NEGOPERAND", "EXPOPERAND"},
	public NegationSyntaxNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		// TODO Auto-generated constructor stub
		if(getInputTokens().size() == 2)
			type = UNARY_MINUS;
		else
			type = IDENTITY;
	}
	
	public ExpressionTreeNode generateExpressionTree()
	{
		ExpressionTreeNode child;
		
		if(type == UNARY_MINUS)
		{
			child = ((SyntaxTreeNode)(getInputTokens().get(1))).generateExpressionTree();
			child.negate();
		}
		else
		{
			child = ((SyntaxTreeNode)(getInputTokens().get(0))).generateExpressionTree();
		}
		
		return child;
		
/*		if(type == UNARY_MINUS){
			
			ExpressionTreeNode neg = ((SyntaxTreeNode)(getInputTokens().get(1))).generateExpressionTree();
			ArrayList<ExpressionTreeNode> temp2 = new ArrayList<ExpressionTreeNode>();
			temp2.add(neg);
			
			return new NegationNode(temp2);
		}
		else //IDENTITY 
		{
			//return ((SyntaxTreeNode)(getInputTokens().get(0))).generateExpressionTree();
		}*/
	}

	
	public String toActualString()
	{
		if(this.myStringValue == ""){
			if(type == IDENTITY)
				myStringValue = getChildNode(0).toActualString();
			else//UNARY_MINUS
				myStringValue = "-" + getChildNode(1).toActualString();
		}

		return myStringValue;
	}
}
