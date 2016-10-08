package edu.cmu.old_pact.toolframe;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.settings.Settings;
import edu.cmu.pact.Utilities.trace;

public class ImageButton extends Canvas implements Runnable{
	String m_Name = "";
	Point m_MousePos=new Point(0,0);
	Image m_EnabledImage=null;
	Image m_DisabledImage=null;
	public boolean m_bPressed=false;
	private boolean m_bKeep = false;// used for keeping button pressed without sending event out.
	private boolean keepPressed = false;
	Thread m_ToolTipThread=null;
	private boolean showTip = true;
	private Vector actionListeners;
	private FastProBeansSupport changes = new FastProBeansSupport(this);
	protected static int delay = 1000;
	

	public ImageButton(String ImageFile,String name, boolean showTip, boolean keepPressed)
	{
		super();
		trace.out (10, "toolframe.ImageButton", "constructor: imageFile = " + ImageFile + " name = " + name);
	
		m_Name=name;
		setImage(ImageFile);
		actionListeners = new Vector();
		this.enableEvents(	AWTEvent.MOUSE_EVENT_MASK |
							AWTEvent.MOUSE_MOTION_EVENT_MASK );
		this.showTip = showTip;
		this.keepPressed = keepPressed;
	}
	public ImageButton(String ImageFile,String name, boolean showTip) {
		this(ImageFile,name,showTip,false);
		trace.out (10, "toolframe.ImageButton", "constructor: imageFile = " + ImageFile + " name = " + name);
	}
	
	public ImageButton()
	{
		super();
		actionListeners = new Vector();
		this.enableEvents(	AWTEvent.MOUSE_EVENT_MASK |
							AWTEvent.MOUSE_MOTION_EVENT_MASK );
		trace.out (10, "toolframe.ImageButton", "constructor");
		System.out.println ("MMMMM");
	}
	
	public static void setDelay (int d) {
		delay = d;
	}
	
	public static int getDelay () {
		return delay;
	}
	
	public void setKeepPressed(boolean k){
		keepPressed = k;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		changes.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		changes.removePropertyChangeListener(listener);
	}
	
	public void setTipText(String text){
		m_Name = text;
	}
		
	public void setShowTip(boolean b){
		showTip = b;
	}
	
	public ImageButton(String ImageFile,String name){
		this(ImageFile, name, true);
	}
	
	public void setShowToolTip(boolean s){
		showTip = s;
	}
		
	public void addActionListener(ActionListener al){
		actionListeners.addElement(al);
	}
	
	public void removeActionListener(ActionListener al){
		actionListeners.removeElement(al);
	}
	
	public void clear(){
		if (actionListeners != null)
			actionListeners.removeAllElements();
		actionListeners = null;
		changes = null;
		m_ToolTipThread=null;
	}
	
	public String getName(){
		return m_Name;
	}
	
	public String getToolTipText()
	{
		return m_Name;
	}	
	
	public Point getMousePosition()
	{
		return m_MousePos;
	}	
	
	public Point getLocationInFrame()
	{
		Point location=new Point(0,0);
		Component item = this;
		Component parent=getParent();
		
		do {
			location.x+=item.location().x;
			location.y+=item.location().y;
			item=parent;
			parent=item.getParent();
		} while (parent!=null);
		return location;
	}
	
	public Dimension preferredSize()
	{
		return size();
	}
	
	public void sendActionEvent(String command){
		try{
		ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command); 
		int s = actionListeners.size();
		for(int i=0; i<s; i++) {
			try {
     			((ActionListener) actionListeners.elementAt(i)).actionPerformed(evt);
    		} catch (Exception ex) {
      			System.out.println("toolframe.ImageButton: Exception occured during event dispatch");
      			ex.printStackTrace();
    		}
  		}
  		}catch (NullPointerException e) { }
  	}
	

	public void setImage(String ImageFile)
	{
		m_EnabledImage=null;
		m_DisabledImage=null;
		//m_EnabledImage = Toolkit.getDefaultToolkit().getImage(ImageFile);
		m_EnabledImage = Settings.loadImage(this, ImageFile);
		if (m_EnabledImage==null)
			System.out.println("Image "+ImageFile+" not found"); 
		ImageFilter intensityFilter=new IntensityFilter();
		m_DisabledImage=Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
			m_EnabledImage.getSource(),intensityFilter));
		
	}
	
	public void shadow1(Graphics g,Rectangle rect,Color bright,Color dark)
	{
		Color upper_left=null;
		Color lower_right=null;
	
		//if (m_bPressed) {
		if(m_bPressed || m_bKeep){
			upper_left=dark;
			lower_right=bright;
		} else {	
			upper_left=bright;
			lower_right=dark;
		}
	
		g.setColor(upper_left);
		g.drawLine(rect.x,rect.y+rect.height,rect.x,rect.y);
		g.drawLine(rect.x,rect.y,rect.x+rect.width,rect.y);
		g.setColor(lower_right);
		g.drawLine(rect.x+rect.width,rect.y,rect.x+rect.width,rect.y+rect.height);
		g.drawLine(rect.x+rect.width,rect.y+rect.height,rect.x,rect.y+rect.height);
	}

	public void shadow(Graphics g)
	{
		Color bright=getBackground().brighter();
		Color dark=getBackground().darker();
		Rectangle rect=new Rectangle(0,0,size().width-1,size().height-1);
		shadow1(g,rect,Settings.imageShadowTop,Settings.imageShadowBottom);
		rect.grow(-1,-1);
		shadow1(g,rect,getBackground(),getBackground().darker());
	}	
		
	
	
	public void paint(Graphics g)
	{
		if(m_EnabledImage != null){
			Dimension dim=size();
			Color old_color=g.getColor();
			int xPos,yPos,offset;
			Image image;
		
		
			//offset=(m_bPressed) ? 1 : 0;
			//offset=(m_bKeep) ? 1 : 0;
			offset=(m_bPressed || m_bKeep) ? 1 : 0;
			image=(isEnabled()) ? m_EnabledImage : m_DisabledImage;
			xPos=dim.width/2-image.getWidth(this)/2+offset-2;
			yPos=dim.height/2-image.getHeight(this)/2+offset-2;
		
			g.setColor(getBackground());
			g.fillRect(0,0,dim.width,dim.height);
		//g.drawImage(image,xPos,yPos,dim.width-2,dim.height-2,this);
			g.drawImage(image,offset,offset,dim.width,dim.height,this);
			shadow(g);
		
			g.setColor(old_color);
		}
	}	
	
	private void startToolTip()
	{
		if(showTip){
			if (m_ToolTipThread!=null && m_ToolTipThread.isAlive()) {
				m_ToolTipThread.stop();
				m_ToolTipThread=null;
			}	
			m_ToolTipThread=new Thread(this);
			m_ToolTipThread.start();
		}
	}
	
	private void stopToolTip()
	{
		if(showTip){
		if (m_ToolTipThread!=null && m_ToolTipThread.isAlive()) {
			m_ToolTipThread.stop();
			m_ToolTipThread=null;
		}	
		sendActionEvent("Hide ToolTip");
		}
	}
	
    public boolean gotFocus (Event evt, Object what) 
    {
   		sendActionEvent("itemGotFocus");
 		super.gotFocus(evt,what);
    	return true;
    } 	
	
	public void processMouseEvent(MouseEvent evt){
		if (evt.getID() == MouseEvent.MOUSE_PRESSED)
			mousePressed(evt);
		else if (evt.getID() == MouseEvent.MOUSE_RELEASED)
			mouseReleased(evt);
		else if(evt.getID() == MouseEvent.MOUSE_EXITED)
			mouseExited(evt);
		else if(evt.getID() == MouseEvent.MOUSE_ENTERED){
			mouseEntered(evt);
		}
		else if(evt.getID() == MouseEvent.MOUSE_MOVED) {
			mouseMoved(evt);
		}
		else
			super.processMouseEvent(evt);
	}
	
	public void mouseMoved(MouseEvent evt){
		m_MousePos.x = evt.getX();
		m_MousePos.y = evt.getY();
	}
	
	public void mouseEntered(MouseEvent evt){
		startToolTip();
	}
	
	public void mouseExited(MouseEvent evt){
		stopToolTip();
	}
	
	public void mousePressed(MouseEvent evt){
		if (isEnabled()) {
			m_bPressed=true;
			stopToolTip();
			if(keepPressed)
				m_bKeep = !m_bKeep;
			repaint();
		}
	}
		
	public boolean isKept(){
		return m_bKeep;
	}
	
	public void setPressed(boolean pressed){
		m_bPressed = pressed; 
		if(keepPressed)
			m_bKeep = pressed;
		repaint();
	}
	
	public boolean getPressed(){
		return m_bPressed;
	}
	
	public void mouseReleased(MouseEvent evt){
		if (isEnabled()) {
			//if (m_bPressed && !m_bKeep)
			if (m_bPressed)
				sendActionEvent(m_Name);
			m_bPressed=false;					
			repaint();
			changes.firePropertyChange("Pressed", null, this);
		}
	}

	public void run() {
	    try { 
	    	Thread.sleep(delay); 
	    } catch (InterruptedException e) { }
	    sendActionEvent("Show ToolTip");
		m_ToolTipThread=null;
	}	
}	



