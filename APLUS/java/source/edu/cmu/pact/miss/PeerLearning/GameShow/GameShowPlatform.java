package edu.cmu.pact.miss.PeerLearning.GameShow;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Calendar;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStPLEActionListener;
import edu.cmu.pact.miss.PeerLearning.StudentAvatarDisplay;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameShowPlatform extends JComponent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Fields
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // BR_Controller
    BR_Controller brController = null;
    
    // A flag showing if the platform is running as an Applet
    boolean runningAsApplication = false;
    
    // SimSt Contestant control module
    Contestant contestant = null;

    // Student Interface authored by CTAT 
    JComponent studentInterface = null;
    
    // Size of the top-panel window
    Dimension platformSize = null;
    
    // SimStudent avator Icon
    JLabel simStAvatorIcon = null;
    StudentAvatarDisplay simStAvatar = null;
    JLabel matchupAvatarIcon = null;
    StudentAvatarDisplay matchupAvatar = null;
    final int simStAvatorIconWidth = 200; 
    final int simStAvatorIconHeight = 200; 
    Dimension simStAvatorIconSize = new Dimension(simStAvatorIconWidth, simStAvatorIconHeight);
    
	static Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD,14);
	//static Font chatFont = new Font(Font.SANS_SERIF,Font.PLAIN,11);
	static Font chatFont = new Font("Monospaced", Font.PLAIN,11);
		
    // SimStudent name label
    JLabel simStNameLabel = null;
    JLabel matchupNameLabel = null;
    
   
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
    
    JPanel gamePanel;
    JPanel matchupPanel;
    JPanel reviewPanel;
    
    JPanel topPanel;
    
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Constructor
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    public GameShowPlatform(JComponent tutorPanel, TutorController brController) {
    	
        BR_Controller temp = (BR_Controller)brController;
        setBrController(temp);
         
        Dimension tutorPanelPreferredSize = tutorPanel.getPreferredSize();
       
        // Reading a desk-top screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        trace.out("miss", "screenSize = " + screenSize);
        // The size of this platform is 2*(width of the tutorPanel) x (height of the screen)
        int width = tutorPanelPreferredSize.width * 2 + 100;
        setPlatformSize(new Dimension(width, screenSize.height));
        this.setPreferredSize(getPlatformSize());
        trace.out("miss", "platformSize = " + getPlatformSize());
        // This tutoring platform is BoxLayout, top to bottom
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        
        // The top row containing Student Interface
        setStudentInterface(tutorPanel);
        SimStPLE.setComponentFont(tutorPanel, labelFont);

        int tpWidth = getPlatformSize().width;
        int tpHeight = getPlatformSize().height;
        
        
        //tabPane = new JTabbedPane();
        topPanel = new JPanel();
        examplePanelSize = new Dimension(tpWidth, tpHeight);
        topPanel.setPreferredSize(getExamplePanelSize());
        topPanel.setLayout(new FlowLayout());

        
        //set up each of the different 'windows'
        setupMatchupPanel();
        setupGameshowPanel(tutorPanel);
        setupReviewPanel();
        
        topPanel.add(matchupPanel);
        topPanel.add(gamePanel);
        topPanel.add(reviewPanel);
        
        //Set up a different contestant for dummies vs real players
        if(this.getBrController().getMissController().getSimSt().isDummyContestResponse())
        {
        	setContestant(new DummyContestantV2(temp, this));
        }
        else
        {
        	setContestant(new Contestant(temp, this));
        }
        
        //Add action listeners to newly created components
        randomContest.addActionListener(getContestant());
        challengeContest.addActionListener(getContestant());
        challengeContest.setEnabled(false);
        continueButton.addActionListener(getContestant());
        participantList.addListSelectionListener(getContestant());
        participantList.setCellRenderer(getContestant().new ParticipantListCellRenderer());
        input.addActionListener(getContestant());
        groupInput.addActionListener(getContestant());
        reviewTabs.addChangeListener(getContestant());
        leaderButton.addActionListener(getContestant());
        
        topPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ctrl R"), "reconnect");
            topPanel.getActionMap().put("reconnect", getContestant().getReconnectAction());
        
        // The top row contains a StudentInterface
        this.add(topPanel);
        topPanel.add(new JLabel());

        this.requestFocus();

		viewMatchup();
		
    }

    private JFrame problemInputDialog;
    JTextField lhsInput,rhsInput;
    JButton problemInputOk,problemBankButton;
    
    //Create a frame for input of problem
    public void showProblemInputDialog()
    {    
    	int seconds = 60;
    	
    	GridBagConstraints cPrompt = new GridBagConstraints();
        cPrompt.gridx = 0;
        cPrompt.gridy = 0;
        cPrompt.fill = GridBagConstraints.BOTH;
        cPrompt.weighty = .3;
        cPrompt.weightx = 1;
        cPrompt.insets = new Insets(0,5,0,5);
        GridBagConstraints cEquation = new GridBagConstraints();
        cEquation.gridx = 0;
        cEquation.gridy = 1;
        cEquation.fill = GridBagConstraints.HORIZONTAL;
        cEquation.weighty = .2;
        cEquation.weightx = 1;
        cEquation.insets = new Insets(0,5,0,5);
        GridBagConstraints cButton = new GridBagConstraints();
        cButton.gridx = 0;
        cButton.gridy = 2;
        cButton.fill = GridBagConstraints.NONE;
        cButton.weighty = .4;
        cButton.weightx = 1;
        GridBagConstraints cButton2 = new GridBagConstraints();
        cButton2.gridx = 0;
        cButton2.gridy = 3;
        cButton2.fill = GridBagConstraints.NONE;
        cButton2.weighty = .4;
        cButton2.weightx = 1;
            	
    	problemInputDialog = new JFrame("Please Provide the Next Problem");
    	problemInputDialog.setSize(new Dimension(350,200));
    	problemInputDialog.setLocationRelativeTo(studentInterface);
    	problemInputDialog.setAlwaysOnTop(true);
    	problemInputDialog.setVisible(true);
    	problemInputDialog.getContentPane().setLayout(new GridBagLayout());
        problemInputDialog.addWindowListener(new WindowAdapter()
                {public void windowClosing(WindowEvent e){
                    getContestant().actionPerformed(new ActionEvent(problemInputDialog.getWindowListeners()[0], 0, Contestant.WINDOW_EXIT));
                }});
    	JPanel equation = new JPanel();
    	equation.setLayout(new BoxLayout(equation,BoxLayout.X_AXIS));
    	lhsInput = new JTextField();
    	{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(lhsInput); }
    	rhsInput = new JTextField();
    	{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(rhsInput); }
    	JLabel eq = new JLabel(" = ");
    	lhsInput.setFont(labelFont);
    	rhsInput.setFont(labelFont);
    	eq.setFont(labelFont);
    	equation.add(lhsInput);
    	equation.add(eq);
    	equation.add(rhsInput);
    	JLabel prompt = new JLabel("<html>Please Provide the Next Problem.<br> You have " + seconds + " seconds to respond.</html>");
    	prompt.setFont(labelFont);
    	problemInputOk = new JButton("OK");
    	problemInputOk.setActionCommand(Contestant.PROBLEM_INPUT_SUBMIT);
    	problemInputOk.addActionListener(getContestant());
    	problemInputDialog.getContentPane().add(prompt,cPrompt);
    	problemInputDialog.getContentPane().add(equation,cEquation);
    	problemInputDialog.getContentPane().add(problemInputOk,cButton);
    	
    	problemBankButton = new JButton("Generate Potential Problems");
    	problemBankButton.setActionCommand(Contestant.PROBLEM_BANK_BUTTON);
    	problemBankButton.addActionListener(getContestant());
    	problemInputDialog.getContentPane().add(problemBankButton,cButton2);
        problemInputDialog.validate();
    	
    	Timer timer = new Timer(problemInputDialog, prompt);
    }
    
    class Timer implements Runnable {
    	
    	private JFrame frame;
    	private JLabel promptLabel;
    	private Thread thread = null;
    	private int seconds = 60;
    	private final int min = 0;
    	
    	public Timer(JFrame frame, JLabel prompt) {
    		this.frame = frame;
    		this.promptLabel = prompt;
    		thread = new Thread(this);
    		thread.start();
    	}
    	
    	public void run() {
    		while(seconds > min) {
    			seconds--;
    			this.promptLabel.setText("<html>Please Provide the Next Problem:<br>(You have " + seconds + " seconds to respond.)</html>");
    			//this.frame.setTitle("You have " + seconds + "  seconds to respond");
    			try {
    				Thread.sleep(850);
    			} catch(InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    /*
     * If problem Dialog window is visible and exists, make it not visible
     */
    public void closeProblemInputDialog()
    {
    	if(problemInputDialog != null)
    		problemInputDialog.setVisible(false);
    }


    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    // Methods 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
    void initApplicationGUI(JComponent studentInterface) {
 
        setRunningAsApplication(true);
        setTopLevelFrame(new JFrame());
        getTopLevelFrame().getContentPane().add(studentInterface);

    }
    
    private boolean setToClose = false;
    
    @Override
	public void paint(Graphics g)
    {
    	super.paint(g);
    	if(!setToClose)
    	{
			setToClose = true;
			Container parent = this.getParent();
			while(parent != null && !(parent instanceof GameShowWrapper))
			{
				parent = parent.getParent();
			}
    		if(parent != null)
    		{
				((GameShowWrapper) parent).setContestant(getContestant());
    		}
    	}
    }
    
    JLabel ratingLabel,oppRatingLabel, statsLabel, oppStatsLabel;
    JLabel oppName,oppAvatarImage;
    JList participantList;

    StudentAvatarDisplay oppAvatarImg;
    
	JButton randomContest;
	JButton challengeContest;
    //JTextArea groupChat;
	JLabel groupChat;
    JTextField groupInput;
    JScrollPane chatScroll;
    JLabel commentLabel;
    JLabel leaderboard;
    JButton leaderButton;
    
    /*
     * Set up the GUI components for the matchup screen in which students can find another
     * student to start a game show with and review statistics
     */
    private void setupMatchupPanel()
    {
    	matchupPanel = new JPanel();
    	
    	JPanel mainPanel = new JPanel();
    	
    	JPanel avatarPanel = new JPanel();
    	JPanel listPanel = new JPanel();
    	JPanel buttonPanel = new JPanel();
    	
    	JPanel myAvatarPanel = new JPanel();
    	JPanel oppAvatarPanel = new JPanel();
    	
    	JPanel myAvImagePanel = new JPanel();
    	JPanel oppAvImagePanel = new JPanel();
    	
    	JPanel myRatingPanel = new JPanel();
    	JPanel oppRatingPanel = new JPanel();
    	
    	JPanel commentPanel = new JPanel();
    	JPanel leaderPanel = new JPanel();
    	
    	
    	matchupPanel.setLayout(new BoxLayout(matchupPanel, BoxLayout.X_AXIS));
    	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    	avatarPanel.setLayout(new BoxLayout(avatarPanel, BoxLayout.X_AXIS));
    	listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.X_AXIS));
    	buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    	myAvatarPanel.setLayout(new BoxLayout(myAvatarPanel, BoxLayout.X_AXIS));
    	oppAvatarPanel.setLayout(new BoxLayout(oppAvatarPanel, BoxLayout.X_AXIS));
    	myAvImagePanel.setLayout(new BoxLayout(myAvImagePanel, BoxLayout.Y_AXIS));
    	oppAvImagePanel.setLayout(new BoxLayout(oppAvImagePanel, BoxLayout.Y_AXIS));
    	myRatingPanel.setLayout(new BoxLayout(myRatingPanel, BoxLayout.Y_AXIS));
    	oppRatingPanel.setLayout(new BoxLayout(oppRatingPanel, BoxLayout.Y_AXIS));
    	commentPanel.setLayout(new BoxLayout(commentPanel, BoxLayout.X_AXIS));
    	leaderPanel.setLayout(new BoxLayout(leaderPanel, BoxLayout.Y_AXIS));
    	
    	myRatingPanel.setBorder(BorderFactory.createEtchedBorder());
    	oppRatingPanel.setBorder(BorderFactory.createEtchedBorder());
    	commentPanel.setBorder(BorderFactory.createEtchedBorder());
    	
    	mainPanel.add(avatarPanel);
    	mainPanel.add(commentPanel);
    	mainPanel.add(listPanel);
    	   	
    	matchupPanel.add(mainPanel);
    	matchupPanel.add(leaderPanel);
    	
    	avatarPanel.add(myAvatarPanel);
    	avatarPanel.add(oppAvatarPanel);
    	
    	participantList = new JList();
    	//participantList.setFont(new Font("Monospaced", Font.PLAIN,12));
    	participantList.setFont(chatFont);
    	//participantList.setPreferredSize(new Dimension(100,50));
    	//participantList.setMinimumSize(new Dimension(100,20));
    	listPanel.add(participantList);
    	listPanel.add(buttonPanel);
    	    	
    	myAvatarPanel.add(myAvImagePanel);
    	ratingLabel = new JLabel();
    	ratingLabel.setFont(labelFont);
    	//myAvatarPanel.add(ratingLabel);
    	statsLabel = new JLabel();
    	myAvatarPanel.add(myRatingPanel);
    	myRatingPanel.add(ratingLabel);
    	myRatingPanel.add(statsLabel);
        matchupAvatarIcon = new JLabel(new ImageIcon(SimStPLE.STUDENT_IMAGE));
        matchupAvatar = new StudentAvatarDisplay(SimStPLE.STUDENT_IMAGE);
        //matchupAvatar = new StudentAvatarDisplay();
        //myAvImagePanel.add(matchupAvatarIcon);
        myAvImagePanel.add(matchupAvatar);
        matchupNameLabel = new JLabel();
        matchupNameLabel.setFont(labelFont);
        myAvImagePanel.add(matchupNameLabel);

    	oppRatingLabel = new JLabel();
    	oppRatingLabel.setFont(labelFont);
    	//oppAvatarPanel.add(oppRatingLabel);
    	oppStatsLabel = new JLabel();
    	oppAvatarPanel.add(oppRatingPanel);
    	oppRatingPanel.add(oppRatingLabel);
    	oppRatingPanel.add(oppStatsLabel);
        oppAvatarPanel.add(oppAvImagePanel);
        oppAvatarImage = new JLabel();
        oppAvatarImg = new StudentAvatarDisplay();
        oppAvatarImg.clearImage();
        //oppAvImagePanel.add(oppAvatarImage);
        oppAvImagePanel.add(oppAvatarImg);
        oppName = new JLabel();
        oppName.setFont(labelFont);
        oppAvImagePanel.add(oppName);
        

    	commentLabel = new JLabel();
    	commentLabel.setFont(chatFont);
        commentPanel.add(commentLabel);
        
        challengeContest = new JButton("Challenge Selected Student");
        challengeContest.setActionCommand(Contestant.CONTEST_CHALLENGE_BUTTON);
        buttonPanel.add(challengeContest);
        
        randomContest = new JButton("Enter Random Game Show");
        randomContest.setActionCommand(Contestant.CONTEST_REQUEST_BUTTON);
        //buttonPanel.add(randomContest);
        
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setPreferredSize(new Dimension(425,175));
        mainPanel.add(chatPanel);
        
        GridBagConstraints cChat = new GridBagConstraints();
        cChat.gridx = 0;
        cChat.gridy = 0;
        cChat.fill = GridBagConstraints.BOTH;
        cChat.weighty = .9;
        cChat.weightx = 1;
        cChat.anchor = GridBagConstraints.FIRST_LINE_START;
        GridBagConstraints cInput = new GridBagConstraints();
        cInput.gridx = 0;
        cInput.gridy = 1;
        cInput.fill = GridBagConstraints.BOTH;
        cInput.weighty = .1;
        cInput.weightx = 1;
        
        //groupChat = new JTextArea();
        groupChat = new JLabel();
        groupChat.setText("<html>");
        groupChat.setBackground(Color.white);
        groupChat.setVerticalAlignment(JLabel.TOP);
        //groupChat.setEditable(false);
        groupChat.setFont(chatFont);
        chatScroll = new JScrollPane(groupChat);
        chatScroll.setBackground(Color.white);
        chatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        groupInput = new JTextField();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(groupInput); }
        groupInput.setFont(chatFont);
        groupInput.setActionCommand(Contestant.GROUP_INPUT_FIELD);
        chatPanel.add(chatScroll, cChat);
        chatPanel.add(groupInput, cInput);
        
        leaderboard = new JLabel();
        leaderboard.setFont(chatFont);
        leaderboard.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        leaderPanel.add(leaderboard);
        leaderButton = new JButton("Refresh Leaderboard");
        leaderButton.setActionCommand(Contestant.LEADERBOARD_BUTTON);
        leaderPanel.add(leaderButton);
        leaderPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
    }
    
    JLabel hostSpeech, problem, hostImage, contImage, oppImage, contLabel, oppLabel, contAnswer, oppAnswer;
    JLabel contScore, oppScore;
    JLabel chat;
    JTextField input;
    JScrollPane privateChatScroll;
    StudentAvatarDisplay contSimStAvatar,contOppAvatar;
    
    /*
     * Set up a gameshow panel in which the actual game show competition takes place, solving the
     * problems, feedback on correctness, host messages
     */
    private void setupGameshowPanel(JComponent tutorPanel)
    {

        gamePanel = new JPanel();
        //gamePanel.setPreferredSize(getExamplePanelSize());
        gamePanel.setLayout(new GridBagLayout());
        //gamePanel.add(tutorPanel);
        
        GridBagConstraints cHost = new GridBagConstraints();
        cHost.gridx = 0;
        cHost.gridy = 0;
        cHost.gridwidth = 1;
        GridBagConstraints cCont = new GridBagConstraints();
        cCont.gridx = 1;
        cCont.gridy = 0;
        cCont.gridwidth = 2;
        GridBagConstraints cWork = new GridBagConstraints();
        cWork.gridx = 3;
        cWork.gridy = 0;
        cWork.gridwidth = 3;
        
        JPanel hostPanel = new JPanel();
        JPanel contestantPanel = new JPanel();
        JPanel workPanel = new JPanel();
        
        hostPanel.setLayout(new BoxLayout(hostPanel, BoxLayout.Y_AXIS));
        contestantPanel.setLayout(new BoxLayout(contestantPanel, BoxLayout.Y_AXIS));
        workPanel.setLayout(new BoxLayout(workPanel, BoxLayout.Y_AXIS));
                
        gamePanel.add(hostPanel, cHost);
        gamePanel.add(contestantPanel, cCont);
        gamePanel.add(workPanel, cWork);
        
        hostSpeech = new JLabel("Welcome to the Contest!");
        hostSpeech.setFont(labelFont);
        hostSpeech.setSize(new Dimension(200,200));
        hostSpeech.setBorder(BorderFactory.createEtchedBorder());
        hostImage = new JLabel(createImageIcon("img/host.png"));
        hostPanel.add(hostSpeech);
        hostPanel.add(hostImage);
                
        problem = new JLabel();
        problem.setFont(labelFont);
        problem.setSize(new Dimension(400,100));
        problem.setBorder(BorderFactory.createEtchedBorder());
        JPanel contsPanel = new JPanel();
        contsPanel.setLayout(new BoxLayout(contsPanel, BoxLayout.X_AXIS));
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new GridBagLayout());
        chatPanel.setPreferredSize(new Dimension(325,175));
        contestantPanel.add(problem);
        contestantPanel.add(contsPanel);
        contestantPanel.add(chatPanel);
        
        GridBagConstraints cChat = new GridBagConstraints();
        cChat.gridx = 0;
        cChat.gridy = 0;
        cChat.fill = GridBagConstraints.BOTH;
        cChat.weighty = .9;
        cChat.weightx = 1;
        GridBagConstraints cInput = new GridBagConstraints();
        cInput.gridx = 0;
        cInput.gridy = 1;
        cInput.fill = GridBagConstraints.BOTH;
        cInput.weighty = .1;
        cInput.weightx = 1;
        
        chat = new JLabel();
        chat.setText("<html>");
        chat.setBackground(Color.white);
        chat.setVerticalAlignment(JLabel.TOP);
        //chat.setEditable(false);
        chat.setFont(chatFont);
        privateChatScroll = new JScrollPane(chat);
        privateChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        input = new JTextField();
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(input); }
        input.setFont(chatFont);
        input.setActionCommand(Contestant.INPUT_FIELD);
        chatPanel.add(privateChatScroll, cChat);
        chatPanel.add(input, cInput);
        
        JPanel conts1Panel = new JPanel();
        conts1Panel.setLayout(new BoxLayout(conts1Panel, BoxLayout.Y_AXIS));
        JPanel conts2Panel = new JPanel();
        conts2Panel.setLayout(new BoxLayout(conts2Panel, BoxLayout.Y_AXIS));
        contsPanel.add(conts1Panel);
        contsPanel.add(conts2Panel);
        
        JPanel contLabelPanel = new JPanel();
       // contLabelPanel.setLayout(new BoxLayout(contLabelPanel, BoxLayout.X_AXIS));
        JPanel oppLabelPanel = new JPanel();
        //oppLabelPanel.setLayout(new BoxLayout(oppLabelPanel, BoxLayout.X_AXIS));
        contImage = new JLabel(new ImageIcon(SimStPLE.STUDENT_IMAGE));
        contSimStAvatar = new StudentAvatarDisplay(SimStPLE.STUDENT_IMAGE);
        
        ///contSimStAvatar.setBorder(BorderFactory.createEtchedBorder());
        
        oppImage = new JLabel(new ImageIcon(SimStPLE.STUDENT_IMAGE));
        contOppAvatar = new StudentAvatarDisplay(SimStPLE.STUDENT_IMAGE);
        contAnswer = new JLabel();
        contAnswer.setFont(labelFont);
        contAnswer.setMinimumSize(new Dimension(50,30));
        contAnswer.setBorder(BorderFactory.createEtchedBorder());
        contAnswer.setSize(new Dimension(100,50));
        oppAnswer = new JLabel();
        oppAnswer.setFont(labelFont);
        oppAnswer.setMinimumSize(new Dimension(50,30));
        oppAnswer.setBorder(BorderFactory.createEtchedBorder());
        oppAnswer.setSize(new Dimension(100,50));
        //conts1Panel.add(contImage);
        conts1Panel.add(contSimStAvatar);
        conts1Panel.add(contLabelPanel);
        conts1Panel.add(contAnswer);
        //conts2Panel.add(oppImage);
        conts2Panel.add(contOppAvatar);
        conts2Panel.add(oppLabelPanel);
        conts2Panel.add(oppAnswer);
        
        contLabel = new JLabel();
        contScore = new JLabel();
        contLabelPanel.add(contLabel);
        contLabelPanel.add(contScore);

        oppLabel = new JLabel();
        oppScore = new JLabel();
        oppLabelPanel.add(oppLabel);
        oppLabelPanel.add(oppScore);
        
        simStAvatorIcon = new JLabel();
        simStAvatorIcon.setFont(labelFont);
        workPanel.add(simStAvatorIcon);
        //simStAvatar = new StudentAvatarDisplay();
        //workPanel.add(simStAvatar);
        SimStPLE.setComponentEnabled(false,tutorPanel);
        workPanel.add(tutorPanel);
      
        simStNameLabel = new JLabel();

        
    }
    
    JButton continueButton;
    JLabel reviewHost, reviewHostSpeech;
    JTabbedPane reviewTabs;
    JLabel reviewContImgOverall, reviewContNameOverall, reviewOppImgOverall, reviewOppNameOverall;
    JLabel reviewContOverall, reviewOppOverall;
    JLabel[] reviewContName, reviewContImg, reviewOppName, reviewOppImg, reviewCont, reviewOpp;
    JLabel[] reviewContCorrect,reviewOppCorrect;
    
    StudentAvatarDisplay reviewContAvatarOverall,reviewOppAvatarOverall;
    StudentAvatarDisplay[] reviewContAvatar,reviewOppAvatar;
    
    /*
     * Set up a review panel in which the statistics from a just finished game are displayed, along
     * with announcement of the winner by the host and tabs which show the solutions of both this
     * contestant and their opponent for each problem
     */
    private void setupReviewPanel()
    {
    	reviewPanel = new JPanel();
    	
    	gamePanel.setLayout(new GridBagLayout());
        //gamePanel.add(tutorPanel);
        
        GridBagConstraints cHost = new GridBagConstraints();
        cHost.gridx = 0;
        cHost.gridy = 0;
        cHost.gridwidth = 2;
        GridBagConstraints cCont = new GridBagConstraints();
        cCont.gridx = 2;
        cCont.gridy = 0;
        cCont.gridwidth = 6;
        cCont.fill = GridBagConstraints.BOTH;
        
        JPanel hostPanel = new JPanel();
        reviewPanel.add(hostPanel, cHost);
       // JPanel contPanel = new JPanel();
        //reviewPanel.add(contPanel, cCont);

        hostPanel.setLayout(new BoxLayout(hostPanel, BoxLayout.Y_AXIS));
        reviewHostSpeech = new JLabel("Welcome to the Contest!");
        reviewHostSpeech.setFont(labelFont);
        reviewHostSpeech.setSize(new Dimension(200,200));
        reviewHostSpeech.setBorder(BorderFactory.createEtchedBorder());
        reviewHost = new JLabel(createImageIcon("img/host.png"));
        hostPanel.add(reviewHostSpeech);
        hostPanel.add(reviewHost);
        
        reviewTabs = new JTabbedPane();
        reviewTabs.setMinimumSize(new Dimension(800,400));

    	reviewContName = new JLabel[5];
    	reviewContImg = new JLabel[5];
    	reviewContAvatar = new StudentAvatarDisplay[5];
    	reviewOppName = new JLabel[5];
    	reviewOppImg  = new JLabel[5];
    	reviewOppAvatar = new StudentAvatarDisplay[5];
    	reviewCont = new JLabel[5];
    	reviewOpp = new JLabel[5];
    	reviewContCorrect = new JLabel[5];
    	reviewOppCorrect = new JLabel[5];
        
        JPanel overallTab = new JPanel();
    	overallTab.setPreferredSize(new Dimension(700,300));
        overallTab.setLayout(new GridLayout(1,2));
        reviewTabs.addTab("Overall", overallTab);
        //contPanel.add(reviewTabs);
        reviewPanel.add(reviewTabs,cCont);
        
        
        GridBagConstraints cAvatar = new GridBagConstraints();
        cAvatar.gridx = 0;
        cAvatar.gridy = 0;
        cAvatar.gridwidth = 1;
        cAvatar.gridheight = 1;
        cAvatar.weighty = .3;
        cAvatar.fill = GridBagConstraints.HORIZONTAL;
        cAvatar.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints cDetails = new GridBagConstraints();
        cDetails.gridx = 0;
        cDetails.gridy = 1;
        cDetails.gridwidth = 1;
        cDetails.gridheight = 3;
        cDetails.weighty = .9;
        cDetails.fill = GridBagConstraints.BOTH;
        cDetails.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints cPaper = new GridBagConstraints();
        cPaper.gridx = 0;
        cPaper.gridy = 1;
        cPaper.gridwidth = 1;
        cPaper.gridheight = 4;
        
        JPanel contOverall = new JPanel();
        contOverall.setBorder(BorderFactory.createEtchedBorder());
        //contOverall.setLayout(new BoxLayout(contOverall, BoxLayout.Y_AXIS));
        contOverall.setLayout(new GridBagLayout());
        overallTab.add(contOverall);
        JPanel oppOverall = new JPanel();
        oppOverall.setBorder(BorderFactory.createEtchedBorder());
        //oppOverall.setLayout(new BoxLayout(oppOverall, BoxLayout.Y_AXIS));
        oppOverall.setLayout(new GridBagLayout());
        overallTab.add(oppOverall);
        
        JPanel contOverallNameImg = new JPanel();
        contOverallNameImg.setLayout(new BoxLayout(contOverallNameImg, BoxLayout.X_AXIS));
        contOverall.add(contOverallNameImg, cAvatar);
        reviewContImgOverall = new JLabel();
        reviewContAvatarOverall = new StudentAvatarDisplay();
        reviewContNameOverall = new JLabel();
        reviewContNameOverall.setFont(labelFont);
        //contOverallNameImg.add(reviewContImgOverall);
        contOverallNameImg.add(reviewContAvatarOverall);
        contOverallNameImg.add(reviewContNameOverall);
        reviewContOverall = new JLabel();
        reviewContOverall.setFont(labelFont);
        contOverall.add(reviewContOverall, cDetails);
                
        JPanel oppOverallNameImg = new JPanel();
        oppOverallNameImg.setLayout(new BoxLayout(oppOverallNameImg, BoxLayout.X_AXIS));
        oppOverall.add(oppOverallNameImg, cAvatar);
        reviewOppImgOverall = new JLabel();
        reviewOppAvatarOverall = new StudentAvatarDisplay();
        reviewOppNameOverall = new JLabel();
        reviewOppNameOverall.setFont(labelFont);
        //oppOverallNameImg.add(reviewOppImgOverall);
        oppOverallNameImg.add(reviewOppAvatarOverall);
        oppOverallNameImg.add(reviewOppNameOverall);
        reviewOppOverall = new JLabel();
        reviewOppOverall.setFont(labelFont);
        oppOverall.add(reviewOppOverall,cDetails);
        
        for(int i=0;i<5;i++)
        {
        	setUpReviewPane(i);
        }
        
    	continueButton = new JButton("Continue");
    	continueButton.setActionCommand(Contestant.REVIEW_CONTINUE_BUTTON);
    	hostPanel.add(continueButton);
    	
    	
    }
    
    /*
     * Set up an individual tab showing contestant and their opponents solution paths to one
     * problem
     * Number argument is the index of the problem 0-4
     */
    public JPanel setUpReviewPane(int number)
    {

        GridBagConstraints cAvatar = new GridBagConstraints();
        cAvatar.gridx = 0;
        cAvatar.gridy = 0;
        cAvatar.gridwidth = 1;
        cAvatar.gridheight = 1;
        cAvatar.weighty = .3;
        cAvatar.fill = GridBagConstraints.HORIZONTAL;
        cAvatar.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints cDetails = new GridBagConstraints();
        cDetails.gridx = 0;
        cDetails.gridy = 1;
        cDetails.gridwidth = 1;
        cDetails.gridheight = 3;
        cDetails.weighty = .9;
        cDetails.fill = GridBagConstraints.BOTH;
        cDetails.anchor = GridBagConstraints.NORTHWEST;
        GridBagConstraints cCheck = new GridBagConstraints();
        cCheck.gridx = 0;
        cCheck.gridy = 4;
        cCheck.gridwidth = 1;
        cCheck.gridheight = 1;
        cCheck.weighty = .2;
        cCheck.anchor = GridBagConstraints.PAGE_END;
        
    	JPanel tab = new JPanel();
    	tab.setPreferredSize(new Dimension(700,300));
        tab.setLayout(new GridLayout(1,2));
        reviewTabs.addTab("Problem #"+(number+1), tab);
                
        JPanel cont = new JPanel();
        cont.setBorder(BorderFactory.createEtchedBorder());
        //cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setLayout(new GridBagLayout());
        tab.add(cont);
        JPanel opp = new JPanel();
        opp.setBorder(BorderFactory.createEtchedBorder());
        //opp.setLayout(new BoxLayout(opp, BoxLayout.Y_AXIS));
        opp.setLayout(new GridBagLayout());
        tab.add(opp);
        
        JPanel contNameImg = new JPanel();
        contNameImg.setLayout(new BoxLayout(contNameImg, BoxLayout.X_AXIS));
        cont.add(contNameImg, cAvatar);
        reviewContImg[number] = new JLabel();
        reviewContAvatar[number] = new StudentAvatarDisplay();
        reviewContName[number] = new JLabel();
        reviewContName[number].setFont(labelFont);
        //contNameImg.add(reviewContImg[number]);
        contNameImg.add(reviewContAvatar[number]);
        contNameImg.add(reviewContName[number]);
        reviewCont[number] = new JLabel();
        reviewCont[number].setFont(labelFont);
        cont.add(reviewCont[number],cDetails);
        
        JPanel oppNameImg = new JPanel();
        oppNameImg.setLayout(new BoxLayout(oppNameImg, BoxLayout.X_AXIS));
        opp.add(oppNameImg,cAvatar);
        reviewOppImg[number] = new JLabel();
        reviewOppAvatar[number] = new StudentAvatarDisplay();
        reviewOppName[number] = new JLabel();
        reviewOppName[number].setFont(labelFont);
        //oppNameImg.add(reviewOppImg[number]);
        oppNameImg.add(reviewOppAvatar[number]);
        oppNameImg.add(reviewOppName[number]);
        reviewOpp[number] = new JLabel();
        reviewOpp[number].setFont(labelFont);
        opp.add(reviewOpp[number],cDetails);
        
        reviewContCorrect[number] = new JLabel(new ImageIcon("true.png"));
        cont.add(reviewContCorrect[number],cCheck);
        reviewOppCorrect[number] = new JLabel(new ImageIcon("true.png"));
        opp.add(reviewOppCorrect[number],cCheck);
        
        
        return tab;
    	
    }

    /*
     * Set the matchup screen to visible and the other screens to not visible
     */
    public void viewMatchup()
    {
		gamePanel.setVisible(false);
		matchupPanel.setVisible(true);
		reviewPanel.setVisible(false);
    }
    
    /*
     * Set the gameshow screen to visible and the other screens to not visible
     */
    public void viewGameshow()
    {
    	//participantList.setSelectedIndex(-1);
		participantList.clearSelection();
		gamePanel.setVisible(true);
		matchupPanel.setVisible(false);
		reviewPanel.setVisible(false);
    }
    
    /*
     * Set the review screen to visible and the other screens to not visible.
     * Start with the overall summary tab as the one showing.
     */
    public void viewReview()
    {
		gamePanel.setVisible(false);
		matchupPanel.setVisible(false);
		reviewPanel.setVisible(true);
		reviewTabs.setSelectedIndex(0);
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

    public Contestant getContestant() {
        return contestant;
    }
    public void setContestant(Contestant contest) {
        this.contestant = contest;
    }

    public JComponent getStudentInterface() {
        return studentInterface;
    }
    public void setStudentInterface(JComponent studentInterface) {
        this.studentInterface = studentInterface;
    }

    public void setParticipantList(Object[] names)
    {
    	participantList.setListData(names);
    }
   
    public Competitor getSelectedParticipant()
    {
    	return (Competitor) participantList.getSelectedValue();
    }
    
    public JFrame getTopLevelFrame() {
        return topLevelFrame;
    }
    public void setTopLevelFrame(JFrame topLevelFrame) {
        this.topLevelFrame = topLevelFrame;
    }

    //Set the contestant image in all locations it is used
    public void setImage(String img)
    {    	
    	ImageIcon newImg = new ImageIcon(img);
    	Image thumbnail = newImg.getImage().getScaledInstance(86, -1, Image.SCALE_DEFAULT);
    	//simStAvatorIcon.setIcon(new ImageIcon(thumbnail));
    	//simStAvatar.setImage(img);
    	contImage.setIcon(newImg);
    	contSimStAvatar.setImage(img);
    	//getSimStAvatorIcon().setIcon(new ImageIcon(img));
    	//matchupAvatarIcon.setIcon(newImg);
    	matchupAvatar.setImage(img);
    	
    	reviewContImgOverall.setIcon(new ImageIcon(thumbnail));
    	reviewContAvatarOverall.setImage(img);
    	for(int i=0;i<reviewContImg.length;i++)
    	{
    		reviewContImg[i].setIcon(new ImageIcon(thumbnail));
    		reviewContAvatar[i].setImage(img);
    	}
        
    }
    
    public void setGameShowExpression(String express)
    {
    	contSimStAvatar.setExpression(express);
    }
    
    public void setOppGameShowExpression(String express)
    {
    	contOppAvatar.setExpression(express);
    }
    
    public void setReviewExpression(String express)
    {
    	reviewContAvatarOverall.setExpression(express);
    	for(int i=0;i<reviewContImg.length;i++)
    	{
    		reviewContAvatar[i].setExpression(express);
    	}
    }
    
    public void setOppReviewExpression(String express)
    {
    	reviewOppAvatarOverall.setExpression(express);
    	for(int i=0;i<reviewContImg.length;i++)
    	{
    		reviewOppAvatar[i].setExpression(express);
    	}
    }
    
    //Set the opponent image & name in all locations it is used
    public void setOpponent(Competitor comp)
    {
    	if(comp == null)
    	{
        	ImageIcon newImg = new ImageIcon();
        	//oppImage.setIcon(newImg);
        	contOppAvatar.clearImage();
        	oppName.setText("");
        	oppLabel.setText("");
        	oppRatingLabel.setText("");
        	oppAvatarImage.setIcon(newImg);
        	oppAvatarImg.clearImage();
        	oppStatsLabel.setText("");

        	reviewOppNameOverall.setText("");
        	return;
    	}
    	ImageIcon newImg = new ImageIcon(comp.img);
    	oppImage.setIcon(newImg);
    	contOppAvatar.setImage(comp.img);
    	oppName.setText(comp.name);
    	oppLabel.setText(comp.name);
    	oppRatingLabel.setText("  "+comp.rating);
    	oppAvatarImage.setIcon(newImg);
    	oppAvatarImg.setImage(comp.img);
    	oppStatsLabel.setText("<html>Wins: "+comp.wins+"<br>Losses: "+comp.losses+"<br>Ties: "+comp.ties+"</html>");

    	reviewOppNameOverall.setText(comp.name);
    	if(comp.img != null)
    	{
	    	Image thumbnail = newImg.getImage().getScaledInstance(86, -1, Image.SCALE_DEFAULT);
	    	reviewOppImgOverall.setIcon(new ImageIcon(thumbnail));
	    	reviewOppAvatarOverall.setImage(comp.img);
	    	for(int i=0;i<reviewOppImg.length;i++)
	    	{
	    		reviewOppImg[i].setIcon(new ImageIcon(thumbnail));
	    		reviewOppAvatar[i].setImage(comp.img);
	        	reviewOppName[i].setText(comp.name);
	    	}
    	}
    }
    
    //Set the contestant name in all locations it is used
    public void setName(String name)
    {
    	matchupNameLabel.setText(name);
    	contLabel.setText(name);
    	reviewContNameOverall.setText(name);
    	simStAvatorIcon.setText(name+"'s Worksheet");
    	for(int i=0;i<reviewContName.length;i++)
    	{
        	reviewContName[i].setText(name);
    	}
    }
    
    public void setRating(int rating)
    {
    	ratingLabel.setText(rating+"  ");
    }
    
    //Set the rating and win/loss/tie counts
    public void setRating(Competitor comp)
    {
    	ratingLabel.setText(comp.rating+"  ");
    	statsLabel.setText("<html>Wins: "+comp.wins+"<br>Losses: "+comp.losses+"<br>Ties: "+comp.ties+"</html>");
    }
    
    public void enableChallengeButton(boolean enable)
    {
    	challengeContest.setEnabled(enable);
    }
    
    public void colorOpponentRating(Color color)
    {
    	oppRatingLabel.setForeground(color);
    }
    
    public void setAnswer(String answer)
    {
    	contAnswer.setText(answer);
    }
  
    public void setOppAnswer(String answer)
    {
    	oppAnswer.setText(answer);
    }
    
    public void clearAnswers()
    {
    	contAnswer.setText("");
    	oppAnswer.setText("");
    	contAnswer.setForeground(Color.black);
    	oppAnswer.setForeground(Color.black);
    }
    
    public void setCorrectness(boolean cont, boolean opp)
    {
    	if(cont)
    	{
    		contAnswer.setForeground(Color.green.darker());
    	}
    	else
    	{
    		contAnswer.setForeground(Color.red);
    	}
    	if(opp)
    	{
    		oppAnswer.setForeground(Color.green.darker());
    	}
    	else
    	{
    		oppAnswer.setForeground(Color.red);
    	}
    }
    
    public void setScores(int cont, int opp)
    {
    	contScore.setText(""+cont);
    	oppScore.setText(""+opp);
    }
    
    public static String NOT_ATTEMPED = "Problem Not Attempted";
    
    public void setSolutions(String[] cont, String[] opp, String[] contCorr, String[] oppCorr)
    {
    	for(int i=0;i<cont.length;i++)
    	{
    		reviewCont[i].setText(cont[i]);
    	}
    	for(int i=cont.length;i<reviewCont.length;i++)
    	{
    		reviewCont[i].setText(NOT_ATTEMPED);
    	}
    	
    	for(int i=0;i<opp.length;i++)
    	{
    		reviewOpp[i].setText(opp[i]);
    	}
    	for(int i=opp.length;i<reviewOpp.length;i++)
    	{
    		reviewOpp[i].setText(NOT_ATTEMPED);
    	}
    	
    	//Set images to represent the correctness of each solution
    	for(int i=0;i<contCorr.length;i++)
    	{
    		reviewContCorrect[i].setIcon(new ImageIcon(contCorr[i]+".png"));
    	}
    	for(int i=contCorr.length;i<reviewContCorrect.length;i++)
    	{
    		reviewContCorrect[i].setIcon(new ImageIcon("noattempt.png"));
    	}
    	
    	for(int i=0;i<oppCorr.length;i++)
    	{
    		reviewOppCorrect[i].setIcon(new ImageIcon(oppCorr[i]+".png"));
    	}
    	for(int i=oppCorr.length;i<reviewOppCorrect.length;i++)
    	{
    		reviewOppCorrect[i].setIcon(new ImageIcon("noattempt.png"));
    	}
    }
    
    public void setRatings(String cont, String opp)
    {
    	reviewContOverall.setText(cont);
    	reviewOppOverall.setText(opp);
    }
    
    public void setHostSpeech(String text)
    {
    	hostSpeech.setText(text);
    	reviewHostSpeech.setText(text);
    }
    
    public void setProblem(String text)
    {
    	problem.setText(text);
    }
    
    public JLabel getSimStAvatorIcon() {
        return simStAvatorIcon;
    }
    public void setSimStAvatorIcon(JLabel simStAvatorIcon) {
        this.simStAvatorIcon = simStAvatorIcon;
    }
    
    public Dimension getPlatformSize() {
        return platformSize;
    }
    public void setPlatformSize(Dimension plaformSize) {
        this.platformSize = plaformSize;
    }
    
    //Add regular text to the chat window on the gameshow panel
    public void addPrivateChatText(String text)
    {
    	chat.setText(chat.getText()+getWrappedText(text,300,"black"));
    	scrollPaneToBottom();
    }

    //Add announcement-colored text to the chat window on the gameshow panel
    public void addPrivateAnnounceText(String text)
    {
    	chat.setText(chat.getText()+getWrappedText(text,300,"blue"));
    	scrollPaneToBottom();
    }
    
    //Add notification-colored text to the chat window on the gameshow panel
    public void addPrivateNotification(String text)
    {
    	chat.setText(chat.getText()+getWrappedText(text,300,"red"));
    	scrollPaneToBottom();
    }
    
    //Remove all text from chat window on the gameshow panel
    public void clearPrivateChat()
    {
    	chat.setText("<html>");
    }
    
    //Add regular text to the chat window on the matchup panel
    public void addGroupChatText(String text)
    {
    	groupChat.setText(groupChat.getText()+getWrappedText(text,420,"black"));
    	scrollPaneToBottom();
    }

    //Add announcement-colored text to the chat window on the matchup panel
    public void addGroupAnnounceText(String text)
    {
    	groupChat.setText(groupChat.getText()+getWrappedText(text,420,"blue"));
    	scrollPaneToBottom();
    }
    
  //Add notification-colored text to the chat window on the matchup panel
    public void addGroupNotification(String text)
    {
    	groupChat.setText(groupChat.getText()+getWrappedText(text,420,"red"));
    	scrollPaneToBottom();
    }
    
    //Get the text on the gameshow screen's chat input line and clear the line
    public String takeInput()
    {
    	String inputString = input.getText();
    	input.setText("");
    	return inputString;
    }
    
    public String getLhsInput()
    {
    	return lhsInput.getText();
    }
    
    public String getRhsInput()
    {
    	return rhsInput.getText();
    }

    //Get the text on the matchup screen's chat input line and clear the line
    public String takeGroupInput()
    {
    	String inputString = groupInput.getText();
    	groupInput.setText("");
    	return inputString;
    }
    
    //Attempt to scroll both chat scroll panes to the bottom to view the most recent text
    //Must be handled differently depending on which thread is calling it
    public void scrollPaneToBottom() {
   	 
    	if(SwingUtilities.isEventDispatchThread())
    	{
    		SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					chatScroll.getVerticalScrollBar().setValue(
						chatScroll.getVerticalScrollBar().getMaximum());
					privateChatScroll.getVerticalScrollBar().setValue(
							privateChatScroll.getVerticalScrollBar().getMaximum());
				}
			});
    	}
    	else
    	{
			//SwingUtilities.invokeLater(new Runnable() {
	    	try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						chatScroll.getVerticalScrollBar().setValue(
							chatScroll.getVerticalScrollBar().getMaximum());
						privateChatScroll.getVerticalScrollBar().setValue(
								privateChatScroll.getVerticalScrollBar().getMaximum());
					}
				});
			} catch (Exception e) {
			}
    	}
	}
    
    /*
     * Wrap provided text to width (if displayed used the chat font) by HTML markups, and
     * add in color for message.  Add timestamp at beginning.
     */
    public static String getWrappedText(String text,int width,String color)
    {
    	Calendar cal = Calendar.getInstance();
    	String now;
    	if(cal.get(Calendar.MINUTE) < 10)
        	now = "["+cal.get(Calendar.HOUR)+":0"+cal.get(Calendar.MINUTE)+"]";
    	else
    		now = "["+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE)+"]";
    	if(now.contains("[0:"))
    		now = now.replace("[0:", "[12:");
    	text = now+" "+text;
    	JLabel temp = new JLabel(text);
    	temp.setFont(chatFont);
    	
    	int textWidth = temp.getPreferredSize().width;
    	if( textWidth > width && width != 0)
    	{
    		//Text Does Not Fit - Fancy formatting
    		double percentFits = ((double) width)/textWidth;
    		int charsFit = (int) ( percentFits * text.length());
    		String remaining = text;
    		String formatted = "<font color="+color+">";
    		while(remaining.length() > 0)
    		{
    			
    			if(remaining.length() < charsFit)
    			{
    				int newLine = remaining.indexOf("\\n");
    				String tempString = "";
    				if(newLine != -1)
    				{
    					tempString = remaining.substring(0, newLine);
        				formatted += tempString+"<br>";
    					remaining = remaining.substring(newLine+2);
    				}
    				formatted += remaining+"<br>";
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
    				formatted += tempString+"<br>";
    				
    			}
    		}
    		formatted += "</font>";
    		return formatted;
       	}
    	else
    	{
    		
    		return "<font color="+color+">"+text+"<br></font>";
    	}

    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path) {
    	String file = "/edu/cmu/pact/miss/PeerLearning"+"/"+path;
    	URL url = this.getClass().getResource(file);
    	
    	return new ImageIcon(url);
    	
    }
    
    JFrame problemBankFrame;
    JTable problemBankTable;
    
    public void displayProblemBank(String[] columns, Object[][] problemBank)
    {
    	if(problemBankFrame != null && problemBankFrame.isEnabled())
    		problemBankFrame.dispose();    	
    	
    	problemBankFrame = new JFrame("Bank of Problems");
		problemBankFrame.setVisible(true);
		
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
		problemBankFrame.getContentPane().add(scroll);
		problemBankFrame.setSize(400,400);
		problemBankTable.setSize(400,400);
		//problemBankTable.setAutoCreateRowSorter(true);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(problemBankTable.getModel());
		
		/*Comparator<String> comparator = new Comparator<String>() {
		    public int compare(String s1, String s2) {
		        if(s1.contains("%") && s2.contains("%"))
		        {
		        	Integer i1 = new Integer(s1.substring(0, s1.indexOf('%')));
		        	Integer i2 = new Integer(s2.substring(0, s2.indexOf('%')));
		        	return i1.compareTo(i2);
		        }
		        return s1.compareTo(s2);
		    }
		};

		sorter.setComparator(3, comparator);*/
		
		problemBankTable.setRowSorter(sorter);
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
    }

	public void setMatchupComment(String comment) {
		// TODO Auto-generated method stub
		commentLabel.setText(comment);
	}


	public void setLeaderboard(String leaderboardText) {
		leaderboard.setText(leaderboardText);
		
	}


}
