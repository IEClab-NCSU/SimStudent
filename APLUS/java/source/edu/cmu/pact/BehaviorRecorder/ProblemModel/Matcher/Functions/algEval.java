package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.CTATExpressionParser;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;

/**
 * Attempt to evaluate an expression given the variableTable and brd-specific-settings.
 */
public class algEval implements UsesVariableTable, Serializable
{
	ExpressionTreeProperties properties;
	
	/**
	 * Attempt to evaluate an expression given the variableTable and brd-specific-settings.
	 * Warning: A valid expression can evaluate to a NaN (i.e. (-1)^(-1))
	 * @param expression String representing an algebraic expression.
	 * @return Double representing the evaluated value or NaN if any issues were encountered.
	 */
	public Double algEval(String expression){		
		CTATExpressionParser matcher = new CTATExpressionParser(properties);
		Double result = matcher.evaluate(expression);
		if (trace.getDebugCode("functions"))
			trace.out("functions", "CTATEvaluate result = " + result);
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
	 * @param first arg is expression to evaluate
	 */
	public static void main(String[] args) {
		algEval ae = new algEval();
		fmtDecimal fd = null;
		int prec = 4;
		if(args.length < 1 || args[0].startsWith("-h")) {
			System.err.printf("Usage:\n"+
					"java -cp ... %s [-f[p]] \"expression to evaluate\"\n"+
					"where--\n"+
					"  -f[p] means post-process with fmtDecimal() using precision p (default %d);\n"+
					"  \"expression to evaluate\" is an algebraic expression.\n",
					ae.getClass().getSimpleName(), prec);
			System.exit(2);
		}
		List<String> argList = Arrays.asList(args);
		int i = 0;
		if(argList.get(i).startsWith("-f")) {
			fd = new fmtDecimal();
			String p = argList.get(i++).substring(2);
			if(p.length() > 0)
				prec = Integer.valueOf(p);
		}
		for(String arg : argList.subList(i, argList.size())) {
			Double d = ae.algEval(arg);
			if(fd == null)
				System.out.printf("%11.6f = %s\n", d, arg);
			else
				System.out.printf("%11s = %11.6f = %s\n", fd.fmtDecimal(d.doubleValue(), prec), d, arg);
		}
	}
}
