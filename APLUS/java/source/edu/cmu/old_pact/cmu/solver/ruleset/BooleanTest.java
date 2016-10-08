package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

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
                      System.out.println("BooleanTest: passes(" + ((Equation)info).debugForm() + ")");
                      }
                      else if(info instanceof Expression){
                      System.out.println("BooleanTest: passes(" + ((Expression)info).debugForm() + ")");
                      }
                      else{
                      System.out.println("BooleanTest: passes(" + info + ")");
                      }*/
			boolean thisValue = info.evalQuery(propertyString).getBooleanValue();
			if (thisValue == comparison)
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
	
	public String toString() {
		return "[BooleanTest: \""+ofString(propertyString)+"\" is "+comparison+"]";
	}
	
}
