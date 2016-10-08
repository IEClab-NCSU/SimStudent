package edu.cmu.old_pact.cmu.spreadsheet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;

public class RowMatrixElement extends DorminMatrixElement{
	
	public RowMatrixElement(int pos, String header, CellMatrix cellMatrix){
		super(pos, header, cellMatrix);
		intCol = -1;
		intRow = pos;
	}
	
	public RowMatrixElement(int pos, CellMatrix cellMatrix){
		super(pos, null, cellMatrix);
		name = "R";
	}

	public void setPos(int p){
		pos = p;
		intRow = pos;
	}
	
	public void getAccrossCells(){
		if(accrossVisCells == null) {
			clearAccrossListener();
			accrossMatrixEls = cellMatrix.getZeroColElements();
			getAccrossVisCells(accrossMatrixEls);
			addAccrossListeners();
		}
	}
	
	public void setWidth(int w){
	}
	public void setHeight(int h){
		PropertyChangeEvent evt = new PropertyChangeEvent(this,"HEIGHT", Integer.valueOf(String.valueOf(height)), 
														  Integer.valueOf(String.valueOf(h)));
		try{
			vetoableChange(evt);
		} catch (PropertyVetoException e){
			//if(h < height)
			//	System.out.println("in RowMatrixElement can't setHeight to "+h+" minHeight = "+height);
		}
	}	
	
	public  void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException{
		if(evt.getPropertyName().equalsIgnoreCase("WIDTH")) {
			if(evt.getSource() instanceof HeaderTextField){
				int newsize = ((Integer)evt.getNewValue()).intValue();
				boolean canModify = false;
				getAccrossCells();
				
				if((width > newsize && canModifyWidth(newsize, accrossVisCells)) ||
					width < newsize)
					canModify = true;
		//System.out.println("for "+this+" canModify = "+canModify+" accross = "+accross);
				
				if(canModify) {
					accross.firePropertyChange("width", Integer.valueOf(String.valueOf(width)),Integer.valueOf(String.valueOf(newsize)));
					width = newsize;
					cellMatrix.getZeroColElement().setWidth(width);
				}
				else
					throw new PropertyVetoException("Can't change width", evt);
			}
		}
		else {
			super.vetoableChange(evt);
		}
	}
	
	void addCell(Cell cell){
		super.addCell(cell);
		cell.setHeight(height);
	}
}
	
			
	
	