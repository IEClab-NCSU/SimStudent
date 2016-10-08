/**
*  Speed up the bean.VetoableChangeSupport, based upon the InfoBus 1.2 specification
**/

package edu.cmu.old_pact.fastbeanssupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;


public class FastVetoBeansSupport implements java.io.Serializable {
	transient private Vector listeners = new Vector();
    private Object source;
    private int propertyChangeSupportSerializedDataVersion = 2;
    
    public FastVetoBeansSupport(Object sourceBean) {
		source = sourceBean;
    }

    
    public synchronized void addVetoableChangeListener(
					VetoableChangeListener listener) {
		Vector v = (Vector)listeners.clone();
		v.addElement(listener);
		listeners = v;
    }

    public synchronized void removeVetoableChangeListener(
					VetoableChangeListener listener) {
		if (listeners == null) 
	    	return;
		Vector v = (Vector)listeners.clone();
		v.removeElement(listener);
		listeners = v;
    }

    public void fireVetoableChange(String propertyName, 
					Object oldValue, Object newValue)
					throws PropertyVetoException {

	  if (oldValue != null && oldValue.equals(newValue)) 
	    return;
	  if (listeners == null) 
	    return;

	    Vector targets = (java.util.Vector) listeners.clone();
        PropertyChangeEvent evt = new PropertyChangeEvent(source,
					    propertyName, oldValue, newValue);

	  try {
	    for (int i = 0; i < targets.size(); i++) {
	        VetoableChangeListener target = 
				(VetoableChangeListener)targets.elementAt(i);
	        target.vetoableChange(evt);
	    }
	  } catch (PropertyVetoException veto) {
	    // Create an event to revert everyone to the old value.
       	evt = new PropertyChangeEvent(source, propertyName, newValue, oldValue);
	    for (int i = 0; i < targets.size(); i++) {
		  try {
	            VetoableChangeListener target =
				(VetoableChangeListener)targets.elementAt(i);
	            target.vetoableChange(evt);
		  } catch (PropertyVetoException ex) {
		     // We just ignore exceptions that occur during reversions.
		  }
	    }
	    // And now rethrow the PropertyVetoException.
	    throw veto;
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
	    for(int i = 0; i < listeners.size(); i++) {
	        VetoableChangeListener l = (VetoableChangeListener)v.elementAt(i);
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
	    addVetoableChangeListener((VetoableChangeListener)listenerOrNull);
        }
    }
}
