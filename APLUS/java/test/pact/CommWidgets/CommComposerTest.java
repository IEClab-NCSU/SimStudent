package pact.CommWidgets;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.BehaviorRecorder.Controller.SingleSessionLauncher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class CommComposerTest extends TestCase {
	JCommComposer commComposer;
	
	protected void setUp() throws Exception {
		super.setUp();
		BR_Controller controller = new CTAT_Launcher(new String[0]).getFocusedController();
        controller.initAllWidgets_movedFromCommWidget();
		commComposer = new JCommComposer();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		commComposer = null;
	}

	public void testPropertiesSetting() {
		int comboBoxNumber = 5;
		String comboBoxesvalues[] = new String[comboBoxNumber];
		
		comboBoxesvalues[0] = "aaaaaa, bbbbbbb";
		comboBoxesvalues[1] = "dddddddddd, eeeeeeee";
		comboBoxesvalues[2] = "qqqqqqq, pppppppp";
		comboBoxesvalues[3] = "xxxxxxx, yyyyyyy, zzzzzzz";
		comboBoxesvalues[4] = "ssssss";
		
		String itemValuesString = "";
		
		for (int i=0; i<comboBoxNumber - 1; i++)
			itemValuesString += comboBoxesvalues[i] + ";" ;
		
		itemValuesString += comboBoxesvalues[comboBoxNumber - 1];
		
		commComposer.setComboBoxesNumber(comboBoxNumber);
		commComposer.setItemValues(itemValuesString);
		
		MessageObject  o = commComposer.formDescriptionMessage();
		commComposer.doInterfaceDescription(o);
		
		assertEquals(comboBoxNumber, commComposer.getComboBoxesNumber());
			
		String tempString;
		for (int i=0; i<comboBoxNumber; i++) {
			tempString = commComposer.getValues(i);
			assertEquals(comboBoxesvalues[i], tempString);
		}
		return;
	}
	
	public static Test suite() {
        return new TestSuite(CommComposerTest.class);
	}
	
	public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
        
        System.exit(0);
	}
}
