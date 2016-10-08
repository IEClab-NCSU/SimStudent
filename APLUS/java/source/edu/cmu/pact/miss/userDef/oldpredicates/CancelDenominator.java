/**
 * Describe class CancelDenominator here.
 *
 *
 * Created: Tue Sep 27 16:06:40 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class CancelDenominator extends EqFeaturePredicate {

    /**
     * Creates a new <code>CancelDenominator</code> instance.
     *
     */
    public CancelDenominator() {
	setName( "cancel-denominator" );
	setArity( 1 );
    }

    public String apply( Vector /* String */ args ) {
	return cancelDenominator( (String)args.get(0) );
    }
}
