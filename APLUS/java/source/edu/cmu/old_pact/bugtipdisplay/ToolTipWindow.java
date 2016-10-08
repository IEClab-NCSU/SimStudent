package edu.cmu.old_pact.bugtipdisplay;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;

import edu.cmu.old_pact.cmu.messageInterface.MultiLineLabel;
import edu.cmu.old_pact.cmu.messageInterface.SingleMessageWindow;
import edu.cmu.old_pact.settings.Settings;

public class ToolTipWindow extends SingleMessageWindow{ 

	String m_Text;
	private Color backGround = Settings.bugMessageBackground;
	private Color foreGround = Settings.bugMessageForeground;
	private int xPos, yPos;
	private MultiLineLabel multiLineLabel;
	private  Font font; 

	public ToolTipWindow(Frame parent, Point pos, String imageBase)
	{
		super(parent, imageBase);
		if((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
			font = new Font("geneva", Font.PLAIN, 9);
		else
			font = new Font("arial", Font.PLAIN, 9); 
		
		xPos = pos.x;
		yPos = pos.y;
		setMessageColor(Settings.bugForeground);
		setBackgroundColor(backGround);
		setLocation(xPos, yPos);
	}

	public Dimension preferredSize() {
		Dimension d = super.preferredSize();
		return new Dimension(d.width, d.height+25);
	}
		
  	public void setVisible(boolean v){
 		Dimension d_before = preferredSize();
  		if (v) 
  			this.setSize(d_before);
  		
  		super.setVisible(v);
  		Dimension d_after = preferredSize();
  		if(d_after != d_before && v)
  			this.setSize(d_after);
  		if(v)
  			resetLocation();
  	}
  	
  	public void paint(Graphics g){
  		Dimension d = getSize();
  		int width = d.width - 1;
    	int height = d.height - 1;
    	
    	g.setColor(Color.black);
    	g.drawLine(0, height, width - 1, height); 	// Bottomleft to bottomright
    	g.drawLine(width, 0, width, height);     	// Topright to bottomright
    	g.drawLine(0, 0, 0, height - 1); 			// Topleft to bottomleft
    	g.drawLine(0, 0, width - 1, 0); 			// Topleft to topright
    }
    
    public void resetLocation(){
    	Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    	Dimension dim = getSize();
    	Point loc = getLocationOnScreen();
    	if((loc.y+dim.height) >= (screen.height-40))
    		setLocation(loc.x, (loc.y-dim.height));
    }
  	
}    