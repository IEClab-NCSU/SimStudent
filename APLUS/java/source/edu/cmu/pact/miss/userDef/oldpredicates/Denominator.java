/**
 * Describe class Denominator here.
 *
 *
 * Created: Fri Aug 05 16:40:56 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class Denominator extends EqFeaturePredicate {

    /**
     * Creates a new <code>Denominator</code> instance.
     *
     */
    public Denominator() {

	setName( "denominator" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return denominator( (String)args.get(0) );
    }
}
