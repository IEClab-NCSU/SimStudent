/**
 * Copyright (c) 2013 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.CTATExpressionParser;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;

/**
 * @author sewall
 *
 */
public class isAlgValid implements UsesVariableTable, Serializable {

	private ExpressionTreeProperties properties;

	/**
	 * Determine whether the argument is a valid algebraic expression. Returns false
	 * if {@link CTATExpressionParser#stringToExpressionTreeNode(String)} returns null.
	 * @param expression string to try to parse
	 * @return true if the expression is valid; else false
	 */	
	public Boolean isAlgValid(String expression)
	{
		CTATExpressionParser matcher = new CTATExpressionParser(properties);
		boolean result = (matcher.stringToExpressionTreeNode(expression) != null);
		if (trace.getDebugCode("functions"))
			trace.out("functions", "isAlgValid("+expression+") result = " + result);
		return result;
	}

	@Override
	public void setVariableTable(VariableTable variableTable) {
		if(properties == null)
			properties = new ExpressionTreeProperties();
		if (variableTable == null)
			properties.variableTable = new VariableTable();
		else
			properties.variableTable = variableTable;
	}

	/**
	 * Test harness.
	 * @param args expressions to evaluate
	 */
	public static void main(String[] args) {
		isAlgValid fn = new isAlgValid();
		if(args.length < 1 || args[0].startsWith("-h")) {
			System.err.printf("Usage:\n"+
					"java -cp ... %s \"expression to evaluate\" ...\n"+
					"where--\n"+
					"  \"expression to evaluate\" is an algebraic expression.\n",
					fn.getClass().getSimpleName());
			System.exit(2);
		}
		for(String arg : args)
			System.out.printf("%-5b <= %s\n", fn.isAlgValid(arg), arg);
	}
}

