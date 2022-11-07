/**
 * Describe class AlgebraExpPoly here.
 *
 *
 * Created: Mon Aug 01 18:37:29 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.text.ParseException;

import mylib.MathLib;
import edu.cmu.pact.Utilities.trace;

public class AlgebraExpPoly extends AlgebraExp {

    public boolean isPolynomial() { return true; }
    public boolean isTerm() {
	return getOp().equals( "*" ) ||
	    getOp().equals( "/" );
    }
    public boolean isValidPolynomial() {
	return getFirstTerm() != null && getSecondTerm() != null;
    }

    public boolean isZero() { return false; }

    /**
     * Creates a new <code>AlgebraExpPoly</code> instance.
     *
     */
    public AlgebraExpPoly() {

    }

    /**
     * Creates a new <code>AlgebraExp</code> instance.
     *
     */
    public AlgebraExpPoly( String op, AlgebraExp firstT, AlgebraExp secondT ) {

	setOp( op );
	setFirstTerm( firstT );
	setSecondTerm( secondT );
	containsDecimals=firstT.isDecimal()||secondT.isDecimal();
    }

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    String getVarName() {

	String exp = getFirstTerm().getVarName();
	if ( exp == null ) {
	    exp = getSecondTerm().getVarName();
	}

	return exp;
    }

    String getCoefficient() {

	String coefficient = null;

	if ( getFirstTerm().isTerm() && getSecondTerm().isTerm() &&
	     ( getOp().equals( "*" ) || getOp().equals( "/" ) ) ) {

	    String c1 = getFirstTerm().getCoefficient();

	    if ( c1 != null ) {
		String c2 = getSecondTerm().isConstTerm() ?
		    getSecondTerm().getExp() : 
		    getSecondTerm().getCoefficient();
		
		coefficient = c1 + getOp() + c2;
	    }
	}
	return coefficient;
    }

    // - 
    // - Simplify expressions - * - * - * - * - * - * - * - * - * - 
    // - 

    public AlgebraExp evalArithmetic() {

	// trace.out(this + ".evalArithmetic() ... ");
	AlgebraExp evalArithmetic = null;

	if ( getSecondTerm().isTerm() ) {

	    evalArithmetic = doArithmetic();

	} else {

	    AlgebraExp normalSecond = getSecondTerm().evalArithmetic();
	    evalArithmetic = normalSecond.isZero() ?
		getFirstTerm().evalArithmetic() :
		doArithmetic( getOp(), getFirstTerm().evalArithmetic(), normalSecond ) ;
	}

	// trace.out(evalArithmetic);
	// trace.out(this + ".evalArithmetic() -> " + evalArithmetic);
	return evalArithmetic;
    }

    AlgebraExp doArithmetic() {
	return doArithmetic( getOp(), getFirstTerm().evalArithmetic(), getSecondTerm().evalArithmetic() );
    }

    // Assume that the expression "t1" is a term and the expression
    // "t2" is a normal term (i.e., the terms in the expression is
    // ordered), place t1 in an appropriate position in t2 with a
    // necessary calculation.
    // 
    AlgebraExp doArithmetic( String op, AlgebraExp t1, AlgebraExp t2 ) {
    	
	AlgebraExp doArithmetic = null;

	if ( op.equals( "+" ) ) {
	    
	    // trace.out("doArithmetic(" + t1 + "," + t2 + ")...");
	    
	    if ( t2.isZero() ) {

		doArithmetic = t1;

	    } else if ( t1.isTerm() && t2.isTerm() ||(t1.isSimpleFraction() && t2.isSimpleFraction()) ) {
		
		// trace.out("t2.isTerm()");
		doArithmetic = evalArithmeticAdd( t1, t2 );

	    } else if ( !t1.isTerm() && t2.isTerm() ) {
		
		doArithmetic = doArithmetic( op, t2, t1 );
		
	    } else if ( !t1.isTerm() && !t2.isTerm() ) {
		
		// trace.out("!t1.isTerm() : " + t1);
		/*
		doArithmetic = t1.comesBefore(t2) ?
		    new AlgebraExpPoly( op, t1, t2 ) :
		    new AlgebraExpPoly( op, t2, t1 ) ;
		*/
		doArithmetic = 
		    doArithmetic(op, t1.getSecondTerm(), doArithmetic(op, t1.getFirstTerm(), t2));

	    } else {

		AlgebraExp sameTerm = t2.lookupSameTypeTerm( "+", t1 );
		// trace.out( "doArithmetic: sameTerm = " + sameTerm );
		if ( sameTerm != null ) {
		    
		    AlgebraExp newTerm = evalArithmeticAdd( t1, sameTerm );
		    // trace.out("newTerm = " + newTerm);
		    doArithmetic = t2.replaceTerm( sameTerm, newTerm );

		} else {
		    
		    doArithmetic = new AlgebraExpPoly( op, t1, t2 );
		}
	    }
	    
	} else if ( op.equals( "*" ) ) {
	    
	    doArithmetic = evalArithmeticMult( t1, t2 );
	    
	} else if ( op.equals( "/" ) ) {
	    
	    doArithmetic = evalArithmeticDiv( t1, t2 );

	} else {
	
	    String msg = "Invalid expression: " + toString();
	    RuntimeException e = new RuntimeException( msg );
	    e.printStackTrace();
	}

	// doArithmetic migth eventually got a polynomial that has
	// null as its term
	if ( doArithmetic != null &&
	     doArithmetic.isPolynomial() &&
	     !doArithmetic.isValidPolynomial() ) {
	    doArithmetic = null;
	}

	return doArithmetic;
    }

    public AlgebraExp evalArithmetic_obsolete() {

	AlgebraExp t1 = getFirstTerm();
	AlgebraExp t2 = getSecondTerm();

	if ( getOp().equals( "+" ) ) {
	    
	    return evalArithmeticAdd( t1, t2 );
	    
	} else if ( getOp().equals( "*" ) ) {
	    
	    // trace.out("AlgebraExpPoly.evalArithmetic() -> Mult");
	    return evalArithmeticMult( t1, t2 );
	    
	} else if ( getOp().equals( "/" ) ) {
	    
	    return evalArithmeticDiv( t1, t2 );
	}

	String msg = "Invalid expression: " + toString();
	RuntimeException e = new RuntimeException( msg );
	e.printStackTrace();

	return null;
    }

    public AlgebraExp addTerm( AlgebraExp term ) {
    	AlgebraExp t1=this;
    	AlgebraExp t2=term;
		
	trace.out("eqfp", "addTerm(" + term + ") called upon " + this);
	
	if(t1.equals(t2))
		return new AlgebraExpPoly("*",t1,new AlgebraExpTerm("2"));
	
	if(t1.isParenQuanity() && t2.isParenQuanity())
	
	{
		
		//remove the coefficients
		String s1=t1.toString();
		String s2=t2.toString();
		int leftParenIndex1=s1.indexOf("(");
		int leftParenIndex2=s2.indexOf("(");
		int rightParenIndex1=s1.indexOf(")");
		int rightParenIndex2=s1.indexOf(")");
		try
		{
		AlgebraExp withoutCoe1=AlgebraExp.parseExp(s1.substring(leftParenIndex1+1,rightParenIndex1));
		AlgebraExp withoutCoe2=AlgebraExp.parseExp(s2.substring(leftParenIndex2+1,rightParenIndex2));
		if(withoutCoe1.equals(withoutCoe2))
		{
			
			AlgebraExp coe1=AlgebraExp.parseExp(s1.substring(0,leftParenIndex1));
			AlgebraExp coe2=AlgebraExp.parseExp(s2.substring(0,leftParenIndex2));
			return new AlgebraExpPoly("*",coe1.addTerm(coe2),t1);
		}
		}
		catch(ParseException e)
		{
			return null;
		}
		
		
		
	}

	if(t1.isSimpleFraction() && t2.isSimpleFraction())
	{
	
		AlgebraExp d1=t1.getDenominator();
		AlgebraExp d2=t2.getDenominator();
		if(!d1.isDecimal()&&!d2.isDecimal())
		{
			int d1value;
			int d2value;

			
			if(!d1.isVarTerm())
				d1value=Integer.parseInt(d1.toString());
			else
				return null;
			
			
			if(!d2.isVarTerm())
				d2value=Integer.parseInt(d2.toString());
			else
				return null;
			int commonDenominator=MathLib.lcm(d1value,d2value);
			
			try
			{
			AlgebraExp multiplier1=AlgebraExp.parseExp(String.valueOf(commonDenominator/d1value));
			AlgebraExp multiplier2=AlgebraExp.parseExp(String.valueOf(commonDenominator/d2value));
			
			return new AlgebraExpPoly("/",t1.getNumerator().multTerm(multiplier1).addTerm(t2.getNumerator().multTerm(multiplier2)),AlgebraExp.parseExp(String.valueOf(commonDenominator)));
			}
			catch (ParseException e)
			{
				return null;
			}
		}
	}
	
		return null;
    }

    public AlgebraExp divTerm( AlgebraExp term ) {

//	 trace.out(this + ".AlgebraExpPoly.divTerm(" + term + ")");
	AlgebraExp divTerm = null;
	AlgebraExp inverseTerm = null;

	if ( term.isZero() ) {

	    return null;

	} else if ( this.equals(term) ) {

	    return new AlgebraExpTerm( "1" );

	} else if ( term.isFraction() && !term.getNumerator().isZero() ) {

	    inverseTerm = new AlgebraExpPoly( "/",
					      term.getDenominator(),
					      term.getNumerator() );
	    divTerm = multTerm( inverseTerm );
	    
	} else if ( isFraction() ) {

	    inverseTerm = getNumerator().divTerm( term );

	    // trace.out("inverseTerm = " + inverseTerm);

	    if ( inverseTerm != null ) {

		if ( inverseTerm.isFraction() ) {
		    
		    AlgebraExp denominator =
			getDenominator().multTerm( inverseTerm.getDenominator() );
		    
		    if ( denominator != null && !denominator.isZero() ) {
			divTerm =
			    new AlgebraExpPoly( "/",
						inverseTerm.getNumerator(),
						denominator );
		    }
		    
		} else {
		    
		    divTerm =
			new AlgebraExpPoly("/", inverseTerm, getDenominator());
		}
	    }

	} else {

	    divTerm = new AlgebraExpPoly( "/", this, term );

	}

	// trace.out(" ==> " + divTerm);
	return divTerm;
    }

    public AlgebraExp multTerm( AlgebraExp term ) {

	AlgebraExp multTerm = null;
	if(term.isTerm())
		 term=term.evalArithmetic();
	
		if ( isFraction()) {

			
	    // trace.out(this + ".multTerm(" + term + ")");
			
	    AlgebraExp multiplier = term.divTerm( getDenominator() );
	    // trace.out("multiplier = " + multiplier);

	    if ( multiplier != null ) {

		if ( multiplier.isFraction() ) {

		    AlgebraExp numerator =
			getNumerator().multTerm( multiplier.getNumerator() );
		    
		    if ( numerator != null ) 
			multTerm =
			    new AlgebraExpPoly( "/",
						numerator,
						multiplier.getDenominator() );
		} else {
		    
		    multTerm = getNumerator().multTerm( multiplier );
		}
	    }

	} else if ( isAddition() ) {

	    AlgebraExp exp1 = getFirstTerm().multTerm( term );
	    AlgebraExp exp2 = getSecondTerm().multTerm( term );
	    if (exp1 != null && exp2 != null) 
		multTerm = new AlgebraExpPoly( getOp(), exp1, exp2 );

	} else { // isMultiplication()

	    AlgebraExp exp = getSecondTerm().multTerm( term );
	    if ( exp != null) 
		multTerm = new AlgebraExpPoly( getOp(), getFirstTerm(), exp );
	}
	
	
	
	return multTerm;
    }

    // -
    // - Look up for a particular term - * - * - * - * - * - * - * - * 
    // -

    


    // -
    // - Predicates - * - * - * - * - * - * - * - * - * - * - * - * -
    // -

    boolean isConstTerm() {
	return getFirstTerm().isConstTerm() &&
	    getSecondTerm().isConstTerm();
    }

    boolean isVarTerm() {
	return getFirstTerm().isVarTerm() || getSecondTerm().isVarTerm();
    }

    boolean equals( AlgebraExp term ) {

	return term.isPolynomial() &&
	    getOp().equals( term.getOp() ) &&
	    getFirstTerm().equals( term.getFirstTerm() ) &&
	    getSecondTerm().equals( term.getSecondTerm() );
    }

    // -
    // - Printing - * - * - * - * - * - * - * - * - * - * - * - * - *
    // -

    public String parseTree() {
	String parseTree =
	    "(" +
	    getOp() + " " +
	    getFirstTerm().parseTree() + " " +
	    getSecondTerm().parseTree() +
	    ")";
	return parseTree;
    }

    /**
     * Convert to plane string
     *
     * @return a <code>String</code> value
     */
    public String toString() {

	String term1 = getFirstTerm().toString();
	String term2 = getSecondTerm().toString();

	if ( getFirstTerm().isAddition() ) {
	    term1 = "(" + term1 + ")";
	}
	if  ( secondTermNeedParenthesis() ) {
	    term2 = "(" + term2 + ")"; 
	}

	String op = ( getOp().equals( "+" ) && term2.charAt(0) == '-' ) ?
	    "" :
	    getOp();
	return term1 + op + term2;
    }

    private boolean secondTermNeedParenthesis() {

	boolean answer = false;

	if ( !getOp().equals("+") && getSecondTerm().isPolynomial() ) {

	    answer = true;

	} else if ( getOp().equals("/") ) {

	    if ( getSecondTerm().isVarTerm() &&
		 !getSecondTerm().getCoefficient().equals("1") ) {

		answer = true;

	    } else if ( getSecondTerm().isConstTerm() &&
			getSecondTerm().toString().charAt(0) == '-' ) {

		answer = true;
	    }
	}
	return answer;
    }

}
