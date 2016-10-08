/**
 * Remove the last term as a part of a transfer operation for that
 * term the other side of the equation
 *
 * Created: Mon Mar 14 22:51:10 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 **/

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class RemoveLastTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>RemoveTerm</code> instance.
     *
     */
    public RemoveLastTerm() {

	setName( "remove-last-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return removeLastTerm( (String)args.get(0) );
    }
}
