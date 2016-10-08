package interaction;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import interaction.InterfaceAttribute.Style;
import jess.JessException;
import tracer.MTResult;
import tracer.MTSAI;
import tracer.MTSolver;
import tracer.MTResult.MTResultType;

/*
 * This MetaBackend will be created for every time an interface talks to the Gatekeeper.
 * The MetaBackend has access to the ModelTracer
 */
public class MetaBackend extends Backend{

	private String HINT_BUTTON_NAME = "Hint";											//name of the hint button
	private String DONE_BUTTON_NAME =  "Done";											//name of the done button
	public BKT theBKT = new BKT(); 									//creates the BKT
	public ModelTracerBackend modelTracer;												//model Tracer
	public Map<String,Boolean> answeredIncorrect = new HashMap<String,Boolean>();		//boolean if student answers q wrong for each KC
	public boolean hintUsed = false;														//boolean to represent whether student used Hint for next
	public String userID;
	public String problemName;
	
	/*
	 * Constructor for creating an instance of MetaBackend
	 * @param init is the wme init file for for the problem
	 * @param types is the wme types file for the KC model
	 * @param production is the wme productionRules file for the KC model
	 * @param vals is the dictionary of KCs, for a certain KC model, to their L,G,S,T values
	 */
	
	public MetaBackend(String[] argV, Map<String, Double[]> vals) {
		super(argV);
		modelTracer = new ModelTracerBackend(argV);												//Creates the Model Tracer for the KC model
		for (String currSkill : vals.keySet()) {
			theBKT.vals.put(currSkill, new Double[4]);												//Creates Array for L,G,S,T values
			theBKT.vals.get(currSkill)[0] = vals.get(currSkill)[0];								//Stores L Value
			theBKT.vals.get(currSkill)[1] = vals.get(currSkill)[1];								//Stores G Value
			theBKT.vals.get(currSkill)[2] = vals.get(currSkill)[2];								//Stores S Value
			theBKT.vals.get(currSkill)[3] = vals.get(currSkill)[3];								//Stores T Value
			answeredIncorrect.put(currSkill, false);
			
		}
	}
	
 	
//	public MetaBackend(String init, String types, String production, Map<String, Double[]> vals) { 
//		modelTracer = new ModelTracerBackend(init, types, production);							//Creates the Model Tracer for the KC model
//		for (String currSkill : vals.keySet()) {
//			BKT.vals.put(currSkill, new Double[4]);												//Creates Array for L,G,S,T values
//			BKT.vals.get(currSkill)[0] = vals.get(currSkill)[0];								//Stores L Value
//			BKT.vals.get(currSkill)[1] = vals.get(currSkill)[1];								//Stores G Value
//			BKT.vals.get(currSkill)[2] = vals.get(currSkill)[2];								//Stores S Value
//			BKT.vals.get(currSkill)[3] = vals.get(currSkill)[3];								//Stores T Value
//			answeredIncorrect.put(currSkill, false);
//			
//		}
//	}
	
	public void replaceBKT(HashMap<String, Double[]> vals) {
		for (String currSkill : vals.keySet()) {
			theBKT.vals.put(currSkill, new Double[4]);												//Creates Array for L,G,S,T values
			theBKT.vals.get(currSkill)[0] = vals.get(currSkill)[0];								//Stores L Value
			theBKT.vals.get(currSkill)[1] = vals.get(currSkill)[1];								//Stores G Value
			theBKT.vals.get(currSkill)[2] = vals.get(currSkill)[2];								//Stores S Value
			theBKT.vals.get(currSkill)[3] = vals.get(currSkill)[3];								//Stores T Value
			answeredIncorrect.put(currSkill, false);
		}
	}
	
	public void updateLValue(HashMap<String, Double[]> vals) {
		for (String currSkill : vals.keySet()) {
			theBKT.vals.get(currSkill)[0] = vals.get(currSkill)[0];								//Stores L Value
		}
	}
	
	public void resetBooleans() {
		for (String currSkill : answeredIncorrect.keySet()) {
			answeredIncorrect.put(currSkill, false);
		}
	}

	@Override
	/**
	 * This function is written assuming an attribute will be added to InterfaceEvent to determine the ID of the student
	 * @param ie is the event that is sent every time an action occurs
	 */
	public void processInterfaceEvent(InterfaceEvent ie) {
		switch(ie.getType()){
		case SAI:
			processSAI(ie.getEvent(), ie.getTransactionID());
			break;
		default:
			break;
		}
	}
	
	/**
	 * This function handles events that are SAIs
	 * @param sai is the SAI event that occurred
	 */
	private void processSAI(SAI sai, String transactionID) {
		String action = sai.getFirstAction();													//Action performed on interface
		if(action.equals("UpdateTextArea")){													//Text area typed into
			processUpdateTextArea(sai);															//Use below function to update
		}else if(action.equals("ButtonPressed")){												//Button is pressed
			processButtonPressed(sai, transactionID);															//Use below function to update
		}
	}
	
	private void processUpdateTextArea(SAI sai) {
		
		MTSAI mtsai = modelTracer.makeTracerSai(sai);

		try {
			String selection = sai.getFirstSelection();													//Get name of button
			if (selection != null) {
//				 this should never happen, but right now there is a weird behavior where
//				 moving away from a textbox causes an updatetextarea sai, so we have to
//				 avoid considering the same sai twice by checking if the textbox is enabled,
//				 or else the tutor will not function properly
				InterfaceAttribute at = getComponent(selection);
				if (at != null && at.getIsEnabled()) {
//					System.out.println("Sending sai to solver..... " + mtsai);
					if (modelTracer.solver == null) System.out.println("SOLVER tracer is null");
					MTResult result = modelTracer.solver.sendSAI(mtsai);								//Sending the sai to solver
					String currSkill = result.getFiredRule();											//Get the name of the Fired Rule
					switch (result.getType()) {
					case CORRECT:
						modelTracer.setCorrect(getComponent(sai.getFirstSelection()));					//Set the text box to Correct
						if (answeredIncorrect.get(currSkill)) {											//Incorrect on First Attempt
							theBKT.update(0, currSkill);													//Update BKT for Incorrect		
						}
						else {																			//Correct on First Attempt
							if (hintUsed) {
								theBKT.update(0, currSkill);												//Update BKT for Incorrect because hint used
							}
							else {
								theBKT.update(1, currSkill);												//Update BKT for Correct
							}
						}
						hintUsed = false;																//reset hintUsed for next step
						resetBooleans();																//reset first attempt flags
						break;
					case INCORRECT:
						modelTracer.setIncorrect(getComponent(sai.getFirstSelection()));				//Set the text box to Incorrect
						answeredIncorrect.put(currSkill,true);											//set the incorrect flag for KC
						break;
					case BUGGY:
						modelTracer.setIncorrect(getComponent(sai.getFirstSelection()));				//Set the text box to Incorrect
						modelTracer.sendHintWindowMessage(result.getMessage());							//Send Hint Message for Bug
						answeredIncorrect.put(currSkill,true);											//set the incorrect flag for KC
						break;
					case UNAVAILABLE:
						System.out.println("Unavailable!");												//tracer can be unavailable if currently processing hint/sai request

						break;
					default:
						System.out.println("Unexpected result type: " + result.getType());
						break;
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	/**
	 * This function handles SAIs that are Button Presses
	 * @param sai is the SAI event that occurred
	 * @param studentID is the student ID
	 */
	private void processButtonPressed(SAI sai, String transactionID) {
		try {
			String selection = sai.getSelection().get(0);											//Get name of button
			if (selection.equals(HINT_BUTTON_NAME)) {												//Hint Button Pressed
				hintUsed = true;																	//set the hintUsde flag to true
				modelTracer.processButtonPressed(sai, transactionID);								//send the hint properly
			} else if (selection.equals(DONE_BUTTON_NAME)){											//Done Button Pressed
				MTResult result = modelTracer.solver.sendSAI(modelTracer.makeTracerSai(sai));		//send the SAI
				if(result.getType() == MTResultType.CORRECT) {										//Student Answers Correctly
					if (theBKT.isMastered()) {																//Policy Maker Says Stop
						modelTracer.sendHintWindowMessage("Congratulations, you finished!");		//Send completion message
						//NEED TO SIGNAL TO GATEKEEPER THAT THIS META-BACKEND IS DONE
					}
					else {																			//Policy Maker Says Keep Going
						modelTracer.sendHintWindowMessage("Nice Job! Keep Going!");	//***PUT IN WAIT TIMER FOR ~5 SEC BEFORE GOING TO NEXT QUESTIONS
						int questionIndex = LMS.nextBestQuestion(theBKT.vals);
						//NEED TO LAUNCH NEW QUESTION WITH INDEX FROM QUESTION BANK
					}
				}
				else {																				//Student doesn't answer correctly
					modelTracer.setIncorrect(getComponent(sai.getFirstSelection()));				//Set the DONE Button to Incorrect if problem not completely correct
				}
			} else {																				//Unknown Button
				
			}
			
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
	@Override
	public void parseArgument(String[] arg) {
		modelTracer.parseArgument(arg);
		
	}
	

	void sendHintWindowMessage(String message) {
		modelTracer.sendHintWindowMessage(message);
	}
	
	private void formatHintWindow() {
		modelTracer.formatHintWindow();
	}
	
	private void disableHintButton(InterfaceAttribute ia){
		modelTracer.disableHintButton(ia);
	}
	void setCorrect(InterfaceAttribute ia) {
		modelTracer.setCorrect(ia);
	}
	 
	void setGiven(InterfaceAttribute ia) {
		modelTracer.setGiven(ia);
	}
	
	void setIncorrect(InterfaceAttribute ia) {
		modelTracer.setIncorrect(ia);
	}
	
	/**
	 * Use this method to convert servlet sai to model tracer's sai
	 * @param sai SAI to convert to tracer's version of sai
	 * @return
	 */
	MTSAI makeTracerSai(SAI sai) {
		return new MTSAI(formatSelection(sai.getFirstSelection()), sai.getFirstAction(), sai.getFirstInput());
	}
	
	// for some reason, brd generated jess deftemplates use _ in the names
	// while the ctat components use . in their names
	// this is a temporary fix
	private String formatSelection(String selection) {
		return selection.replace('.', '_');
	}
	
	/**
	 * Initial sai's found in the brd are used to initialize the working memory
	 */
	@Override
	public void initializeInterfaceAttribute(InterfaceAttribute im) {
		modelTracer.initializeInterfaceAttribute(im);
	}
		
}
