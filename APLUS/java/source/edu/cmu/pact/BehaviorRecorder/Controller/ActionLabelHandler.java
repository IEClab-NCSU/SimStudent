package edu.cmu.pact.BehaviorRecorder.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;

import edu.cmu.pact.BehaviorRecorder.Dialogs.EditMinMaxLinkTraversals;
import edu.cmu.pact.BehaviorRecorder.Dialogs.EditStudentInputDialog;
import edu.cmu.pact.BehaviorRecorder.Dialogs.HelpSuccessPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.DialogueSystemInfo;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.BehaviorRecorder.View.NodeView;
import edu.cmu.pact.Log.AuthorActionLog;
import edu.cmu.pact.Utilities.trace;

// Kevin Zhao - Vector Matcher
//				Demonstrate SAI
//				Preserve groups
//				Preserve Hints and add a new one
//				More stuff

/////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 
 */
// ///////////////////////////////////////////////////////////////////////////////////////////////
public class ActionLabelHandler extends MouseInputAdapter
    implements ActionListener, ItemListener {
	public static final String CHANGE_ACTION_TYPE = "Change Action Type";
	public String jDialogResult;
    public static final String SET_AS_PREFERRED_PATH = "Set As Preferred Path";
    public static final String EDIT_HINT_AND_SUCCESS_MESSAGES = "Edit Hint and Success Messages...";
    public static final String SET_CALL_BACK_FUNCTION = "Set Callback Function";
    public static final String EDIT_BUG_MESSAGE = "Edit Bug Message...";
    public static final String INCORRECT_ACTION_NOT_IN_MODEL = "Incorrect Action not in Model (Untraceable Error)";
    public static final String SUBOPTIMAL_ACTION = "Suboptimal Action (Fireable Bug)";
    public static final String INCORRECT_ACTION = "Incorrect Action (Bug)";
    public static final String CORRECT_ACTION = "Correct Action";
    public static final String TEST_PRODUCTION_MODEL = "Test Cognitive Model";

    public static final String ATTACH_DIALOGUE = "Attach Dialogue...";

    public static final String EDIT_STUDENT_INPUT = "Edit Student Input Matching...";
    public static final String REUSEABLE_LINKS = "Set min/max traversals";
	
    public static final String DELETE_EDGE = "Delete...";
    public static final String INSERT_NODE_ABOVE = "Insert a Blank State in Link (Above)";
    public static final String INSERT_NODE_BELOW = "Insert a Blank State in Link (Below)";
	///////////////////////////////////////////////////////////////////////////////////////////////
	
    public static final String DEMONSTRATE_LINK = "Demonstrate this Link";
    public static final String DEMONSTRATE_LINK_DISABLED = "Demonstrate this Link (Disabled in 'Set Start State' mode)";
    public static final String CANCEL_DEMONSTRATE_LINK = "Cancel Demonstrate This Link Mode";
	///////////////////////////////////////////////////////////////////////////////////////////////

    private static final String TEST_TPA = "Test Tutor-Performed Action";
	
    public static final String COPY_LINK = "Copy Link";
    public static final String PASTE_LINK = "Paste Link";
    public static final String PASTE_SPECIAL_LINK = "Paste Link (Special)";
    
    public static final String CLIPBOARD_COPY_LINK = "Copy Link to Clipboard";
    public static final String CLIPBOARD_PASTE_LINK = "Paste Link to Clipboard";
	///////////////////////////////////////////////////////////////////////////////////////////////

    ActionLabel actionLabel;
    LinkEditFunctions functions;
    ProblemEdge problemEdge;

    ProblemNode parentNode;

    ProblemNode childNode;

    NodeView parentVertex;

    NodeView childVertex;

    // used to display Selection, Action, Input values only
    JDialog displayDialog;
    private EdgeData edgeData;
    private transient BR_Controller controller;
    
    /**
     * Enable a calling routine to set transient fields after deserializing.
     * @param controller value for {@link #controller}
     */
    public void restoreTransients(BR_Controller controller) {
    	this.controller = controller;
    }


    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public ActionLabelHandler(ActionLabel actionLabelP, BR_Controller controller) {
		//trace.out("mg", "ActionLabelHandler: edge = " + actionLabelP.getName());
        actionLabel = actionLabelP;
        this.controller = controller;
        this.controller.comeIntoFocus();
        controller.getProblemModel().printSelectedLinks();
        
        displayDialog = new JDialog(controller.getActiveWindow(), "Action Display", true);
        
        edgeData = actionLabelP.getEdge();
        problemEdge = controller.getProblemModel().getEdge(edgeData.getUniqueID());
        parentNode = problemEdge.getNodes()[ProblemEdge.SOURCE];
        childNode = problemEdge.getNodes()[ProblemEdge.DEST];
        parentVertex = parentNode.getNodeView();
        childVertex = childNode.getNodeView();
        functions = problemEdge.getLinkEditFunctions();
    }


    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void mouseReleased(MouseEvent e) {
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void mousePressed(MouseEvent e) {
        evaluatePopup(e, controller, this);
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void itemStateChanged(ItemEvent e) {
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public static void evaluatePopup(MouseEvent e, BR_Controller controller, ActionLabelHandler handler) {
    	trace.out("mg", "ActionLabelHandler (evaluatePopup): HERE");
        controller.getProblemModel().printSelectedLinks();
    	controller.comeIntoFocus();
    	String currentActionType = handler.problemEdge.getActionType();
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setName("actionLabelPopupMenu");
        
        // [Kevin Zhao](kzhao) - Special Case for canceling DemonstrateThisLinkMode
        if (controller.getCtatModeModel().isDemonstrateThisLinkMode()) {
        	JMenuItem menuItem = new JMenuItem(CANCEL_DEMONSTRATE_LINK);
        	popupMenu.add(menuItem);
        	menuItem.addActionListener(handler);
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
        	return;
        }
        ////////////////////////////////////////////////////////////////////////////////////

        JMenuItem menuItem = new JMenuItem(TEST_PRODUCTION_MODEL);
        menuItem.addActionListener(handler);
        popupMenu.add(menuItem);
        if (controller.getCtatModeModel().isExampleTracingMode())
            menuItem.setEnabled(false);

        JMenu submenu = new JMenu(CHANGE_ACTION_TYPE);

        JCheckBoxMenuItem cbMenuItem1 = new JCheckBoxMenuItem(CORRECT_ACTION);
        cbMenuItem1.addActionListener(handler);
        submenu.add(cbMenuItem1);
        
        JCheckBoxMenuItem cbMenuItem2 = new JCheckBoxMenuItem(
                INCORRECT_ACTION);
        cbMenuItem2.addActionListener(handler);
        submenu.add(cbMenuItem2);

        JCheckBoxMenuItem cbMenuItem3 = new JCheckBoxMenuItem(
                SUBOPTIMAL_ACTION);
        cbMenuItem3.addActionListener(handler);
        submenu.add(cbMenuItem3);


        JCheckBoxMenuItem cbMenuItem4 = new JCheckBoxMenuItem(
                INCORRECT_ACTION_NOT_IN_MODEL);
        cbMenuItem4.addActionListener(handler);

        submenu.add(cbMenuItem4);

        //test to see whether it makes sense to change to correct/suboptimal
        if(currentActionType.equalsIgnoreCase(EdgeData.BUGGY_ACTION)||
        		currentActionType.equalsIgnoreCase(EdgeData.UNTRACEABLE_ERROR)){
        	if(handler.childNode.getInDegree()>1){
        		cbMenuItem1.setEnabled(false);
        		cbMenuItem3.setEnabled(false);
        		String toolTipText = "Cannot have a correct and an incorrect link leading to the same state";
        		cbMenuItem1.setToolTipText(toolTipText);
        		cbMenuItem3.setToolTipText(toolTipText);
        	}
        }	
        
        popupMenu.add(submenu);

        if (currentActionType.equalsIgnoreCase(
                EdgeData.CORRECT_ACTION)) {
            menuItem = new JMenuItem(EDIT_HINT_AND_SUCCESS_MESSAGES);
            menuItem.addActionListener(handler);
            popupMenu.add(menuItem);
            cbMenuItem1.setSelected(true);
        } else if (currentActionType.equalsIgnoreCase(
                EdgeData.BUGGY_ACTION)) {
            cbMenuItem2.setSelected(true);
            menuItem = new JMenuItem(EDIT_BUG_MESSAGE);
            menuItem.addActionListener(handler);
            popupMenu.add(menuItem);
        } else if (currentActionType.equalsIgnoreCase(
                EdgeData.FIREABLE_BUGGY_ACTION)) {
            cbMenuItem3.setSelected(true);
            menuItem = new JMenuItem(EDIT_BUG_MESSAGE);
            menuItem.addActionListener(handler);
            popupMenu.add(menuItem);
        } else if (currentActionType.equalsIgnoreCase(
                EdgeData.UNTRACEABLE_ERROR))
            cbMenuItem4.setSelected(true);
        
        
        if(controller.getShowCallbackFn() == true){
        	menuItem = new JMenuItem(SET_CALL_BACK_FUNCTION);
        	popupMenu.add(menuItem);
        	menuItem.addActionListener(handler);
            menuItem.setEnabled(true);
        }
        
        
        menuItem = new JMenuItem(EDIT_STUDENT_INPUT);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);

        menuItem = new JMenuItem(REUSEABLE_LINKS);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);	
		
        if (DialogueSystemInfo.getUseDialogSystem(controller)) {
            JMenuItem dialogueItem = new JMenuItem(ATTACH_DIALOGUE);
            dialogueItem.addActionListener(handler);
            popupMenu.add(dialogueItem);
            dialogueItem.setEnabled(true);
        }

       /* menuItem = new JMenuItem(DELETE_EDGE);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        */
        menuItem = new JMenuItem(DELETE_EDGE);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        //enabled only on non-leaf nodes.. otherwise just use delete edge
        //menuItem.setEnabled(!(handler.problemEdge.getDest().isLeaf()));
        menuItem.setEnabled(true);
        
        menuItem = new JMenuItem(INSERT_NODE_ABOVE);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        
        //
        menuItem = new JMenuItem(INSERT_NODE_BELOW);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        ////////////////////////////////////////////////////////////////////////////////////
        
        // [Kevin Zhao](kzhao) - This should add a menu item for Demonstrating a Link
        if(controller.getCtatModeModel().isDefiningStartState()) {
        	menuItem = new JMenuItem(DEMONSTRATE_LINK_DISABLED);
        	menuItem.setEnabled(false);
        }
        else {
        	menuItem = new JMenuItem(DEMONSTRATE_LINK);
        	menuItem.setEnabled(true);
        }
        menuItem.addActionListener(handler);
        popupMenu.add(menuItem);

    	menuItem = new JMenuItem(TEST_TPA);
    	if(handler.edgeData.isTutorPerformed(null)) {
    		menuItem.setEnabled(true);
    		menuItem.setToolTipText("Show the effect of this step on the student interface.");
    	} else {
    		menuItem.setEnabled(false);
    		menuItem.setToolTipText("This step is not tutor-performed.");
    	}
        menuItem.addActionListener(handler);
        popupMenu.add(menuItem);
        
        int keyMask;
        if ((System.getProperty("os.name").toUpperCase()).startsWith("MAC"))
            keyMask = (ActionEvent.META_MASK | ActionEvent.SHIFT_MASK);
        else
            keyMask = (ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
        
        menuItem = new JMenuItem(COPY_LINK);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,keyMask));
        
        menuItem = new JMenuItem(PASTE_LINK);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,keyMask));
        // disable if you have nothing copied; enable otherwise
        menuItem.setEnabled(EdgeData.getCopyData() != null);
/*        
        menuItem = new JMenuItem(PASTE_SPECIAL_LINK);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,keyMask));
        // disable if you have nothing copied; enable otherwise
        menuItem.setEnabled(EdgeData.getCopyData() != null);
        
        menuItem = new JMenuItem(CLIPBOARD_COPY_LINK);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
        
        menuItem = new JMenuItem(CLIPBOARD_PASTE_LINK);
        popupMenu.add(menuItem);
        menuItem.addActionListener(handler);
*/        
        ///////////////t/////////////////////////////////////////////////////////////////////
        
        
		// CTAT1563: removed obsolete menu item
        
        if (controller.isChangePreferredPath()
                && handler.edgeData.getActionType().equalsIgnoreCase(
                        EdgeData.CORRECT_ACTION)
                && !handler.edgeData.isPreferredEdge())

        {
            JMenuItem prefPathMenuItem = new JMenuItem(SET_AS_PREFERRED_PATH);
            prefPathMenuItem.addActionListener(handler);
            popupMenu.add(prefPathMenuItem);
        }

        if (trace.getDebugCode("LI_HeaderPanel")) trace.outNT("LI_HeaderPanel", "Showing Popup menu.");
        popupMenu.show(e.getComponent(), e.getX(), e.getY());

        
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public void actionPerformed(ActionEvent e) {
		if(trace.getDebugCode("key"))
			trace.out("key","ActionLabelHandler.actionPerformed() source "+e.getSource()+
					", action "+e.getActionCommand());
    	BR_Controller.setCopySubgraphNode(null);
        final String action = e.getActionCommand();
    	final String serializedBeforeEdit = problemEdge.toXMLString();
    	if (trace.getDebugCode("undo"))
    		trace.out("undo", "ActionLabelHandler.actionPerformed("+action+") XML before:\n"+
    				serializedBeforeEdit);

        doLogEvent(action);
        	
        if (action.equals(TEST_PRODUCTION_MODEL)) {
           checkWithProductionSystem();
        } else if (action.equals(EDIT_STUDENT_INPUT)) {
            EditStudentInputDialog.show(edgeData, controller);
        } else if (action.equals(CORRECT_ACTION)
                || action.equals(INCORRECT_ACTION)
                || action.equals(SUBOPTIMAL_ACTION)
                || action.equals(INCORRECT_ACTION_NOT_IN_MODEL)) {

            updateActionType(action);
        } else if (action.equals(EDIT_BUG_MESSAGE)) {
            problemEdge.getLinkEditFunctions().showEditBuggyMsgPanel();
        } else if (action.equals(EDIT_HINT_AND_SUCCESS_MESSAGES)) {
            new HelpSuccessPanel(
                    controller,
                    edgeData, false).setVisible(true);

            actionLabel.updateToolTip();
        } else if (action.equals(SET_CALL_BACK_FUNCTION)){
        	
        	String ans = (String) JOptionPane.showInputDialog(null, "Set the callback function for the swf to execute upon traversing this link",
        												SET_CALL_BACK_FUNCTION,JOptionPane.PLAIN_MESSAGE, null, null, edgeData.getCallbackFn());
        	System.out.println("ans = " + ans);
        	if(ans != null)
        		edgeData.setCallbackFn(ans);
        } else if (action.equals(ATTACH_DIALOGUE)) {
            new DialogueSystemInfoDialog(edgeData.getDialogueSystemInfo());
        }/* else if (action.equals(SHORT)) {
            actionLabel.setClassView(ActionLabel.VIEW_SHORT);
            for(ExampleTracerLink link : controller.getExampleTracerGraph().getLinks()){
        		link.getEdge().getActionLabel().update();
        	}            
        }*/ else if (action.equals(SET_AS_PREFERRED_PATH)) {
            functions.setPreferredArc(null);
        } else if (action.equals(REUSEABLE_LINKS)) {
			new EditMinMaxLinkTraversals(
					controller.getActiveWindow(), 
					REUSEABLE_LINKS, 
					true, 
					this.edgeData, controller);   
        } else if (action.equals(DELETE_EDGE)){ 
        	functions.processDeleteSingleEdge(false);
        	
    		
        // [Kevin Zhao](kzhao) - Adding in INSERT_NODE_ABOVE and INSERT_NODE_BELOW options
        }else if (action.equals(INSERT_NODE_ABOVE)) {
        	functions.processInsertNodeAbove2(true);
        	
        	//Undo checkpoint for Inserting Node Above ID: 1337
        	ActionEvent ae = new ActionEvent(this, 0, INSERT_NODE_ABOVE);
    		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        }
        else if (action.equals(INSERT_NODE_BELOW)) {
        	functions.processInsertNodeAbove2(false);
        	
        	//Undo checkpoint for Inserting Node Below ID: 1337
        	ActionEvent ae = new ActionEvent(this, 0, INSERT_NODE_BELOW);
    		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        }
        ///////////////////////////////////////////////////////////////////////////////////
        
        // [Kevin Zhao] (kzhao) - Adding in a DEMONSTRATE_LINK option and CANCEL_DEMONSTRATE_LINK option
        else if (action.equals(DEMONSTRATE_LINK)) {
        	functions.processDemonstrateLink();
        }
        else if (action.equals(CANCEL_DEMONSTRATE_LINK)) {
        	functions.processCancelDemonstrateLink();
        }
        else if (action.equals(TEST_TPA)) {
        	edgeData.testTPA();
        }
        else if (action.equals(COPY_LINK)) {
        	functions.processCopyLink();
        }
        else if (action.equals(PASTE_LINK)) {
        	functions.processPasteLink();
        	ActionEvent ae = new ActionEvent(this, 0, PASTE_LINK);
    		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        }
        else if (action.equals(PASTE_SPECIAL_LINK)) {
        	functions.processPasteSpecialLink();
        	ActionEvent ae = new ActionEvent(this, 0, PASTE_SPECIAL_LINK);
    		controller.getUndoPacket().getCheckpointAction().actionPerformed(ae);
        }
        else if (action.equals(CLIPBOARD_COPY_LINK)) {
        	functions.processClipboardCopyLink();
        }
        else if (action.equals(CLIPBOARD_PASTE_LINK)) {
        	functions.processClipboardPasteLink();
        }

        String serializedAfterEdit = problemEdge.toXMLString();
        if (!(serializedBeforeEdit.equals(serializedAfterEdit))) {
        	if (trace.getDebugCode("undo"))
        		trace.out("undo", "ActionLabelHandler.actionPerformed("+action+") XML before:\n"+
        				serializedBeforeEdit+"\nXML after:\n"+serializedAfterEdit);
        }        		

 //       controller.fireCtatModeEvent(CtatModeEvent.REPAINT); 
 //       controller.brPanel.repaint();
    }
 


    
    /**
     * @param action
     */
    private void doLogEvent(String action) {
        controller
	    .getLoggingSupport()
	    .authorActionLog(AuthorActionLog.BEHAVIOR_RECORDER, 
			     BR_Controller.MENU_ITEM, 
			     action, "", "");
    }
	

	


    /**
     * @param action
     */
    public void updateActionType(String action) {
        // map action to authorIntent on ActionLabel
        String newActionType = "";

        if (action.equals(CORRECT_ACTION))
            newActionType = EdgeData.CORRECT_ACTION;
        else if (action.equals(INCORRECT_ACTION))
            newActionType = EdgeData.BUGGY_ACTION;
        else if (action.equals(SUBOPTIMAL_ACTION))
            newActionType = EdgeData.FIREABLE_BUGGY_ACTION;
        else
            newActionType = EdgeData.UNTRACEABLE_ERROR;

        functions.changeActionType(newActionType);
    }


    
 

    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    private void checkWithProductionSystem() {

        // we need to send slection and input to LISP for check
    	if (parentNode != controller.getCurrentNode())
    		controller.goToState(parentNode);
        controller.checkWithLispSingle(problemEdge);
    }

 
   



    // ///////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * integration of Dialogue System
     * 
     * @author zzhang
     */
    // ///////////////////////////////////////////////////////////////////////////////////////////////
    public class DialogueSystemInfoDialog extends javax.swing.JDialog 
	implements ActionListener {
		private static final long serialVersionUID = 1L;

		// Components Variables declaration
        private javax.swing.JButton okButton;

        private javax.swing.JButton cancelButton;
		
		private javax.swing.JLabel hintJLabel = new JLabel("Student's Hint Request:");
		private javax.swing.JTextField hintJTextField;
		
		private javax.swing.JLabel successJLabel = new JLabel("Succesful Completion of the Step:");
		private javax.swing.JTextField successJTextField;
		
		private javax.swing.JLabel errorJLabel = new JLabel("Student Error:");
		private javax.swing.JTextField errorJTextField;
		
		private DialogueSystemInfo dialogueSystemInfo;
		
        /** Creates new form Dialogue_System */
        public DialogueSystemInfoDialog(DialogueSystemInfo dialogueSystemInfo) {
            super(controller.getActiveWindow(), true);
			this.dialogueSystemInfo = dialogueSystemInfo;
			
            initComponents();
        }

        private void initComponents() {
    
			hintJTextField = new javax.swing.JTextField();
			successJTextField = new javax.swing.JTextField();
			errorJTextField = new javax.swing.JTextField();
			
            okButton = new javax.swing.JButton();
            cancelButton = new javax.swing.JButton();

            getContentPane().setLayout(null);

            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

			// hint 
			getContentPane().add(hintJLabel);
			hintJLabel.setBounds(30, 30, 150, 20);
			
			getContentPane().add(hintJTextField);
			hintJTextField.setBounds(30, 55, 350, 20);
			
			// success 
			getContentPane().add(successJLabel);
			successJLabel.setBounds(30, 80, 200, 20);
			
			getContentPane().add(successJTextField);
			successJTextField.setBounds(30, 105, 350, 20);
			
			// error 
			getContentPane().add(errorJLabel);
			errorJLabel.setBounds(30, 130, 120, 20);
			
			getContentPane().add(errorJTextField);
			errorJTextField.setBounds(30, 155, 350, 20);
			
			//
            String authorIntent = edgeData.getActionType();
			
			if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
				errorJTextField.setEditable(false);
				
				if (dialogueSystemInfo.isStudent_Hint_Request())
					hintJTextField.setText(dialogueSystemInfo.getStudent_Hint_Request());
				
				if (dialogueSystemInfo.isStep_Successful_Completion())
					successJTextField.setText(dialogueSystemInfo.getStep_Successful_Completion());
			} else {
				hintJTextField.setEditable(false);
				successJTextField.setEditable(false);
				
				if (dialogueSystemInfo.isStep_Student_Error())
					errorJTextField.setText(dialogueSystemInfo.getStep_Student_Error());
			}
				
            okButton.setText("OK");
            getContentPane().add(okButton);
            okButton.setBounds(120, 210, 65, 23);
            okButton.addActionListener(this);

            cancelButton.setText("Cancel");
            getContentPane().add(cancelButton);
            cancelButton.setBounds(240, 210, 65, 23);
            cancelButton.addActionListener(this);

            String title = "Attach Dialogue: ";

            NodeView tempNodeView = problemEdge.getNodes()[ProblemEdge.SOURCE].getNodeView();

            title = title + " from " + tempNodeView.getText();

            tempNodeView = problemEdge.getNodes()[ProblemEdge.DEST].getNodeView();
            title = title + " to " + tempNodeView.getText();
            this.setTitle(title);

            addWindowListener(new java.awt.event.WindowAdapter() {

                public void windowClosing(java.awt.event.WindowEvent e) {
                    thisWindowClosing();
                }
            });

            setLocation(new java.awt.Point(300, 100));

            setSize(430, 300);

            setResizable(false);

            setVisible(true);
        }

        // ///////////////////////////////////////////////////////////////////////////////
        /**
         * 
         */
        // ///////////////////////////////////////////////////////////////////////////////
        public void thisWindowClosing() {
            setVisible(false);
            dispose();
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == okButton) {
				String authorIntent = edgeData.getActionType();
				
				if (authorIntent.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
					dialogueSystemInfo.setStudent_Hint_Request(hintJTextField.getText().trim());
					dialogueSystemInfo.setStep_Successful_Completion(successJTextField.getText().trim());
				} else
					dialogueSystemInfo.setStep_Student_Error(errorJTextField.getText().trim());
            }
	            
			thisWindowClosing();
        }
    }

    /**
     * Handle a double-click on the label by acting as if the user chose {@value #EDIT_STUDENT_INPUT}.
     * @param e event from click
     * @param controller
     * @param actionHandler
     */
	public static void doubleClick(MouseEvent e, BR_Controller controller,
			ActionLabelHandler actionHandler) {
		EditStudentInputDialog.show(actionHandler.edgeData, controller);		
	}
}
