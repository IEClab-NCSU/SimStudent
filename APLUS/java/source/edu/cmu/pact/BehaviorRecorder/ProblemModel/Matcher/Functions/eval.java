/**
 * Copyright 2007 Carnegie Mellon University.
 */
package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.CTATFunctions;
import edu.cmu.pact.Utilities.trace;

/**
 * Simplify an algebraic expression.
 */
public class eval implements UsesProblemModel, UsesVariableTable {
	
	private ProblemModel problemModel;
	private VariableTable variableTable;

	/**
	 * <p>Evaluate an algebraic expression.</p>
	 * @param expr expression to simplify
	 * @return simplified result
	 */
	public Object eval(String expr) {
		CTATFunctions evaluator = null;
		Object result = null;
		try {
			evaluator = new CTATFunctions(variableTable, problemModel, null);
			result = evaluator.evaluate(expr);
			if (trace.getDebugCode("functions")) trace.out("functions", "eval(\""+expr+"\") = "+result);
			return result;               // mismatch on all
		} catch (Exception e) {
			System.err.println("Error from eval(\""+expr+"\"): "+e+
					(e.getCause() == null ? "" : "; cause "+ e.getCause().toString()));
			return null;
		}
	}

	public void setProblemModel(ProblemModel pm) {
		this.problemModel = pm;
	}

	public void setVariableTable(VariableTable variableTable) {
		this.variableTable = variableTable; 
	}

	/**
	 * Test harness: evaluate each argument. Note that all share a {@link VariableTable}
	 * and {@link ProblemModel} instance.
	 * @param args
	 */
	public static void main(String[] args) {
		VariableTable vt = new VariableTable();
		ProblemModel pm = new ProblemModel(null);

		int i = 0;
		for(String expr : args) {
			String result = null;
			try {
				eval ev = new eval();
				ev.setProblemModel(pm);
				ev.setVariableTable(vt);
				result = ev.eval(expr).toString();
			} catch(Exception ex) {
				ex.printStackTrace(System.out);
			}
			System.out.printf("%d. eval %-25s => %s\n", ++i, expr, result);
		}
	}
}
