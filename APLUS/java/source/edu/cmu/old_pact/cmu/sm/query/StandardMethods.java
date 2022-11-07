//StandardMethods is a class that can be used with Queryable objects
//since Queryable is an interface, not a class, you can't have classes
//inherit methods. Since evalQuery, in particular, is the same for all
//object that implement Queryable, an easy way to implement this is to
//call standardMethods.evalQuery
//That is, any queryable object can define evalQuery as:
//	public Queryable evalQuery(String query) throws NoSuchFieldException {
//		return StandardMethods.evalQuery(query,this);
//	}


package edu.cmu.old_pact.cmu.sm.query;
import edu.cmu.pact.Utilities.trace;

import java.util.Vector;

public abstract class StandardMethods {
	public static boolean traceBool = false;

	public static Queryable evalQuery(String[] query,Queryable obj) throws NoSuchFieldException{
		return doEvalQuery(query,obj);
	}

	public static Queryable evalQuery(String query,Queryable obj) throws NoSuchFieldException {
		if (query.charAt(0) == '[')
			return performEmbeddedOp(query,obj);
		else
			return doEvalQuery(query,obj);
	}
	
	//doEvalQuery does all the work. It looks for " of " to parse the containment hierarchy and calls "get Property"
	//on the objects
	public static Queryable doEvalQuery(String query,Queryable obj) throws NoSuchFieldException {
		int bracketPlace = query.indexOf(" of [");
		if(bracketPlace != -1){
			String thisProperty = query.substring(bracketPlace+4);
			Queryable thisResult = evalQuery(thisProperty,obj);
			return thisResult.evalQuery(query.substring(0,bracketPlace));
		}
		else{
			int ofPlace = query.lastIndexOf(" of ");
			if (ofPlace > -1) {
				String thisProperty = query.substring(ofPlace+4);
				Queryable thisResult = evalQuery(thisProperty,obj);
				if (traceBool) {
					String resultClass = thisResult.getClass().getName();
					trace.out("<<property \""+thisProperty+"\" of "+obj.getStringValue()+" is "+thisResult.getStringValue()+"["+resultClass+"]>>");
				}
				return thisResult.evalQuery(query.substring(0,ofPlace));
			}
			else { //no "of", this must be the last property
				Queryable finalResult;
				if(query.charAt(0) == '['){
					finalResult = performEmbeddedOp(query,obj);
				}
				else{
					finalResult = obj.getProperty(query);
				}
				if (traceBool) {
					String finalClass = finalResult.getClass().getName();
					trace.out("<<property \""+query+"\" of "+obj.getStringValue()+" is "+finalResult.getStringValue()+"["+finalClass+"]>>");
				}
				return finalResult;
			}
		}
	}

	/*instead of doEvalQuery("foo of bar of baz",...), this works like
      doEvalQuery({"foo","bar","baz"},...)*/
	public static Queryable doEvalQuery(String[] query,Queryable obj) throws NoSuchFieldException{
		Queryable q = obj;
		for(int i=query.length-1;i>=0;i--){
			if(traceBool){
				trace.out("<<property \""+query[i]+"\" of "+q.getStringValue()+" is ");
			}
			q = evalQuery(query[i],q);
			if(traceBool){
				String resultClass = q.getClass().getName();
				trace.out(q.getStringValue()+"["+resultClass+"]>>");
			}
		}

		return q;
	}

	//performEmbeddedOp is used for scripts that perform actions other than property access
	//(like set, add, subtract, multiply and divide)
	//These actions are specified within square brackets [], and their arguments are separated by square brackets []
	//so "[add] [term 1 of expression] [term 2 of expression]" returns the sum of the two queries
	//the arguments to the operation can also themselves be scripts:
	//"[add] [term 1 of expression] [[subtract] ['1'] ['2']]"
	private static Queryable performEmbeddedOp(String query,Queryable obj) throws NoSuchFieldException {
		int endCommandPlace = query.indexOf("]");
		String command = query.substring(1,endCommandPlace);
		//evaluate arguments
		boolean done=false;
		String queryLeft = query.substring(endCommandPlace+1);
		Vector args = new Vector();
		while (!done) {
			int argStart = queryLeft.indexOf("[");
			if(argStart >= 0){
				int argEnd = findMatchingChar(queryLeft.substring(argStart),'[',']') + argStart;
				if (argStart >=0 && argEnd >=0) {
					if (queryLeft.charAt(argStart+1) == '"' ||
						queryLeft.charAt(argStart+1) == '\'')    { //if value is in quotes (single or double), dont evaluate it
						args.addElement(new StringQuery(queryLeft.substring(argStart+2,argEnd-1)));
						}
					else {
						String thisArg = queryLeft.substring(argStart+1,argEnd);
						Queryable evaledArg = evalQuery(thisArg,obj);
						args.addElement(evaledArg);
					}
					queryLeft = queryLeft.substring(argEnd+1);
				}
				else
					done = true;
			}
			else{
				done = true;
			}
		}
		return (Queryable)(obj.applyOp(command,args));
	}

	private static int findMatchingChar(String s,char open,char close){
		int currIndx = 0;
		int currLvl = 0;
		int openi,closei;
		do{
			openi = s.indexOf(open,currIndx);
			closei = s.indexOf(close,currIndx);
			if(openi > -1){
				if((closei > -1) &&
				   (closei < openi)){
					currLvl--;
					currIndx = closei+1;
				}
				else{
					currLvl++;
					currIndx = openi+1;
				}
			}
			else if(closei > -1){
				currLvl--;
				currIndx = closei+1;
			}
			else{
				return -1;
			}
		}while(currLvl > 0);

		return currIndx-1;
	}

//	public Queryable getProperty(String prop) throws NoSuchFieldException {
//		//by default, no properties
//		throw new NoSuchFieldException("No field "+prop+" in StandardMethods");
//	}
	
	//In the "standard" method, there is only a "set" operator, which calls setProperty
	public static Queryable applyOp(String op,Vector args) throws NoSuchFieldException {
		if (op.equalsIgnoreCase("set")) {
			String prop = ((Queryable)(args.elementAt(0))).getStringValue();
			Queryable obj = (Queryable)(args.elementAt(1));
			String value = ((Queryable)(args.elementAt(2))).getStringValue();
			obj.setProperty(prop,value);
			return obj;
		}
		else
			throw new NoSuchFieldException("Can't apply operator "+op+" to "+args);
	}
}
