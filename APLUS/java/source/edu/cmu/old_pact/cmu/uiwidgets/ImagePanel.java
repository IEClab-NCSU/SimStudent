package edu.cmu.old_pact.cmu.uiwidgets;

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
     * Internally used size and location.
     */
    private int width=20, height=20, _x=0, _y=0;
    

    /**
     * Constructs an ImagePanel.
     */
    public ImagePanel() {
        super();
        setBackground(getBackground());
    }
    
     /**
     * Constructs an ImagePanel.
     */
    public ImagePanel(int w, int h) {
       this();
       width = w;
       height = h;
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
    
    public synchronized void setImage(Image image){
    	clearPanel();
        this.image = image;
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
        	image = null;
        }
    }

    public void update(Graphics g) {
        paint(g);
    }
    
    public void paint(Graphics g) {
    	super.paint(g);
    	if(image != null) 
    		g.drawImage(image, 0, 0, width-1, height-1, this);
	}
	
	public void reshape(int x, int y, int w, int h){
		super.reshape(_x, _y, width, height);
	}
	
	/**
     * Sets the position of ImagePanel
     * Workaround the reshape problem if used in StackLayout.
     */
	public void setPosition(int x, int y){
		_x = x;
		_y = y;
		super.setLocation(_x, _y);
	}
    
    public Dimension preferredSize() {
    	return new Dimension(width, height);
    }
}

