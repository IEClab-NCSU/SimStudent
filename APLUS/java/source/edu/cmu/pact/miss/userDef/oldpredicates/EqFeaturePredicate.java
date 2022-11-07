/**
 * f:/Project/CTAT/ML/ISS/miss/userDef/EqFeaturePredicate.java
 *
 *	Feature predicate must extend this class and implement
 *	Userfunction (for Jess external code) as well as Serializable
 *	(for Model Tracing).
 *
 *	A feature predicate returns Jess Value.  
 *
 * Created: Sat Feb 26 17:32:32 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 **/

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import mylib.MathLib;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.FeaturePredicate;

public abstract class EqFeaturePredicate extends FeaturePredicate {

    // -
    // - Construction - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Creates a new <code>EqFeaturePredicate</code> instance.
     *
     */
    public EqFeaturePredicate() {
    }

    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = =
    // Predicates and operators
    // 


    static private boolean isArithmeticExpression( String exp ) {
	
	boolean isArithmeticExpression = true;
	if (exp.indexOf(' ') > -1) {
	    isArithmeticExpression = false;
	}
	return isArithmeticExpression;
    }
    
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *
    // INFORMATION RETRIEVER - * - *- * - * - *- * - * - * - *
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *

    /**
     * Returns a coefficient of the given monomial term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    static public String coefficient( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	// trace.out("coefficient(" + term + ")...");

	String coefficient = null;
	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );
	    if ( exp.isVarTerm() ) {
		coefficient = exp.getCoefficient();
	    }
	} catch (java.text.ParseException e) {
	    e.printStackTrace();
	}
	/*
	catch (Exception e) {
	    trace.out("miss", "coefficient(" + term + ")...");
	    trace.out("miss", "GoalTest: " + RhsGoalTest.getGoalTest() );
	    trace.out("miss", "RhsState: " + RhsGoalTest.getRhsState() );
	    e.printStackTrace();
	    try {
		// trace.out("miss", "Enter any key to continue...");
		// System.in.read();
	    } catch (Exception ee) {
		ee.printStackTrace();
	    }
	}
	*/

	// trace.out("coefficient(" + term + ") = " + coefficient);
	return coefficient;
    }

    public String coefficient_obsolete( String term ) {
	
	String coefficient = null;

	if ( term.indexOf('/') == -1 ) {

	    if ( monomial( term ) != null && varTerm( term ) != null ) {
		
		if ( term.length() == 1 ) {
		    
		    coefficient = "1";
		    
		} else if ( term.length() == 2 && term.charAt(0) == '-' ) {
		    
		    coefficient = "-1";
		    
		} else {
		    
		    coefficient = term.substring( 0, term.length() -1 );
		}
	    }
	}

	return coefficient;
    }

    public String hasCoefficient( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String hasCoefficient = null;

	if ( isVarTerm( term ) ) {

	    // try {
		String coefficient = coefficient( term );
		if ( coefficient != null && !coefficient.equals( "1" ) )
		    hasCoefficient = "T";
		    
	    /*
	    } catch (NullPointerException e) {
		trace.out("hasCoefficient( " + term + " )");
		e.printStackTrace();
	    }
	    */
	}
	return hasCoefficient;
    }

    public String firstVarTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String firstVarTerm = null;
	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );
	    if (exp.isPolynomial()) {
		AlgebraExp firstVarExp = exp.getFirstVarTerm();
		if (firstVarExp != null) {
		    firstVarTerm = firstVarExp.toString();
		}
	    }
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}
	// trace.out("firstVarTerm(" + term + ") = |" + firstVarTerm + "|");
	return firstVarTerm;
    }

    public String firstVarTerm_obsolete( String term ) {

	String firstVarTerm = (hasVarTerm( term ) != null) ? "" : null;
	
	if ( firstVarTerm != null ) {

	    ArrayList /* String */ termTokens = tokenizeTerms( term );
	    for (int i = 0; i < termTokens.size(); i++) {
		String theTerm = (String)termTokens.get(i);
		if ( varTerm( theTerm ) != null ) {
		    firstVarTerm = trimPlusSignInFront( theTerm );
		    break;
		}
	    }
	}
	// trace.out("firstVarTerm(" + term + ") = |" + firstVarTerm + "|");
	return firstVarTerm;
    }
    
    /**
     * Returns the first term of the given polynmial term
     * 
     * @param term
     * @return
     */
    public String firstTerm( String term ) {
	
	if (!isArithmeticExpression( term )) return null;
	
	String firstTerm = null;
	
	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );
	    
	    if ( exp.isPolynomial() ) {
		
		AlgebraExp getFirstTerm = exp.getFirstTerm();
		
		if ( getFirstTerm != null ) {
		    firstTerm = getFirstTerm.toString();
		}
	    }
	} catch ( ParseException e ) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}
	
	return firstTerm;
    }

    /**
     * Returns the last term of a given polynomial term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String lastTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String lastTerm = null;

	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );

	    if ( exp.isPolynomial() ) {

		AlgebraExp getLastTerm = exp.getLastTerm();

		if ( getLastTerm != null ) {
		    lastTerm = getLastTerm.toString();
		}
	    }

	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("lastTerm(" + term + ") = " + lastTerm);
	return lastTerm;
    }

    public String lastTerm_Obsolete( String term ) {

	/*
	if ( term.equals("0x") ) {
	    Exception e = new Exception();
	    e.printStackTrace();
	}
	*/

	String lastTerm = (polynomial( term ) != null) ? "" : null;
	
	if ( lastTerm != null ) {
	    
	    ArrayList /* String */ termTokens = tokenizeTerms( term );
	    lastTerm = (String)termTokens.get(termTokens.size()-1);
	    lastTerm = trimPlusSignInFront( lastTerm );
	}
	// trace.out("lastTerm(" + term + ") = " + lastTerm);
	return lastTerm;
    }

    public String lastConstTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String lastConstTerm = null;

	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );
	    
	    if ( exp.isPolynomial() ) {
		
		AlgebraExp lastConstExp = exp.getLastConstTerm();
		if ( lastConstExp != null ) {
		    lastConstTerm = lastConstExp.toString();
		}
	    }

	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("lastConstTerm(" + term + ") = " + lastConstTerm);
	return lastConstTerm;

    }

    public String lastConstTerm_obsolete( String term ) {

	String lastConstTerm = (hasConstTerm( term ) != null) ? "" : null;

	if ( lastConstTerm != null ) {

	    ArrayList /* String */ termTokens = tokenizeTerms( term );
	    for ( int i = termTokens.size(); i > 0; i-- ) {

		String theTerm = (String)termTokens.get(i-1);
		if ( varTerm( theTerm ) == null ) {
		    lastConstTerm = trimPlusSignInFront( theTerm );
		    break;
		}
	    }
	}

	// trace.out("lastConstTerm(" + term + ") = " + lastConstTerm);
	return lastConstTerm;
    }

    /**
     * Subtract a last constant term (if any) from the given term
     *
     * @param term a <code>String</code> value
     **/
    public String cancelLastConstTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String cancelLastConstTerm = null;

	String lastConstTerm = lastConstTerm( term );
	if ( lastConstTerm != null ) {

	    cancelLastConstTerm =
		addTermBy( term, reverseSign( lastConstTerm ) );
	    
	}
	return cancelLastConstTerm;
    }

    /**
     * Returns a variable name of the given monomial term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String varName( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String name = null;

	for ( int i = 0; i < term.length(); i++ ) {

	    char c = term.charAt(i);
	    if ( ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ) {
		name = "" + c;
		break;
	    }
	}
	// trace.out("varName(" + term + ")=" + name);
	return name;
    }

    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *
    // PREDICATES - * - *- * - * - *- * - * - *- * - * - * - *
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *

    /**
     * Returns "T" if the term is "1"
     * 
     * @param term
     * @return
     */
    public String isOne( String term ) {
	return term.matches("1([.]0+)?") ? "T" : null;
    }
    
    /**
     * Returns non-null if the term can be simplified.  This is an
     * ad-hoc solution for feature extraction for do-arith-{lhs|rhs}
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     **/
    public String canBeSimplified( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String canBeSimplified = null;

	String evalTerm = evalArithmetic( term );
	if ( evalTerm != null && !evalTerm.equals( term ) ) {

	    canBeSimplified = "T";
	}

	return canBeSimplified;
    }

    /**
     * Thu May 11 16:31:48 2006
     * 
     * Added to see if dropping canBeSimplified and adding two "primitive" predicates 
     * canBeSimplifiedTerm and canBeSimplifiedPolynomial would eventually 
     * have SimSt (FOIL, indeed) compose production rules with LHS conjuncts
     * 
     * @param term
     * @return
     */
    public String canBeSimplifiedMonomial( String term ) {
	
	if (!isMonomial(term)) return null;

	String canBeSimplifiedTerm = null;
	String evalTerm = evalArithmetic( term );
	if ( evalTerm != null && !evalTerm.equals( term ) ) {
	    canBeSimplifiedTerm = "T";
	}
	return canBeSimplifiedTerm;
    }
    
    public String canBeSimplifiedPolynomial( String term ) {
	
	if (!isPolynomial(term)) return null;
	
	String canBeSimplifiedPolynomial = null;
	String evalTerm = evalArithmetic( term );
	if ( evalTerm != null && !evalTerm.equals( term ) ) {
	    canBeSimplifiedPolynomial = "T";
	}
	return canBeSimplifiedPolynomial;
    }
    /*
    /**
     * 
     * @param term a <code>String</code> representing an AlgebraExp
     * @return "T" if term contains constants which can be simplifed 
     */
    /**
     * Returns non-null if the expression has a variable term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String hasVarTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String hasVarTerm = null;

	try {
	    if ( hasVarTerm( AlgebraExp.parseExp( term ) ) ) {
		hasVarTerm = "T";
	    }
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	return hasVarTerm;
    }

    public String hasVarTerm_obsolete( String term ) {

	String hasVarTerm = null;

	if ( hasVarTerm( tokenizeTerms( term ) ) ) {
	    hasVarTerm = "T";
	}
	return hasVarTerm;
    }

    public String hasConstTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String hasConstTerm = null;

	if ( hasConstTerm( tokenizeTerms( term ) ) ) {
	    hasConstTerm= "T";
	}
	return hasConstTerm;
    }

    /**
     * Returns T, if the given term is a monomial and a variable term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     **/
    public String varTerm( String term ) {
	
	if (!isArithmeticExpression( term )) return null;
	
	String returnVal = null;

	if ( isMonomial( term ) ) {

	    /*
	    char lastLetter = term.toUpperCase().charAt( term.length() -1 );
	    if ( 'A' <= lastLetter && lastLetter <= 'Z' ) {
		returnVal = "T";
	    }
	    */

	    // Tue Oct 11 13:51:16 2005
	    // 3x/(-1), -4/x must be varTerm
	    term = term.toUpperCase();
	    for ( int i = 0; i < term.length(); i++) {

		char c = term.toUpperCase().charAt( i );
		if ( 'A' <= c && c <= 'Z' ) {
		    returnVal = "T";
		    break;
		}

	    }
	}
	// trace.out("varTerm(" + term + ")=" + returnVal);
	return returnVal;
    }

    /**
     * Monomial shouldn't be a fraction, neither a polynomial
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String monomial( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	/**
	 * Tue Oct 11 13:34:55 2005
	 * 2x/5 must be monomial
	 **/
	// return fractionTerm( term ) == null && polynomial( term ) == null ?
	return polynomial( term ) == null ? "T" : null;
    }

    public String polynomial( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	ArrayList termTokens = tokenizeTerms( term );
	return (termTokens.size() == 1) ? null : "T";
    }

    /**
     * Returns non-null if the given expression consists of same type
     * of terms (var-term or const-term)
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     **/
    public String homogeneous( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String homogeneous = null;

	ArrayList /* String */ termTokens = tokenizeTerms( term );

	String firstTerm = null;
	// try {
	    firstTerm = (String)termTokens.get(0);
	    termTokens.remove(0);
	/*
	} catch (Exception e) {
	    trace.out("homogeneous(" + term + ")");
	    e.printStackTrace();
	}
	*/

	if ( varTerm( firstTerm ) != null ) {
	    if ( !hasConstTerm( termTokens ) ) {
		homogeneous = "T";
	    }
	} else {
	    if ( !hasVarTerm( termTokens ) ) {
		homogeneous = "T";
	    }
	}
	// trace.out("homogeneous(" + term + ") = " + homogeneous);
	return homogeneous;
    }


    /**
     * Describe <code>fractionTerm</code> method here.
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String isFractionTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	AlgebraExp termParsed = null;
	try {
	    termParsed = AlgebraExp.parseExp( term );
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		trace.out("missalgebra", "fractionTerm(" + term + ")");
		e.printStackTrace();
	    }
	}
	return termParsed.isFraction() ? "T" : null;
    }

    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *
    // MODIFY or REPLACE - * - * - *- * - * - *- * - * - * - *
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *

    /**
     * Remove a coefficiant from a given monomial term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String ripCoefficient( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String ripCoefficient = null;

	AlgebraExp exp = null;
	try {

	    exp = AlgebraExp.parseExp( term );

	    // A list of SHOULDNTs
	    if ( !exp.isTerm() ) {
		return null;
	    }
	    if ( exp.isFraction() &&
		 ( exp.getDenominator().isPolynomial() ||
		   exp.getNumerator().isPolynomial() ) ) {
		return null;
	    }

	    // Only when the expression is a valiable term, ...
	    if ( exp.getVarName() != null &&
		 !exp.getVarName().equals("") ) {
		ripCoefficient = exp.getVarName();
	    }

	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}
	/*
	catch (Exception e) {
	    trace.out("ripCoefficient(" + term + ") .... ");
	    e.printStackTrace();
	}
	*/

	// trace.out("ripCoefficient(" + term + ")=" + ripCoefficient);
	return ripCoefficient;
    }

    public String ripCoefficient_obsolete( String term ) {

	String val = (monomial( term ) != null ? varName( term ) : null);
	// trace.out("ripCoefficient(" + term + ")=" + val);
	return val;
    }

    /**
     * Remove the first variable term from the given polynomial
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String removeFirstVarTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String removeFirstVarTerm = null;
	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );

	    if ( exp.isTerm() && exp.isVarTerm() ) {
		return "0";
	    }

	    AlgebraExp newExp = exp.removeFirstVarTerm();
	    if ( newExp != null ) {
		removeFirstVarTerm = newExp.toString();
	    }
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("removeFirstVarTerm(" + term + ")=" + removeFirstVarTerm);
	return removeFirstVarTerm;
    }

    public String removeFirstVarTerm_obsolete( String term ) {

	String result = (polynomial( term ) != null) ? "" : null;

	if ( result != null ) {
	    
	    boolean termRemoved = false;
	    
	    ArrayList /* String */ termTokens = tokenizeTerms( term );
	    for ( int i = 0; i < termTokens.size(); i++ ) {

		String theTerm = (String)termTokens.get(i);
		if ( !termRemoved && (varTerm( theTerm ) != null) ) {
		    termRemoved = true;
		} else {
		    result += theTerm;
		}
	    }
	}
	if ( result != null ) {
	    result = trimPlusSignInFront( result );
	}

	// trace.out("removeFirstVarTerm(" + term + ")=" + result);
	return result;
    }

    /**
     * Remove the last term from a given polynomial
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String removeLastTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String removeLastTerm = null;
	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );
	    AlgebraExp newExp = exp.removeLastTerm();
	    if (newExp != null) {
		removeLastTerm = newExp.toString();
	    }
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("removeLastTerm(" + term + ") = " + removeLastTerm);
	return removeLastTerm;
    }

    public String removeLastTerm_obsolete( String term ) {

	String removeLastTerm = null;

	if ( polynomial( term ) != null ) {

	    int indexPlus = term.lastIndexOf( '+' );
	    int indexMinus = term.lastIndexOf( '-' );
	    int theIndex = ( indexPlus < indexMinus ) ? indexMinus : indexPlus;
	    
	    removeLastTerm = term.substring( 0, theIndex );

	}

	// trace.out("removeLastTerm(" + term + ") = " + removeLastTerm);
	return removeLastTerm;
    }

    /**
     * Returns an expression that drops the first constant term from
     * the given expression
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     **/
    public String removeLastConstTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String removeLastConstTerm = null;

	try {

	    AlgebraExp exp = AlgebraExp.parseExp( term );

	    if ( exp.isTerm() && exp.isConstTerm() ) {
		return "0";
	    }

	    AlgebraExp newExp = exp.removeLastConstTerm();
	    if (newExp != null) {
		removeLastConstTerm = newExp.toString();
	    }

	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("removeLastConstTerm(" + term + ")=" + removeLastConstTerm);
	return removeLastConstTerm;
    }

    public String removeLastConstTerm_Obsolete( String term ) {

	String result = (polynomial( term ) != null) ? "" : null;

	if ( result != null ) {

	    boolean termRemoved = false;

	    ArrayList /* String */ termTokens = tokenizeTerms( term );
	    for ( int i = termTokens.size(); i > 0; i-- ) {

		String theTerm = (String)termTokens.get(i-1);
		// trace.out(theTerm);
		if ( (!termRemoved) && (varTerm( theTerm ) == null) ) {
		    termRemoved = true;
		} else {
		    result = theTerm + result;
		}
	    }
	}
	if ( result != null ) {
	    result = trimPlusSignInFront( result );
	}
	// trace.out("removeLastConstTerm(" + term + ")=" + result);
	return result;
    }

    /**
     * Reverse the sign of the monomial term
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String reverseSign( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	String reverseSign = null;

	try {
	    AlgebraExp exp = AlgebraExp.parseExp(term);
	    if(exp.isMultiplication())
	    	return mulTermBy(reverseSign(exp.getFirstTerm().toString()),exp.getSecondTerm().toString());
	    	
	    if ( exp.isTerm() ) {

		if ( exp.isFraction() ) {
		    
		    // trace.out("1stTerm: " + exp.getFirstTerm());
		    // trace.out("2ndTerm: " + exp.getSecondTerm());
			// trace.out(term);
		    String t1 = reverseSign( exp.getFirstTerm().toString() );
		    String t2 = exp.getSecondTerm().toString();
		    if ( isVarTerm( t2 ) ) t2 = "(" + t2 + ")";
		    return t1 + "/" + t2;

		} else {

		    String c = exp.isVarTerm() ? exp.getCoefficient() : term;
		    try {
		    String rc;
		    if(exp.isDecimal())
		    	rc=String.valueOf(Double.parseDouble(c)*-1);
		    else
		    	rc= "" + (Integer.parseInt(c) * (-1));
			String v = exp.getVarName();
			
			if ( !v.equals("") ) {
			    if ( rc.equals("1") ) {
				rc = "";
			    } else if ( rc.equals("-1") ) {
				rc = "-";
			    }
			}
			reverseSign = rc + v;

		    } catch (Exception e) {

			trace.out("eqfp", "reverseSign(" + term + ")");
		    }
		}

	    } else {

		if ( term.charAt(0) == '-' ) {
		    term = term.substring(1);
		    if ( term.charAt(0) == '(' ) {
			term = term.substring(0,term.length());
		    }
		} if ( term.charAt(0) == '(' ) {
		    term = "-" + term;
		} else {
		    term = "-(" + term + ")";
		}
		return term;
	    }
	
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("reverseSign(" + term + ") = " + reverseSign);
	return reverseSign;
    }
    /**
     * NOTE doesn't handle factoring out varibles
     * NOTE doesn't handle fractions
     *  
     * @param term String representing an AlgebraExp
     * @return the expression with the largest common term factored out, or null if the expression can't be factored
     */
    public String factorOutCommonTerm(String term)
    {
    	try
    	{
    		AlgebraExp exp=AlgebraExp.parseExp(term);
    		AlgebraExp factor=exp.getFactor();
    		if(factor==null)
    			return null;
    		AlgebraExp innerExp;
    		//innerExp=exp.divByConst(Integer.parseInt(factor.toString()));
    		innerExp=exp.multTerm(factor.Invert());
    		return factor+"("+innerExp+")";
    	}
    	catch(ParseException e)
    	{
    		return null;
    	}
    }
    /**
     * Note that if there are multiple quanitity which can be distrubuted(e.g. 5(x+2)+3(x+4), only one will be  
     * does not handle variable or fractional fractors
     * @param term String representing an AlgebraExp
     * @return a string with a factor distributed across a parentheesized expression, null if distrubution doesn't apply
     */
    public String distributeFactor(String term)
    {
    	if(!term.matches(".*[0-9]+[*]?[(].+[)].*"))
    		return null;
    	
    	int leftParenWithMulIndex=term.indexOf("*(");
    	int leftParenIndex=term.indexOf("(");
    	int rightParenIndex=term.indexOf(")");
    	int factorStartIndex=findFirstDistributableFactorIndex(term);
    	int factorEndIndex;
    	if(leftParenWithMulIndex==-1)
    		factorEndIndex=leftParenIndex-1;
    	else
    		factorEndIndex=leftParenWithMulIndex-1;
    	String factor=term.substring(factorStartIndex,factorEndIndex+1);
    	//get the expression between the parens
    	String expression=term.substring(leftParenIndex+1,rightParenIndex);
    	AlgebraExp exp;
    	try
    	{
    		exp=AlgebraExp.parseExp(expression);
    		exp=exp.multTerm(AlgebraExp.parseExp(factor));
    	}
    		
    		catch(ParseException e)
    		{
    			return null;
    		}
    	
    	
    	
    	//replace the parentheized expression with the new expresion
    	String before="";
    	String after="";
    	if(factorStartIndex!=0)
    		before=term.substring(0,factorStartIndex);
    	if(rightParenIndex!=term.length()-1)
    		after=term.substring(rightParenIndex+1);
    	
    	return before+exp+after;
    	
    		
    	
    	
    }
    

    /**
     * Inverse a term (e.g., x -> 1/x)
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String inverseTerm( String term ) {

	if (!isArithmeticExpression( term )) return null;
	
	if ( isPolynomial( term ) ) {
	    return null;
	}

	String inverseTerm = null;

	try {

	    String evalTerm = evalArithmetic(term);
	    if ( evalTerm != null && !evalTerm.equals( "0" ) ) {
		
		AlgebraExp exp = AlgebraExp.parseExp( term );
		AlgebraExp newExp = null;
		
		if ( exp.isFraction() ) 
		{
		    
		    	newExp = new AlgebraExpPoly( "/",
						 exp.getDenominator(),
						 exp.getNumerator() );
		} 
		else 
		{
			
				newExp = new AlgebraExpPoly( "/",
						 AlgebraExp.parseExp( "1" ),
						 exp );
			
		}
		inverseTerm = newExp.toString();
	    }
		
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		trace.out("missalgebra", "inverseTerm(" + term + ")...");
		e.printStackTrace();
	    }
	}
	
	return inverseTerm;
    }

    public String inverseTerm_obsolete_II( String term ) {

	// trace.out("inverseTerm(" + term + ")...");

	String inverseTerm = null;

	// try {

	    if ( !term.equals( "0" ) ) {

		try {
		    AlgebraExp exp = AlgebraExp.parseExp( term );
		    AlgebraExp newExp = exp.inverseTerm();
		    if ( newExp != null ) {
			inverseTerm = newExp.toString();
		    }
		} catch (java.text.ParseException e) {
		    if (trace.getDebugCode("missalgebra")) {
			e.printStackTrace();
		    }
		}

		// inverseTerm( "8x/4" ) must be "4/(8x)" not "4/8x"
		if ( inverseTerm != null ) {
		    String denominator = denominator( inverseTerm );
		    // trace.out("denominator: " + denominator);
		    if ( denominator != null ) {
			String numerator = numerator( inverseTerm );
			// trace.out("numerator: " + numerator);
			if ( numerator != null ) {
			    inverseTerm = divTermBy( numerator, denominator );
			}
		    }
		}

	    } else {

		inverseTerm = term;
	    }

	/*
	} catch (Exception e) {
	    trace.out("inverseTerm(" + term + ")...");
	    e.printStackTrace();
	}
	*/

	return inverseTerm;
    }
    
    public String inverseTerm_obsolete( String term ) {

	String inverseTerm = null;
	
	if ( term.equals( "0" ) ) {

	    inverseTerm = term;

	} else if ( !term.equals("") && isMonomial(term) ) {
	    
	    switch ( term.charAt(0) ) {
	    case '-':
		inverseTerm = term.substring( 1, term.length() );
		break;
	    case '+':
		inverseTerm = "-" + term.substring( 1, term.length() );
		break;
	    default:
		inverseTerm = "-" + term;
		break;
	    }
	}

	// trace.out("inverseTerm(" + term + ") = " + inverseTerm);
	return inverseTerm;
    }

    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *
    // ARITHMETIC OPERATIONS - * - *- * - * - *- * - * - * - *
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *

    /**
     * Just write an expression, not do any algebra
     *
     * @param t1 a <code>String</code> value
     * @param t2 a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String divTermBy( String t1, String t2 ) {

	if (!isArithmeticExpression( t1) || !isArithmeticExpression(t2)) return null;
	
	// Prohibit dividing a variable term by a variable term
	if ( hasVarTermP( t1 ) && hasVarTermP( t2 ) ) {
	    return null;
	}

	String divTermBy = null;

	if ( !t2.equals( "0" ) ) {

	    // trace.out("divTermBy(" + t1 + "," + t2 + ") = " );
	    if ( t1.indexOf('-') != -1 ||
		 t1.indexOf('+') != -1 ||
		 t1.indexOf('/') != -1 ) {
		t1 = "(" + t1 + ")";
	    }
	    if ( (isVarTerm( t2 ) && hasCoefficient( t2 ) != null) ||
		 isPolynomial( t2 ) ||
		 t2.indexOf('-') != -1 ) {
		t2 = "(" + t2 + ")";
	    }
	    divTermBy = t1 + "/" + t2;
	}

	// trace.out(divTermBy);
	return divTermBy;
    }

    public String addTermBy( String t1, String t2 ) {

	if (!isArithmeticExpression(t1) || !isArithmeticExpression(t2)) return null;
	
	// trace.out("addTermBy(" + t1 + "," + t2 + ") = ");

	// if ( t1.equals("0") ) return t2;
	// if ( t2.equals("0") ) return t1;

	String addTermBy = null;

	try {
	    AlgebraExp exp2 = AlgebraExp.parseExp( t2 );

	    if ( t2.charAt(0) == '-' ) {
		if ( exp2.isTerm() || exp2.isAddition() ) {
		    addTermBy = t1 + t2;
		} else {
		    addTermBy = t1 + "+(" + t2 + ")";
		}
	    } else {
		addTermBy = t1 + "+" + t2;
	    }
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}
	// trace.out(addTermBy);
	return addTermBy;
    }

    public String mulTermBy( String t1, String t2 ) {

	if (!isArithmeticExpression(t1) || !isArithmeticExpression(t2)) return null;
	
	// Mon Dec 26 16:32:25 2005
	// Prohibit making 2nd order polynomials
	if ( hasVarTermP( t1 ) && hasVarTermP( t2 ) ) {
	    return null;
	}

	String mulTermBy = null;

	if ( t1.indexOf('-') != -1 ||
	     t1.indexOf('+') != -1 ||
	     t1.indexOf('/') != -1 ) {
	    t1 = "(" + t1 + ")";
	}
	if ( t2.indexOf('-') != -1 ||
	     t2.indexOf('+') != -1 ||
	     t2.indexOf('*') != -1 ||
	     t2.indexOf('/') != -1 ) {
	    t2 = "(" + t2 + ")";
	}

	mulTermBy = t1 + "*" + t2;
	
	// trace.out("mulTermBy(" + t1 + "," + t2 + ")=" + mulTermBy);
	return mulTermBy;
    }

    public String subTermBy( String t1, String t2 ) {

	if (!isArithmeticExpression(t1) || !isArithmeticExpression(t2)) return null;

	if ( t2.charAt(0) == '-' ) {
	    t2 = "(" + t2 + ")";
	}
	return t1 + "-" + t2;
    }
    
    

    public String subTerm(String expString1,String expString2) {
        
        String subTerm = null;
        
        if (isArithmeticExpression(expString1) && isArithmeticExpression(expString2)) {
            String negExpString2 = reverseSign(expString2);
            subTerm = addTerm(expString1,negExpString2);
        }
        return subTerm;
    
    }
    

    /**
     * Returns the quotient of the given argument which is a modulo to
     * 10.
     *
     * @param n a <code>String</code> value
     * @return a <code>String</code> value
     **/
    public String modTen( String n ) {

	if (!isArithmeticExpression(n)) return null;

	String modTen = null;

	try {

	    int nInt = Integer.parseInt( n );
	    modTen = "" + (nInt % 10);

	} catch (NumberFormatException e) {
	    ;
	}

	return modTen;
    }

    public String divTen( String n ) {

	if (!isArithmeticExpression(n)) return null;

	String divTen = null;

	try {

	    if(n.indexOf(".")!=-1)
	    	divTen=String.valueOf(Double.parseDouble(n)/10.0);
	    else
	    
	    divTen = "" + (Integer.parseInt(n) / 10);

	} catch (NumberFormatException e) {
	    ;
	}

	return divTen;
    }

    /**
     * Do division with two given monomial terms
     *
     * @param t1 a <code>String</code> value
     * @param t2 a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String divTerm( String t1, String t2 ) {

	if (!isArithmeticExpression(t1) || !isArithmeticExpression(t2)) return null;

	// Mon Dec 26 13:49:57 2005
	// Prohibit dividing a variable term by a variable term
	/*if ( hasVarTermP( t1 ) && hasVarTermP( t2 ) ) {
	    return null;
	}*/

	String divTerm = null;

	if ( t2.equals( "1" ) ) {

	    divTerm = t1;

	} else if ( !t2.equals( "0" ) ) {

	    try {
		AlgebraExp exp1 = AlgebraExp.parseExp(t1);
		AlgebraExp exp2 = AlgebraExp.parseExp(t2);

		AlgebraExp exp = exp1.divTerm( exp2 );
		// The term t2 could be zero but in the form of "0/x"
		// or "x*0", hence exp could be null
		if ( exp != null ) {
		    divTerm = exp.toString();
		}
	    } catch (java.text.ParseException e) {
		if (trace.getDebugCode("missalgebra")) {
		    e.printStackTrace();
		}
	    }
	    /*
	    catch (NullPointerException e) {
		trace.out("divTerm(" + t1 + "," + t2 + ")");
		trace.out("Goal test: " + RhsGoalTest.getGoalTest());
		trace.out("Rhs State: " + RhsGoalTest.getRhsState());
		e.printStackTrace();
	    }
	    */
	    
	    /*
	    if (divTerm != null && divTerm.toUpperCase().indexOf("XX") != -1) {
		divTerm = null;	    
	    }
	    */
	    
	    // trace.out("EqFeaturePredicate.divTerm(" + t1 + "," + t2 + ") = ");
	    // trace.out(divTerm);
	}

	return divTerm;
    }

    public String mulTerm( String t1, String t2 ) {

	if (!isArithmeticExpression(t1) || !isArithmeticExpression(t2)) return null;

	// Mon Dec 26 13:42:10 2005
	// Prohibite multiplying variable terms
	if ( hasVarTermP( t1 ) && hasVarTermP( t2 ) ) {
	    return null;
	}

	String mulTerm = null;

	try {
	    AlgebraExp exp1 = AlgebraExp.parseExp( t1 );
	    AlgebraExp exp2 = AlgebraExp.parseExp( t2 );
	    
	    // trace.out("exp1 is term? " + exp1.isTerm());

	    AlgebraExp exp = exp1.multTerm( exp2 );

	    if ( exp != null ) {
		mulTerm = exp.toString();
	    }

	} catch (java.text.ParseException e) {
	    
		if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}
	return mulTerm;
    }

    public String divTerm_obsolete( String t1, String t2 ) {
	
	// trace.out("divTerm(" + t1 + "," + t2 + ")");
	String quatient = null;

	//
	if ( isPolynomial( t1 ) || isPolynomial( t2 ) ) {
	    return null;
	}
	// Illegal calls 
	try {
	    if ( isVarTerm( t2 ) || 
		 ( !MathLib.isFraction( t2 ) &&
		   Integer.parseInt( t2 ) == 0 ) ) {
		return null;
	    }
	} catch (NumberFormatException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	    return null;
	}

	// Legal calls 
	if ( isVarTerm( t1 ) ) {
	    if ( isVarTerm( t2 ) ) {
		quatient = divTerm( coefficient( t1 ), coefficient( t2 ) );
	    } else {
		String coefficient = divTerm( coefficient( t1 ), t2 );
		if ( coefficient.equals("1") ) {
		    coefficient = "";
		}
		quatient = coefficient + varName( t1 );
	    }
	} else {
	    quatient = MathLib.div( t1, t2 );
	}
	// trace.out("divTerm(" + t1 + "," + t2 + ") = " + quatient);
	return quatient;
    }
    
    /**
     * Add two monomial terms
     *
     * @param term1 a <code>String</code> value
     * @param term2 a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String addTerm( String term1, String term2 ) {

	if (!isArithmeticExpression(term1) || !isArithmeticExpression(term2)) return null;

	// trace.out("addTerm(" + term1 + "," + term2 + ")...");
	
	String addTerm = null;
	try {
	    AlgebraExp exp1 = AlgebraExp.parseExp( term1 );
	    AlgebraExp exp2 = AlgebraExp.parseExp( term2 );
	    AlgebraExp exp = null;
	    if ( exp1.isTerm() && exp2.isTerm() &&
		 ( ( isVarTerm( term1 ) && isVarTerm( term2 ) ) ||
		   ( !isVarTerm( term1 ) && !isVarTerm( term2 ) ) ) ) {
		
		if ( exp1 != null) {
			
			exp = exp1.addTerm( exp2 );
		}

	    } else {
	    
	    	
			exp = new AlgebraExpPoly( "+", exp1, exp2 ).evalArithmetic();
	    }
		
	    if ( exp != null ) {
		addTerm = exp.toString();
	    }
	    
	} catch (java.text.ParseException e) {
	    trace.out("missalgebra", "addTerm(" + term1 + "," + term2 + ")...");
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("addTerm(" + term1 + "," + term2 + ")=" + addTerm);
	return addTerm;
    }

    public String addTerm_obsolete( String term1, String term2 ) {

	String addTerm = null;

	if ( !term1.equals("") && !term2.equals("") ) {
	    addTerm = 
		term1 +
		(term2.charAt(0) != '+' && term2.charAt(0) != '-' ? "+" : "") +
		term2;
	}
	// trace.out("addTerm(" + term1 + ", " + term2 + ")=" + addTerm);

	return addTerm;
    }

    /**
     * Do alithmetic on a given polynomial
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String evalArithmetic( String term ) {

	if (!isArithmeticExpression(term)) return null;

	// trace.out("evalArithmetic(" + term + ")");
	
	AlgebraExp algebraExp = null;

	try {
	    algebraExp = AlgebraExp.parseExp( term );
	    // trace.out("evalArithmetic: algebraExp = " + algebraExp);
	    // trace.out( algebraExp.isTerm() );
	    algebraExp = algebraExp.evalArithmetic();
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}
	return ( algebraExp != null ) ? algebraExp.toString() : null;
    }
    
    public String evalArithmetic_Old( String term ) {

	// trace.out("evalArithmetic(" + term + ")");

	String doAlgebra = ( isPolynomial( term ) && isHomogeneous( term ) ) ?
	    "" : null;

	if ( doAlgebra != null ) {

	    String varName = "";
	    String value = "0";

	    ArrayList termTokens = tokenizeTerms( term );
	    for (int i = 0; i < termTokens.size(); i++) {

		try {
		    String item = (String)termTokens.get(i);
		    if ( item.charAt(0) == '+' ) {
			item = item.substring( 1, item.length() );
		    }
		    if ( isVarTerm( item ) ) {
			varName = varName( item );
			item = coefficient( item );
		    }
		    value = MathLib.add( value, item );

		} catch (NumberFormatException e) {

		    if (trace.getDebugCode("missalgebra")) {
			trace.out("missalgebra", "evalArithmetic(" + term + ")");
			trace.out("missalgebra", ">>> " + (String)termTokens.get(i));
			trace.out("missalgebra", varTerm((String)termTokens.get(i)));
			e.printStackTrace();
		    }
		}
	    }
	    doAlgebra =
		( varName != "" && value.equals("1") ? "" : value)
		+ ( value.equals("0") ? "" : varName );
	}

	/*
	if ( doAlgebra != null ) {
	    trace.out("evalArithmetic(" + term + ") = " + doAlgebra);
	}
	*/

	return doAlgebra;
    }

    public String numerator( String term ) {

	if (!isArithmeticExpression(term)) return null;

	String numerator = null;

	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );

	    if ( exp.isFraction() ) {
		numerator = exp.getNumerator().toString();
	    }

	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {
		e.printStackTrace();
	    }
	}

	// trace.out("numerator(" + term + ") = " + numerator );
	return numerator;
    }

    public String denominator( String term ) {

	if (!isArithmeticExpression(term)) return null;

	String denominator = null;

	try {
	    AlgebraExp exp = AlgebraExp.parseExp( term );

	    if ( exp != null && exp.isFraction() ) {
	    
		denominator = exp.getDenominator().toString();
	    }
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {	    
		e.printStackTrace();
	    }
	}

	// trace.out("denominator(" + term + ") = " + denominator);
	return denominator;
    }

    
    /**
     * Describe <code>cancelDenominator</code> method here.
     *
     * @param term a <code>String</code> value
     * @return a <code>String</code> value
     */
    public String cancelDenominator( String term ) {

	if (!isArithmeticExpression(term)) return null;

	String cancelDenominator = null;

	if ( isFractionTerm( term ) != null ) {

	    String denominator = denominator( term );
	    cancelDenominator = mulTermBy( term, denominator );
	}

	return cancelDenominator;
    }

    public String cancelCoefficient( String term ) {

	if (!isArithmeticExpression(term)) return null;

	String cancelCoefficient = null;

	if ( hasCoefficient( term ) != null ) {

	    String coefficient = coefficient( term );
	    cancelCoefficient = divTermBy( term, coefficient );
	}

	return cancelCoefficient;
    }

    
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *
    // FEATURE PREDICATES for WME locations - *- * - * - * - *
    // - * - *- * - * - *- * - * - *- * - * - *- * - * - * - *
    //
    // Actually, they are implemented as JESS functions, which are
    // embedded into the production rule file

    public String sameRow( String fact1, String fact2 ) {
	trace.out("sameRow(" + fact1 + "," + fact2 + ")");
	return null;
    }

    public String consecutiveRow( String fact1, String fact2 ) {
	trace.out("consecutiveRow(" + fact1 + "," + fact2 + ")");
	return null;
    }

    public String sameColumn( String fact1, String fact2 ) {
	trace.out("sameColumn(" + fact1 + "," + fact2 + ")");
	return null;
    }

    public String consecutiveColumn( String fact1, String fact2 ) {
	trace.out("consecutiveColumn(" + fact1 + "," + fact2 + ")");
	return null;
    }

    // = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = 
    // Internal helper methods
    // 

    /*
    private boolean isConstant( String term ) {
	return isMonomial( term ) && !isVarTerm( term );
    }
    */
    private Vector /* of String */  getTopLevelQuantities(String term)
    {
    	//returns the all in parentized quanities at the top level of term
    	//get rid of any outer parens
    	while(term.charAt(0)=='(')
    		term=stripOuterParens(term);
    	char[] array=term.toCharArray();
    	int unmatchedParens=0;
    	Vector quanities=new Vector();
    	int startIndex=-1;
    	
    	for(int termIndex=0; termIndex<term.length(); termIndex++)
    	{
    		if(array[termIndex]=='(')
    		{
    			if(unmatchedParens==0)
    				startIndex=termIndex;
    			unmatchedParens++;
    		}
    		if(array[termIndex]==')')
    		{
    			unmatchedParens--;
    			if(unmatchedParens==0)
    			{
    				//top level, add to vector
    				quanities.add(term.substring(startIndex,termIndex+1));
    			}
    			
    		}
    			
    		
    	}
    	
    	if(quanities.isEmpty())
    		return null;
    	else
    		return quanities;
    			
    	
    }
    private String stripOuterParens(String term)
    {
    	//remove the outer parens (if any) from term
    	int leftParenIndex=term.indexOf("(");
    	if(leftParenIndex==-1)
    		return term;
    	else
    		return term.substring(leftParenIndex+1,term.length()-1);
    }
    private boolean isVarTerm( String term ) {
	return varTerm( term ) != null;
    }

    private boolean isHomogeneous( String term ) {
	return homogeneous( term ) != null;
    }

    public boolean isPolynomial( String term ) {
	return polynomial( term ) != null;
    }

    private boolean isMonomial( String term ) {
	return monomial( term ) != null;
    }

    private boolean hasConstTerm( ArrayList terms ) {
	return !allVarTerms( terms );
    }

    private boolean hasVarTerm( AlgebraExp exp ) {

	// trace.out("hasVarTerm( " + exp + " )");

	if ( exp.isTerm() ) {
	    return isVarTerm( exp.toString() );
	} else {
	    return hasVarTerm( exp.getFirstTerm() ) ||
		hasVarTerm( exp.getSecondTerm() );
	}
    }

    private boolean hasVarTerm( ArrayList terms ) {
	return !allConstTerms( terms );
    }

    private boolean hasVarTermP( String term ) {

	boolean hasVarTermP = false;

	try {
	    hasVarTermP = hasVarTerm( AlgebraExp.parseExp( term ) );
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {	    
		e.printStackTrace();
	    }
	}
	return hasVarTermP;
    }

    private boolean allVarTerms( ArrayList terms ) {

	boolean allVarTerms = true;
	for (int i = 0; i < terms.size(); i++) {
	    if ( varTerm( (String)terms.get(i) ) == null ) {
		allVarTerms = false;
		break;
	    }
	}
	return allVarTerms;
    }

    private boolean allConstTerms( ArrayList terms ) {

	boolean allConstTerms = true;
	for (int i = 0; i < terms.size(); i++) {
	    if ( varTerm((String)terms.get(i)) != null ) {
		allConstTerms = false;
		break;
	    }
	}
	return allConstTerms;
    }
    
    private int findFirstDistributableFactorIndex(String term)
    {
    	int withMulIndex=term.indexOf("*(");
    	int leftParenIndex=term.indexOf("(");
    	int factorEndIndex;
		if(withMulIndex!=-1)
			factorEndIndex=withMulIndex-1;
		else
			factorEndIndex=leftParenIndex-1;
		//convert to an array to make this faster
		char[] temp=term.toCharArray();
		//find the index where the factor starts
		int factorStartIndex=factorEndIndex;
		if(factorEndIndex!=0)
		{
	    	int index=factorEndIndex;
	    	char curChar=temp[index];
	    	while(index>0&&(('0'<=curChar && curChar<='9') || curChar=='/'))
	    	{
	    		index--;
	    		curChar=temp[index];
	    		if(curChar=='/')
	    		{
	    			//check to see if it's a fraction factor or an operator
	    			char prev=temp[index-1];
	    			if('0'<=prev && '9'>=prev)
	    				continue;
	    			else
	    			{
	    				index++;
	    				break;
	    			}
	    		}
	    		
	    	}
	    	factorStartIndex=index;
	    	
		}
    
		return factorStartIndex;
   }

    private String trimPlusSignInFront( String term ) {

	if ( term.charAt(0) == '+' ) {
	    term = term.substring(1);
	}
	return term;
    }

    // Tokenize a polynomial expression into a list of monomials
    ArrayList /* String */ tokenizeTerms( String term ) {

	AlgebraExp exp = null;
	try {
	    exp = AlgebraExp.parseExp( term );
	} catch (java.text.ParseException e) {
	    if (trace.getDebugCode("missalgebra")) {	    
		e.printStackTrace();
	    }
	}
	return tokenizeTerms( exp );
    }

    ArrayList /* String */ tokenizeTerms( AlgebraExp exp ) {

	if ( exp.isPolynomial() &&
	     (exp.getOp().equals("+") || exp.getOp().equals("-")) ) {
	    
	    return append( tokenizeTerms( exp.getFirstTerm() ),
			   tokenizeTerms( exp.getSecondTerm() ) );
	    
	} else {
	    
	    ArrayList token = new ArrayList();
	    token.add( exp.toString() );
	    return token;
	}
    }

    ArrayList append( ArrayList list1, ArrayList list2 ) {

	ArrayList newList = new ArrayList( list1 );

	for (int i = 0; i < list2.size(); i++) {
	    newList.add( list2.get(i) );
	}

	return newList;
    }

    // Tokenize a polynomial expression into monomials
    ArrayList /* String */ tokenizeTerms_obsolete( String term ) {

	ArrayList tokenizedTermes = new ArrayList();

	StringTokenizer termTokens = new StringTokenizer( term, "+-", true );

	/*
	StringTokenizer x = new StringTokenizer( term, "+-", true );
	trace.out("[" + term + "] termTokens: ");
	while ( x.hasMoreTokens() ) {
	    trace.out( x.nextToken() + " | " );
	}
	trace.out();
	*/

	while ( termTokens.hasMoreTokens() ) {

	    String token = termTokens.nextToken();
	    if ( token.equals( "+" ) || token.equals( "-" ) ) {
		token += termTokens.nextToken();
	    }
	    tokenizedTermes.add( token );
	}
	return tokenizedTermes;
    }
    
    
}

//
// end of f:/Project/CTAT/ML/ISS/miss/userDef/EqFeaturePredicate.java
// 
