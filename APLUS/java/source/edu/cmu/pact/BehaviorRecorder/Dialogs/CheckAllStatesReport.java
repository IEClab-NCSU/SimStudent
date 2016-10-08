package edu.cmu.pact.BehaviorRecorder.Dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.cmu.pact.ctat.view.ViewUtils;

public class CheckAllStatesReport extends JDialog implements ActionListener
{
    final private String arcExplainText = "\n\n\nTo see the link arc corresponding to the arc numbers above, please select 'Show Last Cog. Model Check Labels' from the menu 'Graph'.";
    private JTextArea displayJTextArea;
    protected JScrollPane displaytextJTextAreaScrollPane;
    private JPanel contentPanel = new JPanel();
    private JPanel closePanel = new JPanel();
    private JButton closeButton = new JButton("Close");
	
	public CheckAllStatesReport (JFrame frame, String displayText)
	{
	    super(frame, "Production Model Test Report");

        setLocationRelativeTo(null);
	    setSize(400, 450);
	    setResizable(true);

	    contentPanel.setLayout(new BorderLayout());
	    ViewUtils.setStandardBorder(contentPanel);

	    displayJTextArea = new JTextArea(displayText + arcExplainText);	
	    displaytextJTextAreaScrollPane = new JScrollPane(displayJTextArea);
	    contentPanel.add(displaytextJTextAreaScrollPane, BorderLayout.CENTER);

	    displayJTextArea.setEditable(false);
	    displayJTextArea.setLineWrap(true);
	    displayJTextArea.setWrapStyleWord(true);

	    closePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
	    closeButton.setVisible(true);
	    closePanel.add(closeButton);
		
	    contentPanel.add(closePanel, BorderLayout.SOUTH);
	    getContentPane().add(contentPanel);
		
		closeButton.addActionListener(this);
		
		addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        setVisible(false);
                                dispose();
                    }
                });

                setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		setVisible(false);
		dispose();
	}
	
}
