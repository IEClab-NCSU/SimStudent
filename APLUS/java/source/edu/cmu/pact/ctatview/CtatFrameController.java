/*
 * Created on Sep 9, 2004
 *
 */
package edu.cmu.pact.ctatview;

import javax.swing.JOptionPane;

import edu.cmu.pact.BehaviorRecorder.Controller.CTAT_Launcher;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctat.view.CtatFrame;
 
/**
 * 
 * This file manages the docking and undocking of windows 
 * in the authoring tools
 * 
 * @author mpschnei
 *
 * Collinl: This appears to be a basic container class 
 * 	just handles storage of the layouts within the frame.
 * 	The core components are the DockManager and the CtatFrame.
 */
public class CtatFrameController  
{	
    private DockManager dockManager;
    
	public CtatFrameController(CTAT_Launcher server) 
	{
        dockManager = server.getDockManager();
        dockManager.ctatFrame = new CtatFrame(server);
	}
    
    /**
     * @param b
     */
    public void dockWindowsNow(boolean showCtatWindow) 
    {
		try 
		{
			dockManager.dockWindowsNow(showCtatWindow);
		} 
		catch (CtatviewException ce) 
		{
			StringBuffer errMsg = new StringBuffer("Error trying to dock windows.");
			errMsg.append(" Additional information:\n").append(ce);
			Throwable cause = ce.getCause();

			if (cause != null)
				errMsg.append("\n: Cause: ").append(cause);
			
			trace.errStack("dockWindowsNow(): "+errMsg, ce);
			
			JOptionPane.showMessageDialog(dockManager.ctatFrame, errMsg, "Error docking windows",JOptionPane.ERROR_MESSAGE);
		}
    }
    
    public DockManager getDockManager() 
    { 
    	return this.dockManager; 
    }

    public CtatFrame getDockedFrame() 
    {
        return dockManager.ctatFrame;
    }

    public void saveLayout(String mode) 
    {
        dockManager.saveLayout(mode);
    }


    public void restoreDefaultView() 
    {
    	dockManager.restoreDefaultView();
    }
    
}
