package TabbedTest;

import java.util.Hashtable;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import pact.CommWidgets.JCommButton;
import pact.CommWidgets.JCommWidget;
import TabbedTest.recover.JCommMultipleChoiceRecover;
import TabbedTest.recover.JCommRecover;
import TabbedTest.recover.JCommTextAreaRecover;
import TabbedTest.recover.JCommTextFieldRecover;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

public class TestTimeOut {

	private Hashtable<String, JCommWidget> interfaceElements;
	
	public TestTimeOut(Hashtable<String, JCommWidget> interfaceElements2){
		interfaceElements=new Hashtable<String, JCommWidget>();
		interfaceElements.putAll(interfaceElements2);	
		
		fillOutTest();
		
	}
	
	
	/**
	 * Method to fill out the test when timeout button is clicked
	 * Logic is to check if interface element is not selected, and if not then
	 * send the "TimeOut" value.
	 */
	public void fillOutTest(){
		
		Set<String> keys = interfaceElements.keySet();
		for(String key: keys){
			
			//trace.out("Value of "+key+" is: "+interfaceElements.get(key));
            
            if (interfaceElements.get(key) instanceof JCommMultipleChoiceRecover){
            	JCommMultipleChoiceRecover multipleChoice = (JCommMultipleChoiceRecover) interfaceElements.get(key);
            	if (multipleChoice.getSelectedChoiceIndex()==-1){          		
            		multipleChoice.choiceTexts.add("TimeOut");
            		multipleChoice.setNChoices(multipleChoice.choiceTexts.size());            		
            		multipleChoice.choiceTexts.set(multipleChoice.choiceTexts.size()-1 ,"TimeOut");
            		multipleChoice.recoverStudentAction("TimeOut");		
            	}
            		
			}
            if (interfaceElements.get(key) instanceof JCommTextFieldRecover){
            	JCommTextFieldRecover textField = (JCommTextFieldRecover) interfaceElements.get(key);
            	if (textField.getInputText().length()<1){
            		textField.recoverStudentAction("TimeOut");		
            	}
            		
			}
            else if (interfaceElements.get(key) instanceof JCommTextAreaRecover){
            	JCommTextAreaRecover textField = (JCommTextAreaRecover) interfaceElements.get(key);
            	if (textField.getInputText().length()<1){
            		textField.recoverStudentAction("TimeOut");		
            	}
            		
			}
            
        }
		
		JCommButton done=(JCommButton) interfaceElements.get("Done");
		done.doClick();
		
		
	}

}

