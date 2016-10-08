package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import edu.cmu.old_pact.cmu.sm.SmSetter;
import edu.cmu.old_pact.cmu.solver.uiwidgets.DisplaySetter;
import edu.cmu.old_pact.cmu.solver.uiwidgets.SolverMenuItem;
import edu.cmu.old_pact.cmu.solver.uiwidgets.TypeInSetter;

 

public class SolverMenuBar extends MenuBar {
	Menu solverMenu = null;
	Menu optionsMenu = null;
	int numItems=0;	
	
	public SolverMenuBar()
	{
		super();
		// debug menu commented out
		//addDebugMenuItems();	
	}
	
	public void delete(){
		solverMenu = null;
		optionsMenu = null;
		if(getMenu(0) != null)
			remove(0);
	}
	
	// Adds the specified menu to the menu bar BEFORE
	// Help and Windows menus, if they exist in this menu bar.
	// store Solver menu in solverMenu global variable 
	public Menu add(Menu m){
		int count = getMenuCount();
		
		if (count>3) {
			// Help menu must be in the last position,
			// Windows menu is always located before Help menu
		  Menu help = getMenu(count - 1);
		  Menu win = getMenu(count - 2);
		  
		  if((help.getName().equalsIgnoreCase("help")) &&
		     (win.getName().equalsIgnoreCase("windows")) ) {
		  	   if(getMenu(count-3).getLabel().equalsIgnoreCase(m.getLabel()))
		  	 	 return null;  // "m" already exists on the menu bar
		  	remove(count-1);
		  	remove(count-2);
		  	super.add(m);
		  	super.add(win);
		  	super.add(help);
		  	storeMenu(m);
		  	return(m);
		  }
		}
		storeMenu(m);
		return super.add(m);
	}
		
	
	private void storeMenu(Menu m) {
		String mLabel = m.getLabel();
		if(mLabel.equalsIgnoreCase("SOLVER"))
			solverMenu = m;
	}
	
	public void addDebugMenuItems() {
		
		addSMOptions();
	}
	
	private void addSMOptions() {
		optionsMenu = new Menu("Preferences");
		optionsMenu.enable(true);
		add(optionsMenu);
		MenuItem prefMenuItem = new MenuItem("Calculation Preferences...");
		prefMenuItem.addActionListener(new ActionListener() {
										 public void actionPerformed(ActionEvent event) {
									 		SolverFrame sf = SolverFrame.getSelf();
									 		SmSetter theSetter = new SmSetter(sf.getSM());
										 	theSetter.pack();
										 	theSetter.show();
										 }
										});
		optionsMenu.add(prefMenuItem);
		
		MenuItem displayMenuItem = new MenuItem("Display Preferences...");
		displayMenuItem.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent event) {
									 			DisplaySetter theSetter = new DisplaySetter();
									 			theSetter.pack();
									 			theSetter.show();
									 		}
									 	});
		optionsMenu.add(displayMenuItem);	
		
		MenuItem typeInMenuItem = new MenuItem("TypeIn Preferences...");
		typeInMenuItem.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent event) {
												SolverFrame sf = SolverFrame.getSelf();
									 			TypeInSetter theSetter = new TypeInSetter(sf);
									 			theSetter.pack();
									 			theSetter.show();
									 		}
									 	});
		optionsMenu.add(typeInMenuItem);													
	}
	
	Menu getMenuByName(String menuName){
		if(menuName.equalsIgnoreCase("Solver"))
			return solverMenu;
		else if(menuName.equalsIgnoreCase("Preferences"))
			return optionsMenu;
		return null;
	}
	
	public void disableOperations(){
		setOperationsState(false);
	}
	
	public void enableOperations(){
		setOperationsState(true);
	}
	
	private void setOperationsState(boolean state){
		if(solverMenu == null) return;
		Vector operationItems = ((SolverMenu)solverMenu).getOperationVector();
		if(operationItems == null) return;
		int s = operationItems.size();
		if(s == 0) return;
		SolverMenuItem currItem;
		for(int i=0; i<s; i++) {
			currItem = (SolverMenuItem)operationItems.elementAt(i);
			currItem.setEnabled(state);
		}
	}
		
	
	public String[] getActions() {
		Vector actions = new Vector();
		int numMenus = getMenuCount();
		for (int i=0;i<numMenus;++i) {
			Menu thisMenu = getMenu(i);
			int numItems = thisMenu.getItemCount();
			for (int j=0;j<numItems;++j) {
				MenuItem thisItem = thisMenu.getItem(j);
				if (thisItem instanceof SolverMenuItem && !(thisItem.getLabel()).equals("-")){
					actions.addElement(((SolverMenuItem)thisItem).getInternalName());
				}
				else if(thisItem instanceof Menu){
					Menu thisSubMenu = (Menu)thisItem;
					int numSubItems = thisSubMenu.getItemCount();
					for(int k=0;k<numSubItems;k++){
						MenuItem thisSubItem = thisSubMenu.getItem(k);
						if (thisSubItem instanceof SolverMenuItem && !(thisSubItem.getLabel()).equals("-")){
							actions.addElement(((SolverMenuItem)thisSubItem).getInternalName());
						}
					}
				}
			}
		}
		String[] actStrings = new String[actions.size()];
		for (int i=0;i<actions.size();++i)
			actStrings[i] = (String)(actions.elementAt(i));
		return actStrings;
	}
	
	
}


