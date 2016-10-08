package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class StringTest extends Test {
	String comparison;

	public StringTest(String[] property,String comp){
		propertyString = property;
		comparison = comp;
	}
	
	public StringTest(String property,String comp) {
		this(new String[] {property},comp);
	}
	
	public boolean passes(Queryable info) {
		try {
			String thisValue = info.evalQuery(propertyString).getStringValue();
			if (thisValue.equalsIgnoreCase(comparison))
				return true;
			else
				return false;
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				System.out.println("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}

	public String toString(){
		return "[STRING: " + ofString(propertyString) + ", " + comparison + "]";
	}
}
