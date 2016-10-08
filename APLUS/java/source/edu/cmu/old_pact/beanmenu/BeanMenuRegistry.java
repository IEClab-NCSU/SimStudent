package edu.cmu.old_pact.beanmenu;
// stores menus with the same name in a Vector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import java.util.Vector;

public class BeanMenuRegistry implements PropertyChangeListener{

	private BeanMenuRegistry(){
	}
	
	public static BeanMenuRegistry knownObjects = new BeanMenuRegistry();
	
	private Hashtable hashtable = new Hashtable(); 
	
	static synchronized public void registerObject (String type, Object obj) {
		registerObject(type, obj, true);
	}
	
	static synchronized public void registerObject (String type, Object obj, boolean update) {
		Vector objs = (Vector)getObject(type); 
		if(objs == null) {
			objs = new Vector();
		}
		objs.addElement(obj);
		((DynamicMenu)obj).addPropertyChangeListener((PropertyChangeListener)knownObjects);
		if(objs.size() >1 && update)
			updateNewMenu((PropertyChangeListener)obj, (DynamicMenu)objs.elementAt(0));
		knownObjects.hashtable.put(type, objs);
	}
	
	static void updateNewMenu(PropertyChangeListener toUpdate, DynamicMenu fromUpdate) {
		Vector existingLabels = fromUpdate.getLabels();
		int s = existingLabels.size();
		for(int i=0; i<s; i++) {
			PropertyChangeEvent evt = new PropertyChangeEvent(knownObjects,"Add", "", (String)existingLabels.elementAt(i));
			toUpdate.propertyChange(evt);
		}
	}
	
	public static void addToMenu(String menuName, String itemName){
		PropertyChangeEvent evt  = new PropertyChangeEvent(knownObjects, "Add", menuName, itemName); 	
		knownObjects.propertyChange(evt);
	}	
	
	static synchronized public void unregisterObject (String name) {
		knownObjects.hashtable.remove(name);
	}
	
	static synchronized public Object getObject(String type) {
		return knownObjects.hashtable.get(type);
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		// this is a cheatting implementation :
		//	in order to know what type of  menu to update, I send
		//	type in an oldValue();
		String type = (String)evt.getOldValue();
		Vector objs = (Vector)(knownObjects.getObject(type));
		if(objs == null) return;
		int s = objs.size();
		if(s == 0) return;
		for(int i=0; i<s; i++) 
			((PropertyChangeListener)objs.elementAt(i)).propertyChange(evt);
	}
	
	public static void disableMenuItem(String menuName, String itemName){
		PropertyChangeEvent evt = new PropertyChangeEvent(knownObjects,"Disable", menuName, itemName);	
		knownObjects.propertyChange(evt);
	}
	
	public static void enableMenuItem(String menuName, String itemName){
		PropertyChangeEvent evt = new PropertyChangeEvent(knownObjects,"Enable", menuName, itemName);	
		knownObjects.propertyChange(evt);
	}
	
	public static void removeMenuItem(String menuName, String itemName){
		PropertyChangeEvent evt = new PropertyChangeEvent(knownObjects,"Remove", menuName, itemName);	
		knownObjects.propertyChange(evt);
	}
	
	public int getSize(){
		return knownObjects.hashtable.size();
	}
			
}
	