package edu.cmu.pact.miss.userDef.algebra;
import java.util.Vector;
public class Homogeneous extends EqFeaturePredicate 
{
    public Homogeneous() 
    {
	setArity(1);
	setName("homogeneous");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
	String definition_homogenous = "contain a single term which is either a "
			+ "constant or a variable or a multiplication of constant and variable or "
			+ "contain multiple terms of which all are variable terms or all are constant "
			+ "terms";
	setFeatureDescription(getName(),definition_homogenous);
    }

    public String apply(Vector args) 
    {
	return homogeneous((String)args.get(0)); 
    }
    
}
