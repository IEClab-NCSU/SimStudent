package edu.cmu.pact.miss.userDef.algebra;
import java.util.Vector;
public class Homogeneous extends EqFeaturePredicate 
{
    public Homogeneous() 
    {
	setArity(1);
	setName("homogeneous");
	setArgValueType(new int[]{TYPE_ARITH_EXP});
    }

    public String apply(Vector args) 
    {
	return homogeneous((String)args.get(0)); 
    }
    
}
