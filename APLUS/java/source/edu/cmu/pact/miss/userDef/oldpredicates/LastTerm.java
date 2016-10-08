/**
 * Describe class LastTerm here.
 *
 *
 * Created: Tue Mar 15 16:33:17 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class LastTerm extends EqFeaturePredicate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>LastTerm</code> instance.
     *
     */
    public LastTerm() {

	setName( "last-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return lastTerm( (String)args.get(0) );
    }
}
