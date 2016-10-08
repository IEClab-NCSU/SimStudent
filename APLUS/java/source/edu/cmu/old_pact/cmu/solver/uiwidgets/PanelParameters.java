package edu.cmu.old_pact.cmu.solver.uiwidgets;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

//PanelParameters is a class which holds parameters used in the creation of
//panels by the SolverPanelFactory
//
//This class holds general parameters that apply to any panel
//If the panel needs other parameters, either subclass this or just pass them along

public class PanelParameters {
	protected int width;
	protected int height;
	protected Font font;
	protected Color foreColor;
	protected Color backColor;

	public PanelParameters(int w,int h,Font f, Color fore, Color back) {
		width = w;
		height = h;
		font = f;
		foreColor = fore;
		backColor = back;
	}
	
	public Dimension getSize() {
		return new Dimension(width,height);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int w) {
		width = w;
	}

	public int getHeight() {
		return height;
	}
	
	public void setHeight(int h) {
		height = h;
	}

	public Font getFont() {
		return font;
	}
	
	public void setFont(Font f) {
		font = f;
	}

	public Color getForeColor() {
		return foreColor;
	}

	public void setForeColor(Color f) {
		foreColor = f;
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color b) {
		backColor = b;
	}
}
