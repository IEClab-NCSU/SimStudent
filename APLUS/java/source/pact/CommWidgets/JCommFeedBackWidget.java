/*
 * Created on Dec 18, 2003
 *
 */
package pact.CommWidgets;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.cmu.pact.ctat.MessageObject;     // import edu.cmu.old_pact.dormin.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

/**
 * This class represents a feedback widget having a submit button along with a text 
 * field for feedback. This feedback is emailed to the given email address that can
 * be set at the interface design time using the property editor.
 * feedback should also be locked.
 * Properties specific to this widget that user can set:
 * 		1. emailAddress: Email address to which the feedback should be sent
 * 
 * @author sanket
 *
 */
public class JCommFeedBackWidget extends JCommWidget implements ActionListener{
	/**
	 * submit button when the user clicks on the submit button the feed back is sent
	 * to the server and emailed from there to the email address specified.
	 */
	JButton submitBtn = new JButton("Submit");
	/**
	 * The text field that contains the feedback message
	 */
	JTextField feedBackTxt = new JTextField(20);
	{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(feedBackTxt); }
	/**
	 * email address to which the feedback should be sent
	 */
	String emailAddress = "";
	
	public JCommFeedBackWidget(){
		setActionName("UpdateFeedBack");
		
		submitBtn.addActionListener(this);
		feedBackTxt.addActionListener(this);
		createFeedBackWidget();
	}
	
	/**
	 * 
	 */
	private void createFeedBackWidget() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel lbl = new JLabel("Feedback");
		this.add(lbl);
		// add the feedback textField
		this.add(feedBackTxt);
		// add the submit button
		this.add(submitBtn);
		locked = false;		
	}


	protected boolean initialize() {

		if (!super.initialize(getController()))
			return false;
	
		addCommWidgetName (commName);
		addCommListener ();
		if (getController().isShowWidgetInfo()) 
			setToolTipWidgetInfo();
		return true;
	}

	/**
	 * this method adds the answer to the command listner so that it can receive
	 * comm messages
	 */
	public void addCommListener () {

		String componentName = commName;

		JCommWidgetsToolProxy childProxy 
				= new JCommWidgetsToolProxy("Component", componentName, 
											getController().getUniversalToolProxy().getToolProxy(), componentName);
				
		edu.cmu.pact.CommManager.CommManager.instance().registerMessageReceiver(this, childProxy.toString());
	}

	public Object getValue(){
		return this.feedBackTxt.getText();
	}

	/**
	 *	This method returns a message to the production system to create 
	 *	working memory elements for this widget
	 */
	public MessageObject getDescriptionMessage () {
		
		
        MessageObject mo = MessageObject.create("InterfaceDescription");
        mo.setVerb("SendNoteProperty");

		if (!initialize(getController())) {
			trace.out(
				5,
				this,
				"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setProperty("WidgetType", "JCommFeedBackWidget");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));


		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();
		
if(deftemplates != null)  mo.setProperty("jessDeftemplates", deftemplates);

		
		if(instances != null)    mo.setProperty("jessInstances", instances);

		
		serializeGraphicalProperties(mo);

	    
		return mo;
	}


	/**
	 * This method is called when the state changes in the behavior recorder and
	 * the action type is a correct action
	 */
	public void doCorrectAction (String selection, String action, String input) {
		doInterfaceAction(null, null, input);
	}

	public void doLISPCheckAction (String selection, String input) {
		doInterfaceAction(null, null, input);
		locked = false;
	}

	public void doIncorrectAction (String selection, String input) {
		doInterfaceAction(selection, null, input);
		locked = false;
	}

	public void doInterfaceAction (String selection, String action, String input) {

		this.feedBackTxt.setText(input);	
	}

	
	/**
	 * 
	 */
	public boolean isChangedFromResetState () {
		if(!this.feedBackTxt.getText().equals("")){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Used to reset the commwidget
	 */
	public void reset (TutorController controller) {
		initialize();
		this.feedBackTxt.setText("");
		locked = false;
	}

	/**
	 * Returns the comm name of the currently selected choice
	 */
	public String getCommNameToSend () {
		String s = commName;
		return s;
	}
	
	/**
	 * This method creates the jess deftemplates for the multiple choice widget
	 * @return - returns a vector of deftemplates for the multiple choice widget
	 */
	public Vector createJessDeftemplates(){
		Vector deftemplates = new Vector();
		
		String choiceTemplate = "(deftemplate feedBackWidget (slot name) (slot value) (slot emailAddress))";
		deftemplates.add(choiceTemplate);		
		return deftemplates;
	}
	/**	this method is for creating the instances corresponding to the multiple choice widget
	 * @return
	 */
	public Vector createJessInstances(){
		Vector instances = new Vector();
		String str = "(bind ?" + getCommName() + " (assert (feedBackWidget (name " + getCommName() + ")(emailAddress " + this.emailAddress + "))))";

		instances.add(str);
		return instances;
	}


	public static void main(String[] args) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		dirty = true;
		sendValue();
	}
	/**
	 * @return
	 */
	public synchronized String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * @param emailAddress
	 */
	public synchronized void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void mousePressed(MouseEvent e) {
	}
}
