package edu.cmu.old_pact.bugtipdisplay;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.ToolPointerVector;
import edu.cmu.old_pact.cmu.messageInterface.UserMessage;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.Utilities.trace;


public class BugObject implements PropertyChangeListener, ComponentListener {
	private String imageBase;
	private String title;
	private String urlBase;
	private String value = "";
	private UserMessage[] userMessage = null;
	private boolean visible = false;
	BugDisplayable otf;
	private ToolTipWindow tipWindow = null;
	private boolean stayHidden=false;
	private Frame ourFrame;
	
	public BugObject(BugDisplayable otf){
		this.otf = otf;
	}
	
	public  void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equalsIgnoreCase("SHOWTIP"))//apowers edited from MOUSEENTERED
			showTip();
		
		if(evt.getPropertyName().equalsIgnoreCase("HIDETIP"))//apowers edited from MOUSEEXITED
			hideTip();
		
		if(evt.getPropertyName().equalsIgnoreCase("KEYPRESSED"))
			destroyTip();
		if(evt.getPropertyName().equalsIgnoreCase("DESTROY"))
			otf = null;
	
	}
	
	public void setMessage(UserMessage[] userMessage, String imageBase){
   		this.imageBase = imageBase;
   		this.title = userMessage[0].getTitle();
   		this.userMessage = userMessage;
   		setValue(userMessage[0].getText());
   	}
   	
   	public UserMessage[] getUserMessage(){
   		return userMessage;
   	}
   	
   	public Dimension getSize(){
   		if(tipWindow != null)
   			return tipWindow.getSize();
   		return new Dimension(0,0);
   	}
   	
   	public Rectangle getBounds() {
   		return tipWindow.getBounds();
   	}
   	
   	public void setLocation(Point p){
   		if(tipWindow != null && (tipWindow.getLocation().x != p.x || tipWindow.getLocation().y != p.y))
   			tipWindow.setLocation(p.x, p.y);
   	}
   	
   	public void storeMessage(MessageObject inEvent){
   		String image = "";
		try{
			urlBase = inEvent.extractStrValue("Image");
		} catch (DorminException e) { }
		String title = "";
		try{
			title = inEvent.extractStrValue("Title");
		} catch (DorminException e) { }
		Vector pointers = null;
		try{
			pointers = inEvent.extractListValue("Pointers");
		} catch (DorminException e) { }
		try{
			Vector mes = inEvent.extractListValue("Message");
			String thisBase = imageBase;
			if(urlBase != "")
				thisBase = imageBase+urlBase;
			ToolPointerVector pV = new ToolPointerVector(mes,pointers,title,thisBase);
			UserMessage[] userMessage = pV.getUserMessages();
			setMessage(userMessage, thisBase);
		}
		catch (DorminException e) { 
			trace.out("BugObject storeMessage "+e.toString());
		}
	}	
	
	public void setValue(String s){
		value = s;
		if(!value.equals(""))
			visible = true;
		else {
			visible = false;
			hideTip();
		}
		otf.repaint();
	}
	
	public String getValue(){
		return value;
	}
	
	protected void showTip(){
		if(visible && !value.equals("") && !stayHidden){
			otf.setBugActive(true);
			otf.repaint();
			
			if(tipWindow == null)
			{
				tipWindow = new ToolTipWindow(otf.getFrame(), getPointPosition(), imageBase);

			}
   			tipWindow.setLocation(getPointPosition());
   			tipWindow.presentMessages(userMessage[0], title);
   			setTipLocation();
		}
	}
	
	//apowers added setTipLocation to make tooltips not go off the right edge of the screen 
	// when the window is too close to it.
	protected void setTipLocation()
	{
		if (ourFrame==null)
		{
			ourFrame = otf.getFrame();
			ourFrame.addComponentListener(this);
		}
   		Rectangle otfBounds = otf.getBounds();
       	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Point otfGlobalPoint = otf.getLocationOnScreen();
		Rectangle tipBounds = tipWindow.getBounds();
		Point p = getPointPosition();
		tipBounds.x=p.x;
		tipBounds.y=p.y;
		if (tipBounds.x+tipBounds.width>screenSize.width)
		{  // if it is at the right edge of the screen...
			tipBounds.x=otfGlobalPoint.x+1;
			if (tipBounds.x+tipBounds.width>screenSize.width)
				tipBounds.x=screenSize.width-tipBounds.width - 1;
			
			tipWindow.setLocation(tipBounds.x,(otfGlobalPoint.y+otfBounds.height));
		}
		else if (tipBounds.y+tipBounds.height>screenSize.height)
		{  // if it is at the bottom edge of the screen...
			tipBounds.y=otfGlobalPoint.y+1;
			if (tipBounds.y+tipBounds.height>screenSize.height)
				tipBounds.y=screenSize.height-tipBounds.height - 1;
			
			tipWindow.setLocation(tipBounds.x,tipBounds.y);

		}
		else
			tipWindow.setLocation(tipBounds.x,tipBounds.y);

	}
	

    public synchronized void focusGained(FocusEvent evt) {
    	hideTip();
    }


	//apowers added all of these for the purposes of the tooltip when its frame moves.
	public void componentResized(ComponentEvent e)
	{
		if (tipWindow!=null && tipWindow.isVisible())
			setTipLocation();
	}
	public void componentMoved(ComponentEvent e)
	{
		if (tipWindow!=null && tipWindow.isVisible())
			setTipLocation();
	}
	public void componentShown(ComponentEvent e){}
	public void componentHidden(ComponentEvent e){}
	//end apowers' additions for tooltips
	
	private Point getPointPosition(){
		Point screenLoc = otf.getLocationOnScreen();
		Point inGridLoc = otf.getBugPointPosition();
		Point toret = new Point(screenLoc.x+inGridLoc.x, screenLoc.y+inGridLoc.y);
		return toret;
	}
	
	public void adjustLocation(){
		if(tipWindow != null)
			tipWindow.setLocation(getPointPosition());
	}
	
	public void setVisible(boolean b){
		visible = b;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	protected void  hideTip(){
		otf.setBugActive(false);
		otf.repaint();
		if(tipWindow != null)
			tipWindow.setVisible(false);
	}
	
	public void destroyTip(){
		synchronized (this){
			setVisible(false);
			hideTip();
			if(tipWindow != null){
				tipWindow.setVisible(false);
				tipWindow.dispose();
				tipWindow = null;
			}
		}
	}
}