package edu.cmu.old_pact.cmu.spreadsheet;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.dormin.toolframe.DorminToolProxy;
import edu.cmu.pact.Utilities.trace;

public class WorksheetProxy extends DorminToolProxy {
	public WorksheetProxy(){
		super();
	}

	public WorksheetProxy(ObjectProxy parent) {
		 super(parent, "Worksheet");
	}
	
	public WorksheetProxy(ObjectProxy parent, String myType){
		super(parent, myType);
	}
	
	public void init(ObjectProxy parent){
		super.init(parent, "Worksheet");
	}
	
	//will create itself if type in the constructor is the same as a prameter childType.
	public boolean isMyType(String childType){
		if(childType.equalsIgnoreCase(type))
			return true;
		return false;
	}
	
	public WorkSheet createWorksheet(int numRows, int numCols){
		return new WorkSheet(numRows, numCols, this,"Worksheet");
	}
	
	public  void create(MessageObject inEvent) throws DorminException{
		trace.out (5, this, "creating worksheet");
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(isMyType(childType)) {
				Vector propertyNames = inEvent.extractListValue("PROPERTYNAMES");
				Vector propertyValues = inEvent.extractListValue("PROPERTYVALUES");
				
				int rowsPos = fieldPosition(propertyNames,"NUMBEROFROWS");
				int colPos = fieldPosition(propertyNames,"NUMBEROFCOLUMNS");
				Integer strRows = (Integer)propertyValues.elementAt(rowsPos);
				Integer strCols = (Integer)propertyValues.elementAt(colPos);
				
				WorkSheet ws = createWorksheet(strRows.intValue(), strCols.intValue());
				this.setRealObject(ws);
				ws.setProxyInRealObject(this);
				ws.setWorksheetMenuBar();
				setRealObjectProperties((Sharable)ws, inEvent);
				
			}
			else if (childType.equalsIgnoreCase("Row")){ 
				((WorkSheet)getObject()).sp.addNewRow();
				((WorkSheet)getObject()).updateFontSize();
			}
			else
				super.create(inEvent);
		}
		catch (DorminException e) {
			throw e;
		}	
	}
	
	public int fieldPosition(Vector from, String fieldName){
		int toret = -1;
		int s = from.size();
		for(int i=0; i<s; i++)
			if(((String)from.elementAt(i)).equalsIgnoreCase(fieldName))
				return i;
		return toret;
	}
}