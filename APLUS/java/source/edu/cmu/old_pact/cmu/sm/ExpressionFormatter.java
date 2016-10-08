//an ExpressionFormatter can be defined to produce any kind of output needed
//(anyone want to write a LaTex ExpressionFormatter?)

package edu.cmu.old_pact.cmu.sm;

public interface ExpressionFormatter {
	public String produceOutput(Expression theExpression);
}
