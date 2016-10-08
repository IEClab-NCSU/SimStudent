//TypeInSetter allows us to set typeIn mode

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

import edu.cmu.old_pact.cmu.uiwidgets.SolverFrame;
import edu.cmu.old_pact.cmu.uiwidgets.StackLayout;

public class TypeInSetter extends Frame {
	private Panel mainPanel;
	SolverFrame sf;
	
	public TypeInSetter(SolverFrame sf) {
		this.sf = sf;
		mainPanel = new Panel();
		add(mainPanel);
		mainPanel.setLayout(new StackLayout(2));
		Panel titlePanel = new Panel();
		titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		titlePanel.add(new Label("TypeIn Mode"));
		mainPanel.add(titlePanel);
		mainPanel.add(createTypeInPanel());
		
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
	
	private Component createTypeInPanel() {
		Panel typeInPanel = new Panel();
		typeInPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		Checkbox typeInType = new Checkbox("Use TypeIn Mode",SolverPanelFactory.getPanelType().equals("WebEq"));
		typeInType.setState(sf.getTypeInMode());
		typeInType.addItemListener (new ItemListener() {
										public void itemStateChanged(ItemEvent e) {
											if (e.getStateChange() == ItemEvent.SELECTED) {
												sf.setTypeinMode(true);
											}
											else 
												sf.setTypeinMode(false);
										}
									});
		typeInPanel.add(typeInType);
		return typeInPanel;
	}
}

