package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;

import cl.utilities.sm.Expression;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class CanBeSimplified extends EqFeaturePredicate{
	
	public CanBeSimplified(){
		//setArity(1);
		//setName("is-improper-fraction");
		//setArgValueType(new int[]{TYPE_ARITH_EXP});
		
		setArity(2);
		setName("can-be-simplified");
		//setArgValueType(new int[]{TYPE_ARITH_EXP});
		setArgValueType(new int[]{TYPE_ARITH_EXP,TYPE_ARITH_EXP});
		
		
	}


 	public int gcd(int number1, int number2) {
        //base case
        if(number2 == 0){
            return number1;
        }
        return gcd(number2, number1%number2);
    }
    
    
	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);		
		String expString2 = (String)args.get(1);		
	
		if (expString1.contains("nil")|| expString2.contains("nil")) return null;
		if (expString1.contains("Specified")|| expString2.contains("Specified")) return null;
		if (expString1.contains("Reduce")|| expString2.contains("Reduce")) return null;
		if (expString1.contains("Add")|| expString2.contains("Add")) return null;
		
		
		int numerator = Integer.parseInt(expString1);
		int denominator = Integer.parseInt(expString2);

		if (numerator==denominator) return null;
		
		int gcd1=gcd(numerator,denominator);
		
		if (gcd1>1){
			return "T";
		}
		else {
			return null;
		}
		
	
	}
}


