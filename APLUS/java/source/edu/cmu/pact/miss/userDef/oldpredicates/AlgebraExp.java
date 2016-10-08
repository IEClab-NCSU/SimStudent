/**
 * Describe class AlgebraExp here.
 *
 *
 * Created: Mon Aug 01 18:28:28 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import mylib.MathLib;
import edu.cmu.pact.Utilities.trace;
public abstract class AlgebraExp {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    public abstract boolean isTerm();
    public abstract boolean isPolynomial();
    public abstract boolean isValidPolynomial();
    
    protected boolean containsDecimals;
    private String op;
    String getOp() { return this.op; }
    void setOp( String op ) {
	this.op = op;
    }

    private AlgebraExp firstTerm;
    AlgebraExp getFirstTerm() { return this.firstTerm; }
    void setFirstTerm( AlgebraExp firstTerm ) {
	this.firstTerm = firstTerm;
    }

    private AlgebraExp secondTerm;
    AlgebraExp getSecondTerm() { return this.secondTerm; }
    void setSecondTerm( AlgebraExp secondTerm ) {
	this.secondTerm = secondTerm;
    }

    private String exp = null;
    String getExp() { return this.exp; }
    void setExp( String exp ) { this.exp = exp; } 

    abstract String getVarName();
    /**
     * 
     * @return true if this expression is a simple term, like a constant or a variable with a coefficent, false if it is a complex term (due to a unary minus)
     */
    boolean isSimpleTerm()
    {
    		return isTerm() && !isParenQuanity();
    }
    /** 
     * 
     * @return true if this expression is a fraction and both the numerator and denominator are simple terms
     */
    boolean  isSimpleFraction()
    {
    	return isFraction() &&getNumerator().isSimpleTerm() && getDenominator().isSimpleTerm(); 
    }
    abstract String getCoefficient();
    
    public  boolean isDecimal()
    {
    	return containsDecimals;
    }
    
    /**
     * 
     * @return A Vector of Strings containing all the variables used in this expression(INCLUDING DUPLICATES), if it contains no variables, return null
     */
    public Vector /* of String*/ getAllVars()
    {	Vector variables=new Vector();
    	if(this.isConstTerm())
    		return null;
    	if(this.isTerm())
    		variables.add(getVarName());
    	else
    	{
	    	String upperCaseExp=firstTerm.toString().toUpperCase()+op+secondTerm.toString().toUpperCase();
	    	for(int charIndex=0; charIndex<upperCaseExp.length(); charIndex++)
	    	{
	    		char curChar=upperCaseExp.charAt(charIndex);
	    		if('A'<=curChar && 'Z'>=curChar)
	    			variables.add(String.valueOf(curChar));
	    		
	    	}
    	}
    	
    	return variables;
    	
    	
    }
    /**
     * 
     * @return a vector containing all of the terms in the top level expression 
     * @author ajzana 6-02-06
     */
    public Vector/* of String */ getTerms()
    {
    	Vector terms=new Vector();
    	if(this.isTerm())
    	{
    		terms.add(this.toString());
    		return terms;
    		
    	}
    	 terms.addAll(firstTerm.getTerms());
    	 if(secondTerm!=null)
    		 terms.addAll(secondTerm.getTerms());
    	return terms;
    		 
    }
    /**
     * 
     * @return the number of unbound(not counting those inside parens) constants (not coefficients) in this expression
     */
    public int countUnboundConstants()
    {
    	int firstTermValue=0;
    	int secondTermValue=0;
    	if(this.isConstTerm())
    	{
    		if(this.secondTerm==null)
    			return 1;
    		if(this.isFraction())
    			return 1;
    		
    	}
    	else
    	{
	    	if(this.isTerm())
	    	{
	    		
	    		return 0;
	    	}
    	}
    	if(!firstTerm.isParenQuanity())
    		firstTermValue=firstTerm.countUnboundConstants();//not in parens, so recurse
    	if(!secondTerm.isParenQuanity())
    		secondTermValue=secondTerm.countUnboundConstants();//not in parens, so recurse
    	return firstTermValue+secondTermValue; 
    }
    
    /**
     * NOTE does not handle fractions
     *  NOTE does not handle variable factors(e.g 5x^2+5x should return 5x, but returns 5)
     * @return a factor(other than one) which divides every term of the expression, otherwise run null
     */ 
     
    public AlgebraExp getFactor()
    {
    	if(isDecimal())
    		return null;
    		
    	if(isSimpleTerm())
    	{
    		if(isVarTerm())
    			try
    			{	
    				return parseExp(this.getCoefficient());
    			}
    			catch(ParseException e)
    			{
    				return null;
    			}
    		else
    			return this;
    	}
    	if(firstTerm.isSimpleTerm() && secondTerm.isSimpleTerm())
    	{
    		int const1;
    		int const2;
    		if(firstTerm.isVarTerm())
    			const1=Integer.parseInt(firstTerm.getCoefficient().toString());
    		else
    			const1=Integer.parseInt(firstTerm.toString());
    		if(secondTerm.isVarTerm())
    			const2=Integer.parseInt(secondTerm.getCoefficient().toString());
    		else
    			const2=Integer.parseInt(secondTerm.toString());
    		int gcd=MathLib.gcd(const1,const2);
    		if(gcd==1)
    			return null;
    		try
    		{
    		return parseExp(String.valueOf(gcd));
    		}
    		catch(ParseException e)
    		{
    			return null;
    		}
    		
    			
    	}
    	AlgebraExp factorOne=firstTerm.getFactor();
    		if(factorOne==null)//if the first one's null,don't bother figuring out the second
    			return null;
    	AlgebraExp factorTwo=secondTerm.getFactor();
    	if(factorTwo==null)
    		return null;
    	int gcd1=Integer.parseInt(factorOne.toString());
    	int gcd2=Integer.parseInt(factorTwo.toString());
    	int gcd=MathLib.gcd(gcd1,gcd2);
    	if(gcd==1)
    		return null;
    	try
    	{
    		
    	
    	return parseExp(String.valueOf(gcd));
    	}
    	catch(ParseException e)
    	{
    		return null;
    	}
    	
    	
    }
    

    String getCoefficient_obsolete() {

	// System.out.println(this + ".getCoefficient()");

	String coefficient = null;

	if ( isTerm() ) {

	    if ( getVarName().equals( "" ) ) {
		coefficient = getExp();
	    } else {
		coefficient = getExp().substring( 0, getExp().length() -1 );
	    }
	    
	    if ( coefficient.equals("") ) {
		coefficient = "1";
	    } else if ( coefficient.equals("-") ) {
		coefficient = "-1";
	    }
	}

	return coefficient;
    }

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - -
    // -

    // public AlgebraExp() {}

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - -
    // -

    /**
     * Parse a given expression, return an AlgebraExp object that
     * represents an albebraic structure of the expression. 
     *
     * @param exp a <code>String</code> value
     * @return an <code>AlgebraExp</code> value
     **/

    static private final String OPEN_P = "OPEN_P";
    static private final String PARSE_POLYNOMIAL = "PARSE_POLYNOMIAL";

    static public AlgebraExp parseExp( String exp ) throws ParseException {
    	
    	
	// System.out.println("AlgebraExp( " + exp + " ) in...");

	// Stack representing a finite state transition
	Stack /* String */ parseState = new Stack();

	// the index for parsing from left to right
	int i = 0;
	char c = ' '; 

	// for a compound term, which has a multiplication
	// (e.g. 2x*(-2)), this variable stores an operator (should be
	// either '*' or '/') that has been read
	String lastOp = null;
	int lastOpIndex = -1;

	while ( i < exp.length() ) {
	    
	    c = exp.charAt(i++);
	    // System.out.println("i = " + i + ", c = " + c);

	    if ( c == '(' ) {

		// Open parenthesis
		parseState.push( OPEN_P );

	    } else if ( c == ')' ) {

		// Close parenthesis
		if ( !isParseStateAt( parseState, OPEN_P ) ) {
		    throw new ParseException( exp, i );
		} else {
		    parseState.pop();
		}

	    } else if ( c == '*' || c == '/' ) {

		// Multiplication or division
		if ( !isParseStateAt( parseState, OPEN_P ) ) {
		    lastOp = String.valueOf( c );
		    lastOpIndex = i-1;
		}

	    } else if ( c == '+' || c == '-' ) {

		// Addition or subtruction
		if ( parseState.empty() && i > 1 ) {
		    parseState.push( PARSE_POLYNOMIAL );
		    // stop parsing and compose an AlgebraExp
		    break;
		}
	    }
	}

	// System.out.println("lastOp: " + lastOp + ", lastOpIndex: " + lastOpIndex);

	if ( isParseStateAt( parseState, PARSE_POLYNOMIAL ) ) {

	    // The given expression is a polynomial
	    // System.out.println("PARSE_POLYNOMIAL");
	    AlgebraExp secondTerm = ( c == '+' ) ?
		parseExp( exp.substring( i ) ) :
		parseExp( exp.substring( i -1 ) );
	    		
	    	return new AlgebraExpPoly( "+",
				       parseExp(exp.substring( 0, i -1 )),
				       secondTerm );

	} else if ( i == exp.length() ) {

	    // The parser hits the end of the expression...
	    if ( lastOp != null ) {

		// The given expression is a multiplication
		// System.out.println("lastOp = " + lastOp + ", lastOpIndex = " + lastOpIndex);
		String firstTerm = exp.substring( 0, lastOpIndex );
		String secondTerm = exp.substring( lastOpIndex +1 );
		AlgebraExpPoly expPoly = 
		    new AlgebraExpPoly( lastOp,
					parseExp( firstTerm ),
					parseExp( secondTerm ) );

		// System.out.println("--> " + expPoly);
		return expPoly;

	    } else if ( isSurroundedByParenthesis( exp ) ) {

		// System.out.println(exp + " is surrounded by ( )");
		return parseExp( exp.substring( 1, exp.length() -1 ) );

	    } else {
	    	return new AlgebraExpTerm( exp);

	    }
	}
	return null;
    }
    
    static private boolean isParseStateAt( Stack stack, String state ) {
	return !stack.empty() && stack.peek().equals( state );
    }; 

    // Returns true when the given expression is surrounded by a pair
    // of parenthesis
    static private boolean isSurroundedByParenthesis( String exp ) {
	return !exp.equals("") &&
	    exp.charAt(0) == '(' &&
	    exp.charAt(exp.length() -1) == ')';
    }

    // -
    // - Predicates - * - * - * - * - * - * - * - * - * - * - * - *
    // - 

    abstract boolean equals( AlgebraExp term );

    /**
     * Reurns T if this expression (could be a term or a polynomial)
     * is "smaller" in orfer than the given expression regarding its
     * complexity and variable name
     *
     * @param exp an <code>AlgebraExp</code> value
     * @return a <code>boolean</code> value
     **/
    public boolean comesBefore( AlgebraExp exp ) {

	boolean comesBefore = false;

	// term vs. ...
	if ( isTerm() ) {

	    // term vs. term...
	    if ( exp.isTerm() ) {

		String v1 = getVarName();
		String v2 = exp.getVarName();

		if ( v2 == null ) {
		    // System.out.println(this + ".comesBefore(" + exp + ") v2 got null = = = = = = = = = = = = = = = " );
		}

		// Const-term vs. term...
		if ( v1.equals("") ) {

		    comesBefore = v2.equals("");

		// Var-term vs. term...
		} else {

		    // Var-term vs. var-term
		    if ( v2 != null && !v2.equals("") ) {
			
			comesBefore = ( v1.compareToIgnoreCase(v2) < 0 );

		    // var-term vs. const-term
		    } else {

			comesBefore = true;
		    }

		}

	    // term vs. polynomial
	    } else {

		comesBefore = true;
	    }

	} else if ( exp.isTerm() && !exp.isVarTerm() ) {

	    comesBefore = true;
	}
	
	return comesBefore;
    }

    public boolean isSameType( AlgebraExp term ) {

	String v1 = getVarName();
	String v2 = term.getVarName();

	// System.out.println(this + ".isSameType(" + term + ")");
	// System.out.println("v1 = " + v1 + ", v2 = " + v2);

	return v1.equals( v2 );
    }

    abstract boolean isConstTerm();
    abstract boolean isVarTerm();
    abstract boolean isZero();

    // -
    // - Alithmetic operations - * - * - * - * - * - * - * - * - *
    // - 

    abstract public AlgebraExp evalArithmetic();
    /*
    {
	System.out.println("AlgebraExp.evalArithmetic() called on " + this);
	return null;
    }
    */

    abstract public AlgebraExp addTerm( AlgebraExp term );
    /*
      {
	System.out.println("AlgebraExp.addTerm() called on " + this);
	return null;
    }
    */

    abstract public AlgebraExp multTerm( AlgebraExp term );
    /*
    {
	System.out.println("AlgebraExp.multTerm() called on " + this);
	return null;
    }
    */

    abstract public AlgebraExp divTerm( AlgebraExp term );
    /*
    {
	System.out.println("AlgebraExp.divTerm() called on " + this);
	return null;
    }
    */

    public AlgebraExp evalArithmeticAdd( AlgebraExp t1, AlgebraExp t2 ) {
    	
	AlgebraExp newTerm = null;

	if ( t1.toString().equals("0") ) {

	    newTerm = t2;

	} else if ( t2.toString().equals("0") ) {

	    newTerm = t1;

	} else if ( t1.isSimpleTerm() && t2.isSimpleTerm() && t1.isSameType( t2 ) ) {

	    newTerm = t1.addTerm( t2 );

	} else 
	{
		if(t1.isSimpleFraction() && t2.isSimpleFraction())
		{
			return t1.addTerm(t2);
		}
		
	    newTerm = t1.comesBefore(t2) ?
		new AlgebraExpPoly( "+", t1, t2 ) :
		new AlgebraExpPoly( "+", t2, t1 ) ;
	}

	return newTerm;
    }
    /**
     * 
     * @return 1/this expression
     */
    public AlgebraExp Invert()
    {
    	if(isZero())
    		return null;
    	try
    	{
    	return parseExp("1/"+this);
    	}
    	catch(ParseException e)
    	{
    		return null;
    	}
    }
    
    
    public AlgebraExp evalArithmeticMult( AlgebraExp t1, AlgebraExp t2 ) {

	AlgebraExp newTerm = null;

	// System.out.println("evalArithmeticMult(" + t1 + "," + t2 + ")");

	if ( t1.isTerm() ) {
	    newTerm = t2.multTerm( t1 );
	} else if ( t2.isTerm() ) {
	    newTerm = t1.multTerm( t2 );
	} else {
	    newTerm = t1.comesBefore(t2) ?
		new AlgebraExpPoly( "*", t1, t2 ) :
		new AlgebraExpPoly( "*", t2, t1 ) ;
	}

	return newTerm;
    }

    public AlgebraExp evalArithmeticDiv( AlgebraExp t1, AlgebraExp t2 ) {

	AlgebraExp newTerm = null;

	if ( t2.isTerm() && !t2.isZero() ) {
	    newTerm = t1.divTerm( t2 );
	} else {
	    newTerm = new AlgebraExpPoly( "/", t1, t2 );
	}

	return newTerm;
    }

    // -
    // - Modify expression - * - * - * - * - * - * - * - * - * - * - *
    // -

    AlgebraExp removeFirstVarTerm() {

	AlgebraExp firstVarTerm = getFirstVarTerm();
	return (firstVarTerm != null) ? removeTerm( firstVarTerm ) : null;
    }

    AlgebraExp removeLastConstTerm() {

	AlgebraExp lastConstTerm = getLastConstTerm();
	return (lastConstTerm != null) ? removeTerm( lastConstTerm ) : null;
    }

    AlgebraExp removeLastTerm() {

	AlgebraExp lastTerm = getLastTerm();
	return (lastTerm != null) ? removeTerm( lastTerm ) : null;
    }

    AlgebraExp removeTerm( AlgebraExp term ) {

	if ( isPolynomial() && getOp().equals( "+" ) ) {

	    // System.out.println("trem = " + term + ", 1st = " + getFirstTerm());

	    if ( getFirstTerm().equals( term ) ) {

		return getSecondTerm();

	    } else {

		AlgebraExp term2 = getSecondTerm().removeTerm(term);

		return (term2 != null) ?
		    new AlgebraExpPoly( "+", getFirstTerm(), term2 ) :
		    getFirstTerm();
	    }

	} else {

	    return this.equals( term ) ? null : this;
	}
    }

    AlgebraExp inverseTerm() {

	// System.out.println(this + ".inverseTerm()");

	AlgebraExpTerm termOne = new AlgebraExpTerm( "1" );

	return termOne.divTerm( this );
    }

    AlgebraExp replaceTerm( AlgebraExp oldTerm, AlgebraExp newTerm ) {

	// System.out.println(this + ".replaceTerm(" + oldTerm + "," + newTerm + ")");
        if(newTerm==null)
            return this;    
	if ( this == oldTerm ) {

	    return newTerm;

	} else if ( getFirstTerm() == oldTerm ) {

	    if ( getOp().equals("+") && newTerm.isZero() ) {

		return getSecondTerm();

	    } else {

		return new AlgebraExpPoly( getOp(), newTerm, getSecondTerm() );
	    }

	} else {

	    AlgebraExp new2 = getSecondTerm().replaceTerm(oldTerm, newTerm);

	    if (getOp() == null || new2 == null) {

		trace.out("missalgebra", "AlgebraExp.replaceTerm(" + oldTerm + "," + newTerm + ")");
		trace.out("missalgebra", "NullPointerException on " + this);

		return null;

	    } else if ( getOp().equals("+") && new2.isZero() ) {
		
		return getFirstTerm();

	    } else {

		return new AlgebraExpPoly( getOp(), getFirstTerm(), new2 );
	    }
	}
    }

    // -
    // - Look up for a particular term * - * - * - * - * - * - * - *
    // -

    /**
     * Returns a term that is the same type as the given "term", but
     * only the scope of the given "operator"
     *
     * @param op a <code>String</code> value
     * @param term an <code>AlgebraExp</code> value
     * @return an <code>AlgebraExp</code> value
     **/
    AlgebraExp lookupSameTypeTerm( String op, AlgebraExp term ) {

	if ( isTerm() ) {

	    return isSameType( term ) ? this : null;

	} else if ( getFirstTerm().isSameType( term ) ) {
		
	    return getFirstTerm();
	    
	} else if ( getOp().equals( op ) && (getSecondTerm() != null) ) {
	    
	    return getSecondTerm().lookupSameTypeTerm( op, term );
	}
	return null;
    }

    // Returns a last term of a polynomial expression
    AlgebraExp getLastTerm() {

	if ( isPolynomial() && getOp().equals( "+" ) ) {
	    
	    AlgebraExp lastTerm = getSecondTerm().getLastTerm();
	    
	    return lastTerm != null ? lastTerm : getSecondTerm();
	    
	} else {

	    return null;
	}
    }

    // Returns a last constant term
    AlgebraExp getLastConstTerm() {
    	
	return getLastConstTerm( null );
    }

    AlgebraExp getLastConstTerm( AlgebraExp term ) {
    	
	if ( isPolynomial() && getOp().equals( "+" ) ) {

	    return getFirstTerm().isConstTerm() ?
		getSecondTerm().getLastConstTerm( getFirstTerm() ) :
		getSecondTerm().getLastConstTerm( term );

	} else {

	    return isTerm() && isConstTerm() ? this : term;
	}
    }

    AlgebraExp getFirstVarTerm() {

	if ( isAddition() ) {

	    return getFirstTerm().isVarTerm() ?
		getFirstTerm() :
		getSecondTerm().getFirstVarTerm();

	} else if ( isTerm() ) {

	    return isVarTerm() ? this : null;
	    
	} else {

	    return null;
	}
    }
    public boolean hasUncombindedVariables()
    {
    	
    	if(isTerm())
    		return false;
    	
    	boolean firstTermValue;
    	boolean secondTermValue;
    	boolean firstTermParens=firstTerm.isParenQuanity();
    	boolean secondTermParens=secondTerm.isParenQuanity();
    	if(!firstTermParens &&!secondTermParens)
    	{
    		//no parens, so just look at the whole exp
    		Vector variables=this.getAllVars();
    		if(variables==null)
    			return false;
    		else
    			return hasDuplicates(variables);
    	}
    	
    	if(firstTermParens)
    		firstTermValue=false;//this has parens, so ignore it
    	else
    	{
    		
    		Vector variables=firstTerm.getAllVars();
    		if(variables==null)
    			firstTermValue= false;
    		else
    			firstTermValue=hasDuplicates(variables);
    	}
    	if(secondTermParens)
    		secondTermValue=false;//this has parens, ignore it
    	else
    	{
    		Vector variables=secondTerm.getAllVars();
        	
    		if(variables==null)
        		secondTermValue=false;
    		else
    			secondTermValue=hasDuplicates(variables);
    	}
    	return firstTermValue || secondTermValue;
        		
    }
   
    
    /**
     * @return true if v contains any duplicates (according to equals()), false otherwise
     */
    public static boolean hasDuplicates(Vector v)
    {
    	Set s=new HashSet();
    	for(int vectorIndex=0; vectorIndex<v.size(); vectorIndex++)
    	{
    		if(!s.add(v.get(vectorIndex)))
    				return true;
    		
    	}
    	return false;
    	
    }
    /**
     * 
     * @return true if consists on a parenithized quanitity times a factor (not including 1) (e.g. 5*(4x+7),4(3x+2))
     */
    public boolean isParenQuanity()
    {
    	
    	return this.toString().matches("([0-9]|-)+([.][0-9]+)?[*]?[(].*[)]");
    	
    }
    // -
    // - Operators for Fractions - * - * - * - * - * - * - * - * - * 
    // - 
    public boolean isFraction() {
	String op = getOp();
	return op != null && op.equals( "/" );
    }
    AlgebraExp getNumerator() { return getFirstTerm(); }
    AlgebraExp getDenominator() { return getSecondTerm(); }

    // -
    // - 
    // -
    public boolean isAddition() {

	String op = getOp();
	return op != null && op.equals( "+" );
    }

    public boolean isMultiplication() {

	String op = getOp();
	return op != null && op.equals( "*" );
    }

    // -
    // - Printing - * - * - * - * - * - * - * - * - * - * - * - *
    // -

    public String parseTree() {
	trace.out("missalgebra", "AlgebraExp.parseTree() called...");
	return null;
    }
    
    

}
