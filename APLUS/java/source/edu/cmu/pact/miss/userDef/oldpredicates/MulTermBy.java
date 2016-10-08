/**
 * Describe class MulTermBy here.
 *
 *
 * Created: Mon Aug 01 16:58:01 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class MulTermBy extends EqFeaturePredicate {

    /**
     * Creates a new <code>MulTermBy</code> instance.
     *
     */
    public MulTermBy() {
	setName( "mul-term-by" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* string */ args ) {
	return mulTermBy( (String)args.get(0), (String)args.get(1) );
    }
}
