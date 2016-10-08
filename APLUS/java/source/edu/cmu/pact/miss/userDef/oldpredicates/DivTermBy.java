/**
 * Describe class DivTermBy here.
 *
 *
 * Created: Mon Aug 01 16:52:59 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class DivTermBy extends EqFeaturePredicate {

    /**
     * Creates a new <code>DivTermBy</code> instance.
     *
     */
    public DivTermBy() {
	setName( "div-term-by" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return divTermBy( (String)args.get(0), (String)args.get(1) );
    }
}
