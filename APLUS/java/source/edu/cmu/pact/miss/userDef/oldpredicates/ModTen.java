/**
 * Describe class ModTen here.
 *
 *
 * Created: Tue Sep 13 15:27:56 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class ModTen extends EqFeaturePredicate {

    /**
     * Creates a new <code>ModTen</code> instance.
     *
     */
    public ModTen() {
	setName( "mod-ten" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
	return modTen( (String)args.get(0) );
    }
}
