package edu.cmu.old_pact.cmu.sm;

/*A literal expression is used to represent a variable for which we
  are not solving.  It differs from a VariableExpression only in that
  it does not count itself via variablesUsed().*/

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class LiteralExpression extends VariableExpression {
	
	public LiteralExpression(String name) {
                super(name);
	}
	
        public Expression simplifiedCoefficient(){
            //System.out.println(debugForm() + ".simplifiedCoefficient(): returning this");
            return this;
        }

	//addLikeTerms for literals sums the coefficients (we don't need to check whether they're the same
	//variable, since addLikeTerms assumes that canCombine succeeded)
	protected Expression addLikeTerms(Expression ex) {
		if (ex instanceof LiteralExpression)
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
			throw new IllegalArgumentException("LiteralExpression.addLikeTerms {"+debugForm()+"} called on "+ex.debugForm());
	}
			
	//if we're multiplying two LiteralExpressions, we get a power of 2
	protected Expression iMultiply(LiteralExpression ex) {
		return new ExponentExpression(ex,2);
	}
	
	public boolean exactEqual(Expression ex) {
		if (ex instanceof LiteralExpression)
			return varString.equalsIgnoreCase(((LiteralExpression)ex).getString());
		else
			return false;
	}
	
	//Literals sort before variables
        //Literals sort before other literals, if their string is less
	//Literals sort after numeric expressions (and before everything else)
	public boolean termSortBefore(Expression ex) {
		/*System.out.println(debugForm() + ".termSortBefore(" +
		  ex.debugForm() + ")");*/
		if (ex instanceof LiteralExpression) {
			LiteralExpression vEx = (LiteralExpression)ex;
			if (getString().compareTo(vEx.getString()) < 0){
				//System.out.println("\ttrue");
				return true;
			}
			else{
				//System.out.println("\tfalse");
				return false;
			}
		}
		/*else if (ex instanceof VariableExpression){
		  System.out.println("\ttrue");
		  return true;
		  }
		  else if (ex instanceof PolyExpression){
		  System.out.println("\ttrue");
		  return true;
		  }*/
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
		//newV.addElement(varString);
		return newV;
	}
	
//	public boolean exactEqual(LiteralExpression ex) {
//		return varString.equals(ex.getString());
//	}

	public String toASCII(String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();
		else
			return varString;
	}
	
	public String toMathML() {
		return addMathMLAttributes((new StringBuffer(mathmlSBsize)).append("<mi>").append(varString).append("</mi>").toString());
	}
	
	public String debugForm() {
		return "[lit: "+varString+"]";
	}

	public Queryable getProperty(String prop) throws NoSuchFieldException {
            //System.out.println("getProperty (literal): "+prop);
            Queryable result=null;
            if (prop.equalsIgnoreCase("isLiteral")) {
                result = new BooleanQuery(true);
            }
            else{
                result = super.getProperty(prop);
            }

            return result;
        }
}
