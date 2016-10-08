/**
 * 
 */
package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class GetFirstNumberString extends EqFeaturePredicate {

    /*
     * 
     */
    public GetFirstNumberString() {
        setArity(1);
        setName("get-first-number-string");
        setReturnValueType(TYPE_ARITH_EXP);
        setArgValueType(new int[]{TYPE_ARITH_EXP});
    }
    
    public String apply(Vector args) {
        return getFirstNumberString((String)args.get(0));
    }
}
