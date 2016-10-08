package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;

/* StyleScrollerPanel allows you to display the panel
 *in a specific style
*/

public class StyleScrollerPanel extends Panel {

	final static int FLAT=0;
	final static int RAISED=1;
	final static int LOWERED=2;

	int m_Style=FLAT;

	public StyleScrollerPanel()
	{
		super();
		layoutComponents();
	}	

	public Dimension preferredSize() {
		return size();
	}
	
	public Dimension minimumSize() {
		return size();
	}
	
	Frame getFrame() 
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
}	
	