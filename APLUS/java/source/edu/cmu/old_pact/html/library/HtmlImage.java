package edu.cmu.old_pact.html.library;


import java.awt.Image;

class HtmlImage
{
  protected int x;
  protected int y;
  protected int w;
  protected int h;
  protected Image img;

  protected HtmlImage(int x, int y, int w, int h, Image img)
  {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.img = img;
  }
  
  protected void delete(){
  	img = null;
  }
}
