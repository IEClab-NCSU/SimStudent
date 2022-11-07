package edu.cmu.old_pact.cmu.solver.ruleset;

import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.ArrayQuery;
import edu.cmu.old_pact.cmu.sm.query.Queryable;

//AnyGreaterThanTest tests that any of the numbers are greater than the given value

public class AnyGreaterThanTest extends Test {
	double comparison;

	public AnyGreaterThanTest(String[] items,double compare){
		propertyString = items;
		comparison = compare;
	}

	public AnyGreaterThanTest(String items,double compare) {
		this(new String[] {items},compare);
	}

	public boolean passes(Queryable info) {
		try {
			/*trace.out("AGTT.p(" + info + ")");
			  trace.out("AGTT.p: set = " + set + "; comparison = " + comparison);*/
			boolean OK = false;
			Vector values = new Vector();
			Queryable q1 = info.evalQuery(propertyString);
			/*if(q1 instanceof ArrayQuery){
			  trace.out("AGTT.p: q1 = " + ((ArrayQuery)q1).debugString());
			  }
			  else if(q1 instanceof NumberQuery){
			  trace.out("AGTT.p: q1 = " + q1.getNumberValue());
			  }
			  else{
			  trace.out("AGTT.p: q1 = " + q1);
			  }*/
			Queryable[] q = q1.getArrayValue();
			for(int i=0;i<q.length;i++){
				values.addElement(q[i]);
			}
			for(boolean moreArrays = true;moreArrays;){
				moreArrays = false;
				for(int i=0;i<values.size();i++){
					if(values.elementAt(i) instanceof ArrayQuery){
						moreArrays = true;
						Queryable q2[] = ((ArrayQuery)values.elementAt(i)).getArrayValue();
						values.removeElementAt(i);
						for(int j=0;j<q2.length;j++){
							values.insertElementAt(q2[j],i);
						}
					}
				}
			}
			//trace.out("AGTT.p: values(" + values.size() + "): " + values);
			for (int i=0;i<values.size()&&!OK;++i) {
				Number thisEx = ((Queryable)values.elementAt(i)).getNumberValue();
				if (thisEx.doubleValue() > comparison)
					OK = true;
			}
			return OK;
		}
		catch (NoSuchFieldException err) {
			if(Rule.debug()){
				System.out.println("Error resolving test:"+err+" info = "+info.getStringValue()+" class = "+getClass());
			}
			return false;
		}
	}
	
	public String toString() {
		return "[AnyGreaterThanTest: "+ofString(propertyString)+"any are greater than "+comparison+"]";
	}
}
