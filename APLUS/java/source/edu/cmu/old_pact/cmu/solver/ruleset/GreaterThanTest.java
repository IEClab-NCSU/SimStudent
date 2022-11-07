package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.pact.Utilities.trace;

//GreaterThanTest tests that all numbers are greater than the given value

public class GreaterThanTest extends Test {
	double comparison;
	String[] compareString = null;
	boolean isTrue;
	
	public GreaterThanTest(String[] items,double compare,boolean shouldBe){
		propertyString = items;
		comparison = compare;
		isTrue = shouldBe;
	}

	public GreaterThanTest(String[] items,double compare){
		this(items,compare,true);
	}

	public GreaterThanTest(String items,double compare) {
		this(new String[] {items},compare,true);
	}

	public GreaterThanTest(String items,double compare,boolean shouldBe) {
		this(new String[] {items},compare,shouldBe);
	}

	public GreaterThanTest(String[] items,String compareTo[], boolean shouldBe){
		propertyString = items;
		compareString = compareTo;
		isTrue = shouldBe;
	}

	public GreaterThanTest(String[] items,String compareTo,boolean shouldBe){
		this(items,new String[] {compareTo},shouldBe);
	}

	public GreaterThanTest(String items,String compareTo,boolean shouldBe){
		this(new String[] {items},new String[] {compareTo},shouldBe);
	}

	public GreaterThanTest(String[] items,String[] compareTo){
		this(items,compareTo,true);
	}

	public GreaterThanTest(String items,String[] compareTo){
		this(new String[] {items},compareTo,true);
	}

	public GreaterThanTest(String[] items,String compareTo){
		this(items,new String[] {compareTo},true);
	}

	public GreaterThanTest(String items,String compareTo) {
		this(new String[] {items},new String[] {compareTo},true);
	}

	public boolean passes(Queryable info) {
		if (compareString != null) { //comparing two queries
			boolean OK = true;
			try {
				Queryable comparison = info.evalQuery(compareString);
				Queryable[] values = info.evalQuery(propertyString).getArrayValue();
				double compVal = comparison.getNumberValue().doubleValue();
				for (int i=0;i<values.length;++i) {
					double thisExVal = values[i].getNumberValue().doubleValue();
					if (thisExVal <= compVal)
						OK = false;
				}
				return (OK == isTrue);
			}
			catch (NoSuchFieldException err) {
				if(Rule.debug()){
					trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
				}
				return false;
			}
		}
		else { //comparing a query to a constant
			try {
				boolean OK = true;
				Queryable[] values = info.evalQuery(propertyString).getArrayValue();
				for (int i=0;i<values.length;++i) {
					double thisExVal = values[i].getNumberValue().doubleValue();
					if (thisExVal <= comparison)
						OK = false;
				}
				return (OK == isTrue);
			}
			catch (NoSuchFieldException err) {
				if(Rule.debug()){
					trace.out("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
				}
				return false;
			}
		}
	}
	
	public String toString() {
		if(compareString != null){
			return "[GreaterThanTest: "+ofString(propertyString)+" is greater than "+ofString(compareString)+" is "+isTrue+"]";
		}
		else{
			return "[GreaterThanTest: "+ofString(propertyString)+" is greater than "+comparison+" is "+isTrue+"]";
		}
	}
}
