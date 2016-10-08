package edu.cmu.old_pact.wizard;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeListener;
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
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.settings.ParameterSettings;

public class DorminChoice extends Choice implements Sharable,
													ItemListener,
													CanAskForHelp, 
													FocusListener{
	private int x = 0;
	private int y = 0;
	private int width = 10;
	private int height = 10;
	private ObjectProxy chProxy;
	private String selectedItem = "";
	
	private static final int DEFAULT_SIZE = 10;
	private static final String DEFAULT_FONT = "dialog";
	
	private FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public DorminChoice(ObjectProxy parent){
		super();
		
		setFont(new Font(DEFAULT_FONT, Font.PLAIN, DEFAULT_SIZE));
		chProxy = new WizardProxy(parent, "Choice");
		chProxy.setRealObject(this);
		addItemListener(this);
		addFocusListener(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void itemStateChanged(ItemEvent e){
		// workaround: ignoring Focus events on MAC
		if(isEnabled())
			changes.firePropertyChange("FOCUSGAINED", null, this);
		
		if(chProxy != null && !selectedItem.equals((String)e.getItem())){
			selectedItem = (String)e.getItem();
			MessageObject mo = new MessageObject("NOTEPROPERTYSET");
			mo.addParameter("OBJECT",chProxy);
			
			Vector pNames = new Vector();
			pNames.addElement("SELECTEDITEM");
			mo.addParameter("PROPERTYNAMES", pNames);
			Vector pValues = new Vector();
			pValues.addElement(e.getItem().toString());
			mo.addParameter("PROPERTYVALUES", pValues);
			chProxy.send(mo);
			pNames.removeAllElements();
			pNames = null;
			pValues.removeAllElements();
			pValues = null;
		}
	}	
		
	public ObjectProxy getObjectProxy(){
		return chProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		chProxy = op;
	}
	
	public Dimension preferredSize(){
		return new Dimension(width, height);
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			if(loc.size() != 2)
				throw new InvalidPropertyValueException("For Object of type '"+chProxy.type+"', for Property 'Location' value '"+loc+"' isn't acceptable");  
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
		else if (propertyName.equalsIgnoreCase("ISENABLED")){
			boolean enabled = DataConverter.getBooleanValue(propertyName,propertyValue);
			setEnabled(enabled);
			if(!enabled){
				if(getParent() != null && getParent() instanceof DorminPanel)
					changes.firePropertyChange("FOCUSGAINED", null, getParent());
				else
					changes.firePropertyChange("FOCUSGAINED", this, null);
			}
		}
		else if (propertyName.equalsIgnoreCase("NAME")){
			chProxy.setName((String)propertyValue);
			chProxy.defaultNameDescription();
		}
		else if(propertyName.equalsIgnoreCase("FONT")){
			Font f = ParameterSettings.getFont( propertyValue);
			if(f != null) {
			   if(f.getSize() == 0)
		  		  f =new Font(f.getName(), f.getStyle(), getFont().getSize());
			   setFont(f);
			}	
			// workaround MRJ 2.2 bug.
			if(getParent() != null) {
				Frame frame = getFrame();
				if(frame instanceof WizardFrame)
					((WizardFrame)frame).refresh();
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
		else if(propertyName.equalsIgnoreCase("ADD")){
			Vector items = DataConverter.getListValue(propertyName,propertyValue);
			int s = items.size();
			if(s ==0) return;
			String toadd;
			for(int i=0; i<s; i++){
				toadd = (String)items.elementAt(i);
				// workaround MRJ 2.2 bug.
				if("".equals(toadd)){
					addItem("");
				}
				else
					addItem(toadd);
			}
			selectedItem =  (String)items.elementAt(0);
			toadd = null;
			if(getParent() != null)
				getParent().repaint();
			checkEnabled();
		}
		else if(propertyName.equalsIgnoreCase("REMOVEITEM")){
			remove((String)propertyValue);
			checkEnabled();
		}
		else if(propertyName.equalsIgnoreCase("REMOVEALL")){
			removeAll();
			checkEnabled();
		}
		else if(propertyName.equalsIgnoreCase("REMOVE")){
			chProxy.deleteProxy();
		}
		else if(propertyName.equalsIgnoreCase("SELECTEDITEM")) {
			try{
				select((String)propertyValue);
				selectedItem = (String)propertyValue;
			} catch (IllegalArgumentException e){
				throw new NoSuchPropertyException("No such Item: "+(String)propertyValue+" for Object of type '"+chProxy.type+"'"); 
  			}
		}
		else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
			try {
				int newSize = DataConverter.getIntValue(propertyName, propertyValue);
				setFontSize(newSize);
			} catch(DataFormattingException ex){
  				String st = ex.getMessage()+" for Object of type "+chProxy.type;
  				throw new DataFormatException(st);
  			}
  		}
  		else 
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+chProxy.type+"'"); 
  		}catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+chProxy.type;
  			throw new DataFormatException(st);
  		}
	}
	
	private void checkEnabled(){
		int s = getItemCount();
		if(s == 0 && isEnabled())
			setEnabled(false);
		else if(s > 0 && !isEnabled())
			setEnabled(true);
	}
	
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
	
	public void focusGained(FocusEvent evt){
		if(isEnabled())
			changes.firePropertyChange("FOCUSGAINED", null, this);
		else
			changes.firePropertyChange("FOCUSGAINED", this, null);
	}
	public void focusLost(FocusEvent evt){
	}
	
	public boolean askedForHelp(){
		if( chProxy != null && isEnabled()){
			MessageObject mo = new MessageObject("GETHINT");
			mo.addParameter("OBJECT",chProxy);
			chProxy.send(mo);
			return true;
		}
		return false;
	}
	
	private Frame getFrame() {
		Component parent = getParent();
		Component root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return ((Frame) root);
	}	
	 
	public void delete(){
		chProxy = null;
		if(getParent() != null)
			getParent().remove(this);
		removeAll();
		removeItemListener(this);
		removeFocusListener(this);
	}
	
	public void setFontSize(int size) {
		Font currentFont = getFont();
		setFont(new Font(currentFont.getName(), currentFont.getStyle(), size));
	}
}