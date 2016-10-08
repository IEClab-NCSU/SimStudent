package edu.cmu.old_pact.dorminbutton;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dataconverter.DataConverter;
import edu.cmu.old_pact.dataconverter.DataFormattingException;
import edu.cmu.old_pact.dormin.DataFormatException;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.InvalidPropertyValueException;
import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.settings.ParameterSettings;

public class DorminButton extends Button implements Sharable{
	private int x = 0;
	private int y = 0;
	private int width = 20;
	private int height = 15;
	public ObjectProxy butProxy;
	
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_FONT = "dialog";
	private String name = null;
	
	public DorminButton(ObjectProxy parent){
		super("");
		createProxy(parent);
		setFont(new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_SIZE));
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage();
			}
		});
	}
	public void createProxy(ObjectProxy parent){
		if(parent != null){
			butProxy = new ButtonProxy(parent, "Button");
			butProxy.setRealObject(this);
		}
	}
	
	// Action request is sent by the container
	public void sendMessage(){
		MessageObject mo = new MessageObject("ActionRequest");
		try{
			ObjectProxy par = butProxy.getContainer();
			mo.addParameter("OBJECT",butProxy.getContainer());
			if(name == null)
				mo.addParameter("Action", getLabel());
			else
				mo.addParameter("Action", name);
			butProxy.getContainer().send(mo);
		} catch (NullPointerException e) { }	
	}
		
	public ObjectProxy getObjectProxy(){
		return butProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		butProxy = op;
	}
	
	public Dimension preferredSize(){
		return new Dimension(width, height);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			if(loc.size() != 2)
				throw new InvalidPropertyValueException("For Object of type '"+butProxy.type+"', for Property 'Location' value '"+loc+"' isn't exceptable");  
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
			setWidth(w);
		}
		else if(propertyName.equalsIgnoreCase("HEIGHT")){
			int h = DataConverter.getIntValue(propertyName,propertyValue);
			setHeight(h);
		}
		else if (propertyName.equalsIgnoreCase("ISVISIBLE"))
			setVisible(DataConverter.getBooleanValue(propertyName,propertyValue));
		// Name is an action command	
		else if (propertyName.equalsIgnoreCase("Name")){
			name = (String)propertyValue;
			butProxy.setName((String)propertyValue);
			butProxy.defaultNameDescription();
		}
		else if (propertyName.equalsIgnoreCase("Label"))
			setLabel((String)propertyValue);
		else if (propertyName.equalsIgnoreCase("COLOR")){
			Color c = ParameterSettings.getColor(propertyValue);
			if(c != null)
				setForeground(c);
		}
		else if(propertyName.equalsIgnoreCase("FONT")){
			Font f = ParameterSettings.getFont( propertyValue);
			if(f != null)
				setFont(f);
		}
		else if(propertyName.equalsIgnoreCase("REMOVE")){
			butProxy.deleteProxy();
		}
		else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
			try {
				int newSize = DataConverter.getIntValue(propertyName, propertyValue);
				setFontSize(newSize);
			} catch(DataFormattingException ex){
  				String st = ex.getMessage()+" for Object of type "+butProxy.type;
  				throw new DataFormatException(st);
  			}
  		}
  		else {
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+butProxy.type+"'"); 
  		}
  		}catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+butProxy.type;
  			throw new DataFormatException(st);
  		}
	}
/*	
	public void setLabel(String lab){
		Font f = getFont();
		FontMetrics fm = getFontMetrics(f);
		int w = fm.stringWidth(lab);
		Dimension dim = getSize(); 
		if(dim.width<(w+10))
			setSize(w+10, dim.height);
		try{
			getParent().repaint();
		} catch (NullPointerException e) { }
		super.setLabel(lab);
	}
*/	
	public void setWidth(int w){
		if(width != w){
			width = w;
			reshape(x,y,width,height);
		}
	}	
	
	public void setHeight(int h){
		if(height != h){
			height = h;
			reshape(x,y,width,height);
		}
	}
	
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
	 
	public void delete(){
		getParent().remove(this);
		butProxy = null;
	}

	public void setFontSize(int size) {
		Font currentFont = getFont();
		setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
	}
}