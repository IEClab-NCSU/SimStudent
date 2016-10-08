package edu.cmu.pact.BehaviorRecorder.ProblemModel.Matcher.Functions;

/**
 * Evaluate the given expression as a desk calculator would. Uses {@link Expression#compute()}.
 */
public class calc {

	/**
	 * <p>Evaluate the given expression as a desk calculator would.</p> Uses {@link SymbolManipulator#calc(String)}.
	 * @param expr arithmetic expression to compute. 
	 * @return calculated value
	 * @throws RuntimeException
	 */
	public double calc(String expr) throws RuntimeException {
		return SymbolManipulator.calc(expr);
	}
	
	/**
	 * @param args expressions to compute
	 */
	public static void main(String[] args) {
		calc c = new calc();
		for (String expr : args) {
			try {
				System.out.printf("%-12s = %f\n", expr, c.calc(expr));
			} catch (Exception e) {
				;  // handled below
			}
		}
	}

}
