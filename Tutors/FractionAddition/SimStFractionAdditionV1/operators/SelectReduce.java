package SimStFractionAdditionV1.operators;

import java.util.Vector;
import edu.cmu.pact.miss.FeaturePredicate;

public class SelectReduce extends FeaturePredicate{
	
	public SelectReduce(){
		setArity(1);
		setName("select-reduce");
		setReturnValueType(TYPE_OBJECT);
		setArgValueType(new int[]{TYPE_OBJECT});
	}

	public String apply(Vector args) 
	{
		
		return (String) "Reduce";
	}
}


