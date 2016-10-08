package edu.cmu.old_pact.toolframe;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class LineCanvas extends Canvas{
	private int width = 45;
	private boolean isResizable = false;
	
	public LineCanvas(boolean isResizable){
		super();
		this.isResizable = isResizable;
	}
	
	public LineCanvas(){
		this(false);
	}
	
	public LineCanvas(int w){
		this(true);
		width = w;
	}

	public Dimension preferredSize(){
		if(!isResizable || getParent() == null)
			return new Dimension(width,2);
		else {
			width = getParent().getSize().width;
			return new Dimension(width,2);
		}
	}

	public void paint(Graphics g){
		super.paint(g);
		Dimension size = preferredSize();
		g.setColor(Color.black);
		g.drawLine(0,1,size.width-1,1);
	}
}