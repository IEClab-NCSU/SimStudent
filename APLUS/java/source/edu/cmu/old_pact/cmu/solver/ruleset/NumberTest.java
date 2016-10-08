package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

public class NumberTest extends Test {
	Number comparison;
	
	public NumberTest(String[] property,Number comp){
		propertyString = property;
		comparison = comp;
	}

	public NumberTest(String property,Number comp) {
		this(new String[] {property},comp);
	}
	
	public NumberTest(String property,int comp) {
		this(new String[] {property},new Integer(comp));
	}

	public NumberTest(String[] property,int comp){
		this(property,new Integer(comp));
	}

	public boolean passes(Queryable info) {
		try {
			Number thisValue = info.evalQuery(propertyString).getNumberValue();
			if (Math.abs(thisValue.doubleValue() - comparison.doubleValue()) < .00001)
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
		return "[NUMBER: " + ofString(propertyString) + ", " + comparison + "]";
	}
}
