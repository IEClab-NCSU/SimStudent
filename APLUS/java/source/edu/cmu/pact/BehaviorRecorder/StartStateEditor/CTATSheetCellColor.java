/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.6  2011/08/23 14:36:09  vvelsen
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
//import javax.swing.Box;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 
 */	
public class CTATSheetCellColor extends CTATJPanel implements TableCellRenderer 
{
	private static final long serialVersionUID = -1568793295263302888L;
	
	//Border unselectedBorder = null;
    //Border selectedBorder = null;
    boolean isBordered = true;
    
	private CTATSerializable object=null;    

    private JLabel colorLabel=new JLabel ("");
    private JPanel colorPicker=new JPanel ();	
	
    /**
	 * 
	 */	    
    public CTATSheetCellColor (boolean isBordered) 
    {
    	//setClassName ("CTATSheetCellColor");
        debug ("CTATSheetCellColor ()");
        
        this.setLayout(new BoxLayout (this,BoxLayout.X_AXIS));
        this.setMinimumSize(new Dimension (10,10));
        this.setMaximumSize(new Dimension (5000,5000));        
        
        this.isBordered = isBordered;
        setOpaque (true); //MUST do this for background to show up.
        
	    colorLabel=new JLabel ("");
	    colorLabel.setHorizontalAlignment (JLabel.LEFT);
	    colorLabel.setFont(new Font("Dialog", 1, 10));
	    colorLabel.setOpaque(true);
	    colorLabel.setBackground(new Color (255,255,255));
	    colorLabel.setMinimumSize(new Dimension (50,20));
	    colorLabel.setMaximumSize(new Dimension (2000,20));
	        
	    colorPicker=new JPanel ();
	    //colorPicker.setBackground(color);
	    colorPicker.setBorder(BorderFactory.createLineBorder(Color.black));
	    colorPicker.setMinimumSize(new Dimension (30,20));
	    //colorPicker.setPreferredSize(new Dimension (20,20));
	    colorPicker.setMaximumSize(new Dimension (30,30));
	    //colorPicker.addMouseListener(this);
	    
	    /*
	    Box labelBox = new Box (BoxLayout.X_AXIS);
	    labelBox.add(colorLabel);
	    labelBox.add(colorPicker);
	    
	    this.add(labelBox);
	    */

	    this.add(colorLabel);
	    this.add(colorPicker);
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
    	
    	/*
		if (aValue==null)
		{
			debug ("Internal error: value object is null!");
			return (this);
		}
		*/	
		
		Color newColor=null;
    	    	
    	if (aValue instanceof String)
    	{
    		String safety=(String) aValue;
    		debug ("For some reason we're now getting a string back, going into safety mode ...");
        	debug ("Parsing: " + safety);
        	
        	colorLabel.setText(safety);
        	
            newColor=(Color) CTATColorUtil.parse(safety);
            colorPicker.setBackground(newColor);    		
    	}
    	else
    	{
    		
    		CTATSerializableTableEntry value=(CTATSerializableTableEntry) aValue;
    	
    		object=(CTATSerializable) value.getEntry();
    	    	
    		debug ("Parsing: " + object.getValue());
    	
    		colorLabel.setText(object.getValue());
    	
    		newColor=(Color) CTATColorUtil.parse(object.getValue());
    		colorPicker.setBackground(newColor);
    		colorPicker.repaint();
        
    		/*
        	if (isBordered) 
        	{
            	if (isSelected) 
            	{
                	if (selectedBorder == null) 
                	{
                    	selectedBorder = BorderFactory.createMatteBorder(2,5,2,5,table.getSelectionBackground());
                	}
                
                	setBorder(selectedBorder);
            	}
            	else 
            	{
                	if (unselectedBorder == null) 
                	{
                    	unselectedBorder = BorderFactory.createMatteBorder(2,5,2,5,table.getBackground());
                	}
                
                	setBorder(unselectedBorder);
            	}
        	}
    		 */
    	}
    		
        setToolTipText("RGB value: " + newColor.getRed() + ", "
                                     + newColor.getGreen() + ", "
                                     + newColor.getBlue());
        return this;
    }
}
