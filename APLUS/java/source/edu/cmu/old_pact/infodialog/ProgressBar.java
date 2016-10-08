package edu.cmu.old_pact.infodialog;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

public class ProgressBar extends Canvas {

	private int canvasWidth, canvasHeight;
	private int x, y, width, height;
	private int barWidth = 20;
	private double percent;
	private int count;
	private boolean showBar;
	private boolean negativeProgress;
	
	private Color backgroundColor, frameBrighter, frameDarker;
	private Color barColor = Color.gray;
	private Color fillColor = new Color(204,204,255);


	ProgressBar(int canvasWidth, int canvasHeight, Color backgroundColor) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;
		this.backgroundColor = backgroundColor;
		frameBrighter = backgroundColor.brighter();
		frameDarker = backgroundColor.darker();
		x = 1;
		y = 5;
		width = canvasWidth - 8;
		height = canvasHeight - 7;
		showBar = false;
		negativeProgress = false;
		count = 0;
		percent = 0.0;
	}
	
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
    void fill3DRect(int x, int y, int width, int height) {
    	Graphics g = getGraphics();
    	if(g == null) return;
      try {
			
			g.setColor(fillColor);
			g.fillRect(x+1, y+1, width-2, height-2);
			g.setColor(frameDarker);
			g.drawLine(x, y, x, y+height);
			g.drawLine(x+1, y, x+width-1, y);
			g.setColor(frameBrighter);
			g.drawLine(x+1, y+height, x+width, y+height);
			g.drawLine(x+width, y, x+width, y+height-1);
			
	  } 
	  catch (NullPointerException e) { }
	 finally {g.dispose();}
	 
    }
    
    public void runIt(int numLoops) {
    	showBar = true;
    	setVisible(true);
    	getParent().repaint();
		while(showBar) {
		for(int j = 0; j<=numLoops; j++) {
			for (int i=0; i<=numLoops; i++) {
				wait(50);
				show(i/(double)numLoops);
			}
		}
		}
	} 
	
	public static void wait(int msecs) {
		try {Thread.sleep(msecs);}
		catch (InterruptedException e) { }
	}   


	public void show(double percent) {
	try{
		fill3DRect(x-1, y-1, width+1, height+1);
		showBar = true;

		negativeProgress = percent<this.percent;
		this.percent = percent;
		Graphics gr = getGraphics();
		if (gr == null)
			return;
		try{
    	if (percent>=1.0) {
			count = 0;
			percent = 0.0;
    		erase(gr);
			showBar = false;
			return;
    	}
		if (showBar)
			paint(gr);
		} finally {
			gr.dispose();
		}
	} catch (NullPointerException e) { }
	}


	public void erase(Graphics g) {
	  try {
			g.setColor(backgroundColor);
			g.fillRect(0, 0, canvasWidth, canvasHeight);
	  } catch (NullPointerException e) { }
	}


	public void update(Graphics g) {
		paint(g);
	}


   public void paint(Graphics g) {
     try {
    	if (!showBar)
    		return;
    	if (percent<0.0)
    		percent = 0.0;
    	int barEnd = (int)(width*percent);
    	int barStart = barEnd - barWidth;
		if (negativeProgress) {
			g.setColor(fillColor);
			g.fillRect(barEnd+2, y, width-barEnd, height);
		}
		else {
			if(barStart > x) {
				g.setColor(fillColor);
				g.fillRect(x, y, barStart+x, height);
			}
			
			g.setColor(barColor);
			g.fillRect(barStart+1, y, barWidth, height);
			
			g.setColor(fillColor);
			g.fillRect(barEnd+x, y,width-barEnd, height); 
		}	
	  } catch (NullPointerException e) { }
    }

    public Dimension preferredSize() {
        return new Dimension(canvasWidth, canvasHeight);
    }

}
