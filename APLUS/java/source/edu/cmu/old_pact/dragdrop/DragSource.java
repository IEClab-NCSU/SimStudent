package edu.cmu.old_pact.dragdrop;

import java.awt.event.MouseEvent;
import java.util.Vector;

public interface DragSource{

	public void mousePressed(MouseEvent evt);
	
	public void mouseDragged(MouseEvent evt);
	
	public DragSource getSource();
	
	public void startDragSession();
	
	public void dragCompleted(boolean completed);	
	
	public Draggable getDragObject();
	public Vector getDragObjects();
	
	public void performMouseUp(int x, int y);
	
}
	