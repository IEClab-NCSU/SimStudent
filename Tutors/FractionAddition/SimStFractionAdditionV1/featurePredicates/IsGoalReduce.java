package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsGoalReduce extends EqFeaturePredicate{
	
	public IsGoalReduce(){
		setArity(1);
		setName("is-goalReduce");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
				
		if (expString1.equals("Reduce")) return "T";
		else return null;
	}
}


