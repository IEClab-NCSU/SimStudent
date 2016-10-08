/**------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2013-05-16 12:53:02 -0400 (Thu, 16 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.7  2012/10/09 00:19:26  sewall
 Made subclass constructor compatible with changed CTATLink constructor, passing new CTATDesktopFileManager().

 Revision 1.6  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.5  2012/05/31 15:09:35  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.4  2011/09/08 18:44:23  vvelsen
 The start state editor now properly processes any incoming interface action that is transmitted during start state editing.

 Revision 1.3  2011/09/02 18:40:33  vvelsen
 Added a way to obtain XML representations of all interface description messages and all interface actions used for storing in the BRD start state.

 Revision 1.2  2011/09/02 16:16:29  vvelsen
 Finalized the code that sends a preview of the Flash tutor to the Start State Editor.

 Revision 1.1  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.2  2011/06/10 12:44:01  vvelsen
 Added missing files and changed the w3c parser to the jdom parser.

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

import java.awt.Image;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import org.jdom.Element;

import edu.cmu.hcii.ctat.CTATFileManager;
import edu.cmu.hcii.ctat.CTATLink;
import edu.cmu.hcii.ctat.ExitableServer;

public class CTATSSELink extends CTATLink
{    		    
    public static ArrayList<CTATComponent> components=null;
    public static DefaultTableModel SAIModel=null;
    
    public static ExitableServer wServer=null;
    public static Image preview=null;
    public static Boolean showPreview=true;
	
	/**
	 *
	 */
    public CTATSSELink (CTATFileManager aManager) 
    {
    	super (aManager);

		setClassName ("CTATSSELink");
		debug ("CTATSSELink ()");	    			
    }  
    
	/**
	 *
	 */
    public static Element getInterfaceDescriptionElements ()
    {
    	debug ("CTATSSELink","getInterfaceDescriptionElements ()");
    	
    	Element intRoot=new Element ("InterfaceDescriptions");
    	
    	if (components!=null)
    	{
    		for (int i=0;i<components.size();i++)
    		{
    			CTATComponent comp=components.get(i);
    			intRoot.addContent(comp.toStringElement(CTATSerializable.IncludeIn.sparse));
    		}
    	}	
    	
    	return (intRoot);
    }

    /**
     * Generate a root &lt;StartState&gt; element whose children are the InterfaceDescription
     * message contents needed for BR_Controller#sendInterfaceDescription().
     * @param includeMode one of {@link CTATSerializable#INCLUDE_SPARSE}, etc.
     * @return root element
     */
    public static Element generateStartState (CTATSerializable.IncludeIn includeMode)
    {
    	Element startStateRoot=new Element ("StartState");
    	
    	if (components==null)
    		return (null);
	   
		for (int i=0;i<components.size();i++)
		{ // FIXME look also for InterfaceAction msgs
			CTATComponent comp=components.get(i);  // FIXME STATIC!
				
			startStateRoot.addContent(comp.toStringElement(includeMode)); // CTATSerializable.INCLUDE_SPARSE, etc.
		}    	
    	
    	return (startStateRoot);
    }
	/**
	 *
	 */
    public static Element getInterfaceActionElements ()
    {
    	debug ("CTATSSELink","getInterfaceActionElements ()");
	   
    	Element actRoot=new Element ("InterfaceActions");
	
    	if (SAIModel!=null)
    	{
    		int size=SAIModel.getRowCount();
   	
    		for (int i=0;i<size;i++)
   			{
   				CTATSAITreeNode node = (CTATSAITreeNode) SAIModel.getValueAt(i,0); 
   				CTATSAI sai=node.getSAI();	
   				actRoot.addContent(sai.toStringElement ());
   			}
    	}	
    	
    	return (actRoot);   	
    }    
    /**
     * 
     * @param anInstance
     * @return
     */
    public static CTATComponent getComponent (String anInstance)
    {
    	debug ("CTATSSELink","getComponent ()");
	   
    	if (components==null)
    		return (null);
	   
		for (int i=0;i<components.size();i++)
		{
			CTATComponent comp=components.get(i);
				
			if (comp.getInstanceName().equals(anInstance)==true)
				return (comp);
		}
		
	   	return (null);
    }
}
