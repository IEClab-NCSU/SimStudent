/**
 * Describe class NotNull here.
 *
 *
 * Created: Tue Apr 12 23:01:40 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class NotNull extends EqFeaturePredicate {

    /**
     * Creates a new <code>NotNull</code> instance.
     *
     */
    public NotNull() {

	setName( "not-null" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return !((String)args.get(0)).equals("") ? "T" : null;
    }
}
