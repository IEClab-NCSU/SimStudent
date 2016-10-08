/*
 * Copyright 2005 Carnegie Mellon University.
 */
package edu.cmu.pact.jess;


/**
 * Function callable from Jess code (see {@link #getName()} to test
 * student-entered selection, action and input against the given arguments.
 * Will test against selection, action and input values currently in
 * {@link JessModelTracing}.
 * @author sewall
 */
public class predict extends PredictObservableAction {
	
	/** Function name, as known to Jess. */
	private static final String PREDICT = "predict";


	/**
	 * No-argument constructor for use from (load-function).
	 */
	public predict() {
		super();
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public predict(JessModelTracing jmt) {
		super();

	}

	/**
	 * Return the name of this function as registered with Jess.
	 * @return "PREDICT"
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return PREDICT;
	}
}
