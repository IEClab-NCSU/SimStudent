package edu.cmu.old_pact.scrollpanel;

/* BevelPanel allows you to display the panel
 *in rased or lowered style
*/

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;

public class BevelPanel extends Panel {

	public final static int FLAT=0;
	public final static int RAISED=1;
	public final static int LOWERED=2;
	public final static int PLAIN=3; 

	int m_Style=FLAT;

	public BevelPanel()
	{
		super();
		layoutComponents();
	}	

	public BevelPanel(int style)
	{
		super();
		layoutComponents();
		setStyle(style);
	}
		
	public Frame getFrame() 
	{
		Component parent = getParent();
		Component root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return ((Frame) root);
	}

	public void layoutComponents()
	{
	}
	public void setStyle(int style)
	{
		m_Style=style;
	}	

	public int getRight(Component comp)
	{
		return comp.location().x+comp.size().width;
	}	
	
	public int getBottom(Component comp)
	{
		int height=Math.max(comp.minimumSize().height,
							Math.max(comp.size().height,comp.preferredSize().height));
		return comp.location().y+height;
	}	 

	public void shadow(Graphics g)
	{
		Color upper_left=null;
		Color lower_right=null;
	
		if (m_Style!=FLAT) {
			if (m_Style==LOWERED) {
				upper_left=getBackground().darker();
				lower_right=getBackground().brighter();
			} else if (m_Style==RAISED) {	
				upper_left=getBackground().brighter();
				lower_right=getBackground().darker();
			} else if (m_Style==PLAIN) {	
				upper_left=getBackground().darker();
				lower_right=getBackground().darker();
			}
	
			Color c=g.getColor();
			Dimension d=size();
			d.width-=1;
			d.height-=1;
			g.setColor(upper_left);
			g.drawLine(0,d.height,0,0);
			g.drawLine(0,0,d.width,0);
			g.setColor(lower_right);
			g.drawLine(d.width,0,d.width,d.height);
			g.drawLine(d.width,d.height,0,d.height);
			g.setColor(c);
		}	
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		shadow(g);
	}		
}	
	

