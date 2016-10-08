//d:/Pact-CVS-Tree/Tutor_Java/./src/Tools/Java/CoreInterface/CoreInterface.java
package edu.cmu.old_pact.cl.coreInterface;

import edu.cmu.old_pact.dormin.ObjectProxy;

public interface CoreInterface {
	public void createInterfaceProxy();
	public ObjectProxy getObjectProxy();
	public void setProperty(String n, Object v);
	public void addTeacherOptions();

}
