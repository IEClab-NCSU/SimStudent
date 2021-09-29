package edu.cmu.pact.miss.PeerLearning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.text.DefaultCaret;
import javax.swing.text.StyleConstants;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommComboBox;
import pact.CommWidgets.JCommLabel;
import pact.CommWidgets.JCommTable;
import pact.CommWidgets.JCommTable.TableCell;
import pact.CommWidgets.JCommTextArea;
import pact.CommWidgets.JCommTextField;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.JTabbedPaneWithCloseIcons;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.MetaTutor.APlusHintDialog;
import edu.cmu.pact.miss.MetaTutor.APlusHintDialogInterface;
import edu.cmu.pact.miss.MetaTutor.MetaTutorAvatarComponent;
import edu.cmu.pact.miss.PeerLearning.MetatutorSplashScreen.SplashFrame;

public class AplusPlatform extends SimStPeerTutoringPlatform implements ChangeListener {

	private static final long serialVersionUID = 1L;
	
	BR_Controller brController;
	SimStPLE simStPLE;
	Hashtable<Integer,String> sections;
	
	SimStLogger logger;
	
	public JComponent studentInterface;
	JComponent exampleInterface;
	public JComponent quizInterface;
	
	JTabbedPaneWithCloseIcons tabPane;
	public JPanel tutoringTab, videoTab, overviewTab, exampleTab, quizTab, bankTab;
	JPanel commPane, exampleCommPane;
	JLabel skillometerLabel;
	JPanel skillometer;
	
	public JTabbedPaneWithCloseIcons getAplusTabs(){
		return this.tabPane;
	}
	
	
	
	JXTaskPaneContainer exampleContainer;
	public JXTaskPaneContainer quizContainer;
	public List<JXTaskPane> examplePanes;
	public List<QuizPane> quizPanes;
	
	public List<List<JLabelIcon>> quizProblemsJLabelIcon;

	public List<List<ExampleAction>> quizProblems;
	public List<List<ExampleAction>> exampleProblems;
	
	public static Color studentColor = new Color(148,182,210);
	//public static Color studentColor = new Color(204,148,102);
	Color videoColor = new Color(221,128,71);
	Color overviewColor = new Color(165,171,129);
	Color exampleColor = new Color(216,178,92);
	Color quizColor = new Color(123,167,157);
	Color bankColor = new Color(94,156,117);
	//Color studentColorCogTutor = new Color(204,148,102);
	
	Color studentColorCogTutor = new Color(148,182,210);
	
	Color studentUnfocusColorCogTutor = new Color(219,184,156);
	Color studentUnfocusColor = new Color(209,217,225);
	Color videoUnfocusColor = new Color(227,204,189);
	Color overviewUnfocusColor = new Color(213,215,204);
	Color exampleUnfocusColor = new Color(226,216,194);
	Color quizUnfocusColor = new Color(202,214,211);
	Color bankUnfocusColor = new Color(125,212,156);
	
	Color[] focusColors = {studentColor, bankColor,videoColor, overviewColor,exampleColor,quizColor};
	Color[] unfocusColors = {studentUnfocusColor, bankUnfocusColor, videoUnfocusColor, overviewUnfocusColor, exampleUnfocusColor,quizUnfocusColor};
	
	Color[] focusColorsCogTutor = {studentColorCogTutor, videoColor, overviewColor,exampleColor};
	Color[] unfocusColorsCogTutor = {studentUnfocusColor, videoUnfocusColor, overviewUnfocusColor, exampleUnfocusColor};
	
	
	Color[] focusColorsCogTutorAplusCtrl = {studentColorCogTutor, bankColor,videoColor, overviewColor,exampleColor,quizColor};
	Color[] unfocusColorsCogTutorAplusCtrl = {studentColorCogTutor, bankUnfocusColor, videoUnfocusColor, overviewUnfocusColor, exampleUnfocusColor,quizUnfocusColor};
	
	
	//Color[] focusColors = {studentColor, overviewColor,exampleColor,quizColor};
	//Color[] unfocusColors = {studentUnfocusColor, overviewUnfocusColor,	exampleUnfocusColor,quizUnfocusColor};
	
	//Color[] focusColors = {studentColor, overviewColor,quizColor};
	//Color[] unfocusColors = {studentUnfocusColor, overviewUnfocusColor,quizUnfocusColor};
	 // Color[] focusColors;
	 // Color[] unfocusColors;
	
	public static final int BUTTON_BORDER_WIDTH = 3;
	public static final int BORDER_WIDTH = 10;
	public static final Insets BORDER = new Insets(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH);
	public static final Font BOARD_FONT = new Font("Comic Sans MS", Font.PLAIN, 16);
	public static final Font SPLASH_FONT = new Font("Comic Sans MS", Font.PLAIN, 17);
	public static final Font SPLASH_FONT_BIG = new Font("Comic Sans MS", Font.PLAIN, 20);
	public static final Font FONT = new Font("Serif", Font.PLAIN, 18);
	public static final Font MED_FONT = new Font("Serif", Font.PLAIN, 16);
	public static final Font SMALL_FONT = new Font("Serif", Font.PLAIN, 12);
	
	public static final Font S5_FONT = new Font("Comic Sans MS", Font.PLAIN, 18);
	public static final Font S5_MED_FONT = new Font("Comic Sans MS", Font.PLAIN, 16);
	public static final Font S5_SMALL_FONT = new Font("Comic Sans MS", Font.PLAIN, 12);
	public static final Font S5_MED_MED_FONT = new Font("Comic Sans MS", Font.PLAIN, 14);
	
	
	public static final String videoTabTitle = "Introduction Video";
	public static final String overviewTabTitle = "Unit Overview";
	public static final String exampleTabTitle = "Examples";
	public static final String quizTabTitle = "Quiz";
	public static final String bankTabTitle = "Problem Bank";
	public static String skillometerLabelText = "SimStName's Skillometer:";
	public static String sectionMeterLabelText = "SimStName's Quiz Progress:";
	public SimStPLEActionListener actionListener;
	protected SimStPLEMouseMotionListener mouseMotionListener;
	private static final String TEACHER_MSG = " If you want to ask something, why don't you ask me at my desk on the SimStName tab ";
	
	public static final int METATUTOR_IMAGE_WIDTH = 272;
	public static final int METATUTOR_IMAGE_HEIGHT = 225;

	
	private static final String  BUTTON_NEXT = "Next Button";
	private static final String  BUTTON_PREVIOUS = "Previous Button";
	private static final String  BUTTON_FFWD = "Fast Forward Button";
	private static final String  BUTTON_FBWD = "Fast Backward Button";
	private static final int MAX_NUMBER_OF_TABS=5;
	
	//private boolean modelTracer = true;
	
	private SimStExample exampleTemp;
	private String currentStep;
	private  HashSet validSteps4display; //hash set to hold all the FOA that must be displayed in examples tab steps
	private  boolean splashShown=false;
	public static  boolean splashBLShown=false;
   
	public static  boolean avatarDesignerShown=false;
	public boolean hasExampleSection=false;
	
	private boolean displayProblemBank=true;
	public void setDisplayProblemBank(boolean flag){this.displayProblemBank=flag;}
	private boolean getDispalyProblemBank(){return this.displayProblemBank;}
			
	private boolean displayQuiz=true;
	public void setDisplayQuiz(boolean flag){this.displayQuiz=flag;}
	private boolean getDisplayQuiz(){return this.displayQuiz;}
	 
	public static int EXAMPLES_TAB_INDEX=3;
	public static int VIDEO_TAB_INDEX=2;
	
	//public static boolean overviewScrolled = false;
	//public static boolean exampleReviewed = false;
	
	/*The panel that contains the progress bars*/
    public JPanel sectionMeterPanel;
	public JPanel getSectionMeterPanel(){return sectionMeterPanel;}
    JLabel sectionMeterLabel;
	TitledBorder title;
	public static String exampleProblem = "";
	
	
	
	public AplusPlatform(JComponent tutorPanel, TutorController brController)
	{
		BR_Controller temp = (BR_Controller)brController;
        setBrController(temp);

		logger = new SimStLogger(temp);
		
	
		setDefaultLookAndFeel();
		updateAplusAppearanceParemeters();

		
        SimStPLE.setComponentFont(tutorPanel, new Font("Serif", Font.PLAIN, 16));
		
		setSimStPLE(new SimStPLE(temp, this));
		setLayout(new BorderLayout());
		
		setStudentInterface(tutorPanel);
		
        actionListener = new SimStPLEActionListener(getBrController());
        mouseMotionListener=new SimStPLEMouseMotionListener(getBrController());
		
        
		sections = new Hashtable<Integer,String>();
		quizProblems = new LinkedList<List<ExampleAction>>();
		quizProblemsJLabelIcon=new LinkedList<List<JLabelIcon>>();
		
		exampleProblems = new LinkedList<List<ExampleAction>>();
				
		tabPane = new JTabbedPaneWithCloseIcons(this);
		add(tabPane, BorderLayout.CENTER);
		
		
		
		hasExampleSection=this.getSimStPLE().hasExamples;
		
		// focusColors=new Color[MAX_NUMBER_OF_TABS+1];
		// unfocusColors=new Color[MAX_NUMBER_OF_TABS+1];
		
		  
		setUpTutoringTab(tutorPanel);
		//	focusColors[0]=studentColor;
		//unfocusColors[0]=studentUnfocusColor;
		
		//it would be nice to select if we want the problem bank from command line arqument
		if (getDispalyProblemBank())
			setUpProblemBankTab();
		//	focusColors[1]=bankColor;
		//	unfocusColors[1]=bankUnfocusColor;
		
		setUpVideoTab();
		//focusColors[2]=videoColor;
		//unfocusColors[2]=videoUnfocusColor;
		
		
		setUpOverviewTab();
		//	focusColors[3]=overviewColor;
		//	unfocusColors[3]=overviewUnfocusColor;
		
		if (hasExampleSection){
		//	focusColors[4]=exampleColor;
		//	unfocusColors[4]=exampleUnfocusColor;		
			setUpExampleTab();		
		}
		
		
			setUpQuizTab();
		
		//focusColors[5]=quizColor;
		//	unfocusColors[5]=quizUnfocusColor;
		
	
		
		
        tabPane.addChangeListener(actionListener);
		tabPane.addChangeListener(this);
		if (this.getBrController().getMissController().getSimSt().isInterfaceTimed()){
			this.getBrController().getMissController().getSimSt().scheduleInterfaceInactivenessTimer();
			tabPane.addMouseMotionListener(mouseMotionListener);
			tutorPanel.addMouseMotionListener(mouseMotionListener);
		}
       
		
		
		for(int i=0;i<getSimStPLE().getSections().size();i++)
		{
			addSection(getSimStPLE().getSections().get(i));
		}
		
		for(int i=0;i<getSimStPLE().getExamples().size();i++)
		{
			addExample(getSimStPLE().getExamples().get(i));
		}
		
		getSimStPLE().reloadQuizQuestions(false);
		unlockQuiz(getSimStPLE().getCurrentQuizSectionNumber());
		
		
		/*in cog tutor control. update the quiz progress bar.*/
		if (brController.getMissController().getSimSt().isSsCogTutorMode() && !brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
			if (brController.getMissController().getSimStPLE().quizProg.get(brController.getMissController().getSimStPLE().currentQuizSectionNumber)!=null){	
				int value = brController.getMissController().getSimStPLE().getSsCognitiveTutor().getSimStBKT().getAverageMastery();
				brController.getMissController().getSimStPLE().quizProg.get(brController.getMissController().getSimStPLE().currentQuizSectionNumber).setValue(value);
			}
		}
		
		restoreSkilloMeterBarValues();
		//initialize variables necessary for examples player
		currentStep="-1";
		validSteps4display = new HashSet();
		
		// getSimStPLE().messageDialog.showMessage("Congratulations, you have mastered all skills of this level. Click OK to proceed");	 
		 
		
							
	}
	
	
	
	
	
	/**
	 * Method that sets the appearance of APLUS (e.g. colors, 
	 * tabs displayed etc) based on its mode (Normal or CogTutor)
	 * 
	 */
	private void updateAplusAppearanceParemeters(){
		SimSt simSt=this.getBrController().getMissController().getSimSt();
		
		if (simSt.isSsCogTutorMode()){
			/*determine which tabes to show and rearrange default colors*/
			//studentColor=studentColorCogTutor;
			//studentUnfocusColor=studentUnfocusColorCogTutor;
			
			
			/*set the proper metatutor images (in Aplus Control keep the same image)*/
			if (!simSt.isSsAplusCtrlCogTutorMode()){
				setDisplayProblemBank(false);
				setDisplayQuiz(false);
				SimStPLE.METATUTOR_EMPTY_DESK=SimStPLE.METATUTOR_EMPTY_DESK_COGTUTOR;
				SimStPLE.METATUTOR_IMAGE=SimStPLE.METATUTOR_IMAGE_COGTUTOR;
				SimStPLE.METATUTOR_STUDENT_INTERACTION_IMAGE=SimStPLE.METATUTOR_STUDENT_INTERACTION_IMAGE_COGTUTOR;
				
				
				
				focusColors=focusColorsCogTutor;
				unfocusColors=unfocusColorsCogTutor;
				
			}
			else{
				AplusPlatform.EXAMPLES_TAB_INDEX=4;
				//focusColors=focusColorsCogTutorAplusCtrl;
				//unfocusColors=unfocusColorsCogTutorAplusCtrl;
			}
			
			
		}
		
		
		
		
		
		
		
		
		
	}
	
	private void setDefaultLookAndFeel()
	{
		try {			
			if (this.getBrController().getMissController().getSimSt().isSs2014FractionAdditionAdhoc())
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else 
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
		UIManager.put("TabbedPane.focus", studentColor);
		UIManager.put("TabbedPane.selected", studentColor);
		UIManager.put("TabbedPane.font", S5_SMALL_FONT);
		UIManager.put("Label.font", MED_FONT);
		UIManager.put("Button.font", MED_FONT);
		UIManager.put("Button.highlight", Color.WHITE);
		UIManager.put("TaskPane.font", MED_FONT);
		UIManager.put("Button.foreground", Color.BLACK);
		UIManager.put("Button.background", studentUnfocusColor);

	}
	
	
	
	private void setUpTutoringTab(JComponent tutorPanel)
	{
		tutoringTab = new JPanel();
		

		tutoringTab.setBackground(studentColor);
		
		
		tutoringTab.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));
		
		tutoringTab.setLayout(new GridBagLayout());
				
		JScrollPane interfacePane = new JScrollPane(tutorPanel);
		tutorPanel.setBackground(studentColor);
		tutorPanel.setAlignmentX(CENTER_ALIGNMENT);
		GridBagConstraints ipConst = new GridBagConstraints();
		ipConst.gridx = 0;
		ipConst.gridy = 0;
		ipConst.gridwidth = 1;
		ipConst.gridheight = 2;
		ipConst.weightx = .75;
		ipConst.weighty = .75;
		ipConst.fill = GridBagConstraints.BOTH;
		ipConst.anchor = GridBagConstraints.PAGE_START;
		tutoringTab.add(interfacePane, ipConst);
		interfacePane.setPreferredSize(tutorPanel.getPreferredSize());

		JPanel metaTutorPane = new JPanel();
		metaTutorPane.setBackground(studentColor);
		GridBagConstraints mtpConst = new GridBagConstraints();
		mtpConst.gridx = 1;
		mtpConst.gridy = 2;
		mtpConst.gridwidth = 1;
		mtpConst.gridheight = 1;
		mtpConst.weightx = .25;
		mtpConst.weighty = .25;
		mtpConst.fill = GridBagConstraints.BOTH;
		mtpConst.anchor = GridBagConstraints.PAGE_START;
		
		
		if(simStPLE.getSimSt().isSsMetaTutorMode())
		{
			
			String metaTutorImage = (brController.getMissController().getSimStPLE().isFirstTimeAPLUS())? SimStPLE.METATUTOR_EMPTY_DESK : SimStPLE.METATUTOR_IMAGE;
			metaTutorComponent = new MetaTutorAvatarComponent(metaTutorImage, getBrController().getMissController().getSimSt());
			
			
            aPlusHintDialogInterface = new APlusHintDialog(new Frame(),simStPLE.getMissController().
            		getAPlusHintMessagesManager(), logger, metaTutorComponent);
            if(aPlusHintDialogInterface instanceof JDialog) {
            	((JDialog)aPlusHintDialogInterface).setLocationRelativeTo(this);
            }
            metaTutorComponent.setPreferredSize(new Dimension(AplusPlatform.METATUTOR_IMAGE_WIDTH, 
            		AplusPlatform.METATUTOR_IMAGE_HEIGHT));
            metaTutorComponent.setBackground(studentColor);
            setMetaTutorComponent(metaTutorComponent);
        }
		else
		{
			metaTutorComponent = new JLabel();
			((JLabel) metaTutorComponent).setIcon(createImageIcon(SimStPLE.BASELINE_MR_WILLIAMS_IMAGE));
			metaTutorComponent.setPreferredSize(new Dimension(AplusPlatform.METATUTOR_IMAGE_WIDTH, 
            		AplusPlatform.METATUTOR_IMAGE_HEIGHT));
			metaTutorComponent.setBackground(studentColor);
		}
		metaTutorPane.add(metaTutorComponent);
		
		tutoringTab.add(metaTutorPane, mtpConst);
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setBackground(studentColor);
		GridBagConstraints bpConst = new GridBagConstraints();
		bpConst.gridx = 1;
		bpConst.gridy = 0;
		bpConst.gridwidth = 1;
		bpConst.gridheight = 1;
		bpConst.weightx = .25;
		bpConst.weighty = .2;
		bpConst.fill = GridBagConstraints.BOTH;
		bpConst.anchor = GridBagConstraints.PAGE_START;
		tutoringTab.add(buttonPane, bpConst);
		
		if (!brController.getMissController().getSimSt().isSsCogTutorMode())
			setUpButtonPanel(buttonPane);
		else if(brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
			setUpButtonPanelAplusCogTutor(buttonPane);
		}
		else{
			setUpButtonPanelCogTutor(buttonPane);
		}
		
		
		
		skillometer = new JPanel();
		skillometer.setBackground(studentColor);
		skillometer.setLayout(new BoxLayout(skillometer, BoxLayout.Y_AXIS));
		GridBagConstraints barpConst = new GridBagConstraints();
		barpConst.gridx = 1;
		barpConst.gridy = 1;
		barpConst.gridwidth = 1;
		barpConst.gridheight = 1;
		barpConst.weightx = .25;
		barpConst.weighty = .5;
		barpConst.insets = BORDER;
		barpConst.fill = GridBagConstraints.BOTH;
		barpConst.anchor = GridBagConstraints.PAGE_START;
	//	tutoringTab.add(skillometer, barpConst);
		
		skillometerLabel = new JLabel(AplusPlatform.skillometerLabelText.replaceAll("SimStName", SimSt.SimStName));
		skillometer.setAlignmentX(LEFT_ALIGNMENT);
		skillometerLabel.setFont(FONT);
		skillometer.add(skillometerLabel);
		JXPanel skillScale = new JXPanel();
		skillScale.setBackgroundPainter(getTricolorPainter(Color.red.darker(), Color.orange, Color.green.darker()));
		skillScale.setMaximumSize(new Dimension(1000,50));
		skillScale.setLayout(new BorderLayout());
		skillScale.add(new JLabel(" Poor"), BorderLayout.WEST);
		skillScale.add(new JLabel("Excellent "), BorderLayout.EAST);
		skillScale.setBorder(BorderFactory.createLineBorder(studentColor, BORDER_WIDTH));
	//	skillometer.add(skillScale);
		
		
		
		
		//Code to display the progress bars (either they are quiz progress or section progress).
		sectionMeterPanel = new JPanel();
		sectionMeterPanel.setBackground(studentColor);
		Border bord=BorderFactory.createEtchedBorder(EtchedBorder.LOWERED); 		
		

		//String actualTitle= (this.getBrController().getMissController().getSimSt().isSsCogTutorMode())? "Section Progress" : AplusPlatform.sectionMeterLabelText.replaceAll("SimStName", SimSt.SimStName);
				
	
		String actualTitle= AplusPlatform.sectionMeterLabelText.replaceAll("SimStName", SimSt.SimStName);
		if (getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
			actualTitle="Quiz Progress";
		}
		else if (this.getBrController().getMissController().getSimSt().isSsCogTutorMode()){
			actualTitle="Section Progress";
		}
		
		
		
		title = BorderFactory.createTitledBorder(bord, actualTitle);
		title.setTitleJustification(TitledBorder.CENTER);
		title.setTitleFont(S5_FONT);
		sectionMeterPanel.setBorder(title);

	
		sectionMeterPanel.setLayout(new BoxLayout(sectionMeterPanel, BoxLayout.Y_AXIS));
		GridBagConstraints barpConst1 = new GridBagConstraints();
		barpConst1.gridx = 1;
		barpConst1.gridy = 1;
		barpConst1.gridwidth = 1;
		barpConst1.gridheight = 1;
		barpConst1.weightx = .25;
		barpConst1.weighty = .5;
		barpConst1.insets = BORDER;
		barpConst1.fill = GridBagConstraints.BOTH;
		barpConst1.anchor = GridBagConstraints.PAGE_START;
		
		
		sectionMeterPanel.setPreferredSize(new Dimension(350, 200));
		sectionMeterPanel.setMinimumSize(new Dimension(350, 200));
		sectionMeterPanel.setMaximumSize(new Dimension(350, 200));
		
		tutoringTab.add(sectionMeterPanel, barpConst1 );
		
		sectionMeterLabel = new JLabel(AplusPlatform.sectionMeterLabelText.replaceAll("SimStName", SimSt.SimStName));
		sectionMeterLabel.setFont(S5_MED_FONT);
	
		Hashtable<Integer, Integer> map= new Hashtable<Integer,Integer>();
		for (int i=0;i<this.getSimStPLE().quizSections.size(); i++){
						
				Object obj = map.get(this.getSimStPLE().quizSections.get(i));
				if (obj == null) {
					map.put(this.getSimStPLE().quizSections.get(i), 1);
				} else {
					int r = ((Integer) obj).intValue() + 1;
					map.put(this.getSimStPLE().quizSections.get(i), new Integer(r));
				}
		}

		
		this.getSimStPLE().quizProg = new ArrayList<ClickableProgressBar>();
		
		  UIManager.put("ProgressBar.foreground",quizColor); //colour of progress bar
		  UIManager.put("ProgressBar.selectionBackground",quizColor); //colour of percentage counter on background
   		  UIManager.put("Progress.selectionForeground",Color.BLACK); //colour of precentage counter on  progress bar
   		
   		  
   		  
   		  	
   			  for (int i=0;i< this.getSimStPLE().getSections().size(); i++){
   				  Integer val;
   				  Object obj = map.get(i);
   				  
   				  if (obj!=null)
   					  val=(Integer)obj;
   				  else val=100;
   				 
   				  if (getBrController().getMissController().getSimSt().isSsCogTutorMode() && !getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
   					  val=100;
   					  
   				  boolean showSkillometerOnClick=false;//this.getBrController().getMissController().getSimSt().isSsCogTutorMode()?true:false;
   				  if (this.getBrController().getMissController().getSimSt().isSsCogTutorMode() && !this.getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
   					showSkillometerOnClick=true;
   				  else 
   					showSkillometerOnClick=false;
   				  
   				  ClickableProgressBar pb = new ClickableProgressBar(0,val,showSkillometerOnClick,i,getBrController().getMissController().getSimStPLE());
   				  
   				  JLabel levelLabel= new JLabel(getSimStPLE().getSections().get(i).replaceAll("-", ""));
   				  levelLabel.setFont(S5_MED_FONT);
   				  pb.setStringPainted(true);

   				  
   				  Border emptyBorder = BorderFactory.createLineBorder(Color.black,1);
   				  pb.setBorder(emptyBorder);
			
   				  
   				  if (i < this.getSimStPLE().currentQuizSectionNumber)
   					  	pb.setValue(val);
   				  else
						pb.setValue(0);	
   				
   				
			
   				  this.getSimStPLE().quizProg.add(pb);
   				  sectionMeterPanel.add(levelLabel);
   				  sectionMeterPanel.add(this.getSimStPLE().quizProg.get(i));
   				  
			
					
   			 }
   			  
   			  	  
		commPane = new JPanel();
		commPane.setBackground(studentColor);
		GridBagConstraints cpConst = new GridBagConstraints();
		cpConst.gridx = 0;
		cpConst.gridy = 2;
		cpConst.gridwidth = 1;
		cpConst.gridheight = 1;
		cpConst.weightx = .75;
		cpConst.weighty = .25;
		cpConst.fill = GridBagConstraints.BOTH;
		cpConst.anchor = GridBagConstraints.PAGE_START;		
		tutoringTab.add(commPane, cpConst);

		
		
		
		
		/*do not display the communication pane when in CogTutor mode*/
		if (!getSimStPLE().getMissController().getSimSt().isSsCogTutorMode())
			setUpTutoringCommPanel(commPane);
		else if (getSimStPLE().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
			commPane.setLayout(new GridBagLayout()); /*to align it to center*/
			setUpCogTutorStartProblemButton(commPane);

		}
		
		
		//tabPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>"+getSimStPLE().getSimStName()+"</body></html>", exampleTab);
		tabPane.addTab(getSimStPLE().getSimStName(), tutoringTab);
		int index = tabPane.getTabCount()-1;
		tabPane.setBackgroundAt(index, unfocusColors[index]);
	}
	
	
	

	
	private void setUpVideoTab()
	{
		videoTab = new JPanel();
		videoTab.setBackground(videoColor);
		videoTab.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));

		CurriculumBrowser browser = new CurriculumBrowser();
		
		//remove this when we start loading from SimStPLE
		//SimStPLE.setVideoIntroductionName("demoVideoBriefNoSE.mov");
		
		if(SimStPLE.videoIntroductionName != null && SimStPLE.videoIntroductionName.length() > 0)
        	browser.setVideoSource(SimStPLE.videoIntroductionName);
     
		Component video = browser.getVideoPanel();
		
		videoTab.setLayout(new BorderLayout());
		videoTab.add(video, BorderLayout.CENTER);
		
		//tabPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>"+videoTabTitle+"</body></html>", exampleTab);
		tabPane.addTab(videoTabTitle, videoTab);
		int index = tabPane.getTabCount()-1;
		tabPane.setBackgroundAt(index, unfocusColors[index]);
	}

	JLabel overviewTeacher;
	
	private void setUpOverviewTab()
	{
		overviewTab = new JPanel();
		overviewTab.setBackground(overviewColor);
		overviewTab.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));
		
		CurriculumBrowser browser = new CurriculumBrowser();
		
		//remove this when we start loading from SimStPLE
		SimStPLE.setOverviewPageName("curriculum.html");
		
		if(SimStPLE.overviewPageName != null && SimStPLE.overviewPageName.length() > 0)
				browser.setHtmlSource(SimStPLE.overviewPageName);
		Component overview = browser.getBrowserPane();
		
	    JScrollPane scrollbar = (JScrollPane)overview;
	   
		scrollbar.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){

			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				// TODO Auto-generated method stub
				if(e.getValueIsAdjusting()){
				
					//System.out.println("Scrolled ");
					if(!brController.getMissController().getSimSt().getModelTraceWM().isOverviewScrolled())
						brController.getMissController().getSimSt().getModelTraceWM().setOverviewScrolled(true);
					//System.out.println("********************************");
				}
			}
			
		});
		overviewTab.setLayout(new GridBagLayout());
		
		GridBagConstraints ovConst = new GridBagConstraints();
		ovConst.gridx = 1;
		ovConst.gridy = 0;
		ovConst.gridwidth = 1;
		ovConst.gridheight = 2;
		ovConst.weightx = .95;
		ovConst.weighty = 1;
		ovConst.fill = GridBagConstraints.BOTH;
		
		overviewTab.add(overview, ovConst);
	
		JPanel avatarPanel = new JPanel();
		avatarPanel.setBackground(overviewColor);
		overviewTeacher = new JLabel(createImageIcon(SimStPLE.TEACHER_IMAGE));
		
		GridBagConstraints otConst = new GridBagConstraints();
		otConst.gridx = 0;
		otConst.gridy = 1;
		otConst.gridwidth = 1;
		otConst.gridheight = 1;
		otConst.weightx = .05;
		otConst.weighty = .25;
		otConst.fill = GridBagConstraints.HORIZONTAL;
		otConst.anchor = GridBagConstraints.LAST_LINE_START;
				
		// Add a mouse listener to exampleTeacher label to direct students to use SimStudent tab for asking
		/*if(getSimStPLE() != null && getSimStPLE().getSimSt() != null && getSimStPLE().getSimSt()
				.isSsMetaTutorMode()) {
			overviewTeacher.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					showMenuWithTeacherMessage(me);
				}
			});
		}*/
		
		overviewTab.add(overviewTeacher, otConst);

		
		//tabPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>"+overviewTabTitle+"</body></html>", exampleTab);
		tabPane.addTab(overviewTabTitle, overviewTab);
		int index = tabPane.getTabCount()-1;
		tabPane.setBackgroundAt(index, unfocusColors[index]);
		//System.out.println ("");
		
	}

	private void setUpExampleTab()
	{
		exampleTab = new JPanel();
		exampleTab.setBackground(exampleColor);
		exampleTab.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));

		exampleTab.setLayout(new GridBagLayout());

		 
		try {
			exampleInterface = getStudentInterface().getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		exampleInterface.setPreferredSize(getStudentInterface().getPreferredSize());
		
		
		SimStPLE.setComponentFont(exampleInterface, FONT);
		
		
		JScrollPane interfacePane = new JScrollPane(exampleInterface);
		exampleInterface.setBackground(exampleColor);
		SimStPLE.setComponentEnabled(false,exampleInterface);
		exampleInterface.setAlignmentX(CENTER_ALIGNMENT);
		GridBagConstraints ipConst = new GridBagConstraints();
		ipConst.gridx = 0;
		ipConst.gridy = 0;
		ipConst.gridwidth = 1;
		ipConst.gridheight = 2;
		ipConst.weightx = .75;
		ipConst.weighty = .75;
		ipConst.fill = GridBagConstraints.BOTH;
		ipConst.anchor = GridBagConstraints.PAGE_START;
		exampleTab.add(interfacePane, ipConst);
		interfacePane.setPreferredSize(exampleInterface.getPreferredSize());

		exampleContainer = new JXTaskPaneContainer();
		exampleContainer.setBackground(exampleColor);
		
		JScrollPane exampleScroller = new JScrollPane(exampleContainer);
		GridBagConstraints epConst = new GridBagConstraints();
		epConst.gridx = 1;
		epConst.gridy = 0;
		epConst.gridwidth = 1;
		epConst.gridheight = 3;
		epConst.weightx = .25;
		epConst.weighty = 1;
		epConst.fill = GridBagConstraints.BOTH;
		epConst.anchor = GridBagConstraints.PAGE_START;
		exampleTab.add(exampleScroller, epConst);
		setUpExampleIndex(exampleContainer);
		
		exampleCommPane = new JPanel();
		exampleCommPane.setBackground(exampleColor);
		GridBagConstraints cpConst = new GridBagConstraints();
		cpConst.gridx = 0;
		cpConst.gridy = 2;
		cpConst.gridwidth = 1;
		cpConst.gridheight = 1;
		cpConst.weightx = .75;
		cpConst.weighty = .25;
		cpConst.fill = GridBagConstraints.BOTH;
		cpConst.anchor = GridBagConstraints.PAGE_START;
		exampleTab.add(exampleCommPane, cpConst);
		setUpExampleCommPanel(exampleCommPane);
				
		tabPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>"+exampleTabTitle+"</body></html>", exampleTab);
		//tabPane.addTab(exampleTabTitle, exampleTab);
		int index = tabPane.getTabCount()-1;
		tabPane.setBackgroundAt(index, unfocusColors[index]);
	}
	
	public static void showComponents(Component component)
	{
		if(component instanceof JCommTable)
		{
			JCommTable table = (JCommTable) component;
			int rows = table.getRows();
			int columns = table.getColumns();
			for(int r=0;r<rows;r++)
			{
				for(int c=0;c<columns;c++)
				{
					TableCell cell = table.getCell(r, c);
					cell.setText("");
					cell.setToolTipText("");
					//JOptionPane.showMessageDialog(null, "cell: " + cell.getCommName());
				}
			}
			
		}
		else if(component instanceof JCommTextField)
		{
			JCommTextField field = (JCommTextField) component;
			field.setText("");
			field.setToolTipText("");
		}
		else if(component instanceof JCommTextArea)
		{
			JCommTextArea area = (JCommTextArea) component;
			area.setText("");
			area.setToolTipText("");
		}
		else if(component instanceof JCommLabel)
		{
			JCommLabel label = (JCommLabel) component;
			label.setToolTipText("");
		}
		else if(component instanceof JCommButton)
		{
			JCommButton button = (JCommButton) component;
			button.setToolTipText("");
			//JOptionPane.showMessageDialog(null, "Button: " + button.getName());
			//JOptionPane.showMessageDialog(null, "Button: " + button.getCommName());
			button.setBorder(BorderFactory.createEmptyBorder());
		} else if(component instanceof Container)
		{
			Container container = (Container) component;
			for(Component comp: container.getComponents())
			{
				showComponents(comp);
			}
		}
	}
	
	
	

	private void setUpQuizTab()
	{
		quizTab = new JPanel();
		quizTab.setBackground(quizColor);
		quizTab.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));

		quizTab.setLayout(new GridBagLayout());


		try {
			quizInterface = getStudentInterface().getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}				
		
		
		quizInterface.setPreferredSize(getStudentInterface().getPreferredSize());
		//quizInterface.setMinimumSize(getStudentInterface().getPreferredSize());
		//quizInterface.setMaximumSize(getStudentInterface().getPreferredSize());
		
		
		if (getBrController().getMissController().getSimSt().isSs2014FractionAdditionAdhoc()){
			quizInterface.getComponent(17).setVisible(false);
			quizInterface.getComponent(16).setVisible(false);
			quizInterface.getComponent(32).setVisible(false);
		}
		
		SimStPLE.setComponentFont(quizInterface, FONT);
				
		JScrollPane interfacePane = new JScrollPane(quizInterface);
		quizInterface.setBackground(quizColor);
		SimStPLE.setComponentEnabled(false, quizInterface);
		quizInterface.setAlignmentX(CENTER_ALIGNMENT);
		GridBagConstraints ipConst = new GridBagConstraints();
		ipConst.gridx = 0;
		ipConst.gridy = 0;
		ipConst.gridwidth = 1;
		ipConst.gridheight = 2;
		ipConst.weightx = .75;
		ipConst.weighty = .75;
		ipConst.fill = GridBagConstraints.BOTH;
		ipConst.anchor = GridBagConstraints.PAGE_START;
		quizTab.add(interfacePane, ipConst);
		interfacePane.setPreferredSize(quizInterface.getPreferredSize());
		
		quizContainer = new JXTaskPaneContainer();
		quizContainer.setBackground(quizColor);
		
		JScrollPane quizScroller = new JScrollPane(quizContainer);
		GridBagConstraints qpConst = new GridBagConstraints();
		qpConst.gridx = 1;
		qpConst.gridy = 0;
		qpConst.gridwidth = 1;
		qpConst.gridheight = 3;
		qpConst.weightx = .25;
		qpConst.weighty = 1;
		qpConst.fill = GridBagConstraints.BOTH;
		qpConst.anchor = GridBagConstraints.PAGE_START;
		quizTab.add(quizScroller, qpConst);
		setUpQuizIndex(quizContainer);
		
		JPanel commPane = new JPanel();
		commPane.setBackground(quizColor);
		GridBagConstraints cpConst = new GridBagConstraints();
		cpConst.gridx = 0;
		cpConst.gridy = 2;
		cpConst.gridwidth = 1;
		cpConst.gridheight = 1;
		cpConst.weightx = .75;
		cpConst.weighty = .25;
		cpConst.fill = GridBagConstraints.BOTH;
		cpConst.anchor = GridBagConstraints.PAGE_START;
		quizTab.add(commPane, cpConst);
		
		setUpQuizCommPanel(commPane);
		
		//tabPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>"+quizTabTitle+"</body></html>", exampleTab);
		if (getDisplayQuiz()){
			tabPane.addTab(quizTabTitle, quizTab);
			int index = tabPane.getTabCount()-1;
			tabPane.setBackgroundAt(index, unfocusColors[index]);
		}
	}
	
	private StudentAvatarDisplay tutoringAvatar;
	private JLabel studentName;
	private JTextArea tutoringSpeechText;
	private JScrollPane speechScroller;
	private JComboBox tutoringSpeechEntry;
	private JXButton yesButton;
	private JXButton noButton;
	private JXButton submitButton;
	private GridBagConstraints yesConst;
	private GridBagConstraints noConst;
	private JPanel dialogPanel;
	private JPanel yesPanel, noPanel, submitPanel;
	
	private JPanel aplusControlConfirmPanel;
	private JLabel aplusControlConfirmLabel;
	private JXButton aplusControlConfirmButton;
	
	private JFrame aplusControlConfirmationFrame;
	public boolean aplusControlConfirmationFrameClicked=false;
	public boolean aplusControlConfirmationFrameShown=false;
	boolean t=false;
	
	public void setAplusControlConfirmPanelVisible(boolean flag){

		
		String message=brController.getMissController().getSimStPLE().getConversation().getMessage(SimStConversation.COG_TUTOR_START_PROBLEM_TOPIC);
		aplusControlConfirmLabel.setText(message);
		aplusControlConfirmPanel.setVisible(flag); 
		
	/*	if  (flag && !aplusControlConfirmationFrameClicked && !aplusControlConfirmationFrameShown){
			 aplusControlConfirmationFrame = new JFrame("");
			 
			 aplusControlConfirmationFrameShown=true;
			 
			//this.setUndecorated(true);
			 aplusControlConfirmationFrame.setBounds(600, 100, 413, 98);

			 aplusControlConfirmationFrame.setLocationRelativeTo(brController.getMissController().getSimStPLE().getSimStPeerTutoringPlatform().getStudentInterface());
			 
			 
			 aplusControlConfirmationFrame.setLayout(new FlowLayout());
			//aplusControlConfirmationFrame.getContentPane().add(new JLabel(" HEY!!!"));
			aplusControlConfirmationFrame.setUndecorated(true);
			aplusControlConfirmationFrame.setAlwaysOnTop(true);
			
			aplusControlConfirmationFrame.getContentPane().setBackground(studentColor);
			
			
			
			aplusControlConfirmPanel = new JPanel();		
			aplusControlConfirmPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),new EmptyBorder(5, 5, 5, 5)));
			
			aplusControlConfirmPanel.setLayout(new GridBagLayout());
			aplusControlConfirmPanel.setBackground(studentColor);
			
			//aplusControlConfirmPanel.setBackground(studentColor);
			
			aplusControlConfirmLabel = new JLabel("When you are ready to start your practice, click OK");
			aplusControlConfirmLabel.setBorder(new EmptyBorder(0,0,5,0));
			aplusControlConfirmLabel.setFont(this.S5_MED_FONT);
			GridBagConstraints avConst = new GridBagConstraints();
			avConst.gridx = 0;
			avConst.gridy = 0;
			avConst.anchor = GridBagConstraints.CENTER;
			aplusControlConfirmPanel.add(aplusControlConfirmLabel, avConst);
			
			
			
			
			
			JXButton jButton1=new JXButton("");
			jButton1.setIcon(createImageIcon("img/confirm.png"));
			
			jButton1.setBackground(new Color(219,184,156));
			
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BorderLayout());
			//buttonPanel.setBackground(studentColor);
			buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());		
			buttonPanel.add(jButton1, BorderLayout.CENTER);
			
			
			GridBagConstraints avConst1 = new GridBagConstraints();
			avConst1.gridx = 0;
			avConst1.gridy = 1;
			avConst1.anchor = GridBagConstraints.CENTER;
			aplusControlConfirmPanel.add(buttonPanel,avConst1);
			
			
			
			aplusControlConfirmationFrame.getContentPane().add(aplusControlConfirmPanel);
			//aplusControlConfirmationFrame.getContentPane().add(buttonPanel);
			jButton1.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e)
			    {
			    	aplusControlConfirmationFrameClicked=true;
			    	aplusControlConfirmationFrame.setVisible(false);
			    	aplusControlConfirmationFrame.dispose();
			    	aplusControlConfirmationFrameShown=false;
			    	brController.getMissController().getSimStPLE().getSsCognitiveTutor().startProblem();
					//aplusControlConfirmPanel.setVisible(false);
				
			    }
			});
			
			aplusControlConfirmationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//aplusControlConfirmationFrame.setSize(413, 98);
			aplusControlConfirmationFrame.setVisible(flag);
			
			
		}
		
		if (aplusControlConfirmationFrame!=null){
			aplusControlConfirmationFrame.revalidate();
			aplusControlConfirmationFrame.repaint();
			aplusControlConfirmPanel.revalidate();
			aplusControlConfirmPanel.repaint();		
		}
		*/
		
	}
	
	private void setUpCogTutorStartProblemButton (JPanel commPanel){
				
		aplusControlConfirmPanel = new JPanel();		
		aplusControlConfirmPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),new EmptyBorder(5, 5, 5, 5)));
		
		aplusControlConfirmPanel.setLayout(new GridBagLayout());
		//aplusControlConfirmPanel.setBackground(Color.GRAY.brighter());
		aplusControlConfirmPanel.setBackground(studentColor);
		
		aplusControlConfirmLabel = new JLabel("");
		aplusControlConfirmLabel.setBorder(new EmptyBorder(0,0,5,0));
		aplusControlConfirmLabel.setFont(this.S5_MED_FONT);
		GridBagConstraints avConst = new GridBagConstraints();
		avConst.gridx = 0;
		avConst.gridy = 0;
		avConst.anchor = GridBagConstraints.CENTER;
		//aplusControlConfirmPanel.add(aplusControlConfirmLabel, avConst);
				
		//aplusControlConfirmButton=new JXButton("OK");
		
	    aplusControlConfirmButton=new JXButton("");
		aplusControlConfirmButton.setIcon(createImageIcon("img/confirm_big2.png"));
		
		//aplusControlConfirmButton.setBackground(new Color(219,184,156));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		//buttonPanel.setBackground(studentColor);
		buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());		
		buttonPanel.add(aplusControlConfirmButton, BorderLayout.CENTER);
		
		//buttonPanel.setBounds(10,10,100,200);
		aplusControlConfirmButton.setPreferredSize(new Dimension(355,40));
		
		//aplusControlConfirmButton.setBounds(10,10,100,200);		
		//aplusControlConfirmButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));		
		//aplusControlConfirmButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black),new EmptyBorder(4, 4, 4, 4)));
		
		aplusControlConfirmButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
						brController.getMissController().getSimStPLE().getSsCognitiveTutor().startProblem();
						aplusControlConfirmPanel.setVisible(false);
			 	
					}});
		
		
		GridBagConstraints avConst1 = new GridBagConstraints();
		avConst1.gridx = 0;
		avConst1.gridy = 1;
		avConst1.anchor = GridBagConstraints.CENTER;
		aplusControlConfirmPanel.add(buttonPanel,avConst1);
		
		aplusControlConfirmPanel.setVisible(false);
		
		commPanel.add(aplusControlConfirmPanel);
			
	}
	
	
	private void setUpTutoringCommPanel(JPanel commPanel)
	{
		//commPanel.setLayout(new GridBagLayout());
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.X_AXIS));
		
		dialogPanel = new JPanel();
		dialogPanel.setLayout(new GridBagLayout());
		dialogPanel.setBackground(studentColor);
				
		JPanel avatarPanel = new JPanel();
		avatarPanel.setBorder(BorderFactory.createLineBorder(studentColor, BORDER_WIDTH));
		avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.Y_AXIS));
		avatarPanel.setBackground(studentColor);
		tutoringAvatar = new StudentAvatarDisplay(SimStPLE.STUDENT_IMAGE);
		avatarPanel.add(tutoringAvatar);
		GridBagConstraints avConst = new GridBagConstraints();
		avConst.gridx = 0;
		avConst.gridy = 0;
		avConst.gridwidth = 1;
		avConst.gridheight = 2;
		avConst.weightx = .2;
		avConst.weighty = .8;
		avConst.anchor = GridBagConstraints.CENTER;
		commPanel.add(avatarPanel, avConst);	
		this.setTutoringAvatarPanel(avatarPanel);
		studentName = new JLabel(SimSt.getSimStName());
		studentName.setAlignmentX(CENTER_ALIGNMENT);
		studentName.setAlignmentY(TOP_ALIGNMENT);
		studentName.setFont(S5_FONT);
		GridBagConstraints snConst = new GridBagConstraints();
		snConst.gridx = 0;
		snConst.gridy = 1;
		snConst.gridwidth = 1;
		snConst.gridheight = 1;
		snConst.weightx = .2;
		snConst.weighty = .15;
		snConst.anchor = GridBagConstraints.PAGE_START;
		//commPanel.add(studentName, snConst);
		avatarPanel.add(studentName);
		
		avatarPanel.setMinimumSize(new Dimension(tutoringAvatar.getPreferredSize().width+2*BORDER_WIDTH, 
				tutoringAvatar.getPreferredSize().height+BORDER_WIDTH+studentName.getPreferredSize().height));
		avatarPanel.setPreferredSize(new Dimension(tutoringAvatar.getPreferredSize().width+3*BORDER_WIDTH, 
				tutoringAvatar.getPreferredSize().height+2*BORDER_WIDTH+studentName.getPreferredSize().height));
		avatarPanel.setMaximumSize(new Dimension(tutoringAvatar.getPreferredSize().width+2*BORDER_WIDTH, 
				tutoringAvatar.getPreferredSize().height+2*BORDER_WIDTH+studentName.getPreferredSize().height));	
		
		commPanel.add(dialogPanel);

		tutoringSpeechText = new JTextArea();
		tutoringSpeechText.setLineWrap(true);
		tutoringSpeechText.setWrapStyleWord(true);
		tutoringSpeechText.setFont(MED_FONT);
		tutoringSpeechText.setEditable(false);
		tutoringSpeechText.setBorder(BorderFactory.createLineBorder(Color.white, BORDER_WIDTH));
		DefaultCaret caret = (DefaultCaret) tutoringSpeechText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		speechScroller = new JScrollPane(tutoringSpeechText);
		speechScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		speechScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		GridBagConstraints stConst = new GridBagConstraints();
		stConst.gridx = 1;
		stConst.gridy = 0;
		stConst.gridwidth = 4;
		stConst.gridheight = 1;
		stConst.weightx = .8;
		stConst.weighty = .8;
		stConst.fill = GridBagConstraints.BOTH;
		stConst.insets = BORDER;
		stConst.anchor = GridBagConstraints.PAGE_START;
		dialogPanel.add(speechScroller, stConst);
		speechScroller.setMinimumSize(tutoringAvatar.getPreferredSize());
		speechScroller.setPreferredSize(new Dimension(avatarPanel.getWidth(),tutoringAvatar.getPreferredSize().height));
		
		/*JPopupMenu popup = new JPopupMenu();
		popup.add(new JMenuItem("Erase back through here."));
		tutoringSpeechText.addMouseListener(new PopupListener(popup));*/
		
		tutoringSpeechEntry = new JComboBox();
		tutoringSpeechEntry.setFont(MED_FONT);
		tutoringSpeechEntry.setUI(new BasicComboBoxUI() {
		    @Override
		    protected JButton createArrowButton() {
		        return new JButton() {
		                @Override
		                public int getWidth() {
		                        return 0;
		                }
		        };
		    }
		    @Override
		    public void setPopupVisible(JComboBox c, boolean v) {
		        // keeps the popup from coming down if there's nothing in the combo box
		        if (c.getItemCount() > 0) {
		            super.setPopupVisible(c, v);
		        }
		    }

		});
		tutoringSpeechEntry.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		GridBagConstraints entConst = new GridBagConstraints();
		entConst.gridx = 1;
		entConst.gridy = 1;
		entConst.gridwidth = 3;
		entConst.gridheight = 1;
		entConst.weightx = .75;
		entConst.weighty = .15;
		entConst.insets = BORDER;
		entConst.fill = GridBagConstraints.HORIZONTAL;
		entConst.anchor = GridBagConstraints.CENTER;
		dialogPanel.add(tutoringSpeechEntry, entConst);
		tutoringSpeechEntry.setVisible(false);
				
		
		yesButton = new JXButton("Yes");
		//yesButton.setBackgroundPainter(getPainter(true,new Point2D.Float(100, 50),studentUnfocusColor, studentColor));
		yesConst = new GridBagConstraints();
		yesConst.gridx = 1;
		yesConst.gridy = 1;
		yesConst.gridwidth = 1;
		yesConst.gridheight = 1;
		yesConst.weightx = .25;
		yesConst.weighty = .15;
		//yesConst.fill = GridBagConstraints.BOTH;
		yesConst.anchor = GridBagConstraints.CENTER;
		//dialogPanel.add(yesButton, yesConst);
		//yesButton.setVisible(false);
		//yesButton.setForeground(Color.black);
		//yesButton.setBackground(studentUnfocusColor);

		yesPanel = new JPanel();
		yesPanel.setLayout(new BorderLayout());
		yesPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		dialogPanel.add(yesPanel, yesConst);
		yesPanel.add(yesButton, BorderLayout.CENTER);
		yesPanel.setVisible(false);
		
		noButton = new JXButton("No");
		//noButton.setBackgroundPainter(getPainter(true,new Point2D.Float(100, 50),studentUnfocusColor, studentColor));
		noConst = new GridBagConstraints();
		noConst.gridx = 2;
		noConst.gridy = 1;
		noConst.gridwidth = 1;
		noConst.gridheight = 1;
		noConst.weightx = .25;
		noConst.weighty = .15;
		//noConst.fill = GridBagConstraints.BOTH;
		noConst.anchor = GridBagConstraints.LINE_START;
		//dialogPanel.add(noButton, noConst);
		//noButton.setVisible(false);
		//noButton.setForeground(Color.black);
		//noButton.setBackground(studentUnfocusColor);

		noPanel = new JPanel();
		noPanel.setLayout(new BorderLayout());
		noPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		dialogPanel.add(noPanel, noConst);
		noPanel.add(noButton, BorderLayout.CENTER);
		noPanel.setVisible(false);
		
		submitButton = new JXButton("Submit");
		//submitButton.setBackgroundPainter(getPainter(true,new Point2D.Float(100, 50),studentUnfocusColor, studentColor));
		GridBagConstraints suConst = new GridBagConstraints();
		suConst.gridx = 4;
		suConst.gridy = 1;
		suConst.gridwidth = 1;
		suConst.gridheight = 1;
		suConst.weightx = .25;
		suConst.weighty = .15;
		//suConst.fill = GridBagConstraints.BOTH;
		suConst.anchor = GridBagConstraints.CENTER;
		//dialogPanel.add(submitButton, suConst);
		//submitButton.setVisible(false);
		
		submitPanel = new JPanel();
		submitPanel.setLayout(new BorderLayout());
		submitPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		dialogPanel.add(submitPanel, suConst);
		submitPanel.add(submitButton, BorderLayout.CENTER);
		submitPanel.setVisible(false);
		//submitButton.setForeground(Color.black);
		//submitButton.setBackground(studentUnfocusColor);
		
		JLabel spacer = new JLabel(" ");
		spacer.setBackground(studentColor);
		dialogPanel.add(spacer, suConst);
				
	}
	
	JLabel overviewTeacherBankTab;
	private Component problemBankComponent = null;
	// add a problem bank tab
	private void setUpProblemBankTab()
	{
		bankTab = new JPanel();
		bankTab.setBackground(bankColor);
		bankTab.setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH,BORDER_WIDTH));


		String[] columns = {"Problem","Type","Difficulty"};
		getSimStPLE().readProblemStatisticFile();
		problemBankComponent = getSimStPLE().getSimStPeerTutoringPlatform().createProblemBankNew(columns, getSimStPLE().problemStatData);
		problemBankComponent.setSize(700, 700);
		bankTab.setLayout(new GridBagLayout());

		GridBagConstraints ovConst = new GridBagConstraints();
		ovConst.gridx = 1;
		ovConst.gridy = 0;
		ovConst.gridwidth = 1;
		ovConst.gridheight = 2;
		ovConst.weightx = .95;
		ovConst.weighty = .75;
		ovConst.fill = GridBagConstraints.BOTH;		
		bankTab.add(problemBankComponent,ovConst);


		JPanel avatarPanel = new JPanel();
		avatarPanel.setBackground(overviewColor);
		
		if (brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
			overviewTeacherBankTab = new JLabel(createImageIcon(SimStPLE.TEACHER_BANK_IMAGE_COGTUTOR));
		else
			overviewTeacherBankTab = new JLabel(createImageIcon(SimStPLE.TEACHER_BANK_IMAGE));

		GridBagConstraints otConst = new GridBagConstraints();
		otConst.gridx = 0;
		otConst.gridy = 1;
		otConst.gridwidth = 1;
		otConst.gridheight = 1;
		otConst.weightx = .05;
		otConst.weighty = .25;
		otConst.fill = GridBagConstraints.HORIZONTAL;
		otConst.anchor = GridBagConstraints.LAST_LINE_START;

		// Add a mouse listener to exampleTeacher label to direct students to use SimStudent tab for asking
		/*if(getSimStPLE() != null && getSimStPLE().getSimSt() != null && getSimStPLE().getSimSt()
				.isSsMetaTutorMode()) {
			overviewTeacherBankTab.addMouseListener(new MouseAdapter() {

				public void mousePressed(MouseEvent me) {
					showMenuWithTeacherMessage(me);
				}

				public void mouseEntered(MouseEvent me){
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
					setCursor(cursor);	
				}

				public void mouseExited(MouseEvent me){					
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
					setCursor(cursor);			     
				}


			});
		}*/

		bankTab.add(overviewTeacherBankTab, otConst);


		//tabPane.addTab("<html><body leftmargin=15 topmargin=8 marginwidth=15 marginheight=5>"+overviewTabTitle+"</body></html>", exampleTab);
		tabPane.addTab(bankTabTitle, bankTab);
		int index = tabPane.getTabCount()-1;
		tabPane.setBackgroundAt(index, unfocusColors[index]);
	}

		
		
	public JXButton nextProblemButton = null;
    JXButton quizButton = null;
    public JXButton restartButton = null;
    JXButton undoButton = null;
    
    JXButton nextProblemButtonQuiz = null;
    public JXButton restartButtonQuiz = null;
    public JXButton quizButtonQuiz = null;
    JXButton undoButtonQuiz = null;

    JXButton nextProblemButtonEx = null;
    JXButton restartButtonEx = null;
    JXButton quizButtonEx = null;
    JXButton undoButtonEx = null;
    
    private void createButtons()
    {
		nextProblemButton = new JXButton();
		nextProblemButton.setIcon(createImageIcon("img/new.png"));
		
		restartButton = new JXButton();
		
		restartButton.setIcon(createImageIcon("img/restart.png"));
		
		undoButton = new JXButton();
		undoButton.setIcon(createImageIcon("img/erase.png"));
		
		quizButton = new JXButton();
		quizButton.setIcon(createImageIcon("img/quiz.png"));

		nextProblemButtonQuiz = new JXButton();
		nextProblemButtonQuiz.setIcon(createImageIcon("img/new.png"));
		
		restartButtonQuiz = new JXButton();
		restartButtonQuiz.setIcon(createImageIcon("img/restart.png"));
		
		undoButtonQuiz = new JXButton();
		undoButtonQuiz.setIcon(createImageIcon("img/erase.png"));
		
		quizButtonQuiz = new JXButton();
		quizButtonQuiz.setIcon(createImageIcon("img/quiz.png"));

		nextProblemButtonEx = new JXButton();
		nextProblemButtonEx.setIcon(createImageIcon("img/new.png"));
		
		restartButtonEx = new JXButton();
		restartButtonEx.setIcon(createImageIcon("img/restart.png"));
		
		undoButtonEx = new JXButton();
		undoButtonEx.setIcon(createImageIcon("img/erase.png"));
		
		quizButtonEx = new JXButton();
		quizButtonEx.setIcon(createImageIcon(getQuizButtonImage()));

		setNextProblemButtonText(getSimStPLE().getNextProblemButtonTitleString());
		setRestartButtonText(getSimStPLE().getRestartButtonTitleString());
		setUndoButtonText(getSimStPLE().getUndoButtonTitleString());
		setQuizButtonText(getSimStPLE().getQuizButtonTitleString());
		

        
        nextProblemButton.setActionCommand(SimStPLE.NEXT_PROBLEM);
        nextProblemButton.addActionListener(actionListener);
        nextProblemButtonQuiz.setActionCommand(SimStPLE.NEXT_PROBLEM);
        nextProblemButtonQuiz.addActionListener(actionListener);
        nextProblemButtonEx.setActionCommand(SimStPLE.NEXT_PROBLEM);
        nextProblemButtonEx.addActionListener(actionListener);
        
        undoButton.setActionCommand(SimStPLE.UNDO);
        undoButton.addActionListener(actionListener);
        undoButtonQuiz.setActionCommand(SimStPLE.UNDO);
        undoButtonQuiz.addActionListener(actionListener);
        undoButtonEx.setActionCommand(SimStPLE.UNDO);
        undoButtonEx.addActionListener(actionListener);
        
        restartButton.setActionCommand(SimStPLE.RESTART);
        restartButton.addActionListener(actionListener);
        restartButtonQuiz.setActionCommand(SimStPLE.RESTART);
        restartButtonQuiz.addActionListener(actionListener);
        restartButtonEx.setActionCommand(SimStPLE.RESTART);
        restartButtonEx.addActionListener(actionListener);
        
        quizButton.setActionCommand(SimStPLE.QUIZ);
        quizButton.addActionListener(actionListener);
        quizButtonQuiz.setActionCommand(SimStPLE.QUIZ);
        quizButtonQuiz.addActionListener(actionListener);
        quizButtonEx.setActionCommand(SimStPLE.QUIZ);
        quizButtonEx.addActionListener(actionListener);
        
        setNextProblemButtonEnabled(true);
        setRestartButtonEnabled(true);
        setUndoButtonEnabled(true);
        setQuizButtonEnabled(true);
        
        
     

    }
    
    
	private void setUpButtonPanel(JPanel buttonPanel)
	{
		buttonPanel.setBorder(BorderFactory.createLineBorder(studentColor, BORDER_WIDTH));
		//GridLayout layout = new GridLayout(2,2);
			
		GridLayout layout = new GridLayout(1,3);
		
		layout.setHgap(BORDER_WIDTH);
		layout.setVgap(BORDER_WIDTH);
		buttonPanel.setLayout(layout);

		buttonPanel.setPreferredSize(new Dimension(metaTutorComponent.getPreferredSize().width, 30));
		buttonPanel.setMaximumSize(new Dimension(metaTutorComponent.getPreferredSize().width, 30));

		createButtons();


		JPanel nextProblemPanel = new JPanel();
		nextProblemPanel.setLayout(new BorderLayout());
		nextProblemPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(nextProblemPanel);
		nextProblemPanel.add(nextProblemButton, BorderLayout.CENTER);

		
		
		
		JPanel restartPanel = new JPanel();
		restartPanel.setLayout(new BorderLayout());
		restartPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(restartPanel);
		restartPanel.add(restartButton, BorderLayout.CENTER);
		

		
		


		JPanel undoPanel = new JPanel();
		undoPanel.setLayout(new BorderLayout());
		undoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(undoPanel);
		undoPanel.add(undoButton, BorderLayout.CENTER);

		
	
	/*	JPanel quizPanel = new JPanel();
		quizPanel.setLayout(new BorderLayout());
		quizPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(quizPanel);
		quizPanel.add(quizButton, BorderLayout.CENTER);
	*/
	}
	
	private void setUpButtonPanelCogTutor(JPanel buttonPanel)
	{
		buttonPanel.setBorder(BorderFactory.createLineBorder(studentColor, BORDER_WIDTH));
		//GridLayout layout = new GridLayout(2,2);

		GridLayout layout = new GridLayout(1,3);

		layout.setHgap(BORDER_WIDTH);
		layout.setVgap(BORDER_WIDTH);
		buttonPanel.setLayout(layout);

		buttonPanel.setPreferredSize(new Dimension(metaTutorComponent.getPreferredSize().width, 30));
		buttonPanel.setMaximumSize(new Dimension(metaTutorComponent.getPreferredSize().width, 30));

		createButtons();

		JPanel restartPanel = new JPanel();
		restartPanel.setLayout(new BorderLayout());
		restartPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(restartPanel);
		restartPanel.add(restartButton, BorderLayout.CENTER);



		JPanel nextProblemPanel = new JPanel();
		nextProblemPanel.setOpaque(false);
		buttonPanel.add(nextProblemPanel);



		JPanel undoPanel = new JPanel();
		undoPanel.setOpaque(false);
		buttonPanel.add(undoPanel);

	}
	
	
	private void setUpButtonPanelAplusCogTutor(JPanel buttonPanel)
	{
		buttonPanel.setBorder(BorderFactory.createLineBorder(studentColor, BORDER_WIDTH));
		//GridLayout layout = new GridLayout(2,2);

		GridLayout layout = new GridLayout(1,3);

		layout.setHgap(BORDER_WIDTH);
		layout.setVgap(BORDER_WIDTH);
		buttonPanel.setLayout(layout);

		buttonPanel.setPreferredSize(new Dimension(metaTutorComponent.getPreferredSize().width, 30));
		buttonPanel.setMaximumSize(new Dimension(metaTutorComponent.getPreferredSize().width, 30));

		createButtons();

		JPanel nextProblemPanel = new JPanel();
		nextProblemPanel.setLayout(new BorderLayout());
		nextProblemPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(nextProblemPanel);
		nextProblemPanel.add(nextProblemButton, BorderLayout.CENTER);

	
		
		JPanel restartPanel = new JPanel();
		restartPanel.setLayout(new BorderLayout());
		restartPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(restartPanel);
		restartPanel.add(restartButton, BorderLayout.CENTER);


		JPanel undoPanel = new JPanel();
		undoPanel.setOpaque(false);
		buttonPanel.add(undoPanel);

	}
	
	
	private void setUpQuizButtonPanel(JPanel buttonPanel)
	{
		//buttonPanel.setBorder(BorderFactory.createLineBorder(quizColor, BORDER_WIDTH));
			
		GridLayout layout = new GridLayout(1,3);
			
		layout.setHgap(BORDER_WIDTH);
		layout.setVgap(BORDER_WIDTH);
		buttonPanel.setLayout(layout);
	
		
		buttonPanel.setPreferredSize(new Dimension(metaTutorComponent.getPreferredSize().width, 70));
		buttonPanel.setMaximumSize(new Dimension(metaTutorComponent.getPreferredSize().width, 70));
		
		JPanel quizPanel = new JPanel();
		quizPanel.setLayout(new BorderLayout());
		quizPanel.setBorder(BorderFactory.createRaisedBevelBorder());
	
		buttonPanel.add(quizPanel);
		quizPanel.add(quizButtonQuiz, BorderLayout.CENTER);
		
		
		JPanel nextProblemPanel = new JPanel();
		//nextProblemPanel.setLayout(new BorderLayout());
		//nextProblemPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		nextProblemPanel.setOpaque(false);
		buttonPanel.add(nextProblemPanel);
	//	nextProblemPanel.add(nextProblemButtonQuiz, BorderLayout.CENTER);
				
		JPanel restartPanel = new JPanel();
		//restartPanel.setLayout(new BorderLayout());
		//restartPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		restartPanel.setOpaque(false);
		buttonPanel.add(restartPanel);
	//	restartPanel.add(restartButtonQuiz, BorderLayout.CENTER);
	//	setRestartButtonEnabled(true);
		
	/*	JPanel undoPanel = new JPanel();
		undoPanel.setLayout(new BorderLayout());
		undoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		buttonPanel.add(undoPanel);
		undoPanel.add(undoButtonQuiz, BorderLayout.CENTER);
		setUndoButtonEnabled(true);
	*/	
		
		
		setQuizButtonEnabled(true);
	}
	
	private void setUpQuizButtonPanelCogTutor(JPanel buttonPanel)
	{
		//buttonPanel.setBorder(BorderFactory.createLineBorder(quizColor, BORDER_WIDTH));
			
		GridLayout layout = new GridLayout(1,3);
			
		layout.setHgap(BORDER_WIDTH);
		layout.setVgap(BORDER_WIDTH);
		buttonPanel.setLayout(layout);
	
		
		buttonPanel.setPreferredSize(new Dimension(metaTutorComponent.getPreferredSize().width, 70));
		buttonPanel.setMaximumSize(new Dimension(metaTutorComponent.getPreferredSize().width, 70));
		


		JPanel undoPanel = new JPanel();
		undoPanel.setOpaque(false);
		buttonPanel.add(undoPanel);
		
		JPanel restartPanel = new JPanel();
		restartPanel.setLayout(new BorderLayout());
		restartPanel.setBorder(BorderFactory.createRaisedBevelBorder());
	
		buttonPanel.add(restartPanel);
		restartPanel.add(restartButtonQuiz, BorderLayout.CENTER);
		
		

		JPanel undoPanel1 = new JPanel();
		undoPanel1.setOpaque(false);
		buttonPanel.add(undoPanel1);
		
		restartButtonQuiz.setEnabled(false);

		//setQuizButtonEnabled(true);
	}
	
	
	
	
	
	
	
	private void setUpExampleButtonPanel(JPanel buttonPanel)
	{
		//buttonPanel.setBorder(BorderFactory.createLineBorder(exampleColor, BORDER_WIDTH));
			
		GridLayout layout = new GridLayout(1,3);
		
		layout.setHgap(BORDER_WIDTH);
		layout.setVgap(BORDER_WIDTH);
		buttonPanel.setLayout(layout);
		
		buttonPanel.setPreferredSize(new Dimension(metaTutorComponent.getPreferredSize().width, 70));
		buttonPanel.setMaximumSize(new Dimension(metaTutorComponent.getPreferredSize().width, 70));
		
		
		JPanel nextProblemPanel = new JPanel();
		//nextProblemPanel.setLayout(new BorderLayout());
		//nextProblemPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		nextProblemPanel.setOpaque(false);
		buttonPanel.add(nextProblemPanel);
		//nextProblemPanel.add(nextProblemButtonEx, BorderLayout.CENTER);
				
		JPanel restartPanel = new JPanel();
		//restartPanel.setLayout(new BorderLayout());
		//restartPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		restartPanel.setOpaque(false);
		buttonPanel.add(restartPanel);
		////restartPanel.add(restartButtonEx, BorderLayout.CENTER);
		setRestartButtonEnabled(true);
		
		JPanel undoPanel = new JPanel();
		//undoPanel.setLayout(new BorderLayout());
		//undoPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		undoPanel.setOpaque(false);
		buttonPanel.add(undoPanel);
		//undoPanel.add(undoButtonEx, BorderLayout.CENTER);
		//setUndoButtonEnabled(true);
		
		/*JPanel quizPanel = new JPanel();
		//quizPanel.setLayout(new BorderLayout());
		//quizPanel.setBorder(BorderFactory.createRaisedBevelBorder());
		quizPanel.setOpaque(false);
		buttonPanel.add(quizPanel);
		//quizPanel.add(quizButtonEx, BorderLayout.CENTER);
		//setQuizButtonEnabled(true);*/
	}
	
	private JLabel exampleTeacher;
	//private JTextArea exampleSpeechText;
	private JTextPane exampleSpeechText;
	
	public JComponent metaTutorComponent;
	
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
	
	private void setUpExampleCommPanel_old(JPanel commPanel)
	{
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.X_AXIS));
		
		JPanel avatarPanel = new JPanel();
		avatarPanel.setBackground(exampleColor);
		exampleTeacher = new JLabel(createImageIcon(SimStPLE.TEACHER_IMAGE));
				
		// Add a mouse listener to exampleTeacher label to direct students to use SimStudent tab for asking
		/*if(getSimStPLE() != null && getSimStPLE().getSimSt() != null && getSimStPLE().getSimSt()
				.isSsMetaTutorMode()) {
			exampleTeacher.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					showMenuWithTeacherMessage(me);
				}
			});
		}*/
		
		commPanel.add(exampleTeacher);

		exampleSpeechText = new JTextPane();
		exampleSpeechText.setFont(FONT);
		exampleSpeechText.setEditable(false);
		exampleSpeechText.setBorder(BorderFactory.createLineBorder(Color.white, BORDER_WIDTH));
		JScrollPane speechScroller = new JScrollPane(exampleSpeechText);
		speechScroller.setBorder(BorderFactory.createLineBorder(exampleColor, BORDER_WIDTH));
		
		commPanel.add(speechScroller);
	}

	private void setUpExampleCommPanel(JPanel commPanel)
	{
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.X_AXIS));
		
		JPanel avatarPanel = new JPanel();
		avatarPanel.setBackground(exampleColor);
		exampleTeacher = new JLabel(createImageIcon(SimStPLE.TEACHER_IMAGE));
				
		// Add a mouse listener to exampleTeacher label to direct students to use SimStudent tab for asking
		/*if(getSimStPLE() != null && getSimStPLE().getSimSt() != null && getSimStPLE().getSimSt().isSsMetaTutorMode()) {
			    exampleTeacher.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					showMenuWithTeacherMessage(me);
				}
				public void mouseEntered(MouseEvent me){
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
				     setCursor(cursor);	
				}
				
				public void mouseExited(MouseEvent me){					
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
					     setCursor(cursor);			     
				}
				
			});
		}*/
		
		commPanel.add(exampleTeacher);
		
		/*get the board dimensions. Everything should be inside here*/
	    ImageIcon boardImg=createImageIcon(SimStPLE.BOARD_IMAGE);
	    
		JLayeredPane boardLayeredPane= new JLayeredPane();
		boardLayeredPane.setPreferredSize(new Dimension(boardImg.getIconWidth(), boardImg.getIconHeight()));
		
		JPanel boardPanel = new JPanel();
		boardPanel.setOpaque(false);

	     JLabel theBoard= new JLabel(createImageIcon(SimStPLE.BOARD_IMAGE));
	    theBoard.setLayout(new BorderLayout());
	    boardLayeredPane.add(theBoard,new Integer(50));
		exampleSpeechText = new JTextPane();
		exampleSpeechText.setFont(BOARD_FONT);
		exampleSpeechText.setEditable(false);
		exampleSpeechText.setForeground(Color.white);
	    exampleSpeechText.setOpaque(false);
	    
	   	    
	    
		JScrollPane speechScroller = new JScrollPane(exampleSpeechText);
		speechScroller.setBorder(BorderFactory.createEmptyBorder(65, 30, 20, 35));
		speechScroller.setOpaque(false);
		speechScroller.getViewport().setOpaque(false);
		speechScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		speechScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
				
		exampleSpeechText.setText(getSimStPLE().EXAMPLE_COMM_TEXT);
		
		
		
		theBoard.add(speechScroller);
		
		/*initialize the panel that will hold the buttons*/
		JPanel buttonPane = new JPanel();
		buttonPane=setUpExamplePlayerButtons(buttonPane);
		
		
		theBoard.setBounds( 0, 0, boardImg.getIconWidth(), boardImg.getIconHeight());
		buttonPane.setBounds( 130, -10,  140, 100 );
		
		boardLayeredPane.add(buttonPane,new Integer(150));
	
		boardPanel.add(boardLayeredPane);
		
		commPanel.add(boardPanel);
		
		
	}
	
	/** nbarba 01/21/2014  
	 * function that adds step by step view player for the examples tab
	 * 
	 * */
	public JButton fbwdButton = null; 
	public JButton prevButton = null;
    public JButton nextButton = null;
	public JButton ffwdButton = null;
	public int nextButtonClicked = 0;
	public boolean isLogged = false;
	private JPanel setUpExamplePlayerButtons(JPanel buttonPane){
		
		
		GridLayout buttonsLayout = new GridLayout(1,4);	
		buttonPane.setLayout(buttonsLayout);
		buttonPane.setOpaque(false);
		
		fbwdButton = new JButton(createImageIcon(SimStPLE.FBWD_EXAMPLE_IMAGE));
		fbwdButton.setOpaque(false);
		fbwdButton.setContentAreaFilled(false);
		fbwdButton.setBorderPainted(false);
	//	buttonPane.add(fbwdButton);
		
		
	    prevButton = new JButton(createImageIcon(SimStPLE.PREVIOUS_EXAMPLE_IMAGE));
		prevButton.setOpaque(false);
		prevButton.setContentAreaFilled(false);
		prevButton.setBorderPainted(false);
		prevButton.setEnabled(false);
		buttonPane.add(prevButton);
		
		nextButton = new JButton(createImageIcon(SimStPLE.NEXT_EXAMPLE_IMAGE));
		nextButton.setOpaque(false);
		nextButton.setContentAreaFilled(false);
		nextButton.setEnabled(false);
		buttonPane.add(nextButton);
		
		ffwdButton = new JButton(createImageIcon(SimStPLE.FFWD_EXAMPLE_IMAGE));
		ffwdButton.setOpaque(false);
		ffwdButton.setContentAreaFilled(false);
		ffwdButton.setBorderPainted(false);
		//buttonPane.add(ffwdButton);
		
		
		
		
		nextButton.addMouseListener(new MouseAdapter() {
			  @Override
			  public void mouseClicked(MouseEvent e) {
				  		  
			  	/*If example has been initalized and we are not in the last step*/
				  
				  //System.out.println(" Current Step : "+currentStep+"  Last Step : "+exampleTemp.getLastStep());
				  if (!currentStep.equals("-1") && !currentStep.equals(exampleTemp.getLastStep())){
					  
					  

					  	//	logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_PLAYER_BUTTON, BUTTON_NEXT, logger.getCurrentTime());
							
					  
				  			clearInterface(exampleInterface);
				  			nextButtonClicked++;
				  			if(nextButtonClicked >= 2 && !brController.getMissController().getSimSt().getModelTraceWM().isExampleProblemViewed()){
				  				//System.out.println("Next button Clicked : "+nextButtonClicked);
				  				 brController.getMissController().getSimSt().getModelTraceWM().setExampleProblemViewed(true);
				  			}
				  			
				  			String nextStep=exampleTemp.getNextStep4Display(currentStep);
				  			
				  			currentStep=nextStep;
				  			/*get the FOA elements that need to be displayed*/
				  			validSteps4display.clear();
				  			validSteps4display = exampleTemp.getValidSteps4display(nextStep);
	
				  			setSpeechHTML(exampleTemp.getStepTooltipHover(nextStep));
				  			//System.out.println(" To be filled : "+exampleTemp+ " current Step : "+currentStep+ " validStep4dispaly :"+validSteps4display);
				  			fillInExampleStep(exampleInterface,exampleTemp);
				  			if(currentStep.equals("done") && !isLogged) {
				  				 long exampleDuration = (Calendar.getInstance().getTimeInMillis() - actionListener.getExampleStartTime())/1000;
						    		logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_VIEW_END, 
						    				"","FinalStep", "", (int) exampleDuration);
						    		isLogged = true;
				  			}
				  			
				  			if(!prevButton.isEnabled())
				  				prevButton.setEnabled(true);
				  }
				  else if(currentStep.equals(exampleTemp.getLastStep())) {
					  nextButton.setEnabled(false);
					  nextButtonClicked = 0;
				  }
			              
			  }
			  
			  public void mouseEntered(MouseEvent me){
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
				     setCursor(cursor);	
				}
				
				public void mouseExited(MouseEvent me){					
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
					     setCursor(cursor);			     
				}
				
			});
		
		
		
		
		prevButton.addMouseListener(new MouseAdapter() {
			  String prev = "";
			  @Override
			  public void mouseClicked(MouseEvent e) {
				  
			  	if (!currentStep.equals("-1")){
			  		
			  		
			  			//logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_PLAYER_BUTTON, BUTTON_PREVIOUS, logger.getCurrentTime());
					
			  		
			  			clearInterface(exampleInterface);
			  				//JOptionPane.showMessageDialog(null, currentStep);
			  			String previousStep=exampleTemp.getPreviousStep4Display(currentStep);
			  			currentStep=previousStep;
			  			/*get the FOA elements that need to be displayed*/
			  			validSteps4display.clear();
			  			validSteps4display = exampleTemp.getValidSteps4display(previousStep);
			  			
			  			if (!currentStep.equals(exampleTemp.getMinimumStep()))
			  				setSpeechHTML(exampleTemp.getStepTooltipHover(previousStep));
			  			else 
			  				setSpeechHTML(exampleTemp.getShortDescription());
			  			
			  			fillInExampleStep(exampleInterface,exampleTemp);
			  			if(!nextButton.isEnabled())
			  				nextButton.setEnabled(true);
			  	}
			  	if(prev.equals(currentStep)){
			  		prevButton.setEnabled(false);
			  		prev = "";
			  	}
			  	else
			  		prev = currentStep;
			  }
			  public void mouseEntered(MouseEvent me){
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
				     setCursor(cursor);	
				}
				
				public void mouseExited(MouseEvent me){					
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
					     setCursor(cursor);			     
				}
				
			});
		
		
		ffwdButton.addMouseListener(new MouseAdapter() {
			  @Override
			  public void mouseClicked(MouseEvent e) {
			  	if (!currentStep.equals("-1")){
			  		
			  		//logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_PLAYER_BUTTON, BUTTON_FFWD, logger.getCurrentTime());
					
			  		
				  clearInterface(exampleInterface);
				
				  currentStep=exampleTemp.getLastStep();
				  validSteps4display.clear();
	  			  validSteps4display = exampleTemp.getValidSteps4display(currentStep);
	  			
	  	
	  							setSpeechHTML(exampleTemp.getExplanation());
				  
				  fillInExampleStep(exampleInterface,exampleTemp);
				  }
			  	
			  	
			  }
			  public void mouseEntered(MouseEvent me){
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
				     setCursor(cursor);	
				}
				
				public void mouseExited(MouseEvent me){					
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
					     setCursor(cursor);			     
				}
				
			});
		
		fbwdButton.addMouseListener(new MouseAdapter() {
			  @Override
			  public void mouseClicked(MouseEvent e) {
			  	if (!currentStep.equals("-1")){
			  		
			  		//logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.EXAMPLE_PLAYER_BUTTON, BUTTON_FBWD, logger.getCurrentTime());
					
			  		clearInterface(exampleInterface);
	  				
			  		currentStep=exampleTemp.getMinimumStep();
	 
			  		/*get the FOA elements that need to be displayed*/
			  		validSteps4display.clear();
			  		validSteps4display = exampleTemp.getValidSteps4display(currentStep);
			  		setSpeechHTML(exampleTemp.getShortDescription());
			  		fillInExampleStep(exampleInterface,exampleTemp);
			  				  		
			  	}
			  	
			  }
			  public void mouseEntered(MouseEvent me){
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
				     setCursor(cursor);	
				}
				
				public void mouseExited(MouseEvent me){					
					Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); 
					     setCursor(cursor);			     
				}
			});
		
		
		return buttonPane;
	}
	
	
	public void enableExampleButton(boolean status,String button){
		if(button.equals("nextButton"))
				nextButton.setEnabled(status);
		else
		        prevButton.setEnabled(status);
	}
	
	
	private JLabel quizTeacher;
	private JTextArea quizSpeechText;
	
	private void setUpQuizCommPanel(JPanel commPanel)
	{
		commPanel.setLayout(new BoxLayout(commPanel, BoxLayout.X_AXIS));
		
		JPanel avatarPanel = new JPanel();
		avatarPanel.setBackground(quizColor);
		quizTeacher = new JLabel(createImageIcon(SimStPLE.TEACHER_IMAGE));
		
		// Add a mouse listener to quizTeacher label to direct students to use SimStudent tab for asking
		/*if(getSimStPLE() != null && getSimStPLE().getSimSt() != null && getSimStPLE().getSimSt()
				.isSsMetaTutorMode()) {
			quizTeacher.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
					showMenuWithTeacherMessage(me);
				}
			});
		}
		*/
		//if (!getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
			commPanel.add(quizTeacher);

		/*if (getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
			    JComponent metaTutorComponent1 = new MetaTutorAvatarComponent(SimStPLE.METATUTOR_IMAGE, getBrController().
	    			getMissController().getSimSt());
			   APlusHintDialogInterface aPlusHintDialogInterface1 = new APlusHintDialog(new Frame(),simStPLE.getMissController().
	            		getAPlusHintMessagesManager(), logger, metaTutorComponent);
	            if(aPlusHintDialogInterface1 instanceof JDialog) {
	            	((JDialog)aPlusHintDialogInterface1).setLocationRelativeTo(this);
	            }
	            metaTutorComponent1.setPreferredSize(new Dimension(AplusPlatform.METATUTOR_IMAGE_WIDTH, 
	            		AplusPlatform.METATUTOR_IMAGE_HEIGHT));
	            metaTutorComponent1.setBackground(quizColor);
	            commPanel.add(metaTutorComponent1);
	            
		}*/
		
		
		/*quizSpeechText = new JTextArea();
		quizSpeechText.setFont(FONT);
		quizSpeechText.setEditable(false);
		quizSpeechText.setBorder(BorderFactory.createLineBorder(Color.white, BORDER_WIDTH));
		JScrollPane speechScroller = new JScrollPane(quizSpeechText);
		speechScroller.setBorder(BorderFactory.createLineBorder(quizColor, BORDER_WIDTH));
		
		commPanel.add(speechScroller);*/
		ImageIcon boardImg=createImageIcon(SimStPLE.BOARD_IMAGE);
		JLayeredPane boardLayeredPane= new JLayeredPane();
		boardLayeredPane.setPreferredSize(new Dimension(boardImg.getIconWidth(), boardImg.getIconHeight()));	
		JPanel boardPanel = new JPanel();
		boardPanel.setOpaque(false);

	    JLabel theBoard= new JLabel(createImageIcon(SimStPLE.BOARD_IMAGE));
	    theBoard.setLayout(new BorderLayout());
	    boardLayeredPane.add(theBoard,new Integer(50));
	    
		quizSpeechText = new JTextArea();
		quizSpeechText.setFont(BOARD_FONT);
		quizSpeechText.setEditable(false);
		quizSpeechText.setForeground(Color.white);
	    quizSpeechText.setOpaque(false);
		JScrollPane speechScroller = new JScrollPane(quizSpeechText);
		speechScroller.setBorder(BorderFactory.createEmptyBorder(25, 30, 50, 35));
		speechScroller.setOpaque(false);
		speechScroller.getViewport().setOpaque(false);
		
	

		theBoard.add(speechScroller);	
		theBoard.setBounds( 0, 0, boardImg.getIconWidth(), boardImg.getIconHeight());	
		boardPanel.add(boardLayeredPane);	
		
		if (!getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
			commPanel.add(boardPanel);
			
		getSimStPLE().setAplusQuizTextArea(quizSpeechText);
	}
	
	private void showMenuWithTeacherMessage(MouseEvent me) {
		
		JPopupMenu menu = new JPopupMenu();
		menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		menu.setBackground(Color.lightGray);
		
		JLabel label1 = new JLabel(MetaTutorAvatarComponent.MR_WILLIAMS_SAYS_MSG);
		label1.setBackground(Color.lightGray);
		label1.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		menu.add(label1);
		menu.add(new JSeparator());
		
		JLabel label2 = new JLabel(TEACHER_MSG.replaceAll("SimStName", SimSt.SimStName));
		label2.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		label2.setBackground(Color.white);
		menu.add(label2);
		menu.show(me.getComponent(), 0 , -((int)menu.getPreferredSize().getHeight()));
	}
	
	private void setUpExampleIndex(JXTaskPaneContainer examplePane)
	{
		examplePanes = new LinkedList<JXTaskPane>();

		JPanel exampleButtons = new JPanel();
		exampleButtons.setBackground(exampleColor);
		GridBagConstraints qzConst = new GridBagConstraints();
		qzConst.gridx = 0;
		qzConst.gridy = 2;
		qzConst.gridwidth = 1;
		qzConst.gridheight = 1;
		qzConst.weightx = 1;
		qzConst.weighty = .2;
		qzConst.insets = BORDER;
		qzConst.fill = GridBagConstraints.HORIZONTAL;
		qzConst.anchor = GridBagConstraints.CENTER;
		examplePane.add(exampleButtons, qzConst);
				
		setUpExampleButtonPanel(exampleButtons);
		
	}
	
	private void setUpQuizIndex(JXTaskPaneContainer quizPane)
	{
		quizPanes = new LinkedList<QuizPane>();
		
		JPanel quizButtons = new JPanel();
		quizButtons.setBackground(quizColor);
		GridBagConstraints qzConst = new GridBagConstraints();
		qzConst.gridx = 0;
		qzConst.gridy = 2;
		qzConst.gridwidth = 1;
		qzConst.gridheight = 1;
		qzConst.weightx = 1;
		qzConst.weighty = .2;
		qzConst.insets = BORDER;
		qzConst.fill = GridBagConstraints.HORIZONTAL;
		qzConst.anchor = GridBagConstraints.CENTER;
		quizPane.add(quizButtons, qzConst);
		
		if (this.brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode())
			setUpQuizButtonPanelCogTutor(quizButtons);
		else
			setUpQuizButtonPanel(quizButtons);
	}
	
	public void setNextProblemButtonEnabled(boolean isEnabled)
	{
		nextProblemButton.setEnabled(isEnabled);
		nextProblemButtonQuiz.setEnabled(isEnabled);
		nextProblemButtonEx.setEnabled(isEnabled);
		if(isEnabled)
		{
			nextProblemButton.setForeground(Color.black);
			nextProblemButton.setBackground(studentUnfocusColor);
			nextProblemButtonQuiz.setForeground(Color.black);
			nextProblemButtonQuiz.setBackground(quizUnfocusColor);
			nextProblemButtonEx.setForeground(Color.black);
			nextProblemButtonEx.setBackground(exampleUnfocusColor);
		}
		else
		{
			nextProblemButton.setForeground(Color.gray);
			nextProblemButton.setBackground(studentColor);
			nextProblemButtonQuiz.setForeground(Color.gray);
			nextProblemButtonQuiz.setBackground(quizColor);
			nextProblemButtonEx.setForeground(Color.gray);
			nextProblemButtonEx.setBackground(exampleColor);
		}
		//nextProblemButton.setBackgroundPainter(getPainter(isEnabled,new Point2D.Float(200, 100),studentUnfocusColor, studentColor));
		nextProblemButton.repaint();
	}
	

	public void setRestartButtonEnabled(boolean isEnabled)
	{
		restartButton.setEnabled(isEnabled);
		restartButtonQuiz.setEnabled(false);
		restartButtonEx.setEnabled(false);
				
		restartButtonQuiz.setForeground(Color.gray);
		restartButtonQuiz.setBackground(quizColor);
		restartButtonEx.setForeground(Color.gray);
		restartButtonEx.setBackground(exampleColor);
		
	
		if(isEnabled)
		{
			restartButton.setForeground(Color.black);
			//restartButton.setBackground(studentUnfocusColor);
			//if (brController.getMissController().getSimSt().isSsCogTutorMode()){	
			//	restartButton.setBackground(new Color(219,184,156));
			//}
			//else
				restartButton.setBackground(studentUnfocusColor);
		}
		else
		{
			restartButton.setForeground(Color.gray);
			restartButton.setBackground(studentColor);
			
			
		}
		//restartButton.setBackgroundPainter(getPainter(isEnabled,new Point2D.Float(200, 100),studentUnfocusColor, studentColor));
		restartButton.repaint();
	}
	
	public void setQuizButtonEnabled(boolean isEnabled)
	{
		quizButton.setEnabled(isEnabled);
		quizButtonEx.setEnabled(isEnabled);
		quizButtonQuiz.setEnabled(isEnabled);
		if(isEnabled)
		{
			quizButton.setForeground(Color.black);
			quizButton.setBackground(studentUnfocusColor);
			quizButtonEx.setForeground(Color.black);
			quizButtonEx.setBackground(exampleUnfocusColor);
			quizButtonQuiz.setForeground(Color.black);
			quizButtonQuiz.setBackground(quizUnfocusColor);
		}
		else
		{
			quizButton.setForeground(Color.gray);
			quizButton.setBackground(studentColor);
			quizButtonEx.setForeground(Color.gray);
			quizButtonEx.setBackground(exampleColor);
			quizButtonQuiz.setForeground(Color.gray);
			quizButtonQuiz.setBackground(quizColor);
		}
		//quizButton.setBackgroundPainter(getPainter(isEnabled,new Point2D.Float(200, 100),quizUnfocusColor, quizColor));
		quizButton.repaint();
		quizButtonQuiz.repaint();
	}

	public String lastClickedQuizProblem=null;
	public int lastClickedQuizProblemIndex=-1;
	
	
	
	
	public void setUndoButtonEnabled(boolean isEnabled)
	{
		undoButton.setEnabled(isEnabled);
		undoButtonEx.setEnabled(false);
		undoButtonQuiz.setEnabled(false);

		undoButtonEx.setForeground(Color.gray);
		undoButtonEx.setBackground(exampleColor);		
		undoButtonQuiz.setForeground(Color.gray);
		undoButtonQuiz.setBackground(quizColor);
		
		if(isEnabled)
		{
			undoButton.setForeground(Color.black);
			undoButton.setBackground(studentUnfocusColor);
		}
		else
		{
			undoButton.setForeground(Color.gray);
			undoButton.setBackground(studentColor);
		}
		//undoButton.setBackgroundPainter(getPainter(isEnabled,new Point2D.Float(200, 100),studentUnfocusColor, studentColor));
		undoButton.repaint();
	}
	
	
	/*class QuizAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;
		
		String problem;
		String status;
		
		QuizAction(String problem, String status){
			this.problem = problem;
			this.status = status;
		    putValue(Action.NAME, problem);
		    putValue(Action.SHORT_DESCRIPTION, "Look at quiz item "+problem+".");
		    putValue(Action.SMALL_ICON, createImageIcon("img/"+status+".png"));
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(status.equals(QUIZ_LOCKED))
				JOptionPane.showMessageDialog(null, "This Quiz Item is Not Yet Available");
			else if(status.equals(QUIZ_CORRECT))
				JOptionPane.showMessageDialog(null, "Fill in correct quiz item "+problem);
			else if(status.equals(QUIZ_INCORRECT))
				JOptionPane.showMessageDialog(null, "Fill in incorrect quiz item "+problem);
			else if(status.equals(QUIZ_INCOMPLETE))
				JOptionPane.showMessageDialog(null, "Fill in incomplete quiz item "+problem);
		}
	}
	
	class QuizPane extends JXTaskPane
	{
		private static final long serialVersionUID = 1L;
		boolean locked = false;
		boolean hasResults = false;
		
		public QuizPane(String title)
		{
			super(title);
			
		}
		
		void updatePane(boolean lock, boolean finished)
		{
			locked = lock;
			if(finished)
			{
				 setIcon(createImageIcon("img/medal.png"));
				 hasResults = true;
			}
			else if(locked)
				setIcon(createImageIcon("img/lock.png"));
			else
				setIcon(createImageIcon("img/nolock.png"));
			
		}
				
		@Override
		public void setCollapsed(boolean collapsed)
		{
			if(locked)
				return;
			if(!hasResults && !collapsed)
			{
				int result = JOptionPane.showConfirmDialog(null, "This quiz has not yet been taken.  Do you want "+SimSt.getSimStName()+" to take it now?",
						"Take Quiz?", JOptionPane.YES_NO_OPTION);
				logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.UNTAKEN_QUIZ_EXPAND_ACTION, ""+(result==JOptionPane.YES_OPTION));
				if(result == JOptionPane.NO_OPTION || result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION)
					return;
				logger.simStLog(SimStLogger.SIM_STUDENT_ACTION_LISTENER, SimStLogger.UNTAKEN_QUIZ_INITIATE_ACTION,"");
		        if(brController.getMissController() != null && brController.getMissController().getSimSt() != null && brController.getMissController().getSimSt().isSsMetaTutorMode()) {
		        	if(brController.getAmt() != null) {
		        		brController.getAmt().handleInterfaceAction(SimStPLE.QUIZ, "ButtonPressed", "-1");
		        	}
		        }
				actionListener.takeQuiz();
			}
			super.setCollapsed(collapsed);
		}
		
	}
	*/
	
	
	//Functions called to update the tutoring interface from the players buttons
	private void fillInExampleStep(JCommTable table, SimStExample example)
	{
		int rows = table.getRows();
		int columns = table.getColumns();
		for(int r=0;r<rows;r++)
		{
			for(int c=0;c<columns;c++)
			{
				TableCell cell = table.getCell(r, c);
				String selection = table.getName()+"_C"+(c+1)+"R"+(r+1);
				
				if (validSteps4display.contains(selection)){
				
					String input = example.getStepInput(selection);
					
							String tooltip = example.getStepTooltip(selection);
							cell.setText(input);
							cell.setToolTipText(tooltip);
							//cell.setBackground(Color.WHITE);
							Color color = example.getStepColor(selection);
							cell.setDisabledTextColor(color);
				}
				
			}
		}
		
		
		
		
	}

	
			
	private void fillInExampleStep(Component component,SimStExample example)
	{
		
		//JCommTextField, JCommTextArea
		if(component instanceof JCommTable)
		{
			fillInExampleStep((JCommTable) component,example);
		}
		else if(component instanceof JCommTextField)
		{
		
		//	if (validSteps4display.contains("commTextField")){
			if (validSteps4display.contains(component.getName())){	
				JCommTextField field = (JCommTextField) component;
				String name = ""+field.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				field.setText(input);
				field.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				field.setForeground(color);
			}
			
		}
		else if(component instanceof JCommComboBox)
		{	
			if (validSteps4display.contains(component.getName())){
				JCommComboBox dropDown = (JCommComboBox) component;
				String name=component.getName();
				String input = example.getStepInput(name);
				dropDown.doCorrectAction(name,"UpdateComboBox", input);
			}
		}
		else if(component instanceof JCommTextArea)
		{
			JCommTextArea area = (JCommTextArea) component;
			String name = ""+area.getName();
			String input = example.getStepInput(name);
			String tooltip = example.getStepTooltip(name);
			area.setText(input);
			area.setToolTipText(tooltip);
			Color color = example.getStepColor(name);
			area.setForeground(color);
		}
		else if(component instanceof JCommLabel)
		{
			JCommLabel label = (JCommLabel) component;
			String name = ""+label.getName();
			String input = example.getStepInput(name);
			String tooltip = example.getStepTooltip(name);
			if(input.length() > 0)
				label.setText(input);
			label.setToolTipText(tooltip);
			Color color = example.getStepColor(name);
			label.setForeground(color);
		}
		else if(component instanceof JCommButton)
		{
			JCommButton button = (JCommButton) component;
			String name = ""+button.getName();
			String input = example.getStepInput(name);
			String tooltip = example.getStepTooltip(name);
			button.setToolTipText(tooltip);
			Color color = example.getStepColor(name);
			if (validSteps4display.contains(component.getName())){
				button.setForeground(color);
				if(!color.equals(Color.gray))
					button.setBorder(BorderFactory.createLineBorder(color, BUTTON_BORDER_WIDTH));
				else
					button.setBorder(BorderFactory.createEmptyBorder());
			}
			
		} else if(component instanceof Container)
		{
			Container container = (Container) component;
			for(Component comp: container.getComponents())
			{
				fillInExampleStep(comp,example);
			}
		}
			
		
	}
	
	public class JLabelIcon
	{
		private static final long serialVersionUID = 1L;
		public SimStExample example;
		String status;
		boolean quiz = false;
	
		
		public ImageIcon image;
		public JLabel label=null;
		
		
		public JLabelIcon(SimStExample example){
			this.example = example;
			refresh();
			    		    
		}
		

		public void refresh()
		{
			String title=example.getTitle();
			
			image =  createImageIcon("img/"+example.getStatus()+"Open.png");
			if (label==null)
				label = new JLabel(title.replaceAll("=", " = "), image, JLabel.LEFT);
			else {
				label.setText(title.replaceAll("=", " = "));
				label.setIcon(image);
			}
			
		}
		
		
		private void fillInExample(JCommTable table,boolean lockCorectSteps)
		{
			
			
			int rows = table.getRows();
			int columns = table.getColumns();
			//nbarba 01/21/2014: when in quiz, show all rows. when in examples, show only up to example step
			for(int r=0;r< rows ;r++){
				for(int c=0;c<columns;c++){
					quiz=true;
					if (quiz){ /*when in quiz, fill the whole table*/
						//Thread.dumpStack();
							TableCell cell = table.getCell(r, c);
								String selection = table.getName()+"_C"+(c+1)+"R"+(r+1);
									String input = example.getStepInput(selection);
										String tooltip = example.getStepTooltip(selection);
											cell.setText(input);
												cell.setToolTipText(tooltip);
													Color color = example.getStepColor(selection);
													    /*by default have everything unlocked*/
														if (lockCorectSteps)
															cell.setEnabled(true);
														
														cell.setDisabledTextColor(color);
														cell.setForeground(color);
														
														/*If we are in Aplus Cog Tutor mode then also lock the correct steps*/
														if (lockCorectSteps && color.equals(SimStExample.CORRECT_COLOR)){			
																 cell.setEnabled(false);
														}
													
														if (lockCorectSteps && selection.equals("dorminTable1_C1R1")){			
															 cell.setEnabled(false);
														}
														
														if (lockCorectSteps && selection.equals("dorminTable2_C1R1")){			
															 cell.setEnabled(false);
														}
														
														
														
														
					}
					else{	/*when in examples, fill only the FOA's up to the current step*/
								TableCell cell = table.getCell(r, c);
								String selection = table.getName()+"_C"+(c+1)+"R"+(r+1);
								if (validSteps4display.contains(selection)){
								
											String input = example.getStepInput(selection);
											String tooltip = example.getStepTooltip(selection);
											cell.setText(input);
											cell.setToolTipText(tooltip);
											Color color = example.getStepColor(selection);
											cell.setDisabledTextColor(color);
								}
						
					}//end of else
				} //end of for c
			} // end of for r
		} //end of function
		
		
		public void fillInExample(Component component,boolean lockCorectSteps)
		{
			
			//JCommTextField, JCommTextArea
			if(component instanceof JCommTable)
			{
				fillInExample((JCommTable) component,lockCorectSteps);
			}
			else if(component instanceof JCommComboBox)
			{	
				 
				if (quiz){
					JCommComboBox dropDown = (JCommComboBox) component;
					String name=component.getName();
					String input = example.getStepInput(name);
					dropDown.doCorrectAction(name,"UpdateComboBox", input);
				}
			}	
			else if(component instanceof JCommTextField)
			{	
				
				
				if (quiz){
					JCommTextField field = (JCommTextField) component;
					String name = ""+field.getName();
					String input = example.getStepInput(name);
					String tooltip = example.getStepTooltip(name);
					field.setText(input);
					field.setToolTipText(tooltip);
					Color color = example.getStepColor(name);
					field.setForeground(color);
				}
			}
			else if(component instanceof JCommTextArea)
			{
				//JOptionPane.showMessageDialog(null, component.getName() + " is JCommTextArea");  
				 
				JCommTextArea area = (JCommTextArea) component;
				String name = ""+area.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				area.setText(input);
				area.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				area.setForeground(color);
			}
			else if(component instanceof JCommLabel)
			{
				//JOptionPane.showMessageDialog(null, component.getName() + " is JCommLabel");  
				JCommLabel label = (JCommLabel) component;
				String name = ""+label.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				if(input.length() > 0)
					label.setText(input);
				label.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				label.setForeground(color);
			}
			else if(component instanceof JCommButton)
			{
				//JOptionPane.showMessageDialog(null, component.getName() + " is JCommButton");  
				JCommButton button = (JCommButton) component;
				String name = ""+button.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				button.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				//JOptionPane.showMessageDialog(null, "Setting doen to " + color + " and quiz is " + quiz); 
				quiz=true;
				if (quiz){
					
				button.setForeground(color); // sewall 2013-07-22: was getButton().setForeground(..);
				if(!color.equals(Color.gray))
					button.setBorder(BorderFactory.createLineBorder(color, BUTTON_BORDER_WIDTH));
				else
					button.setBorder(BorderFactory.createEmptyBorder());
				}
				
				
				
				//JOptionPane.showMessageDialog(null, component.getName() + " color set..."); 
				
			} else if(component instanceof Container)
			{
				Container container = (Container) component;
				for(Component comp: container.getComponents())
				{
					
					//JOptionPane.showMessageDialog(null, component.getName() + " lets see");
					fillInExample(comp,lockCorectSteps);
					
				}
			}
				
			
		}
		
		
		
	}
	
	
	
	public class ExampleAction extends AbstractAction
	{
		private static final long serialVersionUID = 1L;
		
		public SimStExample example;
		String status;
		boolean quiz = false;
				
		public ExampleAction(SimStExample example){
			this.example = example;
			refresh();
			    		    
		}
	
		public ExampleAction(SimStExample example,boolean quiz){
			this.example = example;
			this.quiz=quiz;
			refresh();
			    		    
		}

		
		public void refresh()
		{
			this.status = example.getStatus();

			//trace.out("ss"," ---> Updating example with index "+ example.getIndex() + " and title" + example.getTitle());
		    putValue(Action.NAME, example.getTitle().replaceAll("=", " = "));
		    putValue(Action.SMALL_ICON, createImageIcon("img/"+example.getStatus()+".png"));
		    
		    
		    
		    if(!status.equals(SimStExample.EXAMPLE))
		    	quiz = true;
		    if(status.equals(SimStExample.QUIZ_OLD))
		    {
		    	setEnabled(false);
		    	putValue(Action.SHORT_DESCRIPTION, "This problem or one like it was already completed in a previous session." );
		    }
		    else if(status.equals(SimStExample.QUIZ_LOCKED))
		    {
		    	setEnabled(false);
		    	putValue(Action.SHORT_DESCRIPTION, "This problem is not yet available.  You'll need to take more quizzes!" );
		    }
		    else if (status.equals(SimStExample.COGTUTOR_QUIZ_NOT_TAKEN)){
		    	setEnabled(true);
		    	putValue(Action.SHORT_DESCRIPTION, "This problem is not yet solved!" );
		    }
		    else
		    {
		    	setEnabled(true);
			    putValue(Action.SHORT_DESCRIPTION, "" );
		    }

		    if(!status.equals(SimStExample.EXAMPLE))
		    	quiz = true;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode() && quiz)
				return;
			
			if(status.equals(SimStExample.QUIZ_LOCKED) || status.equals(SimStExample.QUIZ_OLD))
				return;

			refreshList(quiz);
			
			if(quiz){
				
				/*store the last clicked quiz problem (useful for AplusControl CogTutor)*/
				lastClickedQuizProblem=this.example.getTitle();
				lastClickedQuizProblemIndex=this.example.getIndex();
			}
			
			
					
		    putValue(Action.SMALL_ICON, createImageIcon("img/"+example.getStatus()+"Open.png"));
			if(quiz)
			{
						
					actionListener.quizSwitched(example.getTitle());
					setSpeech(example.getExplanation(), true);
					
					
					fillInExample(quizInterface,getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode(),false);
					
					
					
					//String problem=example.getTitle().substring(3, example.getTitle().length());
					String problem=example.getTitle();

				
					
					if (example.getSimSt()!=null && example.getSimSt().isSsAplusCtrlCogTutorMode()){
						/*when example is empty, unlock the quiz interface so student can type the solution*/
						
						example.getSimSt().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().tmpFiledQuizedExample=example;
						
						
						
						if (!example.isExampleFilled)
							SimStPLE.setComponentEnabled(!example.isExampleFilled, quizInterface);
						
						
						
						/*when example is not empty, enable the restart button.*/
						restartButtonQuiz.setEnabled(example.isExampleFilled);
						
						//example.getSimSt().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().initQuizSolution();
						example.getSimSt().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().initQuizSolutionHash();
						example.getSimSt().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().setCurrentQuizIndex(example.getIndex());
						//trace.out("ss","****** Quiz index for " + example.getTitle() + " is " + example.getIndex());
						example.getSimSt().getBrController().getMissController().getSimStPLE().getSsInteractiveLearning().clearQuizGraph();				
						example.getSimSt().getBrController().getMissController().getSimStPLE().getSsInteractiveLearning().createStartStateQuizProblem(problem);
						
						example.getSimSt().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().initFailedQuizSolutionHash();
						if (!example.getQuizSolutionHash().isEmpty())
							example.getSimSt().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().getFailedQuizSolutionHash().putAll(example.getQuizSolutionHash());

						
					}
									
			}
			else
			{
				/* When in examples tab, we don't want the whole solution to be visible */
			
					exampleTemp=example;
					currentStep=example.getMinimumStep();
				 
					/*get the FOA elements that need to be displayed*/
					validSteps4display.clear();
			    	validSteps4display = example.getValidSteps4display(currentStep);
			
			    	clearInterface(exampleInterface);
			    	//System.out.println(" Before the example Switched ");
			    	isLogged = false;
			    	nextButtonClicked = 0;
			    	exampleProblem = example.getTitle();
			    	actionListener.exampleSwitched(example.getTitle());
			    	//System.out.println(" Action Listener ");
			    	//setSpeech(example.getShortDescription());
			    	setSpeechHTML(example.getShortDescription());
			    	

			    	fillInExample(exampleInterface,false,false);
			    	prevButton.setEnabled(false);
			    	nextButton.setEnabled(true);
			    	
			}
		}
		
		private void fillInExample(JCommTable table,boolean lockCorectSteps)
		{
			
			
			int rows = table.getRows();
			int columns = table.getColumns();
			//nbarba 01/21/2014: when in quiz, show all rows. when in examples, show only up to example step
			for(int r=0;r< rows ;r++){
				for(int c=0;c<columns;c++){
					if (quiz){ /*when in quiz, fill the whole table*/
						//Thread.dumpStack();
							TableCell cell = table.getCell(r, c);
								String selection = table.getName()+"_C"+(c+1)+"R"+(r+1);
									String input = example.getStepInput(selection);
										String tooltip = example.getStepTooltip(selection);
											cell.setText(input);
												cell.setToolTipText(tooltip);
													Color color = example.getStepColor(selection);
													    /*by default have everything unlocked*/
														if (lockCorectSteps)
															cell.setEnabled(true);
														
														cell.setDisabledTextColor(color);
														cell.setForeground(color);
														
														/*If we are in Aplus Cog Tutor mode then also lock the correct steps*/
														if (lockCorectSteps && color.equals(SimStExample.CORRECT_COLOR)){			
																 cell.setEnabled(false);
														}
														
														if (lockCorectSteps && selection.equals("dorminTable1_C1R1")){			
															 cell.setEnabled(false);
														}
														
														if (lockCorectSteps && selection.equals("dorminTable2_C1R1")){			
															 cell.setEnabled(false);
														}
														
														
					}
					else{	/*when in examples, fill only the FOA's up to the current step*/
								TableCell cell = table.getCell(r, c);
								String selection = table.getName()+"_C"+(c+1)+"R"+(r+1);
								if (validSteps4display.contains(selection)){
								
											String input = example.getStepInput(selection);
											String tooltip = example.getStepTooltip(selection);
											cell.setText(input);
											cell.setToolTipText(tooltip);
											Color color = example.getStepColor(selection);
											cell.setDisabledTextColor(color);
								}
						
					}//end of else
				} //end of for c
			} // end of for r
		} //end of function
		
		
		public void fillInExample(Component component,boolean lockCorectSteps,boolean doneBlack)
		{
			
			if(component instanceof JCommTable)
			{
				fillInExample((JCommTable) component,lockCorectSteps);
			}
			else if(component instanceof JCommComboBox)
			{	
				 
				if (quiz){
					JCommComboBox dropDown = (JCommComboBox) component;
					String name=component.getName();
					String input = example.getStepInput(name);
					dropDown.doCorrectAction(name,"UpdateComboBox", input);
				}
			}	
			else if(component instanceof JCommTextField)
			{	
				
				
				if (quiz){
					JCommTextField field = (JCommTextField) component;
					String name = ""+field.getName();
					String input = example.getStepInput(name);
					String tooltip = example.getStepTooltip(name);
					field.setText(input);
					field.setToolTipText(tooltip);
					Color color = example.getStepColor(name);
					field.setForeground(color);
				}
			}
			else if(component instanceof JCommTextArea)
			{
				//JOptionPane.showMessageDialog(null, component.getName() + " is JCommTextArea");  
				 
				JCommTextArea area = (JCommTextArea) component;
				String name = ""+area.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				area.setText(input);
				area.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				area.setForeground(color);
			}
			else if(component instanceof JCommLabel)
			{
				//JOptionPane.showMessageDialog(null, component.getName() + " is JCommLabel");  
				JCommLabel label = (JCommLabel) component;
				String name = ""+label.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				if(input.length() > 0)
					label.setText(input);
				label.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				label.setForeground(color);
			}
			else if(component instanceof JCommButton)
			{
				//JOptionPane.showMessageDialog(null, component.getName() + " is JCommButton");  
				JCommButton button = (JCommButton) component;
				String name = ""+button.getName();
				String input = example.getStepInput(name);
				String tooltip = example.getStepTooltip(name);
				button.setToolTipText(tooltip);
				Color color = example.getStepColor(name);
				if (quiz){
		
				button.setForeground(color); // sewall 2013-07-22: was getButton().setForeground(..);
				
				if(!color.equals(Color.gray))
					button.setBorder(BorderFactory.createLineBorder(color, BUTTON_BORDER_WIDTH));
				else
					button.setBorder(BorderFactory.createEmptyBorder());
				}
				
				if (doneBlack){
					button.setForeground(Color.black);
					button.setBorder(BorderFactory.createEmptyBorder());
				}
							
			} else if(component instanceof Container)
			{
				Container container = (Container) component;
				for(Component comp: container.getComponents())
				{
					
					//JOptionPane.showMessageDialog(null, component.getName() + " lets see");
					fillInExample(comp,lockCorectSteps,doneBlack);
					
				}
			}
				
			
		}
	}
	
	
	



	
	/** this painter draws a gradient fill */
	public Painter getPainter(boolean enabled, Point2D size, Color unfocusColor, Color focusColor) 
	{  
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(200, 100);
		//float[] dist = {0.0f, 0.2f, 1.0f};
		if(enabled)
		{
			float[] dist = {0.0f, 0.5f, 1.0f};
			LinearGradientPaint gradientPaint = new LinearGradientPaint(start, size, dist,
				new Color[]{Color.WHITE, unfocusColor,focusColor});
			MattePainter mattePainter = new MattePainter(gradientPaint);
			return mattePainter;
		}
		else
		{
			float[] dist = {0.0f, 1.0f};
			LinearGradientPaint gradientPaint = new LinearGradientPaint(start, size, dist,
				new Color[]{unfocusColor,focusColor});
			MattePainter mattePainter = new MattePainter(gradientPaint);
			return mattePainter;
		}
	}
	
	public Painter getTricolorPainter(Color color1, Color color2, Color color3) 
	{  
		Point2D start = new Point2D.Float(0, 0);
		Point2D end = new Point2D.Float(300, 50);
		
		float[] dist = {0.0f, 0.5f, 1.0f};
		LinearGradientPaint gradientPaint = new LinearGradientPaint(start, end, dist,
			new Color[]{color1, color2, color3});
		MattePainter mattePainter = new MattePainter(gradientPaint);
		return mattePainter;
	}
	
	
	public void addSection(String sectionName)
	{
		
		if(sectionName.startsWith("-"))
		{			
			sectionName = sectionName.substring(1);
			if (hasExampleSection)
				sections.put(examplePanes.size(),sectionName);
			
			quizProblems.add(new LinkedList<ExampleAction>());
			quizProblemsJLabelIcon.add(new LinkedList<JLabelIcon>());

			if (hasExampleSection)
				exampleProblems.add(new LinkedList<ExampleAction>());
			
			QuizPane quizTaskPane = new QuizPane(sectionName, logger, brController, actionListener);
			quizPanes.add(quizTaskPane);
			((Container)quizContainer).add(quizTaskPane);
			
			quizTaskPane.setCollapsed(true);

			Hashtable<Integer,JLabel> label = new Hashtable<Integer,JLabel>();
			label.put(0, new JLabel(sectionName));
			
			quizTaskPane.updatePane(true, false);
			
			return;
		}

		quizProblems.add(new LinkedList<ExampleAction>());
		quizProblemsJLabelIcon.add(new LinkedList<JLabelIcon>());
		
		
		if (hasExampleSection){
		sections.put(examplePanes.size(),sectionName);
		
		exampleProblems.add(new LinkedList<ExampleAction>());
			
		JXTaskPane exampleTaskPane = new JXTaskPane(sectionName);
		examplePanes.add(exampleTaskPane);
		((Container)exampleContainer).add(exampleTaskPane);
			if(examplePanes.size() > 1)
				exampleTaskPane.setCollapsed(true);
		}
		
		QuizPane quizTaskPane = new QuizPane(sectionName, logger, brController, actionListener);
		quizPanes.add(quizTaskPane);
		((Container)quizContainer).add(quizTaskPane);
		
	
		quizTaskPane.setCollapsed(true);
		
		Hashtable<Integer,JLabel> label = new Hashtable<Integer,JLabel>();
		label.put(0, new JLabel(sectionName));
		
		JLabel skillLabel = new JLabel(sectionName);
		skillLabel.setBackground(studentColor);
		skillometer.add(skillLabel);
		JSlider skillometerBar = new JSlider(JSlider.HORIZONTAL,0,10,0);
		skillometerBar.setForeground(studentColor.darker().darker());
		skillometerBar.setBackground(studentColor);
		skillometerBar.setLabelTable(label);
		skillometerBar.setPaintLabels(false);
		skillometer.add(skillometerBar);
		
		skillometerBar.addChangeListener(actionListener);
		
		//quizTaskPane.setIcon(createImageIcon("img/lock.png"));
		quizTaskPane.updatePane(true, false);
			
	}
	
	public void restoreSkilloMeterBarValues() {
		
		Hashtable<String, Integer> skillNameValue = null;
		if(getSimStPLE() != null && getSimStPLE().getSimSt() != null)
			skillNameValue = getSimStPLE().getSimSt().getSkillSliderNameValuePair();
		
		if(skillNameValue != null) {
			Component comp[] = skillometer.getComponents();
			
			for(int i = 0; i < comp.length ; i++) {
				Component currentComp = comp[i];
				if(currentComp instanceof JSlider) {
					Hashtable labelTable = (Hashtable) ((JSlider)currentComp).getLabelTable();
					String nameCurrentComp =  ((JLabel)labelTable.get(0)).getText();
					
					if(skillNameValue.get(nameCurrentComp) != null) {
						int valueCurrentComp = skillNameValue.get(nameCurrentComp).intValue();
						((JSlider) currentComp).setValue(valueCurrentComp);
					}
				}
			}
		}

	}
	
	@Override
	public void stateChanged(ChangeEvent event) {
		
		setDefaultLookAndFeel();
		
        int index = tabPane.getSelectedIndex();
        UIManager.put("TaskPane.titleBackgroundGradientStart", Color.white);
        UIManager.put("TaskPane.titleBackgroundGradientEnd", focusColors[index]);
    	UIManager.put("TabbedPane.focus", focusColors[index]);
    	UIManager.put("TabbedPane.selected", focusColors[index]);
    	
    	java.awt.EventQueue.invokeLater(new Runnable() {
    		public void run() {
    	    	tabPane.updateUI();
    	    	
    	    	if(hasExampleSection){
    	    	for(JXTaskPane pane:examplePanes)
    	    		pane.updateUI();
    	    	}
    	    	
    	    	for(JXTaskPane pane:quizPanes)
    	    		pane.updateUI();
    		}
    	});
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame platform = new JFrame();
		platform.getContentPane().add(new AplusPlatform(null,null));

		platform.setSize(1000,700);
		platform.setVisible(true);
		platform.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		

	}
	 /** Returns an ImageIcon, or null if the path was invalid. */
    public ImageIcon createImageIcon(String path) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	return new ImageIcon(url);
    	
    }
    
    public void appendSpeech(String text, String name)
    {
    	String prevText = getSpeechText().getText();
    	text = name+": "+text;
    	
    	String step = getBrController().getMissController().getSimSt().getProblemStepString();
    	getSimStPLE().logger.simStLog(SimStLogger.SIM_STUDENT_DIALOGUE, SimStLogger.CHAT_DIALOG_ACTION, step, "", "", 0, text);
    	
    	getSpeechText().append(text+"\n");

    	scrollPaneToBottom();

		
    }
        
    public JTextArea getSpeechText() {
		return tutoringSpeechText;
	}

	public void clearSpeech()
    {
		getSpeechText().setText("");

		getTextResponse().setEditable(false);
    	getTextResponse().setEnabled(false);
    	getTextResponseSubmitButton().setEnabled(false);
    }
	

    public JButton getTextResponseSubmitButton() {
		return submitButton;
	}

	public JComboBox getTextResponse() {
		return tutoringSpeechEntry;
	}

	public void scrollPaneToBottom() {
    	 
    	if(SwingUtilities.isEventDispatchThread())
    	{
    		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					speechScroller.getVerticalScrollBar().setValue(
						speechScroller.getVerticalScrollBar().getMaximum());
				}
			});
    	}
    	else
    	{
	    	try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						speechScroller.getVerticalScrollBar().setValue(
							speechScroller.getVerticalScrollBar().getMaximum());
					}
				});
			} catch (Exception e) {
			}
    	}
	}
	
	public BR_Controller getBrController() {
        return brController;
    }

    public void setBrController(BR_Controller brController) {
        this.brController = brController;
    }
    
    public String getCurrentSpeechText()
    {
    	return getSpeechText().getText();
    }
    
    public boolean getButtonsShowing()
    {
    	return yesPanel.isVisible();
    }

	public JButton getYesResponseButton() {
		return yesButton;
	}
	
	public JButton getNoResponseButton() {
		return noButton;
	}
	
	public JTabbedPaneWithCloseIcons getExamplePane() 
	{ 
		return tabPane;
	}
	
	public JButton getNextProblemButton() {
        return nextProblemButton;
    }
	
    public JButton getQuizButton() {
        return quizButton;
    }
    
    public JButton getRestartButton()
    {
    	return restartButton;
    }
    
    public JLayeredPane getSimStAvatarLayerIcon()
    {
    	return tutoringAvatar;
    }
    
    public JLabel getSimStNameLabel() {
        return studentName;
    }
    
    public SimStPLE getSimStPLE() {
        return simStPLE;
    }
    
    public JComponent getStudentInterface() {
        return studentInterface;
    }
    public void setStudentInterface(JComponent studentInterface) {
        this.studentInterface = studentInterface;
        
        /* We do not want to disable colors in CogTutor mode because in normal APLUS mode
         * when cells are "locked" then colors are lost (CTAT JCommWidget does not support
         * colors for disabled widgets */
       if (!getBrController().getMissController().getSimSt().isSsCogTutorMode()){
        	getSimStPLE().removeComponentColor(studentInterface);
       }

       
        getSimStPLE().setUpUndo(studentInterface);
    }
    
    public void refresh()
    {
    	this.repaint();
    }
    
    public void showButtons(boolean show)
    {
    	trace.out("ss", "SHOW BUTTONS ********************************");
		if(show)
		{
			if(!SwingUtilities.isEventDispatchThread())
			{
		    	try {
					SwingUtilities.invokeAndWait(new Runnable() {
						public void run() {
							showTextResponse(false);
							getYesResponseButton().setVisible(false);
							getNoResponseButton().setVisible(false);
							yesButton = new JXButton("Yes");
							noButton = new JXButton("No");
							getYesResponseButton().setPreferredSize(new Dimension(getYesResponseButton().getPreferredSize().width*2, getYesResponseButton().getPreferredSize().height));
							getNoResponseButton().setPreferredSize(new Dimension(getNoResponseButton().getPreferredSize().width*2, getNoResponseButton().getPreferredSize().height));
							yesPanel.add(getYesResponseButton(), BorderLayout.CENTER);
							noPanel.add(getNoResponseButton(), BorderLayout.CENTER);
							getYesResponseButton().validate();
							getNoResponseButton().validate();
							yesPanel.setVisible(true);
							noPanel.setVisible(true);
						}
					});
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}	
			}
			else
			{
					showTextResponse(false);
					getYesResponseButton().setVisible(false);
					getNoResponseButton().setVisible(false);
					yesButton = new JXButton("Yes");
					noButton = new JXButton("No");
					getYesResponseButton().setPreferredSize(new Dimension(getYesResponseButton().getPreferredSize().width*2, getYesResponseButton().getPreferredSize().height));
					getNoResponseButton().setPreferredSize(new Dimension(getNoResponseButton().getPreferredSize().width*2, getNoResponseButton().getPreferredSize().height));
					yesPanel.add(getYesResponseButton(), BorderLayout.CENTER);
					noPanel.add(getNoResponseButton(), BorderLayout.CENTER);
					getYesResponseButton().validate();
					getNoResponseButton().validate();
					yesPanel.setVisible(true);
					noPanel.setVisible(true);
			}
		 }
		 else
		 {
		    		getYesResponseButton().validate();
					getNoResponseButton().validate();
					yesPanel.setVisible(false);
					noPanel.setVisible(false);
					showTextResponse(false);
		 }
		refresh();

		
		
		final JFrame f = new JFrame();	
		//Must schedule the close before the dialog becomes visible
		ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();     
		s.schedule(new Runnable() {
		    public void run() {
		    	f.setVisible(true);
		    	f.dispose();
		    }
		}, 280, TimeUnit.MILLISECONDS);

		f.setUndecorated(true); // Remove title bar
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
				
		//showSplashScreen(show);		
    }
    
    public JFrame getAplusFrame(){
    	Window parent=SwingUtilities.windowForComponent(tutoringTab);	
    	return (JFrame) parent;
    }
    /**
     * Method to display the splash screen when APLUS launches
     */
    public void showSplashScreen(/*boolean show*/){
    	
    	/*splash displayed when in Metatutor mode or CogTutor mode (i.e. not in baseline)*/
    	if (splashShown==false && (brController.getMissController().getSimSt().isSsMetaTutorMode() || brController.getMissController().getSimSt().isSsCogTutorMode())){

			    boolean completedFinalChallenge = brController.getMissController().getSimStPLE().getHasPassedFinalChallenge();
				
				logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.SPLASH_SCREEN_APPEARED, "");	  
				
				new MetatutorSplashScreen(getAplusFrame(),true,getSimStPLE().getSimStName(),getSplashScreenFrames(completedFinalChallenge),this,completedFinalChallenge);
				
				logger.simStLog(SimStLogger.SIM_STUDENT_PLE, SimStLogger.SPLASH_SCREEN_CLOSED, "");
				
				((MetaTutorAvatarComponent) metaTutorComponent).changeMetaTutorImage(SimStPLE.METATUTOR_IMAGE);
	
			
			
			splashShown=true;

		}
    	this.refresh();
    }
  
    
  /**
   * Method that actually determines what will be shown in the frames
   * of the splash screen
   * @return
   */
    public Vector<SplashFrame> getSplashScreenFrames(boolean completedFinalChallenge){
    	
    	Vector<SplashFrame> returnVector= new Vector<SplashFrame>();
    	
    	if (brController.getMissController().getSimSt().isSsCogTutorMode() && !brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
    		/*cog tutor version of APLUS*/
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_2,SimStPLE.COGTUTOR_APLUS_SPLASH_MSG_1,3,10,50,35,SPLASH_FONT,StyleConstants.ALIGN_LEFT,170,60,240,280));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_4,225,240,155,30));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_SPLASH_IMG_5,385,240,70,30));
    		returnVector.add(new SplashFrame(SimStPLE.LBT_APLUS_SPLASH_IMG_5,SimStPLE.COGTUTOR_APLUS_SPLASH_MSG_3,90,170,45,35,SPLASH_FONT_BIG,StyleConstants.ALIGN_CENTER,0,0,40,40));
    	}
    	else if(brController.getMissController().getSimSt().isSsAplusCtrlCogTutorMode()){
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_2_APLUS,SimStPLE.COGTUTOR_APLUS_SPLASH_MSG_2,3,10,50,35,SPLASH_FONT,StyleConstants.ALIGN_LEFT,170,60,240,280));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_3,180,35,295,70));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_4_APLUS,255,240,155,30));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_5,415,240,70,30));
    		returnVector.add(new SplashFrame(SimStPLE.LBT_APLUS_SPLASH_IMG_5,SimStPLE.COGTUTOR_APLUS_SPLASH_MSG_4,90,170,45,35,SPLASH_FONT_BIG,StyleConstants.ALIGN_CENTER,0,0,40,40));
    	}
    	else if (completedFinalChallenge){
    		/*normal APLUS - final challenge completed*/
    		returnVector.add(new SplashFrame(SimStPLE.WELCOME_SPLASH,SimStPLE.LBT_APLUS_SPLASH_MSG_3,90,70,50,35,SPLASH_FONT,StyleConstants.ALIGN_CENTER,Color.white));
    	}
    	else{
    		/*normal Aplus */
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_2_APLUS,SimStPLE.LBT_APLUS_SPLASH_MSG_1,3,10,50,35,SPLASH_FONT,StyleConstants.ALIGN_LEFT,170,60,240,280));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_3,180,35,295,70));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_4_APLUS,255,240,155,30));
    		returnVector.add(new SplashFrame(SimStPLE.COGTUTOR_APLUS_SPLASH_IMG_5,415,240,70,30));
    		returnVector.add(new SplashFrame(SimStPLE.LBT_APLUS_SPLASH_IMG_5,SimStPLE.LBT_APLUS_SPLASH_MSG_2,90,170,45,35,SPLASH_FONT_BIG,StyleConstants.ALIGN_CENTER,0,0,40,40));
    	}
    	
    	
    	
    	return returnVector;
    }
    
    
           
    public void showTextResponse(boolean show)
    {
    	if(show)
    	{
    		showButtons(false);
    	}
    	getTextResponse().setVisible(show);
		getTextResponse().setEditable(show);
		getTextResponse().setSelectedItem("");
    	getTextResponse().setEnabled(show);
    	submitPanel.setVisible(show);
    	getTextResponseSubmitButton().setEnabled(show);
    	
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

    public void restoreButtons()
    {
    	yesPanel.setVisible(true);
    	noPanel.setVisible(true);
		getYesResponseButton().setVisible(true);
    	getNoResponseButton().setVisible(true);
    	
		/*yesButton.setForeground(Color.black);
		yesButton.setBackground(studentUnfocusColor);
		noButton.setForeground(Color.black);
		noButton.setBackground(studentUnfocusColor);*/
    	
    	getTextResponse().setVisible(false);
    	getTextResponseSubmitButton().setVisible(false);
    }
    
    public void setExpression(String expression)
    {
    	tutoringAvatar.setExpression(expression);
    }

    public void setFormattedSpeech(String text)
    {
		exampleSpeechText.setText(text);
    }
    
    public void setImage(String img)
    {
    	tutoringAvatar.setImage(img);
    }
    

    public void setName(String name)
    {
    	studentName.setText(SimSt.getSimStName());
    	tabPane.setTitleAt(0, name);
    	nextProblemButton.setToolTipText(getSimStPLE().getNextProblemButtonTitleString());
    	quizButton.setToolTipText(getSimStPLE().getQuizButtonTitleString());
    	quizButtonQuiz.setToolTipText(getSimStPLE().getQuizButtonTitleString());
    }
    
    public void setQuizProgress(double percentCorrect)
    {
    	//int progress = (int)(percentCorrect*100);
    	//quizProgress.setValue(progress);
    }
    
    public void setSimStPLE(SimStPLE simStPLE) {
        this.simStPLE = simStPLE;
    }
    
    public void setSpeech(String text)
    {
    	setSpeech(text, false);
    }
        
    public void setSpeechHTML(String text){
    	exampleSpeechText.setContentType("text/html");
    	String textToGive="<html><body style=\"font-family: Comic Sans MS; color: white; font-size:12px; padding: 0.3cm 0.2cm 0.2cm 0.4cm;\">"+text+"</body></html>";
    	exampleSpeechText.setText(textToGive);
    }
    
    public void setSpeech(String text, boolean quiz)
    {
    	//JTextArea speechLocation = exampleSpeechText;
    	//if(quiz)
    	JTextArea speechLocation = quizSpeechText;
    	
    	speechLocation.setText("");
    	int width = (int)(speechLocation.getWidth())-30;
    	JLabel temp = new JLabel(text);
    	temp.setFont(FONT);
    	
    	int textWidth = temp.getPreferredSize().width;
    	if( textWidth > width && width != 0)
    	{
    		//Text Does Not Fit - Fancy formatting
    		double percentFits = ((double) width)/textWidth;
    		int charsFit = (int) ( percentFits * text.length())-3;
    		String remaining = text;
    		String formatted = "";
    		while(remaining.length() > 0)
    		{
    			
    			if(remaining.length() < charsFit)
    			{
    				int newLine = remaining.indexOf("\n");
    				if(newLine == -1)
    					newLine = remaining.indexOf("\\n");
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
    				int newLine = remaining.indexOf("\n");
    				if(newLine == -1)
    					newLine = remaining.indexOf("\\n");
    				if(newLine == -1)
    					newLine = remaining.length();
    				String tempString = "";
    				if(newLine < charsFit)
    				{
    					tempString = remaining.substring(0, newLine)+"\n";
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
    		speechLocation.append(formatted);
       	}
    	else
    	{
    		speechLocation.setText(text+"\n");

    	}

    }
    
    public static final String WAIT_MESSAGE = "   Please Wait While Your Previous Work is Loaded";
    JProgressBar progressBar = new JProgressBar(0,100);
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
    
    public void showQuizResultFrame(boolean isShowing)
    {
    	clearQuizInterface();
    	if(isShowing)
    		java.awt.EventQueue.invokeLater(new Runnable() {
    		public void run() {
    			int lastUnlocked = 0;
    			for(int i=0;i< quizPanes.size();i++)
    			{
    				QuizPane quizPane = quizPanes.get(i);
    				if(!quizPane.locked)
    					lastUnlocked = i;
    			}
    			List<ExampleAction> problems = quizProblems.get(lastUnlocked);
    			if(problems != null && problems.size() > 0)
    			{
    				ExampleAction problem = problems.get(0);
    				problem.putValue(Action.SMALL_ICON, createImageIcon("img/"+problem.example.getStatus()+"Open.png"));
    				actionListener.quizSwitched(problem.example.getTitle());
    				setSpeech(problem.example.getExplanation(), true);
    				problem.fillInExample(quizInterface,getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode(),false);
    			}
        		tabPane.setSelectedComponent(quizTab);
    		}
    		});
   
    	refreshList(true);
    }
    
    public void clearQuizInterface()
    {
    	clearInterface(quizInterface);
    }
    	
	private void clearInterface(Component component)
	{
		if(component instanceof JCommTable)
		{
			JCommTable table = (JCommTable) component;
			int rows = table.getRows();
			int columns = table.getColumns();
			for(int r=0;r<rows;r++)
			{
				for(int c=0;c<columns;c++)
				{
					TableCell cell = table.getCell(r, c);
					cell.setText("");
					cell.setToolTipText("");
					cell.setBackground(Color.WHITE);
				}
			}
		}
		else if(component instanceof JCommTextField)
		{
			JCommTextField field = (JCommTextField) component;
			field.setText("");
			field.setToolTipText("");
		}
		else if(component instanceof JCommTextArea)
		{
			JCommTextArea area = (JCommTextArea) component;
			area.setText("");
			area.setToolTipText("");
		}
		else if(component instanceof JCommLabel)
		{
			JCommLabel label = (JCommLabel) component;
			label.setToolTipText("");
		}
		else if(component instanceof JCommButton)
		{
			JCommButton button = (JCommButton) component;
			button.setToolTipText("");
			button.setBorder(BorderFactory.createEmptyBorder());
		} else if(component instanceof Container)
		{
			Container container = (Container) component;
			for(Component comp: container.getComponents())
			{
				clearInterface(comp);
			}
		}
	}

	public JButton getUndoButton() 
	{ 
		return undoButton; 
	}
	
	public void setNextProblemButtonText(String text)
	{
		nextProblemButton.setToolTipText(text);
		nextProblemButtonQuiz.setToolTipText(text);
		nextProblemButtonEx.setToolTipText(text);
	}

	public void setRestartButtonText(String text)
	{
		restartButton.setToolTipText(text);
		restartButtonQuiz.setToolTipText(text);
		restartButtonEx.setToolTipText(text);
	}

	public void setQuizButtonText(String text)
	{
		quizButton.setToolTipText(text);
		quizButtonEx.setToolTipText(text);
		quizButtonQuiz.setToolTipText(text);
	}
	
	public void setQuizButtonImg(String img)
	{
		quizButtonEx.setIcon(createImageIcon(img));
		quizButtonQuiz.setIcon(createImageIcon(img));
	}
	
	
	public String quizButtonImage="img/quiz.png";
	public void setQuizButtonImage(String img){this.quizButtonImage=img;}
	public String getQuizButtonImage(){return this.quizButtonImage;}
	
	public void refreshQuizButtonImage(){
		setQuizButtonImg(getQuizButtonImage());
		refresh();
	}
	
	
	public void setUndoButtonText(String text)
	{
		undoButton.setToolTipText(text);
		undoButtonEx.setToolTipText(text);
		undoButtonQuiz.setToolTipText(text);
	}
	
	public void addExample(SimStExample example)
	{
		
		int sectionIndex = example.getSection();
		JXTaskPane pane = examplePanes.get(sectionIndex);
		pane.setFont(S5_MED_MED_FONT);
		ExampleAction exampleAction = new ExampleAction(example);
		pane.add(exampleAction);
		
		exampleProblems.get(sectionIndex).add(exampleAction);
	}
	
	
	  private JPanel createPanel(String s,SimStExample example) {
          JPanel p = new JPanel(new BorderLayout());
          p.add(new JLabel(s, JLabel.RIGHT), BorderLayout.WEST);
         
          String file = "/edu/cmu/pact/miss/PeerLearning"+"/img/"+example.getStatus()+".png";
          URL url = this.getClass().getResource(file);
          
          Icon icon = UIManager.getIcon("img/"+example.getStatus()+".png");
          p.add(new JLabel(icon, JLabel.LEFT), BorderLayout.EAST);
         // p.setBorder(BorderFactory.createLineBorder(Color.blue));
          return p;
      }
	  
	  
	public void addQuiz(SimStExample quiz){
		this.addQuiz(quiz, false);
	}
	
	
	public void addQuizLabelIcon(SimStExample quiz, boolean fillInQuizInterface)
	{
		int sectionIndex = quiz.getSection();

		QuizPane pane = quizPanes.get(sectionIndex);
		pane.setFont(S5_MED_MED_FONT);
		List<JLabelIcon> quizzesLabelIcon = quizProblemsJLabelIcon.get(sectionIndex);
		
		boolean allCorrect = true;
		boolean alreadyPresent = false;
				
		
		if(!quiz.getStatus().equals(SimStExample.QUIZ_CORRECT) && !quiz.getStatus().equals(SimStExample.QUIZ_OLD)){	
			allCorrect = false;
		}
		
		for(int i=0;i<quizzesLabelIcon.size();i++)
		{
			//The problem index is already present from a previous time
			if(quizzesLabelIcon.get(i).example.getIndex() == quiz.getIndex())
			{
				
				quizzesLabelIcon.get(i).example = quiz;
				quizzesLabelIcon.get(i).refresh();
				alreadyPresent = true;
				
				if (fillInQuizInterface){
					quizzesLabelIcon.get(i).fillInExample(quizInterface,getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode());
				}
			}
			
			if(!quizzesLabelIcon.get(i).example.getStatus().equals(SimStExample.QUIZ_CORRECT) && !quizzesLabelIcon.get(i).example.getStatus().equals(SimStExample.QUIZ_OLD))
				allCorrect = false;
			
		}
		
		if(!alreadyPresent)
		{
			
			JLabelIcon tmp= new JLabelIcon(quiz);
			pane.add(tmp.label);
			
			quizzesLabelIcon.add(tmp);
		}
		
		if(!quiz.getStatus().equals(SimStExample.QUIZ_LOCKED))
			pane.hasResults = true;
		
		pane.updatePane(quiz.getStatus().equals(SimStExample.QUIZ_LOCKED), allCorrect);
		
		if(!quiz.getStatus().equals(SimStExample.QUIZ_LOCKED) && !quiz.getStatus().equals(SimStExample.QUIZ_OLD)){
			pane.setCollapsed(false);
		}
	}
	
	
	//Returns true if adding this quiz makes the entire quiz passed
	public void addQuiz(SimStExample quiz, boolean fillInQuizInterface)
	{
		int sectionIndex = quiz.getSection();

		QuizPane pane = quizPanes.get(sectionIndex);
		pane.setFont(S5_MED_MED_FONT);
		List<ExampleAction> quizzes = quizProblems.get(sectionIndex);
		
		
		boolean allCorrect = true;
		boolean alreadyPresent = false;
				
		
		if(!quiz.getStatus().equals(SimStExample.QUIZ_CORRECT) && !quiz.getStatus().equals(SimStExample.QUIZ_OLD)){	
			allCorrect = false;
		}
		
		for(int i=0;i<quizzes.size();i++)
		{
			//The problem index is already present from a previous time
			if(quizzes.get(i).example.getIndex() == quiz.getIndex())
			{
				
				quizzes.get(i).example = quiz;
				quizzes.get(i).refresh();
				alreadyPresent = true;
				
				if (fillInQuizInterface){
					quizzes.get(i).fillInExample(quizInterface,getBrController().getMissController().getSimSt().isSsAplusCtrlCogTutorMode(),false);
				}
			}
			
			if(!quizzes.get(i).example.getStatus().equals(SimStExample.QUIZ_CORRECT) && !quizzes.get(i).example.getStatus().equals(SimStExample.QUIZ_OLD))
				allCorrect = false;
			
		}
		
		if(!alreadyPresent)
		{
			ExampleAction quizAction = new ExampleAction(quiz);
			
				
			pane.add(quizAction);
				
			quizzes.add(quizAction);
		}
		
		if(!quiz.getStatus().equals(SimStExample.QUIZ_LOCKED))
			pane.hasResults = true;
		
		pane.updatePane(quiz.getStatus().equals(SimStExample.QUIZ_LOCKED), allCorrect);
		
		if(!quiz.getStatus().equals(SimStExample.QUIZ_LOCKED) && !quiz.getStatus().equals(SimStExample.QUIZ_OLD)){
			pane.setCollapsed(false);
		}
	}
	
	public void unlockQuiz(int index)
	{
		if(quizPanes.size() > index) {
			
			final QuizPane pane = quizPanes.get(index);
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					pane.updatePane(false, false);
				}
			});
		}
	}
	
	
	public void makeOldQuiz(int index)
	{
		if(quizPanes.size() > index) {
			
			final QuizPane pane = quizPanes.get(index);
			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					pane.updatePane(false, true);
				}
			});
		}
	}
	
	
	public void updateSkillometerLabelText(String ssName) 
	{
		if(ssName.isEmpty())
			return;
		
		skillometerLabel.setText(skillometerLabelText.replaceAll("SimStName", ssName));
		skillometerLabel.repaint();
		skillometer.repaint();
	}
	
	public void updateSectionMeterLabelText(String ssName) 
	{
		if(ssName.isEmpty())
			return;
		
		title.setTitle(sectionMeterLabelText.replaceAll("SimStName", ssName));
		sectionMeterPanel.repaint();
	}
	
	
	public void refreshList(boolean quiz)
	{
		if(quiz)
		{
			for(List<ExampleAction> list:quizProblems)
			{
				for(ExampleAction quizProb:list)
				{
					quizProb.refresh();
				}
			}
			
			for(List<JLabelIcon> list:quizProblemsJLabelIcon)
			{
				for(JLabelIcon quizProb:list)
				{
					quizProb.refresh();
				}
			}
			
			
		}
		else
		{
			for(List<ExampleAction> list:exampleProblems)
			{
				for(ExampleAction example:list)
				{
					example.refresh();
				}
			}
		}
	}
	
	
	public void clearQuizzes()
	{
		for(int i =0;i<sections.size();i++)
		{
			QuizPane pane = quizPanes.get(i);
			pane.removeAll();
			pane.setCollapsed(true);
			pane.hasResults = false;
			quizProblems.get(i).clear();
			quizProblemsJLabelIcon.get(i).clear();
			pane.updatePane(true, false);	//<-- this was true false
		}
	}
    
	
	public void clearQuizzesfinalChallenge()
	{
		for(int i =0;i<sections.size();i++)
		{
			QuizPane pane = quizPanes.get(i);
			//pane.removeAll();
			pane.setCollapsed(true);
			pane.hasResults = false;
			quizProblems.get(i).clear();
			quizProblemsJLabelIcon.get(i).clear();

			pane.updatePane(false, true);	//<-- this was true false
		}
	}
	
	
	class PopupListener extends MouseAdapter {
		PopupListener(JPopupMenu menu)
		{
			popup = menu;
		}
		JPopupMenu popup;
		
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	            popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	
	JFrame quizSplashScreen;
	StudentAvatarDisplay quizSplashStudent;
	JLabel quizMessage;
	
	public void setUpTakingQuiz()
	{
		if(quizSplashScreen != null)
			quizSplashScreen.setVisible(false);
		quizSplashScreen = new JFrame();
		quizSplashScreen.setTitle("Taking Quiz");
		quizSplashScreen.setSize(400, 175);
		quizSplashScreen.setVisible(true);
		quizSplashScreen.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		quizSplashScreen.setAlwaysOnTop(true);
		quizSplashScreen.setLocationRelativeTo(this);
		quizSplashScreen.getContentPane().setBackground(quizColor);
		quizSplashStudent = new StudentAvatarDisplay(SimStPLE.STUDENT_IMAGE);
		quizSplashStudent.setExpression(SimStPLE.THINK_EXPRESSION);
		quizSplashScreen.getContentPane().add(quizSplashStudent);
		quizMessage = new JLabel("Working on Quiz Problem #1");
		quizSplashScreen.getContentPane().add(quizMessage);
		quizSplashScreen.getContentPane().setLayout(null);
		quizSplashStudent.setBounds(0, 0, StudentAvatarDisplay.PREFERRED_WIDTH, StudentAvatarDisplay.PREFERRED_HEIGHT);
		quizMessage.setBounds(StudentAvatarDisplay.PREFERRED_WIDTH+10, 75, 400-StudentAvatarDisplay.PREFERRED_WIDTH+10, 30);
	}
	
	public void setQuizMessage(String message)
	{
		if (quizMessage!=null)
			quizMessage.setText(message);
	}
	
	public void hideTakingQuiz()
	{
		if(quizSplashScreen != null)
			quizSplashScreen.setVisible(false);
	}

	@Deprecated
	public void addTrophy(boolean show){    }
	@Deprecated
	public void augmentMedals(int medals, boolean show){   }
	@Deprecated
	public Component createProblemBank(String[] columns, Object[][] problemBank) { return null; }
	@Deprecated
	public void displayQuizResults(String solutions) { }
	@Deprecated
	public void displayQuizResults(String solutions, int sectionNumber, boolean complete, int levelNumber) { }
	@Deprecated
	public int getMedalCount() { return 0; }
	@Deprecated
    public JLabel getSimStAvatorIcon() { return null;  }
	@Deprecated
	public JTabbedPaneWithCloseIcons getTabPane() { return null; }
	@Deprecated
	public void setImageTeacher(boolean isTeacher) { }
	@Deprecated
	public void showMedals(boolean show) { }
	@Deprecated
	public void showTextResponseOptions(final boolean show, List<String> options) 
	{
		this.showTextResponse(show);
	}
	public String getExampleProblem() {
		return exampleProblem;
	}
	public void setExampleProblem(String exampleProblem) {
		this.exampleProblem = exampleProblem;
	}
	
	
}
