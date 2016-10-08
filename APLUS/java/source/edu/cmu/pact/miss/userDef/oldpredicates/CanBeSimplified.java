/**
 * Describe class CanBeSimplified here.
 *
 *
 * Created: Thu Nov 17 23:23:40 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CanBeSimplified extends EqFeaturePredicate {

    /**
     * Creates a new <code>CanBeSimplified</code> instance.
     *
     */
    public CanBeSimplified() {
	setName( "can-be-simplified" );
	setArity( 1 );
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {

	return canBeSimplified( (String)args.get(0) );
    }
}
