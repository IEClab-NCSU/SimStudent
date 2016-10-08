package edu.cmu.old_pact.cmu.uiwidgets;

import java.awt.Choice;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Panel;

import edu.cmu.old_pact.cmu.messageInterface.GridbagCon;


public class ChoiceDialogOkCancel extends CModalDialog {
	Choice ch;
	String[] items = {"Both sides","Left side","Right side"};
	String[] messages = {"both","left","right"};
	
	public ChoiceDialogOkCancel(ModalDialogListener parent,String title,boolean modal) {
		super(parent,title,modal);
		init();
	}
	
	public ChoiceDialogOkCancel(ModalDialogListener parent,String title,boolean modal, String cmd) {
		super(parent,title,modal,cmd);
		init();
	}

		
	private void init() {
		setLayout(new GridBagLayout());

		setBackground(new Color(204, 204, 204));
		ch = WidgetFactory.makeChoice(items);
		
		GridbagCon.viewset(this,WidgetFactory.makeLabel("Apply to:"), 0,0,1,1,25,25,0,0);
		GridbagCon.viewset(this,ch,1,0,2,1,25,0,0,25); 
				
		Panel bottom = WidgetFactory.okCancelPanel();
		GridbagCon.viewset(this,bottom,1,1,2,1,25,0,15,25);
				
		pack();
		//setVisible(true);
	}

	int getSelectedIndex()
	{
		return ch.getSelectedIndex();
	}
	
	public String getArgument() {
		return messages[getSelectedIndex()];
	}
	
	public void show()
	{
		super.show();
	}	

}
