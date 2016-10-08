package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

public abstract class TransformationPanel extends SolverPanel {
	//ImagePanel imagePanel;
	
	public TransformationPanel(PanelParameters parms) {
		super(parms);
		//int size = myFont.getSize()*4/3;
		//imagePanel = new ImagePanel(size, size);
		//add("West", imagePanel);
	}
	public abstract void setStep(String left, String right, String op, String arg);
	public abstract void setColor(Color theColor);
	public abstract void setFont(Font theFont);
	public abstract void displayCompletionMessage();
	
	public  void clear(){
		//setWarning(false);
		Component[] components = getComponents();
		int s = components.length;
		if(s==0) return;
		for(int i=0; i<s; i++){
			//if(!(components[i] instanceof ImagePanel))
			remove(components[i]);
		}
	}
	/**
	*	Not in use 
	**/
	/*	
	public void setWarning(boolean makeVisible){
		if(makeVisible){
			Image image = Settings.loadImage(this, "warning.gif");
			imagePanel.setImage(image);
			imagePanel.setPosition(5,0);
		}
		else
			imagePanel.clearPanel();
	}
	
	public void update(Graphics g){
		imagePanel.setLocation(5,0);
		super.update(g);
	}
	*/
}
