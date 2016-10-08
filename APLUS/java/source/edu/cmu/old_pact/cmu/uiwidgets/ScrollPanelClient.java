/*
 * Created on Mar 16, 2005
 *
 */
package edu.cmu.old_pact.cmu.uiwidgets;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.Panel;

public class ScrollPanelClient extends Panel {

	public ScrollPanelClient() {
		super();
	}
	
	public void recalcLayout()
	{
		LayoutManager layout=getLayout();
		if (layout!=null) {
			resize(layout.preferredLayoutSize(this));

		}
	}	
	
	public void paint(Graphics g)
	{
		
		Dimension oldsize;
		Dimension newsize;
		LayoutManager layout=getLayout();
		if (layout!=null) {
			oldsize=size();
			newsize=layout.preferredLayoutSize(this);
			if ((oldsize.width!=newsize.width) || (oldsize.height!=newsize.height)) {
				resize(newsize);
				layout();
			}	
		}
		
		super.paint(g);
	}	
	
	public void scrollToBottom()
	{
		LightComponentScroller scroller=LightComponentScroller.getScroller(this);
		if (scroller!=null) {
			scroller.validate();
			int max=scroller.getVerticalScrollbar().getMaximum();
			if (scroller.getVerticalScrollbar().isVisible()) {
				scroller.getVerticalScrollbar().setValue(max);
			} else
				scroller.getVerticalScrollbar().setValue(0);
			scroller.scroll();
			scroller.validate();
		}	
	}	

	
	public Component add(Component comp)
	{
		super.add(comp);
		
		recalcLayout();
		return comp;	
	}
	
	public void remove(Component comp)
	{
		super.remove(comp);
		recalcLayout();
	}
	
	public void removeAll()
	{
		super.removeAll();
		recalcLayout();
	}	
}		

