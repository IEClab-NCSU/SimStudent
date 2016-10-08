package edu.cmu.old_pact.wizard;

//import cmu.toolagent.AdjustableHtmlPanel;

import java.awt.Dimension;
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
import edu.cmu.old_pact.htmlPanel.AdjustableHtmlPanel;

public class DorminHtmlPanel extends AdjustableHtmlPanel implements Sharable{
	private ObjectProxy htmlProxy;
	
	public DorminHtmlPanel(ObjectProxy parent){
		super(300, 100,0);
		htmlProxy = new WizardProxy(parent, "htmlPanel");
		htmlProxy.setRealObject(this);
		// applications only!!
		setImageBase("file:///"+System.getProperty("user.dir"));
	}
	
	public ObjectProxy getObjectProxy(){
		return htmlProxy;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		htmlProxy = op;
	}
	
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException{
		return null;
	}
	public void delete(){
		removeAll();
		htmlProxy = null;
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("LOCATION")){
			Vector loc = DataConverter.getListValue(propertyName,propertyValue);
			if(loc.size() != 2)
				throw new InvalidPropertyValueException("For Object of type '"+htmlProxy.type+"', for Property 'Location' value '"+loc+"' isn't exceptable");  
			int x_loc = ((Integer)loc.elementAt(0)).intValue();
			int y_loc = ((Integer)loc.elementAt(1)).intValue();
			Dimension dim = getSize();
			reshape(x_loc,y_loc,dim.width,dim.height);
			if(getParent() != null)
				getParent().validate();
		}
		else if(propertyName.equalsIgnoreCase("WIDTH")){
			int w = DataConverter.getIntValue(propertyName,propertyValue);
			setWidth(w);
			Rectangle bounds = getBounds();
			reshape(bounds.x,bounds.y,w,bounds.height);
		}
		else if(propertyName.equalsIgnoreCase("HEIGHT")){
			int h = DataConverter.getIntValue(propertyName,propertyValue);
			setHeight(h);
			Rectangle bounds = getBounds();
			reshape(bounds.x,bounds.y,bounds.width,h);
		}
		else if (propertyName.equalsIgnoreCase("ISVISIBLE"))
			setVisible(DataConverter.getBooleanValue(propertyName,propertyValue));
		else if (propertyName.equalsIgnoreCase("Name")){
			htmlProxy.setName((String)propertyValue);
			htmlProxy.defaultNameDescription();
		}
		else if(propertyName.equalsIgnoreCase("HIGHLIGHT")) {
			Vector tags;
			if(	propertyValue.toString().equalsIgnoreCase("FALSE") ||
				propertyValue.toString().equals("[0, 0]")) // empty tags
				tags = new Vector();
			else
				tags = DataConverter.getListValue(propertyName,propertyValue);
			showtxt(tags);
		}
		else if (propertyName.equalsIgnoreCase("FONTSIZE")) {
			int fontSize = DataConverter.getIntValue(propertyName,propertyValue);
			setFontSize(fontSize);
		}
		else if(propertyName.equalsIgnoreCase("TEXT")) {
			displayHtml((String)propertyValue);
		}
  		else 
  			throw new NoSuchPropertyException("No such property: "+propertyName+" for Object of type '"+htmlProxy.type+"'"); 
  		
  		}catch (DataFormattingException ex){
  			String st = ex.getMessage()+" for Object of type "+htmlProxy.type;
  			throw new DataFormatException(st);
  		}
	}
}