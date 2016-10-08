/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.ProblemSetWizard;

import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATFileManager;
import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATJPanel;

/**
 * 
 */
public class CTATDeploymentPanel extends CTATJPanel
{		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CTATFileManager fManager=null;	
		
	/**
	 * 
	 */		
	public CTATDeploymentPanel () 
    {		
    	//setClassName ("CTATDeploymentPanel");
    	debug ("CTATDeploymentPanel ()"); 
    	
		fManager=new CTATFileManager ();
            
    }	
}
