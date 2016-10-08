/**
 * Describe class AddTermBy here.
 *
 *
 * Created: Mon Aug 01 16:55:08 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class AddTermBy extends EqFeaturePredicate {

    /**
     * Creates a new <code>AddTermBy</code> instance.
     *
     */
    public AddTermBy() {
	setName( "add-term-by" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return addTermBy( (String)args.get(0), (String)args.get(1) );
    }
}
