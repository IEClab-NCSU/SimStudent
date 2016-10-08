package edu.cmu.old_pact.cmu.uiwidgets;
import java.awt.event.ActionEvent;

public class ModalDialogEvent extends ActionEvent {
	private boolean pickedOK;
	private String commandArg;
	
	public static final int DIALOG_DONE = 99;
	
	public ModalDialogEvent(Object parent,String command, String arg, boolean isOK) {
		super(parent,DIALOG_DONE,command);
		pickedOK = isOK;
		commandArg = arg;
	}
	
	public boolean isOK() {
		return pickedOK;
	}
	
	public String getArgument() {
		return commandArg;
	}
}
