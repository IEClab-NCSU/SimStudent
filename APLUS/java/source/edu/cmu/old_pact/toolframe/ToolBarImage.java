package edu.cmu.old_pact.toolframe;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import edu.cmu.old_pact.settings.Settings;


public class ToolBarImage extends Canvas {
	Point m_MousePos=new Point(0,0);
	Image myImage=null;
	
	public ToolBarImage(String ImageFile)
	{
		super();
		setImage(ImageFile);
	}
		
	public Point getLocationInFrame()
	{
		Point location=new Point(0,0);
		Component item = this;
		Component parent=getParent();
		
		do {
			location.x+=item.location().x;
			location.y+=item.location().y;
			item=parent;
			parent=item.getParent();
		} while (parent!=null);
		return location;
	}
	
	public Dimension preferredSize()
	{
		return size();
	}	

	public void setImage(String imageFile)
	{
		//myImage = Toolkit.getDefaultToolkit().getImage(ImageFile);
		myImage = Settings.loadImage(this, imageFile);
		if (myImage==null)
		    throw new Error ("Image: " + imageFile + " could not be found.");
    }
	
	public void paint(Graphics g)
	{
		Dimension dim=size();
		Color old_color=g.getColor();
		int xPos,yPos;
		
		xPos=dim.width/2-myImage.getWidth(this)/2-1;
		yPos=dim.height/2-myImage.getHeight(this)/2;
		
		g.setColor(getBackground());
		g.fillRect(0,0,dim.width,dim.height);
		//g.drawImage(myImage,xPos,yPos,this);
		g.drawImage(myImage,0,0,this);
		
		g.setColor(old_color);
	}		
}	



