/**
 *	DraggSourceViewer allows to create a DragView for the objects with 
 *  the <DRAG> tags.
 **/
package edu.cmu.old_pact.html.library;
import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Vector;

import edu.cmu.old_pact.dragdrop.DragSession;
import edu.cmu.old_pact.dragdrop.DragSource;
import edu.cmu.old_pact.dragdrop.Draggable;
import edu.cmu.old_pact.dragdrop.MouseUpSimulator;

public class DraggSourceViewer extends HtmlViewer implements DragSource {
	private Vector dragViews;
  	private DragSession session;
  	private Frame frame;
  	private boolean inDrag = false;
  	private MouseUpSimulator m_up;
  	
  	public DraggSourceViewer(Container par) {
  		super(par);
  		m_up = new MouseUpSimulator(this);
  	}
  	
  	public DraggSourceViewer(String toshow, Container par) {
  		super(toshow, par);
  		m_up = new MouseUpSimulator(this);
  	}
  	
  	public DraggSourceViewer(URL url){
  		super(url);
  		m_up = new MouseUpSimulator(this);
  	}

	public DraggSourceViewer() {
  		super();
  		m_up = new MouseUpSimulator(this);
  	}
  	
  	public void run() {
    	if (event.target == canvas && event.id == Event.MOUSE_DOWN) {
      		int x = event.x - canvas.location().x;
      		int y = event.y - canvas.location().y;
      	
      		DragObject toDrag = canvas.getSelectable(x,y);
           	if(toDrag != null && canvas.inDraggables(toDrag.text)) 
      			mousePressed(new MouseEvent(canvas, event.id, event.when, event.modifiers,
                     						event.x, event.y, event.clickCount, false));
      		else if(toDrag == null && !inDrag)
      			dragCompleted(true);
      		
     	}
     	else if (event.target == canvas && event.id == Event.MOUSE_UP) {
     		m_up.stopThread();
    		performMouseUp(event.x, event.y);
    	} 
    	else if (event.target == canvas && event.id == Event.MOUSE_DRAG) {
    		mouseDragged(new MouseEvent(canvas, event.id, event.when, event.modifiers,
                     						event.x, event.y, event.clickCount, false));
              // Can't catch MOUSE_UP event if mouse moves shaking and very quick.
             m_up.startThread(event.x, event.y);
		}	
		super.run();
  	}
  	
  	public void performMouseDown(int x, int y){
  		
  	}	
 
  	
  	public void performMouseUp(int x, int y){
  		x = x - canvas.location().x;
      	y = y - canvas.location().y;
     	DragObject toDrag = canvas.getSelectable(x,y);
   
  		boolean done = false;
 		if(session != null){
    		//((Draggable)dragViews.elementAt(0)).mouseReleased(new MouseEvent(canvas, event.id, event.when, event.modifiers,
                     						//event.x, event.y, event.clickCount, false));
             ((Draggable)dragViews.elementAt(0)).mouseReleased(new MouseEvent(canvas, 5, System.currentTimeMillis(),
             																 0, x, y, 1, false));
    		done = true;
    	}  
 
 		if(!done){	
      		if(toDrag != null && dragViews == null){
				canvas.addDraggable(toDrag);
				//changeDocument(canvas.getDocument());
				changeDocument(canvas.getDocument(), false);
			}
			else if(toDrag == null && !inDrag){
				dragCompleted(true);
			}
			
    	}
  	}
  	
  	public Frame getFrame(){
  		if(frame != null) return frame;
		Component parent = getParent();
		Component root = null;
		while(parent != null){
			root = parent;
			parent = parent.getParent();
		}
		frame = (Frame)root;
		return frame;
	}
  	
  	public void mousePressed(MouseEvent evt){
  		if(!inDrag){
  			inDrag = true;
  		prepareToDrag();
  		if(dragViews != null){
  			int s = dragViews.size();
  			for(int i=0; i<s; i++)
  				((Draggable)dragViews.elementAt(i)).mousePressed(evt);
  		}
  		}
  	}
  
  	public void prepareToDrag(){
  		int s = canvas.draggables.size();
  		if(s > 0){
  			DragObject drObj;
  			
  			dragViews = new Vector();
  			Point p = canvas.getLocationOnScreen();
  			Rectangle dragRect;
  			for(int i=0; i<s; i++){
  				drObj = (DragObject)canvas.draggables.elementAt(i);
  				//drObj.startY = canvas.heights[drObj.startLine]+canvas.start;
  				//drObj.endY = canvas.heights[drObj.endLine]+canvas.start;
  				drObj.startY = canvas.heights[drObj.startLine]-canvas.start;
  				drObj.endY = canvas.heights[drObj.endLine]-canvas.start;
  				drObj.lineWidth = canvas.width;
  			
  				dragRect = drObj.getBounds();
  				HtmlDragWindow dragWindow = new HtmlDragWindow(getFrame(), p.x+dragRect.x,p.y+dragRect.y);
  				dragViews.addElement(dragWindow);
//  				dragWindow.setWidth(dragRect.width);
//  				dragWindow.setHeight(dragRect.height);
  				((HtmlDragWindow)dragWindow).setUrl(canvas.getUrl());
  				((HtmlDragWindow)dragWindow).setFontSize(drObj.font.getSize());
				((HtmlDragWindow)dragWindow).setDraggable(drObj);
				dragWindow.setData(drObj.text);
			}
			startDragSession();
			for(int i=0; i<s; i++)
				((HtmlDragWindow)dragViews.elementAt(i)).setVisible(true);	
  		}
  	}
  
  	public synchronized void mouseDragged(MouseEvent evt) { 
  		if(dragViews != null) {
  			int s = dragViews.size();
  			for(int i=0; i<s; i++)
  				((Draggable)dragViews.elementAt(i)).mouseDragged(evt);
  		}
  	}
  
  	public DragSource getSource() {
  		return this;
  	}
  
  	public void startDragSession(){
  
  		session = new DragSession(this, true);
  		session.setWaitForResponse(true);
  	}
	
  	public void dragCompleted(boolean completed){
		if(completed){
			if(canvas.hasDraggables()){
				canvas.resetDraggables();
				//changeDocument(canvas.getDocument());
				changeDocument(canvas.getDocument(), false);
			}
		}
		if(dragViews != null)
			dragViews.removeAllElements();
		inDrag = false;
		dragViews = null;
		session = null;
		event = null;
  	}	
	
  	public Draggable getDragObject(){
  		return (Draggable)dragViews.elementAt(0);
  	}
  	
  	public Vector getDragObjects(){
  		return dragViews;
  	}
  }