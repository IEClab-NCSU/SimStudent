package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommWidget;
//import sun.tools.tree.ThisExpression;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.JTabbedPaneWithCloseIcons;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.SimStTutalk;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.MetaTutor.APlusHintDialog;
import edu.cmu.pact.miss.MetaTutor.APlusHintDialogInterface;
import edu.cmu.pact.miss.MetaTutor.MetaTutorAvatarComponent;
import edu.cmu.pact.miss.PeerLearning.GameShow.ProblemBankTableModel;
import edu.cmu.pact.miss.PeerLearning.SimStPLE.UndoThread;
import edu.cmu.pact.miss.jess.WorkingMemoryConstants;

import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class SimStPeerTutoringPlatform extends JComponent {

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // BR_Controller
    BR_Controller brController = null;
    
    // A flag showing if the platform is running as an Applet
    boolean runningAsApplication = false;
    
    // SimSt Peer Learning Environment control module
    SimStPLE simStPLE = null;

    // Title label showing "Prepare Lucky for Quiz Level 1!"
    //JLabel titleLabel = null;

    // Student Interface authored by CTAT 
    JComponent studentInterface = null;
    
    JComponent metaTutorComponent = null;
    
    public JComponent getMetaTutorComponent() {
		return metaTutorComponent;
	}

	public void setMetaTutorComponent(JComponent metaTutorComponent) {
		this.metaTutorComponent = metaTutorComponent;
	}
	
	public String trigger_msg;
	//public JButton OK = new JButton("OK");
	public boolean is_ok = false;
	public void showMetaTutorTrigger(final String msg, final String ruleNickName, final SimStLogger logger) {
		trigger_msg = msg;
		is_ok = false;
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				JPopupMenu menu = new JPopupMenu();
				menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
				
				JLabel label1 = new JLabel(MetaTutorAvatarComponent.MR_WILLIAMS_SAYS_MSG);
				label1.setBackground(Color.gray);
				label1.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
				menu.add(label1);
				menu.add(new JSeparator());
				
				JLabel label2 = new JLabel(trigger_msg);
				label2.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
				label2.setBackground(Color.white);
				label2.setOpaque( true );
				menu.add(label2);
				menu.add(new JSeparator());
				
				JButton OK = new JButton("OK");
				OK.setFont(new Font(Font.DIALOG, Font.BOLD, 10));
				OK.setBackground(Color.white);
				OK.setOpaque( true );
				//OK.setLayout(new FlowLayout());
				OK.addActionListener( new ActionListener()
				{
				    @Override
				    public void actionPerformed(ActionEvent e)
				    {
				    	is_ok = true;
				    	//System.out.println("Do Something Clicked");
				    }
				});
				menu.add(OK, BorderLayout.CENTER);
				logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR, SimStLogger.METATUTOR_HINT_TRIGGER_ACTION, msg);
				while (is_ok == false) {
					menu.show(metaTutorComponent, 0 , -((int)menu.getPreferredSize().getHeight()));	
					menu.setVisible(true);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				menu.setVisible(false);
	    			
	    	}
		};
		
		runnable.run();
		
    }

	public MouseListener[] setMetaTutorComponentEnabled(boolean isEnabled, MouseListener[] mListener) {
		
		if(!isEnabled) {
			MouseListener[] ml = metaTutorComponent.getMouseListeners();
			for(int i=0; i < ml.length; i++) {
				metaTutorComponent.removeMouseListener(ml[i]);
			}
			metaTutorComponent.setEnabled(isEnabled); // Disabling a component does not prevent it from receiving MouseEvents
			metaTutorComponent.validate();
			return ml;
		} else if(isEnabled) {
			
			for(int i=0; i < mListener.length; i++) {
				metaTutorComponent.addMouseListener(mListener[i]);
			}
			metaTutorComponent.setEnabled(isEnabled); // Disabling a component does not prevent it from receiving MouseEvents
			metaTutorComponent.validate();
			return null;
		}
		return null;
	}
	
	APlusHintDialogInterface aPlusHintDialogInterface;
	
	public APlusHintDialogInterface getAPlusHintDialogInterface() {
		return aPlusHintDialogInterface;
	}

	public void setAPlusHintDialogInterface(
			APlusHintDialogInterface aPlusHintDialogInterface) {
		this.aPlusHintDialogInterface = aPlusHintDialogInterface;
	}

	//Tabbed pane to show student interface and example problems
    JTabbedPaneWithCloseIcons tabPane = null;

    public JTabbedPaneWithCloseIcons getTabPane() {
		return tabPane;
	}

    public void setTabPane(JTabbedPaneWithCloseIcons tabPane) {
		this.tabPane = tabPane;
	}

    private String targetWindow = "";
    private String previousWindow = "";
    
	// Size of the top-panel window
    Dimension platformSize = null;
    
    // SimStudent avator Icon
    //JLayeredPane layeredIcon = null;
    StudentAvatarDisplay layeredIcon = null;
    JLabel simStAvatorIcon = null;
    final int simStAvatorIconWidth = 200; 
    final int simStAvatorIconHeight = 200;
    final int medalWidth = 37;
    final int medalHeight = 50;
    final int trophyHeight = 70;
    int medals, trophies = 0;
    int totalWidth = 0;
    Dimension simStAvatorIconSize = new Dimension(simStAvatorIconWidth, simStAvatorIconHeight);
    
    JPanel simStAvatorPanel = null;
    JPanel metaTutorAvatorPanel = null;
    JPanel simStAvatarSpeechPanel = null;
    JLayeredPane medalPane = null;
    JPanel speechPanel = null;
    JTextArea speechText = null;
    JScrollPane speechScroll = null;
    JPanel yesNoPanel = null;
    JButton yesResponseButton = null;
    JButton noResponseButton = null;
    //JTextField textResponse = null;
    JComboBox textResponse = null;
    JButton textResponseSubmitButton = null;
    
    // Buttons at the bottom of the platform
    JButton nextProblemButton = null;
    JButton quizButton = null;
    JButton curriculumBrowserButton = null;
    JButton exampleButton = null;
    JButton undoButton = null;
    JButton restartButton = null;
    
    // SimStudent name label
    JLabel simStNameLabel = null;
    
    //Jinyu
    // Quiz Results TabbedPane
    JTabbedPane resultsPane = new JTabbedPane();
    JTabbedPane sectionResultsPane;
   	int quizNumber = 1;
   	JFrame quizResultFrame = new JFrame("Quiz Results");
   	JProgressBar quizProgress = new JProgressBar(0,100);

   	public JPanel getSimStAvatarPanel(){
   		return simStAvatorPanel;
   	}
   	
    //
    // GUI components
    // 
    // Top-level Container needed when running as an application
    JFrame topLevelFrame;

    // Window size
    Dimension windowSize = new Dimension(800, 900);
    
    //Example window size
    Dimension examplePanelSize = new Dimension(500,400);
    public Dimension getExamplePanelSize(){ return examplePanelSize; }
    
    // Container
    JDesktopPane desktop;
    
    public static final String WAIT_MESSAGE = "   Please Wait While Your Previous Work is Loaded";
	JProgressBar progressBar = new JProgressBar(0,100);
	

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
	
	public SimStPeerTutoringPlatform()
	{
		
	}
	
    public SimStPeerTutoringPlatform(JComponent tutorPanel, TutorController brController) {
        BR_Controller temp = (BR_Controller)brController;
        setBrController(temp);
                
        //this.setFont(new Font("SansSerif", Font.PLAIN, 13));
        SimStPLE.setComponentFont(tutorPanel, new Font("Serif", Font.PLAIN, 16));
        /*UIManager.getLookAndFeelDefaults().put("TextArea.font", getFont());
        UIManager.getLookAndFeelDefaults().put("Label.font", getFont());
        UIManager.getLookAndFeelDefaults().put("TextField.font", getFont());
        UIManager.getLookAndFeelDefaults().put("TextPane.font", getFont());
        UIManager.getLookAndFeelDefaults().put("FormattedTextField.font", getFont());
        UIManager.getLookAndFeelDefaults().put("Viewport.font", getFont());
        UIManager.getLookAndFeelDefaults().put("TabbedPane.font", getFont());
        UIManager.getLookAndFeelDefaults().put("Button.font", getFont());*/

        quizResultFrame.setLayout(new BorderLayout());
        quizProgress.setStringPainted(true);
        quizResultFrame.add(quizProgress, BorderLayout.NORTH);
        
        Dimension tutorPanelPreferredSize = tutorPanel.getPreferredSize();
        Dimension tutorPanelSize = tutorPanel.getSize();
        if(trace.getDebugCode("miss"))trace.out("miss", "SimStPeerTutoringPlatform: tutorPanelPreferredSize = " + tutorPanelPreferredSize);
        if(trace.getDebugCode("miss"))trace.out("miss", "SimStPeerTutoringPlatform: tutorPanelSize = " + tutorPanelSize);
        
        setSimStPLE(new SimStPLE(temp, this));
        
        // Reading a desk-top screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(trace.getDebugCode("miss"))trace.out("miss", "screenSize = " + screenSize);
        // The size of this platform is 2*(width of the tutorPanel) x (height of the screen)
        int width = tutorPanelPreferredSize.width * 2 + 100;
        setPlatformSize(new Dimension(width, screenSize.height));
        this.setPreferredSize(getPlatformSize());
        if(trace.getDebugCode("miss"))trace.out("miss", "platformSize = " + getPlatformSize());
        // This tutoring platform is BoxLayout, top to bottom
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Button row (the bottom row) 
        setNextProblemButton(new JButton(getSimStPLE().getNextProblemButtonTitleString()));
        setQuizButton(new JButton(getSimStPLE().getQuizButtonTitleString()));
        setCurriculumBrowserButton(new JButton(getSimStPLE().getCurriculumBrowserButtonTitleString()));
        setExampleButton(new JButton(getSimStPLE().getShowExampleButtonTitleString()));
        setUndoButton(new JButton(getSimStPLE().getUndoButtonTitleString()));
        setRestartButton(new JButton(getSimStPLE().getRestartButtonTitleString()));
        
        
        ActionListener actionListener = new SimStPLEActionListener(getBrController());
        
        getNextProblemButton().setActionCommand(SimStPLE.NEXT_PROBLEM);
        getNextProblemButton().addActionListener(actionListener);
        getQuizButton().setActionCommand(SimStPLE.QUIZ);
        getQuizButton().addActionListener(actionListener);
        getCurriculumBrowserButton().setActionCommand(SimStPLE.CURRICULUM_BROWSER);
        getCurriculumBrowserButton().addActionListener(actionListener);
        getExampleButton().setActionCommand(SimStPLE.EXAMPLES);
        getExampleButton().addActionListener(actionListener);
        getUndoButton().setActionCommand(SimStPLE.UNDO);
        getUndoButton().addActionListener(actionListener);
        getRestartButton().setActionCommand(SimStPLE.RESTART);
        getRestartButton().addActionListener(actionListener);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.GREEN);
        buttonPanel.add(getNextProblemButton());
        buttonPanel.add(getQuizButton());
        buttonPanel.add(getUndoButton());
        buttonPanel.add(getRestartButton());
        //buttonPanel.add(getCurriculumBrowserButton());
        //buttonPanel.add(getExampleButton());
        
        if(trace.getDebugCode("miss"))trace.out("miss", "buttonPanel size = " + buttonPanel.getSize());

        int buttonHeight = getNextProblemButton().getHeight();
        if (buttonHeight < getQuizButton().getHeight()) 
            buttonHeight = getQuizButton().getHeight();
        if (buttonHeight < getCurriculumBrowserButton().getHeight()) 
            buttonHeight = getCurriculumBrowserButton().getHeight();
        if (buttonHeight < getExampleButton().getHeight()) 
            buttonHeight = getExampleButton().getHeight();
        if(trace.getDebugCode("miss"))trace.out("miss", "buttonHeight = " + buttonHeight);
        if(trace.getDebugCode("miss"))trace.out("miss", "buttonBounds = " + getNextProblemButton().getBounds());
        
        int buttonWidth = getNextProblemButton().getWidth();
        if (buttonWidth < getQuizButton().getWidth())
            buttonWidth = getQuizButton().getWidth();
        if (buttonWidth < getCurriculumBrowserButton().getWidth())
            buttonWidth = getCurriculumBrowserButton().getWidth();
        if (buttonWidth < getExampleButton().getWidth())
            buttonWidth = getExampleButton().getWidth();
        if(trace.getDebugCode("miss"))trace.out("miss", "buttonWidth= " + buttonWidth);
        
        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);
        // getQuizButton().setPreferredSize(buttonSize);
        // getNextProblemButton().setPreferredSize(buttonSize);
        
        Dimension buttonPanelSize = new Dimension(getPlatformSize().width, buttonHeight + 20);
        // buttonPanel.setPreferredSize(buttonPanelSize);
        
        // The "status" area, showing the current level
        //setTitleLabel(new JLabel());
        //getTitleLabel().setText(getSimStPLE().genCurrentLevelTitleString());
        
        // SimStudent avator
        // setSimStAvatorIcon(new JLabel("SimSt Image"));
    	//setSimStAvatorIcon(new JLabel(SimStPLE.STUDENT_IMAGE));
        /*String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+SimStPLE.STUDENT_IMAGE;
    	ClassLoader cl = this.getClass().getClassLoader();
    	Icon stacyIcon = new ImageIcon(cl.getResource(file));*/

        setSimStAvatorIcon(new JLabel());
        //setSimStAvatorIcon(new JLabel(new ImageIcon("Test.gif")));
        getSimStAvatorIcon().setPreferredSize(getSimStAvatorIconSize());
        getSimStAvatorIcon().setBackground(Color.YELLOW);
        
        setSpeechPanel(new JPanel());
        getSpeechPanel().setBorder(BorderFactory.createEtchedBorder());
        getSpeechPanel().setBackground(Color.white);
        getSpeechPanel().setLayout(new BorderLayout());
        
        setSimStNameLabel(new JLabel(getSimStPLE().getSimStName(), JLabel.CENTER));
        getSimStNameLabel().setFont(new Font("SansSerif", Font.PLAIN, 18));
        
        totalWidth = (int)getPlatformSize().getWidth();
        int aiWidth = (int)getSimStAvatorIconSize().width;
        int aiHeight = (int)getSimStAvatorIconSize().height + getSimStNameLabel().getHeight();
        
        simStAvatorPanel = new JPanel();
        simStAvatorPanel.setLayout(new BoxLayout(simStAvatorPanel, BoxLayout.Y_AXIS));
        simStAvatorPanel.setPreferredSize(new Dimension(aiWidth, aiHeight*2));
        
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.setPreferredSize(new Dimension(aiWidth, 30));
        
        //panel for quiz awards
        medalPane = new JLayeredPane();
        medalPane.setLayout(null);
        medalPane.setPreferredSize(new Dimension(aiWidth, simStAvatorIconHeight/2 - 30));
        
  
    	
        //Add simStudent image and name to panel
        setUpLayeredIcon();
        setImage(SimStPLE.STUDENT_IMAGE);
        namePanel.add(getSimStNameLabel(), BorderLayout.NORTH);
        simStAvatorPanel.add(layeredIcon);
        simStAvatorPanel.add(namePanel); 
        simStAvatorPanel.add(medalPane);
        
        
        simStAvatarSpeechPanel = new JPanel();
        simStAvatarSpeechPanel.setLayout(new BoxLayout(simStAvatarSpeechPanel,BoxLayout.X_AXIS));
        getSpeechPanel().setPreferredSize(new Dimension((int)(2.5*aiWidth),aiHeight/2));
        getSpeechPanel().setMinimumSize(new Dimension(aiWidth,aiHeight/3));
        getSpeechPanel().setMaximumSize(new Dimension((int)(2.5*aiWidth),2*aiHeight/3));
        
        
        simStAvatarSpeechPanel.setPreferredSize(new Dimension(totalWidth,aiHeight*2));
        simStAvatarSpeechPanel.add(simStAvatorPanel);
        
        JPanel commPanel = new JPanel();
        yesNoPanel = new JPanel();
        setYesResponseButton(new JButton("Yes"));
        setNoResponseButton(new JButton("No"));
        yesNoPanel.setLayout(new BoxLayout(yesNoPanel,BoxLayout.X_AXIS));
        //int yesNoWidth = getYesResponseButton().getPreferredSize().width + getNoResponseButton().getPreferredSize().width;
        int yesNoWidth = getYesResponseButton().getPreferredSize().width ;
        //int yesNoHeight = getYesResponseButton().getPreferredSize().height;
        int yesNoHeight = getYesResponseButton().getPreferredSize().height + getNoResponseButton().getPreferredSize().height;
        yesNoPanel.setPreferredSize(new Dimension(yesNoWidth, yesNoHeight));
        //yesNoPanel.setMaximumSize(new Dimension(yesNoWidth*2,yesNoHeight*2));
        yesNoPanel.add(getYesResponseButton());
        yesNoPanel.add(getNoResponseButton());
        
        //setTextResponse(new JTextField());
        setTextResponse(new JComboBox());
        getTextResponse().setMaximumSize(new Dimension(500,40));

        setTextResponseSubmitButton(new JButton("Submit"));
        getTextResponseSubmitButton().setMaximumSize(new Dimension(75,40));
        
        setSpeechText(new JTextArea());
        getSpeechText().setFont(new Font("Monospace", Font.PLAIN,12));
        getSpeechText().setLineWrap(true);
        getSpeechText().setEditable(false);
        
        speechScroll = new JScrollPane(getSpeechText());
        speechScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        //getSpeechText().setFont(new Font(getSpeechText().getFont().getFontName(),Font.PLAIN,18));
        getSpeechPanel().setLayout(new BoxLayout(getSpeechPanel(),BoxLayout.Y_AXIS));
        
        //getSpeechPanel().add(getSpeechText(), BorderLayout.NORTH);
        getSpeechPanel().add(speechScroll, BorderLayout.NORTH);
        
        JPanel tmpPanel = new JPanel();
        tmpPanel.setLayout(new BoxLayout(tmpPanel,BoxLayout.X_AXIS));

        tmpPanel.add(getTextResponse());
        tmpPanel.add(getTextResponseSubmitButton());
        getSpeechPanel().add(tmpPanel);
        //getSpeechPanel().add(getTextResponse(), BorderLayout.CENTER);
        //getSpeechPanel().add(getTextResponseSubmitButton(), BorderLayout.SOUTH);
        
        //getSpeechPanel().add(yesNoPanel,BorderLayout.SOUTH);
        showButtons(false);
        showTextResponse(false);
                
        
        //commPanel.setLayout(new BoxLayout(commPanel,BoxLayout.Y_AXIS));
        commPanel.setLayout(null);
        commPanel.setPreferredSize(new Dimension((int)(2.75*aiWidth), aiHeight));
        getSpeechPanel().setBounds(0, 5, (int)(2.5*aiWidth), 2*aiHeight/3);
        yesNoPanel.setBounds(0, 2*aiHeight/3+5, (int)(2.5*aiWidth), aiHeight/3);
        commPanel.add(getSpeechPanel(), BorderLayout.NORTH);
        commPanel.add(yesNoPanel,BorderLayout.SOUTH);
                
        simStAvatarSpeechPanel.add(commPanel);
        if(simStPLE.getSimSt().isSsMetaTutorMode()){
        	metaTutorComponent = new MetaTutorAvatarComponent(SimStPLE.METATUTOR_IMAGE, getBrController().
        			getMissController().getSimSt());
        	metaTutorComponent.setPreferredSize(new Dimension(aiWidth, aiHeight*2));
            simStAvatarSpeechPanel.add(metaTutorComponent);
            
            aPlusHintDialogInterface = new APlusHintDialog(new Frame(),simStPLE.getMissController().
            		getAPlusHintMessagesManager(), simStPLE.logger, metaTutorComponent);
        }
        
        // The top row containing Student Interface
        setStudentInterface(tutorPanel);

        int tpWidth = getPlatformSize().width;
        int tpHeight = getPlatformSize().height;
        if(trace.getDebugCode("miss"))trace.out("miss", "tpHeight = " + tpHeight);
        //tpHeight -= getTitleLabel().getHeight();
        if(trace.getDebugCode("miss"))trace.out("miss", "tpHeight = " + tpHeight);
        tpHeight -= simStAvatorPanel.getHeight();
        if(trace.getDebugCode("miss"))trace.out("miss", "tpHeight = " + tpHeight);
        tpHeight -= buttonPanel.getHeight();
        if(trace.getDebugCode("miss"))trace.out("miss", "tpHeight = " + tpHeight);
        
        
        tabPane = new JTabbedPaneWithCloseIcons(this);
        JPanel topPanel = new JPanel();
        examplePanelSize = new Dimension(tpWidth, tpHeight);
        topPanel.setPreferredSize(getExamplePanelSize());
        topPanel.setLayout(new FlowLayout());
        topPanel.add(tutorPanel);
        tabPane.insertTab(simStPLE.getSimStName(), null, topPanel, "", 0);
        ChangeListener changeListener = new SimStPLEActionListener(getBrController());
        tabPane.addChangeListener(changeListener);
        //topPanel.add(getCurriculumBrowser(); - CB now separate window
        
        // Add the Title Label first
       // this.add(getTitleLabel());
        // The top row contains a StudentInterface
        this.add(tabPane);
        // The 2nd row contains a SimSt avator
        this.add(simStAvatarSpeechPanel);
        //The 3rd row contains quiz medals
        //this.add(medalPanel);
        // The Button panel
        this.add(buttonPanel);

        getSimStPLE().generateProblemBankTab();
        getSimStPLE().setUpOverviewTab();
        getSimStPLE().showExamples();
        getSimStPLE().setUpVideoTab();
        
        if (!getSimStPLE().getMissController().getSimSt().isSsCogTutorMode())
        	getSimStPLE().removeComponentColor(getStudentInterface());
        
        this.requestFocus();
        
        simStAvatorPanel.setMinimumSize(new Dimension((int)(aiWidth*1.1),(int)(aiWidth*1.1)));
        simStAvatorPanel.setMaximumSize(new Dimension(aiWidth*2,aiWidth*2));
        
    }

    

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    void initApplicationGUI(JComponent studentInterface) {
        
        setRunningAsApplication(true);
        setTopLevelFrame(new JFrame());
        getTopLevelFrame().getContentPane().add(studentInterface);
    }
    
    //Add medals to panel in simStudent tab
    public void showMedals(boolean show)
    {   
        if (medalPane != null)
        {
            //medalPanel = new JPanel();
            //medalPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            //medalPanel.setPreferredSize(new Dimension(totalWidth, 50));
            //medalPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
            
            //clear current awards
            medalPane.removeAll();
            int offset = medalWidth;
            if ((simStAvatorIconWidth-medalWidth) < (medalWidth)*(2*trophies+medals-1))        
                offset = (simStAvatorIconWidth - medalWidth)/(2*trophies+medals-1);
            
            //display trophies, twice as wide as medals
            for (int j = 1; j <= trophies; j++)
            {
                JLabel t = new JLabel(createImageIcon("img/trophy" + j + ".png"));
                t.setBounds((j-1)*offset*2,0,2*medalWidth,trophyHeight);
                medalPane.add(t, new Integer(j));
            }
            //display medals
            for (int k = 1; k <= medals; k++)
            {
                JLabel m = new JLabel(createImageIcon("img/medal" + k + ".png"));
                m.setBounds((2*trophies+k-1)*offset,trophyHeight-medalHeight,medalWidth,medalHeight);
                medalPane.add(m, new Integer(k+trophies));
            }
            //medalPane.setBackground(simStAvatorPanel.getBackground());
            medalPane.validate();
            //set visible to false first just to prevent jpane bugginess from 
            //leaving images that should have been deleted.
            medalPane.setVisible(false);
            medalPane.setVisible(show);
        }
    }
    
    public int getMedalCount()
    {
        return medals;
    }
    
    //Change medal count
    public void augmentMedals(int medals, boolean show)
    {
        this.medals += medals;
        showMedals(show);
    }
    
    //Add a trophy to the medal panel, replacing previous medals
    public void addTrophy(boolean show)
    {
        trophies++;
        augmentMedals(-1*medals, show);
    }
    
    public void showQuizResultFrame(boolean isShowing)
    {
    	quizResultFrame.setVisible(isShowing);
    }
    // Given a set of solutions for the quiz, display them in a dialogue window
    public void displayQuizResults(String solutions) {
        
    	//Jinyu - adding tabs to quiz result display
    	
    	JLabel label = new JLabel(solutions);
    	label.setFont(new Font("SansSerif", Font.PLAIN, 14));
    	resultsPane.insertTab("Trial #"+quizNumber, null, label, null, 0);
    	resultsPane.setSelectedIndex(0);
    	quizResultFrame.add(resultsPane, BorderLayout.CENTER);
    	quizResultFrame.pack();
        quizNumber++;
    }
    
    JLabel backgroundLabel,hairLabel,eyeLabel,noseLabel,shirtLabel,faceLabel;
    protected void setUpLayeredIcon()
    {
        //layeredIcon = new JLayeredPane();
    	layeredIcon = new StudentAvatarDisplay(simStAvatorIconWidth/4, 0);
        /*
	    ImageIcon icon = createImageIcon("img/silhouette.png");
	    backgroundLabel = new JLabel(icon);
	    backgroundLabel.setBounds(50,50,icon.getIconWidth(),icon.getIconHeight());
	    layeredIcon.setPreferredSize(new Dimension(icon.getIconWidth(),icon.getIconHeight()));
	    layeredIcon.add(backgroundLabel, new Integer(2));
	    icon = createImageIcon("img/hair1.png");
	    hairLabel = new JLabel(icon);
	    hairLabel.setBounds(50,50,icon.getIconWidth(),icon.getIconHeight());
	    layeredIcon.add(hairLabel, new Integer(3));
	    icon = createImageIcon("img/happy.png");
	    eyeLabel = new JLabel(icon);
	    eyeLabel.setBounds(50,50,icon.getIconWidth(),icon.getIconHeight());
	    layeredIcon.add(eyeLabel, new Integer(3));
	    icon = createImageIcon("img/nose1.png");
	    noseLabel = new JLabel(icon);
	    noseLabel.setBounds(50,50,icon.getIconWidth(),icon.getIconHeight());
	    layeredIcon.add(noseLabel, new Integer(3));
	    icon = createImageIcon("img/tshirt1.png");
	    shirtLabel = new JLabel(icon);
	    shirtLabel.setBounds(50,50,icon.getIconWidth(),icon.getIconHeight());
	    layeredIcon.add(shirtLabel, new Integer(1));
	    icon = createImageIcon("img/face1.png");
	    faceLabel = new JLabel(icon);
	    faceLabel.setBounds(50,50,icon.getIconWidth(),icon.getIconHeight());
	    layeredIcon.add(faceLabel, new Integer(3));
	    */
	    getSimStAvatorIcon().setBounds(0,0,getSimStAvatorIconSize().width,getSimStAvatorIconSize().height);
	    layeredIcon.add(getSimStAvatorIcon(),new Integer(4));
	    getSimStAvatorIcon().setVisible(false);
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    public ImageIcon createImageIcon(String path) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	
    	return new ImageIcon(url);
    	
    }
 
    // Given a set of solutions for the quiz, display them in a dialogue window, using tabs for
    //the different sections. If section was not passed, sectionComplete is -1 or 0.  If section
    //is passed, sectionComplete is the number of the section which was complete and the next set
    //of results will go on the next section tab
    public void displayQuizResults(String solutions, int sectionNumber, boolean complete, int levelNumber) {
        
    	//Jinyu - adding tabs to quiz result display
    	
    	if(sectionResultsPane == null)
    	{
    		sectionResultsPane = new JTabbedPane();
    		resultsPane.insertTab("Section "+levelNumber+"-"+sectionNumber, null, sectionResultsPane, null, 0);
    		resultsPane.setSelectedIndex(0);
        	quizResultFrame.add(resultsPane, BorderLayout.CENTER);
        	quizResultFrame.pack();
    	}
    	
    	JLabel label = new JLabel(solutions);
    	label.setFont(new Font("SansSerif", Font.PLAIN, 14));
    	Dimension size = new Dimension((int)(1.5*simStAvatorIconWidth), getHeight());
    	if(complete)
    	{
    		sectionResultsPane.insertTab("Section "+levelNumber+"-"+sectionNumber+" Complete", null, label, null, 0);

        	sectionResultsPane.setSelectedIndex(0);
        	quizNumber=1;
            sectionResultsPane = null;
    	}
    	else
    	{
	        sectionResultsPane.insertTab("Trial #"+quizNumber, null, label, null, 0);

	    	sectionResultsPane.setSelectedIndex(0);
	        quizNumber++;
    	}

    	quizResultFrame.setSize(size);
    	
    }
    
    public void setQuizProgress(double percentCorrect)
    {
    	int progress = (int)(percentCorrect*100);
    	quizProgress.setValue(progress);
    }
    
    public void setImageTeacher(boolean isTeacher)
    {
    	//JOptionPane.showMessageDialog(null, "setImageTeacher "+isTeacher);
    	if(isTeacher)
    	{
    		//getSimStAvatorIcon().setIcon(new ImageIcon(SimStPLE.TEACHER_IMAGE));
        	/*String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+SimStPLE.TEACHER_IMAGE;
        	ClassLoader cl = this.getClass().getClassLoader();
        	Icon teacherIcon = new ImageIcon(cl.getResource(file));

    		getSimStAvatorIcon().setIcon(teacherIcon);*/
    		setImage(SimStPLE.TEACHER_IMAGE);
    		this.getSimStNameLabel().setText(simStPLE.getTeacherName());
    		layeredIcon.setIsStaticGraphic(true);
    	}
    	else
    	{
    		//getSimStAvatorIcon().setIcon(new ImageIcon(SimStPLE.STUDENT_IMAGE));
        	/*String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+SimStPLE.STUDENT_IMAGE;
        	ClassLoader cl = this.getClass().getClassLoader();
        	Icon stacyIcon = new ImageIcon(cl.getResource(file));

    		getSimStAvatorIcon().setIcon(stacyIcon);*/
    		getSimStAvatorIcon().setVisible(false);
    		setImage(SimStPLE.STUDENT_IMAGE);
    		this.getSimStNameLabel().setText(simStPLE.getSimStName());
    		layeredIcon.setIsStaticGraphic(false);
    	}
        
    }
    
    
    public void setImage(String img)
    {
    	//JOptionPane.showMessageDialog(null, "setImage "+img);
    	/*if(!img.startsWith("%"))
    	{
    		getSimStAvatorIcon().setVisible(true);
	        /*String file = WebStartFileDownloader.SimStAlgebraPackage+"/"+img;
	        ClassLoader cl = this.getClass().getClassLoader();
	        Icon icon = new ImageIcon(cl.getResource(file));*//*
    		Icon icon = createImageIcon(img);
	    	getSimStAvatorIcon().setIcon(icon);
    	}
    	else
    	{
    		//getSimStAvatorIcon().setVisible(false);
    		String[] parts = img.split("%");

            final ImageIcon icon = createImageIcon(parts[1]);
            final ImageIcon icon2 = createImageIcon(parts[2]);
            final ImageIcon icon3 = createImageIcon(parts[3]);
            final ImageIcon icon4 = createImageIcon(parts[4]);
            final ImageIcon icon5 = createImageIcon(parts[5]);
            
            backgroundLabel.setIcon(icon);
            hairLabel.setIcon(icon2);
            eyeLabel.setIcon(icon3);
            noseLabel.setIcon(icon4);
            shirtLabel.setIcon(icon5);
    	}*/
    	layeredIcon.setImage(img);
    }
    
    public void setName(String name)
    {
    	if(tabPane.getSelectedIndex() == 0)
    	{
        	simStNameLabel.setText(SimSt.getSimStName());
    	}
    	tabPane.setTitleAt(0, name);
    	nextProblemButton.setText(getSimStPLE().getNextProblemButtonTitleString());
    	quizButton.setText(getSimStPLE().getQuizButtonTitleString());
    }
    
    
	private StudentAvatarDisplay tutoringAvatar;
    public StudentAvatarDisplay getTutoringAvatar(){
    	return tutoringAvatar;
    }
    public void setTutoringAvatar(StudentAvatarDisplay tutoringAvatar){
    	this.tutoringAvatar=tutoringAvatar;
    }
    
    private JPanel tutoringAvatarPanel;
    public JPanel getTutoringAvatarPanel(){
    	return tutoringAvatarPanel;
    }
    public void setTutoringAvatarPanel(JPanel tutoringAvatarPanel){
    	this.tutoringAvatarPanel=tutoringAvatarPanel;
    }
    
    
    public void setExpression(String expression)
    {
        //final ImageIcon icon = createImageIcon(expression);
        //faceLabel.setIcon(icon);
    	layeredIcon.setExpression(expression);
    }

    public void setSpeech(String text)
    {
    	
    	
    	int width = (int)(2*getSimStAvatorIconSize().width)-20;
    	JLabel temp = new JLabel(text);
    	temp.setFont(new Font("Monospace", Font.PLAIN,12));
    	
    	int textWidth = temp.getPreferredSize().width;
    	if( textWidth > width && width != 0)
    	{
    		//Text Does Not Fit - Fancy formatting
    		double percentFits = ((double) width)/textWidth;
    		int charsFit = (int) ( percentFits * text.length());
    		String remaining = text;
    		String formatted = "";
    		while(remaining.length() > 0)
    		{
    			if(remaining.length() < charsFit)
    			{
    				int newLine = remaining.indexOf("\\n");
    				String tempString = "";
    				if(newLine != -1)
    				{
    					tempString = remaining.substring(0, newLine);
        				formatted += tempString+"\n";
    					remaining = remaining.substring(newLine+2);
    				}
    				formatted += remaining+"\n";
    				remaining = "";
    			}
    			else
    			{
    				int firstSpace = remaining.indexOf(' ');
    				if(firstSpace == -1)
    					firstSpace = remaining.length();
    				int newLine = remaining.indexOf("\\n");
    				if(newLine == -1)
    					newLine = remaining.length();
    				String tempString = "";
    				if(newLine < charsFit)
    				{
    					tempString = remaining.substring(0, newLine);
    					remaining = remaining.substring(newLine+2);
    				}
    				else if(firstSpace > charsFit)
    				{
    					tempString = remaining.substring(0,firstSpace);
    					remaining = remaining.substring(firstSpace+1);
    				}
    				else
    				{
    					tempString = remaining.substring(0,charsFit);
    					int lastSpace = tempString.lastIndexOf(' ');
    					remaining = remaining.substring(lastSpace+1);
    					tempString = tempString.substring(0,lastSpace);
    				}
    				formatted += tempString+"\n";
    				
    			}
    		}
    		formatted += "";
    		getSpeechText().setText(formatted);
       	}
    	else
    	{
    		getSpeechText().setText(text);
    	}

		speechScroll.repaint();
		
    }
    
    public void setFormattedSpeech(String text)
    {
		getSpeechText().setText(text);
    	scrollPaneToBottom();
    }
    
    public void appendSpeech(String text, String name)
    {
    	// When tutor launches the Overview Tab text gets overwritten to start Problem message
    	if(tabPane.getSelectedComponent().getName() != null 
    			&& tabPane.getSelectedComponent().getName().equalsIgnoreCase("Unit Overview")) {
    		getSimStPLE().showTabText(tabPane.getSelectedComponent().getName());
    		return;
    	}
    	String prevText = getSpeechText().getText();
    	Calendar cal = Calendar.getInstance();
    	String now;
    	if(cal.get(Calendar.MINUTE) < 10)
        	now = "["+cal.get(Calendar.HOUR)+":0"+cal.get(Calendar.MINUTE)+"]";
    	else
    		now = "["+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+"]";
    	if(now.contains("[0:"))
    		now = now.replace("[0:", "[12:");
    	text = now+" "+name+": "+text;
    	int width = (int)(2.5*getSimStAvatorIconSize().width)-25;
    	JLabel temp = new JLabel(text);
    	temp.setFont(new Font("Monospace", Font.PLAIN,12));
    	
    	int textWidth = temp.getPreferredSize().width;
    	if( textWidth > width && width != 0)
    	{
    		//Text Does Not Fit - Fancy formatting
    		double percentFits = ((double) width)/textWidth;
    		int charsFit = (int) ( percentFits * text.length());
    		String remaining = text;
    		String formatted = "";
    		while(remaining.length() > 0)
    		{
    			if(remaining.length() < charsFit)
    			{
    				int newLine = remaining.indexOf("\\n");
    				String tempString = "";
    				if(newLine != -1)
    				{
    					tempString = remaining.substring(0, newLine);
        				formatted += tempString+"\n";
    					remaining = remaining.substring(newLine+2);
    				}
    				formatted += remaining+"\n";
    				remaining = "";
    			}
    			else
    			{
    				int firstSpace = remaining.indexOf(' ');
    				if(firstSpace == -1)
    					firstSpace = remaining.length();
    				int newLine = remaining.indexOf("\\n");
    				if(newLine == -1)
    					newLine = remaining.length();
    				String tempString = "";
    				if(newLine < charsFit)
    				{
    					tempString = remaining.substring(0, newLine);
    					remaining = remaining.substring(newLine+2);
    				}
    				else if(firstSpace > charsFit)
    				{
    					tempString = remaining.substring(0,firstSpace);
    					if(firstSpace+1>=remaining.length())
    						remaining = "";
    					else
    						remaining = remaining.substring(firstSpace+1);
    				}
    				else
    				{
    					tempString = remaining.substring(0,charsFit);
    					int lastSpace = tempString.lastIndexOf(' ');
    					remaining = remaining.substring(lastSpace+1);
    					tempString = tempString.substring(0,lastSpace);
    				}
    				formatted += tempString+"\n";
    				
    			}
    		}
    		formatted += "";
    		if(prevText.endsWith(text))
    			return;
    		getSpeechText().append(formatted);
       	}
    	else
    	{
    		if(prevText.endsWith(text.substring(7)+"\n"))
    			return;
    		getSpeechText().append(text+"\n");
    		//appendAndScroll(text);
    	}

		speechScroll.repaint();
    	scrollPaneToBottom();
		
    }
        
    public void clearSpeech()
    {
		getSpeechText().setText("");

		getTextResponse().setEditable(false);
    	getTextResponse().setEnabled(false);
    	getTextResponseSubmitButton().setEnabled(false);
    	if(getTextResponse().getItemCount() > 0)
    		getTextResponse().removeAllItems();
    	
    }
    
    public void scrollPaneToBottom() {
    	 
    	if(SwingUtilities.isEventDispatchThread())
    	{
    		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					speechScroll.getVerticalScrollBar().setValue(
						speechScroll.getVerticalScrollBar().getMaximum());
				}
			});
    	}
    	else
    	{
			//SwingUtilities.invokeLater(new Runnable() {
	    	try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						speechScroll.getVerticalScrollBar().setValue(
							speechScroll.getVerticalScrollBar().getMaximum());
					}
				});
			} catch (Exception e) {
			}
    	}
	}
    

    
    public void refresh()
    {
    	//paintImmediately(this.getBounds());
    }
    
    public void showButtons(boolean show)
    {
    	/*getYesResponseButton().setVisible(show);
    	getNoResponseButton().setVisible(show);
    	getYesResponseButton().validate();
    	getNoResponseButton().validate();
    	refresh();*/
    	if(show)
    	{
    		//showTextResponse(false);
    		getYesResponseButton().setVisible(false);
	    	getNoResponseButton().setVisible(false);
	    	setYesResponseButton(new JButton("Yes"));
	        setNoResponseButton(new JButton("No"));
	        getYesResponseButton().setPreferredSize(new Dimension(getYesResponseButton().getPreferredSize().width*2, getYesResponseButton().getPreferredSize().height));
	        getNoResponseButton().setPreferredSize(new Dimension(getNoResponseButton().getPreferredSize().width*2, getNoResponseButton().getPreferredSize().height));
	        yesNoPanel.add(getYesResponseButton());
	        yesNoPanel.add(getNoResponseButton());
	        getYesResponseButton().validate();
	        getNoResponseButton().validate();
	        yesNoPanel.validate();
	        yesNoPanel.paintImmediately(yesNoPanel.getBounds());
    	}
        else
        {
    		getYesResponseButton().setVisible(show);
	    	getNoResponseButton().setVisible(show);
	    	getYesResponseButton().validate();
	    	getNoResponseButton().validate();
        }
    	refresh();
    }
    
    public void restoreButtons()
    {
		getYesResponseButton().setVisible(true);
    	getNoResponseButton().setVisible(true);
    }
    
    public void showTextResponse(boolean show)
    {
    	if(show)
    	{
    		showButtons(false);
    	}
    	//getTextResponse().setVisible(show);
		getTextResponse().setEditable(show);
    	getTextResponse().setEnabled(show);
    	getTextResponseSubmitButton().setEnabled(show);
    	/*getTextResponse().requestFocus();
	    getTextResponse().validate();*/
    	
    	if(show)
    	{
	    	SwingUtilities.invokeLater(new Runnable() {
				public void run() {
			    	getTextResponse().requestFocus();
				    getTextResponse().validate();
				}
			});
    	}

    }
    

    public void showTextResponseOptions(final boolean show, List<String> options)
    {
    	if(show)
    	{
    		showButtons(false);
    	}
    	getTextResponse().removeAllItems();
    	if(options != null)
    	{
    		for(String option:options)
    			getTextResponse().addItem(option);
    	}
		getTextResponse().setEditable(show);
    	getTextResponse().setEnabled(show);
    	getTextResponseSubmitButton().setEnabled(show);

    	if(show)
    	{
	    	SwingUtilities.invokeLater(new Runnable() {
				public void run() {
			    	getTextResponse().requestFocus();
				    getTextResponse().validate();
				    if(show)
				    	getTextResponse().showPopup();
				}
			});
    	}

    }
    
    JFrame waitMsg;
    
    public void showWaitMessage(boolean show)
    {
    	if(show)
    	{
    		waitMsg = new JFrame();
    		waitMsg.setUndecorated(true);
    		waitMsg.setSize(450,100);
    		waitMsg.setLocationRelativeTo(null);
    		waitMsg.setAlwaysOnTop(true);
    		JLabel msg = new JLabel(WAIT_MESSAGE);
    		msg.setFont(new Font(msg.getFont().getFamily(),msg.getFont().getStyle(), 14));
    		msg.setBorder(BorderFactory.createRaisedBevelBorder());
    		//progressBar.setIndeterminate(true);
    		waitMsg.getContentPane().setLayout(new BorderLayout());
    		waitMsg.getContentPane().add(msg,BorderLayout.CENTER);
    		waitMsg.getContentPane().add(progressBar,BorderLayout.SOUTH);
    		waitMsg.setVisible(true);
    	}
    	else if(waitMsg != null)
    	{
    		waitMsg.setVisible(false);
    	}
    }
    

    public void setWaitProgress(double percentProgress)
    {
    	int progress = (int)(percentProgress*100);
    	progressBar.setValue(progress);
    }
    
    //JFrame problemBankFrame;
    JTable problemBankTable;
    
    public Component createProblemBank(String[] columns, Object[][] problemBank)
    {

    	//problemBankFrame = new JFrame("Bank of Problems");
	//problemBankFrame.setVisible(true);

		
		final String[] columnToolTips = {
	    	    "Generated Problems to Try Solving", 
	    	    "Number of Times Students Tried Problems Similar to This One", 
	    	    //"Number of Times Students Got Problems Like This One Correct",
	    	    //"Percentage of the Time Students Got Problems Like This One Correct",
	    	    "Estimated Difficulty of the Problem"
	    };
		
		//problemBankTable = new JTable(problemBank,columns);
		problemBankTable = new JTable(new ProblemBankTableModel(columns,problemBank))
		{
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e)
			{
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = 
                        columnModel.getColumn(index).getModelIndex();
                return columnToolTips[realIndex];
			}
		};
		problemBankTable.setFillsViewportHeight(true);
		
		/*
		if(getSimStPLE() != null && getSimStPLE().getSimSt() != null && getSimStPLE().getSimSt().isSsMetaTutorMode()) {
			problemBankTable.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
	 					getSimStPLE().getSimSt().getModelTraceWM().setLastAction(WorkingMemoryConstants.PROBLEM_BANK_REVIEWED_ACTION);
	 				}				
			});
			problemBankTable.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent me) {
 					getSimStPLE().getSimSt().getModelTraceWM().setLastAction(WorkingMemoryConstants.PROBLEM_BANK_REVIEWED_ACTION);
				}
			});
		}
		*/
		JScrollPane scroll = new JScrollPane(problemBankTable);
		//problemBankFrame.getContentPane().add(scroll);
		//problemBankFrame.setSize(550,400);
		problemBankTable.setSize(400,400);
		problemBankTable.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(problemBankTable.getModel());
				
		problemBankTable.setRowSorter(sorter);
		
		return scroll;
		
    }
    
    
    public Component createProblemBankNew(String[] columns, Object[][] problemBank)
    {
  	
  		final String[] columnToolTips = {
  	    	    "Generated Problems to Try Solving", 
  	    	    "Number of Times Students Tried Problems Similar to This One", 
  	    	    //"Number of Times Students Got Problems Like This One Correct",
  	    	    //"Percentage of the Time Students Got Problems Like This One Correct",
  	    	    "Estimated Difficulty of the Problem"
  	    };
  		
  		//problemBankTable = new JTable(problemBank,columns);
  		problemBankTable = new JTable(new ProblemBankTableModel(columns,problemBank))
  		{
  			private static final long serialVersionUID = 1L;
  			public String getToolTipText(MouseEvent e)
  			{
                java.awt.Point p = e.getPoint();
                int index = columnModel.getColumnIndexAtX(p.x);
                int realIndex = 
                        columnModel.getColumn(index).getModelIndex();
                return columnToolTips[realIndex];
  			}
  		};
  		problemBankTable.setFillsViewportHeight(true);
  		
	
  			JScrollPane scroll = new JScrollPane(problemBankTable);

  			problemBankTable.setSize(400,400);
  		

  			problemBankTable.setAutoCreateRowSorter(true);	 
  	  		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(problemBankTable.getModel());
  	  		problemBankTable.setRowSorter(sorter);
  	  		problemBankTable.getRowSorter().toggleSortOrder(1);
  	  	    problemBankTable.getRowSorter().toggleSortOrder(1);
  	  		problemBankTable.getTableHeader().setBackground(new Color(202,202,202));
  		
    		
  	
  		return scroll;
    }
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Getters and Setters
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    public boolean isRunningAsApplication() {
        return runningAsApplication;
    }
    public void setRunningAsApplication(boolean runningAsApplication) {
        this.runningAsApplication = runningAsApplication;
    }

    public BR_Controller getBrController() {
        return brController;
    }
    public void setBrController(BR_Controller brController) {
        this.brController = brController;
    }

    public SimStPLE getSimStPLE() {
        return simStPLE;
    }
    public void setSimStPLE(SimStPLE simStPLE) {
        this.simStPLE = simStPLE;
    }
/*
    public JLabel getTitleLabel() {
        return titleLabel;
    }
    public void setTitleLabel(JLabel titleLabel) {
        this.titleLabel = titleLabel;
    }
*/
    public JComponent getStudentInterface() {
        return studentInterface;
    }
    public void setStudentInterface(JComponent studentInterface) {
        this.studentInterface = studentInterface;
    }

    
    public void setSpeechPanel(JPanel panel)
    {
    	this.speechPanel = panel;
    }
    
    public JPanel getSpeechPanel()
    {
    	return speechPanel;
    }
    
    private void setSpeechText(JTextArea speechText)
    {
    	this.speechText = speechText;
    }
    
    private JTextArea getSpeechText()
    {
    	return speechText;
    }
    
    private void setYesResponseButton(JButton yesButton)
    {
    	yesResponseButton = yesButton; 
    }
    
    public JButton getYesResponseButton()
    {
    	return yesResponseButton;
    }
    
    private void setNoResponseButton(JButton noButton)
    {
    	noResponseButton = noButton; 
    }
    
    public JButton getNoResponseButton()
    {
    	return noResponseButton;
    }
    
    private void setTextResponse(JComboBox text)
    {
    	textResponse = text; 
    }
    
    public JComboBox getTextResponse()
    {
    	return textResponse;
    }

    private void setTextResponseSubmitButton(JButton text)
    {
    	textResponseSubmitButton = text; 
    }
    
    public JButton getTextResponseSubmitButton()
    {
    	return textResponseSubmitButton;
    }
    
    public JLabel getSimStAvatorIcon() {
        return simStAvatorIcon;
    }
    public void setSimStAvatorIcon(JLabel simStAvatorIcon) {
        this.simStAvatorIcon = simStAvatorIcon;
    }
    
    public JLayeredPane getSimStAvatarLayerIcon()
    {
    	return layeredIcon;
    }
    
    public void setSimStAvatarLayerIcon(StudentAvatarDisplay icon)
    {
    	layeredIcon = icon;
    }
    
    public JLabel getSimStNameLabel() {
        return simStNameLabel;
    }
    public void setSimStNameLabel(JLabel simStNameLabel) {
        this.simStNameLabel = simStNameLabel;
    }
    

    public JFrame getTopLevelFrame() {
        return topLevelFrame;
    }
    public void setTopLevelFrame(JFrame topLevelFrame) {
        this.topLevelFrame = topLevelFrame;
    }

    public Dimension getSimStAvatorIconSize() {
        return simStAvatorIconSize;
    }
    public void setSimStAvatorIconSize(Dimension simStAvatorIconSize) {
        this.simStAvatorIconSize = simStAvatorIconSize;
    }

    public Dimension getPlatformSize() {
        return platformSize;
    }
    public void setPlatformSize(Dimension plaformSize) {
        this.platformSize = plaformSize;
    }

    public JButton getNextProblemButton() {
        return nextProblemButton;
    }
    public void setNextProblemButton(JButton nextProblemButton) {
        this.nextProblemButton = nextProblemButton;
    }

    public JButton getQuizButton() {
        return quizButton;
    }
    public void setQuizButton(JButton quizButton) {
        this.quizButton = quizButton;
    }
    
    public JButton getExampleButton() {
        return exampleButton;
    }
    public void setExampleButton(JButton cbButton) {
        this.exampleButton = cbButton;
    }

    public void setUndoButton(JButton cbButton) {
        this.undoButton = cbButton;
    }
    
    public JButton getUndoButton()
    {
    	return undoButton;
    }
    
    public void setRestartButton(JButton cbButton) {
        this.restartButton = cbButton;
    }
    
    public JButton getRestartButton()
    {
    	return restartButton;
    }
    
    
    public JButton getCurriculumBrowserButton() {
        return curriculumBrowserButton;
    }
    public void setCurriculumBrowserButton(JButton cbButton) {
        this.curriculumBrowserButton = cbButton;
    }
    
    public JTabbedPaneWithCloseIcons getExamplePane()
    {
    	return tabPane;
    }
    
    public String getCurrentSpeechText()
    {
    	return getSpeechText().getText();
    }
    
    public boolean getButtonsShowing()
    {
    	return this.getYesResponseButton().isVisible();
    }
    
    public void setNextProblemButtonEnabled(boolean isEnabled)
	{
		nextProblemButton.setEnabled(isEnabled);
		nextProblemButton.repaint();
	}
	

	public void setRestartButtonEnabled(boolean isEnabled)
	{
		restartButton.setEnabled(isEnabled);
		restartButton.repaint();
	}
	
	public void setQuizButtonEnabled(boolean isEnabled)
	{
		quizButton.setEnabled(isEnabled);
		quizButton.repaint();
	}


	public void setUndoButtonEnabled(boolean isEnabled)
	{
		undoButton.setEnabled(isEnabled);
		undoButton.repaint();
	}
	
	public void setUndoButtonText(String text)
	{
		undoButton.setText(text);
	}
	
	public void addSection(String sectionName)
	{
		
	}
	
	public void addQuiz(SimStExample quiz){}
	public void clearQuizzes() {}
	public void clearQuizzesFinalChallenge() {}
	public void unlockQuiz(int index) {}
	
	public void setSpeech(String text, boolean quiz)
	{
		setSpeech(text);
	}
	
    public void setWait(boolean isWaiting)
    {
    	if(isWaiting)
    	{
    		 setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	}
    	else
    	{
    		setCursor(Cursor.getDefaultCursor());
    	}
    }
    
    public void setUpTakingQuiz()
	{
    	refresh();
    	clearSpeech();
    	setExpression(SimStPLE.THINK_EXPRESSION);
	}
    
    public void hideTakingQuiz(){}

	public void setQuizMessage(String message)
	{
		simStPLE.giveMessage(message);
	}
        
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

	public String getTargetWindow() {
		return targetWindow;
	}

	public void setTargetWindow(String targetWindow) {
		this.targetWindow = targetWindow;
	}

	public String getPreviousWindow() {
		return previousWindow;
	}

	public void setPreviousWindow(String previousWindow) {
		this.previousWindow = previousWindow;
	}


}
