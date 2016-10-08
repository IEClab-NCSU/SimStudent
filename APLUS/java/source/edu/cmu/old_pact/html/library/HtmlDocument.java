package edu.cmu.old_pact.html.library;


import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.Hashtable;

//public class HtmlDocument extends Object
public class HtmlDocument
{
  private IntVector codes = new IntVector();
  private StringVector strings = new StringVector();
  private ImageVector images = new ImageVector();
  //private String title = null;
  private String title = "Test Document";
  private String location = null;
  protected String context;
  private String start = null;
  private Color bgColor = null;
  private Color textColor = null;
  private Color linkColor = null;
  protected URL base = null;
  private WebEqImage webEqImage = new WebEqImage();
  private String endOfText = null;
  private HtmlPager pager = null;
  
  public HtmlDocument(String context, URL url, Color c) throws IOException
  {
  	base = url;
  	textColor = c;
    fillLocationAndStart(null);
    this.context = context;
    StringReader in = new StringReader(context);
    HtmlTokenizer tok = new HtmlTokenizer(in);
    parse(tok);
    tok.delete();
    initDocument();
  }
  
  public HtmlDocument(String context, URL url) throws IOException
  {
  	this(context,url,null);
  }
  
  public HtmlDocument(URL url) throws IOException
  {
    base = url;
    fillLocationAndStart(url);
    HtmlTokenizer tok = new HtmlTokenizer(url.openStream());
    parse(tok);
    tok.delete();
    initDocument();
  }
  
   public HtmlDocument(String context) throws IOException
  {
  	this(context,null);
  }
  
	private void initDocument(){
		codes.trimToSize();
    	strings.trimToSize();
    	images.trimToSize();
    }

  public String getTitle()
  {
    return title;
  }

  public String getLocation()
  {
    return location;
  }

  public String getStart()
  {
    return start;
  }
 // Olga 
  protected synchronized void delete() {
	codes.delete();
  	strings.delete();
  	images.delete();
  	bgColor = null;
  	textColor = null;
  	linkColor = null;
  	base = null;
  	
  	if(webEqImage != null)
  		webEqImage.delete();
  	webEqImage = null;
  	deleteHtmlPager();
  }
  
  private void deleteHtmlPager(){
  	if(pager != null)
  		pager.delete();
  	pager = null;
  }
 
// end Olga
  public String getURLString()
  {
    if (start == null)
      return location;
    else
      return location + "#" + start;
  }

  synchronized protected void draw(HtmlPager p)
  { 
  	deleteHtmlPager();
  	pager = p;
    p.setBase(base);
    p.setColors(bgColor, textColor, linkColor);
    codes.reset();
    strings.reset();
    images.reset();
    while (codes.hasMoreElements())
    {
      int code = codes.nextElement();
      if (code >= 0)
        drawOpen(code, p);
      else
        drawClose(-code, p);
    }
    p.finish();
    webEqImage.delete();
    System.gc();
  }

  private void fillLocationAndStart(URL url)
  {
  	if(url != null) {
    String protocol = url.getProtocol();
    String host = url.getHost();
    int port = url.getPort();
    String file = url.getFile();

    while (file.startsWith("/"))
      file = file.substring(1);

    if (port < 0)
      location = protocol + "://" + host + "/" + file;
    else
      location = protocol + "://" + host + ":" + port + "/" + file;

    start = url.getRef();
    }
  }
  
  private void parse(HtmlTokenizer tokenizer)
  {
    while (!tokenizer.eof())
    {
      if (!parseTextItem(tokenizer) &&
	  !parseOpenItem(tokenizer) &&
	  !parseCloseItem(tokenizer))
	tokenizer.getTagOrText();
    }
  }

  private boolean parseTextItem(HtmlTokenizer tokenizer)
  {
    String text;
    if (endOfText == null)
      text = tokenizer.getText();
    else
      text = tokenizer.getPreformattedText(endOfText);

    if (text != null)
    {
      codes.addElement(HTMLTag.TEXT);
      strings.addElement(text);
      return true;
    }

    return false;
  }

  private boolean parseOpenItem(HtmlTokenizer tokenizer)
  {
    for (int code = 1; code < HTMLTag.MAXCODES; code++)
    {
      Hashtable attr = tokenizer.getOpenTag(HTMLTag.names[code]);
     	
      if (attr != null)      
      {
		codes.addElement(code);
		switch (code) {
		  case HTMLTag.A:
	  		strings.addElement(get(attr, "HREF"));
	  		strings.addElement(get(attr, "NAME"));
	  	  break;

		  case HTMLTag.BASE:
	  		String href = get(attr, "HREF");
	  		strings.addElement(href);
	  		try { base = new URL(href); } catch (Exception e) {}
	  	  break;

		  case HTMLTag.BODY:
	  		bgColor = getColor(get(attr, "BGCOLOR"), bgColor);
	  		textColor = getColor(get(attr, "TEXT"), textColor);
	  		linkColor = getColor(get(attr, "LINK"), linkColor);
	  	  break;

		  case HTMLTag.FONT:
	  		strings.addElement(get(attr, "SIZE"));
	  		strings.addElement(get(attr, "COLOR"));
	  	  break;

		  case HTMLTag.IMG:
	  		int w = getInt(get(attr, "WIDTH"));
	  		int h = getInt(get(attr, "HEIGHT"));
	  		images.addElement(base, get(attr, "SRC"), w, h);
	  		strings.addElement(get(attr, "ALIGN"));
	  	  break;

		  case HTMLTag.PRE:
	  		endOfText = "";
	  	  break;
	  
		  case HTMLTag.TITLE:
	  		title = tokenizer.getText();
	  		while (!tokenizer.getCloseTag(HTMLTag.names[code]))
	    		tokenizer.getTagOrText();
	  	  break;
	
		  case HTMLTag.MATHML:		  
		  	String mathmlText = tokenizer.getPreformattedText
		  				//(new String("/"+HTMLTag.names[code]));
		  				("/MATHML");
			if (mathmlText != null)
      		   strings.addElement(mathmlText);
		  break;
	
		  case HTMLTag.DRAG:
	  		String dragStr = tokenizer.getText();
	  		strings.addElement(dragStr); 
	  	  break;
	  
		  case HTMLTag.LISTING:
		  case HTMLTag.XMP:
	  		endOfText = "/" + HTMLTag.names[code];
	  	  break;

		  default:
	  		break;
		 }
        return true;
      }
    }

    return false;
  }

  private boolean parseCloseItem(HtmlTokenizer tokenizer)
  {
    for (int code = 1; code < HTMLTag.MAXCODES; code++)
    {
      if (tokenizer.getCloseTag(HTMLTag.names[code]))
      {
		codes.addElement(-code);
		if (code == HTMLTag.PRE || code == HTMLTag.LISTING || code == HTMLTag.XMP)
	  		endOfText = null;
		return true;
      }
    }

    return false;
  }

  private void drawOpen(int code, HtmlPager p)
  {
    int size;
    String href;
    String name;
    String align;
    String text;
    HtmlImage image;
    String select;
    Color color;
    
    switch (code)
    {
    case HTMLTag.TEXT:		text = strings.nextElement();
							p.drawText(text,false); return;
    case HTMLTag.A:			href = strings.nextElement();
							name = strings.nextElement();
							p.pushAnchor(href, name); return;
	case HTMLTag.DRAG:		select = strings.nextElement();
							p.pushSelectable(select);
							if(p.parent.inDraggables(select))
								p.drawText(select,true);
							else
								p.drawText(select,false);
							return;
    case HTMLTag.ADDRESS:	p.pushItalic(); return;
    case HTMLTag.B:			p.pushBold(); return;
    case HTMLTag.BASE:		return;
    case HTMLTag.BIG:		p.pushFontSize(p.getFontSize() - 1); return;
    case HTMLTag.BLOCKQUOTE:	p.pushRightMargin(); p.pushLeftMargin(true); return;
    case HTMLTag.BODY:		return;
    case HTMLTag.BR:		p.drawNewLine(false); return;
    case HTMLTag.CENTER:	p.pushCenter(); return;
    case HTMLTag.CITE:		return;
    case HTMLTag.CODE:		p.pushFixedFont(); return;
    case HTMLTag.DD:		p.drawNewLine(false); return;
    case HTMLTag.DIR:		p.pushLeftMargin(true); p.pushListButton(); return;
    case HTMLTag.DL:		p.pushLeftMargin(true); return;
    case HTMLTag.DT:		p.popLeftMargin(true); p.pushLeftMargin(false); return;
    case HTMLTag.EM:		p.pushItalic(); return;
    case HTMLTag.FONT:		size = getFontSize(strings.nextElement(), p);
							color = getColor(strings.nextElement(), p.getFontColor());
							p.pushFontColor(color);
							p.pushFontSize(size); return;
    case HTMLTag.H1:		p.drawNewLine(true); pushHeader(1, p); return;
    case HTMLTag.H2:		p.drawNewLine(true); pushHeader(2, p); return;
    case HTMLTag.H3:		p.drawNewLine(true); pushHeader(3, p); return;
    case HTMLTag.H4:		p.drawNewLine(true); pushHeader(4, p); return;
    case HTMLTag.H5:		p.drawNewLine(true); pushHeader(5, p); return;
    case HTMLTag.H6:		p.drawNewLine(true); pushHeader(6, p); return;
    case HTMLTag.HR:		p.drawRule(); return;
    case HTMLTag.I:			p.pushItalic(); return;
    case HTMLTag.IMG:		align = strings.nextElement();
							image = images.nextElement();
							p.drawImage(image, align); 
							return;
    case HTMLTag.KBD:		p.pushFixedFont(); return;
    case HTMLTag.LI:		p.drawNewLine(false); p.drawListItem(); return;
    case HTMLTag.LISTING:	p.pushPreformatted(); return;
  
    case HTMLTag.MATHML:	//create WebEq image, draw it
    						webEqImage.setPointsize(p.getRealFontSize());
 							webEqImage.createEquation(strings.nextElement(), p.parent);	
      						HtmlImage hi = webEqImage.getHtmlImage();
      						webEqImage.delete();
      						p.drawImage(hi, "MATHML1");//"TOP"); //"MATHML"); //"MIDDLE");
    						return;
    case HTMLTag.MENU:		p.pushLeftMargin(true); p.pushListButton(); return;
    case HTMLTag.OL:		p.pushLeftMargin(true); p.pushListNumber(); return;
    case HTMLTag.P:			p.drawNewLine(true); return;
    case HTMLTag.PRE:		p.pushPreformatted(); return;
    case HTMLTag.SAMP:		p.pushFixedFont(); return;
    case HTMLTag.SMALL:		p.pushFontSize(p.getFontSize() + 1); return;
    case HTMLTag.STRONG:	p.pushBold(); return;
    case HTMLTag.TITLE:		return;
    case HTMLTag.TT:		p.pushFixedFont(); return;
    case HTMLTag.UL:		p.pushLeftMargin(true); p.pushListButton(); return;
    case HTMLTag.VAR:		p.pushFixedFont(); p.pushBold(); return;
    case HTMLTag.XMP:		p.pushPreformatted(); return;
    
    case HTMLTag.LASTTAG:	p.setLastTagLine(); return;
    }
  }
  
  private void drawClose(int code, HtmlPager p)
  {
    switch (code)
    {
    case HTMLTag.TEXT:		return;
    case HTMLTag.A:			p.popAnchor(); return;
    case HTMLTag.DRAG:		p.popSelectable(); return;
    case HTMLTag.ADDRESS:	p.popFont(); return;
    case HTMLTag.B:			p.popFont(); return;
    case HTMLTag.BASE:		return;
    case HTMLTag.BIG:		p.popFont(); return;
    case HTMLTag.BLOCKQUOTE:	p.popRightMargin(); p.popLeftMargin(true); return;
    case HTMLTag.BODY:		return;
    case HTMLTag.BR:		return;
    case HTMLTag.CENTER:	p.popCenter(); return;
    case HTMLTag.CITE:		return;
    case HTMLTag.CODE:		p.popFont(); return;
    case HTMLTag.DD:		return;
    case HTMLTag.DIR:		p.popList(); p.popLeftMargin(true); return;
    case HTMLTag.DL:		p.popLeftMargin(true); return;
    case HTMLTag.DT:		return;
    case HTMLTag.EM:		p.popFont(); return;
    case HTMLTag.FONT:		p.popFontColor(); p.popFont(); return;
    case HTMLTag.H1:		popHeader(p); p.drawNewLine(true); return;
    case HTMLTag.H2:		popHeader(p); p.drawNewLine(true); return;
    case HTMLTag.H3:		popHeader(p); p.drawNewLine(true); return;
    case HTMLTag.H4:		popHeader(p); p.drawNewLine(true); return;
    case HTMLTag.H5:		popHeader(p); p.drawNewLine(true); return;
    case HTMLTag.H6:		popHeader(p); p.drawNewLine(true); return;
    case HTMLTag.HR:		return;
    case HTMLTag.I:			p.popFont(); return;
    case HTMLTag.IMG:		return;
    case HTMLTag.KBD:		p.popFont(); return;
    case HTMLTag.LI:		return;
    case HTMLTag.LISTING:	p.popPreformatted(); return;
    case HTMLTag.MATHML:	return;
    case HTMLTag.MENU:		p.popList(); p.popLeftMargin(true); return;
    case HTMLTag.OL:		p.popList(); p.popLeftMargin(true); return;
    case HTMLTag.P:			return;
    case HTMLTag.PRE:		p.popPreformatted(); return;
    case HTMLTag.SAMP:		p.popFont(); return;
    case HTMLTag.SMALL:		p.popFont(); return;
    case HTMLTag.STRONG:	p.popFont(); return;
    case HTMLTag.TITLE:		return;
    case HTMLTag.TT:		p.popFont(); return;
    case HTMLTag.UL:		p.popList(); p.popLeftMargin(true); return;
    case HTMLTag.VAR:		p.popFont(); p.popFont(); return;
    case HTMLTag.XMP:		p.popPreformatted(); return;
    }
  }

  private void pushHeader(int size, HtmlPager p)
  {
    p.pushStandardFont();
    p.pushBold();
    p.pushFontSize(size);
  }
  private void popHeader(HtmlPager p)
  {
    p.popFont();
    p.popFont();
    p.popFont();
  }

  private static int getFontSize(String str, HtmlPager p)
  {

    if (str == null)
      return p.getFontSize();

    try
    {
      if (str.charAt(0) == '+')
	return p.getFontSize() - Integer.parseInt(str.substring(1), 10);
      else if (str.charAt(0) == '-')
	return p.getFontSize() + Integer.parseInt(str.substring(1), 10);
      else
	return 7 - Integer.parseInt(str, 10);
    }
    catch (Exception e)
    {
      return p.getFontSize();
    }
  }

  private static Color getColor(String str, Color defaultColor)
  {
    if (str == null)
      return defaultColor;

    if (str.charAt(0)=='#')
      str = str.substring(1);

    try
    {
      int r = Integer.parseInt(str.substring(0,2), 16);
      int g = Integer.parseInt(str.substring(2,4), 16);
      int b = Integer.parseInt(str.substring(4,6), 16);
      if (r < 0 || g < 0 || b < 0)
	return defaultColor;

      return new Color(r, g, b);
    }
    catch (Exception e)
    {
      return defaultColor;
    }
  }

  private static int getInt(String str)
  {
    try
    {
      return Integer.parseInt(str, 10);
    }
    catch (Exception e)
    {
      return -1;
    }
  }

  private static String get(Hashtable attr, String key)
  {
    return (String)attr.get(key);
  }
  
  
}
