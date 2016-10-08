package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsComplexFractionChunkImproper extends EqFeaturePredicate{
	
	public IsComplexFractionChunkImproper(){
				
		setArity(2);
		setName("is-complex-fraction-improper");
		setArgValueType(new int[]{TYPE_OBJECT});

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
		String wholeString=parts[0];
				
				
		if (!wholeString.contains("nil"))  return null;
	
		if (numeratorString.contains("nil")|| denominatorString.contains("nil")) return null;
		
		int numerator = Integer.parseInt(numeratorString);
		int denominator = Integer.parseInt(denominatorString);


		if (numerator>denominator){
			return "T";
		}
		else {
			return null;
		}
		//return (numerator>denominator) ? "T" : null ;	

	
	}
}


