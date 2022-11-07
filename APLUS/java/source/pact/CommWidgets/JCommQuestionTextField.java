/*
 * Created on Dec 17, 2003
 *
 */
package pact.CommWidgets;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.Highlighter;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;


/**
 * This class represents a question that has a textfield in which the student can
 * write the answer
 * Properties specific to this widget that user can set:
 * 		a. nCharacters: width of the answer field.
 * 		b. answerLayout: Where should the answer textField be placed.
 * 			1 - On the same line after the question 
 * 			2 - On the line below the question
 * @author sanket
 *
 */
public class JCommQuestionTextField extends JCommQuestion implements ActionListener, FocusListener, KeyListener{
	/**
	 * Textfield for answer
	 */
	private JTextField answerTxt = new JTextField();
	{ JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(answerTxt); }
	/**
	 * Width of the answer textfield
	 */
	private int nCharacters = 5;
	/**
	 * Determines whether the answer text field should be placed on the same line - 1
	 * as the question text or on the next line as the question text - 2
	 */
	private int answerLayout = 1;
	/**
	 * Keeps track of the number of instances of this widget
	 */
	private static int counter = 0;
	/**
	 * Boolean variable that is used to make sure that only one widget is displayed for a correct action
	 */
	private boolean alreadyDone = false;
	
	protected Highlighter defaultHighlighter;

	String previousValue;
	/**
	 * indicates whether the text field should be used as an algebra widget
	 */
	private boolean isAlgebraWidget = true;
	/**
	 * number of digits after the decimal point to round to
	 *
	 */
	private int roundTo = 2;
	
	protected String resetValue = "";
	
	public JCommQuestionTextField(){
		setActionName(UPDATE_QUESTION_TEXT_FIELD);
		answerTxt.setDocument(new JCommDocument());
	
		locked = false;		
		((JCommDocument)this.answerTxt.getDocument()).locked = false;
		
		this.answerTxt.setColumns(this.nCharacters);
		this.answerTxt.addActionListener(this);
		this.answerTxt.addFocusListener(this);
		this.answerTxt.addKeyListener(this);
		defaultHighlighter = answerTxt.getHighlighter();
		createQuestionPanel();

	}
	
	/**
	 * 
	 */
	private void createQuestionPanel() {
        this.removeAll();
		if(this.answerLayout == 1){
			// set the layout
			this.setLayout(new FlowLayout(FlowLayout.LEFT));
			// add the question
			this.add(this.questionLbl);
			// add the answer text
			this.add(this.answerTxt);			
			// add the feedback widget
		}else{
			GridBagLayout gb = new GridBagLayout();
			GridBagConstraints gbc = new GridBagConstraints();
			this.setLayout(gb);
			
			// add the question
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.gridwidth = 3;
			gb.setConstraints(questionLbl, gbc);
			this.add(questionLbl);
			
			// add the answer textfield
			gbc.gridx = 0;
			gbc.gridy = 1;
			gbc.gridwidth = 1;
			gb.setConstraints(answerTxt, gbc);
			this.add(answerTxt);


		}
		
		this.backColor = this.getBackground();
		this.questionLbl.setFocusable(false);
		this.answerTxt.setFont(getDefaultFont());
        this.validate();
	}

	protected boolean initialize() {
		this.alreadyDone = false;
		if (!super.initialize())
			return false;
	
		// add the listeners so that the messages are cleared when the question
		// is answered correctly
			this.addStudentActionListener((StudentActionListener) getController().getStudentInterface().getHintInterface());
			this.addIncorrectActionListener((IncorrectActionListener) getController().getStudentInterface().getHintInterface());
//		addCommWidgetName (commName);
		addCommListener ();
		if (getController().isShowWidgetInfo()) 
			setToolTipWidgetInfo();
		return true;
	}

	public Object getValue(){
		return this.answerTxt.getText().trim();
	}

	/**
	 *	This method returns a message to the production system to create 
	 *	working 
	 */
	public MessageObject getDescriptionMessage() {

		MessageObject mo = MessageObject.create("InterfaceDescription");

		if (!initialize()) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommQuestionTextField");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();

		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null)
			mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);
		return mo;
	}

	/**
	 * This method is called when the state changes in the behavior recorder and
	 * the action type is a correct action
	 */
	public void doCorrectAction(String selection, String action, String input) {
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			this.answerTxt.setForeground(this.correctColor);
			this.answerTxt.setFont(this.correctFont);
			((JCommDocument) answerTxt.getDocument()).locked = false;
			this.answerTxt.setText(input);
			if (getController().getUniversalToolProxy().lockWidget()) {
				((JCommDocument) answerTxt.getDocument()).locked = true;
				this.locked = true;
				this.answerTxt.setFocusable(false);
			}

			if (!this.alreadyDone) {
				if (trace.getDebugCode("inter")) trace.out("inter",
						"firing studentAction in JCommQuestionTextField: "
								+ this.alreadyDone
								+ "- JCommQuestionTextField.java ");
				this.fireStudentAction(new StudentActionEvent(this));
				alreadyDone = true;
			}
			// answerTxt.setHighlighter(null);
			this.answerTxt.setBackground(backgroundNormalColor);
			this.questionLbl.setBackground(this.backColor);
			this.setBackground(this.backColor);
			// removeHighlight("");
		}
	}

	public void doLISPCheckAction (String selection, String input) {
		this.answerTxt.setForeground(this.LISPCheckColor);
		this.answerTxt.setFont(this.correctFont);
		this.answerTxt.setText(input);
		if (getController().getUniversalToolProxy().lockWidget()) {
			((JCommDocument) answerTxt.getDocument()).locked = true;
                        this.locked = true;
                        this.answerTxt.setFocusable(false);
                }
                
		this.alreadyDone = false;
	}

	public void doIncorrectAction (String selection, String input) {
//		trace.out("incorrect called");
		this.answerTxt.setForeground(this.incorrectColor);
		this.answerTxt.setFont(this.incorrectFont);
		this.answerTxt.setText(input);
		((JCommDocument) answerTxt.getDocument()).locked = false;
		locked = false;
		this.alreadyDone = false;
		answerTxt.setHighlighter(defaultHighlighter);
		this.setEnabled(true);
		this.answerTxt.setFocusable(true);
//		this.answerTxt.setForeground(this.startColor);
		this.fireIncorrectAction(new IncorrectActionEvent(this));
		
		// check to see if the student input contains '$' or '%' if so then
		// display message to the students
		int index = input.indexOf('$');
		if(index < 0){
			index = input.indexOf('%');
		}
		if(index >= 0){
			// display the message to the student to not include '$' and '%'
			// in the answers.
            getController().getStudentInterface().getHintInterface().displayBuggyMessage("Do not include '$' or '%' symbols in your answer.");
		}
	}

	public void doInterfaceAction (String selection, String action, String input) {
		
		if (action.equalsIgnoreCase("UpdateQuestionTextField")) {
			this.answerTxt.setText(input);

			if (getController().isStartStateInterface())
				 ((JCommDocument) answerTxt.getDocument()).locked = true;

			return;
		}
		else if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: "+input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
		}
		else if (SET_VISIBLE.equalsIgnoreCase(action)) {   // suppress feedback
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		}

	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void highlight(String subElement, Border highlightBorder) {
	    setBorder(highlightBorder);
	}

	//////////////////////////////////////////////////////
	/**
	 */
	//////////////////////////////////////////////////////
	public void removeHighlight(String subElement) {
		trace.out ("mps", "REMOVE HIGHLIGHT");
	    setBorder(originalBorder);
	}
	
	
	public boolean isChangedFromResetState() {
        if (!answerTxt.getText().equals(resetValue)) {
            ((JCommDocument) answerTxt.getDocument()).locked = true;
			answerTxt.setHighlighter(null);
            setFocusable(false);
            
            return true;
        }
        
        return false;
    }
	
	
	/**
	 * Used to reset the commwidget
	 */
	public void reset (TutorController controller ) {
//		trace.out("reset: JCommQuestionTextField");
		initialize();
		this.setEnabled(true);
		((JCommDocument)this.answerTxt.getDocument()).locked = false;
		this.answerTxt.setText(resetValue);
		this.previousValue = "";
		this.answerTxt.setForeground(this.startColor);
		locked = false;
		this.alreadyDone = false;
		this.answerTxt.setFocusable(true);
//		answerTxt.setHighlighter(defaultHighlighter);

	}

	public void setSize (Dimension d) {
		super.setSize (d);
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
		
		String choiceTemplate = "(deftemplate questionTextField (slot name) (slot value) (slot question))";
		deftemplates.add(choiceTemplate);		
		return deftemplates;
	}
	/**	this method is for creating the instances corresponding to the multiple choice widget
	 * @return
	 */
	public Vector createJessInstances(){
		Vector instances = new Vector();
		String str = "(bind ?" + getCommName() + " (assert (questionTextField (name " + getCommName() + ")(question \"" + this.questionLbl.getText() + "\"))))";

		instances.add(str);
		return instances;
	}

	public static void main(String[] args) {
	}
	
	/**
	 * @return
	 */
	public synchronized int getAnswerLayout() {
		return answerLayout;
	}

	/**
	 * @param answerLayout
	 */
	public synchronized void setAnswerLayout(int answerLayout) {
		this.answerLayout = answerLayout;
        this.createQuestionPanel();
	}

	/**
	 * @return
	 */
	public synchronized int getNCharacters() {
		return nCharacters;
                
	}

	/**
	 * @param characters
	 */
	public synchronized void setNCharacters(int characters) {
		nCharacters = characters;
        this.answerTxt.setColumns(characters);
        this.validate();
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
	    trace.out ("mps", "ACTION PERFORMED");
	    removeHighlight(commName);
		if(((JCommDocument)this.answerTxt.getDocument()).locked){
			return;
		}
		if(!locked){
			if(!this.answerTxt.getText().trim().equals("") && !this.answerTxt.getText().equals(this.previousValue)){
				dirty = true;
				this.previousValue = this.answerTxt.getText();
				sendValue();
			}
		}
	}

	public void focusGained(FocusEvent e) {
	    
		trace.out ("mps", "FOCUS GAINED");
		removeHighlight (commName);
		
		if (!((JCommDocument) answerTxt.getDocument()).locked) {
			if(!this.answerTxt.getText().trim().equals("") && !this.answerTxt.getText().equals(this.previousValue)){
				this.previousValue = this.answerTxt.getText();
				answerTxt.setForeground(startColor);
				answerTxt.setBackground(backgroundNormalColor);
				super.focusGained(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
	 */
	public void focusLost(FocusEvent e) {
		if(!locked){
			if (getController().getUniversalToolProxy().getAutoCapitalize() == true
				|| getAutoCapitalize() == true) {
				answerTxt.setText(answerTxt.getText().toUpperCase());
			}
			if(((JCommDocument)this.answerTxt.getDocument()).locked){
				return;
			}
			if(!this.answerTxt.getText().trim().equals("") && !this.answerTxt.getText().equals(this.previousValue)){
				dirty = true;
				this.previousValue = this.answerTxt.getText();
				sendValue();
				super.focusLost(e);
			}
		}
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent e) {
		
		this.answerTxt.setForeground(this.startColor);
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
	}


	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {
	}
	/**
	 * @return
	 */
	public synchronized boolean isAlgebraWidget() {
		return isAlgebraWidget;
	}

	/**
	 * @param isAlgebraWidget
	 */
	public synchronized void setAlgebraWidget(boolean isAlgebraWidget) {
		this.isAlgebraWidget = isAlgebraWidget;
	}

	/**
	 * @return
	 */
	public synchronized int getRoundTo() {
		return roundTo;
	}

	/**
	 * @param roundTo
	 */
	public synchronized void setRoundTo(int roundTo) {
		this.roundTo = roundTo;
	}
}
