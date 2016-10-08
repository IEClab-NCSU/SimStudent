
package edu.cmu.pact.BehaviorRecorder.View;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import pact.CommWidgets.UniversalToolProxy;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.Utilities.trace;

/////////////////////////////////////////////////////
/**

*/
//////////////////////////////////////////////////////
public class ConnectedIcon extends JLabel  implements MouseListener, Runnable {


	ImageIcon connectedIcon, notConnectedIcon;
	Thread thread;
	final int pauseTime = 30000;
	UniversalToolProxy utp;
    private BR_Controller controller;
	
	//////////////////////////////////////////////////////
	/**

	*/
	//////////////////////////////////////////////////////
	public void run () {

		if (utp == null)
			return;
			
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			
		}

//		boolean check = controller.connectedToProductionSystem();

//		if (check == true) 
			setConnected (true);
//		else
//			setConnected (false);

		return;
	}
	
	//////////////////////////////////////////////////////
	/**

	*/
	ConnectedIcon (BR_Controller controller) {
        this.controller = controller;
		setFont (new Font ("SanSerif", Font.ITALIC, 11));
		try {
			URL iconURL = ClassLoader.getSystemResource("pact/Connected.gif");

			if (iconURL != null) {
			    connectedIcon = new ImageIcon(iconURL,
			                         "connected");
			}

			iconURL = ClassLoader.getSystemResource("pact/NotConnected.gif");
			if (iconURL != null) {
			    notConnectedIcon = new ImageIcon(iconURL,
			                         "not connected");
			}

		} catch (Exception e) {
			trace.out (5, this, "error loading images for connection icon: exception = " + e);
			return;
		}
		utp = controller.getUniversalToolProxy();
		thread = new Thread (this);
		thread.start();
		
	}

	//////////////////////////////////////////////////////
	/**

	*/
	//////////////////////////////////////////////////////
	private boolean checkConnection () {
		if (! utp.connectToTutor() )
			return false;
		else
			return true;
	}	


	//////////////////////////////////////////////////////
	/**

	*/
	//////////////////////////////////////////////////////
	public void setConnected (boolean connected) {
//		connectedIcon = null;
//		notConnectedIcon = null;
    	if (connected) {
			if (connectedIcon != null)
		   		setIcon (connectedIcon);
		   	else
		   		setText ("Connected");
    		setToolTipText ("Connected to production system");
    	} else {
			if (notConnectedIcon != null)
	    		setIcon (notConnectedIcon);
	    	else
	    		setText ("Not Connected");
    		setToolTipText ("Not connected to production system.  Set useLisp property of UTP to true to connect.");
    	}
	}
	

//	public void mouseMoved (mouseEvent e) {}
	public void mouseEntered (MouseEvent e) {}
	public void mouseExited (MouseEvent e) {}
	public void mousePressed (MouseEvent e) {}
	public void mouseReleased (MouseEvent e) {}
	public void mouseClicked (MouseEvent e) {}

}
