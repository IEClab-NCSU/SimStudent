package pact.CommWidgets;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.client.HintMessagesManagerForClient;
import edu.cmu.pact.ctat.CtatLMSClient;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;
import edu.cmu.pact.miss.SimStWrapperSupport;


//////////////////////////////////////////////////////
/**
 * This class is used as the base class for tutor infterfaces built in the
 * authoring tools.
 */
// ////////////////////////////////////////////////////
public class TutorWindow extends AbstractCtatWindow implements MouseListener {

    public static final String LOGOUT_TEXT = "Save and Log Out";

    public static final String LOGIN_TEXT = "Log In";

	private JMenuItem retractOneStepMenu;
	
    javax.swing.JMenu authorMenu;

    javax.swing.JMenu fileMenu;

    javax.swing.JMenuItem advanceProblemMenuItem;

    javax.swing.JMenuItem logoutMenuItem;

    javax.swing.JMenuItem lmsLoginMenuItem;

    Container contentPane;

    Login loginWindow;

    boolean showLoginWindow = false;

    String fileName;

    String userName;

    private UniversalToolProxy utp;

    private JMenuBar menuBar;
    
    /** Delegate for common code. */
    protected final WrapperSupport wrapperSupport;

	/** Text of query to display to user when he or she presses the exit button. */
	protected String studentConfirmQuitQuestion = "Do you want to save your work and log out?";


    //protected TutorController controller;
    /**
     * Access to the BR_Controller used by this instance.
     * 
     * @return value of {@link #controller}
     */
    protected TutorController getBRController() {
        return wrapperSupport.getController();
    }


    // ////////////////////////////////////////////////////
    /**
     * Constructor
     */
    // ////////////////////////////////////////////////////
    public TutorWindow(TutorController controller) {

        super(controller.getServer());
        wrapperSupport = (VersionInformation.isRunningSimSt() && !Utils.isRuntime()
        		? new SimStWrapperSupport(this.getContentPane()) 
        		: new WrapperSupport(this.getContentPane()));

        setTitle("Student Interface");

        setName("Student Interface");
        setResizable(false);
        wrapperSupport.setController(controller);
        this.utp = wrapperSupport.getController().getUniversalToolProxy();
        setDockable(false);
    }

    /***
     * @author Vishnu Priya Chandra Sekar
     * To resize the Tutor window
     * @param resizable
     */
    public void setTutorResizable(boolean resizable) {
    	setResizable(resizable);
    }
    public void setVisible(boolean visible) {
    	if (trace.getDebugCode("wh")) trace.printStack("wh", "tutorWindow");
        super.setVisible(visible);
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void initTutorWindow() {

        addMenus();

        setCloseOperation();

        applyPreferences();

		studentConfirmQuitQuestion =
			wrapperSupport.getController().getPreferencesModel().getStringValue("Student Confirm Quit Question");
        
        Boolean loginWindowValue =
        	wrapperSupport.getController().getPreferencesModel().getBooleanValue("Login Window");
        if (loginWindowValue != null)
            showLoginWindow(loginWindowValue.booleanValue());
    }

    /**
     * 
     */
    private void setCloseOperation() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
            	if (trace.getDebugCode("close")) trace.out("close", "windowClosing event "+e);
                doLogout();
            }
        });
    }

    /**
     * 
     */
    protected void showLoginWindow(boolean loginWindowValue) {
        showLoginWindow = loginWindowValue;
        if (showLoginWindow) {
           // loginWindow = new Login(this);
           // userName = loginWindow.getUserName();
            utp.showLogin();
            if (trace.getDebugCode("log")) trace.out("log",wrapperSupport.getController().getLogger().getStudentName());
           // wrapperSupport.getController().getLoggingSupport().setStudentName(userName);
           // trace.out("log", "TutorWindow.userName to logger "+userName);
        } else { // let Logger calculate user name
            userName = wrapperSupport.getController().getLogger().getStudentName();
            if (trace.getDebugCode("log")) trace.out("log", "TutorWindow.userName from logger "+userName);
        }
    }



    //////////////////////////////////////////////////////
    /**
     * 
     */
    //////////////////////////////////////////////////////
    protected void addMenus() {

    	if (trace.getDebugCode("options")) trace.out("options", "TUTOR WINDOW: ADD MENUS NOW");
        fileMenu = new javax.swing.JMenu("Student");
        logoutMenuItem = new javax.swing.JMenuItem(LOGOUT_TEXT);
        lmsLoginMenuItem = new javax.swing.JMenuItem(LOGIN_TEXT);

        authorMenu = new javax.swing.JMenu("Teacher");
        advanceProblemMenuItem = new javax.swing.JMenuItem("Advance Problem");

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        menuBar.add(fileMenu);

        fileMenu.add(lmsLoginMenuItem);
        lmsLoginMenuItem.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        doLMSLogin();
                    }
                });

        fileMenu.add(logoutMenuItem);
        logoutMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        doLogout();
                    }
                });

        retractOneStepMenu = new JMenuItem(WrapperSupport.RETRACT_LAST_STEP);
        retractOneStepMenu.setMnemonic(KeyEvent.VK_R);
        fileMenu.add(retractOneStepMenu);
        retractOneStepMenu.addActionListener(wrapperSupport);
        retractOneStepMenu.setEnabled(true);
        retractOneStepMenu.setVisible(false);
        retractOneStepMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
                ActionEvent.ALT_MASK));

        // utp.setUseLisp(true);
//    	trace.out("options", "TUTOR WINDOW: add teacher menu: showAdvance = " + controller.getShowAdvanceProblemMenu());
//		if (controller.getShowAdvanceProblemMenu()) {
//            showAdvanceProblemMenuItem();
//        }

    }

    protected void doLMSLogin() {
    }

    public void showAdvanceProblemMenuItem() {
    	trace.out ("options", "DISPLAY ADVANCE PROBLEM MENU NOW");
        menuBar.add(authorMenu);
        authorMenu.add(advanceProblemMenuItem);
        advanceProblemMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        doAdvanceProblemConfirm();
                    }
                });
    }

    // ////////////////////////////////////////////////////
    /**
     *  confirm the teacher's password. if success send message
     * "AdvanceProblem" to Lisp Tutor.
     */
    // ////////////////////////////////////////////////////
    void doAdvanceProblemConfirm() {

        int tryTimes = 0;

        // format the start dialog
        JPasswordField passwordField = new JPasswordField(20);
        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(new Object[] { "Please enter your password:",
                passwordField });
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);

        JDialog dialog = optionPane.createDialog(getActiveWindow(), "Password");

        // allow the user try 4 times.
        while (tryTimes < 4) {
            dialog.show();

            Integer value = (Integer) optionPane.getValue();

            dialog.dispose();

            // cancel
            if (value == null || value.intValue() == JOptionPane.CANCEL_OPTION)
                return;

            // process password
            String password = String.valueOf(passwordField.getPassword());
            // trace.out(5, this, "Password = " + password);

            // upon Chang's request we hard-code teacher's passeord as "pact123"
            if (password.equals("pact123")) {
				CtatLMSClient ctatLms = utp.getController().getCTAT_LMS();
				if (ctatLms != null) {
					trace.out("inter",
							"doAdvanceProblemConfirm =>  doneActionPerformed");
					wrapperSupport.doneActionPerformed();
				} else {
					MessageObject newMessage = MessageObject.create("AdvanceProblem");
					newMessage.setVerb("NotePropertySet");
					newMessage.addPropertyElement("Object", utp.getToolProxy());
					utp.sendProperty(newMessage);

					trace.out(5, this,
							"send message 'AdvanceProblem' to Lisp Tutor: "
							+ newMessage.toString());
				}
                return;
            }

            // reformat the dialog
            optionPane.setMessage(new Object[] {
                    "Wrong password. Please enter your password:",
                    passwordField });
            dialog = optionPane.createDialog(this, "Password");

            // flush the current password
            passwordField.setText("");
            tryTimes++;
        }

        return;
    }

    public void setLogoutMenu(String newOption) {
        logoutMenuItem.setText(newOption);

    }

    /*
     * CL LMS no longer used public void doLogin() { // log in
     * pact.BehaviorRecorder.StudentFileManager.instance().login(); }
     */

    public void doLogout() {
        doLogout(true, true);
    }

    // ////////////////////////////////////////////////////
    /**
     * 
     */
    // ////////////////////////////////////////////////////
    public void doLogout(boolean confirm, boolean saveBrdFile) {
//    	trace.printStack();
        int result = JOptionPane.YES_OPTION;
        if (confirm) {
			if (studentConfirmQuitQuestion != null && studentConfirmQuitQuestion.length() > 0)
				result = JOptionPane.showConfirmDialog(this, studentConfirmQuitQuestion, "Log out",
													   JOptionPane.YES_NO_OPTION);
			else
				result = JOptionPane.YES_OPTION;
        }
        //Jinyu
        //getBRController().getMissController().autoSaveInstructions();
        if (result != JOptionPane.YES_OPTION)
            return;
        TutorController controller = wrapperSupport.getController();
        if (controller == null) {
        	trace.err("TutorWindow.doLogout(): null controller from WrapperSupport "+wrapperSupport);
        	return;
        }
    	controller.closeStudentInterface();
        doLogout(saveBrdFile, controller);
    }

    /**
     * Exit the application.
     * @param saveBrdFile argument for {@link BR_Controller#closeApplication(boolean)}
     * @param controller
     */
    private static void doLogout(boolean saveBrdFile,TutorController controller) {

        if (controller.getUniversalToolProxy() != null) {
            MessageObject mo = MessageObject.create("doLogout");
            mo.setVerb("NotePropertySet");
            controller.getUniversalToolProxy().sendProperty(mo);
        }
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
        }

        // zz save student traversed links in Lisp PRODUCTION_SYSTEM_MODE
        // if the problem is loaded from Lisp Tutor

//        boolean tutorModeFlag = controller.getMode().equalsIgnoreCase(
//                CtatModeModel.PRODUCTION_SYSTEM_MODE);
		
        if (controller.getCtatModeModel().isTDKMode()
                && controller.getProblemModel()
                        .isProblemLoadedFromLispTutor()) {
			if (trace.getDebugCode("lisp")) trace.out("lisp", "start saving traversed path.");
            controller.saveTraversedPathFile();
        }

        // Save all the files and state before exiting when in SimStudent mode
        if(controller != null && controller.getCtatModeModel() != null && controller.getCtatModeModel()
        		.isSimStudentMode()) {
        	if(controller.getServer().getMissController() != null && controller.getMissController().getSimSt() != null 
        			&& controller.getServer().getMissController().getSimSt().isWebStartMode()) {

        		controller.getServer().getMissController().getSimSt().archiveAndSaveFilesOnLogout();
        	}
        }
        
        controller.closeApplication(saveBrdFile);
    }


    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {

	// JCommWidget.removeAllHighlights();

	// Tue Mar 28 22:53:11 2006  Noboru
        // Added arg0 to handle double clicking to get focus of attention 
        // for SimStudent 
        // trace.out("miss", "Tutorsindow.mouseClicked: " + arg0);
    	((HintMessagesManagerForClient) wrapperSupport.getHintMessagesManager()).tutorWindowClicked(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
    }

    /**
     * Access to the {@link WrapperSupport} object with common methods.
     * @return WrapperSupport 
     */
    public WrapperSupport getWrapperSupport() {
		return wrapperSupport;
    }    
}

class Login extends JDialog implements ActionListener {
    JTextField nameTxt;

    JPasswordField passTxt;

    Login(JFrame parent) {
        super(parent, true);
        this.setTitle("Login");

        setSize(200, 150);

        Container panel = this.getContentPane();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel textPanel = new JPanel();
        JLabel nameLbl = new JLabel("Login Name: ");
        JLabel passLbl = new JLabel("Password: ");
        nameTxt = new JTextField(10);
        { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(nameTxt); }
        passTxt = new JPasswordField(10);

        textPanel.setLayout(new GridLayout(2, 2));
        textPanel.add(nameLbl);
        textPanel.add(nameTxt);
        textPanel.add(passLbl);
        textPanel.add(passTxt);
        panel.add(textPanel);

        JPanel buttonPanel = new JPanel();
        JButton loginBtn = new JButton("Login");
        loginBtn.setActionCommand("login");
        loginBtn.addActionListener(this);
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setActionCommand("cancel");
        cancelBtn.addActionListener(this);
        buttonPanel.add(loginBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        this.addWindowListener(new WindowAdapter() {
            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            public void windowClosing(WindowEvent arg0) {
                System.exit(0);
            }
        });

        this.getRootPane().setDefaultButton(loginBtn);
        this.pack();
        Toolkit tk = Toolkit.getDefaultToolkit();
        double x = tk.getScreenSize().getWidth() / 2 - this.getWidth() / 2;
        double y = tk.getScreenSize().getHeight() / 2 - this.getHeight() / 2;
        this.setLocation((int) x, (int) y);

        this.show();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        String actionCommand = event.getActionCommand();

        if (actionCommand.equals("login")) {
            if (nameTxt.getText() != null && passTxt.getPassword() != null) {
                if (!(nameTxt.getText()).equals("")
                        && !(passTxt.getPassword().toString()).equals("")) {
                    // send the name and password info to the ESE_Frame
                    // and also to the Production System side

                    this.setVisible(false);
                }
            }
        } else if (actionCommand.equals("cancel")) {
            System.exit(0);
        }
    }

    public String getUserName() {
        return this.nameTxt.getText();
    }

    public void setSize(Dimension d) {
        if (trace.getDebugCode("mps")) trace.out("mps", "size = " + d);
    }

}
