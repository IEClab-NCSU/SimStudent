//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/TriangleCanvas.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

public class TriangleCanvas extends Canvas{
	private Polygon triangle;
	Font trFont; 
	
	public TriangleCanvas(){
		super();
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			trFont = new Font("geneva", Font.BOLD, 12);
		else
			trFont = new Font("arial", Font.BOLD, 12);
			
		int[] x_pos = new int[]{0,5,10};
		int[] y_pos = new int[]{19,9,19};
		triangle = new Polygon(x_pos,y_pos,3);
	}
	
	public Dimension preferredSize(){
		return new Dimension(11,20);
	}
	
	public void paint(Graphics g){
		super.paint(g);
		Font curFont = g.getFont();
		g.setColor(Color.black);
		g.setFont(trFont);
		g.drawPolygon(triangle);
		g.setFont(curFont);
	}
}
		
	