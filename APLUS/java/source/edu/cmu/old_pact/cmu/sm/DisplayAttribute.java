package edu.cmu.old_pact.cmu.sm;

//a DisplayAttribute is just an attribute/value pair. This is used for MathML formatting
public class DisplayAttribute {
	private String attribute;
	private String value;

	DisplayAttribute(String att, String val) {
		attribute = att;
		value = val;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public String getValue() {
		return value;
	}

	public String toString() {
		return attribute+"="+"'"+value+"'";
	}

}

