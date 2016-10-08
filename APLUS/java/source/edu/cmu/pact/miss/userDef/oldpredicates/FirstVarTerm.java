/**
 * Describe class FirstVarTerm here.
 *
 *
 * Created: Sun Apr 10 14:11:35 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class FirstVarTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>FirstVarTerm</code> instance.
     *
     */
    public FirstVarTerm() {

	setName( "first-var-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {

	return firstVarTerm( (String)args.get(0) );
    }
}
