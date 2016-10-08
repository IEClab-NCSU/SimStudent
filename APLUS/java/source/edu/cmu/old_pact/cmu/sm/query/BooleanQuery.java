package edu.cmu.old_pact.cmu.sm.query;
//a BooleanQuery is just a wrapper around a string

public class BooleanQuery extends PrimitiveValueQuery {
	boolean val;
	
	public BooleanQuery(boolean isTrue) {
		val = isTrue;
	}
			
	public boolean getBooleanValue() {
		return val;
	}
	
	public String getStringValue() {
		if (val)
			return "true";
		else
			return "false";
	}
	
}
