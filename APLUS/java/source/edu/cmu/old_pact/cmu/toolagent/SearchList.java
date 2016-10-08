//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/SearchList.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Color;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;

public class SearchList extends List implements PropertyChangeListener, ItemListener{
	private String currItem = "";
	private Font font;
	private Vector itemNameV = new Vector();
	private Vector fileNameV = new Vector();
	protected FastProBeansSupport changes = new FastProBeansSupport(this);

	
	public SearchList(int numRows){
		super(numRows);
		addItemListener(this);
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			font = new Font("geneva", Font.BOLD, 11);
		else
			font = new Font("arial", Font.BOLD, 11);
			
		setFont(font);
		setBackground(Color.white);
	}
	/*
	public Dimension preferredSize(){
		return new Dimension(200, 100);
	}
	*/
	void setItemNames(Vector v){
		itemNameV.removeAllElements();
		int s = v.size();
		if(s == 0) return;
		for(int i=0; i<s; i++)
			itemNameV.addElement(v.elementAt(i));
		setListView(itemNameV);
	}
	
	public void delete(){
		changes = null;
		itemNameV.removeAllElements();
		itemNameV = null;
		fileNameV.removeAllElements();
		fileNameV = null;
		font = null;
		removeAll();
		removeItemListener(this);
	}	

	void setFileNames(Vector v){
		fileNameV.removeAllElements();
		int s = v.size();
		if(s == 0) return;
		for(int i=0; i<s; i++)
			fileNameV.addElement(v.elementAt(i));
	}
	
	public void itemStateChanged(ItemEvent e){
		String selection = getSelectedItem();
		if(!currItem.equalsIgnoreCase(selection)){
			int ind = itemNameV.indexOf(selection);
			changes.firePropertyChange("DisplayImage", currItem, (String)fileNameV.elementAt(ind));
		}
		currItem = selection;
	}
	
	public  void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		if(eventName.equalsIgnoreCase("SEARCHTEXT")) {
			searchList((String)evt.getNewValue());
		}
		else if(eventName.equalsIgnoreCase("ITEMNAMES"))
			setItemNames((Vector)evt.getNewValue());
		else if(eventName.equalsIgnoreCase("FILENAMES"))
			setFileNames((Vector)evt.getNewValue());
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	private synchronized void setListView(Vector selection){
		removeAll();
		int s = selection.size();
		if ( s == 0) {
			changes.firePropertyChange("ResultLabel", "","No items found");
			return;
		}
		for(int i=0; i<s; i++)
			addItem((String)selection.elementAt(i));
		int fs = itemNameV.size();
		String text;
		if(s == fs)
			text = "You see ALL items";
		else
			text = "You see "+String.valueOf(s)+" out of "+String.valueOf(fs)+" items";
		changes.firePropertyChange("ResultLabel", "",text);
	}		
		
	
	public void searchList(String searchText){
		changes.firePropertyChange("DisplayImage", currItem, "");
		currItem = "";
		searchText = searchText.toLowerCase();
		if(searchText.equalsIgnoreCase("")){
			setListView(itemNameV);
			return;
		}
		else{
			int s = itemNameV.size();
			if(s == 0) return;
			Vector v = new Vector();
			String str;
			for(int i=0; i<s; i++){
				str = (String)itemNameV.elementAt(i);
				if((str.toLowerCase()).indexOf(searchText) != -1)
					v.addElement(str);
			}
			setListView(v);
		} 
	}
}