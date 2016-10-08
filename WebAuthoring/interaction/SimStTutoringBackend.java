package interaction;

import java.util.ArrayList;


/* Toy interface for testing purposes */

public class SimStTutoringBackend extends Backend {
	
	
	private static final Color ENABLED_BACKGROUND_COLOR = new Color(255,255,255);
	private static final Color DISABLED_BACKGROUND_COLOR = new Color(215,215,215);
	private static final Color CORRECT_BORDER_COLOR = new Color(19,230,90);
	private static final Color INCORRECT_BORDER_COLOR = new Color(255,0,0);
	private static final Color GIVEN_BORDER_COLOR = new Color(0,0,0);
	private static final int BORDER_WIDTH = 3;
			
	private static String YES_BUTTON_NAME="yesButton";
	private static String NO_BUTTON_NAME="noButton";
	private static String START_STATE_BUTTON_NAME="startStateButton";
	private static String NEW_PROBLEM_BUTTON_NAME="newProblemButton";
	private static String SIMST_COMM_TEXT_AREA="textFieldSS";
	private static String SKILL_TEXT_AREA="textFieldSkill";
	
	
	private boolean learningStarted=false;
	private void setLearningStarted(boolean flag){ learningStarted=flag;}
	private boolean getLearningStarted(){return learningStarted;}
	
	
	private ArrayList<SAI> startStateElements;
	private void clearStartStateElements(){
		if (startStateElements!=null)
				startStateElements.clear();	
	}
	
	private void addStartStateElement(SAI sai){
		if (startStateElements==null)
			startStateElements=new ArrayList<SAI>();
		
		startStateElements.add(sai);
	}
	
	//Added argument in constructor to match the parent constructor - Shruti
	public SimStTutoringBackend(String[] argV) {
		super(argV);
		
	}
	
	
	/**
	 * Overridden event listener that catches events from the interface 
	 */
	@Override
	public void processInterfaceEvent(InterfaceEvent ie) {
		// TODO Auto-generated method stub
		switch(ie.getType()){
		case SAI:
			processSAI(ie.getEvent());
			break;
		}
	}
	
	/**
	 * Utility method to display a text on the SimStudent communication textarea on the interface
	 * @param text : a string with the text to be displayed
	 */
	private void sendSimStudentComm(String text){
		SAI test1=new SAI(SIMST_COMM_TEXT_AREA,"UpdateTextArea",text);
		sendSAI(test1);	
	}
	
	
	/**
	 * Method that controls what happens if an SAI even is taken on the interface 
	 * @param sai
	 */
	private void processSAI(SAI sai) {
		String action = sai.getFirstAction();
		if(action.equals("UpdateTextArea")){
			processUpdateTextArea(sai);
		}else if(action.equals("ButtonPressed")){
			processButtonClick(sai);
		}
	}
	

	/**
	 * Lower-level method that controls what happens if a button is clicked on the interface
	 * @param sai
	 */
	private void processButtonClick(SAI sai){
		
		try {
			String selection = sai.getFirstSelection();
			if (selection != null) {
				InterfaceAttribute at = getComponent(selection);
				if (selection.equals(YES_BUTTON_NAME))
						sendSimStudentComm("Yes is pressed!");
				else if (selection.equals(NO_BUTTON_NAME))
					sendSimStudentComm("No is pressed");			
				else if (selection.equals(START_STATE_BUTTON_NAME)){	
					disableStartStateElements();
					sendSimStudentComm("StartState from interface is: " + displayStartState());
				}
				else if (selection.equals(NEW_PROBLEM_BUTTON_NAME)){	
					clearStartStateElementsInterface();
					sendSimStudentComm("New problem is clicked!");
					this.clearStartStateElements();
				}
	
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Utility method to display the start state elements user has selected
	 * @return a string with the start state elements
	 */
	private String displayStartState(){
		String ss="";
		for (int i = 0; i < startStateElements.size(); i++) {
		    SAI sai = startStateElements.get(i);
		    ss=ss + ", " + sai.getFirstSelection() + ":" + sai.getFirstInput();
		    
		}
		return ss;
	}
	
	/**
	 * Method to disable the start state elements once start state is clicked
	 */
	private void disableStartStateElements(){
		for (int i = 0; i < startStateElements.size(); i++) {
		    SAI sai = startStateElements.get(i);    
		    InterfaceAttribute at = getComponent(sai.getFirstSelection());
		    if (at!=null) this.disableElement(at);
		    
		}
	}
	/**
	 * Method to clear the start state elements 
	 */
	private void clearStartStateElementsInterface(){
		for (int i = 0; i < startStateElements.size(); i++) {
		    SAI sai = startStateElements.get(i);		    
		    InterfaceAttribute at = getComponent(sai.getFirstSelection());
		    if (at!=null) this.clearElement(at);	
		    SAI test1=new SAI(sai.getFirstSelection(),sai.getFirstAction(),"");
			sendSAI(test1);
		}
	}
	
	
	/**
	 * Method to process what happens if a text area is updated
	 * @param sai
	 */
	private void processUpdateTextArea(SAI sai) {
		

		try {
			String selection = sai.getFirstSelection();
			if (selection != null) {
				
				InterfaceAttribute at = getComponent(selection);
				if (at != null && at.getIsEnabled()) {
					
					
						//formatElement(getComponent(sai.getFirstSelection()));
						
						if (this.getLearningStarted()==false){
								addStartStateElement(sai);
						}				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Method to re-enable an interface element
	 * @param ia
	 */
	private void clearElement(InterfaceAttribute ia) {
		ia.setBackgroundColor(ENABLED_BACKGROUND_COLOR);
		ia.setIsEnabled(true);
		modifyInterface(ia);
	}
	
	/**
	 * Method for disabling an interface element
	 * @param ia
	 */
	private void disableElement(InterfaceAttribute ia) {
		ia.setBackgroundColor(DISABLED_BACKGROUND_COLOR);
		ia.setIsEnabled(false);
		modifyInterface(ia);
	}
	

	private void formatElement(InterfaceAttribute ia) {
		ia.setBorderColor(CORRECT_BORDER_COLOR);
		//ia.setBackgroundColor(DISABLED_BACKGROUND_COLOR);
		//ia.setIsEnabled(false);
		ia.setBorderWidth(BORDER_WIDTH);
		modifyInterface(ia);
	}
	
	@Override
	public void initializeInterfaceAttribute(InterfaceAttribute im) {

	}
	
	//Added unimplemented abstract method - Shruti
	@Override
	public void parseArgument(String[] arg) {
		// TODO Auto-generated method stub
		
	}
	
}
