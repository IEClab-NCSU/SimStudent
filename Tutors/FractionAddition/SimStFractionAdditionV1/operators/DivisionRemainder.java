package SimStFractionAdditionV1.operators;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class DivisionRemainder extends EqFeaturePredicate{
	
	public DivisionRemainder(){
		setArity(2);
		setName("division-remainder");
		setReturnValueType(TYPE_ARITH_EXP);
		setArgValueType(new int[]{TYPE_ARITH_EXP,TYPE_ARITH_EXP});
	}

	public String apply(Vector args) 
	{

		String expString1 = (String)args.get(0);
		String expString2 = (String)args.get(1);
		
		
		int numerator = Integer.parseInt(expString1);
		int denominator = Integer.parseInt(expString2);
		int integerPart = (int) numerator/denominator;

		int mixedNumerator=numerator-integerPart*denominator;

		return (String) Integer.toString(mixedNumerator);
		
	}
}


