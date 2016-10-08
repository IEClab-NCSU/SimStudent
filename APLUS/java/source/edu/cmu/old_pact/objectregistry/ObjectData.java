package edu.cmu.old_pact.objectregistry;

import java.awt.Dimension;
import java.awt.Point;

 public class ObjectData {
 	 // now only fontSizeInd is used
	int fontSizeInd = 1;
	Dimension windowSize = null;
	Point windowLocation = null;

 // DO WE WANT TO RESTORE WINDOW SIZE AND LOCATION???
	
	public ObjectData () {
	}
	 	
	public ObjectData (int fs, Dimension d, Point loc) {
	 	 fontSizeInd = fs;
	 	 windowSize = d;
	 	 windowLocation = loc;
	}
	 	
	public int getFontSizeInd () {
	   return fontSizeInd;
	}
	
	public Dimension getWindowSize() {
	 	return windowSize;
	}
	
	public Point getWindowLocation() {
	   return windowLocation;
	}
}
		