package edu.cmu.old_pact.cmu.solver.uiwidgets;

//new exception class
public class UninitializedError extends Error {
	public UninitializedError (String text) {
		super(text);
	}
}
