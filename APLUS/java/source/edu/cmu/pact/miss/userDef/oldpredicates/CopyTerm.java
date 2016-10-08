/**
 * Describe class CopyTerm here.
 *
 *
 * Created: Tue Apr 12 16:42:27 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CopyTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>CopyTerm</code> instance.
     *
     */
    public CopyTerm() {
	setName( "copy-term" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return (String)args.get(0);
    }
}
