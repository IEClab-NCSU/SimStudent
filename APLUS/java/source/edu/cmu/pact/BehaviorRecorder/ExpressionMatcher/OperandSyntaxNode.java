package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class OperandSyntaxNode extends SyntaxTreeNode {

	
	public OperandSyntaxNode(Rule rule, List inputTokens, List ranges,
			ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		if(getInputTokens().size() == 3){
			type = PAREN_EXPRESSION;
		}else{
			if(getChildNode(0) instanceof VarRefSyntaxNode){
				type = VARREF;
			}else{
				type = NUMBER;
			}
		}
	}
	
	public ExpressionTreeNode generateExpressionTree()
	{
		if(type == VARREF || type == NUMBER)
			return  ((SyntaxTreeNode)getInputTokens().get(0)).generateExpressionTree();
		else { //Remove parentheses, but mark as not a simple term
			ExpressionTreeNode result = ((SyntaxTreeNode)getInputTokens().get(1)).generateExpressionTree();
			result.setSimpleTerm(false);
			return result;
		}
	}

	public String toActualString()
	{
		if(this.myStringValue == ""){
			if(type == VARREF || type == NUMBER)
				myStringValue = getChildNode(0).toActualString();
			else//UNARY_MINUS
				myStringValue = "(" + getChildNode(1).toActualString() + ")";
		}
		return myStringValue;
	}

}
