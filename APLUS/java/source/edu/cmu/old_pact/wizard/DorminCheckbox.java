package edu.cmu.old_pact.wizard;
/**
*	Now constructor doesn't setLabel for Checkbox, it sets the "Name" for ObjectProxy
*	Property "Label" shpuld be set independently
**/	 
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import edu.cmu.pact.Utilities.trace;

public class DorminCheckbox extends Checkbox implements Sharable, ItemListener {
	final int WIDTH = 10;
	final int HEIGHT = 10;
		
	private int x = 0;
	private int y = 0;
	private int width;
	private int height;
	private ObjectProxy checkboxProxy;
	
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_FONT = "dialog";
	
	public DorminCheckbox(ObjectProxy parent){
		super();
		width = WIDTH;
		height = HEIGHT;
		checkboxProxy = new WizardProxy(parent, "Checkbox");
		checkboxProxy.setRealObject(this);
		addItemListener(this);
		setFont(new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_SIZE));
	}
	
	public DorminCheckbox(ObjectProxy parent, String name) {
		this(parent);
		//setLabel(label);
		try {
			setProperty("NAME", name);
		} catch (DorminException e) {
			trace.out(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public DorminCheckbox(ObjectProxy parent, String name, CheckboxGroup group){
		super("", group, false);
		width = WIDTH;
		height = HEIGHT;
		checkboxProxy = new WizardProxy(parent, "Checkbox");
		checkboxProxy.setRealObject(this);
		addItemListener(this);
		setFont(new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_SIZE));
		//setLabel(label);
		try {
			setProperty("NAME", name);
		} catch (DorminException e) {
			trace.out(e.getMessage());
			e.printStackTrace();
		}
	}

	public void itemStateChanged(ItemEvent e){
		boolean isChecked;
		
		if(checkboxProxy != null){
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT", checkboxProxy);
			Vector pNames = new Vector();
			pNames.addElement("ISCHECKED");
			mo.addParameter("PROPERTYNAMES", pNames);
			if (e.getStateChange() == ItemEvent.SELECTED)
				isChecked = true;
			else
				isChecked = false;
			Vector pValues = new Vector();
			pValues.addElement(String.valueOf(isChecked));	
			mo.addParameter("PROPERTYVALUES", pValues);
			checkboxProxy.send(mo);
			pNames.removeAllElements();
			pNames = null;
			pValues.removeAllElements();
			pValues = null;
		}
	}
	
	public ObjectProxy getObjectProxy(){
		return checkboxProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		checkboxProxy = op;
	}
	
	public Dimension preferredSize(){
		return new Dimension(width, height);
	}
	
	public Frame getFrame(){
		Component parent = getParent();
		Component root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return ((Frame) root);
	}
	
	public void refresh(){
		Frame f = getFrame();
		if(f instanceof WizardFrame)
			((WizardFrame)f).refresh();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try {
			if(propertyName.equalsIgnoreCase("LABEL")) {
				setLabel((String)propertyValue);
			}
			else if(propertyName.equalsIgnoreCase("LOCATION")){
				Vector loc = DataConverter.getListValue(propertyName, propertyValue);
				if(loc.size() != 2)
					throw new InvalidPropertyValueException("For Object of type '" + checkboxProxy.type + "', for Property 'Location' value '"+loc+"' isn't acceptable");
				int x_loc = ((Integer)loc.elementAt(0)).intValue();
				int y_loc = ((Integer)loc.elementAt(1)).intValue();
				if(x != x_loc || y != y_loc) {
					x = x_loc;
					y = y_loc;
					reshape(x,y,width,height);
					refresh();
				}
			}
			else if(propertyName.equalsIgnoreCase("WIDTH")){
				int w = DataConverter.getIntValue(propertyName, propertyValue);
				if(width != w){
					width = w;
					reshape(x,y,width,height);
					refresh();
				}
			}
			else if(propertyName.equalsIgnoreCase("HEIGHT")){
				int h = DataConverter.getIntValue(propertyName, propertyValue);
				if(height != h){
					height = h;
					reshape(x,y,width, height);
					refresh();
				}
			}
			else if (propertyName.equalsIgnoreCase("ISVISIBLE"))
				setVisible(DataConverter.getBooleanValue(propertyName, propertyValue));
			else if (propertyName.equalsIgnoreCase("NAME")){
				checkboxProxy.setName((String)propertyValue);
				checkboxProxy.defaultNameDescription();
			}
			else if(propertyName.equalsIgnoreCase("FONT")){
				Font f = ParameterSettings.getFont( propertyValue);
				if(f != null) {
				  if(f.getSize() == 0)
		  			 f = new Font(f.getName(), f.getStyle(), getFont().getSize());
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
			else if (propertyName.equalsIgnoreCase("COLOR")){
				Color c = ParameterSettings.getColor(propertyValue);
				if(c != null)
					setForeground(c);
			}			
			else if (propertyName.equalsIgnoreCase("ISCHECKED")){
				setState(((Boolean)propertyValue).booleanValue());			
			}
			else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
				try {
					int newSize = DataConverter.getIntValue(propertyName, propertyValue);
					setFontSize(newSize);
				} catch(DataFormattingException ex){
  					String st = ex.getMessage()+" for Object of type "+checkboxProxy.type;
  					throw new DataFormatException(st);
  				}
  			}
			else
			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+checkboxProxy.type+"'");
			
  		}
		catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+checkboxProxy.type;
  			throw new DataFormatException(st);
  		}
  	}
  	
  	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}

	public void delete(){
		checkboxProxy = null;
		getParent().remove(this);
		removeItemListener(this);
	}	 

	public void setFontSize(int size) {
		Font currentFont = getFont();
		setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
	}
}
  	