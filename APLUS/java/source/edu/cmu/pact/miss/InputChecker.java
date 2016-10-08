/**
 * 
 */
package edu.cmu.pact.miss;

import java.awt.Component;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

/**
 * @author mazda
 *
 */
public abstract class InputChecker {

        /**
     * must be overridden
     */
    public abstract boolean checkInput(String selection, String input, String[] foa, BR_Controller brController);
    
    public abstract boolean checkVariables(String cellText1, String cellText2);
    
    public abstract String invalidInputMessage(String selection, String input, String[] foa);
    
    public abstract String invalidVariablesMessage(String cellText1, String cellText2);
    
    //Attempts to interpret an invalid input for a given selection into a valid one.
    //If it is able to change the input so that checkInput returns true, it should
    //return the new input.  If not, it should return null.
    public abstract String interpret(String selection, String input, String[] foa);
    
    public abstract String interpret(String selection, String input);
    
	public abstract Sai checkInputHighlightedWidget(Component comp);
			
	public abstract boolean checkSkipStep(String selection);
}
