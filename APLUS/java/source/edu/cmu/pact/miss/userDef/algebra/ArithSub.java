/**
 * 
 */
package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class ArithSub extends EqFeaturePredicate {

    /**
     * 
     */
    public ArithSub() {
        setArity(2);
        setName("arith-sub");
    }

    /* (non-Javadoc)
     * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
     */
    public String apply(Vector args) {
        
        String diff = null;
        
        try {
            int n1 = Integer.parseInt((String)args.get(0));
            int n2 = Integer.parseInt((String)args.get(1));
            diff = "" + (n1 - n2);

        } catch (NumberFormatException e) {
            ;
        }
        return diff;
    }

}
