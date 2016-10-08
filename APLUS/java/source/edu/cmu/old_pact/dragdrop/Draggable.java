package edu.cmu.old_pact.dragdrop;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

public interface Draggable{
	
	public void setDragSession(DragSession session);
	
	public void mouseDragged(MouseEvent event);
	public void mousePressed(MouseEvent event);
	public void mouseReleased(MouseEvent event);
	
	public Component getView();
	
	public void endDrag();
	
	public void backToSource();
	
	public void setVisible(boolean b);
	
	public Object getData();
	public void setData(Object data);
	public String getDataType();
	public void setDataType(String dataType);
	
	public void setWidth(int w);
	public void setHeight(int h);
	
	public Point getLocationOnScreen();
}