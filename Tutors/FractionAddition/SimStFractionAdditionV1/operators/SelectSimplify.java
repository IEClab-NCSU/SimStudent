package SimStFractionAdditionV1.operators;

import java.util.Vector;
import edu.cmu.pact.miss.FeaturePredicate;

public class SelectSimplify extends FeaturePredicate{
	
	public SelectSimplify(){
		setArity(1);
		setName("select-simplify");
		setReturnValueType(TYPE_OBJECT);
		setArgValueType(new int[]{TYPE_OBJECT});
	}

	public String apply(Vector args) 
	{
		
		return (String) "Simplify";
	}
}


