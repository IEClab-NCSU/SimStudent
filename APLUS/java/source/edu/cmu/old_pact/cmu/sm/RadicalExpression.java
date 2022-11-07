package edu.cmu.old_pact.cmu.sm;

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//RadicalExpressions
//a RadicalExpression is an ExponentExpression that displays as a radical
//The exponents of RadicalExpressions should always be fractions (or ratios),
//and we allow radicals with exponents having non-1 numerators, since this
//allows us to display a radical with an exponent and no parens around the body

public class RadicalExpression extends ExponentExpression {
	public RadicalExpression(Expression ex,int exp) {
		super(ex,exp);
	}
	
	public RadicalExpression(Expression ex,Expression expo) {
		super(ex,expo);
	}
	
	//for a RadicalExpression, we know that the first component is the base and the second is the exponent
	protected Expression buildFromComponents(Vector components) {
		Expression base = (Expression)(components.elementAt(0));
		Expression exp = (Expression)(components.elementAt(1));
		return new RadicalExpression(base,exp).cleanExpression();
	}
	
	protected Expression buildFromComponents(ExpressionArray components) {
		return new RadicalExpression(components.expressionAt(0),components.expressionAt(1)).cleanExpression();
	}
	

	//cleanExpression can change a RadicalExpression into an ExponentExpression
	//If the exponent is not a fraction or ratio, this is a badly formed radical
	public Expression cleanExpression() {
		if (!((exponent instanceof FractionExpression) || 
			  (exponent instanceof RatioExpression))) {
			trace.out("exponent is not fraction or ratio");
			return new ExponentExpression(body,exponent);
		}
		else if (!(exponent.numerator().isOne())) {
			trace.out("numerator is not one");
			return new ExponentExpression(body,exponent);
		}
		else
			return this;
	}
	
	public String toASCII(String openParen, String closeParen) {
		StringBuffer ret = new StringBuffer(asciiSBsize);
		if (exponent.numerator().isOne() && //special-case squareroot
			exponent.denominator().algebraicEqual(new NumberExpression(2))){
			ret.append("sqrt(").append(body.toASCII(openParen,closeParen)).append(")");
		}
		else{
			ret.append("root(").append(body.toASCII(openParen,closeParen)).append(",");
			ret.append(exponent.denominator().toASCII(openParen,closeParen)).append(")");
		}

		return ret.toString();
	}
	
	public String toMathML() {
		StringBuffer ret = new StringBuffer(mathmlSBsize);
		if (exponent.numerator().isOne() && //special-case squareroot
			exponent.denominator().algebraicEqual(new NumberExpression(2)))
			ret.append("<msqrt>").append(body.toMathML()).append("</msqrt>");
		else if (exponent.numerator().isOne())
			ret.append("<mroot>").append(body.toMathML()).append(exponent.denominator().toMathML()).append("</mroot>");
		else //should never happen, but just in case...
			ret.append("<mroot>").append(new ExponentExpression(body,exponent.numerator()).toMathML()).append(exponent.denominator().toMathML()).append("</mroot>");

		return addMathMLAttributes(ret.toString());
	}

	public String debugForm() {
		return "[RadicalExpression: "+body.debugForm()+" :: "+ exponent.debugForm() +"]";
	}

	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("isRadical")){
			return new BooleanQuery(true);
		}
		else{
			return super.getProperty(prop);
		}
	}
}
