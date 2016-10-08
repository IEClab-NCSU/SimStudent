/**
 * Describe class SubTermBy here.
 *
 *
 * Created: Mon Aug 01 16:59:37 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */
package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class SubTermBy extends EqFeaturePredicate {

    /**
     * Creates a new <code>SubTermBy</code> instance.
     *
     */
    public SubTermBy() {
	setName( "sub-term-by" );
	setArity( 2 );
    }

    public String apply( Vector /* String */ args ) {
	return subTermBy( (String)args.get(0), (String)args.get(1) );
    }
}
