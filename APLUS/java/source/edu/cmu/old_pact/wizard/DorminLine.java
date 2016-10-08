package edu.cmu.old_pact.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.InvalidPropertyValueException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.settings.ParameterSettings;

public class DorminLine implements Sharable, Paintable{
	private int width = 1;
	private int length = 10;
	private int xSt = 0;
	private int ySt = 0;
	Color color = Color.black;
	private ObjectProxy lineProxy;
	Component realParent = null;
	// default horizontal line
	private int degree = 0;
	
	
	public DorminLine(ObjectProxy parent){
		lineProxy = new WizardProxy(parent, "Line");
		lineProxy.setRealObject(this);
		
		realParent = (Component)parent.getObject();
	}
	
	public ObjectProxy getObjectProxy(){
		return lineProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		lineProxy = op;
	}
	
	public void delete(){
		lineProxy = null;
	}
	
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
	
	public void repaint(){
		if(realParent != null)
			realParent.repaint();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			if(loc.size() != 2)
				throw new InvalidPropertyValueException("For Object of type '"+lineProxy.type+"', for Property 'Location' value '"+loc+"' isn't exceptable");  
			xSt = ((Integer)loc.elementAt(0)).intValue();
			ySt = ((Integer)loc.elementAt(1)).intValue();
			repaint();
			//reshape(x_loc,y_loc,length,height);
		}
		else if(propertyName.equalsIgnoreCase("WIDTH")){
			int w = DataConverter.getIntValue(propertyName,propertyValue);
			width = w;
			repaint();
			//Point location = getLocation();
			//reshape(location.x, location.y,length, width+2);
			
		}
		else if(propertyName.equalsIgnoreCase("LENGTH")){
			int l = DataConverter.getIntValue(propertyName,propertyValue);
			length = l;
			repaint();
			//Point location = getLocation();
			//reshape(location.x, location.y,length, height+2);
		}
		else if(propertyName.equalsIgnoreCase("ANGLE")){
			int d = DataConverter.getIntValue(propertyName,propertyValue);
			degree = d;
			repaint();
		}
		else if (propertyName.equalsIgnoreCase("COLOR")){
			Color c = ParameterSettings.getColor(propertyValue);
			if(c != null){
				color = c;
				repaint();
			}
		}
		else if (propertyName.equalsIgnoreCase("NAME")){
			lineProxy.setName((String)propertyValue);
			lineProxy.defaultNameDescription();
		}
		else 
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+lineProxy.type+"'"); 
  		
  		}catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+lineProxy.type;
  			throw new DataFormatException(st);
  		}
  	}
  	
  	protected synchronized Point getEndPoint(){
  		double radians = 2*Math.PI*degree/360;
    	int xEnd = xSt+(int)(length*Math.cos(radians));
    	int yEnd = ySt-(int)(length*Math.sin(radians));
    	return new Point(xEnd, yEnd);
  	}
  	
  	public Rectangle getBounds(){
  		Point p = getEndPoint();
  		int yEnd;
  		if(ySt == p.y || ySt > p.y)
  			yEnd = ySt;
  		else 
  			yEnd = p.y;
  		return new Rectangle(xSt, ySt, (int)Math.abs(xSt-p.x)+1, (int)Math.abs(ySt-p.y)+1);
  	}
		

	public void paint(Graphics g){
    	Color origColor = g.getColor();
    	g.setColor(color);
   	
    	Point p = getEndPoint();
    	int xEnd = p.x;
    	int yEnd = p.y;
    	if (width == 1)
      		g.drawLine(xSt, ySt, xEnd, yEnd);
    	else {
      		double angle;
      		double halfWidth = ((double)width)/2.0;
      		double deltaX = (double)(xEnd - xSt);
      		double deltaY = (double)(yEnd - ySt);
      		if (xSt == xEnd)
				angle=Math.PI;
      		else
				angle=Math.atan(deltaY/deltaX)+Math.PI/2;
      		int xOffset = (int)(halfWidth*Math.cos(angle));
      		int yOffset = (int)(halfWidth*Math.sin(angle));
      		int[] xCorners = { 	xSt-xOffset, xEnd-xOffset+1,
			 					xEnd+xOffset+1, xSt+xOffset };
      		int[] yCorners = { 	ySt-yOffset, yEnd-yOffset,
			 					yEnd+yOffset+1, ySt+yOffset+1 };
      		g.fillPolygon(xCorners, yCorners, 4);
      	}
   	
    	g.setColor(origColor);
    }
}