package edu.cmu.pact.miss.jess;

import java.io.Serializable;

import jess.Context;
import jess.JessException;
import jess.RU;
import jess.Userfunction;
import jess.Value;
import jess.ValueVector;
import edu.cmu.pact.Utilities.trace;

/**
 *
 */
public class ConstructMenuOption implements Userfunction, Serializable {

	/**	 */
	private static final String CONSTRUCT_MENU_OPTION = "construct-menu-option";
	
	/**	 */
	protected transient ModelTracer amt;
	
	/**	 */
	protected transient Context context;
	 
	public ConstructMenuOption(){
		this(null);
	}
	
	public ConstructMenuOption(ModelTracer amt){
		this.amt = amt;
	}
	
	@Override
	public Value call(ValueVector vv, Context context) throws JessException {
		
		Value v;
		Value returnValue;
		String returnString = "";
		String tempString;
		char c,e;
		ValueVector returnVV = new ValueVector();
		
		for(int i=1; i < vv.size(); i++) {
			v = vv.get(i);
			try {
				tempString = v.resolveValue(context).stringValue(context);
			} catch(JessException je) {
				trace.out("Exception handled: " + je.getMessage());
				trace.out(je.getProgramText());
				trace.out(je.getLineNumber());
				tempString = v.toString();
				if(tempString.endsWith("]")) {
					tempString = tempString.substring(0, tempString.length()-1);
					returnString += tempString;
					returnValue = new Value(returnString, RU.STRING);
					returnVV.add(returnValue);
					returnString = "";
					continue;
				}
			}
			
			tempString.replaceAll("\n", " ");
			tempString.replaceAll("\t", " ");
			tempString.replaceAll("\\s+", " ");
			
			if(tempString.length() > 0){
				c = tempString.charAt(0);
				e = tempString.charAt(tempString.length()-1);
			} else {
				continue;
			}
			
			if(c == '[') {
				if(!returnString.trim().equals("")){
					returnValue = new Value(returnString, RU.STRING);
					returnVV.add(returnValue);
					returnString = "";
				}
				tempString = tempString.substring(1);
				if(tempString.length() > 0){
					c = tempString.charAt(0);
				} else {
					continue;
				}
			}
			
			if(e == ']') {
				tempString = tempString.substring(0, tempString.length()-1);
				returnString += tempString;
				returnValue = new Value(returnString, RU.STRING);
				returnVV.add(returnValue);
				returnString = "";
				continue;
			}
			
			if(c == '?' || c == '?') {
				try {
					v = v.resolveValue(context);
					returnString += v.stringValue(context) + " ";
				} catch(JessException je){
					returnString += v.stringValue(context) + " ";
				}	
			} else {
				returnString += tempString + " ";
			}
			
		}
		
		returnValue = new Value(returnString, RU.STRING);
		returnVV.add(returnValue);
		
		if(amt != null){
			amt.setMenuOptionMessages(returnVV, context);
		}
			
		return new Value("T", RU.STRING);
	}

	@Override
	public String getName() {
		return CONSTRUCT_MENU_OPTION;
	}
}
