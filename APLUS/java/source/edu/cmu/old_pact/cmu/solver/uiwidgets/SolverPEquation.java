package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Graphics;
import java.awt.Image;

import webeq3.app.Handler;
import webeq3.app.PEquation;

public class SolverPEquation extends PEquation {
  Image my_face = null;
  Graphics my_graphics = null;
  
	public SolverPEquation(Handler h) {
		super(h);
		resize(500,60);
	}
	
	public void redraw () {
		createPeImage();
	}
			
	public void createPeImage() { 
	 	int w = 0;
	 	int h = 0;
	 	
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
    	my_graphics = my_face.getGraphics();
    	
// System.out.println("from createPeImage: w=" + w +" h=" + h);
    	
    	my_graphics.setColor(handler.getBackground()); 
    	my_graphics.fillRect(0, 0, w,h); //size().width, size().height);
    	//my_graphics.setColor(Color.black); 
    	 
    	try {
//      		root.offsetx = 0;
//      		root.offsety = 0;
      		root.paint(my_graphics, 0,0); // actual char drawing happens here   
  
    	} catch (Exception e) {
      	    e.printStackTrace(); }
      	    
		my_graphics.dispose();
       }
	}


	public void paint(Graphics g) {
    	
      if (my_face != null) {          
      	int w = my_face.getWidth(this);
      	int h = my_face.getHeight(this);
      	
      	if ((getSize().width < w) || (getSize().height < h))
      	   resize(w,h);
        g.drawImage(my_face, 0, 0, w, h, handler.getImageObserver());        
    	g.dispose();
      }   
    }
 
	public Image getImage() {
    	return my_face;
  	}

}