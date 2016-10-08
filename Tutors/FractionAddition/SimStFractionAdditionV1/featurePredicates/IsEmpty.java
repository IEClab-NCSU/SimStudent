package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsEmpty extends EqFeaturePredicate{
	
	public IsEmpty(){
		setArity(1);
		setName("is-empty");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
				
		if (expString1.isEmpty()) return "T";
		else return null;

	}
}


