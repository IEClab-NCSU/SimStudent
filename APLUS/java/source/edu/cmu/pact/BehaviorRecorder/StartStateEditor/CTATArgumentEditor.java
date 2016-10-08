/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.1  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.


 -------------------------------------------------------------------------------------
 License:
 -------------------------------------------------------------------------------------
 ChangeLog:
 -------------------------------------------------------------------------------------
 Notes:
 ------------------------------------------------------------------------------------
*/

package edu.cmu.pact.BehaviorRecorder.StartStateEditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class CTATArgumentEditor extends CTATJDialog implements ActionListener
{    		    	
	private static final long serialVersionUID = 1L;
	private CTATSAI SAI=null;

	//private JPanel messagePane =null;
	JButton cancelButton =null;
	JButton okButton=null;
	
	CTATPropertyTable parameterTable=null;
	JPanel parameterPanel=null;
	
	DefaultTableModel parameterModel=null;	
	
	String[] columnNames = {"Name","Value"};	
	
	/**
	 *
	 */
    public CTATArgumentEditor (JFrame parent, String title) 
    {
    	super (parent, title, true);
    	 
		//setClassName ("CTATArgumentEditor");
		debug ("CTATArgumentEditor ()");
		        
        Box panelBox=new Box (BoxLayout.Y_AXIS);
        this.add (panelBox);
                                       
        parameterTable=new CTATPropertyTable ();
        parameterTable.setBorder(BorderFactory.createLineBorder(Color.black));
        parameterTable.setMaximumSize(new Dimension (5000,5000));
                
        JScrollPane parameterScrollList=new JScrollPane (parameterTable);
        parameterScrollList.setMinimumSize(new Dimension (10,10));
        parameterScrollList.setMaximumSize(new Dimension (5000,5000));
                
        panelBox.add(parameterScrollList,BorderLayout.CENTER);		
	            
        Box buttonBox=new Box (BoxLayout.X_AXIS);
        panelBox.add (buttonBox);        
        
        /*
	    cancelButton = new JButton("Cancel");
	    buttonBox.add(cancelButton);
	    cancelButton.addActionListener(this);
	    */
	    
	    okButton = new JButton("Close");
	    buttonBox.add(okButton);
	    okButton.addActionListener(this);  
	    
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);	    	    
    }
	/**
	 *
	 */
    public void setSAI (CTATSAI anSAI)
    {
    	debug ("setSAI ()");
    	
    	SAI=anSAI;
    	updateForm ();
    }
	/**
	 *
	 */
    private void updateForm ()
    {
    	debug ("updateForm ()");
    	
		parameterModel=new DefaultTableModel (null,columnNames);
		
		ArrayList <CTATArgument>arguments=SAI.getArguments();
						
		for (int j=0;j<arguments.size();j++)
		{
			CTATArgument arg=arguments.get(j);
			
			debug ("Adding: " + arg.getName() + " with value: " + arg.getValue());
							
			CTATSerializableTableEntry entry1=new CTATSerializableTableEntry (arg.getName());			
			CTATSerializableTableEntry entry2=new CTATSerializableTableEntry (arg.getValue());
			entry2.setArgument (arg);
			
			CTATSerializableTableEntry[] styleData = {entry1,entry2};
			
			parameterModel.addRow (styleData);							
		}
					
		parameterTable.setModel(parameterModel);
		
        TableColumn colS = parameterTable.getColumnModel().getColumn(1);
        colS.setCellEditor(new CTATSheetCellEditor());			
    }
	/**
	 *
	 */    
	@Override
	public void actionPerformed (ActionEvent arg0) 
	{		
		if (arg0.getSource ()==okButton) 
		{

		}		
		
		/*
		if (arg0.getSource ()==cancelButton) 
		{

		}
		*/		
		
	    setVisible(false); 
	    dispose(); 		
	}	
}
