package edu.cmu.old_pact.doublebufferedpanel;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

public class DoubleBufferedPanel extends Panel{
	Image offscreen;
	
	public void invalidate(){
		super.invalidate();
		offscreen = null;
	}
	
	public void update(Graphics g){
		paint(g);
	}
	
	public void paint(Graphics g){
		if(offscreen == null)
			offscreen = createImage(getSize().width, getSize().height);
		Graphics gr = offscreen.getGraphics();
		gr.setClip(0,0,getSize().width,getSize().height);
		super.paint(gr);
		try{
			g.drawImage(offscreen,0,0,null);
		} catch (NullPointerException e) { }
		gr.dispose();
	}
}	