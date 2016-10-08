/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;

import jess.Context;
import jess.JessException;
import jess.Value;
import jess.ValueVector;

/**
 * @author sewall
 *
 */
public class ConstructSuccessMessage extends ConstructMessage {

	/**
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return "construct-success-message";
	}

	/**
	 * Entry point from Jess.
	 * Calls {@link #call(JessModelTracing.MessageGroup, ValueVector, Context)}.
	 * @param vv arguments from Jess
	 * @param ctx Jess bindings for argument evaluation
	 * @return result from call(Boolean.TRUE, vv, ctx) 
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context ctx) throws JessException {
		return call(JessModelTracing.MessageGroup.Success, vv, ctx); 
	}

	public ConstructSuccessMessage() {
		this(null);
	}

	/**
	 * @param jmt
	 */
	public ConstructSuccessMessage(JessModelTracing jmt) {
		super(jmt);
	}
}