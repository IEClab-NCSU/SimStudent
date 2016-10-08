/**
 * Describe class DivTen here.
 *
 *
 * Created: Tue Sep 13 15:34:06 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class DivTen extends EqFeaturePredicate {

    /**
     * Creates a new <code>DivTen</code> instance.
     *
     */
    public DivTen() {
	setName( "div-ten" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return divTen( (String)args.get(0) );
    }
}
