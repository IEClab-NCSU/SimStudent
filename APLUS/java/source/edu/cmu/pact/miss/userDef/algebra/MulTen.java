/**
 * 
 */
package edu.cmu.pact.miss.userDef.algebra;

import java.util.Vector;

/**
 * @author mazda
 *
 */
public class MulTen extends EqFeaturePredicate {

    /**
     * 
     */
    public MulTen() {
        setName("mul-ten");
        setArity(1);
    }

    /* (non-Javadoc)
     * @see edu.cmu.pact.miss.FeaturePredicate#apply(java.util.Vector)
     */
    public String apply(Vector args) {
        return mulTen((String)args.get(0));
    }
}
