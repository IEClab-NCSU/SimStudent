package edu.cmu.old_pact.html.library;

//http://www.geocities.com/SiliconValley/Way/7979/html.java.zip
// new page:
//http://www.xs4all.nl/~griffel/java/index.html

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.event.MouseEvent;
import java.net.URL;

public class HtmlViewer extends Panel implements Runnable {
  private static final int GAP = 5;

  protected HtmlCanvas canvas;
  private Scrollbar scrollbar;
  //private Button forward;
  //private Button backward;
  //private Button reload;
 //private Label label;
 //private TextField location;
  //private Vector history;
  //private int historyIndex;
  protected Event event = null;
  private String start = null;
  protected Thread ownThread = null;
  private boolean showScrollbar = true;
  private Color bgColor = Color.white;
    
  
  Container parent = null;

  public HtmlViewer()
  {
    //history = new Vector();
   // historyIndex = -1;
    //parent.setTitle("(No document)");

    setLayout(null);

    //add(backward = new Button("Backward"));
    //add(forward = new Button("Forward"));
    //add(reload = new Button("Reload"));
    //add(label = new Label("Location:"));
    //add(location = new TextField());
    add(canvas = new HtmlCanvas());
    add(scrollbar = new Scrollbar(Scrollbar.VERTICAL));

    //backward.disable();
    //forward.disable();
    canvas.setBackground(bgColor);
    canvas.resize(600, 500);
    setBackground(bgColor);
    //pack();
    //show();
  }
  
  public HtmlViewer(Container par, boolean showBar)
   {
     //history = new Vector();
    //historyIndex = -1;
     setLayout(null);
     add(canvas = new HtmlCanvas());
     scrollbar = new Scrollbar(Scrollbar.VERTICAL);
	 setShowScrollbar(showBar);
     if(showBar)
     	add(scrollbar);
     canvas.setBackground(bgColor);
     canvas.resize(600, 500);
     setBackground(bgColor);
     parent = par;
   }

  public HtmlViewer(Container par) {
  	this();
  	parent = par;
  }

  
  public HtmlViewer(String toshow, Container par) {
  	this();
  	parent = par;
  	if(parent instanceof Frame)
  		((Frame)parent).setCursor(3);
    try
    {
      //changeNewDocument(new HtmlDocument(toshow));
      changeDocument(new HtmlDocument(toshow));
    }
    catch (Exception e)
    {
       e.printStackTrace();
    }
    if(parent instanceof Frame)
    	((Frame)parent).setCursor(0);
  }
  

  public HtmlViewer(URL url)
  {
    this();
	if(parent != null && parent instanceof Frame)
    	((Frame)parent).setCursor(3);
    try
    {
      //changeNewDocument(new HtmlDocument(url));
      changeDocument(new HtmlDocument(url));
    }
    catch (Exception e)
    {
       e.printStackTrace();
    }
    if(parent != null && parent instanceof Frame)
    	((Frame)parent).setCursor(0);
  }
  
	public void setBorder(boolean b){
		canvas.setBorder(b);
	}

	public boolean getBorder(){
		return canvas.getBorder();
	}

  public void setParent(Container par){
  	parent = par;
  }
  
  public void setShowScrollbar(boolean b){
  	showScrollbar = b;
  }
  
  public void removeAll(){
  	super.removeAll();
  	delete();
  }
  
  public void setBgColor(Color myColor){
  	bgColor = myColor;
  	setBackground(bgColor);
  	canvas.setBackground(bgColor);
  }
  
  public HtmlCanvas getHtmlCanvas(){
  	return canvas;
  }
  //Olga
  public int  setWidth(int w){
  	return canvas.setWidth(w);
  }	
  
  public void scrollToBottom(){
  	if(scrollbar.isVisible()){
  		int max = scrollbar.getMaximum(); 		
		scrollbar.setValue(max);
  	}
  }
  
  public void scrollToTop(){
  	if(scrollbar.isVisible()){
		scrollbar.setValue(0);
  	}
  }
  
  public void scrollToLastTag(){
    if(scrollbar.isVisible() && canvas.getLastLineY() >0){
  	   int max = scrollbar.getMaximum(); 		
  	   int val = max * canvas.getLastTagY() / canvas.getLastLineY();
  	    
 	   scrollbar.setValue(val);
 	   validate();
 	}
  }
  
  
  public boolean handleEvent(Event evt)
  {
  /*
    if (evt.id == Event.WINDOW_DESTROY)
    {
      dispose();
      return true;
    }
   */
    if (showScrollbar && evt.target == scrollbar){
      switch (evt.id){
      case Event.SCROLL_ABSOLUTE:
      case Event.SCROLL_LINE_DOWN:
      case Event.SCROLL_LINE_UP:
      case Event.SCROLL_PAGE_DOWN:
      case Event.SCROLL_PAGE_UP:
	canvas.setStart(scrollbar.getValue());
	start = null;
	return true;
      }
    }
    
    if (evt.target == canvas && evt.id == Event.MOUSE_DOWN || 
    	evt.id == Event.MOUSE_UP ||
    	evt.id == Event.MOUSE_DRAG ||
    	evt.id == Event.ACTION_EVENT) {
    
      if (event == null){
		event = evt;
		//new Thread(this).start();
		startThread();
      }
      return true;
    }
    
    return super.handleEvent(evt);
  }
  
  public void delete(){
  	if(ownThread != null && ownThread.isAlive()){
  		ownThread.stop();
  		ownThread = null;
  	}
  	
  	canvas.delete();
  	canvas.draggables = null;
  	canvas = null;
 } 	
  
  protected void startThread(){
  	if(ownThread != null && ownThread.isAlive()){
  		ownThread.stop();
  		ownThread = null;
  	}
  	ownThread = new Thread(this);
  	ownThread.start();
  }
  
 
  public void mousePressed(MouseEvent evt){ }

  public void run() {
 /*
    if (event.target == canvas && event.id == Event.MOUSE_DOWN) {
      	int x = event.x - canvas.location().x;
      	int y = event.y - canvas.location().y;
//    Not in use for now.
	
//      URL href = canvas.getHref(x, y);
//      if (href != null) {
//			try {
//	  			changeNewDocument(new HtmlDocument(href));
//			}catch (Exception e) {
//	  			e.printStackTrace();
//			}
//      } 
     }
    */ 
/*    
    if (evt.id == Event.ACTION_EVENT) {
      if (evt.target == forward) {
	    changeDocument((HtmlDocument)history.elementAt(++historyIndex));
      }
      if (evt.target == backward) {
	    changeDocument((HtmlDocument)history.elementAt(--historyIndex));
      }
      if (evt.target == reload && historyIndex >= 0) {
		try {
	  	  HtmlDocument document = (HtmlDocument)history.elementAt(historyIndex);
	  	  document = new HtmlDocument(new URL(document.getURLString()));
	  	  history.setElementAt(document, historyIndex);
	  	  changeDocument(document);
		} catch (Exception e) {
	  		e.printStackTrace();
		  }
      } 
      if (evt.target == location) {
	    try {
	      changeNewDocument(new HtmlDocument(new URL(location.getText())));
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
     }      
  }
*/
	if(parent instanceof Frame)
   	 	((Frame)parent).setCursor(0);
    event = null;
  }
  
//below was private
/*
  public void changeNewDocument(HtmlDocument document)
  {
  
    if (historyIndex < 0 ||
	!((HtmlDocument)history.elementAt(historyIndex)).getURLString().
    						equals(document.getURLString()))
    {
      history.setSize(++historyIndex + 1);
      history.setElementAt(document, historyIndex);
   
      changeDocument(document);
    }
  }
 */ 
//below was private
/*
  public void changeDocument(HtmlDocument document) {
    canvas.changeDocument(document);
 
    if (historyIndex == history.size() - 1)
      forward.disable();
    else
      forward.enable();
    if (historyIndex <= 0)
      backward.disable();
    else
      backward.enable();
    location.setText(document.getLocation());
  
  if(parent instanceof Frame) 
    	((Frame)parent).setTitle(document.getTitle());
 	
 	// start Olga
    //scrollbar.setValue(canvas.setStart(start = document.getStart()));
    canvas.setStart(scrollbar.getValue());
    // end Olga
	synchronized (this){
    layout();
    }
  }
 */ 
  public void changeDocument(HtmlDocument document, boolean doDelete) {
    canvas.changeDocument(document, doDelete);
     canvas.setStart(scrollbar.getValue());
	synchronized (this){
    	layout();
    }
  }
  
  public void changeDocument(HtmlDocument document) {
    	changeDocument(document, true);
   }

  public void setSize(int width,int height){
	  super.setSize(width,height);
	  canvas.setSize(new Dimension(width,height));
  }

  public Dimension minimumSize()
  {
    return layoutSize(true);
  }

  public Dimension preferredSize()
  {
    return layoutSize(false);
  }
  
	/*public int preferredHeight(){
  	Insets i = insets();

    // first check whether minimum of preferred sizes must be used
    boolean minimum = false;
    Dimension available = size();
    available.width -= i.left + i.right;
    available.height -= i.top + i.bottom;
    Dimension d = layoutSize(false);

    if (d.width > available.width || d.height > available.height)
      minimum = true;

    int w = GAP;
    int h = controlHeight(minimum);
    available.height -= h;
	
	if(showScrollbar && scrollbar.isVisible()) {
    	d = componentSize(scrollbar, minimum);
    	available.width -= d.width;
    }
   
    int needed = canvas.setWidth(available.width);
    return needed;
	}*/

	public int preferredHeight(){
		return canvas.getHeight();
	}
  
  public void doLayout(){
  	layout();
  }

  public void layout()
  {
  	if(isVisible()){
    Insets i = insets();

    // first check whether minimum of preferred sizes must be used
    boolean minimum = false;
    Dimension available = size();
    available.width -= i.left + i.right;
    available.height -= i.top + i.bottom;
    Dimension d = layoutSize(false);
    if (d.width > available.width || d.height > available.height)
      minimum = true;

    int w = GAP;
    int h = controlHeight(minimum);
    /*
    d = componentSize(forward, minimum);
    forward.reshape(w + i.left, GAP + i.top, d.width, h);
    w += d.width + GAP;
    d = componentSize(backward, minimum);
    backward.reshape(w + i.left, GAP + i.top, d.width, h);
    w += d.width + GAP;
    d = componentSize(reload, minimum);
    reload.reshape(w + i.left, GAP + i.top, d.width, h);
    w += d.width + GAP;
    d = componentSize(label, minimum);
    label.reshape(w + i.left, GAP + i.top, d.width, h);
    w += d.width + GAP;
    d = componentSize(location, minimum);
    location.reshape(w + i.left, GAP + i.top, available.width - w - GAP, h);
    h += 2 * GAP;
    */
    available.height -= h;
    
    int canvas_width;
    if(!showScrollbar && parent != null)
    	canvas_width = parent.getSize().width;
    else
		canvas_width = canvas.setWidth(available.width);
	
    //if (canvas.setWidth(available.width) > available.height &&
    if (canvas_width > available.height &&
		available.height > 0 && showScrollbar)
    {
    
      	d = componentSize(scrollbar, minimum);
      	available.width -= d.width;
      	int needed = canvas.setWidth(available.width);
	  //scrollbar.setValues(scrollbar.getValue(), 1,
					//0, needed - available.height - 1);
		scrollbar.setValue(scrollbar.getValue());
		scrollbar.setMinimum(0);
		scrollbar.setMaximum(needed - available.height - 1);
	  
     	scrollbar.setPageIncrement(available.height);
      	scrollbar.setLineIncrement(3);
      	scrollbar.reshape(available.width + i.left, h + i.top,
						d.width, available.height);
      	scrollbar.setVisible(true);
      	scrollbar.setMaximum(needed - available.height - 1);
      	scrollbar.setValue(scrollbar.getValue());
    }
    else
    {
      scrollbar.setValue(0);
      scrollbar.setVisible(false);
    }
    //canvas.repaint(10);
    canvas.repaint(1);
    //trace.out (5, this, "set canvas width to: " + available.width);
    //trace.out (5, this, "set top point = " + i.left + "," + (h + i.top));
    canvas.reshape(i.left, h + i.top, available.width, available.height);
    if (start == null) {
      canvas.setStart(scrollbar.getValue());
    }
    else {
      scrollbar.setValue(canvas.setStart(start));
    }
    }// if(isVisible())
  }
  
  private int controlHeight(boolean minimum)
  {
    //Dimension d;

    int h = 0;
    /*
    d = componentSize(forward, minimum);
    h = d.height;
    d = componentSize(backward, minimum);
    h = Math.max(h, d.height);
    d = componentSize(reload, minimum);
    h = Math.max(h, d.height);
    d = componentSize(label, minimum);
    h = Math.max(h, d.height);
    d = componentSize(location, minimum);
    h = Math.max(h, d.height);
	*/
    return h;
  }

  private Dimension layoutSize(boolean minimum)
  {
    Dimension d;

    int w = GAP;
    int h = 0;
    /*
    d = componentSize(forward, minimum);
    w += d.width + GAP;
    h = d.height;
    d = componentSize(backward, minimum);
    w += d.width + GAP;
    h = Math.max(h, d.height);
    d = componentSize(reload, minimum);
    w += d.width + GAP;
    h = Math.max(h, d.height);
    d = componentSize(label, minimum);
    w += d.width + GAP;
    h = Math.max(h, d.height);
    d = componentSize(location, minimum);
    w += d.width + GAP;
    h = Math.max(h, d.height);
   */
    //h += 2 * GAP;
    d = componentSize(canvas, minimum);
    w = Math.max(w, d.width);
    h += d.height;

    Insets i = insets();
    return new Dimension(i.left + i.right + w, i.top + i.bottom + h);
  }

  private Dimension componentSize(Component c, boolean minimum)
  {
    return minimum ? c.minimumSize() : c.preferredSize();
  }
  
  public int getNeededWidth(){
  	return canvas.getNeededWidth();
  }

}
