/**------------------------------------------------------------------------------------
 $Author: sewall $ 
 $Date: 2014-01-21 19:29:35 -0500 (Tue, 21 Jan 2014) $ 
 $Header$ 
 $Name$ 
 $Locker$ 
 $Log$
 Revision 1.36  2012/09/28 13:37:56  sewall
 Changes for Julie Booth integration with CL.

 Revision 1.35  2012/05/31 15:09:35  blojasie
 Performed a "remove unused imports" on the source-code for all files in packages starting with edu.cmu.*, except edu.cmu.pact.miss.

 Revision 1.34  2012/02/29 04:59:43  sewall
 Revise file dialog.

 Revision 1.33  2011/09/13 19:39:49  vvelsen
 Fixed a bug where component panels would always be re-created if they already existed in the editor from a previous run. More tests are needed to determine which version to use however.

 Revision 1.32  2011/09/08 18:44:23  vvelsen
 The start state editor now properly processes any incoming interface action that is transmitted during start state editing.

 Revision 1.31  2011/09/07 14:51:04  vvelsen
 Small fix to make sure our new action processing, which isn't complete, doesn't interfere with regular development.

 Revision 1.30  2011/09/02 18:40:33  vvelsen
 Added a way to obtain XML representations of all interface description messages and all interface actions used for storing in the BRD start state.

 Revision 1.28  2011/09/02 17:01:14  vvelsen
 Fixed a number of import bugs and re-worked part of the layout of the start state editor. It will now always show the preview window no matter what edit pane you're on and it should be a scaleable window now.

 Revision 1.27  2011/09/02 16:16:29  vvelsen
 Finalized the code that sends a preview of the Flash tutor to the Start State Editor.

 Revision 1.26  2011/08/26 13:12:13  vvelsen
 Added Kevin's standalone tutorshop client. Changed it a bit so that it can handle different request handler interfaces. Also unified the classes CTATLink and CTATBase with the ones from the start state editor. The start state editor should now be almost feature complete. The only task still left is to finish the argument editor for SAIs that have multiple arguments.

 Revision 1.25  2011/08/23 14:36:09  vvelsen
 Mostly complete start state editor with some gui improvements to the top menu bar.

 Revision 1.24  2011/07/22 12:00:37  sewall
 Change "Dormin" to "JComm" or "Comm".

 Revision 1.23  2011/07/19 19:54:32  vvelsen
 Major commit that now has all of the main start state editor functionality in place. It will take more finishing touches however. Message are sent when tables are changed but values aren't properly propagated yet from cell editors back into table cells. Also we need to add functionality to properly take in interface actions in the start state, map them to existing instances and show them in the SAI list.

 Revision 1.22  2011/07/01 19:31:40  vvelsen
 Added an XML viewer so we can debug both the entire start state as well as any outgoing messages.

 Revision 1.21  2011/07/01 18:34:57  vvelsen
 Lots of small fixes that make the start stated editor integrate better with the flash code.

 Revision 1.20  2011/06/28 13:35:15  vvelsen
 Cleaned up a lot of the state management and took away some of the scaffolding.

 Revision 1.19  2011/06/28 12:50:16  vvelsen
 Fixed removal of SAIs from table. Better preview and the interface now properly responds to interfaces being disconnected and re-started.

 Revision 1.18  2011/06/27 17:18:12  vvelsen
 Added some code to connect to socket proxy to the preview window in the start state editor. This way authors can see if a Flash tutor is actually connected to the BR.

 Revision 1.17  2011/06/23 16:45:42  vvelsen
 Small fixes to the code that sends interface actions from the start state editor. You can now also delete actions from the start state.

 Revision 1.16  2011/06/22 13:51:52  vvelsen
 You can now add actions to the start state action table. You can also execute an action directly.

 Revision 1.15  2011/06/21 22:29:35  vvelsen
 Fixed and added so that the build won't fail.

 Revision 1.14  2011/06/21 20:09:14  vvelsen
 Ooops.

 Revision 1.13  2011/06/21 20:07:54  vvelsen
 Re-enabled some old functionality to make sure the start state gets handled old-style for testing purposes.

 Revision 1.12  2011/06/20 15:05:51  vvelsen
 This should properly process the Commshell and show a first proper preview version of a Flash tutor.

 Revision 1.11  2011/06/16 14:15:43  vvelsen
 Added much better intraspection support. The BR has a much better idea of the state and layout of the tutor.

 Revision 1.10  2011/06/14 13:52:21  vvelsen
 Further refined the message processing. We can now also generate outgoing messages from the start state editor. For example we can send highlight and unhighlight messages to indicate which component an author is working on. Also we can send interfacedescription messages backt to the interface to update the appearance.

 Revision 1.9  2011/06/13 16:47:06  vvelsen
 Fixes to make the damn thing compile under 1.5

 Revision 1.8  2011/06/13 12:47:56  vvelsen
 Added missing start state event file.

 Revision 1.7  2011/06/11 14:12:17  vvelsen
 First version of start state editor that fully processes and displays the start state coming from an AS3 based tutor.

 Revision 1.5  2011/06/09 17:55:36  vvelsen
 Added the start state editor into the docking pane and changed the w3c parser to the jdom parser.

 Revision 1.4  2011/06/01 19:39:38  vvelsen
 More serialization functionality. We also now have an output console and better cell rendering.

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
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

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
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jdom.Element;

import edu.cmu.hcii.ctat.CTATDesktopFileManager;
import edu.cmu.hcii.ctat.CTATHTTPServer;
import edu.cmu.hcii.ctat.ExitableServer;
import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Dialogs.DialogUtilities;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.ctat.MsgType;

/**
 * 
 */
public class CTATStartStateEditor extends CTATJPanel implements ActionListener, ProblemModelListener, TreeSelectionListener, ListSelectionListener, MouseListener, ComponentListener
{	
	private static final long serialVersionUID = 1L;
	public static CTATSSELink link=null;
	
	private CTATFileManager fManager=null;
	
    private JButton saveButton=null;
    private JButton saveAsButton=null;
    private JButton loadButton=null;	
    private JButton xmlButton=null;
    
    private JButton addButton=null;
    private JButton removeButton=null;    
    private JButton executeButton=null;
    private JButton upButton=null;
    private JButton downButton=null;    
	
    private JButton zoomInButton=null;
    private JButton zoomOutButton=null;
    
    private JLabel infoLabel=null;
    
    private Box componentBox=null;
    
    private StringBuffer loader=null;
        
    private JTree SAITree=null;
    private JTree SAIByActionTree=null;
    
    private DefaultMutableTreeNode SAIRoot=null;
    private DefaultMutableTreeNode SAIRootByAction=null;
    
    private DefaultTreeModel SAIActionModel=null;
    private DefaultTreeModel SAIActionByActionModel=null;
        
    //private JTextArea outp=null;
    private CTATXMLViewer outp=null;
    
    private JTabbedPane tabbedPane=null;
    
    private CTATTutorPreview preview=null;
    
    private CTATComponent componentSelection=null;
    private CTATSAI actionSelection=null;
    private CTATSAITable actionTable=null;
    
    private JCheckBox showPreviewCheck=null;
    
    //private DefaultTableModel SAIModel=null;
    
	Icon up=null;
	Icon down=null;    
	
	Icon in=null;
	Icon out=null;
    
	String[] columnNames = {"Instance/Selection","Action","Input"};	
	
	// BR specific objects
	
	BR_Controller controller=null;	
	ProblemModel pm=null;
	
	/**
	 * 
	 */		
	public CTATStartStateEditor () 
    {		
    	//setClassName ("CTATStartStateEditor");
    	debug ("CTATStartStateEditor ()"); 
    	
		link=new CTATSSELink (new CTATDesktopFileManager ());
		if (CTATSSELink.components==null)
			CTATSSELink.components=new ArrayList<CTATComponent> ();
		
		up=new ImageIcon(getClass().getClassLoader().getResource("pact/CommWidgets/close.png"));
		down=new ImageIcon(getClass().getClassLoader().getResource("pact/CommWidgets/open.png"));			
		
		in=new ImageIcon(getClass().getClassLoader().getResource("pact/CommWidgets/zoom-icon-in.png"));
		out=new ImageIcon(getClass().getClassLoader().getResource("pact/CommWidgets/zoom-icon-out.png"));		
		
		loader=new StringBuffer ();
				
		fManager=new CTATFileManager ();

				
	    //>-----------------------------------------------
				    	
        BorderLayout frameBox=new BorderLayout();
        this.setLayout (frameBox);
		        
	    //>-----------------------------------------------        
        
	    tabbedPane=new JTabbedPane();
	    tabbedPane.setFont(new Font("Dialog",1,10));	    	
	    
        //this.add(tabbedPane,BorderLayout.CENTER);
                       
        JPanel editor=new JPanel ();
        editor.setLayout (new BoxLayout (editor,BoxLayout.X_AXIS));
        editor.setMinimumSize(new Dimension (20,20));
        editor.setMaximumSize(new Dimension (5000,2000));        
        //editor.setBorder(BorderFactory.createRaisedBevelBorder());
        editor.setBorder(BorderFactory.createMatteBorder(3,3,3,3,new Color (180,180,180)));
        editor.setBackground (new Color (180,180,180));
        editor.setFont(new Font("Dialog", 1, 10));
                
        Box mainBox=new Box (BoxLayout.Y_AXIS);     
        mainBox.setMinimumSize(new Dimension (20,20));
        mainBox.setMaximumSize(new Dimension (5000,5000));
        //mainBox.setBorder(BorderFactory.createLineBorder(Color.black));
        editor.add (mainBox);
                                
        JScrollPane contentScrollList=new JScrollPane (editor);
        contentScrollList.setMinimumSize(new Dimension (20,20));
        contentScrollList.setMaximumSize(new Dimension (5000,2000));
        contentScrollList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScrollList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                        
        tabbedPane.addTab ("Configuration",null,contentScrollList,"tbd");
                
	    //>-----------------------------------------------		

	    Box previewBox = new Box (BoxLayout.Y_AXIS);
	    previewBox.setMinimumSize(new Dimension (20,20));
	    previewBox.setMaximumSize(new Dimension (5000,200));
        
        preview=new CTATTutorPreview ();
        preview.setMinimumSize(new Dimension (20,200));
        preview.setPreferredSize(new Dimension (this.getWidth(),200));
        preview.setMaximumSize(new Dimension (5000,200));
        preview.setBorder(BorderFactory.createLineBorder(Color.black));
        preview.setBackground(new Color (220,220,200));
        //previewBox.add (preview);
        
        JScrollPane previewScrollPane=new JScrollPane (preview);
        
        previewBox.add (previewScrollPane);
                		
	    Box buttonBox = new Box (BoxLayout.X_AXIS);
	    buttonBox.setMinimumSize(new Dimension (20,20));
	    buttonBox.setMaximumSize(new Dimension (5000,20));

	    infoLabel=new JLabel ();
	    infoLabel.setMaximumSize(new Dimension (5000,20));
	    infoLabel.setText(" Tutor: ");
	    infoLabel.setOpaque(true);
	    infoLabel.setBackground(new Color (220,220,200));
	    infoLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	    buttonBox.add (infoLabel);
	    
	    showPreviewCheck=new JCheckBox ();
	    showPreviewCheck.setMaximumSize(new Dimension (150,20));
	    showPreviewCheck.setText("Live Preview");
	    showPreviewCheck.setSelected(true);
	    //infoLabel.setOpaque(true);
	    //infoLabel.setBackground(new Color (220,220,200));
	    //infoLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	    buttonBox.add (showPreviewCheck);
	    
	    zoomInButton=new JButton ();
	    zoomInButton.setFont(new Font("Dialog", 1, 10));
	    zoomInButton.setMinimumSize(new Dimension (30,20));
	    zoomInButton.setPreferredSize(new Dimension (30,20));
	    zoomInButton.setMaximumSize(new Dimension (30,20));
	    //zoomInButton.setText("+");
	    zoomInButton.setIcon (in);
	    zoomInButton.setEnabled(false);
	    buttonBox.add (zoomInButton);
	    
	    zoomOutButton=new JButton ();
	    zoomOutButton.setFont(new Font("Dialog", 1, 10));
	    zoomOutButton.setMinimumSize(new Dimension (30,20));
	    zoomOutButton.setPreferredSize(new Dimension (30,20));
	    zoomOutButton.setMaximumSize(new Dimension (30,20));
	    //zoomOutButton.setText("-");
	    zoomOutButton.setIcon(out);
	    zoomOutButton.setEnabled(false);
	    buttonBox.add (zoomOutButton);
	    	    
	    saveButton=new JButton ();
	    saveButton.setFont(new Font("Dialog", 1, 10));
	    saveButton.setMaximumSize(new Dimension (5000,20));
	    saveButton.setText("Save");
	    //buttonBox.add (saveButton);

	    saveAsButton=new JButton ();
	    saveAsButton.setFont(new Font("Dialog", 1, 10));
	    saveAsButton.setMaximumSize(new Dimension (5000,20));
	    saveAsButton.setText("SaveAs");	
	    //buttonBox.add (saveAsButton);
	    
	    loadButton=new JButton ();
	    loadButton.setFont(new Font("Dialog", 1, 10));
	    loadButton.setMaximumSize(new Dimension (5000,20));
	    loadButton.setText("Load");	
	    //buttonBox.add (loadButton);
	    
	    xmlButton=new JButton ();
	    xmlButton.setFont(new Font("Dialog", 1, 10));
	    xmlButton.setMinimumSize(new Dimension (75,20));
	    xmlButton.setPreferredSize(new Dimension (75,20));
	    xmlButton.setMaximumSize(new Dimension (75,20));
	    xmlButton.setText("To XML");	
	    buttonBox.add (xmlButton);	    
	    
	    //mainBox.add (buttonBox);
	    //mainBox.add (Box.createVerticalStrut(2));
	    
	    previewBox.add(buttonBox);
	    	    
	    showPreviewCheck.addActionListener (this);
	    zoomInButton.addActionListener (this);
	    zoomOutButton.addActionListener (this);
	    //saveButton.addActionListener (this);
	    //saveAsButton.addActionListener (this);
	    //loadButton.addActionListener (this); 
	    xmlButton.addActionListener (this);	    
	    
	    //>-----------------------------------------------
	    	    
        componentBox=new Box (BoxLayout.Y_AXIS);
        componentBox.setMinimumSize(new Dimension (20,20));
        componentBox.setMaximumSize(new Dimension (5000,5000));
        //componentBox.setBorder(BorderFactory.createLineBorder(Color.red));
        
        mainBox.add(componentBox);
        
        //>-----------------------------------------------
                      
        JPanel SAIEditor=new JPanel ();        
        tabbedPane.addTab ("InterfaceActions",null,SAIEditor,"tbd");
        
        SAIEditor.setBorder(BorderFactory.createMatteBorder(3,3,3,3,new Color (180,180,180)));
        SAIEditor.setLayout (new BoxLayout (SAIEditor,BoxLayout.X_AXIS));

        Box SAISubBox=new Box (BoxLayout.Y_AXIS);
        
        SAIEditor.add(SAISubBox);

	    JTabbedPane tabbedSAIPane=new JTabbedPane();
	    tabbedSAIPane.setFont(new Font("Dialog",1,10));
	    
	    SAISubBox.add(tabbedSAIPane);
        
        SAIRoot=new DefaultMutableTreeNode ("Available Instances");            
        SAIActionModel = new DefaultTreeModel(SAIRoot);
        
        //DefaultMutableTreeNode SAITest=new DefaultMutableTreeNode ("Test");
        //SAIRoot.add(SAITest);        
        
        SAITree=new JTree(SAIActionModel);
        SAITree.setFont(new Font("Dialog", 1, 10));
        SAITree.setBorder(BorderFactory.createLineBorder(Color.black));        
        SAITree.setMinimumSize(new Dimension (20,20));
        SAITree.setMaximumSize(new Dimension (5000,200));
        SAITree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        SAITree.addTreeSelectionListener(this);
        SAITree.addMouseListener(this);
        //SAITree.setRootVisible(false);
        SAITree.setEditable(false);
                       
        JScrollPane SAITreeScrollList=new JScrollPane (SAITree);
        SAITreeScrollList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          
        tabbedSAIPane.addTab ("By Instance",null,SAITreeScrollList,"tbd");
        
        SAIRootByAction=new DefaultMutableTreeNode ("Available Actions");
        SAIActionByActionModel = new DefaultTreeModel(SAIRootByAction);
                
        SAIByActionTree=new JTree(SAIActionByActionModel);
        SAIByActionTree.setFont(new Font("Dialog", 1, 10));
        SAIByActionTree.setBorder(BorderFactory.createLineBorder(Color.black));        
        SAIByActionTree.setMinimumSize(new Dimension (20,20));
        SAIByActionTree.setMaximumSize(new Dimension (5000,200));
        SAIByActionTree.addMouseListener(this);
        //SAIByActionTree.setRootVisible(false);
        SAIByActionTree.setEditable(false);
               
        JScrollPane SAITreeScrollListByAction=new JScrollPane (SAIByActionTree);
        SAITreeScrollListByAction.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          
        tabbedSAIPane.addTab ("By Action",null,SAITreeScrollListByAction,"tbd");        
                		
	    Box SAIControlBox = new Box (BoxLayout.X_AXIS);
	    addButton=new JButton ();
	    addButton.setFont(new Font("Dialog", 1, 10));
	    addButton.setMaximumSize(new Dimension (5000,20));
	    addButton.setText("Add");
	    SAIControlBox.add (addButton);

	    removeButton=new JButton ();
	    removeButton.setFont(new Font("Dialog", 1, 10));
	    removeButton.setMaximumSize(new Dimension (5000,20));
	    removeButton.setText("Remove");	
	    SAIControlBox.add (removeButton);
	    
	    executeButton=new JButton ();
	    executeButton.setFont(new Font("Dialog", 1, 10));
	    executeButton.setMaximumSize(new Dimension (5000,20));
	    executeButton.setText("Execute");	
	    SAIControlBox.add (executeButton);	    
	    
	    upButton=new JButton ();
	    upButton.setFont(new Font("Dialog", 1, 10));
	    upButton.setMaximumSize(new Dimension (5000,20));
	    //upButton.setText("Up");
	    upButton.setIcon(up);
	    SAIControlBox.add (upButton);
	    
	    downButton=new JButton ();
	    downButton.setFont(new Font("Dialog", 1, 10));
	    downButton.setMaximumSize(new Dimension (5000,20));
	    //downButton.setText("Down");
	    downButton.setIcon(down);
	    SAIControlBox.add (downButton);	    
	    
	    addButton.addActionListener (this);
	    removeButton.addActionListener (this);
	    executeButton.addActionListener (this);
	    upButton.addActionListener (this);
	    downButton.addActionListener (this);	    
	    	    
	    SAISubBox.add (SAIControlBox);
        
        actionTable=new CTATSAITable ();
        actionTable.setBorder(BorderFactory.createLineBorder(Color.black));
        actionTable.setMinimumSize(new Dimension (10,10));
        actionTable.setMaximumSize(new Dimension (5000,5000));
        
        ListSelectionModel listSelectionModel;
        
        listSelectionModel=actionTable.getSelectionModel ();
        listSelectionModel.addListSelectionListener (this);
        listSelectionModel.setSelectionMode (ListSelectionModel.SINGLE_SELECTION);        
        
        JScrollPane SAIScrollList=new JScrollPane (actionTable);
        SAIScrollList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        SAIScrollList.setMinimumSize(new Dimension (10,10));
        SAIScrollList.setMaximumSize(new Dimension (5000,5000));
        
        SAISubBox.add(SAIScrollList);
        
        CTATSSELink.SAIModel=new DefaultTableModel (null,columnNames);
		actionTable.setModel(CTATSSELink.SAIModel);
		CTATSSELink.SAIModel.addTableModelListener(new TableModelListener() 
		{
			@Override
			public void tableChanged(TableModelEvent arg0) 
			{
				debug ("Table changed: " + arg0.getFirstRow() + "," + arg0.getType());
				
				if (arg0.getType()==TableModelEvent.UPDATE)
				{
					debug ("Propagating SAI settings back into CTAT object ...");
					
					CTATSerializableTableEntry entry=(CTATSerializableTableEntry) actionTable.getValueAt(arg0.getFirstRow(),0);
					CTATSAI sai=entry.getSAI();
					if (sai!=null)
					{					
						/*
						String newValue=(String) actionTable.getValueAt(arg0.getFirstRow(),2);
						CTATSAI sai=entry.getSAI();
					
						debug ("Modifying: " + sai.getAction() + "(" + newValue + ")");
					
						sai.setInput(newValue);
						*/
					}
					else
						debug ("Error, table model doesn't contain an SAI object");
				}
			}
		});

        //>-----------------------------------------------

        JPanel console=new JPanel ();
        console.setLayout (new BoxLayout (console,BoxLayout.X_AXIS));
        console.setMinimumSize(new Dimension (20,20));
        console.setMaximumSize(new Dimension (5000,2000));
        console.setBorder(BorderFactory.createMatteBorder(3,3,3,3,new Color (180,180,180)));
        console.setBackground (new Color (180,180,180));
        console.setFont(new Font("Dialog", 1, 10));

        outp=new CTATXMLViewer ();
        outp.setFont(new Font("Dialog", 1, 10));
        console.add(outp);

        tabbedPane.addTab ("Console",null,console,"tbd");		
        
        //>-----------------------------------------------        
        
        JSplitPane splitPane=null;

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,previewBox,tabbedPane);
        //splitPane.setResizeWeight(0.5);
        //splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);
        
        this.add (splitPane);
        
        previewBox.setMinimumSize(new Dimension (200,300));
        
        //>-----------------------------------------------        
        
        this.addComponentListener(this);

		buildDisplayList ();
		
		//>-----------------------------------------------
		// Start the local web server, and make sure we 
		// only do it once during the life cycle of the BR
		
		if (CTATSSELink.wServer==null)
		{
			debug ("Starting local webserver for incoming image preview materials ...");
			
			try
			{
				ExitableServer srv=new CTATHTTPServer (1504,"./","/logs",new CTATPreviewHandler (preview));
				CTATSSELink.wServer=srv;
			}
			catch (Exception e)
			{
				debug ("Weirdness just happened!");
				e.printStackTrace();
			}
			
			debug ("Server now running");
		}
		else
			debug ("We already have a local webserver running, no need to start another one");
		
		debug ("Initialization of the Start State Editor completed.");
    }	
	/**
	 *
	 */	
	public void setController (BR_Controller aController)
	{
		debug ("setController ()");
		
		controller=aController;
		if (controller!=null)
		{
			pm=controller.getProblemModel();
			preview.setController (aController);
		}	
		
		pm.addProblemModelListener(this);
	}	
	/**
	 *
	 */		
	public void actionPerformed(ActionEvent e) 
	{
		debug ("actionPerformed ()");
	   
		//>------------------------------------------------------		
		
		if (e.getSource ()==showPreviewCheck)
		{
			if (showPreviewCheck.isSelected()==true)
			{
				CTATSSELink.showPreview=true;
				preview.updatePreview();
			}
			else
			{
				CTATSSELink.showPreview=false;
				preview.updatePreview();
			}
		}
		
		//>------------------------------------------------------
	  	
		if (e.getSource ()==xmlButton) 
		{
			tabbedPane.setSelectedIndex (2);
			
			StringBuffer buffer=new StringBuffer();
			
			buffer.append("<StartState>");

			for (int i=0;i<CTATSSELink.components.size();i++)
			{
				CTATComponent component=CTATSSELink.components.get(i);
				buffer.append(component.toString());
			}
			
			buffer.append("</StartState>");		
			
			String result=buffer.toString();
			
			//debug (result);
			
			//outp.setText(result);
			outp.setXML(result);
		}
		         
		//>------------------------------------------------------
  	
		if (e.getSource ()==loadButton) 
		{
			//FileNameExtensionFilter filter=new FileNameExtensionFilter (".xml files", "xml");
			//fc.setFileFilter(filter);
			File file = DialogUtilities.chooseFile(null, null, null, "Load", "Open", this);
			if (file != null)
			{
				int n=0;

/*				
          	Object[] options = {"Yes",
          	                    "No",
          	                    "Cancel"};
          	n=JOptionPane.showOptionDialog(this,
          	    "Loading a saved set will override any existing selections, do you want to continue?",
          	    "SIDE Info Panel",
          	    JOptionPane.YES_NO_CANCEL_OPTION,
          	    JOptionPane.QUESTION_MESSAGE,
          	    null,
          	    options,
          	    options[2]);
*/
				if (n==0)
				{          	
					debug ("Loading: " + file.getName());

					fromXML (fManager.loadContentsXML(file.getAbsolutePath()));          		
				}	
			} 
			else 
			{
				debug ("Open command cancelled by user.");
			}
		}
      
      //>------------------------------------------------------        
      
      if (e.getSource() == saveButton) 
      {
    	  File file = DialogUtilities.chooseFile(null, null, null, "Save", "Save", this);
    	  if (file != null)
    	  {
              debug ("Saving: " + file.getName());
              
              toXML ();
              
              //debug ("XML: " + loader.toString());
              
              fManager.saveContents(file.getAbsolutePath(),loader.toString());
          } 
          else 
          {
          	debug ("Save command cancelled by user");
          }
      }
      
      //>------------------------------------------------------        
      
      if (e.getSource() == saveAsButton) 
      {
    	  File file = DialogUtilities.chooseFile(null, null, null, "Save As", "Save", this);
    	  if (file != null)
    	  {
              debug ("Saving: " + file.getName());
              
              toXML ();
              
              //debug ("XML: " + loader.toString());
              
              fManager.saveContents(file.getAbsolutePath(),loader.toString());
          } 
          else 
          {
          	debug ("Save command cancelled by user");
          }
      }        
      
      //>------------------------------------------------------
      
      if (e.getSource()==addButton)
      {
    	  addSAI ();
      }
      
      //>------------------------------------------------------
      
      if (e.getSource()==removeButton)
      {
    	  debug ("Removing row ...");

    	  int sel=actionTable.getSelectedRow ();
    	  
    	  if (sel==-1)
    	  {
    		  debug ("Nothing selected!");
    		  return;
    	  }
    	  
    	  debug ("Removing "+sel+" ...");
    	  
    	  CTATSSELink.SAIModel.removeRow(sel);
      }
      
      //>------------------------------------------------------
      
      if (e.getSource()==executeButton)
      {
    	  int sel=actionTable.getSelectedRow ();
    	  
    	  if (sel==-1)
    	  {
    		  debug ("Nothing selected!");
    		  return;
    	  }
		
    	  int index=sel;
		
    	  CTATSerializableTableEntry entry=(CTATSerializableTableEntry) actionTable.getValueAt(index,0);
    	  CTATComponent component=entry.getComponent();
    	  CTATSAI SAI=entry.getSAI();
		
    	  debug ("Selected row is: "+index + " instance: " + component.getInstanceName());
		
    	  // Send interface action ...
		
    	  if (controller!=null)
    	  {
    		  controller.sendInterfaceActionMsg(s2v (component.getInstanceName()),s2v (SAI.getInput()),s2v (SAI.getAction()));
    	  }
    	  else
    		  debug ("Error: no controller available to send interface action action");    	  
      }
      
      //>------------------------------------------------------
      
      if (e.getSource()==upButton)
      {
    	  int sel=actionTable.getSelectedRow ();
    	  
    	  if (sel==-1)
    	  {
    		  debug ("Nothing selected!");
    		  return;
    	  }
		    	  
    	  CTATSSELink.SAIModel.moveRow (sel,sel,sel-1);
      }
      
      //>------------------------------------------------------
      
      if (e.getSource()==downButton)
      {
    	  int sel=actionTable.getSelectedRow ();
    	  
    	  if (sel==-1)
    	  {
    		  debug ("Nothing selected!");
    		  return;
    	  }
				
    	  CTATSSELink.SAIModel.moveRow (sel,sel,sel+1);    	  
      }
      
      //>------------------------------------------------------
      
      if (e.getSource()==zoomInButton)
      {
    	  preview.zoomIn ();
      }
      
      //>------------------------------------------------------
      
      if (e.getSource()==zoomOutButton)
      {
    	  preview.zoomOut ();
      }      
      
      //>------------------------------------------------------      
  	}
	/**
	 * 
	 */	
	public String toXML ()
	{
		debug ("toXML ()");
		
		StringBuffer formatter=new StringBuffer ();
		
		return (formatter.toString());
	}
	/**
	 * 
	 */
	public void fromXML (Element root)
	{
		debug ("fromXML ()");
		
		if (root.getName ().equals ("StartState"))
		{	
			debug ("Found start state element");
			
			Iterator itr = (root.getChildren()).iterator();
			
			while (itr.hasNext()) 
			{
	            Element elem = (Element) itr.next();
	            
				if (elem.getName ().equals ("InterfaceActions"))
				{					
				
				}
				else
				{
					CTATComponent newComponent=new CTATComponent ();
					newComponent.fromXML(elem);
					CTATSSELink.components.add(newComponent);
				}	            
			}    			
		}
		else
			debug ("Error: no start state element found");
		
		buildDisplayList ();
	}
	/**
	 * This here is an extremely important method. We will use this to determine if a
	 * component panel needs to be updated or if we can use it as is. If a panel 
	 * doesn't exist already it will be created, nothing special there. If it does
	 * exist then a different test is needed. TBD.
	 */	
	private CTATComponentPanel findComponentPanel (CTATComponent comp)
	{
		debug ("findComponentPanel ()");
		
		int count=componentBox.getComponentCount();
		
		for (int i=0;i<count;i++)
		{
			Object obj=componentBox.getComponent(i);
			
			if (obj instanceof CTATComponentPanel)
			{
				CTATComponentPanel componentPanel=(CTATComponentPanel) obj;
			
				if (componentPanel.getComponent().getInstanceName().equals(comp.getInstanceName())==true)
					return (componentPanel);
			}	
		}
		
		return (null);
	}
	/**
	 * 
	 */
	public void reset ()
	{
		debug ("reset ()");
		
		infoLabel.setText(" Tutor: ");
		CTATSSELink.components=new ArrayList<CTATComponent> ();
		
		componentBox.removeAll();
		SAITree.removeAll();
		SAIByActionTree.removeAll();
		
		this.revalidate();		
	}
	/**
	 * 
	 */
	public void buildDisplayList ()
	{
		debug ("buildDisplayList ()");
							
		for (int i=0;i<CTATSSELink.components.size();i++)
		{
			CTATComponent component=CTATSSELink.components.get(i);
			
			if (component.getClassType().equals("CTATCommShell")==true)
			{
				debug ("Processing tutor info (CommShell)...");
				preview.setTutorWidth((int) component.getWidth());
				preview.setTutorHeight((int) component.getHeight());
				
				zoomInButton.setEnabled(true);
				zoomOutButton.setEnabled(true);
				
				infoLabel.setText(" Tutor: "+ component.getWidth() + "x" + component.getHeight());
			}
			else
			{			
				debug ("Processing regular component: "+component.getClassType()+" ...");
				
				CTATComponentPanel componentPanel=null;
				
				componentPanel=findComponentPanel (component);
				
				if (componentPanel==null)
				{
					// First the basic component information such as instance and type ...
					
					componentPanel=new CTATComponentPanel ();
					componentPanel.setMinimumSize(new Dimension (20,26));
					componentPanel.setPreferredSize(new Dimension (componentBox.getWidth(),componentPanel.getFixedHeight()));
					componentPanel.setMaximumSize(new Dimension (5000,componentPanel.getFixedHeight()));
					componentPanel.setComponent(component);
					componentPanel.setController (controller);
					componentPanel.setPreview(preview);
				
					componentBox.add (componentPanel);
				
					componentPanel.foldIn();
			
					debug ("Fill the SAI panel(s) ...");
			
					DefaultMutableTreeNode compInstance=new DefaultMutableTreeNode(component.getInstanceName ()+" ("+component.getClassType()+")");
		    
					ArrayList<CTATSAI> SAIs=component.getSAIs();
		    
					for (int j=0;j<SAIs.size();j++)
					{
						CTATSAI interfaceAction=(CTATSAI) SAIs.get(j);
											
						StringBuffer interfaceFormat=new StringBuffer ();
						if (interfaceAction.getArgumentSize()>1)
							interfaceFormat.append(interfaceAction.getAction() +" ("+interfaceAction.toArgumentString ()+")");
						else
							interfaceFormat.append(interfaceAction.getAction() +" ("+interfaceAction.getType()+")");
		    	
						debug ("Adding: " + interfaceFormat.toString());
						
						CTATSAITreeNode SAIInstance=new CTATSAITreeNode(interfaceFormat.toString());
						SAIInstance.setSAI(interfaceAction);
						SAIInstance.setComponent (component);
						compInstance.add(SAIInstance);
					}

					SAIRoot.add(compInstance);
					
					for (int j=0;j<SAIs.size();j++)
					{
						CTATSAI interfaceAction=(CTATSAI) SAIs.get(j);
											
						StringBuffer interfaceFormat=new StringBuffer ();
						if (interfaceAction.getArgumentSize()>1)
							interfaceFormat.append(interfaceAction.getAction() +" ("+interfaceAction.toArgumentString ()+")");
						else
							interfaceFormat.append(interfaceAction.getAction() +" ("+interfaceAction.getType()+")");
		    	
						debug ("Adding: " + interfaceFormat.toString());
						
						CTATSAITreeNode SAIInstance=new CTATSAITreeNode(interfaceFormat.toString());
						SAIInstance.setSAI(interfaceAction);
						SAIInstance.setComponent (component);
						SAIRootByAction.add(SAIInstance);
					}
										
					SAITree.updateUI ();
															
					for (int k = 0; k < SAITree.getRowCount(); k++) 
					{
						SAITree.expandRow(k);
					}
					
					for (int l = 0; l < SAITree.getRowCount(); l++) 
					{
						SAIByActionTree.expandRow(l);
					}
				}
			}
			
			//debug (SAIActionModel.toString());					
		}					
		
		this.revalidate();
		preview.repaint();
	}
	/**
	 * 
	 */
	public void problemModelEventOccurred(ProblemModelEvent e) 
	{
		debug ("problemModelEventOccurred ()");
		
		if (e instanceof CTATStartStateEvent)
		{
			CTATStartStateEvent event=(CTATStartStateEvent) e;
		
			if (event.getState().equals(MsgType.INTERFACE_ACTION))
			{
				processStartStateSAI ((CTATSAI) event.getTarget ());
				return;
			}			
			
			if (event.getState().equals(MsgType.INTERFACE_DESCRIPTION))
			{
				buildDisplayList ();
				return;
			}
		
			if (event.getState().equals("NewGraph"))
			{
				reset ();
				return;
			}
			
			if (event.getState().equals("InterfaceConnected"))
			{
				preview.setIsConnected (true);
				reset ();
				return;
			}
			
			if (event.getState().equals("InterfaceDisconnected"))
			{
				//reset ();
				infoLabel.setText(" Tutor: ");
				zoomInButton.setEnabled(false);
				zoomOutButton.setEnabled(false);
		    	//CTATSSELink.components=new ArrayList<CTATComponent> ();
				preview.setIsConnected (false);
				return;
			}			
		}	
	}
	private void processStartStateSAI (CTATSAI anSAI)
	{
		debug ("processStartStateSAI ()");
			
  		debug ("Adding: " + anSAI.getClassName());
  		
  		if (CTATSSELink.SAIModel==null)
  		{
  	        CTATSSELink.SAIModel=new DefaultTableModel (null,columnNames);
  			actionTable.setModel(CTATSSELink.SAIModel);  			
  		}
  		
  		CTATComponent incomingSelection=CTATSSELink.getComponent (anSAI.getName());
  		
		CTATSerializableTableEntry selectionEntry=new CTATSerializableTableEntry (anSAI.getName());
  		selectionEntry.setSAI(anSAI);
  		selectionEntry.setComponent(incomingSelection);
		  
  		CTATSerializableTableEntry actionEntry=new CTATSerializableTableEntry (anSAI.getAction());
  		actionEntry.setSAI(anSAI);
 		actionEntry.setComponent(incomingSelection);
		  
  		CTATSerializableTableEntry valueEntry=new CTATSerializableTableEntry (anSAI.getInput());
  		valueEntry.setSAI(anSAI);
  		valueEntry.setComponent(incomingSelection);    		  
		  
  		CTATSerializableTableEntry[] parameterData={selectionEntry,actionEntry,valueEntry};
		
  		CTATSSELink.SAIModel.addRow (parameterData);
		
  		actionTable.setModel(CTATSSELink.SAIModel);
  	  	
        TableColumn colS = actionTable.getColumnModel().getColumn(2);
        colS.setCellEditor(new CTATSheetCellEditor());			
	}
	/**
	 * 
	 */	
	private void addSAI ()
	{
		debug ("addSAI ()");
		
  	  	if ((actionSelection==null) || (componentSelection==null))
  	  	{
  	  		debug ("Info: nothing selected!");
  	  	}
  	  	else
  	  	{
  	  		debug ("Adding: " + actionSelection.getClassName());
		      		    			
  	  		CTATSerializableTableEntry selectionEntry=new CTATSerializableTableEntry (componentSelection.getInstanceName());
  	  		selectionEntry.setSAI(actionSelection);
  	  		selectionEntry.setComponent(componentSelection);
		  
  	  		CTATSerializableTableEntry actionEntry=new CTATSerializableTableEntry (actionSelection.getAction());
  	  		actionEntry.setSAI(actionSelection);
  	  		actionEntry.setComponent(componentSelection);
		  
  	  		CTATSerializableTableEntry valueEntry=new CTATSerializableTableEntry (actionSelection.getInput());
  	  		valueEntry.setSAI(actionSelection);
  	  		valueEntry.setComponent(componentSelection);    		  
		  
  	  		CTATSerializableTableEntry[] parameterData={selectionEntry,actionEntry,valueEntry};
		
  	  		CTATSSELink.SAIModel.addRow (parameterData);
		
  	  		actionTable.setModel(CTATSSELink.SAIModel);
  	  	}
  	  	
        TableColumn colS = actionTable.getColumnModel().getColumn(2);
        colS.setCellEditor(new CTATSheetCellEditor());	
	}	
	/**
	 * 
	 */
	public void valueChanged(TreeSelectionEvent event) 
	{
		debug ("SAITree: valueChanged ()");
		
/*		
		actionSelection=null;
		componentSelection=null;
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) SAITree.getLastSelectedPathComponent();
		
	    if (node==null)	
	        return;		
				
		if (node instanceof CTATSAITreeNode)
		{
			CTATSAITreeNode tNode=(CTATSAITreeNode) node;
			
			actionSelection=tNode.getSAI();
			componentSelection=tNode.getComponent();
			
			debug ("Selected SAI: " + actionSelection.getClassName());
		}
*/		
	}
	/**
	 * 
	 */	
	public void valueChanged(ListSelectionEvent event) 
	{
		debug ("SAITable: valueChanged ()");
				
	}
	/**
	 * 
	 */	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		debug ("mouseClicked ()");
		
		if (e.getClickCount()==1)
		{			
			debug ("single click ...");
			
			if (e.getSource()==SAITree)
			{							
				actionSelection=null;
				componentSelection=null;
			
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) SAITree.getLastSelectedPathComponent();
			
				if (node==null)	
					return;		
					
				if (node instanceof CTATSAITreeNode)
				{
					CTATSAITreeNode tNode=(CTATSAITreeNode) node;
				
					actionSelection=tNode.getSAI();
					componentSelection=tNode.getComponent();
				
					debug ("Selected SAI: " + actionSelection.getClassName());
				}
			}							
		}
		
		if (e.getClickCount()==2) // Only process double clicks
		{
			debug ("double click ...");
			
			if (e.getSource()==SAITree)
			{
				addSAI ();
			}
			
			if (e.getSource()==SAIByActionTree)
			{
				// Now what to do				
			}			
		}
	}
	/**
	 * 
	 */	
	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */	
	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */	
	@Override
	public void mousePressed(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */	
	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */	
	@Override
	public void componentMoved(ComponentEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */	
	@Override
	public void componentResized(ComponentEvent e) 
	{
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 */	
	@Override
	public void componentHidden(ComponentEvent arg0) 
	{
		debug ("componentHidden ()");
		
	}	
	/**
	 * 
	 */	
	@Override
	public void componentShown(ComponentEvent e) 
	{
		debug ("componentShown ()");
		
	}
}
