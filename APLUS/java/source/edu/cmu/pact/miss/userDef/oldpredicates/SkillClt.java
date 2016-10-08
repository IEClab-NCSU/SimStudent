package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class SkillClt extends EqFeaturePredicate {

    /**
     * This skill takes no argument.  It just returns a string "clt"
     */
    public SkillClt() {
        setArity(0);
        setName("skill-clt");
    }
    
    public String apply(Vector args) {
        return "clt";
    }
}
