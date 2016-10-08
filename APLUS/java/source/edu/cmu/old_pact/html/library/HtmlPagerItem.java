package edu.cmu.old_pact.html.library;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Vector;

class HtmlPagerItem
{
  protected final static int TOP = 1;
  protected final static int MIDDLE = 0;
  protected final static int BOTTOM = -1;
  protected final static int MATHML = -2;
  protected final static int MATHML1 = -3;


  private Color color = null;
  private int length = 0;
  private HtmlImage image = null;
  private int align = 0;
  private Font font = null;
  private String text = null;
  private boolean draggable = false;
  private Color dragColor = new Color(0,250,180); //Color.green;

  protected HtmlPagerItem(Color color, int length)
  {
    this.color = color;
    this.length = length;
  }

  protected HtmlPagerItem(Color color, HtmlImage image, int align)
  {
    this.color = color;
    this.image = image;
    this.align = align;
  }

  protected HtmlPagerItem(Color color, Font font, String text,boolean draggable)
  {
    this.color = color;
    this.font = font;
    this.text = text;
    this.draggable = draggable;
  }
  
  protected HtmlPagerItem(Color color, Font font, String text){
  	this(color,font,text,false);
  }

  protected int getAscent(FontMetrics fm)
  {
    if (font != null)
    {
      return fm.getMaxAscent();
    }
    else if (image != null)
    {
      int h = image.h;
      //if (color != null)
		h += 2;
      switch (align)
      {
      case TOP: return -1;
      case MIDDLE: return h / 2;
      case BOTTOM: return h;
      case MATHML: return 3*h/4 -1;   //3*h/4;
      case MATHML1: return 5*h/8;
      }
      return 0;
    }
    else
    {
      return 7;
    } 
  }

  protected int getHeight(FontMetrics fm)
  {
    if (font != null)
    {
      return fm.getHeight();
    }
    else if (image != null)
    {
      if (color != null)
        return image.h + 6;
      else
        return image.h+4;
    }
    else
    {
      return 14;
    } 
  }

  protected void draw(Graphics g, int x, int y, int ascent, Vector imgs)
  {
    g.setColor(color);
 
    if (font != null)
    {
      g.setFont(font);
	  if(draggable) {
		FontMetrics fm = g.getFontMetrics(font);
		int h = fm.getHeight();
		int w = fm.stringWidth(text);
		int des = fm.getDescent();
		g.setColor(dragColor);
		g.fillRect(x,(y-h+des),w,h+2);
		g.setColor(color);
	  }
      g.drawString(text, x, y);
    }
    else if (image != null)
    {
      int h = image.h;
      int deltaW = 2;
      //  if (color != null)
		h += 2;

      switch (align)
      {
      case TOP: y -= ascent; break;
      case MIDDLE: y -= h/2; break;
      case BOTTOM: y -= h; break;
      case MATHML: y -= ascent; deltaW = 0; break; 
      case MATHML1: y -= ascent; deltaW = 0; break; 
      default: return;
      }
      if (color != null)
      {
		g.drawRect(x, y, image.w + deltaW, h);
		x++;
		y++;
      }
      image.x = x;
      image.y = y;
      imgs.addElement(image);

    }
    else
    {
      g.drawLine(x, y, x + length, y);
    } 
  }
}
