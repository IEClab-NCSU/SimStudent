package edu.cmu.pact.jess;

import jess.Context;
import jess.JessException;
import jess.Rete;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
/**
 * This interface is used to define a userfunction which will be called by the ModelTracer with every step. 
 * @author ajzana 7-12-06
 * USEAGE:
 *  The method that is called by the java ModelTracer is javaCall()
 *  The call method is called by jess and differs by the fact that jess pasts in the name of the user function 
 *  as the first argument of vv
 *The getArguments method is used by the model tracer call to construct arguments, given an SAI
 */
public interface ModelTracingUserfunction extends Userfunction {
	
	/**
	 *  given an SAI tuple, figure out what arguments to call this function with
	 * @param selection the selection part of the SAI tuple
	 * @param action the action part of the SAI tuple
	 * @param input the input part of the SAI tuple
	 * @parma rete the Rete for the model tracer
	 * @return a Jess ValueVector containing the arguments to call 
	 */
	
	
	public abstract ValueVector getArguments(String selection, String action, String input,Rete rete) throws JessException;

	/**
	 * 
	 * @param vv a ValueVector containing the argments to the function (without the function name)
	 * @param context a Jess context
	 * @return a true Jess Value
	 * @throws JessException
	 */
	public abstract Value javaCall(ValueVector vv, Context context) throws JessException;
	
}
