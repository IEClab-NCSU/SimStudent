package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.ViewUtils;

public class RuleNamesDisplayDialog extends JDialog implements ActionListener {
    private JPanel contentPanel = new JPanel();
    private JPanel listJPanel = new JPanel();
	private JPanel closeJPanel = new JPanel();
    private JList ruleList;
    private JScrollPane listScrollPane;
    private DefaultListModel listModel;
	private JButton closeJButton = new JButton("Close");

	private BR_Controller controller;

	public RuleNamesDisplayDialog(final BR_Controller controller) {
		this(controller, true);
	}
	
	/**
	 * Create the Dialog.
	 * @param controller
	 * @param visible true if should be visible on instantiation.
	 */
	public RuleNamesDisplayDialog(final BR_Controller controller, boolean visible) {
		super(controller.getActiveWindow(), "Skill Names...", false);
		this.controller = controller;
		contentPanel.setLayout(new BorderLayout());
		ViewUtils.setStandardBorder(contentPanel);
		listJPanel.setLayout(new GridLayout(1, 1));
        listModel = new DefaultListModel();
        ruleList = new JList(listModel);
        ruleList.setName("ruleList");
        ruleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ruleList.setSelectedIndex(-1);
        listScrollPane = new JScrollPane(ruleList);
        listJPanel.add(listScrollPane);
		closeJPanel.setLayout(new FlowLayout());
		closeJPanel.add(closeJButton);
		contentPanel.add(listJPanel, BorderLayout.CENTER);
		contentPanel.add(closeJPanel, BorderLayout.SOUTH);
		getContentPane().add(contentPanel);
		
        setLocationRelativeTo(null);
		setSize(300, 400);

        setRuleProductionList(visible);
        
		closeJButton.addActionListener(this);
		addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				setVisible(false);
			}
		});

	}
	
	/**
	 * Recreate the {@link #ruleList}.
	 * @param visible whether to make it visible
	 */
	private void setRuleProductionList(boolean visible) {
		trace.out ("controller = " + controller);
		List<String> skillNames =
				controller.getRuleProductionCatalog().getRuleDisplayNames(true);
		int stringsNumber = skillNames.size();
        
		if (stringsNumber > 0) {
	        ruleList.setVisibleRowCount(Math.min(stringsNumber, 10));
			String allSkills[] = new String[stringsNumber];
			skillNames.toArray(allSkills);
			Arrays.sort(allSkills);		// sort skills string array
	        for (int i = 0; i < stringsNumber; i++)
	             listModel.addElement(allSkills[i]);

		} else {
            listModel.addElement("No skill names currently defined.");
		}

		if (visible) {
		    listScrollPane.setVisible(visible);
		    setVisible(visible);
		}
			
	}


	/**
	 * Clear {@link #listModel} and rebuild. Calls {@link #setRuleProductionList(boolean)}.
	 * @param visible
	 */
	public void resetRuleProductionList(boolean visible) {
	    listModel.removeAllElements();
		setRuleProductionList(visible);
	}

	public void actionPerformed(java.awt.event.ActionEvent ae) {

		if (ae.getSource() == closeJButton) {
			setVisible(false);
		}

		return;
	}

}