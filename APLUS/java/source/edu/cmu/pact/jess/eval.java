/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;

/**
 * Function callable from Jess code (see {@link #getName()} to test
 * student-entered selection, action and input against the given arguments.
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}.
 * @author sewall
 */
public class eval implements Userfunction , Serializable {
	
	/** Function name, as known to Jess. */
	private static final String EVALUATION_FUNCTION_NAME = "eval";

	/** Model tracer instance with student values. */
	protected transient JessModelTracing jmt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;
	
	/**
	 * No-argument constructor for use from (load-function).
	 */
	public eval() {
		this(null);
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public eval(JessModelTracing jmt) {
		super();
		this.jmt = jmt;
		
		
	}	



	/**
	 * Return the name of this function as registered with Jess.
	 * @return "predict-observable-action"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return EVALUATION_FUNCTION_NAME;
	}

	/**
	 * Test the given arguments against the student values stored in
	 * the attached model tracer {@link #jmt}.  <b>This function halts
	 * the Rete when the match fails.</b>
	 * @param vv argument list: order is<ol>
	 *        <li>selection</li>
	 *        <li>action</li>
	 *        <li>input</li>
	 *        </ol>
	 * @param context Jess context for resolving values
	 * @return {@link jess.Funcall#FALSE} on match failure--and halts Rete;
	 *         else {@link jess.Funcall#TRUE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException  {

		
		this.context = context;

		
		if(!vv.get(0).stringValue(context).equals(EVALUATION_FUNCTION_NAME))
			throw new JessException(EVALUATION_FUNCTION_NAME, "called but ValueVector head differs",
					vv.get(0).stringValue(context));
		
		if (getJmt() == null)           // to avoid problems with startup
			return Funcall.TRUE;
		
		String fName = vv.get(1).resolveValue(context).stringValue(context);
		
		
		String[] funArgs = new String[10];


		for (int i=2 ; i < vv.size(); i++)
		{   
			funArgs[i-2] = vv.get(i).resolveValue(context).stringValue(context);
		}


		if (trace.getDebugCode("mt")) trace.out("mt", "(eval "+ fName + " "+ funArgs[0] + " "+funArgs[1] +")");

	//	Value result = context.getEngine().eval(" (sum 2 3) ");	
		Value result = context.getEngine().eval("("+ fName + " "+ funArgs[0] + " "+funArgs[1] +")");

		
		
		String resultStr = result.toStringWithParens();
		trace.out("resultStr = " + resultStr);

		return result;
	}
	/**
	 * Get a reference to the model tracer.  If the field {@link #jmt} is null,
	 * tries to reestablish the connection via {@link MTRete#getJmt()}.
	 * @return Returns the jmt.
	 */
	JessModelTracing getJmt() {
		if (jmt == null) {
			if (context != null) {
				if (context.getEngine() instanceof MTRete)
					jmt = ((MTRete) context.getEngine()).getJmt();
			}
		}
		return jmt;
	}

	/**
	 * @param jmt The jmt to set.
	 */
	void setJmt(JessModelTracing jmt) {
		this.jmt = jmt;
	}


}
