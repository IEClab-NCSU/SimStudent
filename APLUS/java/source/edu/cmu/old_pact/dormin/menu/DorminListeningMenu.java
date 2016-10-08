//d:/Pact-CVS-Tree/Tutor_Java/./src/Utilities/Java/DorminMenu/DorminListeningMenu.java
package edu.cmu.old_pact.dormin.menu;

/**
* Class DorminListeningMenu includes Frame as a Listener 
* for each contained MenuItem.
**/

import java.awt.event.ActionListener;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.toolframe.ToolFrame;


public class DorminListeningMenu extends DorminMenu {
	
	public DorminListeningMenu(String menuName, ObjectProxy parent, ToolFrame frame) {
		super(menuName, parent, frame);
	}
	
	public DorminMenuItem createMenuItem(){
		DorminMenuItem mi =  super.createMenuItem();
		mi.addActionListener((ActionListener)getFrame());
		return mi;
	}
	
}
