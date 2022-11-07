package SimStFractionAdditionV1.featurePredicates;

import java.util.Vector;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.userDef.algebra.EqFeaturePredicate;

public class IsComplexFractionChunkComplex extends EqFeaturePredicate{
	
	public IsComplexFractionChunkComplex(){
				
		setArity(2);
		setName("is-complex-fraction-complex");
		setArgValueType(new int[]{TYPE_OBJECT});

	}

	public String apply(Vector args) 
	{
		String expString1 = (String)args.get(0);				
	
	//	trace.out("Chunk value is " + expString1); 
		if (!expString1.contains("["))
			return null;
			
		expString1=expString1.replace("[","");
		expString1=expString1.replace("]","");
		String[] parts=expString1.split(",");
		String numeratorString=parts[1];
		String denominatorString=parts[2];	
		String wholeString=parts[0];
		
		if (wholeString.contains("nil") || numeratorString.contains("nil") || denominatorString.contains("nil") )  return null;
		else return  "T";
		
		
		//if (numeratorString.contains("nil")|| denominatorString.contains("nil")) return null;
		
	
		//trace.out("numerator = " + numerator + ", denominator = " + denominator);
	//	if (numerator<denominator){
//			return "T";
//		}
//		else {
//			return null;
//		}
		//return (numerator>denominator) ? "T" : null ;	

	
	}
}


