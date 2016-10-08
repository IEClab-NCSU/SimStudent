package edu.cmu.old_pact.cmu.spreadsheet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

public class ColMatrixElement extends DorminMatrixElement{
	
	public ColMatrixElement(int pos, String header, CellMatrix cellMatrix){
		super(pos, header, cellMatrix);
		intRow = -1;
		intCol = pos;
	}
	
	public ColMatrixElement(int pos, CellMatrix cellMatrix){
		super(pos, null, cellMatrix);
		name = "C";
	}
	
	public void setPos(int p){
		pos = p;
		intCol = pos;
	}

	public void getAccrossCells(){
		if(accrossVisCells == null) {
			clearAccrossListener();
			accrossMatrixEls = cellMatrix.getZeroRowElements();
			getAccrossVisCells(accrossMatrixEls);
			addAccrossListeners();
		}
	}
	
	public void setHeight(int h){
	}
	public void setWidth(int w){
		PropertyChangeEvent evt = new PropertyChangeEvent(this,"WIDTH", Integer.valueOf(String.valueOf(width)),
														  Integer.valueOf(String.valueOf(w)));
		try{
			vetoableChange(evt);
		} catch (PropertyVetoException e){
			//if(w < width)
			//	System.out.println("in ColMatrixElement can't setWidth to "+w+" minWidth = "+width);
		}
	}
	
	
	void addCell(Cell cell){
		super.addCell(cell);
		cell.setWidth(width);
	}
	
	public  void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException{
		if(evt.getPropertyName().equalsIgnoreCase("HEIGHT")) {
			if(evt.getSource() instanceof HeaderTextField){
				int newsize = ((Integer)evt.getNewValue()).intValue();
				boolean canModify = false;
				getAccrossCells();
				if((height > newsize && canModifyHeight(newsize, accrossVisCells)) ||
					height < newsize)
					canModify = true;
				if(canModify) {
					accross.firePropertyChange("height", Integer.valueOf(String.valueOf(height)), 
											   Integer.valueOf(String.valueOf(newsize)));
					height = newsize;
					cellMatrix.getZeroRowElement().setHeight(height);
				}
				else
					throw new PropertyVetoException("Can't change height", evt);
			}
		}
	
		else
			super.vetoableChange(evt);
	}
}