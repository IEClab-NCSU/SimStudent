/**
 * f:/Project/CTAT/ML/ISS/miss/userDef/Coefficient.java
 *
 *
 * Created: Mon Feb 28 17:32:41 2005
 *
 * @author <a href="mailto:mazda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 **/

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class Coefficient extends EqFeaturePredicate {

    /**
     * Creates a new <code>Coefficient</code> instance.
     *
     */
    public Coefficient() {

	setName( "coefficient" );
	setArity( 1 );
	setReturnValueType(TYPE_ARITH_EXP);
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* of String */ args ) {
	return coefficient( (String)args.get(0) );
    }

}

//
// end of f:/Project/CTAT/ML/ISS/miss/userDef/Coefficient.java
// 
