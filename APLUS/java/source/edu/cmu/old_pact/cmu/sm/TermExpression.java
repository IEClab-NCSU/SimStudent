package edu.cmu.old_pact.cmu.sm;

//TermExpressions
//TermExpressions are chains of terms

import java.util.Arrays;
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class TermExpression extends Expression implements CompoundExpression {
	private Expression subterms[] = new Expression[5];
	private int numSubs = 0;

	public TermExpression(Expression t1,Expression t2) {
		subterms[0] = t1;
		subterms[1] = t2;
		numSubs=2;
	}
	
	public TermExpression() {
	}
	
	//if anything in the subterms vector is itself a TermExpression, we break it up and add
	//the subterms
	public TermExpression(Vector subs) {
		for (int i=0;i<subs.size();++i) {
			if (subs.elementAt(i) instanceof TermExpression &&
				!(subs.elementAt(i) instanceof NegatedExpression)) {
				TermExpression addTerm = (TermExpression)(subs.elementAt(i));
				for (int j=0;j<addTerm.numSubTerms();++j)
					subterms = Expression.addToArray(addTerm.getTerm(j),subterms,numSubs++);
			}
			else
				subterms = Expression.addToArray((Expression)(subs.elementAt(i)),subterms,numSubs++);
		}
	}
	
	public TermExpression(Expression subs[],int subnum) {
		for (int i=0;i<subnum;++i) {
			if (subs[i] instanceof TermExpression &&
				!(subs[i] instanceof NegatedExpression)) {
				TermExpression addTerm = (TermExpression)(subs[i]);
				for (int j=0;j<addTerm.numSubTerms();++j)
					subterms = Expression.addToArray(addTerm.getTerm(j),subterms,numSubs++);
			}
			else
				subterms = Expression.addToArray((Expression)(subs[i]),subterms,numSubs++);
		}
	}
	
	//In the constructor with 2 vectors, the first vector is a list of terms
	//in the numerator; the second is a list of terms in the denominator (where the exponents
	//are given as positive)
	public TermExpression(Vector numTerms, Vector denTerms) {
		//First, try to make a ratio out of the numbers
		
		int numInt = -1;
		int denInt = -1;
		
		for (int i=0;i<numTerms.size() && numInt == -1;++i) {
			Expression thisTerm = (Expression)(numTerms.elementAt(i));
			if (thisTerm instanceof NumberExpression &&
				((NumberExpression)thisTerm).isIntegerType())
				numInt = i;
		}
		
		for (int i=0;i<denTerms.size() && denInt == -1;++i) {
			Expression thisTerm = (Expression)(denTerms.elementAt(i));
			if (thisTerm instanceof NumberExpression &&
				((NumberExpression)thisTerm).isIntegerType())
				denInt = i;
		}
		
		if (numInt > -1 && denInt > -1) {
			Expression newExp = new FractionExpression((NumberExpression)(numTerms.elementAt(numInt)),
													   (NumberExpression)(denTerms.elementAt(denInt)));
			subterms = Expression.addToArray(newExp,subterms,numSubs++);
			numTerms.removeElementAt(numInt);
			denTerms.removeElementAt(denInt);
		}

		for (int i=0;i<numTerms.size();++i)
			subterms= Expression.addToArray((Expression)(numTerms.elementAt(i)),subterms,numSubs++);
		for (int i=0;i<denTerms.size();++i) {
			Expression thisTerm = (Expression)(denTerms.elementAt(i));
			if (thisTerm instanceof ExponentExpression) {
				ExponentExpression theTerm = (ExponentExpression)thisTerm;
				ExponentExpression newExp = new ExponentExpression(theTerm.getBody(),theTerm.getExponent().negate());
				subterms = Expression.addToArray(newExp,subterms,numSubs++);
			}
			else if (thisTerm instanceof NumberExpression &&
					((NumberExpression)thisTerm).isIntegerType())
				subterms = Expression.addToArray(new FractionExpression(new NumberExpression(1),(NumberExpression)thisTerm),subterms,numSubs++);
			else {
				Expression newExp = thisTerm.reciprocal();
				subterms = Expression.addToArray(newExp,subterms,numSubs++);
			}
		}
	}

	public void finalize() throws Throwable{
		try{
			for (int i=0;i<numSubs;++i)
				subterms[i]=null;
			subterms = null;
		}
		finally{
			super.finalize();
		}
	}

	/*this is used by the parser to get rid of instances of
      FencedExpressions where the parens really are mathematically
      necessary*/
	public Expression removeRedundantFencesWhole(){
		TermExpression unfenced = uncleanBuildFromComponents(subterms,numSubs);
		/*loop over the subterms, looking for a fencedexpression.
          each time we find one, unfence it and see if the parens
          still pop out of termString().  If they do, then they're
          mathematically necessary and we can stick with the modified
          version of the subterm.*/
		ExpressionArray comps = unfenced.getComponentArray();
		for(int i=0;i<comps.size();i++){
			if(comps.expressionAt(i) instanceof FencedExpression){
				String oldExp = unfenced.toASCII("(",")");
				/*unfence it*/
				comps.setExpressionAt((comps.expressionAt(i)).unfence(),i);
				unfenced = uncleanBuildFromComponents(comps.getExpressions(),comps.size());
				/*check if we've broken the parenthesization*/
				if(!oldExp.equals(unfenced.toASCII("(",")"))){
					/*we did; put it back in the fences*/
					comps.setExpressionAt(new FencedExpression(comps.expressionAt(i)),i);
					unfenced = uncleanBuildFromComponents(comps.getExpressions(),comps.size());
				}
			}
		}

		ExpressionArray.deallocate(comps);
		
		return unfenced;
	}

        /*returns true if the above constructor would make a
          RatioExpression out of any of the given denominator terms*/
        public static boolean wouldBecomeRatio(Vector denTerms){
            for(int i=0;i<denTerms.size();i++){
                Expression thisTerm = (Expression)(denTerms.elementAt(i));
                if (thisTerm instanceof ExponentExpression) {
                    ;
                }
                else if (thisTerm instanceof NumberExpression &&
                         ((NumberExpression)thisTerm).isIntegerType()){
                    ;
                }
                else {
                    Expression newExp = thisTerm.reciprocal();
                    if(newExp instanceof RatioExpression){
                        return true;
                    }
                }
            }

            return false;
        }
	
	protected TermExpression uncleanBuildFromComponents(Vector components){
		return new TermExpression(components);
	}

	protected TermExpression uncleanBuildFromComponents(Expression components[],int numcomp){
		return new TermExpression(components,numcomp);
	}
	
	protected Expression buildFromComponents(Vector components) {
		return uncleanBuildFromComponents(components).cleanExpression();
	}
	
	protected Expression buildFromComponents(ExpressionArray components) {
		return uncleanBuildFromComponents(components.getExpressions(),components.size()).cleanExpression();
	}
	
	public Vector getComponents() {
		Vector comps = new Vector();
		for (int i=0;i<numSubs;++i) {
			comps.addElement(getTerm(i));
		}
		return comps;
	}
	
	public ExpressionArray getComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate(subterms,numSubs);
//		trace.out("in getComponentArray, allocated EA");
		return compArray;
	}

	public TermExpression insert(Expression ex) {
		///hmm - this used to be non-destructive; now it is destructive
		subterms = Expression.addToArray(ex,subterms,numSubs++);
		return this;
		
		///non-destructive (slow) version
/////		Vector newterms = new Vector();
/////		for (int i=0;i<numSubs;++i)
/////			newterms.addElement(subterms[i]);
/////		newterms.addElement(ex);
/////		return uncleanBuildFromComponents(newterms);
		
		
//		Vector newterms = (Vector)subterms.clone();
//		newterms.addElement(ex);
//		return uncleanBuildFromComponents(newterms);
	}
	
	//mergeTerms merges the expressions together
	//If the Expression to be merged in is a TermExpression, we add it term-by-term
	//Otherwise, we add it whole
	private TermExpression mergeTerms(Expression ex) {
		TermExpression ret = (TermExpression)clone();
		if (ex instanceof TermExpression &&
			!(ex instanceof NegatedExpression)) {
			TermExpression tEx = (TermExpression)ex;
			for (int i=0;i<tEx.numSubTerms();++i){
				ret = ret.insert(tEx.getTerm(i));
			}
		}
		else
			ret = ret.insert(ex);

		return ret;
	}
	
	public Expression getTerm(int num) {
		if (num < numSubs)
			return subterms[num];
		else
			return null;
	}
	
	//numeratorTerms returns a vector of terms that would be in the numerator
	private Vector numeratorTerms() {
		return numeratorTerms(false);
	}
	
	private Vector numeratorTerms(boolean includeNegExponents) {
		Vector nTerms = new Vector();
		for (int i=0;i<numSubs;++i) {
			Expression thisTerm = (Expression)(subterms[i]);
			if (thisTerm instanceof FractionExpression) {
				NumberExpression num = (NumberExpression)(((FractionExpression)thisTerm).numerator());
				if (num.doubleValue() != 1.0)
					nTerms.addElement(((FractionExpression)thisTerm).numerator());
			}
			else if (thisTerm instanceof RatioExpression) {
				Expression num = ((RatioExpression)thisTerm).numerator();
				if (num instanceof TermExpression &&
					!(num instanceof NegatedExpression))
					for (int j=0;j<((TermExpression)num).numSubTerms();++j)
						nTerms.addElement(((TermExpression)num).getTerm(j));
				else if (!num.isOne()) //don't add 1 to numerator
					nTerms.addElement(num);
			}
			else if (thisTerm instanceof ExponentExpression && 
					 (((ExponentExpression)thisTerm).getExponent().isNegative())) {
				if (includeNegExponents) //leave this as a case under ExponentExpression, since we don't want the default case
					nTerms.addElement(thisTerm);
			}
			//break up TermExpressions
			else if (thisTerm instanceof TermExpression &&
					 !(thisTerm instanceof NegatedExpression)) {
				TermExpression addTerm = ((TermExpression)thisTerm).flatten();
				for (int j=0;j<addTerm.numSubTerms();++j)
					nTerms.addElement(addTerm.getTerm(j));
			}
			else
				nTerms.addElement(thisTerm);
		}
		return nTerms;
	}
				
	//denominatorTerms returns a vector of terms that would be in the denominator
	//We leave the exponent positive
	
	private Vector denominatorTerms() {
		return denominatorTerms(true);
	}
	
	private Vector denominatorTerms(boolean includeNegExponents) {
		Vector nTerms = new Vector();
		for (int i=0;i<numSubs;++i) {
			Expression thisTerm = (Expression)(subterms[i]);
			if (thisTerm instanceof FractionExpression)
				nTerms.addElement(((FractionExpression)thisTerm).denominator());
			else if (thisTerm instanceof ExponentExpression && 
					 (((ExponentExpression)thisTerm).getExponent().isNegative()) &&
					 includeNegExponents) {
				Expression termBody = ((ExponentExpression)thisTerm).getBody();
				termBody = termBody.cleanExpression();
				Expression exp = ((ExponentExpression)thisTerm).getExponent();
				if (exp.isNegOne()) {//with sign reversed, we don't need exponent
					if (termBody instanceof TermExpression &&
						!(termBody instanceof NegatedExpression)) {
						TermExpression addTerm = ((TermExpression)termBody).flatten();
						for (int j=0;j<addTerm.numSubTerms();++j)
							nTerms.addElement(addTerm.getTerm(j));
					}	
					else
						nTerms.addElement(termBody);
				}
				else {
					ExponentExpression termCopy = new ExponentExpression(termBody,exp.negate().removeDoubleSignsWhole());
					nTerms.addElement(termCopy);
				}
			}
			else if (thisTerm instanceof RatioExpression) {
				Expression den = ((RatioExpression)thisTerm).denominator();
				if (den instanceof TermExpression &&
					!(den instanceof NegatedExpression))
					for (int j=0;j<((TermExpression)den).numSubTerms();++j)
						nTerms.addElement(((TermExpression)den).getTerm(j));
				else
					nTerms.addElement(den);
			}
		}
		return nTerms;
	}

	//removeSubTerm removes a term from the polynomial
	public TermExpression removeSubTerm (Expression ex) {
		Vector newterms = new Vector(Arrays.asList((Expression[])subterms.clone()));
		int pos = newterms.indexOf(ex);
		if (pos > -1) {
			newterms.removeElement(ex);
		}
		return uncleanBuildFromComponents(newterms);
	}

	public boolean algEqualMember(Expression ex) {
		for (int i=0;i<numSubTerms();++i)
			if (getTerm(i).algebraicEqual(ex))
				return true;
		return false;
	}
		
	public int numSubTerms() {
		return numSubs;
	}
	
	//isLike returns true if the two expressions can be added together
	public boolean isLike(Expression ex) {
		if(!numericSimplifiedCoefficient().exactEqual(numericUnsimplifiedCoefficient()) ||
		   !ex.numericSimplifiedCoefficient().exactEqual(ex.numericUnsimplifiedCoefficient())){
			//have to perform mult before you can add like terms
			return false;
		}
		Expression myBody = exceptUnsimplifiedCoefficient();
		Expression otherBody = ex.exceptUnsimplifiedCoefficient();
//			if (myBody != null && otherBody != null)
//				trace.out("CC with "+this.toString()+" and "+ex.toString()+":"+myBody.toString()+":"+otherBody.toString()+":"+myBody.algebraicEqual(otherBody));
		if (myBody != null && otherBody != null &&         //myBody can't ever be null (but otherBody could)
			!myBody.isEmpty() && !otherBody.isEmpty() &&   //myBody would be empty if I'm a product, e.g. 3*4
			myBody.algebraicEqual(otherBody)) {
//			trace.out(myBody+" is like "+otherBody+":::"+this+":::"+ex);
			return true;
		}
		else if (myBody instanceof NumericExpression && 
		         ex instanceof NumericExpression &&
		         myBody.algebraicEqual(ex)) //3 can be combined with 2*3
			return true;
		else if(ex instanceof FencedExpression){
			return ex.isLike(this);
		}
		else
			return false;
	}
	
	///Multiply methods
	//Multiply any Expression
	public Expression multiply(Expression ex) {
		if (numSubTerms() == 0)
			return ex;
		else if (ex instanceof TermExpression && ((TermExpression)ex).numSubTerms() == 0) 
			return this;
		else {
			TermExpression newEx = uncleanBuildFromComponents(subterms,numSubs);
			newEx = newEx.insert(ex);
			return newEx;
		}
	}
	
        public NumericExpression numericSimplifiedCoefficient() {
            NumericExpression total = null;

            for (int i=0;i<numSubs;++i) {
                NumericExpression subtermCoeff = ((Expression)(subterms[i])).numericSimplifiedCoefficient();
                if (total == null)
                    total = subtermCoeff;
                else if (subtermCoeff.isOne())
                    ; //do nothing
                else
                    total = total.numMultiply(subtermCoeff);
            }
            if (total == null)
                return new NumberExpression(1);
            else
                return total;
	}

	public Expression exceptNumericSimplifiedCoefficient(){
		Expression total = null;
		for(int i=0;i<numSubs;i++){
			Expression sub = ((Expression)(subterms[i])).exceptNumericSimplifiedCoefficient();
			if(sub != null){
				if(total == null){
					total = sub;
				}
				else{
					total = total.multiply(sub);
				}
			}
		}

		return total;
	}

        public NumericExpression numericUnsimplifiedCoefficient() {
            NumericExpression result = new NumberExpression(1);
            boolean foundNum=false;
            
            for (int i=0;i<numSubs&&!foundNum;++i) {
                Expression thisSubterm = (Expression)(subterms[i]);
                if (thisSubterm instanceof NumericExpression) {
                    result = (NumericExpression)thisSubterm;
                    foundNum=true;
                }
                else {
                    NumericExpression subCoeff = thisSubterm.numericUnsimplifiedCoefficient();
                    if (!subCoeff.isOne()) {
                        result = subCoeff;
                        foundNum=true;
                    }
                }
            }
            return result;
	}

	public Expression exceptNumericUnsimplifiedCoefficient(){
		TermExpression ret = new TermExpression();
		boolean foundNum = false;

		for(int i=0;i<numSubs;i++){
			if(foundNum || !(subterms[i] instanceof NumericExpression)){
				ret = ret.insert((Expression)subterms[i]);
			}
			else{
				foundNum = true;
			}
		}

		return ret;
	}

	//simplifiedCoefficient is a NumericExpression, which is the product of the coefficients of the
	//subterms
	public Expression simplifiedCoefficient() {
		NumericExpression numCoeff = null;
                Expression otherCoeff = null;

                //trace.out(debugForm() + ".simplifiedCoefficient() ...");
		
		for (int i=0;i<numSubs;++i) {
			Expression subtermCoeff = ((Expression)(subterms[i])).simplifiedCoefficient();
                        if(subtermCoeff instanceof NumericExpression){
                            if (numCoeff == null){
				numCoeff = (NumericExpression)subtermCoeff;
                            }
                            else if (subtermCoeff.isOne()){
				; //do nothing
                            }
                            else{
				numCoeff = numCoeff.numMultiply((NumericExpression)subtermCoeff);
                            }
                        }
                        else{
                            if(otherCoeff == null){
                                otherCoeff = subtermCoeff;
                            }
                            else{
                                otherCoeff = otherCoeff.multiply(subtermCoeff);
                            }
                        }
		}

                //trace.out("\tnumCoeff == " + numCoeff);
                //trace.out("\totherCoeff == " + otherCoeff);

                Expression total;
                if(numCoeff == null){
                    if(otherCoeff == null){
                        total = null;
                    }
                    else{
                        total = otherCoeff;
                    }
                }
                else{
                    if(otherCoeff == null){
                        total = numCoeff;
                    }
                    else{
                        total = numCoeff.multiply(otherCoeff);
                    }
                }

		if (total == null){
                    //trace.out("TermExpression.simplifiedCoefficient(): null");
			return new NumberExpression(1);
                }
		else{
			return total;
                }
	}

	//unsimplifiedCoefficient returns the first numericExpression
	public Expression unsimplifiedCoefficient() {
		Expression result = new NumberExpression(1);
		boolean foundCoeff=false;
		
		for (int i=0;i<numSubs&&!foundCoeff;++i) {
			Expression thisSubterm = (Expression)(subterms[i]);
			if (thisSubterm instanceof NumericExpression) {
				result = (NumericExpression)thisSubterm;
				foundCoeff=true;
			}
                        else if(thisSubterm instanceof LiteralExpression ||
                                thisSubterm instanceof ConstantExpression){
                            result = (VariableExpression)thisSubterm;
                            foundCoeff = true;
                        }
			else {
                                Expression subCoeff = thisSubterm.unsimplifiedCoefficient();
				if (!subCoeff.isOne()) {
					result = subCoeff;
					foundCoeff=true;
				}
			}
		}
		return result;
	}
	
	//negate for a term
	//If the first thing in the term is a number, negate it
	//Otherwise, add -1 to the start of the term
	public Expression negate() {
		TermExpression newEx = new TermExpression();
		if (getTerm(0) instanceof NumericExpression) {
			Expression newCoeff = getTerm(0).negate();
			newEx = newEx.insert(newCoeff);
			/*if (!newCoeff.isOne() && numSubs > 1)
			  newEx = newEx.insert(newCoeff);*/
			for (int i=1;i<numSubTerms();++i)
				newEx = newEx.insert(getTerm(i));
		}
		else if(getTerm(0) instanceof NegatedExpression){
			newEx = newEx.insert(getTerm(0).negate());
			for (int i=1;i<numSubTerms();++i){
				newEx = newEx.insert(getTerm(i));
			}
		}
		else {
			return super.negate();
		}
		return newEx.cleanExpression();
	}
	
	//we consider the term to be negative if the first term is negative
	//[this probably should be called something else, but this is what we need for now]
	public boolean isNegative() {
		return getTerm(0).isNegative();
	}
		
	public Expression exceptSimplifiedCoefficient() {
		TermExpression result = new TermExpression();
		for (int i=0;i<numSubs;++i){
			if(subterms[i] instanceof NegatedExpression){
				result = result.insert(((TermExpression)subterms[i]).getTerm(1));
			}
			else if (!(subterms[i] instanceof NumericExpression)){
				result = result.insert((Expression)(subterms[i]));
			}
		}
		return result.cleanExpression();
	}
	
	public Expression exceptUnsimplifiedCoefficient() {
		boolean foundCoeff=false;
		TermExpression result = new TermExpression();
		for (int i=0;i<numSubs;++i) {
			Expression thisSubterm = subterms[i];
			if(thisSubterm instanceof NumericExpression){
				if(foundCoeff){
					result = result.insert(thisSubterm);
				}
				else{
					foundCoeff = true;
				}
			}
			else if(thisSubterm instanceof NegatedExpression){
				if(foundCoeff){
					result = result.insert(thisSubterm);
				}
				else{
					foundCoeff = true;
					result = result.insert(((TermExpression)thisSubterm).getTerm(1));
				}
			}
			else{
				result = result.insert(thisSubterm);
			}
		}
		return result.cleanExpression();
	}


	//If we're combining termExpressions, the only thing different is the (unsimplified) coefficients,
	//so we just add them and copy the body
	//If we're combining a TermExpression and a variable, call it the other way
        //mmmBUG: what happens when we do addLikeTerms on "ax" and "ax"?  do we get "2ax" or "(a+a)x"?
	public Expression addLikeTerms (Expression ex) {
		if (ex instanceof TermExpression) {
			NumericExpression thisCoeff = numericUnsimplifiedCoefficient();
			NumericExpression otherCoeff = ex.numericUnsimplifiedCoefficient();
			NumericExpression totalCoeff = thisCoeff.numAdd(otherCoeff);
			TermExpression finalEx = new TermExpression();
			finalEx = finalEx.insert(totalCoeff);
			Expression body = this.exceptUnsimplifiedCoefficient();
			finalEx = finalEx.mergeTerms(body);
			return finalEx;
		}
		else if(ex instanceof RatioExpression){
			RatioExpression rex = (RatioExpression)ex;
			NumericExpression thisNum = numericUnsimplifiedCoefficient().numMultiply((NumericExpression)rex.denominator());
			NumericExpression totalNum = thisNum.numAdd(rex.numerator().numericUnsimplifiedCoefficient());
			if(thisNum.isIntegerType() &&
			   totalNum.isIntegerType() &&
			   ((NumericExpression)rex.denominator()).isIntegerType()){
				/*everthing is ints, so maintain fractional structure*/
				RatioExpression ret = new RatioExpression(totalNum.multiply(exceptUnsimplifiedCoefficient()),
														  rex.denominator());
				return ret;
			}
			else{
				/*something is a decimal, so we'll convert the whole
                  mess to a single decimal and make a termexpression
                  out of it*/
				TermExpression ret = new TermExpression();
				ret = ret.insert(totalNum.numDivide((NumericExpression)rex.denominator()));
				ret = ret.mergeTerms(exceptUnsimplifiedCoefficient());
				return ret;
			}
		}
		//adding an ExponentExpression is just like adding a TermExpression, except we know that the coefficient of the ExponentExpression is 1
		//if we're adding to a number, we know the termBody equals the number, so add 1 to coefficient (e.g. 2*4+4 = 3*4)
		else if (ex instanceof ExponentExpression || ex instanceof NumericExpression) {
			NumericExpression totalCoeff = numericUnsimplifiedCoefficient().numAdd(new NumberExpression(1));
			TermExpression finalEx = new TermExpression();
			finalEx = finalEx.insert(totalCoeff);
			Expression body = this.exceptUnsimplifiedCoefficient();
			finalEx = finalEx.mergeTerms(body);
			return finalEx;
		}			
		else if (ex instanceof VariableExpression) {
			return ex.addLikeTerms(this);
		}
		else if(ex instanceof FencedExpression){
			return ex.addLikeTerms(this);
		}
		else
			throw new IllegalArgumentException("TermExpression.addLikeTerms {"+debugForm()+"} called on "+ex.debugForm());
	}
	
	//startsWithNumber returns true if the ASCII representation of the expression starts with a number
	//(or decimal point or negative sign). We use this to determine when to put an explict * between
	//terms
	private static boolean startsWithNumber(Expression ex) {
		char start = ex.toString().charAt(0);
		if (Character.isDigit(start))
			return true;
		else if (start == '.')
			return true;
		else if (start == '-')
			return true;
		else 
			return false;
	}
	
	//termString takes a vector of terms and puts them in a string
	//This code worries about where to put explicit times and where to put parens so that the parser will
	//interpret things correctly
	private String termString(Expression subterms[], String openParen, String closeParen) {
		if (Expression.printStruct)
			return debugForm();

		StringBuffer finalString = new StringBuffer(asciiSBsize);
		boolean prevWasNumeric=false;
		boolean prevWasNegSign=false;
		boolean prevWasVariable=false;
		boolean prevWasCompound=false;
		boolean prevWasRatio=false;
		boolean encloseNext = false;
		boolean finishEnclose = false;
		int negParenCount = 0;
		for (int i=0;i<numSubs;++i) {
			Expression exp1 = subterms[i];
			if (i<numSubs) {
				if (i>0) {
					if (exp1 instanceof NumericExpression && !prevWasNegSign) {
						finalString.append("*");
					}
					else if (prevWasVariable == true)
						finalString.append("*");
					else if ((exp1 instanceof VariableExpression) && 
							 (prevWasCompound == true)) {
						finalString.append("*");
					}
					else if (exp1 instanceof RatioExpression ||
							 exp1 instanceof FractionExpression ||
							 (exp1 instanceof FencedExpression &&
							  (((FencedExpression)exp1).getFenceContents() instanceof RatioExpression ||
							   ((FencedExpression)exp1).getFenceContents() instanceof FractionExpression)))
						finalString.append("*");
					else if (prevWasRatio)
						finalString.append("*");
					else if (exp1 instanceof NegatedExpression){
						finalString.append("*");
						if(((NegatedExpression)exp1).getTerm(1) instanceof VariableExpression){
							prevWasVariable = true;
						}
					}
				}
			}
 			
			if (exp1 instanceof PolyExpression || 
				(exp1 instanceof TermExpression && !(exp1 instanceof NegatedExpression)) ||
				(exp1 instanceof ExponentExpression && !prevWasNumeric) ||
				(exp1 instanceof ExponentExpression && i < numSubs-1) ||
				encloseNext){
				finalString.append(openParen);
				encloseNext = false;
				finishEnclose = true;
			}
			else if (prevWasNumeric && startsWithNumber(exp1) && 
					 !(finalString.charAt(finalString.length()-1) == '*'))
 				finalString.append("*");
			//special-case -1 and 1 before variable
			prevWasNegSign=false;
			if (i<numSubs-1 && exp1 instanceof NumberExpression) {
				// -1*-5 should print out as -(-5)
				/*if (exp1.isNegOne() && (!(subterms[i+1] instanceof NumberExpression) ||
										((Expression)subterms[i+1]).isNegative())){
					prevWasNegSign = true;
					finalString.append("-");
					if(!(subterms[i+1] instanceof FencedExpression) &&
					   ((Expression)subterms[i+1]).isNegative()){
						encloseNext = true;
					}
					}*/
				if (exp1.isOne() && subterms[i+1] instanceof VariableExpression)
					;//don't print "1" before a variable
				else
					finalString.append(exp1.toASCII(openParen,closeParen));
			}
	//Olga	
			else if(exp1 instanceof RatioExpression ||
					exp1 instanceof FractionExpression){
				if(i<numSubs-1 && !finishEnclose &&
				   (subterms[i+1] instanceof RatioExpression ||
					subterms[i+1] instanceof FractionExpression ||
					(subterms[i+1] instanceof FencedExpression &&
					 (((FencedExpression)subterms[i+1]).getFenceContents() instanceof RatioExpression ||
					  ((FencedExpression)subterms[i+1]).getFenceContents() instanceof FractionExpression)))){
					finalString.append(openParen).append(exp1.toASCII(openParen,closeParen)).append(closeParen);
					if(!(subterms[i+1] instanceof FencedExpression)){
						encloseNext = true;
					}
				}
				else if(encloseNext && !finishEnclose) {
					finalString.append(openParen).append(exp1.toASCII(openParen,closeParen)).append(closeParen);
					encloseNext = false;
				}
				else
					finalString.append(exp1.toASCII(openParen,closeParen));
			}
	// end Olga
			else
				finalString.append(exp1.toASCII(openParen,closeParen));
				
			if(finishEnclose){
				if(prevWasNegSign){
					negParenCount++;
				}
				else{
					finalString.append(closeParen);
				}
				finishEnclose = false;
			}
			if(!prevWasNegSign){
				for(int j=0;j<negParenCount;j++){
					finalString.append(closeParen);
				}
				negParenCount = 0;
			}
			
			prevWasNumeric=false;
			prevWasVariable=false;
			prevWasCompound=false;
			prevWasRatio=false;
			if (exp1 instanceof NumericExpression && !prevWasNegSign) //neg. sign is treated as non-numeric here
				prevWasNumeric = true;
			else if (exp1 instanceof VariableExpression)
				prevWasVariable = true;
			else if (exp1 instanceof TermExpression || exp1 instanceof RatioExpression ||
					 exp1 instanceof PolyExpression || exp1 instanceof FencedExpression)
				prevWasCompound = true;
			if (exp1 instanceof RatioExpression || exp1 instanceof FractionExpression)
				prevWasRatio = true;
		}
		return finalString.toString();
	}

/*	public String toASCII(String openParen, String closeParen) {
		Vector numTerms = numeratorTerms();
		Vector denTerms = denominatorTerms();
		
//		trace.out("Num terms: "+numTerms+"::"+termString(numTerms));
//		trace.out("Den terms: "+denTerms+"::"+termString(denTerms));
		
		if (numTerms.size() == 0) {
			if (denTerms.size() > 1)
				return "1/"+openParen+termString(denTerms,openParen,closeParen)+closeParen;
			else
				return "1/"+termString(denTerms,openParen,closeParen);
		}
		else if (denTerms.size() == 0)
			return termString(numTerms,openParen,closeParen);
		//Weird special case here:
		//We never print x*1/3, since x/3 is represented as [x,1/3]
		//Similarly, we never print x*-1/3, since -x/3 is represented as [x,-1/3] -- note that [-1,x,1/3] also prints as -x/3, though it probably shouldn't
		else if (denTerms.size() == 1 &&
		         denTerms.elementAt(0) instanceof NumericExpression &&
		         numTerms.size() == 2 &&
		         (((Expression)(numTerms.elementAt(1))).isOne() ||
		          ((Expression)(numTerms.elementAt(1))).isNegOne())) {
				    if (((Expression)(numTerms.elementAt(1))).isOne()) {
				    	Expression firstNumTerm = (Expression)(numTerms.elementAt(0));
				    	Expression firstDenTerm = (Expression)(denTerms.elementAt(0));
				    	return firstNumTerm.toASCII(openParen,closeParen)+"/"+firstDenTerm.toASCII(openParen,closeParen);
				    }
				    else {
				    	Expression firstNumTerm = (Expression)(numTerms.elementAt(0));
				    	Expression firstDenTerm = (Expression)(denTerms.elementAt(0));
				    	return "-"+firstNumTerm.toASCII(openParen,closeParen)+"/"+firstDenTerm.toASCII(openParen,closeParen);
				    }
		}
		else {
			//if numerator ends in an exponent, put parens around it, so 2x^2/3 will output as (2x^2)/3 [and exponent won't be interpreted as 2/3]
			//if denominator is complex, put parens around it [probably could limit parens to denominators with ExponentExpressions and/or PolyExpressions...]
			if (numTerms.elementAt(numTerms.size()-1) instanceof ExponentExpression &&
				denTerms.size() > 1)
				return openParen+termString(numTerms,openParen,closeParen)+closeParen+"/"+openParen+termString(denTerms,openParen,closeParen)+closeParen;
			else if (numTerms.elementAt(numTerms.size()-1) instanceof ExponentExpression)
				return openParen+termString(numTerms,openParen,closeParen)+closeParen+"/"+termString(denTerms,openParen,closeParen);
			else if (denTerms.size() > 1)
				return termString(numTerms,openParen,closeParen)+"/"+openParen+termString(denTerms,openParen,closeParen)+closeParen;
			else
				return termString(numTerms,openParen,closeParen)+"/"+termString(denTerms,openParen,closeParen);
		}
	}*/
	
	public String toASCII(String openParen, String closeParen) {
		return termString(subterms,openParen,closeParen);
	}

	//note -- this MathML rendering is significantly different from the ascii toString rendering...
	public String toMathML() {
		StringBuffer finalString = new StringBuffer(mathmlSBsize);
		finalString.append("<mrow> ");
		int start=0;
		int subsize = numSubs;
		int finalParenCount = 0;
		boolean lastWasFenced = false;
		Expression exp1;
		for (int i=start;i<subsize;++i) {
			exp1 = getTerm(i);
			if (exp1 instanceof PolyExpression){
				finalString.append("<mfenced>").append(exp1.toMathML()).append("</mfenced>");
				lastWasFenced = true;
			}
			else{
				if (i > start && startsWithNumber(exp1) &&
					(getTerm(i-1) instanceof NumericExpression ||
					 getTerm(i-1) instanceof VariableExpression ||
					 (getTerm(i-1) instanceof NegatedExpression  &&
					  ((NegatedExpression)getTerm(i-1)).getTerm(1) instanceof NumericExpression) ||
					 lastWasFenced ||
					 (exp1.isNegative() && !(exp1 instanceof FencedExpression))))
					finalString.append("<mo>&cdot;</mo>").append(exp1.toMathML());
				else if (i<subsize-1 && exp1 instanceof NumberExpression &&
						 exp1.isOne() && subterms[i+1] instanceof VariableExpression) 
					;//don't print "1" before a variable
				// Olga
				else if (i > start && (exp1 instanceof RatioExpression ||  exp1 instanceof FractionExpression) && 
						 (getTerm(i-1) instanceof RatioExpression || getTerm(i-1) instanceof FractionExpression))
					finalString.append("<mo>&cdot;</mo>").append(exp1.toMathML());
				// end Olga
				else
					finalString.append(exp1.toMathML()).append(" ");
				//add &InvisibleTimes?
				lastWasFenced = exp1 instanceof FencedExpression;
			}
		}
		for(int i=0;i<finalParenCount;i++){
			finalString.append("</mfenced>");
		}

		return addMathMLAttributes(finalString.append("</mrow>").toString());
	}
	
	public String debugForm() {
		StringBuffer finalString = new StringBuffer(asciiSBsize);
		finalString.append("[TermExp(").append(numSubs).append("): ");
		for (int i=0;i<numSubs;++i) {
			Expression exp1 = subterms[i];
			finalString.append(" ").append((i+1)).append(":").append(exp1.debugForm());
		}
		finalString.append("]");
		return finalString.toString();
	}
	
	//TermExpressions sort after numbers and smaller TermExpressions
	//but before everything else
	public boolean termSortBefore(Expression ex) {
		/*trace.out(debugForm() + ".termSortBefore(" +
		  ex.debugForm() + ")");*/
		if (ex instanceof TermExpression) {
			TermExpression tEx = (TermExpression)ex;
			if (numSubTerms() < tEx.numSubTerms()){
				//trace.out("\ttrue");
				return true;
			}
			else if (numSubTerms() > tEx.numSubTerms()){
				//trace.out("\tfalse");
				return false;
			}
			else{
				//trace.out("\tchecking subterms (" + numSubTerms() + ")");
				if (numSubTerms() > 1){
					TermExpression thisSort = (TermExpression)sort();
					TermExpression tExSort = (TermExpression)tEx.sort();
					int i;
					for(i=0;i<thisSort.numSubTerms() &&
							thisSort.getTerm(i).exactEqual(tExSort.getTerm(i));i++);
					if(i < thisSort.numSubTerms()){
						return thisSort.getTerm(i).termSortBefore(tExSort.getTerm(i));
					}
					else{
						//terms are identical ...
						return false;
					}
				}
				else //this should check the coefficient as well...
					return exceptSimplifiedCoefficient().termSortBefore(tEx.exceptSimplifiedCoefficient());
			}
		}
		else if (ex instanceof NumericExpression ||
				 ex instanceof VariableExpression){
			//trace.out("\tfalse");
			return false;
		}
		else{
			//trace.out("\ttrue (default)");
			return true;
		}
	}
	
	//standardizing a TermExpression means to distribute, simplify and sort it
	public Expression standardizeWhole(int type) {
            //trace.out("TermExpression.standardizeWhole(): " + debugForm());
		Expression simpEx = this.distributeWhole(type);
                //trace.out("TermExpression.standardizeWhole(): ck 1");
		simpEx = simpEx.simplify();
                //trace.out("TermExpression.standardizeWhole(): ck 2");
		if (simpEx instanceof TermExpression) {
                    //trace.out("TermExpression.standardizeWhole(): ck 3");
			TermExpression tEx = (TermExpression)simpEx;
			return tEx.sortTerm();
		}
		else{
                    //trace.out("TermExpression.standardizeWhole(): ck 4");
			return simpEx.standardize(type);
                }
	}
	
	//since both 2x/3 and 2/3*x are "standard" forms, we need to merge all numbers
	//so 2/3*x [not 2*x*1/3] is the cannonical form
	public Expression initialCannonicalize() {
            //trace.out("TE.initialCannonicalize: " + debugForm());
		Vector nonNumbers = new Vector();
		Expression numbers = getCombinedCoefficient(nonNumbers,true);
                //trace.out("TE.initialCannonicalize: numbers = " + numbers.debugForm());
                //trace.out("TE.initialCannonicalize: nunNumbers = " + nonNumbers);
		nonNumbers.insertElementAt(numbers,0);
		Expression ret = uncleanBuildFromComponents(nonNumbers);

		nonNumbers.removeAllElements();
		nonNumbers = null;
		return ret;
	}	
	
	//getCombinedCoefficient returns a numeric that is the product of all numbers in the term
	//There is one important special case: 3x/4 is represented as subterms [3,x,1/4] but 3/4x is represented as [3/4,x]
	//When multiplying through, we want to preserve their original forms (though when cannonicalizing, we don't), so
	//the caller has the option of combining a "separated fraction" or not
	// --NOTE: this "separated fraction" stuff is now superseeded by RatioExpression and can probably be removed--
	private NumericExpression getCombinedCoefficient(Vector nonNumericTerms,boolean combineSeparatedFraction) {
		NumericExpression coefficient = new NumberExpression(1);
		int separatedFractionState=0; //0-start, 1-found integer numerator, 2-found something after integer numerator, 3-found 1/n, 4-off path
		int sepFractionCoeffPlace=0;
		for (int i=0;i<numSubTerms();++i) {
			Expression thisTerm = getTerm(i).unfence();
                        //trace.out("TE.gCC: processing term: " + thisTerm.debugForm());
			if (thisTerm instanceof NumericExpression) {
				//NumericExpression numTerm = (NumericExpression)(getTerm(i));
				NumericExpression numTerm = (NumericExpression)(thisTerm);
				if (numTerm.denominator().isOne()) {
					coefficient = coefficient.numMultiply(numTerm);
					if (numTerm.isIntegerType()) {
						if (separatedFractionState == 0) {
							separatedFractionState = 1;
							sepFractionCoeffPlace = i;
						}
						else
							separatedFractionState = 4;
					}
				}
				else {
					NumberExpression numerate = (NumberExpression)(numTerm.numerator());
					NumberExpression denominate = (NumberExpression)(numTerm.denominator());
					NumberExpression coeffTop = (NumberExpression)(coefficient.numerator());
					NumberExpression coeffBottom = (NumberExpression)(coefficient.denominator());
					coefficient = new FractionExpression((NumberExpression)(coeffTop.numMultiply(numerate)),
													  (NumberExpression)(coeffBottom.numMultiply(denominate)));
					if (separatedFractionState == 2 && numerate.isOne())
						separatedFractionState = 3;
					else
						separatedFractionState = 4;
				}
                                //trace.out("TE.gCC: processed numeric term; coefficient == " + coefficient.debugForm());
			}
			else if (thisTerm instanceof TermExpression || thisTerm instanceof RatioExpression ||
					thisTerm instanceof ExponentExpression) {
				//trace.out("GCC: term "+getTerm(i).toString()+ " is termExp");
				NumericExpression coeffPart = thisTerm.numericSimplifiedCoefficient();
                                //trace.out("TE.gCC: numeric coefficient is " + coeffPart.debugForm());
				coefficient = coefficient.numMultiply(coeffPart);
				if(thisTerm.exceptSimplifiedCoefficient() != null){
					nonNumericTerms.addElement(thisTerm.exceptSimplifiedCoefficient());
				}
				if (separatedFractionState == 1 && coeffPart.isOne())
					separatedFractionState = 2;
				else if (!coeffPart.isOne())
					separatedFractionState = 4;
                                //trace.out("TE.gCC: processed special term; coefficient == " + coefficient.debugForm());
			}
			else {
//				trace.out("term "+getTerm(i).toString()+ " is other: "+getTerm(i).expressionType());
				nonNumericTerms.addElement(thisTerm);
				if (separatedFractionState == 1)
					separatedFractionState = 2;
                                //trace.out("TE.gCC: processed non-numeric term; coefficient == " + coefficient.debugForm());
			}
		}
		//If we found a separated fraction, and we're excluding them from the coefficient, return the whole number
		//as the coefficient and everything else as the non-numerics
		if (!combineSeparatedFraction && separatedFractionState == 3) {
			nonNumericTerms.removeAllElements();
			NumericExpression result=null;
			for (int i=0;i<numSubTerms();++i) {
				if (i == sepFractionCoeffPlace)
					result = (NumericExpression)getTerm(i);
				else
					nonNumericTerms.addElement(getTerm(i));
			}
			return result;
		}
		else
			return coefficient;
	}



//	public Vector variablesUsed() {
//		Vector vars = new Vector();
//		Vector subvars;
//		for (int i=0;i<numSubTerms();++i) {
//			Expression subterm = getTerm(i);
//			subvars = subterm.variablesUsed();
//			for (int subitem=0;subitem<subvars.size();++subitem) {
//				String thisItem = (String)subvars.elementAt(subitem);
//				boolean found=vars.contains(thisItem);
//				if (!found)
//					vars.addElement(thisItem);
//			}
//		}
//		return vars;
//	}
		
	
	public Expression sortTermWhole(){
		Vector sTerms = getExplicitFactors(false);
		int negCount = 0;
		for(int i=0;i<sTerms.size();i++){
			if(sTerms.elementAt(i) instanceof NegatedExpression ||
			   (sTerms.elementAt(i) instanceof NumericExpression &&
				((Expression)sTerms.elementAt(i)).isNegative() &&
				!((Expression)sTerms.elementAt(i)).isNegOne())){
				sTerms.setElementAt(((Expression)sTerms.elementAt(i)).negate(),i);
				negCount++;
			}
		}
		sTerms = sortVector(sTerms);
		Expression ret = new TermExpression(sTerms);
		for(int i=0;i<negCount;i++){
			ret = ret.softNegate();
		}

		sTerms.removeAllElements();
		sTerms = null;
		return ret;
	}

	/*public Expression sortTermWhole() {
	  trace.out("TE.sTW: " + debugForm());
	  int numNegCount = 0;
	  int denNegCount = 0;
	  Vector numTerms = numeratorTerms();
	  Vector denTerms = denominatorTerms();
	  if (numTerms.size() > 0){
	  for(int i=0;i<numTerms.size();i++){
	  if(((Expression)numTerms.elementAt(i)).isNegative()){
	  numTerms.setElementAt(((Expression)numTerms.elementAt(i)).negate(),i);
	  numNegCount++;
	  }
	  }
	  numTerms = Expression.sortVector(numTerms);
	  }
	  if (denTerms.size() > 0){
	  for(int i=0;i<denTerms.size();i++){
	  if(((Expression)denTerms.elementAt(i)).isNegative()){
	  denTerms.setElementAt(((Expression)denTerms.elementAt(i)).negate(),i);
	  denNegCount++;
	  }
	  }
	  denTerms = Expression.sortVector(denTerms);
	  }
	  int negCount = Math.abs(numNegCount - denNegCount);
	  //append denTerms to numTerms
	  for (int i=0;i<denTerms.size();++i) {
	  Expression denEx = (Expression)(denTerms.elementAt(i));
	  numTerms.addElement(denEx.reciprocal());
	  }
	  Expression ret = uncleanBuildFromComponents(numTerms);
	  for(int i=0;i<negCount;i++){
	  ret = ret.softNegate();
	  }

	  numTerms.removeAllElements();
	  numTerms = null;
	  denTerms.removeAllElements();
	  denTerms = null;

	  trace.out("TE.sTW: returning: " + ret.debugForm());
	  return ret;
	  }*/
	
	//multipliableTerms returns a vector of terms that can be multiplied with the given one
	//The resulting vector will NOT include the original (comparison) term
	private Vector multipliableTerms(Expression comparison,Vector otherTerms) {
		Vector result = new Vector();
		Expression compBody = comparison.exceptExponent();
		for (int i=0;i<otherTerms.size();++i) {
			Expression thisTerm = (Expression)otherTerms.elementAt(i);
			Expression thisBody = thisTerm.exceptExponent();
			if (compBody.exactEqual(thisBody)) {
				result.addElement(thisTerm);
			}
		}
		return result;
	}


	//multiplyThroughWhole combines terms that are multiplied together
	public Expression multiplyThroughWhole () {
		/*level++;
		  trace.out("TE.mTW: " + debugForm());
		  if(level > 100){
		  (new Exception()).printStackTrace();
		  }*/
		
		TermExpression newExp = this;
		boolean didMultiply=false;

		//collect all numeric terms
		Vector nonNumericTerms = new Vector();
		NumericExpression coefficient = getCombinedCoefficient(nonNumericTerms,true);
		//trace.out("TE.mTW: Combined coeff is "+coefficient+" nonNum: "+nonNumericTerms);
		//if the coefficient combines two numbers, then this simplification did something
		//if (nonNumericTerms.size() != numSubs-1)
		/*trace.out("TE.mTW: comparing: " + unsimplifiedCoefficient().debugForm());
		  trace.out("                   " + coefficient.debugForm());*/
		if(!coefficient.exactEqual(unsimplifiedCoefficient()))
			didMultiply=true;
		else if (coefficient.isOne()) //we'll also do something if the coefficient is 1
			didMultiply=true;
		else if (coefficient.isZero()) //or if its 0
			didMultiply=true;
		/*else if (!coefficient.isOne() && !(getTerm(0) instanceof NumericExpression)) //if the first subterm is not a number, we'll MT (e.g. x*2 MTs to 2x)
		  didMultiply=true;*/
		//trace.out("TE.mTW: didMultiply: "+didMultiply);
		//Next, combine all multipliable terms
		Expression firstTerm;
		Vector termsToMultiply=null;
		Vector combinedTerms = new Vector();
		//trace.out("TermExpression.multiplyThroughWhole(): begin while loop");
		//trace.out("TE.mTW: nonNumericTerms: " + nonNumericTerms);
		while (nonNumericTerms.size() > 0) {
			firstTerm = (Expression)(nonNumericTerms.elementAt(0));
			nonNumericTerms.removeElement(firstTerm);
			termsToMultiply = newExp.multipliableTerms(firstTerm,nonNumericTerms);
			if (termsToMultiply.size() > 0)
				didMultiply=true;
			Expression combinedExponent = firstTerm.getExponent();
			for (int i=0;i<termsToMultiply.size();++i) {
				Expression exponent = ((Expression)(termsToMultiply.elementAt(i))).getExponent();
				combinedExponent = combinedExponent.add(exponent);
			}
			if (combinedExponent.combineLikeTerms().isOne())
				combinedTerms.addElement(firstTerm.exceptExponent());
			else if (!combinedExponent.combineLikeTerms().isZero()) { 
				combinedTerms.addElement(new ExponentExpression(firstTerm.exceptExponent(),combinedExponent));
			}
			//else if combinedExponent.getValue() == 0; do nothing
			for (int i=0;i<termsToMultiply.size();++i)
				nonNumericTerms.removeElement(termsToMultiply.elementAt(i));
			termsToMultiply.removeAllElements();
			termsToMultiply = null;
		}
		//trace.out("TE.mTW: " + debugForm() + ": combined terms: "+combinedTerms);
		
		Expression finalExp;
		
		if (combinedTerms.size() == 0)
			finalExp = coefficient;
		else if (coefficient.isZero())
			finalExp = new NumberExpression(0);
		else if (coefficient.isOne()) {
			if (combinedTerms.size() == 1)
				finalExp = (Expression)(combinedTerms.elementAt(0));
			else
				finalExp = uncleanBuildFromComponents(combinedTerms);
		}
		else if(coefficient.isNegOne() &&
				combinedTerms.size() == 1){
			didMultiply = true;
			finalExp = new NegatedExpression((Expression)combinedTerms.elementAt(0));
		}
		else {
			if(coefficient.isNegOne()){
				//if we've just got -1 out front, it should print as -x, not -1x
				didMultiply = true;
				combinedTerms.setElementAt(new NegatedExpression((Expression)combinedTerms.elementAt(0)),0);
			}
			else{
				combinedTerms.insertElementAt(coefficient,0); //start with the coefficient
			}
			finalExp = uncleanBuildFromComponents(combinedTerms);
		}
		//finally, combine numerator terms and denominator terms to get a RatioExpression
		//(e.g. 1/2*x/5 --> [1*x]/[2*5]
		//We do this if there is at least one RatioExpression
		// (since we want to combine 3 * x/y but not 1/2 * x * y)
		//or if there is a FractionExpression that is not in the first place
		// (so term:[4,x,1/2] is converted to ratio:[4x,2])
		if (finalExp instanceof TermExpression) {
			//trace.out("TE.mTW: checking for ratio: " + finalExp.debugForm());
			boolean hasRatio=false;
			for (int i=0;i<((TermExpression)finalExp).numSubTerms()&&!hasRatio;++i) {
				if (((TermExpression)finalExp).getTerm(i) instanceof RatioExpression)
					hasRatio=true;
				else if (((TermExpression)finalExp).getTerm(i) instanceof FractionExpression)
					hasRatio=true;
			}

			if (hasRatio)
				finalExp = ((TermExpression)finalExp).toRatioExpression();
			if (finalExp instanceof RatioExpression) //if so, toRatioExpression did something...
				didMultiply = true;
			//trace.out(this+" : finalExp = "+finalExp);
		}
		Expression ret = finalExp.cleanExpression();
		/*trace.out("TE.mTW[" + level + "]: returning: " + ret.debugForm());
		  level--;*/

		nonNumericTerms.removeAllElements();
		nonNumericTerms = null;
		combinedTerms.removeAllElements();
		combinedTerms = null;

		/*comparing the number of subterms here catches eliminating 1
          in cases like 3*1*/
		if(ret instanceof TermExpression){
			if(((TermExpression)ret).numSubTerms() != numSubTerms()){
				didMultiply = true;
			}
		}

		if(!didMultiply && ret instanceof TermExpression){
			/*trace.out("TE.mTW: just sorted: " + ret.debugForm());
			  trace.out("                     " + ret.sort().debugForm());
			  trace.out("                     " + debugForm());*/
			return this;
		}

		return ret;
	}
	
	//this one's tricky, so we'll do it the easy way
	public boolean canMultiplyThroughWhole() {
		return !(debugForm().equals(multiplyThrough().debugForm()));
	}
	
	//toRatioExpression turns the TermExpression into a RatioExpression (if it is appropriate)
	private Expression toRatioExpression() {
		//trace.out("TE.tRE: " + debugForm());
		Expression ret;
		Vector numTerms = numeratorTerms(true);
		Vector denTerms = denominatorTerms(false);
		if (numSubTerms() > 1 && numTerms.size() > 0 && denTerms.size() > 0)
			ret = new RatioExpression(buildFromComponents(numTerms),buildFromComponents(denTerms));
		else
			ret = this;

		numTerms.removeAllElements();
		numTerms = null;
		denTerms.removeAllElements();
		denTerms = null;

		//trace.out("TE.tRE: returning: " + ret.debugForm());
		return ret;
	}
	
	//Two termExpressions are exactEqual if their subterms are exactEqual (order counts)
	public boolean exactEqual(Expression ex) {
		if (ex instanceof TermExpression) {
			boolean same=true;
			TermExpression tEx = (TermExpression)ex;
			if (numSubTerms() != tEx.numSubTerms())
				same = false;
			int thisTerm = 0;
			while (thisTerm < numSubTerms() && same) {
				if (!getTerm(thisTerm).exactEqual(tEx.getTerm(thisTerm)))
					same = false;
				thisTerm++;
			}
			return same;
		}
		else
			return false;
	}

	public Expression factor() {
		Expression ret;
		Vector numT = numeratorTerms();
		Vector denT = denominatorTerms();
		boolean didFactor = false;
		for (int i=0;i<numT.size();++i) {
			Expression thisTerm = ((Expression)(numT.elementAt(i))).unfence();
			if (thisTerm.canFactor()) {
				numT.setElementAt(thisTerm.factor(),i);
				didFactor = true;
			}
		}
		for (int i=0;i<denT.size();++i) {
			Expression thisTerm = (Expression)(denT.elementAt(i));
			if (thisTerm.canFactor()) {
				denT.setElementAt(thisTerm.factor(),i);
				didFactor = true;
			}
		}
		if (didFactor)
			ret = new TermExpression(numT,denT);
		else
			ret = this;

		numT.removeAllElements();
		numT = null;
		denT.removeAllElements();
		denT = null;

		return ret;
	}
	
	public Expression factor(Expression fact) {
		Expression ret;
		Vector numT = numeratorTerms();
		Vector denT = denominatorTerms();
		boolean didFactor = false;
		for (int i=0;i<numT.size();++i) {
			Expression thisTerm = ((Expression)(numT.elementAt(i))).unfence();
			if (thisTerm.canFactor(fact)) {
				numT.setElementAt(thisTerm.factor(fact),i);
				didFactor = true;
			}
		}
		for (int i=0;i<denT.size();++i) {
			Expression thisTerm = (Expression)(denT.elementAt(i));
			if (thisTerm.canFactor(fact)) {
				denT.setElementAt(thisTerm.factor(fact),i);
				didFactor = true;
			}
		}
		if (didFactor)
			ret = new TermExpression(numT,denT);
		else
			ret = this;

		numT.removeAllElements();
		numT = null;
		denT.removeAllElements();
		denT = null;

		return ret;
	}
	
	public Expression factorPiecemeal(Expression fact){
		Expression ret;
		Vector numT = numeratorTerms();
		Vector denT = denominatorTerms();
		boolean didFactor = false;
		for (int i=0;i<numT.size();++i) {
			Expression thisTerm = ((Expression)(numT.elementAt(i))).unfence();
			if (thisTerm.canFactorPiecemeal(fact)) {
				numT.setElementAt(thisTerm.factorPiecemeal(fact),i);
				didFactor = true;
			}
		}
		for (int i=0;i<denT.size();++i) {
			Expression thisTerm = (Expression)(denT.elementAt(i));
			if (thisTerm.canFactorPiecemeal(fact)) {
				denT.setElementAt(thisTerm.factorPiecemeal(fact),i);
				didFactor = true;
			}
		}
		if (didFactor)
			ret = new TermExpression(numT,denT);
		else
			ret = this;

		numT.removeAllElements();
		numT = null;
		denT.removeAllElements();
		denT = null;

		return ret;
	}

	//a TermExpression's expansion includes the expansion of all its subterms
	//(so 2y^3--> 2,y,y,y)
	protected Vector getExpandedForm() {
		Vector vec = new Vector();
		for (int i=0;i<numSubs;++i) {
			Expression thissub = getTerm(i);
			Vector subExp = thissub.getExpandedForm();
			for (int j=0;j<subExp.size();++j)
				vec.addElement(subExp.elementAt(j));
		}
		return vec;
	}
	
	protected Vector getExplicitFactors(boolean expandNegated) {
		if(expandNegated){
			Vector ret = new Vector(numSubs);
			for(int i=0;i<numSubs;i++){
				if(subterms[i] instanceof NegatedExpression){
					ret.addElement(((TermExpression)subterms[i]).getTerm(0));
					ret.addElement(((TermExpression)subterms[i]).getTerm(1));
				}
				else{
					ret.addElement(subterms[i]);
				}
			}

			return ret;
		}
		else{
			Vector ret = new Vector(numSubs);
			for (int i=0;i<numSubs;++i)
				ret.addElement(subterms[i]);
			return ret;
		}
	}
	
	public boolean canFactor() {
		Vector numT = numeratorTerms();
		Vector denT = denominatorTerms();
		boolean willFactor = false;
		for (int i=0;i<numT.size();++i) {
			Expression thisTerm = ((Expression)(numT.elementAt(i))).unfence();
			if (thisTerm.canFactor()) {
				willFactor = true;
			}
		}
		for (int i=0;i<denT.size();++i) {
			Expression thisTerm = (Expression)(denT.elementAt(i));
			if (thisTerm.canFactor()) {
				willFactor = true;
			}
		}

		numT.removeAllElements();
		numT = null;
		denT.removeAllElements();
		denT = null;

		return willFactor;
	}
		
	public boolean canFactor(Expression fact) {
		Vector numT = numeratorTerms();
		Vector denT = denominatorTerms();
		boolean willFactor = false;
		for (int i=0;i<numT.size();++i) {
			Expression thisTerm = ((Expression)(numT.elementAt(i))).unfence();
			if (thisTerm.canFactor(fact)) {
				willFactor = true;
			}
		}
		for (int i=0;i<denT.size();++i) {
			Expression thisTerm = (Expression)(denT.elementAt(i));
			if (thisTerm.canFactor(fact)) {
				willFactor = true;
			}
		}

		numT.removeAllElements();
		numT = null;
		denT.removeAllElements();
		denT = null;

		return willFactor;
	}
		
	public boolean canFactorPiecemeal(Expression fact) {
		Vector numT = numeratorTerms();
		Vector denT = denominatorTerms();
		boolean willFactor = false;
		for (int i=0;i<numT.size();++i) {
			Expression thisTerm = ((Expression)(numT.elementAt(i))).unfence();
			if (thisTerm.canFactorPiecemeal(fact)) {
				willFactor = true;
			}
		}
		for (int i=0;i<denT.size();++i) {
			Expression thisTerm = (Expression)(denT.elementAt(i));
			if (thisTerm.canFactorPiecemeal(fact)) {
				willFactor = true;
			}
		}

		numT.removeAllElements();
		numT = null;
		denT.removeAllElements();
		denT = null;

		return willFactor;
	}
		
//	public Expression substitute(String var,Expression newExp) {
//		Vector newTerms = new Vector();
//		for (int i=0;i<numSubs;++i) {
//			Expression thisExp = (Expression)(subterms[i]);
//			newTerms.addElement(thisExp.substitute(var,newExp));
//		}
//		return new TermExpression(newTerms);
//	}
	
	//cleanExpression changes terms with just one subterm into
	//the subterm
	public Expression cleanExpression() {
		Expression ret;
		//trace.out("TE.cE: "+debugForm());
		/*if (numericSimplifiedCoefficient().isZero()) {
		  return new NumberExpression(0);
		  }
		  else {*/
			Vector outterms = new Vector();
			for (int i=0;i<numSubs;++i) {
				Expression cleanTerm = (subterms[i]).cleanExpression();
				if (!cleanTerm.isEmpty())
					outterms.addElement(cleanTerm);
			}
			if (outterms.size() == 1)
				ret = (Expression)(outterms.elementAt(0));
			else
				//mmmBUG: should this be just buildFromComponents?
				ret = uncleanBuildFromComponents(outterms);
			//}

			outterms.removeAllElements();
			outterms = null;

			return ret;
	}
	
	//flatten removes any nested TermExpressions
	private TermExpression flatten() {
		Vector outterms = new Vector();
		for (int i=0;i<numSubs;++i) {
			if (subterms[i] instanceof TermExpression &&
				!(subterms[i] instanceof NegatedExpression)) {
				TermExpression inside = ((TermExpression)(subterms[i])).flatten();
				for (int j=0;j<inside.numSubTerms();++j)
					outterms.addElement(inside.getTerm(j));
			}
			else
				outterms.addElement(subterms[i]);
		}
		TermExpression ret = uncleanBuildFromComponents(outterms);

		outterms.removeAllElements();
		outterms = null;

		return ret;
	}
	
	public double degree() {
		double highDegree = -99999.0;
		
		for (int i=0;i<numSubs;++i) {
			if (getTerm(i).degree() > highDegree)
				highDegree = getTerm(i).degree();
		}
		return highDegree;
	}
	
	public boolean isEmpty() {
		if (numSubs == 0)
			return true;
		else
			return false;
	}
	
	//isAx returns true if the TermExpression is just a number and a variable (like 3x)
	public boolean isAx() {
		return (numSubTerms()==2 && getTerm(0) instanceof NumberExpression && getTerm(1) instanceof VariableExpression);
	}
	
	public Expression distributeWhole(int type) {
		Expression ret;

		//determine terms to distribute
		Vector distTerms;
		Vector denomTerms = new Vector();
		if ((type & DISTDEN) != 0) {
			distTerms = new Vector();
			for (int i=0;i<numSubs;++i)
				distTerms.addElement(subterms[i]);
		}
		else { //exclude terms in the denominator
			distTerms = new Vector();
			for (int i=0;i<numSubTerms();++i) {
				Expression thisTerm = getTerm(i).unfence(); //Distribution ignores fenced expressions
				if (thisTerm instanceof FractionExpression) {
					Expression numer = ((FractionExpression)thisTerm).numerator();
					if (!numer.isOne())
						distTerms.addElement(numer);
					denomTerms.addElement(((FractionExpression)thisTerm).denominator().reciprocal());
				}
				else if (thisTerm instanceof ExponentExpression && ((ExponentExpression)thisTerm).getExponent().isNegative()) {
					denomTerms.addElement(thisTerm);
				}
				else
					distTerms.addElement(thisTerm);
			}
		}
		//Find a polynomial to distribute across
		PolyExpression firstPoly = null;
		int firstPolyPos = -1;
		
		for (int i=0;i<distTerms.size()&&firstPolyPos==-1;++i) {
			Expression subTerm = ((Expression)(distTerms.elementAt(i))).unfence();
			if (subTerm instanceof PolyExpression) {
				firstPolyPos = i;
				firstPoly = (PolyExpression)subTerm;
			}
		}
		
		//found a polynomial, now distribute across it
		if (firstPolyPos != -1) {
			Vector outExpression = new Vector();
			for (int i=0;i<firstPoly.numberOfTerms();++i) { //for each term in the poly...
				Vector termSubTerms = new Vector();
				for (int j=0;j<distTerms.size();++j) { //for each term to distribute across
					if (j == firstPolyPos) //make sure the term from the polynomial maintains the same order relative to other terms
						termSubTerms.addElement(firstPoly.getTermAt(i));
					else
						termSubTerms.addElement(distTerms.elementAt(j));
				}
//				trace.out("item "+i+" in distributed term is "+termSubTerms);
				outExpression.addElement(uncleanBuildFromComponents(termSubTerms));
				termSubTerms.removeAllElements();
				termSubTerms = null;
			}
			PolyExpression newPoly = new PolyExpression(outExpression);
			Expression result = newPoly.distribute(type);
			//if we didn't distribute denominator terms, add them back in
			if (((type & DISTDEN) == 0) && denomTerms.size() > 0) {
				denomTerms.insertElementAt(result,0); //add the polynomial at the beginning
				ret = uncleanBuildFromComponents(denomTerms);
			}
			else
				ret = result;
			outExpression.removeAllElements();
			outExpression = null;
		}
		else
			ret = this;

		/*if distributeDenominator, then distTerms points directly to
          this TermExpression's subterms vector, and we don't want to
          empty that*/
		if(distTerms != null && ((type & DISTDEN) == 0)){
			distTerms.removeAllElements();
		}
		distTerms = null;
		denomTerms.removeAllElements();
		denomTerms = null;

		return ret;
	}
	
	public boolean canDistributeWhole(int type) {
		boolean canD = false;
		
		if (((type & DISTDEN) == 0) && denominatorTerms().size() > 0)
			canD = false;
		else if (numSubTerms()>1) {
			for (int i=0;i<numSubTerms()&&canD==false;++i) {
				if (getTerm(i) instanceof PolyExpression)
					canD = true;
				else if ((getTerm(i) instanceof FencedExpression) &&
						 (((FencedExpression)getTerm(i)).getFenceDeepContents() instanceof PolyExpression))
					canD = true;
			}
		}
		return canD;
	}
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("numerator")) {
			if (denominatorTerms().size() == 0)
				return this;
			else
				return uncleanBuildFromComponents(numeratorTerms());
		}
		else if (prop.equalsIgnoreCase("denominator")) {
			if (denominatorTerms().size() == 0) {
				return (new NumberExpression(1));
			}
			else {
				return uncleanBuildFromComponents(denominatorTerms());
			}
		}
		else if (prop.equalsIgnoreCase("factors")) {
			return new ArrayQuery(subterms,numSubs);
		}
		else if(prop.equalsIgnoreCase("constant factor")){
			if(variablesUsed().size() == 0){
				return this;
			}
			else{
				Expression ret = null;
				ExpressionArray comp = getComponentArray();
				for(int i=0;i<comp.size();i++){
					if((comp.expressionAt(i)).variablesUsed().size() == 0){
						if(ret == null){
							ret = comp.expressionAt(i);
						}
						else{
							ret = ret.multiply(comp.expressionAt(i));
						}
					}
				}
				if(ret == null){
					throw new NoSuchFieldException("No constant factor in " + this);
				}
				ExpressionArray.deallocate(comp);
				return ret;
			}
		}
		else if (prop.equalsIgnoreCase("numerator terms")) {
			return new ArrayQuery(numeratorTerms());
		}
		else if (prop.equalsIgnoreCase("denominator terms")) {
			return new ArrayQuery(denominatorTerms());
		}
		//exponent is sort-of strange. If the term is 3x^2, the exponent is 2, even though that's not really
		//the exponent of the term (just of the last subterm in the term).
		else if (prop.equalsIgnoreCase("exponent")) {
			double expValue = 1.0;
			Expression exp = new NumberExpression(1);
			for (int i=0;i<numSubTerms();++i) {
				Expression thisExp = getTerm(i).getExponent();
				if (thisExp instanceof NumericExpression) {
					double expNum = ((NumericExpression)thisExp).doubleValue();
					if (expNum > expValue) {
						expValue = expNum;
						exp = thisExp;
					}
				}
			}
			return exp;
		}
		else if (prop.length() > 15 && prop.substring(0,15).equalsIgnoreCase("factor matching")) {
			Expression found = null;
			try {
				Equation matchForm = Equation.makeForm(prop.substring(16));
				ExpressionArray comp = getComponentArray();
				for (int i=0;i<comp.size() && found==null;++i) {
					Expression thisEx = comp.expressionAt(i);
					Equation thisExInfo = new Equation(thisEx,null);
					if (matchForm.patternMatches(thisExInfo)){
						found = thisEx;
					}
				}
				ExpressionArray.deallocate(comp);
			}
			catch (BadExpressionError err) {
				System.out.println("bad expression in factor matching: "+prop.substring(16));
			}
			if (found == null){
				throw new NoSuchFieldException("No factor matching "+prop.substring(16)+" in "+this);
			}
			else{
				return found;
			}
		}
		else
			return super.getProperty(prop);
	}
							
}
