package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;

public class CommandLineOkCancelDialog extends CModalDialog {  
	
	TextField tf=null;
	Label promptLabel;
	
	public CommandLineOkCancelDialog(ModalDialogListener parent,String title,boolean modal) {
		super(parent,title,modal);
		init();
	}
	
	public CommandLineOkCancelDialog(ModalDialogListener parent,String title,boolean modal, String cmd) {
		super(parent,title,modal,cmd);
		init();
	}
	public CommandLineOkCancelDialog(ModalDialogListener parent,
									String title,boolean modal, 
									String cmd, String text) {
		this(parent,title,modal,cmd);
		tf.setText(text);
	}
	
	private void init() {
		setLayout(new GridBagLayout());
		
		//setBackground(new Color(204, 204, 204));
		setBackground(Color.lightGray);
		tf = WidgetFactory.makeTextField(15);
		
		promptLabel = WidgetFactory.makeLabel("Enter expression:");
		GridbagCon.viewset(this,promptLabel,0,0,1,1,20,25,0,0);
		GridbagCon.viewset(this,tf,1,0,2,1,20,0,0,25); 
		
		Panel bottom = WidgetFactory.okCancelPanel();
		GridbagCon.viewset(this,bottom,1,1,2,1,20,0,15,25);
				
		pack();
		//setVisible(true);
		
	}
	
	public void setPromptLabel(String l){
		promptLabel.setText(l);
	}
	
	public void finishModalDialog(boolean key)
	{
		if((key && !(tf.getText().trim()).equals("")) || !key)
			super.finishModalDialog(key);
	}
	
	public void setVisible(boolean v){
		super.setVisible(v);
		if(v)
			tf.requestFocus();
	}
	
	public String getText()
	{
		return tf.getText();
	}
	
	public String getArgument() {
		return getText();
	}
}	
	
