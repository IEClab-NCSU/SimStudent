package edu.cmu.old_pact.html.library;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.io.IOException;
import java.net.URL;

import edu.cmu.old_pact.dragdrop.DragWindow;

public class HtmlDragWindow extends DragWindow{
	private HtmlViewer htmlViewer;
	private URL base;
	private AutoWrapper autoWrap;
	private HtmlDragWindow[] connectedWindows;
	private int connSize = 0;
	protected DragObject dragObj;

	public HtmlDragWindow(Frame parent, int startX, int startY){
		super(parent, startX, startY);
		autoWrap = new AutoWrapper("title", "");
		autoWrap.setBGColor("#FFFFFF");
		
		htmlViewer = new HtmlViewer(this, false);
		htmlViewer.canvas.setXMargin(0);
		htmlViewer.canvas.setYMargin(0);
		setLayout(new BorderLayout());
		add("Center",htmlViewer);
		
	}
	
	public void setUrl(URL base){
		this.base = base;
		if(connSize != 0){
			for(int i=0; i<connSize; i++)
				connectedWindows[i].setUrl(base);
		}	
	}
	
	public void setVisible(boolean v){
		super.setVisible(v);
		if(connSize != 0){
			for(int i=0; i<connSize; i++)
				connectedWindows[i].setVisible(v);
		}	
	}
	
	public void addConnectedWindows(HtmlDragWindow[] w){
		connectedWindows = w;
		connSize = connectedWindows.length;
	}
	
	protected void setDraggable(DragObject draggable){
		htmlViewer.canvas.addDraggable(draggable);
		dragObj = draggable;
	}
	
	public void setFontSize(int s){
		autoWrap.setFontSize(s);
		if(connSize != 0){
			for(int i=0; i<connSize; i++)
				connectedWindows[i].setFontSize(s);
		}
	}
	
	public void setData(Object data){
		super.setData(data);
		String theData = "<DRAG>"+(String)data+"</DRAG>";
		setDataType("html");
		autoWrap.newBody(theData);
		try{
			//htmlViewer.changeDocument(new HtmlDocument(autoWrap.wrappedText(), base));
			htmlViewer.changeDocument(new HtmlDocument(autoWrap.wrappedText(), base), false);
		}catch (IOException e) { 
			System.out.println("HtmlDragWindow setData "+e.toString());
		}
	}
	
	public Object getData(){
		String toret = (String)super.getData();
		if(connSize != 0){
			for(int i=0; i<connSize; i++)
				toret = toret+connectedWindows[i].getData();
		}
		return toret;
	}
	
	public void setWindowLocation(int x, int y){
   		int delta_x = x-mousePosX;
   		int delta_y = y-mousePosY;
   		synchronized (this){
    		Point loc = getLocation();
    		setLocation(loc.x+delta_x,loc.y+delta_y);
   			htmlViewer.canvas.repaint();
    	}
    	mousePosX = x;
    	mousePosY = y;
    	if(connSize != 0){
			for(int i=0; i<connSize; i++)
				connectedWindows[i].setWindowLocation(x,y);
		}
    }
}