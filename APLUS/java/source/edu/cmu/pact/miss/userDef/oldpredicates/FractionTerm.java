/**
 * Describe class FractionTerm here.
 *
 *
 * Created: Mon Aug 01 16:30:41 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class FractionTerm extends EqFeaturePredicate {

    /**
     * Creates a new <code>FractionTerm</code> instance.
     *
     */
    public FractionTerm() {
	setName( "fraction-term" );
	setArity( 1 );
    }

    public String apply( Vector /* String */ args ) {
	return isFractionTerm( (String)args.get(0) );
    }

}
