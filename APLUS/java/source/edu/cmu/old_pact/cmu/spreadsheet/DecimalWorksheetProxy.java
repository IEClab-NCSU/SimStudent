package edu.cmu.old_pact.cmu.spreadsheet;

import java.util.Vector;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;

public class DecimalWorksheetProxy extends WorksheetProxy {
	
	public DecimalWorksheetProxy(ObjectProxy parent) {
		 super(parent, "DecimalArithTool_no_labels");
	}
	
	public DecimalWorksheetProxy(ObjectProxy parent, String myType){
		super(parent, myType);
	}
	
	public DecimalWorkSheet createWorkSheet(int numRows, int numCols){
		return new DecimalWorkSheet(numRows, numCols, this, "DecimalArithTool_no_labels");
	}
	
	public DecimalWorkSheet createWorkSheet(int numRows, int numCols, int numDigits){
	//System.out.println("!!!!!!! NATASHA: in DecimalWorksheetProxy.createWorkSheet !!!!!!!");
	//System.out.println("numRows=  "+numRows+"  numCols=  "+numCols+"  numDigits=  "+numDigits);	
		return new DecimalWorkSheet(numRows, numCols, numDigits, this, "DecimalArithTool_no_labels");
	}
	
	public  void create(MessageObject inEvent) throws DorminException{
		try{
			String childType = inEvent.extractStrValue("OBJECTTYPE");
			if(childType.equalsIgnoreCase("DecimalArithTool_no_labels") ||
			   childType.equalsIgnoreCase("DecimalArithTool_labels") ||
			   childType.equalsIgnoreCase("DecimalArithTool_money")) {
			
			  	Vector propertyNames = inEvent.extractListValue("PROPERTYNAMES");
				Vector propertyValues = inEvent.extractListValue("PROPERTYVALUES");
				
				int rowsPos = fieldPosition(propertyNames,"NUMBEROFROWS");
				int colPos = fieldPosition(propertyNames,"NUMBEROFCOLUMNS");
				int digitsPos = fieldPosition(propertyNames,"NUMBEROFDIGITS");
						
				Integer strRows = (Integer)propertyValues.elementAt(rowsPos);
				Integer strCols = (Integer)propertyValues.elementAt(colPos);
				Integer strDigits = (Integer)propertyValues.elementAt(digitsPos);
				Object realCont = getRealParent();				
				DecimalWorkSheet ws = createWorkSheet(strRows.intValue(),strCols.intValue(), 
														strDigits.intValue());
				this.setRealObject(ws);
				ws.setProxyInRealObject(this);
				ws.setWorksheetMenuBar();
				setRealObjectProperties((Sharable)ws, inEvent);	
	 	     }  		
			else if (childType.equalsIgnoreCase("Row")) 
				((DecimalWorkSheet)getObject()).sp.addNewRow();
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