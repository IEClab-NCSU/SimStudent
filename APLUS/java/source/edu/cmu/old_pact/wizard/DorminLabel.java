package edu.cmu.old_pact.wizard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
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

public class DorminLabel extends Label implements Sharable{
	private int x = 0;
	private int y = 0;
	private int width = 10;
	private int height = 10;
	private ObjectProxy labProxy;
	
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_FONT = "dialog";
	
	public DorminLabel(ObjectProxy parent){
		super("");
		labProxy = new WizardProxy(parent, "Label");
		labProxy.setRealObject(this);
		setFont(new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_SIZE));
	}	
		
	public ObjectProxy getObjectProxy(){
		return labProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		labProxy = op;
	}
	
	public Dimension preferredSize(){
		return new Dimension(width, height);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			if(loc.size() != 2)
				throw new InvalidPropertyValueException("For Object of type '"+labProxy.type+"', for Property 'Location' value '"+loc+"' isn't exceptable");  
			int x_loc = ((Integer)loc.elementAt(0)).intValue();
			int y_loc = ((Integer)loc.elementAt(1)).intValue();
			if(x != x_loc || y != y_loc) {
				x = x_loc;
				y = y_loc;
				reshape(x,y,width,height);
			}
		}
		else if(propertyName.equalsIgnoreCase("WIDTH")){
			int w = DataConverter.getIntValue(propertyName,propertyValue);
			if(width != w){
				width = w;
				reshape(x,y,width,height);
			}
		}
		else if(propertyName.equalsIgnoreCase("HEIGHT")){
			int h = DataConverter.getIntValue(propertyName,propertyValue);
			if(height != h){
				height = h;
				reshape(x,y,width,height);
			}
		}
		else if (propertyName.equalsIgnoreCase("ISVISIBLE"))
			setVisible(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if(propertyName.equalsIgnoreCase("Text"))
			setText((String)propertyValue);
		else if (propertyName.equalsIgnoreCase("Name")){
			labProxy.setName((String)propertyValue);
			labProxy.defaultNameDescription();
		}
		else if (propertyName.equalsIgnoreCase("COLOR")){
			Color c = ParameterSettings.getColor(propertyValue);
			if(c != null)
				setForeground(c);
		}
		else if(propertyName.equalsIgnoreCase("FONT")){
			Font f = ParameterSettings.getFont( propertyValue);
			if(f != null){
				if(f.getSize() == 0)
		  			f =new Font(f.getName(), f.getStyle(), getFont().getSize());
				setFont(f);
			}
		}
		else if(propertyName.equalsIgnoreCase("FONTSTYLE")){
			int intStyle = ParameterSettings.getFontStyle((String)propertyValue);
			Font f = getFont();
			if(f != null) {				
				Font newF = new Font(f.getName(), intStyle, f.getSize());
				setFont(newF);
			}
		}
		else if(propertyName.equalsIgnoreCase("REMOVE")){
			labProxy.deleteProxy();
		}
		else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
			try {
				int newSize = DataConverter.getIntValue(propertyName, propertyValue);
				setFontSize(newSize);
			} catch(DataFormattingException ex){
  				String st = ex.getMessage()+" for Object of type "+labProxy.type;
  				throw new DataFormatException(st);
  			}
  		}
  		else {
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+labProxy.type+"'"); 
  		}
  		}catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+labProxy.type;
  			throw new DataFormatException(st);
  		}
	}
	
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
	 
	public void delete(){
		if(getParent() != null)
			getParent().remove(this);
		labProxy = null;
	}

	public void setFontSize(int size) {
		Font currentFont = getFont();
		setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
	}
}