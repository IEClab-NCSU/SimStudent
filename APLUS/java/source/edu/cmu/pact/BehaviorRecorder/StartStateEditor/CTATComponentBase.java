/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-26 09:12:13 -0400 (Fri, 26 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.1  2011/05/26 16:12:06  vvelsen
 Added first version of the start state editor. There's a test rig under the test directory.

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import edu.cmu.hcii.ctat.CTATBase;

/**
*
*/
public class CTATComponentBase extends CTATBase
{
	/**
	 *
	 */
    public CTATComponentBase () 
    {
    	setClassName ("CTATComponentBase");
    	debug ("CTATComponentBase ()");
    }
}
