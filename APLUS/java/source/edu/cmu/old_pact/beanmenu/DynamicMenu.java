package edu.cmu.old_pact.beanmenu;

import java.awt.Font;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;

public class DynamicMenu extends Menu implements PropertyChangeListener{
	private Vector items;
	private Vector labels;
	private String type;
	private boolean sendMessage = true;
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public DynamicMenu(String type){
		this(type, true);
	}
	
	public DynamicMenu(String type, boolean update){
		super(type);
		this.type = type;
		items = new Vector();
		labels = new Vector();
		BeanMenuRegistry.registerObject(type,this, update);
	}
	
	// bean support
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public Vector getLabels(){
		return labels;
	}
	
	public void add(String label){
		if(!labels.contains(label)){
			MenuItem mi = new MenuItem(label);
			add(mi);
		}
	}
	
	public MenuItem add(MenuItem item){
		MenuItem addedItem = null;
		if(items.contains(item))
			return item;
		synchronized (this){
			if(!items.contains(item)){
				items.addElement(item);
				String actualLabel = item.getLabel();
				labels.addElement(actualLabel);
				ActionListener al = (ActionListener)(ObjectRegistry.knownObjects.getObject(actualLabel));
				
				// change menu item label so it appears as "scenario" on the windows menu.
				// in order for the system to work properly without modifying dormin
				// messages coming from the tutor, action command name has been changed back to
				// "problemstatement" (see DorminToolFrame.actionPerformed)
				if(actualLabel.equalsIgnoreCase("ProblemStatement")) 
					item.setLabel("Scenario");
	
				addedItem = super.add(item);
				if(al != null){
					addedItem.addActionListener(al);
				}
				if(sendMessage)
	 				changes.firePropertyChange("Add", type, actualLabel);
			}
		}
		
		return addedItem;
	}
	
	private int getLabelIndex(String label){
		return labels.indexOf(label);
	}
	
	private void removeFromListeners(int ind){
		MenuItem item = (MenuItem)items.elementAt(ind);
		
		ActionListener al = (ActionListener)(ObjectRegistry.knownObjects.getObject(item.getLabel()));
		if(al != null)
			item.removeActionListener(al);
	}
	
	public void deleteListeners(){
		changes = null;
	}
	
	private void remove(String label){
		synchronized (this){
			int ind = getLabelIndex(label);
			
			if(ind == -1)
				return;
			removeFromListeners(ind);
			items.removeElementAt(ind);
			labels.removeElementAt(ind);
			if(sendMessage)
				changes.firePropertyChange("Remove", type, label);
			super.remove(ind);
		}
	}
	
	private void enableItem(String label){
		synchronized (this){
			int ind = getLabelIndex(label);
			if(ind == -1)
				return;
			
			((MenuItem)items.elementAt(ind)).enable();
			if(sendMessage)
				changes.firePropertyChange("Enable", type, label);
		}
	}	
	
	private void disableItem(String label){
		synchronized (this){
			int ind = getLabelIndex(label);
			if(ind == -1)
				return;
			
			((MenuItem)items.elementAt(ind)).disable();
			if(sendMessage)
				changes.firePropertyChange("Disable", type, label);
		}
	}
	
	private void checkItem(String label, boolean check){
		synchronized (this){
			int ind = getLabelIndex(label);
			if(ind == -1)
				return;
			MenuItem curItem = (MenuItem)items.elementAt(ind);
			Font font = curItem.getFont();
			if(check)
				font = new Font(font.getFamily(), Font.BOLD, font.getSize()+2);
			else
				font = new Font(font.getFamily(), Font.PLAIN, font.getSize()-2);
			curItem.setFont(font);
		}
	}
	
	public MenuItem getMenuItem(String label){
		int ind = getLabelIndex(label);
		MenuItem mi = null;
		try{
			mi = (MenuItem)items.elementAt(ind);
		} catch (ArrayIndexOutOfBoundsException e){
			System.out.println("DynamicMenu getMenuItem No such Label "+label);
		}
		return mi;
	}
		
	public void propertyChange(PropertyChangeEvent evt){
		String propertyName = evt.getPropertyName();
		sendMessage = false;
		if(propertyName.equalsIgnoreCase("ADDNEWITEM")) {
			add((MenuItem)evt.getNewValue());
			sendMessage = true;
			return;
		}
		String label = "";
		try{
			label = (String)evt.getNewValue();
		} catch (ClassCastException e) {return;}
		if(propertyName.equalsIgnoreCase("ADD"))
			add(label);
		else if(propertyName.equalsIgnoreCase("REMOVE"))
			remove(label);
		else if(propertyName.equalsIgnoreCase("ENABLE"))
			enableItem(label);
		else if(propertyName.equalsIgnoreCase("DISABLE"))
			disableItem(label);
		else if(propertyName.equalsIgnoreCase("CHECK"))
			checkItem(label, true);
		else if(propertyName.equalsIgnoreCase("UNCHECK"))
			checkItem(label, false);
		sendMessage = true;
	}
}	
		
	