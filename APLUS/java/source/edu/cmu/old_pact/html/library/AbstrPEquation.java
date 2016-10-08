package edu.cmu.old_pact.html.library;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import webeq3.app.Handler;
import webeq3.app.PEquation;

public class AbstrPEquation extends PEquation {
  Image my_face = null;
  Graphics my_graphics = null;
  
	public AbstrPEquation(Handler h) {
		super(h);
		// doesn't work with Alg2 java tutor
		//setSize(200,60);

	}
	
	public void redraw () {
		createPeImage();
	}
		
	public void createPeImage() {
	 	int w = 0;
	 	int h = 0;
	 	int descent = 0;
	 if (handler.getComponent()!=null) {	
    	try {
      		root.layout();
    	}
    	catch (Exception e) {
      		System.out.println("layout errors -- rendering failed\n" + e);
      		e.printStackTrace();
    	}
    	root.setLeft(2);
    	w = root.getWidth();
    	h = root.getHeight();  
    						
    	my_face = handler.getComponent().createImage(w, h);  
    	if(my_face == null)
    		return;
    	my_graphics = my_face.getGraphics();
    		//System.out.println("from createPeImage: w=" + w +" h=" + h);
    	
    	my_graphics.setColor(handler.getBackground()); 
    	my_graphics.fillRect(0, 0, w,h); //size().width, size().height);
    	my_graphics.setColor(Color.black); 
    	 
// System.out.println("WebEqImage: w="+w+" h="+h+"  descent="+root.getDescent());
    	
    	if(root.getDescent() <=4) 
    	  descent = 3;
    	 
    	try {
//      		root.offsetx = 0;
//      		root.offsety = 0;
      		root.paint(my_graphics, 0,0);//descent); // actual char drawing happens here   
 
    	} catch (Exception e) {
      	    e.printStackTrace(); }
      	    
		my_graphics.dispose();
       }
	}


	public Image getImage() {
    	return my_face;
  	}

}