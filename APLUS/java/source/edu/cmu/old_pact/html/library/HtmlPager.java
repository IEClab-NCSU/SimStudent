package edu.cmu.old_pact.html.library;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.net.URL;
import java.util.Hashtable;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

class HtmlPager
{
  private static final int BUTTON = -1;
  //protected static final int MARGIN = 20;
  //private static final int sizes[] = { 32, 28, 24, 20, 17, 15, 14 };
  protected static final int sizes[] = { 32, 24, 18, 14, 12, 11, 10 };
  private int offset = 0;
  private Stack fonts = new Stack();
  private Font font = null;
  private int spaceWidth = 0;
  private FontMetrics metrics = null;
  private Stack anchors = new Stack();
  private boolean anchor = false;
  //private int leftMargin = MARGIN;
  //private int rightMargin = -MARGIN;
  //private int heightMargin = 5;
  private int leftMargin;
  private int rightMargin;
  private int heightMargin;
  private int MARGIN;
  private int center = 0;
  private int preformatted = 0;
  private Stack lists = new Stack();
  private int list = BUTTON;
  private Vector hrefs = new Vector();
  private Vector selectables = new Vector();
  private Stack draggables = new Stack();
  private boolean draggable = false;
  private DragObject selectable = null;
  private Hashtable names = new Hashtable();
  private int heights[] = null;
  private Vector lines = new Vector();
  private HtmlPagerLine line = null;
  private Href href = null;
  private boolean lineEmpty = true;
  private boolean prevLineEmpty = false;
  private URL url = null;
  protected HtmlCanvas parent = null;
  private int width = -1;
  private Color bgColor;
  private Color textColor;
  private Color linkColor;
  private Stack fontColors = new Stack();
  private Color fontColor;
  private int lastTagLineNumber = 0;

  protected HtmlPager(HtmlCanvas parent, int width)
  {
    this.width = width;
    this.parent = parent;
    MARGIN = parent.getXMargin();
    leftMargin = parent.getXMargin();
  	rightMargin = -parent.getXMargin();
  	heightMargin = parent.getYMargin();
    rightMargin += width;
    
	if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
    	pushFont("geneva", Font.PLAIN, sizes[4]);
    else
    	pushFont("arial", Font.PLAIN, sizes[4]);
    	 
    drawNewLine(true);
    bgColor = parent.getBackground();
    textColor = parent.getForeground();
    linkColor = Color.blue;
    fontColor = textColor;
    lastTagLineNumber = 0;
  }
  
  
  protected synchronized void finish() {
    int lineCount = lines.size();
    
    // create heights
    int heights[] = new int[lineCount + 2];
    //heights[0] = MARGIN;
    heights[0] = heightMargin;
    for (int i = 0; i < lineCount; i++)
      heights[i + 1] = heights[i] + ((HtmlPagerLine)lines.elementAt(i)).getHeight();
    //heights[lineCount + 1] = heights[lineCount] + MARGIN;
    heights[lineCount + 1] = heights[lineCount] + heightMargin;
    // create hrefs
    Href hrefs[] = new Href[this.hrefs.size()];
    this.hrefs.copyInto(hrefs);
    
    // create draggables
    	DragObject selectablesDO[] = new DragObject[this.selectables.size()];
    	this.selectables.copyInto(selectablesDO);
    // create temporary image storage
    Vector tmpImgs = new Vector();
    
    // set position of the last hightlight tag
    if(lastTagLineNumber > 0) {
      parent.setLastTagY(heights[lastTagLineNumber-1]);
      parent.setLastLineY(heights[lineCount-1]);
    }
    // create image
    Image image = null;
    if (width > 0) {
      	image = parent.createImage(width, heights[lineCount + 1]);
      	if (image != null) {
      synchronized(image){
			parent.setBackground(bgColor);
	
			Graphics g = image.getGraphics();
			try{

				g.setColor(bgColor);
				g.fillRect(0, 0, width, heights[lineCount + 1]);
				for (int i = 0; i < lineCount; i++)
	  				((HtmlPagerLine)lines.elementAt(i)).draw(g, heights[i], tmpImgs);
    		}// end try
    		finally {
    			g.dispose();
    		}
    }
    	}
 
   
    }
    // create image array
    HtmlImage imgs[] = new HtmlImage[tmpImgs.size()];
  	if(tmpImgs.size() > 0)
    tmpImgs.copyInto(imgs);
    tmpImgs.removeAllElements();
    tmpImgs  = null;
  
    parent.setData(heights, hrefs, names, imgs, image,selectablesDO);
  }
 
  protected void setLastTagLine ()
  {
  	lastTagLineNumber = lines.size()-1;
  }
  
  protected void setBase(URL base)
  {
    url = base;
  }

  protected void setColors(Color bg, Color text, Color link)
  {
    if (bg != null)
      bgColor = bg;
    if (text != null)
      textColor = text;
    if (link != null)
      linkColor = link;
    pushFontColor(anchor ? linkColor : textColor);
  }

  protected void pushStandardFont()
  {
    pushFont("TimesRoman", font.getStyle(), font.getSize());
  }
  protected void pushFixedFont()
  {
     pushFont("Courier", font.getStyle(), font.getSize());
  }
  
  protected void pushFontSize(int size)
  {
     if (size < 0)
       size = 0;
     if (size >= sizes.length)
       size = sizes.length - 1;
     pushFont(font.getName(), font.getStyle(), sizes[size]);
  }
  
  protected int getFontSize()
  {
  	//if the current size is not in the sizes[] array
  	// then return the closest known size (instead of 
  	// the biggest one)
    int currentSize = font.getSize();
    int ind=0;
    for (int i = 0; i < sizes.length; i++){
      if (currentSize == sizes[i])
		return i;
	  if(sizes[i] < currentSize) 
  		ind = i; 
  	}
    return ind;
  }
 
  public int getRealFontSize()
  {
    return font.getSize();
  }
    
  protected void pushBold()
  {
     pushFont(font.getName(), font.getStyle() | Font.BOLD, font.getSize());
  }
  protected void pushItalic()
  {
     pushFont(font.getName(), font.getStyle() | Font.ITALIC, font.getSize());
  }
  private void pushFont(String name, int style, int size)
  {
    fonts.push(font);
    font = new Font(name, style, size);
    metrics = parent.getFontMetrics(font);
    spaceWidth = metrics.stringWidth(" ");
  }
  protected void popFont()
  {
    if (fonts.size() > 1)
    {
      font = (Font)fonts.pop();
      metrics = parent.getFontMetrics(font);
      spaceWidth = metrics.stringWidth(" ");
    }
  }

  protected void pushFontColor(Color color)
  {
    fontColors.push(fontColor);
    fontColor = color;
  }
  protected void popFontColor()
  {
    if (fontColors.size() > 0)
      fontColor = (Color)fontColors.pop();
  }
  protected Color getFontColor()
  {
    return fontColor;
  }

  protected void pushAnchor(String href, String name)
  {
    anchors.push(Boolean.valueOf(String.valueOf(anchor)));
    if (href != null)
    {
      anchor = true;
      pushFontColor(linkColor);
      this.href = new Href();
      this.href.startLine = lines.size() - 1;
      this.href.startOffset = offset;
      try
      {
        this.href.url = new URL(url, href);
      }
      catch (Exception e)
      {
        this.href.url = null;
      }
    }
    }
    
    protected void pushSelectable(String select)
  {
    draggables.push(Boolean.valueOf(String.valueOf(draggable)));
    if (select != null)
    {
      draggable = true;
      
      this.selectable = new DragObject();
      this.selectable.startLine = lines.size() - 1;
 //System.out.println("start : heights[lines.size() - 1] = "+heights[lines.size() - 1]);
      this.selectable.startOffset = offset;
      this.selectable.text = select;
      this.selectable.font = font;
      this.selectable.margin = MARGIN;
      
      DragObject drObj = parent.getDragObject(select);
      if(drObj != null){
      	pushFontColor(textColor);    //(linkColor);
      	parent.draggables.removeElement(drObj);
      	parent.addDraggable(this.selectable);
      }
    }
  }
  protected void popAnchor()
  {
    if (anchors.size() > 0)
    {
      if (anchor)
      {
	href.endLine = lines.size() - 1;
	href.endOffset = offset;
	hrefs.addElement(href);
	href = null;
      }
      anchor = ((Boolean)anchors.pop()).booleanValue();
      popFontColor();
    }
  }
  protected void popSelectable()
  {
    if (draggables.size() > 0)
    {
      if (draggable){
		selectable.endLine = lines.size() - 1;
//System.out.println("end : heights[lines.size() - 1] = "+heights[lines.size() - 1]);
		selectable.endOffset = offset;
		selectables.addElement(selectable);
		
      	if(parent.inDraggables(selectable.text))
      		popFontColor();
	  	selectable = null;
      }
      draggable = ((Boolean)draggables.pop()).booleanValue();
    }
  }

  protected void pushLeftMargin(boolean newline)
  {
    leftMargin += MARGIN;
    if (newline)
      drawNewLine(true);
  }
  protected void popLeftMargin(boolean newline)
  {
    leftMargin -= MARGIN;
    if (newline)
      drawNewLine(true);
  }

  protected void pushRightMargin()
  {
    rightMargin -= MARGIN;
  }
  protected void popRightMargin()
  {
    rightMargin += MARGIN;
  }

  protected void pushCenter()
  {
    drawNewLine(false);
    center++;
  }
  protected void popCenter()
  {
    drawNewLine(false);
    center--;
  }

  protected void pushPreformatted()
  {
    pushFixedFont(); 
    drawNewLine(true);
    preformatted++;
    prevLineEmpty = true;
  }
  
  protected void popPreformatted()
  {
    preformatted--;
    drawNewLine(true);
    popFont();
  }

  protected void pushListButton()
  {
    lists.push(Integer.valueOf(String.valueOf(list)));
    list = BUTTON;
  }
  protected void pushListNumber()
  {
    lists.push(Integer.valueOf(String.valueOf(list)));
    list += 0;
  }
  protected void popList()
  {
    if (lists.size() > 0)
      list = ((Integer)lists.pop()).intValue();
  }

  private void finishLine()
  {
    prevLineEmpty = lineEmpty;
    if (center > 0)
      line.translate((rightMargin - offset) / 2);
    line = new HtmlPagerLine();
    lines.addElement(line);
    offset = 0;
    addItem(new HtmlPagerItem(fontColor, font, ""));
    offset = leftMargin;
    lineEmpty = true;
  }
  
  protected void drawNewLine(boolean allways)
  {
    if (lineEmpty)
      offset = leftMargin;
    if (lineEmpty && lines.size() == 1)
      return;
    if (lineEmpty)
    {
       // Olga removed 6-25-99
       //if ((preformatted > 0 || allways) && !prevLineEmpty)
	  finishLine();
    }
    else
    {
      finishLine();
      if (allways)
	finishLine();
    }
  }
  
  protected void drawRule()
  {
    drawNewLine(false);
    addItem(new HtmlPagerItem(fontColor, rightMargin - leftMargin));
    drawNewLine(false);
  }
  
  protected void drawListItem()
  {
    int oldOffset = offset;
    offset = leftMargin - MARGIN;
    if (list == BUTTON)
      addItem(new HtmlPagerItem(fontColor, font, "-"));
    else
      addItem(new HtmlPagerItem(fontColor, font, String.valueOf(++list)));
    offset = oldOffset;
  }
  
  synchronized protected void drawImage(HtmlImage img, String align)
  {
    Color color = anchor ? linkColor : null;
    drawNewLine(img.w);
    if ("TOP".equalsIgnoreCase(align))
      addItem(new HtmlPagerItem(color, img, HtmlPagerItem.TOP));
    else if ("MIDDLE".equalsIgnoreCase(align))
      addItem(new HtmlPagerItem(color, img, HtmlPagerItem.MIDDLE));
    else if ("MATHML".equalsIgnoreCase(align))
      addItem(new HtmlPagerItem(color, img, HtmlPagerItem.MATHML));
    else if ("MATHML1".equalsIgnoreCase(align))
      addItem(new HtmlPagerItem(color, img, HtmlPagerItem.MATHML1));
    else
      addItem(new HtmlPagerItem(color, img, HtmlPagerItem.BOTTOM));
    offset += img.w;
  }
  
  protected void drawText(String text, boolean draggable)
  {
    if (preformatted > 0)
    {
      StringTokenizer lines = new StringTokenizer(text, "\n", true);
      while (lines.hasMoreTokens())
      {
	String line = lines.nextToken();
	if (line.equals("\n"))
	{
	  drawNewLine(false);
	  prevLineEmpty = false;
	}
	else
	{
	  addItem(new HtmlPagerItem(fontColor, font, line ,draggable));
	  offset += metrics.stringWidth(line);
	}
      }
    }
    else
    {
      if (offset > leftMargin)
	offset += spaceWidth;
      StringTokenizer words = new StringTokenizer(text);
      int numTok = words.countTokens();
      int curTok = 0;
      while (words.hasMoreTokens())
      {
	String word = words.nextToken();
	int w = metrics.stringWidth(word);
	drawNewLine(w);
	if(draggable && curTok < (numTok-1))
	  word = word+" ";
	addItem(new HtmlPagerItem(fontColor, font, word ,draggable));
	offset += w + spaceWidth;
	curTok++;
      }
      if (words.countTokens() > 0)
	offset -= spaceWidth;
    }
  }
  
  private void drawNewLine(int w)
  {
    if (offset + w >= rightMargin)
      drawNewLine(false);
  }
  
  private void addItem(HtmlPagerItem pi)
  {
    line.addItem(metrics, offset, pi);
    lineEmpty = false;
  }
  
  protected void delete(){
  	font = null;
  	metrics = null;
  	fonts.removeAllElements();
  	fonts = null;
  	lists.removeAllElements();
  	lists = null;
  	anchors.removeAllElements();
  	anchors = null;
  	hrefs.removeAllElements();
  	hrefs = null;
  	selectables.removeAllElements();
  	selectables = null;
  	draggables.removeAllElements();
  	draggables = null;
  	selectable = null;
  	names.clear();
  	names = null;
  	heights = null;
  	lines.removeAllElements();
  	lines = null;
  	line.delete();
  	line = null;
  	href = null;
  	url = null;
  	parent = null;
  	bgColor = null;
  	textColor = null;
 	linkColor = null;
  	fontColors.removeAllElements();
  	fontColors = null;
  	fontColor = null;
 }
}
