package edu.cmu.old_pact.html.library;


import java.net.URL;

class Href
{
  protected int startLine;
  protected int startOffset;
  protected int endLine;
  protected int endOffset;
  protected URL url;

  public String toString()
  {
    return "Href(startLine="+startLine+
		",startOffset="+startOffset+
		",endLine="+endLine+
		",endOffset="+endOffset+
		",url="+url+")";
  }
}
