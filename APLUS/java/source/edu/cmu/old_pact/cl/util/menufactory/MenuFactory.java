package edu.cmu.old_pact.cl.util.menufactory;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.cmu.old_pact.beanmenu.DynamicMenu;
import edu.cmu.old_pact.dormin.NoSuchPropertyException;
import edu.cmu.old_pact.dormin.Sharable;
import edu.cmu.old_pact.objectregistry.ObjectRegistry;
import edu.cmu.old_pact.toolframe.ToolFrame;


public class MenuFactory {
	//public static String fileDir ="file:///"+System.getProperty("user.dir")+java.io.File.separator;
	// should be set from the StudentInterface, otherwise doesn't work with applets
	public static String fileDir = null;
	public static AboutTutorWindow aboutWindow = null;
	public static String version = "Default Version";
	
	public static MenuBar getGeneralMenuBar(boolean addToolMenu, ToolFrame frame, 
											String windowName){
		return getGeneralMenuBar(addToolMenu, frame, windowName, false);
	}
	
	public static MenuBar getGeneralMenuBar(ToolFrame frame, String windowName){
		return getGeneralMenuBar(true, frame, windowName, false);
	}
	
	public static MenuBar getGeneralMenuBar(ToolFrame frame, String windowName, 
											boolean isListener){
		return getGeneralMenuBar(true, frame, windowName, isListener);
	}
	
	public static MenuBar getGeneralMenuBar(boolean addToolMenu, ToolFrame frame, 
											String windowName, boolean isListener){		
		MenuBar m_bar = new GeneralMenuBar();
		m_bar.add(getFileMenu());
		m_bar.add(getEditMenu(frame, windowName, isListener));
		
		if(addToolMenu)
			m_bar.add(getTutorMenu(frame));
		m_bar.add(getWindowsMenu(windowName));
		
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			// display About window from the About menu item in the Apple menu
			CLAbout clAbout = new CLAbout();
			m_bar.setHelpMenu(getHelpMenu(frame));
		}
		else
			// for PC add Help menu with Hint and About items
			m_bar.add(getHelpMenu(frame));
		return m_bar;
	}
	
	
	public static Menu[] getGeneralMenus(ToolFrame frame, String windowName, boolean isListener){
		int menuCount = 5;
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			menuCount = 4;
		Menu[] menus = new Menu[menuCount];
		menus[0] = getFileMenu();
		menus[1] = getEditMenu(frame, windowName, isListener);
		menus[2] = getTutorMenu(frame);
		menus[3] = getWindowsMenu(windowName);
		if(!(System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			menus[4] = getHelpMenu(frame);
		return menus;
	}
	
	public static Menu getFileMenu(){
		Menu file = new Menu("File");
		file.setName("file");
		file.add(getQuitMenuItem());
		return file;
	}
	
	public static MenuItem getQuitMenuItem(){
		ActionListener sInterface = (ActionListener)ObjectRegistry.getObject("Application");
		MenuItem quitItem = new MenuItem("Quit", new MenuShortcut(KeyEvent.VK_Q));
		quitItem.setActionCommand("Quit");
		quitItem.addActionListener(sInterface); 
		return quitItem;
	}

	public static Menu getEditMenu(ToolFrame frame, String windowName, 
									boolean isListener){
		Menu edit = new Menu("Edit");
		edit.setName("edit");
		String[] commands = new String[]{"CUT","COPY","PASTE"};
		MenuItem[] mi = new MenuItem[3];
 	
		mi[0] = new MenuItem("Cut", new MenuShortcut(KeyEvent.VK_X));
		mi[1] = new MenuItem("Copy", new MenuShortcut(KeyEvent.VK_C));
		mi[2] = new MenuItem("Paste", new MenuShortcut(KeyEvent.VK_V));
		
		for(int i=0; i<commands.length; i++){
			mi[i].setActionCommand(commands[i]);
			if(isListener) 
				mi[i].addActionListener((ActionListener)frame);
			else
				mi[i].disable();
			edit.add(mi[i]);
		}
		
		edit.add(new MenuItem("-"));  // a separator
		edit.add(getPreferencesMenuItem(frame, windowName)); 
		return edit;
	}
	
	public static Menu getTutorMenu(ToolFrame frame){
		DynamicMenu tutor = new DynamicMenu("Tutor");
		tutor.setName("Tutor");
	//KeyListeningMenu tutor = new KeyListeningMenu("Tutor");
		/*
		tutor.add("Login");
		MenuItem loginItem = tutor.getMenuItem("Login");
		ActionListener login = (ActionListener)(ObjectRegistry.knownObjects.getObject("Login"));
		loginItem.addActionListener(login); 
		loginItem.disable();
		*/
		String[] commands = new String[]{"DONE"};
		String[] labels = new String[]{"Done"};	
		int[] shortcuts = new int[]{KeyEvent.VK_D};
		for(int i=0; i<labels.length; i++){
			tutor.add(labels[i]);
			MenuItem mi = tutor.getMenuItem(labels[i]);
			mi.setActionCommand(commands[i]);
			mi.setShortcut(new MenuShortcut(shortcuts[i]));
			mi.addActionListener((ActionListener)frame);
		}
		Sharable appl = (Sharable)ObjectRegistry.getObject("Application");
		Vector v = new Vector();
		v.addElement("CanSendFeedback");
		boolean canSendFeedback = false;
		try{
			Hashtable prop = appl.getProperty(v);
			Enumeration e = prop.elements();
			canSendFeedback = ((Boolean)e.nextElement()).booleanValue();
		} catch (NoSuchPropertyException e) { }
		if(canSendFeedback){
			MenuItem sep = new MenuItem("-");
			tutor.add(sep);
			ActionListener sInterface = (ActionListener)appl;
			MenuItem fbItem = new MenuItem("Send Feedback");
			fbItem.setActionCommand("ShowFeedbackFrame");
			fbItem.addActionListener(sInterface); 
			tutor.add(fbItem);
		}
		return tutor;
	}
	
	public static MenuItem getAboutMenuItem(){
		ActionListener sInterface = (ActionListener)ObjectRegistry.getObject("Application");
		MenuItem aboutItem = new MenuItem("About Tutor...");
		aboutItem.setActionCommand("About");
		aboutItem.addActionListener(sInterface);
		aboutItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
		return aboutItem;
	}
	
	public static Menu getHelpMenu(ToolFrame frame){
		Menu help = new Menu("Help");
		help.setName("help");
		help.add(getHintMenuItem(frame));
		
		if(!(System.getProperty("os.name").toUpperCase()).startsWith("MAC")){
			help.add(new MenuItem("-"));  // a separator
			help.add(getAboutMenuItem());
		}
		return help;
	}
	
	public static MenuItem getHintMenuItem(ToolFrame frame){
		MenuItem hintItem = new MenuItem("Hint");
		hintItem.setActionCommand("Hint");
		hintItem.addActionListener((ActionListener)frame);
		hintItem.setShortcut(new MenuShortcut(KeyEvent.VK_H));
		return hintItem;
	}
	
	public static MenuItem getPreferencesMenuItem(ToolFrame frame, 
												 String windowName){
		MenuItem prefItem = new MenuItem("Preferences...");
		prefItem.setActionCommand("Preferences");
		prefItem.addActionListener((ActionListener)frame);
		//prefItem.setShortcut(new MenuShortcut(KeyEvent.VK_P));
		return prefItem;
	
	}
	
	public static Menu getWindowsMenu(String windowName){
		DynamicMenu windows = new DynamicMenu("Windows");
		windows.setName("Windows");
		windows.add(windowName);
		return windows;
	}
	
	public static void showAboutWindow(){
		if(aboutWindow == null){
			aboutWindow = new AboutTutorWindow(fileDir, version);
		}
		aboutWindow.setShowDetails(true);
		aboutWindow.setVisible(true); 
		aboutWindow.toFront();
	}

}

 class CLAbout 
// implements MRJAboutHandler 
 {

	public CLAbout() {	
//		MRJApplicationUtils.registerAboutHandler(this);
	}
	
	// called when the user selects the About menu item in the Apple menu on MAC
	public void handleAbout(){	
		MenuFactory.showAboutWindow();
	}
}