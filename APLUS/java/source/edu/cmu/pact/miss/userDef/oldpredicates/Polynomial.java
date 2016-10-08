/**
 * Describe class Polynomial here.
 *
 *
 * Created: Mon Mar 07 17:59:11 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class Polynomial extends EqFeaturePredicate {

    /**
     * Creates a new <code>Polynomial</code> instance.
     *
     */
    public Polynomial() {

	setName( "polynomial" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply( Vector /* of String */ args ) {
	return polynomial( (String)args.get(0) );
    }

}
