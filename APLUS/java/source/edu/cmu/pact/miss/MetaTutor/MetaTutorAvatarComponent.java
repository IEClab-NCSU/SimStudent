package edu.cmu.pact.miss.MetaTutor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import jess.Activation;
import pact.CommWidgets.JCommTable.TableCell;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.AskHint;
import edu.cmu.pact.miss.Sai;
import edu.cmu.pact.miss.SimSt;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.PeerLearning.AplusSpotlight;
import edu.cmu.pact.miss.PeerLearning.SimStLogger;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStTimeoutDlg;
import edu.cmu.pact.miss.jess.ModelTracer;
import edu.cmu.pact.miss.jess.ActivationListDrop;

/**
 * Shows the image for the meta tutor. Also handles the mouse click action and displays the pop-up menu for
 * the activated rule if any.
 */
public class MetaTutorAvatarComponent extends JPanel {

	/**	Default constant for Serialization */
	private static final long serialVersionUID = 1L;

	/**	Pattern to match the bug rule */
	private static final Pattern bugPrefix = Pattern.compile("^[Mm][Aa][Ii][Nn][:][:][bB][uU][gG].*");
	
	public static Object metaTutorLock = new Object();
	/** Offset for the x component of the pop-up menu. Aligns it with the meta tutor image */
	private static int OFFSET = 0;
	/**	Boolean to denote if the thread is blocked waiting for activation list */
	public static boolean isWaitingForActivationList = false;
	/**	Array to pick the message randomly when student is in the midst of solving a problem */
	private String[] METATUTOR_NO_HINT_MSG = {
						 " No hint is available right now.",
						 };
	
	public static final String MR_WILLIAMS_SAYS_MSG = " Mr. Williams says    	";
	private static final String ASK_MR_WILLIAMS_MSG = " Ask Mr. Williams    	";
	private static final String META_COGNITIVE_HINT = "MetaCognitiveHint";
	private static final String COGNITIVE_HINT = "CognitiveHint";
	private static final String COGNITIVE_HINT_SELECTION = "CLHint";
	private static final String META_COGNITIVE_HINT_SELECTION = "hint";
	private static final String HINT_ACTION = "MetaTutorClicked";
	private static final String HINT_INPUT = "-1";
	private static final int MAX_THREAD_COUNT = 1;

	private String runType = System.getProperty("appRunType");

	/**	Thread pool with MAX_THREAD_COUNT available threads at any point of time. */
	private static ExecutorService metatutorThreadPool = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
	
	/**	Display image for the meta tutor */
	private Image img;
	
	/**	Logger to log the meta tutor click action */
	private SimStLogger logger;
	
	/**	Link to the SimStudent environment */
	private SimSt simStudent;
	public SimSt getSimStudent() {
		return simStudent;
	}
	public void setSimStudent(SimSt simStudent) {
		this.simStudent = simStudent;
	}

	/**	BehaviorControlFramework instance */
	private BehaviorControlFramework bcf;
	public BehaviorControlFramework getBcf() {
		return bcf;
	}
	public void setBcf(BehaviorControlFramework bcf) {
		this.bcf = bcf;
	}

	/**	Selection, Action and Input */
	private Sai sai;
	public Sai getSai() {
		return sai;
	}
	public void setSai(Sai sai) {
		this.sai = sai;
	}

	/**	Component to highlight */
	private Component highlightedComponent;
	public Component getHighlightedComponent() {
		return highlightedComponent;
	}
	public void setHighlightedComponent(Component highlightedComponent) {
		this.highlightedComponent = highlightedComponent;
	}
	
	/**	Object to parse the XML file containing the pop-up menu messages */
	private XMLReader reader;
	
	/**	Pop up menu instance */
	private JPopupMenu menu = null;

	/**	Message for the producer and the consumer */
	private static ActivationListDrop aListDrop;
	public static ActivationListDrop getaListDrop() {
		return aListDrop;
	}

	
	/**
	  Constructs the MetaTutorAvatar
	  @param path the image to use for the avatar
	  @param ss link to the SimStudent environment
	 */
	public MetaTutorAvatarComponent(String path, SimSt ss) {
		
		init();
		Icon icon = createImageIcon(path);
		img = ((ImageIcon) icon).getImage();
		addMouseListener(listener);
		simStudent = ss;
		reader = new XMLReader(simStudent);
		new APlusPopUpMenu();
		logger = new SimStLogger(simStudent.getBrController());
	}
	
	/**
	  Initializes the environment by looking for the files if they exist locally on the system
	  or by extracting it from the jar if running on webstart
	 */
	private void init() {
		
		WebStartFileDownloader downloader = new WebStartFileDownloader();
		//String rulesFile = downloader.findFile(APlusModelTracing.JESS_RULES_FILE);
		String wmeTypesFile = downloader.findFile(ModelTracer.WME_TYPES_MT_FILE);
		String initwmFile = downloader.findFile(ModelTracer.INIT_WM_MT_FILE);
		String prodRulesFile = downloader.findFile(ModelTracer.PRODUCTION_RULES_MT_FILE);
		
		String actualPrFile=XMLReader.PR_MESSAGES_FILE;
		

		
		String prMsgsFile = downloader.findFile(XMLReader.PR_MESSAGES_FILE);
		//String prMsgsFile1 = downloader.findFile(XMLReader.PR_MESSAGESCOG_FILE);
		//String prMsgsFile2 = downloader.findFile(XMLReader.PR_MESSAGESMETACOG_FILE);
	}

	/**
	   Creates an image icon
	   @param path the path where the image is located
	   @return 
	 */
    protected ImageIcon createImageIcon(String path) {
    	
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	return new ImageIcon(url);
    }

    /**
       Highlights the widget
       @param component
     */
    public void widgetHighlighted(Component component){
    	
    	if(!(component instanceof TableCell))
    		return;
    	setHighlightedComponent(component);
    }
    
    public void paint(Graphics g){
    	super.paint(g);
    	g.drawImage(img, 0,0, this);
    }
	public ArrayList<ArrayList<String>> handleMouseClickEvent(MouseEvent e){
		if (getSimStudent().isSsAplusCtrlCogTutorMode() && getSimStudent().getModelTraceWM().getStudentEnteredProblem()!=null){
			getSimStudent().getModelTraceWM().setRequestType("hint-request");
		}


		/*If in cogTutorMode (cogTutor control), no need to show the hint menu, just display the hint. */
		if (simStudent.isSsCogTutorMode() && !simStudent.isSsAplusCtrlCogTutorMode()){
			getSimStudent().getModelTraceWM().setRequestType("hint-request");
			trace.out(" Mr Williams clicked");
			getSimStudent().getBrController().getAmt().handleInterfaceAction(COGNITIVE_HINT_SELECTION, HINT_ACTION, HINT_INPUT);
			/*keep a note that hint is given, so BTK is properly updated */
			getSimStudent().getBrController().getMissController().getSimStPLE().getSsCognitiveTutor().setStepHintGiven(true);



			return new ArrayList<ArrayList<String>>();
		}


		ArrayList<ArrayList<String>> llist = new ArrayList<ArrayList<String>>();
		int count = 0;

		if(menu != null && menu.isVisible()) {
			menu.setEnabled(false);
		}

		aListDrop = new ActivationListDrop();
		isWaitingForActivationList = true;
		simStudent.getBrController().getAmt().handleInterfaceAction("activations", "MetaTutorClicked", "-1");
		ArrayList activations = aListDrop.take();
		//trace.err("activations are : " + activations);
		isWaitingForActivationList = false;

		String message = "";
		String fired = "";
		String fireList = "";

		Iterator itr = activations.iterator();

		while(itr.hasNext()) {

			Activation act = (Activation) itr.next();
			String ruleName = act.getRule().getName();
			if(fired.length() == 0)	fired = ruleName;
			fireList += ruleName+";";

			if(count < 1) { // Include the menu-option for only the most high-priority rule
				reader.parseXMLFile(ruleName, llist);
			}
			++count;
		}

		//trace.out(" FireList : "+fireList);
		//trace.out(" Message length : "+llist.size());

		/*clear the list so we do not display anything*/
		//	if (getSimStudent().isSsMetaTutorMode() && getSimStudent().getSsMetaTutorModeLevel().equals(SimSt.METACOGNITIVE) && getSimStudent().getModelTraceWM().getStudentEnteredProblem()!=null)
		//		llist.clear();
		//	else if (getSimStudent().isSsMetaTutorMode() && getSimStudent().getSsMetaTutorModeLevel().equals(SimSt.COGNITIVE) && getSimStudent().getModelTraceWM().getStudentEnteredProblem()==null)
		//		llist.clear();

		//	if (!getSimStudent().isSsCogTutorMode() && getSimStudent().isSsMetaTutorMode() && getSimStudent().getModelTraceWM().getStudentEnteredProblem()!=null)
		//		llist.clear();


		if(count > 0 && llist.size() > 0) {
			//trace.out(" count : "+count);
			if(menu != null) {
				menu.setVisible(false);
				menu = null;
			}

			menu = new JPopupMenu();
			menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			menu.setBackground(Color.lightGray);

			JLabel label = new JLabel(ASK_MR_WILLIAMS_MSG);
			label.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
			label.setBackground(Color.lightGray);
			menu.add(label);
			menu.add(new JSeparator());

			ActionListener al = new PopUpMenuListListener();

			for(int i=0; i< llist.size(); i++) {
				JMenuItem item = new JMenuItem();
				item.setHorizontalAlignment(SwingConstants.LEFT);
				item.setActionCommand((llist.get(i)).get(0).trim());
				//item.setActionCommand(simStudent.getHintType((llist.get(i)).get(1).trim()));
				item.setText(getCognitiveMenuItem((llist.get(i)).get(1).trim()));

				item.addActionListener(al);
				//trace.out(" Adding the item ");
				menu.add(item);
				menu.add(new JSeparator());
			}
			menu.addPopupMenuListener(new PopUpMenuListListener());


			// Position the menu to avoid the menu exceed the parent window
			int componentWidth = e.getComponent().getWidth();
			int menuWidth = (int) menu.getPreferredSize().getWidth();
			int offset = 38;
			int heightOffset = 5;

			// If menuWidth > (componentWidth + 50) then set OFFSET = (componentWidth+50)-menuWidth
			if(menuWidth > (componentWidth + 50))
				offset = ((componentWidth + 50) - menuWidth);

			menu.show(e.getComponent(), offset , -((int)menu.getPreferredSize().getHeight()) + heightOffset);
			menu.repaint();

			String step = simStudent.getProblemStepString();
			logger.simStInfoLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_CLICK_ACTION, step, fired, fireList);


		} else {

			if(menu != null) {
				menu.setVisible(false);
				menu = null;
			}

			// No rule fired in the current context
			//trace.out(" No rule fired ! ");
			menu = new JPopupMenu();
			menu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			menu.setBackground(Color.lightGray);
			menu.addPopupMenuListener(new PopUpMenuListListener());






			JLabel label1 = new JLabel(MR_WILLIAMS_SAYS_MSG);
			label1.setBackground(Color.lightGray);
			label1.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
			menu.add(label1);
			menu.add(new JSeparator());

			Random generator = new Random();
			int arrayIndex = generator.nextInt(METATUTOR_NO_HINT_MSG.length); // random no. between 0 and n-1

			JLabel label2 = new JLabel(METATUTOR_NO_HINT_MSG[arrayIndex].replace("SimStName", SimSt.SimStName));
			label2.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
			label2.setBackground(Color.white);
			menu.add(label2);

			if(e != null || runType == null || !runType.equals("springBoot")) {

				// Position the menu to avoid the menu exceed the parent window
				int componentWidth = e.getComponent().getWidth();
				int menuWidth = (int) menu.getPreferredSize().getWidth();
				int offset = 38;
				int heightOffset = 5;

				// If menuWidth > (componentWidth + 50) then set OFFSET = (componentWidth+50)-menuWidth
				if (menuWidth > (componentWidth + 50))
					offset = ((componentWidth + 50) - menuWidth);

				menu.show(e.getComponent(), offset, -((int) menu.getPreferredSize().getHeight()) + heightOffset);
				menu.repaint();
			}

			String step = simStudent.getProblemStepString();
			logger.simStInfoLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_CLICK_ACTION, step, "NoHint", "");

		}
		return llist;
	}

	public List<String> hintClickHandlerSpringBoot(String actionCommand, String hintText){
		String question = "";
		Vector<String> messages = new Vector<String>();
		if(META_COGNITIVE_HINT.equalsIgnoreCase(actionCommand)) {

			messages = getSimStudent().getBrController().getAmt().handleInterfaceActionSpringBoot(META_COGNITIVE_HINT_SELECTION, HINT_ACTION, HINT_INPUT);

			//logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, source.getText(), META_COGNITIVE_HINT, logger.getCurrentTime());
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, hintText, "" , logger.getCurrentTime(), null , null , "" , "" ,
					"" , "" , 0, "" , "" , question , 0,false, META_COGNITIVE_HINT , null , logger.getCurrentTime());


		} else if(COGNITIVE_HINT.equalsIgnoreCase(actionCommand)) {

			// Ask the Cognitive class to provide the hint message
			messages = getSimStudent().getBrController().getAmt().handleInterfaceActionSpringBoot(COGNITIVE_HINT_SELECTION, HINT_ACTION, HINT_INPUT);

			//logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, source.getText(), COGNITIVE_HINT, logger.getCurrentTime());
			logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, hintText, "" , logger.getCurrentTime(), null , null , "" , "" ,
					"" , "" , 0, "" , "" , question , 0, false, COGNITIVE_HINT, null , logger.getCurrentTime());

		}
		return messages;
	}

	/**
	   Listener to listen for mouse events when the meta tutor image is clicked
	 */
	private MouseListener listener = new MouseAdapter() {
		
		/**	Invoked when the mouse button has been clicked on the component */
		public void mouseClicked(final MouseEvent e) {

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
		    		synchronized (metaTutorLock) {
						handleMouseClickEvent(e);
					}								
				}
			};
			
			Future<?> future = metatutorThreadPool.submit(runnable);
			//Thread t = new Thread(runnable);
			//t.start();
		}
		
		/**	Invoked when the mouse enters a component */
		public void mouseEntered(MouseEvent me){
			/*Determine which is the appropriate MetaTutor image and show it*/	
			String metatutorImage=(!simStudent.isSsCogTutorMode())? SimStPLE.METATUTOR_STUDENT_INTERACTION_IMAGE  :  (simStudent.isSsAplusCtrlCogTutorMode())? SimStPLE.METATUTOR_STUDENT_INTERACTION_IMAGE :  SimStPLE.METATUTOR_STUDENT_INTERACTION_IMAGE_COGTUTOR ;
			
			/*change cursor to hand*/
			Cursor cursor = Cursor.getDefaultCursor();
			cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); 
		    setCursor(cursor);
			
			changeMetaTutorImage(metatutorImage);
		
		}
		
		/**	Invoked when the mouse exits a component */
		public void mouseExited(MouseEvent me){
			
			if(menu != null && menu.isShowing())
				return;
			
			/*Determine which is the appropriate MetaTutor image and show it*/
			String metatutorImage=(!simStudent.isSsCogTutorMode())? SimStPLE.METATUTOR_IMAGE  :  (simStudent.isSsAplusCtrlCogTutorMode())? SimStPLE.METATUTOR_IMAGE :  SimStPLE.METATUTOR_IMAGE_COGTUTOR ;

			
			changeMetaTutorImage(metatutorImage);
		}
	};
	
	/**
	 * Utility method to define the cognitive hint menu item (i.e. In case of hint (not feedback), then 
	 * menu item should be something like e.g. "How do I divide 3").  If hint is for type-in, then current skill 
	 * is used in the cognitive menu item.
	 * @param xmlFileEntry the menu item defined in the xml file. Its consists of two entries: "menu item when no skill available | menu item when skill available")
	 * @return
	 */
	public String getCognitiveMenuItem1(String xmlFileEntry){
		
		String cogMenuItem=xmlFileEntry;
		AskHint hint=simStudent.askForHintQuizGradingOracle(simStudent.getBrController(), simStudent.getBrController().getCurrentNode());
		String typeOfStep= simStudent.getFoaGetter().getTypeOfStep(hint.getSelection(),simStudent.getBrController());
		
		
		if (cogMenuItem.contains(",")){
			String[] parts=xmlFileEntry.split(",");
			if (typeOfStep.equals(SimStTimeoutDlg.TYPE_TYPEIN)){		/*when skill is present (i.e. type-in), get second part (i.e. "How do I bla")*/ 
				String skill=simStudent.getFoaGetter().getStepSkill(hint.getSelection(),simStudent.getBrController());
				cogMenuItem=parts[1].replace("skill", skill);
			}
			else{						 /*when skill is not present, get the first part (i.e. "What's the next step")*/ 
				cogMenuItem=parts[0];
			}
		}
		return cogMenuItem;
	}
	
	
	/**
	 * Utility method to define the cognitive hint menu item (i.e. In case of hint (not feedback), then 
	 * menu item should be something like e.g. "How do I divide 3").  If hint is for type-in, then current skill 
	 * is used in the cognitive menu item.
	 * @param xmlFileEntry the menu item defined in the xml file. Its consists of two entries: "menu item when no skill available | menu item when skill available")
	 * @return
	 */
	public String getImprovedCognitiveMenuItem(String xmlFileEntry){	
		String cogMenuItem=xmlFileEntry;
		AskHint hint=simStudent.askForHintQuizGradingOracle(simStudent.getBrController(), simStudent.getBrController().getCurrentNode());		
		return simStudent.getFoaGetter().formulateBasedOnTypeOfString(hint.getSelection(),simStudent.getBrController(),xmlFileEntry);
		
		
	}
	
	

	
	/**
	 * Utility method to define the cognitive hint menu item (i.e. In case of hint (not feedback), then 
	 * menu item should be something like e.g. "How do I divide 3").  If hint is for type-in, then current skill 
	 * is used in the cognitive menu item.
	 * @param xmlFileEntry the menu item defined in the xml file. Its consists of two entries: "menu item when no skill available | menu item when skill available")
	 * @return
	 */
	public String getCognitiveMenuItem(String xmlFileEntry){
		
		String cogMenuItem=xmlFileEntry;
		String simStudentSkill=simStudent.getLastSkillOperand();
		if (cogMenuItem.contains(",")){
			String[] parts=xmlFileEntry.split(",");
			if (simStudentSkill!=null){		/*when skill is present (i.e. type-in), get second part (i.e. "How do I bla")*/ 
				cogMenuItem=parts[1].replace("skill", simStudentSkill);
			}
			else{						 /*when skill is not present, get the first part (i.e. "What's the next step")*/ 
				cogMenuItem=parts[0];
			}
		}
		return cogMenuItem;	
		
	}
	
	
	
	
	
	/**
	   Utility method to change the meta tutor avatar image
	   @param imageName
	 */
	public void changeMetaTutorImage(String imageName) {

		Icon icon = createImageIcon(imageName);
		img = ((ImageIcon) icon).getImage();
		revalidate();
		repaint();
	}
	
	/**
	   Utility method to find the path from the start node to the end node recursively 
	   @param startNode
	   @param endNode
	   @return
	 */
	public static Vector findPathDepthFirst(ProblemNode /*SimStNode*/ startNode, ProblemNode /*SimStNode*/ endNode){
		
		/* Base case */
		if(startNode == endNode)
			return null;
		
		/* Recursive case */
		ProblemEdge edge = null;
		if((edge = (startNode.isChildNode(endNode))) != null){
			Vector path = new Vector();
			path.add(0, edge);
			return path;
		} else {
			Vector children = startNode.getChildren();
			if(children.isEmpty())
				return null;
			for(int i=0; i < children.size(); i++){
				ProblemNode childNode = (ProblemNode) children.elementAt(i);
				Vector path = findPathDepthFirst(childNode, endNode);
				if(path != null) {
					path.add(0, startNode.isChildNode(childNode));
					return path;
				}
			}
			return null;
		}
	}

	/**
	    Pop up menu for the meta tutor component. If the student selects the menu item it invokes
	    the model tracer to see if there are any hint messages available
	 */
	private class PopUpMenuListListener implements ActionListener, PopupMenuListener {

		public void actionPerformed(ActionEvent ae) {
			String question = "";
			//System.out.println(" Calling Mr Williams " );
			if(ae.getSource() instanceof JMenuItem) {
				
				
				
				JMenuItem source = (JMenuItem) ae.getSource();
				question = source.getText();
				//logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_QUESTION_ACTION, source.getText());
			}
			if(ae.getActionCommand().equalsIgnoreCase(META_COGNITIVE_HINT)) {
				
				getSimStudent().getBrController().getAmt().handleInterfaceAction(META_COGNITIVE_HINT_SELECTION, HINT_ACTION, HINT_INPUT);
				
				JMenuItem source = (JMenuItem) ae.getSource();
				//logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, source.getText(), META_COGNITIVE_HINT, logger.getCurrentTime());
				logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, source.getText(), "" , logger.getCurrentTime(), null , null , "" , "" ,
				   		"" , "" , 0, "" , "" , question , 0,false, META_COGNITIVE_HINT , null , logger.getCurrentTime());
				
				
			} else if(ae.getActionCommand().equalsIgnoreCase(COGNITIVE_HINT)) {
							
				// Ask the Cognitive class to provide the hint message
				getSimStudent().getBrController().getAmt().handleInterfaceAction(COGNITIVE_HINT_SELECTION, HINT_ACTION, HINT_INPUT);
				
				JMenuItem source = (JMenuItem) ae.getSource();
				//logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, source.getText(), COGNITIVE_HINT, logger.getCurrentTime());
				logger.simStLog(SimStLogger.SIM_STUDENT_METATUTOR_AL, SimStLogger.METATUTOR_HINT_REQUESTED, source.getText(), "" , logger.getCurrentTime(), null , null , "" , "" ,
				   		"" , "" , 0, "" , "" , question , 0, false, COGNITIVE_HINT, null , logger.getCurrentTime());
				
			}
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			changeMetaTutorImage(SimStPLE.METATUTOR_IMAGE);
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			changeMetaTutorImage(SimStPLE.METATUTOR_STUDENT_INTERACTION_IMAGE);
		}		
	}
}
