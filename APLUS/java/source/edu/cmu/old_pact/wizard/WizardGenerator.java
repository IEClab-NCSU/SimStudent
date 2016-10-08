package edu.cmu.old_pact.wizard;

import edu.cmu.old_pact.cmu.toolagent.FactoringSingleField;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.pact.Utilities.trace;


public class WizardGenerator {
	
	public static Object getObject(String type,ObjectProxy parent){
		if(type.equalsIgnoreCase("Label"))
			return new DorminLabel(parent);
		else if(type.equalsIgnoreCase("Choice"))
			return new DorminChoice(parent);
		else if(type.equalsIgnoreCase("HtmlPanel"))
			return new DorminHtmlPanel(parent);
		else if(type.equalsIgnoreCase("Button"))
			return new WizardButton(parent);
		else if(type.equalsIgnoreCase("Line"))
			return new DorminLine(parent);
		else if(type.equalsIgnoreCase("SingleField")){
			trace.out (10,null, "creating SingleField");
			FactoringSingleField fsf = new FactoringSingleField(parent);
			trace.out (10,null, "2");
			fsf.setInitText();
			trace.out (10,null, "3");
			fsf.getObjectProxy().defaultPosDescription();
			trace.out (10,null, "4");
			return fsf;
		}
		else if(type.equalsIgnoreCase("Checkbox"))
			return new DorminCheckbox(parent);
		else if(type.equalsIgnoreCase("Panel"))
			return new DorminPanel(parent);
		return null;
	}
}