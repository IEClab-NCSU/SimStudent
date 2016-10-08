/**
 * Describe class MissConsole here.
 *
 *
 * Created: Tue May 03 16:12:12 2005
 *
 * @author <a href="mailto:Noboru.Matsuda@cs.cmu.edu">Noboru Matsuda</a>
 * @version 1.0
 */

package edu.cmu.pact.miss.console.view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.console.controller.MissController;
import edu.cmu.pact.miss.console.controller.MissMouseHandler;

public class MissConsole extends JRootPane {

    //-
    //- Class Fields - - - - - - - - - - - - - - - - - - - - 
    //- 

    // Window Name and Version infor.
    private final String toolName = "Simulated Student Console";
    private final String version = "Ver. 1.00";
    String getToolName() {
        return toolName;
    }
    String getVersion() {
        return version;
    }

    // The base window size
    private final int WIDTH = 550;
    private final int HEIGHT = 400;
    // Ratio of contents size to the base window size
    private final double MARGIN_RATE = 0.95;

    // Message Area size
    private final double MA_RATE = 0.2;
    private final int MA_WIDTH = (int)(WIDTH * MARGIN_RATE);
    private final int MA_HEIGHT = (int)(HEIGHT * MARGIN_RATE * MA_RATE);
    private final int MA_TOP_MARGIN=20;

    // Task Pane size
    private final int TP_WIDTH = (int)(WIDTH * MARGIN_RATE);
    private final int TP_HEIGHT = (int)((HEIGHT - MA_HEIGHT)*MARGIN_RATE);
    //problem pane sizes
    private final int PROBLEM_WIDTH=(int)(TP_WIDTH/2);
    private final int PROBLEM_HEIGHT=TP_HEIGHT;
    private final int BK_WIDTH=(int)(TP_WIDTH/2);
    private final int BK_HEIGHT=TP_HEIGHT;
    
    // Content Pane
    //
    /*
    private JPanel contentPane;
    JPanel getContentPane() { return this.contentPane; }
    void setContentPane( JPanel contentPane ) {
	this.contentPane = contentPane;
    }
    */

    // Task Pane
    // 
    private JTabbedPane taskPane;
    JTabbedPane getTaskPane() { return this.taskPane; };
    void setTaskPane( JTabbedPane taskPane ) {
	this.taskPane = taskPane;
    }

    // Title strings for tabs in the task pane 
    private final String PROBLEM_TP_NAME = "Problems";
    private final String CURRENT_DEMO_NAME = "Current Demo.";
    private final String BK_TP_NAME = "Background Knowledge";

    // A list of problem
    private JList problemList = new JList();
    private Vector problemListVector = new Vector();
    private JList getProblemList() { return this.problemList; }
    public void addProblemList( String problem ) {
	problemListVector.add( problem );
	problemList.setListData( problemListVector );
    }
    /*
    void setProblemList( JTextArea problemList ) {
	this.problemList = problemList;
    }
    */

    // Problem Information
    /*
    private final String NPB_LABEL = "New Problem";
    */

    // A list of Skill Names
    private JList skillNameList = new JList();
    private JList getSkillNameList() { return this.skillNameList; }
    public void updateSkillNameList( Vector /* String */ skillNames ) {
	this.skillNameList.setListData( skillNames );
    }
    /*
    void setSkillNameList( JTextArea skillNameList ) {
	this.skillNameList = skillNameList;
    }
    */

    // "Background Knowledge" tab
    private final String WME_TYPE_FILE_LABEL = "File name for WME Types:";
    private final String INIT_WME_FILE_LABEL = "File name for initial WMEs:";
    private final String NOT_GIVEN = "Not given";

    private JLabel wmeTypeFileLabel = new JLabel();
    private JLabel getWmeTypeFileLabel() { return this.wmeTypeFileLabel; }
    public void setWmeTypeFileLabel( String text ) {
	this.wmeTypeFileLabel.setText( text );
    }

    private JLabel initWmeFileLabel = new JLabel();
    private JLabel getInitWmeFileLabel() { return this.initWmeFileLabel; }
    public void setInitWmeFileLabel( String text ) {
	this.initWmeFileLabel.setText( text );
    }

    // List of Feature predicate symbols
    private JList featureList = new JList();
    private JList getFeatureList() { return this.featureList; }

    // List of Operator symbols
    private JList operatorList = new JList();
    private JList getOperatorList() { return this.operatorList; }

    // "Current Demo." tab
    // 
    private JLabel currentProblemName = new JLabel( "N/A" );
    private JLabel getCurrentProblemName() { return this.currentProblemName; }
    public void setCurrentProblemName( String name ) {
	this.currentProblemName.setText( name );
    }
    private JLabel numStepsDemonstrated = new JLabel( "N/A" );
    public JLabel getNumStepsDemonstrated() { return numStepsDemonstrated; }
    public void setNumStepsDemonstrated( int n ) {
	this.numStepsDemonstrated.setText( "" + n );
    }
    private JLabel numProductionRules = new JLabel( "N/A" );
    public JLabel getNumProductionRules() { return numProductionRules; }
    public void setNumProductionRules( int n ) {
	this.numProductionRules.setText( "" + n );
    }
    private JLabel percentStepsModelTraced = new JLabel( "N/A" );
    private JLabel getPercentStepsModelTraced() {
	return percentStepsModelTraced;
    }
    private void setPercentStepsModelTraced( double n ) {
	this.percentStepsModelTraced.setText( "" + n );
    }
    
    // Message Area
    // 
    private JTextArea messageArea = new JTextArea();
    JTextArea getMessageArea() { return this.messageArea; }
    void setMessageArea( JTextArea messageArea ) {
	this.messageArea = messageArea;
    }

    // Miss Controller
    private MissController missController;
    private MissController getMissController() { return this.missController; }
    private void setMissController( MissController missController ) {
	this.missController = missController;
    }

    //-
    //- Constructor - - - - - - - - - - - - - - - - - - - - -
    //- 

    /**
     * Creates a new <code>MissConsole</code> instance.
     *
     */
    public MissConsole( MissController missController,
			BR_Controller brController ) {


        setMissController( missController );

        // Initialize GUI components
        setNativeLookAndFeel();
        setName( getToolName() );

        initComponents();

        // Display default Init WME file name if available
        if ( getMissController().getSimStInitStateFile() != null ) {
            setInitWmeFileLabel( getMissController().getSimStInitStateFile() );
        }
        // Display default WME Type file name, if availabe
        if ( getMissController().getSimStWmeTypeFile() != null ) {
            setWmeTypeFileLabel( getMissController().getSimStWmeTypeFile() );
        }

    }
    
    private void setNativeLookAndFeel() {
        // Get the native look and feel class name
        String nativeLF = UIManager.getSystemLookAndFeelClassName();

        // Install the native look and feel
        try {
            UIManager.setLookAndFeel(nativeLF);
        } catch (Exception e) {
            e.printStackTrace();
    		getMissController().getLogger().simStLogException(e);
        }
    }

    // Line up Swing GUI components
    private void initComponents() {

        // Set the top level pane
        setContentPane( new JPanel() );
        BoxLayout layout = new BoxLayout( getContentPane(), BoxLayout.Y_AXIS );
        getContentPane().setLayout( layout );

        setSize( WIDTH, HEIGHT );

        //This used to set up the Miss menus, which now has been moved to CTAT menu SimStudent.
        //setupMenu();
        
        setGuiComponents();

    }

    private JMenuItem missHibernationMenu =
	new JMenuItem(MissController.HIBERNATE_SS);
    public JMenuItem getMissHibernationMenu() {
	return this.missHibernationMenu;
    }

    // Set up a menu bar
    private void setupMenu() {

	// Obtain Miss Controller object that is in charge
	MissController missController = getMissController();
	MissMouseHandler mouseListener = new MissMouseHandler(missController);

	// Obtain a key mask
	/*
	int keyMask;
	if ( (System.getProperty("os.name").toUpperCase()).startsWith("MAC") )
	    keyMask = ActionEvent.META_MASK;
	else
	    keyMask = ActionEvent.CTRL_MASK;
	*/

	// The menu bar
	JMenuBar menuBar = new JMenuBar();

	// "File" menu
	JMenu fileMenu = new JMenu( MissController.FILE_MENU );
	fileMenu.addMouseListener( mouseListener );
	
	menuBar.add( fileMenu );
	
	String menuItems[] = { MissController.NEW_PROBLEM,
			       null,
			       MissController.SAVE_INSTRUCTIONS,
			       MissController.LOAD_INSTRUCTIONS,
			       null,
			       MissController.LOAD_WME_TYPE,
			       MissController.INIT_WME,
			       MissController.LOAD_PREDICATE,
			       MissController.LOAD_OPERATOR };
	// Added 6/7/07 - Reid Van Lehn <rvanlehn@mit.edu>
	// Provides ToolTip implementation for usabiity. Text corresponds
	// to entries above. Add help text here.
	String toolTipText[] = { "This doesn't do anything currently.",
					         null,
					         "Save results of current demonstrations.",
					         "Load results of previous demonstrations.",
					         null,
					         "Load WME types from specific file.",
					         "Load initial WME definitions from file.",
					         "Load predicate symbols from file.",
					         "Load operator symbols from file." }; 
	setupMenuItems( fileMenu, menuItems, toolTipText, mouseListener );

	// "Prod.System" menu
	JMenu prodSysemMenu = new JMenu( MissController.PROD_SYS_MENU );
	prodSysemMenu.addMouseListener( mouseListener );

	menuBar.add( prodSysemMenu );

	String prodSysMenuItems[] = { MissController.TEST_MODEL_ON };
	setupMenuItems( prodSysemMenu, prodSysMenuItems, null, mouseListener );

	// "Debugging" menu
	JMenu debugMenu = new JMenu( MissController.DEBUG_MENU );

	menuBar.add( debugMenu );

	getMissHibernationMenu().addActionListener( mouseListener );
	debugMenu.add( getMissHibernationMenu() );

	// Add and display the menu bar
	setJMenuBar( menuBar );
    }

    // Add menu items to a menu
    // Modified 6/7/06 to include tooltips
    private void setupMenuItems( JMenu jMenu, String[] menuItems,
    			String[] toolTips, MissMouseHandler mouseListener ) {

	for (int i = 0; i < menuItems.length; i++) {
	    
	    if ( menuItems[i] != null ) {
		JMenuItem menuItem = new JMenuItem( menuItems[i] );
		if (toolTips != null)
			menuItem.setToolTipText(toolTips[i]);
		menuItem.addActionListener( mouseListener );
		jMenu.add( menuItem );
	    } else {
		jMenu.addSeparator();
	    }
	}
    }

    // Set up a GUI components
    private void setGuiComponents() {

        // Task Pane (TabbedPane)
        // 
        setTaskPane( new JTabbedPane() );
        // "Problems" tab
        JComponent problemTaskPane = makeProblemTaskPane();
        getTaskPane().addTab( PROBLEM_TP_NAME, problemTaskPane );
        // "Current Demonstration" tab
        JComponent currentDemoPane = makeCurrentDemonstrationPane();
        getTaskPane().addTab( CURRENT_DEMO_NAME, currentDemoPane );
        // "Background Knowledge" tab
        JComponent bkTaskPane = makeBkTaskPane();
        getTaskPane().addTab( BK_TP_NAME, bkTaskPane );
        // Add the task pane to the content pane
        getContentPane().add( getTaskPane() );

        // Message Area
        // 
        Dimension size = new Dimension( MA_WIDTH, MA_HEIGHT );
        getMessageArea().setPreferredSize( size );
        getMessageArea().setEditable(false);//ajz 5-24-06 prevent editing the message area
        // Add the message area to the content pane
        getContentPane().add( getMessageArea() );
    }
    
    // Problem Task Pane, which consists of (i) a list of problems,
    // (ii) a problem information, and (iii) a list of skill names.
    private JComponent makeProblemTaskPane() {

	JPanel problemTaskPane = new JPanel();

	// Problem List Area
	Dimension problemListSize=new Dimension(PROBLEM_WIDTH,PROBLEM_HEIGHT);
	JPanel problemListPanel = new JPanel();
	BoxLayout layout = new BoxLayout( problemListPanel, BoxLayout.Y_AXIS );
	problemListPanel.setLayout( layout );
	problemListPanel.setPreferredSize(problemListSize);
	problemListPanel.add( new JLabel( "List of Problems" ) );
	makeProblemList();
	problemListPanel.add( new JScrollPane( getProblemList() ) );

	/*
	// Problem Information Area
	JPanel problemInfoPanel = new JPanel();
	layout = new BoxLayout( problemInfoPanel, BoxLayout.Y_AXIS );
	problemInfoPanel.setLayout( layout );
	JButton newProblemButton = makeNewProblemButton();
	problemInfoPanel.add( newProblemButton );
	*/

	
	problemTaskPane.add( problemListPanel );
	// problemTaskPane.add( problemInfoPanel );
	
	// Skill Names
	JPanel snPane = new JPanel();
	Dimension skillListSize=new Dimension(PROBLEM_WIDTH,PROBLEM_HEIGHT);
	snPane.setLayout( new BoxLayout( snPane, BoxLayout.Y_AXIS ) );
	snPane.add( new JLabel( "Skill Names:" ) );
	// Add the list of Skill Name 
	JScrollPane jsp = new JScrollPane( getSkillNameList() );
	snPane.add( jsp );
	snPane.setPreferredSize(skillListSize);
	problemTaskPane.add( snPane );

	

	return problemTaskPane;
    }

    private void makeProblemList() {

	/*
	int width = (int)((TP_WIDTH / 3) * MARGIN_RATE);
	int height = (int)(TP_HEIGHT * 0.85);
	Dimension size = new Dimension( width, height );
	getProblemList().setPreferredSize( size );
	*/
    }

    private void makeSkillNameList() {
	// *****
    }

    /*
    private JButton makeNewProblemButton() {

	JButton newProblemButton = new JButton( NPB_LABEL );
	return newProblemButton;
    }
    */

    // "Current Demo." tab has two panes, (1) a set of text fields
    // showing statistis of the learning on the current problem and
    // (2) a Skill Names
    private JComponent makeCurrentDemonstrationPane() {

	JPanel currentDemoPane = new JPanel();

	// Background color of this tab
	Color bColor = new Color(151,224,228);

	// Statistics of learning on the current problem
	JPanel statPane = new JPanel();
	statPane.setBackground( bColor );
	statPane.setLayout( new BoxLayout( statPane, BoxLayout.Y_AXIS ) );
	// Current Problem Name:
	JPanel cpPane = new JPanel();
	// cpPane.setLayout( new BoxLayout( cpPane, BoxLayout.Y_AXIS ) );
	cpPane.add( new JLabel( "Currrent Problem: " ) );
	cpPane.add( getCurrentProblemName() );
	statPane.add( cpPane );
	// Num of steps demonstrated:
	JPanel sdPane = new JPanel();
	// sdPane.setLayout( new BoxLayout( sdPane, BoxLayout.Y_AXIS ) );
	sdPane.add( new JLabel( "Num steps demonstrated: " ) );
	sdPane.add( getNumStepsDemonstrated() );
	statPane.add( sdPane );
	// Num of production rules:
	JPanel prPane = new JPanel();
	// prPane.setLayout( new BoxLayout( prPane, BoxLayout.Y_AXIS ) );
	prPane.add( new JLabel( "Num production rules: " ) );
	prPane.add( getNumProductionRules() );
	statPane.add( prPane );
	// % steps model traced:
	JPanel mtPane = new JPanel();
	// mtPane.setLayout( new BoxLayout( mtPane, BoxLayout.Y_AXIS ) );
	mtPane.add( new JLabel( "% steps model traced: " ) );
	mtPane.add( getPercentStepsModelTraced() );
	statPane.add( mtPane );
	//	 Add statPane
	currentDemoPane.add( statPane );
	return currentDemoPane;

    }

    // Setting up a Background Knowledge tab
    // 
    private JComponent makeBkTaskPane() {

	int listWidth = (int)((TP_WIDTH / 3 ) * MARGIN_RATE );
	int listHeight = (int)(TP_HEIGHT * 0.85 );
	

	// This tab has a top part (wmeFilePane) and a bottom part
	// (symbolPane)
	JPanel bkTaskPane = new JPanel();
	bkTaskPane.setBackground( Color.cyan );
	BoxLayout bkLayout = new BoxLayout( bkTaskPane, BoxLayout.Y_AXIS );
	bkTaskPane.setLayout( bkLayout );

	// wmeFilePane: shows file names for WME types and Initial
	// WMEs from to p to bottom
	JPanel wmeFilePane = new JPanel();
	BoxLayout wfLayout = new BoxLayout( wmeFilePane, BoxLayout.Y_AXIS );
	wmeFilePane.setLayout( wfLayout );
	// WME Type definition file
	JPanel wmeTypeLabelPane = new JPanel();
	JLabel wmeTypeLabel = new JLabel( WME_TYPE_FILE_LABEL );
	wmeTypeLabelPane.add(wmeTypeLabel);
	setWmeTypeFileLabel( NOT_GIVEN );
	wmeTypeLabelPane.add( getWmeTypeFileLabel() );
	wmeFilePane.add(wmeTypeLabelPane);
	// Initial WMEs definition file
	JPanel initWmeLabelPane = new JPanel();
	JLabel initWmeLabel = new JLabel( INIT_WME_FILE_LABEL );
	initWmeLabelPane.add( initWmeLabel );
	setInitWmeFileLabel( NOT_GIVEN );
	initWmeLabelPane.add( getInitWmeFileLabel() );
	wmeFilePane.add( initWmeLabelPane );

	// symbolPane: shows a list of Feature Predicates and a list
	// of Operators side by side
	JPanel symbolPane = new JPanel();
	Dimension bkListSize=new Dimension(BK_WIDTH,BK_HEIGHT);
	// A list of feature symbols
	JPanel featureListPane = new JPanel();
	featureListPane.setBackground( Color.red );
	BoxLayout flLayout = new BoxLayout(featureListPane, BoxLayout.Y_AXIS);
	featureListPane.setLayout( flLayout );
	featureListPane.add( new JLabel( "Features:" ) );
	// The list object
	int lsm = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
	getFeatureList().setSelectionMode( lsm );
	MissMouseHandler ml = new MissMouseHandler( getMissController() );
	getFeatureList().addListSelectionListener( ml );
	// A scroll pane object
	JScrollPane jsp1 = new JScrollPane( getFeatureList() );
	jsp1.setPreferredSize( bkListSize );
	featureListPane.add( jsp1 );
	
	// A list of operator symbols
	JPanel opListPane = new JPanel();
	opListPane.setBackground( Color.pink );
	BoxLayout olLayout = new BoxLayout( opListPane, BoxLayout.Y_AXIS );
	opListPane.setLayout( olLayout );
	opListPane.add( new JLabel( "Operators:" ) );
	// The list object
	getOperatorList().setSelectionMode( lsm );
	getOperatorList().addListSelectionListener( ml );
	// A scroll pane object
	JScrollPane jsp2 = new JScrollPane( getOperatorList() );
	jsp2.setPreferredSize( bkListSize );
	opListPane.add( jsp2 );
	

	// Put the list objects
	symbolPane.add( featureListPane );
	symbolPane.add( opListPane );

	// Put things all together
	bkTaskPane.add( wmeFilePane );
	bkTaskPane.add( symbolPane );

	return bkTaskPane;
    }

    //-
    //- Methods - - - - - - - - - - - - - - - - - - - - - - - 
    //- 

    /**
     * Display a specified text message 
     *
     * @param text to be displayed
     */
    public void message( String text ) {
	this.messageArea.setText( text );
    }

    /**
     * Clear contents of the message area
     *
     */
    public void clearMessage() {
	this.messageArea.setText( "" );
    }

    /**
     * Display a list of predicate symbols into the featureList 
     *
     * @param predicates a <code>Vector</code> value
     */
    public void displayPredicates( Vector /* String */ predicates ) {
	getFeatureList().setListData( predicates );
    }

    /**
     * Display a list of operator symbols into the operatorList
     *
     * @param operators a <code>Vector</code> value
     */
    public void displayOperators( Vector /* String */ operators ) {
	getOperatorList().setListData( operators );
    }

    /**
     * Triggered by Sim. St. itself when its hibernation status
     * (isMissHibernating) gets changed.  
     *
     * @param state a <code>boolean</code> value
     **/
    public void switchMissHibernationMenu( boolean status ) {

	getMissHibernationMenu().setText( status ?
					  MissController.WAKEUP_SS :
					  MissController.HIBERNATE_SS );

    }
}

//
// end of MissConsole.java
// 
