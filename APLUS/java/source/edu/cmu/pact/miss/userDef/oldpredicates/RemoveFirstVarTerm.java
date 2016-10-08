/**
 * Describe class RemoveFirstVarTerm here.
 *
 *
 * Created: Sun Apr 10 14:49:37 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class RemoveFirstVarTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>RemoveFirstVarTerm</code> instance.
     *
     */
    public RemoveFirstVarTerm() {

	setName( "remove-first-var-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return removeFirstVarTerm( (String)args.get(0) );
    }
}
