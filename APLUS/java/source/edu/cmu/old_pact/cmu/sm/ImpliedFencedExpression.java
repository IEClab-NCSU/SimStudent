package edu.cmu.old_pact.cmu.sm;
import java.util.Vector;

//an ImpliedFencedExpression is a fenced expression where the fence has been inserted
//to preserve order of operations in the ASCII representations. We make a distinction
//between these kind of parens so that, for example, MathML output can exclude them.
//In the standard ASCII representation, both types of parens are printed the same.
//"Intermediate ASCII" uses "(" and ")" for explicit parens as opposed to "[" and "]"
//for implicit ones. Intermediate ASCII should be used when you are chaining operations and
//producing intermediate ASCII output. This will preserve the implicit or explicit
//nature of the parens, since the parser understand intermediate ASCII.
// For presentation to the user, use either ASCII or MathML
//
//For example:
//
//  Input           ASCII          Intermediate ASCII           MathML (just showing fences)
//
//  "(x+5)/10"      (x+5)/10       (x+5)/10                     <mfenced>x+5</mfenced> / 10
//
//  "x+5" / "10"    (x+5)/10       [x+5]/10                     <mrow>x+5</mrow> / 10
public class ImpliedFencedExpression extends FencedExpression {

	public ImpliedFencedExpression(Expression enclosed) {
		super(enclosed);
	}

	//we know there's only one component
	protected Expression buildFromComponents(Vector components) {
		return new ImpliedFencedExpression((Expression)components.elementAt(0));
	}
	
	protected Expression buildFromComponents(ExpressionArray components) {
		return new ImpliedFencedExpression(components.expressionAt(0));
	}
	
	public Expression removeImpliedFencesWhole() {	
		return unfence();
	}

	//intermediateString uses square brackets
	public String toIntermediateString() {
		return "["+enclosedExpression.toIntermediateString()+"]";
	}
	
	//mathML uses no parens
	public String toMathML() {
		return addMathMLAttributes(enclosedExpression.toMathML());
	}

	public String debugForm() {
		return "[ImpliedFencedExpression: "+enclosedExpression.debugForm()+"]";
	}
}

