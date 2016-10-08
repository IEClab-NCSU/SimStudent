package edu.cmu.old_pact.cmu.sm.query;
//a StringQuery is just a wrapper around a string

public class StringQuery extends PrimitiveValueQuery {
	String str;
	
	public StringQuery(String string) {
		str = string;
	}
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		if (prop.equalsIgnoreCase("strlength"))
			return new NumberQuery (new Integer(str.length()));
		else{
			/*if(prop.equalsIgnoreCase("length")){
			  System.out.println("StringQuery ERROR: property 'length' is deprecated");
			  System.out.println("           target: " + getStringValue());
			  }*/
			throw new NoSuchFieldException("No field "+prop+" in StringQuery");
		}
	}
	
	public String getStringValue() {
		return str;
	}
	
	public boolean getBooleanValue() {
		throw new ClassCastException();
	}
	
}
