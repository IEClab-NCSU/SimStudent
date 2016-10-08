package edu.cmu.pact.miss.PeerLearning.TutorialBuilderUtility;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;

public class GUIUtility {
	
	private static Object lastDrawn;
	private static Border toRestore;
	private static Thread constructive;
	private static Thread destructive;

	/**
	 * Draws a border of a given color around an object, setting this object to the lastDrawn and its border to the toRestore for restoration later
	 * @param comp JComponent to draw border around
	 * @param color Color of border
	 */
	public static synchronized void drawBorder(JComponent comp, Color color) //draws a border of the specified color around the object
	{				
		try {
		toRestore = comp.getBorder();
		lastDrawn = comp;

		if(comp instanceof JButton)
		{
			Graphics2D g = (Graphics2D)comp.getGraphics();			
			g.setColor(color);
			g.drawRect(0,0,comp.getWidth() - 1, comp.getHeight() - 1);
			comp.update(g);
		}
		else
			comp.setBorder(BorderFactory.createLineBorder(Color.RED));
		}
		catch(IllegalArgumentException e)
		{
			lastDrawn = null;
			toRestore = null;
		}
	}

	/**
	 * Removes a border from a given object using the lastDrawn object and the toRestore border
	 */
	public static synchronized void removeBorder()
	{ 
		JComponent comp = (JComponent)lastDrawn;
	
		if(comp == null)
			return;
		
		if(comp instanceof JButton)
		{
			Graphics2D g = (Graphics2D)comp.getGraphics();
			g.clearRect(0,0,comp.getWidth(),comp.getHeight());
			comp.update(g);
		}
		else
			comp.setBorder(toRestore);

	}

	/**
	 * Clears previous border and draws new border on a given component in a thread safe way
	 * @param comp Component to draw new border on
	 * @param color Color to draw border
	 */
	public static void clearAndDraw(JComponent comp, Color color)
	{
		final Color c = color;
		final JComponent cmp = comp;
		
		Runnable remove = new Runnable() { public void run() {removeBorder(); } };
		Runnable draw = new Runnable() { public void run() {drawBorder(cmp, c); } };
		constructive = new Thread(remove);
		destructive = new Thread(draw);
		
		constructive.start();
		try {
			constructive.join();
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
		destructive.start();
		try {
			destructive.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
