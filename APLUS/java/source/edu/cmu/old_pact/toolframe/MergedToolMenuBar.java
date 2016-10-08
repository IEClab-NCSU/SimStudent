package edu.cmu.old_pact.toolframe;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.util.Vector;

//a MergedToolMenuBar gets menus from all of the tools
public class MergedToolMenuBar extends MainMenuBar {

	//keep a record of all menubars of this type, so they can be updated when needed
	private static Vector mergedMenus = new Vector();
	
	public MergedToolMenuBar() {
		super();
		mergedMenus.addElement(this);
	}
	
	public static void updateMergedMenus(MenuBar newMenus, Frame controllingFrame) {
		for (int i=0;i<mergedMenus.size();++i)
			((MergedToolMenuBar)mergedMenus.elementAt(i)).addMenus(newMenus, controllingFrame);
	}
	
	private void addMenus(MenuBar newMenus, Frame controllingFrame) {
		for (int i=0;i<newMenus.countMenus();i++) {
			Menu newMenu = newMenus.getMenu(i);
			String newLabel = newMenu.getLabel();
			boolean found=false;
			//First, check to see if a menu with that name is already here
			for (int j=0;j<countMenus()&&!found;++j) {
				if (newLabel.equalsIgnoreCase(getMenu(j).getLabel()))
					found=true;
			}
			if (!found) {
				ToolFrameMenu menuClone = new ToolFrameMenu(newMenu,controllingFrame);
				add(menuClone);
			}
		}
	}
}

