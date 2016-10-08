/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.cmu.pact.ctatview.wizardframe;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


/**
 * @author mpschnei
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class WizardDialog extends JFrame implements ActionListener {
	
	private JButton nextButton;

	private JButton previousButton;

	private JButton cancelButton;

	private JPanel topPanel = new JPanel();

	private JButton finishButton;

	private HashMap panelTable = new HashMap();

	private WizardPanelInterface currentPanel;

	private String previousPanelName;

	private String currentPanelName;
	
	public WizardDialog () {
		
		
		nextButton = new JButton("Next-->");
		previousButton = new JButton("<--Previous");
		finishButton = new JButton("Finish");
		cancelButton = new JButton("Cancel");
		
		JPanel temp = new JPanel(new BorderLayout());
		Box box = new Box(BoxLayout.LINE_AXIS);
		box.add(cancelButton);
		box.add(Box.createHorizontalStrut(15));
		
		box.add(previousButton);
		box.add(Box.createHorizontalStrut(5));
		box.add(nextButton);
		box.add(Box.createHorizontalStrut(15));
		box.add(finishButton);
		box.add(Box.createHorizontalStrut(5));
		box.setBorder(new EmptyBorder(5, 5, 5, 5));

		
		temp.add(box, BorderLayout.EAST);
		//topPanel.add(temp, BorderLayout.SOUTH);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(temp, BorderLayout.SOUTH);

		topPanel.setLayout (new BorderLayout());
		topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(topPanel, BorderLayout.CENTER);
		
		nextButton.addActionListener (this);
		previousButton.addActionListener(this);
		cancelButton.addActionListener (this);
		finishButton.addActionListener(this);
		
		pack();
	}

	/**
	 * @param nextPanel
	 */
	public void setCurrentPanel(String name) {
		currentPanelName = name;
		currentPanel = getPanel(name);
		JPanel p = currentPanel.getJPanel();
		topPanel.removeAll();
		topPanel.add(p);
		validate();
		repaint();
		updateButtonState();
	}

	public String getCurrentPanelName() {
		return currentPanelName;
	}
	
	public void updateButtonState() {
		previousButton.setEnabled(currentPanel.getPreviousButtonState());
		nextButton.setEnabled(currentPanel.getNextButtonState());
		cancelButton.setEnabled (currentPanel.getCancelButtonState());
		finishButton.setEnabled (currentPanel.getFinishButtonState());
	}

	/**
	 * @param panel1
	 */
	protected void addPanel(WizardPanelInterface panel1) {
		panelTable.put (panel1.getPanelName(), panel1);
		validate();
		repaint();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		
		if (source == cancelButton) {
			cancelButtonPressed();
			return;
		}
		 
		if (source == nextButton) {
			previousPanelName = currentPanel.getPanelName();
			setCurrentPanel (currentPanel.getNextPanelName());
			return;
		}
		
		if (source == previousButton) {
			
			
			setCurrentPanel (previousPanelName);
			
			return;
		}
		
		if (source == finishButton) {
			finishButtonPressed();
		}
	}


	/**
	 * 
	 */
	public void finishButtonPressed() {
		
	}

	/**
	 * 
	 */
	public void cancelButtonPressed() {
		
	}

	
	/**
	 * @param string
	 * @return
	 */
	private WizardPanelInterface getPanel(String panelName) {
		return (WizardPanelInterface) panelTable.get(panelName);
	}
	
	
}