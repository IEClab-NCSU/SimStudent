//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/TableViewer.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Component;
import java.awt.Dimension;

import edu.cmu.old_pact.scrollpanel.LightComponentScroller;


public class TableViewer extends LightComponentScroller{
	int width=10, height=10;
	
	public TableViewer(){
		super();
	}
	
	public TableViewer(Component c){
		super(c);
	}
	
	public Dimension preferredSize(){
		return new Dimension(width, height);
	}
	
	public void setSize(int w, int h){
		width = w; 
		height = h;
		super.setSize(w, h);
	}
}
		