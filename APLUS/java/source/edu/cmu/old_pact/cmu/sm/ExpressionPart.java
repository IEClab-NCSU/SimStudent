package edu.cmu.old_pact.cmu.sm;
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.query.Queryable;
import edu.cmu.old_pact.cmu.sm.query.StandardMethods;

//an ExpressionPart represents some subpart of an expression
//This is usually used to set display formats of parts of expressions which are not, in themselves,
//expressions. For example, the negative sign in front of an integer is an ExpressionPart.
//To allow this sign to take different display attributes, a query might create an
//ExpressionPart, which points back to the NumberExpression and, when setProperty is called
//on it, it tells the NumberExpression to format the negative sign in a particular way.

//ExpressionPart is Queryable, because it is returned as the result of a query
//ExpressionParts may not have any properties, though

public class ExpressionPart implements Queryable {
	protected String myName;
	protected Expression myExpression;
	private String myValue=null; //string value of ExpressionPart [if supported by property]
	
	ExpressionPart(String partName,Expression inExpression) {
		myName = partName;
		myExpression = inExpression;
	}

	ExpressionPart(String partName,Expression inExpression, String value) {
		this(partName,inExpression);
		myValue = value;
	}

	//Queryable methods...
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		throw new NoSuchFieldException("ExpressionParts do not have properties: "+prop);
	}
	
	//setProperty assumes that the property is a display attribute
	public void setProperty(String prop, String value) throws NoSuchFieldException {
//		System.out.println("setting property "+prop+" to "+value+" for "+this.getStringValue());
		myExpression.setPartAttribute(myName,prop,value);
//		throw new NoSuchFieldException("ExpressionParts do not have properties: "+prop);
	}

	public Queryable evalQuery(String[] query) throws NoSuchFieldException{
		return StandardMethods.evalQuery(query,this);
	}

	public Queryable evalQuery(String query) throws NoSuchFieldException {
			return StandardMethods.evalQuery(query,this);
	}
	public Number getNumberValue() {
		throw new ClassCastException();
	}
	
	public String getStringValue() {
		if (myValue != null)
			return myValue;
		else
			return "[ExpressionPart "+myName+" of "+myExpression+"]";
	}
	
	public boolean getBooleanValue() {
		throw new ClassCastException();
	}

	public Queryable[] getArrayValue() {
		throw new ClassCastException();
	}
	
	public Queryable applyOp(String op,Vector args) throws NoSuchFieldException {
		return myExpression.applyOp(op,args);
	}

}
