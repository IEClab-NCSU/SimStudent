package edu.cmu.old_pact.cmu.solver.uiwidgets;


import java.awt.Color;
import java.awt.Panel;

public abstract class EquationPanel extends SolverPanel {
	
	public EquationPanel(PanelParameters parms) {
		super(parms);
	}
	public abstract void setEquation(String left,String right);
	public abstract void alignWith(Panel thepanel);////
	public abstract void initTypein();
	public abstract void setTypeinOK(String side,boolean ok);
	public abstract void setTypeinSideText(String side, String expression);
	public abstract void setTypeinSideColor(String side, Color color);
	public abstract int getStepState();
	public abstract boolean getUseTypein();
	public abstract String getTypeInString();
}
