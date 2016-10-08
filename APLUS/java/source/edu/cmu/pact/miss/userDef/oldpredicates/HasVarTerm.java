/**
 * Describe class HasVarTerm here.
 *
 *
 * Created: Sat Mar 19 14:39:45 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class HasVarTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>HasVarTerm</code> instance.
     *
     */
    public HasVarTerm() {

	setName( "has-var-term" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return hasVarTerm( (String)args.get(0) );
    }

}
