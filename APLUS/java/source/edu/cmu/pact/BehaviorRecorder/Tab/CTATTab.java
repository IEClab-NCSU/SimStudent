package edu.cmu.pact.BehaviorRecorder.Tab;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;

import net.infonode.docking.View;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphPanel;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.CtatMenuBar;

/**
 * A conceptual (rather than concrete) "tab" with a unique associated
 * tab number. Each tab is associated with a graph view and controller.
 * @author Stephanie
 *
 */
public class CTATTab {
	public static final String INIT_TITLE_PREFIX = "Graph ";
	private final int tabNumber;
	private SingleSessionLauncher launcher;
	private String name;
	private View view;
	private boolean visible;

	/** Master set of check box menu items defined on {@link CtatMenuBar}. */
	private HashMap<JMenuItem, boolean[]> menuItems = new HashMap<JMenuItem, boolean[]>();
	
	public CTATTab(int tabNumber) {
		this.tabNumber = tabNumber;
		setName(INIT_TITLE_PREFIX + String.valueOf(tabNumber));
		this.view = null;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setLauncher(SingleSessionLauncher launcher) {
		this.launcher = launcher;
	}
	
	/**
	 * @return		the tab number associated with this tab
	 * 				(e.g., 2 for tab "Graph 2").
	 */
	public int getTabNumber() {
		return this.tabNumber;
	}
	
	public BR_Controller getController() {
		return this.launcher.getController();
	}
	
	public ProblemModel getProblemModel() {
		return getController().getProblemModel();
	}
	
	public JGraphPanel getJGraphPanel() {
		return getController().getJGraphWindow();
	}
	
	public SingleSessionLauncher getLauncher() {
		return this.launcher;
	}
	
	public View getView() {
		if(this.view == null) {
			String problemName = getProblemModel().getProblemName();
			JGraphPanel panel = getController().getJGraphWindow();
			String viewName = (problemName.isEmpty() ? panel.getName() : problemName);
			// initializing with the icon doesn't seem to work for some reason
			/*
			StudentInterfaceConnectionStatus sics = getController().getUniversalToolProxy().getStudentInterfaceConnectionStatus();
			this.view = new View(problemName, sics.getIcon(), panel);
			*/
			this.view = new View(viewName, null, panel);
			this.view.setName(viewName); // work around Marathon bug
		}
		return this.view;
	}

	protected boolean isVisible() {
		if(trace.getDebugCode("mg"))
			trace.out("mg", "Tab "+this.getTabNumber()+" visible? "+visible);
		return visible;
	}

	/** Set whether or not a tab is visible. Controlled by {@link CTATTabManager}.
	 * @param visible
	 */
	protected void setVisible(boolean visible) {
		if(trace.getDebugCode("mg"))
			trace.out("mg", "Setting tab "+this.getTabNumber()+" visible: "+visible);
		this.visible = visible;
	}

	public void addListeners() {
		
	}

	/**
	 * Add an entry to this tab's record of the state of the {@link CtatMenuBar}.
	 * @param menuItem key: item to add
	 * @param values values to store
	 */
	public void addCtatMenuItem(JMenuItem menuItem, boolean[] values) {
		if(trace.getDebugCode("menu"))
			trace.out("menu", "CTATTab.addCtatMenuItems("+menuItem+") nInField "+this.menuItems.size());
		menuItems.put(menuItem, values);
	}

	/**
	 * Retrieve the saved values for a menu item on the {@link CtatMenuBar}.
	 * @param menuItem key to {@link #menuItems}
	 * @return values for key
	 */
	public boolean[] getCtatMenuItemValues(JMenuItem menuItem) {
		return menuItems.get(menuItem);
	}

	/**
	 * Update the saved values for a menu item on the {@link CtatMenuBar}.
	 * @param menuItem key to {@link #menuItems}
	 * @param values state to store
	 */
	public void setCtatMenuItemValues(JMenuItem menuItem, boolean[] values) {
		menuItems.put(menuItem, values);
	}
	
	/**
	 * Initialize the {@link CtatMenuBar} menu items in a new {link CTATTab}
	 * instance from the master set {@link #menuItems}. The first tab instance can
	 * have menu items created before it was connected to the manager, so preserve
	 * any existing entries.
	 * @param menuItems map to copy from
	 */
	void initializeCtatMenuItems(HashMap<JMenuItem, boolean[]> menuItems) {
		if(trace.getDebugCode("menu"))
			trace.out("menu", "CTATTab.initializeCtatMenuItems() nInField "+this.menuItems.size()+
					", nInArg "+menuItems.size());
		for(JMenuItem menuItem : menuItems.keySet()) {
			if(this.menuItems.containsKey(menuItem))
				continue;
			boolean[] values = menuItems.get(menuItem);
			this.menuItems.put(menuItem, Arrays.copyOf(values,values.length));
		}
	}
	
	/**
	 * Set the saved values into the menu items. Call this method when this tab gains focus.  
	 */
	void updateCtatMenuItems() {
		for(JMenuItem menuItem: menuItems.keySet()) {
			boolean[] values = menuItems.get(menuItem);
			if(menuItem instanceof CtatMenuBar.MenuItem)
				((CtatMenuBar.MenuItem) menuItem).setValues(values);
			else if(menuItem instanceof CtatMenuBar.CheckBoxMenuItem)
				((CtatMenuBar.CheckBoxMenuItem) menuItem).setValues(values);
			else
				trace.err("CTATTab.updateCtatMenuItems[tab #"+tabNumber+"] key "+menuItem+
						" in menuItems has unexpected class "+trace.nh(menuItem));
		}
	}
}
