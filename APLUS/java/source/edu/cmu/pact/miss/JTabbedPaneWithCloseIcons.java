package edu.cmu.pact.miss;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import edu.cmu.pact.miss.PeerLearning.SimStPLE;
import edu.cmu.pact.miss.PeerLearning.SimStPeerTutoringPlatform;

 /**
  * A JTabbedPane with Undock icon drawn in the tab.
  */
public class JTabbedPaneWithCloseIcons extends JTabbedPane implements
 		MouseListener, MouseMotionListener {

	private class UnDockTabIcon extends ImageIcon {

		private Component component;
		
		private int x_pos;
		
		private int y_pos;
		
		public UnDockTabIcon(URL path) {
			super(path);
		}
		
		@Override
 		public void paintIcon(Component c, Graphics g, int x, int y) {
			super.paintIcon(c, g, x, y);
 			this.x_pos = x;
 			this.y_pos = y;
 			this.component = c;
		}
		
 		public boolean isMouseOnUndock(int x, int y) {

 			Rectangle rect = new Rectangle(x_pos + 2, y_pos + 2,
 					ICON_WIDTH - 4, ICON_WIDTH - 4);
 			if (rect.contains(x, y)) {
 				return true;
 			}

 			return false;
 		}
	}
	

 	/** icon size. */
 	static private final int ICON_WIDTH = 16;

 	private static final String path = "/edu/cmu/pact/miss/PeerLearning/img/undock.png";

 	private static final long serialVersionUID = 1L;

 	/*
 	 * save last mouse event so that we know where we are if the
 	 * icon is asked to repaint without the mouse moving
 	 */
 	private MouseEvent lastMouseEvent;

 	private SimStPeerTutoringPlatform simStPeerTutoringPlatform = null;
	public SimStPeerTutoringPlatform getSimStPeerTutoringPlatform() {
		return simStPeerTutoringPlatform;
	}

	public void setSimStPeerTutoringPlatform(
			SimStPeerTutoringPlatform simStPeerTutoringPlatform) {
		this.simStPeerTutoringPlatform = simStPeerTutoringPlatform;
	}

	/** Keeps track of the ProblemBank when it is undocked so  that when it needs to be docked it is 
	 * the same ProblemBank Tab as when it was before undocking. Avoids the overhead of creating the
	 * ProblemBank Tab from scratch.	 */
	private Component problemBankComponent = null;
	
	/**
 	 * Instantiates a new tabbed pane with close icons.
 	 */
 	public JTabbedPaneWithCloseIcons(SimStPeerTutoringPlatform ssPlatform) {
 		super();
 		addMouseListener(this);
 		addMouseMotionListener(this);
 		simStPeerTutoringPlatform = ssPlatform;
 	}

 	public void addTab(String title, Component component, boolean flag) {
 		// add the extra undock icon onyl if the component is undockable
 		if(flag) {
 			super.addTab(title, new UnDockTabIcon(this.getClass().getResource(path))/*new CloseTabIcon()*/, component);
 			this.repaint();
 		} else {
 			super.addTab(title, component);
 			this.repaint();
 		}
 	}

 	/**
 	 * close all tabs that have close icons.
 	 */
 	public void closeClosableTabs() {
 		for (int i = this.getTabCount() - 1; i >= 0; i--) {
 			Icon icon = getIconAt(i);
 			if (icon == null)
 				return;
 			
 			this.closeTabCommon(i);
 		}
 	}
 	
 	/**
 	 * 
 	 */
 	@Override
 	public void setSelectedIndex(int index){
 		/*Prevent student from changing tabs while SimStudent is thinking*/
  		if (!getSimStPeerTutoringPlatform().getSimStPLE().getStatus().equals(SimStPLE.THINK_STATUS)){
 			super.setSelectedIndex(index);
 		}
 	}
 	/**
 	 * close the currently selected tab.
 	 */
 	public void closeSelectedTab() {
 		int i = this.getSelectedIndex();
 		this.closeTabCommon(i);
 	}
 	
 	/**
 	 * common internal method that checks if a tab can be closed before closing it
 	 * @param i the tab index
 	 */
 	private void closeTabCommon(int i)
 	{
 		Component c = this.getComponentAt(i);
 		this.removeTabAt(i);
 	}

 	/**
 	 * handle mouse click on a tab icon.
 	 * 
 	 * @param e
 	 *            the e
 	 */
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		// check is mouse is on a tab
 		int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
 		if (tabNumber < 0)
 			return;

 		if(tabNumber > 1) // ignore the video tab
 			return;
 		UnDockTabIcon icon = (UnDockTabIcon) getIconAt(tabNumber);
 		if (icon == null)
 			return;

 		// perform action of the click was on an icon
 		if (icon.isMouseOnUndock(e.getX(), e.getY())) {
 			this.undock();
 		}
 		lastMouseEvent = e;

 	}

 	@Override
 	public void mouseDragged(MouseEvent arg0) {
 	  // empty
 	}

 	@Override
 	public void mouseEntered(MouseEvent e) {
 		mouseMoved(e);
 		lastMouseEvent = e;
 	}

 	@Override
 	public void mouseExited(MouseEvent e) {
 		mouseMoved(e);
 		lastMouseEvent = e;
 	}

 	@Override
 	public void mouseMoved(MouseEvent e) {

 		// if the mouse is on a tab - then highlight it
 		for (int tabNumber = 0; tabNumber < this.getComponentCount(); tabNumber++) {
 			UnDockTabIcon icon = (UnDockTabIcon) getIconAt(tabNumber);
 			if (icon == null)
 				return;

 			//icon.paintHighlight(e);
 		}
 		lastMouseEvent = e;
 	}

 	@Override
 	public void mousePressed(MouseEvent e) {
 		lastMouseEvent = e;
 	}

 	@Override
 	public void mouseReleased(MouseEvent e) {
 		lastMouseEvent = e;
 	}

 	/**
 	 * undock the currently selected tab.
 	 */
 	public void undock() {
 		
 		problemBankComponent = getSelectedComponent();
 		JFrame frame = new JFrame("Problem Bank");
		frame.addWindowListener(new WindowAdapter() { // WindowAdapter to listen for events when the undocked ProblemTab is closed
 			@Override
 			public void windowClosing(WindowEvent e) { 
 				generateProblemBankTab();
 			}		
		});
		frame.add(problemBankComponent);
 		frame.pack();
 		frame.setVisible(true);
 		simStPeerTutoringPlatform.getTabPane().setSelectedIndex(0); // switch to the student tab
 	}
 	
 	public void generateProblemBankTab() {
 	
 		JTabbedPaneWithCloseIcons exampleTabPane = getSimStPeerTutoringPlatform().getExamplePane();
 		if(problemBankComponent == null) {
 			String[] columns = {"Problem","Attempts","Difficulty"};
 			problemBankComponent = getSimStPeerTutoringPlatform().createProblemBank(columns, getSimStPeerTutoringPlatform().getSimStPLE().problemStatData);
 		}
 		String bankTab = "Problem Bank";
    	problemBankComponent.setSize(400, 400);
    	
    	int tabIndex = exampleTabPane.indexOfTab(bankTab);
    	if(tabIndex >= 0)
    	{
    		exampleTabPane.remove(tabIndex);
    	}
    	
    	exampleTabPane.insertTab(bankTab, new UnDockTabIcon(this.getClass().getResource(path))/*new CloseTabIcon()*/, problemBankComponent/*bank*/, "", 1);
    	exampleTabPane.setSelectedIndex(0);
   	}
 }
