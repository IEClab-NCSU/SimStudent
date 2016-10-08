/*
 * Created on Dec 17, 2003
 *
 */
package pact.CommWidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.event.EventListenerList;

import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.Utilities.Utils;
import edu.cmu.pact.Utilities.VersionInformation;

/**
 * @author sanket
 *
 */
public abstract class JCommQuestion extends JCommWidget implements QuestionWidgetInterface{
	/**
	 * Widget for sending the feedback to the server
	 */
	/**
	 * The text representing the question for this multiple choice widget.
	 */
	String questionText = "Question?";
	/**
	 * Label to hold the question text
	 */
	JLabel questionLbl = new JLabel();
	/** 
	 * Holds value of property toolTipText.
	 */
	String toolTipText;
	
	/** order in which the component is to be displayed */
	int scaffoldingOrder = 1;
	
	EventListenerList studentActionListeners;

	Color backColor;

	protected Font startFont;

	private boolean originalQuestion = false;
	/**
	 * Contructor called when an instance of this widget is created.
	 */
	public JCommQuestion(){
		this.questionLbl.setText(questionText);
		this.studentActionListeners = new EventListenerList();
		this.questionLbl.setFocusable(false);
//		this.questionLbl.setFont(getDefaultFont());
//		addMouseListener (controller.getActiveTutorInterface());
	}
	/** 
	 * Getter for property toolTipText.
	 * @return Value of property toolTipText.
	 *
	 */

    protected boolean initialize() {

		if (!super.initialize(getController()))
			return false;
		return true;
	}

        public String getToolTipText() {
		return this.toolTipText;
	}
        
        /////////////////////////////////////////////////////
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
	 * Setter for property toolTipText.
	 * @param toolTipText New value of property toolTipText.
	 *
	 */
	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}
    
	/**
	 * @return
	 */
	public synchronized String getQuestionText() {
		return questionText;
	}

	/**
	 * @param questionText
	 */
	public synchronized void setQuestionText(String qText) {
		this.questionText = qText;
		this.questionLbl.setText(Utils.replaceImg(qText));
		this.validate();
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
		if (questionLbl != null)
			questionLbl.setFont(startFont);
	}

	public Font getFont() {
		if (questionLbl != null)
			return questionLbl.getFont();
		else
			return super.getFont();
	}
	public void addStudentActionListener(StudentActionListener l){
//		Object[] obj = this.studentActionListeners.getListenerList();
		Object[] obj = this.studentActionListeners.getListeners(StudentActionListener.class);
		if(obj == null || obj.length == 0){
			studentActionListeners.add(StudentActionListener.class, l);
			return;
		}
		// check to see if l is already added as a listener
		for(int i = 0; i < obj.length; i++){
			if(obj[i].equals(l)){
				return;
			}
		}
		studentActionListeners.add(StudentActionListener.class, l);
	}

	public void removeStudentActionListener(StudentActionListener l){
		studentActionListeners.remove(StudentActionListener.class, l);
	}

	public EventListener[] getStudentActionListener(){
		return this.studentActionListeners.getListeners(StudentActionListener.class);
	}
	
	public void fireStudentAction(StudentActionEvent e){
//		System.out.println("Inside fireStudentAction");
		Object[] listeners = studentActionListeners.getListenerList();
		for(int i = 0; i < listeners.length; i++){
//			System.out.println("studentListener: " + i + " :" + listeners[i]);
			if(listeners[i] == StudentActionListener.class){
//				System.out.println("calling studentActionPerformed: JCommQuestion.java");
				((StudentActionListener)listeners[i+1]).studentActionPerformed(e);
			}
		}
	}
	
	/**
	 * @return
	 */
	public synchronized int getScaffoldingOrder() {
		return scaffoldingOrder;
	}
	/**
	 * @param dialogOrder
	 */
	public synchronized void setScaffoldingOrder(int dialogOrder) {
		this.scaffoldingOrder = dialogOrder;
	}
	
	public void hideAllComponents(boolean b){
		Component[] components = this.getComponents();
		for(int i = 0; i < components.length; i++){
			components[i].setVisible(!b);
		}
	}
	
	public void addIncorrectActionListener(IncorrectActionListener l){
//		Object[] obj = this.studentActionListeners.getListenerList();
		Object[] obj = this.studentActionListeners.getListeners(IncorrectActionListener.class);
		if(obj == null || obj.length == 0){
			studentActionListeners.add(IncorrectActionListener.class, l);	
			return;
		}
		// check to see if l is already added as a listener
		for(int i = 0; i < obj.length; i++){
			if(obj[i].equals(l)){
				return;
			}
		}
		studentActionListeners.add(IncorrectActionListener.class, l);	

	}

	public void removeIncorrectActionListener(IncorrectActionListener l){
		studentActionListeners.remove(IncorrectActionListener.class, l);
	}

	public void fireIncorrectAction(IncorrectActionEvent e){
//		System.out.println("Inside fireIncorrectActionEvent");
		Object[] listeners = studentActionListeners.getListenerList();
		for(int i = 0; i < listeners.length; i++){
			if(listeners[i] == IncorrectActionListener.class){
//				System.out.println("Calling incorrectActionPerformed");
				((IncorrectActionListener)listeners[i+1]).incorrectActionPerformed(e);
			}
		}
	}
	

	/**
	 * This method creates the jess deftemplates for the widget
	 * @return - returns a vector of deftemplates for the widget
	 */
	public abstract Vector createJessDeftemplates();
	
	/**	this method is for creating the instances corresponding to the widget
	 * @return
	 */
	public abstract Vector createJessInstances();


	/**
	 * this method adds the answer to the command listner so that it can receive
	 * comm messages
	 */
	public void addCommListener () {

		if (!VersionInformation.includesCL())
			return;
		edu.cmu.pact.CommManager.CommManager.instance().addMessageReceiver(this, commName, getController());
	}

	/**
	 * Returns the comm name of the currently selected choice
	 */
	public String getCommNameToSend () {
		return commName;
	}

	/**
	 * @return
	 */
	public synchronized boolean isOriginalQuestion() {
		return originalQuestion;
	}

	/**
	 * @param originalQuestion
	 */
	public synchronized void setOriginalQuestion(boolean originalQuestion) {
		this.originalQuestion = originalQuestion;
	}
	
	public void setEnabled(boolean b){
		for(int i = 0; i < this.getComponents().length; i++){
			this.getComponent(i).setEnabled(b);
//			this.getComponent(i).repaint();
		}
	}

	public void mousePressed(MouseEvent e) {
	}
}
