package edu.cmu.old_pact.wizard;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface Paintable{
	public void paint(Graphics g);
	public Rectangle getBounds();
}