/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import java.io.Serializable;

import edu.cmu.pact.Utilities.trace;

import jess.Context;
import jess.Funcall;
import jess.JessException;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;

/**
 * A Jess boolean function (is-hint) that tells whether the latest student request
 * was a hint ({@value Funcall#TRUE}) or an attempt ({@value Funcall#FALSE}).
 */
public class IsHint implements Userfunction, Serializable {
	
	/** Function name, as known to Jess. */
	private static final String IS_HINT = "is-hint";

	/**
	 * Return the name of this function as registered with Jess.
	 * @return {@value #IS_HINT}
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return IS_HINT;
	}

	/**
	 * Returns value of value of {@link JessModelTracing#isHintTrace()}.
	 * If {@link #jmt} is null, prints a stack trace to stderr and returns {@value Funcall#FALSE}.
	 * @return {@value Funcall#TRUE} or {@value Funcall#FALSE} 
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context context) throws JessException {
		JessModelTracing jmt = getJmt(context);
		if(jmt == null) {
			try {
				throw new IllegalStateException("is-hint called when JessModelTracing reference is null");
			} catch (Exception e) {
				trace.errStack(e.toString(), e);
			}
			return Funcall.FALSE;
		}
		if(trace.getDebugCode("ishint"))
			trace.out("ishint", "(is-hint) jmt@"+hashCode()+" isHintTrace() now "+jmt.isHintTrace());
		if(jmt.isHintMsgsRequired())
			return Funcall.TRUE;
		else
			return Funcall.FALSE;
	}

	/**
	 * Get a reference to the model tracer via {@link MTRete#getJmt()}.
	 * @return {@link MTRete#getJmt()}
	 */
	protected JessModelTracing getJmt(Context context) {
		if (context == null)
			return null;
		if (context.getEngine() instanceof MTRete)
			return ((MTRete) context.getEngine()).getJmt();
		//if(context.getEngine() instanceof SimStRete) 
			//return ((SimStRete)context.getEngine()).getJmt();
		return null;
	}
}
