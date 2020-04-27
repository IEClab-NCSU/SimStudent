package edu.cmu.pact.BehaviorRecorder.Tab;


import java.io.BufferedReader;
import java.io.File;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.UniversalToolProxy;
import net.infonode.docking.OperationAbortedException;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.SocketProxy.SocketProxy;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.CtatMenuBar;
import edu.cmu.pact.ctatview.DockManager;

/**
 * A container class for {@link CTATTab}. Includes convenience and
 * storage/retrieval methods for tab information.
 * @author syyang
 *
 */
public class CTATTabManager {
	/** A list of tabs, for tracking purposes. */
	private final List<CTATTab> tabList;
	/** The tab number to use for the next opened tab. */
	private int nextTabNumber;
	/** The server containing this tab manager. */
	private final CTAT_Launcher server;
	/** Launch arguments, to be passed to {@link SingleSessionLauncher}. */
	private String[] argv;
	/** The currently focused behavior recorder tab. */
	private CTATTab focusedTab;
	/** The maximum number of graphs that may be opened at once. */
	private static int MAX_TABS = 5;
	/** The maximum number of graphs that may be opened at once for servler version**/
	private int MAX_TABS_SERVLET = 5;
	/** The number of graph tabs created. Incremented by {@link #getNewTab()}. */
	private static int NUM_TABS = 0;
	
	/** The number of graph tabs created for the servlet */
	private int NUM_TABS_SERVLET=0;
	
	/** Stores the number of LogConsole windows open currently.
	 *  Tracks the # of windows open instead of using a boolean in case multipel windows open*/
	private int numLogConsoles = 0;
	
	/** Master set of menu items defined on {@link CtatMenuBar}, for initializing tabs. */
	private HashMap<JMenuItem, boolean[]> menuItems = new HashMap<JMenuItem, boolean[]>();
	
	/** Set of menu item names to de*/
	public CTATTabManager(CTAT_Launcher server, String[] argv) {
		this.argv = argv;
		this.tabList = new ArrayList<CTATTab>();
		this.nextTabNumber = 1;
		this.server = server;
		
		//Give LogConsole a reference to the tab manager
		LogConsole.setTabManager(this);
	}
	
	/**
	 * Add a menu item to {@link #menuItems} and call
	 * {@link CTATTab#addCtatMenuItem(CtatMenuBar.MenuItem, boolean[])} for each tab.
	 * Call this from the menu item's constructor to record the new instance in the sets
	 * of menu items to save.
	 * @param menuItem
	 * @param enabled true if the menu item is initially enabled; false if disabled
	 */
	public void addCtatMenuItem(JMenuItem menuItem, boolean[] values) {
		if(trace.getDebugCode("menu"))
			trace.out("menu", "CTATTabManager.addCtatMenuItems("+menuItem+") nInField "+
					menuItems.size()+", tabList size "+tabList.size());
		menuItems.put(menuItem, values);
		for(CTATTab tab : tabList)
			tab.addCtatMenuItem(menuItem, Arrays.copyOf(values, values.length));
	}
	
	/**
	 * @return the {@link #nextTabNumber}
	 */
	public int getNextTabNumber() {
		return nextTabNumber;
	}

	/**
	 * Returns the maximum number of tabs in the tab manager.
	 */
	public static int getMaxNumTabs() {
		return MAX_TABS;
	}
	
	/**
	 * @param maxTabs new value for {@link #MAX_TABS}
	 */
	public static void setMaxNumTabs(int maxTabs) {
		MAX_TABS = maxTabs;
	}

	/**
	 * @return the NUM_TABS
	 */
	public static int getNumTabs() {
		return NUM_TABS;
	}
	
	
	/**
	 * 
	 * @param num
	 * @author Vishnu Priya
	 */
	public static void setNumTabs(int num) {
		NUM_TABS = num;
	}



	/**
	 * Searches for and returns a tab with an empty graph.
	 * @return		The focused tab, the first visible tab, or the first 
	 * 				invisible tab if any such tab exists; null otherwise.
	 */
	public CTATTab getFreeTab() {
		CTATTab focusedTab = getFocusedTab();
		if ((getFocusedTab() != null) && (focusedTab.getProblemModel().isEmpty())) {
			return focusedTab;
		}
		for(CTATTab visibleTab : this.tabList) {
			if(visibleTab.isVisible() && visibleTab.getProblemModel().isEmpty()) {
				return visibleTab;
			}
		}
		for(CTATTab tab : this.tabList) {
			if(tab.getProblemModel().isEmpty()) {
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * Creates and returns a new tab.
	 * @return			A new tab with associated {@link SingleSessionLauncher} and
	 * 					{@link BR_Controller}.
	 */
	public CTATTab getNewTab() {
		return getNewTab(null, null);
	}
	
	/**
	 * Creates and returns a new tab.
	 * @return			A new tab with associated {@link SingleSessionLauncher} and
	 * 					{@link BR_Controller}.
	 */
	public CTATTab getNewTab(CTATTab newTab, SingleSessionLauncher launcher) {
		//String runType = System.getProperty("appRunType");
		
		/*if(runType.equals("servlet")){
			if (NUM_TABS_SERVLET >= MAX_TABS_SERVLET) {
				if(trace.getDebugCode("mg"))
					trace.out("mg", "CTATTabManager (getNewTab): no more tabs ");
				newTab = this.getFreeTab();
				if (newTab == null) {
					JOptionPane.showMessageDialog(this.getFocusedTab().getController().getActiveWindow(), 
							"Only "+MAX_TABS_SERVLET+" graphs can be open at the same time. Please close an open graph and try again.", 
							"Maximum number of graphs", JOptionPane.OK_OPTION);
				}
				return newTab;
			}
			if(launcher == null) {
				newTab = new CTATTab(this.nextTabNumber);
				launcher = new SingleSessionLauncher(this.argv, this, this.server, newTab);
			}
			newTab.setLauncher(launcher);
			this.tabList.add(newTab);
			NUM_TABS_SERVLET++;
			this.nextTabNumber++;
			newTab.initializeCtatMenuItems(menuItems);
			return newTab;
		}
		else{*/
			if (NUM_TABS >= MAX_TABS) {
				if(trace.getDebugCode("mg"))
					trace.out("mg", "CTATTabManager (getNewTab): no more tabs ");
				newTab = this.getFreeTab();
				if (newTab == null) {
					JOptionPane.showMessageDialog(this.getFocusedTab().getController().getActiveWindow(), 
							"Only "+MAX_TABS+" graphs can be open at the same time. Please close an open graph and try again.", 
							"Maximum number of graphs", JOptionPane.OK_OPTION);
				}
				return newTab;
			}
			if(launcher == null) {
				newTab = new CTATTab(this.nextTabNumber);
				launcher = new SingleSessionLauncher(this.argv, this, this.server, newTab);
			}
			newTab.setLauncher(launcher);
			this.tabList.add(newTab);
			NUM_TABS++;
			this.nextTabNumber++;
			newTab.initializeCtatMenuItems(menuItems);
			return newTab;
		//}
	}
	
	/**
	 * Retrieves a tab according to the given tab number.
	 * @param tabNumber		The tab number of the desired tab
	 * 						(e.g., "2" for "Graph 2.")
	 * @return				The {@link CTATTab} corresponding to the given tab
	 * 						number if it is contained within this panel;
	 * 						<code>null</code> otherwise.
	 */
	public CTATTab getTabByNumber(int tabNumber) {
		return this.tabList.get(tabNumber - 1);
	}
	
	/** Clears out the tab panel. */
	public void clear() {
		this.tabList.clear();
	}
	
	/**
	 * Checks whether a given file has already been loaded in any window.
	 * Switches graph focus to the relevant window, if so.
	 * @param filepath		The file to check (as an absolute file path).
	 * @return				True if the given file is currently loaded in a window;
	 * 						false otherwise.
	 */
	public boolean hasLoadedFile(String filepath) {
		File f = new File(filepath);
		for (CTATTab tab : this.tabList)
		{
			File tabFile = new File(tab.getProblemModel().getProblemFullName());
			if(tabFile.equals(f))
			{
				if(trace.getDebugCode("mg"))
					trace.out("mg", "CTATTabManager (hasLoadedFile): same problem path");
		        server.getDockManager().showGraphWindow(tab.getTabNumber());
				return true;
			}
		}
		return false;
	}
	
	public void setFocusedTab(CTATTab tab, boolean reloadViews) {
		//
		int lastFocusedTabNumber = (this.focusedTab != null ? this.focusedTab.getTabNumber() : -1);
		if(trace.getDebugCode("mg"))
			trace.out("mg", "CTATTabMgr.setFocusedTab() arg tab "+tab.getTabNumber()+" ?= lastFocused "+
					lastFocusedTabNumber);
		this.focusedTab = tab;
		if(tab.getTabNumber() == lastFocusedTabNumber) return;
		//
		if(trace.getDebugCode("mg"))
			trace.out("mg", "CTATTabManager (setFocusedTab): focusing on tab " + tab.getTabNumber());
		DockManager dockManager = this.server.getDockManager();
		
		this.focusedTab = tab;
		this.focusedTab.updateCtatMenuItems();
		if(!this.server.isDoneIntializing()) return;
		dockManager.markAsFocused(tab.getTabNumber(), lastFocusedTabNumber);
		if(reloadViews && dockManager != null) {
			dockManager.refreshViews(false);
			this.server.getCtatMenuBar().refreshGraphDependentItems();
			tab.getController().updateStatusPanel(null);
		}
	}
	
	public void setFocusedTabByNumber(int tabNumber, boolean reloadViews) {
		setFocusedTab(getTabByNumber(tabNumber), reloadViews);
	}
	
	/*
	 * Updates the focused tab, refreshing views if necessary.
	 */
	public void updateIfNewTabFocus(int tabNumber) {
		if(getFocusedTab().getTabNumber() != tabNumber)
			setFocusedTab(getTabByNumber(tabNumber), true);
	}
	
	/**
	 * @return		the currently focused/highlighted tab.
	 */
	public CTATTab getFocusedTab() {
		return this.focusedTab;
	}
	
	/**
	 * @param tabNumber
	 * @return true if the tab is displayed, false otherwise.
	 */
	public boolean getTabVisibility(int tabNumber) {
		return this.getTabByNumber(tabNumber).isVisible();
	}
	
	/** Sets a visibility marker for the tab, indicating if the tab is displayed. Visibility
	 *  should be controlled by the window listener {@link edu.cmu.pact.ctatview.DockGraphWindowAdapter}.
	 * @param tabNumber
	 * @param visible
	 */
	public void setTabVisibility(int tabNumber, boolean visible) {
		if (tabNumber > 0 && tabNumber <= NUM_TABS)
			this.getTabByNumber(tabNumber).setVisible(visible);
	}
	
	/** For testing purposes: print information about the contents of the tab manager. */
	public void printInfo() {
		if(trace.getDebugCode("mg")) {
			trace.out("mg", "CTATTabManager (printInfo): size = "
					+ this.tabList.size() + " tab(s)");
			for(CTATTab tab : this.tabList) {
				trace.out("mg", "\t" + tab.getName() + ": problem model = "
						+ tab.getController().getProblemName() + ", "
						+ tab.getController().getProblemModel().getEdgeCount() + " edges and "
						+ tab.getController().getProblemModel().getNodeCount() + " nodes");
				trace.out("mg", "visible: "+tab.isVisible());
			}
		}
	}

	/**
	 * Connect an incoming socket with a behavior graph panel.
	 * @param guid identifier from InterfaceIdentification message
	 * @param sock connected socket
	 * @param br reader opened on the socket
	 * @socketArgs map of {@link SocketProxy}-related command-line arguments and values
	 * @return true if connected; false if caller should close the socket 
	 */
	public boolean connectSocket(String guid, Socket sock, BufferedReader br,
			Map<String, String> socketArgs) {
		CTATTab tab = findTabForConnection(guid);
		if(tab == null)
			return false;
		BR_Controller controller = tab.getLauncher().getController();
		guid = controller.getLauncher().getLauncherServer().editGuidForCollaboration(guid);

		if(trace.getDebugCode("tab"))
			trace.out("tab", "TabManager setting CTAT_Properties guid to " + guid);
		controller.getProperties().setProperty("guid", guid);
		
		controller.getLauncher().addNewSession(guid);  // was server.addNewSession()
		if(trace.getDebugCode("tab")) {trace.out("tab", "TabManager just called addNewSession");}
		
		SocketProxy sp = new SocketProxy(sock, socketArgs.get("MsgFormat"));
		controller.setRemoteProxy(sp);
		sp.setController(controller, br);
		SocketProxy.setMaxIdleTime(Long.MAX_VALUE);  // prevent inactivity timeouts at author time
		sp.setEom(socketArgs.get("EOM"));
		sp.setLogOnly(socketArgs.get("LogOnly"));
		sp.setClientHost(socketArgs.get("ClientHost"));
		sp.setUseSingleSocket(socketArgs.get("UseSingleSocket"));
		sp.setConnectFirst(socketArgs.get("ConnectFirst"));
		sp.setOneMsgPerSocket(socketArgs.get("OneMsgPerSocket"));
		sp.setClientPort(socketArgs.get("ClientPort"));

		sp.setServerPort(sock.getLocalPort());
		sp.setupLogServlet(null);

		UniversalToolProxy utp = controller.getUniversalToolProxy();
		if(trace.getDebugCode("tab")) 
			trace.out("tab", "TabManager.connectSocket() utp "+trace.nh(utp)+
				", sp.getToolProxy "+trace.nh(sp.getToolProxy()));
//		controller.setUniversalToolProxy(sp.getToolProxy());

		utp.awaitSetPreferences(true);
		
		//TODO adjust how the program handles the LogConsole being open
		//TODO check if a different socket passed in than SocketProxy sp below is necessary
		if(getNumLogConsoles() > 0)
			((RemoteToolProxy) utp).setTeeSocket(sock);
		if(trace.getDebugCode("tab")) {trace.out("tab", "TabManager connecting socket. Number of LogConsoles open is " + getNumLogConsoles());}

		sp.start();     // start the listener on this new socket
		return true;
	}

	/**
	 * Choose a tab to receive a new connection from a student interface.
	 * FIXME always returns {@link #getFocusedTab()}
	 * @param guid
	 * @return {@link #getFocusedTab()}
	 */
	private CTATTab findTabForConnection(String guid) {
		CTATTab result = getFocusedTab();
		if(null == result || !result.isVisible())
			return null;
		BR_Controller ctlr = result.getLauncher().getController();
		if(ctlr.getRemoteProxy() instanceof SocketProxy) {
			int reply = JOptionPane.showConfirmDialog(ctlr.getActiveWindow(),
					"The student interface \""+guid+"\" is trying to connect,"+
					"\nbut an interface is already active on this panel. Do you want to"+
					"\ndisconnect and replace it with "+guid+"?",
					"Student interface already active", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if(reply == JOptionPane.CANCEL_OPTION)
				return null;
			ctlr.disconnect(true);  // true => preserve session info
		}
		return result;
	}

	/**
	 * @param tabNumber
	 * @throws OperationAbortedException
	 */
	public void closeTab(int tabNumber) throws OperationAbortedException {
		CTATTab tab = this.getTabByNumber(tabNumber); 
		if (!tab.isVisible()) return; // no visible tab has focus!
		
    	BR_Controller ctlr = tab.getController();

		if (ctlr.startNewProblem() == JOptionPane.CANCEL_OPTION) {
			// abort the close
			throw new OperationAbortedException();
		} else {
			if(trace.getDebugCode("mg"))
				trace.out("mg", "CTATTabManager (closeTab): closing tab " + tabNumber);
			// disconnect from interface...
			if(ctlr.getRemoteProxy() instanceof SocketProxy) { // if Flash
				if(trace.getDebugCode("mg"))
					trace.out("mg", "CTATTabManager (closeTab): disconnecting  " + tabNumber);
				ctlr.disconnect(false);
			} else { // if Java
				// either close the graph *and* interface, or
				// clear the graph and abort the close, leaving the interface connected.
				if (ctlr.getStudentInterface() != null) {
					int reply = JOptionPane.showConfirmDialog(ctlr.getActiveWindow(),
							"Closing the window "+tab.getName()+" will also close the connected interface. \r\n" +
							"Cancel to leave the interface open and connected.  Continue?",
							"Close Student Interface", JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if(reply == JOptionPane.CANCEL_OPTION) {
						// abort the close
						setFocusedTab(tab,true);
						throw new OperationAbortedException();					
					} else {
						ctlr.closeStudentInterface();
					}
				}
			}			
		}
	}

	/**
	 * @return Number of currently visible tabs.
	 */
	public int numVisibleTabs() {
		int num = 0;
		for(CTATTab tabForFocus : this.tabList) {
			if (tabForFocus.isVisible()) {
				num++;
			}
		}
		return num;
	}

	/**
	 * @return the first visible tab whose problem model is nonempty;
	 *         if none, return a visible tab, if any; else return null.
	 */
	public CTATTab chooseVisibleTab() {
		CTATTab aVisibleTab = null;
		for(CTATTab visibleTab : this.tabList) {
			if(!visibleTab.isVisible())
				continue;
			if(!visibleTab.getProblemModel().isEmpty())
				return visibleTab;
			else
				aVisibleTab = visibleTab;
		}
		return aVisibleTab;
	}
	
	/**
	 * When LogConsole created another window, increase numLogConsoles counter by 1
	 */
	public void createdLogConsole(){
		numLogConsoles++;
		if(trace.getDebugCode("tab")) trace.out("tab", "Number of LogConsoles open: " + numLogConsoles);
	}
	/**
	 * When LogConsole closes a window, decrease numLogConsoles counter by 1
	 */
	public void closedLogConsole() {
		numLogConsoles--;
		if(trace.getDebugCode("tab")) trace.out("tab", "Number of LogConsoles open: " + numLogConsoles);
	}
	
	/**
	 * 
	 * @return numLogConsoles: Number of log consoles currently open
	 */
	public int getNumLogConsoles(){
		return numLogConsoles;
	} //TODO Change the way logconsoles are tracked and used with respect to tee sockets

	/*public int getMaxTabsServlet() {
		return MAX_TABS_SERVLET;
	}

	public void setMaxTabsServlet(int mAX_TABS_SERVLET) {
		MAX_TABS_SERVLET = mAX_TABS_SERVLET;
	}

	public int getNumTabServlet() {
		return NUM_TABS_SERVLET;
	}

	public void setNumTabServlet(int nUM_TABS_SERVLET) {
		NUM_TABS_SERVLET = nUM_TABS_SERVLET;
	}*/
}
