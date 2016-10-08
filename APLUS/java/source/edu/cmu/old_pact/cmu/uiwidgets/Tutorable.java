//////////////////////////////////////
// addressable interfaces
package edu.cmu.old_pact.cmu.uiwidgets;

public interface Tutorable {
	boolean flagged();
	void setFlagging(boolean f);
	
	boolean hilited();
	void setHiliting(boolean b);

	boolean locked();
	void setLock(boolean b);
	
	String name();
	void setName(String s);
	
	boolean selected();
	void select();
	void deselect();

}

