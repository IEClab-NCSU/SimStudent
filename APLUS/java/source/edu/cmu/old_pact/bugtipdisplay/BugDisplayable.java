package edu.cmu.old_pact.bugtipdisplay;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;

public interface BugDisplayable {
	public void repaint();
	public void setBugActive(boolean b);
	Frame getFrame();
	public Point getLocationOnScreen();
	public Point getBugPointPosition();
	public Rectangle getBounds();//apowers added this to let the bug message know where the cell is
}