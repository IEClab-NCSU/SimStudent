package TabbedTest.recover;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;
import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.storage.StorageClient;
import pact.CommWidgets.JCommDocument;
import pact.CommWidgets.JCommTextField;
import pact.CommWidgets.JCommWidget;

public class JCommTextFieldRecover extends JCommTextField implements JCommRecover {
	
	
	private RecoverFileHandler fileHandler;
	private void setFileHandler(RecoverFileHandler _fileHander){this.fileHandler=_fileHander;}
	private RecoverFileHandler getFileHandler(){return this.fileHandler;}
	
	/***
	 * Constructor
	 */
	public JCommTextFieldRecover(BR_Controller brController){
		super();			
		setFileHandler(new RecoverFileHandler(brController));
	}	
	
	/***
	 * Overriden method to save student action to recover file once focus is lost
	 */
	@Override
	public void focusLost(FocusEvent e) {

		if (getInputText().length()>0){	
			this.getFileHandler().saveStudentActionToFile(this.getCommName(), "UpdateTextField", this.getInputText());
		}
		//SOS : the following line sends everything to CTAT.
		//super.focusLost(e);
	}

	/**
	 * Method to get the actual input text for this interface element
	 * @return
	 */
	public String getInputText(){
		return (String) this.getText();

	}


	/**
	 * Method to set the current choice
	 * @param choiceText
	 */
	public synchronized void recoverStudentAction(String input) {
		this.doInterfaceAction(this.getCommName(), "UpdateTextField", input);		
		// send the selection action input to the behavior recorder and/or production system
		if(!locked){			
			dirty = true;
			sendValue();
		}

	}

/**
	 * Method to set the current choice
	 * @param choiceText
	 */
	public synchronized void setNotSureText(String input) {
		this.setForeground(Color.GRAY);
		this.doInterfaceAction(this.getCommName(), "UpdateTextField", input);		
		this.setForeground(Color.GRAY);
		// send the selection action input to the behavior recorder and/or production system
		if(!locked){			
			dirty = true;
			sendValue();
		}

	}

}
