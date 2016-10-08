package edu.cmu.old_pact.cmu.sm;

//ExponentExpressions
//ExponentExpressions contain a body and an exponent
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

//ExponentExpression is a combination of a body (which can be any type of expression)
//and an exponent
public class ExponentExpression extends Expression implements CompoundExpression {
	protected Expression body=null;
	protected Expression exponent=null;
	
	public ExponentExpression(Expression ex,int exp) {
		body = ex;
		exponent = new NumberExpression(exp);
	}
	
	public ExponentExpression(Expression ex,Expression expo) {
		body = ex;
		exponent = expo;
		if(SymbolManipulator.canSimplifyExponentPower && exponent.canCombineLikeTerms())
			exponent = exponent.combineLikeTermsWhole();
			
	}
	
	//for an ExponentExpression, we know that the first component is the base and the second is the exponent
	protected Expression buildFromComponents(Vector components) {
		Expression base = (Expression)(components.elementAt(0));
		Expression exp = (Expression)(components.elementAt(1));
		return new ExponentExpression(base,exp);
	}
	
	protected Expression buildFromComponents(ExpressionArray components) {
		return new ExponentExpression(components.expressionAt(0),components.expressionAt(1));
	}
	
	public Vector getComponents() {
		Vector comps = new Vector();
		comps.addElement(body);
		comps.addElement(exponent);
		return comps;
	}
	
	public ExpressionArray getComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate();
		compArray.addExpression(body);
		compArray.addExpression(exponent);
		return compArray;
	}
	
	public Expression getExponent() {
		return exponent;
	}
	
	public Expression getBody() {
		return body;
	}
	
	public Expression reciprocal() {
		return new ExponentExpression(body,exponent.negate());
	}
	
	public Expression exceptExponent() {
		return body;
	}

	public Expression exceptSimplifiedCoefficient() {
		return this;
	}
	
	protected Vector getExpandedForm() {
		Vector result = new Vector();
		if (exponent instanceof NumericExpression) {
			NumericExpression numEx = (NumericExpression)exponent;
			if (numEx.isIntegerType() &&
				!numEx.isNegative()) {
				for (int i=0;i<numEx.getValue().intValue();++i)
					result.addElement(body);
			}
			else if (!numEx.isNegative()) { //y^2.5-->y,y,y^.5
				double expNum = numEx.doubleValue();
				int intPart = (int)expNum;
				double decPart = expNum-intPart;
				for (int i=0;i<intPart;++i)
					result.addElement(body);
				result.addElement(new ExponentExpression(body,new NumberExpression(decPart)));
			}
			else
				result.addElement(this);
		}
		else
			result.addElement(this);
		return result;
	}				
	
	public boolean canExpandExponentWhole() {
		if (exponent.isOne())
			return true;
		else if (body instanceof ExponentExpression)
			return true;
		else if (exponent instanceof NumericExpression && 
				((NumericExpression)exponent).isNegative() &&
				body instanceof CompoundExpression)
			return false;
		else if (body instanceof TermExpression)
		   return true;
		else if (body instanceof RatioExpression)
		   return true;
		else if (body instanceof PolyExpression &&
				exponent instanceof NumericExpression &&
				((NumericExpression)exponent).isIntegerType() &&
				!exponent.isNegative())
			return true;
		else if (body instanceof NumericExpression &&
				exponent instanceof NumericExpression)
		    return true;
		else if (exponent.isZero()) //hmm -- is this really "expandExponent" or is it something else?
			return true;
		else
			return false;
	}
/*	
	public Expression expandExponentWhole() {
		Expression unfencedBody = body.unfence(); //expandExponent ignores fences around body
		if (exponent.isZero())
			return new NumberExpression(1);
		else if (exponent.isOne())
			return body;
		else if (unfencedBody instanceof ExponentExpression) {
			//if the body is an ExponentExpression, multiply the exponents
			return new ExponentExpression(unfencedBody.exceptExponent(),unfencedBody.getExponent().multiply(exponent));
		}
//		else if (exponent instanceof NumericExpression && //for negative exponents, transform to 1/body
//				((NumericExpression)exponent).isNegative() &&
//				body instanceof CompoundExpression) {
//			Expression posValue = exponent.negate();
//			Expression expBody = new ExponentExpression(body,posValue).expandExponentWhole();
//			return new RatioExpression(new NumberExpression(1),expBody).cleanExpression();
//		}
		else if (unfencedBody instanceof TermExpression) {
			//if the body is a term, distribute the exponent over each of the subterms
			TermExpression tBody = (TermExpression)unfencedBody;
			Vector outterms = new Vector();
			for (int i=0;i<tBody.numSubTerms();++i) {
				outterms.addElement(tBody.getTerm(i).power(exponent));
			}
			return new TermExpression(outterms);
		}
		else if (unfencedBody instanceof RatioExpression) {
			//if the body is a Ratio, distribute the exponent over numerator and denominator
			RatioExpression rBody = (RatioExpression)unfencedBody;
			Expression newNum = rBody.numerator().power(exponent);
			Expression newDen = rBody.denominator().power(exponent);
			return new RatioExpression(newNum,newDen);
		}
		else if (unfencedBody instanceof PolyExpression &&
				exponent.isNegOne())
			return unfencedBody.reciprocal();
		else if (unfencedBody instanceof PolyExpression &&
				exponent instanceof NumericExpression &&
				((NumericExpression)exponent).isIntegerType()) {
			//if the body is a polynomial and the exponent is a positive integer, expand the polynomial
			PolyExpression pBody = (PolyExpression)unfencedBody;
			Vector outterms = new Vector();
			int exponInt = ((NumericExpression)exponent).getValue().intValue();
			Vector currentTerms = pBody.getFullComponents();
			for (int exp=0;exp<Math.abs(exponInt)-1;++exp) {
				outterms.removeAllElements();
				for (int i=0;i<pBody.numberOfTerms();++i) {
					for (int j=0;j<currentTerms.size();++j) {
						Expression newTerm = pBody.getTermAt(i).multiply((Expression)(currentTerms.elementAt(j)));
						//need to turn x*3 into 3x
						newTerm = newTerm.sort();
						//need to turn x*x into x^2
						//multiplyThrough("x*x") --> "x^(1+1)", so we CLT on that
						if (newTerm instanceof TermExpression) {
							TermExpression tEx = (TermExpression)newTerm;
							if (!(tEx.getTerm(0).exceptExponent() instanceof NumericExpression) &&
								tEx.getTerm(0).exceptExponent().exactEqual(tEx.getTerm(1).exceptExponent())) {
								Expression newExp = ((ExponentExpression)(tEx.multiplyThrough())).getExponent().combineLikeTerms();
								newTerm = new ExponentExpression(tEx.getTerm(0).exceptExponent(),newExp);
							}
						}
						outterms.addElement(newTerm);
					}
				}
				currentTerms = (Vector)(outterms.clone());
			}
			Expression newEx = new PolyExpression(outterms);
			if (exponInt < 0)
				newEx = newEx.reciprocal();
			return newEx;
		}
		else if (unfencedBody instanceof NumberExpression &&
				exponent instanceof NumericExpression) {
			//if the body is a number and the exponent is a number, do the power
			NumberExpression numBody = (NumberExpression)unfencedBody;
			double expNum = ((NumericExpression)exponent).doubleValue();
			if (numBody.isIntegerType() && expNum < 0) //for something like 4^-2, return 1/16
				return new FractionExpression(new NumberExpression(1),
										 	new NumberExpression(Math.pow(numBody.getValue().doubleValue(),Math.abs(expNum))));
			else
				return new NumberExpression(Math.pow(numBody.getValue().doubleValue(),expNum));
		}
		else if (unfencedBody instanceof NumericExpression) { //body must be a fraction...
			NumericExpression numBody = (NumericExpression)unfencedBody;
			NumericExpression numer = (NumericExpression)(numBody.numerator());
			NumericExpression denom = (NumericExpression)(numBody.denominator());
			double numNumber = numer.getValue().doubleValue();
			double denNumber = denom.getValue().doubleValue();
			if (exponent instanceof NumericExpression) {
				double expNum = ((NumericExpression)exponent).doubleValue();
				return new FractionExpression(Math.pow(numNumber,expNum),Math.pow(denNumber,expNum));
			}
			else {
				Expression num = new NumberExpression(numNumber).power(exponent);
				Expression den = new NumberExpression(denNumber).power(exponent);
				return num.divide(den);
			}
		}
		else 
			return this;
	}
*/	
// Olga
	public Expression expandExponentWhole() {
		Expression toret = this;
		Expression unfencedBody = body.unfence(); //expandExponent ignores fences around body
		Expression unfencedExp = exponent.unfence();
		if (exponent.isZero())
			toret = new NumberExpression(1);
		else if (exponent.isOne())
			toret = body;
		else if (unfencedBody instanceof ExponentExpression) {
			//if the body is an ExponentExpression, multiply the exponents
//			toret = new ExponentExpression(unfencedBody.exceptExponent(),unfencedBody.getExponent().multiply(exponent));
			Expression expo = (unfencedBody.getExponent().unfence()).multiply(unfencedExp).multiplyThrough().reduceFractions();
			toret = new ExponentExpression(unfencedBody.exceptExponent(),expo);
		}
//		else if (exponent instanceof NumericExpression && //for negative exponents, transform to 1/body
//				((NumericExpression)exponent).isNegative() &&
//				body instanceof CompoundExpression) {
//			Expression posValue = exponent.negate();
//			Expression expBody = new ExponentExpression(body,posValue).expandExponentWhole();
//			return new RatioExpression(new NumberExpression(1),expBody).cleanExpression();
//		}
		else if (unfencedBody instanceof TermExpression) {
			//if the body is a term, distribute the exponent over each of the subterms
			TermExpression tBody = (TermExpression)unfencedBody;
			Vector outterms = new Vector();
			for (int i=0;i<tBody.numSubTerms();++i) {
				outterms.addElement(tBody.getTerm(i).power(unfencedExp));
			}
			toret = new TermExpression(outterms);
			outterms.removeAllElements();
			outterms = null;
		}
		else if (unfencedBody instanceof RatioExpression) {
			//if the body is a Ratio, distribute the exponent over numerator and denominator
			RatioExpression rBody = (RatioExpression)unfencedBody;
			Expression newNum = rBody.numerator().power(unfencedExp);
			Expression newDen = rBody.denominator().power(unfencedExp);
			toret = new RatioExpression(newNum,newDen);
		}
		else if (unfencedBody instanceof PolyExpression &&
				unfencedExp.isNegOne())
			toret = unfencedBody.reciprocal();
		else if (unfencedBody instanceof PolyExpression &&
				unfencedExp instanceof NumericExpression &&
				((NumericExpression)unfencedExp).isIntegerType()) {
			//if the body is a polynomial and the exponent is a positive integer, expand the polynomial
			PolyExpression pBody = (PolyExpression)unfencedBody;
			Vector outterms = new Vector();
			int exponInt = ((NumericExpression)unfencedExp).getValue().intValue();
			Vector currentTerms = pBody.getFullComponents();
			for (int exp=0;exp<Math.abs(exponInt)-1;++exp) {
				outterms.removeAllElements();
				for (int i=0;i<pBody.numberOfTerms();++i) {
					for (int j=0;j<currentTerms.size();++j) {
						Expression newTerm = pBody.getTermAt(i).multiply((Expression)(currentTerms.elementAt(j)));
						//need to turn x*3 into 3x
						newTerm = newTerm.sort();
						//need to turn x*x into x^2
						//multiplyThrough("x*x") --> "x^(1+1)", so we CLT on that
						if (newTerm instanceof TermExpression) {
							TermExpression tEx = (TermExpression)newTerm;
							if (!(tEx.getTerm(0).exceptExponent() instanceof NumericExpression) &&
								tEx.getTerm(0).exceptExponent().exactEqual(tEx.getTerm(1).exceptExponent())) {
								Expression newExp = ((ExponentExpression)(tEx.multiplyThrough())).getExponent().combineLikeTerms();
								newTerm = new ExponentExpression(tEx.getTerm(0).exceptExponent(),newExp);
							}
						}
						outterms.addElement(newTerm);
					}
				}
				currentTerms = (Vector)(outterms.clone());
			}
			Expression newEx = new PolyExpression(outterms);
			if (exponInt < 0)
				newEx = newEx.reciprocal();
			toret = newEx;
			outterms.removeAllElements();
			outterms = null;
			currentTerms.removeAllElements();
			currentTerms = null;
		}
		else if (unfencedBody instanceof NumberExpression &&
				unfencedExp instanceof NumericExpression) {
			//if the body is a number and the exponent is a number, do the power
			NumberExpression numBody = (NumberExpression)unfencedBody;
			double expNum = ((NumericExpression)unfencedExp).doubleValue();
			if (numBody.isIntegerType() && expNum < 0) //for something like 4^-2, return 1/16
				toret = new FractionExpression(new NumberExpression(1),
										 	new NumberExpression(Math.pow(numBody.getValue().doubleValue(),Math.abs(expNum))));
			else{
				//for some reason Math.pow() isn't throwing this when it should ...
				if((numBody.getValue().doubleValue() < 0) &&
				   (Math.round(expNum) != expNum)){
					throw new NegativeRootException(numBody,new NumberExpression(expNum),
													"can't raise a negative number (" +
													numBody.getValue().doubleValue() + 
													") to a non-integer power (" + expNum + ")");
				}
				else{
					toret = new NumberExpression(Math.pow(numBody.getValue().doubleValue(),expNum));
				}
			}
		}
		else if (unfencedBody instanceof NumericExpression) { //body must be a fraction...
			NumericExpression numBody = (NumericExpression)unfencedBody;
			NumericExpression numer = (NumericExpression)(numBody.numerator());
			NumericExpression denom = (NumericExpression)(numBody.denominator());
			double numNumber = numer.getValue().doubleValue();
			double denNumber = denom.getValue().doubleValue();
			if (unfencedExp instanceof NumericExpression) {
				double expNum = ((NumericExpression)unfencedExp).doubleValue();
				toret = new FractionExpression(Math.pow(numNumber,expNum),Math.pow(denNumber,expNum));
			}
			else {
				Expression num = new NumberExpression(numNumber).power(unfencedExp);
				Expression den = new NumberExpression(denNumber).power(unfencedExp);
				toret = num.divide(den);
			}
		}
		if(toret.canExpandExponent())
			toret = toret.expandExponent();
		return toret;
	}
// end Olga	

	public Expression eliminateExponentWhole(){
		//System.out.println("EE.eE: " + debugForm());
		if(getExponent() instanceof NumericExpression &&
		   ((NumericExpression)getExponent()).isIntegerType()){
			Expression ret = null;
			int expvalue = ((NumericExpression)getExponent()).getValue().intValue();
			boolean recip = false;
			if(expvalue == 0){
				ret = new NumberExpression(1);
			}
			else{
				if(expvalue < 0){
					recip = true;
					expvalue *= -1;
				}
				ret = getBody();
				for(int i=1;i<expvalue;i++){
					ret = ret.multiply(getBody());
				}
				if(recip){
					ret = ret.reciprocal();
				}
			}
			//System.out.println("EE.eE: returning: " + ret.debugForm());
			return ret;
		}
		else{
			//System.out.println("EE.eE: returning this");
			return this;
		}
	}

	public boolean canEliminateExponentWhole(){
		return (getExponent() instanceof NumericExpression) &&
			(((NumericExpression)getExponent()).isIntegerType());
	}

	//since both (2x)^-2 and 1/4*x^-2 are standard forms, we need to pick one to cannonicalize
	//We pick the second
	public Expression initialCannonicalize() {
		if (exponent instanceof NumericExpression && //for negative exponents, transform to 1/body
			((NumericExpression)exponent).isNegative() &&
			body instanceof TermExpression) {
			TermExpression tBody = (TermExpression)body;
			Vector outterms = new Vector();
			for (int i=0;i<tBody.numSubTerms();++i) {
				outterms.addElement(tBody.getTerm(i).power(exponent));
			}
			Expression ret = new TermExpression(outterms);
			outterms.removeAllElements();
			outterms = null;
			return ret;
		}
		else
			return this;
	}	

		
	//we can two ExponentExpressions are like terms if they have the same exponent and their bodies
	//are equal
	public boolean isLike(Expression ex) {
		//If the other thing is an ExponentExpression, exponents are the same and the bodies are the same, the terms can be combined
		if (ex instanceof ExponentExpression) {
			ExponentExpression et = (ExponentExpression)ex;
			if (exponent.algebraicEqual(et.getExponent()) &&
				body.algebraicEqual(et.getBody()))
				return true;
			else
				return false;
		}
		/*we can also combine with an ExponentExpression multiplied by a coefficient*/
		else if(ex instanceof TermExpression){
			/*Expression exBody = ex.exceptNumericSimplifiedCoefficient();
			  if(isLike(exBody)){
			  return true;
			  }
			  else{
			  return false;
			  }*/
			return ex.isLike(this);
		}
		else if(ex instanceof FencedExpression){
			return ex.isLike(this);
		}
		else
			return false;
	}
	
	public boolean exactEqual(Expression ex) {
		if (ex instanceof ExponentExpression) {
			if (getExponent().exactEqual(ex.getExponent()) &&
				exceptExponent().exactEqual(ex.exceptExponent()))
				return true;
			else
				return false;
		}
		else
			return false;
	}
	
	protected Expression addLikeTerms(Expression ex) {
		//combining with another ExponentExpression
		//  since bodies are the same, just add a 2 in the termExpression
		if (ex instanceof ExponentExpression) {
			NumberExpression coeff = new NumberExpression(2);
			TermExpression combo = new TermExpression(coeff,this);
			return combo;
		}
		else if(ex instanceof TermExpression){
			/*NumericExpression otherCoeff = ex.numericSimplifiedCoefficient();
			  
			  NumericExpression newCoeff = otherCoeff.numAdd(new NumberExpression(1));
			  return new TermExpression(newCoeff,this);*/
			return ex.addLikeTerms(this);
		}
		else if(ex instanceof FencedExpression){
			return ex.addLikeTerms(this);
		}
		else
			throw new IllegalArgumentException("addLikeTerms (ExponentExpression) called on uncombinable objects: "+debugForm()+" and "+ex.debugForm());
	}
	
	//ExponentExpressions sort before ExponentExpressions with smaller exponents (or smaller bodies)
	//and after compound expressions
	public boolean termSortBefore(Expression ex) {
		if (ex instanceof ExponentExpression) {
			ExponentExpression eEx = (ExponentExpression)ex;
			if (getExponent().termSortBefore(eEx.getExponent()))
				return true;
			else if (getExponent().exactEqual(eEx.getExponent()))
				return exceptExponent().termSortBefore(eEx.exceptExponent());
			else
				return false;
		}
		else if (ex instanceof PolyExpression)
			return true;
		else if (ex instanceof VariableExpression)
			return true;
		else
			return false;
	}
	
//	public Expression substitute(String var,Expression newVal) {
//		return new ExponentExpression(getBody().substitute(var,newVal),getExponent());
//	}
	
	/*mmmBUG: this throws a ClassCastException when called on "a^x".
	  in theory this should do the same thing as getExponent() ... ?*/
	public double degree() {
		NumericExpression expCo = (NumericExpression)(exponent.simplifiedCoefficient()); //for constants, coefficient is number itself
		return expCo.getValue().doubleValue();
	}
	
//	public Vector variablesUsed() {
//		return body.variablesUsed();
//	}
		
	public String toASCII(String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();

		StringBuffer finalString = new StringBuffer(asciiSBsize);
		if (body instanceof ExponentExpression ||
			body instanceof TermExpression ||
			body instanceof PolyExpression ||
			body instanceof FractionExpression ||
			body instanceof RatioExpression)
			finalString.append(openParen).append(body.toASCII(openParen,closeParen)).append(closeParen);
		else
			finalString.append(body.toASCII(openParen,closeParen));
		if (!exponent.isOne()) {
			if (exponent instanceof ExponentExpression ||
				exponent instanceof TermExpression ||
				exponent instanceof PolyExpression ||
				exponent instanceof FractionExpression ||
				exponent instanceof RatioExpression)
				finalString.append("^").append(openParen+exponent.toASCII(openParen,closeParen)).append(closeParen);
			else
				finalString.append("^").append(exponent.toASCII(openParen,closeParen));
		}

		return finalString.toString();
	}
	
	public String toMathML() {
		StringBuffer bodyPart = new StringBuffer(mathmlSBsize);
		if (body instanceof PolyExpression)
			bodyPart.append("<mfenced>").append(body.toMathML()).append("</mfenced>");
		else
			bodyPart.append(body.toMathML());
		return addMathMLAttributes(bodyPart.insert(0,"<msup>").append(" ").append(exponent.toMathML()).append("</msup>").toString());
	}
	
	public String debugForm() {
		return "[ExponentExpression: "+body.debugForm()+" :: "+ exponent.debugForm() +"]";
	}
	
	//specialize this for ExponentExpression, since, most of the time when the expononent is a number, we don't want to variablize exponents
	//That is, the pattern in 3x^2+4=5 should be ax^2+b=c, not ax^b+c=d
	
	public Expression getBindings(Equation bind) {
		// For now: for 33^2 returns ConstantExpression a;
		if(body instanceof NumberExpression && exponent instanceof NumberExpression){
			double thisVal = ((NumericExpression)body).getValue().doubleValue();
			double thisExp = ((NumericExpression)exponent).getValue().doubleValue();
			thisVal = Math.pow(thisVal, thisExp);
			Expression exp = new NumberExpression(thisVal);
			return exp.getBindings(bind);
		}
		if (body instanceof VariableExpression) {
			String letter = bind.getPatternVariable(((VariableExpression)body).getString());
			body = new VariableExpression(letter);
		}
			
		Expression bodyBind = body.getBindings(bind);
		Expression expBind=null;
		if (exponent instanceof NumberExpression)
			expBind = (NumberExpression)exponent;
		else
			expBind = exponent.getBindings(bind);
		return new ExponentExpression(body,expBind);
	}
	
	//For now: for 33^2 returns ConstantExpression;
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("constant terms") &&
			body instanceof NumberExpression && 
			exponent instanceof NumberExpression) {
			double thisVal = ((NumericExpression)body).getValue().doubleValue();
			double thisExp = ((NumericExpression)exponent).getValue().doubleValue();
			thisVal = Math.pow(thisVal, thisExp);
			return new ArrayQuery(new NumberExpression(thisVal));
		}
		else if(prop.equalsIgnoreCase("base")){
			return body;
		}
		else if(prop.equalsIgnoreCase("exponent")){
			return exponent;
		}
		else if(prop.equalsIgnoreCase("isExponent")){
			return new BooleanQuery(true);
		}
		else
			return super.getProperty(prop);
	}
	
}

