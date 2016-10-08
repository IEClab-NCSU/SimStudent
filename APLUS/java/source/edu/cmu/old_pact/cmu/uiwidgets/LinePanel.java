package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;

public class LinePanel extends Panel {
	private int height;
	Color[] colors;
	public LinePanel(int h, Color[] colors){
		super();
		this.height = h+1;
		this.colors = colors;
	}
	
	public Dimension preferredSize(){
		return (new Dimension(getParent().getSize().width, height));
	}

	public void paint(Graphics g){
    	super.paint(g);
    	int width = getSize().width - 1;
    	int s = colors.length;
    	if(s == 0) return;
    	for(int i=0; i<s; i++){
    		g.setColor(colors[i]);
    		g.drawLine(0, i, width - 1, i);  // Topleft to topright
    	}
    }
}