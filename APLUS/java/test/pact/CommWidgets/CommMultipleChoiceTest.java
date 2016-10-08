package pact.CommWidgets;


import java.io.File;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import pact.CommWidgets.JCommMultipleChoice.StartUpdateDialog;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.OpenInterfaceDialog;
import edu.cmu.pact.BehaviorRecorder.View.BRPanel;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.MessageObject;

public class CommMultipleChoiceTest extends TestCase {
	JCommMultipleChoice commMultipleChoice;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		commMultipleChoice = new JCommMultipleChoice();	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		commMultipleChoice = null;
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(CommMultipleChoiceTest.class);
		
		return suite;
	}
	
	public void testPropertiesSetting() {
		String questionText = "testing question text";
		String choiceTexts = "zzChoice1,zzChoice2,zzChoice3";
		int NChoice = 3;
		
		commMultipleChoice.setQuestionText(questionText);
		commMultipleChoice.setChoiceTexts(choiceTexts);
		commMultipleChoice.setNChoices(NChoice);
		
		MessageObject  o = commMultipleChoice.formDescriptionMessage();
		commMultipleChoice.doInterfaceDescription(o);
		trace.out("dw", "InterfaceDescription:\n  "+o.toXML());

		assertEquals(questionText, commMultipleChoice.getQuestionText());
		assertEquals(choiceTexts, commMultipleChoice.getChoiceTexts());
		assertEquals(NChoice, commMultipleChoice.getNChoices());
	}
	
	
}
