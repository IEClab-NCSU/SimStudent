package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsEquivalentFraction extends EqFeaturePredicate{
	
	public IsEquivalentFraction(){
				
		setArity(4);
		setName("is-equivalent-fraction");
		setArgValueType(new int[]{TYPE_ARITH_EXP,TYPE_ARITH_EXP,TYPE_ARITH_EXP,TYPE_ARITH_EXP});
		
		
	}

	public String apply(Vector args) 
	{
	
		String expString1 = (String)args.get(0);		
		String expString2 = (String)args.get(1);		
		String expString3 = (String)args.get(1);
		String expString4 = (String)args.get(1);
			
		if (expString1.contains("nil")|| expString2.contains("nil") || expString3.contains("nil") || expString4.contains("nil")) return null;
		
		int numerator1 = Integer.parseInt(expString1);
		int denominator1 = Integer.parseInt(expString2);
		int numerator2 = Integer.parseInt(expString1);
		int denominator2 = Integer.parseInt(expString2);
		
		//cross multiply to check if they are equivalent
		int cross_multiplication1=numerator1*denominator2;
		int cross_multiplication2=numerator2*denominator1;

		return (cross_multiplication1==cross_multiplication2)? "T" : null;
		
	}
}


