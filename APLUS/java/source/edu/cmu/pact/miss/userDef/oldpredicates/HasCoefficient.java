/**
 * Describe class HasCoefficient here.
 *
 *
 * Created: Fri Jun 03 11:03:05 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class HasCoefficient extends EqFeaturePredicate {

    /**
     * Creates a new <code>HasCoefficient</code> instance.
     *
     */
	public HasCoefficient() {

		setName( "has-coefficient" );
		setArity( 1 );
		setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply( Vector /* String */ args ) {
    	return hasCoefficient( (String)args.get(0) );
    }
    @Override
    public String getDescription() {
    	return "contains a variable term that has a coefficient";
    }
    
}
