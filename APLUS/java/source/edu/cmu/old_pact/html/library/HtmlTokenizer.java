package edu.cmu.old_pact.html.library;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Hashtable;

//class HtmlTokenizer extends Object
class HtmlTokenizer
{
  private Reader in;
  private char inBuf[] = new char[100];
  private char buf[] = new char[200];
  private String fullDoc;
  private int index = 0;
  private int length = 0;


  protected HtmlTokenizer(InputStream in)
  {
  	this(new InputStreamReader(in));
  }
  
  protected HtmlTokenizer(Reader in)
  {
  	this.in = in;
  }
  
  protected void delete(){
  	in = null;
  	inBuf = null;
  	buf = null;
  }

 
  // return null if no open token for name found
  // return (possibly empty) hashtable with attributes otherwise
  protected Hashtable getOpenTag(String name)
  {
    try
    {
      reset();
      readSpaces();
      if (read() != '<')
		return null;
      readSpaces();
      String tagName = readIdentifier();
      if (!name.equalsIgnoreCase(tagName))
		return null;
      readSpaces();

      Hashtable attr = new Hashtable();
      while (read() != '>')
      {
		index--;
		clear();
		String key = readIdentifier();
		String value = "";
		readSpaces();
		if (read() == '=')
		{
	  		readSpaces();
          	value = readIdentifier();
	  		readSpaces();
		}
		attr.put(key.toUpperCase(), value);
      }
      clear();

      return attr;
    }
    catch (Exception e)
    {
      return null;
    }
  }

  // return true if close tag for name found
  protected boolean getCloseTag(String name)
  {
    try
    {
      reset();
      readSpaces();
      if (read() != '<')
		return false;
      readSpaces();
      if (read() != '/')
		return false;
      readSpaces();
      String tagName = readIdentifier();
      if (!name.equalsIgnoreCase(tagName))
		return false;
      readUntil('>');
      read();
      clear();
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  protected boolean eof()
  {
    reset();
    try
    {
      read();
      return false;
    }
    catch (Exception e)
    {
      return true;
    }
  }

  // return true if tag found
  protected void getTagOrText()
  {
    try
    {
      reset();
      readSpaces();
      if (read() == '<')
      {
		readUntil('>');
		read();
		clear();
      }
      else
      {
		getText();
      }
    }
    catch (Exception e)
    {
      getText();
    }
  }

  // return text until next open or close tag
  // empty string is returned as null
  protected String getText()
  {
    try
    {
      reset();
      if (read() == '<')
	return null;
      index--;
      String t = readUntil('<');
      clear();
      return t;
    }
    catch (Exception e)
    {
      return null;
    }
  }

  // return all text until EOL or  close tag for name
  // empty string is returned as null
  protected String getPreformattedText(String name)
  {
    reset();
    try
    {
      while (!endOfPreformattedText(name))
	read();
    }
    catch (Exception e)
    {
    }
    index -= 1 + name.length();
    if (index == 0)
      return null;
    String t = makeMassagedString(buf, 0, index);
    clear();
    
    return t;
  }

  private boolean endOfPreformattedText(String name)
  {
    if (index < 1 + name.length())
      return false;
    if (buf[index - 1 - name.length()] != '<')
      return false;
    for (int i = 0; i < name.length(); i++)
      if (name.charAt(i) != buf[index - name.length() + i])
	return false;
    return true;
  }

  private void reset()
  {
    index = 0;
  }

  private void clear()
  {
    int i;
    int j;

    for (i = 0, j = index; j < length; i++, j++)
      buf[i] = buf[j];

    length -= index;
    index = 0;
  }

  private char read() throws Exception
  {
    while (index == length) {
    	try{

    		int l = in.read(inBuf, 0, inBuf.length);
      		if (l <= 0)
				throw new Exception();
      		if (length + l > buf.length) {
				char newBuf[] = new char[buf.length + inBuf.length];
				System.arraycopy(buf, 0, newBuf, 0, length);
       			 buf = newBuf;
      		}
      		for (int i = 0; i < l; i++, length++) 
        		buf[length] = inBuf[i];
      		
    	}
    	catch ( IOException e) { System.out.println(e.toString()); }
   }
    return buf[index++];
  }

  private void readSpaces() throws Exception
  {
    while (Character.isSpace(read()))
      ;
    index--;
  }

  private String readIdentifier() throws Exception
  {
    int start = index;
    char c = read();
    if (c == '\"')
    {
      String s = readUntil(c);
      read();
      return s;
    }
    else if (identifierChar(c))
    {
      while (identifierChar(read()))
        ;
      index--;
      return new String(buf, start, index - start);
    }
    else
    {
      while (!identifierChar(read()))
        ;
      index--;
      return new String(buf, start, 1);
    }
  }

  private boolean identifierChar(char c)
  {
    if (c == '_' || c == '#' || c == '+' || c == '-')
      return true;
    if (Character.isLetterOrDigit(c))
      return true;
    return false;
  }

  private String readUntil(char c)
  {
    int start = index;
    try
    {
      while (read() != c)
	;
      index--;
    }
    catch (Exception e)
    {
    }
    for (int i = start; i < index; i++) {
      if (Character.isSpace(buf[i]))
		buf[i] = ' ';
	  // replace "\n" with 2 spaces
	  if (i < index-1 &&
	  	  buf[i] == '\\' &&
	  	  buf[i+1] == 'n' ) {
	  	buf[i]=' ';
	  	buf[i+1]=' ';
	  }
	}
    return makeMassagedString(buf, start, index - start);
  }

  // this routine is written by Dave Joubert
  private String makeMassagedString(char[] oldBuf, int oldStart, int oldCount)
  {
    char[] newBuf = new char[oldCount] ;
    int newCount = 0 ;

    for( int i=0 ; i < oldCount ; i++ )
    {
      char ch = oldBuf[oldStart+i] ;
      if( ch == '&' )
      {
	int endMarker = -1 ;
	char t = ' ' ;
	for( int j=i+1 ; (endMarker<0) && (j<oldCount) ; j++ )
	{
	  if( oldBuf[oldStart+j] == ';' )
	  {
	    /*
	     * Is it one of the special sequences we can handle ?
	     */
	    String s = new String(oldBuf, oldStart+i+1, j-i-1) ;
            if (     s.equals("lt"))	{ t='<'		; endMarker = j ; }
            else if (s.equals("gt"))	{ t='>'		; endMarker = j ; }
            else if (s.equals("amp"))	{ t='&'		; endMarker = j ; }
            else if (s.equals("quot"))	{ t='\"'	; endMarker = j ; }

            else if (s.equals("nbsp"))	{ t='\u00a0'	; endMarker = j ; }
            else if (s.equals("copy"))	{ t='\u00a9'	; endMarker = j ; }
            else if (s.equals("reg"))	{ t='\u00ae'	; endMarker = j ; }

            else if (s.equals("Agrave")){ t='\u00c0'	; endMarker = j ; }
            else if (s.equals("agrave")){ t='\u00e0'	; endMarker = j ; }
            else if (s.equals("Aacute")){ t='\u00c1'	; endMarker = j ; }
            else if (s.equals("aacute")){ t='\u00e1'	; endMarker = j ; }
            else if (s.equals("Acirc")) { t='\u00c2'	; endMarker = j ; }
            else if (s.equals("acirc")) { t='\u00c2'	; endMarker = j ; }
            else if (s.equals("Atilde")){ t='\u00c3'	; endMarker = j ; }
            else if (s.equals("atilde")){ t='\u00e3'	; endMarker = j ; }
            else if (s.equals("Auml"))	{ t='\u00c4'	; endMarker = j ; }
            else if (s.equals("auml"))	{ t='\u00e4'	; endMarker = j ; }
            else if (s.equals("Aring")) { t='\u00c5'	; endMarker = j ; }
            else if (s.equals("aring")) { t='\u00e5'	; endMarker = j ; }
            else if (s.equals("Aelig")) { t='\u00c6'	; endMarker = j ; }
            else if (s.equals("aelig")) { t='\u00e6'	; endMarker = j ; }

            else if (s.equals("Ccedil")){ t='\u00c7'	; endMarker = j ; }
            else if (s.equals("ccedil")){ t='\u00e7'	; endMarker = j ; }

            else if (s.equals("Egrave")){ t='\u00c8'	; endMarker = j ; }
            else if (s.equals("egrave")){ t='\u00e8'	; endMarker = j ; }
            else if (s.equals("Eacute")){ t='\u00c9'	; endMarker = j ; }
            else if (s.equals("eacute")){ t='\u00e9'	; endMarker = j ; }
            else if (s.equals("Ecirc")) { t='\u00ca'	; endMarker = j ; }
            else if (s.equals("ecirc")) { t='\u00ea'	; endMarker = j ; }
            else if (s.equals("Euml"))	{ t='\u00cb'	; endMarker = j ; }
            else if (s.equals("euml"))	{ t='\u00eb'	; endMarker = j ; }

            else if (s.equals("Igrave")){ t='\u00cc'	; endMarker = j ; }
            else if (s.equals("igrave")){ t='\u00ec'	; endMarker = j ; }
            else if (s.equals("Iacute")){ t='\u00cd'	; endMarker = j ; }
            else if (s.equals("iacute")){ t='\u00ed'	; endMarker = j ; }
            else if (s.equals("Icirc")) { t='\u00ce'	; endMarker = j ; }
            else if (s.equals("icirc")) { t='\u00ee'	; endMarker = j ; }
            else if (s.equals("Iuml"))	{ t='\u00cf'	; endMarker = j ; }
            else if (s.equals("iuml"))	{ t='\u00ef'	; endMarker = j ; }

            else if (s.equals("ETH"))	{ t='\u00d0'	; endMarker = j ; }
            else if (s.equals("eth"))	{ t='\u00f0'	; endMarker = j ; }

            else if (s.equals("Ntilde")){ t='\u00d1'	; endMarker = j ; }
            else if (s.equals("ntilde")){ t='\u00f1'	; endMarker = j ; }

            else if (s.equals("Ograve")){ t='\u00d2'	; endMarker = j ; }
            else if (s.equals("ograve")){ t='\u00f2'	; endMarker = j ; }
            else if (s.equals("Oacute")){ t='\u00d3'	; endMarker = j ; }
            else if (s.equals("oacute")){ t='\u00f3'	; endMarker = j ; }
            else if (s.equals("Ocirc")) { t='\u00d4'	; endMarker = j ; }
            else if (s.equals("ocirc")) { t='\u00f4'	; endMarker = j ; }
            else if (s.equals("Otilde")){ t='\u00d5'	; endMarker = j ; }
            else if (s.equals("otilde")){ t='\u00f5'	; endMarker = j ; }
            else if (s.equals("Ouml"))	{ t='\u00d6'	; endMarker = j ; }
            else if (s.equals("ouml"))	{ t='\u00f6'	; endMarker = j ; }
            else if (s.equals("Oslash")){ t='\u00d8'	; endMarker = j ; }
            else if (s.equals("oslash")){ t='\u00f8'	; endMarker = j ; }

            else if (s.equals("Ugrave")){ t='\u00d9'	; endMarker = j ; }
            else if (s.equals("ugrave")){ t='\u00f9'	; endMarker = j ; }
            else if (s.equals("Uacute")){ t='\u00da'	; endMarker = j ; }
            else if (s.equals("uacute")){ t='\u00fa'	; endMarker = j ; }
            else if (s.equals("Ucirc")) { t='\u00db'	; endMarker = j ; }
            else if (s.equals("ucirc")) { t='\u00fb'	; endMarker = j ; }
            else if (s.equals("Uuml"))	{ t='\u00dc'	; endMarker = j ; }
            else if (s.equals("uuml"))	{ t='\u00fc'	; endMarker = j ; }

            else if (s.equals("Yacute")){ t='\u00dd'	; endMarker = j ; }
            else if (s.equals("uacute")){ t='\u00fd'	; endMarker = j ; }

            else if (s.equals("THORN")) { t='\u00de'	; endMarker = j ; }
            else if (s.equals("thorn")) { t='\u00fe'	; endMarker = j ; }

            else if (s.equals("szlig")) { t='\u00df'	; endMarker = j ; }
            else if (s.equals("uuml"))	{ t='\u00ff'	; endMarker = j ; }

            else if (s.charAt(0)=='#')	{
					    // Numeric 
					    int Tot = 0 ;
					    for( int k=+i+2 ; k < j ; k++ )
					    {
						Tot = 10*Tot +
						      oldBuf[oldStart+k] - '0' ;
						endMarker = j ;
					    }
					    t=(char) Tot ;
	    }
	  } // end of == ;
	} // end for j
	if( endMarker > 0 )
	{
	  newBuf[newCount++] = t ;
	  i = endMarker ;		// point to ';' , not '&'
	}
	else
	{
	  /*
	   * We found '&' but did not find the ';'
	   * Do not treat it as special.
	   */
	  newBuf[newCount++] = ch ;
	}
      } // end special
      else
      {
	newBuf[newCount++] = ch ;
      }
    } // end for i
    return new String(newBuf, 0, newCount) ;
  }
}
