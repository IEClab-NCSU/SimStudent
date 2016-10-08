package rmconnective;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.miss.InputChecker;

public abstract class MyInputChecker extends InputChecker {
    public boolean checkInput(String selection, String input, String[] foa, BR_Controller brController)
    {
    	return (PLParserWrapper.parse(input) != null);	
    }
    public String invalidInputMessage(String selection, String input, String[] foa)
    {
		return "Wrong syntax in propositional logic! Please try again!";
    	
    }

}
