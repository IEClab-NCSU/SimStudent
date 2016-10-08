/**
 * Describe class ReverseSign here.
 *
 *
 * Created: Sat Aug 06 03:52:04 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class ReverseSign extends EqFeaturePredicate {

    /**
     * Creates a new <code>ReverseSign</code> instance.
     *
     */
    public ReverseSign() {
	setName( "reverse-sign" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return reverseSign( (String)args.get(0) );
    }

}
