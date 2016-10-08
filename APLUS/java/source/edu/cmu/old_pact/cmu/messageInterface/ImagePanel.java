package edu.cmu.old_pact.cmu.messageInterface;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.Toolkit;
import java.net.URL;

/**
 * A class that displays an image on it.
 */

public class ImagePanel extends Panel {
	/**
	* An image to be displayed.
	*/
    protected Image image = null;
    
	protected Image offscreen;
	/**
	* Used for measuring off-screen height.
	*/
	int delta = 0;

    /**
     * Internally used coordinates.
     */
    private int x, y, imageX = 0, imageY = 0;
    
    /**
     * Calculates image size only for the first entry. 
     */
    boolean firstEntry = true;

    /**
     * Constructs an ImagePanel.
     */
    public ImagePanel() {
        super();
        setBackground(getBackground());
    }

    /**
     * Sets and displays the specified image.
     * @param image - the image to be displayed.
     */
    public synchronized void setImage(String imageBase) {
    	clearPanel();
        this.image = loadImage(imageBase);
        repaint();        
    }
    
    /**
    * Loads a specified image.
    * @param imageBase - a path to the image.
    */
    protected Image loadImage(String imageBase) {
  		Image imag = null;
  		URL url = null;
	 	try{
	 		url = new URL(imageBase);
	 		imag = Toolkit.getDefaultToolkit().getImage(url);
        	waitForImage(this, imag);
   		} catch (Exception e) {
       	e.printStackTrace();
    	}
    	return imag;
  	}
  	/**
  	* Waits until the image is comlitely loaded.
  	* @param component - the component on which the image will be drawn.
  	* @param image - the image to be tracked.
  	*/
  	public static void waitForImage(Component component, Image image) {
    	MediaTracker tracker = new MediaTracker(component);
    	try {
      	tracker.addImage(image, 0);
      	tracker.waitForID(0);
    	} 	catch(InterruptedException e) { }
  	}

    /**
     * Clears the ImagePanel.
     */
    public void clearPanel() {
    	if(this.image != null && this.image.getWidth(this)>0) {
       		Graphics g = this.getGraphics();
        	g.clearRect(0, 0, size().width, size().height);
        }
    }

    public void update(Graphics g) {
        paint(g);
    }
    
    public void paint(Graphics g) {
    	super.paint(g);

    	if (image != null) {
            x = (size().width - image.getWidth(this)) / 2;
  			y = size().height/2 - image.getHeight(this)/2;
   			//if( size().width < image.getWidth(this)+20)
     			//this.setSize(image.getWidth(this)+20, size().height);   
            g.drawImage(image, Math.max(0, x), y, this);
        }
    }
    
    public Dimension preferredSize() {
    	int _x = 0, _y = 0;
    	if(firstEntry) {
    		firstEntry = false;
    		imageX = image.getWidth(this);
    		imageY = image.getHeight(this);
    	}
    	if(imageX > 0) {
    		_x = imageX + 20;
    		_y = imageY + 20;
    	}
    	return new Dimension(_x, _y);
    }

    public void layout() {
        clearPanel();
        super.layout();
    }
}

