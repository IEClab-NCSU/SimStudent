package edu.cmu.pact.miss;

import java.util.Vector;

import jess.Context;
import jess.JessException;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.jess.ModelTracingUserfunction;
/**
 * Class representing the user function called by JessModelTracing at each step
 * it supports storing predicates and focuses of attention
 * @author ajzana
 *
 */
public abstract class SimStudentModelTracerFunction implements ModelTracingUserfunction 
{
	
	protected Vector /* of String*/ foas;
	protected Vector /*of FeaturePredicate*/ predicates;
	
	
	public SimStudentModelTracerFunction() 
	{
		foas=new Vector();
		predicates=new Vector();
	}
	
	/**
	 * add foa to the list used by this function
	 * @param foa
	 */
	public void addFoa(String foa)
	{
		foas.add(foa);

	}
	/**
	 * 
	 * @return the list of foas used by this function
	 * 	
	 */
	public Vector /* of String*/ getFoas()
	{
		return foas;
	}
	
	public void resetFoa()
	{
		foas.clear();
	}
	public void addPredicate(FeaturePredicate fp)
	{
		predicates.add(fp);
	}
	public Value call(ValueVector vv, Context context)
	{
		try
		{
			if(vv!=null)
					vv.remove(0);
			return javaCall(vv,context);
		}
		catch(JessException e)
		{
			e.printStackTrace(context.getEngine().getErrStream());
			return new Value(false);
		}
	}

    /**
     * reset the predicate list of this function
     *
     */
	public void resetPredicates()
    {
        predicates.clear();
        
    }
	
    
}
