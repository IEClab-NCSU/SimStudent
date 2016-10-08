/*
 * Created on Dec 17, 2003
 *
 */
package pact.CommWidgets;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import pact.CommWidgets.event.HelpEvent;
import pact.CommWidgets.event.HelpEventListener;
import pact.CommWidgets.event.IncorrectActionEvent;
import pact.CommWidgets.event.IncorrectActionListener;
import pact.CommWidgets.event.ProblemDoneEvent;
import pact.CommWidgets.event.ProblemDoneListener;
import pact.CommWidgets.event.StudentActionEvent;
import pact.CommWidgets.event.StudentActionListener;
import edu.cmu.pact.ctat.MessageObject;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.TutorController;

public class JCommPanel extends JCommWidget implements ContainerListener, 
			StudentActionListener, IncorrectActionListener, HelpEventListener, ProblemDoneListener{
	
    private static int DEFAULT = 1;
    /**  Constant - widgets will be displayed in the specified order. */
	private static int SPECIFIED = 2;
	/** order as specified by the user */
	private int dialogOrder = DEFAULT;
	/**  Do not use dialog. Display all the components at once */
	private static int NODIALOG = -1;	
	/** keeps track of the current component's number */ 	
	private int currentComponentNumber = 1;
	/** Keeps track of the number of instances of this widget */
	private static int counter = 0;
	/** 
	 * set this variable to true when you want the hint button to be enabled
	 * only after the student performs an incorrect action.
	 */
	private boolean dfaPanel = false;
	/** hint Button */
	private JCommButton hintBtn;
	/** current commWidget that the student is working on */
	private JCommQuestion currentCommQuestion;
	/** cut_to_chase determines if the student can go back and answer the 
	 *  original question before completing all the scaffolding questions
	 */
	
	public JCommPanel(){
		setActionName("UpdatePanel");
		// adding the scrollbars to the JCommPanel
		this.addContainerListener(this);

	}

	/**
	 * Make the component visible and also if is a JCommQuestion then
	 * remember it, so that we can use it later on for displaying the 
	 * scaffolding questions and other things like that
	 * @param component
	 * @param b
	 */
	private void setComponentVisible(Component component, boolean b){
		component.setVisible(b);
		if(b && component instanceof JCommQuestion){
			this.currentCommQuestion = (JCommQuestion)component;	
		}
		if(b){
			component.requestFocus();
		}
	}

	private void showFirstQuestionComponent(){
		// add the help listener to the help button
		this.addHelpListeners();
		this.addDoneListeners();
		
		if(this.dialogOrder == DEFAULT){
			// hide all other comm widgets except the first question widget
			this.currentComponentNumber = 1;
			Component[] components = this.getComponents();
			int i;
			// display the first question widget and all other non question widgets
			// before it
			for(i = 0; i < this.getComponentCount(); i++){
				Component component = components[i];
				this.setComponentVisible(component, true);
				if(component instanceof JCommQuestion){
					if(((JCommQuestion)component).isOriginalQuestion()){
                        getController().getStudentInterface().getHintInterface().setDisplayHint(false);
					}else{
                        getController().getStudentInterface().getHintInterface().setDisplayHint(true);
					}
					break;
				}
			}
			// hide all other components
			for(++i; i < this.getComponentCount(); i++){
				Component component = components[i];
				this.setComponentVisible(component, false);
			}
		}else if(this.dialogOrder == SPECIFIED){
			// get first component according to the order number 
			Component component = getNextComponent(-1, null);
			if(component != null){
				this.setComponentVisible(component, true);
			    getController().getStudentInterface().getHintInterface().setDisplayHint(false);
				hideRest(((JCommQuestion)component).getScaffoldingOrder(), (JCommQuestion)component);
			}
		}
	}
	
	/**
	 * 
	 */
	private void addDoneListeners() {
		JCommButton btn = null;
        for (Iterator i = getController().getCommWidgetTable().values().iterator(); i.hasNext(); ) {
			JCommWidget widget = (JCommWidget) i.next();
			if(widget.getCommName().equalsIgnoreCase("done")){
				if(widget instanceof JCommButton){
					btn = (JCommButton)widget;
					btn.removeAllProblemDoneListeners();
					btn.addProblemDoneListener(this);
				}
				widget.setEnabled(false);
			}
		}
	}

	/**
	 * 
	 */
	private void addHelpListeners() {
		JCommButton btn = null;
        for (Iterator i = getController().getCommWidgetTable().values().iterator(); i.hasNext(); ) {
            JCommWidget widget = (JCommWidget) i.next();
			if(widget.isHintBtn){
				if(widget instanceof JCommButton){
					btn = (JCommButton)widget;
					btn.removeAllHelpListeners();
					btn.addHelpEventListener(this);
				}
			}
		}
	}

	/**
	 * this method returns the next component from the comm panel
	 * this method does a bubble sort on the list of the components 
	 * based on the scaffolding order
	 * @param current - scaffolding order number of the current commquestion
	 * @param currentComp - current commQuestion that is displayed
	 * @return
	 */
	private Component getNextComponent(int current, JCommQuestion currentComp){
		Component[] components = this.getComponents();
		JCommQuestion nextComponent, component = null;
		int minOrder = 1000;
		
		// for each component in the comm panel
		for(int i = 0; i < components.length; i++){
			// if the component is a commQuestion
			if(components[i] instanceof JCommQuestion){
				// get the next commQuestion from the list
				nextComponent = (JCommQuestion)components[i];
				// check to see if the scaffolding order of this component is
				// greater than the current component and less than some other
				// component whose scaffolding order is also greater than the 
				// current component 
				if((nextComponent.getScaffoldingOrder() > current) && 
						(nextComponent.getScaffoldingOrder() < minOrder)){
					minOrder = nextComponent.getScaffoldingOrder();
					component = nextComponent; 
					// else if the scaffolding order of this component is equal
					// to the scaffolding order of the currently displayed 
					// component then return this component and break;
				}else if((nextComponent.getScaffoldingOrder() == current)){
					if(currentComp != null && !nextComponent.equals(currentComp)){
						minOrder = nextComponent.getScaffoldingOrder();
						component = nextComponent; 
						break;
					}
				}
			}
		}
		return component;
	}
	
	/**
	 * Hide all the components whose scaffolding order is greater than or equal 
	 * to the scaffolding order of the current component 
	 * @param current
	 */
	private void hideRest(int current, JCommQuestion comp){
		Component[] components = this.getComponents();
		JCommQuestion nextComponent;
		// for all other components that have order number greater than
		// the current number
		for(int i = 0; i < components.length; i++){
			if(components[i] instanceof JCommQuestion){
				nextComponent = (JCommQuestion)components[i];
				// if the order is greater than the current then hide the
				// component
				if((nextComponent.getScaffoldingOrder() > current )){
					this.setComponentVisible(nextComponent, false);
				}else if((nextComponent.getScaffoldingOrder() == current ) && 
					(comp != null && !nextComponent.equals(comp))){
						this.setComponentVisible(nextComponent, false);
				}
			}
		}
	}
	
	private void isLastQuestionComponent(){
	
	}
		
	public void reset(TutorController controller){
		this.showFirstQuestionComponent();
	}

	//////////////////////////////////////////////////////
	

	/* (non-Javadoc)
	 * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
	 */
	public void componentAdded(ContainerEvent ce) {
		Component component = ce.getChild();
		String className = component.getClass().getName();
		if(component instanceof JCommQuestion){
			// adding the listeners for correct and incorrect actions
			((JCommQuestion)component).addStudentActionListener(this);
			((JCommQuestion)component).addIncorrectActionListener(this);
			((JCommQuestion)component).addStudentActionListener((StudentActionListener) getController().getStudentInterface().getHintInterface());
			((JCommQuestion)component).addIncorrectActionListener((IncorrectActionListener) getController().getStudentInterface().getHintInterface());
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
	 */
	public void componentRemoved(ContainerEvent arg0) {

	}

	/* (non-Javadoc)
	 * @see pact.CommWidgets.event.StudentActionListener#
	 * studentActionPerformed(pact.CommWidgets.event.StudentActionEvent)
	 */
	public void studentActionPerformed(StudentActionEvent sae) {
		// if the question is an original question then get the next component in 
		// sequence to display 
		Object source = sae.getSource();
		if(!((Component)source).isVisible()){
			this.setComponentVisible((Component)source, true);
		}
		if(source instanceof JCommQuestion){
			if(!((JCommQuestion)source).isOriginalQuestion()){
				// get the next component in sequence to display
				// get the component count
				showNextQuestionComponent(source);
			}else{
				this.fireProblemDoneEvent(new ProblemDoneEvent(this));
			}
		}
		
		// if this is a dfa panel then disable the hint button 
		this.hintBtn = this.getHintButton();
		if(this.dfaPanel && this.hintBtn != null){
			this.hintBtn.setEnabled(false);
		}
		
	}
	/* (non-Javadoc)
	 * @see pact.CommWidgets.event.IncorrectActionListener#incorrectActionPerformed(pact.CommWidgets.event.IncorrectActionEvent)
	 */
	public void incorrectActionPerformed(IncorrectActionEvent e) {
		// if this is a dfa panel then enable the hint button
		this.hintBtn = this.getHintButton();
		if(this.dfaPanel && hintBtn != null){
			hintBtn.setEnabled(true);
		}
		// if the question is an original question then get the next component in 
		// sequence to display 
		Object source = e.getSource();
		if(source instanceof JCommQuestion){
			if(((JCommQuestion)source).isOriginalQuestion()){
				// check for various parameters
				this.fireProblemDoneEvent(new ProblemDoneEvent(this));
				 // disable this question and load the scaffolding questions
			}else if(source instanceof JCommQuestionComboBox){
				this.transferFocus();
				((Component)source).getFocusCycleRootAncestor().transferFocusUpCycle();
				this.getHintButton().requestFocus();
			}
		}
	}

	private JCommButton getHintButton(){
		JCommButton btn = null;
        for (Iterator i = getController().getCommWidgetTable().values().iterator(); i.hasNext(); ) {
            JCommWidget widget = (JCommWidget) i.next();
			if(widget.isHintBtn){
				if(widget instanceof JCommButton){
					btn = (JCommButton)widget;
					break;
				}
			}
		}
		return btn;
	}	
	
	public void init(){
		Component[] components = this.getComponents();
		for(int i = 0; i < this.getComponentCount(); i++){
			Component component = components[i];
			String className = component.getClass().getName();
			if(component instanceof JCommQuestion){
				((JCommQuestion)component).addStudentActionListener(this);
				((JCommQuestion)component).addIncorrectActionListener(this);
			}
		}
		this.showFirstQuestionComponent();
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

	private void showNextQuestionComponent(Object source){

		if(this.dialogOrder == DEFAULT){
			int count = this.getComponentCount();
			int i = 0;
			Component comp;
			while(i < count){
				comp = this.getComponent(i++);
				if(source.equals(comp)){
					break;
				}
			}
			this.currentComponentNumber = i;
			// get the next Question widget after this question
			// displaying all the labels other question widgets that are before
			// this question
			while(this.currentComponentNumber > 0 && this.currentComponentNumber < count){
				Component component = this.getComponent(this.currentComponentNumber);
				this.setComponentVisible(component, true);
				this.currentComponentNumber++;
				component.getFocusCycleRootAncestor().transferFocus();
				if(component instanceof JCommQuestion){
					// if this is a comm question then break out of the loop
					break;
				}
			}
			// check to see if the current component is the last component
			if(this.currentComponentNumber == count){
                getController().getStudentInterface().getHintInterface().displaySuccessMessage();
			}
		}else if(dialogOrder == SPECIFIED){
			Component comp = getNextComponent(((JCommQuestion)source).getScaffoldingOrder(), ((JCommQuestion)source));
			if(comp != null){
				this.currentComponentNumber = ((JCommQuestion)comp).getScaffoldingOrder();
				this.setComponentVisible(comp, true);
			}else{
				// no more scaffolding question to be displayed
				// display the message "click done button"
                getController().getStudentInterface().getHintInterface().displaySuccessMessage();
				this.fireProblemDoneEvent(new ProblemDoneEvent(this));
			}
		}
	}
	
	/**
		Returns a comm message which describes this interface
		element.
	*/
	//////////////////////////////////////////////////////
	public MessageObject getDescriptionMessage() {

		if (!initialize()) {
			trace
					.out(
							5,
							this,
							"ERROR!: Can't create Comm message because can't initialize.  Returning empty comm message");
			return null;
		}

		MessageObject mo = MessageObject.create("InterfaceDescription");
		mo.setVerb("SendNoteProperty");

		if (getUniversalToolProxy() == null) {
			return mo;
		}

		mo.setProperty("WidgetType", "JCommPanel");
		mo.setProperty("CommName", commName);
		mo.setProperty("UpdateEachCycle", new Boolean(updateEachCycle));

		Vector<String> deftemplates = createJessDeftemplates();
		Vector<String> instances = createJessInstances();

		if (deftemplates != null)
			mo.setProperty("jessDeftemplates", deftemplates);

		if (instances != null)
			mo.setProperty("jessInstances", instances);

		serializeGraphicalProperties(mo);

		init();
		return mo;
	}


	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessDeftemplates()
	 */
	public Vector<String> createJessDeftemplates() {
		Vector<String> deftemplates = new Vector<String>();
		
		String deftemplateStr = "(deftemplate commPanel (slot name))";
		deftemplates.add(deftemplateStr);
		
		return deftemplates;
	}
	
	/* (non-Javadoc)
	 * @see pact.CommWidgets.JCommWidget#createJessInstances()
	 */
	public Vector<String> createJessInstances() {
		Vector<String> instances = new Vector<String>();
		
		String instanceStr = "(assert (commPanel (name " + commName + ")))";
		instances.add(instanceStr);
		
		return instances;
	}
	
	protected boolean initialize() {

		if (!super.initialize(getController()))
			return false;
	
		if (getController().isShowWidgetInfo()) 
			setToolTipWidgetInfo();
        
		return true;
	}

	public void getCommComponents(){
	}

	public void helpSeeked(HelpEvent e) {
	}
	

	/* (non-Javadoc)
	 * @see pact.CommWidgets.event.ProblemDoneListener#problemDone(pact.CommWidgets.event.ProblemDoneEvent)
	 */
	public void problemDone(ProblemDoneEvent e) {
		JCommButton btn = null;
        for (Iterator i = getController().getCommWidgetTable().values().iterator(); i.hasNext(); ) {
            JCommWidget widget = (JCommWidget) i.next();
			if(widget.getCommName().equalsIgnoreCase("done")){
				widget.setEnabled(true);
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
	}
}
