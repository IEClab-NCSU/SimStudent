/**
 * Copyright 2009 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;

/**
 * An optional interface for Function classes: those that implement will get a
 * reference to the ProblemModel object, which has links to all the context of
 * execution, right after instantiation.
 * See {@link CTATFunctions#FUNCALL(Object, Object, Object, Object)}
 * @author sewall
 */
public interface UsesProblemModel {

	/**
	 * Provide the current {@link ProblemModel} instance.
	 * @param pm
	 */
	public void setProblemModel(ProblemModel pm);
}
