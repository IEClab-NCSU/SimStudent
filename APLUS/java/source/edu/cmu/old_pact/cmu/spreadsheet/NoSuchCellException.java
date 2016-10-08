package edu.cmu.old_pact.cmu.spreadsheet;

public class NoSuchCellException extends Exception {
	private String cellName = "";
	
	NoSuchCellException(String cn) {
		super(cn);
		cellName = cn;
		
	}
	
	NoSuchCellException() {
		super();
	}
	
	public String toString() {
		return "NoSuchCellException: "+cellName;
	}
}