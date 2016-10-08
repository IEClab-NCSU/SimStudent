package edu.cmu.pact.Log;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.OperationAbortedException;
import net.infonode.docking.View;
import edu.cmu.pact.Utilities.EventLogger;
import edu.cmu.pact.Utilities.LoggingSupport;
import edu.cmu.pact.ctat.view.AbstractCtatWindow;

public class AuthorLogListener extends DockingWindowAdapter implements WindowListener {

	private EventLogger eventLogger;
	
	/**
	 * @param ls this should never be called with a null loggingSupport.
	 */
	public AuthorLogListener(LoggingSupport ls)
	{
		this.eventLogger = new EventLogger(ls);
	}

	public AuthorLogListener(EventLogger eventLogger) 
	{
		this.eventLogger = eventLogger;
	}

	public void windowClosing(WindowEvent arg0) 
	{
		eventLogger.log(true, AuthorActionLog.CTAT_WINDOW, 
				AbstractCtatWindow.CLOSE, 
				arg0.getSource().getClass().toString(), "", "");		
	}

	public void windowIconified(WindowEvent arg0) 
	{
		eventLogger.log(true, AuthorActionLog.CTAT_WINDOW, 
						AbstractCtatWindow.ICONIFY, arg0.getSource().getClass().toString(), "", "");
	}
	
	public void windowDeiconified(WindowEvent arg0) 
	{
		eventLogger.log(true, AuthorActionLog.CTAT_WINDOW, 
						AbstractCtatWindow.DEICONIFY, arg0.getSource().getClass().toString(), "", "");
	}
	
	public void windowOpened(WindowEvent arg0) {}
	public void windowActivated(WindowEvent arg0) {}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}

	public void windowAdded(DockingWindow arg0, DockingWindow arg1) {}
	public void windowRemoved(DockingWindow arg0, DockingWindow arg1) {}

	public void windowShown(DockingWindow arg0)
	{
		eventLogger.log(true, AuthorActionLog.DOCKING_WINDOW, 
				"SHOW", arg0.getTitle(), "", "");
	}
	public void windowHidden(DockingWindow arg0)
	{
		eventLogger.log(true, AuthorActionLog.DOCKING_WINDOW, 
				"HIDE", arg0.getTitle(), "", "");
	}
	public void viewFocusChanged(View arg0, View arg1) {}
	public void windowClosing(DockingWindow arg0) throws OperationAbortedException {}
	public void windowClosed(DockingWindow arg0)
	{
		eventLogger.log(true, AuthorActionLog.DOCKING_WINDOW, 
				"CLOSE", arg0.getTitle(), "", "");
	}
	public void windowUndocking(DockingWindow arg0) throws OperationAbortedException
	{
		eventLogger.log(true, AuthorActionLog.DOCKING_WINDOW, 
				"UNDOCK", arg0.getTitle(), "", "");
	}
	public void windowUndocked(DockingWindow arg0) {}
	public void windowDocking(DockingWindow arg0) throws OperationAbortedException
	{
		eventLogger.log(true, AuthorActionLog.DOCKING_WINDOW, 
				"DOCK", arg0.getTitle(), "", "");
	}
	public void windowDocked(DockingWindow arg0) {}
	public void windowMinimizing(DockingWindow arg0) throws OperationAbortedException {}
	public void windowMinimized(DockingWindow arg0)	{}
	public void windowMaximizing(DockingWindow arg0) throws OperationAbortedException {}
	public void windowMaximized(DockingWindow arg0) {}
	public void windowRestoring(DockingWindow arg0) throws OperationAbortedException {}
	public void windowRestored(DockingWindow arg0)
	{
		eventLogger.log(true, AuthorActionLog.DOCKING_WINDOW, 
				"RESTORE", arg0.getTitle(), "", "");
	}


}
