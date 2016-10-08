package edu.cmu.pact.miss.PeerLearning;




import java.awt.FlowLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class JLabelWithImageAndText extends JFrame {

	private static final long serialVersionUID = 1L;

	public JLabelWithImageAndText() {

		// set flow layout for the frame
		this.getContentPane().setLayout(new FlowLayout());

		JLabel label1 = new JLabel("Java Code Geeks - Java Examples");

		ImageIcon icon = new ImageIcon("images/label.jpg");

		JLabel label2 = new JLabel(icon);

		JLabel label3 = new JLabel("Java Code Geeks", icon, JLabel.CENTER);

		// add labels to frame
		add(label1);
		add(label2);
		add(label3);

	}

	private static void createAndShowGUI() {

  //Create and set up the window.

  JFrame frame = new JLabelWithImageAndText();

  //Display the window.

  frame.pack();

  frame.setVisible(true);

  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

	public static void main(String[] args) {

  //Schedule a job for the event-dispatching thread:

  //creating and showing this application's GUI.

  javax.swing.SwingUtilities.invokeLater(new Runnable() {

public void run() {

    createAndShowGUI(); 

}

  });
    }

}