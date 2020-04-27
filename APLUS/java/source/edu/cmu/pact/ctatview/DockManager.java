/*
 * Created on Jun 22, 2006
 *
 * Collinl:  This appears to be where the CTAT meets the infonode road in that
 * 	this code handles loading of items into an infonode view and other aspects
 * 	of the display process.
 * 
 * MvV: Made some changes to the look and feel of the system and made it match
 * the color scheme and tone of the start state editor so that they now better
 * integrate. Hopefully the new look and feel is less harsh on the eyes. For
 * more information on how to customize all the settings please consult:
 * http://www.infonode.net/documentation/idw/guide/IDW%20Developer%27s%20Guide%201.3.pdf 
 */

package edu.cmu.pact.ctatview;

import java.util.HashMap;
import java.util.Map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;

import pact.CommWidgets.StudentInterfaceConnectionStatus;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import net.infonode.docking.properties.RootWindowProperties;
import net.infonode.docking.properties.TabWindowProperties;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent.SetModeEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent.SetVisibleEvent;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeListener;
import edu.cmu.pact.BehaviorRecorder.Dialogs.SkillsConsoleDialog;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorException;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorManager;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTab;
import edu.cmu.pact.BehaviorRecorder.Tab.CTATTabManager;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemSetWizard.CTATProblemSetWizard;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATStartStateEditor;
import edu.cmu.pact.BehaviorRecorder.View.GraphInspector.GroupEditor;
import edu.cmu.pact.BehaviorRecorder.View.VariableViewer.VariableViewer;
import edu.cmu.pact.Log.AuthorLogListener;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.view.CtatFrame;
import edu.cmu.pact.jess.JessConsolePanel;
import edu.cmu.pact.jess.RuleActivationTreePanel;
import edu.cmu.pact.jess.WMEEditorPanel;

public class DockManager extends CTATBase {
	private static final String SIM_STUDENT_PERSPECTIVE = "simStudentPerspective.bin";
	private static final String JESS_PERSPECTIVE = "jessPerspective.bin";
	private static final String EXAMPLE_TRACING_PERSPECTIVE = "exampleTracingPerspective.bin";
	private static final String TDK_PERSPECTIVE = "tdkPerspective.bin";
	private static final String USER_PERSPECTIVE_DIR = 
			System.getProperty("user.home") + File.separatorChar + ".ctat";
	private static final String DEFAULT_PERSPECTIVE_DIR = "perspectives";

	RootWindow rootWindow;
	ViewMap viewMap = new ViewMap();
	ViewMap graphViewMap = new ViewMap();

	private RootWindow graphEditorRootWindow;
	private View graphPlaceholderView;
	private boolean graphPlaceholderAdded = false;


	CtatFrame ctatFrame;

	/*
	 * Dynamic view info. Variables used to handle the dynamic views with a
	 * dynamic view minimum guaranteeing that lower level views will not be
	 * added. Curr ViewCount is updated with each view addition to reflect the
	 * count and thus available ids. DynamicViewMin represents the lowest ID
	 * that will be permitted for a dynamically added view.
	 */
	private int LastViewCount = 0;

	private static final int CONFLICT_TREE = 1;
	private static final int JESS_CONSOLE = 1 + CONFLICT_TREE;
	private static final int WME_EDITOR = 1 + JESS_CONSOLE;
	private static final int GROUP_EDITOR = 1 + WME_EDITOR;
	private static final int VARIABLE_VIEWER = 1 + GROUP_EDITOR;
	private static final int SKILLS_CONSOLE = 1 + VARIABLE_VIEWER;
	private static final int SS_EDITOR = 1 + SKILLS_CONSOLE;
	private static final int PSD_WIZARD = 1 + SS_EDITOR;
	private static final int DYNAMIC_VIEW_MIN_ID = 1 + PSD_WIZARD;
	private static final int MAX_DYNAMIC_VIEWS = 3;
	private static final int SIM_STUDENT = DYNAMIC_VIEW_MIN_ID
			+ MAX_DYNAMIC_VIEWS;
	private static final int GRAPH_EDITOR = SIM_STUDENT + 1;
	
	// Graph windows use a separate view map with index based on tab number
	//private static final int INIT_BEHAVIOR_RECORDER = GRAPH_EDITOR + 1;
	private Map<DockingWindow, Integer> menuIdMap = new HashMap<DockingWindow, Integer>();
	private int currentGraphNumber;

	// LinkPanel Management is handled by this item.
	private LinkInspectorManager LinkPanelManager;

	/** Menu for panels still under development. */
	private JMenu experimentalViewMenu = null;

	// Extra panels to use for additional behavior recorder windows

	private CTAT_Launcher server;
	private GroupEditor groupEditor;
	private VariableViewer variableViewer;
	private SkillsConsoleDialog skillsConsoleDialog;
	private CTATStartStateEditor startStateEditor;
	private RuleActivationTreePanel conflictTreePanel;
	private JessConsolePanel jessPanel;
	private WMEEditorPanel wmeEditorPanel;
	
	public DockManager(CTAT_Launcher server) {
		setClassName("DockManager");
		debug("DockManager ()");
		this.server = server;
		this.conflictTreePanel = null;
		this.wmeEditorPanel = null;
	}

	public DockManager(BR_Controller controller) {
		setClassName("DockManager");
		debug("DockManager ()");

		controller.addCtatModeListener(new CtatModeListener() {
			public void ctatModeEventOccured(CtatModeEvent e) {
				trace.out("br", "ctatModeEventOccured: " + e.toString());

				if (e instanceof SetModeEvent) {
					SetModeEvent m = (SetModeEvent) e;
					String previousMode = m.getPreviousMode();
					String currentMode = m.getMode();
					updatePerspective(previousMode, currentMode);
				} else if (e instanceof SetVisibleEvent) {
					final SetVisibleEvent sve = (SetVisibleEvent) e;

					if (ctatFrame != null)
						javax.swing.SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								ctatFrame.setVisible(sve.isVisible());
							}
						});
				}
			}
		});
	}

	protected void updatePerspective(String previousMode, String currentMode) {
		trace.out("br", "DockManager.updatePerspective(" + previousMode
				+ ", " + currentMode + ")");

		if (!currentMode.equals(previousMode)) {
			saveLayout(previousMode);
			String perspectiveName = getPerspectiveName(currentMode);
			if (perspectiveName == null)
				return;

			String perspectiveFile = getUserPerspectiveFile(currentMode);

			if (perspectiveFile == null || !(new File(perspectiveFile).exists()))
				perspectiveFile = this.getDefaultPerspectiveFile(currentMode);

			if (!loadPerspective(perspectiveFile)) {
				trace.outNT("br", "loadLayout(" + perspectiveFile + ") failed. Retrying...");
				loadPerspective(perspectiveFile);
			}
		}
	}

	/**
	 * Convert a mode name into file name.
	 * 
	 * @param mode
	 * @return one of {@link #EXAMPLE_TRACING_PERSPECTIVE}, etc.; null if mode
	 *         undefined
	 */
	private String getPerspectiveName(String mode) {
		String perspectiveFileName = null;

		if (CtatModeModel.EXAMPLE_TRACING_MODE.equals(mode)) {
			perspectiveFileName = EXAMPLE_TRACING_PERSPECTIVE;
		} else if (CtatModeModel.JESS_MODE.equals(mode)) {
			perspectiveFileName = JESS_PERSPECTIVE;
		} else if (CtatModeModel.SIMULATED_STUDENT_MODE.equals(mode)) {
			perspectiveFileName = SIM_STUDENT_PERSPECTIVE;
		} else if (CtatModeModel.TDK_MODE.equals(mode))
			perspectiveFileName = TDK_PERSPECTIVE;

		return perspectiveFileName;
	}

	public void saveLayout(String mode) {
		if (rootWindow == null) {
			return;
		}

		String perspectiveFileName = getPerspectiveName(mode);
		trace.outNT("br", "DockManager.saveLayout(" + mode + ") perspective "
				+ perspectiveFileName);

		if (perspectiveFileName == null) {
			return;
		}

		try {
			File f = new File(USER_PERSPECTIVE_DIR);
			trace.out("br",	"Does " + USER_PERSPECTIVE_DIR + " exist? " + f.exists());
			if (!f.exists()) {
				trace.out("br", "DockManager.saveLayout(" + mode
						+ ") creating dir " + USER_PERSPECTIVE_DIR);
				f.mkdir();
			}
		} catch (NullPointerException e) {
			trace.out("br", "Value of USER_PERSPECTIVE_DIR is null");
		}

		String perspectiveFile = getUserPerspectiveFile(mode);
		trace.outNT("br", "DockManager.saveLayout(" + mode + ") to file "
				+ perspectiveFile);

		// Write the window state to a byte array
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(new File(perspectiveFile));

			ObjectOutputStream out = new ObjectOutputStream(fos);
			rootWindow.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			trace.err("User perspective file "+perspectiveFile+" not found");
			//e.printStackTrace();
		} catch (IOException e) {
			trace.err("User perspective file "+perspectiveFile+" IO error: "+e.getMessage());
			//e.printStackTrace();
		}
	}

	private String getDefaultPerspectiveFile(String mode) {
		String perspectiveFileName = getPerspectiveName(mode);

		if (perspectiveFileName == null) {
			return null;
		}

		String defaultPerspectiveFile = DEFAULT_PERSPECTIVE_DIR
				+ File.separatorChar + perspectiveFileName;
		return defaultPerspectiveFile;
	}

	private String getUserPerspectiveFile(String mode) {
		String perspectiveFileName = getPerspectiveName(mode);

		if (perspectiveFileName == null) {
			return null;
		}

		String userPerspectiveFile = USER_PERSPECTIVE_DIR + File.separatorChar
				+ perspectiveFileName;

		return userPerspectiveFile;
	}

	/**
	 * Load a perspective file proper to the given mode. Tries first in 
	 * {@value #USER_PERSPECTIVE_DIR}, then {@value #DEFAULT_PERSPECTIVE_DIR}.
	 * @param mode one of {@value CtatModeModel#EXAMPLE_TRACING_MODE}, 
	 *        {@value CtatModeModel#JESS_MODE}, etc.
	 * @return result of {@link #loadPerspective(String)}
	 */
	public boolean loadLayout(String mode) {
		String perspectiveFile = getUserPerspectiveFile(mode);
		File f = new File(perspectiveFile);
		if (trace.getDebugCode("br"))
			trace.outNT("br", "DockManager.loadLayoutForMode(" + mode + ") user perspective file "
					+ perspectiveFile + ", can read "+f.canRead());
		if(!f.canRead())
			perspectiveFile = getDefaultPerspectiveFile(mode);
		return loadPerspective(perspectiveFile);
	}

	private boolean loadPerspective(String filename) {
		boolean layoutLoaded = false;
		trace.outNT("br", "DockManager.loadLayout(" + filename
				+ ") rootWindow " + (rootWindow == null ? "null" : "created"));

		if (filename == null || rootWindow == null) {
			return layoutLoaded;
		}

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(
					filename));
			System.out.println(" File Name : "+filename);
			rootWindow.read(new ObjectInputStream(fileInputStream));
			fileInputStream.close();
			layoutLoaded = true;
		} catch (FileNotFoundException e) {
			trace.err("exception " + e + " on loadLayout(" + filename + "): "
					+ "No stored window layout found");
		} catch (IOException e) {
			trace.err("exception " + e + " on loadLayout(" + filename + ")");
			//e.printStackTrace();
			new File(filename).delete();
		} catch (IndexOutOfBoundsException e) {
			trace.err("exception " + e + " on loadLayout(" + filename + ")");
			//e.printStackTrace();
		} catch (NullPointerException e) {
			trace.err("exception " + e + " on loadLayout(" + filename + ")");
		} catch (Exception e) {
			trace.err("exception " + e + " on loadLayout(" + filename + ")");
		}
		return layoutLoaded;
	}

	private void dockWindows(boolean showCtatWindow) {
		experimentalViewMenu = null; // recreate menu

		/*
		 * Collinl: According to Jonathan the "reduced mode" was created for the
		 * purposes of a specific study. The test here sets the mode and stores
		 * that fact for below.
		 * 
		 * The ordering is essential to preserve as the ID number must remain
		 * consistent with the order of loading. Thus any new windows must be
		 * added below after the group Creater view.
		 */
		final BR_Controller controller = this.server.getFocusedController();

		boolean notReduced = !(controller instanceof BR_Controller && server
				.isReducedMode());

		if (VersionInformation.includesJess()) {
			this.jessPanel = new JessConsolePanel(getServer(), true);
			if (notReduced) {
				this.wmeEditorPanel = new WMEEditorPanel(getServer(), null, false, false);
				this.conflictTreePanel = new RuleActivationTreePanel(getServer());
			}
			addView(this.conflictTreePanel, "Conflict Tree", CONFLICT_TREE);
			addView(this.jessPanel, "Jess Console", JESS_CONSOLE);
			addView(this.wmeEditorPanel, "WME Editor", WME_EDITOR);
		}

		this.groupEditor = new GroupEditor(this.server);
		addView(this.groupEditor, "Group Editor", GROUP_EDITOR);

		this.variableViewer = new VariableViewer(this.server);
		addView(this.variableViewer, "Variable Viewer", VARIABLE_VIEWER);

		this.skillsConsoleDialog = SkillsConsoleDialog.create(this.server);
		addView(this.skillsConsoleDialog, "Skills Console", SKILLS_CONSOLE);

		// Initialize the graph windows
		addGraphWindows();
		// Create the Graph Editor as a RootWindow. It has it's own (not serialized) view map.		
		graphEditorRootWindow = DockingUtil.createRootWindow(graphViewMap, true);
		addGraphEditorView(graphEditorRootWindow, "Behavior Graph Editor", GRAPH_EDITOR);

		this.startStateEditor = new CTATStartStateEditor();
		this.startStateEditor.setController(controller);
		addExperimentalView(this.startStateEditor, "Start State Editor", SS_EDITOR);

		CTATProblemSetWizard psetwizard = new CTATProblemSetWizard();
		addExperimentalView(psetwizard, "Problem Set and Deployment Wizard", PSD_WIZARD);
		
		// addView()
		/*
		 * addView(controller.getMissController().getMissConsole(),
		 * "Simulated Student Console", 6 );
		 */

		/*
		 * Collinl: Handle the initialization of the LinkInspectors. This is
		 * wrapped in a try block just to handle any initialization issues for
		 * the LinkInspectorManager.
		 */
		try {
			trace.out("linkinspector", "Adding Link Inspector Manager.");
			/*
			 * Initialize the LinkInspectorManager feeding it this DockManager
			 * for view updates and the controller for access to the Problem
			 * Model.
			 */
			this.LinkPanelManager = new LinkInspectorManager(this, getServer()
					.getFocusedController());

		} catch (LinkInspectorException E) {
			trace.err("Error in LinkInspector Initialization: " + E);
			//E.printStackTrace();
		} finally {
		}

		// CTAT2709: Add the Experimental submenu to the View menu.
		if (experimentalViewMenu != null)
			ctatFrame.getCtatMenuBar().getViewPanelMenu()
					.add(experimentalViewMenu);

		trace.out("views", "Drawing Window.");

		rootWindow = DockingUtil.createRootWindow(viewMap, true);		
		this.rootWindow.addListener(new DockWindowAdapter(this, 0));
		this.menuIdMap.put(rootWindow, Integer.valueOf(0));
		trace.out("views", "Done Drawing Window.");

		ctatFrame.setTitle("Cognitive Tutor Authoring Tools");
		ctatFrame.setName("Docked Window");
		ctatFrame.setDockable(false);

		RootWindowProperties properties = new RootWindowProperties();

		// Set gradient theme. The theme properties object
		// is the super object of our properties object, which
		// means our property value settings will override the
		// theme values. For more information on how to use this
		// toolkit, please consult:
		// http://www.infonode.net/documentation/idw/guide/IDW%20Developer%27s%20Guide%201.3.pdf

        //properties.addSuperObject(new GradientDockingTheme().getRootWindowProperties());
        properties.addSuperObject(new ShapedGradientDockingTheme().getRootWindowProperties());
    	//properties.addSuperObject(new BlueHighlightDockingTheme().getRootWindowProperties());

		// Our properties object is the super object of the
		// root window properties object, so all property values of the
		// theme and in our property object will be used by the root window
		rootWindow.getRootWindowProperties().addSuperObject(properties);
		graphEditorRootWindow.getRootWindowProperties().addSuperObject(properties);

		ctatFrame.getContentPane().add(rootWindow);
		ctatFrame.applyPreferences();
		ctatFrame.storeLocation();
		ctatFrame.storeSize();
		
		ctatFrame.setVisible(showCtatWindow);
		
		ctatFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		ctatFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				getServer().getFocusedController().closeApplication(true);
			}
		});

	}

	/**
	 * Adds a place-holder tab and the first graph tab to the Graph Editor. 
	 */
	void addGraphWindows() {
		GraphEditorDefaultPanel defaultGraphPanel = new GraphEditorDefaultPanel();
		defaultGraphPanel.setName("defaultGraphPanel");
		graphPlaceholderView = new View("Start", null, defaultGraphPanel);
		graphPlaceholderView.getWindowProperties().setCloseEnabled(true);
		addGraphTabView(1);
	}

	/**
	 * Shows and hides a place-holder panel in the graph editor. Show when no graphs are
	 * visible; hide if one or more graphs are visible. Always show maximized rather than
	 * as a tab. Controlled by {@link DockGraphWindowAdapter}.
	 * @param show
	 */
	public void showGraphPlaceHolder(boolean show) {
		if (show) {
			if (this.graphPlaceholderAdded) {
				graphPlaceholderView.restore();
				graphPlaceholderView.maximize();
			} else {
				DockingUtil.addWindow(graphPlaceholderView, graphEditorRootWindow);
				this.graphPlaceholderAdded = true;
				graphPlaceholderView.maximize();
			}
		} else {
			graphPlaceholderView.close();
		}
	}
	
	/**
	 * Add a menu item to the
	 * 
	 * @param panel
	 * @param title
	 * @param viewCount
	 */
	private void addExperimentalView(JComponent panel, final String title,
			int viewCount) {
		// Perform the core View addition.
		final View view = this.addCoreView(panel, title, viewCount); // View
																		// contains
																		// JPanel

		JMenuItem menuItem = new JMenuItem(view.getTitle());

		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.restore();
			}
		});
		addExperimentalViewMenuItem(menuItem);
	}

	public void addExperimentalViewMenuItem(JMenuItem menuItem) {
		if (this.experimentalViewMenu == null)
			this.experimentalViewMenu = new JMenu("Experimental");
		experimentalViewMenu.add(menuItem);
	}

	/**
	 * @param frame
	 * @return
	 */
	static JComponent getPanelFromFrame(JFrame frame) {
		JComponent panel;
		panel = new JRootPane();

		((JComponent) frame.getContentPane()).setMinimumSize(new Dimension(20,
				20));
		((JRootPane) panel).setContentPane(frame.getContentPane());
		((JRootPane) panel).setJMenuBar(frame.getJMenuBar());
		frame.setVisible(false);
		return panel;
	}

	/**
	 * addView
	 * 
	 * @param panel
	 *            : The JComponent used as a panel for display.
	 * @param title
	 *            : The title string to be used.
	 * @param viewCount
	 *            : The number of views to use.
	 * 
	 *            Collinl: This code is called to add new views to the list of
	 *            windows. It is called at the initialization of the system to
	 *            add panels to the list. It is not clear how this would work
	 *            for the construction of empty windows as it takes an existing
	 *            panel.
	 * 
	 *            In being called it populates, through the ctatFrame.addView
	 *            command, the list of available views.
	 * 
	 *            In the format below the addView command takes a static
	 *            viewCount. This will be supplemented by a dynamic call that
	 *            gets the current View Count and then adds a new view indexed
	 *            higher than it. This is done just to make it possible to add
	 *            dynamic views for the LinkInspector.
	 * 
	 *            I have refactored this to return the View when called for the
	 *            cases where it is necessary to track the view as made.
	 * 
	 *            Formerly this code contained all of the call items necessary.
	 *            Now this calls a view subfunction and then adds the view to
	 *            the ctatFrame which performs the update of the view menu. In
	 *            future the separate menu should be removed.
	 */
	private void addView(JComponent panel, final String title, int viewCount) {
		// Perform the core View addition.
		final View view = addCoreView(panel, title, viewCount);
		view.addListener(new DockWindowAdapter(this, viewCount));

		this.menuIdMap.put(view, Integer.valueOf(viewCount));

		// Now for static views, add them to the Show Window menu.
		this.ctatFrame.addView(view, viewCount);
	}

	private void addGraphEditorView(JComponent panel, final String title, int viewCount) {
		// Perform the core View addition.
		final View view = addCoreView(panel, title, viewCount);
		view.addListener(new DockWindowAdapter(this, -1));
		// special properties for the window containing graph tabs
		view.getViewProperties().setAlwaysShowTitle(false);
		view.getWindowProperties().setCloseEnabled(false);
		//graphEditorRootWindow.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		TabWindowProperties props = graphEditorRootWindow.getRootWindowProperties().getTabWindowProperties();
		props.getCloseButtonProperties().setVisible(false);
		props.getUndockButtonProperties().setVisible(false);
		props.getMaximizeButtonProperties().setVisible(false);
	}

	public void addGraphTabView(final int tabNumber) {
		final CTATTabManager manager = getServer().getTabManager();
		CTATTab tab = manager.getTabByNumber(tabNumber);
		View tabView = tab.getView();
		
		// override graph editor root window settings
		tabView.getWindowProperties().setCloseEnabled(true); 
		tabView.getWindowProperties().setUndockEnabled(false);
		tabView.addListener(new DockGraphWindowAdapter(this, manager, tabNumber));

		this.graphViewMap.addView(tabNumber, tabView);
	}

	public int newGraphTab() {
		final CTATTabManager manager = getServer().getTabManager();
		CTATTab newTab;
		int tabNumber = -1;
		if (manager.getFreeTab() != null) {
			newTab = manager.getFreeTab();
			tabNumber = newTab.getTabNumber();
		} else {
			int tabNum = manager.getNextTabNumber();
			newTab = new CTATTab(tabNum);		
			newTab = manager.getNewTab(newTab, null);
			if (newTab != null) {
				this.addGraphTabView(tabNum);
				DockingUtil.addWindow(newTab.getView(), graphEditorRootWindow);
				updateGraphConnectionStatus(tabNum, newTab.getController().getUniversalToolProxy().getStudentInterfaceConnectionStatus());
				tabNumber = tabNum;
			}
		}
    	if (newTab != null) manager.setFocusedTab(newTab, true);
    	return tabNumber;
	}
	
	/**
	 * Returns the unique ID corresponding to a view displayed in the Show
	 * Window menu (or the root window view). Does not include graph views.
	 * 
	 * @param view
	 *            the view to check for
	 * @return the integer ID of the given view if it is in the Window menu; -1
	 *         otherwise
	 */
	public int findView(DockingWindow view) {
		Integer result = this.menuIdMap.get(view);
		if (result != null)
			return result.intValue();
		return -1;
	}

	/**
	 * addDynamicView
	 * 
	 * @author Collin Lynch
	 * 
	 * @param panel
	 *            : JComponent for the view panel.
	 * @param title
	 *            : The Component title.
	 * 
	 * @returns: An int representing the viewCount.
	 * 
	 *           This method will add a new static view to the ViewMap but will
	 *           use the current viewCount to set the value. It will not make a
	 *           call to the ctatFrame to add the item to the view menu.
	 * 
	 *           NOTE:: This still needs to be called before the
	 *           DockingUtil.createRootWindow call is made.
	 */
	public View addDynamicView(JComponent panel, final String title) {
		int NewViewCount;
		View NewView;

		trace.outNT("views", "Producing Dynamic window.");

		// Calculate the dynamic view ID.
		if (this.LastViewCount < DYNAMIC_VIEW_MIN_ID) {
			NewViewCount = DYNAMIC_VIEW_MIN_ID;
		} else {
			NewViewCount = this.LastViewCount + 1;
		}

		// Now add the window itself.
		NewView = this.addCoreView(panel, title, NewViewCount);
		NewView.restore();

		trace.outNT("views", "Dynamic window: " + NewViewCount);

		// Now return the nre count.
		// return NewViewCount;
		return NewView;
	}

	/**
	 * addCoreView
	 * 
	 * @param panel
	 *            : The JComponent panel to be added.
	 * @param title
	 *            : The title to be used.
	 * @param ViewCount
	 *            : The Number to be assigned to the view in the map.
	 * 
	 * @returns: The View object itself as added to the DockManager.
	 * 
	 *           This code performs the core additions to the ViewMap including
	 *           adding the necessary logging information and others. It does
	 *           not execute the CtatFrame call to add to the View Menu.
	 */
	private View addCoreView(JComponent panel, final String title, int viewCount) {

		final View view = new View(title, null, panel);

		// Collinl: Added the code below for window production.
		trace.out("views", "Producing Window: "+title);

		// for author logging
		if (getServer().getLoggingSupport() != null)
			view.addListener(new AuthorLogListener(getServer()
					.getLoggingSupport()));

		viewMap.addView(viewCount, view);

		/*
		 * Account for the view addition in the system incrementing the
		 * ViewCount as needed as this will reflect system changes. This Does
		 * not force static views to not overwrite one-another leaving that up
		 * to the windowing system.
		 */
		if (viewCount > this.LastViewCount) {
			this.LastViewCount = viewCount;
		}

		// Once the view is added print some trace output indicating
		// the available views.
		trace.outNT("views", "Curr View Count: " + this.viewMap.getViewCount());

		// Return the newly generated view.
		return view;
	}

	/**
     * 
     */
	public View getCoreView(String title) {
		debug("getCoreView (" + title + ")");

		if (viewMap == null) {
			debug("Internal error: viewMap is null");
			return (null);
		}

		for (int i = 0; i < viewMap.getViewCount(); i++) {
			View testView = viewMap.getView(i);

			if (testView != null) {
				if (testView.getTitle() != null) {
					if (testView.getTitle().equalsIgnoreCase(title) == true) {
						debug("Found view " + title + " at index: " + i);
						return (testView);
					}
				} else
					debug("Info: a view in the view map does not have a title");
			} else
				debug("Internal error: view is null at index: " + i);
		}

		return (null);
	}

	/**
	 * addListenerToRootWindow
	 * 
	 * @author Collin Lynch.
	 * 
	 * @param Listener
	 *            : The Listener implementer.
	 * 
	 *            Add a DockingWindowAdapter descendant or other item that
	 *            implements the DockingWindowListener interface to the root
	 *            window so that focus events or other changes may be trapped as
	 *            needed.
	 * 
	 *            This is needed for the LinkInspectorPanels and other "dynamic"
	 *            windows that must be notified about their visual status in
	 *            order to operate.
	 * 
	 *            Note that as per the documentation the authors of the
	 *            infornode code recommend that you not implerment the
	 *            DockingWindowListener interface directly but subclass the
	 *            DockingWindowAdapter.
	 */
	public void addListenerToRootWindow(DockingWindowAdapter Adapter)
			throws CtatviewException {
		if (this.rootWindow == null) {
			throw new CtatviewException("Null Root Window.");
		} else {
			this.rootWindow.addListener(Adapter);
		}
	}

	/**
	 * informViewStatus
	 * 
	 * The dynamic views used in the LinkInspector need to be informed of what
	 * item is loaded or active at this point versus which ones are still
	 * binned. This method passes a call to the LinkInspectorManager to update
	 * those views.
	 * 
	 * Any other dynamic views or views that need to be informed of their status
	 * *after* the layout is loaded should be included here.
	 */
	private void informViewStatus() throws CtatviewException {
		this.LinkPanelManager.postWindowingUpdate();
	}

	public void addSimStConsole() {
		addView(getServer().getMissController().getMissConsole(),
				"Simulated Student Console", SIM_STUDENT);
	}

	/**
	 * dockWindowsNow
	 * 
	 * This call appears to be the one that is called on startup by the
	 * CtatManager. When called this causes the windows to be loaded up via the
	 * dockWindows Command. It will then load the window perspective. Then, once
	 * done it sends an update message via informViewStatus to tell the views
	 * that need to know whether they are maximized or minimized.
	 */
	public void dockWindowsNow(boolean showCtatWindow) throws CtatviewException {
		dockWindows(showCtatWindow);
		String currentMode = getServer().getFocusedController()
				.getCtatModeModel().getCurrentMode();
		// trace.outNT("views", "Done Docking, updating perspective.");
		this.updatePerspective("", currentMode);
		// trace.outNT("views", "Done updating perspective.");
		this.informViewStatus();
	}

	public void restoreDefaultView() {
		// restoration is done from a blank template, so mark everything as
		// hidden
		int firstUnusedID = GRAPH_EDITOR; //INIT_BEHAVIOR_RECORDER + MAX_RECORDERS;
		for (int i = 1; i < firstUnusedID; i++) {
			if (this.ctatFrame.isTracked(i)) {
				setMenuVisibilityMarker(i, false);
			}
		}
		String mode = getServer().getFocusedController().getCtatModeModel()
				.getCurrentMode();
		String perspectiveFileName = getPerspectiveName(mode);
		if (perspectiveFileName == null)
			return;

		String defaultPerspectiveFile = getDefaultPerspectiveFile(mode);
		if (!loadPerspective(defaultPerspectiveFile)) {
			trace.out("br", "loadLayout(" + defaultPerspectiveFile
					+ ") failed. Retrying...");
			loadPerspective(defaultPerspectiveFile);
		}
	}

	private CTAT_Launcher getServer() {
		return this.server;
	}

	/**
	 * Called upon switching focus between graph tabs. Refreshes the group
	 * editor, group creator, etc. according to the currently focused problem.
	 * 
	 * @param connectionChanged
	 *            true if calling after a problem's connection status has been
	 *            updated; false otherwise
	 */
	public void refreshViews(boolean connectionChanged) {
		if (!getServer().isDoneIntializing())
			return;
		changeModes();
		// refresh windows only if they're actually visible
		if (isWindowVisible(VARIABLE_VIEWER)) {
			this.variableViewer.refresh();
		}
		if (connectionChanged)
			return;
		// the following windows don't need to change or update automatically
		// if only the connection status needs updating
		if (isWindowVisible(GROUP_EDITOR)) {
			this.groupEditor.refresh();
		}
		if (isWindowVisible(SKILLS_CONSOLE)) {
			this.skillsConsoleDialog.refresh();
		}

		BR_Controller controller = getServer().getFocusedController();
		if (isWindowVisible(SS_EDITOR)) {
			this.startStateEditor.setController(controller);
		}

		boolean notReduced = !(getServer().isReducedMode());
		if (VersionInformation.includesJess()) {
			if (isWindowVisible(JESS_CONSOLE)) {
				this.jessPanel.refresh();
			}
			if (notReduced) {
				if (isWindowVisible(CONFLICT_TREE)) {
					this.conflictTreePanel.refresh();
				}
				if (isWindowVisible(WME_EDITOR)) {
					this.wmeEditorPanel.refresh();
				}
			}
		}
	}

	/**
	 * Handles tool window showing/hiding. Anything that affects the windows status
	 * should go through here.
	 * 
	 * @param viewID
	 *            The view number with which the desired view is initialized
	 * @param visible
	 *            true if the window is being shown; false if hidden
	 */
	public void setViewVisibility(int viewID, boolean visible) {
		View view = this.viewMap.getView(viewID);
		if (visible) {
			view.restore();
		} else {
			view.close();
		}
		setMenuVisibilityMarker(viewID, visible);
	}

	/**
	 * Handles graph window showing/hiding. Anything that affects the windows status
	 * should go through here.
	 * 
	 * @param viewID
	 *            The view number with which the desired view is initialized
	 * @param visible
	 *            true if the window is being shown; false if hidden
	 */
	public void setGraphVisibility(int viewID, boolean visible) {
		View view = this.graphViewMap.getView(viewID);
		if (visible) {
			view.restore();
		} else {
			view.close();
		}
	}

	/**
	 * Brings up the window corresponding to the given graph tab number if it
	 * isn't already shown on screen, giving it focus if it doesn't already have
	 * focus.
	 * 
	 * @param tabNumber
	 *            The tab number corresponding to the given graph.
	 */
	public void showGraphWindow(int tabNumber) {
		// show the window if it's been hidden and mark as shown
		setGraphVisibility(tabNumber, true);
		// set the focus to this window
		focusOnGraph(tabNumber);
	}

	public void focusOnGraph(int tabNumber) {
		View view = this.graphViewMap.getView(tabNumber);
		view.restoreFocus();
	}

	/**
	 * @param viewID
	 *            The view number with which the desired view is initialized
	 * @return true if the window is shown; false if it is hidden
	 */
	private boolean isWindowVisible(int viewID) {
		return this.ctatFrame.isWindowVisible(viewID);
	}

	/**
	 * Sets the visibility flag/checkbox in the Windows menu. Redundant if
	 * called from a menu selection, but necessary for window hiding via the
	 * top-right "X" button.
	 * 
	 * @param viewID
	 *            The view number with which the desired view is initialized
	 * @param visible
	 *            true if the window is shown; false if it is hidden
	 */
	void setMenuVisibilityMarker(int viewID, boolean visible) {
		if(viewID <= 0) return;
		//trace.out("mg", "DockManager (setMenuVisibilityMarker): view " + viewID + " -> " + (visible ? "(x)" : "( )"));
		this.ctatFrame.setViewVisibilityMarker(viewID, visible);
	}

	public JessConsolePanel getJessPanel() {
		return this.jessPanel;
	}

	public RuleActivationTreePanel getConflictTreePanel() {
		return this.conflictTreePanel;
	}

	/**
	 * Update the author/tutor modes according to the currently focused problem.
	 */
	private void changeModes() {
		this.ctatFrame.getCtatModePanel().changeModes(
				getServer().getFocusedController());
	}

	/**
	 * Called upon opening/restoring a specific view whose properties/contents
	 * vary by focused graph (e.g., group editor, Jess console). Refreshes the
	 * specified window. Use should be mutually exclusive with respect to
	 * refreshViews().
	 * 
	 * @param windowID
	 *            the desired view's stored unique view ID
	 */
	void refreshWindow(int windowID) {
		switch (windowID) {
		case CONFLICT_TREE:
			this.conflictTreePanel.refresh();
			break;
		case JESS_CONSOLE:
			this.jessPanel.refresh();
			break;
		case WME_EDITOR:
			this.wmeEditorPanel.refresh();
			break;
		case GROUP_EDITOR:
			this.groupEditor.refresh();
			break;
		case SKILLS_CONSOLE:
			this.skillsConsoleDialog.refresh();
			break;
		default:
			break;
		}
	}

	public WMEEditorPanel getMainWMEEditorPanel() {
		return this.wmeEditorPanel;
	}

	/**
	 * Updates the title of a behavior recorder tab to display the tab's graph
	 * problem name.
	 * 
	 * @param tabNumber
	 *            The tab number corresponding to the problem/tab to update.
	 */
	public void refreshGraphTitle(int tabNumber) {
		View view = this.graphViewMap.getView(tabNumber);
		// String newTitle =
		// this.server.getTabManager().getTabByNumber(tabNumber).getProblemModel().getProblemName();
		// use the filename as the title
		String newTabTitle, newMenuText;
		ProblemModel pm = this.server.getTabManager().getTabByNumber(tabNumber).getProblemModel();
		if(!pm.isEmpty()) {
			String fullName = this.server.getTabManager().getTabByNumber(tabNumber)
					.getProblemModel().getProblemFullName();
			newTabTitle = fullName.substring(fullName
					.lastIndexOf(File.separator) + 1);
			newMenuText = newTabTitle;
		}
		else {
			newTabTitle = CTATTab.INIT_TITLE_PREFIX+tabNumber;
			//trace.out("mg","refreshGraphTitle2 for tab "+tabNumber+" to "+newTabTitle);
			newMenuText = "";
		}
		view.getViewProperties().setTitle(newTabTitle);
		//getServer().getFocusedController().getJGraphWindow().setName(newTitle);
		//this.ctatFrame.updateGraphMenuItem(tabID, newMenuText);
		view.validate();
		view.repaint();
	}

	/**
	 * Updates the connection status icon in the tab heading for the given tab's
	 * graph.
	 * 
	 * @param tabNumber
	 *            The number of the graph to update
	 * @param sics
	 *            The connection status of the graph's problem
	 */
	public void updateGraphConnectionStatus(int tabNumber,
			StudentInterfaceConnectionStatus sics) {
		if(trace.getDebugCode("mg"))
			trace.out("mg", "DockMgr.udpateGraphConnectionStatus("+tabNumber+", "+sics+")");
		View view = this.graphViewMap.getView(tabNumber);
		view.getViewProperties().setIcon(sics.getIcon());
		refreshViews(true);
	}

	/**
	 * FIXME: index is always -1; instead, should determine if the view equals a tab.
	 * @param view
	 * @return <code>true</code> if the view directly contains a behavior
	 *         recorder graph, <code>false</code> otherwise
	 */
	public boolean isGraphView(View view) {
		int index = -1;
		if (this.graphEditorRootWindow != null) {
			index = this.graphEditorRootWindow.getChildWindowIndex(view);
		}
		if (index > -1) return true;
		return false;
	}

	/**
	 * Cleanup: reset view titles and icons before saving.
	 */
	public void clearGraphViews(String mode) {
		int tabs = 0;
	    tabs = CTATTabManager.getNumTabs();
		for (int i = 0; i < tabs; i++) {
			// get the view
			int tabNumber = i + 1;
			View view = this.graphViewMap.getView(tabNumber);
			CTATTab tab = getServer().getTabManager().getTabByNumber(tabNumber);
			String defaultWindowName = tab.getName();
			// reset the view title to default
			view.getViewProperties().setTitle(defaultWindowName);
			// if the problem is connected to an interface, reset the tab icon
			// to disconnected status
			if (tab.getController().getInterfaceLoaded()) {
				updateGraphConnectionStatus(tabNumber,
						StudentInterfaceConnectionStatus.Disconnected);
			}
		}
		saveLayout(mode);
	}

    public void markAsFocused(int tabNumber, int lastTabNumber) {
		if(tabNumber == lastTabNumber) return;
		if(lastTabNumber > 0) {
			//trace.out("mg", "DockManager (markAsFocused): last = " + lastTabNumber + ", next = " + tabNumber);
			View lastView = this.graphViewMap.getView(lastTabNumber);
			lastView.setBorder(null);
		}
		View view = this.graphViewMap.getView(tabNumber);
		view.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		this.currentGraphNumber = tabNumber;
    }
    
    /** Set the focused tab unless it already has focus.
     * @param tabNumber
     */
    public void graphTabFocused(int tabNumber) {
		if (trace.getDebugCode("mg")) {
			trace.out("mg", "DockManager (graphTabFocused): focused tabNumber = " + tabNumber);
		}
		if(tabNumber != this.currentGraphNumber) {
			getServer().getTabManager().setFocusedTabByNumber(tabNumber, true);
		}
    }
    
    protected int getGraphEditorId() {
    	return GRAPH_EDITOR;
    }

    

	// /**
	// * RedrawRootWindow
	// * @author Collin Lynch
	// *
	// * Force a repaint of the root window in order to
	// * ensure a complete update.
	// *
	// * NOTE:: This has been added in that it might be
	// * needed for proper edge updates of the LinkInspectorManager
	// * in short it appears as if the windowing system does not
	// * always trigger a correct redraw of the views unless a
	// * dock item changes such as a focus change between windows
	// * this will sometimes work to that end.
	// *
	// * For the present it is unused and so commented out.
	// */
	// public void repaintRootWindow() {
	// System.out.println("RepaintingRootWindow");
	// this.rootWindow.repaint();
	// }
}
