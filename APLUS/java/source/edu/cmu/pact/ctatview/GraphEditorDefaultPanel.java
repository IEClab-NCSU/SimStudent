package edu.cmu.pact.ctatview;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel that displays in the graph editor when no graph tabs are open.
 * @author sdemi
 *
 */
public class GraphEditorDefaultPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final String message = "Create a new graph or open an existing graph from the File menu.";

	public GraphEditorDefaultPanel() {
		super();
		init();
	}
	
	public void init() {
		this.setBackground(Color.WHITE);
		JLabel messageLabel = new JLabel(message);
		messageLabel.setName("messageLabel");
		messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
		this.add(messageLabel);
	}


}
