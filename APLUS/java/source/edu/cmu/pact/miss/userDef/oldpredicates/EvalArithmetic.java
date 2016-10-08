/**
 * Describe class EvalArithmetic here.
 *
 *
 * Created: Tue Mar 15 17:29:18 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class EvalArithmetic extends EqFeaturePredicate {

    /**
     * Creates a new <code>EvalArithmetic</code> instance.
     *
     */
    public EvalArithmetic() {

	setName( "eval-arithmetic" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return evalArithmetic( (String)args.get(0) );
    }
}
