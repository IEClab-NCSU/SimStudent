package edu.cmu.old_pact.cmu.toolagent;

// Proxy object ReasonTool row.
// 	Properties   	Object
//	Label			Cell,POSITION,1  	setProperty("Value")
//					Cell,POSITION,2		setProperty("Name")
//	Value			Cell,POSITION,2		setProperty("Value")
// 	Reason			Cell,POSITION,4		setProperty("Value")

import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.MatrixElementProxy;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;

public class  ReasonRowProxy extends MatrixElementProxy {
	
	public ReasonRowProxy(ObjectProxy parent, String type) {
		 super(parent, type);
	}
	
	public void setProperty(MessageObject inEvent) throws DorminException{ 
		Vector propertyNames,propertyValues;
		try{
			propertyNames = inEvent.extractListValue("PROPERTYNAMES");
			propertyValues = inEvent.extractListValue("PROPERTYVALUES");
			
			int num1 = containsName(propertyNames, "LABEL");
			int num2 = containsName(propertyNames, "VALUE");
			int num3 = containsName(propertyNames, "REASON");
			if(num1 > -1 || num2 > -1 || num3 > -1){
				if(num1 > -1) {
					//String labDesc = getStrDescription()+",Cell,POSITION,1";
					ObjectProxy labelProxy = getContainedObjectBy("Cell","Position","1");
					sendSetProperty(labelProxy,"VALUE",(String)propertyValues.elementAt(num1));
					//String valDesc = getStrDescription()+",Cell,POSITION,2";
					ObjectProxy valueProxy = getContainedObjectBy("Cell","Position","2");
					sendSetProperty(valueProxy,"NAME",(String)propertyValues.elementAt(num1));
				}
				if(num2 > -1) {
					//String valDesc = getStrDescription()+",Cell,POSITION,2";
					ObjectProxy valueProxy = getContainedObjectBy("Cell","Position","2");
					sendSetProperty(valueProxy,"VALUE",(String)propertyValues.elementAt(num2));
				}
				if(num3 > -1) {
					//String valDesc = getStrDescription()+",Cell,POSITION,4";
					ObjectProxy reasonCellProxy = getContainedObjectBy("Cell","Position","4");
					sendSetProperty(reasonCellProxy,"VALUE",(String)propertyValues.elementAt(num3));
				}
			}
			else
				super.setProperty(inEvent);
		
		}catch (DorminException e){
			throw e;
		}				
	}
	
	private void sendSetProperty(ObjectProxy objDesc, String proName, String proValue){
		Vector proNames = new Vector();
		Vector proValues = new Vector();
		proNames.addElement(proName);
		proValues.addElement(proValue);
		sendToCell(objDesc, proNames, proValues);
	}
	
	private void sendToCell(ObjectProxy objDesc, Vector proNames, Vector proValues){
		MessageObject mo = new MessageObject("SETPROPERTY");
		mo.addParameter("PROPERTYNAMES", proNames);
		mo.addParameter("PROPERTYVALUES", proValues);
		mo.addObjectParameter("OBJECT", objDesc);
		// send message internally to the cell. 
		//For some reasons (check it!!) Target "Application" needs to be referenced as "Application0".
		send(mo, "Application0");
	}
	
	private int containsName(Vector where, String what){
		int toret = -1;
		int s = where.size();
		if(s == 0) return toret;
		String curWhat; 
		for(int i=0; i<s; i++){
			curWhat = (String)where.elementAt(i);
			if(curWhat.equalsIgnoreCase(what))
				return i;
		}
		return toret;
	}
			
}