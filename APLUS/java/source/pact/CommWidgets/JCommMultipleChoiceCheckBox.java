/*
 * Created on Apr 11, 2004
 */
package pact.CommWidgets;

/**
 * @author supaleka
 * @see pact.CommWidgets
 * @version Apr 11, 2004
 *
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.ctat.TutorController;
/**
 * This widget is a multiple choice widget used to select one choice out of
 * multiple options. This widget has a text property for the question and parameters
 * to set the number of the choices for the question and also to control the layout
 * of the choices either vertical or horizontal.
 * selection: MultipleChoice0
 * action: UpdateMultipleChoice
 * input: text of the choice selected
 * 
 * Properties specific to this widget that user can set:
 * 		a. nChoices: Number of answer choices
 * 		b. questionText: The question
 * 		c. choiceLayout: How the choices should be displayed ie All horizontal in
 * 			one line or all one below the other on different lines.
 * 		d. choiceTexts: The text associated with each choice. Comma separated values.
 * 
 *
 */
public class JCommMultipleChoiceCheckBox extends JCommQuestion implements ActionListener, ItemListener {
	
	private static final String UPDATE_INVISIBLE = "UpdateInVisible";

	boolean choicesCreated, choicesInitialized;
	/**
	 * specify's horizontal layout for the choices
	 */
	private static int HORIZONTAL_LAYOUT = 1;
	/**
	 * specify's vertical layout for the choices
	 */
	private static int VERTICAL_LAYOUT = 2;
	/**
	 * Number of choices in the widget.
	 */
	private int nChoices = 4;
	/**
	 * layout for the choices either HORIZONTAL_LAYOUT or VERTICAL_LAYOUT 
	 */
	private int choiceLayout = 2;
	/**
	 * Array of choices for this question
	 */
	JCheckBox[] choices;
	/**
	 * Panel to hold the choices
	 */
	JPanel choicesPanel = new JPanel();
	/**
	 * Panel to hold the submit Button
	 * 
	 */
	JPanel submitBtnPanel = new JPanel();

	/**
	 * Submit button
	 */
	JButton submitButton;
	/** 
	 * stores the previous selected value to implement the locking behavior 
	 * */
	String previousValue = "";
	/**
	 * Group of radio buttons
	 */
	//ButtonGroup choiceGroup = new ButtonGroup();
	/**
	 * Index of the initially selected choice
	 */
	int currentChoice = -1;
	/**
	 * List of strings corresponding to each choice.
	 */
	ArrayList choiceTexts = new ArrayList();
	/**
	 * Order that will be used to diaplay the widgets when used on a dialogWidget.
	 */
	private int dialogOrder = 1;
	//	  /** 
	//	   * Holds value of property toolTipText. 
	//	   */
	//	  private String toolTipText;
	/**
	 * Dummy radio button to deselect all the radio buttons from this widget, 
	 * when clicked on the start state
	 */
	JCheckBox dummyButton;
	/**
	 * Counts the number of instances created for this widget - used for 
	 * naming the comm widgets with a distinct name
	 */
	private static int counter = 0;
	/**
	 * Boolean variable that is used to make sure that only one widget is displayed for a correct action
	 */
	private boolean alreadyDone = false;

	/**
	 * Contructor called when an instance of this widget is created.
	 * Create the Visual interface for the widget and set the comm name and the 
	 * action name for this widget.
	 *
	 */
	public JCommMultipleChoiceCheckBox() {

		setActionName(UPDATE_MULTIPLE_CHOICE_CHECK_BOX); 

		locked = false;

		for (int i = 0; i < this.nChoices; i++) {
			choiceTexts.add(i, "Option" + i);
		}

		this.questionText = "Question?";
		// intial value of nChoices and also
		// add the question text and lay the choices according to the layout policies
		createChoices();
		// add the listeners so that the messages are cleared when the question
		// is answered correctly
		//		if(controller.getUniversalToolProxy() != null && controller.getUniversalToolProxy().useJess){
		//		}
	}

	/**
	 * This method removes all the existing choices and creates new choices 
	 * depending on the value of nChoices. It is called when ever the value of 
	 * nChoices changes ( by setting the nChoices property value in Netbeans) and
	 * also initially with the default value of nChoices. It also adds the question
	 * text and lays out the choices according to the layout policies
	 *
	 */
	public void createChoices() {
		// remove all the existing panels and the question text
		this.removeAll();

		// remove all the choices from the choicesPanel
		choicesPanel.removeAll();

		//choiceGroup = null;
		//choiceGroup = new ButtonGroup();
		//        
		choices = null;
		choices = new JCheckBox[nChoices];

		// set the layout policy of the choice widget
		// question on top and choices below the question
		this.setLayout(new BorderLayout());

		//set the question text
		this.questionLbl.setText(questionText);

		// set the label to be not focussable so that the focus will 
		// go directly to the options
		this.questionLbl.setFocusable(false);

		// add the questionText
		this.add(this.questionLbl);

		// set the layout for the choices - vertical or horizontal
		if (this.choiceLayout == HORIZONTAL_LAYOUT) {
			choicesPanel.setLayout(new FlowLayout());
		} else {
			choicesPanel.setLayout(new GridLayout(0, 1));
		}

		// create the CheckBoxes
		for (int i = 0; i < nChoices; i++) {
			choices[i] = new JCheckBox();
			if (startFont != null) choices[i].setFont(startFont);
			//choices[i].addActionListener(this);
			choices[i].addItemListener(this);

			if (this.currentChoice == i) {
				choices[i].setSelected(true);
			}
			
			// add the CheckBox to the choice panel
			choicesPanel.add(choices[i]);
		}

		setChoiceText();
		// add the question text to the widget
		this.add(questionLbl, BorderLayout.NORTH);
		// add the choicesPanel to the widget
		this.add(choicesPanel);

		// add the submitButtonPanel ot the widget

		// add the feedback widget
		//		if(UniversalToolProxy.isShowCommentButton()){
		//			this.add(this.feedBackWidget, BorderLayout.EAST);
		//		}else{
		//			this.remove(this.feedBackWidget);
		//		}
		
		//add the submitBtnPanel to the widget
		this.add(submitBtnPanel, BorderLayout.SOUTH);

		// Add the submit button
		if(this.submitButton == null){
			submitButton = new JButton("Submit");
			submitButton.addActionListener(this);
			submitButton.setEnabled(false);
			//add Submit Button to the Submit Button Panel
			submitBtnPanel.add(submitButton);
		}else{
			submitBtnPanel.add(submitButton);
		}
		//this.add(this.submitButton, BorderLayout.SOUTH);

		choicesCreated = true;
		this.choicesPanel.validate();
		this.validate();
	}

	protected boolean initialize() {

	    if (!super.initialize())
		return false;

	    if (choicesInitialized)
		return true;

	    StudentInterfaceWrapper studentInterface = getController().getStudentInterface();
	    if (studentInterface != null) {
		this.addStudentActionListener((StudentActionListener) studentInterface.getHintInterface());
		this.addIncorrectActionListener((IncorrectActionListener) studentInterface.getHintInterface());
	    }

	    //		  addCommWidgetName (commName);
	    addCommListener();
	    if (getController().isShowWidgetInfo())
		setToolTipWidgetInfo();

	    choicesInitialized = true;
	    return true;
	}

	/**
	 *	This method returns a message to the production system to create 
	 *	working memory
	 */
	public MessageObject getDescriptionMessage() {

		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");

		if (!initialize()) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		mo.setProperty("WidgetType", "JCommMultipleChoiceCheckBox");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		mo.setProperty("nChoices", new Integer(nChoices));

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
	 
	 **/

	private void setChoiceColor(String input, Color color, Font font){
		this.resetChoiceColors();
		// Assuming that the "input" string is the same one which we
		// create in the ActionPerformed	
		String[] selectedChoices = input.split(",");
		// Iterate through the list to search for the selected check boxes
		for (int i = 0; i < selectedChoices.length; i++) {
			for (int j = 0; j < choices.length; j++) {
				if (selectedChoices[i].equals(choices[j].getText())) {
						
					choices[j].setSelected(true);
					choices[j].setForeground(color);
					if (font != null) choices[j].setFont(font);
				}
			}
		}
	}

	public void doCorrectAction(String selection, String action, String input) {
		if (SET_VISIBLE.equalsIgnoreCase(action)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		} else {
			setChoiceColor(input, correctColor, correctFont);

			if (getUniversalToolProxy().lockWidget()) {
				locked = true;
				// set disabled the unset check boxes
				for (int i = 0; i < choices.length; i++) {
					if (choices[i].isSelected() == false)
						choices[i].setEnabled(false);
				}
			}

			// this ensures that the next question is displayed only when the
			// student
			// has correctly answered the current question
			if (!alreadyDone) {
				fireStudentAction(new StudentActionEvent(this));
				alreadyDone = true;
			}
		}
	}

	public void doLISPCheckAction(String selection, String input) {

		setChoiceColor(input, LISPCheckColor, correctFont);

		if (getUniversalToolProxy().lockWidget()) {
			locked = true;

			// set disabled the unset check boxes
			for (int i = 0; i < choices.length; i++) {
				if (choices[i].isSelected() == false)
					choices[i].setEnabled(false);
			}
		}
	}

	public void doIncorrectAction(String selection, String input) {
		resetChoices();
		setChoiceColor(input, incorrectColor, incorrectFont);
		locked = false;
		alreadyDone = false;
		fireIncorrectAction(new IncorrectActionEvent(this));
	}

	public void doInterfaceAction(String selection, String action, String input) {

		if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
			if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: " + input);
			if (input.equalsIgnoreCase("true"))
				setInvisible(true);
			else
				setInvisible(false);
			setVisible(!isInvisible());
			// setInvisible(input);
		} else if (SET_VISIBLE.equalsIgnoreCase(action)) { // suppress feedback
			if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
			setVisible(input);
		}
	}

	/**
	 * returns the selected choice and null if no choice is selected
	 * @return
	 */
	public JCheckBox getSelectedChoice() {
		for (int i = 0; i < choices.length; i++) {
			if (choices[i].isSelected()) {
				return choices[i];
			}
		}
		return null;
	}

	public void resetChoiceColors() {
		for (int i = 0; i < choices.length; i++) {
			choices[i].setForeground(startColor);
			if (startFont != null) choices[i].setFont(startFont);
		}
	}

	public void resetChoices() {
		for (int i = 0; i < choices.length; i++) {
			choices[i].setSelected(false);
		}
	}
	/**
	 * This method returns the input value for this comm widget
	 */
	public Object getValue() {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < choices.length; i++) {
			if (choices[i].isSelected() == true) {
				String choice = choices[i].getText();
				sb.append(choice);
				sb.append(",");
			}
		}

		return (sb.toString());
		//JCheckBox btn = this.getSelectedChoice();
		//if(btn != null){
		//	return btn.getText();
		//}else{
		//	return null;
		//}
	}

	/**
	 * Change the background color for the panels, checkboxes.
	 * @param c new background color
	 */
	public void setBackground(Color c) {
		super.setBackground(c);
		if (choicesPanel != null)
			choicesPanel.setBackground(c);
		if (submitBtnPanel != null)
			submitBtnPanel.setBackground(c);
		if (choices == null)
			return;
		for (int i = 0; i < nChoices; ++i)
			choices[i].setBackground(c);
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
		setBorder(originalBorder);
	}
        
	/**
	 * 
	 */
	public boolean isChangedFromResetState() {
		JCheckBox btn = getSelectedChoice();
		return currentChoice != -1 && btn != null && !(choices[currentChoice].equals(btn));
	}

	/**
	 * Used to reset the commwidget
	 */
	public void reset(TutorController controller) {
		//		trace.out("Reset Called: JCommMultipleChoice");
		alreadyDone = false;
		initialize();
		resetChoiceColors();
		previousValue = "";
		setEnabled(true);
		// enable all the radio buttons

		for (int i = 0; i < choices.length; i++) {
			choices[i].setEnabled(true);
			choices[i].setSelected(false);
		}
		JCheckBox btn = getSelectedChoice();
		if (btn != null) {
			btn.setSelected(false);
			btn.setForeground(startColor);
		}
		//this.choiceGroup.setSelected(dummyButton.getModel(), true);

		if (currentChoice != -1
			&& currentChoice < choices.length) {
			choices[currentChoice].setSelected(true);
		}
		locked = false;
	}

	public void setSize(Dimension d) {
		super.setSize(d);
		if (nChoices > 0)
			createChoices();
	}

	public void setNChoices(int n) {
		if (n <= 0) {
			return;
		}
		this.nChoices = n;
		this.createChoices();
	}

	public int getNChoices() {
		return nChoices;
	}

	public String getChoiceTexts() {
		String str = "";
		for (int i = 0; i < choiceTexts.size(); i++) {
			str += choiceTexts.get(i) + ",";
		}
		return str;
	}

	public void setChoiceTexts(String texts) {
		StringTokenizer st = new StringTokenizer(texts, ",");
		choiceTexts.clear();
		while (st.hasMoreTokens()) {
			String str = st.nextToken();
			choiceTexts.add(str);
		}
		setChoiceText();
	}

	public void setChoiceText() {
		Iterator it = choiceTexts.iterator();
		for (int i = 0; i < choices.length && it.hasNext(); i++) {
			choices[i].setText((String) it.next());
			choices[i].setFont(correctFont);
		}
		choicesPanel.validate();
	}

	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFont(Font f) {
		if (f != null) {
			startFont = f;
		} else
			startFont = super.getFont();

		super.setFont(startFont);
		for (int i = 0; i < nChoices; i++)
			choices[i].setFont(startFont);
	}

	/**
	 * Returns the comm name of the currently selected choice
	 */
	public String getCommNameToSend() {
		String s = commName;
		return s;
	}

	/**
	 * This method creates the jess deftemplates for the multiple choice widget
	 * @return - returns a vector of deftemplates for the multiple choice widget
	 */
	public Vector createJessDeftemplates() {
		Vector deftemplates = new Vector();

		String choiceTemplate =
			"(deftemplate multipleChoice  (slot name) (multislot choices) (slot value) (slot question))";
		deftemplates.add(choiceTemplate);
		return deftemplates;
	}
	/**	this method is for creating the instances corresponding to the multiple choice widget
	 * @return
	 */
	public Vector createJessInstances() {
		Vector instances = new Vector();
		String choicesStr = "";
		for (int j = 0; j < getNChoices(); j++) {
			choicesStr += "\"" + choices[j].getText() + "\" ";
		}
		String value = "nil";
		JCheckBox btn = getSelectedChoice();
		if (btn != null) {
			value = btn.getText();
		}
		String str =
			"(bind ?" + getCommName()
			   + " (assert (multipleChoice (name " + getCommName() + ")"
									   + " (choices " + choicesStr	+ ")"
									   + " (question \"" + questionText + "\")"
									   + " (value \"" + value + "\"))))";

		instances.add(str);
		return instances;
	}

	/**
	 * Method for unit testing
	 * @param args
	 */

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JCommMultipleChoiceCheckBox multipleChoice = new JCommMultipleChoiceCheckBox();
		frame.getContentPane().add(multipleChoice);
		frame.pack();
		frame.show();
	}

	/** Listens to the check boxes. */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();

		// Any state change for a check box to SELECTED
		// should enable the submit button
		//(e.getStateChange()== ItemEvent.SELECTED)
		//submitButton.setEnabled(true);
		submitButton.setEnabled(false);

		for (int i = 0; i < choices.length; i++) {
			if (choices[i].isSelected() == true) {
				submitButton.setEnabled(true);
				break;
			}
		}

	}

	/* (non-Javadoc)
	 * @see java.awt.event.getrListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {

		Object source = ae.getSource();

		// See if the submit button was pressed
		if (source == submitButton) {

			// Get the selected values
			String selectedValue = getValue().toString();

			// if the widget is not locked then get the selected value of the combobox
			if (!locked && initialized) {

				//	if the current selection is different 
				// than the previous selection then
				if (!previousValue.equals(selectedValue)) {
					this.previousValue = getValue().toString();
					dirty = true;
					sendValue();
					return;
				}
			} else if (locked) {
				// TODO: Set the right check boxes				
			}

		}

		// send the selection action input to the behavior recorder and/or production system
		//if(locked){
		//	if(((JCheckBox)ae.getSource()).isSelected()){
		//		dirty = true;
		//		sendValue();
		//	}
		//}
	}

	/** Getter for property toolTipText.
	 * @return Value of property toolTipText.
	 *
	 */
	public String getToolTipText() {
		return this.toolTipText;
	}

	/** Setter for property toolTipText.
	 * @param toolTipText New value of property toolTipText.
	 *
	 */
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}

	/**
	 * @return
	 */
	public synchronized int getCurrentChoice() {
		return currentChoice;
	}

	/**
	 * @param currentChoice
	 */
	public synchronized void setCurrentChoice(int currentChoice) {
		this.currentChoice = currentChoice;
	}

	/**
	 * @return
	 */
	public synchronized int getChoiceLayout() {
		return choiceLayout;
	}

	/**
	 * @param layout
	 */
	public synchronized void setChoiceLayout(int layout) {
		this.choiceLayout = layout;
		// repaint the components
		this.choicesPanel.removeAll();
		// set the layout of the components
		if (this.choiceLayout == HORIZONTAL_LAYOUT) {
			this.choicesPanel.setLayout(new FlowLayout());
		} else {
			this.choicesPanel.setLayout(new GridLayout(0, 1));
		}
		// add all the choices in the panel
		for (int i = 0; i < choices.length; i++) {
			this.choicesPanel.add(choices[i]);
		}
		this.choicesPanel.validate();
	}
	/**
	 * @return
	 */
	public synchronized int getDialogOrder() {
		return dialogOrder;
	}

	/**
	 * @param dialogOrder
	 */
	public synchronized void setDialogOrder(int dialogOrder) {
		this.dialogOrder = dialogOrder;
	}

	public void hideAllComponents(boolean b) {
		Component[] components = this.getComponents();
		for (int i = 0; i < components.length; i++) {
			components[i].setVisible(!b);
		}
	}
	
	public void setEnabled(boolean b){
		this.questionLbl.setEnabled(b);
		for(int i = 0; i < this.nChoices; i++){
			this.choices[i].setEnabled(b);
		}
	}
}
