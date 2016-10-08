/**
 * Describe class AlgebraExpTerm here.
 *
 *
 * Created: Mon Aug 01 18:29:37 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;


import java.text.ParseException;

import mylib.MathLib;
import edu.cmu.pact.Utilities.trace;

public class AlgebraExpTerm extends AlgebraExp {

    // -
    // - Fields - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    public boolean isTerm() { return true; }
    public boolean isPolynomial() { return false; }
    public boolean isValidPolynomial() { return false; }

    // -
    // - Constructor - - - - - - - - - - - - - - - - - - - - - - -
    // - 

    /**
     * Creates a new <code>AlgebraExpTerm</code> instance.
     *
     */
    public AlgebraExpTerm( String exp ) {
    	
    	setExp( exp );
    	containsDecimals=exp.indexOf(".")!=-1;
    }
    

    // -
    // - Methods - - - - - - - - - - - - - - - - - - - - - - - - -
    // -

    String getVarName() {

	String exp = getExp();
	// System.out.println("getVarName: exp = " + exp);
	char v =
	    (exp != null) ? exp.toUpperCase().charAt( getExp().length() -1 ) :
	    ' ';

	return ( 'A' <= v && v <= 'Z' ) ?
	    getExp().substring( getExp().length() -1 ) :
	    "";
    }

    String getCoefficient() {

	// System.out.println(this + ".getCoefficient()");

	String coefficient = null;

	if ( isTerm() ) {
	    // System.out.println("isTerm() clear...");
	    if ( !getVarName().equals( "" ) ) {
		// System.out.println("getVarName() clear...");
		coefficient = getExp().substring( 0, getExp().length() -1 );
	    
		if ( coefficient.equals("") ) {
		    coefficient = "1";
		} else if ( coefficient.equals("-") ) {
		    coefficient = "-1";
		}
	    }
	}
	// System.out.println(this + ".getCoefficient() = " + coefficient);
	return coefficient;
    }

    AlgebraExp getLastTerm() { return null; }
    AlgebraExp getLastConstTerm() { return null; }

    // -
    // - Predicates - * - * - * - * - * - * - * - * - * - * - * - *
    // -

    boolean isConstTerm() { return !isVarTerm(); }
    boolean isVarTerm() {
	//added by Gustavo 19Dec2006
	//if (getExp().length()==0) return false;
	char v = getExp().toUpperCase().charAt( getExp().length() -1 );
	return 'A' <= v && v <= 'Z';
    }
    boolean isZero() { return getExp().equals( "0" ); }
    boolean equals( AlgebraExp term ) {
	return term.isTerm() && getExp().equals(term.getExp());
    }

    // -
    // - Alithmetic operations - * - * - * - * - * - * - * - * - *
    // -

    public AlgebraExp evalArithmetic() {
	
	AlgebraExp evalArithmetic = null;
	
	// if the term is "-(....)"
	if ( getExp().charAt(0) == '-' && 
			getExp().charAt(1) == '(' &&
			getExp().charAt(getExp().length()-1) == ')' ) 
	{
	    String expBody = getExp().substring(2,getExp().length()-1);
	
	
	    // System.out.println("AlgebraExpTerm: evalArithmetic.expBody = " + expBody);
	    AlgebraExp exp = null;
	    try {
		exp = AlgebraExp.parseExp(expBody);
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    if (exp != null) {
		evalArithmetic = exp.multTerm( new AlgebraExpTerm("-1") );
	    }
	}    
	 else {
	    
	    evalArithmetic = this;
	}
	
	return evalArithmetic;
    }

    public AlgebraExp addTerm( AlgebraExp term ) {
	if(this.isDecimal()||term.isDecimal())
		return addTermDec(term);
	
	term = term.evalArithmetic();

	String c1 = null;
	String c2 = null;
	int c;
	
	if ( isVarTerm() && term.isVarTerm() && isSameType( term ) ) {

	    c1 = getCoefficient();
	    c2 = term.getCoefficient();
	    try {
		c = (Integer.parseInt( c1 ) + Integer.parseInt( c2 ));
	    } catch (Exception e) {
		trace.out("eqfp", "AlgebraExpTerm: " + this + ".addTerm(" + term + ")");
		return null;
	    }

	    if ( c == 0 ) {
		return new AlgebraExpTerm( "0" );
	    } else {
		String v = getVarName();
		return new AlgebraExpTerm( (c == 1 ? "" : (c == -1 ? "-" : "" + c)) + v );
	    }

	} else if ( term.isTerm() && !isVarTerm() && !term.isVarTerm() ) {

	    c1 = toString();
	    c2 = term.toString();
	    try {
		c = (Integer.parseInt( c1 ) + Integer.parseInt( c2 ));
	    } catch (Exception e) {
		trace.out("eqfp", "AlgebraExpTerm: " + this + ".addTerm(" + term + ")");
		return null;
	    }
	    return new AlgebraExpTerm( "" + c );

	} else {

	    return this.comesBefore( term ) ?
		new AlgebraExpPoly( "+", this, term ) :
		new AlgebraExpPoly( "+", term, this ) ;
	}

    }

	private AlgebraExp addTermDec(AlgebraExp term) {

		
		term = term.evalArithmetic();

		String c1 = null;
		String c2 = null;
		double c;
		
		if ( isVarTerm() && term.isVarTerm() && isSameType( term ) ) {

		    c1 = getCoefficient();
		    c2 = term.getCoefficient();
		    try {
			c = (Double.parseDouble( c1 ) + Double.parseDouble( c2 ));
		    } catch (Exception e) {
			trace.out("eqfp", "AlgebraExpTerm: " + this + ".addTerm(" + term + ")");
			return null;
		    }

		    if ( (int)c==0 ) {
			return new AlgebraExpTerm( "0" );
		    } else {
			String v = getVarName();
			return new AlgebraExpTerm( ((int)c == 1 ? "" : ((int)c == -1 ? "-" : "" + c)) + v );
		    }

		} else if ( term.isTerm() && !isVarTerm() && !term.isVarTerm() ) {

		    c1 = toString();
		    c2 = term.toString();
		    try {
			c = (Double.parseDouble( c1 ) + Double.parseDouble( c2 ));
		    } catch (Exception e) {
			trace.out("eqfp", "AlgebraExpTerm: " + this + ".addTerm(" + term + ")");
			return null;
		    }
		    return new AlgebraExpTerm( "" + c );

		} else {

		    return this.comesBefore( term ) ?
			new AlgebraExpPoly( "+", this, term ) :
			new AlgebraExpPoly( "+", term, this ) ;
		}
	}
	public AlgebraExp multTerm( AlgebraExp term ) {

	
    term = term.evalArithmetic();
	
	// ************************************
	// Sun Dec 25 15:35:01 2005
	// Must be fixed:: this is very ad-hoc!
	// for mulTerm( -(x+3), 1/6 ) etc
	// 6-06-06 I think this is fixed now ajzana

	if ( toString().equals("0") )
	    return this;

	if ( term.toString().equals("0") )
	    return term;
	

	AlgebraExp multTerm = null;
	if(this.isParenQuanity())
		return this.evalArithmetic().multTerm(term);
	
	

	if ( term.isFraction() ) {

	    multTerm = term.multTerm( this );
	    
	} else if ( term.isPolynomial() ) {
		if(this.isConstTerm())
			//distribute
			multTerm=new AlgebraExpPoly(term.getOp(),this.multTerm(term.getFirstTerm()),this.multTerm(term.getSecondTerm()));
		else
			multTerm = new AlgebraExpPoly( "*", this, term );

	} else {
		

	    String c1 = isVarTerm() ? getCoefficient() : toString();
	    String c2 =
		term.isVarTerm() ? term.getCoefficient() : term.toString();
	    String c = null;

	    String v = getVarName() + term.getVarName();
	    if(!this.getVarName().equals("")&&this.getVarName().equals(term.getVarName()))
	    	return null;
	    try {
	    
	    if(this.isDecimal() || term.isDecimal())
	    	c = "" + (Double.parseDouble( c1 ) * Double.parseDouble( c2 ));
		else
			c = "" + (Integer.parseInt( c1 ) * Integer.parseInt( c2 ));

		// System.out.println("c = " + c);

		if ( !v.equals("") && c.equals("1") ) {
		    c = "";
		} else if ( !v.equals("") && c.equals("-1") ) {
		    c = "-";
		}
		multTerm = new AlgebraExpTerm( c + v );

	    } catch (NumberFormatException e) {

		trace.out(this + ".AlgebraExpTerm.multTerm(" + term + ")" );
		trace.out("c1 = " + c1 + ", c2 = " + c2);
		e.printStackTrace();
	    }
	}

	/*
	if ( multTerm.toString().toUpperCase().indexOf("XX") != -1 ) {
	    System.out.println(this + ".AlgebraExpTerm.multTerm(" + term + ")");
	    System.out.println(" ==> " + multTerm);
	}
	*/
	
	// Fri Sep 16 16:19:50 2005 :: Ad-hoc solution to avoid having
	// 2nd order equation, for example, "2x * 3x = 6xx"
	//
	try {
	    if ( multTerm != null &&
		 multTerm.toString().toUpperCase().indexOf("XX") != -1 ) {
		
		multTerm = null;
	    }
	} catch (NullPointerException e) {
	    trace.out("missalgebra", this + ".AlgebraExpTerm.multTerm(" + term + ")");
	    trace.out("missalgebra", " ==> " + multTerm);
	    e.printStackTrace();
	}

	return multTerm;
    }

    public AlgebraExp divTerm( AlgebraExp term ) 
    {
    	
    	term = term.evalArithmetic();
    	
    	
    	// System.out.println(this + ".AlgebraExpTerm.divTerm(" + term + ")");
    	if(this.isDecimal()||term.isDecimal())
    		return divTermDec(term);
    	if ( term.isZero() ) {

    	    return null;

    	} else if ( term.isFraction() ) {

    	    if ( !term.getNumerator().isZero() ) {
    		// System.out.println(term + " is a fraction...");
    		return multTerm( new AlgebraExpPoly( "/",
    						     term.getDenominator(),
    						     term.getNumerator() ) );
    	    } else {

    		return null;
    	    }

    	} else if ( term.isMultiplication() ) {

    	    AlgebraExp term1 = divTerm( term.getFirstTerm() );
    	    return (term1 != null) ?
    		term1.divTerm( term.getSecondTerm() ) :
    		null;
    	    
    	} else if ( term.isAddition() ) {

    	    return new AlgebraExpPoly( "/", this, term );
    	    
    	} else if ( term.isTerm() ) {

    	    String dividend = isVarTerm() ? getCoefficient() : toString();
    	    String divisor =
    		term.isVarTerm() ? term.getCoefficient() : term.toString();
    	    String quatient = "";
    	    try {
    		quatient = MathLib.div( dividend, divisor );
    	    } catch (Exception e) {
    		trace.out("missalgebra", e.toString());
    		trace.out("missalgebra", "AlgebraExp.divTerm(" + term + ")");
    	    }

    	    /*
    	    System.out.println("dividend = " + dividend );
    	    System.out.println("divisor = " + divisor );
    	    System.out.println("quatient = " + quatient);
    	    */

    	    // The quatient must have a variable in it...
    	    if ( !isSameType( term ) ) {
    		
    		// Variable goes with a dividend
    		if ( !getVarName().equals("") ) {
    		    
    		    if ( quatient.indexOf('/') < 0 ) {
    			// The quatient is not a fraction hence simply add
    			// the variable name
    			if ( quatient.equals("1") ) {
    			    quatient = "";
    			}
    			quatient += getVarName();
    		    } else {
    			// The quatient is a fraction hence the variable
    			// name must come to the numerator
    			String[] split = quatient.split("/");
    			if ( split[0].equals("1") ) {
    			    split[0] = "";
    			} else if (split[0].equals("-1")) {
    			    split[0] = "-";
    			}
    			quatient = split[0] + getVarName() + "/" + split[1];
    		    }
    		    
    		// Variable goes with a divisor
    		} else {
    		    
    		    if ( quatient.indexOf('/') < 0 ) {
    			quatient += "/" + term.getVarName();
    		    } else {
    			quatient += term.getVarName();
    		    }
    		}
    	    }
    	    
    	    AlgebraExp algebraExp = null;
    	    try {
    		algebraExp = AlgebraExp.parseExp( quatient );
    	    } catch (ParseException e) {
    		e.printStackTrace();
    	    }
    	    
    	    return algebraExp;

    	} else {

    	    return null;
    	}
    }


    private AlgebraExp divTermDec(AlgebraExp term) 
    {
    	
    	term=term.evalArithmetic();
    	if(term.isZero())
    		return null;
    	if(this.isSimpleTerm()&&term.isSimpleTerm())
    	{
    		double top;
    		double bottom;
    		double result;
    		
    		String var="";
    		if(this.isVarTerm())
    			top=Double.parseDouble(this.getCoefficient());
    		
    		
    		else
    			top=Double.parseDouble(this.toString());
    		if(term.isVarTerm())
    		
    			bottom=Double.parseDouble(term.getCoefficient());
    			
    		
    		else
    			bottom=Double.parseDouble(term.toString());
    		result=top/bottom;
    		
    			if(this.isVarTerm()&&term.isVarTerm())
    			{
    				
    				if(this.isSameType(term))
    					var=MathLib.div(this.getVarName(),term.getVarName());
    				else
    					var=this.getVarName()+"/"+term.getVarName();
    				
    			}
    			else
    			{
    				if(this.isVarTerm())
    					var=this.getVarName();
    				else
    					var=term.getVarName();
    			}
    			try
    			{
    			if(Math.abs(Math.floor(result)-result)<0.00001)
    				return AlgebraExp.parseExp(String.valueOf((int)result).concat(var.toString()));
    			else
    				return AlgebraExp.parseExp(String.valueOf(result).concat(var.toString()));
    			
    			}
    			catch(ParseException e)
    			{
    				e.printStackTrace();
    				return null;
    			}
    		
    		
    		
    	}
    	else
    		return this.evalArithmetic().divTerm(term); 
    	
    		
	}
    // -
    // - Printing - * - * - * - * - * - * - * - * - * - * - * - *
    // -

	public String parseTree() {
	return "|" + getExp() + "|";
    }

    public String toString() {
	return getExp();
    }
    

}
