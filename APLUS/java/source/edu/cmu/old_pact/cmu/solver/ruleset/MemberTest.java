package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.BadExpressionError;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//MemberTest makes sure that the item given is found in the list
//We use algebraicEqual as the comparison
public class MemberTest extends Test {
	String[] list;

	public MemberTest(String[] single,String[] multiple){
		propertyString = single;
		list = multiple;
	}

	public MemberTest(String[] single,String multiple){
		this(single,new String[] {multiple});
	}

	public MemberTest(String single,String[] multiple) {
		this(new String[] {single},multiple);
	}

	public MemberTest(String single,String multiple) {
		this(new String[] {single},new String[] {multiple});
	}
	
	public boolean passes(Queryable info) {
		try {
			String itemValue = info.evalQuery(propertyString).getStringValue();
			Queryable[] listValue = info.evalQuery(list).getArrayValue();
			SymbolManipulator sm = new SymbolManipulator();
			boolean found = false;
			for (int i=0;i<listValue.length&&!found;++i) {
				if (sm.algebraicEqual(itemValue,listValue[i].getStringValue()))
					found = true;
			}
			sm = null;
			return found;
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
	
	public String toString() {
		return "[MemberTest: \""+ofString(propertyString)+"\" is in "+ofString(list)+"]";
	}
}
