/**
 * Describe class Homogeneous here.
 *
 *
 * Created: Sat Apr 09 17:09:42 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class Homogeneous extends EqFeaturePredicate {

    /**
     * Creates a new <code>Homogeneous</code> instance.
     *
     */
    public Homogeneous() {

	setName( "homogeneous" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return homogeneous( (String)args.get(0) );
    }
}
