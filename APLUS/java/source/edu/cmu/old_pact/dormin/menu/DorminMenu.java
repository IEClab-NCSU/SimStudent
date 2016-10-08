//d:/Pact-CVS-Tree/Tutor_Java/./src/Utilities/Java/DorminMenu/DorminMenu.java
package edu.cmu.old_pact.dormin.menu;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.toolframe.ToolFrame;

public class DorminMenu extends Menu implements Sharable {
	MenuProxy menuProxy;
	private Hashtable Properties;
	ObjectProxy parent;
	ToolFrame frame;
	private String[] legalActions = null;
	
	public DorminMenu(String menuName, ObjectProxy parent, ToolFrame frame) {
		super(menuName);
		Properties = new Hashtable();
		try{
			setProperty("name", menuName);
		} catch (NoSuchPropertyException e) { }
		menuProxy = new MenuProxy(parent,menuName);
		setRealObject();
		this.parent = parent;
		this.frame = frame;
	}
	
	
    public void setRealObject() {
		menuProxy.setRealObject(this);
	}
	
	public ObjectProxy getObjectProxy(){
		return menuProxy;
	}
	
	public void setLegalActions(String[] actions){
		legalActions = actions;
	}
	
	public boolean isLegalAction(String action) throws NoSuchFieldException{
		int s = 0;
		if(legalActions != null)
			s = legalActions.length;
		if(s != 0){
			for(int i=0; i<s; i++){
				if(action.equalsIgnoreCase(legalActions[i]))
					return true;
			}
		}
		/*
		String parentType = "";
		if(menuProxy != null)
			parentType = menuProxy.getContainer().type;
		throw new NoSuchFieldException("Menu for Object '"+parentType+"' doesn't include value '"+action+"' for the Property 'Name'");
		*/
		return false;
	}
	
	public void setProxyInRealObject(ObjectProxy op){
		menuProxy = (MenuProxy)op;
	}
	
	public void delete(){
		clearMenu(this);
	    removeAll();
	    //removeNotify();
		MenuBar menuBar = frame.getMenuBar();
		if(menuBar != null){
			menuBar.remove(this);
			menuBar = null;
		}
		Properties.clear();
		Properties = null;
		menuProxy = null;
	    frame = null;
	    parent = null;
	
	}
	
	public void clearMenu(Menu menu){
		int s = menu.getItemCount();
		if(s == 0) return;
		MenuItem item;
		for(int i=0; i<s; i++){
			item = menu.getItem(i); 
			if(item instanceof Menu)
				clearMenu((Menu)item);
			else if(!(item instanceof DorminMenuItem)){
				try{
					item.removeActionListener(frame);
				} catch (NullPointerException e) { }
			}
		}
	}
	
    //SMILLER added for hierarchical menu support	
    public DorminMenu createSubMenu(String menuLabel, ObjectProxy parent, ToolFrame frame)
    {
        return new DorminMenu(menuLabel, parent, frame);
    }

	public DorminMenuItem createMenuItem(){
		return new DorminMenuItem();
	}
	
	protected ToolFrame getFrame(){
		return frame;
	} 
	public Hashtable getAllProperties(){
		return Properties;
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws NoSuchPropertyException{
		if(propertyName.equalsIgnoreCase("Name")){
			Properties.put(propertyName.toUpperCase(), propertyValue);
			if(menuProxy != null){
				menuProxy.setName(propertyName);
				menuProxy.defaultNameDescription();
			}
		}
		else
			throw new NoSuchPropertyException("Menu doesn't have property "+propertyName);
		
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
				throw new NoSuchPropertyException("Menu doesn't have property "+currName);
			toret.put(currName, ob);
		}
		return toret;
	}
	
}
