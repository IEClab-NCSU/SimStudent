/*
 * Created on Dec 15, 2003
 *
 */
package pact.CommWidgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.View.JUndo;
import edu.cmu.pact.Utilities.ComponentDescription;
import edu.cmu.pact.Utilities.StringTokenizerItemValues;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.trace;
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
 * @author sanket
 *
 */
public class JCommMultipleChoice extends JCommQuestion implements ActionListener, MouseListener{
	
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
	MultipleChoiceRadioButton[] choices;
	/**
	 * Panel to hold the choices
	 */
	JPanel choicesPanel = new JPanel();
	/**
	 * Group of radio buttons
	 */	
	ButtonGroup choiceGroup = new ButtonGroup();
	/**
	 * Index of the initially selected choice
	 */
	int currentChoice = -1;
    /**
     * List of strings corresponding to each choice.
     */    
    protected ArrayList choiceTexts = new ArrayList();
	
	// zz add to hold the choiceTexts string value 
	String choiceTextsStr;
	
	/**
	 * Order that will be used to diaplay the widgets when used on a dialogWidget.
	 */
    private int dialogOrder = 1;

	/**
	 * Dummy radio button to deselect all the radio buttons from this widget, 
	 * when clicked on the start state
	 */ 
    MultipleChoiceRadioButton dummyButton = new MultipleChoiceRadioButton();
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
	
	static Vector selectedCellsList = new Vector();
	static Vector selectedValues = new Vector();
	static JFrame selectedCellsFrame;
	static JList nameList, valuesList;
	static DefaultListModel nameModel;
	static DefaultListModel valuesModel;
        
    // zz add 
    protected Font questionFont = new Font("", Font.BOLD | Font.BOLD, 13);
    protected Font choiceTextFont;
        
	public JCommMultipleChoice(){
		
		setActionName(UPDATE_MULTIPLE_CHOICE);	
		
		locked = false;
		backgroundNormalColor = (new MultipleChoiceRadioButton()).getBackground();
		choiceTextsStr = "Option0,Option1,Option2,Option3";
		
        for(int i = 0; i < this.nChoices; i++)
            choiceTexts.add(i, "Option" + i); 
		
        this.questionText = "Question?";
                // intial value of nChoices and also
		// add the question text and lay the choices according to the layout policies
		createChoices();
			
        constructSelectedCellsFrame();
        
        addMouseListener(this);
	}
        
	private void sendSelectedCells() {

	   if(getUniversalToolProxy() == null){
		   JOptionPane.showMessageDialog (null, "Warning: The Connection to the Production System should be made before sending the selection elements. \n Open the Behavior Recorder to establish a connection.","Warning", JOptionPane.WARNING_MESSAGE);
	   }else{
		   // construct the Comm Message Containing the cell selections
       	MessageObject mo = MessageObject.create("SendSelectedElements");
    	mo.setVerb("SendSelectedElements");
		mo.setProperty("SelectedElements", selectedCellsList);
		mo.setProperty("SelectedElementsValues", selectedValues);
			
		   getController().getUniversalToolProxy().sendMessage(mo);			
	   }
	}
	   
   private void constructSelectedCellsFrame() {
        selectedCellsFrame = new JFrame();

        nameModel = new DefaultListModel();
        valuesModel = new DefaultListModel();
        nameList = new JList(nameModel);
        valuesList = new JList(valuesModel);

        selectedCellsFrame.getContentPane().setLayout(new BorderLayout());

        JPanel centerPanel = new JPanel();

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BorderLayout());
        namePanel.add(new JLabel("Name"), BorderLayout.NORTH);
        namePanel.add(nameList, BorderLayout.CENTER);
        namePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel valuesPanel = new JPanel();
        valuesPanel.setLayout(new BorderLayout());
        valuesPanel.add(new JLabel("Value"), BorderLayout.NORTH);
        valuesPanel.add(valuesList, BorderLayout.CENTER);
        valuesPanel.setBorder(BorderFactory.createEtchedBorder());

        JButton okBtn = new JButton("Send");
        okBtn.addActionListener(new ActionListener(){
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent arg0) {
        // send the selected cells and values to ESE_Frame
                sendSelectedCells();
                selectedCellsFrame.hide();
        }
        });
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(new ActionListener(){
                /* (non-Javadoc)
                 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                 */
                public void actionPerformed(ActionEvent arg0) {
                        selectedCellsFrame.hide();		
                }

        });
        JPanel btnPanel = new JPanel();
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        centerPanel.setLayout(new GridLayout(1,1));
        centerPanel.add(namePanel);
        centerPanel.add(valuesPanel);

        selectedCellsFrame.getContentPane().add(centerPanel,BorderLayout.CENTER);
        selectedCellsFrame.getContentPane().add(btnPanel,BorderLayout.SOUTH);
    }
   
	/**
	 * This method removes all the existing choices and creates new choices 
	 * depending on the value of nChoices. It is called when ever the value of 
	 * nChoices changes ( by setting the nChoices property value in Netbeans) and
	 * also initially with the default value of nChoices. It also adds the question
	 * text and lays out the choices according to the layout policies
	 *
	 */
	public void createChoices(){
		// remove all the existing panels and the question text
		this.removeAll();
		
		// remove all the choices from the choicesPanel
		choicesPanel.removeAll();

        choiceGroup = null;
        choiceGroup = new ButtonGroup();

        choices = null;
        choices = new MultipleChoiceRadioButton[nChoices];

		// set the layout policy of the choice widget
		// question on top and choices below the question
		this.setLayout(new BorderLayout());
		
		//set the question text
		this.setQuestionText(questionText);
		
		// set the label to be not focussable so that the focus will 
		// go directly to the options
		this.questionLbl.setFocusable(false);
		this.questionLbl.setFont(questionFont);
                
		// add the questionText
		this.add(this.questionLbl);
		
		// set the layout for the choices - vertical or horizontal
		if(this.choiceLayout == HORIZONTAL_LAYOUT)
			choicesPanel.setLayout(new FlowLayout());
		else 
			choicesPanel.setLayout(new GridLayout(0, 1));
		
		// create the radio buttons
		for (int i = 0; i < nChoices; i++) {
			choices[i] = new MultipleChoiceRadioButton();
			choices[i].setFont(choiceTextFont);
			choices[i].addActionListener(this);	
			
	        if(this.currentChoice == i)
	            choices[i].setSelected(true);
			
			// add the radio button to the button group
			choiceGroup.add(choices[i]);
			// add the radio button to the choice panel
			choicesPanel.add(choices[i]);
			choices[i].addMouseListener(new MouseListenerClass());
		}
                
        setChoiceText();
		// add the question text to the widget
		this.add(questionLbl, BorderLayout.NORTH);
		// add the choicesPanel to the widget
		this.add(choicesPanel);
		setBackground(backgroundNormalColor);  // sewall 2010/01/21 fix color reversion on load brd
		
		choicesCreated = true;
        this.choicesPanel.validate();
        this.validate();
	}	

	protected boolean initialize() {
        if (choicesInitialized)
            return true;

        if (!super.initialize())
            return false;

        // add the listeners so that the messages are cleared when the question
        // is answered correctly
        this.addStudentActionListener((StudentActionListener) getController().getStudentInterface().getHintInterface());
        this.addIncorrectActionListener((IncorrectActionListener) getController().getStudentInterface().getHintInterface());

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
	public MessageObject getDescriptionMessage () {
		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");
		if(getController().getUniversalToolProxy() == null){
			return mo;
		}
		
		if (!initialize()) {
			trace.out (5, this, "ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null; 
		}
		
        mo = formDescriptionMessage();
 
		return mo;
	}

	public MessageObject formDescriptionMessage() {
		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");

		mo.setProperty("WidgetType", "JCommMultipleChoice");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));
		mo.setProperty("Question", getQuestionText());
		mo.setProperty("nChoices", new Integer(nChoices));
		mo.setProperty("ChoicesValues", getChoiceTexts());

		Vector deftemplates = createJessDeftemplates();
		Vector instances = createJessInstances();

		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null)
			mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		return mo;
	}
        
	protected void doAction(String input, Color color, Font font){
		this.resetChoiceColors();
		// index of the choice corresponding to the input
		int index = this.choiceTexts.indexOf(input);
		
        // get the choice corresponding to the input
        for(int i = 0; i < this.choices.length; i++){
            if(i == index){
                // change the text color to green and set this as selected
                this.choices[i].setSelected(true);
                MultipleChoiceRadioButton b = this.choices[i];
                b.setForeground(color);
                //b.setFont(font);
            }
        }
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
			doAction(input, this.correctColor, this.correctFont);

			if (getUniversalToolProxy().lockWidget()) {
				locked = true;
				// disable the choices that are not selected
				for (int i = 0; i < this.choices.length; i++) {
					if (!this.choices[i].isSelected()) {
						this.choices[i].setEnabled(false);
					}
				}
			}

			// this ensures that the next question is displayed only when the
			// student
			// has correctly answered the current question
			if (!this.alreadyDone) {
				this.fireStudentAction(new StudentActionEvent(this));
				this.alreadyDone = true;
			}
		}
	}

	public void doLISPCheckAction (String selection, String input) {
		this.doAction(input, this.LISPCheckColor, this.correctFont);

        if (getUniversalToolProxy().lockWidget())
            locked = true;
	}

	public void doIncorrectAction (String selection, String input) {
		this.doAction(input, this.incorrectColor, this.incorrectFont);

		locked = false;
		alreadyDone = false;
        fireIncorrectAction(new IncorrectActionEvent(this));
	}
        
    public void doInterfaceDescription(MessageObject messageObject) {

        ComponentDescription cd = new ComponentDescription(this);
    	cd.executeGraphicalProperties(messageObject);

        String question = 
                (String) messageObject.getProperty( "Question");
                                
        if (question == null)
            return;
        
        setQuestionText(question.trim());
        
        String choicesValues = 
            (String) messageObject.getProperty( "ChoicesValues");
                                        
        
        Object nChoices = messageObject.getProperty("nChoices");
        
        if (choicesValues == null || nChoices == null)
            return;
        
        int nChoicesInt = Integer.parseInt(nChoices.toString());
        if (nChoicesInt <= 0)       // sewall 9/3/08 CTAT2065: nChoices was retrieved as String 
            return;
        
        setChoiceTexts(choicesValues);
        
        setNChoices(nChoicesInt);
        
        createChoices();   
    }

	public void doInterfaceAction (String selection, String action, String input) {
//		this.doAction(input, this.startColor, defaultFont);

		//trace.out (5, this, "doInterfaceAction: selection = " + selection);
		
    	if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
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

    public void setQuestionFont(Font questionFont) {
        this.questionFont = questionFont;
        this.questionLbl.setFont(questionFont);
    }
    
    public Font getQuestionFont () {
        return this.questionFont;
    }
    
    public void setChoiceTextFont(Font choiceTextFont) {
        this.choiceTextFont = choiceTextFont;
        if (choicesCreated) {
            for(int i = 0; i < nChoices; i++)
                choices[i].setFont(choiceTextFont);
        }
        
        return;
    }
    
    public Font getChoiceTextFont () {
        return this.choiceTextFont;
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
	
	
	//////////////////////////////////////////////////////
	/**
	
	*/
	//////////////////////////////////////////////////////
	public void setFocus(String subWidgetName) {
		requestFocus();
		return;
	}

	/**
	 * returns the selected choice and null if no choice is selected
	 * @return
	 */
	public MultipleChoiceRadioButton getSelectedChoice(){
		for(int i = 0; i < this.choices.length; i++){
			if(choices[i].isSelected()){
				return choices[i];
			}
		}
		return null;
	}
	

	/**
	 * returns the selected choice and null if no choice is selected
	 * @return
	 */
	public int getSelectedChoiceIndex(){
		for(int i = 0; i < this.choices.length; i++){
			if(choices[i].isSelected()){
				return i;
			}
		}
		return -1;
	}

	public void resetChoiceColors(){
		for(int i = 0; i < this.choices.length; i++){
			this.choices[i].setForeground(this.startColor);
			this.choices[i].setFont(getDefaultFont());
		}
	}
	
	/**
	 * This method returns the input value for this comm widget
	 */
	public Object getValue() {
		int index = this.getSelectedChoiceIndex();
		if(index != -1){
			return this.choiceTexts.get(index);
		}else{
			return null;
		}
/*		JRadioButton btn = this.getSelectedChoice();
		if(btn != null){
			return btn.getText();
        }else{
            return null;
        }
*/	}
	
	/**
	 * 
	 */
	public boolean isChangedFromResetState () {
		MultipleChoiceRadioButton btn = this.getSelectedChoice();
        if(this.currentChoice != -1 && btn != null){
            if(choices[this.currentChoice].equals(btn)){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
	}
	
	/**
	 * Used to reset the commwidget
	 */
	public void reset (TutorController controller) {
//		trace.out("Reset Called: JCommMultipleChoice");
		this.alreadyDone = false;
		initialize();
		this.setEnabled(true);
		// enable all the radio buttons
		
		for(int i = 0; i < choices.length; i++){
			choices[i].setEnabled(true);
		}
		MultipleChoiceRadioButton btn = this.getSelectedChoice();
		if (btn != null) {
            btn.setSelected(false);
            btn.setForeground(this.startColor);
        }
                
		this.choiceGroup.setSelected(dummyButton.getModel(), true);
		        
		if (this.currentChoice != -1 && this.currentChoice < this.choices.length) {
            this.choices[this.currentChoice].setSelected(true);
        }
        
        locked = false;
	}

	public void setSize (Dimension d) {
		super.setSize (d);
		if (nChoices > 0)
			createChoices();
	}
	
	public void setNChoices(int n){
		if(n <= 0){
			return;
		}
		this.nChoices = n;
		this.createChoices();
	}
	
	public int getNChoices(){
		return this.nChoices;
	}
        
    public String getChoiceTexts(){
		
		trace.out(5, this, "choiceTextsStr = " + choiceTextsStr);
		
		if (choiceTextsStr == null)
			return "";
		
		return choiceTextsStr;
		
		/*
        String str = "";
        
        if (choiceTexts == null) 
            return str;
        
        for (int i = 0; i < choiceTexts.size() - 1; i++) {
            str += this.choiceTexts.get(i) + ",";
        }
        
        str += this.choiceTexts.get(choiceTexts.size() - 1);
        
        return str;*/
    }
    
    private boolean isMatchNChoiceTexts(
                            int choiceNumber, 
                            String choiceTexts) {
                                
        if (choiceTexts == null)
            return false;
        
        int choiceTextsNum = 0;
        
		StringTokenizerItemValues st = new StringTokenizerItemValues(choiceTexts, ',', '/');
        
        while(st.hasMoreTokens()) {
            st.nextToken();
            choiceTextsNum++;
        }
        
        if (choiceNumber == choiceTextsNum)
            return true;
        
        return false;   
    }
    
    /**
     * separate the text for each choice and store it in the list
     * @param texts
     */
    public void setChoiceTexts(String texts){
        if (texts == null)
            return;
		
		choiceTextsStr = texts;

		StringTokenizerItemValues st = new StringTokenizerItemValues(texts, ',', '/');
        choiceTexts.clear();
        while(st.hasMoreTokens()){
            String str = st.nextToken();
            choiceTexts.add(str);
        }
        setChoiceText();
    }
    
    /**
     * set the text in the choices of the radio buttons
     *
     */
    public void setChoiceText(){
        Iterator it = choiceTexts.iterator();
        for(int i = 0; i < choices.length && it.hasNext(); i++){
            choices[i].setText(Utils.replaceImg((String)it.next()));
            //choices[i].setFont(this.correctFont);
        }
        choicesPanel.validate();
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
		
		String choiceTemplate = "(deftemplate multipleChoice  (slot name) (multislot choices) (slot value) (slot question))";
		deftemplates.add(choiceTemplate);		
		return deftemplates;
	}
	/**	this method is for creating the instances corresponding to the multiple choice widget
	 * @return
	 */
	public Vector createJessInstances(){
		Vector instances = new Vector();
		String choicesStr = "";
		for (int j = 0; j < getNChoices(); j++)
			choicesStr += "\"" + choices[j].getText() + "\" ";
		
        String value = null;
        MultipleChoiceRadioButton btn = this.getSelectedChoice();
        int index = this.getSelectedChoiceIndex();
/*        if(btn != null){
            value = btn.getText();
        }
*/
        if (index != -1)
            value = (String)this.choiceTexts.get(index);
	trace.out("index=" + index + ", value=" + value);

        String str = "(bind ?" + getCommName() + " (assert (multipleChoice (name " + getCommName() + ") (choices " +  choicesStr + ") (question \"" + this.questionText + "\") (value " + (value == null ? "nil" : "\"" + value + "\"") + "))))";

		instances.add(str);
                
		return instances;
	}
		// sanket@cs.wpi.edu
	
	/**
	 * Method for unit testing
	 * @param args
	 */

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		JCommMultipleChoice multipleChoice = new JCommMultipleChoice();
		frame.getContentPane().add(multipleChoice);
		frame.pack();
		frame.show();
	}

	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
        trace.err ("action performed");
	    removeHighlight (commName);
		// send the selection action input to the behavior recorder and/or production system
		if(!locked){
			if(((MultipleChoiceRadioButton)ae.getSource()).isSelected()){
				dirty = true;
				sendValue();
			}
		}
	}
        
	//////////////////////////////////////////////////////
	/**
	 *  
	 */
	//////////////////////////////////////////////////////
	public void setBackground(Color c) {
		setBackgroundTemporary(c);
		backgroundNormalColor = c;
	}

	public void setBackgroundTemporary(Color c) {
		super.setBackground(c);
		if (choicesPanel != null)
			choicesPanel.setBackground(c);
		if (choices == null)
			return;
		for (int i = 0; i < nChoices; ++i)
			choices[i].setBackground(c);
	}

	public void setForeground(Color c) {
		super.setForeground(c);
		if (choicesPanel != null)
			choicesPanel.setForeground(c);
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
        if(this.choiceLayout == HORIZONTAL_LAYOUT)
            this.choicesPanel.setLayout(new FlowLayout());
        else
            this.choicesPanel.setLayout(new GridLayout(0,1));
		
        // add all the choices in the panel
        for (int i = 0; i < choices.length; i++)
            this.choicesPanel.add(choices[i]);
 
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
	
	public void hideAllComponents(boolean b){
		Component[] components = this.getComponents();
		for(int i = 0; i < components.length; i++){
			components[i].setVisible(!b);
		}
	}
	
	public void setEnabled(boolean b){
		this.questionLbl.setEnabled(b);
		for(int i = 0; i < this.nChoices; i++){
			this.choices[i].setEnabled(b);
		}
	}
	
	public StartUpdateDialog createStartUpdateDialog() {
		return new StartUpdateDialog();
	}
	
    public void mouseClicked(MouseEvent e) {
    	if(trace.getDebugCode("dw"))
    		trace.out("dw", "JCMultipleChoice.mouseClicked("+e.paramString()+
    				"): ctlr.isDefiningStartState() "+getController().isDefiningStartState());
		if (getController().isDefiningStartState()
            && SwingUtilities.isRightMouseButton(e)) {
			createStartUpdateDialog();
        }
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    public void mousePressed(MouseEvent e) {
    }
        
    public void mouseReleased(MouseEvent e) {
    }
        
	protected class MultipleChoiceRadioButton extends JRadioButton {
	
		//String previousValue = "";
		boolean selected = false;
	
	}
        
    class StartUpdateDialog extends JDialog implements ActionListener {
            private JPanel titlePanel;
            private JLabel optionJLabel;
            private JPanel optionValuesJPanel;
            private Container contentPane = getContentPane();

            private JLabel questiobJLabel = new JLabel("Question:");
            private JLabel nChoiceJLabel = new JLabel("nChoice:");
            private JLabel choiceTextsJLabel = new JLabel("ChoiceTexts:");
            
            // private JLabel questionFontJLabel = new JLabel("QuestionFont:");
            // private JLabel choiceTextFontJLabel = new JLabel("ChoiceTextFont:");

            private JTextField questionJTextField = new JTextField();
            { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(questionJTextField); }
            private JTextField nChoiceJTextField = new JTextField();
            { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(nChoiceJTextField); }
            private JTextField choiceTextsJTextField = new JTextField();
            { JUndo.JTextUndoPacket jtup = JUndo.makeTextUndoable(choiceTextsJTextField); }
            
            private JPanel resetCancelPanel;
            private JButton resetJButton = new JButton("Set");
            private JButton cancelJButton = new JButton("Cancel");

            private final int DIALOG_LCATION_X = 300;
            private final int DIALOG_LCATION_Y = 200;
            
            private final int LABEL_WIDTH = 80;    
            private final int FIELD_WIDTH = 250;
            
            private final int ROW_HEIGHT = 20;
            
            private final int ROW_START_Y = 20;
            private final int ROW_START_X = 10;
            
            private final int ROW_SPACE = 10;
            
            private final int HEAD_FOOT_HEIGHT = 130;
            
            private final int LEFT_RIGHT_SPACE = 10;
            
            int rowNumber = 0;
            
            public JButton getSetButton() {
                return resetJButton;
            }
            
            /** Creates a new instance of StartUpdateDialog */
            public StartUpdateDialog() {
                super(getController().getActiveWindow(), true);
                setTitle("Set Widget Start Values");

                contentPane.setLayout(new BorderLayout());
                
                // titlePanel
                titlePanel = new JPanel();
                titlePanel.setLayout(new FlowLayout());
                
                String labelText = "Please set your values for widget ";
                labelText += commName + ":";
                optionJLabel = new JLabel(labelText);
                
                titlePanel.add(optionJLabel);
                contentPane.add(titlePanel, BorderLayout.NORTH);
                
                // optionValuesJPanel
                optionValuesJPanel = new JPanel();
                optionValuesJPanel.setLayout(null);                
            
                // 1st row
                rowNumber = 1;
                questiobJLabel.setSize(LABEL_WIDTH, ROW_HEIGHT);
                questiobJLabel.setLocation(ROW_START_X, 
                                            ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber - 1));
                optionValuesJPanel.add(questiobJLabel);
                
                questionJTextField.setSize(FIELD_WIDTH, ROW_HEIGHT);
                questionJTextField.setLocation(ROW_START_X + LABEL_WIDTH, 
                                                ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber - 1));
                questionJTextField.setText(questionText);
                optionValuesJPanel.add(questionJTextField);
                
                // 2nd row
                rowNumber = 2;
                nChoiceJLabel.setSize(LABEL_WIDTH, ROW_HEIGHT);
                nChoiceJLabel.setLocation(ROW_START_X, 
                                            ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber - 1));
                
                optionValuesJPanel.add(nChoiceJLabel);
                
                nChoiceJTextField.setSize(FIELD_WIDTH, ROW_HEIGHT);
                nChoiceJTextField.setLocation(ROW_START_X + LABEL_WIDTH, 
                                                ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber - 1));
                
                nChoiceJTextField.setText(nChoices + "");
                optionValuesJPanel.add(nChoiceJTextField);

                // 3rd row
                rowNumber = 3;
                choiceTextsJLabel.setSize(LABEL_WIDTH, ROW_HEIGHT);
                choiceTextsJLabel.setLocation(ROW_START_X, 
                                                ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber - 1));
                
                optionValuesJPanel.add(choiceTextsJLabel);
                
                choiceTextsJTextField.setText(getChoiceTexts());
                choiceTextsJTextField.setSize(FIELD_WIDTH, ROW_HEIGHT);
                choiceTextsJTextField.setLocation(ROW_START_X + LABEL_WIDTH,
                                                    ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber - 1));
                
                optionValuesJPanel.add(choiceTextsJTextField);

                contentPane.add(optionValuesJPanel, BorderLayout.CENTER);

                // resetCancelPanel
                resetCancelPanel = new JPanel();

                resetCancelPanel.setLayout(new FlowLayout());

                resetJButton.addActionListener(this);
                cancelJButton.addActionListener(this);
                
                resetCancelPanel.add(resetJButton);
                resetCancelPanel.add(cancelJButton);

                contentPane.add(resetCancelPanel, BorderLayout.SOUTH);
                
                // dialog close
                addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        dispose();
                    }
                });

                // set dialog size & location
                setSize(ROW_START_X * 2 + LABEL_WIDTH + FIELD_WIDTH + LEFT_RIGHT_SPACE, 
                        ROW_START_Y + (ROW_HEIGHT + ROW_SPACE) * (rowNumber  - 1) + HEAD_FOOT_HEIGHT);

                setLocation(DIALOG_LCATION_X, DIALOG_LCATION_Y);
                
                // display the dialog
                setVisible(true);
            }

            public void actionPerformed(ActionEvent ae) {
                
                    if (ae.getSource() == resetJButton) {
                            String choiceNumberString = nChoiceJTextField.getText().trim();                          
                            int choiceNumber = Integer.parseInt(choiceNumberString);
                            String choiceTexts = choiceTextsJTextField.getText().trim();
                            
                            if (!isMatchNChoiceTexts(choiceNumber, choiceTexts)) {
                                JOptionPane.showMessageDialog(
                                                null,
                                                "OptionNumber and OptionValues don't match.",
                                                "Warning",
                                                JOptionPane.INFORMATION_MESSAGE);

                                return;
                            }
   
                            setQuestionText(questionJTextField.getText().trim());
                            setNChoices(choiceNumber);
                            setChoiceTexts(choiceTexts);
                            
                            MessageObject mo = getDescriptionMessage();
                            getUniversalToolProxy().sendMessage(mo);

                    }
                    
                    dispose();
            }
        }

	
	private class MouseListenerClass implements MouseListener{
		
			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent me) {
		    	if(trace.getDebugCode("dw"))
		    		trace.out("dw", "JCMultipleChoice.MouseLisCls.mouseClicked("+me.paramString()+
		    				"): ctlr.isDefiningStartState() "+getController().isDefiningStartState());
				if (getController().isDefiningStartState()
			            && SwingUtilities.isRightMouseButton(me)) {
						createStartUpdateDialog();
			        }

				if(me.isControlDown()){
					//JCommMultipleChoice dt = (JCommMultipleChoice)me.getComponent().getParent();
					
					MultipleChoiceRadioButton tc = (MultipleChoiceRadioButton)me.getComponent();
			
					//tc.previousValue = tc.getText();
					//previousFont = tc.getFont();
					//previousColor = tc.getForeground();
					//textField[tc.row][tc.column].setForeground(startColor);
					//if (startFont != null)
						//textField[tc.row][tc.column].setFont(startFont);
					//currentRow = tc.row + 1;
					//currentColumn = tc.column + 1;
					
					repaint();
					
					if(tc.selected){
						tc.setBackground(Color.WHITE);
						tc.selected = false;
						selectedCellsList.remove(getCommNameToSend());
						selectedValues.remove(getValue());
						nameModel.removeElement(getCommNameToSend());
						valuesModel.removeElement(getValue());
					}else{
						tc.setBackground(Color.PINK);
						tc.selected = true;
						addToSelectedList(getCommNameToSend());
						addToSelectedValues(getValue());
					}
					selectedCellsFrame.validate();
					selectedCellsFrame.pack();
					selectedCellsFrame.show();
				}

				if(me.getClickCount() == 2){
//					JCommTable dt = (JCommTable)me.getComponent().getParent();
//					clearSelectedList();
//					clearSelectedValues();
//					for(int i = 0; i < dt.rows; i++){
//						for(int j = 0; j < dt.columns; j++){
//							if(dt.textField[i][j].selected){
//								dt.currentColumn = j + 1;
//								dt.currentRow = i + 1;
//								addToSelectedList(getCommNameToSend());
//								addToSelectedValues(getValue());
//							}
//						}
//					}
//					sendSelectedCells();
				}
			}

			/**
			 * 
			 */
			private void clearSelectedValues() {
				selectedValues.removeAll(selectedValues);			
			}

			/**
			 * @param object
			 */
			private void addToSelectedValues(Object o) {
				selectedValues.add(o);			
				valuesModel.addElement(o);
			}

			/**
			 * 
			 */
			private void clearSelectedList() {
				selectedCellsList.removeAll(selectedCellsList);
			}

			/**
			 * @param string
			 */
			private void addToSelectedList(String cellName) {
				selectedCellsList.add(cellName);
				nameModel.addElement(cellName);
				
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
			 */
			public void mouseEntered(MouseEvent arg0) {
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
			 */
			public void mouseExited(MouseEvent arg0) {
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
			 */
			public void mousePressed(MouseEvent arg0) {
			}

			/* (non-Javadoc)
			 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
			 */
			public void mouseReleased(MouseEvent arg0) {
			}
		}
}

