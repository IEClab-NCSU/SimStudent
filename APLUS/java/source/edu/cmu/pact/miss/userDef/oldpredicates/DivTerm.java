/**
 * Describe class DivTerm here.
 *
 *
 * Created: Thu Mar 10 17:54:42 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class DivTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>DivTerm</code> instance.
     *
     */
    public DivTerm() {

	setName( "div-term" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {

	return divTerm( (String)args.get(0), (String)args.get(1) );
    }
}
