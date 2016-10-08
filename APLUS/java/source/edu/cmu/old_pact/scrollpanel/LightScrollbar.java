package edu.cmu.old_pact.scrollpanel;

/** BDDScrollbar Copyright 1996                       <br>
 *  by Timothy W Macinta                              <br>
 *  All Rights Reserved                               <br>
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
 
 import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Scrollbar;

import edu.cmu.old_pact.settings.Settings;

 
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
    setForeground(Settings.scrollbarColor);
    setWidth(17);   //20);
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
 	g.setColor(Settings.scrollbarBackgroundColor);
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
          postEvent(new Event(this, Event.SCROLL_LINE_UP, Integer.valueOf(String.valueOf(value))));
          thread_state = SCROLLING_UP;
          start();
        } else if (my > size().height - arrow_width_height) { // inside bottom arrow
          value += line_increment;
          if (value > maximum) value = maximum;
          bottom_up = false;
          repaint();
          postEvent(new Event(this, Event.SCROLL_LINE_DOWN, Integer.valueOf(String.valueOf(value))));
          thread_state = SCROLLING_DOWN;
          start();
        } else if (my < bar_xy) {   // clicking just above bar
          state = SCROLLING_UP;
          value -= page_increment;
          if (value < minimum) value = minimum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_UP, Integer.valueOf(String.valueOf(value))));
        } else if (my >= bar_xy + bar_height) {  // clicking just below bar
          state = SCROLLING_DOWN;
          value += page_increment;
          if (value > maximum) value = maximum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_DOWN, Integer.valueOf(String.valueOf(value))));
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
          postEvent(new Event(this, Event.SCROLL_LINE_UP, Integer.valueOf(String.valueOf(value))));
          thread_state = SCROLLING_UP;
          start();
        } else if (mx > size().width - arrow_width_height) { // inside right arrow
          value += line_increment;
          if (value > maximum) value = maximum;
          bottom_up = false;
          repaint();
          postEvent(new Event(this, Event.SCROLL_LINE_DOWN, Integer.valueOf(String.valueOf(value))));
          thread_state = SCROLLING_DOWN;
          start();
        } else if (mx < bar_xy) {   // clicking just to left of bar
          state = SCROLLING_UP;
          value -= page_increment;
          if (value < minimum) value = minimum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_UP, Integer.valueOf(String.valueOf(value))));
        } else if (mx >= bar_xy + bar_height) {  // clicking just to right of bar
          state = SCROLLING_DOWN;
          value += page_increment;
          if (value > maximum) value = maximum;
          repaint();
          postEvent(new Event(this, Event.SCROLL_PAGE_DOWN, Integer.valueOf(String.valueOf(value))));
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
          postEvent(new Event(this, Event.SCROLL_ABSOLUTE, Integer.valueOf(String.valueOf(value))));
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
          postEvent(new Event(this, Event.SCROLL_ABSOLUTE, Integer.valueOf(String.valueOf(value))));
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

  public synchronized void reshape(int x, int y, int width, int height) {
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

  		g.setColor(Settings.imageShadowBottom);
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
  	g.setColor(Settings.scrollBarArrowColor);
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
			postEvent(new Event(this, Event.SCROLL_LINE_UP, Integer.valueOf(String.valueOf(value))));
		} else if ( thread_state == SCROLLING_DOWN ) {
			value += line_increment;
			if (value > maximum) value = maximum;
			bottom_up = false;
			repaint();
			postEvent(new Event(this, Event.SCROLL_LINE_DOWN, Integer.valueOf(String.valueOf(value))));
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