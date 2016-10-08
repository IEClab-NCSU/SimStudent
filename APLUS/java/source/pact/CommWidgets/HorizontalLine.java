package pact.CommWidgets;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;


public class HorizontalLine extends JPanel {

	protected JPanel container;
	
	public HorizontalLine () {
		super();
		setOpaque (false);
	
	}

    
    public void paint(Graphics g) {
		super.paint(g);
		Dimension size = getSize();
		g.drawLine (0,0,size.width, 0);
	}

}
