package edu.cmu.old_pact.settings;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;

public class ImageCanvas extends Canvas {

	private Image image;
	public ImageCanvas (Image image) {
		MediaTracker mt = new MediaTracker (this);
		mt.addImage (image, 0);
		
		try { mt.waitForID(0); }
		catch (Exception e) { e.printStackTrace(); }
		
		this.image = image;
	}

	public void paint (Graphics g) {
		g.drawImage (image, 0, 0, this);
	}
	
	public void update (Graphics g) {
		paint (g);
	}
	
	public Dimension  getPreferredSize() {
		return new Dimension (image.getWidth (this), 
							  image.getHeight (this));
	}

}