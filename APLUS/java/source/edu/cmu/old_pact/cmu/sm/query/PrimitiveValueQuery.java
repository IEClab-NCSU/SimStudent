//PrimitiveValueQuery is an abstract class that implements default methods
//for NumberQuery, StringQuery and BooleanQuery

package edu.cmu.old_pact.cmu.sm.query;

import java.util.Vector;

public abstract class PrimitiveValueQuery implements Queryable {
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		//by default, no properties
		throw new NoSuchFieldException("No field "+prop+" in PrimitiveValueQuery");
	}

	public void setProperty(String prop, String value) throws NoSuchFieldException {
		//by default, no properties
		throw new NoSuchFieldException("No field "+prop+" in PrimitiveValueQuery");
	}

	public Queryable evalQuery(String query) throws NoSuchFieldException {
		return StandardMethods.evalQuery(query,this);
	}

	public Queryable evalQuery(String[] query) throws NoSuchFieldException{
		return StandardMethods.evalQuery(query,this);
	}

	public Number getNumberValue() {
		throw new ClassCastException("Can't cast "+this+" to a number");
	}
	
	public String getStringValue() {
		throw new ClassCastException("Can't cast "+this+" to a string");
	}
	
	public boolean getBooleanValue() {
		throw new ClassCastException("Can't cast "+this+" to a boolean");
	}

	public Queryable[] getArrayValue() {
		Queryable[] result = new Queryable[1];
		result[0] = this;
		return result;
	}

	public String toString(){
		try{
			return getStringValue();
		}
		catch(ClassCastException cce){
			return super.toString();
		}
	}
	
	//applyOp, by default, returns an error (probably shouldn't be NoSuchFieldException, though)
	public Queryable applyOp(String op,Vector args) throws NoSuchFieldException {
		throw new NoSuchFieldException("Can't apply operator "+op+" to "+args);
	}
}
