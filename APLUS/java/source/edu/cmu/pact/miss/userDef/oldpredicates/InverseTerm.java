/**
 * Describe class InverseTerm here.
 *
 *
 * Created: Tue Mar 15 16:54:18 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class InverseTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>InverseTerm</code> instance.
     *
     */
    public InverseTerm() {

	setName( "inverse-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return inverseTerm( (String)args.get(0) );
    }
}
