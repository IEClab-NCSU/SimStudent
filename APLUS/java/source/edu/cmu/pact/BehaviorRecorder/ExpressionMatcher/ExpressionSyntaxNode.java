package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.AdditionNode;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class ExpressionSyntaxNode extends SyntaxTreeNode {

	
	/*  { "EXPRESSION", "TERM" },
        { "EXPRESSION", "EXPRESSION", "'+'", "TERM" },
        { "EXPRESSION", "EXPRESSION", "'-'", "TERM" },
	 */
	//private boolean isSummation = false;
	public ExpressionSyntaxNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		if(trace.getDebugCode("expr"))
			trace.printStack("expr", "ExpressionSyntaxNode("+rule+", "+inputTokens+", "+ranges+", "+properties+")");
		if(getInputTokens().size()==1){
			type = IDENTITY;
		}else{
			if(getInputTokens().get(1).equals("+"))
				type = PLUS;
			else
				type = MINUS;
		}
	}
	
	public ExpressionTreeNode generateExpressionTree()
	{
		if(type == IDENTITY){
			return  ((SyntaxTreeNode)getInputTokens().get(0)).generateExpressionTree();
		}
		else
		{
			ExpressionTreeNode left = ((SyntaxTreeNode)(getInputTokens().get(0))).generateExpressionTree();
			ExpressionTreeNode right =  ((SyntaxTreeNode)(getInputTokens().get(2))).generateExpressionTree();
			
			if(type == MINUS)
				right.negate();
		/*	
			if(type == MINUS){
				ArrayList<ExpressionTreeNode> temp = new ArrayList<ExpressionTreeNode>();
				temp.add(right);
				right = new NegationNode(temp);
			}
			*/
			ArrayList<ExpressionTreeNode> temp2 = new ArrayList<ExpressionTreeNode>();
			temp2.add(left);
			temp2.add(right);
			return new AdditionNode(temp2, false, properties);
		}
	}
	
	
	
	public String toActualString()
	{
		if(myStringValue != "")
			return myStringValue;
		
		if(type == IDENTITY)
			myStringValue = getChildNode(0).toActualString();
		else
			myStringValue = getChildNode(0).toActualString() + getChildString(1) + getChildNode(2).toActualString();
		return myStringValue;
	}

}
