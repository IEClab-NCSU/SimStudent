package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class GridBox extends DorminGridElement{
	protected Cell cell;
	protected RowMatrixElement rowElement;
	protected ColMatrixElement colElement;
	
	public GridBox(Cell cell, int r, int c, Gridable textField, int minWidth){
		super(r, c);
		this.textField = textField;
		
		if(minWidth != -1) {
			textField.setMinWidth(minWidth);
		    textField.setWidth(minWidth);
		}
		setInit();
		this.cell = cell;
		this.rowElement = (RowMatrixElement)cell.getMatrixElement("ROW");
		this.colElement = (ColMatrixElement)cell.getMatrixElement("COLUMN");

		textField.addPropertyChangeListener(this);
		if(textField instanceof PropertyChangeListener)
			addPropertyChangeListener((PropertyChangeListener)textField);
		textField.addPropertyChangeListener(cell);
		textField.setName(cell.getName());
		
		rowElement.addVisualCell(textField);
		colElement.addVisualCell(textField);
		//initTextField(cell.getAllProperties());
	}
	
		
	public GridBox(Cell cell, int r, int c){
		this(cell, r, c, (new OrderedTextField()), -1);		
	}
	
	public GridBox(Cell cell, int r, int c, int w){
		this(cell, r, c, (new OrderedTextField()), w);		
	}
	
	public void initTextField(){
		initTextField(cell.getAllProperties());
	}
	
	public int getWidth(){
		return textField.getWidth();
	}
	
	public synchronized void clear(boolean delete_Proxy){
		rowElement.removeVisualCell(textField);
		colElement.removeVisualCell(textField);
		textField.removePropertyChangeListener(this);
		textField.removePropertyChangeListener(cell);
		if(textField instanceof PropertyChangeListener)
			removePropertyChangeListener((PropertyChangeListener)textField);
		textField.clear();
		removeAll();
		textField = null;
		super.clear(delete_Proxy);
	}
	
	public int getHeight(){
		return textField.getHeight();
	}
	
/*	
	public boolean hasFocus(){
		//Hashtable cellProperties = cell.getAllProperties();
		//return ((Boolean)cellProperties.get("HASFOCUS")).booleanValue();
		return textField.hasFocus();
	}
*/
	public  void propertyChange(PropertyChangeEvent evt){
		super.propertyChange(evt);
		if(evt.getPropertyName().equalsIgnoreCase("HEIGHT")) 
			cell.setHeight(((Integer)evt.getNewValue()).intValue());
		
	}

	public Dimension preferredSize(){
		Dimension d;
		if(textField == null)
			d = new Dimension(70, 20);
		else 
			d = new Dimension(textField.getWidth(), textField.getHeight());
		return d;
	}
	
	public int[] getPosition(){
		return cell.getPosition();
	}
	
	public Dimension minimumSize(){
		return preferredSize();
	}
	
}
		