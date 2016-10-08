package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.Serializable;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.CTATExpressionParser;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;

/**
 * Determine whether 2 algebraic expressions are equivalent when fully simplified. 
 */
public class algEquiv implements UsesVariableTable, Serializable
{
	/**	Use for access to the variable table. */
	ExpressionTreeProperties properties;

	/**
	 * Determine whether 2 algebraic expressions are equivalent when fully simplified.
	 * @param expression1 first expression, as string with algebra syntax
	 * @param expression2 second expression
	 * @return {@value Boolean#TRUE} if the 2 expressions are equivalent; else false
	 */
	public Boolean algEquiv(String expression1, String expression2)
	{
		CTATExpressionParser matcher = new CTATExpressionParser(properties);
		//Double result = matcher.evaluate(expression);
		Boolean result = matcher.twoExpressionsEqualComplex(expression1, expression2);
		if (trace.getDebugCode("functions"))
			trace.out("functions","CTATEquivalence result = " + result);
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
		algEquiv fn = new algEquiv();
		fn.setVariableTable(new VariableTable());
		if(args.length < 1 || args[0].startsWith("-h")) {
			System.err.printf("Usage:\n"+
					"java -cp ... %s \"expr0\" \"expr1\" ...\n"+
					"where--\n"+
					"  \"expr0\" is an algebraic expression;\n"+
					"  \"expr1\" ... are algebraic expressions to compare against expr0\n",
					fn.getClass().getSimpleName());
			System.exit(2);
		}
		for(int i = 1; i < args.length; ++i) {
			try {
				System.out.printf("%s %s %s\n",
						args[0], (fn.algEquiv(args[0], args[i]) ? "==" : "!="), args[i]);
			} catch (Exception e) {
				System.out.printf("\nError comparing expr[0] \"%s\" with expr[%d] \"%s\": %s; cause %s.\n\n",
						args[0], i, args[i], e.toString(),
						(e.getCause() == null ? "null" : e.getCause().toString()));
				e.printStackTrace(System.out);
			}
		}
	}
}
