package edu.cmu.pact.jess;

import jess.Context;
import jess.JessException;
import jess.Value;
import jess.ValueVector;

public class ConstructHintMessage extends ConstructMessage {

	/**
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return "construct-hint-message";
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
		return call(JessModelTracing.MessageGroup.Hint, vv, ctx); 
	}

	public ConstructHintMessage() {
		this(null);
	}

	/**
	 * @param jmt
	 */
	public ConstructHintMessage(JessModelTracing jmt) {
		super(jmt);
	}
}
