package edu.cmu.old_pact.html.library;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

public class HtmlCanvas extends Canvas
{
  private HtmlDocument document = null;
  private int xMargin = 20;
  private int yMargin = 5;
  protected int start = 0;
  protected int width = 300;
  protected int heights[] = null;
  private Href hrefs[] = null;
  private DragObject[] selectable = null;
  private Hashtable names = null;
  private Image image = null;
  private HtmlImage imgs[] = null;
  protected Vector draggables = new Vector();
  private boolean border=false;
  private int lastTagY = 0;
  private int lastLineY = 0;
  private boolean inUpdate = false; 
  
  public HtmlCanvas()
  {
    this.document = null;
  }

  public HtmlCanvas(HtmlDocument document)
  {
    changeDocument(document);
  }

	public void setBorder(boolean b){
		border = b;
	}

	public boolean getBorder(){
		return border;
	}
	
/*
  public void changeDocument(HtmlDocument doc)
  {
  	if(this.document != null) 
  		delete();
  	this.document = doc;
  	//end Olga
  	inUpdate = true;
    document.draw(new HtmlPager(this, width));
    this.document = document;
    inUpdate = false;
    start = 0;
    // 3/23 irene commented out a call to gc - there is one
    //            at the end of document.draw
   // System.gc();
  }
  */
  public void changeDocument(HtmlDocument doc, boolean doDelete) {
  	if(this.document != null && doDelete) 
  		delete();
  	this.document = doc;
  	inUpdate = true;
    document.draw(new HtmlPager(this, width));
    this.document = document;
    inUpdate = false;
    start = 0;
    //trace.out (5, this, "repaint");
    //trace.out (5, this, "width = " + this.getSize().width);
    //repaint();
  }
  
   public void changeDocument(HtmlDocument doc){
   	changeDocument(doc, true);
   }
  
  public void delete(){
  	//trace.out (5, this, "delete this html document");
  	document.delete();
  	document = null;
  //draggables.removeAllElements();
  }
  
  protected URL getUrl(){
  	if(document != null)
  		return document.base;
  	return null;
  }
  
  public int getXMargin(){
  	return xMargin;
  }
  
  public void setXMargin(int m){
  	xMargin = m;
  }
  
  public int getYMargin(){
  	return yMargin;
  }
  
  public void setYMargin(int m){
  	yMargin = m;
  }
  
  protected void setLastTagY(int y){
  	lastTagY = y;
  }
  
  protected void setLastLineY(int y){
  	lastLineY = y;
  }
  
  protected int getLastTagY(){
  	return lastTagY;
  }
  
  protected int getLastLineY(){
  	return lastLineY;
  }
  
  protected void addDraggable(DragObject toDrag){
  	if(!draggables.contains(toDrag))
  		draggables.addElement(toDrag);
  }
  
  protected void removeDraggable(DragObject toDrag){
  	if(draggables.contains(toDrag))
  		draggables.removeElement(toDrag);
  }
  
  protected synchronized void resetDraggables(){
  	draggables.removeAllElements();
  }
  
  protected HtmlDocument getDocument(){
  	return document;
  }
  
  protected boolean inDraggables(String toCompare){
  	int s = draggables.size();
  	if(s == 0) return false;
  	DragObject dragObj;
  	for(int i=0; i<s; i++){
  		dragObj = (DragObject)draggables.elementAt(i);
  		if(toCompare.equals(dragObj.text))
  			return true;
  	}
  	return false;
  }
  
  protected DragObject getDragObject(String dragStr){
  	int s = draggables.size();
  	if(s == 0) return null;
  	DragObject dragObj;
  	for(int i=0; i<s; i++){
  		dragObj = (DragObject)draggables.elementAt(i);
  		if(dragStr.equals(dragObj.text))
  			return dragObj;
  	}
  	return null;
  }
  
  protected boolean  hasDraggables(){
  	if(draggables.size() > 0)
  		return true;
  	return false;
  }
  
  
  public DragObject getSelectable(int x, int y){
  	if(selectable == null) {
  		return null;
  	}
  	int line = lineAt(y+start);
  	for (int i = 0; i < selectable.length; i++)
    {
      DragObject sel = selectable[i];
      if (line == sel.startLine && x < sel.startOffset)
        continue;
      if (line == sel.endLine && x >= sel.endOffset)
        continue;
      if (line >= sel.startLine && line <= sel.endLine)
        return sel;
    }
    return null;
  }
 
  public URL getHref(int x, int y)
  {
    if (hrefs == null)
      return null;

    int line = lineAt(y + start);

    for (int i = 0; i < hrefs.length; i++)
    {
      Href href = hrefs[i];
      if (line == href.startLine && x < href.startOffset)
        continue;
      if (line == href.endLine && x >= href.endOffset)
        continue;
      if (line >= href.startLine && line <= href.endLine)
        return href.url;
    }
    return null;
  }

  public int setStart(String name)
  {
    if (names == null || name == null)
      start = 0;
    else
    {
      Integer i = (Integer)names.get(name);
      if (i == null)
		start = 0;
      else
		start = heights[i.intValue() - 1];
    }
    repaint();
    return start;
  }
  
  public void setStart(int start)
  {
    this.start = start;
    repaint();
  }

	public void setSize(Dimension d){
		super.setSize(d);
		setWidth(d.width);
	}

	/*similar to setWidth, but doesn't change any values or do any
      drawing/painting*/
	public int getHeight(){
		try{
			if(document == null)
				return 0;
		
			else{
				int toret = heights[heights.length-1];
				return toret;
			}
		} catch (NullPointerException e) {
			return 0;
		}
	}

  public int setWidth(int w)
  {
  try{
  	if(w > 0 && Math.abs(width-w) < 1 && heights != null && heights.length > 0 && document != null)
  		return heights[heights.length - 1];
    if (w == 0 && heights!=null && heights.length>0) // Fringe case.  I apologize for the hack.  Sometimes this gets called with a zero when it should not be, and I couldn't track down all the cases when it did so.
      return heights[heights.length - 1];
    if (document == null)
    {
      width = w;
      return 0;
    }

    if (w != width)
    {
      width = w;
      	document.draw(new HtmlPager(this, w));
	  	invalidate();
	  	repaint();
	 
    }
    return heights[heights.length - 1];
    } catch (NullPointerException e) { 
    	width = w;
    	return 0;
    }
  }

  private String statusString(int info)
  {
    String s = "";
    if ((info & ABORT) != 0) s += "ABORT ";
    if ((info & ALLBITS) != 0) s += "ALLBITS ";
    if ((info & ERROR) != 0) s += "ERROR ";
    if ((info & FRAMEBITS) != 0) s += "FRAMEBITS ";
    if ((info & HEIGHT) != 0) s += "HEIGHT ";
    if ((info & PROPERTIES) != 0) s += "PROPERTIES ";
    if ((info & SOMEBITS) != 0) s += "SOMEBITS ";
    if ((info & WIDTH) != 0) s += "WIDTH ";
    return s + info;
  }
  
	public Dimension minimumSize(){
		return getSize();
	}

	public Dimension preferredSize(){
		//System.out.println("HC.pS: getNeededWidth returns: " + getNeededWidth());
		return new Dimension(getNeededWidth(),getHeight());
	}

  public int getNeededWidth(){
  	int toret  = width;
  	if (document == null || imgs == null  || imgs.length ==0)
  		return toret;
  	for (int i = 0; i < imgs.length; i++)
  		toret = Math.max(toret, imgs[i].w);
  	return toret;
  }
  
  synchronized public void paint(Graphics g) {
  	if(inUpdate)
  		return;
    g.setColor(getBackground());
    if (document == null)
    {
      g.fillRect(0, 0, size().width, size().height);
    }
    else {
      if (image == null){
		document.draw(new HtmlPager(this, width));
		}
		Graphics gg = image.getGraphics();
		try{
      	for (int i = 0; i < imgs.length; i++)
        	gg.drawImage(imgs[i].img, imgs[i].x, imgs[i].y, imgs[i].w, imgs[i].h, this);
        
        }
        finally {
        	gg.dispose();
        }
      g.drawImage(image, 0, -start, this);
      int w = image.getWidth(null);
      if (w < size().width)
        g.fillRect(w, 0, size().width - w, size().height);
      int h = image.getHeight(null) - start;
      if (h < size().height)
        g.fillRect(0, h, size().width, size().height - h);

	  if(border){
		  int xbase = getBounds().x;
		  int ybase = getBounds().y;
		  int width = getSize().width;
		  int height = getSize().height;
		  
		  g.setColor(Color.white);
		  g.drawLine(xbase,ybase+height-1,xbase+width,ybase+height-1);
	  }
    }
  }
  
  public void update(Graphics g)
  {
    paint(g);
  }
  
  synchronized protected void setData(	int heights[],
										Href hrefs[],
										Hashtable names,
										HtmlImage imgs[],
										Image image,
										DragObject selectable[])
  {
    this.heights = heights;
    this.hrefs = hrefs;
    this.names = names;
    this.imgs = imgs;
    this.image = image;
    this.selectable = selectable;
  }

  private int lineAt(int y)
  {
    for (int line = 0; line < heights.length - 1; line++)
      if (y >= heights[line] && y < heights[line + 1])
        return line;
    return -1;
  }
}
