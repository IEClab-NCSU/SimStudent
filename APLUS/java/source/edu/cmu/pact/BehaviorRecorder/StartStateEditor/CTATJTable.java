/**------------------------------------------------------------------------------------
 $Author: blojasie $ 
 $Date: 2012-05-31 11:09:39 -0400 (Thu, 31 May 2012) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.1  2011/07/19 19:54:33  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 
 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.awt.Font;

import javax.swing.JTable;

import edu.cmu.hcii.ctat.CTATBase;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

public class CTATJTable extends JTable
{
	private static final long serialVersionUID = -1127341907493007641L;
	
	BR_Controller controller=null;	
	
	/**
	 *
	 */
    public CTATJTable () 
    {
    	debug ("CTATJTable ()");
    	    	
    	this.setFont(new Font("Dialog", 1, 10));
    }
    /**
	 * 
	 */
    protected void debug (String aMessage)
    {
    	CTATBase.debug ("CTATJTable",aMessage);
    }  
	/**
	 * 
	 */	
	public void setController (BR_Controller aController)
	{
		debug ("setController ()");
		
		controller=aController;
	}    
	/**
	 *
	 */    
    public boolean isCellEditable (int rowIndex, int vColIndex) 
    {    	
        return true;
    }        
}
