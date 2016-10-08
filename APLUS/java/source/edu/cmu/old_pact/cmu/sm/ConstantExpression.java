package edu.cmu.old_pact.cmu.sm;

/*a constant expression is something like pi or e.  It's really a
  number, but it's usually written as a symbol.  In addition to a
  string representation (e.g. "pi"), it also may have special mathml
  code (e.g.? "<pi>") used to display it.  Since we want to be able to
  treat them as variables until an equation is otherwise solved, and
  then convert all constants to their decimal equivalents, constants
  are a subclass of VariableExpression, not NumericExpression.*/

import java.util.Vector;

public class ConstantExpression extends VariableExpression {
        String mathml;
        double value;
	
	public ConstantExpression(String name) {
            /*should we throw out this constructor entirely?*/
            this(name,"<mi>"+name+"</mi>",1.0);
	}

        public ConstantExpression(String name,String mathmlRep, double val){
            super(name);
            mathml = mathmlRep;
            value = val;
        }

        public Expression simplifiedCoefficient(){
            return this;
        }

	//addLikeTerms for constants sums the coefficients (we don't need to check whether they're the same
	//constant, since addLikeTerms assumes that canCombine succeeded)
	protected Expression addLikeTerms(Expression ex) {
		if (ex instanceof ConstantExpression)
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
			throw new IllegalArgumentException("ConstantExpression.addLikeTerms {"+debugForm()+"} called on "+ex.debugForm());
	}
			
	//if we're multiplying two constantExpressions, we get a power of 2
	protected Expression iMultiply(ConstantExpression ex) {
		return new ExponentExpression(ex,2);
	}
	
	public boolean exactEqual(Expression ex) {
		if (ex instanceof ConstantExpression)
			return varString.equalsIgnoreCase(((ConstantExpression)ex).getString());
		else
			return false;
	}

	public boolean algebraicEqual(Expression ex){
		if(ex instanceof NumericExpression){
			return ex.algebraicEqual(this);
		}
		else{
			return super.algebraicEqual(ex);
		}
	}
	
	//constants sort before variables
	//constants sort before other constants, if their string is less
	//constants sort after numeric expressions (and before everything else)
	public boolean termSortBefore(Expression ex) {
		if (ex instanceof ConstantExpression) {
			ConstantExpression vEx = (ConstantExpression)ex;
			if (getString().compareTo(vEx.getString()) > 0)
				return true;
			else
				return false;
		}
		else if (ex instanceof VariableExpression){
			return true;
		}
		else if (ex instanceof NumericExpression)
			return false;
		else
			return true;
	}

        public Expression substConstantsWhole(){
            return new NumberExpression(value);
        }

        public boolean canSubstConstantsWhole(){
            return true;
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
		//newV.addElement(varString);
		return newV;
	}
	
//	public boolean exactEqual(ConstantExpression ex) {
//		return varString.equals(ex.getString());
//	}

	public String toASCII(String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();
		else
			return varString;
	}
	
	public String toMathML() {
            return addMathMLAttributes(mathml);
	}
	
	public String debugForm() {
		return "[const: "+varString+"]";
	}
}
