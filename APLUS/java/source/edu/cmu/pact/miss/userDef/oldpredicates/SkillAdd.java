/**
 * @author mazda
 * 
 * For Carnegie Algebra I Tutor 
 * Emulating student's action to select a pull down menu to identy an action to be taken
 * 
 */

package edu.cmu.pact.miss.userDef.oldpredicates;

import java.util.Vector;

public class SkillAdd extends EqFeaturePredicate {
    
    public SkillAdd() {
	setArity(1);
	setName( "skill-add" );
    }

    public String apply(Vector args) {
	
	return "add " + (String)args.get(0);
    }

}
