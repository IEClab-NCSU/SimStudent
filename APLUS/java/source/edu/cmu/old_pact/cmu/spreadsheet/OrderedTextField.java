package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import edu.cmu.old_pact.bugtipdisplay.BugDisplayable;
import edu.cmu.old_pact.bugtipdisplay.BugObject;
import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.settings.Settings;

public class OrderedTextField extends CustomTextField implements BugDisplayable{
	protected BugObject bugObject;
	protected boolean bugActive = false;
	protected boolean firstBugShown = false;
	protected boolean sendAnyway = false; 
	protected String imageBase; 
	private boolean fontWasItalic = false;
	private boolean messageJustSet = false;
	private boolean mouseInside = false;
	private boolean showBugImmediately = false;
	private boolean firstFocusLost = false;

	public OrderedTextField() {
    	super();
    	bugObject = new BugObject(this);
    	//changes.addPropertyChangeListener(bugObject);
	}
	
	public void setShowBugImmediately(boolean b){
		showBugImmediately = b;
	}
	
	public boolean getShowBugImmediately(){
		return showBugImmediately;
	}
    
    public void mouseEntered(MouseEvent evt) {
    	changes.firePropertyChange("MouseEntered", "false", "true");
   		//apowers added these lines to make the bug message not go away when students are looking at it
    	if (bugObject.isVisible() && !bugActive)
    	{
	    	changes.firePropertyChange("SHOWTIP", "false", "true");
	    }
	    
    	mouseInside=true;
    }
    
     public void mouseExited(MouseEvent evt) {
     	if(isVisible()){
	    changes.firePropertyChange("MouseExited", "false", "true");
   		//apowers added these lines to make the bug message not go away when students are looking at it
		if (bugObject.isVisible())
     	{
    		changes.firePropertyChange("HIDETIP", "false", "true");
   			firstBugShown = false;
    	}
    	// There's an interesting little bug here that the upper left hand cell THINKS it has focus before 
    	// the first time it is clicked on.
    	// I dunno why.  But fortunately, the kid has to click on the cell first 
    	// (so it corrects its delusions) before a bug message can appear.
    	// If that ever changes, the "!hasFocus()" above may be a culprit in a bug.  
    	// Note this shows in the FakeLispInterface because that isn't the case.
    	// It might be Java, it might be us.  Note: I could be wrong.  - apowers
    	super.mouseExited(evt);
    	mouseInside=false;
    	}
    }
    
    // the only way in JBindery to stop bug thread. It doesn't react on keyPressed event.
    public void mousePressed(MouseEvent evt){
    	//changes.firePropertyChange("MouseExited", "false", "true");//Bad programming practics to lie to the computer, saying that the MouseExited when it didn't
    	super.mousePressed(evt);
    	if(!locked() && editMode){
    		Font f= getFont();
			if(f.isItalic()){
				setFont(new Font(f.getName(), Font.PLAIN, f.getSize()));
				fontWasItalic = true;
			}
		}
		changes.firePropertyChange("MousePressed", null, evt);
    }
    
    public void setBugActive(boolean b){
    	bugActive = b;
    }
    // suppose, that cell can have only one message, which is a bug message
    public void showMessage(UserMessage[] userMessage,String imageBase,String title, int startFrom){
		this.imageBase = imageBase;
		if(userMessage.length == 1 && !bugObject.getValue().equals(userMessage[0].getText())) {
			changes.firePropertyChange("bugMessage", bugObject.getUserMessage(), userMessage);
			//bugObject.setMessage(userMessage, imageBase);
			messageJustSet = true;
			
			if (bugObject.isVisible() && !bugActive && (mouseInside || showBugImmediately) ){
	    		changes.firePropertyChange("SHOWTIP", "false", "true");
	    		if(showBugImmediately) {
	    			firstBugShown = true;
	    		}
	    	}
	    }
	}
	
	public void setBugMessage(UserMessage[] userMessage){
//		if(userMessage != null)
//			bugObject.setMessage(userMessage, imageBase);
 	}
	
	public void clear(){
		//trace.out (5, "OrderedTextField.java", "clear");
		if (bugObject != null)
			bugObject.destroyTip();
		if (changes != null) {
			changes.firePropertyChange("destroy", null, "this");
//			changes.removePropertyChangeListener(bugObject);
		}
		bugObject = null;
		super.clear();
	}
	
	public void setSelected(boolean s){
		if(editable){
			if(s)
				setBackground(selectedColor);
			else {
				if(highlighted)
					setBackground(highlightColor);
				else
					setBackground(backgroundColor);
			}
		}
		if(canBeSelected)
			sendIsSelected(s);
		
		super.setSelected(s);
		
	}
	
	// does nothing in the spreadsheet mode. 
	public void sendIsSelected(boolean s){
		changes.firePropertyChange("isselected", Boolean.valueOf(String.valueOf(selected)),
									 Boolean.valueOf(String.valueOf(s)));
	}
	
	
	public void setInternalSelected(boolean s){
		if(editable){
			if(s || selected)
				setBackground(selectedColor);
			else {
				if(highlighted)
					setBackground(highlightColor);
				else
					setBackground(backgroundColor);
			}
		}
		changes.firePropertyChange("internalselected", Boolean.valueOf(String.valueOf(selected)),
								   Boolean.valueOf(String.valueOf(s)));
		super.setInternalSelected(s);
	}
	
	public String getEventName(KeyEvent evt) {
		String eventName = "";
		int key = evt.getKeyCode();
		if (evt.isActionKey()) {
			switch (key) {
			case KeyEvent.VK_UP:
				eventName = "PrevInColumn";
				break;
			case KeyEvent.VK_DOWN:
				eventName = "NextInColumn";
				break;
			case KeyEvent.VK_LEFT:
				eventName = "PrevInRow";
				break;
			case KeyEvent.VK_RIGHT:
				eventName = "NextInRow";
				break;					
			}
			if(!eventName.equals("") && editMode)
				return new String("");
		}
		else if(evt.isShiftDown()){
    		switch (key){
    			case KeyEvent.VK_TAB:
    				eventName = "PrevInRow";
    				break;
    			case KeyEvent.VK_ENTER:
    				eventName = "PrevInColumn";
    				break;
    			default:
    				break;
    				
    		}
 		}     
		else {
			switch (key) {
			case  9:
				eventName = "NextInRow";
				break;
			case 10:
			case 3:
				eventName = "NextInColumn";
				break;
			}
		}
		return eventName;
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("FOCUSGAINEDBYCELL")) {
			if(firstBugShown && !firstFocusLost)
				firstFocusLost = true;
			else if(firstBugShown && firstFocusLost && this != evt.getNewValue()){
				firstBugShown = false;
				changes.firePropertyChange("HIDETIP", "false", "true");
			}
			else if(this == evt.getNewValue() && firstBugShown)
				firstBugShown = false;
		}
		else
			super.propertyChange(evt);
	}

				
	public void keyPressed(KeyEvent evt){	
		String eventName = getEventName(evt);			
		if(evt.isActionKey() && evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_F1 ){
			changes.firePropertyChange("OpenTeacherWindow", "here", "there");
			return;
    	}		
		if(!eventName.equals("")) {	
		    changes.firePropertyChange(eventName, null, this);
 		    sendValue = true;
			return;
		}
/*
		else if (!locked() && firstClick && bugObject.isVisible()) {
			sendAnyway = true;
			changes.firePropertyChange("KeyPressed", "here", "there");
			firstClick = false;
		}
*/		
		else if (!locked() && bugObject.isVisible()) 
			sendAnyway = true;

		super.keyPressed(evt);				
	}

	public synchronized void paint(Graphics g){
		try{
			super.paint(g);
			if(	bugObject != null && bugObject.isVisible())
				drawBugObject(g);
		} catch (NullPointerException e) {
			//e.printStackTrace(); 
		}
	}
	
	public void setOwnProperty(String p_name, Object p_value) throws NoSuchFieldException{
		if(p_name.equalsIgnoreCase("SHOWBUGIMMEDIATELY")){
			boolean l = DataConverter.getBooleanValue(p_name,p_value);
			showBugImmediately  = l;
			return;
		}
		else if(p_name.equalsIgnoreCase("VALUE")){
			if(!getText().equals(p_value.toString())){
				changes.firePropertyChange("bugMessage", "removeBugMessage", "");
				bugObject.setValue("");
			}
		}
		super.setOwnProperty(p_name, p_value);
	}
	
	public synchronized void setEditable(boolean b){
		if(!b )
			removeBugObject();
		super.setEditable(b);
	}
	
	public void removeBugObject(){
		if(bugObject != null && bugObject.isVisible()){
			changes.firePropertyChange("bugMessage", "removeBugMessage", "");
			bugObject.setValue("");
		}
	}
	
	public synchronized void focusLost(FocusEvent evt) {
		try{ 
		//apowers added this line to make the bug message not go away when students are looking at it
		
		if (bugObject.isVisible() && !evt.isTemporary())
//			if (!mouseInside)
//			{   
				changes.firePropertyChange("HIDETIP", "false", "true");
//				}
		
		if(modified && commitedContents.equals(getText())) {
			if(sendAnyway) {
				changes.firePropertyChange("value", commitedContents, getText());
				//changes.firePropertyChange("value", "", getText());
				sendAnyway = false;
			}
			if(!(bugObject.getValue()).equals("")) {
				bugObject.setVisible(true);
				repaint();
			}
		}
		if(!locked() && (!getText().trim().equals("") || !getText().equals(commitedContents))){
			sendValue = true;
			
			if(!getText().equalsIgnoreCase(commitedContents) && bugObject.isVisible()){
				changes.firePropertyChange("bugMessage", "removeBugMessage", "");
				bugObject.setValue("");
			}
/*
			if( !messageJustSet){
				changes.firePropertyChange("bugMessage", "removeBugMessage", "");
				bugObject.setValue("");
			}
			else
*/
				repaint();
		}
		if(!locked() && fontWasItalic){
			Font f = getFont();
			setFont(new Font(f.getName(), Font.ITALIC, f.getSize()));
			fontWasItalic = false;
		}
		super.focusLost(evt);
		messageJustSet = false;
		} catch (NullPointerException e) { }
	}
	
	public void askHint(){ }
	
		
	public Point getBugPointPosition(){
		Rectangle r = getBounds();
 		int x = r.x+r.width;
 		int y = r.y+4;
 		return new Point(x, y);
 	}
		
	protected void drawBugObject(Graphics g){
		Color c = g.getColor();
		Rectangle r = getBounds();
		int dotx, doty;
		Polygon p;
		int ww = 8;
		int hh = 8;
		int delta = 0;
		
		if (getHasBounds()) delta =1;
		if (r.height > 40) {
			ww = 11;
			hh =11;
		} else 
			if (r.height > 30) {
			  ww = 9;
			  hh =9;
		  }
				
		if(bugActive) {
			g.setColor(Settings.bugMessageBackground);
			dotx = r.width-8;
			doty = 2;

			g.fillOval(dotx, doty, 6, 6);
			g.setColor(Color.black);
			g.drawOval(dotx, doty, 6, 6);
			g.drawOval(dotx, doty, 5, 5);
		}	
		else{ // right top triangle
			g.setColor(Color.red);
			p = new Polygon();
			p.addPoint(r.width-ww, delta);
			p.addPoint(r.width-delta, delta);
			p.addPoint(r.width-delta, hh);
			g.fillPolygon(p);
		}		
		g.setColor(Color.red);
			// right bottom
		p = new Polygon();
		p.addPoint(r.width-ww, r.height-delta);
		p.addPoint(r.width-delta, r.height-delta);
		p.addPoint(r.width-delta, r.height-hh);
		g.fillPolygon(p);
			// left top
		p = new Polygon();
		p.addPoint(ww, delta);
		p.addPoint(delta, delta);
		p.addPoint(delta, hh);
		g.fillPolygon(p);
			// left bottom
		p = new Polygon();
		p.addPoint(delta, r.height-delta);
		p.addPoint(ww, r.height-delta);
		p.addPoint(delta, r.height-hh);
		g.fillPolygon(p);
		
		g.setColor(c);	
	}
	
	/*	
		Polygon [] pp = new Polygon[3];
		int mov_x = 0;
		int mov_y = 0;
		
		for(int i=0; i<3; i++){
			pp[i] = new Polygon();
			pp[i].addPoint(wid+1+mov_x, mov_y);
			pp[i].addPoint(1+mov_x, mov_y);
			if(i ==2)
				pp[i].addPoint(r.width, hh+mov_y);
			else
				pp[i].addPoint(1+mov_x, hh+mov_y);
			if(i == 0){
				mov_y = r.height;
				hh = (-1)*hh;
			}
			if(i ==1)
				mov_x = r.width-wid-2;
			g.fillPolygon(pp[i]);
		}		
		pp= null;
		*/	

}