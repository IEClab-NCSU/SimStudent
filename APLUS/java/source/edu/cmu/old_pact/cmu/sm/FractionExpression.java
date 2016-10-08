package edu.cmu.old_pact.cmu.sm;

//Fractions
//Fractions are ratios of any numbers (including floats)
//they can be unsimplified

import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class FractionExpression extends NumericExpression {
	private NumberExpression top;
	private NumberExpression bottom;
	
	public FractionExpression(NumberExpression numerator,NumberExpression denominator) {
		top = numerator;
		bottom = denominator;
	}
			
	public FractionExpression(int numerator,int denominator) {
		top = new NumberExpression(numerator);
		bottom = new NumberExpression(denominator);
	}
	
	public FractionExpression(long numerator,long denominator) {
		top = new NumberExpression(numerator);
		bottom = new NumberExpression(denominator);
	}
	
	public FractionExpression(double numerator,double denominator) {
		top = new NumberExpression(numerator);
		bottom = new NumberExpression(denominator);
	}

	public static long gcf (long one, long two) {
		long temp,x1,x2;
	
		if (one == 0.0 || two == 0.0)
			return (long)0.0;
		else {
			x1=Math.abs(one);
			x2=Math.abs(two);
			while (x2>0) {
				temp=x1%x2;
				x1=x2;
				x2=temp;
			}
			return x1;
		}
	}
	
	public static long gcf (long[] nums) {
		long current = nums[0];
		for (int i=1;i<nums.length;++i)
			current = gcf(current,nums[i]);
		return current;
	}
	
	public static long lcm (long one, long two) {
		long mult = one * two;
		long fact = gcf(one,two);
		return (mult/fact);
	}
	
	public Expression reciprocal() {
		if (top.isOne())
			return bottom;
		else
			return new FractionExpression(bottom,top).adjustNegative();
	}
	
	public Expression negate() {
		NumberExpression newTop = top;
		NumberExpression newBottom = bottom;
		if (top.isNegative())
			newTop = (NumberExpression)(top.negate());
		else if (bottom.isNegative()) //if positive/negative, turn into positive/positive
			newBottom = (NumberExpression)(bottom.negate());
		//positive/positive ==> negative/positive
		else if(top.isZero()){
			/*negating zero gives us a NegatedExpression, because we
              can't represent -0 as just a Number.  But since a
              NegatedExpression isn't a NumberExpression, we have to
              convert to a RatioExpression here.*/
			return new RatioExpression(top.negate(),bottom);
		}
		else{
			newTop = (NumberExpression)(top.negate());
		}
		return new FractionExpression(newTop,newBottom);
	}
	
	public boolean isIntegerType() {
		return false;
	}
	
	public boolean isIntegerFraction() {
		return (top.isIntegerType() && bottom.isIntegerType());
	}
	
	public boolean isFloatType() {
		return false;
	}

	public boolean isFractionType() {
		return true;
	}
	
	public double doubleValue() {
		return (top.numDivide(bottom).doubleValue());
	}

        public NumericExpression numericSimplifiedCoefficient(){
            return this;
        }

	public Expression simplifiedCoefficient() {
		return this;
	}

	public boolean exactEqual(Expression ex){
		if(ex instanceof FractionExpression){
			return top.exactEqual(((FractionExpression)ex).numerator()) &&
				bottom.exactEqual(((FractionExpression)ex).denominator());
		}
		else{
			return false;
		}
	}

	public Number getValue() {
		return (new Double(top.numDivide(bottom).doubleValue()));
	}
	
	protected Expression cannonicalizeWhole() {
		return new NumberExpression(top.numDivide(bottom).doubleValue());
	}
		
	public Expression exceptSimplifiedCoefficient() {
		return null;
	}

	public Expression exceptNumericSimplifiedCoefficient(){
		return null;
	}

	//adjustNegatives makes sure that we don't end up with fractions where both the numerator and denominator
	//are negative or where just the denominator is negative
	public FractionExpression adjustNegative() {
		FractionExpression result; 
		long topval = top.getValue().longValue();
		long bottomval = bottom.getValue().longValue();

		if (bottomval < 0 && topval > 0) {
			result = new FractionExpression(-topval,-bottomval);
		}
		else if (bottomval < 0 && topval < 0) {
			result = new FractionExpression(-topval,-bottomval);
		}
		else
			result = this;
		return result;
	}
	
	public Expression reduceFractionsWhole() {
		Expression result=this;
		//System.out.println("FE.rFW: " + debugForm());
		if (bottom.isZero())
			throw new DivideByZeroException(this,"reducing fractions");
		if (top.isIntegerType() && bottom.isIntegerType()) {
			long topval = top.getValue().longValue();
			long bottomval = bottom.getValue().longValue();
			if (bottomval == 1)
				result = top;
			else if (bottomval == -1)
				result = top.negate();
			else if (topval == 0)
				result = top;
			else {
				long gcf = gcf(topval,bottomval);
 				if (gcf != 0 && gcf != 1) {
					long topred = topval/gcf;
					long bottomred = bottomval/gcf;
					if (bottomred == 1)
						result = new NumberExpression(topred);
					else if (bottomred == -1)
						result = new NumberExpression(-topred);
					else
						result = new FractionExpression(topred,bottomred);
				}
				if (result == this) { //no reduction done yet, see if negatives cancel
					result = this;
				}
			}
			if(result instanceof FractionExpression){
				result = ((FractionExpression)result).adjustNegative();
			}
		}
		else
			result = top.numDivide(bottom);
		return result;
	}
	
	//cheat on this one...
	public boolean canReduceFractionsWhole() {
		return !(debugForm().equals(reduceFractions().debugForm()));
	}

	public Expression fractionToDecimalWhole() {
		return new NumberExpression(doubleValue());
	}

	public int complexity(){
		return top.complexity() + bottom.complexity();
	}
	
	public String toASCII(String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();

		StringBuffer finalString = new StringBuffer(asciiSBsize);
		finalString.append(top.toASCII(openParen,closeParen)).append("/").append(bottom.toASCII(openParen,closeParen));
		return finalString.toString();
	}
	
	public String toMathML() {
		return addMathMLAttributes((new StringBuffer(mathmlSBsize)).append("<mfrac>").append(top.toMathML()).append(" ").append(bottom.toMathML()).append("</mfrac>").toString());
	}
	
	public String debugForm() {
		return "[Fraction: "+top.debugForm()+" :: "+bottom.debugForm()+"]";
	}
	
	public Expression numerator() {
		return top;
	}
	
	public Expression denominator() {
		return bottom;
	}

	protected boolean isLike(Expression ex){
		if(ex instanceof RatioExpression){
			return ex.isLike(this);
		}
		else{
			return super.isLike(ex);
		}
	}

	//If Ratios are added to multiplied with ratios, keep everything in fractions...
	protected Expression addLikeTerms(Expression ex) {
		if ((ex instanceof FractionExpression) &&
			isIntegerFraction() &&
			((FractionExpression)ex).isIntegerFraction()) {
			FractionExpression otherRatio = (FractionExpression)ex;
			long thisDen = bottom.getValue().intValue();
			long thisNum = top.getValue().intValue();
			long otherDen = otherRatio.bottom.getValue().intValue();
			long otherNum = otherRatio.top.getValue().intValue();
			long newDen = lcm(thisDen,otherDen);
			long thisConvert = thisNum*newDen/thisDen;
			long otherConvert = otherNum*newDen/otherDen;
			if(thisConvert+otherConvert == 0){
				return new NumberExpression(0);
			}
			else{
				return new FractionExpression(thisConvert+otherConvert,newDen);
			}
		}
		else if (isIntegerFraction() &&
				 ex instanceof NumericExpression &&
				 ((NumericExpression)ex).isIntegerType()) {
			NumericExpression otherNum = (NumericExpression)ex;
			long thisDen = bottom.getValue().intValue();
			long totalNum = top.getValue().intValue() + otherNum.getValue().intValue()*thisDen;
			if(totalNum == 0){
				return new NumberExpression(0);
			}
			else{
				return new FractionExpression(totalNum,thisDen);
			}
		}
		else if (ex instanceof NumericExpression) {
			NumericExpression nex = (NumericExpression) ex;
			return this.numAdd(nex);
		}
		else if(ex instanceof FencedExpression){
			return ex.addLikeTerms(this);
		}
		else if(ex instanceof RatioExpression){
			//hack to let the code in ratioexpression.java handle this
			RatioExpression thisre = new RatioExpression(numerator(),denominator());
			return thisre.addLikeTerms(ex);
		}
		else
			throw new IllegalArgumentException("addLikeTerms (RatioExpession) called on uncombinable objects");
	}
	
	protected Expression iMultiply(Expression ex) {
		if ((ex instanceof FractionExpression) &&
			isIntegerFraction() &&
			((FractionExpression)ex).isIntegerFraction()) {
			FractionExpression otherRatio = (FractionExpression)ex;
			long thisDen = bottom.getValue().intValue();
			long thisNum = top.getValue().intValue();
			long otherDen = otherRatio.bottom.getValue().intValue();
			long otherNum = otherRatio.top.getValue().intValue();
			return new FractionExpression(thisNum*otherNum,thisDen*otherDen);
		}
		else if (ex instanceof NumericExpression) {
			NumericExpression nex = (NumericExpression) ex;
			return this.numMultiply(nex);
		}
		else
			throw new IllegalArgumentException("iMultiply (RatioExpession) called on uncombinable objects");
	}
	
	//specialize this for FractionExpression, since, most of the time, we want 2/3 to be a/b
	//An exception is when the numerator is 1. In that case, we use 1/a
	
/////
///// Nah, fractions are just treated as constants. I don't know what I was thinking here. I'm leaving the
///// code, just in case this starts to make sense to me someday

/*	public Expression getBindings(Equation bind) {
		Expression result;
		if (top.isOne()) {
			String letter = bind.getNextLetter();
			Number thisVal = bottom.getValue();
			bind.addBinding(thisVal,letter);
			result = new TermExpression(new NumberExpression(1),new VariableExpression(letter).reciprocal());
		}
		else {
			String topLetter = bind.getNextLetter();
			Number topVal = bottom.getValue();
			bind.addBinding(topVal,topLetter);
			//need to addBinding before getting next letter
			String bottomLetter = bind.getNextLetter();
			Number bottomVal = bottom.getValue();
			bind.addBinding(bottomVal,bottomLetter);
			result = new TermExpression(new VariableExpression(topLetter),new VariableExpression(bottomLetter).reciprocal());
		}
		return result;
	}
*/	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("numerator"))
			return top;
		else if (prop.equalsIgnoreCase("denominator"))
			return bottom;
		else if (prop.equalsIgnoreCase("isNotDecimal"))
			return new BooleanQuery(isIntegerFraction());
		else if (prop.equalsIgnoreCase("isFraction"))
			return new BooleanQuery(true);
		else
			return super.getProperty(prop);
	}

}
