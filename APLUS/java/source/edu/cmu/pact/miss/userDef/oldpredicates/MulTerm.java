/**
 * Describe class MulTerm here.
 *
 *
 * Created: Sat Sep 17 17:49:39 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class MulTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>MulTerm</code> instance.
     *
     */
    public MulTerm() {
	setName( "mul-term" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {

	return mulTerm( (String)args.get(0), (String)args.get(1) );
    }
}
