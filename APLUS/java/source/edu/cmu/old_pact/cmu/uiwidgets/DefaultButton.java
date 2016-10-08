package edu.cmu.old_pact.cmu.uiwidgets;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Panel;

public class DefaultButton extends Panel  {
	public static final int DOWN = 0; 
	public static final int UP = 1;

	private int state;
	
	public DefaultButton(Button button, int state)  {
		add(button);
		this.state = state;
	}

	public void paint(Graphics g) {
    	super.paint(g);
		
    	int width = size().width - 1;
    	int height = size().height - 1;
    	if(state == UP)
    		g.setColor(Color.black);
    	else if(state == DOWN)
    		g.setColor(Color.white);
    	g.drawLine(0, height, width - 1, height); // Bottomleft to bottomright
    	g.drawLine(width, 0, width, height);      // Topright to bottomright
    	if(state == UP)
    		g.setColor(Color.white);
    	else if(state == DOWN)
    		g.setColor(Color.black);
    	g.drawLine(0, 0, 0, height - 1); // Topleft to bottomleft
    	g.drawLine(0, 0, width - 1, 0);  // Topleft to topright
    }
}