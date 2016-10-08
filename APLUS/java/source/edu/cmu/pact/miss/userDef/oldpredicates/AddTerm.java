/**
 * Describe class AddTerm here.
 *
 *
 * Created: Tue Mar 15 17:00:19 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;
public class AddTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>AddTerm</code> instance.
     *
     */
    public AddTerm() {

	setName( "add-term" );
	setArity( 2 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP, TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return addTerm( (String)args.get(0), (String)args.get(1) );
    }

}
