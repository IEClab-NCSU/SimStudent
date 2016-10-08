package edu.cmu.old_pact.toolframe;


import java.awt.Font;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.util.Vector;

public class MainMenuBar extends MenuBar {

	//maintain a mapping between names and frames
	private static String frameNames[] = new String[50];
	private static Frame frames[] = new Frame[50];
	private static int numFrames=0;

	public MainMenuBar()
	{
		super();
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			setFont(new Font("geneva",0,10));
		else
			setFont(new Font("arial",0,10));
	}
	
	public static Frame getNamedFrame(String name) {
		Frame foundFrame=null;
		for (int i=0;i<numFrames&&foundFrame==null;++i) {
			if (name.equalsIgnoreCase(frameNames[i]))
				foundFrame = frames[i];
		}
		return foundFrame;
	}
	
	public void addWindowList(Vector list)
	{
		int i;
		Menu menu=null;
		
		for (i=0;i<countMenus();i++) {
			menu=getMenu(i);
			if ("Windows".equals(menu.getLabel()))
				break;
		}
		if (menu!=null)
			for (i=0;i<list.size();i++) {
				Frame thisFrame = (Frame)list.elementAt(i);
				String frameName = thisFrame.getTitle();
				frameNames[i] = frameName;
				frames[i] = thisFrame;
				numFrames++;
				menu.add(new MenuItem(frameName));
			}		
	}
	
	
	public MenuComponent CloneItem(MenuComponent item)
	{
		int i;
		MenuComponent mc=null;
		if (item instanceof MenuBar) {
			mc=new MenuBar();
			for (i=0;i<((MenuBar)item).countMenus();i++)
				((MenuBar)mc).add((Menu)CloneItem(((MenuBar)item).getMenu(i)));
		} else if (item instanceof Menu) {
			mc=new Menu(((Menu)item).getLabel());
			for (i=0;i<((Menu)item).countItems();i++)
				((Menu)mc).add((MenuItem)CloneItem(((Menu)item).getItem(i)));
		} else if (item instanceof MenuItem) 
			mc=new MenuItem(((MenuItem)item).getLabel());

		return mc;
	}
	
	
	public Object clone()
	{
		return CloneItem(this);
		
	}		
	
}		

