/**------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2013-05-19 14:58:35 -0400 (Sun, 19 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.7  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.6  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.5  2011/06/11 14:12:17  vvelsen
 First version of start state editor that fully processes and displays the start state coming from an AS3 based tutor.

 Revision 1.4  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.3  2011/05/31 20:11:16  vvelsen
 Fixed a package namespace problem. Added a couple of cell renderers and further refined the serialization classes.

 Revision 1.2  2011/05/27 20:58:25  vvelsen
 Many more pieces of functionality. The serialization is much better, according to spec and in sync with the AS3 version.

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

import java.util.Iterator;

import org.jdom.Element;

import edu.cmu.pact.BehaviorRecorder.StartStateEditor.CTATSerializable.IncludeIn;
import edu.cmu.pact.Utilities.trace;

/**

*/
public class CTATParameter extends CTATSerializable
{		
	/**
	 *
	 */
    public CTATParameter () 
    {
    	setClassName ("CTATParameter");
    	debug ("CTATParameter ()");
    }
	/**
	*	
	*/		
    public void fromXML (Element node)
    {
    	debug ("fromXML ()");
    	
    	setClassType (node.getName ());    	
   	    	
    	Iterator itr = (node.getChildren()).iterator();
		
		while (itr.hasNext()) 
		{
            Element elem=(Element) itr.next();
            
            debug ("Parsing text node ("+elem.getName()+")...");
            
			if (elem.getName().equals ("name")==true)
			{
				debug ("Parsing name: " + elem.getValue());
				setName (elem.getValue());
			}   
			
			if (elem.getName().equals ("value")==true)
			{
				debug ("Parsing value: " + elem.getValue());				
				setValue (elem.getValue());				
				setType (elem.getAttributeValue("type")); 		

				setIncludeIn(IncludeIn.minimal);
				if (elem.getAttributeValue("includein")==null || elem.getAttributeValue("includein").length()<1)
					includeIn = IncludeIn.minimal;  // default for backward compatibility
				else
				{
					try {
						includeIn = IncludeIn.valueOf(elem.getAttributeValue("includein").toLowerCase());
					} catch (Exception e) {
						trace.err("Invalid includeIn attribute \""+elem.getAttributeValue("includein")+
								"\" for element "+node.getName()+"; default "+IncludeIn.full);
						includeIn = IncludeIn.minimal;  // default for backward compatibility
					}
				}
			}
		}        	    
    }    
}
