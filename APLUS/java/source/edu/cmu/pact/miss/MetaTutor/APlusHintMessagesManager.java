package edu.cmu.pact.miss.MetaTutor;

import java.util.ArrayList;

import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;

/**
 * This class is responsible for the messages that are displayed in the Hint dialog window.
 * It keeps track of which message is being displayed, and the messages before and after the
 * current message.
 */
public class APlusHintMessagesManager {

	/** Message type */
	protected String type;

	/**	HTML format hint messages to display, each element is a string */
	private ArrayList<String> messages;
	
	/** Currently displayed hint message  */
	private String currentMessage;
	
	/**	Maximum hint message index = number of messages - 1 */
	private int maxMessageIndex;
	
	/**	Index of currently displaying message */
	private int currentMessageIndex;
	
	/** Hint dialog window for the APlus environment */
	private APlusHintDialogInterface diagInterface;
	
	public APlusHintDialogInterface getDiagInterface() {
		return diagInterface;
	}

	public void setDiagInterface(APlusHintDialogInterface diagInterface) {
		this.diagInterface = diagInterface;
	}

	/**
	 * @param controller
	 */
	public APlusHintMessagesManager(CTAT_Launcher server){
		reset();
	}
	
	public void reset(){
		
		maxMessageIndex = -1;
		currentMessageIndex = -1;
		type = "";
		messages = new ArrayList<String>();
		currentMessage = "";
		return;
	}
	
	public boolean hasPreviousMessage(){
		return (currentMessageIndex > 0);
	}
	
	public boolean hasNextMessage(){
		return (currentMessageIndex < maxMessageIndex);
	}
	
	public String getPreviousMessage(){
		
		if(this.hasPreviousMessage()){
			currentMessageIndex--;
			currentMessage = messages.get(currentMessageIndex);
			return currentMessage;
		}
		return null;
	}
	
	public String getFirstMessage(){
		
		if(maxMessageIndex >= 0){
			currentMessageIndex = 0;
			currentMessage = messages.get(currentMessageIndex);
			return currentMessage;
		}
		return null;
	}
	
	public String getNextMessage(){
	
		if(this.hasNextMessage()){
			currentMessageIndex++;
			currentMessage = messages.get(currentMessageIndex);
			return currentMessage;
		}
		return null;
	}
	
	public String getMessageType(){
		return this.type;
	}
	
	public void dialogCloseCleanUp(){
		reset();
		return;
	}
	
	public void setMessages(ArrayList<String> msgs){
		
		messages = msgs;
		maxMessageIndex = messages.size() - 1;
		currentMessageIndex = -1;
		return;
	}
	
}
