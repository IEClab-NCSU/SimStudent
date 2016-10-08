package edu.cmu.old_pact.linkvector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.cmu.spreadsheet.OrderedTextField;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;


public class LinkVector implements PropertyChangeListener{
	private Vector vertLinks;
	private Vector horLinks;
	private OrderedTextField field_pointer;
	private FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public LinkVector(){
		vertLinks = new Vector();
		horLinks = new Vector();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void delete(){
		deleteFromVector(vertLinks);
		deleteFromVector(horLinks);
		field_pointer = null;
		changes = null;
	}
	
	private void deleteFromVector(Vector vect){
		if (vect == null) return;
		int s = vect.size();
		if(s == 0) return;
		for(int i=0; i<s; i++) {
			OrderedTextField otf = (OrderedTextField)vect.elementAt(i);
			if (otf != null){
				otf.removePropertyChangeListener(this);
			}
		}
		vect.removeAllElements();
		vect = null;
	}
	
	public OrderedTextField getCurrentCell(){
		return field_pointer;
	}
	
	public void removeLink(OrderedTextField obj){
		vertLinks.removeElement(obj);
		horLinks.removeElement(obj);
		if(field_pointer == obj)
			field_pointer = null;
		obj.removePropertyChangeListener(this);
	}
	
	public void setCurrCell(OrderedTextField d){
		field_pointer = d;
	}
	
	public boolean currAskedForHelp(){
		if(field_pointer != null && field_pointer.isEditable() && field_pointer.isVisible()){
			field_pointer.askHint();
			return true;
		}
		return false;
	}
	
	public void focusCurrentCell(){
		if(field_pointer != null)
			field_pointer.requestFocus();
	}
	
	public void addVecticalLink(OrderedTextField obj){
		vertLinks.addElement(obj);
	}
	
	public void addHorisontalLink(OrderedTextField obj){
		horLinks.addElement(obj);
	}
	
	public void getNext(OrderedTextField obj, Vector links){
		OrderedTextField otf = getNextField(obj, links);
		if(otf == obj)
			sendFieldValue(otf);
	}
	
	public void sendFieldValue(OrderedTextField otf){
		if(!otf.getText().equals("") && otf.isEditable())
			otf.sendUserValue("", otf.getText());
	}
	
	public void getPrev(OrderedTextField obj, Vector links){
		OrderedTextField otf = getPrevField(obj, links);
		if(otf == obj)
			sendFieldValue(otf);
	}
	
	private OrderedTextField getNextField(OrderedTextField obj, Vector links){
		int ind = links.indexOf(obj);
		int s = links.size();
		int f_ind = -1;
		if(ind+1 < s){
			f_ind = findInNextSec(ind+1, s, links);
			if(f_ind != -1)
				return (OrderedTextField)links.elementAt(f_ind);
		}
		if(ind == 0)
			return (OrderedTextField)links.elementAt(0) ;
		if(s-ind > 0){
			f_ind = findInNextSec(0, ind, links);
			if(f_ind != -1)
				return (OrderedTextField)links.elementAt(f_ind);
		}
		return obj;
	}
	
	
	public OrderedTextField getPrevField(OrderedTextField obj, Vector links){
		int ind = links.indexOf(obj);
		int s = links.size();
		int f_ind = -1;
		if(ind > 0) {
			f_ind = findInPrevSec(0, ind, links);
			if(f_ind != -1)
				return (OrderedTextField)links.elementAt(f_ind);
		}
		if(ind == s-1)
			return (OrderedTextField)links.elementAt(ind);
		if(ind+1 < s){
			f_ind = findInPrevSec(ind+1, s, links);
			if(f_ind != -1)
				return (OrderedTextField)links.elementAt(f_ind);
		}
		return obj;
	}
	
	private int findInNextSec(int st, int end, Vector links){
		OrderedTextField next;
		for(int i=st; i<end; i++){
			next = (OrderedTextField)links.elementAt(i);
			if(next.isEditable() && next.isVisible()) {
				next.requestFocus();
				return i;
			}
		}
		return -1;
	}
	
	private int findInPrevSec(int st, int end, Vector links){
		OrderedTextField next;
		int i = end;
		while (i >= st){
			next = (OrderedTextField)links.elementAt(i);
			if(next.isEditable() && next.isVisible()) {
				next.requestFocus();
				return i;
			}
			i--;
		}
		return -1;
	}
	
	public void propertyChange(PropertyChangeEvent evt){
		String eventName = evt.getPropertyName();
		Object obj = evt.getNewValue();
		if(eventName.equalsIgnoreCase("NEXTINCOLUMN")) { 
			getNext((OrderedTextField) obj, vertLinks);
		}
		else if(eventName.equalsIgnoreCase("NEXTINROW")) {
			getNext((OrderedTextField)obj, horLinks);
		}
		else if(eventName.equalsIgnoreCase("PREVINCOLUMN")) {
			getPrev((OrderedTextField)obj, vertLinks);
		}
		else if(eventName.equalsIgnoreCase("PREVINROW")) {
			getPrev((OrderedTextField)obj, horLinks);
		}
		else if (eventName.equalsIgnoreCase("FOCUSGAINED")) {
			field_pointer = (OrderedTextField)obj;
		}
		else if (eventName.equalsIgnoreCase("REMOVELINK")) {
			removeLink((OrderedTextField)obj);
		}
		else if (eventName.equalsIgnoreCase("OPENTEACHERWINDOW")) 
			changes.firePropertyChange("OpenTeacherWindow", "","new");
		
	}
}	