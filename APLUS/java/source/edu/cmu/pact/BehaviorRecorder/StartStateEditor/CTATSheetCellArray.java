/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.2  2011/08/26 14:39:38  vvelsen
 Finally figured out how to get rid of the black background in the dock manager that appeared as black areas around all the view panes. I've added documentation in the code so that people can customize this further.

 Revision 1.1  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.5  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

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

//import javax.swing.BorderFactory;
//import javax.swing.BoxLayout;
//import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 */	
public class CTATSheetCellArray extends CTATJPanel implements TableCellRenderer 
{
	private static final long serialVersionUID = -5533904694972285280L;

	boolean isBordered = true;
    
	private CTATSAI object=null;    

    private JLabel arrayLabel=new JLabel ("");
	
    /**
	 * 
	 */	    
    public  CTATSheetCellArray () 
    {
    	//setClassName ("CTATSheetCellArray");
        debug (" CTATSheetCellArray ()");
                
        this.setLayout(new BoxLayout (this,BoxLayout.X_AXIS));
        this.setMinimumSize(new Dimension (10,10));
        this.setMaximumSize(new Dimension (5000,5000));        
                
	    arrayLabel=new JLabel ("");
	    arrayLabel.setHorizontalAlignment (JLabel.LEFT);
	    arrayLabel.setFont(new Font("Dialog", 1, 10));
	    arrayLabel.setOpaque(true);
	    arrayLabel.setBackground(new Color (255,255,255));
	    arrayLabel.setMinimumSize(new Dimension (50,20));
	    arrayLabel.setMaximumSize(new Dimension (2000,20));
	        
	    this.add(arrayLabel);        
    }    
    /**
	 * 
	 */	
    public Component getTableCellRendererComponent (JTable table, 
    												Object aValue,
                            						boolean isSelected, 
                            						boolean hasFocus,
                            						int row, 
                            						int column) 
    {
    	debug ("getTableCellRendererComponent ("+aValue.getClass().getName()+")");
    		    	    
    	if (aValue instanceof String)
    	{
    		String safety=(String) aValue;
    		debug ("For some reason we're now getting a string back, going into safety mode ...");
        	debug ("Parsing: " + safety);
        	        			
    	}
    	else
    	{
    		
    		CTATSerializableTableEntry value=(CTATSerializableTableEntry) aValue;
    	
    		object=(CTATSAI) value.getSAI();
    	    	    	
    		arrayLabel.setText(object.toArgumentString());
    	    		
    		/*
        	setToolTipText("RGB value: " + newColor.getRed() + ", "
                                     	+ newColor.getGreen() + ", "
                                     	+ newColor.getBlue());
    		 */
    	}
    	
        return this;
    }
}
