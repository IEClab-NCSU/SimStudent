/**
 * Describe class Monomial here.
 *
 *
 * Created: Tue Mar 01 22:32:28 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class Monomial extends EqFeaturePredicate {

    /**
     * Creates a new <code>Monomial</code> instance.
     *
     */
    public Monomial() {

	setName( "monomial" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* of String */ args ) {
	return monomial( (String)args.get(0) );
    }

}
