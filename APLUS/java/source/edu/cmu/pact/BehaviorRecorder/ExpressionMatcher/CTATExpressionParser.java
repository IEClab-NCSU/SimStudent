package edu.cmu.pact.BehaviorRecorder.ExpressionMatcher;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTree.ExpressionTreeNode;
import edu.cmu.pact.Utilities.trace;

public class CTATExpressionParser {
	ExpressionTreeProperties properties = null;
	CTATExpressionSemantic semantic;
	
	public CTATExpressionParser(ExpressionTreeProperties props)
	{
		properties = props;
		if(semantic == null)
			semantic = new CTATExpressionSemantic(properties, null);
	}

	/**
	 * Parse an expression and return its syntax tree.
	 * @param expression
	 * @return result from {@link CTATExpressionSemantic#generateExpressionTree()};
	 *         null if an error occurs
	 */
	public ExpressionTreeNode stringToExpressionTreeNode(String expression)	{
		Boolean result;
		Throwable parseError = null;
		try {
			result = semantic.evaluate(expression);
		} catch (Exception e) {
			trace.errStack("Error from semantic.evaluate("+expression+"): ", e);
			result = false;
			parseError = e;
		}
		if(trace.getDebugCode("expr"))
			trace.out("expr", "CTATExprParser() stringToExprTN("+expression+") parse result "+
					result+"; parseError "+parseError);
		if(result)
			return semantic.generateExpressionTree();
		else
			return null;
	}
	
	public Boolean twoExpressionsEqualBasicSameOrder(String a, String b){
		return simplifyBasicTermsUnsorted(a).equals(simplifyBasicTermsUnsorted(b));
	}
	
	public Boolean twoExpressionsEqualBasic(String a, String b){
		return simplifyBasicTerms(a).equals(simplifyBasicTerms(b));
	}
	
	public Boolean twoExpressionsEqualComplex(String a, String b){
		if(trace.getDebugCode("functions"))
			trace.out("functions", "EP.twoExpressionsEqualComplex() before simplifyComplex(): a="+
					a+"; b="+b+";");
		String a0 = simplifyComplexExpr(a);
		String b0 = simplifyComplexExpr(b);
		if(trace.getDebugCode("functions"))
			trace.out("functions", "EP.twoExpressionsEqualComplex() after simplifyComplex(): a0="+
					a0+"; b0="+b0+";");
		String a1 = simplifyBasicTerms(a0);
		String b1 = simplifyBasicTerms(b0);
		if(trace.getDebugCode("functions"))
			trace.out("functions", "EP.twoExpressionsEqualComplex() after simplifyBasicTerms(): a1="+
					a1+"; b1="+b1+";");
		return a1.equals(b1);
	}

	public CTATExpressionSemantic getSemantic(){
		return semantic;
	}
	
	/**
	 * Parse and evaluate an algebraic expression. This method expects to simplify the expression
	 * to a numeric constant. If variables are included, they must be in the {@link VariableTable}
	 * in {@link #properties}.
	 * @param expression with variables defined in {@link ExpressionTreeProperties#variableTable} 
	 * @return the number calculated; {@link Double#NaN} if an error occurs
	 */
	public Double evaluate(String expression)
	{
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return Double.NaN;
		Boolean result = node.evaluate();
		if(!result)
			return Double.NaN;
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " evals to " + node.getEvalValue());
		return node.getEvalValue();
	}
	
	public String simplifyBasicTermsUnsorted(String expression)
	{
		
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		// node.performBasicSimplification(false);
		String result = node.makeCanonicalWithoutCombining(false);  // false => don't sort
		
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to "+result); // node.getNegatedString());
		return result;
	}
	
	public String strictBasicTermsUnsorted(String expression)
	{
		
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		// node.performBasicSimplification(false);
		String result = node.simplifyWithoutCanonicalWithoutCombining(false);  // false => don't sort
		
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to "+result); // node.getNegatedString());
		return result;
	}
	
	public String simplifyComplexExpr(String expression) {
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		// node.performBasicSimplification(false);
		String result = node.makeCanonical(true);  // true => sort to canonical order
		
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to "+result); // node.getNegatedString());
		return result;
	}
	
	public String simplifyBasicTerms(String expression)
	{		
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		// node.performBasicSimplification(false);
		String result = node.makeCanonicalWithoutCombining(true);  // true => sort to canonical order
		
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to "+result); // node.getNegatedString());
		return result;
	}
	
	public String strictBasicTerms(String expression)
	{		
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		// node.performBasicSimplification(false);
		String result = node.simplifyWithoutCanonicalWithoutCombining(true);  // true => sort to canonical order
		
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to "+result); // node.getNegatedString());
		return result;
	}

	/**
	 * @deprecated This method fails to consider "1x" equivalent to "x", among other troubles. Instead
	 *             use {@link #simplifyBasicTerms(String)} or {@link #simplifyBasicTermsUnsorted(String)}.
	 * @param expression
	 * @return
	 */
	public String simplifyBasic(String expression)
	{
		
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		node.performBasicSimplification(true);
		
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to " + node.getNegatedString());
		return node.getNegatedString();
	}
	
	public String simplifyComplex(String expression)
	{
		ExpressionTreeNode node = stringToExpressionTreeNode(expression);
		if(node == null)
			return expression;
		node = node.performComplexSimplification();
		if (trace.getDebugCode("functions"))
			trace.out("functions",expression + " simplifies to " + node.getNegatedString());
		return node.getNegatedString();
	}
}
