//d:/Pact-CVS-Tree/Tutor_Java/./src/Geometry/PC/Interface/SearchTextField.java
package edu.cmu.old_pact.cmu.toolagent;

import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;

import edu.cmu.old_pact.fastbeanssupport.FastProBeansSupport;

public class SearchTextField extends TextField implements KeyListener{
	protected FastProBeansSupport changes = new FastProBeansSupport(this);
	private String currText = "";
	
	public SearchTextField(int s){
		super(s);
		addKeyListener(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l){
		changes.addPropertyChangeListener(l);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l){
		changes.removePropertyChangeListener(l);
	}
	
	public void delete(){
		changes = null;
	}
	
	public void keyTyped(KeyEvent e){ }
	
    public void keyPressed(KeyEvent e){ }

    public void keyReleased(KeyEvent e){
    	changes.firePropertyChange("SearchText", currText, getText());
		currText = getText();
    }
    public void setText(String t){
    	super.setText(t);
    	changes.firePropertyChange("SearchText", currText, t);
		currText = t;
    }
}
	