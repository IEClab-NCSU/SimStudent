/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;

/**
 * An optional interface for Function classes: those that implement will get a
 * reference to the current VariableTable object, specific to the interpretation
 * in whose context the matcher function is called.
 * See {@link CTATFunctions#FUNCALL(Object, Object, Object, Object)}
 * @author sewall
 */
public interface UsesVariableTable {

	/**
	 * Provide the current {@link VariableTable} instance.
	 * @param variableTable
	 */
	public void setVariableTable(VariableTable variableTable);
}
