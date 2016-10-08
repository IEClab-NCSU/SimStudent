package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;

import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.settings.ParameterSettings;
import edu.cmu.old_pact.settings.Settings;

public abstract class CellElement implements PropertyChangeListener {
	protected String value = "";
	protected UserMessage[] bugMessage = null;
	// clicked = true if cellView gained a focus.
	protected boolean clicked = false;
	// selected is set from tutor.
	protected boolean selected = false;
	// internal_selected = true, if row or column gained a focus.
	protected boolean internal_selected = false;
	protected boolean calculatable = false;
	protected boolean numeric = false;
	protected boolean hasBounds = true;
	protected boolean displayWebEq = false;
	
	protected boolean highlighted = false;
	protected boolean traceUserAction = false;
	// ? do I need to store these GUI parameters?
	protected int width = 70, height=20;
	protected Color foregroundColor = Color.black;
	protected Color backgroundColor = Color.white;
	protected Color currColor = backgroundColor;
	protected Color hasFocusColor = Settings.hasFocusColor;
	protected Color selectedColor = new Color(175,190,190);
	protected Color highlightColor = new Color(0,220,170); //Color.green;
	protected Font textFont = new Font("Arial",Font.PLAIN, 11);
	protected boolean editable = true;
	protected boolean canBeSelected = false;
	protected String grow = "VERTICAL";
	
	protected int intRow;
	protected int intCol;
	
	protected String name;
	
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public CellElement(){
	}

	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setCalculate(boolean c){
		calculatable = c;
	}
	
	public void setNumeric(boolean n){
		numeric = n;
	}
	
	public void setCanBeSelected(boolean b){
		canBeSelected = b;
	}
	
	public boolean isCalculate(){
		return calculatable;
	}	
	
	public void setEditable(boolean e){
		editable = e;
		if(!editable)
			removeBugMessage();
	}
	
	public boolean isEditable(){
		return editable;
	}
	
	public void setHasBounds(boolean e){
		hasBounds = e;
	}
	
	public void setDisplayWebEq(boolean e){
		displayWebEq = e;
	}
	
	
	public boolean isHasBounds(){
		return hasBounds;
	}
	
	public void setClicked(boolean c){
		boolean old = clicked;
		clicked = c;
		if(clicked) 
			currColor = hasFocusColor;
		else if(selected || internal_selected)
			currColor = selectedColor;
		else
			currColor = backgroundColor;
	}
	
	public boolean isClicked(){
		return clicked;
	}
	
	public void setSelected(boolean c){
		boolean old = selected;
		selected = c;
		if(selected && !clicked)
			currColor = selectedColor;
		else if(!selected)
			currColor = backgroundColor;
	}
	
	protected void setInternalSelected(boolean c){
		internal_selected = c;
		if(internal_selected && !clicked)
			currColor = selectedColor;
		else if(!internal_selected || !selected)
			currColor = backgroundColor;
	}
	
	
	public boolean isSelected(){
		return selected;
	}
	
	public boolean isInternalSelected(){
		return internal_selected;
	}
	
	public void setWidth(int w){
		int oldw = width;
		if(width != w){
			width = w;
			changes.firePropertyChange("width", Integer.valueOf(String.valueOf(oldw)), 
										Integer.valueOf(String.valueOf(width)));
		}
	}
	public int getWidth(){
		return width;
	}
	
	public void setHeight(int h){
		int oldh = height;
		if(height != h){
			height = h;
			changes.firePropertyChange("height", Integer.valueOf(String.valueOf(oldh)), 
										Integer.valueOf(String.valueOf(height)));
		}
	}
	public int getHeight(){
		return height;
	}
	
	public void setHighlighted(boolean h){
		highlighted = h;
	}
	public boolean isHighlighted(){
		return highlighted;
	}
	
	public void setTraceUserAction(boolean b){
		traceUserAction = b;
	}
	
	public Hashtable getAllProperties(){
		Hashtable toret = new Hashtable();
		toret.put("VALUE", getValue());
		if(bugMessage != null)
			toret.put("BUGMESSAGE", bugMessage);
		toret.put("HASFOCUS", Boolean.valueOf(String.valueOf(clicked)));
		toret.put("ISHIGHLIGHTED", Boolean.valueOf(String.valueOf(highlighted)));
		toret.put("TRACEUSERACTION", Boolean.valueOf(String.valueOf(traceUserAction)));
		toret.put("ISEDITABLE", Boolean.valueOf(String.valueOf(editable)));
		toret.put("CANBESELECTED", Boolean.valueOf(String.valueOf(canBeSelected)));
		toret.put("ISSELECTED", Boolean.valueOf(String.valueOf(selected)));
		toret.put("INTERNALSELECTED", Boolean.valueOf(String.valueOf(internal_selected)));
		toret.put("ISCALCULATABLE", Boolean.valueOf(String.valueOf(calculatable)));
		toret.put("ISNUMERIC", Boolean.valueOf(String.valueOf(numeric)));
		toret.put("WIDTH", Integer.valueOf(String.valueOf(width)));
		toret.put("HEIGHT", Integer.valueOf(String.valueOf(height)));
		toret.put("HASBOUNDS", Boolean.valueOf(String.valueOf(hasBounds)));
		toret.put("DISPLAYWEBEQ", Boolean.valueOf(String.valueOf(displayWebEq)));
		toret.put("BACKGROUNDCOLOR",backgroundColor);
		toret.put("FOREGROUNDCOLOR",foregroundColor);
		toret.put("HASFOCUSCOLOR",hasFocusColor);
		toret.put("SELECTEDCOLOR",selectedColor);
		toret.put("HIGHLIGHTCOLOR", highlightColor);
		toret.put("FONT", textFont);
		toret.put("GROW", grow);
		return toret;
	}
	
	public void setColor(String ident, Color c){
		if (ident.equalsIgnoreCase("FOREGROUNDCOLOR"))
			foregroundColor = c;
		else if (ident.equalsIgnoreCase("BACKGROUNDCOLOR"))
			backgroundColor = c;
		else if (ident.equalsIgnoreCase("HASFOCUSCOLOR"))
			hasFocusColor = c;
		else if (ident.equalsIgnoreCase("SELECTEDCOLOR"))
			selectedColor = c;
		else if (ident.equalsIgnoreCase("HIGHLIGHTCOLOR"))
			highlightColor = c;
		
	}
	
	public int[] getPosition(){
		int[] toret = {intRow, intCol};
		return toret;
	}
	
	public void setPosition(int rowPos, int colPos){
		intRow = rowPos;
		intCol = colPos;
	}
	
	public void setBugMessage(UserMessage[] mess){
		bugMessage = mess;
	}
	
	public UserMessage[] getBugMessage(){
		return bugMessage;
	}
	
	protected void removeBugMessage() {
		if(bugMessage != null)
			bugMessage = null;
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String propertyName = evt.getPropertyName();
		if(propertyName.equalsIgnoreCase("VALUE")) 
			setValue((String)evt.getNewValue());
		else if(propertyName.equalsIgnoreCase("GOTFOCUS"))
			clicked = true;
		else if(propertyName.equalsIgnoreCase("LOSTFOCUS"))
			clicked = false;
		else if(propertyName.equalsIgnoreCase("GROW"))
			grow = evt.getNewValue().toString();
		else if(propertyName.equalsIgnoreCase("BUGMESSAGE")){
			Object newValue = evt.getNewValue();
			if(newValue instanceof String &&
				((String)newValue).equals("")) 
				removeBugMessage();
			else if(newValue instanceof UserMessage[])
				bugMessage = (UserMessage[])newValue;
		}
		else if(propertyName.equalsIgnoreCase("HEIGHT"))
			height = ((Integer)evt.getNewValue()).intValue();
		else if(propertyName.equalsIgnoreCase("WIDTH")) 
			width = ((Integer)evt.getNewValue()).intValue();
		else if(propertyName.equalsIgnoreCase("ISSELECTED")) 
			setSelected(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("INTERNALSELECTED")) 
			setInternalSelected(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("ISEDITABLE")) 
			setEditable(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("CAMNBESELECTED")) 
			setCanBeSelected(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("HASBOUNDS")) 
			setHasBounds(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("ISCALCULATABLE")) 
			setCalculate(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("ISNUMERIC")) 
			setNumeric(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("ISHIGHLIGHTED")) 
			setHighlighted(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("TRACEUSERACTION")) 
			setTraceUserAction(((Boolean)evt.getNewValue()).booleanValue());
		else if(propertyName.equalsIgnoreCase("DISPLAYWEBEQ")) 
			setDisplayWebEq(((Boolean)evt.getNewValue()).booleanValue());
				
		else if (propertyName.equalsIgnoreCase("FOREGROUNDCOLOR") ||
			propertyName.equalsIgnoreCase("BACKGROUNDCOLOR") ||
			propertyName.equalsIgnoreCase("HASFOCUSCOLOR")||
			propertyName.equalsIgnoreCase("SELECTEDCOLOR") ||
			propertyName.equalsIgnoreCase("HIGHLIGHTCOLOR")) {
			
			setColor(propertyName, (Color)evt.getNewValue());
		}
		else if(propertyName.equalsIgnoreCase("FONT")) {
			setFont((Font)evt.getNewValue());
		}
		else if(propertyName.equalsIgnoreCase("FONTSTYLE")) {
				int intStyle = ParameterSettings.getFontStyle((String)evt.getNewValue());
				Font f = getFont();
				if(f != null && f.getStyle() != intStyle) {				
					Font newF = new Font(f.getName(), intStyle, f.getSize());
					setFont(newF);
				}
			}
	}
	
	public Font getFont(){
		return textFont;
	}
	
	public void setFont(Font f){
		textFont = f;
	}
	
	public void setValue(String val){
		value = val;
	}
	
	public abstract String getValue();
	
}	
	