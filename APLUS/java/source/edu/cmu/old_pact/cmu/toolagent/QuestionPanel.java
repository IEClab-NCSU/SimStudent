//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/QuestionPanel.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagLayout;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;
import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;
import edu.cmu.old_pact.scrollpanel.LightComponentScroller;
import edu.cmu.old_pact.toolframe.Hintable;


public class QuestionPanel extends Panel implements PropertyChangeListener{
	private int yPos = 0;
	Vector tablePanels;
	Vector listeners;
	private int width = 10;
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	
	public QuestionPanel(){
		super();
		setLayout(new GridBagLayout());
		tablePanels = new Vector();
		listeners = new Vector();
	}
	
	// add next component in the column x=0
	public void addComponent(Component c){
		GridbagCon.viewset(this,c, 0, yPos, 1, 1, 0, 0, 0 ,0);
		if(c instanceof Hintable) {
			tablePanels.addElement(c);
		}
		if(c instanceof PropertyChangeListener) {
			listeners.addElement(c);
			addPropertyChangeListener((PropertyChangeListener)c);
		}
		yPos++;
	}
	
	public void scrollToQuestion(Panel question){		
		int y = 0;
		for(int i =0; i<tablePanels.indexOf(question); i++)
		  y += ((Panel)tablePanels.elementAt(i)).getSize().height;		
	
		((LightComponentScroller)getParent().getParent()).scrollToYpos(y);
	
	}
	 
	 
	public void removeAll(){
		int s = tablePanels.size();
		for(int i=0; i<s; i++)
			((Container)tablePanels.elementAt(i)).removeAll();
		deleteListeners();
		tablePanels.removeAllElements();
		tablePanels = null;
		listeners.removeAllElements();
		listeners = null;
		changes = null;
		super.removeAll();
		//removeNotify();
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void deleteListeners(){
		int s = listeners.size();
		if (s == 0) return;
		for(int i=0; i<s; i++)
			removePropertyChangeListener((PropertyChangeListener)listeners.elementAt(i));
	}
	
	public boolean asksForHint(){
		boolean toret = false;
		int s = tablePanels.size();
		if(s == 0) return toret;
		Hintable curPanel;
		for(int i=0; i<s; i++){
			curPanel = (Hintable)tablePanels.elementAt(i);
			if(curPanel.asksForHint()){
				((Component)curPanel).requestFocus();
				return true;
			}
		}
		return toret;
	}
	
	public void setSize(int w, int h){
		super.setSize(w,h);
 		changes.firePropertyChange("Width", Integer.valueOf(String.valueOf(width)), Integer.valueOf(String.valueOf(w)));
 		width = w;
 	}
 	
 	public void propertyChange(PropertyChangeEvent evt){
 		String evtName = evt.getPropertyName();
 		if(evtName.equalsIgnoreCase("FONTSIZE"))
 			changes.firePropertyChange("FONTSIZE", evt.getOldValue(), evt.getNewValue());
 	}
 
}