/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2011-08-23 10:36:09 -0400 (Tue, 23 Aug 2011) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.1  2011/06/22 13:51:52  vvelsen
 You can now add actions to the start state action table. You can also execute an action directly.

 Revision 1.4  2011/06/14 13:52:21  vvelsen
 Further refined the message processing. We can now also generate outgoing messages from the start state editor. For example we can send highlight and unhighlight messages to indicate which component an author is working on. Also we can send interfacedescription messages backt to the interface to update the appearance.

 Revision 1.3  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.2  2011/05/31 20:11:16  vvelsen
 Fixed a package namespace problem. Added a couple of cell renderers and further refined the serialization classes.

 Revision 1.1  2011/05/27 20:58:25  vvelsen
 Many more pieces of functionality. The serialization is much better, according to spec and in sync with the AS3 version.

 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

//import java.awt.Font;

//import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class CTATSAITable extends CTATJTable
{
	private static final long serialVersionUID = -1127341907493007641L;
	
	private CTATSheetCellRenderer defaultRenderer=null;
	private CTATSheetCellColor colorRenderer=null;	
	private CTATSheetCellArray arrayRenderer=null;
	
	/**
	 *
	 */
    public CTATSAITable () 
    {
    	debug ("CTATSAITable ()");
    	
    	defaultRenderer=new CTATSheetCellRenderer ();
    	colorRenderer=new CTATSheetCellColor (true);
    	arrayRenderer=new CTATSheetCellArray ();
    }
	/**
	 *
	 */    
    public boolean isCellEditable(int rowIndex, int vColIndex) 
    {
    	if (vColIndex<2)
    		return (false);
    	
        return true;
    }        
	/**
	 *
	 */    
   public TableCellRenderer getCellRenderer(int row, int column) 
   {
	   debug ("getCellRenderer ("+row+","+column+")");
   	
       if (column==2)
       {
       		if (this.getValueAt (row,column)!=null)
       		{
/*       			
       			Object tester=this.getValueAt (row,column);
       		
       			if (tester instanceof String)
       			{
       				debug ("INSTANCE IS STRING NOT SERIALIZABLE OBJECT!");
       			
       				return (defaultRenderer);        			
       			}
       			
       			if ((tester instanceof CTATSerializable) || (tester instanceof CTATSAI)) 
       			{       		
       				debug ("Table value is CTATSerializable");
       			
       				CTATSAI object=(CTATSAI) this.getValueAt (row,column);
       	
       				if (object.getArgumentSize()>1)
       				{
       					debug ("We've got an array people!");
       					return (arrayRenderer);
       				}
       				
       				if (object.getType().equals("Color")==true)
       					return (colorRenderer);

       				return (defaultRenderer);
       			}
       			
      			debug ("ERROR! No appropriate cell renderer found");
*/

       			CTATSerializableTableEntry tester=(CTATSerializableTableEntry) this.getValueAt (row,column);
           		
       			CTATSAI object=tester.getSAI();
       	
       			if (object.getArgumentSize()>1)
       			{
       				debug ("We've got an array people!");
       				return (arrayRenderer);
       			}
       				
       			if (object.getType().equals("Color")==true)
       				return (colorRenderer);
       		}	
       }

       return super.getCellRenderer (row,column);
   }        
}
