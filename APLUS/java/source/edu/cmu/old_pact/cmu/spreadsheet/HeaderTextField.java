package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;

public class HeaderTextField extends OrderedTextField {

	private boolean firstClicking = false;

	public HeaderTextField(){
		super();
	}
	
	public synchronized void focusGained(FocusEvent evt) { 
		try {
			vetos.fireVetoableChange("internalselected", Boolean.valueOf(String.valueOf(isInternalSelected())),
									 Boolean.valueOf("true")); 
		} catch (PropertyVetoException e) { }
		super.focusGained(evt);
	}
	
	public synchronized void focusLost(FocusEvent evt) { 
 		firstClicking = false;
		sendUnSelect();
		super.focusLost(evt);
	}
	
	protected void sendUnSelect(){
		try {
			if(vetos != null)
				vetos.fireVetoableChange("internalselected", 
										 Boolean.valueOf(String.valueOf(isInternalSelected())), 
										 Boolean.valueOf("false")); 
		} catch (PropertyVetoException e) { }
	}

	public void keyPressed(KeyEvent evt){
		String eventName = getEventName(evt);
		
		if(eventName.equals("")) {
			sendUnSelect();
			setBackground(hasFocusColor);
		}
		super.keyPressed(evt);
	}
	
	public void mouseReleased(MouseEvent evt) {
		firstClicking = true;
		super.mouseReleased(evt); 
	}
	
	public void mouseDragged(MouseEvent evt){
		if(firstClicking){
			sendUnSelect();
			setBackground(hasFocusColor);
		}
		super.mouseDragged(evt);
	}
	
	public void mousePressed(MouseEvent evt){
		if(!justSelected){
			sendUnSelect();
			setBackground(hasFocusColor);
		}
		super.mousePressed(evt);
	}
	
	protected synchronized Color getBackgroundColor(){	
 		if(highlighted)
 			return highlightColor;
		if(hasFocus) 
			return hasFocusColor;
		if(selected || internal_selected)
			return selectedColor;
		return backgroundColor;
	}
}	