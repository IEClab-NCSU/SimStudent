package edu.cmu.old_pact.cmu.sm;

//RatioExpression
//RatioExpression are ratios of any expressions, EXCEPT ratios of
//one number to another (those are FractionExpressions).
//
//RatioExpressions are mathematically like TermExpressions, but they print differently
//X*1/3 is a TermExpression with 2 terms. X/3 is an algebraically equal RatioExpression

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

public class RatioExpression extends Expression implements CompoundExpression {
	private Expression top;
	private Expression bottom;
	
	public RatioExpression(Expression numerator,Expression denominator) {
		if(numerator == null){
			trace.out("RE: created with null numerator!");
			(new Exception()).printStackTrace();
		}
		top = numerator;
		bottom = denominator;
	}
	
	//This constructor builds TermExpression(s) out of the given Vectors
	//[unless the Vector has 0 or 1 elements]
	public RatioExpression(Vector numerator,Vector denominator) {
		if (numerator.size() == 0)
			top = new NumberExpression(1);
		else if (numerator.size() == 1)
			top = (Expression)(numerator.elementAt(0));
		else
			top = new TermExpression(numerator);
		if (denominator.size() == 0)
			bottom = new NumberExpression(1);
		else if (denominator.size() == 1)
			bottom = (Expression)(denominator.elementAt(0));
		else
			bottom = new TermExpression(denominator);
		if(top == null){
			trace.out("RE: created with null numerator!");
			(new Exception()).printStackTrace();
		}
	}

	
	//Since RatioExpressions can't include just numerics, we use cleanExpression
	//to take care of this case	
	public Expression cleanExpression() {
		if (top instanceof NumberExpression && bottom instanceof NumberExpression)
			return new FractionExpression((NumberExpression)top,(NumberExpression)bottom);
/*
else if (top instanceof TermExpression || bottom instanceof TermExpression)	{
TermExpression texp = new TermExpression(top, bottom);
return texp.cleanExpression();
}
*/
		else
			return this;
	}
	
	/*this is used by the parser to get rid of instances of
      FencedExpressions where the parens really are mathematically
      necessary*/
	public Expression removeRedundantFencesWhole(){
		RatioExpression unfenced = new RatioExpression(top,bottom);
		/*if the top or bottom is a FencedExpression, unfence it and
          see if the parens still pop out of toASCII().  If they do,
          then they're mathematically necessary and we can stick with
          the modified version of the subterm.*/
		if(top instanceof FencedExpression){
			String oldExp = unfenced.toASCII("(",")");
			/*unfence it*/
			unfenced = new RatioExpression(top.unfence(),bottom);
			/*check if we've broken the parenthesization*/
			if(!oldExp.equals(unfenced.toASCII("(",")"))){
				/*we did; put it back in the fences*/
				unfenced = new RatioExpression(top,bottom);
			}
		}
		if(bottom instanceof FencedExpression){
			String oldExp = unfenced.toASCII("(",")");
			/*unfence it*/
			unfenced = new RatioExpression(unfenced.numerator(),bottom.unfence());
			/*check if we've broken the parenthesization*/
			if(!oldExp.equals(unfenced.toASCII("(",")"))){
				/*we did; put it back in the fences*/
				unfenced = new RatioExpression(unfenced.numerator(),bottom);
			}
		}

		return unfenced;
	}

	protected Vector getComponents() {
		Vector vec = new Vector();
		vec.addElement(top);
		vec.addElement(bottom);
		return vec;
	}
	
	public ExpressionArray getComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate();
		compArray.addExpression(top);
		compArray.addExpression(bottom);
		return compArray;
	}

	
	protected Expression buildFromComponents(Vector comp) {
		Expression num = (Expression)(comp.elementAt(0));
		Expression den = (Expression)(comp.elementAt(1));
		return new RatioExpression(num,den);
	}
	
	protected Expression buildFromComponents(ExpressionArray comp) {
		Expression num = comp.expressionAt(0);
		Expression den = comp.expressionAt(1);
		return new RatioExpression(num,den);
	}

	public boolean exactEqual(Expression other) {
		if (other instanceof RatioExpression) {
			RatioExpression otherR = (RatioExpression)other;
			return top.exactEqual(otherR.numerator()) &&
				   bottom.exactEqual(otherR.denominator());
		}
		else
			return false;
	}

	public boolean isNegative(){
		return reduceFractions().numerator().isNegative();
	}

	public Expression negate() {
		Expression newNum = top.negate();
		return new RatioExpression(newNum,bottom);
	}
	
	//The degree of the ratio is equal to the degree of the numerator (seems right...)
	public double degree() {
		return top.degree();
	}
	
	public Expression reciprocal() {
		return new RatioExpression(bottom,top);
	}
	
	public Expression numerator() {
		return top;
	}
	
	public Expression denominator() {
		return bottom;
	}
	
	public boolean isLike(Expression other) {
		if (other instanceof RatioExpression)
			return bottom.exactEqual(((RatioExpression)other).denominator());
		else if(other instanceof FractionExpression){
			return bottom.exactEqual(((FractionExpression)other).denominator());
		}
		else if(other instanceof FencedExpression){
			return other.isLike(this);
		}
		else if(other instanceof TermExpression){
			return other.isLike(this);
		}
		else
			return false;
	}
	
	//for addLikeTerms, we know the other term is a RatioExpression,
	//so we just add the numerators
	protected Expression addLikeTerms(Expression other) {
		if(other instanceof RatioExpression){
			RatioExpression rOther = (RatioExpression)other;
			if(rOther.numerator().isNegative()){
				//mmmBUG should check that rOther.numerator() isn't something like x/-3
				return new RatioExpression(top.subtract(rOther.numerator().negate()),bottom);
			}
			else{
				return new RatioExpression(top.add(rOther.numerator()),bottom);
			}
		}
		else if(other instanceof FractionExpression){
			FractionExpression fOther = (FractionExpression)other;
			if(fOther.numerator().isNegative()){
				//mmmBUG should check that fOther.numerator() isn't something like 1/-3
				return new RatioExpression(top.subtract(fOther.numerator().negate()),bottom);
			}
			else{
				return new RatioExpression(top.add(fOther.numerator()),bottom);
			}
		}
		else if(other instanceof FencedExpression){
			return other.addLikeTerms(this);
		}
		else if(other instanceof TermExpression){
			return other.addLikeTerms(this);
		}
		else{
			throw new IllegalArgumentException("addLikeTerms (RatioExpression) called on uncombinable objects");
		}
	}
	
        public NumericExpression numericSimplifiedCoefficient(){
            NumericExpression topNum = top.numericSimplifiedCoefficient();
            NumericExpression bottomNum = bottom.numericSimplifiedCoefficient();
            if (bottomNum.isOne())
                return topNum;
            else if (topNum instanceof NumberExpression && bottomNum instanceof NumberExpression)
                return new FractionExpression((NumberExpression)topNum,(NumberExpression)bottomNum);
            else { //one or both of top and bottom coefficients are fractions
                NumericExpression total = (NumericExpression)(topNum.divide(bottomNum).simplify());
                return total;
            }
        }

	public Expression exceptNumericSimplifiedCoefficient(){
		Expression topEx = top.exceptNumericSimplifiedCoefficient();
		Expression botEx = bottom.exceptNumericSimplifiedCoefficient();
		if(botEx == null || botEx.isOne()){
			return topEx;
		}
		else{
			if(topEx == null){
				topEx = new NumberExpression(1);
			}
			return new RatioExpression(topEx,botEx);
		}
	}

        public NumericExpression numericUnsimplifiedCoefficient() {
		NumericExpression topNum = top.numericUnsimplifiedCoefficient();
		NumericExpression bottomNum = bottom.numericUnsimplifiedCoefficient();
		if (bottomNum.isOne())
			return topNum;
		else if (topNum instanceof NumberExpression && bottomNum instanceof NumberExpression)
			return new FractionExpression((NumberExpression)topNum,(NumberExpression)bottomNum);
		else { //one or both of top and bottom coefficients are fractions
			NumericExpression total = (NumericExpression)(topNum.divide(bottomNum).simplify());
			return total;
		}
	}

	public Expression exceptNumericUnsimplifiedCoefficient(){
		Expression topEx = top.exceptNumericSimplifiedCoefficient();
		Expression botEx = bottom.exceptNumericSimplifiedCoefficient();
		if(botEx == null || botEx.isOne()){
			return topEx;
		}
		else{
			if(topEx == null){
				topEx = new NumberExpression(1);
			}
			return new RatioExpression(topEx,botEx);
		}
	}

	public Expression simplifiedCoefficient() {
		Expression topNum = top.simplifiedCoefficient();
		Expression bottomNum = bottom.simplifiedCoefficient();
		if (bottomNum.isOne())
			return topNum;
		else if (topNum instanceof NumberExpression && bottomNum instanceof NumberExpression)
			return new FractionExpression((NumberExpression)topNum,(NumberExpression)bottomNum);
		else { //one or both of top and bottom coefficients are fractions
			Expression total = topNum.divide(bottomNum).simplify();
			return total;
		}
	}
	
	public Expression unsimplifiedCoefficient() {
		Expression topNum = top.unsimplifiedCoefficient();
		Expression bottomNum = bottom.unsimplifiedCoefficient();
		if (bottomNum.isOne())
			return topNum;
		else if (topNum instanceof NumberExpression && bottomNum instanceof NumberExpression)
			return new FractionExpression((NumberExpression)topNum,(NumberExpression)bottomNum);
		else { //one or both of top and bottom coefficients are fractions
                        Expression total = topNum.divide(bottomNum).simplify();
			return total;
		}
	}
	
	public Expression exceptSimplifiedCoefficient() {
		//trace.out("top is "+top+" and bottom is "+bottom);
		Expression topEx = top.exceptSimplifiedCoefficient();
		Expression bottomEx = bottom.exceptSimplifiedCoefficient();
		if (bottomEx == null && topEx == null) //if number/number, return null
			return null;
		if (bottomEx == null)
			bottomEx = new NumberExpression(1);
		if (topEx == null)
			topEx = new NumberExpression(1);
		if (bottomEx.isOne())
			return topEx;
		else
			return new RatioExpression(topEx,bottomEx);
	}
	
	public Expression exceptUnsimplifiedCoefficient() {
		Expression topEx = top.exceptUnsimplifiedCoefficient();
		Expression bottomEx = bottom.exceptUnsimplifiedCoefficient();
		if (bottomEx == null && topEx == null) //if number/number, return null
			return null;
		if (bottomEx == null)
			bottomEx = new NumberExpression(1);
		if (topEx == null)
			topEx = new NumberExpression(1);
		if (bottomEx.isOne())
			return topEx;
		else
			return new RatioExpression(topEx,bottomEx);
	}

	//toTermExpression allows us to re-use the TermExpression simplification routines
	//Basically, any simplification involves turning the Ratio into a Term and simplifying it
	private TermExpression toTermExpression() {
		//trace.out("RE.toTermExpression: " + debugForm());
		TermExpression ret = null;
		Vector topTerms;
		Vector bottomTerms;
//		Expression openTop = top.unfence();
//		Expression openBottom = bottom.unfence();
		Expression openTop = top;
		Expression openBottom = bottom;
		
		if (openTop instanceof TermExpression)
			topTerms = openTop.getFullComponents();
		else {
			topTerms = new Vector();
			topTerms.addElement(openTop);
		}
		if (openBottom instanceof TermExpression)
			bottomTerms = openBottom.getFullComponents();
		else {
			bottomTerms = new Vector();
			bottomTerms.addElement(openBottom);
		}
		/*trace.out("RE.toTermExpression: creating new TermExpression: {" + topTerms +
		  "}, {" + bottomTerms + "}");*/
		if(!TermExpression.wouldBecomeRatio(bottomTerms)){
			ret = new TermExpression(topTerms,bottomTerms);
		}

		topTerms.removeAllElements();
		topTerms = null;
		bottomTerms.removeAllElements();
		bottomTerms = null;

		return ret;
	}
	
	//cheat on this one (since reducing fractions for a term involves relationships across components)
	public boolean canReduceFractionsWhole() {
		//trace.out("RatioExpression: canReduceFractionsWhole(): " + debugForm());
		boolean ret = !(debugForm().equals(reduceFractions().debugForm()));
		//trace.out("RatioExpression: canReduceFractionsWhole(): " + ret);
		return ret;
	}
	
	//reducing fractions on a ratio expression involves lots of different sub-operations (which maybe should be split out
	//into individual simplification steps):
	// removeFractionalComponent: [4/6]*x / [2/6] --> [4/6]*x*6 / 2
	// cancelTerms: 3x/x --> 3
	// reduceTermExponents: [3x^2]/x --> 3x
	// reduceNumerics: 3/6 --> 1/2
	// reducePolyNumerics: [3(x+2)] / 3 --> x+2
	// factorAndReduce:  [3x+6] / 3 --> x+2
	
	public Expression reduceFractionsWhole() {
		//trace.out("RatioExpression: reduceFractionsWhole(): " + debugForm());
		Expression finalEx = removeFractionalComponent();
		if (finalEx instanceof RatioExpression) {
			finalEx = ((RatioExpression)finalEx).cancelTerms().cleanExpression();
			//trace.out("after cancelterms: "+finalEx);
			if (finalEx instanceof RatioExpression) {
				finalEx = ((RatioExpression)finalEx).reduceTermExponents();
				//trace.out("after reduceTermExponents: "+finalEx);
				if (finalEx instanceof RatioExpression) {
					finalEx = ((RatioExpression)finalEx).reduceNumerics();
					//trace.out("after reduceNumerics: "+finalEx);
					if (finalEx instanceof RatioExpression) {
						finalEx = ((RatioExpression)finalEx).reducePolyNumerics(); //shouldn't have to special-case this, but test 26 doesn't pass without it...
						//trace.out("after reducePolyNumerics: "+finalEx.debugForm());
						if (finalEx instanceof RatioExpression) {
							finalEx = ((RatioExpression)finalEx).factorAndReduce();
							//trace.out("after factorAndReduce: "+finalEx);
						}
						else
							finalEx = finalEx.reduceFractions();
					}
					else
						finalEx = finalEx.reduceFractions();
				}
				else
					finalEx = finalEx.reduceFractions();
			}
			else
				finalEx = finalEx.reduceFractions();
		}
		else
			finalEx = finalEx.reduceFractions(); //now that its something else, try reducing that...

		//trace.out("RatioExpression: reduceFractionsWhole()> " + finalEx.debugForm());
		return finalEx;
	}

	//removeFractionalComponent takes care of cases where either the numerator or the denominator is a fraction
	private Expression removeFractionalComponent() {
		Expression topDenom = top.denominator();
		Expression topNum = top.numerator();
		Expression bottomDenom = bottom.denominator();
		Expression bottomNum = bottom.numerator();
		if (!bottomDenom.isOne() || !topDenom.isOne()) {
			if (bottomNum.isOne() && topDenom.isOne())
				return top.multiply(bottomDenom);
			else if (bottomNum.isOne() && !topDenom.isOne())
				return topNum.multiply(bottomDenom).divide(topDenom);
			else if (!bottomNum.isOne() && topDenom.isOne())
				return top.multiply(bottom.reciprocal());
			else
				return topNum.multiply(bottomDenom).divide(topDenom.multiply(bottomNum));
		}
		else
			return this;
	}

	//cancelTerms cancels terms in the numerator and denominator that are algebraically equal
	private Expression cancelTerms() {
		Expression ret;
		Vector numTerms = top.getExplicitFactors(true);
		Vector denTerms = bottom.getExplicitFactors(true);
		Vector denLeft = new Vector();
		boolean didCancel = false;

		for (int i=0;i<denTerms.size();++i) {
			Expression ex1 = (Expression)denTerms.elementAt(i);
			boolean foundMatch = false;
			for (int j=0;j<numTerms.size();++j) {
				Expression ex2 = (Expression)numTerms.elementAt(j);
				if (ex1.algebraicEqual(ex2)) {
					numTerms.removeElementAt(j);
					foundMatch = true;
					didCancel = true;
				}
			}
			if (!foundMatch)
				denLeft.addElement(ex1);
		}
		if (!didCancel)
			ret = this;
		else if (numTerms.size() == 0 && denLeft.size() == 0)
			ret = new NumberExpression(1);
		else if (numTerms.size() == 0)
			ret = new RatioExpression(new NumberExpression(1),NegatedExpression.makeNegatedTerms(denLeft));
		else if (denLeft.size() == 0 && numTerms.size() == 1)
			ret = (Expression)(numTerms.elementAt(0));
		else if (denLeft.size() == 0)
			ret = NegatedExpression.makeNegatedTerms(numTerms);
		else if (numTerms.size() > 1 && denLeft.size() > 1)
			ret = new RatioExpression(NegatedExpression.makeNegatedTerms(numTerms),
									  NegatedExpression.makeNegatedTerms(denLeft));
		else if (numTerms.size() == 1)
			ret = new RatioExpression((Expression)(numTerms.elementAt(0)),
									  NegatedExpression.makeNegatedTerms(denLeft));
		else
			ret = new RatioExpression(NegatedExpression.makeNegatedTerms(numTerms),
									  (Expression)(denLeft.elementAt(0)));

		numTerms.removeAllElements();
		numTerms = null;
		denTerms.removeAllElements();
		denTerms = null;
		denLeft.removeAllElements();
		denLeft = null;

		return ret;
	}
	
	private boolean canCancelTerms() {
		Vector numTerms = top.getExplicitFactors(true);
		Vector denTerms = bottom.getExplicitFactors(true);
		Vector denLeft = new Vector();
		boolean didCancel = false;
		
		for (int i=0;i<denTerms.size();++i) {
			Expression ex1 = (Expression)denTerms.elementAt(i);
			for (int j=0;j<numTerms.size();++j) {
				Expression ex2 = (Expression)numTerms.elementAt(j);
				if (ex1.algebraicEqual(ex2)) {
					didCancel = true;
				}
			}
		}

		numTerms.removeAllElements();
		numTerms = null;
		denTerms.removeAllElements();
		denTerms = null;
		denLeft.removeAllElements();
		denLeft = null;

		return didCancel;
	}
	
	private Expression reduceTermExponents() {
		Vector numTerms = top.getExplicitFactors(true);
		Vector denTerms = bottom.getExplicitFactors(true);
		
		boolean foundMatch = false;
		
		for (int i=0;i<numTerms.size() && !foundMatch;++i) {
			Expression ex1 = (Expression)numTerms.elementAt(i);
			Expression base1 = ex1.exceptExponent();
			Expression exp1 = ex1.getExponent();
			for (int j=0;j<denTerms.size() && !foundMatch;++j) {
				Expression ex2 = (Expression)denTerms.elementAt(j);
				Expression base2 = ex2.exceptExponent();
				Expression exp2 = ex2.getExponent();
				if (base1.algebraicEqual(base2)) {
					foundMatch = true;
					boolean removeBottomTerm;
					//We've found two terms with equal bases. If their exponents are numbers, leave the larger one (with reduced exponent)
					if (exp1 instanceof NumericExpression &&
						exp2 instanceof NumericExpression) {
						if (((NumericExpression)exp1).doubleValue() >=
							((NumericExpression)exp2).doubleValue())
							removeBottomTerm=true;
						else
							removeBottomTerm=false;
					}
					//with non-numeric exponents, keep the term with the more complex exponent
					else if (exp1.complexity() >= exp2.complexity())
						removeBottomTerm=true;
					else
						removeBottomTerm=false;
					if (removeBottomTerm) {
						denTerms.removeElementAt(j);
						Expression newExponent = exp1.subtract(exp2).combineLikeTerms();
						if (newExponent.isOne())
							numTerms.setElementAt(base1,i);
						else
							numTerms.setElementAt(new ExponentExpression(base1,newExponent),i);
					}
					else {
						numTerms.removeElementAt(i);
						Expression newExponent = exp2.subtract(exp1).combineLikeTerms();
						if (newExponent.isOne())
							denTerms.setElementAt(base1,j);
						else
							denTerms.setElementAt(new ExponentExpression(base1,newExponent),j);
					}
				}
			}
		}
		//now, we've reduced one set of terms, recurse to remove any others
		Expression ret;
		if (foundMatch) {
			if (denTerms.size() == 0)
				ret = new TermExpression(numTerms);
			else {
				RatioExpression newExp = new RatioExpression(numTerms,denTerms);
				ret = newExp.reduceTermExponents();
			}
		}
		else
			ret = this;

		numTerms.removeAllElements();
		numTerms = null;
		denTerms.removeAllElements();
		denTerms = null;

		return ret;
	}
	
	private Expression factorAndReduce() {
		Expression result = this;
		//trace.out("RE.fAR: " + debugForm());
		if (canFactor()) {
			//trace.out("RE.fAR: canFactor");
			RatioExpression factored = (RatioExpression)(factor());
			//trace.out("RE.fAR: factored to: " + debugForm());
			if (factored.canCancelTerms()) {
				//trace.out("RE.fAR: canCancelTerms");
				result = factored.cancelTerms();
			}
		}
		return result;
	}
	
	//reduceNumerics reduces the numbers in a term expression
	private Expression reduceNumerics() {
		Expression num = new TermExpression(top.getExplicitFactors(true));
		Expression den = new TermExpression(bottom.getExplicitFactors(true));
		
		Expression topExpr = num.numericSimplifiedCoefficient();
		Expression bottomExpr = den.numericSimplifiedCoefficient();

		/*trace.out("RE.rN: " + topExpr.debugForm());
		  trace.out("       " + bottomExpr.debugForm());*/

		NumericExpression topNum=null, bottomNum=null;

		if(topExpr instanceof NumericExpression){
			topNum = (NumericExpression)topExpr;
		}
		if(bottomExpr instanceof NumericExpression){
			bottomNum = (NumericExpression)bottomExpr;
		}
				
		if (topNum != null && bottomNum != null){
			if(topNum.isIntegerType() && bottomNum.isIntegerType()) {
				/*here we cancel out common factors, but maintain the
                  fractional structure*/
				long topLong = (long)topNum.doubleValue();
				long botLong = (long)bottomNum.doubleValue();
				boolean floatNegative = false;
				if (botLong < 0) {
					/*move negatives to the numerator or factor out -1
					  from top & bottom (because gcf() operates on the abs
					  values of its arguments)*/
					topLong = -topLong;
					botLong = -botLong;
					floatNegative = true;
				}
				long gcf = FractionExpression.gcf(topLong,botLong);
				/*have to check here to prevent div by zero in
				  sm.canReduceFractions("0/a")*/
				if(gcf == 0){
					if(botLong == 0){
						/*but doing so breaks some of the opDivZero tests
						  in jsm.java, so we need to generate a div by
						  zero exception here.*/
						throw new DivideByZeroException("Division by zero in reduceNumerics");
					}
					else{
						return new NumberExpression(0);
					}
				}
				else if (gcf != 1 || floatNegative) {
					topLong = topLong/gcf;
					botLong = botLong/gcf;
					Expression newNum;
					Expression newDen;
					Expression withoutNum = num.exceptSimplifiedCoefficient();
					Expression withoutDen = den.exceptSimplifiedCoefficient();
					if (topLong != 1) {
						if (withoutNum.isEmpty())
							newNum = new NumberExpression(topLong);
						else{
							if(topLong == -1){
								newNum = withoutNum.negate();
							}
							else{
								newNum = new NumberExpression(topLong).multiply(withoutNum);
							}
						}
					}
					else
						newNum = withoutNum;
					if (botLong != 1) {
						if (withoutDen.isEmpty())
							newDen = new NumberExpression(botLong);
						else{
							if(botLong == -1){
								newDen = withoutDen.negate();
							}
							else{
								newDen = new NumberExpression(botLong).multiply(withoutDen);
							}
						}
					}
					else {
						//					trace.out("Den is "+den.toString()+" without coeff: "+den.exceptCoefficient().toString());
						newDen = withoutDen;
					}
					//				trace.out("newNum: "+newNum.debugForm()+" newden: "+newDen.debugForm());
					if (newNum.isEmpty() && newDen.isEmpty())
						return new NumberExpression(1);
					else if (newNum.isEmpty())
						return newDen.reciprocal();
					else if (newDen.isEmpty())
						return newNum;
					else
						return new RatioExpression(newNum,newDen);
				}
				else
					return this;
			}
			else{
				/*we already have at least one decimal, so we'll just
                  do the divison*/
				double newCoeff = topNum.doubleValue() / bottomNum.doubleValue();
				Expression newNum = num.exceptSimplifiedCoefficient();
				Expression newDen = den.exceptSimplifiedCoefficient();
				if(newCoeff == 0){
					return new NumberExpression(0);
				}
				else if(newCoeff != 1){
					newNum = (new NumberExpression(newCoeff)).multiply(newNum);
				}
				if(newDen.isOne() || newDen.isEmpty()){
					return newNum;
				}
				else{
					return new RatioExpression(newNum,newDen);
				}
			}
		}
		else
			return this;
	}
	
	//look for a polynomial over an integer
	private Expression reducePolyNumerics() {
		Expression result = this;
		Vector denT = bottom.getExplicitFactors(true);
		if (denT.size() == 1 && denT.elementAt(0) instanceof NumericExpression) {
			if (((NumericExpression)(denT.elementAt(0))).isIntegerType()) {
				NumericExpression numD = ((NumericExpression)(denT.elementAt(0)));
				long denom = (long)(numD.doubleValue());
//				trace.out("denom is "+denom);
				Vector numT = top.getExplicitFactors(true);
				PolyExpression numPoly = null;
				if (numT.size() == 1) {
					if ((numT.elementAt(0) instanceof PolyExpression))
						numPoly = (PolyExpression)(numT.elementAt(0));
					else if (numT.elementAt(0) instanceof FencedExpression &&
							 ((FencedExpression)(numT.elementAt(0))).getFenceDeepContents() instanceof PolyExpression)
						numPoly = (PolyExpression)(((FencedExpression)(numT.elementAt(0))).getFenceDeepContents());
					if (numPoly != null) {
						int numTerms = numPoly.numberOfTerms();
						long[] coeffs = new long[numTerms+1];
						for (int i=0;i<numTerms;++i) {
							coeffs[i] = (long)(numPoly.getTermAt(i).numericSimplifiedCoefficient().doubleValue());
//							trace.out("coeff "+i+" is "+coeffs[i]);
						}
						coeffs[numTerms] = denom;
						long gcf = FractionExpression.gcf(coeffs);
//						trace.out("gcf is "+gcf);
						if (gcf != 0 && gcf != 1) { //finally, we can do something
							Vector newNumerator = new Vector();
							for (int i=0;i<numTerms;++i) {
								long newCoeff = coeffs[i]/gcf;
//								trace.out("new coeff is "+newCoeff);
								Expression thisEx = numPoly.getTermAt(i);
//								trace.out("checking "+thisEx);
								Expression withoutCoeff = thisEx.exceptSimplifiedCoefficient();
								if (newCoeff != 1 && withoutCoeff != null)
									newNumerator.addElement(new NumberExpression(newCoeff).multiply(withoutCoeff));
								else if (newCoeff != 1) //can't really happen...
									newNumerator.addElement(new NumberExpression(newCoeff));
								else if (withoutCoeff != null)
									newNumerator.addElement(withoutCoeff);
								else //withoutCoeff is null -- must be a constant
									newNumerator.addElement(new NumberExpression(newCoeff));
//								trace.out("numerator is now "+newNumerator);
							}
							long newDen = denom/gcf;
							if (newDen != 1) {
								result = new RatioExpression(new PolyExpression(newNumerator),new NumberExpression(newDen));
							}
							else
								result = new PolyExpression(newNumerator);
						}
						coeffs = null;
					}
				}
				numT.removeAllElements();
				numT = null;
			}
		}
		denT.removeAllElements();
		denT = null;
		return result;
	}
	
	public boolean canDistributeWhole(int type) {
		if (((type & DISTDEN) != 0) &&
			(top.unfence() instanceof PolyExpression))
			return true;
		else
			return false;
	}

	
	public Expression distributeWhole(int type) {
		//trace.out("RE.dW(" + distributeDenominator + ") " + debugForm());
		if ((type & DISTDEN) != 0) {
			Expression numEx = top.unfence(); //ignore fences during distribution
			//trace.out("RE.dW: numEx = " + numEx.debugForm());
			if (numEx instanceof PolyExpression) {
				Vector components = numEx.getComponents();
				Object compInfo = numEx.getComponentInfo();
				for (int i=0;i<components.size();++i)
					components.setElementAt(((Expression)(components.elementAt(i))).divide(bottom),i);
				//trace.out("RE.dW: about to bFC: " + components);
				Expression ret = numEx.buildFromComponents(components,compInfo);
				//trace.out("RE.dW: returning: " + ret.debugForm());
				components.removeAllElements();
				components = null;
				return ret;
			}
			else
				return this;
		}
		else
			return super.distributeWhole(type);
	}
	


	protected Expression fractionToDecimalWhole(){
		//trace.out("RE.fTDW: begin: " + debugForm());
		NumericExpression coeff = top.numericSimplifiedCoefficient().numDivide(bottom.numericSimplifiedCoefficient());
		Expression topRest = top.exceptNumericSimplifiedCoefficient();
		Expression botRest = bottom.exceptNumericSimplifiedCoefficient();

		/*trace.out("RE.fTDW: coeff: " + coeff + "; topRest: " + topRest +
		  "; botRest: " + botRest);*/

		Expression rest;
		if(topRest == null){
			topRest = new NumberExpression(1);
		}
		if(botRest == null || botRest.isOne()){
			rest = topRest;
		}
		else{
			rest = new RatioExpression(topRest,botRest);
		}

		if(coeff.isOne()){
 			return rest;
		}
		else if(rest.isOne()){
			return coeff;
		}
		else{
			return new TermExpression(coeff,rest);
		}
	}
	
	public boolean canFactor() {
		return (top.unfence().canFactor() || bottom.unfence().canFactor());
	}

        public boolean canFactor(Expression fact){
            return (top.unfence().canFactor(fact) || bottom.unfence().canFactor(fact));
        }

	public boolean canFactorPiecemeal(Expression fact){
		return (top.unfence().canFactorPiecemeal(fact) ||
				bottom.unfence().canFactorPiecemeal(fact));
	}
	
	public Expression factor() {
		Expression topFact = top.unfence().factor();
		Expression bottomFact = bottom.unfence().factor();
		return new RatioExpression(topFact,bottomFact);
	}

        public Expression factor(Expression fact){
            Expression topFact = top.unfence().factor(fact);
            Expression bottomFact = bottom.unfence().factor(fact);
            return new RatioExpression(topFact,bottomFact);
        }
	
	public Expression factorPiecemeal(Expression fact){
		Expression topFact = top.unfence().factorPiecemeal(fact);
		Expression bottomFact = bottom.unfence().factorPiecemeal(fact);
		return new RatioExpression(topFact,bottomFact);
	}

	public Queryable getProperty(String prop) throws NoSuchFieldException {
		// ALLEN
		if (prop.equalsIgnoreCase("isRatio")) {
			return new BooleanQuery(true);
		}
		// end ALLEN
		if (prop.equalsIgnoreCase("numerator"))
			return top;
		else if (prop.equalsIgnoreCase("denominator"))
			return bottom;
		if (prop.equalsIgnoreCase("numerator terms"))
			return new ArrayQuery(top.getFullComponents());
		else if (prop.equalsIgnoreCase("denominator terms"))
			return new ArrayQuery(bottom.getFullComponents());
		else
			return super.getProperty(prop);
	}
	
	protected Vector getExpandedForm() {
		Vector topExp = top.getExpandedForm();
		topExp.addElement(new RatioExpression(new NumberExpression(1),bottom).cleanExpression());
		return topExp;
	}
	
	//RatioExpresions usually don't exist in the cannonical form, so
	//we try to convert to a termExpression
	public Expression initialCannonicalize() {
		// in case "3.17/x" it falls into the infinite loop with terms, because in terms :
		//[TermExp(2):  1:[NumExp: 3::3.17] 2:[Ratio: [NumExp: 0::1.0] :: [var: x]]]
		/*level++;
		  trace.out("RE.initialCannonicalize[" + level + "]: " + debugForm());
		  if(level > 100){
		  (new Exception()).printStackTrace();
		  System.exit(1);
		  }*/
            if(!top.canSimplify() && bottom instanceof VariableExpression){
                /*trace.out("RE.initialCannonicalize: doing nothing");
				  level--;*/
				return this;
            }
            //trace.out("RE.initialCannonicalize[" + level + "]: converting to TermExpression");
            /*toTermExpression returns null if it can't get rid of all
              the RatioExpressions*/
            TermExpression te = toTermExpression();
            if(te != null){
                //trace.out("RE.initialCannonicalize[" + level + "]: toTermExpression successful");
                Expression ret = te.simplify();
				if(ret instanceof RatioExpression){
					/*getting rid of the RatioExpressions in
                      toTermExpression was only temporary -- they've
                      re-surfaced.*/
					/*trace.out("RE.initialCannonicalize[" + level + "]: simplified back to ratio: doing nothing");
					  level--;*/
					return this;
				}
				else{
					//trace.out("RE.initialCannonicalize[" + level + "]: simplify successful");
					/*ret = ret.initialCannonicalize();
					  trace.out("RE.initialCannonicalize[" + level + "]: initialCannonicalize successful");*/
					//level--;
					return ret;
				}
            }
            else{
                /*trace.out("RE.initialCannonicalize[" + level + "]: doing nothing after toTermExpression");
				  level--;*/
                return this;
            }
	}

	protected Expression cannonicalizeWhole(){
		return fractionToDecimal();
	}

	public String toASCII(String openParen, String closeParen) {
		StringBuffer numString = new StringBuffer(asciiSBsize);
		StringBuffer denString = new StringBuffer(asciiSBsize);
		if (top instanceof TermExpression &&
			((TermExpression)top).getTerm(((TermExpression)top).numSubTerms()-1) instanceof ExponentExpression){
		 //if the numerator is a term which ends with an exponent, but parens around it
		 // so x^2/3 is (x^2)/3, not misinterpreted as x^(2/3)
			numString.append(openParen).append(top.toASCII(openParen,closeParen)).append(closeParen);
		}
		else if (top instanceof PolyExpression ||
				 top instanceof ExponentExpression ||
				 top instanceof FractionExpression ||
				 top instanceof RatioExpression)
			numString.append(openParen).append(top.toASCII(openParen,closeParen)).append(closeParen);
		else
			numString.append(top.toString());
		if (bottom instanceof TermExpression ||
			bottom instanceof PolyExpression ||
			bottom instanceof FractionExpression ||
			bottom instanceof RatioExpression)
			denString.append(openParen).append(bottom.toASCII(openParen,closeParen)).append(closeParen);
		else
			denString.append(bottom.toASCII(openParen,closeParen));

		return numString.append("/").append(denString.toString()).toString();
	}
	
	public String toMathML() {
		return addMathMLAttributes("<mfrac>"+top.toMathML()+" "+bottom.toMathML()+"</mfrac>");
	}

	public String debugForm() {
		return "[Ratio: "+top.debugForm()+" :: "+bottom.debugForm()+"]";
	}

}
