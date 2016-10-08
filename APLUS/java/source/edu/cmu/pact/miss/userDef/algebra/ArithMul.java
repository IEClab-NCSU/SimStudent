/**
 * 
 */
package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class ArithMul extends EqFeaturePredicate {

    /**
     * 
     */
    public ArithMul() {
        setName("arith-mul");
        setArity(2);
    }

    /* (non-Javadoc)
     * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
     */
    public String apply(Vector args) {

        String product = null;
        
        try {
            int n1 = Integer.parseInt((String)args.get(0));
            int n2 = Integer.parseInt((String)args.get(1));
            product = "" + (n1 * n2);

        } catch (NumberFormatException e) {
            ;
        }
        return product;
    }

}
