package edu.cmu.old_pact.dormin;


//The ParseResult object is used to contain information while parsing an incoming message

public class ParseResult {
	private Object value;
	private char type;
	private int position;
	
	public ParseResult (Object val,int pos,char kind) {
		value = val;
		position = pos;
		type = kind;
	}
	
	public Object getParsedValue() {
		return value;
	}
	
	public char getParsedType() {
		return type;
	}
	
	public int getNewPosition() {
		return position;
	}
}
