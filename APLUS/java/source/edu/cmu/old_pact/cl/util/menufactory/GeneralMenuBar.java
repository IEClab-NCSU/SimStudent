package edu.cmu.old_pact.cl.util.menufactory;

import java.awt.Menu;
import java.awt.MenuBar;


public class GeneralMenuBar extends MenuBar {
	
	public GeneralMenuBar() {
		super();
	}

	// Adds the specified menu to the menu bar BEFORE
	// Help and Windows menus, if they exist in this menu bar
	public Menu add (Menu m) {
		int count = getMenuCount();
		
		if (count>3) {
			// Help menu must be in the last position,
			// Windows menu is always located before Help menu
		  Menu help = getMenu(count - 1);
		  Menu win = getMenu(count - 2);
		  
		  if((help.getName().equalsIgnoreCase("help")) &&
		     (win.getName().equalsIgnoreCase("windows")) ) {
		  	   if(getMenu(count-3).getLabel().equalsIgnoreCase(m.getLabel()))
		  	 	 return null;
		  	remove(count-1);
		  	remove(count-2);
		  	super.add(m);
		  	super.add(win);
		  	super.add(help);
		  	return(m);
		  }
		}

		return super.add(m);
	}
}