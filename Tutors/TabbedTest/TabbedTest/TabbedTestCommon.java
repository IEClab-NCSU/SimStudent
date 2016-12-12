package TabbedTest;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import TabbedTest.recover.JCommMultipleChoiceRecover;
import TabbedTest.recover.JCommTextAreaRecover;
import TabbedTest.recover.JCommTextFieldRecover;
import TabbedTest.recover.TestRecover;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;

public class TabbedTestCommon extends javax.swing.JPanel {

	public Hashtable<String, Object> interfaceElements;
	/**
	 * Method that is responsible for two things
	 * 1. naming all the interface elements so that test is compliant to latest ctat
	 * 2. populating the interface elements list, so we can recover a test.
	 * @param obj
	 */
	protected Hashtable<String, Object> initializeCommonCode(Object obj){
        interfaceElements=new Hashtable();

		for (Field field : obj.getClass().getDeclaredFields()) {
			field.setAccessible(true); // You might want to set modifier to public first.
			Object value=null;
			try {
				value = field.get(obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
			if (value != null) {
				if (value instanceof JCommMultipleChoiceRecover)  {   		
					JCommMultipleChoiceRecover multipleChoice=(JCommMultipleChoiceRecover) value;
					multipleChoice.setCommName(field.getName());	        		
					interfaceElements.put(field.getName(),multipleChoice);
				}
				else if (value instanceof JCommTextAreaRecover)  {   		
					JCommTextAreaRecover textArea=(JCommTextAreaRecover) value;
					textArea.setCommName(field.getName());	        		
					interfaceElements.put(field.getName(),textArea);
				}

				else if (value instanceof JCommTextFieldRecover)  {   		
					JCommTextFieldRecover textArea=(JCommTextFieldRecover) value;
					textArea.setCommName(field.getName());	        		
					interfaceElements.put(field.getName(),textArea);
				}
			}
		}

			return interfaceElements;
	}










}
