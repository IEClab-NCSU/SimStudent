package edu.cmu.old_pact.scrollpanel;

import java.awt.Component;
import java.awt.Dimension;

/**
 * This component has been adapted from a lightweight vertical scrollbar
 * component (BDDScrollbar.java from http://web.mit.edu/twm/www/swapware/)
 * and the gjt scrolling classes. Horizontal scrollbars have been added,
 * and to end up with a lightweight scroll pane which can be used with
 * Java 1.0. 
 *
 * Scrolls any component.  Component to be scrolled may be a
 * container, so ultimately many components may be scrolled
 * at once.<p>
 *
 * Component to be scrolled may be specified at construction
 * time, or may be set after construction via 
 * void setComponent(Component).<p>
 *
 * @author John Lehmann (johnl@axis.com.au)
 * @author Paul Bedworth (paul.bedworth@ed.ac.uk)
 * @author Tim Macinta
 * @author David Geary
 */
 
 /* Version history 
 *  23-02-1998 Adapted from LightComponentScroller by Paul Bedworth by John Lehmann
 *             Modified graphic display, added continuous scrolling
 *
 */

public class LightComponentScroller extends Scroller {
    private Component scrollMe;

    public LightComponentScroller() {
    }
    
    public LightComponentScroller(Component component) {
        setComponent(component);
    }
    
    public void setComponent(Component component) {
        scrollMe = component;
        viewport.setLayout(new BulletinLayout());
        viewport.add      (scrollMe);
        viewport.move     (0,0);
    }

    public void setComponent(Component component, int vertLines) {
    	setVertLines(vertLines);
    	setComponent(component);
    }
    
    public void scrollTo(int x, int y) {
        scrollMe.move(-x,-y);
    }
    public Dimension getScrollAreaSize() {
        return scrollMe.preferredSize();
    }
    public static LightComponentScroller getScroller(Component me) {
      Component p = me;
      do {
        p = p.getParent();
      } while (p != null && !(p instanceof LightComponentScroller));
      if (p == null) {
        System.err.println("Expected component to be managed by a LightComponentScroller");
      }
      return (LightComponentScroller)p;
    }
    
    public void removeAll(){
    	if(scrollMe != null)
    		remove(scrollMe);
    	super.removeAll();
    }
    
    // 3/15/2001 Irene - added this method   
    public void scrollToYpos(int y)
	{
		LightComponentScroller scroller=LightComponentScroller.getScroller(scrollMe);
		if (scroller!=null) {
			int max=scroller.getVerticalScrollbar().getMaximum();
			int val = y * max / scroller.getSize().height;						
			scroller.getVerticalScrollbar().setValue(val);
			
			scroller.scroll();
		}	
	}	
	
    public void scrollToBottom()
	{
		LightComponentScroller scroller=LightComponentScroller.getScroller(scrollMe);
		if (scroller!=null) {
			scroller.validate();
			int max=scroller.getVerticalScrollbar().getMaximum();
			if (scroller.getVerticalScrollbar().isVisible()) {
				scroller.getVerticalScrollbar().setValue(max);
			} else
				scroller.getVerticalScrollbar().setValue(0);
			scroller.scroll();
			scroller.validate();
		}	
	}	
}



