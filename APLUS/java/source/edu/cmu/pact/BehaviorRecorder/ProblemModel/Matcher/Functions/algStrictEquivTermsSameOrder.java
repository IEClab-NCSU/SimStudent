package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

import java.io.Serializable;

import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.CTATExpressionParser;
import edu.cmu.pact.BehaviorRecorder.ExpressionMatcher.ExpressionTreeProperties;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.VariableTable;
import edu.cmu.pact.Utilities.trace;

/**
 * Determine whether 2 algebraic expressions are equivalent when simplified
 * to discrete terms. This function will declare 2 expressions equivalent if
 * their differences are limited to order of commutative factors. Unlike
 * {@link algStrictEquivTerms}, addends in the expressions must be in the same order.
 * Unlike {@link algEquivTerms}, it will distinguish terms with and without parentheses and
 * terms with implied operators from those with explicit operator: 2*x != 2x.
 */
public class algStrictEquivTermsSameOrder implements UsesVariableTable, Serializable
{
	ExpressionTreeProperties properties;

	/**
	 * Determine whether 2 algebraic expressions are equivalent when simplified
	 * to discrete terms. This function will declare 2 expressions equivalent if
	 * their differences are limited to order of commutative factors. Unlike
	 * {@link algStrictEquivTerms}, addends in the expressions must be in the same order.
	 * Unlike {@link algEquivTerms}, it will distinguish terms with and without parentheses and
	 * terms with implied operators from those with explicit operator: 2*x != 2x.
	 * @param expression1 first expression, as string with algebra syntax
	 * @param expression2 second expression
	 * @return {@value Boolean#TRUE} if the 2 expressions are equivalent; else false
	 */	
	public Boolean algStrictEquivTermsSameOrder(String expression1, String expression2)
	{
		CTATExpressionParser matcher = new CTATExpressionParser(properties);
		String s1 = matcher.strictBasicTermsUnsorted(expression1);
		String s2 = matcher.strictBasicTermsUnsorted(expression2);
		Boolean result = null;
		if(s1 == null)
			result = new Boolean(s2 == null);
		else
			result = new Boolean(s1.equalsIgnoreCase(s2));
		if (trace.getDebugCode("functions"))
			trace.out("functions", "CTATEquivalence result = " + result);
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
		algStrictEquivTermsSameOrder fn = new algStrictEquivTermsSameOrder();
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
						args[0], (fn.algStrictEquivTermsSameOrder(args[0], args[i]) ? "==" : "!="), args[i]);
			} catch (Exception e) {
				System.out.printf("\nError comparing expr[0] \"%s\" with expr[%d] \"%s\": %s; cause %s.\n\n",
						args[0], i, args[i], e.toString(),
						(e.getCause() == null ? "null" : e.getCause().toString()));
				e.printStackTrace(System.out);
			}
		}
	}
}