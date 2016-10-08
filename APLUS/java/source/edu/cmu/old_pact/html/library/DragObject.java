package edu.cmu.old_pact.html.library;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Toolkit;

class DragObject {
	protected int startLine;
  	protected int startOffset;
  	protected int endLine;
  	protected int endOffset;
  	protected String text = "theText";
  	protected Font font;
  	// additional properties, used only for the bounds calculation
  	protected int startY  = 0;
  	protected int endY = 0;
  	protected int lineWidth = 0;
  	protected int margin;
  	
  	
  	protected void setText(String text){
  		this.text = text;
  	}
  	
  	public Rectangle getBounds(){
  		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
  		
  		int h;
  		if(endLine == startLine)
  			h = fm.getHeight()+1;
  		else
  			h = fm.getHeight()+fm.getDescent()+fm.getAscent();
  		int w = endOffset-startOffset-fm.stringWidth(" ")-2;
  		int x = startOffset;
  		if(endLine > startLine) {
  			h = h*(endLine-startLine);
  			w = lineWidth-2*margin-fm.stringWidth(" ")-2;
  			x = margin;
  		}
  		return new Rectangle(x,startY,w,h);
  	}	

  	public String toString() {
    	return "Selectable(startLine="+startLine+
			",startOffset="+startOffset+
			",endLine="+endLine+
			",endOffset="+endOffset+
			",text="+text+
			",font="+font+")";
  	}
}
