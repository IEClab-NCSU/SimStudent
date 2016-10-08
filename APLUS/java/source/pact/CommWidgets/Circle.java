package pact.CommWidgets;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


public class Circle extends JPanel {

	protected JPanel container;
	
	public Circle () {
		super();
		setOpaque (false);
	
	}

    
    public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		g.drawOval (0,0,size.width - 2, size.height - 2);
	}

}
