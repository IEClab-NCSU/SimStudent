/**
 * Describe class HasConstTerm here.
 *
 *
 * Created: Tue Apr 12 15:55:50 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class HasConstTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>HasConstTerm</code> instance.
     *
     */
    public HasConstTerm() {

	setName( "has-const-term" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return hasConstTerm( (String)args.get(0) );
    }
}
