package edu.cmu.old_pact.cmu.spreadsheet;

import java.beans.PropertyChangeEvent;

public class Cell extends CellElement{	
	private MatrixElement rowEl=null, colEl=null;

	public Cell(int intRow, int intCol, String value){
		super();
		this.intRow = intRow;
		this.intCol = intCol;
		this.value = value;
		name = "R"+String.valueOf(intRow)+"C"+String.valueOf(intCol);
	}
	
	public Cell(int intRow, int intCol){
		this(intRow, intCol, "");
	}

	public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("WIDTH")) 
			width = ((Integer)evt.getNewValue()).intValue();
		
		else if(evt.getPropertyName().equalsIgnoreCase("HEIGHT"))
			height = ((Integer)evt.getNewValue()).intValue();
		
		else
			super.propertyChange(evt);
	}
	
	public String getValue(){
		return value;
	}
		
	public  MatrixElement getMatrixElement(String byName){
		if(byName.equalsIgnoreCase("ROW"))
			return rowEl;
		else
			return colEl;
	}
	
	public void setRowElement(MatrixElement r){
		rowEl = r;
		rowEl.addPropertyChangeListener(this);
	}
	
	public void setColElement(MatrixElement c){
		colEl = c;
		colEl.addPropertyChangeListener(this);
	}
	
	public void removeColElement(MatrixElement c) {
		c.removePropertyChangeListener(this);
	}
	
	public void removeRowElement(MatrixElement c ) {
		c.removePropertyChangeListener(this);
	}

}	 