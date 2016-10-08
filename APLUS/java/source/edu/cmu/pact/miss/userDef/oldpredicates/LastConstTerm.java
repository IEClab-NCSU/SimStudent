/**
 * Describe class LastConstTerm here.
 *
 *
 * Created: Sun Apr 10 11:38:42 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class LastConstTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>LastConstTerm</code> instance.
     *
     */
    public LastConstTerm() {

	setName( "last-const-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return lastConstTerm( (String)args.get(0) );
    }
}
