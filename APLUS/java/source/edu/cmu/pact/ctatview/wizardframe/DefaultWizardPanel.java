/*
 * Created on Oct 12, 2004
 *
 */
package edu.cmu.pact.ctatview.wizardframe;

import java.awt.BorderLayout;

import javax.swing.JPanel;


/**
 * @author mpschnei
 * 
 */
public abstract class DefaultWizardPanel extends JPanel implements WizardPanelInterface {

	protected JPanel contentPanel = new JPanel();


	protected WizardDialog parent;


	private String panelName;
	
	public DefaultWizardPanel(WizardDialog parent, String panelName) {
		this.parent = parent;
		JPanel topPanel = new JPanel(new BorderLayout());
		
		this.panelName = panelName;
	}

	
	
	public JPanel getJPanel() {
		return this;
	}

	public String getPanelName() {
		return panelName;
	}
}