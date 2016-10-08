package TabbedTest.recover;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.awt.Color;
import TabbedTest.TabbedTestA;
import TabbedTest.recover.JCommTextAreaRecover;
import pact.CommWidgets.JCommTextArea;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.LoadFileDialog;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.miss.WebStartFileDownloader;
import edu.cmu.pact.miss.storage.StorageClient;
import pact.CommWidgets.JCommMultipleChoice;


public class JCommMultipleChoiceRecover extends JCommMultipleChoice implements JCommRecover {

	private RecoverFileHandler fileHandler;
	private void setFileHandler(RecoverFileHandler _fileHander){this.fileHandler=_fileHander;}
	private RecoverFileHandler getFileHandler(){return this.fileHandler;}
	
	/***
	 * Constructor
	 */
	public JCommMultipleChoiceRecover(BR_Controller brController){	
		super();
		setFileHandler(new RecoverFileHandler(brController));
	}

	/**
	 * Method to get the actual input text for this interface element
	 * @return
	 */
	public String getInputText(){
		return (String) this.choiceTexts.get(this.getSelectedChoiceIndex());

	}
	
	
	/**
	 * Method to set the current choice
	 * @param choiceText
	 */
	public synchronized void recoverStudentAction(String input) {
		
			
		this.doAction(input, this.correctColor, this.correctFont);
		//this.doInterfaceAction(this.getCommName(), "UpdateMultipleChoice", input);

		// send the selection action input to the behavior recorder and/or production system
		if(!locked){			
			dirty = true;
			sendValue();
		}

	}

	public JCommTextAreaRecover textBox=null;
	/***
	 * Overriden method to save student action to recover file once student selects a radio button
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (this.getInputText().equals("NotSure") && textBox!=null){
			//JOptionPane.showMessageDialog(null,textBox.getCommName());
			/*textBox.doInterfaceAction(textBox.getCommName(), "UpdateTextArea", "Not Sure123");		
			// send the selection action input to the behavior recorder and/or production system
			if(!locked){			
				dirty = true;
				sendValue();
			}*/
			 textBox.recoverStudentAction("Not Sure");
	
			 textBox.setForeground(Color.LIGHT_GRAY);
             textBox.setBackground(Color.LIGHT_GRAY);
             textBox.setFocusable(false);
             textBox.setText(".");
			}
		else {
			if (textBox!=null){
				textBox.setForeground(Color.BLACK);
             	textBox.setBackground(Color.WHITE);
        		textBox.setFocusable(true);
        		textBox.setText("");
        	}
		}	
		
		this.getFileHandler().saveStudentActionToFile(this.getCommName(), "UpdateMultipleChoice",this.getInputText());
		//SOS : the following line sends everything to CTAT.
	//	super.actionPerformed(ae);
	}

}
