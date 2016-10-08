package edu.cmu.old_pact.cmu.spreadsheet;

import edu.cmu.old_pact.dormin.ObjectProxy;

public class DecimalWorksheetProxy_money extends DecimalWorksheetProxy {
	
	public DecimalWorksheetProxy_money(ObjectProxy parent) {
		 super(parent, "DecimalArithTool_money");
	}
	
	public DecimalWorkSheet createWorkSheet(int numRows, int numCols){
		return new DecimalWorkSheet_money(numRows, numCols, this, "DecimalArithTool_money");
	}
	
	public DecimalWorkSheet createWorkSheet(int numRows, int numCols, int numDigits){
		return new DecimalWorkSheet_money(numRows, numCols, numDigits, this, "DecimalArithTool_money");
	}	
		
}