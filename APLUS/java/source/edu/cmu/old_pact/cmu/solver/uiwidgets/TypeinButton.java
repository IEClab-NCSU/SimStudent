package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.uiwidgets.CModalDialog;
import edu.cmu.old_pact.cmu.uiwidgets.CommandLineOkCancelDialog;
import edu.cmu.old_pact.cmu.uiwidgets.ModalDialogListener;

public class TypeinButton extends ResizableButton implements ActionListener {
	private ModalDialogListener eventHandler;
	String myName;
	
	public TypeinButton(String name, ModalDialogListener sf) {
		super(name);
		setForeground(Color.black);
		eventHandler = sf;
		myName = name;
		addActionListener(this); //listen to myself
	}
	
	public void actionPerformed (ActionEvent event) {
		String prompt = "Enter the new "+myName+" side:";
		CModalDialog dlog;
		if(myName.equals(getLabel())){
			dlog = new CommandLineOkCancelDialog(eventHandler,prompt,true,myName);
		}
		else{
			dlog = new CommandLineOkCancelDialog(eventHandler,prompt,true,myName,getLabel());
		}
		dlog.show();
	}
	public String getName(){
		return myName;
	}
}
