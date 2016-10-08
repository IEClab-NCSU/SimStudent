package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.jess.SimStRete;
import edu.cmu.pact.miss.SimSt;

/**
 *
 */
public class ConstructTutorHintMessage implements Userfunction, Serializable {

	/**	 */
	private static final long serialVersionUID = 1L;

	/**	 */
	private static final String CONSTRUCT_TUTOR_HINT_MESSAGE = "construct-tutor-hint-message";
	/** Model tracer instance with student values */
	protected transient ModelTracer amt;
	
	/** Link to current variable context and, thence, to the Rete. */
	protected transient Context context;

	public ConstructTutorHintMessage() { 
		this(null);
	}
	
	public ConstructTutorHintMessage(ModelTracer amt) {
		this.amt = amt;
	}
	
	/**
	 * 
	 */
	public Value call(ValueVector vv, Context context) throws JessException {

		Value v;
		Value returnValue;
		String returnString = "";
		String tempString;
		char  c, e;
		ValueVector returnVV = new ValueVector();
		/*variables to hold if rule fired pro-actively or if previously fired pro-actively*/
		boolean isRuleProactivelyFired=false;
		boolean isPreviousHintProactivelyGiven=false;
			
	/*	if (amt != null){
				isRuleProactivelyFired=amt.getHintTrigger().equals(APlusModelTracing.PROACTIVE_HINT_SELECTION)? true : false;
				String tmp=amt.getTimelineQueue().peek();
				if (tmp!=null)
				isPreviousHintProactivelyGiven=tmp.equals(APlusModelTracing.PROACTIVE_HINT_SELECTION)? true : false;		
		}
		*/	
		
		for(int i = 1; i < vv.size(); i++){  // vv.get(0) is function name "construct-hint-message")
			v = vv.get(i);
			try{
				tempString = v.resolveValue(context).stringValue(context);
			}catch (JessException ex) {
				trace.out("Exception handled: " + ex.getMessage());
				trace.out(ex.getProgramText());
				trace.out(ex.getLineNumber());
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
			// remove any references to the SimStudents name with the current name
			tempString = tempString.replaceAll("SimStName", SimSt.SimStName);

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
			
			if(e == ']'){
				tempString = tempString.substring(0,tempString.length() - 1);					
				returnString += tempString;
				returnValue = new Value(returnString,RU.STRING);
				returnVV.add(returnValue);
				returnString = "";
				continue;
			}
			
			if(c == '?' || c == '$'){
				try{
					v = v.resolveValue(context);
					returnString += v.stringValue(context) + " ";
				}catch(JessException je){
					returnString += v.stringValue(context) + " ";
				}
			}else {
				returnString += tempString + " ";
			}
		}

		returnValue = new Value(returnString,RU.STRING);
		returnVV.add(returnValue);

		if(amt == null) {
			if(context.getEngine() instanceof SimStRete) {
				amt = ((SimStRete)context.getEngine()).getAmt();
			}
		}
	
		//if (jmt == null) {
		//	if (ctx.getEngine() instanceof MTRete)
		//		jmt = ((MTRete) ctx.getEngine()).getJessModelTracing(); 
		//}
		
		if (amt != null) {
			amt.setFiringNodeMessages(returnVV, context);
		}	
		
		return new Value(returnVV, RU.LIST);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return CONSTRUCT_TUTOR_HINT_MESSAGE;
	}

}
