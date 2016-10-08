package edu.cmu.old_pact.cmu.sm;
import java.util.Vector;

//a FencedExpression is an expression enclosed in mathematically unnecessary parentheses
//(this class might be generalized for other types of enclosures...)

/*mmmBUG: this isn't exactly true: the expression x*(a+b) parses to something like
  [TermExp(2):  1:[var: x] 2:[FencedExpression: [Poly(2):  1:{+}[var: a] 2:{+}[var: a]]]]
  Here the parens are mathematically necessary, but we're getting a FencedExpression out
  of the parser anyway.  But if we multiply("x","a+b") in the symbolmanipulator, then the
  fencedexpression isn't there -- in this case the parens are supplied by the outer
  TermExpression when needed (e.g. in toASCII()).  So this is either a bug in the parser
  or the above definition of a FencedExpression isn't entirely accurate.  We're going
  with the latter for now.

  update: the parser now calls removeRedundantFences(), which should take care of the above.*/

public class FencedExpression extends Expression implements CompoundExpression {
	protected Expression enclosedExpression;
	
	public FencedExpression(Expression enclosed) {
		enclosedExpression = enclosed;
	}
	
	//here, there's only one component
	protected Expression buildFromComponents(Vector components) {
		return new FencedExpression((Expression)components.elementAt(0));
	}
	
	protected Expression buildFromComponents(ExpressionArray components) {
		return new FencedExpression(components.expressionAt(0));
	}
	
	protected Vector getComponents() {
		Vector comps = new Vector();
		comps.addElement(enclosedExpression);
		return comps;
	}
	
	protected ExpressionArray getComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate();
		compArray.addExpression(enclosedExpression);
		return compArray;
	}

	public boolean isNegative(){
		return getFenceContents().isNegative();
	}

	public boolean isLike(Expression ex) {
		return enclosedExpression.isLike(ex);
	}
	
	protected Expression addLikeTerms(Expression ex){
		return enclosedExpression.addLikeTerms(ex);
	}

//	public Expression standardizeWhole(boolean dd) {
//		return enclosedExpression;
//	}
	
	public boolean exactEqual(Expression ex) {
		if (ex instanceof FencedExpression) {
			FencedExpression fEx = (FencedExpression)ex;
			return fEx.getFenceContents().exactEqual(enclosedExpression);
		}
		else
			return false;
	}
	
	public Expression getFenceContents() {
		return enclosedExpression;
	}
	
	//getFenceDeepContents removes all non-nested fences
	public Expression getFenceDeepContents() {
		if (enclosedExpression instanceof FencedExpression)
			return ((FencedExpression)enclosedExpression).getFenceDeepContents();
		else
			return enclosedExpression;
	}
	
	public Expression unfence() {
		return getFenceDeepContents();
	}
	
	public Vector getExplicitFactors(boolean expandNegated) {
		return unfence().getExplicitFactors(expandNegated);
	}

	protected Expression removeParensWhole() {
		return enclosedExpression;
		//return (new ImpliedFencedExpression(enclosedExpression));
	}
	
	public boolean canRemoveParensWhole() {
		return true;
	}
	
	//the openParen and closeParen args to toASCII are for *implied* open
	//and close parens. Since fencedExpressions use, by definition, explict
	//parens, we ignore the open and close args (except to pass them on to the
	//enclosed expression)
	public String toASCII(String openParen,String closeParen) {
		return (new StringBuffer(asciiSBsize)).append("(").append(enclosedExpression.toASCII(openParen,closeParen)).append(")").toString();
	}
	
	public String toMathML() {
		return addMathMLAttributes((new StringBuffer(mathmlSBsize)).append("<mfenced>").append(enclosedExpression.toMathML()).append("</mfenced>").toString());
	}
	
	public String debugForm() {
		return "[FencedExpression: "+enclosedExpression.debugForm()+"]";
	}
	
}
	
