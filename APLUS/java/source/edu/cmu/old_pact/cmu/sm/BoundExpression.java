//BoundExpressions are used in patterns (like aX+b=c)
//They are like VariableExpressions, in that they are strings
//But they have the property that all BoundExpressions are algebraicEqual to one another
//(so aX+b equals bX+a equals cX+d) but they can never be combined (so aX+bX never gets
//combined into (a+b)X

package edu.cmu.old_pact.cmu.sm;

public class BoundExpression extends VariableExpression {
	public BoundExpression(String letter) {
		super(letter);
	}

	public boolean exactEqual(Expression ex) {

		if (ex instanceof BoundExpression)
			return true;
		return false;
	}
	
	public String debugForm() {
		return "[bound: "+getString()+"]";
	}
	
	public boolean isLike(Expression ex) {
		return false;
	}
	
	//Bounds sort before variables
        //Bounds sort before other Bounds, if their string is less
	//Bounds sort after numeric expressions (and before everything else)
	public boolean termSortBefore(Expression ex) {
		/*trace.out(debugForm() + ".termSortBefore(" +
		  ex.debugForm() + ")");*/
		if (ex instanceof BoundExpression) {
			BoundExpression vEx = (BoundExpression)ex;
			if (getString().compareTo(vEx.getString()) > 0){
				//trace.out("\ttrue");
				return true;
			}
			else{
				//trace.out("\tfalse");
				return false;
			}
		}
		else if (ex instanceof VariableExpression){
			//trace.out("\ttrue");
			return true;
		}
		else if (ex instanceof NumericExpression){
			//trace.out("\tfalse");
			return false;
		}
		else{
			//trace.out("\ttrue");
			return true;
		}
	}
	
	protected Expression addLikeTerms(Expression ex) {
		throw new IllegalArgumentException("addLikeTerms on BoundExpression");
	}

	protected Expression iMultiply(Expression ex) {
		throw new IllegalArgumentException("iMultiply on BoundExpression");
	}
	
	//Olga
	public double degree() {
		return 0.0;
	}
	//end Olga
}
