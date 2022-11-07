package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

/*ComparisonTest is an abstract class that executes two queries and
  then calls one of the SymbolManipulator's algebraic comparison
  functions on the resultant expressions.*/

public abstract class ComparisonTest extends Test {
	String[] first;
	String[] second;
	static SymbolManipulator sm = new SymbolManipulator();

	static{
		sm.setMaintainVarList(true);
	}

	ComparisonTest(String[] one,String[] two){
		first = one;
		second = two;
	}

	abstract boolean compare(String s1,String s2) throws BadExpressionError;

	public boolean passes(Queryable info){
		try {
			String firstValue = info.evalQuery(first).getStringValue();
			String secondValue = info.evalQuery(second).getStringValue();
			boolean toret = false;
			if(compare(firstValue,secondValue)){
				toret = true;
			}
			return toret;
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
		catch (BadExpressionError err) {
			if(Rule.debug()){
				trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}
}
