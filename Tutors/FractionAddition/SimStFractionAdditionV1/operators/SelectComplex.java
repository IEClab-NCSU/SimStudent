package SimStFractionAdditionV1.operators;

import java.util.Vector;
import edu.cmu.pact.miss.FeaturePredicate;


public class SelectComplex extends FeaturePredicate{
	
	public SelectComplex(){
		setArity(1);
		setName("select-complex");
		setReturnValueType(TYPE_OBJECT);
		setArgValueType(new int[]{TYPE_OBJECT});
	}

	public String apply(Vector args) 
	{

		return (String) "Conversion";
	}
}


