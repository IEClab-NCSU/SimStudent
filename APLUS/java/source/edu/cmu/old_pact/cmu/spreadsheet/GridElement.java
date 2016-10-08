package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Component;
import java.awt.Font;
import java.awt.Panel;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;

public abstract class GridElement extends Panel implements 	PropertyChangeListener, 
												ComponentListener, Focusable{
	protected Gridable textField = null;
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public GridElement(){
	}
	
	protected void setInit(){
		setLayout(null);
		add((Component)textField);
		textField.setLocation(0, 0);
		addComponentListener(this);
		textField.setHasClipboardEvents(false); // if frame has a menu shortcut
	}

	public void clear(boolean delete_Proxy){
		removeComponentListener(this);
	}
	
	public boolean hasFocus(){
		if(textField != null){
			return textField.hasFocus();
		}
		return false;
	}
	
	public String getName(){
		return null;
	}
	
	public Gridable getGridable(){
		return textField;
	}
	
	public void writeToCell(){
		if(textField != null)
			textField.writeToCell();
	}
	
	public void deleteAll(){
		textField = null;
	}
	
	public boolean isEditable(){
		if(textField != null)
			return textField.isEditable();
		return false;
	}
	
	public void requestFocus(){
		super.requestFocus();
		if(textField != null) {
			textField.requestFocus();
		}
	}
	
	protected void initTextField(Hashtable cellProperties){
	/*
		try{
			Enumeration keys = cellProperties.keys();
			while(keys.hasMoreElements()){
				String iden = (String)keys.nextElement();
				textField.setOwnProperty(iden,cellProperties.get(iden));
			}
		} catch (NoSuchFieldException e) {System.out.println("in GE initTextField");e.printStackTrace(); }
	*/
		textField.setWidth(((Integer)cellProperties.get("WIDTH")).intValue());
		textField.setHeight(((Integer)cellProperties.get("HEIGHT")).intValue());
		textField.setValue((String)cellProperties.get("VALUE"));
		textField.setEditable(((Boolean)cellProperties.get("ISEDITABLE")).booleanValue());
		textField.setBugMessage((UserMessage[])cellProperties.get("BUGMESSAGE"));
		textField.setHasBounds(((Boolean)cellProperties.get("HASBOUNDS")).booleanValue());
		textField.setCalculate(((Boolean)cellProperties.get("ISCALCULATABLE")).booleanValue());
		textField.setNumeric(((Boolean)cellProperties.get("ISNUMERIC")).booleanValue());
		try{
			textField.setOwnProperty("displayWebEq",cellProperties.get("DISPLAYWEBEQ"));
			textField.setOwnProperty("foregroundColor",cellProperties.get("FOREGROUNDCOLOR")); 
			textField.setOwnProperty("backgroundColor",cellProperties.get("BACKGROUNDCOLOR"));
			textField.setOwnProperty("hasFocusColor",cellProperties.get("HASFOCUSCOLOR"));
			textField.setOwnProperty("selectedColor",cellProperties.get("SELECTEDCOLOR"));
			textField.setOwnProperty("highlightColor",cellProperties.get("HIGHLIGHTCOLOR"));
			textField.setOwnProperty("font",(Font)cellProperties.get("FONT"));
			textField.setOwnProperty("hasFocus",cellProperties.get("HASFOCUS"));
			textField.setOwnProperty("GROW",cellProperties.get("GROW"));
		} catch (NoSuchFieldException e) { }
		textField.setHighlighted(((Boolean)cellProperties.get("ISHIGHLIGHTED")).booleanValue());
		textField.setTraceUserAction(((Boolean)cellProperties.get("TRACEUSERACTION")).booleanValue());
		
		if(textField instanceof CustomTextField){
			((CustomTextField)textField).setAlignment (AltTextField.ALIGN_MIDDLE, AltTextField.ALIGN_CENTER);
			//((CustomTextField)textField).setGrow(AltTextField.VERTICAL_GROW);	
		}
			
		repaint();
	}
	
	
	public void setTextFieldAlignment(int v, int h){
		if(textField instanceof CustomTextField)
			((CustomTextField)textField).setAlignment (v,h);
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(eventName.equalsIgnoreCase("HEIGHT")) {
			int height = ((Integer)evt.getNewValue()).intValue();
			setSize(getSize().width, height);
		}
		else if(eventName.equalsIgnoreCase("WIDTH")) {
			int width = ((Integer)evt.getNewValue()).intValue();
			setSize(width, getSize().height);
		}
		else if(eventName.equalsIgnoreCase("NEXTINCOLUMN") ||
			eventName.equalsIgnoreCase("NEXTINROW") ||
			eventName.equalsIgnoreCase("PREVINCOLUMN") ||
			eventName.equalsIgnoreCase("PREVINROW") ) {
			changes.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
		}
		else if (eventName.equalsIgnoreCase("FONTSIZE")){
			try{
				setCellProperty(eventName,evt.getNewValue());
			} catch (NoSuchFieldException e){ }
		}
	}
	
	public void setCellProperty(String propertyName, Object propertyValue) throws  NoSuchFieldException,DataFormattingException{
		if(textField == null)
			return;
		try{
		if(propertyName.equalsIgnoreCase("VALUE")) 
			//textField.setValue(propertyValue.toString());
			textField.setOwnProperty(propertyName, propertyValue);
		else if(propertyName.equalsIgnoreCase("HASBOUNDS")) 
			textField.setHasBounds(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("ISEDITABLE")) 
			textField.setEditable(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("CANBESELECTED")) 
			textField.setCanBeSelected(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("ISSELECTED")) 
			textField.setSelected(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("TREACEUSERACTION")) 
			textField.setTraceUserAction(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("INTERNALSELECTED")) 
			textField.setInternalSelected(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("NAME")) 
			textField.setName((String)propertyValue);
		else if(propertyName.equalsIgnoreCase("ISCALCULATABLE") ||
				propertyName.equalsIgnoreCase("PAINTBLTR")){
			//boolean b = DataConverter.getBooleanValue(propertyName,propertyValue); 
			textField.setOwnProperty(propertyName, propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("ISNUMERIC")){
			boolean b = DataConverter.getBooleanValue(propertyName,propertyValue); 
			textField.setOwnProperty(propertyName, propertyValue);
		}
		else if(propertyName.equalsIgnoreCase("MINIMUMWIDTH")) {
			textField.setWidth(((Integer)propertyValue).intValue());
			repaint();
		}
		/*
		else if (propertyName.equalsIgnoreCase("FOREGROUNDCOLOR") ||
			propertyName.equalsIgnoreCase("BACKGROUNDCOLOR") ||
			propertyName.equalsIgnoreCase("HASFOCUSCOLOR")||
			propertyName.equalsIgnoreCase("SELECTEDCOLOR") ||
			propertyName.equalsIgnoreCase("HIGHLIGHTCOLOR") ||
			propertyName.equalsIgnoreCase("HIGHLIGHT") ||
			propertyName.equalsIgnoreCase("FONT")||
			propertyName.equalsIgnoreCase("WIDTH")||
			propertyName.equalsIgnoreCase("HEIGHT") ||
			propertyName.equalsIgnoreCase("FONTSIZE") ||
			propertyName.equalsIgnoreCase("HASFOCUS")) {
			textField.setOwnProperty(propertyName, propertyValue);
		}
		*/
		else 
			textField.setOwnProperty(propertyName, propertyValue);
		
		}
		catch(DataFormattingException ex){ throw ex;}
		catch(NoSuchFieldException exc){ throw exc;}
	}
	
	public Hashtable getCellProperty(Vector proNames) throws NoSuchFieldException{
		try{
			Hashtable toret = textField.getOwnProperty(proNames);
			return toret;
		} catch (NoSuchFieldException e){
			throw e;
		}
	}
	
	public void select(){
		textField.requestFocus();
	}

	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void addFrameAsListener(PropertyChangeListener l){
		textField.addPropertyChangeListener(l);
	}
	
	public void removeFrameAsListener(PropertyChangeListener l){
		textField.removePropertyChangeListener(l);
	}
	
	public void cut(){
		textField.cut();
	}
		
	public void copy(){
		textField.copy();
	}
	
	public void paste(){
		textField.paste();
	}
	
	public void askForHint(){
		textField.askForHint();
	}
	
	public void componentResized(ComponentEvent e){
		if(getParent() != null) {
			getParent().layout();
			getParent().setSize(getParent().preferredSize());
			repaint();
		}
	}

    public void componentMoved(ComponentEvent e) {
    }
    public void componentShown(ComponentEvent e) {
    }
    public void componentHidden(ComponentEvent e){
    }
}