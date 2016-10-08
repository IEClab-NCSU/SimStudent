package edu.cmu.old_pact.cmu.sm;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.BooleanQuery;
import edu.cmu.old_pact.cmu.sm.query.NumberQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.cmu.sm.query.StandardMethods;
import edu.cmu.old_pact.cmu.sm.query.StringQuery;

//The abstract "expression" class

public abstract class Expression  implements Cloneable, Queryable, Serializable {

	private Vector displayAttributes = null; //attributes relating to the expression as a whole
	protected Hashtable partAttributes = null;    //attributes relating to some subpart of the expression (subclass-specific)
	
	//initial size is prime, and will continue to be prime for a while
	//under the default growth factor (2n+1).  This size is pretty
	//reasonable for the way the SM is used in the solver; bigger
	//would make the test suite perform better, and smaller would
	//probably work okay in non-solver lessons.
	private static Hashtable simplificationCache = new Hashtable(89);
	private static Hashtable standardizationCache = new Hashtable(89);
	protected static boolean printStruct = false;
	private boolean encapsulateVar = false;

	//see comment in SymbolManipulator.java for details about this ...
	private static boolean maintainVars = false;

	/*initial sizes for the StringBuffers used by toASCII() and
	  toMathML() (mathml strings tend to be longer, so I'm guessing
	  maybe it would be useful to have different values for these at
	  some point ... ?)*/
	protected static final int asciiSBsize = 64;
	protected static final int mathmlSBsize = asciiSBsize;

	protected static final int DISTNUM =  1;
	protected static final int DISTDEN =  2;
	protected static final int DISTBOTH = DISTNUM | DISTDEN;

	//static int level = 0;

	public Expression() {
	}
	
	public void finalize() throws Throwable{
		try{
			if(displayAttributes != null){
				displayAttributes.removeAllElements();
			}
			displayAttributes = null;
			
			if(partAttributes != null){
				partAttributes.clear();
			}
			partAttributes = null;
		}
		finally{
			super.finalize();
		}
	}

	//allow anyone to clone me
	public Object clone() {
		try {
			return super.clone();
		}
		catch (CloneNotSupportedException e) {
			System.out.println("clone not supported in Expression");
			return null;
		}
	}
	
	///Add method
	//Add any expression
	public Expression add(Expression ex) {
		PolyExpression newEx = new PolyExpression(this,ex,1);
		return newEx;
	}
	
	///Subtract method
	//Subtract any expression
	public Expression subtract(Expression ex) {
		PolyExpression newEx = new PolyExpression(this,ex,0);
		return newEx;
	}

	///Multiply method
	//Multiply any expression
	public Expression multiply(Expression ex) {
		TermExpression newEx = new TermExpression(this,ex);
		Expression ret = newEx.cleanExpression();
		return ret;
	}
	public Expression divide(Expression ex) {
		return new RatioExpression(this,ex).cleanExpression();
	}
	
	public Expression power(Expression ex) {
		return new ExponentExpression(this,ex);
	}
	
	public Expression squareroot() {
		return new RadicalExpression(this,new FractionExpression(1,2));
	}
	
	public Expression root(Expression ex) {
		return new RadicalExpression(this,ex.reciprocal()).cleanExpression();
	}

	public Expression negate() {
		return NegatedExpression.negate(this);
	}

	/*this usually does the same thing as negate, unless we're already
      negative, in which case it preserves the double-negative.  Used
      by the parser to keep from removing double-negatives.*/
	public Expression softNegate(){
		return NegatedExpression.softNegate(this);
	}
	
	public double degree() {
		return 0.0;
	}
	
	public Expression absoluteValue() {
		if (isNegative())
			return this.negate();
		else
			return this;
	}
	
	public boolean isNegative() {
		return false;
	}
	
	//checking for 0,1,-1 is so common, we shortcut it here
	public boolean isZero() {
		return false;
	}
	
	public boolean isZeroSimplified() {
		return simplify().isZero();
	}

	
	public boolean isOne() {
		return false;
	}
	
	public boolean isOneSimplified() {
		return simplify().isOne();
	}

	public boolean isNegOne() {
		return false;
	}
	
	public boolean isNegOneSimplified() {
		return simplify().isNegOne();
	}

	public boolean isEmpty() {
		return false;
	}

	public Expression reciprocal() {
//		System.out.println("expression reciprocal: "+debugForm());
//		Expression recip = new ExponentExpression(this,-1);
		Expression recip = new RatioExpression(new NumberExpression(1),this);
		return recip;
	}
	
	public Expression numerator() {
		return this;
	}
	
	public Expression denominator() {
		return new NumberExpression(1);
	}
	
	//unfence removes any external parens
	public Expression unfence() {
		return this;
	}
	
	public final Expression removeImpliedFences() {
		Expression removed;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively remove implied fences
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.removeImpliedFences();
				components.setExpressionAt(thisComp,i);
			}
			removed = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			removed = this;
		return removed.removeImpliedFencesWhole();
	}
	
	protected Expression removeImpliedFencesWhole() {
		return this;
	}
		
	/*this is used by the parser to get rid of instances of
      FencedExpressions where the parens really are mathematically
      necessary*/
	public Expression removeRedundantFences(){
		Expression unfence;
		if(canHaveComponents()){
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively remove redundant fences
			for(int i=0;i<components.size();i++){
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.removeRedundantFences();
				components.setExpressionAt(thisComp,i);
			}
			unfence = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else{
			unfence = this;
		}
		return unfence.removeRedundantFencesWhole();
	}

	public Expression removeRedundantFencesWhole(){
		return this;
	}

	
	//canHaveComponents returns true for those types of expressions that can contain different components (like TermExpression, PolyExpression and ExponentExpression)
	public boolean canHaveComponents() {
		return this instanceof CompoundExpression;
	}
	
	//getComponents is used internally so that expressions with sub-components (TermExpression, PolyExpression, ExponentExpression)
	//can be handled the expression level, rather than having separate methods
	//By default, the only component is the expression itself
	protected Vector getComponents() {
		Vector comp = new Vector();
		comp.addElement(this);
		return comp;
	}
	
	protected ExpressionArray getComponentArray() {
		ExpressionArray compArray = ExpressionArray.allocate();
		compArray.addExpression(this);
		return compArray;
	}

	
	//getFullComponents is used to get components, without omitting any information that might be provided separately
	//in the componentInfo. In particular, a PolyExpression will return components that include a sign here
	//You should not call "buildFromComponents" on the results of getFullComponents, only on getComponents
	protected Vector getFullComponents() {
		return getComponents();
	}
	
	protected ExpressionArray getFullComponentArray() {
		return getComponentArray();
	}
	
	//Expression classes can override getComponentInfo to return any information they need in order to reassemble an
	//expression out of (possibly simplified) components. In particular, the PolyExpression class uses componentInfo
	//to store the signs of its components
	protected Object getComponentInfo() {
		return null;
	}
	
	//buildFromComponents returns an expression, given a vector of components
	//For expression types that don't have components (where this really shouldn't be called anyway),
	//we just take the first component. For component-based expression types, we build the expression in
	//a component-specific manner
	protected Expression buildFromComponents(Vector comp) {
		return (Expression)(comp.elementAt(0));
	}
	
	protected Expression buildFromComponents(ExpressionArray comp) {
		return comp.expressionAt(0);
	}
	
	protected Expression buildFromComponents(Vector comp,Object componentInfo) {
		return buildFromComponents(comp);
	}
	
	protected Expression buildFromComponents(ExpressionArray comp,Object componentInfo) {
		return buildFromComponents(comp);
	}
	
	/////////
	//Simplification Operations
	/////////
	
	//The following simplification methods are supported:
	//reduceFractions - cancel common factors in the numerator and denominator of a fraction: 2/4->1/2
	//combineLikeTerms - add or subtract terms with a common basis: 3x+4x->7x
	//multiplyThrough - multiply terms containing constants: 3*4x->12x
	//removeParens - remove extraneous parentheses: 3+(4+x)->3+4+x
	//distribute - multiply a term across a polynomial: 3(x+4)->3x+12
	//expand-exponent - expand a polynomial or complex term according to the exponent: (x+3)^2->x^2+6x+9
	//expand - combines expand-exponent and distribute
	
	//All simplification operations have two public methods: XX and canXX
	//The "can" method returns a boolean telling whether the operation will have any affect
	//Simplification operations also have 2 protected methods, XXWhole and canXXWhole
	//The general operator applies recursively to the components of an expression (e.g. the terms
	//in a polynomial), while the "whole" operator applies to the whole thing (assuming that the recursive
	//simplification has already been done). When appropriate, the "whole" methods are overridden
	//for particular expression types
	
	
	////////////////////
	//Reducing Fractions
	////////////////////
	
	//Reducing fractions involves cancelling common parts of the numerator and denominator
	//This includes canceling terms in algebraic expression
	//We also include canceling negatives in both numerator and denominator and moving negatives to the numerator
	//in reduceFractions

	public final Expression reduceFractions() {
		//System.out.println("Expression: reduceFractions(): " + debugForm());
		Expression reduced;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively reduceFractions
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.reduceFractions();
				components.setExpressionAt(thisComp,i);
			}
			reduced = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			reduced = this;
		return reduced.reduceFractionsWhole();
	}

	
	//these "whole" functions should be protected, but reflection won't let me find them, if they are...
	public Expression reduceFractionsWhole() {
		//System.out.println("Expression: reduceFractionsWhole(): " + debugForm());
		return this;
	}
	
	public boolean canReduceFractions() {
            //System.out.println("Expression: canReduceFractions(): " + debugForm());
		boolean canRed=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canRed;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canReduceFractions())
					canRed=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canRed)
			canRed = canReduceFractionsWhole();
		return canRed;
	}

	
	//canReduceFractionsWhole is the method that subclasses should override
	//This does not need to check any sub-components (if there are any)
	public boolean canReduceFractionsWhole() {
            //System.out.println("Expression: canReduceFractionsWhole(): " + debugForm());
		return false;
	}
	
	////////////////////
	//Combining Like Terms
	////////////////////

	public final Expression combineLikeTerms() {
		Expression combined;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively CLT
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.combineLikeTerms();
				components.setExpressionAt(thisComp,i);
			}
			combined = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			combined = this;
		return combined.combineLikeTermsWhole();		
	}
	
	protected Expression combineLikeTermsWhole() {
		return this;
	}
	
	public boolean canCombineLikeTerms() {
		boolean canComb=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canComb;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canCombineLikeTerms())
					canComb=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canComb)
			canComb = canCombineLikeTermsWhole();
		return canComb;
	}
	
	protected boolean canCombineLikeTermsWhole() {
		return false;
	}

	////////////////////
	//Multiplying through
	////////////////////

	public final Expression multiplyThrough() {
		/*level++;
		  System.out.println("E.mT[" + level + "]: " + debugForm());
		  /*if(level > 100){
			(new Exception()).printStackTrace();
			}*/
		Expression mult;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively MT
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.multiplyThrough();
				components.setExpressionAt(thisComp,i);
			}
			mult = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			mult = this;
		//System.out.println("E.mT[" + level + "]: calling whole");
		Expression ret = mult.multiplyThroughWhole();		
		/*System.out.println("E.mT[" + level + "]: returning: " + ret.debugForm());
		  level--;*/
		return ret;
	}
	
	protected Expression multiplyThroughWhole() {
		return this;
	}
	
	public final boolean canMultiplyThrough() {
		boolean canMult=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canMult;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canMultiplyThrough())
					canMult=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canMult)
			canMult = canMultiplyThroughWhole();
		return canMult;
	}
	
	protected boolean canMultiplyThroughWhole() {
		return false;
	}
	
	////////////////////
	//removeDoubleSigns
	////////////////////
	public final Expression removeDoubleSigns() {
		Expression dub;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively removeDoubleSigns
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.removeDoubleSigns();
				components.setExpressionAt(thisComp,i);
			}
			dub = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			dub = this;
		return dub.removeDoubleSignsWhole();		
	}
	
	protected Expression removeDoubleSignsWhole() {
		return this;
	}
	
	public final boolean canRemoveDoubleSigns() {
		boolean canRem=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canRem;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canRemoveDoubleSigns())
					canRem=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canRem)
			canRem = canRemoveDoubleSignsWhole();
		return canRem;
	}
	
	protected boolean canRemoveDoubleSignsWhole() {
		return false;
	}
	
	////////////////////
	//Removing unneeded parens
	////////////////////
	
	public final Expression removeParens() {
		Expression removed;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively remove parens
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.removeParens();
				components.setExpressionAt(thisComp,i);
			}
			removed = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			removed = this;
		return removed.removeParensWhole();
	}
	
	protected Expression removeParensWhole() {
		return this;
	}
	
	public final boolean canRemoveParens() {
		boolean canRem=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canRem;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canRemoveParens())
					canRem=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canRem)
			canRem = canRemoveParensWhole();
		return canRem;
	}

	protected boolean canRemoveParensWhole() {
		return false;
	}

	////////////////////
	//Distribution
	////////////////////

	//Distribute means to multiply a term across a polynomial (e.g. 3(x+5) = 3x+15)
	public final Expression distribute(int type) {
		//System.out.println("Expression: distribute: " + debugForm());
		Expression dist;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively distribute
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.distribute(type);
				components.setExpressionAt(thisComp,i);
			}
			dist = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			dist = this;

		//System.out.println("Expression: distribute: about to distributeWhole: " + dist.debugForm());
		return dist.distributeWhole(type);
	}
	
	protected Expression distributeWhole(int type) {
		return this;
	}
	
	public final boolean canDistribute(int type) {
		boolean canDist=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canDist;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canDistribute(type))
					canDist=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canDist)
			canDist = canDistributeWhole(type);
		return canDist;
	}
	
	protected boolean canDistributeWhole(int type) {
		return false;
	}
	
	//distributeOne can be used to perform only one distribution, in the case that there
	//are multiple ones to be performed. For example,
	//distributeOne("(3(x+y))/5",true) -->  (3x+3y)/5
	//distributeOne("(3(x+y)-6)/5",true) -->  (3(x+y))/5 - 6/5
	//distributeOne("3(x+y)+4(x+y)",true) -->  3x+3y+4(x+y)
	public final Expression distributeOne(int type) {
		if (canDistributeWhole(type))
			return distributeWhole(type);
		else if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			boolean foundDistribute = false;
			Expression dist=null;
			for (int i=0;i<components.size() && !foundDistribute;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canDistribute(type)) {
					thisComp = thisComp.distributeOne(type);
					foundDistribute = true;
					components.setExpressionAt(thisComp,i);
				}
			}
			dist = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
			return dist;
		}
		else
			return this;
	}
	
	//getOneToDistribute returns the expression that will be distributed in distributeOne
	public final Expression getOneToDistribute(int type) {
		if (canDistributeWhole(type))
			return this;
		else if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			boolean foundDistribute = false;
			Expression oneToDist=null;
			for (int i=0;i<components.size() && !foundDistribute;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canDistribute(type)) {
					oneToDist = thisComp.getOneToDistribute(type);
					foundDistribute = true;
				}
			}
			ExpressionArray.deallocate(components);
			return oneToDist;
		}
		else
			return null;
	}
	
	//getPartsToDistribute returns all sub-expressions that can be distributed
	//Is this useful?
	public final Expression[] getPartsToDistribute(int type) {
		Vector parts = new Vector();
		if (canDistributeWhole(type))
			parts.addElement(this);
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canDistribute(type)) {
					Expression[] theseDist = thisComp.getPartsToDistribute(type);
					for (int j=0;j<theseDist.length;++j)
						parts.addElement(theseDist[j]);
					theseDist = null;
				}
			}
			ExpressionArray.deallocate(components);
		}
		Expression[] result = new Expression[parts.size()];
		for (int i=0;i<parts.size();++i)
			result[i] = (Expression)(parts.elementAt(i));
		return result;
	}
			
	
	////////////////////
	//Changing Fractions to decimals
	////////////////////
	
	public final Expression fractionToDecimal() {
		Expression dec;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively convert fractions to decimals
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.fractionToDecimal();
				components.setExpressionAt(thisComp,i);
			}
			dec = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			dec = this;
		return dec.fractionToDecimalWhole();		
	}
	
	protected Expression fractionToDecimalWhole() {
		return this;
	}
	

	//factor factors out the given expression
	public Expression factor(Expression fact) {
		return this;
	}
	
	//factor without any arguments factors a polynomial (if it can)
	public Expression factor() {
		return this;
	}

	/*piecemeal factoring will only factor out of some terms.  Take
      for example "ax+bx+c".  Plain old factor() doesn't do anything;
      factor(x) will give "x(a+b+c/x)"; factorPiecemeal allows you to
      get "x(a+b)+c".  You always have to give factorPiecemeal an
      argument, because factorPiecemeal("ax+bcx+bd") could be
      interpreted as "x(a+bc)+bd" or as "ax+b(cx+d)".*/
	public Expression factorPiecemeal(Expression fact){
		return this;
	}

	public boolean canFactor() {
		return false;
	}
	
	public boolean canFactor(Expression fact){
		return false;
	}

	public boolean canFactorPiecemeal(Expression fact){
		return false;
	}

	public Expression factorQuadratic() {
		return this;
	}
	
	//the expanded form returns each expression as a list of "primitive" types
	// -- so 4y^3 becomes 4,y,y,y. This is useful in factoring
	protected Vector getExpandedForm() {
		Vector vec = new Vector();
		vec.addElement(this);
		return vec;
	}
	
	//getExplicitFactors returns the elements multiplied together in the term
	// (basically, this is the Expression itself, except for TermExpressions, in which it is a Vector
	// of the subterms).
	protected Vector getExplicitFactors(boolean expandNegated) {
		Vector result = new Vector();
		result.addElement(this);
		return result;
	}

        ////////////////////
        //substConstants
        ////////////////////


        public Expression substConstants(){
            Expression subConst;
            if(canHaveComponents()){
                ExpressionArray components = getComponentArray();
                Object componentInfo = getComponentInfo();
                //recursively substitute constants
                for(int i=0;i<components.size();i++){
                    Expression thisComp = components.expressionAt(i);
                    thisComp = thisComp.substConstants();
                    components.setExpressionAt(thisComp,i);
                }
                subConst = buildFromComponents(components,componentInfo);
                ExpressionArray.deallocate(components);
            }
            else{
                subConst = this;
            }
            return subConst.substConstantsWhole();
        }

        public Expression substConstantsWhole(){
            return this;
        }

        public boolean canSubstConstants(){
            boolean canSubConst = false;
            if(canHaveComponents()){
                ExpressionArray components = getComponentArray();
                //check recursively
                for(int i=0;i<components.size() && !canSubConst;i++){
                    Expression thisComp = components.expressionAt(i);
                    if(thisComp.canSubstConstants()){
                        canSubConst = true;
                    }
                }
                ExpressionArray.deallocate(components);
            }
            if(!canSubConst){
                canSubConst = canSubstConstantsWhole();
            }
            return canSubConst;
        }

        public boolean canSubstConstantsWhole(){
            return false;
        }

	
	////////////////////
	//ExpandExponent
	////////////////////
	
	
	public final Expression expandExponent() {
		Expression expexp;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively expand exponent
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.expandExponent();
				components.setExpressionAt(thisComp,i);
			}
			expexp = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			expexp = this;
		return expexp.expandExponentWhole();		
	}
	
	protected Expression expandExponentWhole() {
		return this;
	}
	
	public final boolean canExpandExponent() {
		boolean canEx=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canEx;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canExpandExponent())
					canEx=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canEx)
			canEx = canExpandExponentWhole();
		return canEx;
	}
	
	protected boolean canExpandExponentWhole() {
		return false;
	}

	////////////////////
	//Eliminate Exponent
	////////////////////

	public final Expression eliminateExponent(){
		Expression expexp;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively eliminate exponent
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.eliminateExponent();
				components.setExpressionAt(thisComp,i);
			}
			expexp = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			expexp = this;
		return expexp.eliminateExponentWhole();
	}

	protected Expression eliminateExponentWhole(){
		return this;
	}

	public final boolean canEliminateExponent(){
		boolean canEx=false;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			//check recursively
			for (int i=0;i<components.size() && !canEx;++i) {
				Expression thisComp = components.expressionAt(i);
				if (thisComp.canEliminateExponent())
					canEx=true;
			}
			ExpressionArray.deallocate(components);
		}
		if (!canEx)
			canEx = canEliminateExponentWhole();
		return canEx;
	}

	protected boolean canEliminateExponentWhole(){
		return false;
	}

	////////////////////
	//Expand
	////////////////////

	//Expand includes distribution and expanding exponents (e.g. 4(x+3)^2 = 4x^2+24x+36)
	public final Expression expand(int disttype) {
		return this.distribute(disttype).expandExponent();
	}
	
	public final boolean canExpand(int disttype) {
		return (canDistribute(disttype) || canExpandExponent());
	}
	
	////////////////////
	//Simplify
	////////////////////

/*	public final Expression simplify() {
//		System.out.println("Starting simplify of "+this+"::"+debugForm());
		Expression simp;
		if (canHaveComponents()) {
			Vector components = getComponents();
			Object componentInfo = getComponentInfo();
			//recursively simplify
			for (int i=0;i<components.size();++i) {
				Expression thisComp = (Expression)(components.elementAt(i));
				thisComp = thisComp.simplify();
				components.setElementAt(thisComp,i);
			}
			simp = buildFromComponents(components,componentInfo);
//			System.out.println("components simplified, "+toString()+" is now "+simp.toString());
		}
		else {
			simp = this;
		}
		return simp.simplifyWhole();		
	}
*/	
	public final Expression simplify() {
		///quick check of caching...
		/*System.out.println("Expression.simplify(): begin level " + level);
              level++;*/
		Key key = new Key(debugForm());
		Expression cached = (Expression)(simplificationCache.get(key));
		if (cached != null) {
                    /*level--;
                      System.out.println("Expression.simplify():  end  level " + level);*/
			return cached;
		}
		///
		
		Expression intermediate = this;
		boolean foundSimp = true;
		int cycles = 0;
		while (foundSimp && cycles<10) {
//			System.out.println("simplifying "+intermediate+"::"+intermediate.debugForm()+" cycle: "+cycles);
			foundSimp = false;
			if (intermediate.canRemoveParens()) {
				intermediate = intermediate.removeParens();
//				System.out.println("after removeParens: "+intermediate);
				foundSimp=true;
			}
			if (intermediate.canReduceFractions()) {
				intermediate = intermediate.reduceFractions();
//				System.out.println("after reduceFractions: "+intermediate);
				foundSimp=true;
			}
			if (intermediate.canCombineLikeTerms()) {
				intermediate = intermediate.combineLikeTerms();
//				System.out.println("after combineLikeTerms: "+intermediate);
				foundSimp=true;
			}
			if (intermediate.canMultiplyThrough()) {
				intermediate = intermediate.multiplyThrough();
//				System.out.println("after multiplyThrough: "+intermediate.debugForm());
				foundSimp=true;
			}
			if (intermediate.canRemoveDoubleSigns()) {
				intermediate = intermediate.removeDoubleSigns();
//				System.out.println("after removeDoubleSigns: "+intermediate);
				foundSimp=true;
			}
			if (intermediate.canExpandExponent()) {
				intermediate = intermediate.expandExponent();
//				System.out.println("after expandExponent: "+intermediate);
				foundSimp=true;
			}
			cycles +=1;
		}
		if (cycles == 10) //only cycle 10 times. If we don't simplify by then, some "can" predicate is returning true when we can't really simplify
			System.out.println("Error: simplification loop for "+toString()+" results in "+intermediate.toString());
		////
		///mmmBUG -- should cache on something other than a string for performance reasons
		simplificationCache.put(key,intermediate);
		////
                /*level--;
                  System.out.println("Expression.simplify():  end  level " + level);*/
		return intermediate;
	}
		

	public final boolean canSimplify() {
            //System.out.println("canSimplify: checking: " + debugForm());
            if (canCombineLikeTerms()){
                //System.out.println("             CLT");
                return true;
            }
            else if(canMultiplyThrough()){
                //System.out.println("             MT");
                return true;
            }
            else if(canReduceFractions()){
                //System.out.println("             RF");
                return true;
            }
            else if(canRemoveDoubleSigns()){
                //System.out.println("             RDS");
                return true;
            }
            else if(canRemoveParens()){
                //System.out.println("             RP");
                return true;
            }
            else if(canExpandExponent()){
                //System.out.println("             EE");
                return true;
            }
            else{
                //System.out.println("             * all failed *");
                return false;
            }
	}

    /*public final boolean canSimplify() {
		if (canCombineLikeTerms() ||
			canMultiplyThrough() ||
			canReduceFractions() ||
			canRemoveDoubleSigns() ||
			canRemoveParens() ||
			canExpandExponent())
			return true;
		else
			return false;
                        }*/

	public final boolean canSimplifyWhole() {
		if (canCombineLikeTermsWhole() ||
			canMultiplyThroughWhole() ||
			canReduceFractionsWhole() ||
			canRemoveDoubleSignsWhole() ||
			canRemoveParensWhole() ||
			canExpandExponentWhole())
			return true;
		else
			return false;
	}

	//simplifyWhole simplifies the top-level expression
	//This is really different from general simplification pattern, because
	//simplifying the top-level expression can create components that need to be simplified
	//For example, multiplyThroughWhole of 3x^2*3x^3 is 3x^(2+3)
	//so, we check to see if the "whole" simplifications have resulted in something with components
	//that need to be simplified. If so, we call "simplify" before trying the "whole" simplifications again
	protected Expression simplifyWhole() {
		Expression intermediate = this;
		int cycles = 0;
		while (intermediate.canSimplifyWhole() && cycles<10) {
//			System.out.println("Simplifying "+intermediate.debugForm()+" cycle "+cycles);
/*
			System.out.println("Simplifying "+intermediate.toString()+" cycle "+cycles);
			System.out.println("  "+intermediate.canCombineLikeTerms()+intermediate.canMultiplyThrough()+
									intermediate.canExpandExponent()+intermediate.canReduceFractions()+intermediate.canRemoveDoubleSigns()
								    +intermediate.canRemoveParens());
*/
			intermediate = intermediate.removeParensWhole();
//			System.out.println("After RP: "+intermediate.toString());
			intermediate = intermediate.combineLikeTermsWhole();
//			System.out.println("after CLT: "+intermediate.toString());
			intermediate = intermediate.multiplyThroughWhole();
//			System.out.println("after MT: "+intermediate.toString());
			intermediate = intermediate.reduceFractionsWhole();
//			System.out.println("After RF: "+intermediate.toString());
			intermediate = intermediate.removeDoubleSignsWhole();
//			System.out.println("After RDS: "+intermediate.toString());
			intermediate = intermediate.cleanExpression();
//			System.out.println("After CE: "+intermediate.toString());
			intermediate = intermediate.expandExponentWhole();
//			System.out.println("after EE: "+intermediate.toString());
//			if (!intermediate.canSimplifyWhole() && intermediate.canSimplify())
//				intermediate = intermediate.simplify();
			cycles +=1;
		}
		if (cycles == 10) //only cycle 10 times. If we don't simplify by then, some "can" predicate is returning true when we can't really simplify
			System.out.println("Error: simplification loop for "+toString()+" results in "+intermediate.toString());
		return intermediate;
	}

	////////////////////
	//Standardize
	////////////////////
	
	//standardize means to algebraically simplify and put in a standard format
	//(sorting polynomials and distributing, for example)
	//Standardized expressions look like ones that people who know algebra should recognize
	//Generally two algebraically equal, standardized expressions should look identical, with two main exceptions:
	// 1. fractions and decimals can both be in standardized expressions
	// 2. fractions can be split across an expression, so both 3x/4 and 3/4*x are legitimate standardized forms
	//Because of these exceptions, two expressions can be algebraically equal, even if both
	//are standardized and they aren't string-equal
	//Cannonicalized expressions (see "cannonicalize") are algebraically-equal iff they are string-equal
	public final Expression standardize(int type) {
		Key key = new Key(debugForm()+type);
		Expression standard = (Expression)(standardizationCache.get(key));
		if(standard != null){
			return standard;
		}

		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively standardize
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.standardize(type);
				components.setExpressionAt(thisComp,i);
			}
			standard = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else {
			standard = this;
		}
		standard = standard.standardizeWhole(type);

		standardizationCache.put(key,standard);
		return standard;
	}

	public final boolean canStandardize(int type) {
		if (canSimplify())
			return true;
		else {
			Expression stand = standardize(type);
			if (stand.debugForm().equals(debugForm()))
				return false;
			else
				return true;
		}
	}

	protected Expression standardizeWhole(int type) {
		Expression intermediate=this;
		int count=0;
                //System.out.println("standardizeWhole(): " + debugForm());
		boolean canSimp = intermediate.canSimplify();
		boolean canDist = intermediate.canDistributeWhole(type);
		//System.out.println(intermediate+" cansimp: "+intermediate.canSimplify()+" canCLT: "+intermediate.canCombineLikeTerms());
		while ((canDist || canSimp) && count < 10) {
                    String pre = intermediate.debugForm();
                    //System.out.println("standardizeWhole, start of step "+count+": "+intermediate.debugForm());
			if (canSimp)
				intermediate = intermediate.simplify(); //was simplifyWhole...
			else if (canDist)
				intermediate = intermediate.distributeWhole(type);
			//System.out.println("standardizeWhole, end of step "+count+": "+intermediate.debugForm());
                        if(intermediate.debugForm().equals(pre)){
                            //System.out.println("!!! standardizeWhole: error: simplification didn't change expression: " +
                            //                 pre + "\n!!! canDist: " + canDist + "; canSimp: " + canSimp);
                        }
			count++;
			canSimp = intermediate.canSimplify();
			canDist = intermediate.canDistributeWhole(type);
		}
		if (count == 10){
                    //System.out.println("Standardization error; result is "+intermediate.toString());
                }
                //System.out.println("standardizeWhole() returning: " + intermediate.debugForm());
		return intermediate;
	}
	
	public final Expression sortPoly() {
		Expression sorted;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively sortPoly
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.sortPoly();
				components.setExpressionAt(thisComp,i);
			}
			sorted = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else {
			sorted = this;
		}
		sorted = sorted.sortPolyWhole();		
                return sorted;
	}

	protected Expression sortPolyWhole() {
		return this;
	}
	
	public final Expression sortTerm() {
		Expression sorted;
		if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively sortTerm
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				thisComp = thisComp.sortTerm();
				components.setExpressionAt(thisComp,i);
			}
			sorted = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else {
			sorted = this;
		}
		return sorted.sortTermWhole();		
	}
	
	protected Expression sortTermWhole() {
		return this;
	}
	
	//sort sorts the expression (with higher-degree terms first as well as appropriately within terms)
	public final Expression sort() {
		return this.sortTerm().sortPoly();
	}

	//cleanExpression is used to eliminate "invalid" expressions -- ones that have a better representation, such as
	//TermExpressions and PolyExpressions that have only 1 subterm
	//cleanExpression isn't a simplification operation, since it doesn't change the semantics of the expression
	public Expression cleanExpression() {
		return this;
	}
	
	//initialCannonicalize is done before we try to cannonicalize any of the components (but after we standardize)
	protected Expression initialCannonicalize() {
		return this;
	}

	//cannonicalize means to put in a standard form.
	//If two expressions are algebraically equal, their cannonical forms are
	//identical
	//We want to standardize first, so 1/3*x*2 --> 2/3*x --> .67x, instead of 1/3*x*2 --> .33*x*2 --> .66x
	
	public final Expression cannonicalize() {
		/*level++;
		  System.out.println("cannonicalize[" + level + "]: begin: "+this);*/
		Expression cannon = standardize(DISTBOTH);
		//System.out.println("cannonicalize[" + level + "]:"+this+" after standardize:"+cannon.debugForm());
		cannon = cannon.initialCannonicalize();
		//System.out.println("cannonicalize[" + level + "]:"+this+" after initialCannonicalize:"+cannon.debugForm());
		if (cannon.canHaveComponents()) {
			ExpressionArray components = cannon.getComponentArray();
			Object componentInfo = cannon.getComponentInfo();
			//recursively cannonicalize
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				//System.out.println("cannonicalize[" + level + "]: recurring on component " + i);
				thisComp = thisComp.cannonicalize();
				//System.out.println("cannonicalize[" + level + "]: done recurring on comp " + i);
				components.setExpressionAt(thisComp,i);
			}
			cannon = cannon.buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		//System.out.println("cannonicalize[" + level + "]:" +this+" before cannonWhole:"+cannon);
		Expression ret = cannon.cannonicalizeWhole();		
		/*System.out.println("cannonicalize[" + level + "]:" +this+" after cannonWhole:"+cannon.cannonicalizeWhole());
		  level--;*/
		return ret;
	}
		
	protected Expression cannonicalizeWhole() {
		return this;
	}
	
	//isLike is true iff the two expressions can be added together into a single term (that is, they are "like terms")
	protected boolean isLike(Expression ex) {
//		System.out.println("In expression -- canCombine");
		return false;
	}
	
	//addLikeTerms is a low-level addition
	//addLikeTerms methods can assume that the expressions can be combined together after addition
	//(that is, that isLike has returns true, so we know the expressions are like terms)
	protected Expression addLikeTerms(Expression ex) {
		throw new IllegalArgumentException("addLikeTerms called on uncombinable objects: "+debugForm()+" and "+ex.debugForm());
	}
	
	//iMultiply is a low-level multiplication
	//iMultiply methods can assume that the expression can legally be multiplied
	protected Expression iMultiply(Expression ex) {
		throw new IllegalArgumentException("iMultiply called on uncombinable objects");
	}

	//two expressions are similar if they are algebraically equal and at the same level of simplification
	// (e.g. x+3 is similar to 3+x, but x+1+2 is not)
	public boolean similar(Expression ex) {
		/*System.out.println("E.s: " + debugForm());
		  System.out.println("     " + ex.debugForm());*/
		boolean sim=true;
		if (algebraicEqual(ex)) {
			if (canHaveComponents()) {
				Vector myComps = sort().getFullComponents();
				Vector otherComps = ex.sort().getFullComponents();
				if (myComps.size() == otherComps.size()) {
					for (int i=0;i<myComps.size()&&sim;++i) {
						Expression myPart = (Expression)(myComps.elementAt(i));
						Expression otherPart = (Expression)(otherComps.elementAt(i));
						if (!myPart.similar(otherPart))
							sim=false;
					}
				}
				else
					sim=false;
				myComps.removeAllElements();
				otherComps.removeAllElements();
				myComps = null;
				otherComps = null;
			}
			else if (this instanceof NumericExpression && ex instanceof NumericExpression)
				sim = algebraicEqual(ex);
			else
				sim = exactEqual(ex);
		}
		else
			sim = false;
//		if (sim == false)
//			System.out.println(whyNotSimilar(ex));
		return sim;
	}
	
	//whyNotSimilar is used to debug Similar
	public String whyNotSimilar(Expression ex) {
		boolean sim=true;
		String result="Similar";
		if (algebraicEqual(ex)) {
			if (canHaveComponents()) {
				Vector myComps = sort().getFullComponents();
				Vector otherComps = ex.sort().getFullComponents();
				if (myComps.size() == otherComps.size()) {
					for (int i=0;i<myComps.size()&&sim;++i) {
						Expression myPart = (Expression)(myComps.elementAt(i));
						Expression otherPart = (Expression)(otherComps.elementAt(i));
						if (!myPart.similar(otherPart)) {
							result = myPart.toString()+" not similar to "+otherPart.toString()+" ["+myPart.whyNotSimilar(otherPart)+"]";
							sim=false;
						}
					}
				}
				else{
					sim=false;
					result = "different number of components";
				}
				myComps.removeAllElements();
				otherComps.removeAllElements();
				myComps = null;
				otherComps = null;
			}
			else if (this instanceof NumericExpression && ex instanceof NumericExpression){
				sim = algebraicEqual(ex);
				if(!sim){
					result = "this should never happen";
					//(because NumericExpressions don't have components
				}
			}
			else{
				sim = exactEqual(ex);
				if(!sim){
					result = "not exactly equal";
				}
			}
		}
		else {
			result = "not algebraically equal";
			sim = false;
		}
		return result;
	}

		
	public boolean algebraicEqual(Expression ex) {
		/*level++;
		  try{
		  if(level > 100){
		  throw new Exception();
		  }
		  System.out.println("algebraicEqual[" + level + "]: comparing "+this+" to "+ex+":  "+
		  this.debugForm()+" to "+ex.debugForm());*/
			//		System.out.println("comparing "+this+" to "+ex+":  "+this.cannonicalize()+" to "+ex.cannonicalize());
            Expression cannon,excannon;
            boolean ret;
            cannon = this.cannonicalize();
            //System.out.println("algebraicEqual[" + level + "]: this.cannonicalize() = " + cannon);
            excannon = ex.cannonicalize();
            //System.out.println("algebraicEqual[" + level + "]: ex.cannonicalize() = " + excannon);
            ret = cannon.exactEqual(excannon);
            /*System.out.println("algebraicEqual[" + level + "]: result: " + ret);
			  level--;*/
            return ret;
            //return this.cannonicalize().exactEqual(ex.cannonicalize());
			/*}
			  catch(Exception e){
			  System.out.println("algebraicEqual[" + level + "]: overflow, level " + level);
			  e.printStackTrace();
			  level--;
			  System.exit(1);
			  return false;
			  }*/
	}
	
	public boolean exactEqual(Expression ex) {
		return false;
	}
	
	//simplifiedCoefficient is the coefficient of the expression, after the expression has been simplified
	//(the distinction between this and unsimplifiedCoefficient is really only important for terms like 3*4x)
        public NumericExpression numericSimplifiedCoefficient(){
            return new NumberExpression(1);
        }

        public NumericExpression numericUnsimplifiedCoefficient(){
            return numericSimplifiedCoefficient();
        }

	public Expression exceptNumericSimplifiedCoefficient(){
		return this;
	}

	public Expression exceptNumericUnsimplifiedCoefficient(){
		return exceptNumericSimplifiedCoefficient();
	}

	public Expression simplifiedCoefficient() {
		return (new NumberExpression(1));
	}
	
	public Expression unsimplifiedCoefficient() {
		return simplifiedCoefficient();
	}
	
	public Expression exceptSimplifiedCoefficient() {
		return this;
	}
	
	public Expression exceptUnsimplifiedCoefficient() {
		return exceptSimplifiedCoefficient();
	}
	
	public Expression exceptExponent() {
		return this;
	}
	
	public Expression getExponent() {
		return new NumberExpression(1);
	}
	
	//termSortBefore returns True if the expression comes before the expression argument
	//in the standard sort order. This is used to sort terms.
	public boolean termSortBefore(Expression ex) {
		return false;
	}
	
	//polySortBefore returns True if the expression comes before the argument
	//in the standard sort order for polynominals (where highest degree comes first).
	//This is used to sort polynomials
	public boolean polySortBefore(Expression ex) {
		double myDegree = degree();
		double otherDegree = ex.degree();

		if (myDegree > otherDegree)
			return true;
		else if (myDegree < otherDegree)
			return false;
		else
			return !termSortBefore(ex);
	}
	
	public void setPartAttribute(String partName,String attribute,String value) {
		Vector attList;
		if (partAttributes != null && partAttributes.containsKey(partName)) {
			attList = (Vector)(partAttributes.get(partName));
		}
		else {
			attList = new Vector();
			if (partAttributes == null)
				partAttributes = new Hashtable(13);
			partAttributes.put(partName,attList);
		}
		attList.addElement(new DisplayAttribute(attribute,value));
	}
	
	public Vector getPartAttributes(String partName) {
		if (partAttributes == null)
			return null;
		else
			return (Vector)(partAttributes.get(partName));
	}
	
	protected String toASCII(String openParen, String closeParen) {
		return "<<Null Expression>>";
	}
	
	public String toString() {
		return toASCII("(",")");
	}
	
	//an "intermediate string" deals with parens slightly differently -- see ImpliedFencedExpression
	public String toIntermediateString() {
		return toASCII("[","]");
	}
	
	//addMathMLAttributes adds formatting attributes to the MathML output
	//toMathML should always call this to ensure that these attributes get added
	//subclasses of Expression should make sure to call addMathMLAttributes on super, so that any
	//general display attributes will get used.
	//
	//mphantom (make the piece invisible) is treated differently from the others, since it is a directive
	//on its own, rather than an argument to mstyle
	public String addMathMLAttributes(String internalML) {
		return addMathMLAttributes(internalML,displayAttributes);
	}
	
	public String addMathMLAttributes(String internalML,Vector attributes) {
		if (attributes == null || attributes.size() == 0)
			return internalML;
		else {
			String styles = "";
			boolean phantom = false;
			boolean hasStyleAttributes = false;
			for (int i=0;i<attributes.size();++i) {
				DisplayAttribute thisAtt = (DisplayAttribute)(attributes.elementAt(i));
				String attribute = thisAtt.getAttribute();
				if (attribute.equalsIgnoreCase("mphantom"))
					phantom = true;
				else if (attribute.equalsIgnoreCase("prefix"))
					internalML = thisAtt.getValue()+internalML;
				else if (attribute.equalsIgnoreCase("suffix"))
					internalML = internalML+thisAtt.getValue();
				else {
					styles += " "+thisAtt.toString();
					hasStyleAttributes = true;
				}
			}
			if (phantom && !hasStyleAttributes){
				return "<mphantom>"+internalML+"</mphantom>";
			}
			else if (!phantom && hasStyleAttributes) 
				return "<mstyle"+styles+">"+internalML+"</mstyle>";
			else if (!phantom && !hasStyleAttributes) //must be just prefixes and suffixes
				return internalML;
			else //phantom + styles -- include styles, even though they won't be shown, since they can affect spacing
				return "<mphantom>"+"<mstyle"+styles+">"+internalML+"</mstyle>"+"</mphantom>";
		}
	}
	
	public String addMathMLPartAttributes(String partName,String internalML) {
		Vector att = getPartAttributes(partName);
		if (att == null)
			return internalML;
		else
			return addMathMLAttributes(internalML,att);
	}

	//by default, display the string as text (this shouldn't be called)
	public String toMathML() {
		return addMathMLAttributes("<mtext>"+toString()+"</mtext>");
	}
	
	public String debugForm() {
		return "<<Null Expression>>";
	}
	
	public Expression substitute(Expression old,Expression newEx) {
		Expression subbed;
		if (exactEqual(old))
			subbed = newEx;
		else if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively substitute
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				components.setExpressionAt(thisComp.substitute(old,newEx),i);
			}
			subbed = buildFromComponents(components,componentInfo);
			ExpressionArray.deallocate(components);
		}
		else
			subbed = this;
		return subbed;		
	}
	
	public void print(PrintStream stream) {
		stream.print(toString());
	}
	
	public static Vector sortVector(Vector invector) {
		Vector outExpressions = new Vector();
		
		outExpressions.addElement(invector.elementAt(0));
		for (int i=1;i<invector.size();++i) {
			boolean inserted=false;
			Expression thisEx = (Expression)(invector.elementAt(i));
			for (int j=0;j<outExpressions.size() && !inserted;++j) {
				if (thisEx.termSortBefore((Expression)(outExpressions.elementAt(j)))) {
					outExpressions.insertElementAt(thisEx,j);
					inserted=true;
				}
			}
			if (!inserted)
				outExpressions.addElement(thisEx);
		}
		return outExpressions;
	}
	
	public Vector variablesUsed() {
		if (canHaveComponents()) {
			Vector vars = new Vector();
			ExpressionArray components = getFullComponentArray();
			Vector subvars;
			for (int i=0;i<components.size();++i) {
				Expression comp = components.expressionAt(i);
				subvars = comp.variablesUsed();
				for (int subitem=0;subitem<subvars.size();++subitem) {
					String thisItem = (String)subvars.elementAt(subitem);
					boolean found=vars.contains(thisItem);
					if (!found)
						vars.addElement(thisItem);
				}
				subvars.removeAllElements();
				subvars = null;
			}
			ExpressionArray.deallocate(components);
			return vars;
		}
		else
			return new Vector(); //must override for VariableExpression
	}
	
	public Vector allNumbers() {
		Vector nums = allNumbersWhole(); //get "immediate" numbers (this returns an empty vector for everything but numbers)
		if (canHaveComponents()) {
			Vector components = getFullComponents();
			//get numbers contained in components
			for (int i=0;i<components.size();++i) {
				Expression thisComp = (Expression)(components.elementAt(i));
				Vector newNumbers = thisComp.allNumbers();
				for (int j=0;j<newNumbers.size();++j)
					nums.addElement(newNumbers.elementAt(j));
			}
			components.removeAllElements();
			components = null;
		}
		return nums;	
	}

	protected Vector allNumbersWhole() {
		return new Vector();
	}
	
	public Expression getBindings(Equation bind) {
		Expression result;

		if (this instanceof NumericExpression){
			String letter = bind.getNextLetter();
			Number thisVal = ((NumericExpression)this).getValue();
			bind.addBinding(thisVal,letter);
			result = new BoundExpression(letter);
		}
                else if (this instanceof ConstantExpression) {
                    String letter = bind.getNextLetter();
                    String thisVal = ((ConstantExpression)this).getString();
                    bind.addBinding(thisVal,letter);
                    result = new BoundExpression(letter);
                }
                else if (this instanceof LiteralExpression) {
                    String letter = bind.getNextLetter();
                    String thisVal = ((LiteralExpression)this).getString();
                    bind.addBinding(thisVal,letter);
                    result = new BoundExpression(letter);
                }
		else if (this instanceof VariableExpression) {
			String letter = bind.getPatternVariable(((VariableExpression)this).getString());
			result = new VariableExpression(letter);
		}
		else if (canHaveComponents()) {
			ExpressionArray components = getComponentArray();
			Object componentInfo = getComponentInfo();
			//recursively getBindings
			for (int i=0;i<components.size();++i) {
				Expression thisComp = components.expressionAt(i);
				//Olga
				/*
				if(thisComp instanceof VariableExpression){
					if(i== 0 || !((Expression)(components.elementAt(i-1)) instanceof BoundExpression)){
						String letter = bind.getNextLetter();
						Number thisVal = (new NumberExpression()).getValue();
						bind.addBinding(thisVal,letter);
						compCopy.setElementAt(thisComp,k);
						((Vector)componentInfo).insertElementAt(Integer.valueOf("1"),k);
						k++;	
					}
				}					
				*/	
				// end Olga
				thisComp = thisComp.getBindings(bind);
				components.setExpressionAt(thisComp,i);
			}
			Expression pattern = buildFromComponents(components,componentInfo);
			result = pattern;
			ExpressionArray.deallocate(components);
		}
		else
			result = this;
		bind.setPattern(result);
		return result;
	}
	
	public int complexity() {
		int complex = 0;
		if (canHaveComponents()) {
			Vector comps = getFullComponents();
			for (int i=0;i<comps.size();++i)
				complex += ((Expression)(comps.elementAt(i))).complexity();
			comps.removeAllElements();
			comps = null;
		}
		else
			complex = 1;
		return complex;
	}

	public static boolean getMaintainVars(){
		return maintainVars;
	}

	public static void setMaintainVars(boolean b){
		maintainVars = b;
	}

	public boolean getEncapsulateVar(){
		return encapsulateVar;
	}

	public void setEncapsulateVar(boolean c){
		if(c){
		// check if there is no var with other degrees
			double deg = degree();
			int degInt = (int)deg;
			if(deg > 1.0){
				boolean onlyOneDegree = true;
				int k = degInt-1;
				Queryable st = null;
				while(k>1){
					try{
						st = getProperty("term with degree "+String.valueOf(k));
					} catch (NoSuchFieldException e) { }
					if(st != null && !st.getStringValue().startsWith("0")){
						onlyOneDegree = false;
						break;
					}
					k--;
				}
				if(onlyOneDegree){
					try{
						st = getProperty("term with degree 1");
					} catch (NoSuchFieldException e) {System.out.println("in Exp :"); e.printStackTrace(); }	
					if(st != null && !st.getStringValue().startsWith("0"))
						onlyOneDegree = false;
				}
				encapsulateVar = onlyOneDegree;
			}
			else
				encapsulateVar = false;
		}
		else
			encapsulateVar = c;
	}
	
	
	//Queryable Interface
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		//System.out.println("getProperty (expression): "+prop);
		Queryable result=null;
		if (prop.equalsIgnoreCase("self") || prop.equalsIgnoreCase("term 1"))
			return this;
		else if (prop.equalsIgnoreCase("coefficient"))
			result = simplifiedCoefficient();
		else if (prop.equalsIgnoreCase("reciprocal"))
			result = reciprocal();
		else if (prop.equalsIgnoreCase("negative"))
			result = negate();
		else if (prop.equalsIgnoreCase("terms")) {
			result = new ArrayQuery(this);
		}
		else if (prop.equalsIgnoreCase("factors")) {
			result = new ArrayQuery(this);
		}
		else if (prop.equalsIgnoreCase("exponent"))
			result = getExponent();
		else if (prop.equalsIgnoreCase("variable terms")) {
			if (variablesUsed().size() > 0) {
				result = new ArrayQuery(this);
			}
			else
				result = new ArrayQuery(); //empty array
		}
		else if (prop.equalsIgnoreCase("constant terms")) {
			if (variablesUsed().size() == 0)
				result = new ArrayQuery(this);
			else
				result = new ArrayQuery(); //empty array
		}
		/*variable factor of x*a*(b+c)*x^2 ==> x*x^2*/
		else if (prop.equalsIgnoreCase("variable factor")){
			Expression ret = null;
			if(this instanceof PolyExpression){
				;/*leave ret == null*/
			}
			else if(canHaveComponents()){
				ExpressionArray comp = getComponentArray();
				for(int i=0;i<comp.size();i++){
					if((comp.expressionAt(i)).variablesUsed().size() > 0){
						if(ret == null){
							ret = comp.expressionAt(i);
						}
						else{
							ret = ret.multiply(comp.expressionAt(i));
						}
					}
				}
				ExpressionArray.deallocate(comp);
			}
			else if(variablesUsed().size() > 0){
				ret = this;
			}
			if(ret == null){
				throw new NoSuchFieldException("No variable factor in " + this);
			}

			return ret;
		}
		/*constant factor of x*a*(b+c)*x^2 ==> a*(b+c)*/
		else if (prop.equalsIgnoreCase("constant factor")){
			if(variablesUsed().size() == 0){
				return this;
			}
			else{
				throw new NoSuchFieldException("No constant factor in " + this);
			}
		}
		else if(prop.equalsIgnoreCase("base")){
			return this;
		}
		else if(prop.equalsIgnoreCase("exponent")){
			return new NumberExpression(1);
		}
		else if (prop.equalsIgnoreCase("numerator")) {
			result = this;
		}
		else if (prop.equalsIgnoreCase("denominator")) {
			result = new NumberExpression(1);
		}
		else if (prop.equalsIgnoreCase("numerator terms")) {
			result = new ArrayQuery(this);
		}
		else if (prop.equalsIgnoreCase("denominator terms")) {
			result = new ArrayQuery();
		}
		else if (prop.length() > 16 && prop.substring(0,16).equalsIgnoreCase("term with degree")) {
			int desiredDegree = Integer.parseInt(prop.substring(17));
			if (degree() == desiredDegree)
				return this;
			else //create 0x^exp
				return new TermExpression(new NumberExpression(0),
										  new ExponentExpression(new VariableExpression("X"),
														   new NumberExpression(desiredDegree)));
		}
		else if (prop.equalsIgnoreCase("Pattern") ||
				 prop.equalsIgnoreCase("Form")) {
			try {
				Equation thisForm = Equation.makeForm(toString());
				result = new StringQuery(thisForm.getPattern());
			}
			catch (BadExpressionError err) {
				System.out.println("bad expression in term with degree: "+prop.substring(14));
			}
		}
		else if (prop.length() > 13 && prop.substring(0,13).equalsIgnoreCase("term matching")) {
			try {
				Equation thisForm = new Equation(this,null);
				Equation matchForm = Equation.makeForm(prop.substring(14));
				if (thisForm.patternMatches(matchForm))
					result = this;
				else
					throw new NoSuchFieldException(this+" does not match "+prop.substring(14));
			}
			catch (BadExpressionError err) {
				System.out.println("bad expression in term matching: "+prop.substring(14));
			}
		}
		else if (prop.length() > 15 && prop.substring(0,15).equalsIgnoreCase("factor matching")){
			try{
				Equation thisForm = new Equation(this,null);
				Equation matchForm = Equation.makeForm(prop.substring(16));
				if(thisForm.patternMatches(matchForm)){
					result = this;
				}
				else{
					throw new NoSuchFieldException(this + "does not match " + prop.substring(16));
				}
			}
			catch(BadExpressionError err){
				System.out.println("bad expression in factor matching: " + prop.substring(16));
			}
		}
		//Value should only be used when the (simplified) expression is a NumericExpression
		else if (prop.equalsIgnoreCase("value")) {
			Expression simp = simplify();
			if (simp instanceof NumericExpression) {
				NumericExpression nEx = (NumericExpression)simp;
				result = new NumberQuery(nEx.getValue());
			}
			else
				throw new NoSuchFieldException("Can't get value of non-numeric expression: "+this);
		}
		else if (prop.equalsIgnoreCase("absolute value")) {
			result = this.absoluteValue();
		}
		else if (prop.equalsIgnoreCase("variables")) {
			Vector vars = variablesUsed();
			StringQuery[] items = new StringQuery[vars.size()];
			// that works only if there is only one variable in any int power
			String pow = "";
			if(encapsulateVar){
				double deg = degree();
				int degInt = (int)deg;
				pow = "^"+String.valueOf(degInt);
			}
			for (int i=0;i<vars.size();++i)
				items[i] = new StringQuery((String)(vars.elementAt(i))+pow);
			
			vars.removeAllElements();
			vars = null;
			result = new ArrayQuery(items);
		}
                else if (prop.length() > 16 && prop.substring(0,16).equalsIgnoreCase("target variables")){
                    Vector vars = variablesUsed();
                    String v = prop.substring(17);
                    if(vars.contains(v)){
                        result = new ArrayQuery(new StringQuery(v));
                    }
                    else{
                        result = new ArrayQuery();
                    }

					vars.removeAllElements();
					vars = null;
                    return result;
                }
		//components with property returns any sub-components of an expression that have the desired property
		else if (prop.length() > 24 && prop.substring(0,24).equalsIgnoreCase("components with property")) {
			String predicate = prop.substring(25);
			if(evalQuery(predicate).getBooleanValue()){
				return new ArrayQuery(this);
			}
			Vector components = getFullComponents();
			Vector passingComponents = new Vector();
			for (int i=0;i<components.size();++i) {
				Expression thisComponent = (Expression)(components.elementAt(i));
				boolean thisMatch = thisComponent.evalQuery(predicate).getBooleanValue();
				//System.out.println(predicate+" on "+thisComponent+":"+thisMatch);
				if (thisMatch)
					passingComponents.addElement(thisComponent);
				else if (thisComponent.canHaveComponents()) { //recursively check components
					Queryable subcomponents = thisComponent.getProperty(prop);
					Queryable[] allSubValues = subcomponents.getArrayValue();
					for (int j=0;j<allSubValues.length;++j)
						passingComponents.addElement(allSubValues[j]);
					allSubValues = null;
				}
			}
			components.removeAllElements();
			components = null;
			return new ArrayQuery(passingComponents);
		}
		//"numbers" returns an array of NumericExpressions
		else if (prop.equalsIgnoreCase("numbers")) {
			result = new ArrayQuery(allNumbers());
		}
		else if (prop.equalsIgnoreCase("complexity")) {
			result = new NumberExpression(complexity());
		}
		else if (prop.equalsIgnoreCase("isNumber")) {
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isNegative")) {
			result = new BooleanQuery(isNegative());
		}
		else if (prop.equalsIgnoreCase("isPositive")) {
			result = new BooleanQuery(!isNegative() && !isZero());
		}
		//we need "isInteger, isFraction and isDecimal here (not just on NumberQuery), since a NumberQuery won't
		//represent a fraction
		else if (prop.equalsIgnoreCase("isInteger")) {
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isNotDecimal")) { //isNotDecimal is like "isInteger", except that integer Fractions are OK, too
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isDecimal")) {
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isFraction")) {
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isLiteral")) {
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isExponent")){
			result = new BooleanQuery(false);
		}
		else if (prop.equalsIgnoreCase("isRadical")){
			result = new BooleanQuery(false);
		}
		//ALLEN
		else if (prop.equalsIgnoreCase("isRatio")) {
			result = new BooleanQuery(false);
		}
		// end ALLEN
		/*containsXXX just checks recursively for the corresponding
          isXXX property (above)*/
		else if (prop.length() > 8 && prop.substring(0,8).equalsIgnoreCase("contains")){
			String name = prop.substring(8);
			if(canHaveComponents()){
				result = new BooleanQuery(false);
				ExpressionArray comp = getComponentArray();
				for(int i=0;i<comp.size() && !result.getBooleanValue();i++){
					if((comp.expressionAt(i)).getProperty(prop).getBooleanValue() ||
					   (comp.expressionAt(i)).getProperty("is" + name).getBooleanValue()){
						result = new BooleanQuery(true);
					}
				}
				ExpressionArray.deallocate(comp);
			}
			else{
				result = getProperty("is" + name);
			}
		}
		else if (prop.equalsIgnoreCase("canSimplify")) {
			return new BooleanQuery(canSimplify());
		}
		else if (prop.equalsIgnoreCase("canCombineLikeTerms")) {
			return new BooleanQuery(canCombineLikeTerms());
		}
		else if (prop.equalsIgnoreCase("canMultiplyThrough")) {
			return new BooleanQuery(canMultiplyThrough());
		}
		else if (prop.equalsIgnoreCase("canReduceFractions")) {
			return new BooleanQuery(canReduceFractions());
		}
		else if (prop.equalsIgnoreCase("canCombineLikeTermsWhole")) {
			return new BooleanQuery(canCombineLikeTermsWhole());
		}
		else if (prop.equalsIgnoreCase("canMultiplyThroughWhole")) {
			return new BooleanQuery(canMultiplyThroughWhole());
		}
		else if (prop.equalsIgnoreCase("canReduceFractionsWhole")) {
			return new BooleanQuery(canReduceFractionsWhole());
		}
		else if (prop.equalsIgnoreCase("canDistributeMultiplication")) {
			return new BooleanQuery(canDistribute(DISTNUM));
		}
		else if (prop.equalsIgnoreCase("canDistributeDivision")) {
			return new BooleanQuery(canDistribute(DISTDEN));
		}
		else if (prop.equalsIgnoreCase("canDistributeDivisionWhole")) {
			return new BooleanQuery(canDistributeWhole(DISTDEN));
		}
		else if (prop.equalsIgnoreCase("canDistribute")) {
			return new BooleanQuery(canDistribute(DISTBOTH));
		}
                else if (prop.equalsIgnoreCase("canFactor")){
                    return new BooleanQuery(canFactor());
                }
                else if (prop.equalsIgnoreCase("canSubstConstants")){
                    return new BooleanQuery(canSubstConstants());
                }
                else if (prop.length() > 9 && prop.substring(0,9).equalsIgnoreCase("canFactor")){
                    String fact = prop.substring(10);
                    try{
                        Expression ex = new Equation(fact).getLeft();
                        return new BooleanQuery(canFactor(ex));
                    }
                    catch(BadExpressionError bee){
                        /*an unparseable expression can't be factored*/
                        return new BooleanQuery(false);
                    }
                }
		else if (prop.equalsIgnoreCase("sign word")) {
			if (isNegative())
				return new StringQuery("negative");
			else
				return new StringQuery("positive");
		}
		else
			throw new NoSuchFieldException("expression "+toString()+" does not have property: "+prop);
		return result;
	}
	
	//setProperty assumes that the property is a display attribute (it should probably check this...)
	public void setProperty(String property, String value) throws NoSuchFieldException {
		if (displayAttributes == null)
			displayAttributes = new Vector();
		displayAttributes.addElement(new DisplayAttribute(property,value));
	}

	public Queryable evalQuery(String[] query) throws NoSuchFieldException{
		return StandardMethods.evalQuery(query,this);
	}

	public Queryable evalQuery(String query) throws NoSuchFieldException {
		return StandardMethods.evalQuery(query,this);
	}
	
	//applyOp
	public Queryable applyOp(String op,Vector args) throws NoSuchFieldException {
		if (isBinaryOp(op))
			return applyBinaryOp(op,args);
		else if(isUnaryOp(op)){
			return applyUnaryOp(op,args);
		}
		else {
			Queryable applyResult = StandardMethods.applyOp(op,args);
			if (op.equalsIgnoreCase("set")) //little hack here -- we want to return the expression for a "set"
				return this;
			else
				return applyResult;
		}
	}
	
	//applyBinaryOp performs a binary operation on arguments produced by the script. We support the
	//big 4: add, subtract, multiply and divide
	//The arguments do not need to be expressions (they might be TermInPolys, e.g.). If they aren't expressions,
	//we convert them to strings and then parse the strings.
	public static Queryable applyBinaryOp(String op,Vector args) throws NoSuchFieldException {
		//mmmBUG: does it matter that this never calls setMaintainVarList(true)?
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(Expression.getMaintainVars());
		Queryable arg1 = (Queryable)(args.elementAt(0));
		Queryable arg2 = (Queryable)(args.elementAt(1));
		Expression result=null;
		Expression arg1Ex;
		Expression arg2Ex;
		
		try {
			if (arg1 instanceof Expression)
				arg1Ex = (Expression)arg1;
			else {
				String arg1String = arg1.getStringValue();
				arg1Ex = sm.parse(arg1String);
			}
			if (arg2 instanceof Expression)
				arg2Ex = (Expression)arg2;
			else {
				String arg2String = arg2.getStringValue();
				arg2Ex = sm.parse(arg2String);
			}
			if (op.equalsIgnoreCase("add"))
				result = arg1Ex.add(arg2Ex);
			else if (op.equalsIgnoreCase("subtract"))
				result = arg1Ex.subtract(arg2Ex);
			else if (op.equalsIgnoreCase("multiply"))
				result = arg1Ex.multiply(arg2Ex);
			else if (op.equalsIgnoreCase("divide"))
				result = arg1Ex.divide(arg2Ex);
			else
				throw new NoSuchFieldException("Unknown operator for binary op: "+op);
		}
		catch (ParseException ex) {
			System.out.println("Parse exception "+ex+" applying "+op+" to "+arg1.getStringValue()+" and "+arg2.getStringValue());
		}
		return result;
	}
	
	public static boolean isBinaryOp(String op) {
		return (op.equalsIgnoreCase("add") ||
				op.equalsIgnoreCase("subtract") ||
				op.equalsIgnoreCase("multiply") ||
				op.equalsIgnoreCase("divide"));
	}

	public static Queryable applyUnaryOp(String op,Vector varg) throws NoSuchFieldException{
		//mmmBUG: does it matter that this never calls setMaintainVarList(true)?
		SymbolManipulator sm = new SymbolManipulator();
		sm.setMaintainVarList(Expression.getMaintainVars());
		Queryable arg = (Queryable)(varg.elementAt(0));
		Expression result = null;
		Expression argEx;

		try{
			if(arg instanceof Expression){
				argEx = (Expression)arg;
			}
			else{
				argEx = sm.parse(arg.getStringValue());
			}

			if(op.equalsIgnoreCase("simplify")){
				result = argEx.simplify();
			}
			else if(op.equalsIgnoreCase("standardize")){
				//mmmBUG: I guess this is really a binary op, then?
				result = argEx.standardize(DISTBOTH);
			}
			else if(op.equalsIgnoreCase("sort")){
				result = argEx.sort();
			}
			else if(op.equalsIgnoreCase("distribute")){
				//mmmBUG: I guess this is really a binary op, then?
				result = argEx.distribute(DISTBOTH);
			}
			else if(op.equalsIgnoreCase("unfence")){
				result = argEx.unfence();
			}
			else if(op.equalsIgnoreCase("multiplyThrough")){
				result = argEx.multiplyThrough();
			}
			else if(op.equalsIgnoreCase("combineLikeTerms")){
				result = argEx.combineLikeTerms();
			}
			else if(op.equalsIgnoreCase("reduceFractions")){
				result = argEx.reduceFractions();
			}
			else{
				throw new NoSuchFieldException("Unknown operator for unary op: " + op);
			}
		}
		catch(ParseException ex){
			System.out.println("Parse exception "+ex+" applying "+op+" to "+arg.getStringValue());
		}

		return result;
	}

	public static boolean isUnaryOp(String op){
		return (op.equalsIgnoreCase("simplify") ||
				op.equalsIgnoreCase("standardize") ||
				op.equalsIgnoreCase("sort") ||
				op.equalsIgnoreCase("distribute") ||
				op.equalsIgnoreCase("unfence") ||
				op.equalsIgnoreCase("multiplyThrough") ||
				op.equalsIgnoreCase("combineLikeTerms") ||
				op.equalsIgnoreCase("reduceFractions"));
	}

	public Number getNumberValue() {
		if (this instanceof NumericExpression)
			return ((NumericExpression)this).getValue();
		else
			throw new ClassCastException("Can't cast "+this+" to a number");
	}
	public boolean getBooleanValue() {
		throw new ClassCastException("Can't cast "+this+" to a boolean");
	}
	public String getStringValue() {
		return toString();
	}
	public Queryable[] getArrayValue() {
		Queryable[] result = new Queryable[1];
		result[0] = this;
		return result;
	}

	public int hashCode(){
		return debugForm().hashCode();
	}
	
	//addToArray adds an item to the array, growing it, if necessary
	public static Expression[] addToArray(Expression newExp,Expression current[],int curSize) {
		Expression arrayToUse[] = current;
		if (curSize == current.length) {
			arrayToUse = new Expression[2*current.length];
			for (int i=0;i<curSize;++i)
				arrayToUse[i] = current[i];
		}
		arrayToUse[curSize] = newExp;
		return arrayToUse;
	}
		
	//addToIntArray adds an item to the array, growing it, if necessary
	public static int[] addToIntArray(int newItem,int current[],int curSize) {
		int arrayToUse[] = current;
		if (curSize == current.length) {
			arrayToUse = new int[2*current.length];
			for (int i=0;i<curSize;++i)
				arrayToUse[i] = current[i];
		}
		arrayToUse[curSize] = newItem;
		return arrayToUse;
	}		


}

//These methods seemed like a good idea, but reflection is so slow (at least in MRJ2.0) that they're painful to use
//Maybe it'll get better some day...

/*
	//doSimplificationStep does the requested simplification, first on the components of the Expression
	//and then on the full expression itself
	private Expression doSimplificationStep(String step) {
		try {
			Class exClass = Class.forName("cmu.sm.Expression");
			Class[] parameters = new Class[0];
			Method simpMethod = exClass.getMethod(step,parameters);
			String wholeMethodName = step+"Whole";
			Method wholeMethod = exClass.getMethod(wholeMethodName,parameters);
			Object[] args = new Object[0];

			Expression composed;
			if (canHaveComponents()) {
				Vector components = getComponents();
				//recursively simplify
				for (int i=0;i<components.size();++i) {
					Expression thisComp = (Expression)(components.elementAt(i));
					System.out.println(step+" on "+thisComp);
					Object result = simpMethod.invoke(thisComp,args);
					System.out.println("   results in "+result);
					components.setElementAt(result,i);
				}
				composed = buildFromComponents(components);
			}
			else
				composed = this;
			return (Expression)(wholeMethod.invoke(composed,args));
		}
		catch (ClassNotFoundException err) {
			System.out.println(err);
			return this;
		}
		catch (NoSuchMethodException err) {
			System.out.println(err);
			return this;
		}
		catch (IllegalAccessException err) {
			System.out.println(err);
			return this;
		}
		catch (InvocationTargetException err) {
			Throwable exception = err.getTargetException();
			System.out.println(err+exception.toString());
			return this;
		}			
	}
	
	//doSimplificationTest tests whether the requested simplification can be performed, first on the components of the Expression
	//and then on the full expression itself
	private boolean doSimplificationTest(String step) {
		try {
			Class exClass = Class.forName("cmu.sm.Expression");
			Class[] parameters = new Class[0];
			Method testMethod = exClass.getMethod(step,parameters);
			String wholeMethodName = step+"Whole";
			Method wholeMethod = exClass.getMethod(wholeMethodName,parameters);
			Object[] args = new Object[0];

			boolean canDo=false;
			if (canHaveComponents()) {
				Vector components = getComponents();
				//check recursively
				for (int i=0;i<components.size() && !canDo;++i) {
					Expression thisComp = (Expression)(components.elementAt(i));
					Boolean result = (Boolean)(testMethod.invoke(thisComp,args));
					if (result.booleanValue() == true)
						canDo = true;
				}
			}
			if (!canDo) {
				Boolean wholeResult = (Boolean)(wholeMethod.invoke(this,args));
				canDo = wholeResult.booleanValue();
			}
			return canDo;
		}
		catch (ClassNotFoundException err) {
			System.out.println(err);
			return false;
		}
		catch (NoSuchMethodException err) {
			System.out.println(err);
			return false;
		}
		catch (IllegalAccessException err) {
			System.out.println(err);
			return false;
		}
		catch (InvocationTargetException err) {
			Throwable exception = err.getTargetException();
			System.out.println(err+exception.toString());
			return false;
		}			
	}
*/

