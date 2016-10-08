package SimStFractionAdditionV1.operators;

import java.util.Vector;
import edu.cmu.pact.miss.FeaturePredicate;

public class SelectAdd extends FeaturePredicate{
	
	public SelectAdd(){
		setArity(1);
		setName("select-add");
		setReturnValueType(TYPE_OBJECT);
		setArgValueType(new int[]{TYPE_OBJECT});
	}

	public String apply(Vector args) 
	{
		
		return (String) "Add";
	}
}


