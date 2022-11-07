package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Insets;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.old_pact.toolframe.ToolBarPanel;

// here SkillsManager class is connected to WS;

// Properties:
//-------------------------------------------
//		NAME(String)			TYPE(Object)
//-------------------------------------------
//		Variable				String
//		numOfColumnsMatched		int
//		numOfColumns			int
//		numOfColumnsMatched		int

public class DecimalWorkSheet extends WorkSheet { 

	
	public DecimalWorkSheet(int numOfRows, int numOfCols, WorksheetProxy ws_obj, String myName) {
		super(numOfRows, numOfCols, ws_obj, myName);
	}
	
	public DecimalWorkSheet(int numOfRows, int numOfCols, int numOfDigits, 
							WorksheetProxy ws_obj, String myName) {
	
		super(numOfRows, numOfCols, ws_obj, myName);
		
//trace.out("!!!!!!! NATASHA: in DecimalWorksheet AFTER SUPER!!!!!!!");
//trace.out("numOfRows=  "+numOfRows+"  numOfCols=  "+numOfCols+"  numOfDigits=  "
//+numOfDigits+"  about to call setProp 'NUMBERofDIGITS'");		
		try{
			setProperty("numberOfDigits", new Integer(numOfDigits));
		} catch(DorminException e) { }
		
	}

	
	public SpreadsheetPanel createSpreadsheetPanel(CellMatrix cellMatrix, ObjectProxy obj){
		return  new DecimalSpreadsheetPanel(cellMatrix, this, obj);
	}
	
	//setupToolBar adds the buttons and images to the toolbar
	public void setupToolBar(ToolBarPanel tb) {
		add("West",m_ToolBarPanel);
		tb.setBackground(Settings.ssToolBarColor);
		tb.setInsets(new Insets(0,0,0,0));
		tb.addButton(Settings.help,"Help", false);
		//tb.addSeparator();
		//tb.addToolBarImage(Settings.wsLabel,Settings.wsLabelSize);
	}
				
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
			if( propertyName.equalsIgnoreCase("NUMBEROFDIGITS")){
				getAllProperties().put(propertyName.toUpperCase(), propertyValue);
				((DecimalSpreadsheetPanel)sp).numDigits = ((Integer)propertyValue).intValue();
				sp.updateView();
			} 
			else 
				super.setProperty(propertyName, propertyValue);
			} catch (NoSuchPropertyException e){
			throw new NoSuchPropertyException("DecimalWorksheet : "+e.getMessage());
		}
	} 
}