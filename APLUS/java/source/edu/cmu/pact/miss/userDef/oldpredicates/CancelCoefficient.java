/**
 * Describe class CancelCoefficient here.
 *
 *
 * Created: Tue Sep 27 16:28:25 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CancelCoefficient extends EqFeaturePredicate {

    /**
     * Creates a new <code>CancelCoefficient</code> instance.
     *
     */
    public CancelCoefficient() {
	setName( "cancel-coefficient" );
	setArity( 1 );
    }

    public String apply( Vector /* String */ args ) {

	return cancelCoefficient( (String)args.get(0) );
    }
}
