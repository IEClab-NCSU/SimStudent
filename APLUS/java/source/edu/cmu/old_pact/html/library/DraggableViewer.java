package edu.cmu.old_pact.html.library;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.net.URL;

import edu.cmu.old_pact.dragdrop.DragWindow;

public class DraggableViewer extends HtmlViewer {
	private DragWindow window;
	
	public DraggableViewer(){
		super();
		this.enableEvents(	AWTEvent.MOUSE_EVENT_MASK|
        					AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
	
	public DraggableViewer(Window par) {
  		super(par);
  	}
  	
  	public DraggableViewer(String toshow, Window par) {
  		super(toshow, par);
  	}
  	
  	public DraggableViewer(URL url){
  		super(url);
  	}
	
	public DragWindow getView(){
    	if( window != null) return window;
    	
    	Component parent = getParent();
		while(!(parent instanceof DragWindow)){
			parent = parent.getParent();
		}
		window = (DragWindow)parent;
		return window;
	}	
    
    /**
    * Resend all mouseEvents to the DragWindow
    **/
    public void processMouseEvent(MouseEvent evt){
    	if(evt.getID() == MouseEvent.MOUSE_PRESSED)
    		getView().mousePressed(evt);
    	else if(evt.getID() == MouseEvent.MOUSE_RELEASED){
    		getView().mouseReleased(evt);
    	}
    }
    
    public void processMouseMotionEvent(MouseEvent evt){
    	if(evt.getID() == MouseEvent.MOUSE_DRAGGED){
    		getView().mouseDragged(evt);
    	}
    }
}