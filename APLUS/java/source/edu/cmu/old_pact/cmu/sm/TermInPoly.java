package edu.cmu.old_pact.cmu.sm;
import edu.cmu.old_pact.cmu.sm.query.NumberQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

//a TermInPoly represents a term in a polynomial
//This is used in the query interface
//Since the TermInPoly is, itself, an Expression, it implements getProperty
//Properties of PolyExpressions that return lists of terms really return
//lists of TermInPolys

public class TermInPoly extends ExpressionPart {
	int num;
	PolyExpression thePoly;
	
	TermInPoly(Expression theExpression,PolyExpression poly, int expNum) {
		super("Term"+expNum,theExpression);
		thePoly = poly;
		num = expNum;
	}
	
	public String getStringValue() {
		return myExpression.getStringValue();
	}
	
	public Number getNumberValue() {
		return myExpression.getNumberValue();
	}
	
	public boolean getBooleanValue() {
		return myExpression.getBooleanValue();
	}
	
	public Queryable[] getArrayValue() {
		return myExpression.getArrayValue();
	}
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("position")) //position of the term in the polynomial
			return new NumberQuery(new Integer(num));
		else if (prop.equalsIgnoreCase("operator")) { //the operator (+/-) of this term
			return thePoly.getProperty("operator "+(num-1));
		}
		else if (prop.equalsIgnoreCase("body")) { //"body" is the term without the sign
			return myExpression;
		}
		else
			return myExpression.getProperty(prop);
	}
	
	public void setProperty(String prop, String value) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("operator"))
			thePoly.setProperty("operator "+(num-1),value); //subtract 1, since op is *before* term
		else { 
			//since the term includes its operator, call setProperty on both the term and the operator
			ExpressionPart operatorPart = new ExpressionPart("operator "+(num-1),thePoly);
			operatorPart.setProperty(prop,value);
			/*we can't set the property on myExpression, because it's
              a different object than the one actually contained in
              the polynomial, so the property won't persist*/
			//myExpression.setProperty(prop,value);
			thePoly.getTermNoSign(num-1).setProperty(prop,value);
		}
	}
	
}
