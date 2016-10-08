package edu.cmu.old_pact.scrollpanel;

import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Panel;

/**
 * Each Scroller contains a Panel (viewport) and two LightScrollbars 
 * (horizontal and vertical).  Works in conjunction with a 
 * ScrollerLayout, that lays out the viewport and two 
 * scrollbars.<p>
 * 
 * Subclasses must override:<p>
 * <dl>
 * <dd> abstract public void      scrollTo(int x, int y)
 * <dd> abstract public Dimension getScrollAreaSize()
 * </dl>
 * 
 * @version 1.0, Apr 1 1996
 * @author  David Geary
 * @see     ComponentScroller
 * @see     ImageScroller
 * @see     ScrollerLayout
 * @see     gjt.test.ComponentScrollerTest
 * @see     gjt.test.ImageScrollerTest
 */
public abstract class Scroller extends Panel {
    protected Panel     viewport;
    protected LightScrollbar hbar, vbar;

    abstract public void      scrollTo(int x, int y);
    abstract public Dimension getScrollAreaSize();
	int lines = 10;  // default number of lines - used to define line-increment
	
    public Scroller() {
        setLayout(new ScrollerLayout(this));
        add("Scroll", viewport = new Panel());
        add("East", vbar = new LightScrollbar(LightScrollbar.VERTICAL));
        add("South",hbar = new LightScrollbar(LightScrollbar.HORIZONTAL));
    }
    public LightScrollbar getHorizontalScrollbar() {return hbar;    }
    public LightScrollbar getVerticalScrollbar  () {return vbar;    }
    public Panel     getViewport           () {return viewport;}
    
    public void removeAll(){
    	super.removeAll();
    	viewport = null;
    	hbar = null;
    	vbar = null;
    }
    
    public void setScrollbarWidth(int w){
    	vbar.setWidth(w);
    	hbar.setWidth(w);
    }

    public boolean handleEvent(Event event) {
        boolean handledEvent;

        switch(event.id) {
            case Event.SCROLL_LINE_UP:   scrollLineUp(event); 
            break;
            case Event.SCROLL_LINE_DOWN: scrollLineDown(event); 
            break;
            case Event.SCROLL_PAGE_UP:   scrollPageUp  (event); 
            break;
            case Event.SCROLL_PAGE_DOWN: scrollPageDown(event); 
            break;
            case Event.SCROLL_ABSOLUTE:  scrollAbsolute(event); 
            break;
        }
        handledEvent = event.id == Event.SCROLL_LINE_UP   ||
                       event.id == Event.SCROLL_LINE_DOWN ||
                       event.id == Event.SCROLL_PAGE_UP   ||
                       event.id == Event.SCROLL_PAGE_DOWN ||
                       event.id == Event.SCROLL_ABSOLUTE;

        if(handledEvent) return true;
        else             return super.handleEvent(event);
    }
    public void paint (Graphics g) { scroll(); }
    public void update(Graphics g) { paint(g); }

    public void manageScrollbars() {
    	Dimension size=size();
        Dimension scrollAreaSize = getScrollAreaSize();
        if ((scrollAreaSize.width <= size.width) && (scrollAreaSize.height <= size.height)) {
        	if (hbar.isVisible()) {
   		     	hbar.hide();
 		       	hbar.setValue(0);
 		    }   	
        	if (vbar.isVisible()) {
   		     	vbar.hide();
 		       	vbar.setValue(0);
 		    }   	
        	repaint();
       	} else { 	
	        manageHorizontalScrollbar();
    	    manageVerticalScrollbar  ();
 		}
    }
    protected void manageHorizontalScrollbar() {
        Dimension size           = size();
        Dimension scrollAreaSize = getScrollAreaSize();

        if(vbar.isVisible())
            size.width -= vbar.size().width;

        if(scrollAreaSize.width > size.width) {
            if( ! hbar.isVisible())
                hbar.show();
        }
        else if(hbar.isVisible()) {
            hbar.hide();
            hbar.setValue(0);
            repaint();
        }
    }
    protected void manageVerticalScrollbar() {
        Dimension size           = size();
        Dimension scrollAreaSize = getScrollAreaSize();

        if(hbar.isVisible())
            size.height -= hbar.size().height;
		
        if(scrollAreaSize.height > size.height) {
            if( ! vbar.isVisible())
                vbar.show();
        }
        else if(vbar.isVisible()) {
            vbar.hide();
            vbar.setValue(0);
            repaint();
        }
    }
    
    public void setVertLines(int lin){
    	lines = lin;
    }
    public void setScrollbarValues() {
        if(hbar.isVisible()) setHorizontalScrollbarValues();
        if(vbar.isVisible()) setVerticalScrollbarValues();
    }
    protected void setHorizontalScrollbarValues() {
        Dimension vsize          = viewport.size();
        Dimension scrollAreaSize = getScrollAreaSize(); 
        int max = scrollAreaSize.width - vsize.width; /* BUG - vsize.width; */

        hbar.setValues(hbar.getValue(), // value
                       vsize.width,     // amt visible/page
                       0,               // minimum
                       max);            // maximum

        setHorizontalLineAndPageIncrements();
    }
    protected void setVerticalScrollbarValues() {
        Dimension vsize          = viewport.size();
        Dimension scrollAreaSize = getScrollAreaSize(); 
        int max = scrollAreaSize.height - vsize.height; /* BUG - vsize.height; */

        vbar.setValues(vbar.getValue(), // value
                       vsize.height,    // amt visible/page
                       0,               // minimum
                       max);            // maximum

        setVerticalLineAndPageIncrements();
    }
    protected void scrollLineUp  (Event event) { scroll(); }
    protected void scrollLineDown(Event event) { scroll(); }
    protected void scrollPageUp  (Event event) { scroll(); }
    protected void scrollPageDown(Event event) { scroll(); }
    protected void scrollAbsolute(Event event) { scroll(); }

    protected void setHorizontalLineAndPageIncrements() {
        Dimension size = getScrollAreaSize();
        hbar.setLineIncrement(size.width/10); 
        hbar.setPageIncrement(size.width/5);
    }
    protected void setVerticalLineAndPageIncrements() {
        Dimension size = getScrollAreaSize();
        vbar.setLineIncrement(size.height/lines); 
        vbar.setPageIncrement(size.height/5);
    }

    protected void scroll() {
        scrollTo(hbar.getValue(), vbar.getValue());
    }
}
