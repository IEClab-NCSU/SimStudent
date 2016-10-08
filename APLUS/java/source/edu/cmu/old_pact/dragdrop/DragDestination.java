package edu.cmu.old_pact.dragdrop;

import java.awt.Component;

public interface DragDestination {

	public void dragEntered(DragSession session);
	
	public void dragMoved(DragSession session);
	
	public void dragExited(DragSession session);
	
	public boolean dragDropped(DragSession session);
	
	public Component getDestination();
	
	public void register();
}