/*
 * Created on Mar 15, 2004
 *
 */
package pact.CommWidgets;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.text.Highlighter;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

	/**
	 * This class represents a question that has a combobox from which the student can
	 * select the answer
	 * Properties specific to this widget that user can set:
	 * 		a. nComponents: indicated number of label & combobox pairs in this 
	 * 			widget
	 * 		b. answerLayout: Where should the answer combobox be placed.
	 * 			1 - On the same line after the question 
	 * 			2 - On the line below the question
	 * 		c. questionText: comma separated strings for the question label
	 * 		d. 
	 * @author sanket
	 *
	 */


	public class JCommQuestionComboBox extends JCommQuestion implements 
					ActionListener, ItemListener{
		/**
		 * List of panel to hold the label and the combobox pair
		 */
		private ArrayList componentList = new ArrayList();
		/**
		 * Comma separated list of strings for a combo box enclosed between 
		 * [] 
		 */
		private String choicesText = "[choice1, choice2]";
		/**
		 * number of label and combobox pairs in this widget
		 */
		private int nComponents = 1;
		/**
		 * determines how the label combobox pairs are displayed 
		 * 1 - on the same horizontal line
		 * 2 - Veretically one below other
		 */
		private int questionLayout = 1;
		/**
		 * Keeps track of the number of instances of this widget
		 */
		private static int counter = 0;
			
		JButton submitBtn;

		private boolean alreadyDone = false;
				
		private boolean sameSize = false;
		/** stores the previous selected value to implement the locking behavior */
		String previousValue  = "";
		
		protected Highlighter defaultHighlighter;
		protected String resetValue = "", values = "";
		protected Vector comboBoxStrings, comboBoxStringColors;
		protected int sizeOfComboBoxStrings;
		protected JPanel container;
		protected boolean actionFromBR;
		protected boolean locked;
		protected boolean setValuesDone = true;

		private JPanel buttonPanel = new JPanel();
		
		private JLabel endLabel = new JLabel();
		/** for each action entire interface is updated. This causes problems in comboboxes. Hence
		 * this variable prevents updating the combobox once it has been set to correct.
		 */
		
		public JCommQuestionComboBox(){
			setActionName(UPDATE_QUESTION_COMBO_BOX);
			locked = false;		
			this.questionText += ", question part-2";
                        
                        actionFromBR = false;
                        
			createQuestionPanel();
			// add the listeners so that the messages are cleared when the question
			// is answered correctly
		}
	
		/**
		 * 
		 */
		private void createQuestionPanel() {
//			System.out.println("removing question components");
			this.removeAll();

			// set the layout of the panel
			// if pair layout is set to horizontal then use flow layout
//			System.out.println("setting the layout");
			
			if(this.questionLayout == 1){
				this.setLayout(new FlowLayout());
			}else{
				this.setLayout(new GridLayout(0,1));
			}
			
			ComboboxComponent component;
			
			for(int i = 0; i < this.nComponents; i++){
				if(this.componentList.size() <= i || this.componentList.get(i) == null){
					// create a component
					component = new ComboboxComponent();
					component.addActionListener(this);
//					component.addItemListener(this);
					this.componentList.add(component);
				}else {
					component = (ComboboxComponent)this.componentList.get(i);
				}
				// add the component to the main panel
				this.add(component);
//				System.out.println("adding a new question component");
			}
		
			// add the extra label after the end of the combobox
			endLabel.setFocusable(false);
			this.add(endLabel);
			
			// if more than one combobox label pair then add the submit
			// button
			if(this.nComponents > 1){
				if(this.submitBtn == null){
//					System.out.println("adding the submit button");
					submitBtn = new JButton("Submit");
					submitBtn.addActionListener(this);
				}
				buttonPanel.add(submitBtn);
			}else{
				if(this.submitBtn != null){
					buttonPanel.remove(submitBtn);
				}
			}
			
			// set the question texts
			separateQuestionList();
			
			// set the combobox texts
			separateTextList();
			
			// remove the remaining elements from the list.
			for(int i = nComponents; i < this.componentList.size(); i++){
				this.componentList.remove(i);
			}

			
			this.add(buttonPanel);
			
			// caluculate the size of the panel
			this.validate();
			this.repaint();
		}

		/**
		 * @return
		 */
		private void separateQuestionList() {
//			System.out.println("separating the question texts");
			StringTokenizer st = new StringTokenizer(this.questionText, ",");
			int i = 0;
			while(st.hasMoreTokens() && i < this.nComponents){
				((ComboboxComponent)this.componentList.get(i++)).setLabelTxt(st.nextToken());
			}
			while(i < this.nComponents){
				((ComboboxComponent)this.componentList.get(i++)).setLabelTxt("");
			}
			//set the text for the end label
			if(st.hasMoreTokens()){
				this.endLabel.setText(st.nextToken());
			}else{
				this.endLabel.setText("");
			}
			
			this.validate();
		}

		/**
		 * This method separates a list of texts for all the comboboxes.
		 * @return
		 */
		private void separateTextList() {
//			System.out.println("separating the choices list");
			StringBuffer sb = new StringBuffer(this.choicesText);
			int startIndex = 0, endIndex, i = 0;
			do{
				startIndex = sb.indexOf("[", startIndex);
				if(startIndex != -1){
					endIndex = sb.indexOf("]", startIndex);
					((ComboboxComponent)this.componentList.get(i++)).addTextToCombobox(sb.substring(startIndex + 1, endIndex));
					startIndex = endIndex + 1;
				}
			}while(startIndex > 0 && startIndex < sb.length() && i < this.nComponents);
		}
		
		protected boolean initialize() {
			this.alreadyDone = false;
			if (!super.initialize())
				return false;
	
			this.addStudentActionListener((StudentActionListener) getController().getStudentInterface().getHintInterface());
			this.addIncorrectActionListener((IncorrectActionListener) getController().getStudentInterface().getHintInterface());
			addCommWidgetName (commName);
			addCommListener ();
			if (getController().isShowWidgetInfo()) 
				setToolTipWidgetInfo();
			return true;
		}

		/**
		 * This method returns the value ie the input of the selection
		 * action input triple
		 */
		public Object getValue(){
			// the input value is a comma separated list of selected values
			// from each of the comboboxes
			// for each combobox get the selected value and add it to the 
			// value
			StringBuffer sb = new StringBuffer();
			
			for(int i = 0; i < this.nComponents; i++){
				String obj = ((ComboboxComponent)this.componentList.get(i)).getValue();
				if(obj != null){
					sb.append(obj);
					if((i + 1) < this.nComponents){
						sb.append(",");
					}
				}
			}
			return sb.toString();
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
			actionFromBR = true;

			setSelectedValues(input, this.correctColor);

			if (getUniversalToolProxy().lockWidget())
				locked = true;
			// this.getFocusCycleRootAncestor().transferFocus();
			if (!this.alreadyDone) {
				this.fireStudentAction(new StudentActionEvent(this));
				this.alreadyDone = true;
			}
			dirty = false;
			actionFromBR = false;
		}
	}

		public void doLISPCheckAction (String selection, String input) {
                        actionFromBR = true;
			setSelectedValues(input, this.LISPCheckColor);
			if (getUniversalToolProxy().lockWidget())
                            locked = true;
                        actionFromBR = false;
		}

		public void doIncorrectAction (String selection, String input) {
                        actionFromBR = true;
			setSelectedValues(input, this.incorrectColor);
			this.locked = false;
//			setLock(false);
//			this.fireIncorrectAction(new IncorrectActionEvent(this));
			this.alreadyDone = false;
			this.fireIncorrectAction(new IncorrectActionEvent(this));
			actionFromBR = false;
		}

		public void doInterfaceAction(String selection, String action, String input){

			actionFromBR = true;
			this.locked = false;
			setSelectedValues(input, this.startColor);
			
			if (action.equalsIgnoreCase(UPDATE_INVISIBLE)) {
				if (trace.getDebugCode("inter")) trace.out("inter", "Set InVisible: "+input);
				if (input.equalsIgnoreCase("true"))
					setInvisible(true);
				else setInvisible(false);
				setVisible(!isInvisible());
				return;
				// setInvisible(input);
			}
			else if (SET_VISIBLE.equalsIgnoreCase(action)) {   // suppress feedback
				if (trace.getDebugCode("inter")) trace.out("inter", "SetVisible: " + input);
				setVisible(input);
				return;
			}
			
			if (getController().isStartStateInterface()){
				locked = true;
//				setLock(true);
			}
                        actionFromBR = false;
		}
		
		public void setSelectedValues(String input, Color color) {
			// input is a comma separated list of string values for 
			// each of the combo boxes
			StringTokenizer st = new StringTokenizer(input, ",");
			int i = 0;
			ComboboxComponent combo;
			while(st.hasMoreTokens()){
				if(i < this.componentList.size()){
					combo = (ComboboxComponent)this.componentList.get(i++);
					// set the fore color of the text in the combobox
					combo.setColor(color);
					// set the text in the combobox
					combo.setValue(st.nextToken());
					combo.repaint();
				}
			}
		}
	
		/**
		 *	This method returns a message to the production system to create 
		 *	working 
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

		mo.setProperty("WidgetType", "CommQuestionCombobox");
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
		 * 
		 */
		public boolean isChangedFromResetState () {
			ComboboxComponent combo;
			for(int i = 0; i < this.nComponents; i++){
				combo = (ComboboxComponent)this.componentList.get(i);
				if (combo.getValue().equals("")){
					return false;
				}
			}
			return true;
		}
	
		/**
		 * Used to reset the commwidget
		 */
		public void reset (TutorController controller) {
			
			this.alreadyDone = false;
			initialize();
//			this.previousValue = "";
			this.locked = false;
                        actionFromBR = false;
//			this.setLock(false);
			this.setEnabled(true);
			ComboboxComponent combo;
			for(int i = 0; i < this.nComponents; i++){
				combo = (ComboboxComponent)this.componentList.get(i);
				combo.setValue("");
				combo.setForeground(this.startColor);
			}
		}

		public void setSize (Dimension d) {
			super.setSize (d);
		}

		/**
		 * This method creates the jess deftemplates for the multiple choice widget
		 * @return - returns a vector of deftemplates for the multiple choice widget
		 */
		public Vector createJessDeftemplates(){
			Vector deftemplates = new Vector();
		
			String choiceTemplate = "(deftemplate questionCombobox (slot name) (slot value) (slot question))";
			deftemplates.add(choiceTemplate);		
			return deftemplates;
		}
		
		/**	this method is for creating the instances corresponding to the multiple choice widget
		 * @return
		 */
		public Vector createJessInstances(){
			Vector instances = new Vector();
			String str = "(bind ?" + getCommName() + " (assert (questionCombobox (name " + getCommName() + "))))";

			instances.add(str);
			return instances;
		}

		public static void main(String[] args) {
			JFrame frame = new JFrame();
			JCommQuestionComboBox qcb = new JCommQuestionComboBox();
			
			frame.getContentPane().setLayout(new FlowLayout());
			frame.getContentPane().add(qcb);
			frame.pack();
			frame.show();
			
			qcb.setNPairs(2);
			qcb.setQuestionLayout(1);
			qcb.setQuestionText("Question1, Question2, Question3");
			qcb.setChoicesText("[x, y, z][a, ][p, q, r]");
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent ae) {
		    removeHighlight(commName);
		    
			Object source = ae.getSource();
			String selectedValue = getValue().toString();

                        if (actionFromBR)
                            return;
                        
			// if the widget is not locked then get the selected value of the combobox
			if(!locked && initialized && !selectedValue.equals("")){
                                if(this.submitBtn != null && source.equals(this.submitBtn)){
                                        this.previousValue = getValue().toString();
                                        dirty = true;
                                        sendValue();
                                        return;
                                }	
                                
                                if(source.getClass().equals(JComboBox.class)){
                                        // if the current selection is not equal to ""
                                        if(!selectedValue.equals("")){
//							if(!this.previousValue.equals(selectedValue)){
                                                        // set the previous value to the new value
                                                        if(this.nComponents == 1){
                                                                this.previousValue = selectedValue;
                                                                // send the value to the utp
                                                                dirty = true;
                                                                sendValue();							
                                                        }else{
                                                                ((JComboBox)source).setForeground(this.startColor);
                                                        }
//							}
                                        }
                                }
			}else if(locked){
				// if the widget is locked then change the selection to the previous selection
				if(!this.previousValue.equals("") && !this.previousValue.equals(getValue())){
					this.setSelectedValues(previousValue, this.correctColor);
				}
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
			setBorder(originalBorder);
		}
		
	
		public void focusGained(FocusEvent e) {
                    
		}

		/* (non-Javadoc)
		 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent e) {
		}

		/**
		 * @return
		 */
		public synchronized String getChoicesText() {
			return choicesText;
			
		}

		/**
		 * @param choicesText
		 */
		public synchronized void setChoicesText(String choicesText) {
			this.choicesText = choicesText;
			this.separateTextList();
			this.validate();
		}

		/**
		 * @return
		 */
		public synchronized int getNPairs() {
			return nComponents;
		}

		/**
		 * @param pairs
		 */
		public synchronized void setNPairs(int pairs) {
			nComponents = pairs;
			this.createQuestionPanel();
			this.validate();
		}

		/**
		 * @return
		 */
		public synchronized int getQuestionLayout() {
			return questionLayout;
			
		}

		/**
		 * @param questionLayout
		 */
		public synchronized void setQuestionLayout(int pairLayout) {
			this.questionLayout = pairLayout;
			this.createQuestionPanel();
			this.validate();
		}

		public void setQuestionText(String text){
			this.questionText = text;
			this.separateQuestionList();
			this.validate();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			
		}
		
		/**
		 * Returns the comm name of the currently selected choice
		 */
		public String getCommNameToSend () {
			String s = this.commName;
			return s;
		}
}
