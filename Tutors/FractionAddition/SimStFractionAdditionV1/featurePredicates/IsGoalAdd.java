package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsGoalAdd extends EqFeaturePredicate{
	
	public IsGoalAdd(){
		setArity(1);
		setName("is-goalAdd");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
				
		if (expString1.equals("Add")) return "T";
		else return null;
	}
}


