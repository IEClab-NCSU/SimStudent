package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

public class BooleanTest extends Test {
	protected boolean comparison;
	
	public BooleanTest(String[] property,boolean comp){
		propertyString = property;
		comparison = comp;
	}

	public BooleanTest(String property,boolean comp) {
		this(new String[] {property},comp);
	}

	public BooleanTest(String[] property){
		this(property,true);
	}
	
	public BooleanTest(String property) {
		this(new String[] {property},true);
	}

	public boolean passes(Queryable info) {
		try {
                    /*if(info instanceof Equation){
                      trace.out("BooleanTest: passes(" + ((Equation)info).debugForm() + ")");
                      }
                      else if(info instanceof Expression){
                      trace.out("BooleanTest: passes(" + ((Expression)info).debugForm() + ")");
                      }
                      else{
                      trace.out("BooleanTest: passes(" + info + ")");
                      }*/
			boolean thisValue = info.evalQuery(propertyString).getBooleanValue();
			if (thisValue == comparison)
				return true;
			else
				return false;
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}
	
	public String toString() {
		return "[BooleanTest: \""+ofString(propertyString)+"\" is "+comparison+"]";
	}
	
}
