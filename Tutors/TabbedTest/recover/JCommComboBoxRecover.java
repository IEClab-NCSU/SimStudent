package TabbedTest.recover;

import java.awt.event.ActionEvent;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import pact.CommWidgets.JCommComboBox;

public class JCommComboBoxRecover extends JCommComboBox implements JCommRecover{

	private RecoverFileHandler fileHandler;
	private void setFileHandler(RecoverFileHandler _fileHander){this.fileHandler=_fileHander;}
	private RecoverFileHandler getFileHandler(){return this.fileHandler;}
	
	public JCommComboBoxRecover(BR_Controller brController){
		super();
		setFileHandler(new RecoverFileHandler(brController));
	}
	
	
	
	@Override
	public void recoverStudentAction(String input) {
		this.doInterfaceAction(this.getCommName(), "UpdateComboBox", input);
		//this.doInterfaceAction(this.getCommName(), "UpdateMultipleChoice", input);
		// send the selection action input to the behavior recorder and/or production system
		if(!locked){			
			dirty = true;
			sendValue();
		}
	}
	
	

	/***
	 * Overriden method to save student action to recover file once student selects a radio button
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		super.actionPerformed(ae);
		//trace.err("NOT SAVING value is " + (String) this.getValue());
		this.getFileHandler().saveStudentActionToFile(this.getCommName(), "UpdateComboBox",this.getValue().toString());
	
	}
	
	
}
