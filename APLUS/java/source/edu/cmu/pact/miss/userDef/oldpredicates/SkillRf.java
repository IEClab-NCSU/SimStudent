package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class SkillRf extends EqFeaturePredicate {

    public SkillRf() {
        setArity(0);
        setName("skill-rf");
    }

    public String apply(Vector args) {
        return "rf";
    }

}
