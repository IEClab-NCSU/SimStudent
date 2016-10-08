/**
 * 
 */
package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class GetFirstVarString extends EqFeaturePredicate {

    /**
     * 
     */
    public GetFirstVarString() {
        setArity(1);
        setName("get-first-var-string");
        setReturnValueType(TYPE_ARITH_EXP);
        setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    /* (non-Javadoc)
     * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
     */
    public String apply(Vector args) {
        return getFirstVarString((String)args.get(0));
    }

}
