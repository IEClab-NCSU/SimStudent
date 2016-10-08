package edu.cmu.old_pact.cmu.spreadsheet;

import edu.cmu.old_pact.dormin.ObjectProxy;

public class DecimalWorksheetProxy_labels extends DecimalWorksheetProxy {
	
	public DecimalWorksheetProxy_labels(ObjectProxy parent) {
		 super(parent, "DecimalArithTool_labels");
	}
	
	public DecimalWorkSheet createWorkSheet(int numRows, int numCols){
		return new DecimalWorkSheet_labels(numRows, numCols, this, "DecimalArithTool_labels");
	}
	
	public DecimalWorkSheet createWorkSheet(int numRows, int numCols, int numDigits){
		return new DecimalWorkSheet_labels(numRows, numCols, numDigits, this, "DecimalArithTool_labels");
	}
	
}