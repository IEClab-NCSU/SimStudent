package edu.cmu.old_pact.cmu.sm;

//Variables
//Variables are just strings

import java.util.Vector;

public class VariableExpression extends Expression {
	protected String varString;
	
	protected String getString() {
		return varString;
	}
	
	public VariableExpression(String name) {
		varString = name;
	}
	
	public Expression exceptSimplifiedCoefficient() {
		return this;
	}
	
	//addLikeTerms for variables sums the coefficients (we don't need to check whether they're the same
	//variable, since addLikeTerms assumes that canCombine succeeded)
	protected Expression addLikeTerms(Expression ex) {
		if (ex instanceof VariableExpression)
			return new TermExpression(new NumberExpression(2),this);
		else if (ex instanceof TermExpression) {
			NumericExpression termCoeff = ex.numericUnsimplifiedCoefficient();
			NumericExpression newCoeff = termCoeff.numAdd(new NumberExpression(1));
			return new TermExpression(newCoeff,this);
		}			
		else if(ex instanceof FencedExpression){
			return ex.addLikeTerms(this);
		}
		else
			throw new IllegalArgumentException("VariableExpression.addLikeTerms {"+debugForm()+"} called on "+ex.debugForm());
	}
			
	//if we're multiplying two variableExpressions, we get a power of 2
	protected Expression iMultiply(VariableExpression ex) {
		return new ExponentExpression(ex,2);
	}
	
	public boolean isLike(Expression ex) {
		//System.out.println("VE.isLike: " + debugForm() + ".(" + ex.debugForm() + ")");
		if (ex instanceof VariableExpression)
			//return (getString().equals(((VariableExpression)ex).getString()));
			return exactEqual(ex);
		else if (ex instanceof TermExpression) {
			Expression body = ex.exceptUnsimplifiedCoefficient(); //hmm -- can we CLT x and 2*3x?
			if (body instanceof TermExpression) {
				TermExpression bodyEx = (TermExpression)body;
				if (bodyEx.numSubTerms() == 1 && algebraicEqual(bodyEx.getTerm(0)))
					return true;
				else
					return false;
			}
			else if (body instanceof VariableExpression)
				return exactEqual(body);
			else
				return false;
		}
		else if(ex instanceof FencedExpression){
			return ex.isLike(this);
		}
		else
			return false;
	}
	
	public boolean exactEqual(Expression ex) {
		//System.out.println("VE.exactEqual: " + debugForm() + ".(" + ex.debugForm() + ")");
		if (ex instanceof BoundExpression ||
			ex instanceof ConstantExpression ||
			ex instanceof LiteralExpression){
			return false;
		}
		if (ex instanceof VariableExpression)
			return varString.equalsIgnoreCase(((VariableExpression)ex).getString());
		else
			return false;
	}
	
	//variables sort before other variables, if their string is less
	//variables sort after numeric expressions (and before everything else)
	public boolean termSortBefore(Expression ex) {
		/*System.out.println(debugForm() + ".termSortBefore(" +
		  ex.debugForm() + ")");*/
		if (ex instanceof BoundExpression){
			//System.out.println("\tfalse");
			return false;
		}
		else if (ex instanceof LiteralExpression){
			//System.out.println("\tfalse");
			return false;
		}
		else if (ex instanceof ConstantExpression){
			//System.out.println("\tfalse");
			return false;
		}
		else if (ex instanceof VariableExpression) {
			VariableExpression vEx = (VariableExpression)ex;
			if (getString().compareTo(vEx.getString()) > 0){
				//System.out.println("\ttrue");
				return true;
			}
			else{
				//System.out.println("\tfalse");
				return false;
			}
		}
		else if (ex instanceof NumericExpression){
			//System.out.println("\tfalse");
			return false;
		}
		else{
			//System.out.println("\ttrue");
			return true;
		}
	}
	
	//substitute plugs in for the variable...
//	public Expression substitute(String var,Expression newVal) {
//		if (var.equals(varString))
//			return newVal;
//		else
//			return this;
//	}
	
	public double degree() {
		return 1.0;
	}
	
	public Vector variablesUsed() {
		Vector newV = new Vector();
		newV.addElement(varString);
		return newV;
	}
	
//	public boolean exactEqual(VariableExpression ex) {
//		return varString.equals(ex.getString());
//	}

	public String toASCII(String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();
		else
			return varString;
	}
	
	public String toMathML() {
		return addMathMLAttributes("<mi>"+varString+"</mi>");
		//return addMathMLAttributes("<mi color='#33BB33'>"+varString+"</mi>");
	}
	
	public String debugForm() {
		return "[var: "+varString+"]";
	}
}
