//d:/Pact-CVS-Tree/Tutor_Java/./src/Middle-School/Java/cmu/toolagent/FactoringSingleField.java
package edu.cmu.old_pact.cmu.toolagent;
 
import java.awt.FontMetrics;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.AltTextField;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.pact.Utilities.trace;

public class FactoringSingleField extends SingleTextField {
	private boolean hasInitText = true;
	private boolean drawInitText = true;
	//private boolean  = false;
	private boolean sendSelection = false;
	
	public FactoringSingleField(int w, ObjectProxy parent, String name, 
							boolean hasBounds, String type){
		super(w, parent, name, type);
		trace.out (10, this, "creating FactoringSingleTextField from algebra-2");
		setAlignment (AltTextField.ALIGN_LEFT, AltTextField.ALIGN_CENTER);
		// input fields will grow horizontally
		//setGrow(AltTextField.NO_GROW);
		setHasBounds(hasBounds);
		try{
			setOwnProperty("Font", Settings.factoringTextFieldFont);
		} catch (NoSuchFieldException e) { }
	}
	
	public FactoringSingleField(ObjectProxy parent){
		this(40,parent,"Field",false,"SingleField");
	}
	
	public void setProperty(String proName, Object proValue)  throws NoSuchPropertyException{
		try{
			if(proName.equalsIgnoreCase("DrawInitText")){
				Properties.put(proName.toUpperCase(), proValue);
				try{
					boolean h = DataConverter.getBooleanValue(proName,proValue);
					setDrawInitText(h);
				}catch (DataFormattingException ex){
					throw new NoSuchFieldException(ex.getMessage());
				} 
			}
			else if(proName.equalsIgnoreCase("SendSelection")){
				Properties.put(proName.toUpperCase(), proValue);
				try{
					boolean h = DataConverter.getBooleanValue(proName,proValue);
					setSendSelection(h);
				}catch (DataFormattingException ex){
					throw new NoSuchFieldException(ex.getMessage());
				} 
			}
			else
				setOwnProperty(proName, proValue);
			//refresh frame 	
			if(	proName.equalsIgnoreCase("Height") ||
				proName.equalsIgnoreCase("Width") ||
				proName.equalsIgnoreCase("DisplayWebEq") )
			  sendRefreshEvent();			 
		} catch (NoSuchFieldException e){
			throw new NoSuchPropertyException(e.getMessage());
		} 
	}
	
	public void sendRefreshEvent(){
		changes.firePropertyChange("REFRESH", "old", "new");
			//changes.firePropertyChange("REFRESHPANEL", "old", "new");	
	}
	
	public void setSendSelection(boolean b){
		sendSelection = b;
	}
	
	public void focusGained(FocusEvent e){
		super.focusGained(e);
		sendIsSelected();
	}
	
	public void sendIsSelected(){
		if(sendSelection && fProxy != null){
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT",fProxy);
			Vector pNames = new Vector();
			pNames.addElement("IsSelected");
			Vector pValues = new Vector();
			pValues.addElement(Boolean.valueOf("true"));
			mo.addParameter("PROPERTYNAMES", pNames); 
			mo.addParameter("PROPERTYVALUES", pValues);
			fProxy.send(mo);
			pNames.removeAllElements();
			pNames = null;
			pValues.removeAllElements();
			pValues = null;
			mo = null;
		}
	}
	
	public void sendUserValue(String oldText, String newText){
		if(!locked() && drawInitText && getText().equals(getCommitedContents()) && getText().indexOf("_") != -1)
			return;
		super.sendUserValue(oldText, newText);
	}
	
	public void mousePressed(MouseEvent evt){
		if(isFocused() && !getJustSelected())
			sendIsSelected();
		super.mousePressed(evt);
	}		
	
	public void setInitText(){
		if(drawInitText && getText().equals("") || getText().indexOf("_") != -1){
			int w = getWidth();
			FontMetrics fm = getFontMetrics(getFont());
			int char_w = fm.charWidth('_');
			int num = w/char_w-2;
			StringBuffer t = new StringBuffer();
			if(num <= 0)
				hasInitText = false;
			else{
				for(int i=0; i<num; i++)
					t.append("_");
				hasInitText= true;
			}
			setText(t.toString());
			//setCommitedContents(t.toString());
			repaint();
		}
	}
	
	public void setWidth(int w){
		super.setWidth(w);
		if(!locked() && getText().indexOf("_") != -1)
			setInitText();
		setCommitedContents(getText());
	}
	
	public void setHasInitText(boolean b){
		hasInitText = b;
	}
	
	public void setDrawInitText(boolean b){
		drawInitText = b;
		if(!drawInitText){
			hasInitText = false;
			if(getText().indexOf("_") != -1)
				setText("");
		}
	}
/*	
	public synchronized void focusLost(FocusEvent evt) {
		try{
		
		if(!locked() && getText().indexOf("_") != -1) {
			setLock(true);
			//setCommitedContents("");
			super.focusLost(evt);
			setLock(false);
		}
		else
			super.focusLost(evt);
		
		if(hasInitText && !locked() && getText().trim().equals(""))
			setInitText();
		} catch (NullPointerException e) { }
	}
*/
	public synchronized void focusLost(FocusEvent evt) {
		try{
			super.focusLost(evt);
		if(hasInitText && !locked() && getText().trim().equals("")){
			setInitText();
			setCommitedContents(getText());
		}
		} catch (NullPointerException e) { }
	}
		
	public void setText(String value){
		if(value.equals("") && hasInitText && !locked())
			setInitText();
		else
			super.setText(value);
	}
	
	 public synchronized void replaceSelection (char c) {
	 	
 		if(getText().indexOf("_") != -1){
 			boolean saveHasInitText = hasInitText;
 			hasInitText = false;
    		setText("");
    		hasInitText = saveHasInitText;
    	}    	
		super.replaceSelection(c);  
	}
	
/*	
    public void keyPressed(KeyEvent evt){
		int key = evt.getKeyCode();
		if(!evt.isMetaDown() && !evt.isControlDown()) {
    		if(getText().indexOf("_") != -1){
    			//setInside = true;
    			setText("");
    			//setInside = false;
    		}
    	}			
    	super.keyPressed(evt);
    }
*/   
}