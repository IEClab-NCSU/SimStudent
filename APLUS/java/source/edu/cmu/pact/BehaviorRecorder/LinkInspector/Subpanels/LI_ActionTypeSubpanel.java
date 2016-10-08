/**
 * LI_SubpanelInterface.java
 * @author Collin Lynch
 * @date 7/14/2009
 * @copyright 2009 CTAT Project.
 *
 * This file defines the LI_Subpanel abstract class.
 *
 * TODO:: Changing from some to others is odd from buggy to 
 * suboptimal for example.  Need to come back to this.
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels;



import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorException;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.LinkInspectorPanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemNode;
import edu.cmu.pact.BehaviorRecorder.View.ActionLabel;
import edu.cmu.pact.Utilities.trace;


//import org.jgraph.graph.DefaultGraphModel;
//import org.jgraph.graph.GraphLayoutCache;




/**
 * The LI_ActionTypePanel replaces the change action 
 * type popup and the action type display information
 * previously in the header panel.  If clicked the 
 * panel will bring up the change action type as a 
 * popup menu.  
 *
 * This panel will contain a subpanel indicating when
 * the arc is preferred.  This panel will only be 
 * generated when there is more than one arc extending
 * from the source node.
 */
public class LI_ActionTypeSubpanel extends LI_Subpanel {
    
    /* ---------------------------------------------
     * Internal storage.
     * ------------------------------------------ */

    /** Action Label for the system. 
     * Probably should be removed at some point. */
    private ActionLabel CurrActionLabel  = null;

    /* --------------------------------------------
     * Static info.
     * ------------------------------------------*/

    //private static final String INCORRECT_ACTION_NOT_IN_MODEL = "Incorrect Action not in Model (Untraceable Error)";
    private static final String INCORRECT_ACTION_NOT_IN_MODEL = "Incorrect Action not in Model";
    //private static final String SUBOPTIMAL_ACTION = "Suboptimal Action (Fireable Bug)";
    private static final String SUBOPTIMAL_ACTION = "Suboptimal Action";
    //private static final String INCORRECT_ACTION = "Incorrect Action (Bug)";
    private static final String INCORRECT_ACTION = "Incorrect Action";
    private static final String CORRECT_ACTION = "Correct Action";
    
    /* Maximum width for this item. */
    private static final int MAX_WIDTH = 300;

    /* Maximum HEIGHT for this item. */
    private static final int MAX_HEIGHT = 60;

    /* Maximum HEIGHT for this item. */
    private static final int SUBPANEL_HEIGHT = 25;

    /* Preferred height for this item. */
    protected int PrefHeight = 0;

    /* Maximum subpanel height. */
    //private static final int MAX_SUBPANEL_HEIGHT = 20;

    /* --------------------------------------------
     * GUI Features.
     * ----------------------------------------- */

    /** Label for the panel header itself. */
    private JLabel PanelLabel = null;

    /** List for the Action Type. */
    private JComboBox TypeComboBox = null;

    /** Index of the selected Combo Box Type for backup. */
    private int TypeComboLastSelected = -1;

    /** Flag indicating whether changes of selection should
     * be "Responded to" or not.  Initially false but will
     * be set to true on occasion. */
    private boolean HandleSelChanges = false;

    /** Subpanel used to store the Type Choice. */
    private JPanel TypeSubpanel = null;


    private JLabel ButtonLabel = null;
    /** Label for the action pref. */
    private JLabel PrefLabel = null;

    /** Label used to indicate the status. */
    private JLabel PrefStatus = null;

    /** Button used to set pref status. */
    private JButton PrefSetButton = null;

    private JButton DeleteLinkButton = null;
    /** Subpanel used to store the pref. */
    private JPanel PrefSubpanel = null;
    private JPanel ButtonSubpanel = null;
	private JButton DemonstrateLinkButton;

	private JButton CancelDemonstrateLinkButton;

	private JButton InsertNodeAboveButton;

	private JButton InsertNodeBelowButton;

    
    
    /* --------------------------------------------
     * Constructor.
     * ------------------------------------------*/

    /**
     * @param Edge: The problem Edge for this panel.
     *
     * Construct a new ActionTypePanel that will be 
     * used to display the action type for viewing.
     *
     * This actually contains two subpanels.  The top
     * panel is used to handle the 
     */
    public LI_ActionTypeSubpanel(BR_Controller Controller, 
				 ProblemEdge Edge, int CurrWidth,LinkInspectorPanel Panel ) 
	throws LinkInspectorException {
	
	if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Generating Panel");

	/* --------------------------------------
	 * Set the supervening edge, controller
	 * and the ActionLAbel. */
	this.Panel= Panel;
	this.setEdge(Edge);
	this.setController(Controller);
	this.CurrActionLabel = this.CurrEdgeData.getActionLabel();
	if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Setting Edge ActionLabel");


	/* --------------------------------------
	 * Set overall look and feel info. Notably setting the 
	 * initial size.  This will be updated if any other 
	 * subpanels are added. */
	this.setName("LI_ActionTypeSubpanel");
	//this.PrefHeight = SUBPANEL_HEIGHT;
	//lojas//this.setMaximumSize(new Dimension(CurrWidth, this.PrefHeight));
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setBackground(Color.WHITE);
	this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));


	
	// Dimension Pref
	//    = new Dimension(LinkInspectorPanel.PreferredWidth, PrefHeight);
	 
	// this.setMaximumSize(new Dimension(CurrWidth, MAX_HEIGHT));
	// this.setSizePreferences(20);

	/* ---------------------------------------
	 * Set the subpanels. */
	//this.generateButtonPanel(LinkInspectorPanel.PreferredWidth);
	this.generateTypeSubpanel(LinkInspectorPanel.PreferredWidth);
	this.generatePrefSubpanel(LinkInspectorPanel.PreferredWidth);
	this.generateButtonPanel(LinkInspectorPanel.PreferredWidth);
	this.setPreferredSize(new Dimension(CurrWidth, PrefHeight));
	/* ---------------------------------------
	 * Add the relavent listeners. */
	this.addMouseListener(this);
	//revalidate();
	this.repaint();
	this.updateDimensions(LinkInspectorPanel.PreferredWidth);
	if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Loc:" + this.getLocation());
    }



   




	/**
     * When the Edge Data is updated this code will
     * cause the contents of the panel to be reset. In 
     * this case it is the type display that is at 
     * issue.
     *
     * In this case if the Event is null or it changed 
     * the EdgeData it is necessary to check the 
     * Edge Data type.  This code will do so causing 
     * the selection to be updated.
     *
     * At the same time if, it is necessary to support 
     * Updates to the Preferred status this will 
     * add a line indicating whether this is preferred
     * and offering the chance to change it.
     *
     * @param Ev:  The Edge updated event or null.
     */
    public void refreshData(EdgeEvent Ev) {
	
    	if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Update Edge ActionLabel.");
    	//if ((Ev == null) || (Ev.edgeDataChangedP())) {
	    this.setTypeComboBoxSelected();

	    /* If no pref panel exists try making one.
	     * If it does try removing it. */
	    if (this.PrefSubpanel == null) { 
		this.generatePrefSubpanel(this.getWidth());
	    }
	    else { this.testClearPrefSubPanel(); }
	    //}
    }
	    
    

    
    /* ---------------------------------------------
     * Type Panel Label methods.
     * -------------------------------------------*/

    /**
     * Generate the panel used to store the type info 
     * and to display the action type.  This will be 
     * listed first and will add both the type model 
     * and type change panel to the overall panel.
     */
    private void generateTypeSubpanel(int CurrWidth) {

		if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", 
			    "Generating TypeSubpanel:" + CurrWidth);
		
	
		/* -------------------------------------------------
		 * Generate the type subpanel and set its look and feel.*/
		JPanel TPanel = new JPanel();
		Dimension Pref = new Dimension(CurrWidth, SUBPANEL_HEIGHT);
		if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "" + Pref);
		this.updateHeightPref(this.PrefHeight + this.SUBPANEL_HEIGHT +5);
		TPanel.setPreferredSize(new Dimension(CurrWidth, SUBPANEL_HEIGHT));
		TPanel.setMinimumSize(new Dimension(0, SUBPANEL_HEIGHT));
		//TPanel.setMaximumSize(new Dimension(1000, SUBPANEL_HEIGHT));
		TPanel.setLayout(new BoxLayout(TPanel, BoxLayout.X_AXIS));
		TPanel.setBackground(Color.WHITE);
	
		/* Now add the TPanel. */
		this.add(TPanel);
		if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "A" + TPanel.getSize());
	
	
		/* --------------------------------------------------
		 * Generate the Panel Label and add it. */
		JLabel PanelLabel = new JLabel("Action Type: ");
		TPanel.add(PanelLabel);
		if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "B" + TPanel.getSize());
		
		
		/* ----------------------------------------------------
		 * This list contains the static items used for the 
		 * chooser.  These were taken from the menu items 
		 * used in the ActionLabelHandler. */
		String[] Types = { CORRECT_ACTION,
				   INCORRECT_ACTION,
				   SUBOPTIMAL_ACTION,
				   INCORRECT_ACTION_NOT_IN_MODEL };
	
	
		/* --------------------------------------------
		 * Generate the JComboBox and add it to the 
		 * existing list. */
		this.TypeComboBox = new JComboBox(Types);
		this.TypeComboBox.setName("LI_ActionTypeComboBox");
	
		int W = (CurrWidth - PanelLabel.getWidth());
		if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Setting ComboWidth:" + W 
			    + " " + PanelLabel.getSize());
		//this.TypeComboBox.setPreferredSize(new Dimension(W, SUBPANEL_HEIGHT));
		//this.TypeComboBox.setPreferredSize(new Dimension(1000, 40));
		this.TypeComboBox.setMaximumSize(new Dimension(1000, SUBPANEL_HEIGHT));
		//TPanel.setPreferredSize(new Dimension(LinkInspectorPanel.PreferredWidth, 20));
		TPanel.add(this.TypeComboBox);
		if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "C" + TPanel.getSize());
		
	
		/* --------------------------------------------
		 * In the event of an action event this will handle 
		 * pass a change onto the handleTypeChangeEvent. */
		this.TypeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
			    handleTypeChangeEvent();
			}});
		/* ---------------------------------------------
		 * Now set the currently selected edge item. */
		this.setTypeComboBoxSelected();
    }


    /** Set the type selected in the ComboBox. */
    private void setTypeComboBoxSelected() {

	String ActionType = this.CurrEdgeData.getActionType();

	if (ActionType.equalsIgnoreCase(EdgeData.CORRECT_ACTION)) {
		this.TypeComboLastSelected = 0;
		this.TypeComboBox.setSelectedIndex(0);
	    
	}
	else if (ActionType.equalsIgnoreCase(EdgeData.BUGGY_ACTION)) {
		this.TypeComboLastSelected = 1;
		this.TypeComboBox.setSelectedIndex(1);
	    
	}
	else if (ActionType.equalsIgnoreCase(EdgeData.FIREABLE_BUGGY_ACTION)) {
		   this.TypeComboLastSelected = 2;
		this.TypeComboBox.setSelectedIndex(2);
	 
	}
	else if (ActionType.equalsIgnoreCase(EdgeData.UNTRACEABLE_ERROR)) {
		this.TypeComboLastSelected = 3;
		this.TypeComboBox.setSelectedIndex(3);
	    
	}

	/* Now activate the panel so selection changes are
	 * responded to. */
	this.HandleSelChanges = true;
	 
    }


    /* ----------------------------------------------
     * Pref Panel Label.
     * --------------------------------------------*/
 
    /**
     * Generate the preferences panel indicating whether or not
     * this is the preferred arc and, if possible, offering a 
     * button to change that.  This code will generate the 
     * items leaving the update to the next method.
     *
     * The panel will only be generated if there is more than
     * one outgoing edge AND if the edge isn't buggy (since buggy
     * edges cannot be preferred).
     */
    private void generatePrefSubpanel(int CurrWidth) {

	JPanel PPanel;
	if(CurrEdge.isBuggy())
		return;
	/* Check whether there exists more than one neighboring edge.
	 * And in that event create this subpanel else don't. */
	if (this.hasNeighbors()) {
	    
	    if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Generating Pref Subpanel.");
	    
	    /* Allocate room for the panel by resetting the desired 
	     * size.  This simply changes the PrefHeight and queues
	     * a delayed update of the dimensions.  */
	    this.updateHeightPref(this.PrefHeight + this.SUBPANEL_HEIGHT +5);
	    	    	    
	    /* Generate the type subpanel and set its look and feel.*/
	    PPanel = new JPanel();
	    PPanel.setLayout(new BoxLayout(PPanel, BoxLayout.X_AXIS));
	    PPanel.setBackground(Color.WHITE);
	    Dimension Pref = new Dimension(CurrWidth, SUBPANEL_HEIGHT);
	    PPanel.setPreferredSize(Pref);
	    PPanel.setMinimumSize(new Dimension(0, SUBPANEL_HEIGHT));
	    //PPanel.setMaximumSize(Pref);
	    this.PrefSubpanel = PPanel;
	    
	    
	    /* Add the LJabel used to store the pref status. */
	    this.PrefLabel = new JLabel("");
	    this.PrefLabel.setName("LI_PrefLabel");
	    PPanel.add(this.PrefLabel);
	    
	    /* Now add the button but make it invisible. 
	     * If setting this arc as preferred is not 
	     * an option. */
	    JButton NewButton = new JButton("Set");
	    int TmpWidth = CurrWidth - this.PrefLabel.getWidth();
	    Pref = new Dimension(TmpWidth, SUBPANEL_HEIGHT - 5);
	    //lojasNewButton.setPreferredSize(Pref);
	    //NewButton.setMaximumSize(Pref);
	    NewButton.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent E) {
		    	CurrEdge.getLinkEditFunctions().setPreferredArc(Panel.getDefaultColor());
		    }});
	    this.PrefSetButton = NewButton;
	    PPanel.add(NewButton);
	    
	   
	    
	    /* Now update the status. */
	    this.updatePrefPanel();
	    this.add(PPanel);
	}
    }

    //lojas: demo button on multiple linkinspectors.... hmmmm
    private void demonstrateButtonPressed(){
    	if(CurrEdge.getController().getCtatModeModel().isDemonstrateThisLinkMode()){
    		CurrEdge.getLinkEditFunctions().processCancelDemonstrateLink();
    		this.DemonstrateLinkButton.setText("Demonstrate");
    	}else{
    		
    		boolean result = CurrEdge.getLinkEditFunctions().processDemonstrateLink();
    		if(result)
    			this.DemonstrateLinkButton.setText("Cancel");
    	}
    }
    
    /* ---------------------------------------------
     * Edge Updates.
     * -------------------------------------------*/

    private void generateButtonPanel(int preferredWidth) {
		// TODO Auto-generated method stub
    	JPanel PPanel;
    	
    	    /* Allocate room for the panel by resetting the desired 
    	     * size.  This simply changes the PrefHeight and queues
    	     * a delayed update of the dimensions.  */
    	this.updateHeightPref(this.PrefHeight + this.SUBPANEL_HEIGHT +5);
    	    	    	    
    	    /* Generate the type subpanel and set its look and feel.*/
    	PPanel = new JPanel();
	    PPanel.setLayout(new BoxLayout(PPanel, BoxLayout.X_AXIS));
	    PPanel.setBackground(Color.WHITE);
	    Dimension Pref = new Dimension(preferredWidth, SUBPANEL_HEIGHT);
	 //   PPanel.setMinimumSize(new Dimension(0, SUBPANEL_HEIGHT));
	   //\lojas PPanel.setPreferredSize(Pref);
	    //PPanel.setMaximumSize(Pref);
	    this.ButtonSubpanel = PPanel;
	    this.add(PPanel);
	    
	    /* Add the LJabel used to store the pref status. */
	    this.ButtonLabel = new JLabel("");
	    PPanel.add(this.ButtonLabel);
	    
	    /* Now add the button but make it invisible. 
	     * If setting this arc as preferred is not 
	     * an option. */
	    JButton NewButton = new JButton("Delete");
		//int TmpWidth = CurrWidth - this.PrefLabel.getWidth();
		//Pref = new Dimension(TmpWidth, SUBPANEL_HEIGHT - 5);
		//NewButton.setPreferredSize(Pref);
		//NewButton.setMaximumSize(Pref);
		NewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
			    	CurrEdge.getLinkEditFunctions().processDeleteSingleEdge(false);
			    }});
		    this.DeleteLinkButton = NewButton;
		PPanel.add(NewButton);
		
		NewButton = new JButton("Demonstrate");
		NewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
				demonstrateButtonPressed();
			}});
		this.DemonstrateLinkButton = NewButton;
		PPanel.add(NewButton);
	
		/*NewButton = new JButton("UnDemo");
		NewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
			    	CurrEdge.getLinkEditFunctions().processCancelDemonstrateLink();
			    }});
		this.CancelDemonstrateLinkButton = NewButton;
		PPanel.add(NewButton);*/

	/*	NewButton = new JButton("InsertBelow");
		NewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
			    	CurrEdge.getLinkEditFunctions().processInsertNodeAbove2(false);
			    }});
		this.InsertNodeAboveButton = NewButton;
		PPanel.add(NewButton);
		
		NewButton = new JButton("InsertAbove");
		NewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent E) {
			    	CurrEdge.getLinkEditFunctions().processInsertNodeAbove2(true);
			    }});
		this.InsertNodeBelowButton = NewButton;
		PPanel.add(NewButton);*/
	    /* Now update the status. */
	    //this.updatePrefPanel();
	}
    
    /** 
     * If the Pref SubPanel exists but the current 
     * edge does not have any neighbors then it may 
     * need to be removed.  This method will be called
     * on any updateData where the panel exists and
     * will test if it needs to be removed.  If so then
     * it will be eliminated.  If not then it will be
     * updated.
     */
    private void testClearPrefSubPanel() {
	
	/* If no neighbors then remove it. */
	if (!this.hasNeighbors()) {
	    this.remove(this.PrefSubpanel);
	    this.updateHeightPref(SUBPANEL_HEIGHT);
	    this.PrefSubpanel = null;
	}
	/* Else update as normal. */
	else { this.updatePrefPanel(); }
    }
    
	
    /**
     * When called for an update this code will set the 
     * text on the preferences panel and will change the 
     * visibility of the button if necessary.  This will 
     * be called on edge updated events and at the 
     * generation of the subpanel.
     *
     * NOTE:: As a caveat this code uses the 
     * isChangePreferredPath() flag from the controller to
     * determine when to set or reset the path preference
     * status.  I am not sure how this code is set or 
     * updated so I did not change that process but in 
     * future it may make sense to replace this code with 
     * a more "clean" version that simply omits the Pref
     * subpanel when it is not needed.
     */
    private void updatePrefPanel() {

	JLabel TmpLabel;
	int LWidth;

	if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Updating Pref Subpanel.");

	/* Constrain the button width by the text. */
	//LWidth = TmpLabel.getWidth();

	/* Determine the current preference status of the 
	 * edge indicating whether or not it is preferred. */
	if (this.CurrEdgeData.isPreferredEdge()) {
	    this.PrefLabel.setText("Preferred Link");
	    this.PrefLabel.setForeground(Color.GREEN.darker().darker());
	}
	else {
	    this.PrefLabel.setText("Not Preferred  ");
	    this.PrefLabel.setForeground(Color.BLACK);
	}
	 
	/* If updates to the preference status are not 
	 * permitted then we need to make the button 
	 * invisible or vice-versa. */
	if (this.Controller.isChangePreferredPath()
	    && this.CurrEdgeData.getActionType()
	    .equalsIgnoreCase(EdgeData.CORRECT_ACTION)
	    && !this.CurrEdgeData.isPreferredEdge()) {
	    
	    this.PrefSetButton.setVisible(true);
	    this.PrefSetButton.setEnabled(true);
	}
	else { 
	    this.PrefSetButton.setVisible(false);
	    this.PrefSetButton.setEnabled(false);
	}
    }
    

    /* -----------------------------------------
     * Access Methods for internal use.
     * ---------------------------------------*/

    /** Get the source node of the parent. */
    private ProblemNode getParentNode() {
	return this.CurrEdge.getSource();
    }

    /** Get the source node of the parent. */
    private ProblemNode getChildNode () {
	return this.CurrEdge.getDest();
    }

    /**
     * In order to determine if the preferences 
     * table is necessary check to see if this
     * node has any neighbors.
     */
    private boolean hasNeighbors() {
	
	ProblemEdge TempEdge = null;
	ProblemNode Src = this.CurrEdge.getSource();
      	Enumeration<ProblemEdge> EdgeList 
	    = this.Controller.getProblemModel().getProblemGraph().getOutgoingEdges(Src);
        while(EdgeList.hasMoreElements()) {
	    TempEdge = EdgeList.nextElement();
	    /* If a different edge is found then yes. */
	    if (TempEdge != this.CurrEdge) { return true; }
	}
	/* If we reach here then only one item existed in 
	 * the enumerator so we return false. */
	return false;
    }


    /* --------------------------------------------
     * Subpanel "Interface" methods.
     * ----------------------------------------- */

    //     /**
    //      * Accept changes to the dimensions. 
    //      *
    //      * @param NewDimensions:  New Dimensions to be used.
    //      */
    //     public void updateDimensions(Dimension NewDimensions) { 
    //     	int Width = (int) NewDimensions.getWidth();
    //     	Dimension Pref = new Dimension(Width, MAX_HEIGHT);
    //     	this.setPreferredSize(Pref);
    //     	this.setMaximumSize(Pref);
    //     }
    
    /**
     * Return the desired height of this panel given
     * the minimum widh.  In this case the size will
     * be the PrefHeight which is set based upon 
     * the number of panels that are presently active.
     *
     * @param WidthLimit:  The Min Width value.
     */
    protected int getMinHeight(int WidthLimit) { 
	return this.PrefHeight;
    }
    
    

    /**
     * Given a desired height set it as the PrefHeight and then 
     * update the dimensions appropriately.  This will call the 
     * updateDimensions method using the current width.
     */
    private void updateHeightPref(int NewHeightPref) {
	this.PrefHeight = NewHeightPref;
	this.updateDimensions(LinkInspectorPanel.PreferredWidth);
    }
	    


	

    
    /* --------------------------------------------
     * Handling Type Change. 
     * ------------------------------------------*/

    /**
     * In the event that a type change or other action 
     * event is signalled on the TypeComboBox this code
     * will test the Combo box for type changes and, if 
     * a change has occurred will signal the appropriate
     * change.  
     */
    public void handleTypeChangeEvent() {
	
	if (trace.getDebugCode("LI_ActionTypeSubpanel")) trace.outNT("LI_ActionTypeSubpanel", "Action Signalled.");
      
	/* If the currently selected index is the same as 
	 * the prior one then do nothing.  Else kick off an
	 * updated event and change the selected item. */
	int CurrSel = this.TypeComboBox.getSelectedIndex();

	/* In the event that last selecte does not match the 
	 * current and it isn't -1, indicating no change yet,
	 * then we need to change. */
	if ((this.HandleSelChanges) 
	    && (this.TypeComboLastSelected != CurrSel)) {
		
	    /* Set the new type. */
	    switch (CurrSel) {
	    case 0: CurrEdge.getLinkEditFunctions().changeActionType(EdgeData.CORRECT_ACTION); break;
	    case 1: CurrEdge.getLinkEditFunctions().changeActionType(EdgeData.BUGGY_ACTION); break;
	    case 2: CurrEdge.getLinkEditFunctions().changeActionType(EdgeData.FIREABLE_BUGGY_ACTION); break;
	    case 3: CurrEdge.getLinkEditFunctions().changeActionType(EdgeData.UNTRACEABLE_ERROR); break;
	    }

	    /* Now store the change. */
	    this.TypeComboLastSelected = CurrSel;
	    this.fireUpdateEvent(false, true);
	}

	/* Finally fire the update event. */
	
    }
}
