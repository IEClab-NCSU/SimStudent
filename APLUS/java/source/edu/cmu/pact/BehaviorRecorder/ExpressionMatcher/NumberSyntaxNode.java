package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.NumberNode;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class NumberSyntaxNode extends SyntaxTreeNode {
	
	//public String IDENTITY = "IDENTITY";
	
	public NumberSyntaxNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		type = IDENTITY;
		// TODO Auto-generated constructor stub
	}
	
	public ExpressionTreeNode generateExpressionTree()
	{
		NumberNode leaf = new NumberNode((String) getInputTokens().get(0), properties);
		return leaf;
		/*Double eval_value = Double.parseDouble(getInputTokens().get(0).toString());
		if(eval_value < 0){
			ArrayList<ExpressionTreeNode> shortArray = new ArrayList<ExpressionTreeNode>();
			shortArray.add(leaf);
			return new NegationNode(shortArray);
		}else{
			return leaf;
		}*/
		
		///return  ((SyntaxTreeNode)getInputTokens().get(1)).generateExpressionTree();
	}
	
	public String toActualString(){
		if(myStringValue == ""){
			myStringValue = getChildString(0);
		}
		return myStringValue;
	}
}
