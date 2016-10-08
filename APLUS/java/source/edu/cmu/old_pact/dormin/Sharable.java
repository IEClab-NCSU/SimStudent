package edu.cmu.old_pact.dormin;

import java.util.Hashtable;
import java.util.Vector;

public interface Sharable {
	public ObjectProxy getObjectProxy();
	public void setProxyInRealObject(ObjectProxy op);
	public void setProperty(String key, Object obj) throws DorminException;
	public Hashtable getProperty(Vector propertyNames) throws NoSuchPropertyException; 
	public void delete();
}
	
	