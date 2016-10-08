package edu.cmu.old_pact.cmu.sm;

//BadExpression
//a BadExpression is the result of a parsing error


public class BadExpression extends Expression {
	
	public BadExpression() {
	}
	
	protected String toASCII(String openParen, String closeParen) {
		return "BadParse";
	}
		
	public String debugForm() {
		return "BadParse";
	}
}
