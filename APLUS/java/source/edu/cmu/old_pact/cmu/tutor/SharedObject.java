package edu.cmu.old_pact.cmu.tutor;

import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;

public interface SharedObject {
	public Object getProperty(String prop) throws NoSuchPropertyException;
	void setProperty(String prop,Object newValue) throws DorminException;
}
