package edu.cmu.old_pact.cmu.spreadsheet;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.ObjectProxy;


public class DecimalWorkSheet_money extends DecimalWorkSheet {

	//private String myName = "DecimalArithTool_money";

	public DecimalWorkSheet_money(int numOfRows, int numOfCols, WorksheetProxy ws_obj, String myName) {
		super(numOfRows, numOfCols, ws_obj, myName);
	}
	
	public DecimalWorkSheet_money(int numOfRows, int numOfCols, int numOfDigits, 
								  WorksheetProxy ws_obj, String myName) {
	
		super(numOfRows, numOfCols, ws_obj, myName);
		try{
			setProperty("numberOfDigits", new Integer(numOfDigits));
		} catch(DorminException e) { }
	}
	
	public SpreadsheetPanel createSpreadsheetPanel(CellMatrix cellMatrix, ObjectProxy obj){
		return  new DecimalSpreadsheetPanel_money(cellMatrix, this, obj);
	}
	
}