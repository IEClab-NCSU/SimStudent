package edu.cmu.pact.ctatview;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.FactoryConfigurationError;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;
import pact.CommWidgets.RemoteToolProxy;
import pact.CommWidgets.StudentInterfaceConnectionStatus;
import pact.CommWidgets.TutorWindow;
import pact.CommWidgets.UniversalToolProxy;
import apple.dts.samplecode.osxadapter.OSXAdapter;
import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.hcii.ctat.CTATFileItem;
import edu.cmu.hcii.ctat.CTATMoodleMaker;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.CtatModeEvent.SetSimStudentActivationMenuEvent;
import edu.cmu.pact.BehaviorRecorder.Dialogs.BrdFilter;
import edu.cmu.pact.BehaviorRecorder.Dialogs.CreateLMSFilesDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.CreateProblemsTableDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditComponentStartStateSettingsDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadProductionRulesDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.MergeMassProductionDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.OpenInterfaceDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.RuleNamesDisplayDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.SaveFileDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.SkillMatrixDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.SuiFilter;
import edu.cmu.pact.BehaviorRecorder.Dialogs.UnmatchedSelectionsDialog;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.HintPolicyEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.BehaviorRecorder.View.JUndo.JAbstractUndoPacket;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.BehaviorRecorder.View.VariableViewer.VariableViewer;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Log.LogConsole;
import edu.cmu.pact.Preferences.PreferencesModel;
import edu.cmu.pact.Preferences.PreferencesWindow;
import edu.cmu.pact.Utilities.LaunchCTATWebsite;
import edu.cmu.pact.Utilities.LaunchHelp;
import edu.cmu.pact.Utilities.PersonnelInfo;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.model.CtatModeModel;
import edu.cmu.pact.ctat.model.StartStateModel;
import edu.cmu.pact.ctat.view.CtatFrame;
import edu.cmu.pact.jess.WMEEditor;

/**
 * This class creates the menus for the Behavior Recorder window 
 * and provides menu handles for those menus.
 */
public class CtatMenuBar extends CTATBase implements ActionListener, ProblemModelListener,
		WindowListener, ChangeListener, StartStateModel.Listener 
{
	/** Array index of enabled value in {@link MenuItem#makeValues()}. */
	public static final int MENU_ITEM_ENABLED = 0;
	
	/** Array index of check box state value in {@link CheckBoxMenuItem#makeValues()}. */
	public static final int CHECK_BOX_MENU_ITEM_STATE = 1;
	
	/**
	 * Records values of {@link JMenuItem#getEnabled()} in a map to permit us to 
	 * restore the values as the DockManager focus switches among different tabs.
	 */
	public class MenuItem extends JMenuItem {

		/** For compiler warning. */
		private static final long serialVersionUID = 201405152100L;

		/**
		 * Call {@link JMenuItem#JMenuItem(Action)} and save new item to tab.
		 * @param action
		 */
		MenuItem(Action action) { 
			super(action);
			storeInTabs();
			if(trace.getDebugCode("menu"))
				trace.out("menu", "new MenuItem after stored in tab: "+this);
		}
		
		/**
		 * Call {@link JMenuItem#JMenuItem(String)} and save new item to tab.
		 * @param name
		 */
		MenuItem(String name) {
			super(name);
			storeInTabs();
			if(trace.getDebugCode("menu"))
				trace.out("menu", "new MenuItem after stored in tab: "+this);
		}

		/**
		 * Dumps values from superclass.
		 * @return "[<i>text</>@<i>hashCode</i>, {disabled|enabled}]"
		 * @see java.awt.Component#toString()
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			sb.append(getText());
			sb.append('@').append(hashCode());
			sb.append(", ").append(super.isEnabled() ? "enabled" : "disabled");
			return sb.append("]").toString();
		}

		/**
		 * Like {@link #toString()}, but dumps values from argument
		 * @param values
		 * @return "[<i>text</>@<i>hashCode</i>, {disabled|enabled}]"
		 */
		public String toString(boolean[] values) {
			StringBuilder sb = new StringBuilder("[");
			sb.append(getText());
			sb.append('@').append(hashCode());
			sb.append(", ").append(values[MENU_ITEM_ENABLED] ? "enabled" : "disabled");
			return sb.append("]").toString();
		}
		
		/** Add a new item to the saved set. Call only from constructors. */
		private void storeInTabs() {
			boolean[] values = makeValues();
			server.getTabManager().addCtatMenuItem(this, values);
		}
		
		/** Create an array to store the values of interest. */
		private boolean[] makeValues() {
			return new boolean[] {
					super.isEnabled(),   // MENU_ITEM_ENABLED = 0
					false                // unused
			};			
		}

		/**
		 * Save the given enabled status in the tab currently in focus and, via
		 * {@link JMenuItem#setEnabled(boolean)}, in the component itself.
		 * @param enabled status to set
		 * @see javax.swing.JMenuItem#setEnabled(boolean)
		 */
		public void setEnabled(boolean enabled) {
			boolean[] values = getValues();
			super.setEnabled(enabled);
			if(trace.getDebugCode("menu"))
				trace.printStack("menu", "CMB.MenuItem[tab #"+
						server.getTabManager().getFocusedTab().getTabNumber()+
						"].setEnabled("+enabled+") saved values were "+toString(values)+
						", state now "+this);
			values[MENU_ITEM_ENABLED] = enabled;
			saveValues(values);
		}
		
		/**
		 * @return values saved in tab for this menu item
		 */
		private boolean[] getValues() {
			boolean[] values = server.getTabManager().getFocusedTab().getCtatMenuItemValues(this);
			if(values == null) {
				values = makeValues();
				if(trace.getDebugCode("menu"))
					trace.out("menu", "CtatMenuBar.MenuItem.getValues() no entry for menu item "+this+
						"; creating one with value "+values[MENU_ITEM_ENABLED]);
				saveValues(values);
			}
			return values;
		}
		
		/**
		 * @param values values to save in tab for this menu item
		 */
		private void saveValues(boolean[] values) {
			server.getTabManager().getFocusedTab().setCtatMenuItemValues(this, values);			
		}

		/**
		 * Call {@link JMenuItem#setEnabled(boolean)} with value from given array.
		 * @param values array created by {@link #makeValues()}
		 */
		public void setValues(boolean[] values) {
			super.setEnabled(values[MENU_ITEM_ENABLED]);
			if(trace.getDebugCode("menu"))
				trace.printStack("menu", "MenuItem[tab #"+
						server.getTabManager().getFocusedTab().getTabNumber()+
						"].setValues("+toString(values)+") state now "+this);
		}
	}
	
	/**
	 * Records values of {@link JCheckBoxMenuItem#isEnabled()} and 
	 * {@link JCheckBoxMenuItem#isSelected()} in a map to permit us to
	 * restore the values as the DockManager focus switches among different tabs.
	 */
	public class CheckBoxMenuItem extends JCheckBoxMenuItem {

		/** For compiler warning. */
		private static final long serialVersionUID = 201405152100L;

		/**
		 * Call {@link JCheckBoxMenuItem#JCheckBoxMenuItem(String)} and save new item to tab.
		 * @param text
		 * @param b 
		 */
		CheckBoxMenuItem(String text, boolean state) {
			super(text, state);
			storeInTabs();
			if(trace.getDebugCode("menu"))
				trace.out("menu", "new CheckBoxMenuItem after stored in tab: "+this);
		}

		/**
		 * Dumps values from superclass.
		 * @return "[<i>text</>@<i>hashCode</i>, {disabled|enabled}, {checked|unchecked}]"
		 * @see java.awt.Component#toString()
		 */
		public String toString() {
			StringBuilder sb = new StringBuilder("[");
			sb.append(getText());
			sb.append('@').append(hashCode());
			sb.append(", ").append(super.isEnabled() ? "enabled" : "disabled");
			sb.append(", ").append(super.isSelected() ? "checked" : "unchecked");
			return sb.append("]").toString();
		}

		/**
		 * Like {@link #toString()}, but dumps values from argument
		 * @param values
		 * @return "[<i>text</>@<i>hashCode</i>, {disabled|enabled}, {checked|unchecked}]"
		 */
		public String toString(boolean[] values) {
			StringBuilder sb = new StringBuilder("[");
			sb.append(getText());
			sb.append('@').append(hashCode());
			sb.append(", ").append(values[MENU_ITEM_ENABLED] ? "enabled" : "disabled");
			sb.append(", ").append(values[CHECK_BOX_MENU_ITEM_STATE] ? "checked" : "unchecked");
			return sb.append("]").toString();
		}
		
		/** Add a new item to the saved set. Call only from constructors. */
		private void storeInTabs() {
			boolean[] values = makeValues();
			server.getTabManager().addCtatMenuItem(this, values);
		}
		
		/** Create an array to store the values of interest. */
		private boolean[] makeValues() {
			return new boolean[] {
					super.isEnabled(),   // MENU_ITEM_ENABLED = 0
					super.getState()     // CHECK_BOX_MENU_ITEM_STATE = 1
			};			
		}

		/**
		 * Save the given enabled status in the tab currently in focus and, via
		 * {@link JCheckBoxMenuItem#setEnabled(boolean)}, in the component itself.
		 * @param enabled status to set
		 * @see javax.swing.JMenuItem#setEnabled(boolean)
		 */
		public void setEnabled(boolean enabled) {
			boolean[] values = getValues();
			values[MENU_ITEM_ENABLED] = enabled;
			super.setEnabled(enabled);
			saveValues(values);
		}

		/**
		 * Save the given state in the tab currently in focus and, via
		 * {@link JCheckBoxMenuItem#setSelected(boolean)}, in the component itself.
		 * @param state state to set
		 * @see javax.swing.JCheckBoxMenuItem#setSelected(boolean)
		 */
		public void setSelected(boolean state) {
			boolean[] values = getValues();
			values[CHECK_BOX_MENU_ITEM_STATE] = state;
			super.setSelected(state);
			saveValues(values);
		}
		
		/**
		 * @return values saved in tab for this menu item
		 */
		private boolean[] getValues() {
			boolean[] values = server.getTabManager().getFocusedTab().getCtatMenuItemValues(this);
			if(values == null) {
				values = makeValues();
				if(trace.getDebugCode("menu"))
					trace.out("menu", "CtatMenuBar.CheckBoxMenuItem.getValues() no entry for menu item "+this+
						"; creating one with values "+values[MENU_ITEM_ENABLED]+","+values[CHECK_BOX_MENU_ITEM_STATE]);
				saveValues(values);
			}
			return values;
		}
		
		/**
		 * @param values values to save in tab for this menu item
		 */
		private void saveValues(boolean[] values) {
			server.getTabManager().getFocusedTab().setCtatMenuItemValues(this, values);			
		}

		/**
		 * Call {@link JMenuItem#setEnabled(boolean)}, {@link JMenuItem#setSelected(boolean)}
		 * with values from given array.
		 * @param values array created by {@link #makeValues()}
		 */
		public void setValues(boolean[] values) {
			super.setEnabled(values[MENU_ITEM_ENABLED]);
			super.setSelected(values[CHECK_BOX_MENU_ITEM_STATE]);
			if(trace.getDebugCode("menu"))
				trace.printStack("menu", "CheckBoxMenuItem.setValues("+toString(values)+") state now "+this);
		}
	}
	
    public static final String RETRACT_LAST_STEP = "Retract Last Step";   
    private static final String NEXT_PREFERRED_STEP = "Next Preferred Step";    
    private static final String PREVIOUS_PREFERRED_STATE = "Previous Preferred State";    
    public static final Font defaultFont = new Font("", Font.BOLD | Font.ITALIC, 14);
    
    private JMenu file;    
    private JMenu edit;    
    private JMenu viewMenu;
    private JMenu graphMenu;
    private JMenu cognitiveModelMenu;    
    private JMenu simStMenu;
    private JMenu toolsMenu;
    private JMenu windowsMenu;
    private JMenu helpMenu;

    private JMenuItem openInterfaceMenu;
    //private JMenuItem saveAsBrdTemplate; // unused
    //private JMenuItem openBrdTemplate; // unused
    private JMenuItem createProblemsTableMenu;
    private JMenuItem mergeProblemsMenu;

    // end zz

    private JMenuItem newGraphMenu;
    private JMenuItem saveGraphMenu;
    private MenuItem saveGraphAsMenu;
    private JMenuItem openGraphMenu;
    private JMenuItem closeGraphMenu;
    private JMenu openRecentGraphMenu;

	private JMenuItem saveStudentInterfaceMenu;
    
    //    private JMenuItem openJessFileMenu;
    
    private JMenuItem saveJessFactsMenu;
    private JMenuItem saveJessTemplatesMenu;
    private JMenuItem loadRuleNamesMenu;
    private JMenuItem quitMenu;
    private JMenuItem undoMenu;
    private JMenuItem redoMenu;
    private JMenuItem copyLinksMenu;
    private JMenuItem pasteLinksMenu;
    private JMenuItem preferencesMenu;
    private JMenuItem collaborationMenu;
    private JMenuItem logConsoleMenu;
    //private JMenuItem editGroupMenu; // unused
    private JMenuItem nextModeMenu;
    //private JMenuItem deleteStateMenu; // unused
    private JMenuItem cleanupStartStateMenu;
    private JMenuItem showUnmatchedComponentsMenu;
    private JMenuItem createStartStateMenu;
       

    // private JMenuItem loadMessageStreamFilesMenu;

    // private JMenuItem groupManagerMenu;

    private JMenuItem goToStartStateMenu;
    private JMenuItem startStateSettingsMenu;
    private JMenuItem retractOneStepMenu;
    
    //  private JMenuItem previousPreferredState;
    
    private JMenuItem nextPreferredStepMenu;
    private JMenuItem setWMMenu;
    private JMenuItem checkAllActionsMenu;
    private JMenuItem resetLinkColorsMenu;
    private JMenuItem activateMissMenu;   
    private JMenuItem loadProdRulesMenu;    
    private JMenuItem setBreakpointsMenu;
    private JMenuItem clearBreakpointsMenu;
    private JMenuItem resumeBreakpointsMenu;

    private final String SIM_ST_ACTIVATE_MENU = "Activate Sim. Student";
    private final String SIM_ST_DEACTIVATE_MENU = "Deactivate Sim. Student";

    private JMenuItem ruleNamesWindowMenu;    
    //private JMenuItem solutionStateWindowMenu; // unused
    private JMenuItem authoringToolsHelpMenu;
    private JMenuItem moodleMaker;
    private JMenuItem createLMSFilesMenu;    
    private JMenuItem showWebPageMenuItem;    
    private JMenuItem showLicenseMenuItem;
    private JMenuItem skillMatrixMenu;
    private JMenuItem aboutMenu;
    private JMenuItem printGraphMenu;    
    private JMenuItem newProblemMenu;    
    private JMenuItem saveInstructionsMenu;
    private JMenuItem loadInstructionsMenu;    
    private JMenuItem loadWmeTypesMenu;    
    private JMenuItem initializeWmesMenu;
    private JMenuItem loadFeaturePredicatesMenu;
    private JMenuItem loadOperatorsMenu;
    private JMenuItem testModelMenu;      
    
    private JMenuItem outOfOrderMessageGraphMenu; 
    
    private JMenu hintPolicyGraphMenu;
    private JMenu suppressFeedbackGraphMenu; 
    
    private JCheckBoxMenuItem actionLabelsMenu;
    private JCheckBoxMenuItem lastCheckLISPLabelsMenu;
    private CheckBoxMenuItem ruleLabelsMenu;    
    //private JCheckBoxMenuItem showCallbackFnMenu; // unused    
    private JCheckBoxMenuItem caseSensitiveGraphMenu;
    private JCheckBoxMenuItem lockWidgetsGraphMenu;
    
    private JCheckBoxMenuItem confirmDoneGraphMenu;
    private JCheckBoxMenuItem highlightRightSelectionGraphMenu;
    
    
    private int dynamicViewCount = 0;
    private boolean dynamicViewHasClosed = false;
    
    public static final String ECLIPSE_LISTENING_PORT = "Eclipse Plugin Listening Port";
    

    //private BR_Controller controller;

    //private SkillMatrixDialog skillMatrixDialog; // unused?

    private JMenuBar mbar;

    private JMenu viewPanelMenu;

    /** Link to the help window */
    private LaunchHelp launchHelp;
    
    /** keeping track of dynamic graph windows */
    private Map<JMenuItem, DockingWindow> dynamicWindowMap;
	private static final String DYNAMIC_WINDOW_COMMAND = "Dynamic Window View";

    private int maxRecentFiles=15;
    private ArrayList <CTATFileItem> recentFiles=new ArrayList<CTATFileItem> ();
    private final CTAT_Launcher server;

    /**
     * 
     */
    
    public CtatMenuBar(CTAT_Launcher server) 
    {
    	setClassName ("CtatMenuBar");
    	debug ("CtatMenuBar ()");
    	this.server = server;
        createMenus();
    }


    /** Enables or disables the "Save Jess Facts" and "Save Jess Templates" menu items */
    public void enableJessMenus (boolean enabled) 
    {
    	if (!VersionInformation.includesJess())
    		return;
    	
    	// saveJessFactsMenu.setVisible(enabled);
    	saveJessFactsMenu.setEnabled(enabled);
	
    	// saveJessTemplatesMenu.setVisible(enabled);
    	saveJessTemplatesMenu.setEnabled(enabled);
    	loadProdRulesMenu.setEnabled(enabled);
    	//	createMenus();

    	if (enabled) 
    	{
    		// file.add(saveJessFactsMenu);
    		saveJessFactsMenu.addActionListener(this);
    		// file.add(saveJessTemplatesMenu);
    		saveJessTemplatesMenu.addActionListener(this);
    		loadProdRulesMenu.addActionListener(this);
    		// createMenus();	    
    	}
    	else 
    	{
    		// file.remove(saveJessFactsMenu);
    		saveJessFactsMenu.removeActionListener(this);
    		// file.remove(saveJessTemplatesMenu);
    		saveJessTemplatesMenu.removeActionListener(this);
    		loadProdRulesMenu.removeActionListener(this);
    		// createMenus();
    	}
    	
    	file.repaint();
    	file.updateUI();
    }
    /**
     * 
     */
    public void enableInterfaceMenus(boolean enabled) 
    {
    	enableOpenInterfaceMenu(new Boolean(enabled));
        //newGraphMenu.setEnabled(enabled);
    	newGraphMenu.setEnabled(true); // should this be done this way?
        saveGraphMenu.setEnabled(enabled);
        saveGraphAsMenu.setEnabled(enabled);
        // openGraphMenu.setEnabled(enabled);
        openGraphMenu.setEnabled(true); // should this be done this way?
        closeGraphMenu.setEnabled(enabled);
        // Sensitive to Preferences Model. Added by Kim K.C. 04/25/05
        // Boolean eclipsePlugin = getServer().getPreferencesModel()
        // .getBooleanValue(AbstractCtatWindow.DEFAULT_JESS_EDITOR);
        // if (eclipsePlugin != null && eclipsePlugin.booleanValue())
        //    openJessFileMenu.setEnabled(true);
        // else
        // openJessFileMenu.setEnabled(false);
        
        loadRuleNamesMenu.setEnabled(enabled);
       // deleteStateMenu.setEnabled(enabled);
        cleanupStartStateMenu.setEnabled(graphHasCleanUpToDo());
        updateUnmatchedComponents();
//        createStartStateMenu.setEnabled(enabled);  FIXME
        goToStartStateMenu.setEnabled(enabled);
        enableMenuIfInterfaceConnected(startStateSettingsMenu);
        retractOneStepMenu.setEnabled(enabled);
        // previousPreferredState.setEnabled(enabled);
        nextPreferredStepMenu.setEnabled(enabled);
        // loadMessageStreamFilesMenu.setEnabled(enabled);
        actionLabelsMenu.setEnabled(enabled);
        lastCheckLISPLabelsMenu.setEnabled(enabled);
        ruleLabelsMenu.setEnabled(enabled);
        //showCallbackFnMenu.setEnabled(enabled);
        setWMMenu.setEnabled(enabled);
        checkAllActionsMenu.setEnabled(enabled);
        resetLinkColorsMenu.setEnabled(enabled);
        ruleNamesWindowMenu.setEnabled(enabled);
    }
    /**
     * 
     */
	public JMenuBar getMenuBar() 
	{
        return mbar;
    }
    /**
     * 
     * @param enabled
     */
    public void enablePrintGraphMenus(boolean enabled) 
    {
        if(trace.getDebugCode("menu"))
        	trace.out("menu", "CMB.enablePrintGraphMenus("+enabled+")");
    	printGraphMenu.setEnabled(enabled);
    }
    /**
     * Enable or disable together those menu items useful only when a graph is loaded.
     * @param enabled
     */
    public void enableSaveGraphMenus(boolean enabled) 
    {
        if(trace.getDebugCode("menu"))
        	trace.out("menu", "CMB.enableSaveGraphMenus("+enabled+")");
    	saveGraphMenu.setEnabled(enabled);
    	saveGraphAsMenu.setEnabled(enabled);
    	cleanupStartStateMenu.setEnabled(graphHasCleanUpToDo());
        updateUnmatchedComponents();
    	copyLinksMenu.setEnabled(hasLinks());
    	pasteLinksMenu.setEnabled(getController().getProblemModelManager().nSelectedLinks() > 0);
    }

    /**
     * Prompt the menu to update the enabled/disabled status of
     * {@link #showUnmatchedComponentsMenuActionPerformed()}.
     * @param enable
     */
    public void enableFindObsoleteComponents(boolean enable) {
    	showUnmatchedComponentsMenu.setEnabled(graphHasObsoleteComponents());
    }

	/**
     * Tell whether the {@value ObsoleteComponentDialog#SHOW_UNMATCHED_COMPONENT_REFERENCES}
     * dialog can be invoked. 
     * @return true if the dialog can be launched
     */
    public boolean graphHasObsoleteComponents() {
		UniversalToolProxy utp = getController().getUniversalToolProxy();
		if(utp == null)
			return false;
		return utp.enableObsoleteSeletionDialog();
	}

	/**
     * Tell whether the currently loaded graph has data that {@link #cleanupStartStateMenu} 
     * could remove.
     * @return true if there is data to remove
     */
    private boolean graphHasCleanUpToDo() {
    	if(trace.getDebugCode("startstate"))
    		trace.out("startstate", "CtatMenuBar.graphHasCleanUpToDo()");
    	
    	UniversalToolProxy utp = null;
    	if(getController() == null || (utp = getController().getUniversalToolProxy()) == null) {
    		trace.err("CtatMenuBar.graphHasCleanUpToDo(): null getController() "+
    				getController()+" or .getUniversalToolProxy() "+utp);
    		return false;
    	}
    	int nToDelete = utp.getStartStateModel().pruneInterfaceDescriptions(getController().getProblemModel(), false);
		return (nToDelete > 0);
	}


	/**
     * 
     * @param enabled
     */
    public void enableMassProductionMenus(boolean enabled) 
    {
    	//mergeProblemsMenu.setEnabled(enabled);
    } 
    /**
     * 
     * @param enabled
     */
    public void enableCreateStartStateMenus(boolean enabled) 
    {
        if(trace.getDebugCode("menu"))
        	trace.out("menu", "CMB.enableCreateStartStateMenus("+enabled+")");
    	createStartStateMenu.setEnabled(enabled);
    	enableMenuIfInterfaceConnected(startStateSettingsMenu);
    	updateUnmatchedComponents();
    }

	/**
     * 
     * @param enabled
     */
    public void enableGotoStartStateMenus(boolean enabled) 
    {
        if(trace.getDebugCode("menu"))
        	trace.out("menu", "CMB.enableGotoStartStateMenus("+enabled+")");
    	goToStartStateMenu.setEnabled(enabled);
    	enableMenuIfInterfaceConnected(startStateSettingsMenu);
    	updateUnmatchedComponents();
    	retractOneStepMenu.setEnabled(enabled);
    	// previousPreferredState.setEnabled(enabled);
    	nextPreferredStepMenu.setEnabled(enabled);
    }

    /**
     * Enable or disable a menu item according to {@link UniversalToolProxy#getStudentInterfaceConnectionStatus()}:
     * enable if the status is not {@value StudentInterfaceConnectionStatus#Disconnected}.
     * @param menuItem
     * @return updated state of argument's {@link JMenuItem#isEnabled() menuItem.isEnabled()}
     */
    private boolean enableMenuIfInterfaceConnected(JMenuItem menuItem) {
    	boolean previous = menuItem.isEnabled();
    	UniversalToolProxy utp = getController().getUniversalToolProxy();
    	boolean enable = (utp != null && utp.hasInterfaceDescriptions());
    	menuItem.setEnabled(enable);
    	if(trace.getDebugCode("menu"))
    		trace.printStack("menu", "changed state of menu item \""+menuItem.getText()+"\" from "+
    				(previous  ? "enabled" : "disabled")+" to "+(enable ? "enabled" : "disabled"));
    	return enable;
	}
    
    ///////////////////////////////////////////////////////
    /**
     * Create Menus
     * 
     */
    ///////////////////////////////////////////////////////
    public JMenuBar createMenus() 
    {
    	boolean notReducedToolMode = !this.server.isReducedMode();
	
        mbar = new JMenuBar();
        
        int ctrlKeyMask;
        
        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
            ctrlKeyMask = ActionEvent.META_MASK;
        else
            ctrlKeyMask = ActionEvent.CTRL_MASK;
	
        /*/////////////// MENUS //////////////*/
        
        file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        mbar.add(file);
        
        fillFileMenu (file,ctrlKeyMask,notReducedToolMode);
        
        //>-----------------------------------------------------------        
        
        edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        mbar.add(edit);
        
        //>-----------------------------------------------------------        
        
        viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        mbar.add(viewMenu);
        
        fillViewMenu (viewMenu);
        
        //>-----------------------------------------------------------
	
        graphMenu = new JMenu("Graph");
        
        if (notReducedToolMode) 
        {
        	graphMenu.setMnemonic(KeyEvent.VK_G);
        	mbar.add(graphMenu);
        }
        
        //>-----------------------------------------------------------
        
        cognitiveModelMenu = new JMenu("Cognitive Model");
        
        if (notReducedToolMode && VersionInformation.includesJess()) 
        {
        	cognitiveModelMenu.setMnemonic(KeyEvent.VK_C);
        	mbar.add(cognitiveModelMenu);
        }
        
        //>-----------------------------------------------------------
        
        simStMenu = new JMenu("Sim. Student");
        
        if (notReducedToolMode && VersionInformation.isRunningSimSt()) 
        {
        	simStMenu.setMnemonic(KeyEvent.VK_S);
        	mbar.add(simStMenu);
        }
        
        //>-----------------------------------------------------------
        
        toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        mbar.add(toolsMenu);
        
        //>-----------------------------------------------------------        

        windowsMenu = new JMenu("Window");
        windowsMenu.setMnemonic(KeyEvent.VK_W);
        mbar.add(windowsMenu);
        
        //>-----------------------------------------------------------

        helpMenu = new JMenu("Help");
        
        if (notReducedToolMode) 
        {
        	helpMenu.setMnemonic(KeyEvent.VK_H);
        	mbar.add(helpMenu);
        	trace.out("br", "Create help menu");
        }
        	
        /*/////////////////// MENU ITEMS //////////////////////*/
	

        /********************* UNDO TEST 1337 ***************************/

        final BR_Controller controller = getController();
        final ProblemModel model = controller.getProblemModel();
        JAbstractUndoPacket undoPacket = controller.getUndoPacket();
        
        undoMenu = new MenuItem(undoPacket.getUndoAction());
        undoMenu.setName("CtatMenuBar:Edit:Undo");
        edit.add(undoMenu);
		
        redoMenu = new MenuItem(undoPacket.getRedoAction());
        redoMenu.setName("CtatMenuBar:Edit:Redo");
        edit.add(redoMenu);
        
        copyLinksMenu = new MenuItem("Copy Links");
        copyLinksMenu.setName("CtatMenuBar:Edit:CopyLinks");
		copyLinksMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(trace.getDebugCode("mg"))
        			trace.out("mg", "CtatMenuBar (copyLinksMenu.actionPerformed): HERE");
        		if(controller.copySelectedLinks() > 0)
        			pasteLinksMenu.setEnabled(true);
        	}
        });
        copyLinksMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ctrlKeyMask));
        edit.add(copyLinksMenu);
        copyLinksMenu.setEnabled(false);
        
        pasteLinksMenu = new MenuItem("Paste Links");
        pasteLinksMenu.setName("CtatMenuBar:Edit:PasteLinks");
		pasteLinksMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		if(trace.getDebugCode("mg"))
        			trace.out("mg", "CtatMenuBar (pasteLinksMenu.actionPerformed): HERE");
        		controller.pasteLinks();
        		copyLinksMenu.setEnabled(hasLinks());
        		//Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, "Paste Links");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
        });
        pasteLinksMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ctrlKeyMask));
        edit.add(pasteLinksMenu);
        pasteLinksMenu.setEnabled(false);
        
        /****************************************************************/
			
        boolean onMacOS = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
        
        if (onMacOS) 
        { 
        	// CTAT2883: Cmd-Q handler for Mac
        	
        	try 
        	{
        		OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quitHandlerForMac", new Class[] {}));
        	} 
        	catch (Exception e) 
        	{
        		trace.err("Error setting quit handler; os.name "+System.getProperty("os.name")+", onMacOS "+onMacOS);
        		e.printStackTrace();
        	}
        }

        // ////////////////////////////////////////////////////////////

        createStartStateMenu = new MenuItem("Create Start State");
        createStartStateMenu.setMnemonic(KeyEvent.VK_C);
        createStartStateMenu.setAccelerator(KeyStroke.getKeyStroke(
								   KeyEvent.VK_B, ctrlKeyMask));
        graphMenu.add(createStartStateMenu);
        createStartStateMenu.addActionListener(this);

        /*  
        deleteStateMenu = new MenuItem("Delete Current State");
        deleteStateMenu.setMnemonic(KeyEvent.VK_D);
        graphMenu.add(deleteStateMenu);
        deleteStateMenu.addActionListener(this);
        */
        
        goToStartStateMenu = new MenuItem("Jump to Start State");
        goToStartStateMenu.setMnemonic(KeyEvent.VK_J);
        graphMenu.add(goToStartStateMenu);
        goToStartStateMenu.addActionListener(this);
        goToStartStateMenu.setEnabled(false);
        goToStartStateMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ctrlKeyMask));

        retractOneStepMenu = new MenuItem(RETRACT_LAST_STEP);
        retractOneStepMenu.setMnemonic(KeyEvent.VK_R);
        graphMenu.add(retractOneStepMenu);
        retractOneStepMenu.addActionListener(this);
        retractOneStepMenu.setEnabled(false);
        retractOneStepMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));

        // previousPreferredStatsds = new MenuItem(PREVIOUS_PREFERRED_STATE);
        // previousPreferredState.setMnemonic(KeyEvent.VK_P);
        // graphMenu.add(previousPreferredState);
        // previousPreferredState.addActionListener(this);
        // previousPreferredState.setEnabled(false);
        // previousPreferredState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP,ActionEvent.ALT_MASK));

        nextPreferredStepMenu = new MenuItem(NEXT_PREFERRED_STEP);
        nextPreferredStepMenu.setMnemonic(KeyEvent.VK_N);
        graphMenu.add(nextPreferredStepMenu);
        nextPreferredStepMenu.addActionListener(this);
        nextPreferredStepMenu.setEnabled(false);
        nextPreferredStepMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,ActionEvent.ALT_MASK));

        // graphMenu.add (loadMessageStreamFilesMenu);
        // loadMessageStreamFilesMenu.addActionListener(this);

        graphMenu.addSeparator();

        caseSensitiveGraphMenu = new CheckBoxMenuItem("Case Sensitive Exact Matching", currentCaseSensitivityProperty());
        caseSensitiveGraphMenu.setMnemonic(KeyEvent.VK_M);
        graphMenu.add(caseSensitiveGraphMenu);
        caseSensitiveGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		model.setCaseInsensitive(!model.isCaseInsensitive());
        		//Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, "Set Case "+ (model.isCaseInsensitive() ? "Ins" : "S")+"ensitive Matching");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
        });

        lockWidgetsGraphMenu = new CheckBoxMenuItem("Lock Widgets on Correct Action", currentLockWidgetProperty());
        lockWidgetsGraphMenu.setMnemonic(KeyEvent.VK_W);
        graphMenu.add(lockWidgetsGraphMenu);
        lockWidgetsGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		model.setLockWidget(!model.isLockWidget());
        		//Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, (model.isLockWidget() ? "Lock" : "Unlock")+ " Widgets on Correct Action");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
	    });

        suppressFeedbackGraphMenu = new JMenu("Feedback Policy");
        FeedbackEnum currentFe = currentSuppressFeedbackProperty();
        
        for (FeedbackEnum fe : FeedbackEnum.values()) 
        {
        	JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(fe.toString(), fe.equals(currentFe));
        	
        	cbmi.addActionListener(new ActionListener() 
        	{
        		public void actionPerformed(ActionEvent e) 
        		{
        			if (trace.getDebugCode("pr"))
        				trace.out("pr", "suppressFeedback actionPerformed("+e+")");
        			
        			FeedbackEnum oldSetting = model.getSuppressStudentFeedback();
        			FeedbackEnum choice =
        				FeedbackEnum.fromString(e.getActionCommand());
        			model.setSuppressStudentFeedback(choice);
        			
        			for (Component c: suppressFeedbackGraphMenu.getMenuComponents()) 
        			{
        	        	JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
        				cbmi.setSelected(cbmi.getText().equals(e.getActionCommand()));
        			}
        			
            		//Undo checkpoint
        			if (oldSetting == null || !oldSetting.equals(choice)) 
        			{
        				ActionEvent ae = new ActionEvent(this, 0, "Set Feedback Policy "+choice.toString());
        				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        			}
        		}
        	});
        	
        	suppressFeedbackGraphMenu.add(cbmi);
        }
        
        suppressFeedbackGraphMenu.setMnemonic(KeyEvent.VK_F);
        graphMenu.add(suppressFeedbackGraphMenu);
        
        hintPolicyGraphMenu = new JMenu("Hint Policy");
        HintPolicyEnum currentHbe = currentHintBiasProperty();
        
        for (HintPolicyEnum hbe : HintPolicyEnum.values()) 
        {
        	JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(hbe.toString(), hbe.equals(currentHbe));
        	cbmi.addActionListener(new ActionListener() 
        	{
        		public void actionPerformed(ActionEvent e) 
        		{
        			if (trace.getDebugCode("pr"))
        				trace.out("pr", "changeHintBiasPolicy actionPerformed("+e+")");
        			
        			HintPolicyEnum oldSetting = model.getHintPolicy();
        			HintPolicyEnum choice = HintPolicyEnum.fromString(e.getActionCommand());
        			model.setHintPolicy(choice);
        			
        			for (Component c: hintPolicyGraphMenu.getMenuComponents()) 
        			{
        	        	JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
        				cbmi.setSelected(cbmi.getText().equals(e.getActionCommand()));
        			}
        			
            		//Undo checkpoint
        			if (oldSetting == null || !oldSetting.equals(choice)) 
        			{
        				ActionEvent ae = new ActionEvent(this, 0, "Set Hint Policy "+choice.toString());
        				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        			}
        		}
        	});
        	
        	hintPolicyGraphMenu.add(cbmi);
        }
        
        hintPolicyGraphMenu.setMnemonic(KeyEvent.VK_P);
        graphMenu.add(hintPolicyGraphMenu);
        
        confirmDoneGraphMenu = new CheckBoxMenuItem("Confirm Done",getController().getProblemModel().getEffectiveConfirmDone());
        confirmDoneGraphMenu.setMnemonic(KeyEvent.VK_D);
        graphMenu.add(confirmDoneGraphMenu);
        
        confirmDoneGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
    		    Boolean oldSetting = model.getConfirmDone();
    		    Boolean newSetting = new Boolean(confirmDoneGraphMenu.isSelected());
        		model.setConfirmDone(newSetting);
        		
        		//Undo checkpoint
        		if (oldSetting == null || !oldSetting.equals(newSetting)) 
        		{
        			ActionEvent ae = new ActionEvent(this, 0, "Set Confirm Done "+model.getConfirmDone());
        			controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        		}
        	}
	    });

        highlightRightSelectionGraphMenu = new CheckBoxMenuItem("Highlight Right Selection", currentHighlightRightSelectionProperty());
        highlightRightSelectionGraphMenu.setMnemonic(KeyEvent.VK_H);
        graphMenu.add(highlightRightSelectionGraphMenu);
        
        highlightRightSelectionGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		model.setHighlightRightSelection(!model.getHighlightRightSelection());
        		// Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, "Set Highlight Right Selection "+model.getHighlightRightSelection());
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
	    });

        outOfOrderMessageGraphMenu = new MenuItem("Edit Out of Order Message...");
        outOfOrderMessageGraphMenu.setMnemonic(KeyEvent.VK_O);
        graphMenu.add(outOfOrderMessageGraphMenu);
        outOfOrderMessageGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		doOutOfOrderMessageDialog();
        	}
	    });
        
        graphMenu.addSeparator();
        
        cleanupStartStateMenu = new MenuItem("Clean Up Graph...");
        cleanupStartStateMenu.setMnemonic(KeyEvent.VK_S);
        graphMenu.add(cleanupStartStateMenu);
        cleanupStartStateMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		try 
        		{
        			cleanupStartStateMenuActionPerformed();
        		} 
        		catch (Exception err) 
        		{
        			trace.errStack("error on cleanupStartStateMenu", err);
        		}
        	}
	    });
        cleanupStartStateMenu.setEnabled(false);        
        
        showUnmatchedComponentsMenu = new MenuItem(UnmatchedSelectionsDialog.SHOW_UNMATCHED_COMPONENT_REFERENCES+"...");
        showUnmatchedComponentsMenu.setMnemonic(KeyEvent.VK_S);
        graphMenu.add(showUnmatchedComponentsMenu);
        showUnmatchedComponentsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		try 
        		{
        			showUnmatchedComponentsMenuActionPerformed();
        		} 
        		catch (Exception err) 
        		{
        			trace.errStack("error on showUnmatchedComponentsMenu", err);
        		}
        	}
	    });
        showUnmatchedComponentsMenu.setEnabled(false);        

        // preferredPathOnlyMenu = new CheckBoxMenuItem("Preferred Path Only",
        // ese_Frame.preferredPathOnlyFlag);
        // displayMenu.add(preferredPathOnlyMenu);
        // preferredPathOnlyMenu.addActionListener(this);

        // ///////////////////////////////////////////////////////////

        setWMMenu = new MenuItem("Set WM to Current State");
        // removed -- see bug 419
        // prodSystemMenu.add(setWMMenu);
        setWMMenu.addActionListener(this);

        // checkAllActionsMenu = new MenuItem("Check All Actions");
        checkAllActionsMenu = new MenuItem("Test Cognitive Model on All Steps");
        checkAllActionsMenu.setMnemonic(KeyEvent.VK_T);
        checkAllActionsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ctrlKeyMask));
        cognitiveModelMenu.add(checkAllActionsMenu);
        checkAllActionsMenu.addActionListener(this);

        resetLinkColorsMenu = new MenuItem("Reset Link Colors");
        resetLinkColorsMenu.setMnemonic(KeyEvent.VK_R);
        cognitiveModelMenu.add(resetLinkColorsMenu);
        resetLinkColorsMenu.addActionListener(this);

        loadProdRulesMenu = new MenuItem("Load Production Rules");
        loadProdRulesMenu.setMnemonic(KeyEvent.VK_L);
        loadProdRulesMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L,ctrlKeyMask));
        cognitiveModelMenu.add(loadProdRulesMenu);
        
        // loadProdRulesMenu.addActionListener(this);

        cognitiveModelMenu.addSeparator();

        setBreakpointsMenu = new MenuItem("Set Breakpoints...");
        setBreakpointsMenu.setMnemonic(KeyEvent.VK_B);
        cognitiveModelMenu.add(setBreakpointsMenu);
        setBreakpointsMenu.addActionListener(this);
       
        clearBreakpointsMenu = new MenuItem("Clear Breakpoints");
        clearBreakpointsMenu.setMnemonic(KeyEvent.VK_C);
        cognitiveModelMenu.add(clearBreakpointsMenu);
        clearBreakpointsMenu.addActionListener(this);
       
        resumeBreakpointsMenu = new MenuItem("Resume");
        resumeBreakpointsMenu.setMnemonic(KeyEvent.VK_S);
        cognitiveModelMenu.add(resumeBreakpointsMenu);
        resumeBreakpointsMenu.addActionListener(this);
       
        ////////////////////////////////////////
        
        //simStMenu.
        
        //Save Instructions
        saveInstructionsMenu = new MenuItem("Save Instructions");
        saveInstructionsMenu.addActionListener(this);
        saveInstructionsMenu.setMnemonic(KeyEvent.VK_S);
        simStMenu.add(saveInstructionsMenu);
        saveInstructionsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		saveInstructionsMenuActionPerformed();
        	}
	    });
        
        
        //Load Instructions        
        loadInstructionsMenu = new MenuItem("Load Instructions");
        loadInstructionsMenu.addActionListener(this);
        loadInstructionsMenu.setMnemonic(KeyEvent.VK_L);
        simStMenu.add(loadInstructionsMenu);
        loadInstructionsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		loadInstructionsMenuActionPerformed();
        	}
	    });
        
        simStMenu.addSeparator();
        
        //Load WME Types
        loadWmeTypesMenu = new MenuItem("Load WME Types");
        loadWmeTypesMenu.addActionListener(this);
        loadWmeTypesMenu.setMnemonic(KeyEvent.VK_W);
        simStMenu.add(loadWmeTypesMenu);
        loadWmeTypesMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
                    loadWmeTypesMenuActionPerformed();
        	}
	    });


        //Initialize WMEs
        initializeWmesMenu = new MenuItem("Initialize WMEs");
        initializeWmesMenu.addActionListener(this);
        initializeWmesMenu.setMnemonic(KeyEvent.VK_I);
        simStMenu.add(initializeWmesMenu);
        initializeWmesMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
                    initializeWmesMenuActionPerformed();
        	}
	    });
        
        
        //Load Feature Predicates
        loadFeaturePredicatesMenu = new MenuItem("Load Feature Predicates");
        loadFeaturePredicatesMenu.addActionListener(this);
        loadFeaturePredicatesMenu.setMnemonic(KeyEvent.VK_F);
        simStMenu.add(loadFeaturePredicatesMenu);
        loadFeaturePredicatesMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		loadFeaturePredicatesMenuActionPerformed();
        	}
	    });

        //Load Operators
        loadOperatorsMenu = new MenuItem("Load Operators");
        loadOperatorsMenu.addActionListener(this);
        loadOperatorsMenu.setMnemonic(KeyEvent.VK_O);
        simStMenu.add(loadOperatorsMenu);
        loadOperatorsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		loadOperatorsMenuActionPerformed();
        	}
	    });
                
        simStMenu.addSeparator();
        
        testModelMenu = new MenuItem("Test Current Model on...");
        testModelMenu.addActionListener(this);
        testModelMenu.setMnemonic(KeyEvent.VK_T);
        simStMenu.add(testModelMenu);
        testModelMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		testModelMenuActionPerformed();
        	}
	    });
        
     
        // //////////////////////////////////////////////////////////

        preferencesMenu = new MenuItem("Preferences...");
        preferencesMenu.setMnemonic(KeyEvent.VK_P);
        toolsMenu.add(preferencesMenu);
        preferencesMenu.addActionListener(this);
        
        JMenu massProduction=new JMenu ("Mass Production");        
        toolsMenu.add(massProduction);
        
        fillMassProduction (massProduction,notReducedToolMode);
        
        JMenu advancedMenu = new JMenu("Advanced");
        
        collaborationMenu = new MenuItem("Start Collaboration...");
        collaborationMenu.setMnemonic(KeyEvent.VK_C);
        collaborationMenu.addActionListener(this);
        advancedMenu.add(collaborationMenu);
        collaborationMenu.setEnabled(true);
                        
        logConsoleMenu = new MenuItem("Replay from log...");
        logConsoleMenu.setMnemonic(KeyEvent.VK_R);
        advancedMenu.add(logConsoleMenu);
        logConsoleMenu.addActionListener(this);

        createLMSFilesMenu = new MenuItem("Create LMS Files...");
        advancedMenu.add(createLMSFilesMenu);
        createLMSFilesMenu.addActionListener(this);
        
        moodleMaker = new MenuItem("Generate SCORM Package ...");
        advancedMenu.add(moodleMaker);
        moodleMaker.addActionListener(this);        
        
        startStateSettingsMenu = new MenuItem(EditComponentStartStateSettingsDialog.START_STATE_COMPONENT_SETTINGS+"...");
        startStateSettingsMenu.setMnemonic(KeyEvent.VK_S);
        advancedMenu.add(startStateSettingsMenu);
        startStateSettingsMenu.addActionListener(this);
        startStateSettingsMenu.setToolTipText("This operation is enabled when a student interface is connected.");
        enableMenuIfInterfaceConnected(startStateSettingsMenu);
        
        toolsMenu.add(advancedMenu);
        
        // not working: sdemi 
        //nextModeMenu = new MenuItem("Next Mode");
        //nextModeMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, keyMask));
        //toolsMenu.add(nextModeMenu);
        //nextModeMenu.addActionListener(this);

        // ///////////////////////////////////////////////////////////

        skillMatrixMenu = new MenuItem("Skill Matrix");
        skillMatrixMenu.setMnemonic(KeyEvent.VK_S);
        windowsMenu.add(skillMatrixMenu);
        skillMatrixMenu.addActionListener(this);

        ruleNamesWindowMenu = new MenuItem("Skill Names");
        ruleNamesWindowMenu.setMnemonic(KeyEvent.VK_N);
        windowsMenu.add(ruleNamesWindowMenu);
        ruleNamesWindowMenu.addActionListener(this);
        
        windowsMenu.addSeparator();

        JMenuItem defaultView = new JMenuItem("Restore Default View");
        defaultView.setMnemonic(KeyEvent.VK_R);
        windowsMenu.add(defaultView);
        defaultView.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		getServer().restoreDefaultView();
        	}
	    });
        
        /* Collinl:  Production of the basic Window View menu is done here
         * Views themselves are added later on in a CtatFrame call which 
         * is itself used by a DockManager call.
         */
        viewPanelMenu = new JMenu("Show Window");
        viewPanelMenu.setMnemonic(KeyEvent.VK_V);
        windowsMenu.add(viewPanelMenu);
        
        // ///////////////////////////////////////////////////////////

        authoringToolsHelpMenu = new MenuItem("Authoring Tools Help");
        authoringToolsHelpMenu.setMnemonic(KeyEvent.VK_H);
        authoringToolsHelpMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpMenu.add(authoringToolsHelpMenu);
        authoringToolsHelpMenu.addActionListener(this);
        
        showWebPageMenuItem = new MenuItem("CTAT Home Page");
        //showWebPageMenuItem.setMnemonic
        //showWebPageMenuItem.setAccelerator
        helpMenu.add(showWebPageMenuItem);
        showWebPageMenuItem.addActionListener(this);
        
        showLicenseMenuItem = VersionInformation.getMenuItem();
        //showWebPageMenuItem.setMnemonic
        //showWebPageMenuItem.setAccelerator
        if (showLicenseMenuItem != null) 
        {
        	helpMenu.add(showLicenseMenuItem);
        	showLicenseMenuItem.addActionListener(this);
        }
        
        aboutMenu = new MenuItem("About...");
        aboutMenu.setMnemonic(KeyEvent.VK_A);
        helpMenu.add(aboutMenu);
        aboutMenu.addActionListener(this);

        // ////////////////////////////////////////////////////////////
        
        if (getController().getCtatModeModel().isJessMode() && VersionInformation.includesJess())
            enableJessMenus(true);
        else 
        	enableJessMenus(false);
        
        return (mbar);
    }
    /**
     * 
     */
    private void fillFileMenu (JMenu fileMenu,int keyMask,boolean notReducedToolMode)
    {
    	debug ("fillFileMenu ()");
    	
        openInterfaceMenu = new MenuItem("Open Student Interface...");
        fileMenu.add(openInterfaceMenu);
        openInterfaceMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,keyMask));
        openInterfaceMenu.setMnemonic(KeyEvent.VK_I);
        openInterfaceMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		OpenInterfaceDialog.openInterface(getServer());
        	}
	    });
        
        enableOpenInterfaceMenu(null);

        fileMenu.addSeparator();
        
        //>-----------------------------------------------------------

        newGraphMenu = new MenuItem("New Graph");
        newGraphMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,keyMask));
        newGraphMenu.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(newGraphMenu);
        
        newGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		newGraphMenuActionPerformed();
        	}
	    });
        
        //>-----------------------------------------------------------
        
        openGraphMenu = new MenuItem("Open Graph...");
        openGraphMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,keyMask));
        openGraphMenu.setMnemonic(KeyEvent.VK_O);
        
        fileMenu.add(openGraphMenu);
        
        openGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		openGraphMenuActionPerformed();		
        	}
	    });
        
        //>-----------------------------------------------------------
        
        openRecentGraphMenu = new JMenu("Open Recent");
        
        //openRecentGraphMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,keyMask));
        //openRecentGraphMenu.setMnemonic(KeyEvent.VK_O);
        
        fileMenu.add(openRecentGraphMenu);
        
        openRecentGraphMenu.setEnabled(false);
        
        fillRecentMenu ();
        
        //>-----------------------------------------------------------
        
        fileMenu.addSeparator();

        //>-----------------------------------------------------------

        closeGraphMenu = new MenuItem("Close Graph");
        closeGraphMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W,keyMask));
        closeGraphMenu.setMnemonic(KeyEvent.VK_W);
        fileMenu.add(closeGraphMenu);
        
        closeGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		closeGraphMenuActionPerformed();
        	}
	    });
        //>-----------------------------------------------------------
        
        fileMenu.addSeparator();
        
        //>-----------------------------------------------------------
        
        saveGraphMenu = new MenuItem("Save Graph");
        saveGraphMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,keyMask));
        saveGraphMenu.setMnemonic(KeyEvent.VK_S);
        fileMenu.add(saveGraphMenu);
        
        saveGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		try 
        		{
        			saveGraphMenuActionPerformed();
        		} 
        		catch (Exception err) 
        		{
        			trace.errStack("error on save-graph", err);
        		}
        	}
	    });
        saveGraphMenu.setEnabled(false);

        saveGraphAsMenu = new MenuItem("Save Graph As...");
        saveGraphAsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
        		keyMask|KeyEvent.SHIFT_DOWN_MASK));
        saveGraphAsMenu.setMnemonic(KeyEvent.VK_S);
        fileMenu.add(saveGraphAsMenu);
        
        saveGraphAsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent arg0) 
        	{
        		saveGraphAsMenuActionPerformed();
        	}
	    });
        
        saveGraphAsMenu.setEnabled(false);
        
        //>-----------------------------------------------------------
        
        /*
        fileMenu.addSeparator();
        
        //>-----------------------------------------------------------
        
        openBrdTemplate = new MenuItem("Open Template...");
        
        if (notReducedToolMode) 
        {
        	openBrdTemplate.setMnemonic(KeyEvent.VK_T);
        	fileMenu.add(openBrdTemplate);
        }
                
        openBrdTemplate.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		openBRDTemplateActionPerformed();
        	}
	    });
        
        //>-----------------------------------------------------------

        saveAsBrdTemplate = new MenuItem("Save Template...");
        
        if (notReducedToolMode) 
        {
        	saveAsBrdTemplate.setMnemonic(KeyEvent.VK_V);
        	fileMenu.add(saveAsBrdTemplate);
        }

        saveAsBrdTemplate.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		saveAsBRDTemplateMenuActionPerformed();
        	}            
	    });

        //>-----------------------------------------------------------
        
        createProblemsTableMenu = new MenuItem("Create Problems Table...");
        
        if (notReducedToolMode) 
        {
        	createProblemsTableMenu.setMnemonic(KeyEvent.VK_C);
        	fileMenu.add(createProblemsTableMenu);
        }
        
        createProblemsTableMenu.addActionListener(this);

        //>-----------------------------------------------------------
        
        mergeProblemsMenu = new MenuItem("Merge Problems...");
        
        if (notReducedToolMode) 
        {
        	mergeProblemsMenu.setMnemonic(KeyEvent.VK_M);
        	fileMenu.add(mergeProblemsMenu);
        }

        mergeProblemsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		mergeProblemsMenuItemActionPerformed();
        	}
	    });
        
        if (getController().getCtatModeModel().isExampleTracingMode())
        	enableMassProductionMenus(true);        
        else  
        	enableMassProductionMenus(false);
        
        */
        
        fileMenu.addSeparator();
        
        saveStudentInterfaceMenu = new MenuItem("Save Student Interface...");
        saveStudentInterfaceMenu.setMnemonic(KeyEvent.VK_U);
        fileMenu.add(saveStudentInterfaceMenu);
        saveStudentInterfaceMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae) {
            	saveStudentInterfaceMenuActionPerformed();        		
        	}
        });
        saveStudentInterfaceMenu.setEnabled(false);
        
        if (notReducedToolMode)
        	fileMenu.addSeparator();

        // Not functional, removed 7/18/06 dtasse
        // openJessFileMenu = new MenuItem("Open Jess File");
        // file.add(openJessFileMenu);
        // openJessFileMenu.addActionListener(this);
        
        saveJessFactsMenu = new MenuItem("Save Jess Facts");
        
        if (notReducedToolMode && VersionInformation.includesJess()) 
        {
        	saveJessFactsMenu.setMnemonic(KeyEvent.VK_F);
        	saveJessFactsMenu.setVisible(true);
        	fileMenu.add(saveJessFactsMenu);
        }

        /// saveJessFactsMenu.addActionListener(this);
        
        saveJessTemplatesMenu = new MenuItem("Save Jess Templates");
        
        if (notReducedToolMode && VersionInformation.includesJess()) 
        {
        	saveJessTemplatesMenu.setMnemonic(KeyEvent.VK_J);
            saveJessTemplatesMenu.setVisible(true);
            fileMenu.add(saveJessTemplatesMenu);
            /// saveJessTemplatesMenu.addActionListener(this);                                   
            fileMenu.addSeparator();
        }

        loadRuleNamesMenu = new MenuItem("Load Skill Names/Hints...");
        
        if (notReducedToolMode) 
        {
        	loadRuleNamesMenu.setMnemonic(KeyEvent.VK_L);
        	fileMenu.add(loadRuleNamesMenu);
        }

        loadRuleNamesMenu.addActionListener(this);

        printGraphMenu = new MenuItem("Print Graph...");
        printGraphMenu.setMnemonic(KeyEvent.VK_P);
        printGraphMenu.addActionListener(this);
        
        if (notReducedToolMode) 
        {
        	printGraphMenu.setMnemonic(KeyEvent.VK_P);
        	fileMenu.add(printGraphMenu);
            printGraphMenu.setEnabled(false);
            //separator
            fileMenu.addSeparator();
        }

        quitMenu = new MenuItem("Quit");
        quitMenu.setMnemonic(KeyEvent.VK_Q);
        quitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, keyMask));
        fileMenu.add(quitMenu);
        quitMenu.addActionListener(this);    	
    }
    /**
     * 
     */
    private void fillViewMenu (JMenu viewMenu)
    {
    	debug ("fillViewMenu ()");
    	
        if (getServer().getPreferencesModel().getBooleanValue("Show Action Labels") != null)
        	actionLabelsMenu = new CheckBoxMenuItem("Show Action Labels", getServer().getPreferencesModel().getBooleanValue("Show Action Labels").booleanValue()); 
        else 
        	actionLabelsMenu = new CheckBoxMenuItem("Show Action Labels", getController().getShowActionLabels());
        
        actionLabelsMenu.setMnemonic(KeyEvent.VK_A);
        viewMenu.add(actionLabelsMenu);
        actionLabelsMenu.addActionListener(this);

        if (getServer().getPreferencesModel().getBooleanValue("Show Rule Labels") != null)
        	ruleLabelsMenu = new CheckBoxMenuItem("Show Skill Labels", getServer().getPreferencesModel().getBooleanValue("Show Rule Labels").booleanValue());
        else 
        	ruleLabelsMenu = new CheckBoxMenuItem("Show Skill Labels", getController().getShowRuleLabels());
        
        ruleLabelsMenu.setMnemonic(KeyEvent.VK_S);
        viewMenu.add(ruleLabelsMenu);
        ruleLabelsMenu.addActionListener(this);
        
        /*if (getServer().getPreferencesModel().getBooleanValue("Show Callback Functions") != null)
        	showCallbackFnMenu = new CheckBoxMenuItem("Show Callback Functions", 
    						   getServer().getPreferencesModel().getBooleanValue("Show Callback Functions").booleanValue());
            else showCallbackFnMenu = new CheckBoxMenuItem("Show Callback Functions", getController().getShowRuleLabels());
        //showCallbackFnMenu.setMnemonic(KeyEvent.VK_C);
            graphMenu.add(showCallbackFnMenu);
            showCallbackFnMenu.addActionListener(this);
         */
        
        if (getServer().getPreferencesModel().getBooleanValue("Show Last Cog. Model Check Labels") != null)
        	lastCheckLISPLabelsMenu = new CheckBoxMenuItem("Show Last Cog. Model Check Labels",
        			getServer().getPreferencesModel().getBooleanValue("Show Last Cog. Model Check Labels").booleanValue());
        else 
        	lastCheckLISPLabelsMenu = new CheckBoxMenuItem("Show Last Cog. Model Check Labels",
        			getController().isPreCheckLISPLabelsFlag());
        
        lastCheckLISPLabelsMenu.setMnemonic(KeyEvent.VK_L);
        
        viewMenu.add(lastCheckLISPLabelsMenu);
        
        lastCheckLISPLabelsMenu.addActionListener(this);                
    }	
    /**
     * 
     */
    private void fillMassProduction (JMenu aMassProduction,Boolean notReducedToolMode)
    {
    	debug ("fillMassProduction ()");
    	
        createProblemsTableMenu = new MenuItem("Create Problems Table...");
        
        if (notReducedToolMode) 
        {
        	createProblemsTableMenu.setMnemonic(KeyEvent.VK_C);
        	aMassProduction.add(createProblemsTableMenu);
        }
        
        createProblemsTableMenu.addActionListener(this);

        //>-----------------------------------------------------------
        
        mergeProblemsMenu = new MenuItem("Merge Problems...");
        
        if (notReducedToolMode) 
        {
        	mergeProblemsMenu.setMnemonic(KeyEvent.VK_M);
        	aMassProduction.add(mergeProblemsMenu);
        }

        mergeProblemsMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		mergeProblemsMenuItemActionPerformed();
        	}
	    });
        
        if (getController().getCtatModeModel().isExampleTracingMode())
        	enableMassProductionMenus(true);        
        else  
        	enableMassProductionMenus(false);
    }
    /**
     * Open a dialog to save a .brd template for Mass Production.
     */
    public void saveAsBRDTemplateMenuActionPerformed() 
    {
    	ProblemModel pm = getController().getProblemModel();
    	
    	if (pm == null)
    		return;
    	
    	if (pm.getNodeCount() < 1) 
    	{
    		JOptionPane.showMessageDialog(getServer().getActiveWindow(),
    				"There is currently no graph in the Behavior Recorder.",
    				"Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
    	}
    	
    	// sewall 2012/06/14: not sure we need this
    	if (pm.getProblemName() == null || pm.getProblemName().length() < 1) 
    	{
            NodeView vertex = pm.getStartNode().getNodeView();
            pm.setProblemName(vertex.getText());
        }

    	String targetDir = SaveFileDialog.getBrdFileOtherLocation(getController());
    	
        File selectedFile = DialogUtilities.chooseFile(targetDir, pm.getProblemName(),
        		new BrdFilter(), "Save Mass Production Template File",
        		"Save", getController());

        if (selectedFile != null) 
        {
            String problemName = selectedFile.getName();
            String problemFullName = selectedFile.getPath();
            
            if (trace.getDebugCode("massproduction"))
            	trace.out("massproduction", "problemName "+problemName+", problemFullName "+problemFullName);

            if (!problemName.endsWith(".brd")) 
            {
                problemName += ".brd";
                problemFullName += ".brd";
            }
            
            File chosenFile = new File(problemFullName);
            
            if (chosenFile.exists()) 
            {
                int ans = JOptionPane.showConfirmDialog(getServer().getActiveWindow(),
                        "The file already exists. Overwrite?", "File exists",
                        JOptionPane.OK_CANCEL_OPTION);
                
                if (ans != JOptionPane.OK_OPTION)
                	return;
            }
            
            pm.setProblemName(problemName);
            pm.setProblemFullName(problemFullName);

            getController().getProblemStateWriter().saveBRDFile(pm.getProblemFullName());
        }
    }
    /**
     * Open a Mass Production template brd.
     */
    public void openBRDTemplateActionPerformed() throws FactoryConfigurationError 
    {
    	ProblemModel pm = getController().getProblemModel();
    	
        if (pm.getProblemGraph().getNodeCount() > 0) 
        {
            int result = getController().saveCurrentProblemWithUserPrompt(true);
            if (result == JOptionPane.CANCEL_OPTION)
                return;
        }

        BR_Controller controller = getController();
        String targetDir = SaveFileDialog.getBrdFileOtherLocation(controller);
        File selectedFile = DialogUtilities.chooseFile(targetDir,
        		new BrdFilter(), "Open Mass Production Template File",
        		"Open", controller);
        if (selectedFile == null)
        	return;
        
        String problemName = selectedFile.getName();
        
        if (!problemName.endsWith(".brd"))
            problemName += ".brd";
        
        if (!selectedFile.exists()) 
        {
            File tempFile = new File(selectedFile.getPath() + ".brd");

            if (!tempFile.exists()) 
            {
                String message = "The file \"" + selectedFile.getName()
                        + "\" does not exist.";

                JOptionPane.showMessageDialog(getServer().getActiveWindow(),
                        message, "Warning", JOptionPane.INFORMATION_MESSAGE);

                return;
            }
        }
        
        getController().reset();
        String problemFullName = selectedFile.getPath();
        
        if (!problemFullName.endsWith(".brd"))
            problemFullName += ".brd";

        pm.setCourseName("");
        pm.setUnitName("");
        pm.setSectionName("");
        pm.setProblemName(problemName);
        pm.setProblemFullName(problemFullName);
        
        try 
        {
        	getController().openBRDFileAndSendStartState(problemFullName, null, null);
        	ActionEvent ae = new ActionEvent(this, 0, "Loaded template "+problemFullName);
        	getController().getUndoPacket().getInitializeAction().actionPerformed(ae);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        
        String parent = selectedFile.getParent();
        
        if (trace.getDebugCode("massproduction")) 
        	trace.out("massproduction", "openBRDTemplateActionPerformed() new "+
        			SaveFileDialog.BRD_OTHER_DIR_KEY+" = " + parent);
        
		getServer().getPreferencesModel().setStringValue(SaveFileDialog.BRD_OTHER_DIR_KEY, parent);
    }

	/**
     * Invoke {@link CtatMenuBar.OutOfOrderMessageDialog}.
     */
    protected void doOutOfOrderMessageDialog() 
    {
    	if (trace.getDebugCode("menu"))
    		trace.out("menu", "doOutOfOrderMessageDialog()");
    	
    	JDialog ooomDialog = new OutOfOrderMessageDialog();
	}
    
    /**
     * Prompt for a String and call {@link ProblemModel#setOutOfOrderMessage(String)}.
     */
    private class OutOfOrderMessageDialog extends JDialog implements ActionListener 
    {
		private static final long serialVersionUID = 770478628430255130L;
		ProblemModel pm = getController().getProblemModel();
    	JTextArea entry;
    	JButton ok, cancel;
    	
    	OutOfOrderMessageDialog() 
    	{
    		super(getServer().getActiveWindow(), true);  // true => modal
    		setName("OutOfOrderMessageDialog");
    		setTitle("Edit \"Out Of Order\" Message");
    		setLayout(new BorderLayout());
    		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    		
    		JLabel prompt = new JLabel("Enter the feedback message to display "+
    				"when a step is correct but out of order.");
    		prompt.setBorder(new EmptyBorder(4, 4, 2, 4));
    		prompt.setMaximumSize(prompt.getPreferredSize());
    		prompt.setMinimumSize(prompt.getPreferredSize());
    		add(prompt, BorderLayout.NORTH);

    		entry = new JTextArea(2, 40);
    		entry.setName("OutOfOrderMessage");
    		entry.setFont(new Font("Monospaced", Font.PLAIN, 12));
    		entry.setWrapStyleWord(true);
    		entry.setText(pm.getOutOfOrderMessage());
    		{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(entry); }
    		entry.setMinimumSize(entry.getPreferredSize());
    		JScrollPane entryPane = new JScrollPane(entry);
    		entryPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    		entryPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    		entryPane.setBorder(new EmptyBorder(2, 4, 2, 4));
    		add(entryPane, BorderLayout.CENTER);
    		
    		ok = new JButton("OK");
    		ok.setName("OutOfOrderMessageDialogOK");
    		ok.addActionListener(this);
    		cancel = new JButton("Cancel");
    		ok.setName("OutOfOrderMessageDialogCancel");
    		cancel.addActionListener(this);
    		Box btnPanel = new Box(BoxLayout.X_AXIS);
    		btnPanel.add(ok);
    		btnPanel.add(cancel);
    		btnPanel.setBorder(new EmptyBorder (2, 4, 4, 4));
    		btnPanel.setMaximumSize(btnPanel.getPreferredSize());
    		if (trace.getDebugCode("menu"))
    			trace.out("menu", "prefSize: ok "+ok.getPreferredSize()+", cancel "+cancel.getPreferredSize()+
    					", btnPanel"+btnPanel.getPreferredSize());
    		add(btnPanel, BorderLayout.SOUTH);

    		getContentPane().validate();
    		if (trace.getDebugCode("menu"))
    			trace.out("menu", "PrefSize: contentPane "+getContentPane().getPreferredSize()+
    					", entryPane "+entryPane.getPreferredSize()+
    					", entry "+entry.getPreferredSize()+
    					", prompt "+prompt.getPreferredSize());
    		Dimension initSize = getContentPane().getPreferredSize();
    		setSize(new Dimension(initSize.width+36, initSize.height+54));
    		setVisible(true);
    	}
    	
    	public void actionPerformed(ActionEvent evt) 
    	{
    		if (evt.getSource() == ok) 
    		{
        		String oldText = pm.getOutOfOrderMessage();
    			String newText = entry.getText();
    			pm.setOutOfOrderMessage(newText);
    			
    			if (oldText == null || !oldText.equals(newText)) 
    			{
    				ActionEvent ae = new ActionEvent(this, 0, "Edit Out of Order Message");
    				getController().getUndoPacket().getCheckpointAction().actionPerformed(ae);
    			}
    		}
    		
			setVisible(false);
			dispose();
    	}
    }


	/**
     * Handle the {@link #openGraphMenu} click.
     */
    private void openGraphMenuActionPerformed() 
    {
    	// may not need this - LoadFileDialog.doLoadBRDFile(CTAT_Launcher, String, String, boolean)
		// handles the case of tabs with already-loaded graphs
    	/*
    	ProblemModel pm = getController().getProblemModel();
    	if (pm.getProblemGraph().getNodeCount() > 0) {
    		int result = getController().saveCurrentProblemWithUserPrompt(true);
    		if (result == JOptionPane.CANCEL_OPTION) { 
    			return; 
    		}
    	}
    	*/
    	
    	CTATFileItem aFileItem = LoadFileDialog.doDialog(this.server, true);
    	if(aFileItem == null)
    		return;
 		getController().updateStatusPanel(null);
    	addRecentfile (aFileItem.getDirectory(),aFileItem.getFileName());
    	
    	ProblemModel openedPM = getController().getProblemModel();
    	ProblemModel mainPM = getController().getProblemModel();
    	boolean check = ((openedPM != null) && (openedPM.getStartNode() != null) && (openedPM == mainPM));
    	if (check) {
    		enableCreateStartStateMenus(false);
    		enableGotoStartStateMenus(true);
    		enablePrintGraphMenus(true);
    		enableSaveGraphMenus(true);
    	}
	}

	// Sat Jun 04 23:34:36 2005: Noboru
    /**
     * Toggle a menue item for activate/Deactivate Sim Student
     * 
     * @param event
     *            a <code>boolean</code> value
     */
    public void switchSimStActivationMenu(SetSimStudentActivationMenuEvent event) 
    {
        final boolean b = event.newStatus;
        activateMissMenu.setText(b ? SIM_ST_DEACTIVATE_MENU
				 : SIM_ST_ACTIVATE_MENU);
    }
    /**
     * 
     * @param status
     */
    public void enableSimStActivationMenu(boolean status) 
    {
    	if (VersionInformation.isRunningSimSt())
    		activateMissMenu.setEnabled(status);
    }
    /**
     * 
     */
    public void actionPerformed(ActionEvent ae) 
    {
        String arg = ae.getActionCommand();
        Object source = ae.getSource();
        
        if(trace.getDebugCode("menu"))
        	trace.out("menu", "MenuBar.actionPerformed() \""+arg+"\" from "+source.getClass().getSimpleName());

        getServer().getLoggingSupport().authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER, arg, "", "", "");
        
        if (trace.getDebugCode("undo")) trace.out("undo", "CtatMenuBar.actionPerformed("+arg+")");
        
        if(CtatModePanel.AUTHOR_MODE_COMBO_BOX_EVENT_CMD.equals(arg))
        {
        	enableMenuIfInterfaceConnected(startStateSettingsMenu);
        	return;
        }
        
        if (source == printGraphMenu) 
        {
            printGraphActionPerformed();
        }

        if (source == newProblemMenu) 
        {

        }

        if (source == loadProdRulesMenu) 
        {
        	loadProdRulesMenuActionPerformed();
        }
        
        if (source == setBreakpointsMenu) 
        {
        	setBreakpointMenuActionPerformed();
        }
        
        if (source == clearBreakpointsMenu) 
        {
        	clearBreakpointMenuActionPerformed();
        }
        
        if (source == resumeBreakpointsMenu) 
        {
        	resumeBreakpointMenuActionPerformed();
        }
        
        if (source == saveInstructionsMenu) 
        {        	
        }
        
        if (source == loadInstructionsMenu) 
        {        	
	    //	this.loadInstructionsMenuActionPerformed();
        }
        
        if (source == quitMenu) 
        {
            quitMenuActionPerformed();
        }
        
        if (source == loadRuleNamesMenu) 
        {
            loadRulesMenuActionPerformed();
        }
        
        if (source == createProblemsTableMenu) 
        {
            createEmptyExcelConfigurationMenuItemActionPerformed();
        }

        // if (source == openJessFileMenu) 
        // {
        //   openJessFileMenuActionPerformed();
        // }
        
        if (source == saveJessFactsMenu) 
        {
        	if (getController().getCtatModeModel().isExampleTracingMode())
        	{
        		JOptionPane.showMessageDialog(getController().getBR_Frame(),"You're in Example Tracing Mode.  There are no Jess facts to save.");
        	}
        	else
        	{
        		WMEEditor wme =  (WMEEditor) getController().getModelTracer().getWmeEditor();
        		wme.saveFacts();
        	}
        }
        
        if (source == saveJessTemplatesMenu) 
        {
        	if (getController().getCtatModeModel().isExampleTracingMode())
        	{
        		JOptionPane.showMessageDialog(getController().getBR_Frame(), "You're in Example Tracing Mode.  There are no Jess templates to save.");        		
        	}
        	else
        	{
        		WMEEditor wme = (WMEEditor) getController().getModelTracer().getWmeEditor();
        		wme.saveTemplates(true);
        	}
        }

        if (source == ruleNamesWindowMenu) 
        {
            ruleNamesWindowMenuActionPerformed();
        }

        if (source == actionLabelsMenu) 
        {
            actionLabelsMenuActionPerformed();
        }

        if (source == lastCheckLISPLabelsMenu) 
        {
            lastLISPCheckLabelsMenuActionPerformed();
        }

        if (source == ruleLabelsMenu) 
        {
            ruleLabelsMenuActionPerformed();
        }
        
       /* 
        if (source == showCallbackFnMenu){
        	showCallbackFnMenuActionPerformed();
        }*/
        
        if (source == setWMMenu) 
        {
            setWMMenuActionPerformed();
        }
        
        if (source == checkAllActionsMenu) 
        {
        	// getController().updateStatusLabel("            Processing checkAllActions...");
            checkAllActionsMenuActionPerformed();

        }

        if (source == resetLinkColorsMenu) 
        {

            resetLinkColorsMenuActionPerformed();
        }

        if (source == activateMissMenu) 
        {
            activateMissMenuActionPerformed();
        }
        
        if (source == cleanupStartStateMenu)
        {
        	cleanupStartStateMenuActionPerformed();
        }
        
        if (source == showUnmatchedComponentsMenu)
        {
        	showUnmatchedComponentsMenuActionPerformed();
        }

        if (source == createStartStateMenu) 
        {
            createStartStateMenuActionPerformed();
        }

        if (source == preferencesMenu) 
        {
            preferencesMenuActionPerformed();
        }
        
        if (source == collaborationMenu)
        {
        	collaborationMenuActionPerformed();
        }

        if (source == logConsoleMenu) 
        {
            logConsoleMenuActionPerformed();
        }

        if (source == authoringToolsHelpMenu) 
        {
            authoringToolsHelpMenuActionPerformed(ae);
        }
        
        if (source == moodleMaker) 
        {
			CTATMoodleMaker wizard=new CTATMoodleMaker ();
			wizard.init ();
			wizard.show ();
        }
        
        if (source == createLMSFilesMenu) 
        {
        	createLMSFiles(ae);
        }
        	
        if (source == showWebPageMenuItem) 
        {
            invokeBrowserForWebPage(ae);
        }
        
        if (source == showLicenseMenuItem) 
        {
            invokeBrowserForWebPage(ae);
        }
        
        if (source == nextModeMenu) 
        {
            nextModeMenuActionPerformed();
        }
        
        /*  
        if (source == deleteStateMenu) 
        {
            deleteStateMenuActionPerformed();
        }
        */
        
        if (source == skillMatrixMenu) 
        {
            skillMatrixMenuActionPerformed();
        }

        if (source == goToStartStateMenu) 
        {
            goToStartStateMenuActionPerformed();
        }
        
        if (source == startStateSettingsMenu)
        {
        	compareComponentSettingsMenuActionPerformed();
        }

        if (source == retractOneStepMenu) 
        {
            retractOneStepMenuActionPerformed();
        }

        // if (source == previousPreferredState) 
        // {
        //	 previousPreferredStateActionPerformed();
        // }

        if (source == nextPreferredStepMenu) 
        {
            nextPreferredStepMenuActionPerformed();
        }

        if (source == aboutMenu) 
        {
            aboutMenuActionPerformed();
        }
    }

    /**
     * If {@link UniversalToolProxy#getUnmatchedSelectionsDialogLauncher()} has a dialog to show,
     * launch it.
     */
    private void showUnmatchedComponentsMenuActionPerformed() {
		UniversalToolProxy utp = getController().getUniversalToolProxy();
		UnmatchedSelectionsDialog launcher = null;
		boolean hasDialog = false;
		if(utp == null ||
				(launcher = utp.getUnmatchedSelectionsDialogLauncher()) == null ||
				(!(hasDialog = utp.getUnmatchedSelectionsDialogLauncher().hasDialog()))) {
			trace.err("CtatMenuBar.findObsoleteComponentsMenuActionPerformed() cannot invoke dialog"+
				" utp "+trace.nh(utp)+", launcher "+trace.nh(launcher)+", hasDialog "+hasDialog);
			return;
		}
		launcher.launch();
    }

    /**
     * Ask for confirmation and, if yes, call 
     * {@link edu.cmu.pact.ctat.model.StartStateModel#pruneInterfaceDescriptions(ProblemModel)}
     * and then {@link BR_Controller#goToStartState(boolean, boolean) goToStartState(true, true)}.
     */
    private void cleanupStartStateMenuActionPerformed() {
    	if(trace.getDebugCode("startstate"))
    		trace.out("startstate", "CtatMenuBar.cleanupStartStateMenuActionPerformed()");
    	
    	UniversalToolProxy utp = null;
    	if(getController() == null || (utp = getController().getUniversalToolProxy()) == null) {
    		trace.err("CtatMenuBar.cleanupStartStateMenuActionPerformed(): null getController() "+
    				getController()+" or .getUniversalToolProxy() "+utp);
    		return;
    	}
    	int nToDelete = utp.getStartStateModel().pruneInterfaceDescriptions(getController().getProblemModel(), false);
    	if(nToDelete < 1) {
        	JOptionPane.showMessageDialog(getController().getCtatFrameController().getDockedFrame(),
        			"Found no unneeded items in the graph to delete.",
        			"Clean Up Already Complete", JOptionPane.INFORMATION_MESSAGE);
    		return;
    	}
    	int okCancel = JOptionPane.showConfirmDialog(getController().getCtatFrameController().getDockedFrame(),
    			"This graph contains information that is not editable but may no longer be useful; \r\n" +
    			"the extra information can make the tutor slow to start up. Clean up will remove this \r\n" +
    			"unneeded information from the graph and then jump to the start state.  Continue?",
    			"Clean Up Graph", JOptionPane.OK_CANCEL_OPTION);
    	if(okCancel != JOptionPane.OK_OPTION)
    		return;
    	int nDeleted = utp.getStartStateModel().pruneInterfaceDescriptions(getController().getProblemModel(), true);
    	if(nDeleted > 0) {
			ActionEvent ae = new ActionEvent(this, 0, "Clean up start state");
			getController().getUndoPacket().getCheckpointAction().actionPerformed(ae);
		}
    	if(nDeleted != nToDelete)
    		trace.err("CtatMenuBar.cleanupStartStateMenuActionPerformed() nToDelete "+nToDelete+
    				" != nDeleted "+nDeleted);
    	getController().goToStartState(true, true);
    	JOptionPane.showMessageDialog(getController().getCtatFrameController().getDockedFrame(),
    			"Clean up removed "+nDeleted+" unneeded items from the graph.",
    			"Clean Up Completed", JOptionPane.INFORMATION_MESSAGE);
	}


	/**
     * Start or stop an author-time collaboration session.
     */
	private void collaborationMenuActionPerformed() {
		if(collaborationMenu.getText().toLowerCase().startsWith("start")) { // FIXME : different test?
			int teamSize = -1;
			final String prompt0 = "Please enter the total number of\nstudents on the collaborating team:";
			String prompt = prompt0;
			Object entry = "2";
			do {
				entry = JOptionPane.showInputDialog(getController().getCtatFrameController().getDockedFrame(),
						prompt, "Enter collaboration team size", JOptionPane.QUESTION_MESSAGE, null, null,
						entry);
				if(entry == null)  // user pressed Cancel
					return;
				try {
					teamSize = Integer.parseInt(entry.toString().trim());
					if(teamSize < 2)
						throw new IllegalArgumentException("teamSize "+teamSize+" must be > 1");
					if(trace.getDebugCode("collab"))
						trace.out("collab", "teamSize "+teamSize);
				} catch(Exception e) {
					trace.err("CtatMenuBar.collaborationMenuActionPerformed(): invalid teamSize "+
							entry+": "+e+"; cause "+e.getCause());
					prompt = "The team size must be an integer\ngreater than 1. Click Cancel to quit.";
				}
			} while(teamSize < 2);
			getController().getLauncher().getLauncherServer().startAuthorTimeCollaboration(teamSize);
			collaborationMenu.setText("Stop Collaboration");
		} else {
			getController().getLauncher().getLauncherServer().stopAuthorTimeCollaboration();
			collaborationMenu.setText("Start Collaboration...");
		}
	}


	private void compareComponentSettingsMenuActionPerformed()
	{
		CtatModeModel cmm = getController().getCtatModeModel();
		if(cmm != null) {
			if(!(CtatModeModel.DEFINING_START_STATE.equalsIgnoreCase(cmm.getCurrentAuthorMode())))
				cmm.setAuthorMode(CtatModeModel.DEFINING_START_STATE);
		}
		EditComponentStartStateSettingsDialog.create(getController(), getServer().getActiveWindow());
	}

	/**
     * Launch via {@link LogConsole#createConsole(BR_Controller)}.
     */
    private void logConsoleMenuActionPerformed() 
    {
		LogConsole.createConsole(getController());
    }

    /**
     * 
     */
    private void aboutMenuActionPerformed() 
    {
        PersonnelInfo.showAboutBox(getServer().getActiveWindow());
    }

    /**
     * 
     */
    private void goToStartStateMenuActionPerformed() 
    {
        getServer().getLoggingSupport().authorActionLog(
						       AuthorActionLog.BEHAVIOR_RECORDER,
						       BR_Controller.GO_TO_START_STATE, "", "", "");
        getController().goToStartState(true, true);
    }

    /**
     * 
     */
    private void retractOneStepMenuActionPerformed() 
    {
    	BR_Controller controller = getController();
    	controller.getLoggingSupport().authorActionLog(
						       AuthorActionLog.BEHAVIOR_RECORDER, RETRACT_LAST_STEP, "", "", "");
    	controller.getProcessTraversedLinks().retractLinksFromTail(1);
    }

    /**
     * 
     */
    private void previousPreferredStateActionPerformed() 
    {
        getServer().getLoggingSupport().authorActionLog (AuthorActionLog.BEHAVIOR_RECORDER,
        		PREVIOUS_PREFERRED_STATE, "", "", "");
        
        BR_Controller controller = getController();
        if (controller == null || controller.getCurrentNode() == null)
        	return;
        
        controller.MoveToPrevStepOnPreferredPath(getController().getCurrentNode());
    }

    /**
     * 
     */
    private void nextPreferredStepMenuActionPerformed() 
    {
    	BR_Controller controller = getController();
    	ProblemNode currentNode = controller.getCurrentNode();
    	
    	controller.getLoggingSupport().authorActionLog(
						       AuthorActionLog.BEHAVIOR_RECORDER, NEXT_PREFERRED_STEP,
						       currentNode == null ? "null" : currentNode.toString(), "", "");
        if (currentNode== null)
        	return;
        
        controller.MoveToNextStepOnPreferredPath();
    }

    /**
     * 
     */
    private void skillMatrixMenuActionPerformed() 
    {
//        if (skillMatrixDialog == null)
//            skillMatrixDialog = new SkillMatrixDialog(controller);

        SkillMatrixDialog.doDialog(getController());
    }

   /**
     * 
     */
   /* private void deleteStateMenuActionPerformed() {
        NodeView currVertex = getController().getSolutionState()
	    .getCurrentNode().getNodeView();
        currVertex.delete();
    }*/

    /**
     * 
     */
    private void nextModeMenuActionPerformed() 
    {
    	getController().nextMode();
    }

    /**
     * @param evt 
     * 
     */
    private void authoringToolsHelpMenuActionPerformed(ActionEvent evt) 
    {
        if (launchHelp == null)
            launchHelp = new LaunchHelp();
        
        launchHelp.showHelp(evt);
    }
    
    private void createLMSFiles(ActionEvent ae)
    {
    	CreateLMSFilesDialog c = new CreateLMSFilesDialog();
    }
    
    private void invokeBrowserForWebPage(ActionEvent evt) 
    {
    	String licenseWebPage = VersionInformation.getWebPage();
    	
    	if (evt.getSource() == showLicenseMenuItem && licenseWebPage != null)
    		LaunchCTATWebsite.showWebPage(licenseWebPage);
    	else
    		LaunchCTATWebsite.showWebPage(BRPanel.HOME_URL);
    }

    /**
     * 
     */
    private void preferencesMenuActionPerformed() 
    {
        try 
        {
            PreferencesModel model = getServer().getPreferencesModel();
            PreferencesWindow.create(model, getServer()).setVisible(true);
        } 
        catch (Exception eSetPreferences) 
        {
            eSetPreferences.printStackTrace();
        }
    }

    /**
     * 
     */
    void createStartStateMenuActionPerformed()
    {
    	UniversalToolProxy utp;
        if ((utp = getController().getUniversalToolProxy()) == null ||
        		utp.getStudentInterfaceConnectionStatus() == StudentInterfaceConnectionStatus.Disconnected) {
        	int result = JOptionPane.showConfirmDialog(getController().getCtatFrameController().getDockedFrame(),
        			"No student interface is active. Do you really want to create a start state?",
        			"Create Start State", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        	if (result != JOptionPane.YES_OPTION)
        		return;
        }
    	getController().getCtatFrameController().getDockedFrame().getCtatModePanel().createStartState();
    }

    /**
     * 
     */
    private void activateMissMenuActionPerformed() 
    {
        // Toggle status of the MissController
        getController().activateMissController(getController().getCtatModeModel().isSimStudentMode());
    }

    /**
     * 
     */
    private void resetLinkColorsMenuActionPerformed() 
    {
        //            if (!checkValidGraph())
        //                return;
    	ProblemEdge tempEdge;
    	EdgeData tempMyEdge;
        
    	Enumeration<ProblemEdge> iter = getController().getProblemModel().getProblemGraph().edges();
	
    	while (iter.hasMoreElements()) 
    	{
    		tempEdge = (ProblemEdge) iter.nextElement();
    		tempMyEdge = tempEdge.getEdgeData();
        
    		tempMyEdge.setCheckedStatus(EdgeData.NEVER_CHECKED);
    		tempMyEdge.getPreLispCheckLabel().resetAll(tempMyEdge.getUniqueID(),EdgeData.NEVER_CHECKED);
    		
    		getController().setFirstCheckAllStatesFlag(true);
    	}        
    }

    /**
     * 
     */
    private void checkAllActionsMenuActionPerformed() 
    {
        if (!checkValidGraph())
            return;
        
        Vector ESEGraph = new Vector();
        final BR_Controller controller = getController();
        trace.out("mg", "CtatMenuBar (checkAllActionsMenuActionPerformed): on tab " + controller.getTabNumber());
        final ProblemModel model = controller.getProblemModel();
        model.setCheckAllNodes(new Vector());
        controller.setSendESEGraphFlag(false);
        controller.sendBehaviorRecorderGraphToLisp(ESEGraph, model.getStartNode(), 0);
        model.setCheckAllNodes(new Vector());
        
        // getController().setStatusLabel();
    }

    /**
     * 
     */
    private void setWMMenuActionPerformed() 
    {
        if (getController().getSolutionState().getCurrentNode() != getController().getProblemModel().getStartNode())
            getController().checkProductionRulesChainNew(getController().getSolutionState().getCurrentNode());
    }

    /**
     * 
     */
    private void ruleLabelsMenuActionPerformed() 
    {
        getController().setShowRuleLabels(!getController().getShowRuleLabels());
        getServer().getPreferencesModel().setBooleanValue("Show Rule Labels",new Boolean(getController().getShowRuleLabels()));
        getServer().getPreferencesModel().saveToDisk();        
        getController().getJGraphWindow().getJGraph().repaint();
    }

   /* private void showCallbackFnMenuActionPerformed() {
    	getController().setShowCallbackFn(!getController().getShowCallbackFn());
        getServer().getPreferencesModel().setBooleanValue("Show Callback Functions",
							 new Boolean(getController().getShowCallbackFn()));
        getServer().getPreferencesModel().saveToDisk();
        
    }*/
    
    /**
     * 
     */
    private void lastLISPCheckLabelsMenuActionPerformed() 
    {
    	getController().setPreCheckLISPLabelsFlag(!getController().isPreCheckLISPLabelsFlag());

        ProblemEdge tempEdge;
        EdgeData tempMyEdge;

        Enumeration<ProblemEdge> iter = getController().getProblemModel().getProblemGraph().edges();
        
        while (iter.hasMoreElements()) 
        {
            tempEdge = (ProblemEdge) iter.nextElement();
            tempMyEdge = tempEdge.getEdgeData();
            tempMyEdge.getPreLispCheckLabel().setVisible(getController().isPreCheckLISPLabelsFlag());
        }
        
        getServer().getPreferencesModel().setBooleanValue("Show Last Cog. Model Check Labels",new Boolean(getController().isPreCheckLISPLabelsFlag()));
        getServer().getPreferencesModel().saveToDisk();
        getController().getJGraphWindow().getJGraph().repaint();

    }

    /**
     * 
     */
    private void actionLabelsMenuActionPerformed() {
    	BR_Controller controller = getController();
    	
    	controller.setShowActionLabels(!controller.getShowActionLabels());
    	getServer().getPreferencesModel().setBooleanValue("Show Action Labels",
							 new Boolean(controller.getShowActionLabels()));
        getServer().getPreferencesModel().saveToDisk();
        controller.getJGraphWindow().getJGraph().repaint();
    }

    /**
     * 
     */
    private void ruleNamesWindowMenuActionPerformed() {

        RuleNamesDisplayDialog ruleDisplayDialog = getController().getRuleDisplayDialog();

        ruleDisplayDialog.setVisible(true);
        ruleDisplayDialog.resetRuleProductionList(true);
    }

    /**
     * 
     */
    private void mergeProblemsMenuItemActionPerformed() {
        new MergeMassProductionDialog(getController());
    }

    /**
     * 
     */
    private void createEmptyExcelConfigurationMenuItemActionPerformed() {
        new CreateProblemsTableDialog(getController());
    }

    /**
     * 
     */
    private void quitMenuActionPerformed() {
    	getController().closeApplication(true);
    }

    /**
     * For MacOS, the handler for Cmd-Q.
     */
    public boolean quitHandlerForMac() {
	quitMenuActionPerformed();
	return false;
    }

    /**
     * Handle the File:New Graph menu item.
     */
    private void newGraphMenuActionPerformed() 
    {
        DockManager docker=getServer().getCtatFrameController().getDockManager();
        
        if (docker!=null) // should be useless but who knows
        {
        	int tabNumber = docker.newGraphTab();
        	if (tabNumber > 0) docker.showGraphWindow(tabNumber);
        	
        	View testView=docker.getCoreView("Variable Viewer");
        	
        	if (testView!=null)
        	{
        		VariableViewer varViewer=(VariableViewer) testView.getComponent();
        		
        		if (varViewer!=null)
        		{
        			varViewer.reset ();
        		}
        	}
        }
        enableCreateStartStateMenus(true);
        enableGotoStartStateMenus(false);
        enablePrintGraphMenus(false);
        enableSaveGraphMenus(false);

        getController().updateStatusPanel(null);
    }

    /**
     * Handle the File:Close Graph menu item.
     */
    private void closeGraphMenuActionPerformed() 
    {
    	int ret = getController().startNewProblem();
    	
        if (ret == JOptionPane.CANCEL_OPTION)
            return;
        
        enableCreateStartStateMenus(true);
        enableGotoStartStateMenus(false);
        enablePrintGraphMenus(false);
        enableSaveGraphMenus(false);

        getController().updateStatusPanel(null);
        
        DockManager docker=getServer().getCtatFrameController ().getDockManager();
        
        if (docker!=null) // should be useless but who knows
        {
        	try {
        		int tabNumber = getServer().getTabManager().getFocusedTab().getTabNumber();
            	getServer().getTabManager().getTabByNumber(tabNumber).getView().closeWithAbort();
			} catch (OperationAbortedException e) {
				e.printStackTrace();				
			}
        	View testView=docker.getCoreView("Variable Viewer");
        	
        	if (testView!=null)
        	{
        		VariableViewer varViewer=(VariableViewer) testView.getComponent();
        		
        		if (varViewer!=null)
        		{
        			varViewer.reset ();
        		}
        	}
        }
    }

    /**
     * 
     */
    private void printGraphActionPerformed() {
	//        PrintUtilities.printComponent(brFrame.getScrollPanel().drawingArea);
    }

    
    private void newProblemMenuActionPerformed() {
    	trace.out("New Problem...");
    }

    private void loadProdRulesMenuActionPerformed(){
    	getController().getModelTracer().reloadProductionRulesFile();
    }

    private void setBreakpointMenuActionPerformed(){
    	getController().getRuleActivationTree().displayBreakPointsPanel();
    }

    private void clearBreakpointMenuActionPerformed(){
    	getController().getRuleActivationTree().getBreakPointRules().clear();
    }

    private void resumeBreakpointMenuActionPerformed() {
    	getController().getModelTracer().setResume(true);
    }
    
    private void saveInstructionsMenuActionPerformed() {
    	getController().getMissController().saveInstructions();
    }
    
    private void loadInstructionsMenuActionPerformed() {
    	getController().getMissController().loadInstructions();
    }

    private void loadWmeTypesMenuActionPerformed() {
    	getController().getMissController().setSimStWmeTypeFile();
    }

    private void initializeWmesMenuActionPerformed() {
    	getController().getMissController().setSimStInitStateFile();
    }
    
    private void loadFeaturePredicatesMenuActionPerformed() {
    	getController().getMissController().readSimStPredicateSymbols();
    }
    
    private void loadOperatorsMenuActionPerformed() {
    	getController().getMissController().readSimStOperators();
    }
    
    private void testModelMenuActionPerformed() 
    {
    	getController().getMissController().testProductionModelOn();
    }
    
    private void saveGraphAsMenuActionPerformed() 
    {
    	BR_Controller controller = getController();
    	ProblemModel probMod = controller.getProblemModel();
        if (probMod.getProblemName().length() == 0) {
            if (probMod.getStartNode() == null) {
                JOptionPane
		    .showMessageDialog(
		    		getServer().getActiveWindow(),
				       "There is currently no graph in the Behavior Recorder.",
				       "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            NodeView Vertex = probMod.getStartNode().getNodeView();
            probMod.setProblemName(Vertex.getText());
        }
        SaveFileDialog.doDialog(controller, probMod.getProblemName());
        getController().updateStatusPanel(probMod.getProblemFullName());
        //getController().getActiveWindow().setTitle("Cognitive Tutor Authoring Tools - " + getController().getProblemModel().getProblemFullName ());
        
        File converter=new File (controller.getProblemModel().getProblemFullName());
        
        addRecentfile (converter.getParent(),converter.getName());
        
        return;
    }

    /**
     * Action for menu item {@link #saveStudentInterfaceMenu}.
     */
	private void saveStudentInterfaceMenuActionPerformed() {
		String fileName = getController().getLauncher().getSessionId();
		if(fileName.endsWith(".swf"))
			fileName = fileName.substring(0, fileName.length()-4)+".sui";
		String dirName = SaveFileDialog.getBrdDirectoryToSuggest(getController());
		do {
			File chosenFile = DialogUtilities.chooseFile(dirName, fileName, new SuiFilter(),
					"Please set the file name", "Save", getController()); 
			if (chosenFile == null)
				return;
			if (chosenFile.exists()) {
	        	int overwrite =
	        			JOptionPane.showConfirmDialog(getController().getCtatFrameController().getDockedFrame(),
	        			"File "+chosenFile.getPath()+" already exists. Overwrite?",
	        			"Overwrite student interface", JOptionPane.YES_NO_CANCEL_OPTION,
	        			JOptionPane.WARNING_MESSAGE);
	        	if (overwrite == JOptionPane.CANCEL_OPTION)
	        		return;
	        	dirName = chosenFile.getParent();
	        	if (overwrite == JOptionPane.NO_OPTION)
	        		continue;
			}
			try {
				UniversalToolProxy utp = getController().getUniversalToolProxy();
				utp.saveStudentInterfaceFile(chosenFile);
			} catch (Exception e) {
				String errMsg = "Error saving student interface to "+chosenFile.getPath();
				trace.errStack(errMsg, e);
				Utils.showExceptionOccuredDialog(e, errMsg, "Error saving student interface");
			}
			return;
		} while (true);
	}

    /**
     * 
     */
    private void loadRulesMenuActionPerformed() 
    {
    	BR_Controller controller = getController();
        File fi = new File(SaveFileDialog.getProjectsDirectory(controller));
        
        if (!fi.exists())
            JOptionPane.showMessageDialog(getController().getActiveWindow(),
					  "No problem is defined.", "Notice",
					  JOptionPane.INFORMATION_MESSAGE);
        else 
        {
            new LoadProductionRulesDialog(controller);
        }
    }

    /**
     * 
     */
    private void saveGraphMenuActionPerformed()
    {
    	debug ("saveGraphMenuActionPerformed()");
    	BR_Controller controller = getController();
    	ProblemModel probMod = controller.getProblemModel();
    	trace.out("probMod is named " + probMod.getProblemName() + ", " + probMod.getProblemFullName());
    	

        if (probMod.getProblemFullName().length() == 0) 
        {
        	debug ("if A");

            if (probMod.getStartNode() == null) {
                JOptionPane.showMessageDialog(
		    		getServer().getActiveWindow(),
				       "There is currently no graph in the Behavior Recorder.",
				       "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            NodeView Vertex = probMod.getStartNode().getNodeView();
            SaveFileDialog.doDialog(controller, Vertex.getText());
            controller.updateStatusPanel(probMod.getProblemFullName());
            
            File converter=new File (controller.getProblemModel().getProblemFullName());
            
            addRecentfile (converter.getParent(),converter.getName());
            
            return;
        } 
        
        if ( controller.isShowTTSave() &&
        		!controller.getCtatFrameController().getDockedFrame().getTutorTypeLabel().equals(
    			controller.getCtatModeModel().getCurrentMode())) 
        {        
        	debug ("if B");
        	
        	JCheckBox checkbox = new JCheckBox("Do not show this message again.");
        	
    		Object[] params = {"If you save you will change the tutor type of this brd. Are you sure you want to save?", checkbox};
    		
    		int value = JOptionPane.showConfirmDialog(null, params, "Save Confirmation", JOptionPane.YES_NO_OPTION);
    		
    		boolean dontShow = checkbox.isSelected();
    		
    		if (trace.getDebugCode("eep")) trace.out("eep","saveBRDandNotifyTT:checkbox="+dontShow+",resp="+value);
    		
    		if (dontShow)
    			getController().setShowTTSave(false);
    		
    		if (value!=JOptionPane.YES_OPTION)
    			return;
    	}
        if (probMod.getProblemFullName() != null
		   && !probMod.getProblemFullName().equals(""))
		{
        	debug ("if C");

            controller.saveBRDSilently();

            
            File converter=new File (controller.getProblemModel().getProblemFullName());
            
            addRecentfile (converter.getParent(),converter.getName());
            
            return;
        }
        
        trace.out(5, this, "No file is saved.");

    }

    /**
     * 
     */
    public void applyPreferences() {
    	Boolean showActionLabels = getServer().getPreferencesModel().getBooleanValue("Action Labels");
        if (showActionLabels != null) {
            getController().setShowActionLabels(showActionLabels.booleanValue());
            trace.out ("action label menu = " + actionLabelsMenu);
            actionLabelsMenu.setSelected(showActionLabels.booleanValue());
        }
        
        Boolean showRuleLabels = getServer().getPreferencesModel().getBooleanValue("Rule Labels");
        if (showRuleLabels != null) {
            getController().setShowRuleLabels(showRuleLabels.booleanValue());
            ruleLabelsMenu.setSelected(showRuleLabels.booleanValue());
        }
        
       /* Boolean showCallbackFn = getServer().getPreferencesModel().getBooleanValue("Show Callback Functions");
        if (showCallbackFn != null) {
            getController().setShowCallbackFn(showCallbackFn.booleanValue());
            showCallbackFnMenu.setSelected(showCallbackFn.booleanValue());
        }*/
        
        
        Boolean showLastCogModelCheckLabels = getServer().getPreferencesModel().getBooleanValue("Last Cog. Model Check Labels");
        if (showLastCogModelCheckLabels != null) {
            getController().setPreCheckLISPLabelsFlag(showLastCogModelCheckLabels.booleanValue());
            lastCheckLISPLabelsMenu.setSelected(showLastCogModelCheckLabels.booleanValue());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated(WindowEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed(WindowEvent arg0) {
        trace.out(5, this, "window closed");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing(WindowEvent e) {
    	// CTAT1496: this method should now be a no-op, as we no longer
    	// register this listener on the student interface window.
    	// CTAT1506: THAT'S WRONG -- OpenInterfaceDialog registers it
    	if (e.getSource() == getController().getStudentInterface()) {
    		// if interface not a TutorWindow or BR graph is visible,
    		// call getController().closeCurrentInterface()
    		if (!(e.getSource() instanceof TutorWindow) ||
    				getController().getAuthorToolsVisible()) {
    			getController().closeStudentInterface();
    			enableInterfaceMenus(true);
    		}
    		enableOpenInterfaceMenu(Boolean.TRUE);
    	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated(WindowEvent arg0) 
    {

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified(WindowEvent arg0) 
    {
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified(WindowEvent arg0) 
    {
    	
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened(WindowEvent arg0) 
    {
    	
    }

    // Thu Oct 20 17:01:04 2005:: Noboru
    // Sim. St. needs to call this method to test a model hance got "public"
    public boolean checkValidGraph() 
    {
        if (getController().getProblemModel().getStartNode() == null) 
        {
            JOptionPane.showMessageDialog(getServer().getActiveWindow(),
					  "Please first create or load your graph.", "Warning",
					  JOptionPane.WARNING_MESSAGE);

            return false;
        }

        if (getController().getProblemModel().getProblemGraph().degree(getController().getProblemModel().getStartNode()) <= 0) 
        {
            JOptionPane.showMessageDialog(getServer().getActiveWindow(),
					  "Please create links to the graph.", "Warning",
					  JOptionPane.WARNING_MESSAGE);

            return false;
        }

        return true;
    }

    /* Collinl:  Basic accessor called by CtatFrame. */
    public JMenu getViewPanelMenu() 
    {
    	return viewPanelMenu;
    }
	
    /**
     * 
     * @return
     */
    private boolean currentCaseSensitivityProperty() 
    {
    	boolean property;
    	ProblemModel model = getController().getProblemModel();
    	
    	if (model != null) 
    	{
    		property = !(model.isCaseInsensitive());  // CTAT2051: added negation, since has inverse semantics
    	} 
    	else 
    	{
    		property = getServer().getPreferencesModel().getBooleanValue(BR_Controller.CASE_SENSITIVE).booleanValue();
    	}
    	
    	return property;
    }
	
    /**
     * 
     * @return
     */
    private boolean currentLockWidgetProperty() 
    {
    	boolean property;
    	ProblemModel model = getController().getProblemModel();
    	
    	if (model != null) 
    	{
    		property = model.isLockWidget();
    	} 
    	else 
    	{
    		property = getServer().getPreferencesModel().getBooleanValue(BR_Controller.LOCK_WIDGETS).booleanValue();
    	}
    	
    	return property;
    }
	
    /**
     * 
     * @return
     */
    private FeedbackEnum currentSuppressFeedbackProperty() 
    {
    	FeedbackEnum property;
    	ProblemModel model = getController().getProblemModel();
    	
    	if (model != null) 
    	{
    		property = model.getSuppressStudentFeedback();
    	} 
    	else 
    	{
    		property = (FeedbackEnum) getServer().getPreferencesModel().getEnumValue(BR_Controller.SUPPRESS_STUDENT_FEEDBACK);
    	}
    	
    	return property;
    }
	
    /**
     * 
     * @return
     */
    private HintPolicyEnum currentHintBiasProperty() 
    {
    	return getController().getProblemModel().getHintPolicy();
    }
    
    /**
     * 
     * @return
     */
    private boolean currentHighlightRightSelectionProperty() 
    {
    	boolean property;
    	
    	
    	ProblemModel model = getController().getProblemModel();
    	
    	if (model != null) 
    	{
    		property = model.getHighlightRightSelection();
    	} 
    	else 
    	{
    		property = getServer().getPreferencesModel().getBooleanValue(BR_Controller.HIGHLIGHT_RIGHT_WIDGET).booleanValue();
    	}
    	return property;
    }
    
    public void problemModelEventOccurred(ProblemModelEvent event) 
    {
    	ProblemModel pm = getController().getProblemModel();
    	problemModelEventOccurred(pm, event);
    }
	
    
    /**
     * (collinl) This code handles the ProblemModelListener 
     * requirements.  When run it just updates the state of 
     * the graph and does not check the event contents itself 
     * so this will not recurse on subevents as it does not 
     * need to.
     */
    public void problemModelEventOccurred(ProblemModel pm, ProblemModelEvent event) 
    {
    	//ProblemModel pm = getController().getProblemModel();
    	caseSensitiveGraphMenu.setSelected(!(pm.isCaseInsensitive()));
    	lockWidgetsGraphMenu.setSelected(pm.isLockWidget());
    	highlightRightSelectionGraphMenu.setSelected(pm.getHighlightRightSelection());

        FeedbackEnum currentFe = currentSuppressFeedbackProperty();
        
    	for (Component c: suppressFeedbackGraphMenu.getMenuComponents()) 
    	{
        	JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
			cbmi.setSelected(cbmi.getText().equals(currentFe.toString()));
		}
    	
    	HintPolicyEnum currentHbe = currentHintBiasProperty();
    	
    	for (Component c: hintPolicyGraphMenu.getMenuComponents()) 
    	{
        	JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
			cbmi.setSelected(cbmi.getText().equals(currentHbe.toString()));
		}
    	
    	confirmDoneGraphMenu.setSelected(pm.getEffectiveConfirmDone());
    }

    /**
     * External access to enable or disable the Open Interface menu. Modifies
     * argument by checking whether the student interface is part of this Java
     * program: that is, if
     * {@link BR_Controller#getRemoteProxy() controller#getSocketProxy()} is
     * is not null, the menu item will remain disabled.
     * @param b true to enable, false to disable; 
     */
	public void enableOpenInterfaceMenu(Boolean b) 
	{
		boolean enable = (b != null ? b.booleanValue() : true);
		if(trace.getDebugCode("menu"))
			trace.printStack("menu", String.format("enableOpenInterfaceMenu b %s, tabMgr %s, ctlr %s, SUI local %b, enable %b",
					b, getServer().getTabManager(), getController(), getController().isStudentInterfaceLocal(), enable));
		enable &= (getServer().getTabManager() != null &&
				getController() != null &&
				getController().isStudentInterfaceLocal()); 
		openInterfaceMenu.setEnabled(enable);
	}
	
 	private DockingWindow lastRestored = null;
 
 	private JSeparator dynamicWindowSeparator;
 	public void windowRestored(DockingWindow window) {
 		if (window == lastRestored)
 		{
 			return;
 		}
 		String title = window.getTitle();
 		JMenu vm = getViewPanelMenu();
 		if (this.dynamicViewCount <= 0 && !this.dynamicViewHasClosed)
 		{
 			//vm.addSeparator();
 			this.dynamicWindowSeparator = new JSeparator();
 			vm.add(this.dynamicWindowSeparator);
 			dynamicWindowMap = new HashMap<JMenuItem, DockingWindow>();
 		}
 		JMenuItem mitem = new JMenuItem(title);
 		mitem.setActionCommand(DYNAMIC_WINDOW_COMMAND);
 		dynamicWindowMap.put(mitem, window);
 		mitem.addActionListener(this);
 		vm.add(mitem);
 		dynamicViewCount++;
 		lastRestored = window;
 	}
	private DockingWindow lastClosed = null;
	public void windowClosed(DockingWindow window) {
		
		if (window == lastClosed)
		{
			return;
		}
		if (!dynamicViewHasClosed)
		{
			dynamicViewHasClosed = true;
		}
		if (dynamicWindowMap != null)
		{
			JMenuItem closedMenuItem = null;
			for (Entry<JMenuItem, DockingWindow> ent : dynamicWindowMap.entrySet())
			{
				if (ent.getValue() == window)
				{
					closedMenuItem = ent.getKey();
					break;
				}
			}
			if (closedMenuItem != null)
			{
				dynamicWindowMap.remove(closedMenuItem);
				getViewPanelMenu().remove(closedMenuItem);
				lastClosed = window;
				this.dynamicViewCount--;
				if (this.dynamicViewCount <= 0)
				{
					getViewPanelMenu().remove(this.dynamicWindowSeparator);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	private void fillRecentMenu ()
	{			
		debug ("fillRecentMenu ()");

		String data=getServer().getPreferencesModel ().getStringValue ("RecentFiles");
		
		if (data==null)
		{
			debug ("We don't have any recent file structure on disk yet");
			return;
		}
		
		debug ("Recent files: " + data);
		
		openRecentGraphMenu.removeAll();
		
		String [] lines=data.split("!");
		
		if (lines.length>0)
			openRecentGraphMenu.setEnabled(true);		
				
		for (int i=0;i<lines.length;i++)
		{
			debug ("Processing recent file item: " + lines [i]);
			
			String [] splitter=lines [i].split("\\|");
			
			if (splitter.length>1)
			{							
				debug ("Path: " + splitter [0] + " file: " + splitter [1]);
			
				CTATFileItem aFileItem=new CTATFileItem ();
				aFileItem.setDirectory(splitter [0]);
				aFileItem.setFileName(splitter [1]);
					
				JMenuItem menuItem=new JMenuItem((i+1) + " " + aFileItem.getFileName());
			
				aFileItem.setVisual(menuItem);
			
				menuItem.setToolTipText(aFileItem.getDirectory()+aFileItem.getFileName()); 
			
				openRecentGraphMenu.add(menuItem);
				recentFiles.add(aFileItem);
			
				menuItem.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e) 
					{
						JMenuItem targetItem=(JMenuItem) e.getSource();
	        			        		        	
						loadRecent (targetItem);
					}
				});
			}
			else
				debug ("Error: entry in recent file list is corrupted!");
		}					
	}
	/**
	 * 
	 */
	public void addRecentfile (String aPath, String aFile)
	{
		debug ("addRecentfile ("+aPath+","+aFile+")");
		
		if (aFile.isEmpty()==true)
		{
			debug ("Info: the provided file name is empty");
			return;
		}
				
		CTATFileItem aFileItem=new CTATFileItem ();
		aFileItem.setDirectory(aPath+File.separator);
		aFileItem.setFileName(aFile);

		if (hasDuplicate (aFileItem)==true)
		{
			return;
		}
		
		storeRecentFile (aFileItem);
		
		recentFiles.add (0,aFileItem);
		
		if(recentFiles.size()>maxRecentFiles)
		{
			recentFiles.remove(recentFiles.size()-1);
		}
		
		populateRecentFilesMenu ();
	}
	/**
	 * 
	 */
	private Boolean hasDuplicate (CTATFileItem anItem)
	{
		for (int i=0;i<recentFiles.size();i++)
		{
			CTATFileItem testItem=recentFiles.get(i);
			
			if (
					(testItem.getDirectory().equals(anItem.getDirectory())==true) &&
					(testItem.getFileName().equals(anItem.getFileName())==true)
				)	
				return (true);
		}
		
		return (false);
	}

	/**
	 * @param anItem
	 */
	private void loadRecent (JMenuItem anItem)
	{
		debug ("loadRecent ("+recentFiles.size()+")");
		
		for (int i=0;i<recentFiles.size();i++)
		{
			CTATFileItem testItem=recentFiles.get(i);
			
			if (testItem.getVisual()==null)
			{
				debug ("Internal error: recent file item doesn't have a corresponding menu item!");
			}
			else
			{												
				if (testItem.getVisual()==anItem)
				{
			    	ProblemModel pm = getController().getProblemModel();			    											
					debug ("Opening: " + testItem.getDirectory() + testItem.getFileName());				
					LoadFileDialog.doLoadBRDFile(getServer(), testItem.getFileName(), testItem.getDirectory(), false);					
					recentFiles.remove(testItem);					
					recentFiles.add(0,testItem);					
					storeRecentFiles ();					
					populateRecentFilesMenu ();					
			    	pm = getController().getProblemModel();    // could have new instance after load
			    	
			    	if (pm != null && pm.getStartNode() != null)  // if actually loaded a graph
			    	{
			    		enableCreateStartStateMenus(false);
			    		enableGotoStartStateMenus(true);
			    		enablePrintGraphMenus(true);
			    		enableSaveGraphMenus(true);
			    	}
					
					return;
				}
			}	
		}
	}
	/**
	 * 
	 */
	private void storeRecentFile (CTATFileItem aFileItem)
	{		
		StringBuffer recentFileBuffer=new StringBuffer ();
		
		recentFileBuffer.append(aFileItem.getDirectory() + "|" + aFileItem.getFileName());
		
		for (int j=0;j<recentFiles.size();j++)
		{
			recentFileBuffer.append("!");
			
			CTATFileItem testFileItem=recentFiles.get(j);
			
			recentFileBuffer.append(testFileItem.getDirectory() + "|" + testFileItem.getFileName());
		}
		
        getServer().getPreferencesModel ().setStringValue ("RecentFiles",recentFileBuffer.toString());
        getServer().getPreferencesModel ().saveToDisk ();
	}		
	/**
	 * 
	 */
	private void storeRecentFiles ()
	{		
		StringBuffer recentFileBuffer=new StringBuffer ();
				
		for (int j=0;j<recentFiles.size();j++)
		{
			if (j>0)
				recentFileBuffer.append("!");
			
			CTATFileItem testFileItem=recentFiles.get(j);
			
			recentFileBuffer.append(testFileItem.getDirectory() + "|" + testFileItem.getFileName());
		}
		
        getServer().getPreferencesModel ().setStringValue ("RecentFiles",recentFileBuffer.toString());
        getServer().getPreferencesModel ().saveToDisk ();
	}
	/**
	 * 
	 */
	private void populateRecentFilesMenu ()
	{
		debug ("populateRecentFilesMenu ()");
		
		openRecentGraphMenu.removeAll();
		
		if (recentFiles.size()>0)
			openRecentGraphMenu.setEnabled(true);
		
		for (int j=0;j<recentFiles.size();j++)
		{
			CTATFileItem aFileItem = recentFiles.get(j);
			
			JMenuItem menuItem=new JMenuItem((j+1) + " " + aFileItem.getFileName());
			
			aFileItem.setVisual(menuItem);
			
			menuItem.setToolTipText(aFileItem.getDirectory()+aFileItem.getFileName()); 
			
			openRecentGraphMenu.add(menuItem);
			
			menuItem.addActionListener(new ActionListener()
	        {
	        	public void actionPerformed(ActionEvent e) 
	        	{
	        		JMenuItem targetItem=(JMenuItem) e.getSource();
	        			        		        	
	        		loadRecent (targetItem);
	        	}
		    });
		}
	}
	
	private CTAT_Launcher getServer() {
		return this.server;
	}
	
	private BR_Controller getController() {
		return getServer().getFocusedController();
	}
	
	public void refreshGraphDependentItems() {
		//trace.out("mg", "CtatMenuBar (refreshGraphDependentItems): on tab " + getController().getTabNumber());
		
		final BR_Controller controller = getController();
		final ProblemModel model = controller.getProblemModel();
		
		// Edit
		JAbstractUndoPacket undoPacket = controller.getUndoPacket();
		undoMenu.setAction(undoPacket.getUndoAction());
		redoMenu.setAction(undoPacket.getRedoAction());
		
		// View
		this.actionLabelsMenu.setEnabled(true);
		this.actionLabelsMenu.setSelected(controller.getShowActionLabels());
		this.ruleLabelsMenu.setEnabled(true);
		this.ruleLabelsMenu.setSelected(controller.getShowRuleLabels());
		this.lastCheckLISPLabelsMenu.setEnabled(true);
		this.lastCheckLISPLabelsMenu.setSelected(controller.isPreCheckLISPLabelsFlag());
		
		copyLinksMenu.setEnabled(hasLinks());
		pasteLinksMenu.setEnabled(controller.getProblemModelManager().nSelectedLinks() > 0);
		
		// Edit -> Copy Links
		for(ActionListener al : this.copyLinksMenu.getActionListeners()) {
			this.copyLinksMenu.removeActionListener(al);
		}
		//trace.out("mg", "CtatMenuBar (refreshGraphDependentItems): adding listener to copyLinksMenu");
		this.copyLinksMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) 
        	{
        		trace.out("mg", "CtatMenuBar (copyLinksMenu.actionPerformed): HERE");
        		if(controller.copySelectedLinks() > 0)
        			pasteLinksMenu.setEnabled(true);
        	}
        });
		
		// Edit -> Paste Links
		for(ActionListener al : this.pasteLinksMenu.getActionListeners()) {
			this.pasteLinksMenu.removeActionListener(al);
		}
		pasteLinksMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) 
        	{
        		trace.out("mg", "CtatMenuBar (pasteLinksMenu.actionPerformed): HERE");
        		controller.pasteLinks();
        		copyLinksMenu.setEnabled(hasLinks());
        		//Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, "Paste Links");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
        });
		
		
		// Graph
		
		// Graph -> Case sensitivity
		this.caseSensitiveGraphMenu.setSelected(model.isCaseInsensitive());
		for(ActionListener al : caseSensitiveGraphMenu.getActionListeners()) {
			this.caseSensitiveGraphMenu.removeActionListener(al);
		}
        caseSensitiveGraphMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) 
        	{
        		model.setCaseInsensitive(!model.isCaseInsensitive());
        		//Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, "Set Case "+ (model.isCaseInsensitive() ? "Ins" : "S")+"ensitive Matching");
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
        });
        
        // Graph -> Lock widgets
		this.lockWidgetsGraphMenu.setSelected(controller.getProblemModel().getLockWidget());
		for(ActionListener al : lockWidgetsGraphMenu.getActionListeners()) {
			this.lockWidgetsGraphMenu.removeActionListener(al);
		}
        lockWidgetsGraphMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) 
        	{
        		model.setLockWidget(!model.isLockWidget());
        		//Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, (model.isLockWidget() ? "Lock" : "Unlock")+ " Widgets on Correct Action");
        		getController().getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
	    });
        
        // Graph -> Feedback policy
        int numFeedbackOptions = this.suppressFeedbackGraphMenu.getItemCount();
        for(int i = 0; i < numFeedbackOptions; i++) {
        	JCheckBoxMenuItem cb = (JCheckBoxMenuItem)this.suppressFeedbackGraphMenu.getItem(i); 
        	cb.setSelected(cb.getText().equals(currentSuppressFeedbackProperty().toString()));
        	for(ActionListener al : cb.getActionListeners()) {
        		cb.removeActionListener(al);
        	}
    		cb.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			FeedbackEnum oldSetting = model.getSuppressStudentFeedback();
        			FeedbackEnum choice =
        				FeedbackEnum.fromString(e.getActionCommand());
        			model.setSuppressStudentFeedback(choice);
        			for (Component c: suppressFeedbackGraphMenu.getMenuComponents()) {
        	        	JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
        				cbmi.setSelected(cbmi.getText().equals(e.getActionCommand()));
        			}
            		//Undo checkpoint
        			if (oldSetting == null || !oldSetting.equals(choice)) {
        				ActionEvent ae = new ActionEvent(this, 0, "Set Feedback Policy "+choice.toString());
        				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        			}
        		}
        	});
        	
        }

        // Graph -> Hint policy 
        int numHintPolicies = this.hintPolicyGraphMenu.getItemCount();
        for(int i = 0; i < numHintPolicies; i++) {
        	JCheckBoxMenuItem cb = (JCheckBoxMenuItem)this.hintPolicyGraphMenu.getItem(i);
        	cb.setSelected(cb.getText().equals(currentHintBiasProperty().toString()));
        	for(ActionListener al : cb.getActionListeners()) {
        		cb.removeActionListener(al);
        	}
        	cb.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			HintPolicyEnum oldSetting = model.getHintPolicy();
        			HintPolicyEnum choice = HintPolicyEnum.fromString(e.getActionCommand());
        			model.setHintPolicy(choice);
        			for (Component c: hintPolicyGraphMenu.getMenuComponents()) {
        	        	JCheckBoxMenuItem cbmi = (JCheckBoxMenuItem) c;
        				cbmi.setSelected(cbmi.getText().equals(e.getActionCommand()));
        			}
            		//Undo checkpoint
        			if (oldSetting == null || !oldSetting.equals(choice)) {
        				ActionEvent ae = new ActionEvent(this, 0, "Set Hint Policy "+choice.toString());
        				controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        			}
        		}
        	});
        }

        // Graph -> Confirm done 
        this.confirmDoneGraphMenu.setSelected(model.getEffectiveConfirmDone());
        for(ActionListener al : this.confirmDoneGraphMenu.getActionListeners()) {
        	this.confirmDoneGraphMenu.removeActionListener(al);
        }
        this.confirmDoneGraphMenu.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
    		    Boolean oldSetting = model.getConfirmDone();
    		    Boolean newSetting = new Boolean(confirmDoneGraphMenu.isSelected());
        		model.setConfirmDone(newSetting);
        		//Undo checkpoint
        		if (oldSetting == null || !oldSetting.equals(newSetting)) {
        			ActionEvent ae = new ActionEvent(this, 0, "Set Confirm Done "+model.getConfirmDone());
        			controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        		}
        	}
	    });
        
        // Graph -> Highlight right selection 
        this.highlightRightSelectionGraphMenu.setSelected(model.getHighlightRightSelection());
        for(ActionListener al : this.highlightRightSelectionGraphMenu.getActionListeners()) {
        	this.highlightRightSelectionGraphMenu.removeActionListener(al);
        }
        this.highlightRightSelectionGraphMenu.addActionListener(new ActionListener() 
        {
        	public void actionPerformed(ActionEvent e) 
        	{
        		model.setHighlightRightSelection(!model.getHighlightRightSelection());
        		// Undo checkpoint
        		ActionEvent ae = new ActionEvent(this, 0, "Set Highlight Right Selection "+model.getHighlightRightSelection());
        		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        	}
	    });
        
        cleanupStartStateMenu.setEnabled(graphHasCleanUpToDo());
        updateUnmatchedComponents();
	}

	/**
	 * @return true if any links are currently defined.
	 */
	private boolean hasLinks() {
		BR_Controller ctlr = getController();
		ProblemModel pm = (ctlr == null ? null : ctlr.getProblemModel());
		if(pm != null && pm.getProblemGraph() != null)
			return pm.getProblemGraph().hasEdges();
		else
			return false;
	}


	/**
	 * Calls {@link #enableMenuIfInterfaceConnected(JMenuItem)} on interface state change.
	 * @param e
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent e) {
		enableMenuIfInterfaceConnected(startStateSettingsMenu);
		updateUnmatchedComponents();
		if(!(e.getSource() instanceof UniversalToolProxy))
			return;
		UniversalToolProxy utp = (UniversalToolProxy) e.getSource();
		if(utp.getStudentInterfaceConnectionStatus().isConnected()
				&& UniversalToolProxy.FLASH.equals(utp.getStudentInterfacePlatform())) {
			utp.removeStartStateListener(this);  // ensure we aren't added more than once
			utp.addStartStateListener(this);
		} else
			saveStudentInterfaceMenu.setEnabled(false);
	}

	/**
	 * Update the enabled/disabled status of {@link #showUnmatchedComponentsMenu}. Also call
	 * {@link CtatFrame#updateUnmatchedSelections(boolean)}.
	 * @return true if the dialog should be enabled
	 */
	private boolean updateUnmatchedComponents() {
		UniversalToolProxy utp = getController().getUniversalToolProxy();
		if(utp == null)
			return false;
		boolean enable = utp.enableObsoleteSeletionDialog();
		showUnmatchedComponentsMenu.setEnabled(enable);
		getServer().getCtatFrameController().getDockedFrame().updateUnmatchedSelections(enable);
		return enable;
	}

	/**
	 * Enable {@link #saveStudentInterfaceMenu}, because the interface descriptions have arrived.
	 * @param evt
	 * @see edu.cmu.pact.ctat.model.StartStateModel.Listener#startStateReceived(java.util.EventObject)
	 */
	public void startStateReceived(EventObject evt) {
		saveStudentInterfaceMenu.setEnabled(true);
	}
}
