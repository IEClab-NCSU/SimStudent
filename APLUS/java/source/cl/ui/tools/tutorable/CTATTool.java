/*
 * Copyright 2005 Carnegie Mellon University.
 */
package cl.ui.tools.tutorable;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import pact.CommWidgets.HasAllQuestionsAnswered;
import pact.CommWidgets.StudentInterfaceWrapper;
import pact.CommWidgets.TutorWrapper;
import pact.CommWidgets.WrapperSupport;
import cl.common.CL_TutorMessage;
import cl.common.PropertyConstants;
import cl.communication.SendMessage;
import cl.utilities.StringMap;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Options;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.FeedbackEnum;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.View.HintWindow.HintWindowInterface;
import edu.cmu.pact.SocketProxy.XMLConverter;
import edu.cmu.pact.Utilities.Logger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.MsgType;

/**
 * Subclass of CL's Tool abstraction {@link AbstractTutorableToolImpl}
 * which also behaves as a CTAT {@link TutorWrapper}. 
 */
public class CTATTool extends AbstractTutorableToolImpl
		implements StudentInterfaceWrapper, ActionListener {

    /** Delegate for common code. */
    private final WrapperSupport wrapperSupport;
    private LoggingSupport loggingSupport = null;
    private BR_Controller controller;
    private class DoneListener implements PropertyChangeListener {
    	/** Instance to test allQuestionsAnswered(). */
    	private final HasAllQuestionsAnswered haqa;
    	/**
    	 * set the fields
    	 */
    	public DoneListener(HasAllQuestionsAnswered haqa) {
    		this.haqa = haqa;
    	}
		public void propertyChange(PropertyChangeEvent evt) {
			trace.out("inter", "In Done Listener, allQuestionAnswered = " + haqa.allQuestionsAnswered());
			SendMessage.sendNoteValue(getMessagingAddress(), haqa.allQuestionsAnswered() ? "done" : "notdone");	
		}
    }

    /**
	 * Constructor calls superclass constructor.
	 */
	public CTATTool(StringMap props) {
		super(props);
		initializeCLFonts();
		wrapperSupport = new WrapperSupport(this);
		launch(props);
	}
    
    /*
     * Hopefully temporary fix for a CTAT bug 
     */
	private void initializeCLFonts() {
		// just because that's what has been used when creating the problems	
		Font default_text = new Font("Arial", 0, 12);
		trace.out("inter", "initializeCLFonts: default font = " + default_text);
		Font default_mono = new Font("monospaced", Font.PLAIN, 10);

		UIManager.put("TextField.font", default_text);
		UIManager.put("FormattedTextField.font", default_text);
		UIManager.put("PasswordField.font", default_mono);
		UIManager.put("TextArea.font", default_text);
		UIManager.put("TextPane.font", default_text);
		UIManager.put("EditorPane.font", default_text);

		UIManager.put("Button.font", default_text);
		UIManager.put("ToggleButton.font", default_text);
		UIManager.put("RadioButton.font", default_text);
		UIManager.put("CheckBox.font", default_text);
		UIManager.put("ColorChooser.font", default_text);
		UIManager.put("ComboBox.font", default_text);
		if(default_text != null){
			UIManager.put("InternalFrame.titleFont", default_text.deriveFont(Font.BOLD));  // dialogBold12);
		}
		UIManager.put("Label.font", default_text);
		UIManager.put("List.font", default_text);
		UIManager.put("Menu.font", default_text);
		UIManager.put("MenuItem.font", default_text);
		UIManager.put("RadioButtonMenuItem.font", default_text);
		UIManager.put("CheckBoxMenuItem.font", default_text);
		UIManager.put("PopupMenu.font", default_text);
		UIManager.put("OptionPane.font", default_text);
		UIManager.put("OptionPane.messageFont", default_text); //win
		UIManager.put("OptionPane.buttonFont", default_text); //win
		UIManager.put("Panel.font", default_text);
		UIManager.put("ProgressBar.font", default_text);
		UIManager.put("ScrollPane.font", default_text);
		UIManager.put("Viewport.font", default_text);
		UIManager.put("Spinner.font", default_mono);
		UIManager.put("TabbedPane.font", default_text);
		UIManager.put("Table.font", default_text);
		UIManager.put("TableHeader.font", default_text);

		UIManager.put("TitledBorder.font", default_text);
		UIManager.put("ToolBar.font", default_text);
		UIManager.put("ToolTip.font", UIManager.getFont("Default.font.tooltip"));
		UIManager.put("Tree.font", default_text);
	}

    /**
     * Launch this tutor and the BR that goes with it.
	 * @param args command-line args 
     */
    private void launch(StringMap props) {

    	String[] args = new String[0]; //FIXME: get these args from props
    	SingleSessionLauncher ctat_launcher = new SingleSessionLauncher(args);
    	controller = ctat_launcher.getController();
    	wrapperSupport.setController(controller);
    	loggingSupport = controller.getLoggingSupport();
		setDatasetContext(controller.getLogger(), props);
		
		String loginName = null;
		try {
            loginName = (String) props.getProperty(PropertyConstants.LOGIN_NAME);
			loggingSupport.setStudentName(loginName);
            trace.out("inter", "loginName "+loginName);
		} catch (Exception e) {
			String errMsg = "Error getting loginName: "+e;
			trace.err(errMsg);
			JOptionPane.showMessageDialog(this, errMsg, "Error retrieving login name",
					JOptionPane.ERROR_MESSAGE);
		}
		
    	String studentInterfaceName = null;
		try {
			studentInterfaceName = (String) props.getProperty(PropertyConstants.NAME);
			if (studentInterfaceName == null)
				throw new IllegalArgumentException("required property "+
						PropertyConstants.NAME+" is missing or null");
			Class jPanelClass = Class.forName(studentInterfaceName);
			JPanel studentInterface = (JPanel) jPanelClass.newInstance();
			ctat_launcher.launchCL(this, studentInterface);
		} catch (Exception e) {
			String errMsg = "Error loading student interface "+studentInterfaceName+
					": "+e;
			e.printStackTrace();
			trace.err(errMsg);
		}
		
		String problemName = null;
		String problemType = null;
		try {
			problemName = (String) props.getProperty(PropertyConstants.PROBLEM_NAME);
			problemType = (String) props.getProperty(PropertyConstants.PROBLEM_TYPE);
            URL url = Utils.getURL(problemName, this);
            trace.out("inter", "problemType "+problemType+", problemName str = "+
                    problemName+ ", url = " + url);
            if (url == null)
            	throw new FileNotFoundException("null URL for problemFileLocation "+
                        problemName);
            else
                controller.openBRFromURL(url.toString());
		} catch (Exception e) {
			String errMsg = "Error loading problem "+problemName+": "+e;
			trace.err(errMsg);
			JOptionPane.showMessageDialog(this, errMsg, "Error loading problem",
					JOptionPane.ERROR_MESSAGE);
		}

		JComponent tutorPanel = wrapperSupport.getTutorPanel();
		trace.out("inter", "Suppress feedback mode: " + controller.getProblemModel().getSuppressStudentFeedback());
		trace.out("inter", "tutor panel is HasAllQuestionsAnswered: " + (tutorPanel instanceof HasAllQuestionsAnswered));
		if (controller.getProblemModel().getSuppressStudentFeedback() != FeedbackEnum.SHOW_ALL_FEEDBACK
				&& tutorPanel instanceof HasAllQuestionsAnswered) {
    		((HasAllQuestionsAnswered)tutorPanel).addAllQuestionsAnsweredListener(new DoneListener((HasAllQuestionsAnswered)tutorPanel));
    	}
    }
    
	private void setDatasetContext(Logger logger, StringMap props){
		String schoolName = (String)props.getProperty(PropertyConstants.SCHOOL_NAME);
		String unitType = (String)props.getProperty(PropertyConstants.UNIT_TYPE);
		String unitName = (String)props.getProperty(PropertyConstants.UNIT_NAME);
		String sectionType = (String)props.getProperty(PropertyConstants.SECTION_TYPE);
		String sectionName = (String)props.getProperty(PropertyConstants.SECTION_NAME);
		String conditionTypes = (String)props.getProperty(PropertyConstants.CONDITION_TYPES);
		String conditionNames = (String)props.getProperty(PropertyConstants.CONDITION_NAMES);
		String conditionDescriptions = (String)props.getProperty(PropertyConstants.CONDITION_DESCRIPTIONS);

		trace.out("inter", "curriculum info: "
				+ "schoolName = "+ schoolName + ", "
				+ "unitType = " + unitType + ", "
				+ "unitName = " + unitName + ", "
				+ "sectionType = " + sectionType + ", "
				+ "sectionName = " + sectionName + ", "
				+ "conditionTypes = " + conditionTypes + ", "
				+ "conditionNames = " + conditionNames + ", "
				+ "conditionDescriptions = " + conditionDescriptions);

		logger.setSchoolName(schoolName);
		logger.addDatasetLevelType(unitType, 0);
		logger.addDatasetLevelName(unitName, 0);
		logger.addDatasetLevelType(sectionType, 1);
		logger.addDatasetLevelName(sectionName, 1);
		logger.setStudyConditionTypes(conditionTypes);
		logger.setStudyConditionNames(conditionNames);
		logger.setStudyConditionDescriptions(conditionDescriptions);
	}

	/**
	 * Place the student interface panel in the wrapper's container.
	 * @param tutorPanel the student interface panel
	 * @return options created by or for tutorPanel
	 */
    public CTAT_Options setTutorPanel(JComponent tutorPanel) {
    	CTAT_Options options = wrapperSupport.setTutorPanel(tutorPanel);
        wrapperSupport.loadPreferences();
        controller.goToStartState();
        validate(); // doesn't help, but like TutorWrapper
    	wrapperSupport.addActionListener(this);
    	return options;
    }
    
	/**
     * Tell the CL environment we're done.
     * @param e event from the UI component
     */
	public void actionPerformed(ActionEvent e) {
		if (COMPLETE_ALL_ITEMS.equalsIgnoreCase(e.getActionCommand()))
			SendMessage.sendNoteValue(getMessagingAddress(), "done");
	}
	
    /**
     * Convenience method for access to hint interface.
     * @return {@link #wrapperSupport}.getHintInterface()
     */
    public HintWindowInterface getHintInterface() {
    	return wrapperSupport.getHintInterface();
    }

	/**
	 * Access to the student interface panel in the wrapper's container.
	 * @return tutorPanel the student interface panel
	 */
    public JComponent getTutorPanel() {
    	return wrapperSupport.getTutorPanel();
    }

    /**
     * Access to the {@link WrapperSupport} object with common methods.
     * @return WrapperSupport 
     */
	public WrapperSupport getWrapperSupport() {
		return wrapperSupport;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		wrapperSupport.mouseClicked(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		wrapperSupport.mousePressed(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		wrapperSupport.mouseReleased(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		wrapperSupport.mouseEntered(e);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		wrapperSupport.mouseExited(e);
	}

	/**
	 * No-op for CL student interfaces.
	 * @see StudentInterfaceWrapper#enableLMSLogin(boolean)
	 */
	public void enableLMSLogin(boolean loginEnabled) {}

	/**
	 * No-op for CL student interfaces.
	 * @see StudentInterfaceWrapper#showAdvanceProblemMenuItem()
	 */
	public void showAdvanceProblemMenuItem() {}

	/**
	 * Return the enclosing frame for the calling component., but don't know 
	 * how to do this from CL interfaces.
	 * @return null
	 */
	public JFrame getActiveWindow() {
		return null;
	}
	
	/**
	 * Returns the current problem state for saving
	 * @return current problem state
	 */
	public String getProblemState() {
		return controller.getProcessTraversedLinks().getTraversedLinks_asXML();
	}
	
	/**
	 * Sets the current problem state to saved state
	 * @return void
	 */
	public void loadProblemState(String currentState) {
		controller.getProcessTraversedLinks().loadTraversedLinks_FromXML(currentState);
	}

	public Boolean needHintButton()
	{
		trace.out("needHintButton: hasHintButton = " + wrapperSupport.hasHintButton());
		return wrapperSupport.hasHintButton();
//		return !wrapperSupport.getController().getProblemModel().getSuppressStudentFeedback();
	}

	public void hintMessage(CL_TutorMessage[] messageList, int startMsgIndex)
	{
		if (!doneMessageText(messageList)) {
			MessageObject mo = createHintMessage();
			wrapperSupport.getController().handleCommMessage(mo);
		}
		else super.hintMessage(messageList, startMsgIndex);
		//		Session.getToolManager().showHintMessages( messageList, startMsgIndex );
	}
	
	private Boolean doneMessageText(CL_TutorMessage[] messageList)
	{
		for (int i=0; i<messageList.length; i++)
			if (messageList[i].getMessageText().contains("Done")) return true;
        return false;
	}
	
    private MessageObject createHintMessage() {
        MessageObject mo = MessageObject.create(MsgType.INTERFACE_ACTION, "NotePropertySet");
        mo.setSelection("Hint");
        mo.setAction("ButtonPressed");
        mo.setInput("-1");
        return mo;
    }

	public void sendNoteOpen() {
		MessageObject mo = MessageObject.create(MsgType.GLOSSARY, "InterfaceAction");
		mo.setAction("OPEN");

        trace.out("log", "sendNoteOpen: message = " + mo);
        loggingSupport.oliLog(mo, false);
	}
	
	public void sendNoteClose() {
		MessageObject mo = MessageObject.create(MsgType.GLOSSARY, "InterfaceAction");
		mo.setAction("CLOSE");

        trace.out("log", "sendNoteClose: message = " + mo);
        loggingSupport.oliLog(mo, false);
	}
	
	public void sendNoteInspectValue(String item) {
		MessageObject mo = MessageObject.create(MsgType.GLOSSARY,"InterfaceAction");
		mo.setAction("INSPECT");
		mo.setInput(item);

        trace.out("log", "sendNoteInspectValue: message = " + mo);
        loggingSupport.oliLog(mo, false);
	}
	
	public void sendNoteSearchValue(String item) {
		MessageObject mo = MessageObject.create(MsgType.GLOSSARY, "InterfaceAction");
		mo.setAction("SEARCH");
		mo.setInput(item);

        trace.out("log", "sendNoteSearchValue: message = " + mo);
        loggingSupport.oliLog(mo, false);
	}
    
	public void sendCognitiveLoadValue(String item) {
		MessageObject mo = MessageObject.create(MsgType.COGNITIVE_LOAD, "InterfaceAction");
		mo.setAction("SELECT");
		mo.setInput(item);

        trace.out("log", "sendCognitiveLoadValue: message = " + mo);
        loggingSupport.oliLog(mo, false);
	}
    
    /**
     * @return {@link WrapperSupport#getLogger()} 
     * @see pact.CommWidgets.StudentInterfaceWrapper#getLogger()
     */
    public Logger getLogger() {
    	return controller.getLogger();
    }

    /**
     * Request a hint from the tutoring system.
     * FIXME  STUB!!
     * @see pact.CommWidgets.StudentInterfaceWrapper#requestHint()
     */
	public void requestHint() {}
	
	/**
	 * Same as above
	 */
	public void requestDone() {}
}
