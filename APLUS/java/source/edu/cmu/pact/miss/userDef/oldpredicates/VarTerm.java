/**
 * Describe class VarTerm here.
 *
 *
 * Created: Mon Feb 28 17:45:38 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class VarTerm extends EqFeaturePredicate {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new <code>VarTerm</code> instance.
     *
     */
    public VarTerm() {

	setName( "var-term" );
	setArity( 1 ); 
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* of String */ args ) {

	return varTerm( (String)args.get(0) );
    }
}
