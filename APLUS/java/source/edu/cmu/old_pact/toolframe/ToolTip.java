package edu.cmu.old_pact.toolframe;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Window;

import edu.cmu.old_pact.cmu.uiwidgets.StaticLayout;

public class ToolTip extends Window {

	String m_Text;

	public ToolTip(Frame parent)
	{
		super(parent);
		setLayout(new StaticLayout());
		setBackground(Color.white);
		setForeground(Color.black);
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			setFont(new Font("geneva",0,12));
		else
			setFont(new Font("arial",0,12));
		//pack();
		resize(100,20);
		//setSize(100,20);

	}
	
	public void setText(String text)
	{
		m_Text=text;
		//resize(preferredSize());
		setSize(preferredSize());
	}
	
  	public Dimension boundingBox()
  	{
 		Dimension Size=new Dimension(0,0);
  		FontMetrics fm=getFontMetrics(getFont());
  		Size.width=fm.stringWidth(m_Text);
  		Size.height=fm.getHeight();
  		return Size;
  	}	
  	
  	public Dimension preferredSize()
  	{
  		Dimension Size=boundingBox();
  		FontMetrics fm=getFontMetrics(getFont());
  		Size.width+=2*fm.stringWidth(" ");
  		Size.height=6*Size.height/5;
  		return Size;
  	}	
  
  public void paint(Graphics g) 
  {
  	Font oldFont=g.getFont();
  	Color oldColor=g.getColor();
  	FontMetrics fm = getFontMetrics(getFont());
    Dimension Size=preferredSize();
    Point ori=new Point(0,0); 
    
    ori.x=fm.stringWidth(" ");
    ori.y=(fm.getAscent()*Size.height)/(fm.getAscent()+fm.getDescent());
    
    super.paint(g);
    g.setFont(getFont());
    g.setColor(Color.black);
    g.drawString(m_Text,ori.x,ori.y);
    g.drawRect(0,0,Size.width-1,Size.height-1);
    
    g.setFont(oldFont);
    g.setColor(oldColor);
   } 
}    
