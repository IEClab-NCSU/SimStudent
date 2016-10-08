/*
 * Created on Jun 11, 2003
 *
 */
package edu.cmu.pact.jess;

import java.io.Serializable;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;


/**
 * This class is used to construct the hint and the buggy messages from the templates.
 * 
 * The variables are jess variables (either preceded by ? or $).
 * 
 * The Message should be enclosed in (" ") and the if there are variables then the message 
 * should look like this: (" " ?var1 " ") 
 * 
 * Multi level hint or buggy messages can be specified by (" ") (" ") (" ")
 * 
 * More to come: All built in Jess functions can be used in constructing the messgaes
 * so that the messgaes can vary depending on the values of the various parameters
 * and conditional tests.   
 * 
 * @author sanket
 *
 */
public class ConstructMessage implements Userfunction, Serializable{

	/**
	 * @see jess.Userfunction#getName()
	 */
	public String getName() {
		return "construct-message";
	}

	/** Model tracer instance with student values. */
	private transient JessModelTracing jmt;

	/**
	 * No-argument constructor for use from (load-function).
	 */
	public ConstructMessage() {
		this(null);
	}

	/**
	 * Constructor connects to current model tracer.
	 * @param jmt current model tracer
	 */
	public ConstructMessage(JessModelTracing jmt) {
		super();
		this.jmt = jmt;
	}

	/**
	 * Entry point from Jess.
	 * Calls {@link #call(Boolean, ValueVector, Context) call(null, vv, ctx)}
	 * @param vv arguments from Jess
	 * @param ctx Jess bindings for argument evaluation
	 * @return result from call(null, vv, ctx) 
	 * @throws JessException
	 * @see jess.Userfunction#call(jess.ValueVector, jess.Context)
	 */
	public Value call(ValueVector vv, Context ctx) throws JessException {
		return call(JessModelTracing.MessageGroup.Undefined, vv, ctx); 
	}

	/**
	 * Workhorse.
	 * @param whichMsgs whether to set hint, buggy, etc.
	 * @param vv arguments from Jess
	 * @param ctx Jess bindings for argument evaluation
	 * @return list of strings
	 * @throws JessException
	 */
	protected Value call(JessModelTracing.MessageGroup whichMsgs, ValueVector vv, Context ctx)
			throws JessException {
		Value v;
		Value returnValue;
		String returnString = "";
		String tempString;
		char  c, e;
		ValueVector returnVV = new ValueVector();
		

		
		
		for(int i = 1; i < vv.size(); i++){  // vv.get(0) is function name "construct-message")
			v = vv.get(i);
			if (trace.getDebugCode("mt")) trace.outNT("mt", "ConstructMessage arg["+i+"] unresolved "+v+";");
			try{
				tempString = v.resolveValue(ctx).stringValue(ctx);
				if (trace.getDebugCode("mt")) trace.outNT("mt", "ConstructMessage arg["+i+"] resolved "+tempString+";");
			}catch (JessException ex) {
				trace.out("Exception handled: " + ex.getMessage());
				trace.out(ex.getProgramText());
				trace.out(ex.getLineNumber());
				//	tempString = v.stringValue(null);    05/21/2007 chc : to allow parenthesis in the string.
				tempString = v.toString();
				if(tempString.endsWith("]")){
					tempString = tempString.substring(0,tempString.length() - 1);
					returnString += tempString;
					returnValue = new Value(returnString,RU.STRING);
					returnVV.add(returnValue);
					returnString = "";
					continue;
				}
			}
	
			// remove any newline character from the string
			tempString = tempString.replaceAll("\n" , " ");
			tempString = tempString.replaceAll("\t" , " ");
			tempString = tempString.replaceAll("\\s+" , " "); 

			if(tempString.length() > 0){
				c = tempString.charAt(0);
				e = tempString.charAt(tempString.length() - 1);
			}else{
				continue;
			}
			if(c == '['){
				if(!returnString.trim().equals("")){
					returnValue = new Value(returnString,RU.STRING);
					returnVV.add(returnValue);
					returnString = "";
				}
				tempString = tempString.substring(1);
				if(tempString.length() > 0){
					c = tempString.charAt(0);  
				}else{
					continue;
				}
			}
//			if(c == '"'){
//			tempString = tempString.substring(1);						
//			}
//			if(e == '"'){
//			tempString = tempString.substring(1);
//			e = tempString.charAt(tempString.length());
//			}

			if(e == ']'){
				tempString = tempString.substring(0,tempString.length() - 1);					
				returnString += tempString;
				returnValue = new Value(returnString,RU.STRING);
				returnVV.add(returnValue);
				returnString = "";
				continue;
			}	
//			returnString += tempString;									
			if(c == '?' || c == '$'){
				try{
					v = v.resolveValue(ctx);
					returnString += v.stringValue(ctx) + " ";
				}catch(JessException je){
					returnString += v.stringValue(ctx) + " ";
				}
			}else {
				returnString += tempString + " ";
			}
//			System.out.println(v.stringValue(context));
		}

		
		//System.out.println("Return Vector: " + returnVV.toStringWithParens());
		
		returnValue = new Value(returnString,RU.STRING);
		returnVV.add(returnValue);
		
		
		if (jmt == null) {
			//if (ctx.getEngine() instanceof JessOracleRete){
			//	jmt = ((JessOracleRete) ctx.getEngine()).getJmt(); 
			//}
			//else
				if (ctx.getEngine() instanceof MTRete){
				jmt = ((MTRete) ctx.getEngine()).getJmt(); 
			}			
		}
		
		if (trace.getDebugCode("mt")) trace.out("mt", "ConstructMessage() jmt "+jmt+", returnVV.size "+
				returnVV.size());
		
		if (jmt != null && jmt.isModelTracing())
			jmt.setFiringNodeMessages(whichMsgs, returnVV, ctx);

	
		return new Value(returnVV, RU.LIST);
	}
}
