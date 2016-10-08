/**
 * Returns the Least Common Multiple 
 *
 *
 * Created: Tue Sep 20 11:36:47 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

import mylib.MathLib;

public class LCM extends EqFeaturePredicate {

    /**
     * Creates a new <code>LCM</code> instance.
     *
     */
    public LCM() {

	setName( "lcm" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {

	String lcm = null;

	try {
	    int n1 = Integer.parseInt( (String)args.get(0) );
	    int n2 = Integer.parseInt( (String)args.get(1) );
	    
	    if ( n1 > 0 && n2 > 0 )
		lcm = "" + MathLib.lcm( n1, n2 );
	    
	} catch (NumberFormatException e) {
	    ;
	} catch (ArithmeticException e) {
	    // Ignore "/ by zero" exception
	    ;
	}
	
	return lcm;
    }
}
