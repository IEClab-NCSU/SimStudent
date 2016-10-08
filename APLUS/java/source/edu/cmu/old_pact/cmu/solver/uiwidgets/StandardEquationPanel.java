package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Label;

import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;

public abstract class StandardEquationPanel extends EquationPanel {
	protected boolean useTypein;
	
	//states for typein buttons
    protected final static int TYPEIN_INITIAL = 0;   //initial state of button
    protected final static int TYPEIN_FLAGGED = 1;   //button is flagged
    protected final static int TYPEIN_OK = 2;        //user has entered correct value, but button hasn't been removed yet
    protected final static int TYPEIN_CONFIRMED = 2; //side is completed and unchangable
    
    protected int leftTypeinStatus = TYPEIN_INITIAL;
    protected int rightTypeinStatus = TYPEIN_INITIAL;
    
    //protected int stepState = false;			//used in typein mode, = true if both sides are done.   
    
    
	public StandardEquationPanel(PanelParameters parms,boolean typein) {
		super(parms);
		useTypein = typein;
		setLayout(new FlowLayout());
		if (useTypein && SolverFrame.getSelf().getCurrentStepNumber() != 0) {
			initTypein();
			add(new TypeinButton("Left",SolverFrame.getSelf()));
			add(new Label("="));
			add(new TypeinButton("Right",SolverFrame.getSelf()));
			SolverFrame.getSelf().disableMenuOperations();
		}
	}
	
	public void setTypeinSideColor(String side, Color color) {
		getTypeinButton(side).setForeground(color);
	}
	
	public int getStepState(){
		if( !useTypein )
			return SolverFrame.STEPCOMPLETED;
		else {
			int toret = SolverFrame.LEFTNOTSET;
			if (leftTypeinStatus == TYPEIN_CONFIRMED || leftTypeinStatus == TYPEIN_OK){
				toret = SolverFrame.RIGHTNOTSET;
				if(rightTypeinStatus == TYPEIN_CONFIRMED || rightTypeinStatus == TYPEIN_OK)
					toret = SolverFrame.STEPCOMPLETED;
			}
			return toret;
		}
	}

	public void initTypein() {
		leftTypeinStatus = TYPEIN_INITIAL;
		rightTypeinStatus = TYPEIN_INITIAL;
	}
	
	public void setTypeinOK (String side, boolean OK) {
		if(OK){
			if(side.equalsIgnoreCase("left")) 
				leftTypeinStatus = TYPEIN_OK;
			else
				rightTypeinStatus = TYPEIN_OK;
		}
		else{
			if(side.equalsIgnoreCase("left")) 
				leftTypeinStatus = TYPEIN_FLAGGED;
			else
				rightTypeinStatus = TYPEIN_FLAGGED;
		}
	}
	
	private int getSideStatus(String side) {
		if (side.equalsIgnoreCase("left"))
			return leftTypeinStatus;
		else
			return rightTypeinStatus;
	}

	private Button getTypeinButton(String side) {
		Component theButton = null;
		Component[] comp = getComponents();
		int s = comp.length;
		for(int i=0; i<s; i++){
			if(comp[i] instanceof TypeinButton && 
			((TypeinButton)comp[i]).getName().equalsIgnoreCase(side))
				return (Button)comp[i];
		}
		
		return (Button)theButton;
	}

	public void setNextEquation(String eq){
		SolverFrame.getSelf().setNextEquation(eq);
	}

	//setting the text of a typein button has the side-effect of, if appropriate, removing the button
	//(or even both buttons).
	public void setTypeinSideText(String side, String expression) {
		if (getSideStatus(side) == TYPEIN_OK){
			replaceButtonWithEquation(side,expression);
		}
		else {
			Button tin = getTypeinButton(side);
			tin.setLabel(expression);
		}
	}
	
	protected abstract void replaceButtonWithEquation(String side, String exp);

	public String getTypeInString(){
		return null;
	}
	
	public boolean getUseTypein(){
		return useTypein;
	}
}
