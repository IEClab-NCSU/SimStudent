package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.List;
import java.util.regex.Pattern;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.VariableNode;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class VarRefSyntaxNode extends SyntaxTreeNode {

	/** To test whether a string is a variable: accepts any single letter. */
	private static final Pattern Variable = Pattern.compile("^[a-zA-Z]$");

	//public String IDENTITY = "IDENTITY";
	
	public VarRefSyntaxNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		type = IDENTITY;
	}
	
	public ExpressionTreeNode generateExpressionTree()
	{
		return new VariableNode((String) getInputTokens().get(0), properties);
		///return  ((SyntaxTreeNode)getInputTokens().get(1)).generateExpressionTree();
	}
	
	public String toActualString(){
		if(myStringValue == ""){
			myStringValue = getChildString(0);
		}
		return myStringValue;
	}

	/**
	 * Tell whether a string qualifies as a variable reference.
	 * @param var string to test
	 * @return true if not null and satisfies {@link #Variable}
	 */
	public static boolean isVarRef(String var) {
		return var != null && Variable.matcher(var).matches();
	}
}
