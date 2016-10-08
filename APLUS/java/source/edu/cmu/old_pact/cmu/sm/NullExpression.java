//nullExpression is a placeholder for an expression when there's nothing there

package edu.cmu.old_pact.cmu.sm;


public class NullExpression extends Expression {
	
	public NullExpression() {
	}
	
	public String toASCII(String openParen, String closeParen) {
		return "<NULL>";
	}
		
	public String debugForm() {
		return "BadParse";
	}
	
	public boolean algebraicEqual(Expression ex) {
		return false;
	}
}