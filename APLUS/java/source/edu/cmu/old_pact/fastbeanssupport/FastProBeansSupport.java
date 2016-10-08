/**
*  Speed up the bean.PropertyChangeSupport, based upon the InfoBus 1.2 specification
**/

package edu.cmu.old_pact.fastbeanssupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;


public class FastProBeansSupport implements java.io.Serializable {
	transient private Vector listeners = new Vector();
    private Object source;
    private int propertyChangeSupportSerializedDataVersion = 2;
    
    public FastProBeansSupport(Object sourceBean) {
		source = sourceBean;
    }

    public synchronized void addPropertyChangeListener(
				PropertyChangeListener listener) {
		Vector v = (Vector)listeners.clone();
		v.addElement(listener);
		listeners = v;
    }

    public synchronized void removePropertyChangeListener(
				PropertyChangeListener listener) {
		if (listeners == null) 
	    	return;
		Vector v = (Vector)listeners.clone();
		v.removeElement(listener);
		listeners = v;
    }

    public void firePropertyChange(String propertyName, 
					Object oldValue, Object newValue) {
		if (oldValue != null && oldValue.equals(newValue)) 
	    	return;
		if (listeners == null) 
	    	return;
	    	
	    Vector targets = listeners;
        PropertyChangeEvent evt = new PropertyChangeEvent(source,
					    propertyName, oldValue, newValue);

		for (int i = 0; i < targets.size(); i++) {
	    	PropertyChangeListener target = (PropertyChangeListener)targets.elementAt(i);
	    	target.propertyChange(evt);
		}
    }


    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();

	java.util.Vector v = null;
	synchronized (this) {
	    if (listeners != null) {
	        v = (java.util.Vector) listeners.clone();
            }
	}

	if (v != null) {
	    for(int i = 0; i < v.size(); i++) {
	        PropertyChangeListener l = (PropertyChangeListener)v.elementAt(i);
	        if (l instanceof Serializable) {
	            s.writeObject(l);
	        }
            }
        }
        s.writeObject(null);
    }


    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();
      
        Object listenerOrNull;
        while(null != (listenerOrNull = s.readObject())) {
	  addPropertyChangeListener((PropertyChangeListener)listenerOrNull);
        }
    }    
}




