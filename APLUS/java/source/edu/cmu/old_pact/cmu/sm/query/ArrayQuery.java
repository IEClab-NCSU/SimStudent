package edu.cmu.old_pact.cmu.sm.query;

import edu.cmu.pact.Utilities.trace;

import java.util.Vector;

//aa ArrayQuery is just a wrapper around an array of Queryables

public class ArrayQuery extends PrimitiveValueQuery {
	private Queryable qArray[];
	boolean isEmpty = false;
	
	//we provide a constructor with no arguments, which indicates that the array is empty
	public ArrayQuery() {
		isEmpty = true;
	}
	
	//We also provide a special constructor when there's only 1 item in the array
	public ArrayQuery(Queryable theItem) {
		qArray = new Queryable[1];
		qArray[0] = theItem;
	}
	
	public ArrayQuery(Queryable[] items) {
		qArray = new Queryable[items.length];
		for (int i=0;i<items.length;++i)
			qArray[i] = items[i];
	}
			
	//this version creates the ArrayQuery on a subset of the array items
	public ArrayQuery(Queryable[] items,int num) {
		qArray = new Queryable[num];
		for (int i=0;i<num;++i)
			qArray[i] = items[i];
	}

	public ArrayQuery(Vector items) {
		qArray = new Queryable[items.size()];
		for (int i=0;i<items.size();++i)
			qArray[i] = (Queryable)(items.elementAt(i));
	}

	public Queryable[] getArrayValue() {
		return qArray;
	}
	
	public Queryable getProperty(String prop) throws NoSuchFieldException {
		Queryable result=null;
		//trace.out("AQ.gP(" + prop + "): " + getStringValue());
		/*first we try to apply the property to each item in the array*/
		if(qArray == null || qArray.length == 0){
			if(prop.equalsIgnoreCase("length")){
				return new NumberQuery(new Integer(0));
			}
			else{
				throw new NoSuchFieldException("Can't get property of empty array");
			}
		}
		try{
			//trace.out("AQ.gP: passing down to elements");
			Vector results = new Vector();
			for (int i=0;i<qArray.length;++i) {
				Queryable thisValue = qArray[i].getProperty(prop);
				results.addElement(thisValue);
			}
			result = new ArrayQuery(results);
		}
		catch(NoSuchFieldException nsfe){
			//trace.out("AQ.gP: caught exception");
			/*if that fails, try to apply the property to the array itself*/
			if (prop.equalsIgnoreCase("Length")) {
				if (isEmpty)
					result = new NumberQuery(new Integer(0));
				else
					result = new NumberQuery(new Integer(qArray.length));
			}
			else if (prop.length() > 4 && prop.substring(0,4).equalsIgnoreCase("item")) {
				//Olga
				if(prop.equalsIgnoreCase("item any")){
					Vector results = new Vector();
					for (int i=0;i<qArray.length;++i) {
						Queryable thisValue = qArray[i].getProperty("item "+String.valueOf(i+1));
						results.addElement(thisValue);
					}
					result = new ArrayQuery(results);
				}
				// end Olga
				else{
					int itemNum = Integer.parseInt(prop.substring(5));
					if (isEmpty)
						throw new NoSuchFieldException("Can't get item "+itemNum+" from empty array");
					if (itemNum <= qArray.length)
						result = qArray[itemNum-1];
					else
						throw new NoSuchFieldException("No array index "+itemNum+" in array of size "+qArray.length);
				}
			}
			/*conjunct makes an english "and" phrase from the elements
              of the array -- they must all support getStringValue()*/
			else if(prop.equalsIgnoreCase("conjunct")){
				return makeList("and",false);
			}
			else if(prop.equalsIgnoreCase("exprconjunct")){
				return makeList("and",true);
			}
			else if(prop.equalsIgnoreCase("disjunct")){
				return makeList("or",false);
			}
			else if(prop.equalsIgnoreCase("exprdisjunct")){
				return makeList("or",true);
			}
			else {
				/*trace.out("AQ.gP: passing down to elements");
				  Vector results = new Vector();
				  for (int i=0;i<qArray.length;++i) {
				  Queryable thisValue = qArray[i].getProperty(prop);
				  results.addElement(thisValue);
				  }
				  result = new ArrayQuery(results);*/
				//trace.out("AQ.gP: re-throwing exception");
				throw nsfe;
			}
		}
		//trace.out("AQ.gP: returning: " + result.getStringValue());
		return result;
	}

	/*Conjunction Junction, what's your function?
	  Hookin' up words and phrases and clauses.
	  Conjunction Junction, how's that function?
	  I got three favorite cars that get most of my job done.
	  Conjunction Junction, what's their function?
	  I got And, But, and Or.
	  They'll get you pretty far.*/
	private StringQuery makeList(String conjunction,boolean expr){
		StringBuffer ret = new StringBuffer(32*qArray.length);
		if(qArray.length <= 2){
			if(expr){
				ret.append("<expression>");
			}
			ret.append(qArray[0].getStringValue());
			if(expr){
				ret.append("</expression>");
			}
			if(qArray.length == 2){
				ret.append(" ");
				ret.append(conjunction);
				ret.append(" ");
				if(expr){
					ret.append("<expression>");
				}
				ret.append(qArray[1].getStringValue());
				if(expr){
					ret.append("</expression>");
				}
			}
		}
		else{
			for(int i=0;i<qArray.length;i++){
				if(expr){
					ret.append("<expression>");
				}
				ret.append(qArray[i].getStringValue());
				if(expr){
					ret.append("</expression>");
				}
				if(i <= qArray.length-2){
					ret.append(", ");
				}
				if(i == qArray.length-2){
					ret.append(conjunction);
					ret.append(" ");
				}
			}
		}
		return new StringQuery(ret.toString());
	}

	public String debugString(){
		String ret = "[ArrayQuery: ";
		for(int i=0;i<qArray.length;i++){
			if(i != 0){
				ret += ", ";
			}
			if(qArray[i] instanceof ArrayQuery){
				ret += ((ArrayQuery)qArray[i]).debugString();
			}
			else{
				ret += qArray[i].getStringValue();
			}
		}
		ret += "]";

		return ret;
	}

	public boolean getBooleanValue(){
		trace.out("AQ.gBV: calling super");
		return super.getBooleanValue();
	}

	public String getStringValue() {
		if(qArray == null){
			return "[null ArrayQuery]";
		}
		else{
			//return qArray.toString();
			return debugString();
		}
	}
}
