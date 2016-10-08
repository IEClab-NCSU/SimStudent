package edu.cmu.old_pact.cmu.sm;
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.NumberQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

//NumericExpression is an abstract class that includes NumberExpression and FractionExpression

public abstract class NumericExpression extends Expression {

	public abstract boolean isIntegerType();
	public abstract boolean isFloatType();
	public abstract boolean isFractionType();
	public abstract double doubleValue(); //everything can be coerced to a double
	public abstract Number getValue(); //everything can also be packed into a Number
	
	//numAdd, subtract, multiply and divide are low-level operators on NumericExpressions
	//they are guaranteed to return NumericExpressions
	protected NumericExpression numAdd(NumericExpression other) {
		NumberExpression newVal;
		
		newVal = new NumberExpression(this.doubleValue() + other.doubleValue());
		return newVal;
	}

	protected NumericExpression numSubtract(NumericExpression other) {
		NumberExpression newVal;
		
		newVal = new NumberExpression(this.doubleValue() - other.doubleValue());
		return newVal;
	}

	protected NumericExpression numMultiply(NumericExpression other) {
		NumericExpression newVal;
		
		if (this.isFractionType() && other.isFractionType()) {
			NumericExpression oldTop = (NumericExpression)(this.numerator());
			NumericExpression oldBottom = (NumericExpression)(this.denominator());
			NumericExpression newTop = oldTop.numMultiply((NumericExpression)(other.numerator()));
			NumericExpression newBottom = oldBottom.numMultiply((NumericExpression)(other.denominator()));
			newVal = new FractionExpression(new NumberExpression(newTop.getValue()),new NumberExpression(newBottom.getValue()));
		}
		else if (this.isFractionType() && !other.isFractionType()) {
			NumericExpression oldTop = (NumericExpression)(this.numerator());
			NumericExpression newTop = oldTop.numMultiply(other);
			NumberExpression topNum = new NumberExpression(newTop.getValue());
			NumberExpression theBottom = (NumberExpression)(this.denominator());
			newVal = new FractionExpression(topNum,theBottom);
		}
		else if (!this.isFractionType() && other.isFractionType()) {
			NumericExpression newTop = this.numMultiply((NumericExpression)(other.numerator()));
			NumberExpression topNum = new NumberExpression(newTop.getValue());
			NumberExpression theBottom = (NumberExpression)(other.denominator());
			newVal= new FractionExpression(topNum,theBottom);
		}
		else
			newVal = new NumberExpression(this.doubleValue() * other.doubleValue());
		return (NumericExpression)newVal;
	}

	protected NumericExpression numDivide(NumericExpression other) {
		NumberExpression newVal;
		newVal = new NumberExpression(this.doubleValue() / other.doubleValue());
		return newVal;
	}
	
	//numbers can be combined with other numbers
	protected boolean isLike(Expression ex) {
		if (ex instanceof NumericExpression)
			return true;
		else if(ex instanceof TermExpression){
			return ex.isLike(this);
		}
		else if(ex instanceof FencedExpression){
			return ex.isLike(this);
		}
			return false;
	}

	protected Expression addLikeTerms(Expression ex) {
		if (ex instanceof FractionExpression &&
			((FractionExpression)ex).isIntegerFraction())
			return ex.addLikeTerms(this);
		else if (ex instanceof NumericExpression) {
			NumericExpression nex = (NumericExpression) ex;
			return this.numAdd(nex);
		}
		else if(ex instanceof TermExpression){
			return ex.addLikeTerms(this);
		}
		else if(ex instanceof FencedExpression){
			return ex.addLikeTerms(this);
		}
		else
			throw new IllegalArgumentException("addLikeTerms (NumericExpession) called on uncombinable objects");
	}
	
	protected Expression iMultiply(Expression ex) {
		if (ex instanceof NumericExpression) {
			NumericExpression nex = (NumericExpression) ex;
			return this.numMultiply(nex);
		}
		else
			throw new IllegalArgumentException("iMultiply (NumericExpession) called on uncombinable objects");
	}
	
	public Expression numerator() {
		return new NumberExpression(getValue());
	}
	
	public Expression denominator() {
		return new NumberExpression(1);
	}
	
	public NumberExpression toNumberExpression() {
		return new NumberExpression(getValue());
	}
	
	public boolean isNegative() {
		return (doubleValue() < 0);
	}
	
	//numbers sort after larger numbers and before everything else
	public boolean termSortBefore(Expression ex) {
		if (ex instanceof NumericExpression) {
			NumericExpression nEx = (NumericExpression)ex;
			if (doubleValue() < nEx.doubleValue())
				return true;
			else
				return false;
		}
		else
			return true;
	}
	
//	public Expression divide(Expression ex) {
//		if (this instanceof NumberExpression &&
//			ex instanceof NumberExpression)
//			return new FractionExpression((NumberExpression)this,(NumberExpression)ex);
//		else
//			return super.divide(ex);
//	}
	
	public Vector allNumbersWhole() {
		Vector theVec = new Vector();
		theVec.addElement(this);
		return theVec;
	}

	public boolean algebraicEqual(Expression ex){
		if(ex instanceof NumberExpression){
			return ex.algebraicEqual(this);
		}
		if(ex instanceof NumericExpression){
			return ((NumericExpression)ex).doubleValue() == doubleValue();
		}
		else if(ex instanceof ConstantExpression){
			NumericExpression exsub = (NumericExpression)ex.substConstants();
			return exsub.doubleValue() == doubleValue();
		}
		else{
			return super.algebraicEqual(ex);
		}
	}

	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("value"))
			return new NumberQuery(getValue());
		else if (prop.equalsIgnoreCase("isNumber"))
			return new BooleanQuery(true);
		else if (prop.equalsIgnoreCase("isInteger"))
			return new BooleanQuery(isIntegerType());
		else if (prop.equalsIgnoreCase("isNotDecimal"))
			return new BooleanQuery(isIntegerType());
		else if (prop.equalsIgnoreCase("isDecimal"))
			return new BooleanQuery(isFloatType());
		else if (prop.equalsIgnoreCase("isFraction"))
			return new BooleanQuery(isFractionType());
		else
			return super.getProperty(prop);
	}
}

