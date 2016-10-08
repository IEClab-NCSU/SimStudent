
package edu.cmu.old_pact.dragdrop;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Vector;

import edu.cmu.old_pact.dragregistry.DragRegistry;

public class DragSession implements Runnable{
	private DragSource source;
    private DragDestination destination;
    private Thread ownThread;
    
    private Vector dragWindows;
    private boolean waitForResponse = false;

    
    public DragSession(DragSource source, 
                       boolean hasDragWindow) {
        this.source = source;

        if (hasDragWindow){
            dragWindows = source.getDragObjects();
            int s = dragWindows.size();
            for(int i=0; i<s; i++)
            	((Draggable)dragWindows.elementAt(i)).setDragSession(this);
        }
    }
    
    public void run(){
		long timeMark = System.currentTimeMillis();
		while((System.currentTimeMillis() - timeMark) < 5000){
			try{
				Thread.sleep(500);
			} catch (InterruptedException e) { 
	e.printStackTrace();
				endSession(true);
			}
		}
		endSession(true);
	}

    public DragSession(DragSource source){
        this(source, true);
    }

    public void setWaitForResponse(boolean b){
    	waitForResponse = b;
    }
 
    public DragSource getSource() {
        return source;
    }

    
    public void mouseDragged(MouseEvent event) {
        DragDestination currDest = null;
        
        Point p = getAbsMousePos(event.getX(), event.getY());
        
        //Object obj = DragRegistry.knownObjects.getObjectAtLocation(event.getX(),event.getY());
        Object obj = DragRegistry.getObjectAtLocation(p.x, p.y);
        if(obj instanceof DragDestination)
        	currDest = (DragDestination)obj;
        if (destination == null && currDest != null) {
            destination = currDest;
            destination.dragEntered(this);
        } else if (destination != null && currDest == null) {
            destination.dragExited(this);
            destination = null;
        } else if (destination != currDest) {
            destination.dragExited(this);
            destination = currDest;
            destination.dragEntered(this);
        } else if (destination != null) {
            destination.dragMoved(this);
        }
    }
    
    protected Point getAbsMousePos(int x, int y){
    	Point viewPoint = ((Draggable)dragWindows.elementAt(0)).getLocationOnScreen();
    	return viewPoint;
    }
    
    public Object[] getDraggedData(){
    	if(dragWindows == null) return null;
    	int s = dragWindows.size();
    	Object[] obj = new Object[s];
    	for(int i=0;i<s; i++)
    		obj[i] = ((Draggable)dragWindows.elementAt(i)).getData();
    	return obj;
    }

    public void mouseReleased(MouseEvent event) {
        boolean accepted = false;
        boolean goBack = false;
        if(ownThread != null && ownThread.isAlive()){
			//ownThread.stop();
			ownThread = null;
		}
		
		ownThread = new Thread(this);
		ownThread.start();

        try {
			if(destination == null && waitForResponse){
				endSession(true);
				return;
			}
            if (destination != null) {
                accepted = destination.dragDropped(this);
            }
			if( !waitForResponse){
            	if (accepted) 
                	source.dragCompleted(true);
            	else 
                	goBack = true;
            }
            
        } finally {
        	if( !waitForResponse)
        		endSession(goBack);
        }
    }
    
    public void endSession(boolean goBack){
    	if (dragWindows != null) {
            int s = dragWindows.size();
            if (goBack) {
                for(int i=0; i<s; i++)
                    ((Draggable)dragWindows.elementAt(i)).backToSource();
                source.dragCompleted(false);
            } else {
                for(int i=0; i<s; i++)
                    ((Draggable)dragWindows.elementAt(i)).endDrag();
                source.dragCompleted(true);
            }
        }
        dragWindows = null;
        source = null;
        destination = null;

if(ownThread != null && ownThread.isAlive()){
ownThread.stop();
ownThread = null;
}

    }
    
    public Vector getDragWindows(){
    	return dragWindows;
    }	
    
    

    public DragDestination getDestination() {
        return destination;
    }
}
