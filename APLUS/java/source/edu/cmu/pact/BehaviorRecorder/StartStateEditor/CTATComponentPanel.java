/**------------------------------------------------------------------------------------
 $Author: vvelsen $ 
 $Date: 2013-05-13 10:54:31 -0400 (Mon, 13 May 2013) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.18  2011/08/26 13:12:12  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.17  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.16  2011/07/22 12:00:37  sewall
 Change "Dormin" to "JComm" or "Comm".

 Revision 1.15  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.14  2011/07/06 19:57:39  vvelsen
 Added an experiment design tool.

 Revision 1.13  2011/07/01 18:34:57  vvelsen
 Lots of small fixes that make the start stated editor integrate better with the flash code.

 Revision 1.12  2011/06/28 12:50:16  vvelsen
 Fixed removal of SAIs from table. Better preview and the interface now properly responds to interfaces being disconnected and re-started.

 Revision 1.11  2011/06/22 13:51:52  vvelsen
 You can now add actions to the start state action table. You can also execute an action directly.

 Revision 1.10  2011/06/20 15:05:51  vvelsen
 This should properly process the Commshell and show a first proper preview version of a Flash tutor.

 Revision 1.9  2011/06/16 14:15:43  vvelsen
 Added much better intraspection support. The BR has a much better idea of the state and layout of the tutor.

 Revision 1.8  2011/06/14 13:52:21  vvelsen
 Further refined the message processing. We can now also generate outgoing messages from the start state editor. For example we can send highlight and unhighlight messages to indicate which component an author is working on. Also we can send interfacedescription messages backt to the interface to update the appearance.

 Revision 1.7  2011/06/13 16:47:06  vvelsen
 Fixes to make the damn thing compile under 1.5

 Revision 1.6  2011/06/13 12:47:56  vvelsen
 Added missing start state event file.

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;

/**
 * 
 */
public class CTATComponentPanel extends CTATJPanel implements ActionListener, ItemListener
{	
	private static final long serialVersionUID = 1L;
	
	private CTATComponent component=null;
	
	private JButton foldButton=null;
	private JLabel componentType=null;
	
	private JPanel preview=null;
	
	Icon close=null;
	Icon open=null;
	
	private Boolean folded=false;
	
    private int fixedWidth=200;
    private int fixedHeight=300;	
	    
	JPanel parameterPanel=null;
	JPanel stylePanel=null;
	JPanel SAIPanel=null;
	
	JCheckBox componentShow=null;
	JLabel componentIcon=null;
	
	CTATPropertyTable parameterTable=null;
	CTATPropertyTable styleTable=null;
		
	String[] columnNames = {"Name","Value"};	
	
	DefaultTableModel parameterModel=null;
	DefaultTableModel styleModel=null;
	
	BR_Controller controller=null;
	
	/**
	 * 
	 */		
	public CTATComponentPanel () 
    {
    	//setClassName ("CTATComponentPanel");
    	debug ("CTATComponentPanel ()");   
    	
		close=new ImageIcon(getClass().getClassLoader().getResource("pact/CommWidgets/close.png"));
		open=new ImageIcon(getClass().getClassLoader().getResource("pact/CommWidgets/open.png"));		
		
		this.setLayout (new BoxLayout (this,BoxLayout.X_AXIS));		
		this.setBorder(BorderFactory.createRaisedBevelBorder());

        Box panelBox=new Box (BoxLayout.Y_AXIS);
        this.add (panelBox);
        
        //>-------------------------------------------------

        Box controlBox=new Box (BoxLayout.X_AXIS);
        panelBox.add (controlBox);
        
        foldButton=new JButton ();
        foldButton.setIcon(close);
        foldButton.setText("");
        foldButton.setFont(new Font("Dialog", 1, 10));
        foldButton.setMinimumSize(new Dimension (20,20));
        foldButton.setMaximumSize(new Dimension (5000,20));
        foldButton.setHorizontalAlignment(SwingConstants.LEFT);
        foldButton.addActionListener (this);
        controlBox.add (addInHorizontalLayout (foldButton,5000,20));
        
        componentShow=new JCheckBox ("Show");                
        componentShow.setSelected(false);                
        componentShow.addItemListener(this);
        
        controlBox.add(componentShow);
        
        defaultIcon = createImageIcon("CTATButton.png",null);        
        componentIcon=new JLabel ();                
        componentIcon.setMinimumSize(new Dimension (20,20));
        componentIcon.setPreferredSize(new Dimension (20,20));
        componentIcon.setMaximumSize(new Dimension (20,20));
        componentIcon.setIcon(defaultIcon);
        
        controlBox.add(componentIcon);        
                
        //>-------------------------------------------------
               
        componentType=new JLabel();
        componentType.setFont(new Font("Dialog", 1, 10));   
        componentType.setMinimumSize(new Dimension (10,20));
        componentType.setMaximumSize(new Dimension (5000,20));        
        componentType.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
                      
        panelBox.add (addInHorizontalLayout (componentType,5000,20));
        
        //>-------------------------------------------------        
        
	    JTabbedPane tabbedPane=new JTabbedPane();
	    tabbedPane.setMinimumSize(new Dimension (10,20));
	    tabbedPane.setMaximumSize(new Dimension (5000,5000));	    
	    tabbedPane.setFont(new Font("Dialog",1,10));	
	    	    	    
	    panelBox.add(addInHorizontalLayout (tabbedPane,5000,5000));
        
        //>-------------------------------------------------
        
        stylePanel=new JPanel ();
        tabbedPane.addTab ("Styles",null,stylePanel,"tbd");
                                               
        BorderLayout styleBox=new BorderLayout();
        stylePanel.setLayout(styleBox);        
                		        
        styleTable=new CTATPropertyTable ();
        styleTable.setBorder(BorderFactory.createLineBorder(Color.black));
        styleTable.setMinimumSize(new Dimension (20,20));
        styleTable.setMaximumSize(new Dimension (5000,5000));
                
        JScrollPane styleScrollList=new JScrollPane (styleTable);
        styleScrollList.setMinimumSize(new Dimension (10,10));
        styleScrollList.setMaximumSize(new Dimension (5000,5000));
        
        stylePanel.add (styleScrollList,BorderLayout.CENTER);	    
	    
        //>-------------------------------------------------
        	    
        parameterPanel=new JPanel ();
        tabbedPane.addTab ("Parameters",null,parameterPanel,"tbd");
                                               
        BorderLayout configBox=new BorderLayout();
        parameterPanel.setLayout(configBox);
                                       
        parameterTable=new CTATPropertyTable ();
        parameterTable.setBorder(BorderFactory.createLineBorder(Color.black));
        parameterTable.setMaximumSize(new Dimension (5000,5000));
                
        JScrollPane parameterScrollList=new JScrollPane (parameterTable);
        parameterScrollList.setMinimumSize(new Dimension (10,10));
        parameterScrollList.setMaximumSize(new Dimension (5000,5000));
                
        parameterPanel.add(parameterScrollList,BorderLayout.CENTER);
                
        //>-------------------------------------------------
    }
	/**
	 * 
	 */	
	public void setController (BR_Controller aController)
	{
		debug ("setController ()");
		
		controller=aController;
		
		if (parameterTable!=null)
			parameterTable.setController(aController);
		
		if (styleTable!=null)
			styleTable.setController(aController);		
	}
	/**
	 * 
	 */	
	public void setPreview(JPanel preview) 
	{
		this.preview = preview;
	}
	/**
	 * 
	 */		
	public JPanel getPreview() 
	{
		return preview;
	}	
	/**
	 * 
	 */	
	public void setFixedWidth(int fixedWidth) 
	{
		this.fixedWidth = fixedWidth;
	}
	/**
	 * 
	 */	
	public int getFixedWidth() 
	{
		return fixedWidth;
	}
	/**
	 * 
	 */	
	public void setFixedHeight(int fixedHeight) 
	{
		this.fixedHeight = fixedHeight;
	}
	/**
	 * 
	 */	
	public int getFixedHeight() 
	{
		return fixedHeight;
	}	
	/**
	 * 
	 */	
	public void itemStateChanged(ItemEvent e) 
	{
		debug ("itenStateChanged ()");
		
	    Object source=e.getItemSelectable();

	    if (source==componentShow) 
	    	checkComponent ();
	}	
	/**
	 * 
	 */	
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource ()==foldButton) 
		{
			if (folded==true)
			{
				foldOut ();				
			}
			else
			{			
				foldIn ();				
			}
		}
	}
	/**
	 * 
	 */	
	public void foldOut ()
	{
		if (foldButton!=null)
			foldButton.setIcon(close);
		
		if (parameterPanel!=null)
			parameterPanel.setVisible(true);
		
		if (stylePanel!=null)
			stylePanel.setVisible(true);
		
		if (componentType!=null)
			componentType.setVisible (true);
		
		this.setMinimumSize(new Dimension (50,26));
		this.setPreferredSize(new Dimension (this.getWidth (),getFixedHeight()));
		this.setMaximumSize(new Dimension (5000,getFixedHeight()));
		
		folded=false;		
	}
	/**
	 * 
	 */	
	public void foldIn ()
	{
		if (foldButton!=null)
			foldButton.setIcon(open);
		
		this.setMinimumSize(new Dimension (50,26));
		this.setPreferredSize(new Dimension (this.getWidth(),26));				
		this.setMaximumSize(new Dimension (5000,26));
		
		if (componentType!=null)
			componentType.setVisible(false);
		
		if (parameterPanel!=null)
			parameterPanel.setVisible(false);
		
		if (stylePanel!=null)
			stylePanel.setVisible(false);
		
		folded=true;		
	}	
	/**
	 * 
	 */	
	public void setComponent(CTATComponent component) 
	{
		this.component=component;
		this.component.setChecker (componentShow);
		
		configComponentPanel ();
	}
	/**
	 * 
	 */	
	public CTATComponent getComponent() 
	{
		return component;
	}
	/**
	 *
	 */
	private void configComponentPanel ()
	{
		if (foldButton!=null)
			foldButton.setText(component.getInstanceName ());
		
		if (componentType!=null)
		{
			componentType.setText(component.getClassType());
			
	        ImageIcon icon = createImageIcon(component.getClassType()+".png",null);			
	        componentIcon.setIcon(icon);
		}
				
		if (parameterTable!=null)
		{						
			parameterModel=new DefaultTableModel (null,columnNames);
		
			parameterModel.addTableModelListener(new TableModelListener() 
			{
				@Override
				public void tableChanged(TableModelEvent arg0) 
				{
					debug ("Table changed: " + arg0.getFirstRow() + "," + arg0.getType());
					
					if (arg0.getType()==TableModelEvent.UPDATE)
					{
						debug ("Propagating parameter value back into CTAT object ...");
						
						Object tester=parameterTable.getValueAt(arg0.getFirstRow(),1);
						debug ("Style object: " + tester.getClass().getName() + " with value: " + tester);						
						
						CTATSerializableTableEntry value=(CTATSerializableTableEntry) parameterModel.getValueAt(arg0.getFirstRow(),1);
						CTATStyleProperty entry=(CTATStyleProperty) value.getEntry();
						if (entry!=null)
						{							
							debug ("Entry: " + entry.toString());
							
							CTATComponent target=value.getComponent();
						
							entry.setTouched(true);
						
							if (controller!=null)
							{
								//debug ("Sending: " +target.toStringElementTouched());
								
								controller.sendInterfaceDescription (target.getName(),target.toStringElementTouched());
							}	
							else
								debug ("Error: controller object is null!");
						
							entry.setTouched(false);
						
							//untouch ();
						}	
					}
				}
			});						
			
			ArrayList<CTATParameter> params=component.getParameters();
						
			for (int i=0;i<params.size();i++)
			{
				CTATParameter param=params.get(i);
								
				CTATSerializableTableEntry entry1=new CTATSerializableTableEntry (param.getName());				
				
				CTATSerializableTableEntry entry2=new CTATSerializableTableEntry (param.getValue());
				entry2.setEntry(param);
				entry2.setComponent(getComponent());
				
				CTATSerializableTableEntry[] parameterData = {entry1,entry2};
				
				parameterModel.addRow (parameterData);				
			}
			
			parameterTable.setModel(parameterModel);
			
	        TableColumn colP = parameterTable.getColumnModel().getColumn(1);
	        colP.setCellEditor(new CTATSheetCellEditor());			
		}
		
		if (styleTable!=null)
		{
			styleModel=new DefaultTableModel (null,columnNames);			
			
			styleModel.addTableModelListener(new TableModelListener()
			{
				@Override
				public void tableChanged(TableModelEvent arg0) 
				{
					debug ("Table changed: " + arg0.getFirstRow() + "," + arg0.getType());
					
					if (arg0.getType()==TableModelEvent.UPDATE)
					{
						debug ("Propagating style value back into CTAT object ...");
							
						Object tester=styleTable.getValueAt(arg0.getFirstRow(),1);
						//debug ("Style object: " + tester.getClass().getName() + " with value: " + tester);
						
						CTATSerializableTableEntry value=(CTATSerializableTableEntry) styleModel.getValueAt(arg0.getFirstRow(),1);
						CTATStyleProperty entry=(CTATStyleProperty) value.getEntry();
						
						if (entry!=null)
						{
							debug ("Entry: " + entry.toString());
							
							CTATComponent target=value.getComponent();
						
							entry.setTouched(true);
						
							if (controller!=null)
							{
								//debug ("Sending: " +target.toStringElementTouched());
								controller.sendInterfaceDescription (target.getName(),target.toStringElementTouched());
							}
							else
								debug ("Error: controller object is null!");
						
							entry.setTouched(false);
						
							//untouch ();
						}
						else
							debug ("Error: style entity is not the right entry type");
					}
				}
			});			
						
			ArrayList<CTATStyleProperty> styles=component.getStyleProperties();
			
			for (int j=0;j<styles.size();j++)
			{
				CTATStyleProperty style=styles.get(j);
				
				debug ("Adding: " + style.getName() + " with value: " + style.getValue());
								
				CTATSerializableTableEntry entry1=new CTATSerializableTableEntry (style.getName());				
				CTATSerializableTableEntry entry2=new CTATSerializableTableEntry (style.getValue());
				entry2.setEntry(style);
				entry2.setComponent(getComponent());
				
				CTATSerializableTableEntry[] styleData = {entry1,entry2};
				
				styleModel.addRow (styleData);
				
				//trace.printStack();				
			}
						
			styleTable.setModel(styleModel);
			
	        TableColumn colS = styleTable.getColumnModel().getColumn(1);
	        colS.setCellEditor(new CTATSheetCellEditor());			
		}	
	}
	/**
	 * 
	 */
	private void untouch ()
	{
		debug ("untouch ()");
	
		for (int i=0;i<styleModel.getRowCount();i++)
		{
			Object tester=styleModel.getValueAt(i,1);

			if (tester instanceof CTATSerializableTableEntry)
			{
				CTATSerializableTableEntry value=(CTATSerializableTableEntry) styleModel.getValueAt(i,1);
				CTATSerializable entry=value.getEntry();
				entry.setTouched(false);
			}
			else
				debug ("Internal error: item in style table is not a CTAT entry");
		}
		
		for (int j=0;j<parameterModel.getRowCount();j++)
		{
			Object tester=parameterModel.getValueAt(j,1);

			if (tester instanceof CTATSerializableTableEntry)
			{
				CTATSerializableTableEntry value=(CTATSerializableTableEntry) parameterModel.getValueAt(j,1);
				CTATSerializable entry=value.getEntry();
				entry.setTouched(false);
			}
			else
				debug ("Internal error: item in parameter table is not a CTAT entry");
		}
		
		/*
		for (int i=0;i<styleModel.getRowCount();i++)
		{
			Object tester=styleModel.getValueAt(i,1);

			if (tester instanceof CTATSerializableTableEntry)
			{
				CTATSerializableTableEntry value=(CTATSerializableTableEntry) styleModel.getValueAt(i,1);
				CTATSerializable entry=value.getEntry();
				entry.setTouched(false);
			}
			else
				debug ("Internal error: item in style table is not a CTAT entry");
		}
		*/		
	}
	/**
	 * 
	 */	
	public void checkComponent ()
	{
		debug ("checkComponent ()");
		
		if (controller!=null)
		{
			if (componentShow.isSelected()==true)
			{
				controller.sendHighlightMsg("",s2v(component.getInstanceName()),s2v ("dummy"));
				component.setSelected(true);
			}	
			else
			{	
				controller.sendUnHighlightMsg("",s2v(component.getInstanceName()),s2v ("dummy"));
				component.setSelected(false);
			}	
			
			if (preview!=null)
				preview.repaint();
		}
		else
			debug ("Error: controller object is null");
	}
}
