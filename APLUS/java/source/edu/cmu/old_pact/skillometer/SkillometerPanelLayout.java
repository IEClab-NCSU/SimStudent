/*
 * Created on Mar 16, 2005
 *
 */
package edu.cmu.old_pact.skillometer;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;

public class SkillometerPanelLayout implements java.awt.LayoutManager {


    public SkillometerPanelLayout() 
    {
            super();
    }

    public void addLayoutComponent(java.lang.String name, java.awt.Component comp) {}

    public java.awt.Dimension preferredLayoutSize(java.awt.Container parent)
    {
    	SkillometerPanel sp=(SkillometerPanel)parent;
		Enumeration _enum = sp.skills.elements();
		Rectangle bounds=new Rectangle(0,0,0,0);
		Rectangle rect;
		FontMetrics fm=sp.getFontMetrics(sp.getFont());

		while (_enum.hasMoreElements()) {
			rect=((Skill) _enum.nextElement()).getBoundingBox(fm); 
			bounds=unionRect(bounds,rect);
		}
		return new Dimension(bounds.width,bounds.height);	
	}
	
	Rectangle unionRect(Rectangle rect1,Rectangle rect2) {
		int width,height;
		Point TopLeft=new Point(0,0);
		Point BottomRight=new Point(0,0);
	
		TopLeft.x=Math.min(rect1.x,rect2.x);
		TopLeft.y=Math.min(rect1.y,rect2.y);
		BottomRight.x=Math.max(rect1.x+rect1.width,rect2.x+rect2.width);
		BottomRight.y=Math.max(rect1.y+rect1.height,rect2.y+rect2.height);
		width=BottomRight.x-TopLeft.x;
		height=BottomRight.y-TopLeft.y;
	
		return new Rectangle(TopLeft.x,TopLeft.y,width,height);
	}	
	
    public void layoutContainer(java.awt.Container parent) {}

    public java.awt.Dimension minimumLayoutSize(java.awt.Container parent) {
            return preferredLayoutSize(parent);
    }

    public void removeLayoutComponent(java.awt.Component comp) {}
    
}
