package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.cmu.sm.SymbolManipulator;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.settings.ParameterSettings;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.pact.Utilities.trace;

public class CustomTextField extends AltTextField {

    //boolean     	hasFocus = false;
    private boolean selectedFromTutor = false;
    String			name, type;
    String			commitedContents = "";
	protected Color	foregroundColor = Color.black;
    protected Color backgroundColor = Color.white;
    //protected Color	selectedColor = new Color(175,190,190);
    protected Color selectedColor = Color.pink;
    protected Color	hasFocusColor = Settings.lightLightGray;
    protected Color highlightColor = new Color(0,220,170);  //Color.green;
    protected boolean sendValue = true;
    private boolean justHighlighted = false;
    // on mac lostFocus event is NOT temporary ALWAYS!!. 
    //tempoLostFocus param gives us a way to send the value (for "Done" or "Hint") only once.
    private boolean tempoLostFocus = false;
    
    SymbolManipulator sm;
    
    protected Hashtable Properties = null;
	
	
    public CustomTextField() {
    	super();
    	Properties = new Hashtable();
    	try{
    		setOwnProperty("Locked", Boolean.valueOf("false"));
    		setOwnProperty("FOREGROUNDCOLOR", foregroundColor);
    		setOwnProperty("BACKGROUNDCOLOR", backgroundColor);
    		setOwnProperty("HASFOCUSCOLOR", hasFocusColor);
    		setOwnProperty("SELECTEDCOLOR", selectedColor);
    		setOwnProperty("HIGHLIGHTCOLOR", highlightColor);
    		setOwnProperty("DisplayWebEq", Boolean.valueOf("false"));
    		setOwnProperty("hasFocus", Boolean.valueOf("false"));
    	} catch (NoSuchFieldException e) { }
    }
    


    public void clear(){
    	if (Properties != null)
	    	Properties.clear();
    	Properties = null;
    	name = null;
    	commitedContents = null;
		foregroundColor = null;
    	backgroundColor = null;
    	selectedColor = null;
    	hasFocusColor = null;
    	highlightColor = null;
    	text = null;
    	sm = null;
    	super.clear();
 	}
    
    public synchronized void setColor(String whereColor, Color color){
    	boolean force_gc = false;
		if (whereColor.equalsIgnoreCase("FOREGROUNDCOLOR")) {
			foregroundColor = color;
			setForeground(foregroundColor);
		}
		else if(whereColor.equalsIgnoreCase("BACKGROUNDCOLOR")){
			backgroundColor = color;
			setBackground(backgroundColor);
			force_gc = true;
		}
		else if(whereColor.equalsIgnoreCase("HASFOCUSCOLOR"))
			hasFocusColor = color;
		else if(whereColor.equalsIgnoreCase("SELECTEDCOLOR"))
			selectedColor = color;
		else if(whereColor.equalsIgnoreCase("HIGHLIGHTCOLOR"))
			highlightColor = color;
		changes.firePropertyChange(whereColor, (new Color(111,111,111)), color);
	
		if (whereColor.equalsIgnoreCase("FOREGROUNDCOLOR") && displayWebEq) {
	  	  	if(displayImage != null)
	  	  		displayImage.delete();
	  	   	displayImage = null;
			if(getText() != "") 
	  	  		displayImage = CellWebEqHelper.createWebEqImage(getText(),getFont(),foregroundColor,this);
			force_gc = true; 
		}	
		repaint();
	}
	
	
	public void setBackground(Color c) {
	 	super.setBackground(c);
	 	
	 	if(displayWebEq) {
	 	  if(displayImage != null)
	  	  	displayImage.delete();
	  	  displayImage = null; 
	  	  if(getText().length() != 0) 
	  	  	displayImage = CellWebEqHelper.createWebEqImage(getText(),getFont(),foregroundColor,this);
		}	
	 }
	 
	public void setHighlighted(boolean h){
		highlighted = h;
		setBackground(getBackgroundColor());
 	}
 	
 	public void setCommitedContents(String s){
 		commitedContents = s;
 	}
 	
 	public String getCommitedContents(){
 		return commitedContents;
 	}
	
	protected Color getDefaultBgColor() {
		return backgroundColor;
	}
	
	protected synchronized Color getBackgroundColor(){	
		if(!editable) {
			if(highlighted)
 				return highlightColor;
 			if(canBeSelected && selected)
 				return selectedColor;
			return backgroundColor;
		}
 		if(highlighted)
 			return highlightColor;
		if(hasFocus) 
			return hasFocusColor;
		if(selected || internal_selected)
			return selectedColor;
		return backgroundColor;
	}
	
	
 	public void setOwnProperty(String p_name, Object p_value) throws NoSuchFieldException{
 		try{
 		Properties.put(p_name.toUpperCase(), p_value);
 		
 		if(p_name.equalsIgnoreCase("VALUE")) {
 			this.setText(p_value.toString());
 			if(displayWebEq){ 
 				setDisplayWebEq(true);
 				repaint();
 			}
 			updateSize();
 		} 
 		else if(p_name.equalsIgnoreCase("DisplayWebEq")) {
 			boolean d = DataConverter.getBooleanValue(p_name,p_value);
			setDisplayWebEq(d);
 			repaint();
			
 		}			
 		else if(p_name.equalsIgnoreCase("LOCKED")) {
			boolean l = DataConverter.getBooleanValue(p_name,p_value);
			setLock(l);
		}
		else if(p_name.equalsIgnoreCase("TRACEUSERACTION")) {
			boolean l = DataConverter.getBooleanValue(p_name,p_value);
			traceUserAction = l;
		}
		else if(p_name.equalsIgnoreCase("PAINTBLTR")) {
			paintBLTR  = DataConverter.getBooleanValue(p_name,p_value);
			repaint();
		}
		else if(p_name.equalsIgnoreCase("ISHIGHLIGHTED") ||
				p_name.equalsIgnoreCase("HIGHLIGHT")){
			boolean currValue = highlighted;
			boolean h = DataConverter.getBooleanValue(p_name,p_value);
			if(h)
				highlighted = true;
			else  
				highlighted = false;
			changes.firePropertyChange("isHighlighted", Boolean.valueOf(String.valueOf(currValue)), 
										Boolean.valueOf(String.valueOf(highlighted)));			
			synchronized (this){	
				//if (h )
					//requestFocus();
				setBackground(getBackgroundColor());
				repaint();
			}
		}
		else if(p_name.equalsIgnoreCase("VERTICALALIGNMENT")){
			String val = (String)p_value;
			if(val.equalsIgnoreCase("TOP")) {
				vAlignment = AltTextField.ALIGN_TOP;
				repaint();
			}
			else if(val.equalsIgnoreCase("CENTER")) {
				vAlignment = AltTextField.ALIGN_CENTER;
				repaint();
			}
			else if(val.equalsIgnoreCase("BOTTOM")) {
				vAlignment = AltTextField.ALIGN_BOTTOM;
				repaint();
			}
			else 
				throw new NoSuchFieldException("Property '"+p_name+"' for Object 'TextField' doesn't have a value '"+p_value+"'");
		}
		else if(p_name.equalsIgnoreCase("GROW")){
			String val = (String)p_value;
			if(	val.equalsIgnoreCase("NOGROW") ||
				val.equalsIgnoreCase("VERTICAL") ||
				val.equalsIgnoreCase("VERTICAL_GROW_VAR_WIDTH") ||
				val.equalsIgnoreCase("VERTICAL_GROW_CONST_WIDTH") ||
				val.equalsIgnoreCase("HORIZONTAL"))
				changes.firePropertyChange("GROW", "", val);
			if(val.equalsIgnoreCase("NOGROW")) {
				setGrow(AltTextField.NO_GROW);
				repaint();
			}
			else if(val.equalsIgnoreCase("VERTICAL")) {
				setGrow(AltTextField.VERTICAL_GROW);
				setCanGrowHorizontally(true);
				repaint();
			}
			else if(val.equalsIgnoreCase("VERTICAL_GROW_VAR_WIDTH")) {
				setGrow(AltTextField.VERTICAL_GROW);
				setCanGrowHorizontally(true);
				repaint();
			} 
			else if(val.equalsIgnoreCase("VERTICAL_GROW_CONST_WIDTH")) {
				setGrow(AltTextField.VERTICAL_GROW);
				setCanGrowHorizontally(false);
				repaint();
			}
			else if(val.equalsIgnoreCase("HORIZONTAL")) {
				setGrow(AltTextField.HORIZONTAL_GROW);
				repaint();
			}
			else 
				throw new NoSuchFieldException("Property '"+p_name+"' for Object 'TextField' doesn't have a value '"+p_value+"'");
		}
		
		else if(p_name.equalsIgnoreCase("HORIZONTALALIGNMENT")){
			String val = (String)p_value;
			if(val.equalsIgnoreCase("LEFT")) {
				hAlignment = AltTextField.ALIGN_LEFT;
				repaint();
			}
			else if(val.equalsIgnoreCase("MIDDLE")) {
				hAlignment = AltTextField.ALIGN_MIDDLE;
				repaint();
			}
			else if(val.equalsIgnoreCase("RIGHT")) {
				hAlignment = AltTextField.ALIGN_RIGHT;
				repaint();
			}
			else 
				throw new NoSuchFieldException("Property '"+p_name+"' for Object 'TextField' doesn't have a value '"+p_value+"'");
		}		 
				
		else if (p_name.equalsIgnoreCase("FOREGROUNDCOLOR") ||
			p_name.equalsIgnoreCase("BACKGROUNDCOLOR") ||
			p_name.equalsIgnoreCase("HASFOCUSCOLOR")||
			p_name.equalsIgnoreCase("SELECTEDCOLOR") ||
			p_name.equalsIgnoreCase("HIGHLIGHTCOLOR")) {
			Color c = ParameterSettings.getColor(p_value);
			if(c != null)
				setColor(p_name,c);
		}
		else if(p_name.equalsIgnoreCase("TEXTLENGTH")){
			int h = DataConverter.getIntValue(p_name,p_value);
			textLengthLimit = h;
		}
		else if(p_name.equalsIgnoreCase("FONT")){
			Font f = ParameterSettings.getFont( p_value);
			if(f != null)
				setFont(f);
		}
		else if(p_name.equalsIgnoreCase("FONTSTYLE")){
			String style = (String)p_value;
			int intStyle = ParameterSettings.getFontStyle(style);
			Font f = getFont();
			if(f != null && f.getStyle() != intStyle && !displayWebEq) {				
				Font newF = new Font(f.getName(), intStyle, f.getSize());
				setFont(newF);
			}
		}
		else if(p_name.equalsIgnoreCase("FONTSIZE")){
			int s = DataConverter.getIntValue(p_name,p_value);				
			Font f = getFont();
			if(f != null &&  f.getSize() != s) {				
				Font setF = new Font(f.getName(), f.getStyle(), s);
				setFont(setF);
			}	
		}
		else if(p_name.equalsIgnoreCase("WIDTH")){
			int w = DataConverter.getIntValue(p_name,p_value);	
			setMinWidth(w);
			try {
   				vetos.fireVetoableChange("width", (Integer.valueOf(String.valueOf(getSize().width))), ((Integer)p_value));
   				setWidth(w);
   			} catch (PropertyVetoException e) { }
		}
		else if(p_name.equalsIgnoreCase("HEIGHT")){
			int h = DataConverter.getIntValue(p_name,p_value);
			setMinHeight(h);
			try {
   				vetos.fireVetoableChange("height", (Integer.valueOf(String.valueOf(getSize().height))), ((Integer)p_value));
   				setHeight(h);
   			} catch (PropertyVetoException e) { }
		}
		else if(p_name.equalsIgnoreCase("ISCALCULATABLE")) 
			setCalculate(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("ISNUMERIC"))
			setNumeric(DataConverter.getBooleanValue(p_name,p_value));
		else if (p_name.equalsIgnoreCase("LOCATION"))
			setAbsLocation((Vector)p_value);
		else if (p_name.equalsIgnoreCase("NAME")) {
			setName((String)p_value);
		}
		// Irene, this "isEditable" is never set through the properties now, this is bad
		else if(p_name.equalsIgnoreCase("ISEDITABLE"))
			setEditable(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("CANBESELECTED"))
			setCanBeSelected(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("ISVISIBLE"))
			setVisible(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("HASFOCUS")){
			boolean toset = DataConverter.getBooleanValue(p_name,p_value);
			if(toset && !hasFocus) {
//changes.firePropertyChange("FocusGained", null, this);
//changes.firePropertyChange("gotFocus", Boolean.valueOf("false"), Boolean.valueOf("true"));
				this.requestFocus();
			}				
			hasFocus=toset;
			repaint();
		}
		else if(p_name.equalsIgnoreCase("ISSELECTED"))
			setSelected(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("INTERNALSELECTED"))
			setInternalSelected(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("HASBOUNDS"))
			setHasBounds(DataConverter.getBooleanValue(p_name,p_value));
		else if(p_name.equalsIgnoreCase("BUGMESSAGE"))
			setBugMessage((UserMessage[])p_value);
		else
			throw new NoSuchFieldException(p_name);
		} catch (DataFormattingException ex){
			throw new NoSuchFieldException(ex.getMessage());
		}
 	}
 	
 	
/* 	
 	public void transferFocus() {
   		//do nothing here , so parent will not get a focus
   	}
   	
   	public void nextFocus() {
   		//do nothing here , so parent will not get a focus
   	}
 */	
 	public void setAbsLocation(Vector loc){
 		int x = ((Integer)loc.elementAt(0)).intValue();
 		int y = ((Integer)loc.elementAt(1)).intValue();
 		setLocation(x,y);
 	}
 	
 	public Hashtable getOwnProperty(Vector proNames) throws NoSuchFieldException{
 		int s = proNames.size();
 		if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
 			return Properties;
 		Hashtable toret = new Hashtable();
 		String currName;
 		for(int i=0; i<s; i++){
 			currName = ((String)proNames.elementAt(i)).toUpperCase();
 			Object ob = Properties.get(currName);
 			if(ob == null)
 				throw new NoSuchFieldException("Cell doesn't have property "+currName);
 			toret.put(currName, ob);
 		}
 		return toret;
 	}
 	
 // add it later
/* 	
 	public boolean isFocusTraversable(){
 		if(!locked() && isEditable())
   			return true;
   		return false;
   }
*/ 	
 	public void setFont(Font f){
 		Font oldFont = getFont();
 		if(f.getSize() == 0)
		  f =new Font(f.getName(), f.getStyle(), getFont().getSize());
 		super.setFont(f);
 		changes.firePropertyChange("Font", oldFont, f);
	}
	
	public Hashtable getAllProperties(){
		return Properties;
	}
 	
 	public void setText(String t) {
 		super.setText(t);
 		Properties.put("VALUE", t);
 		try{
 			changes.firePropertyChange("value", commitedContents, getText());
 		} catch (NullPointerException e) { 
 			//e.printStackTrace();
 		}
 	}
 	
 	public Object getOwnProperty(String key) {
 		if(key.equalsIgnoreCase("Value"))
 			Properties.put("VALUE", getText());
 		return Properties.get(key.toUpperCase());
 	}
 	
    public String name() {
    	return name;
    }
    
    public void setName (String n) {
    	Properties.put("NAME", n);
    	name = n;
    }
    public String getName(){
    	return name;
    }
    
    public void setType(String t) {
    	type = t;
    }
	
    synchronized public void  reset(String s) {
    	this.reset(s, null);
	}
	
	synchronized public void  reset(String s, String col) {
    	setText(s);
    	//locked=false;
    	//commitedContents = "";
    	try{
    		setOwnProperty("Locked", Boolean.valueOf("false"));
    	} catch (NoSuchFieldException e) { }
    	touched=false;
    	if(col != null){
    		try{
    			setOwnProperty("Color", col);
    		} catch (NoSuchFieldException e) { }
		}
    	repaint();	
	}	

	public synchronized void setEditable(boolean b){
		changes.firePropertyChange("iseditable", Boolean.valueOf(String.valueOf(editable)), 
									Boolean.valueOf(String.valueOf(b)));
		Properties.put("ISEDITABLE",Boolean.valueOf(String.valueOf(editable))); 
		super.setEditable(b);
		setLock(!b);
		repaint();
	}
	
	public void setCanBeSelected(boolean b){
		changes.firePropertyChange("canBeSelected", Boolean.valueOf(String.valueOf(canBeSelected)), 
									Boolean.valueOf(String.valueOf(b)));
		super.setCanBeSelected(b);
	}
	
	public synchronized void setCalculate(boolean b){
		changes.firePropertyChange("iscalculatable", Boolean.valueOf(String.valueOf(calculatable)), 
									Boolean.valueOf(String.valueOf(b)));
		Properties.put("ISCALCULATABLE",Boolean.valueOf(String.valueOf(b))); 
		super.setCalculate(b);
		calculate();
	}
	
	public synchronized void setNumeric(boolean b){
		changes.firePropertyChange("isnumeric", Boolean.valueOf(String.valueOf(numeric)), 
									Boolean.valueOf(String.valueOf(b)));
		Properties.put("ISNUMERIC",Boolean.valueOf(String.valueOf(b))); 
		super.setNumeric(b);
	}
	
	public boolean isEditable(){
		boolean toret =  !locked();
		return toret;
	}
	
	public void setHasBounds(boolean b){
	 	Properties.put("HASBOUNDS",Boolean.valueOf(String.valueOf(b))); 
	 	super.setHasBounds(b);
	 }
	
	public boolean locked() {
		if(Properties != null)
			return ((Boolean)Properties.get("LOCKED")).booleanValue();
		return false;
	}
	
	public void setLock(boolean b) {
		//trace.out (5, "CustomTextField.java", "setLocked: locked = " + b);
		
		Properties.put("LOCKED", Boolean.valueOf(String.valueOf(b)));
		if(b){
			commitedContents = getText();
			//setText(commitedContents);
			modified = false;
			repaint();
		}
		else {
			if(!getText().equals (""))
				commitedContents = getText();				
		}
	}
	
	public boolean askedForHelp(){
		return false;
	}

	synchronized public void paint (Graphics g)  throws NullPointerException{
		g.setColor(foregroundColor);
		try{
			super.paint(g);
		} catch (NullPointerException e) {
			//trace.out("in CustomTF paint: "+e);
		}
		
	}

	public void keyPressed(KeyEvent evt){
		//sendValue = true;
		int key = evt.getKeyCode();
		trace.out (10, this, "key code = " + key);

		// Enter or Tab, respectively
		if (listener != null && (key == 10 || key == 9))
			listener.enterPressed();

		if (!evt.isMetaDown() && !evt.isControlDown()) {
    		switch (key) { 
    		case 13:
    		case 10:
    				modified=false;
    				break;
    		default:
				if (!locked()) {
					super.keyPressed(evt);
					modified = true;
				}
				else {
					(Toolkit.getDefaultToolkit()).beep();
				}
				break;
			}
    	} else if (evt.isMetaDown() || evt.isControlDown()){
    		switch (key){
    			case KeyEvent.VK_C:
    				if(hasClipboardEvents)			
    				copy();
    				break;
    			default:
    				if(!locked()) 
						super.keyPressed(evt);
					else if(key ==KeyEvent.VK_V || key == KeyEvent.VK_X){
						(Toolkit.getDefaultToolkit()).beep();
					}
					break;
 			}    	
    	}
    	sendValue = true;
	}
	
	
	protected void paintText(Graphics g, int start, int end, int vPos) { 
   		super.paintText(g, start, end, vPos);
    	//updateSize();
    }
    
 //   public void updateSize(){
 //   	Rectangle boundingBox = getBoundingBox();    
 //   	if(m_grow == VERTICAL_GROW)
 //   		updateSizeVertically(boundingBox);    		  		
 //  		else if(m_grow == HORIZONTAL_GROW)
 //  			updateSizeHorizontally(boundingBox);
 //  	}
   
   public void updateSize(){
   	try{
   		Rectangle boundingBox = getBoundingBox();
   		boolean isResizedSave = isResized;
    	if(m_grow == VERTICAL_GROW){
    			// if a cell can grow vertically, when changing font size
   				// update both dimensions
   		  if(canGrowHorizontally){	
    		 updateSizeHorizontally(boundingBox);
    			//restore original value of isResized (in case it has been changed
    			//been changed by updateSizeHorizontally
    		 isResized = isResizedSave;
    	  }
    	  updateSizeVertically(boundingBox);
    	}	
   		else if(m_grow == HORIZONTAL_GROW){
   			updateSizeHorizontally(boundingBox);
   			isResized = isResizedSave;
   			updateSizeVertically(boundingBox);
   		}
   	} catch (NullPointerException e) { }
   }

   
   private void updateSizeVertically(Rectangle boundingBox) {
   		int boxh = boundingBox.height;
    	int curh = getSize().height;
   		if(	boxh > curh  ||	(boxh < curh-2 && !isResized)) {
   			isResized = true;  				
   			if(boxh < minHeight)
   				boxh = minHeight; 
   			doUpdateHeight(curh,boxh);
   		}	
   }
   
   private void updateSizeHorizontally(Rectangle boundingBox) {
   		int boxw = boundingBox.width;
    	int curw = getSize().width;
 
    	if(boxw > curw  ||(boxw < curw-2 && !isResized) || boxw < minWidth) {
   			isResized = true;
   			if(boxw < minWidth)
   				boxw = minWidth;
   			doUpdateWidth(curw, boxw); 
   		}
   	}
   	
   public void doUpdateWidth(int oldw, int neww) {
   		try {
   			vetos.fireVetoableChange("width",Integer.valueOf(String.valueOf(oldw)),Integer.valueOf(String.valueOf(neww))); 		
   				//the actual update takes place in vetoableChange 
   			} catch (PropertyVetoException e) {}
   }
   
   
   public void doUpdateHeight(int oldh, int newh) {
   		try {
   			vetos.fireVetoableChange("height",Integer.valueOf(String.valueOf(oldh)),Integer.valueOf(String.valueOf(newh)));   					
  					//the actual update takes place in vetoableChange	
   				} catch (PropertyVetoException e) { }
   }
   
   
  synchronized public void paintWebEqImage(Graphics g) {
		if (displayImage != null) {          
      		//int w = displayImage.getWidth(this);
      		//int h = displayImage.getHeight(this);
      		Image im = displayImage.getEqImage();  
      		int w = im.getWidth(this);
      		int h = im.getHeight(this);
      		int margin = 4; 
      		int newW = w+margin; 
      		int newH = h+margin;    	
    		int curw = getSize().width;
			int curh = getSize().height;
 			boolean isResizedSave = isResized;
 			
   			if(newH > curh  || newH < curh && !isResized) {
   				isResized = true;
   				doUpdateHeight(curh,newH); 
   			}
   				//restore original value of isResized (in case it has been changed
    			//been changed by updateSizeHorizontally
    		isResized = isResizedSave;
    		
    		if((newW > curw || (newW < curw || newW < minWidth) && 
       							!isResized)) {
    		  isResized = true;    				
   			  if(newW < minWidth)
   			    newW = minWidth;
     			   			  
   			  doUpdateWidth(curw, newW);
   			} 
   			int x = 2; // allow space for BoundingBox
   			int y = 2;  
   			curw = getSize().width;	// updated current size
   			curh = getSize().height;
   				//center the image if needed	 			
   			if(w+margin < curw)  {  
    	  	  x = (curw - w) / 2;
      		}     
      		if(h+margin < curh) { 
    	  	  y = (curh - h) / 2;
    	  	}
       //g.drawImage(displayImage,x,y,w,h,this); 
       g.drawImage(im,x,y,w,h,this); 
        //try {
		//  MediaTracker tracker = new MediaTracker(this);
   		// tracker.addImage(im, 0);
   		//tracker.waitForAll();
   		//} catch(Exception e) {}	 
   		
      }   
	}
	
	public void setModified(boolean b){
		modified = b;
	}	
   
   
    public synchronized void focusGained(FocusEvent evt) {
    	try{
		hasFocus = true;
		tempoLostFocus = false;
		Properties.put("HASFOCUS", Boolean.valueOf("true"));
		if (!locked()){
			modified = false;
			commitedContents = getText();
		}
		setBackground(getBackgroundColor());
		super.focusGained(evt);
		// send which cell is in focus info first
		changes.firePropertyChange("FocusGained", null, this);
	   	changes.firePropertyChange("gotFocus", Boolean.valueOf("false"), Boolean.valueOf("true"));
/*
		if(!isEditable()) {
			showCaret = false;
			CaretThread.releaseCaretThread();
		}
*/
		} catch (NullPointerException e) { }
	}
/*
	public synchronized void focusLost(FocusEvent evt) { 
		hasFocus = false;
		Properties.put("HASFOCUS",Boolean.valueOf("false"));
		
		if(!locked() && (!getText().trim().equals("") || !getText().equals(commitedContents))){
			modified = false;
			if(!evt.isTemporary())
				sendUserValue(null, getText());
			else
				commitedContents = getText();

			setText(getText());
			calculate();
		}
		setBackground(getBackgroundColor());
	
		changes.firePropertyChange("lostFocus", Boolean.valueOf("false"), Boolean.valueOf("true"));	
		repaint();
		super.focusLost(evt);
	}
*/	
	public synchronized void focusLost(FocusEvent evt) { 
		hasFocus = false;
		Properties.put("HASFOCUS",Boolean.valueOf("false"));
		setBackground(getBackgroundColor());
	
		changes.firePropertyChange("lostFocus", Boolean.valueOf("false"), Boolean.valueOf("true"));	
		repaint();
		if(!locked() && (!getText().trim().equals("") || !getText().equals(commitedContents))){
			modified = false;
			setText(getText());
			calculate();
			if(!evt.isTemporary())
				sendUserValue(null, getText());
			else
				commitedContents = getText();
		}
		
		super.focusLost(evt);
	}

	public boolean canSendUserValue(){
		if(!locked() && (!getText().trim().equals("") || modified))
			return true;
		return false;
	}		
	
	public synchronized void requestFocus(){
		//don't send anything to tutor when cell requests a focus
		sendValue = false;
		super.requestFocus();
		changes.firePropertyChange("FocusGained", null, this);
	}
	
	public void sendUserValue(String oldText, String newText){
		if(sendValue && !tempoLostFocus) {
			changes.firePropertyChange("userValue", oldText, newText);
			//this fixes the problem with multiple sending "value" 
			//if "help" button has been pressed/released
			sendValue = false;
		}
	}
	
	public void setSendValue(boolean b){
		sendValue = b;
	}
	
	public boolean getSendValue(){
		return sendValue;
	}
	
	public void setTempoLostFocus(boolean b){
		tempoLostFocus = b;
	}
	protected synchronized void calculate(){
		if(calculatable) {
			String currText = getText();
			if(!(getText().trim()).equals("")){
				currText = simplify(currText);
				setText(currText);
				commitedContents = currText;
				repaint();
			}
		}
	}
	
	public String simplify(String expString){
		String toret = expString;
		if(sm == null){
			sm = new SymbolManipulator();
			sm.autoStandardize = true;
		}
		try {
			toret = sm.simplify(expString);
		} catch (edu.cmu.old_pact.cmu.sm.BadExpressionError e) { }
		return toret;
	}
	
	public void askForHint(){ 
		changes.firePropertyChange("getHint", "", "getHint");
	}
	
	public boolean isFocused(){
		return hasFocus;
	}
		
	public void writeToCell(){
		if(modified && !commitedContents.equals(getText())) {
			modified = false;
			changes.firePropertyChange("value", commitedContents, getText());
			commitedContents = getText();
		}
	//changes.firePropertyChange("hasFocus", (new Boolean(false)), (new Boolean(true)));
	}
	
	void revert () {
		setText(commitedContents);
	}

	public Hashtable allProperties() {
		return Properties;
	}
}
