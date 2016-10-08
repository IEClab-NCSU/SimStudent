/**
 * Describe class Numerator here.
 *
 *
 * Created: Sat Aug 06 02:03:32 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class Numerator extends EqFeaturePredicate {

    /**
     * Creates a new <code>Numerator</code> instance.
     *
     */
    public Numerator() {

	setName( "numerator" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return numerator( (String)args.get(0) );
    }
}
