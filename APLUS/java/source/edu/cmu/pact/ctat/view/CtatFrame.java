/*
 * Created on Jun 22, 2006
 *
 */
package edu.cmu.pact.ctat.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
//import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import pact.CommWidgets.UniversalToolProxy;

import net.infonode.docking.View;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Dialogs.UnmatchedSelectionsDialog;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.CtatMenuBar;
import edu.cmu.pact.ctatview.CtatModePanel;
import edu.cmu.pact.ctatview.DockManager;

public class CtatFrame extends AbstractCtatWindow 
{
	private static final long serialVersionUID = -91588009557616193L;
	
	private static final String EMPTY_PROBLEM = "empty";

	private CtatModePanel modeSwitchPanel;
    
    private CtatMenuBar ctatMenuBar;
        
    //private JPanel statusPane;
    
    private JLabel orderStatusLabel;
    
    private JButton obsoleteSelectionButton;
    
    private JLabel currentStateLabel;
    
    private JLabel problemNameLabel;
    
    private JLabel interpStatusLabel;

	private JLabel graphTutorTypeLabel;
	
	private final CTAT_Launcher server;
	
	private Map<Integer, JCheckBoxMenuItem> itemMap;
    
    
    public CtatFrame(CTAT_Launcher server) {
    	super(server);
    	this.server = server;
    	this.itemMap = new HashMap<Integer, JCheckBoxMenuItem>();
    	initUI(server);
    }
    
    public CtatModePanel getCtatModePanel() 
    {
    	return modeSwitchPanel;
    }
    
    private void initUI(CTAT_Launcher server) 
    {
    	modeSwitchPanel = new CtatModePanel(server);
    	JPanel panel = new JPanel(new BorderLayout());
    	panel.add(modeSwitchPanel, BorderLayout.NORTH);
	
    	getContentPane().setLayout(new BorderLayout());
    	getContentPane().add(panel, BorderLayout.NORTH);
	
    	setSize(800, 800);
	
    	setLocationRelativeTo(null);
    	setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	
    	ctatMenuBar = getServer().getCtatMenuBar();
    	modeSwitchPanel.addAuthorModeListener(ctatMenuBar);
    	setJMenuBar(ctatMenuBar.getMenuBar());
	
    	orderStatusLabel = createStatusBarLabel("orderStatusLabel",
    			"select Windows -> Show Window -> Group Editor -> right click 'Top level' to change the graph order mode");
    	
    	obsoleteSelectionButton = UnmatchedSelectionsDialog.getDefaultButton();
    	obsoleteSelectionButton.setBorder(new SBLBorder(BevelBorder.LOWERED));
    	Dimension osld = obsoleteSelectionButton.getPreferredSize();
    	if(osld.height < orderStatusLabel.getPreferredSize().height)
    		osld.height = orderStatusLabel.getPreferredSize().height+2;  // +2: seems needed to center icon in border
    	osld.width += 6;                                                 // +6: seems needed to center icon in border
    	obsoleteSelectionButton.setPreferredSize(osld);
    	obsoleteSelectionButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent evt) {
    			UniversalToolProxy utp = null;
    			if(getServer().getFocusedController() == null ||
    					(utp = getServer().getFocusedController().getUniversalToolProxy()) == null)
    				return;
    			UnmatchedSelectionsDialog launcher = null;
    			boolean hasDialog = false;
    			if(utp == null ||
    					(launcher = utp.getUnmatchedSelectionsDialogLauncher()) == null ||
    					(!(hasDialog = utp.getUnmatchedSelectionsDialogLauncher().hasDialog()))) {
    				trace.err("CtatFrame.obsoleteSelectionButton listener cannot invoke dialog"+
    					" utp "+trace.nh(utp)+", launcher "+trace.nh(launcher)+", hasDialog "+hasDialog);
    				return;
    			}
    			launcher.launch();
    		}
    	});
	
    	graphTutorTypeLabel = createStatusBarLabel("graphTutorLabel", "Behavior Graph Type");
    	
    	currentStateLabel = createStatusBarLabel("currentStateLabel",
    			"Current state in the graph");
    	
    	problemNameLabel = createStatusBarLabel("problemStatusLabel",  // keep name unchanged for automated tests 
    			"select File->'Open Graph' to load the problem");
	
    	interpStatusLabel = createStatusBarLabel("groupStatusLabel",
    			"Number of interpretations in the example tracer");

    	JPanel statusPanel = new JPanel(new GridBagLayout());
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.gridy = 0;
    	gbc.fill = GridBagConstraints.HORIZONTAL;
    	gbc.anchor = GridBagConstraints.LINE_START;
    	
    	gbc.weightx = 0.08; statusPanel.add(orderStatusLabel, gbc);
    	gbc.weightx = 0.12; statusPanel.add(currentStateLabel, gbc);
    	gbc.weightx = 0;    statusPanel.add(obsoleteSelectionButton, gbc);  // fixed size
    	gbc.weightx = 0.40; statusPanel.add(problemNameLabel, gbc);
    	gbc.weightx = 0.15; statusPanel.add(graphTutorTypeLabel, gbc);
    	gbc.weightx = 0.25; statusPanel.add(interpStatusLabel, gbc);
		
		getContentPane().add(statusPanel, BorderLayout.PAGE_END);
    }
    
    /**
     * A class to give some space to the left and right of label text within a
     * {@link BevelBorder}. See {@link CtatFrame#createStatusBarLabel(String, String)}.
     */
	class SBLBorder extends BevelBorder {
		private static final long serialVersionUID = -1513321908788736005L;
		
		SBLBorder(int bevelType) { super(bevelType); }
		public Insets getBorderInsets(Component c) { return new Insets(2,4,2,4); }
		public Insets getBorderInsets(Component c, Insets insets) {
			Insets ins = getBorderInsets(c);
			insets.set(ins.top, ins.left, ins.bottom, ins.right);
			return insets;
		}    		
	};

    /**
     * Setup common to all labels on the status bar.
     * @param name for {@link JLabel#setName(String)}
     * @param toolTipText for {@link JLabel#setToolTipText(String)}
     * @return new JLabel, initialized
     */
    private JLabel createStatusBarLabel(String name, String toolTipText) 
    {
    	JLabel label = new JLabel();
    	label.setName(name);
    	label.createToolTip();
    	label.setToolTipText(toolTipText);
    	label.setBorder(new SBLBorder(BevelBorder.LOWERED));
//    	label.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    	label.setOpaque(true);
    	label.setVisible(true);
    	//label.setBackground(new Color(220, 220, 220));
    	label.setFont(new Font("Dialog", 1, 10));
		return label;
	}

	public void setVisible(boolean visible) 
    {
    	if (trace.getDebugCode("br")) trace.out("br", "setVisible("+visible+")");
    		super.setVisible(visible);
    }
    
    /* Collinl: This call is used by DockManager.java to add views to
     * the underlying Windows->Show Windows submenu.  
     */
    public void addView(final View view, final int viewID) 
    {
    	final DockManager dm = this.server.getDockManager();
    	JMenu viewMenu = ctatMenuBar.getViewPanelMenu();
    	String itemName = view.getTitle(); // + (dm.isGraphView(view) ? (" [" + EMPTY_PROBLEM + "]") : "");
    	final JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(itemName);
		viewMenu.add(menuItem);
		this.itemMap.put(Integer.valueOf(viewID), menuItem);
		
    	menuItem.addActionListener(new ActionListener() 
    	{
    		public void actionPerformed(ActionEvent e) 
    		{	
    			dm.setViewVisibility(viewID, menuItem.isSelected());
    		}
	    });
    }
    
    /**
     * Updates the problem name of the given view in the Window menu.
     * @param viewID			The ID of the graph's view
     * @param problemName		The problem name to set the graph to, <code>null</code>
     * 							if the problem is empty
     */
    public void updateGraphMenuItem(int viewID, String problemName) {
    	String problemText;
    	if(problemName != null && problemName.length() > 0) {
    		problemText = problemName;
    	}
    	else {
    		problemText = EMPTY_PROBLEM;
    	}
    	JCheckBoxMenuItem menuItem = this.itemMap.get(Integer.valueOf(viewID));
    	String itemName = menuItem.getText();
    	String itemNamePrefix = itemName.substring(0, itemName.indexOf("[", 0));
    	menuItem.setText(itemNamePrefix + "[" + problemText + "]");
    }
    
    /**
     * Marks a given view as displayed or hidden in the Windows menu.
     * @param viewID		The integer ID with which the view is initialized
     * @param visible		true if the window should be shown; false if hidden
     */
    public void setViewVisibilityMarker(int viewID, boolean visible) {
		//trace.out("mg", "CtatFrame (setViewVisibilityMarker): view " + viewID
		//		+ " now " + (visible ? "(x)" : "( )"));	
    	this.itemMap.get(Integer.valueOf(viewID)).setSelected(visible);
    }
    
    /**
     * @param viewID
     * @return				true if tracking the visibility of the view with
     * 						the given ID; false otherwise
     */
    public boolean isTracked(int viewID) {
    	return (this.itemMap.get(Integer.valueOf(viewID)) != null);
    }
    
    /**
     * @param viewID		The integer ID with which the view is initialized
     * @return				true if the corresponding window is shown;
     * 						false otherwise
     */
    public boolean isWindowVisible(int viewID) {
    	JCheckBoxMenuItem item = this.itemMap.get(Integer.valueOf(viewID));
    	if(item == null) return false;
    	return item.isSelected();
    }
    
    /**
     * @return Returns the main menu of the CTAT tools.
     */
    public CtatMenuBar getCtatMenuBar() 
    {
    	return ctatMenuBar;
    }
    
    /**
     * Show graph preference information on status label
     */    
    public void setProblemStatusToolTip(String FullProblemName) 
    {
    	problemNameLabel.setToolTipText(FullProblemName);
    }
    
    public String getOrderStatusLabel() 
    {
    	return orderStatusLabel.getText();
    }
    
    public void setOrderStatusLabel(String orderStatusText) 
    {
    	orderStatusLabel.setText(orderStatusText);
    }
    
    public String getCurrentStateLabel() 
    {
    	return currentStateLabel.getText();
    }
    
    public void setCurrentStateLabel(String currentStateText) 
    {
    	if (currentStateText == null)
    		currentStateLabel.setText("No current state");
    	else
    		currentStateLabel.setText(currentStateText);
    }
    
    public String getProblemNameLabel() 
    {
    	return problemNameLabel.getText();
    }
    
    public void setProblemNameLabel(String problemNameText) 
    {
    	problemNameLabel.setText(problemNameText);
    }
    
    public String getTutorTypeLabel() 
    {
    	String s = graphTutorTypeLabel.getText();
    	
    	if (s == null || s.equals("No graph has been opened yet"))
    		return null;
    	
    	return s;
    }
    
    public void setTutorTypeLabel(String tutorTypeText) 
    {
    	graphTutorTypeLabel.setText(tutorTypeText);
    }
    
    public String getInterpStatusLabel() 
    {
    	return interpStatusLabel.getText();
    }
    
    public void setInterpStatusLabel(String interpStatusText) 
    {
    	interpStatusLabel.setText(interpStatusText);
    }
    
    private CTAT_Launcher getServer() {
    	return this.server;
    }

	public void updateUnmatchedSelections(boolean available) {
		UnmatchedSelectionsDialog.updateDialogAvailableButton(available, obsoleteSelectionButton);
	}
}
