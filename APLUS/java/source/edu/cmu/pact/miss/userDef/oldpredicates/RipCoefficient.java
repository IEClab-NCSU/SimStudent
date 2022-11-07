/**
 * Divid a variable term with its coefficient, resulting to have only
 * the variable trem
 *
 *
 * Created: Mon Mar 14 17:27:05 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class RipCoefficient extends EqFeaturePredicate {

    /**
     * Creates a new <code>RipCoefficient</code> instance.
     *
     */
    public RipCoefficient() {

	setName( "rip-coefficient" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	// trace.out("RipCoefficient.apply(" + (String)args.get(0) + ")");
	return ripCoefficient( (String)args.get(0) );
    }
}
