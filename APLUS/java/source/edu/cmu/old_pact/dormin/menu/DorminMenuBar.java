//d:/Pact-CVS-Tree/Tutor_Java/./src/Utilities/Java/DorminMenu/DorminMenuBar.java
package edu.cmu.old_pact.dormin.menu;

import java.awt.MenuBar;

import edu.cmu.old_pact.dormin.ObjectProxy;
import edu.cmu.old_pact.toolframe.ToolFrame;

public class DorminMenuBar extends MenuBar{
	ToolFrame frame;
	
	public DorminMenuBar(ToolFrame frame){
		super();
		this.frame = frame;
	}
	
	public void createMenu(String menuName, ObjectProxy parent){
		DorminMenu menu = new DorminMenu(menuName, parent, frame); 
		this.add(menu);
	}
	
}