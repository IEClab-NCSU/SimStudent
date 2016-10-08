package edu.cmu.old_pact.cmu.sm;

//BadExpressionError - class representing a parse error

public class BadExpressionError extends Exception {
	private String expression;
	
	public BadExpressionError(String exp) {
		expression = exp;
	}
	
	public String toString() {
		return "Cannot parse expression: \""+expression+"\"";
	}
	
	public String theExpression() {
		return expression;
	}
}
