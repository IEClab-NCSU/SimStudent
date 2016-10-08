package edu.cmu.old_pact.toolframe;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;

import edu.cmu.old_pact.doublebufferedpanel.BackgroundImagePanel;
import edu.cmu.old_pact.doublebufferedpanel.DoubleBufferedPanel;
import edu.cmu.old_pact.settings.Settings;

public class TextFieldPanelFactory extends Panel{

	public static Panel getGridPanel(Component com){
		Panel gridPanel = new Panel();
		gridPanel.setLayout(null);
		gridPanel.add(com);
		com.setLocation(0,0);
		gridPanel.setSize(com.preferredSize());
		return gridPanel;
	}
	
	public static Panel getLabeledPanel(String labelS, Component com){
		Panel toret = new Panel();
		toret.setLayout(new FlowLayout(0));
		Label label = new Label(labelS);
		label.setFont(Settings.factoringLabelFont);
		toret.add(label);
		toret.add(com);
		return toret;
	}
	
	public static Panel getDoubleBufferedPanel(String backImage){
		DoubleBufferedPanel dbp = new DoubleBufferedPanel();
		BackgroundImagePanel backImagePanel = new BackgroundImagePanel();
		Image image = Settings.loadImage(backImagePanel, backImage);
		backImagePanel.setImage(image);
		dbp.add(backImagePanel);
		return dbp;
	}
}