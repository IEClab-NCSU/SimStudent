/**
 * Returns the Greatest Common Divisor
 *
 *
 * Created: Tue Sep 20 11:32:55 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

import mylib.MathLib;

public class GCD extends EqFeaturePredicate {

    /**
     * Creates a new <code>GCD</code> instance.
     *
     */
    public GCD() {
	setName( "gcd" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {

	String gcd = null;

	try {
	    int n1 = Integer.parseInt( (String)args.get(0) );
	    int n2 = Integer.parseInt( (String)args.get(1) );
	    
	    if ( n1 > 0 && n2 > 0 )
		gcd = "" + MathLib.gcd( n1, n2 );
	    
	} catch (NumberFormatException e) {
	    // Just ignore a parse error, it must be due to having a
	    // fraction (e.g., 1/9)
	    ;
	} catch (ArithmeticException e) {
	    // Ignore "/ by zero" exception
	    ;
	}
	
	return gcd;
    }
}
