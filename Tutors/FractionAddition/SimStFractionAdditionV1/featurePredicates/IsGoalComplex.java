package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsGoalComplex extends EqFeaturePredicate{
	
	public IsGoalComplex(){
		setArity(1);
		setName("is-goalComplex");
		setArgValueType(new int[]{TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);
				
				
		//System.out.println("*** Checking for complex fraction in " + expString1 + " and this is " + expString1.equals("ComplexFraction"));
				
		if (expString1.equals("Conversion")) return "T";
		else return null;
	}
}


