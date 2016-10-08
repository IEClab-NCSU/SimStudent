package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.cmu.old_pact.cmu.uiwidgets.CModalDialog;
import edu.cmu.old_pact.cmu.uiwidgets.ChoiceDialogOkCancel;
import edu.cmu.old_pact.cmu.uiwidgets.CommandLineOkCancelDialog;
import edu.cmu.old_pact.cmu.uiwidgets.EquationDialog;
import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.cmu.uiwidgets.SolverMenu;
import edu.cmu.old_pact.dormin.DorminException;
import edu.cmu.old_pact.dormin.menu.DorminMenuItem;

//SolverMenuItem is an extended menu item that stores an internal name (so the
//surface name of the menu item can change without changing the action passed to
//the tutor)
public class SolverMenuItem extends DorminMenuItem implements ActionListener{
	private String actionName;
	int itemAction;
	private String prompt = "Enter value:";
	private String operationName  = "";
	
	public static final int GetStringArg = 0;
	public static final int GetSide = 1;
	public static final int NoArgs = 2;
		
	SolverMenu sMenu;
		
	public SolverMenuItem(){
		super();
		addActionListener(this);
	}
	
	public void setMenu(SolverMenu sMenu){
		this.sMenu = sMenu;
	}
	
	public void delete(){
	    removeActionListener(this);
	    sMenu = null;
	    super.delete();
	}
	
	public void setProperty(String propertyName, Object propertyValue) throws DorminException{
		try{
		if(propertyName.equalsIgnoreCase("NAME")){
			actionName = ((String)propertyValue).toLowerCase();
			setOtherParam();
			super.setProperty(propertyName, propertyValue);
		}
		else
			super.setProperty(propertyName, propertyValue);
		} catch(DorminException e) {
				throw e;
		}
	}
	
	private void setOtherParam(){
	//itemAction, isOperation, prompt
		//String[] other= sMenu.getOtherParam(actionName);
		//itemAction = Integer.parseInt(other[0]);
		//if(other[1].equalsIgnoreCase("True"))
		//	sMenu.addOperationItem(this);
		//prompt = other[2];
	}

	public String getInternalName() {
		return actionName;
	}
	
	public int getActionType() {
		return itemAction;
	}
	
	public String getPrompt(){
		return prompt;
	}

	//the SolverMenuItem acts as its own listener
	public void actionPerformed (ActionEvent event) {
		SolverMenuItem source = (SolverMenuItem)(event.getSource());
		int responseType = source.getActionType();
		
		if (responseType == GetStringArg) {
                    if((source.getInternalName()).equalsIgnoreCase("NEW")){
                        EquationDialog dlog = new EquationDialog((SolverFrame)getFrame(),
                                                                 prompt,true,actionName);
                        dlog.setPromptLabel("Enter equation:");
                        dlog.show();
                    }
                    else{
			CommandLineOkCancelDialog dlog = new CommandLineOkCancelDialog((SolverFrame)getFrame(),
                                                                                       prompt,true,actionName);
			dlog.show();
                    }
		}
		else if (responseType == GetSide) {
			CModalDialog dlog = new ChoiceDialogOkCancel((SolverFrame)getFrame(),prompt,true,actionName);
			dlog.show();
		}
		else if (responseType == NoArgs) {
			((SolverFrame)getFrame()).performAction(actionName,null);
		}
	}
}
