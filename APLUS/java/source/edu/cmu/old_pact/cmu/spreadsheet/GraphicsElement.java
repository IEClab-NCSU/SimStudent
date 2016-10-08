package edu.cmu.old_pact.cmu.spreadsheet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

class GraphicsElement {
	
	boolean m_visible=true;
	int m_size=1;
	int m_style=0;
	Color m_color=Color.black;

	public GraphicsElement() {
	}
	
	public GraphicsElement(int dimension,int style,Color color) {
		setAttributes(dimension,style,color);
	}
	
	public GraphicsElement(GraphicsElement elem) {
		setAttributes(elem);
	}	
	
	public void setStyle(int style) {
		m_style=style;
	}
	
	public void setDimension(int size) {
		m_size=size;
	}
	
	public void setColor(Color color) {
		m_color=color;
	}
	
	public int getStyle() {
		return m_style;
	}
	
	public int getDimension() {
		return m_size;
	}		
	
	public Color getColor() {
		return m_color;
	}
	
	public void setAttributes(int dimension,int style,Color color) {
		setDimension(dimension);
		setStyle(style);
		setColor(color);
	}
	
	public void setAttributes(GraphicsElement elem) {
		setAttributes(elem.getDimension(),elem.getStyle(),elem.getColor());
	}
	
	public GraphicsElement getAttributes() {
		return new GraphicsElement(this);
	}	
	
	public void setVisible(boolean visible) {
		m_visible=visible;
	}
		
	
	public void paint(Graphics g) {	
		if (m_visible) {
			Color oldColor=g.getColor();
			IdPaint(g);
			g.setColor(oldColor);
		}	
	}
	
		
	static public Rectangle unionRect(Rectangle rect1,Rectangle rect2)
	{
		int width,height;
		Point TopLeft=new Point(0,0);
		Point BottomRight=new Point(0,0);
		
		try{
		TopLeft.x=Math.min(rect1.x,rect2.x);
		TopLeft.y=Math.min(rect1.y,rect2.y);
		BottomRight.x=Math.max(rect1.x+rect1.width,rect2.x+rect2.width);
		BottomRight.y=Math.max(rect1.y+rect1.height,rect2.y+rect2.height);
		width=BottomRight.x-TopLeft.x;
		height=BottomRight.y-TopLeft.y;
		
		return new Rectangle(TopLeft.x,TopLeft.y,width,height);
		} catch (NullPointerException e) { 
			//System.out.println("GraphicsElement unionRect "+e.toString());
		}
		return null;
	}	
		
	static public Rectangle intersectRect(Rectangle rect1,Rectangle rect2)
	{
		int width,height;
		Point TopLeft=new Point(0,0);
		Point BottomRight=new Point(0,0);
		
		TopLeft.x=Math.max(rect1.x,rect2.x);
		TopLeft.y=Math.max(rect1.y,rect2.y);
		BottomRight.x=Math.min(rect1.x+rect1.width,rect2.x+rect2.width);
		BottomRight.y=Math.min(rect1.y+rect1.height,rect2.y+rect2.height);
		width=BottomRight.x-TopLeft.x;
		height=BottomRight.y-TopLeft.y;
		width=Math.max(width,0);
		height=Math.max(height,0);
		return new Rectangle(TopLeft.x,TopLeft.y,width,height);
	}			
	

	
	public void IdPaint(Graphics g) {
	}
}				