package edu.cmu.old_pact.cmu.solver.ruleset;

import edu.cmu.old_pact.cmu.sm.query.Queryable;

//LessThanTest tests that all numbers are less than the given value

public class LessThanTest extends Test {
	double comparison;
	boolean isTrue;
	
	public LessThanTest(String[] items,double compare,boolean shouldBe){
		propertyString = items;
		comparison = compare;
		isTrue = shouldBe;
	}

	public LessThanTest(String[] items,double compare){
		this(items,compare,true);
	}

	public LessThanTest(String items,double compare) {
		this(new String[] {items},compare);
	}

	public LessThanTest(String items,double compare,boolean shouldBe) {
		this(new String[] {items},compare,shouldBe);
	}

	public boolean passes(Queryable info) {
		try {
			boolean OK = true;
			Queryable[] values = info.evalQuery(propertyString).getArrayValue();
			for (int i=0;i<values.length;++i) {
				double thisExVal = values[i].getNumberValue().doubleValue();
				if (thisExVal >= comparison)
					OK = false;
				/*NumericExpression thisEx = (NumericExpression)(values[i]);
				  if (thisEx.doubleValue() >= comparison)
				  OK = false;*/
			}
			return (OK == isTrue);
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				System.out.println("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}
	
	public String toString() {
		return "[LessThanTest: "+ofString(propertyString)+"is less than "+comparison+" is "+isTrue+"]";
	}
}
