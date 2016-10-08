/**
 * Describe class CancelLastConstTerm here.
 *
 *
 * Created: Tue Sep 27 23:54:31 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CancelLastConstTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>CancelLastConstTerm</code> instance.
     *
     */
    public CancelLastConstTerm() {
	setName( "cancel-last-const-term" );
	setArity( 1 );
    }

    public String apply( Vector /* String */ args ) {
	return cancelLastConstTerm( (String)args.get(0) );
    }
}
