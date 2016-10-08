package edu.cmu.old_pact.cmu.sm.query;
import java.util.Vector;

//The Queryable interface provides a simple scripting interface
//Query strings are of the form "X of Y of Z", where X, Y and Z
//are properties. All properties return a Queryable. After the final
//call, the caller should use one of the getXXXValue methods to return
//a primitive type
public interface Queryable {
	public Queryable getProperty(String prop) throws NoSuchFieldException;
	public void setProperty(String prop,String value) throws NoSuchFieldException;
	public Queryable evalQuery(String query) throws NoSuchFieldException;
	public Queryable evalQuery(String[] query) throws NoSuchFieldException;
	public Queryable applyOp(String op,Vector args)  throws NoSuchFieldException;
	public Number getNumberValue();
	public boolean getBooleanValue();
	public String getStringValue();
	public Queryable[] getArrayValue();
}
