/**------------------------------------------------------------------------------------
$Author: vvelsen $ 
$Date: 2011-06-21 18:29:35 -0400 (Tue, 21 Jun 2011) $ 
$Header$ 
$Name$ 
$Locker$ 
$Log$
Revision 1.5  2011/06/20 15:05:51  vvelsen
This should properly process the Commshell and show a first proper preview version of a Flash tutor.

Revision 1.4  2011/06/14 13:52:21  vvelsen
Further refined the message processing. We can now also generate outgoing messages from the start state editor. For example we can send highlight and unhighlight messages to indicate which component an author is working on. Also we can send interfacedescription messages backt to the interface to update the appearance.

Revision 1.3  2011/06/09 17:55:36  vvelsen
Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

Revision 1.2  2011/06/01 19:39:38  vvelsen
More serialization functionality. We also now have an output console and better cell rendering.

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

import javax.swing.tree.DefaultMutableTreeNode;

public class CTATSAITreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 1L;
	private CTATSAI sai;
	private CTATComponent component;

	/**
	 *
	 */
	public CTATSAITreeNode(String string) 
	{
		super (string);	
	}
	/**
	 *
	 */
	public void setSAI(CTATSAI sai) 
	{
		this.sai = sai;
	}
	/**
	 *
	 */
	public CTATSAI getSAI() 
	{
		return sai;
	}
	/**
	 *
	 */	
	public void setComponent(CTATComponent component) 
	{
		this.component = component;
	}
	/**
	 *
	 */	
	public CTATComponent getComponent() 
	{
		return component;
	}
}
