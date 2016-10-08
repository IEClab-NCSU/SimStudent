//In ASCII output, we clean up terms to, for example, omit 1 coefficients, so 1X prints
//as just X. The usual ASCII output also groups terms into numerator and denominator.
//In this formatter, we include all terms.
//We primarily use this when pattern matching, so that the term "1X" gets the pattern "aX",
//not just "X"

package edu.cmu.old_pact.cmu.sm;

public class ASCIIPlainTermFormatter implements ExpressionFormatter {
	private static ASCIIPlainTermFormatter subFormatter = new ASCIIPlainTermFormatter();
	public ASCIIPlainTermFormatter () {
	}
	
	public String produceOutput(Expression theExpression) {
		//System.out.println("APTF producing output for "+theExpression.debugForm());
		if (theExpression instanceof TermExpression) {
			String outString="";
			TermExpression tEx = (TermExpression)theExpression;
			for (int i=0;i<tEx.numSubTerms();++i) {
				Expression thisSub = tEx.getTerm(i);
				if (i>0)
					outString +="*";
				outString += subFormatter.produceOutput(thisSub);
			}
			return outString;
		}
		else if (theExpression instanceof PolyExpression) {
			PolyExpression pEx = (PolyExpression)theExpression;
			String finalString="";
			for (int i=0;i<pEx.numberOfTerms();++i) {
				Expression exp = pEx.getTermNoSign(i);
				int sign = pEx.getSign(i);
				if (sign == PolyExpression.NEGATIVE)
					finalString += "-";
				else if (i>0)
					finalString += "+";
				finalString += subFormatter.produceOutput(exp);
			}
			return finalString;
		}
		//should be additional cases for ExpTerm, etc...
		else
			return theExpression.toString();
	}
}
