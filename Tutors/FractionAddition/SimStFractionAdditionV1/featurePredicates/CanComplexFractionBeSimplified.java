package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class CanComplexFractionBeSimplified extends EqFeaturePredicate{
	
	public CanComplexFractionBeSimplified(){
				
		setArity(2);
		setName("can-complex-fraction-be-simplified");
		setArgValueType(new int[]{TYPE_OBJECT});

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
	
		if (!expString1.contains("["))
			return null;
			
		expString1=expString1.replace("[","");
		expString1=expString1.replace("]","");
		String[] parts=expString1.split(",");
		String numeratorString=parts[1];
		String denominatorString=parts[2];	

	
		if (numeratorString.contains("nil")|| denominatorString.contains("nil")) return null;
		
		int numerator = Integer.parseInt(numeratorString);
		int denominator = Integer.parseInt(denominatorString);

		int gcd1=gcd(numerator,denominator);
		
		if (gcd1>1){
			return "T";
		}
		else {
			return null;
		}
	
	}
}


