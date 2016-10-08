package edu.cmu.old_pact.wizard;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.OrderedTextField;
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
import edu.cmu.old_pact.linkvector.LinkVector;
import edu.cmu.old_pact.scrollpanel.LightComponentScroller;
import edu.cmu.old_pact.settings.ParameterSettings;



public class DorminPanel extends LightComponentScroller implements Sharable, 
																	PropertyChangeListener,
																	CanAskForHelp,
																	FocusListener {
	final int WIDTH = 300;
	final int HEIGHT = 100;
	private ObjectProxy panelProxy;
	private NullLayoutPanel scrollPanel;
	private LinkVector links = null;
	private boolean ownLinkVector = false;
	private Frame frame;
	
	private FastProBeansSupport changes = new FastProBeansSupport(this);
		
	public DorminPanel(ObjectProxy parent) {
		super();
		
		createLinkVectors();
		
		scrollPanel = new NullLayoutPanel();
		scrollPanel.setLayout(null);
		setComponent(scrollPanel);
		setSize(WIDTH, HEIGHT);
		panelProxy = new WizardProxy(parent, "Panel");
		panelProxy.setRealObject(this);
		addFocusListener(this);
	}
	
	public ObjectProxy getObjectProxy() {
		return panelProxy;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void createLinkVectors(){
		if(links != null){
			links.delete();
			links = null;
		}
		links = new LinkVector();
		ownLinkVector = true;
	}
	
	public void addLinVector(LinkVector l){
		if(links != null && ownLinkVector){
			links.delete();
			links = null;
		}
		links = l;
		ownLinkVector = false;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		panelProxy = op;
	}
	
	public Hashtable getProperty(Vector propertynames) throws NoSuchPropertyException {
		return null;
	}
	public boolean askedForHelp(){
		if( panelProxy != null){
			MessageObject mo = new MessageObject("GETHINT");
			mo.addParameter("OBJECT",panelProxy);
			panelProxy.send(mo);
			return true;
		}
		return false;
	}
	
	public void focusGained(FocusEvent evt){
		changes.firePropertyChange("FOCUSGAINED", null, this);
	}
	public void focusLost(FocusEvent evt){
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException {
		try{
			if(propertyName.equalsIgnoreCase("LOCATION")) {
				Vector loc = DataConverter.getListValue(propertyName, propertyValue);
				if (loc.size() != 2)
					throw new InvalidPropertyValueException("For Object of type '" + panelProxy.type + "', for Property 'Location' value '"+loc+"' isn't exceptable");
				int x_loc = ((Integer)loc.elementAt(0)).intValue();
				int y_loc = ((Integer)loc.elementAt(1)).intValue();
				Dimension dim = getSize();
				reshape(x_loc, y_loc, dim.width, dim.height);
				if(getParent() != null)
					getParent().validate();
			}
			else if (propertyName.equalsIgnoreCase("NAME")){
				panelProxy.setName((String)propertyValue);
				panelProxy.defaultNameDescription();
			}
			else if(propertyName.equalsIgnoreCase("WIDTH")) {
				Dimension dim = getSize();
				int w = DataConverter.getIntValue(propertyName,propertyValue);
				if(dim.width != w){
					setSize(w, dim.height);
				}	
			}
			else if(propertyName.equalsIgnoreCase("HEIGHT")){
				Dimension dim = scrollPanel.getSize();
				int h = DataConverter.getIntValue(propertyName,propertyValue);
				if(dim.height != h){
					setSize(dim.width,h);
				}	
			}
			else if (propertyName.equalsIgnoreCase("ISVISIBLE"))
				setVisible(DataConverter.getBooleanValue(propertyName,propertyValue));
			else if (propertyName.equalsIgnoreCase("Name")){
				panelProxy.setName((String)propertyValue);
				panelProxy.defaultNameDescription();
			}
			else if (propertyName.equalsIgnoreCase("FONT")) {
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
			else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
				try {
					int newSize = DataConverter.getIntValue(propertyName, propertyValue);
					Font currentFont = getFont();
					Font newFont = new Font(currentFont.getName(), currentFont.getStyle(), newSize);
					setFont(newFont);
					scrollPanel.setFont(newFont);
					//setFontSize(newSize);
				} catch(DataFormattingException ex){
  					String st = ex.getMessage()+" for Object of type "+panelProxy.type;
  					throw new DataFormatException(st);
  				}
			}
			else 
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+panelProxy.type+"'");
		}
		catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+panelProxy.type;
  			throw new DataFormatException(st);
  		}
	}
	
	public void setSize(int w, int h) {
		super.setSize(w,h);
		scrollPanel.setSize(w,h);
	}
	
	public void setFont(Font f) {
	
		if(f.getSize() == 0)
		  f =new Font(f.getName(), f.getStyle(), getFont().getSize());
		  
		scrollPanel.setFont(f);		
		Component[] children;		
		children = scrollPanel.getComponents();
		
		for(int i=0;i<children.length;i++) {
			children[i].setFont(f);
		}
	 }
	 
	public void delete(){
		if(links != null && ownLinkVector)
			links.delete();
		links = null;
		panelProxy = null;
		
		removeFocusListener(this);
		
		getParent().remove(this);
		
		scrollPanel.removeAll();
		removeAll();
	}
	
	public void addObject(Object comp){
		if(comp instanceof DorminChoice ){
			getFrame();
			if(frame instanceof PropertyChangeListener)
				((DorminChoice)comp).addPropertyChangeListener((PropertyChangeListener)frame);
			
		}
		else if(comp instanceof OrderedTextField ){
			getFrame();
			if(frame instanceof PropertyChangeListener)
				((OrderedTextField)comp).addPropertyChangeListener((PropertyChangeListener)frame);
			
		}
		if(links != null && comp instanceof OrderedTextField){
			OrderedTextField otf = (OrderedTextField)comp;
//			links.addVecticalLink(otf);
//			links.addHorisontalLink(otf);
			otf.addPropertyChangeListener(links);
			otf.addPropertyChangeListener(this);
		}
		scrollPanel.addObject(comp);
		refresh();

	}
	
	public void refresh() {
		Container parent = getParent();
		
		while(!((parent instanceof DorminPanel) || (parent instanceof WizardFrame) || (parent == null)))
			parent = parent.getParent();
		if (parent != null) {
			if(parent instanceof WizardFrame) 
				((WizardFrame)parent).refresh();
			else if(parent instanceof DorminPanel)
				((DorminPanel)parent).refresh();
		}
	}
	
	Frame getFrame() {
		if(frame != null)
			return frame;
		Component parent = getParent();
		Component root = null;
		
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		frame = (Frame)root;
		return frame;
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		if(evt.getPropertyName().equalsIgnoreCase("REFRESH")) 
			refresh();
	}
}
		