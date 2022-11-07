package edu.cmu.old_pact.html.library;


import java.util.Vector;


//public class HTMLWrapper extends Object{
public class HTMLWrapper {

	private  String title = "No Title";
	private  String text = ""; // String to be returned?
	private  int    namesSize = 0;
	
	public HTMLWrapper() { }
	
	public HTMLWrapper(HTMLElement element) {
		namesSize = HTMLTag.names.length;
		int code = intIdentifier(element.getIdentifier());
		text = "";
		String htmlText = element.getText();
		String onTheEnd = "";
		switch (code) {
			case HTMLTag.TEXT:
				htmlText = wrapText(htmlText, element);
				break;
			case HTMLTag.BODY:
	  			String addToBody = "";
	  			if(element.attributeExist("TEXT"))
	  				addToBody = wrapString(addToBody, element,"TEXT", "",
												"TEXT=", "", false);
				if(element.attributeExist("BGCOLOR"))
					addToBody = wrapString(addToBody, element,"BGCOLOR", "",
												"BGCOLOR=", "", false);
				if(element.attributeExist("LINK"))
					addToBody = wrapString(addToBody, element,"LINK", "",
												"LINK=", "", false);
				htmlText = "<BODY "+addToBody+">"+htmlText+"</BODY>";								
	  			break;			
/*
			case IMG:
	  			int w = getInt(get(attr, "WIDTH"));
	  			int h = getInt(get(attr, "HEIGHT"));
	  			images.addElement(base, get(attr, "SRC"), w, h);
	  			strings.addElement(get(attr, "ALIGN"));
	  			break;
*/
			case HTMLTag.TITLE:
				htmlText = "<HEAD><TITLE>"+htmlText+"</TITLE></HEAD>";
	  			break;
	  		case HTMLTag.HTML:
	  			htmlText = "<HTML>"+htmlText+"</HTML>";
	  			break;

			default:
	  			break;
		}
		text = htmlText;
	}
	
	public String getText() {
//trace.out("text = "+text);
		return text;
	}
	
	private String wrapText(String htmlText, HTMLElement element) {
		Vector attr = element.getAttributeNames();
		boolean  attrExist;
		for (int code = 1; code < HTMLTag.MAXCODES; code++) {
      		attrExist = element.attributeExist(HTMLTag.names[code]);
      		if (attrExist) {
				switch (code) {
					case HTMLTag.FONT:
						htmlText = wrapString(	htmlText, element,"FONT", "SIZE",
												"<FONT SIZE=", "</FONT>", true);
						
	  					htmlText = wrapString(	htmlText, element,"FONT", "COLOR",
												"<FONT COLOR=", "</FONT>", true);
	  					break;
	  				case HTMLTag.PRE:
	  					htmlText = "<PRE>"+htmlText+"</PRE>";
	  					break;
	  				case HTMLTag.STRONG:
	  					htmlText = "<STRONG>"+htmlText+"</STRONG>";
	  					break;
	  				case HTMLTag.B:
	  					htmlText = "<B>"+htmlText+"</B>";
	  					break;
	  				case HTMLTag.CENTER:
	  					htmlText = "<CENTER>"+htmlText+"</CENTER>";
	  					break;
	  				case HTMLTag.DRAG:
	  					htmlText = "<DRAG>"+htmlText+"</DRAG>";
	  					break;
	  				case HTMLTag.H1:
	  					htmlText = "<H1>"+htmlText+"</H1>";
	  					break;
	  				case HTMLTag.H2:
	  					htmlText = "<H2>"+htmlText+"</H2>";
	  					break;
	  				case HTMLTag.H3:
	  					htmlText = "<H3>"+htmlText+"</H3>";
	  					break;
	  				case HTMLTag.H4:
	  					htmlText = "<H4>"+htmlText+"</H4>";
	  					break;
					case HTMLTag.H5:
	  					htmlText = "<H5>"+htmlText+"</H5>";
	  					break;
	  				case HTMLTag.H6:
	  					htmlText = "<H6>"+htmlText+"</H6>";
	  					break;
	  				case HTMLTag.HR:
	  					htmlText = "<HR>"+htmlText+"</HR>";
	  					break;
	  				default:
	  					break;
	  			}//switch
	  		}//if attrExist
	  	}
	  //trace.out("in HTMLWrapper htmlText = "+htmlText);
	  
	  	return htmlText;
	}
	  				
 
	private String wrapString(	String htmlText, HTMLElement element, 
								String attrName, String subName, 
								String start, String end, boolean isEnd) {
		String value = element.getAttributeValue(attrName, subName); 
		if(value == null || value.equals(""))
			return htmlText;
		if(isEnd)
			htmlText =  start + value + ">" + htmlText + end;
		else
			htmlText =  start + value + htmlText + end;
		return htmlText;
	}
	
	private int intIdentifier(String iden) {
		if(iden.equals("TEXT"))
			return 0;
		int toret = -1;
		for(int i=1; i<namesSize; i++) 
			if(HTMLTag.names[i].equals(iden))
				return i;
		return toret;
	}

}
	
	
	
	
	