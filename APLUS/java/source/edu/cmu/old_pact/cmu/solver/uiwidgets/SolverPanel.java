package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Panel;

//SolverPanel is a subclass of panel that stores display information about the
//objects that go in the panel

public class SolverPanel extends Panel {
	protected int myWidth;
	protected int myHeight;
	protected Font myFont;
	protected Color myForeColor;
	protected Color myBackColor;
	protected PanelParameters parms;
	
	public SolverPanel(PanelParameters parms) {
		this.parms = parms;
		setParams(parms);
	}

	public PanelParameters getParams(){
		return parms;
	}

	public void setParams(PanelParameters params){
		myWidth = params.getWidth();
		myHeight = params.getHeight();
		setSize(myWidth,myHeight);
		myFont = params.getFont();
		myForeColor = params.getForeColor();
		myBackColor = params.getBackColor();
	}
	
	public void clear(){
		removeAll();
	}
	
	public void setForeColor(Color c){
		myForeColor = c;
	}

	public void setSize(Dimension d){
		super.setSize(d);
		myWidth = d.width;
		myHeight = d.height;
	}
}
