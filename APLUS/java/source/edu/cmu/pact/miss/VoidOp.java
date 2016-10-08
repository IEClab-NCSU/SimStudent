/**
 * f:/Project/CTAT/ML/ISS/miss/VoidOp.java
 *
 *	This class is used as a fake operator for RHS composition that
 *	just snip off an variable in the ExpList.  This is needed
 *	because some focusOfAttention WMEs are only used for feature
 *	predicates and not RHS operators, still they appear in the
 *	ExpList.
 *
 * Created: Sat Mar 12 21:59:12 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0 */

package edu.cmu.pact.miss;

import java.util.Vector;



public class VoidOp extends FeaturePredicate {

    /**
     * Creates a new <code>VoidOp</code> instance.
     *
     */
    public VoidOp() {

	setName( "void-op" );
	setArity( 1 );
    }

    public String apply( Vector /* String */ args ) {
	return null;
    }

	

}

//
// end of f:/Project/CTAT/ML/ISS/miss/VoidOp.java
// 
