package edu.cmu.pact.BehaviorRecorder.Controller;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import edu.cmu.pact.ctat.TutorController;

public class CTAT_Properties extends PropertyChangeSupport {
	
	/** Substitute for source bean. */
	
	/* This mutex is used to make sure we're not changing our listeners
	 * while data is being broadcast
	 */
	public String listenerMutex = "";
	
	public HashMap properties = new HashMap();
	
	public CTAT_Properties(Object sourceBean) {
		super(sourceBean);
	}
	
	public void setProperty(String key, Object value)
	{
		Object oldValue = properties.put(key,value);
		firePropertyChange(key, oldValue, value);
	}
	
	public Object getProperty(String key)
	{
		return properties.get(key);
	}
}
