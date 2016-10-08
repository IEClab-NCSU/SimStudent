//DisplaySetter allows us to set display parameters

package edu.cmu.old_pact.cmu.solver.uiwidgets;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import edu.cmu.old_pact.cmu.uiwidgets.StackLayout;

public class DisplaySetter extends Frame {
	private Panel mainPanel;
	
	public DisplaySetter() {
		mainPanel = new Panel();
		add(mainPanel);
		//mainPanel.setLayout(new StackLayout(10));
		mainPanel.setLayout(new StackLayout(2));
		Panel titlePanel = new Panel();
		titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		titlePanel.add(new Label("Display Parameters"));
		mainPanel.add(titlePanel);
		mainPanel.add(createDisplayPanel());
		
		Button okButton = new Button("OK");
		okButton.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											hide();
											dispose();
										}
									});
		Panel okPanel = new Panel();
		okPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		okPanel.add(okButton);
		mainPanel.add(okPanel);
		mainPanel.resize(300,200);
		pack();
		setLocation(50,50);

	}
	
	private Component createDisplayPanel() {
		Panel displayPanel = new Panel();
		displayPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		Checkbox displayType = new Checkbox("Use WebEq?",SolverPanelFactory.getPanelType().equals("WebEq"));
		displayType.addItemListener (new ItemListener() {
										public void itemStateChanged(ItemEvent e) {
											if (e.getStateChange() == ItemEvent.SELECTED) {
												SolverPanelFactory.setPanelType("WebEq");
											}
											else {
												SolverPanelFactory.setPanelType("Plain");
											}
										}
									});
		displayPanel.add(displayType);
		return displayPanel;
	}
}

