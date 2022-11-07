package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//EveryTest tests that the test applies to every item returned in the Vector
//If isTrue is false, we negate each of the tests (that is, the test shouldbe false
//for each item in the set

//For example, EveryTest("all numbers","isNotDecimal"); tests that no numbers are decimal

public class EveryTest extends Test {
	protected String[] set;
	boolean isTrue;

	public EveryTest(String[] items,String[] prop,boolean trueP){
		set = items;
		propertyString = prop;
		isTrue = trueP;
	}

	public EveryTest(String[] items,String[] prop){
		this(items,prop,true);
	}

	public EveryTest(String[] items,String prop,boolean trueP){
		this(items,new String[] {prop},trueP);
	}

	public EveryTest(String[] items,String prop){
		this(items,new String[] {prop},true);
	}

	public EveryTest(String items,String[] prop,boolean trueP){
		this(new String[] {items},prop,trueP);
	}

	public EveryTest(String items,String[] prop){
		this(new String[] {items},prop,true);
	}

	public EveryTest(String items,String prop,boolean trueP) {
		this(new String[] {items},new String[] {prop},trueP);
	}	
	
	public EveryTest(String items,String prop) {
		this(new String[] {items},new String[] {prop},true);
	}
	
	//If property is omitted, items should be a list of BooleanQueries
	public EveryTest(String items,boolean trueP) {
		this(new String[] {items},(String[])null,trueP);
	}	

	public EveryTest(String items) {
		this(new String[] {items},(String[])null,true);
	}	

	public boolean passes(Queryable info) {
		try {
			boolean OK = true;
			Queryable[] values = info.evalQuery(set).getArrayValue();
			for (int i=0;i<values.length && OK;++i) {
				boolean thisValue;
				if (propertyString == null)
					thisValue = values[i].getBooleanValue();
				else
					thisValue = values[i].evalQuery(propertyString).getBooleanValue();
				if (thisValue != isTrue)
					OK = false;
			}
			return OK;
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}
	
	public String toString() {
		return "[EveryTest: "+ofString(propertyString)+" is "+isTrue+" of "+ofString(set)+"]";
	}
	
}
