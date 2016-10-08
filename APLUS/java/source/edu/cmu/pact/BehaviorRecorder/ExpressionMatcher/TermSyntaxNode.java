package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.MultiplicationNode;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions.ifThen;
import edu.cmu.pact.Utilities.trace;
import fri.patterns.interpreter.parsergenerator.semantics.TreeBuilderSemantic.Node;
import fri.patterns.interpreter.parsergenerator.syntax.Rule;

public class TermSyntaxNode extends SyntaxTreeNode {

	/**
	 * Factory to check for {@link SyntaxTreeNode#IMPLICIT_MULTIPLY} nodes whose right-hand operand is numeric.
	 * @param rule
	 * @param inputTokens
	 * @param ranges
	 * @param properties
	 * @return new instance of this class normally; instance of {@link Node} on error
	 */
	static Node create(Rule rule, List inputTokens, List ranges,
			ExpressionTreeProperties properties) {
		if(trace.getDebugCode("expr")) {              // dump the inputTokens values and types
			StringBuilder sb = new StringBuilder("TermSyntaxNode.create()");
			sb.append(" inputTokens.size ").append(inputTokens.size());
			for(int i = 0; i < inputTokens.size(); ++i) {
				sb.append("; [").append(i).append("]");
				Object tkn = inputTokens.get(i);
				if(!(tkn instanceof SyntaxTreeNode))  // can't happen?
					sb.append(" {not SyntaxTreeNode} ").append(tkn);
				else {
					SyntaxTreeNode node = (SyntaxTreeNode) tkn;
					sb.append(" \"").append(node.toActualString()).append("\"");
					sb.append(" type ").append(node.getClass().getSimpleName());
				}
			}
			trace.out("expr", sb.toString());
		}
		TermSyntaxNode result = new TermSyntaxNode(rule, inputTokens, ranges, properties);
		if(result.type != IMPLICIT_MULTIPLY)
			return result;
		Object rightTkn = result.getInputTokens().get(1);
		if(!(rightTkn instanceof SyntaxTreeNode))
			return result;               // probably an error, but don't know how to handle it
		SyntaxTreeNode rightOp = (SyntaxTreeNode) rightTkn;
		try {
//			Number rightNum = Double.parseDouble(rightOp.toActualString());
//			--didn't handle x2^3: must check for exponent node in right operand
			boolean rightIsNumeric = false;
			SyntaxTreeNode base = null;
			if(rightOp instanceof ExponentSyntaxNode) {
				base = ((ExponentSyntaxNode) rightOp).getChildNode(0);
				double rightNum = Double.parseDouble(base.toActualString());
				rightIsNumeric = true;
			}
			if(!rightIsNumeric)
				return result;

			Object leftTkn = result.getInputTokens().get(0);
			if(!(leftTkn instanceof SyntaxTreeNode))
				return result;               // probably an error, but don't know how to handle it
			SyntaxTreeNode leftOp = (SyntaxTreeNode) leftTkn;

			if(trace.getDebugCode("expr"))
				trace.out("expr", "IMPLICIT_MULTIPLY: right operand numeric "+base.toActualString()+
						"; left operand is "+leftOp.toActualString()+
						", class "+leftOp.getClass().getSimpleName()+
						", type "+leftOp.type);

			if(leftOp.toActualString().trim().endsWith(")"))
				return result;

			return null;  // can't have numeric following anything but a parenthesized expression
//
//			if(VarRefSyntaxNode.isVarRef(leftStr))
//				return null;                               // syntax error: "x2", "y 3", etc.
//			Number leftNum = Double.parseDouble(leftStr);
//			return null;                                   // syntax error: "3 2", "4.55 3.3", etc.

		} catch(NumberFormatException e) {
			return result;                                     // this is the good return case
		} catch(Exception e) {
			if(trace.getDebugCode("functions"))
				trace.out("functions", "unexpected exception parsing operands: "+e+
						"; cause "+e.getCause());
			return result;               // probably an error, but don't know how to handle it
		}
	}
	
	//public String IDENTITY = "IDENTITY";//	{ "TERM", "NEGOPERAND"}

	private TermSyntaxNode(Rule rule, List inputTokens, List ranges, ExpressionTreeProperties properties) {
		super(rule, inputTokens, ranges, properties);
		
		if(getInputTokens().size() == 1)  //Case 1 :   { "TERM", "NEGOPERAND"},
			type = IDENTITY;
		else if (getInputTokens().size() == 2)
			type = IMPLICIT_MULTIPLY;
		else if (getChildString(1).equals("*"))
			type = MULTIPLY;
		else{
			type = DIVIDE;
		}
	}

	public ExpressionTreeNode generateExpressionTree()
	{
		if(trace.getDebugCode("expr"))
			trace.out("expr", "TermSyntaxNode.gET() inputTokens.size "+getInputTokens().size());
		if(type == MULTIPLY || type == DIVIDE || type == IMPLICIT_MULTIPLY){
			ExpressionTreeNode left = ((SyntaxTreeNode)(getInputTokens().get(0))).generateExpressionTree();
			ExpressionTreeNode right;
			if(type == MULTIPLY || type ==DIVIDE)
				right =  ((SyntaxTreeNode)(getInputTokens().get(2))).generateExpressionTree();
			else // type == IMPLICIT_MULTIPLY
				right = ((SyntaxTreeNode)(getInputTokens().get(1))).generateExpressionTree();

			ArrayList<ExpressionTreeNode> multiplicands,divisors = null;
			
			if((type==MULTIPLY) || (type==IMPLICIT_MULTIPLY)){
				multiplicands = new ArrayList<ExpressionTreeNode>();
				multiplicands.add(left);
				multiplicands.add(right);
			}else{
				multiplicands = new ArrayList<ExpressionTreeNode>();
				multiplicands.add(left);
				divisors = new ArrayList<ExpressionTreeNode>();
				divisors.add(right);
			}
			MultiplicationNode result = new MultiplicationNode(multiplicands, divisors, false, properties);
			result.setSimpleTerm(isSimpleTerm(left, right));
			return result;
		}
		else //IDENTITY 
		{
			return ((SyntaxTreeNode)(getInputTokens().get(0))).generateExpressionTree();
		}
	}

	/**
	 * Tell whether this is a simple term. 
	 * @param left first operand
	 * @param right second operand
	 * @return true only if {@link SyntaxTreeNode#type} is {@link SyntaxTreeNode#IMPLICIT_MULTIPLY}
	 *         and {@link ExpressionTreeNode#isSimpleTerm()} is true for both operands
	 */
	private boolean isSimpleTerm(ExpressionTreeNode left, ExpressionTreeNode right) {
		if(type != IMPLICIT_MULTIPLY)
			return false;
		return left.isSimpleTerm() && right.isSimpleTerm();
	}

	public String toActualString()
	{
		if(this.myStringValue == ""){
			if(type == IDENTITY)
				myStringValue = getChildNode(0).toActualString();
			else if(type == IMPLICIT_MULTIPLY)
				myStringValue = getChildNode(0).toActualString() + getChildNode(1).toActualString();
			else if(type == MULTIPLY)
				myStringValue = getChildNode(0).toActualString() + "*" + getChildNode(2).toActualString();
			else //DIVIDE
				myStringValue = getChildNode(0).toActualString() + "/" + getChildNode(2).toActualString();
		}

		return myStringValue;
	}
}
