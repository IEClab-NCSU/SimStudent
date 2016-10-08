package edu.cmu.old_pact.html.library;


import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

class HtmlPagerLine
{
  private Vector offsets = new Vector();
  private Vector items = new Vector();
  private int count = 0;
  private int height = 0;
  private int ascent = 0;
  private int descent = 0;
  
  protected HtmlPagerLine()
  {
  }

  protected int getHeight()
  {
    if (ascent + descent < height)
      return height;
    else
      return ascent + descent;
  }

  protected void addItem(FontMetrics fm, int horOffset, HtmlPagerItem item)
  {
    offsets.addElement(Integer.valueOf(String.valueOf(horOffset)));
    items.addElement(item);
    count++;

    int h = item.getHeight(fm);
    if (h > height)
      height = h;
    int a = item.getAscent(fm);
    if (a >= 0)
    {
      if (a > ascent)
	ascent = a;
      if (h - a > descent)
	descent = h - a;
    }
  }

  protected void translate(int w)
  {
    for (int i = 0; i < count; i++)
    {
      int x = ((Integer)offsets.elementAt(i)).intValue();
      offsets.setElementAt(Integer.valueOf(String.valueOf(x + w)), i);
    }
  }

  protected void draw(Graphics g, int verOffset, Vector imgs)
  {
    for (int i = 0; i < count; i++)
    {
      int horOffset = ((Integer)offsets.elementAt(i)).intValue();
      HtmlPagerItem item = (HtmlPagerItem)items.elementAt(i);
      item.draw(g, horOffset, verOffset + ascent, ascent, imgs);
    }
  }
  
  protected void delete(){
  	offsets.removeAllElements();
  	offsets = null;
  	items.removeAllElements();
  	items = null;
  }
}
