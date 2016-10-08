//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/TablePanel.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.CellMatrix;
import edu.cmu.old_pact.cmu.spreadsheet.DorminGridElement;
import edu.cmu.old_pact.cmu.spreadsheet.DorminMatrixElement;
import edu.cmu.old_pact.cmu.spreadsheet.Focusable;
import edu.cmu.old_pact.cmu.spreadsheet.GridBox;
import edu.cmu.old_pact.cmu.spreadsheet.GridElement;
import edu.cmu.old_pact.cmu.spreadsheet.HeaderGrid;
import edu.cmu.old_pact.cmu.spreadsheet.NoSuchCellException;
import edu.cmu.old_pact.cmu.spreadsheet.SpreadsheetPanel;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.ObjectProxy;


public class TablePanel extends SpreadsheetPanel{
	private int nextRow = 0;
	private Vector labelColorV;
	private Vector labelFontV;
 	int[] minColWidth = {87,50,50,150};
	
	public TablePanel(	CellMatrix cellMatrix, 
						PropertyChangeListener frameListener, 
						ObjectProxy proxyParent){ 
		super(cellMatrix, frameListener, proxyParent,SpreadsheetPanel.HEADER_NO);
		setCanRemoveComponents(false);
		
		labelColorV = new Vector();
		labelColorV.addElement(Integer.valueOf("255"));
		labelColorV.addElement(Integer.valueOf("255"));
		labelColorV.addElement(Integer.valueOf("255"));
		
		labelFontV = new Vector();
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			labelFontV.addElement("geneva");
		else
			labelFontV.addElement("arial");
			
		labelFontV.addElement("BOLD");
		labelFontV.addElement(Integer.valueOf("10"));
		
		updateView();
		setColumnsWidth();
		setRowProperties();
	}
	
	public void removeAll(){
		labelColorV.removeAllElements();
		labelColorV = null;
		labelFontV.removeAllElements();
		labelFontV = null;
		super.removeAll();
	}
		
	
	private void setColumnsWidth(){
		Vector proNames= new Vector();
		proNames.addElement("WIDTH");
		Vector proValues = new Vector();
				
		MessageObject mo;
		String objPreDesc = proxyParent.getStrDescription()+",Column,POSITION,";
		String objDesc;
		Integer wid = Integer.valueOf(String.valueOf(minColWidth[0]));
		proValues.addElement(wid);		
    	   
		for(int i=0; i<3; i++){
			if(i==1){
				proValues.removeElement(wid);
				wid = Integer.valueOf(String.valueOf(minColWidth[i]));
				proValues.addElement(wid);
			}	
			ObjectProxy colProxy = proxyParent.getContainedObjectBy("Column","POSITION",String.valueOf(i+1));
			mo = createMessageObject(colProxy,proNames, proValues);
			proxyParent.send(mo, "Application0");
		}
    		
		proValues.removeAllElements();
		proValues.addElement(Integer.valueOf(String.valueOf(minColWidth[3])));
		objDesc = objPreDesc+String.valueOf(4);
		ObjectProxy col4Proxy =  proxyParent.getContainedObjectBy("Column","POSITION","4");
		mo = createMessageObject(col4Proxy,proNames, proValues);
		proxyParent.send(mo, "Application0");
		
		proNames.removeAllElements();
		proNames = null;
		proValues.removeAllElements();
		proValues = null;
	}
	
	private MessageObject createMessageObject(ObjectProxy objDesc, Vector proNames, Vector proValues){
		MessageObject mo = new MessageObject("SETPROPERTY");
		mo.addParameter("PROPERTYNAMES", proNames);
		mo.addParameter("PROPERTYVALUES", proValues);
		mo.addObjectParameter("OBJECT", objDesc);
		return mo;
	}	
	
	/**
	* Sets properties of the cell in a just created row.
	**/
	public void setRowProperties(){
		GridBox[][] grids = getAllGrids();
		try{
			for(int i=0; i<3; i++){
				grids[nextRow][i].setProperty("ISEDITABLE",  Boolean.valueOf("false"));
				grids[nextRow][i].setProperty("HASFOCUSCOLOR",labelColorV);
				grids[nextRow][i].setProperty("FONT",labelFontV);
				grids[nextRow][i].setProperty("HASBOUNDS", Boolean.valueOf("false"));
				i++;
			}
			grids[nextRow][0].setTextFieldAlignment(0,1);
			grids[nextRow][0].setProperty("NAME","LABEL");
			grids[nextRow][2].setTextFieldAlignment(2,1);
			grids[nextRow][2].setProperty("GROW", "HORIZONTAL");
			grids[nextRow][2].setProperty("VALUE", "Reason");
			grids[nextRow][3].setProperty("NAME", "Reason");
			nextRow++;
		} catch(DorminException e) { e.printStackTrace();}
		
	}
	

	public Dimension preferredSize(){
		if(getCellMatrix() == null)
			return new Dimension(350,150);
		else
			return super.preferredSize();
	}

	public void addNewRow(){
		super.addNewRow();
		setRowProperties();
    }
	
	public void addZeroElements(){
	  if(zeroRowHeader == null){
 
		zeroRowHeader = new HeaderGrid(cellMatrix.getZeroRowElement(), -1, -1);
		zeroRowHeader.addPropertyChangeListener(this);
		this.addPropertyChangeListener(zeroRowHeader);
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_ROW)
			initGridElement(zeroRowHeader);

		zeroColHeader = new HeaderGrid(cellMatrix.getZeroColElement(), -1, -1);
		zeroColHeader.addPropertyChangeListener(this);
		this.addPropertyChangeListener(zeroColHeader);
		if(showHeaders == HEADER_BOTH || showHeaders == HEADER_COLUMN)
			initGridElement(zeroColHeader);
		}
	}
	
	public HeaderGrid createRowHeaderGrid(int r, int c){
 		HeaderGrid hg = new TableHeaderGrid(getCellMatrix().getRowMatrix(r), r+2, c);
 		return hg;
 	}
 	
 	public HeaderGrid createColHeaderGrid(int r, int c){
 		HeaderGrid hg = new TableHeaderGrid(getCellMatrix().getColMatrix(c), r, c+2);
 		return hg;
 	}

	public GridBox createNewGridBox(int r, int c) throws NoSuchCellException {
	   try{
		   GridBox gb = new GridBox(cellMatrix.getCell(r, c),(r+2),(c+2), minColWidth[c]);
		   return gb;
		} catch (NoSuchCellException e) { }
		return null;
	}
	
	
	public void initCellGridElement(GridElement gridEl, int rowNum, int colNum){
		 DorminMatrixElement re = (DorminMatrixElement)getCellMatrix().getRowMatrixElement(rowNum);
		((DorminGridElement)gridEl).createObjectProxy(re.getObjectProxy());
		addFrameAsListener(gridEl);
	}
	
	public boolean asksForHint(){
		Focusable gr = findFocusedGrid();
		int[] coor = gr.getPosition();
		if( coor[1] != 0 && coor[1] != 2 ){
			(gr.getGridable()).askForHint();
			return true;
		}
		return false;
	}
}