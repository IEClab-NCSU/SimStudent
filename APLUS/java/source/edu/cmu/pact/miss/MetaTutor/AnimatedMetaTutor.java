package edu.cmu.pact.miss.MetaTutor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnimatedMetaTutor extends JFrame {

	private JPanel panel;
	private JLabel imageLabel;
	
	private static final String ANIMATED_METATUTOR_IMAGE = "img/grading.gif";
	
	/** Constructor to initialize the animated Meta-Tutor */
	public AnimatedMetaTutor() {
		imageLabel = new JLabel();
		try {
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			panel = (JPanel)getContentPane();
			panel.setLayout(new BorderLayout());
			setSize(new Dimension(400,340));
			ImageIcon imageIcon = createImageIcon(ANIMATED_METATUTOR_IMAGE);
			imageLabel.setIcon(imageIcon);
			panel.add(imageLabel, "Center");
			setLocationRelativeTo(null);
			setVisible(true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the image from the specified path
	 * @param path
	 * @return
	 */
	private ImageIcon createImageIcon(String path) {
		
		if(path.isEmpty())
			return null;
		
		String file = "/edu/cmu/pact/miss/PeerLearning/"+path;
		URL url = this.getClass().getResource(file);
		if(url != null) {
			return new ImageIcon(url);
		} 
		return null;
	}
	
	public static void main(String[] args){
		new AnimatedMetaTutor();
	}
}
