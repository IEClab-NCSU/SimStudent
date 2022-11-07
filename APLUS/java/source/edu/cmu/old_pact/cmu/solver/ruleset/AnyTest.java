package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//AnyTest tests that the test applies to any item returned in the Vector
//If isTrue is false, we negate each of the tests (that is, the test passes is "any not",
//meaning that it passes if the test fails for any item
public class AnyTest extends Test {
	protected String[] set;
	boolean isTrue;
	protected Test test;

	public AnyTest(String[] items,Test myTest,boolean trueP){
		set = items;
		test = myTest;
		isTrue = trueP;
		propertyString = null;
	}

	public AnyTest(String[] items,Test myTest){
		this(items,myTest,true);
	}

	public AnyTest(String items,Test myTest,boolean trueP){
		this(new String[] {items},myTest,trueP);
	}

	public AnyTest(String items,Test myTest){
		this(new String[] {items},myTest,true);
	}

	public AnyTest(String[] items,String[] prop,boolean trueP){
		set = items;
		propertyString = prop;
		isTrue = trueP;
		test = null;
	}

	public AnyTest(String[] items,String[] prop){
		this(items,prop,true);
	}

	public AnyTest(String items,String prop) {
		this(new String[] {items},new String[] {prop});
	}
	
	public AnyTest(String items,String prop,boolean trueP) {
		this(new String[] {items},new String[] {prop},trueP);
	}

	public AnyTest(String[] items,String prop,boolean trueP){
		this(items,new String[] {prop},trueP);
	}

	public AnyTest(String[] items,String prop){
		this(items,new String[] {prop});
	}

	public AnyTest(String items,String[] prop,boolean trueP){
		this(new String[] {items},prop,trueP);
	}

	public AnyTest(String items,String[] prop){
		this(new String[] {items},prop);
	}

	public boolean passes(Queryable info) {
		try {
			if(propertyString != null){
				boolean OK = false;
				Queryable[] values = info.evalQuery(set).getArrayValue();
				if(values != null){
					for (int i=0;i<values.length && !OK;++i) {
						boolean thisValue = values[i].evalQuery(propertyString).getBooleanValue();
						if (thisValue == isTrue){
							OK = true;
						}
					}
				}
				return OK;
			}
			else{
				boolean OK = false;
				Queryable[] values = info.evalQuery(set).getArrayValue();
				if(values != null){
					for(int i=0;i<values.length && !OK;i++){
						if(test.passes(values[i])){
							OK = true;
						}
					}
				}
				return OK;
			}
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}
	
	public String toString() {
		if(propertyString != null){
			return "[AnyTest: "+ofString(propertyString)+" is "+isTrue+" of "+ofString(set)+"]";
		}
		else{
			return "[AnyTest: "+test+" is "+isTrue+" of "+ofString(set)+"]";
		}
	}
	
}
