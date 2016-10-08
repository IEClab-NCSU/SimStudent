package edu.cmu.old_pact.cmu.uiwidgets;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Scrollbar;

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
    public LightComponentScroller(Component component, int orient) {
    	super(orient);
        setComponent(component);
    }
    
    public void setComponent(Component component) {
        scrollMe = component;
        viewport.setLayout(new BulletinLayout());
        viewport.add      (scrollMe);
        viewport.move     (0,0);
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
}

/** Copyright 1996                                    <br>
 *  by Timothy W Macinta                              <br>
 *  All Rights Reserved                               <br>
 *
 * see http://web.mit.edu/twm/www/swapware/BDDScrollbar.java.txt 
 *
 *  <p>
 *  Permission to use this code for any purpose is hereby granted with the
 *  restriction that this copyright notice always accompany the source code
 *  (or the documentation if no source code is provided).
 */

/** This class was designed as a replacement for java.awt.Scrollbar since
 *  Scrollbars are broken on Win95.
 */
class LightScrollbar
	extends Canvas
	implements Runnable
{
  final int NOTHING = 0;
  final int SCROLLING_UP = 1;
  final int SCROLLING_DOWN = 2;
  final int TRACKING = 4;

  static final int VERTICAL = Scrollbar.VERTICAL;
  static final int HORIZONTAL = Scrollbar.HORIZONTAL;
  
  static final int ARROW_UP = 0;
  static final int ARROW_LEFT = 1;
  static final int ARROW_RIGHT = 2;
  static final int ARROW_DOWN = 3;
  
  static final long DELAY = 100; /* delay in milliseconds between scrolls while holding down an arrow */
  Thread thread;
  int thread_state = NOTHING;
  
  int orientation = VERTICAL;
  int line_increment = 1;
  int maximum;
  int minimum;
  int page_increment = 10;
  int value = 0;
  int visible = 1;

  //static final int arrow_width_height = 18;
  boolean top_up = true;
  boolean bottom_up = true;
  boolean bar_up = true;
  //boolean inside = false;
  int bar_xy = 0;
  int bar_height = 0;
  int lastx = 0;
  int lasty = 0;
  int state = SCROLLING_UP;
  int track_xy = 0;
  int old_bar_xy = 0;

  Image scratch = null;
  Graphics g_scratch = null;
  final static char[] up = {'^'};
  final static char[] down = {'v'};
  
  int arrowsize;
  int barwidth;
  int arrow_width_height;

  public LightScrollbar() {
    this(Scrollbar.VERTICAL, 0, 1, 0, 0);
  }

  /** Note:  "orientation" is ignored.  You always get a vertical scrollbar. */
  public LightScrollbar(int orientation) {
    this(orientation, 0, 1, 0, 0);
  }

  /** Note:  "orientation" is ignored.  You always get a vertical scrollbar. */
  public LightScrollbar(int orientation, int value, int visible, int minimum, int maximum) {
    this.orientation = orientation;
    setValues(value, visible, minimum, maximum);
    setForeground(Color.lightGray);
    setWidth(17);
  }

  public void setWidth(int width)
  {
    double d;
    barwidth=width;
  	arrow_width_height=barwidth-2;
  	d=barwidth;
  	arrowsize=(int)Math.round(d/5.0);
  }	

  public int getLineIncrement() {
    return line_increment;
  }

  public int getMaximum() {
    return maximum;
  }

  public int getMinimum() {
    return minimum;
  }

  public int getOrientation() {
    return orientation;
  }

  public int getPageIncrement() {
    return page_increment;
  }

  public int getValue() {
    return value;
  }

  public int getVisible() {
    return visible;
  }

  public void setLineIncrement(int lin) {
    if (lin < 0) lin = 0;
    line_increment = lin;
  }

  public void setPageIncrement(int pin) {
    if (pin < 0) pin = 0;
    page_increment = pin;
  }

  public void setValue(int value) {
    if (value < minimum) value = minimum;
    if (value > maximum) value = maximum;
    this.value = value;
    repaint();
  }

  public void setValues(int value, int visible, int minimum, int maximum) {
    if (visible < 0) visible = 0;
    this.visible = visible;
    if (maximum < minimum) maximum = minimum;
    this.minimum = minimum;
    this.maximum = maximum;
    if (value < minimum) value = minimum;
    if (value > maximum) value = maximum;
    this.value = value;
    repaint();
  }

  public void update(Graphics g) {
    paint(g);
  }

  public void paint(Graphics g) {
    if (g_scratch == null) {
      scratch = createImage(size().width, size().height);
      g_scratch = scratch.getGraphics();
    }
    paintSingleBuffer(g_scratch);
    g.drawImage(scratch, 0, 0, this);
  }
  	

  public void paintSingleBuffer(Graphics g) {
    int w = size().width;
    int h = size().height;
    Color f = getForeground();
    g.setColor(Color.white);
	g.fill3DRect(0,0,w,h,false);
	
    g.setColor(f);

    // draw arrows
    
    /* JJL - 23 Feb, 1998 - No longer handles small size well */

    switch (orientation) { 
      case VERTICAL:
      	drawArrowHead(0, 0, g, f, ARROW_UP, top_up);
      	drawArrowHead(0, h - arrow_width_height, g, f, ARROW_DOWN, bottom_up);

        break;
      case HORIZONTAL:

      	drawArrowHead(0, 0, g, f, ARROW_LEFT, top_up);
      	drawArrowHead(w - arrow_width_height - 2, 0, g, f, ARROW_RIGHT, bottom_up);

        break;
    }

    g.setColor(f);
    // draw bar in middle

    int tot = maximum - minimum + visible;                            
    int s;
    switch (orientation) {
      case VERTICAL:
      	
	s = h - 2 * arrow_width_height;
        if (tot == 0) {
          bar_xy = arrow_width_height;
          bar_height = s;
        } else {
          bar_xy = arrow_width_height + ( (value - minimum) * s) / tot;
          bar_height = (visible * s) / tot;
        }
        if (bar_height < 4) {
          bar_height = 4;
        }
        if (bar_xy > h - arrow_width_height - 4) {
          bar_xy = h - arrow_width_height - 4;
          if (bar_xy < arrow_width_height) bar_xy = arrow_width_height;
        }
	
	fill3DEmbossedRect(0, bar_xy, arrow_width_height, bar_height, bar_up, g, f);

        break;
      case HORIZONTAL:
	s = w - 2*arrow_width_height;
        if (tot == 0) {
          bar_xy = arrow_width_height;
          bar_height = s;
        } else {
          bar_xy = arrow_width_height + ((value - minimum)*s) / tot;
          bar_height = (visible*s) / tot;
        }
        if (bar_height < 4) {
          bar_height = 4;
        }
        if (bar_xy > w - arrow_width_height - 4) {
          bar_xy = w - arrow_width_height - 4;
          if (bar_xy < arrow_width_height) bar_xy = arrow_width_height;
        }
	fill3DEmbossedRect(bar_xy, 0, bar_height, arrow_width_height, bar_up, g, f);


        break;
    }
  }

  public boolean mouseDown(Event evt, int mx, int my) {
    switch (orientation) {
      case VERTICAL:
        if (my < arrow_width_height) { // inside top arrow
          value -= line_increment;
          if (value < minimum) value = minimum;
          top_up = false;
          repaint();
          postEvent(new Event(this, Event.SCROLL_LINE_UP, new Integer(value)));
          thread_state = SCROLLING_UP;
          start();
        } else if (my > size().height - arrow_width_height) { // inside bottom arrow
          value += line_increment;
          if (value > maximum) value = maximum;
          bottom_up = false;
          repaint();
          postEvent(new Event(this, Event.SCROLL_LINE_DOWN, new Integer(value)));
          thread_state = SCROLLING_DOWN;
          start();
        } else if (my < bar_xy) {   // clicking just above bar
          state = SCROLLING_UP;
          value -= page_increment;
          if (value < minimum) value = minimum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_UP, new Integer(value)));
        } else if (my >= bar_xy + bar_height) {  // clicking just below bar
          state = SCROLLING_DOWN;
          value += page_increment;
          if (value > maximum) value = maximum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_DOWN, new Integer(value)));
        } else {     // inside bar
          track_xy = my;
          old_bar_xy = bar_xy;
          bar_up = false;
          state = TRACKING;
          repaint();
        }
        break;
      case HORIZONTAL:
        if (mx < arrow_width_height) { // inside left arrow
          value -= line_increment;
          if (value < minimum) value = minimum;
          top_up = false;
          repaint();
          postEvent(new Event(this, Event.SCROLL_LINE_UP, new Integer(value)));
          thread_state = SCROLLING_UP;
          start();
        } else if (mx > size().width - arrow_width_height) { // inside right arrow
          value += line_increment;
          if (value > maximum) value = maximum;
          bottom_up = false;
          repaint();
          postEvent(new Event(this, Event.SCROLL_LINE_DOWN, new Integer(value)));
          thread_state = SCROLLING_DOWN;
          start();
        } else if (mx < bar_xy) {   // clicking just to left of bar
          state = SCROLLING_UP;
          value -= page_increment;
          if (value < minimum) value = minimum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_UP, new Integer(value)));
        } else if (mx >= bar_xy + bar_height) {  // clicking just to right of bar
          state = SCROLLING_DOWN;
          value += page_increment;
          if (value > maximum) value = maximum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_DOWN, new Integer(value)));
        } else {     // inside bar
          track_xy = mx;
          old_bar_xy = bar_xy;
          bar_up = false;
          state = TRACKING;
          repaint();
        }
        break;
    }
    lastx = mx;
    lasty = my;
    return false;
  }

  public boolean mouseUp(Event evt, int mx, int my) {
    bottom_up = true;
    top_up = true;
    bar_up = true;
    state = NOTHING;
    repaint();
    lastx = mx;
    lasty = my;
    stop();
    return false;
  }

  public boolean mouseEnter(Event evt, int mx, int my) {
    //inside = true;
    lastx = mx;
    lasty = my;
    if (insideTop(mx, my) || insideBottom(mx, my)) repaint();
    return false;
  }

  public boolean mouseExit(Event evt, int mx, int my) {
    //inside = false;
    if (insideTop(lastx, lasty) || insideBottom(lastx, lasty)) repaint();
    lastx = mx;
    lasty = my;
    return false;
  }

  public boolean mouseDrag(Event evt, int mx, int my) {
    if ((insideTop(mx, my) != insideTop(lastx, lasty)) ||
	(insideBottom(mx, my) != insideBottom(lastx, lasty)))
      repaint();
    switch (orientation) {
      case VERTICAL:
        if (!bar_up) {
          int tmp = my - track_xy + old_bar_xy;
          int h = size().height - bar_height - arrow_width_height;
          if (h == arrow_width_height) return false;
          if (tmp < arrow_width_height) {
  	    tmp = arrow_width_height;
          } else if (tmp > h) {
	    tmp = h;
          }
          value = ((tmp - arrow_width_height) * (maximum - minimum)) /
	    (h - arrow_width_height) + minimum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_ABSOLUTE, new Integer(value)));
        }
        break;
      case HORIZONTAL:
        if (!bar_up) {                
          int tmp = mx - track_xy + old_bar_xy;
          int w = size().width - bar_height - arrow_width_height;
          if (w == arrow_width_height) return false;
          if (tmp < arrow_width_height) {                                       
            tmp = arrow_width_height;
          } else if (tmp > w) {      
            tmp = w;
          }
          value = ((tmp - arrow_width_height) * (maximum - minimum)) /
            (w - arrow_width_height) + minimum;                                 
          repaint();
          postEvent(new Event(this, Event.SCROLL_ABSOLUTE, new Integer(value)));
        }
        break;
    }
    lastx = mx;
    lasty = my;
    return false;
  }

  final boolean insideTop(int mx, int my) {
    switch (orientation) {
      case VERTICAL:
        return (mx > -1 && my > -1 && mx < size().width && my < arrow_width_height);
      case HORIZONTAL:
        return (mx > -1 && my > -1 && my < size().height && mx < arrow_width_height);
    }
    return false;
  }

  final boolean insideBottom(int mx, int my) {
    switch (orientation) {
      case VERTICAL:
        return (mx > -1 && my > size().height-arrow_width_height && mx < size().width && my < size().height);
      case HORIZONTAL:
	 return (my > -1 && mx > size().width-arrow_width_height && my < size().height && mx < size().width);
    }
    return false;
  }

  public void reshape(int x, int y, int width, int height) {
    super.reshape(x, y, width, height);
    if (width > 0 && height > 0) {
      scratch = createImage(width, height);
      if (scratch != null) g_scratch = scratch.getGraphics();
      repaint();
    }
  }

  public Dimension preferredSize() {
    switch (orientation) {
      case VERTICAL:
        return new Dimension(barwidth, 100);
    }
    return new Dimension(100, barwidth);
  }

  public Dimension minimumSize() {
    switch (orientation) {
      case VERTICAL:
        return new Dimension(5, 50);
    }
    return new Dimension(50, 5);
  }

  private void 	fill3DEmbossedRect(int left, int top, int w, int h, boolean raised, Graphics g, Color c) {
  	int right, bottom;
  	right = left + w - 1;
  	bottom = top + h - 1;
  	if (raised) {
  		g.setColor(c);
	  	g.fillRect(left, top, w - 1, h);

  		g.setColor(Color.black);
	  	g.drawLine(left, bottom, right, bottom);
  		g.drawLine(right, top, right, bottom);

		g.setColor(c.brighter());
	  	g.drawLine(left + 1, top + 1, left + 1, bottom - 2);
  		g.drawLine(left + 1, top + 1, right - 2, top + 1);

  		g.setColor(c.darker());
  		g.drawLine(right - 1, bottom - 1, right - 1, top + 1);
  		g.drawLine(right - 1, bottom - 1, left + 1, bottom - 1);
  	} else {
  		g.setColor(c.darker());
  		g.fillRect(left, top, w, h);

  		g.setColor(c);
  		g.fillRect(left + 1, top + 1, w - 2, h - 2);  		
  	}
  }

  private void drawArrowHead(int left, int top,Graphics g, Color base, int direction, boolean raised) {
  	int i,param;
  	int right, bottom;
  	int centerX,centerY;
  	
  	right = left + arrow_width_height;
  	bottom = top + arrow_width_height;
  	centerX=(left+right)/2;
  	centerY=(top+bottom)/2;
  	
  	/* draw the box */
  	fill3DEmbossedRect(left, top, arrow_width_height, arrow_width_height, raised, g, base);

  	/* draw the arrow */
  	g.setColor(Color.black);
  	param=arrowsize-1;
  	switch (direction) {
  		case ARROW_DOWN:
			for (i=0;i<arrowsize;i++)
				g.drawLine(centerX-param+i-1,centerY+i-1,centerX+param-i-1,centerY+i-1);
			break;
  		case ARROW_UP:
			for (i=0;i<arrowsize;i++)
				g.drawLine(centerX-param+i-1,centerY-i-1,centerX+param-i-1,centerY-i-1);
			break;
  		case ARROW_LEFT:
  			for (i=0;i<arrowsize;i++)
				g.drawLine(centerX-i-1,centerY-param+i-1,centerX-i-1,centerY+param-i-1);
			break;
  		case ARROW_RIGHT:
  			for (i=0;i<arrowsize;i++)
				g.drawLine(centerX+i-1,centerY-param+i-1,centerX+i-1,centerY+param-i-1);
			break;
  	}
  }


  public void run() {
  	while (thread != null) {
  		try { Thread.sleep(DELAY); }
  		catch (InterruptedException e) {}
  		/* do scroll */
  		if ( thread_state == SCROLLING_UP ) {
			value -= line_increment;
			if (value < minimum) value = minimum;
			top_up = false;
			repaint();
			postEvent(new Event(this, Event.SCROLL_LINE_UP, new Integer(value)));
		} else if ( thread_state == SCROLLING_DOWN ) {
			value += line_increment;
			if (value > maximum) value = maximum;
			bottom_up = false;
			repaint();
			postEvent(new Event(this, Event.SCROLL_LINE_DOWN, new Integer(value)));
		}
	}
  }
  
  public void start() {
  	if ( thread == null ) {
  		thread = new Thread(this);
  		thread.start();
  	}
  }
  
  public void stop() {
  	if (thread != null) {
  		thread.stop();
  		thread = null;
  	}
  	thread_state = NOTHING;
  }
  
}
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
abstract class Scroller extends Panel {
    protected Panel     viewport;
    protected LightScrollbar hbar=null, vbar=null;
    protected ScrollerLayout sLayout;

    abstract public void      scrollTo(int x, int y);
    abstract public Dimension getScrollAreaSize();

    public Scroller() {
 sLayout = new ScrollerLayout(this);
        setLayout(sLayout);
        add("Scroll", viewport = new Panel());
        add("East", vbar = new LightScrollbar(LightScrollbar.VERTICAL));
        add("South",hbar = new LightScrollbar(LightScrollbar.HORIZONTAL));
    }
    
    public Scroller(int orient){
    	this();
    	if(orient == LightScrollbar.VERTICAL)
    		sLayout.removeLayoutComponent(hbar);
    	
    	else if(orient == LightScrollbar.HORIZONTAL)
    		sLayout.removeLayoutComponent(vbar);
    }
    
    public LightScrollbar getHorizontalScrollbar() {return hbar;    }
    public LightScrollbar getVerticalScrollbar  () {return vbar;    }
    public Panel     getViewport           () {return viewport;}

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

        if( hbar.isVisible())
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
        vbar.setLineIncrement(size.height/10); 
        vbar.setPageIncrement(size.height/5);
    }

    protected void scroll() {
        scrollTo(hbar.getValue(), vbar.getValue());
    }
}

/**
 * Layout manager for a Scroller.<p>
 *
 * Lays out 3 Components:  a horizontal scrollbar, a vertical 
 * scrollbar and a viewport (Panel).<p>
 *
 * Valid names/Component pairs that can be added via 
 * addLayoutComponent(String, Component):<p>
 * <dl>
 * <dd> "East"   LightScrollbar (vertical)
 * <dd> "West"   LightScrollbar (vertical)
 * <dd> "North"  LightScrollbar (horizontal)
 * <dd> "South"  LightScrollbar (horizontal)
 * <dd> "Scroll" Panel (viewport)
 * </dl>
 *
 * @version 1.0, Apr 1 1996
 * @author  David Geary
 * @see     Scroller
 */
class ScrollerLayout implements LayoutManager {
    private Scroller  scroller;
    private LightScrollbar hbar, vbar;
    private String    hbarPosition, vbarPosition;
    private Component viewport;
    private int       top, bottom, right, left;

    public ScrollerLayout(Scroller scroller) {
        this.scroller = scroller;
    }

    public void addLayoutComponent(String name, 
                                   Component comp) {

        if(comp instanceof LightScrollbar) {
            LightScrollbar sbar = (LightScrollbar)comp;

            if(sbar.getOrientation() == LightScrollbar.VERTICAL) {
                vbar         = sbar;
                vbarPosition = name;
            }
            else {
                hbar         = sbar;
                hbarPosition = name;
            }
        }
        else {
            viewport = comp;
        }
    }
    public void removeLayoutComponent(Component comp) {
        if(comp == vbar)     vbar     = null;
        if(comp == hbar)     hbar     = null;
        if(comp == viewport) viewport = null;
    }
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0,0);

        if(vbar != null && vbar.isVisible()) {
            Dimension d = vbar.preferredSize();
            dim.width += d.width;
            dim.height = d.height;
        }
        if(hbar != null && hbar.isVisible()) {
            Dimension d = hbar.preferredSize();
            dim.width += d.width;
            dim.height = Math.max(d.height, dim.height);
        }
        if(viewport != null && viewport.isVisible()) {
            Dimension d = viewport.preferredSize();
            dim.width += d.width;
            dim.height = Math.max(d.height, dim.height);
        }
        return dim;
    }
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0,0);

        if(vbar != null && vbar.isVisible()) {
            Dimension d = vbar.minimumSize();
            dim.width += d.width;
            dim.height = d.height;
        }
        if(hbar != null && hbar.isVisible()) {
            Dimension d = hbar.minimumSize();
            dim.width += d.width;
            dim.height = Math.max(d.height, dim.height);
        }
        if(viewport != null && viewport.isVisible()) {
            Dimension d = viewport.minimumSize();
            dim.width += d.width;
            dim.height = Math.max(d.height, dim.height);
        }
        return dim;
    }
    public void layoutContainer(Container target) {
      doLayoutContainer(target);
      if (hbar != null && hbar.getMinimum() == hbar.getMaximum()) {
        hbar.hide();
        doLayoutContainer(target);
      }
      if (vbar != null && vbar.getMinimum() == vbar.getMaximum()) {
        vbar.hide();
        doLayoutContainer(target);
      }
    }
    public void doLayoutContainer(Container target) {
        Insets insets        = target.insets();
        Dimension targetSize = target.size();

        top    = insets.top;
        bottom = targetSize.height - insets.bottom;
        left   = insets.left;
        right  = targetSize.width - insets.right;

        scroller.manageScrollbars();

        reshapeHorizontalScrollbar();
        reshapeVerticalScrollbar  ();
        reshapeViewport           ();

        scroller.setScrollbarValues();
    }
    private void reshapeHorizontalScrollbar() {
        if(hbar != null && hbar.isVisible()) {
            if("North".equals(hbarPosition)) {
                Dimension d = hbar.preferredSize();
                hbar.reshape(left, top, right - left, d.height);
                top += d.height;
            }
            else {  // South
                Dimension d = hbar.preferredSize();
                hbar.reshape(left, bottom - d.height,
                            right - left,d.height);
                bottom -= d.height;
            }
        }
    }
    private void reshapeVerticalScrollbar() {
        if(vbar != null && vbar.isVisible()) {
            if("East".equals(vbarPosition)) {
                Dimension d = vbar.preferredSize();
                vbar.reshape(right - d.width, top, 
                             d.width, bottom - top);
                right -= d.width;
            }
            else { // West
                Dimension d = vbar.preferredSize();
                vbar.reshape(left, top, 
                             d.width, bottom - top);
                left += d.width;
            }
        }
    }
    private void reshapeViewport() {
        if(viewport != null && viewport.isVisible()) {
            viewport.reshape(left, top, 
                             right - left, bottom - top);
        }
    }
}

/**
 * Lays out components as though they were pinned to 
 * a bulletin board.<p>
 *
 * Components are simply reshaped to their location and their
 * preferred size.  BulletinLayout is preferrable to setting
 * a container's layout manager to null and explicitly positioning
 * and sizing components.<p>
 *
 * @version 1.0, Apr 1 1996
 * @author  David Geary
 */
class BulletinLayout implements LayoutManager {
    public BulletinLayout() {
    }
    public void addLayoutComponent(String name, Component comp) {
    }
    public void removeLayoutComponent(Component comp) {
    }
    public Dimension preferredLayoutSize(Container target) {
        Insets    insets      = target.insets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.countComponents();
        Component comp;
        Dimension d;
        Rectangle preferredBounds = new Rectangle(0,0);
        Rectangle compPreferredBounds;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.preferredSize();
                compPreferredBounds = 
                    new Rectangle(comp.location());
                compPreferredBounds.width  = d.width;
                compPreferredBounds.height = d.height;

                preferredBounds = 
                    preferredBounds.union(compPreferredBounds);
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }
    public Dimension minimumLayoutSize(Container target) {
        Insets    insets      = target.insets();
        Dimension dim         = new Dimension(0,0);
        int       ncomponents = target.countComponents();
        Component comp;
        Dimension d;
        Rectangle minimumBounds = new Rectangle(0,0);
        Rectangle compMinimumBounds;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                d = comp.minimumSize();
                compMinimumBounds = 
                    new Rectangle(comp.location());
                compMinimumBounds.width  = d.width;
                compMinimumBounds.height = d.height;

                minimumBounds = 
                    minimumBounds.union(compMinimumBounds);
            }
        }
        dim.width  += insets.left + insets.right;
        dim.height += insets.top + insets.bottom;

        return dim;
    }
    public void layoutContainer(Container target) {
        Insets    insets      = target.insets();
        int       ncomponents = target.countComponents();
        Component comp;
        Dimension ps;
        Point loc;

        for (int i = 0 ; i < ncomponents ; i++) {
            comp = target.getComponent(i);

            if(comp.isVisible()) {
                ps  = comp.preferredSize();    
                loc = comp.location();
                comp.reshape(insets.left + loc.x, 
                             insets.top + loc.y,
                             ps.width, ps.height);
            }
        }
    }
}

