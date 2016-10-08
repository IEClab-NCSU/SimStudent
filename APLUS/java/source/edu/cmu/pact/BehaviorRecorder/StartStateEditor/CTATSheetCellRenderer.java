/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-26 09:12:13 -0400 (Fri, 26 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.7  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.6  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.5  2011/07/01 18:34:57  vvelsen
 Lots of small fixes that make the start stated editor integrate better with the flash code.

 Revision 1.4  2011/06/14 13:52:21  vvelsen
 Further refined the message processing. We can now also generate outgoing messages from the start state editor. For example we can send highlight and unhighlight messages to indicate which component an author is working on. Also we can send interfacedescription messages backt to the interface to update the appearance.

 Revision 1.3  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.2  2011/06/01 19:39:38  vvelsen
 More serialization functionality. We also now have an output console and better cell rendering.

 Revision 1.1  2011/05/31 20:11:16  vvelsen
 Fixed a package namespace problem. Added a couple of cell renderers and further refined the serialization classes.


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

import javax.swing.table.DefaultTableCellRenderer;

import edu.cmu.hcii.ctat.CTATBase;

/**
 * 
 */	
class CTATSheetCellRenderer extends DefaultTableCellRenderer 
{
	private static final long serialVersionUID = 8503493022905127918L;
	private CTATSerializable object=null;
	private CTATSAI sai=null;
	private CTATArgument arg=null;
	
    /**
	 * 
	 */	
    public CTATSheetCellRenderer() 
    { 
    	//super(); 
    	debug ("CTATSheetCellRenderer ()");
    	this.setFont(new Font("Dialog", 1, 10));
    }
    /**
	 * 
	 */
    private void debug (String aMessage)
    {
    	CTATBase.debug ("CTATSheetCellRenderer",aMessage);
    }
    /**
	 * 
	 */
    public void setValue(Object value) 
    {
    	debug ("setValue ()");
    	
    	if (value==null)
    	{
    		setText("Undefined");
    		return;
    	}
        	    	
		if (value instanceof String)
		{    	    	
			debug ("INSTANCE IS STRING NOT SERIALIZABLE OBJECT!");
			
			String object=(String) value;
			setText(object);
		}	
		
		if (value instanceof CTATSerializableTableEntry)
		{
			debug ("Instance is a CTATSerializableTableEntry");
			
	    	CTATSerializableTableEntry entry=(CTATSerializableTableEntry) value;
	    	
	    	object=(CTATSerializable) entry.getEntry();
    	    if (object!=null)    	    	   
    	    	setText (object.getValue());
    	    
	    	sai=(CTATSAI) entry.getSAI();
    	    if (sai!=null)    	    	   
    	    {
    	    	if (sai.getArgumentSize()>1)
    	    		setText (sai.toArgumentString());
    	    	else
    	    		setText (sai.getValue ());
    	    }	
    	    
	    	arg=(CTATArgument) entry.getArgument();
    	    if (arg!=null)    	    	   
    	    	setText (arg.getValue());    	    
		}	
    }
}
