/**
* DragWindow - the Draggable Window, which can hold objects of different types.
**/
package edu.cmu.old_pact.dragdrop;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;

public abstract class DragWindow extends Window implements Draggable, Runnable{
	private DragSession session;
	private Object data;
	private String dataType;
	private int startX, startY;
    public int mousePosX = 0, mousePosY = 0;
    
    private int width = 10;
    private int height = 10;
    
    private Thread own;
    
    public DragWindow(Frame parent, 
    						int startX, int startY,
    						int mousePosX, int mousePosY,
    						String dataType, Object data){
    	super(parent);
    	this.startX = startX;
        this.startY = startY;
        this.mousePosX = mousePosX;
        this.mousePosY = mousePosY;
        this.dataType = dataType;
        this.data = data;
        
        this.enableEvents(	AWTEvent.MOUSE_EVENT_MASK|
        					AWTEvent.MOUSE_MOTION_EVENT_MASK);
        
        pack();
        setLocation(startX, startY);
    }
    
    public DragWindow(Frame parent, int startX, int startY){
    	this(parent, startX, startY, 0,0, null, null);
    }
    
    
    
    public Dimension preferredSize(){
    	return new Dimension(width, height);
    }
    
    public void setWidth(int w){
    	width = w;
    	setSize(width, getSize().height);
    }
    
    public void setHeight(int h){
    	height = h;
    	setSize(getSize().width, height);
    }
   
    public void processMouseEvent(MouseEvent evt){
    	if(session != null){
    		if(evt.getID() == MouseEvent.MOUSE_PRESSED)
    			mousePressed(evt);
    		else if(evt.getID() == MouseEvent.MOUSE_RELEASED)
    			mouseReleased(evt);
    	}
    }
    
    public void processMouseMotionEvent(MouseEvent evt){
    	if(session != null){
    		if(evt.getID() == MouseEvent.MOUSE_DRAGGED){
    			mouseDragged(evt);
    		}
    	}
    }
    
    public void mousePressed(MouseEvent evt){
    	mousePosX = evt.getX();
    	mousePosY = evt.getY();
    }
    
    public void mouseReleased(MouseEvent evt){
    	session.mouseReleased(evt);
    }
    
    public void mouseDragged(MouseEvent evt){
    	setWindowLocation(evt.getX(), evt.getY());
    	session.mouseDragged(evt);
    }
    
    public void setWindowLocation(int x, int y){
   		int delta_x = x-mousePosX;
   		int delta_y = y-mousePosY;
   		synchronized (this){
    		Point loc = getLocation();
    		setLocation(loc.x+delta_x,loc.y+delta_y);
    	}
    }
    
    public void setVisible(boolean b){
    	super.setVisible(b);
    	if(b)
    		requestFocus();
    }
    
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
    public void setDragSession(DragSession session){
    	this.session = session;
    }
    
    public Component getView(){
    	return this;
    }
    
    public void endDrag(){
    	setVisible(false);
    	session = null;
    	data = null;
    	dispose();
    }
    
    public void backToSource(){
    	if(own == null || !own.isAlive()){
			own=new Thread(this);
			own.start();
		}
    }
    
    public void run() {
    	Point currLoc = getLocationOnScreen();
    	int curr_x = currLoc.x;
    	int curr_y = currLoc.y;
    	int count = 20;
		int increment_x = (curr_x-startX)/count;
		int increment_y = (curr_y-startY)/count;
		
		for(int i=0; i<count; i++) {
			try{
				Thread.sleep(15);
			} catch (java.lang.InterruptedException e ){
e.printStackTrace();
				own = null;
				endDrag();
			}
			currLoc= getLocationOnScreen(); 
			setLocation(currLoc.x-increment_x, currLoc.y-increment_y);
		}
		own = null;
		endDrag();
	}
    
  
    public DragSession getSession(){
    	return session;
    }
    
    public int getStartX(){
    	return startX;
    }
    
    public int getStartY(){
    	return startY;
   }
   
   public void mouseClicked(MouseEvent e){ }

   public void mouseEntered(MouseEvent e){ }

   public void mouseExited(MouseEvent e){ }
}