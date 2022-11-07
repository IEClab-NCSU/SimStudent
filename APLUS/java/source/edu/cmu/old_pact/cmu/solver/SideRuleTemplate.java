package edu.cmu.old_pact.cmu.solver;

import edu.cmu.old_pact.cmu.tutor.TranslatorProxy;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.pact.Utilities.trace;

public class SideRuleTemplate{
	private String[] messages;
	
	public SideRuleTemplate(TranslatorProxy trans, int currState){
		String action = null;
		try{
			action = trans.getProperty("Tool", "current action formatted");
		} catch (NoSuchPropertyException err) {
			try{
				trace.out("SRT: no such property current action formatted");
				action = trans.getProperty("Tool", "current action");
			}
			catch (NoSuchPropertyException err2){ }
		}
		if (action != null)
			action = (action.substring(0, 1)).toUpperCase()+action.substring(1);
		String sideName = "left";
		if(currState > 1)
			sideName = "right";
		messages = new String[2];
		messages[0] = action+" on the "+sideName+" side.";
		String value = "";
		try{
			value = trans.getProperty("Tool", sideName);
		} catch (NoSuchPropertyException err) { }
		messages[1] = "Type in <expression>"+value+"</expression> on the "+sideName+".";
	}
	
	public String[] getMessages(){
		return messages;
	}
}
