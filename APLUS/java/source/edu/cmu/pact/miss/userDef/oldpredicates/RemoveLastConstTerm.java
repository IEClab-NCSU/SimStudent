/**
 * Describe class RemoveLastConstTerm here.
 *
 *
 * Created: Sat Apr 09 22:06:12 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class RemoveLastConstTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>RemoveLastConstTerm</code> instance.
     *
     */
    public RemoveLastConstTerm() {

	setName( "remove-last-const-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return removeLastConstTerm( (String)args.get(0) );
    }
}
