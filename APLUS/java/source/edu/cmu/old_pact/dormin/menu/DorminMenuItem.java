//d:/Pact-CVS-Tree/Tutor_Java/./src/Utilities/Java/DorminMenu/DorminMenuItem.java
package edu.cmu.old_pact.dormin.menu;

import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.beans.PropertyChangeListener;
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
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.toolframe.ToolFrame;

public class DorminMenuItem extends MenuItem implements Sharable{
	private MenuItemProxy miProxy;
	private Hashtable Properties;
	ToolFrame frame = null;
	DorminMenu menu;
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public DorminMenuItem(){
		super();
		Properties = new Hashtable();
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void setFrame(ToolFrame frame){
		this.frame = frame;
		addPropertyChangeListener((PropertyChangeListener)frame);
	}
	
	public ToolFrame getFrame(){
		return frame;
	}
	
	public void setMenu(DorminMenu menu){
		this.menu = menu;
	}
	
	public void setRealObject() {
		miProxy.setRealObject(this);
	}
	
	public Hashtable getAllProperties(){
		return Properties;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		miProxy = (MenuItemProxy)op;
	}
	
	public ObjectProxy getObjectProxy(){
		return miProxy;
	}
	
	public void delete(){
		menu.remove(this);
		removePropertyChangeListener(frame);
		try{
			removeActionListener(frame);
		} catch (NullPointerException e) { }
		changes = null;
		frame = null;
		miProxy = null;
		Properties.clear();
		Properties = null;
		menu = null;
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
                    if(propertyName.equalsIgnoreCase("Label")) {
				String label = (String)propertyValue;
							
				// add menu to the menu bar (if not there)
				// (Worksheet menu might be not on the menu bar, if still empty)
                                MenuContainer parent = menu.getParent();
                                if (parent == null)
                                    frame.getMenuBar().add(menu);
                   
                                setLabel(label);
                                
					//changes.firePropertyChange("AddedMenuItem", " ", label);
			}
			else if(propertyName.equalsIgnoreCase("ADDBUTTON")){
				boolean b = DataConverter.getBooleanValue(propertyName,propertyValue);
				if(b)
					changes.firePropertyChange("AddedMenuItem", " ", getLabel());
			}
				
			else if(propertyName.equalsIgnoreCase("NAME")){
				String actionName = ((String)propertyValue).toLowerCase();
				try{
					if(menu.isLegalAction(actionName)){
						setActionCommand(actionName);
						ObjectProxy op = getObjectProxy();
						op.setName(actionName);
						op.defaultNameDescription();
					}
					else
						setActionCommand((String)propertyValue);
				}
				catch (NoSuchFieldException e){
					throw new InvalidPropertyValueException(e.getMessage());
				}
			}
			else if(propertyName.equalsIgnoreCase("IsEnabled")){
				boolean enabled = ((Boolean)propertyValue).booleanValue();
				setEnabled(enabled);
			}
			else
				throw new NoSuchPropertyException("MenuItem doesn't have property "+propertyName);
			Properties.put(propertyName.toUpperCase(), propertyValue);
		} catch (ClassCastException ex){
			throw new DataFormatException("For object MenuItem property IsEnabled must be of type Boolean");
		} catch (DataFormattingException e){
			throw new NoSuchPropertyException(e.getMessage());
		}
	}
	
	public Hashtable getProperty(Vector proNames) throws NoSuchPropertyException{
		int s = proNames.size();
		if(s == 1 && ((String)proNames.elementAt(0)).equalsIgnoreCase("ALL"))
			return Properties;
		Hashtable toret = new Hashtable();
		String currName;
		for(int i=0; i<s; i++){
			currName = ((String)proNames.elementAt(i)).toUpperCase();
			Object ob = Properties.get(currName);
			if(ob == null)
				throw new NoSuchPropertyException("MenuItem doesn't have property "+currName);
			toret.put(currName, ob);
		}
		return toret;
	}
}
