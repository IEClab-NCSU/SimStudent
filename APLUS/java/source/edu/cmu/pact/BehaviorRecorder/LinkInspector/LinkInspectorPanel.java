/********************************************************
 * LinkInspectorPanel.java
 * @author Collin Lynch
 * @date 06/15/2009
 * 
 * This file implements the root LinkInspector Panel.
 * It should be added to the list of data at runtime.
 *
 * NOTE:: On minimizing this is binned and blanked.
 *
 * Copy over action label handler into here for menu and 
 * other commands making a temporary menu panel.
 */
package edu.cmu.pact.BehaviorRecorder.LinkInspector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.Controller.LinkEditFunctions;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels.LI_ActionTypeSubpanel;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels.LI_HintSubpanel;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels.LI_MessageSubpanel;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels.LI_SAISubpanel;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels.LI_Subpanel;
import edu.cmu.pact.BehaviorRecorder.LinkInspector.Subpanels.LI_TraversalSubpanel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeColorEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeDeletedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeRewiredEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.EdgeUpdatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NodeCreatedEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.EdgeData;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.Utilities.trace;




/**
 * LinkInspectorPanel
 *
 * The LinkInspectorPanel is a container component that
 * handles display of the LinkInspector information for
 * a given arc.  The actual display and activity work
 * is done by subpanels which are informed of changes 
 * to the current arc by this container.  This panel is 
 * what will be used to form the underlying view.
 * <br>
 * One open issue here is the problem of activity.  Some 
 * of the panels will need to be informed when the user
 * selects a different item so as to deactivate the 
 * currently active panel.  This may need to be handled
 * by a listener here for deactivations of this panel on
 * being hidden, and for the subpanels.
 * <br>
 * All classes necessary for the subpanels are defined 
 * in the Subpanels subpackage.  Some of these panels 
 * will be instantiated when the Panel is created.  
 * others will be created but their contents will vary
 * based upon the status of the link e.g. whether it has
 * hints or buggy messages.  
 * <br>
 * 
 * Internally this code includes a single large Scrollpane
 * which will contain the subpanels.  This is done just
 * to make the view of an appropriate size.  The challenge
 * is to handle layout.  This has been largely accomplished
 * but with some fudging here and there.
 * <br>
 *
 * This code includes support for a postponed repaint.  This
 * repaints the panel and all subpanels but only after 
 * postponing the repaint call.
 * <br>
 *
 * TODO:  In its present form this does not support the 
 *   systemDialogue input that was previously employed.
 *   that access needs to be added.  Most likely as a 
 *   separate optional subpanel.
 */
public class LinkInspectorPanel extends JPanel 
    implements MouseListener {


    /* ------------------------------------------------------
     * Static items. 
     * ----------------------------------------------------*/

    /** Static preferred width. */
    public static int PreferredWidth = 100;
    
    /** Static preferred Height. */
    public static int PreferredHeight = 500;
	
	
    /* ------------------------------------------------------
     * Member Fields.
     * ---------------------------------------------------- */
    
    /** Manager class used for ID generation and others. */
    private LinkInspectorManager MyManager = null;

    /** Currently active BRController class. */
    private BR_Controller Controller = null;

    /** Container class for the panel. */
    //private LinkInspectorPanelContainer MyContainer = null;
    
    /**
     *Identifier used mostly for uniqueness.
     * This is not set when constructed but is assigned 
     * by the manager when the panel is added.  Until then
     * it has no unique ID.
     */
    private long Identifier = -1;
    
    /** The problem edge being used. */
    private ProblemEdge MyEdge = null;

    private LinkEditFunctions functions;
    /** 
     * boolean indicating editability o the panel.  If 
     * true then the panel cannot be edited if false it 
     * can.
     */
    private boolean Locked = false;


    /* ----------------------------------------------------
     * Display Labels.
     * --------------------------------------------------*/

    /** Jlabel for status. */
    private JLabel StatusLabel = null;

    /** Jlabel for status. */
    //private JLabel ActiveLabel = null;

    /** Jlabel for width. */
    //private JLabel WidthLabel = null;
    
    /** The Current Dimensions of this panel. */
    private Dimension CurrentDimensions = this.getSize();


    /* ------------------------------------------------------
     * Subpanels.
     * --------------------------------------------------- */
    
    /** 
     * The JPanel that will be used to store all the underlying 
     * Subpanels. */
    private JPanel DisplayPanel = null;
	private LI_ActionTypeSubpanel actionTypeSubpanel;
	private LI_SAISubpanel SAISubpanel;
	private LI_TraversalSubpanel traversalSubpanel;
	private LI_HintSubpanel hintSubpanel;
	private LI_MessageSubpanel messageSubpanel;

    /** 
     * This Scrollpane will act as a view for the DisplayPanel
     * which in turn contains the relavent items. */
    private JScrollPane DisplayScrollPane = null;

    
    /** ArrayList containing the Subpanels. */
    private ArrayList<LI_Subpanel> Subpanels = null;

    ///** Header panel with temp menu. */
    //private LI_HeaderPanel HeaderPanel = null;


    private Color defaultColor;
    public Color getDefaultColor(){
    	return defaultColor;
    }
    
    /* ------------------------------------------------------
     * Constructors.
     * --------------------------------------------------- */

    /**
     * LinkInspectorPanel
     *
     * Produces a new blank panel for the manager.  The panel itself 
     * is empty.  When a new node is selected for this item a storage
     * panel will be produced containing all of the subpanels that 
     * will be instantiated with the containing items.
     *
     * @param Manager   The manager for which the panel is being made.
     */
    LinkInspectorPanel(LinkInspectorManager Manager, BR_Controller Controller, Color color) {
	if (trace.getDebugCode("linkinspector")) trace.outNT("linkinspector", "Generating Blank Panel.");
	this.defaultColor = color;
	/* -----------------------------------
	 * Set internal storage.            */
	this.MyManager = Manager;
	this.Controller = Controller;
	this.MyEdge = null;

	/* -----------------------------------
	 * Define the look and feel of this 
	 * panel including its title and size. */
	this.setBackground(Color.WHITE);
	Dimension PD = new Dimension(LinkInspectorPanel.PreferredWidth, 
				     LinkInspectorPanel.PreferredHeight);
	this.setPreferredSize(PD);
        //this.setBounds(500, 500, 100, 100);
	//this.setSize(100, 100);
	this.setName("LinkInspector");
	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	this.setBorder(BorderFactory.createLineBorder(color,3));

	/* -----------------------------------------------
	 * Now add the display components to the panel. */
	this.StatusLabel = new JLabel("Status Empty.");
	this.StatusLabel.setName("LI_StatusLabel");
	this.add(this.StatusLabel);
	
	/* This display code was added for debugging
	 * and is present only for when it is needed again. */
	/* this.ActiveLabel = new JLabel("");
	   this.add(this.ActiveLabel);
	   // Set the width label
	   Dimension D = this.getSize();
	   this.WidthLabel = new JLabel("Width: " 
	   + D.getWidth() 
	   + " Height: " 
	   + D.getHeight());
	   this.add(this.WidthLabel);
	*/

	
	/* ---------------------------------
	 * Add the necessary Listeners.
	 */
	this.addMouseListener(this);
	//this.addFocusListener(this);
	//this.addPropertyChangeListener(this);


	/* ---------------------------------
	 * Set the current look and feel. */
	//this.makeBlankLook();
	this.clearEdge();
	super.repaint();
	

	if (trace.getDebugCode("linkinspector")) trace.outNT("linkinspector", "Done. Generating Blank Panel.");		
    }
    
    public void restoreEdgeColor(){
    	if(MyEdge!=null && MyEdge.getEdgeData() != null) {
    		EdgeColorEvent evt = new EdgeColorEvent(this,MyEdge, MyEdge.getEdgeData().getDefaultColor());
    		Controller.getProblemModel().fireProblemModelEvent(evt);
    	}
    }
    
    
    /* -------------------------------------------------
     * Accessors/Settors.
     * ---------------------------------------------- */

    /** Retreive the current identifier. */
    long getIdentifier() {

	return Identifier;
    }

    /** Set the current Identifier value. */
    void setIdentifier(long NewID) {
	this.Identifier = NewID;
    }

    
    /** Retreive the currently stored dimensions. */
    Dimension getDimensions() {
	return this.CurrentDimensions;
    }
     

    /* ---------------------------------------------
     * Lock/Unlock the panel for edits.
     * ------------------------------------------ */
    
    void lock() { 
	this.Locked = true; 
	this.StatusLabel.setText("Locked");
    }

    void unlock() {
	this.Locked = false;
	this.StatusLabel.setText("Unlocked");
    }
    
    
    /* ---------------------------------------------
     * Look and Layout and Edges.
     *
     * The methods in this section deal with the 
     * look of the panel and its view.  
     * ------------------------------------------ */

    /**
     * It is necessary to notify the user when this panel 
     * becomes active.  This code will do so by changing 
     * the title informatively.
     */
    // Commented as display not needed presently.
    void makePrimary() { 
	//this.ActiveLabel.setText("active"); 
    }

    
    /**
     * It is necessary to notify the user when this panel 
     * becomes inactive.  This code will do so by changing 
     * the title informatively.
     */
    void makeSecondary() { 
	//this.ActiveLabel.setText(""); 
    }
	


    /**
     * It is necessary to notify the user when this panel 
     * becomes binned.  This code will do so by changing 
     * the title informatively.
     */
    void makeBinned() { 
	//this.ActiveLabel.setText("binned"); 
	this.clearEdge();
    }

    /** 
     * This code is called as needed when an edge is removed from 
     * the panel.  If no edge has been set then this will do nothing.
     * If an edge has been set it will be cleared as will all of the 
     * subpanels and a repaint will occur.
     */
    void clearEdge() {

	if (trace.getDebugCode("linkinspector")) trace.outNT("linkinspector", "Setting blank look and feel.");

    	if (this.MyEdge != null) {

	    // Clear the edge itself.
	    this.MyEdge = null;

	    // Remove the display scroll pane.
	    this.remove(this.DisplayScrollPane);

	    // If any of the subpanels have been defined clear them 
	    // iteratively and clear the display info.
	    for (LI_Subpanel Subp : this.Subpanels) { Subp.clearEdge(); }
	    this.Subpanels = null;
		actionTypeSubpanel = null;
		SAISubpanel= null;
		traversalSubpanel= null;
		hintSubpanel= null;
		messageSubpanel= null;
	    // Now set the Display Panel and Scroll panel to null.
	    this.DisplayPanel = null;
	    this.DisplayScrollPane = null;
	
	    // Force a repaint.
	    this.repaintLater();
	}
    }

    void updateStatusLabel(EdgeData data){
    	String edgeText;
    	ProblemEdge edge = data.getEdge();
    	if (data == null) { edgeText = "Link: "; }
    	else { edgeText 
    		= "Link #" 
    		+ String.valueOf(data.getUniqueID()) 
    		+ ": "; }
    	edgeText 
    	    = edgeText 
    	    + "(" + edge.getSource() 
    	    + "->" + edge.getDest() + ")";
    	this.StatusLabel.setText(edgeText);
    }

    /**
     * setEdge
     * 
     * param Edge:  The problem edge to be set.
     *
     * Set the problem edge for the panel and at the same time
     * reset the look and feel of the panel to include
     * the edge data.  This will add the DisplayPanel and
     * the viewable scroll panel to display it.  
     *
     * Now for simplcity's sake this code will simply clear 
     * the edge if one is already in play and then set the 
     * new one.  
     */
    void setEdge(ProblemEdge Edge) throws LinkInspectorException {
		if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Setting blank look and feel.");
		BoxLayout Layout;
		boolean Status;
		int Height = 0;
		//Color.
		//this.setBorder(BorderFactory.createLineBorder(color));
		EdgeData Data = Edge.getEdgeData();
		//String EdgeText = null;
		LI_Subpanel Sub = null;
		int Width = this.getWidth() - 20;
	
		/*if(this.MyEdge == Edge){
			return;
		}*/
		/* --------------------------------------------
		 * If necessary clear the edge. */
		if (this.MyEdge != null) { 
			restoreEdgeColor();
			this.clearEdge(); 
		}
		
		/* --------------------------------------------
		 * Set the edge appropriately. */
		this.MyEdge = Edge;
		functions = Edge.getLinkEditFunctions();
		//functions = new LinkEditFunctions(MyEdge, Controller);
	
		/* --------------------------------------------
		 * Generate the edge string. */
		updateStatusLabel(Data);
		/* -----------------------------------
		 * Now generate the storage panel that will 
		 * contain all of the subitems.  */
		this.DisplayPanel = new JPanel();
		this.DisplayPanel.setName("LinkInspector:DisplayPanel");
		//this.DisplayPanel.setAlignmentX(LEFT_ALIGNMENT);
		Layout = new BoxLayout(this.DisplayPanel, BoxLayout.Y_AXIS);
		this.DisplayPanel.setLayout(Layout);
		this.DisplayPanel.addMouseListener(this);
	
	
		/* ---------------------------------
		 * Initialize the Subpanels. */
		this.Subpanels = new ArrayList(1);
		
		/* Add the Action Type Panel. */
		actionTypeSubpanel = new LI_ActionTypeSubpanel(this.Controller, Edge, Width, this);
		Height += this.addSubpanel(actionTypeSubpanel);
		
		/* Add the traversal Panel. */
		traversalSubpanel = new LI_TraversalSubpanel(this.Controller, Edge, Width, this);
		Height += this.addSubpanel(traversalSubpanel);
	
		/* Add the SAI Panel. */
		SAISubpanel = new LI_SAISubpanel(this.Controller, Edge, Width, this);
		Height += this.addSubpanel(SAISubpanel);
	
		/* Add the Hint Panel. */
		if(Edge.isCorrectorFireableBuggy()){
			hintSubpanel = new LI_HintSubpanel(this.Controller, Edge, Width, this);
			Height += this.addSubpanel(hintSubpanel);
		}else{
			hintSubpanel =null;
		}
		/* Add the Message Panel. */
		messageSubpanel = new LI_MessageSubpanel(this.Controller, Edge, Width, this);
		Height += this.addSubpanel(messageSubpanel);
		   
		
		///* Add Header Panel. */
	//	Height += this.addSubpanel(new LI_HeaderPanel(this.Controller, Edge));
	
	
	
		/* ----------------------------------
		 * Now set the preferred size for the panel. 
		 * This will be the default width and the sum of the 
		 * heights of each member. */
		if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Setting preferred height: " + Height);
		//this.DisplayPanel.setPreferredSize(new Dimension(100, Height));
	
	
		/* -----------------------------------------
		 * Now generate JScrollPane that will be used to 
		 * contain the subitems and to disply them as needed.
		 * The Height of this scrollpane will be taken from 
		 * the current height of this panel as a whole.
		 */
		this.DisplayScrollPane = new JScrollPane(this.DisplayPanel);
		//Layout = new BoxLayout(this.DisplayScrollPane, BoxLayout.Y_AXIS);
		//this.DisplayScrollPane.setLayout(Layout);
		this.add(this.DisplayScrollPane);
		this.DisplayScrollPane.addMouseListener(this);
		//this.DisplayScrollPane.setPreferredSize(this.CurrentDimensions);
	
		/* -----------------------------------------
		 * Force a redraw. */
		Controller.getProblemModel().fireProblemModelEvent(new EdgeColorEvent(this,MyEdge, defaultColor));
		this.repaintLater();
    }

    
    
    /**
     * Add a subpanel to this panel making the necessary 
     * updates and the listeners.
     *
     * @param Subp:  The LI_Subpanel instance being added. 
     *
     * @return: The Height of the subpanel.
     */
    private int addSubpanel(LI_Subpanel Subp) {
	
	/* Indicate what is occurring. */
	if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", 
		    "Adding new subpanel: " + Subp.getName());

	/* Add the necessary listeners. */
	Subp.addMouseListener(this);
	//Subp.addFocusListener(this);
	//Subp.addMouseListener(this);

	/* Add it to the display panels and Subpanels. */
	this.DisplayPanel.add(Subp);
	this.Subpanels.add(Subp);

	/* Left align the display panel. */
	Subp.setAlignmentX(Component.LEFT_ALIGNMENT);

	/* Then return the height. */
	return Subp.getHeight();
    }




    /**
     * updateContents
     *
     * In the event that the contents of the edge or edgeData is 
     * changed by some external event, this method will trigger
     * an update to all of the necessary subpanels.  The event
     * itself will be passed onto each Subpanel if it proves 
     * necessary.
     *
     * In some sense this can be roundabout as the panels cause 
     * data changes but they will deal with that internally.
     * by recording who changed or not.
     *
     * NOTE:: This code assumes that the edge pointer has not 
     * decayed nor has the EdgeData so that they can be checked
     * again as needed rather than needing to be passed in.
     *
     * @param Ev: An EdgeUpdatedEvent or null.  If null then this 
     *    Will force a complete update. 
     *
     * TODO:: Make update run later along w repaint.
     */
    private void updateContents(EdgeEvent Ev) {
	/* Update the contents of each subpanel.
	 * With the Event as the argument. */
    
    if(Ev instanceof EdgeRewiredEvent){
			this.MyEdge = ((EdgeRewiredEvent)Ev).getEdgeCreatedEvent().getEdge();
			this.updateStatusLabel(MyEdge.getEdgeData());
			//this.Controller.getProblemModel().fireProblemModelEvent(new EdgeColorEvent(this, MyEdge, defaultColor));
	}
    for (LI_Subpanel S : this.Subpanels) { 
		
		S.updateContents(Ev); 
	}
	/* Now queue a repaint for later. */
	this.repaintLater();
    }

     
    
    /** 
     * This method queues a complete repaint of the GUI to 
     * occur later after all of the other GUI events 
     * complete. 
     */
    public void repaintLater() {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() { repaint(); }});
    }
    

    /** Redraw this item. 
     * This method used to override the default repaint method..
     * for some reason however it would clear the edge sometimes...
     * */
    public void repaint2() {
    	
	if (this.DisplayPanel != null) {
	    this.DisplayPanel.repaint();
	    this.DisplayScrollPane.repaint();
	    for (LI_Subpanel S : this.Subpanels) { S.repaint(); }
	}
	super.repaint();
    }





    /* -------------------------------------------
     * Dimensions.
     * ---------------------------------------- */

    /**
     * This queues up a dimension update and a repaint to 
     * occur after all of the other events have taken place
     * this will deal with the repaint tasks as needed.
     */
    public void updateDimensionsLater() {
	SwingUtilities.invokeLater(new Runnable() {
		public void run() { 
		    updateDimensions();
		    repaint(); 
		}});
    }


    /**
     * Update the dimensions through a call to 
     * @ref{#updateDimensionsI} which will then 
     * perform the inner call.
     *
     * @return true if the dimensions have changed.
     */
    private boolean updateDimensions() {
	return this.updateDimensionsI(true);
    }
     

    /**
     * When the dimensions might have been changed it
     * is sometimes necessary to kick off a resizing 
     * of the subcomponents.  This code will do that 
     * by testing whether the immediate dimensions 
     * differ from the stored dimensions and, if so
     * will send out a change.  
     *
     * This is a somewhat finicky beast but the purpose
     * is to have the panels all reduced to the relevant
     * size necessary to make the panels fit horizontally
     * in the LinkInspector leaving the expansions to 
     * be only lengthy.  
     *
     * With that in mind this code uses the size of this 
     * panel (which should be a function of the view) to
     * set the width of the scrollpane and other 
     * subcomponents such as the Display Panel. The 
     * Display panel will be sized to fit within the 
     * DisplayScrollPane by reducing its size by the size of 
     * the vertical scroll bar if it is present.
     *
     * When called this will use the specified flag to control
     * repeats.  It is necessary to cycle once to handle
     * changes in the scrollbar appearenc but we don't want
     * to do it more than once.  
     *
     * @param AllowRecur: if true this can repeat once.
     *
     * @return true if the dimensions have changed.
     */
    private boolean updateDimensionsI(boolean AllowRecur) {

	/*  Get the current size. */
	Dimension NewDimensions = this.getSize();
	/* Before setting the preferred size we need to remove the 
	 * dimensions due to the Status, Active, and Width labels
	 * in order to ensure that the panel does not grow too
	 * tall.  */
	// Commented for display.
	// int HeightLim = this.StatusLabel.getHeight()
	// 	         + this.ActiveLabel.getHeight() 
	// 	         + this.WidthLabel.getHeight()
	// 	         + 4;
	int HeightLim = this.StatusLabel.getHeight()+ 4;


	/* These values are used for tracking the width limit due to
	 * the vertical scroll bar. */
	JScrollBar VBar;
	boolean VScrollVisible = false;
	int WidthLim = 0;

	/* Storage for the desired dimensions. */
	int TmpInt;
	Dimension PreferredDimensions;

	/* If the new dimensions match the old ones 
	 * then don't bother to change anything. */
	if (NewDimensions != this.CurrentDimensions) {
	
	    /* Else, inform the user that the dimensions
	     * have changed and then calculate the new
	     * size for display. */
	    if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Dimensions Changed");
	    this.CurrentDimensions = NewDimensions;
	    // this.WidthLabel.setText("Width: " 
	    // 		+ NewDimensions.getWidth()
	    // 		+ " Height: " 
	    // 	        + NewDimensions.getHeight());
	    
	    /* If the Subpanels have been defined then we need
	     * to update the ScrollPane and other sub values.  
	     * This will set the ScrollPane to the Current size
	     * as needed.  It will then determine the width of 
	     * the scrollbar, if it is present, and then use that 
	     * to set the size of the subpanels constraining them 
	     * to be within the width of the bar.  
	     * 
	     * One issue of course is that if the Scrollbar is not
	     * present before the panels are resized it may be 
	     * present after the fact.  This employs a relatively
	     * cheap hack to deal with that by recalculating if 
	     * the status of the bar changes after setting the panels
	     * as they will only grow longer.  
	     */
	    if ((this.DisplayPanel != null) && (this.Subpanels != null)) {

		/* The Preferred size for the scrollpane is the same 
		 * width as the panel and the height minus the Height
		 * limit.  */
		HeightLim = ((int) NewDimensions.getHeight()) - HeightLim;
		WidthLim = ((int) NewDimensions.getWidth()) - 2;
		PreferredDimensions 
		    = new Dimension(WidthLim, HeightLim);
		this.DisplayScrollPane.setPreferredSize(PreferredDimensions);
		//this.DisplayScrollPane.setMaximumSize(PreferredDimensions);
		

		/* This will also calculate the width of the Vertical
		 * ScrollBar if it is visible and use that as a width
		 * limit for the lower-level items. */
		VBar = this.DisplayScrollPane.getVerticalScrollBar();
		if ((VBar == null) && (VBar.isVisible())) {
		    WidthLim = VBar.getWidth() + 10;
		    VScrollVisible = true;
		}
		else { 
		    WidthLim = 10;
		    VScrollVisible = false;
		}

		
		/* For the underlying panels we want to take into 
		 * account the width limit and set the dimensions 
		 * appropriately.*/
		WidthLim = ((int) NewDimensions.getWidth()) - WidthLim;
		PreferredDimensions = new Dimension(WidthLim, HeightLim);
		this.DisplayPanel.setPreferredSize(PreferredDimensions);
		this.DisplayPanel.setMaximumSize(PreferredDimensions);
		
		// This appears to be supurfluous so I have removed it
		// but the code remains for debugging.
		/* Now update the subpanels with the preferred dimensions. 
		 * In this case they only care about the Width limit. */
		//for (LI_Subpanel S : this.Subpanels) {
		//    S.updateDimensions(WidthLim);
		//}
	    
		
		/* OBTAIN THE SCROLL BAR. */
		JScrollBar VScroll = this.DisplayScrollPane.getVerticalScrollBar();
		if (VScroll == null) { 
		    if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Scroll Panel Null");
		}
		else { 
		    if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Scroll Panel Present ");
		    
		    if (VScroll.isVisible()) { 
			if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Scroll Panel Visible");
		    }
		    else { trace.outNT("LI_Panel", "Scroll Panel invisible"); }
		    if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Scroll Panel " 
				+ VScroll.getWidth() + "x" 
				+ VScroll.getHeight()); 
		    if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Scroll Panel D" 
				+ VScroll.getSize().toString());
		}
		
		/* If after all of this the scroll bar visibility has changed 
		 * and recursion is permitted then make a recursive call. */
		VBar = this.DisplayScrollPane.getVerticalScrollBar();
		if (AllowRecur == true) {
		    if ((VBar == null) && (VBar.isVisible())) {
			if (VScrollVisible == false) {
			    if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", 
					"Scroll Changed visible, recursing.");
			    return this.updateDimensionsI(false);
			}
		    }
		    else if (VScrollVisible == false) {
			if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", 
				    "Scroll Changed invisible, recursing.");
			return this.updateDimensionsI(false);
		    }
		    else { return true; }
		}

		/* Tripped in the event that the above 
		 * does not get run. */
		return true;
	    }
	}
	
	return false; 
    }



    
					       
    /* -----------------------------------------------
     * Problem Model Event Handling.
     * ---------------------------------------------*/
    
    /**
     * @param E: The Problem Model Event being handled.
     *
     * When a problem model event is received this code checks
     * for it or any subevents pertaining to this Edge and
     * if one is found it behaves appropriately.  In the event
     * of an EdgeDeleted Event for this edge it clears the 
     * edge.  In the event of an edge updated event it signals
     * the subpanels to refresh their contents.
     * <br>
     *
     * This is not set as a problem model listener because the
     * manager filters out some unneeded events and only passes 
     * on relevant events here.
     * <br>
     *
     * Because this is event processing this code is somewhat 
     * duplicative but the panel only needs to update once so
     * the algorithm will test for all cases but do only one
     * single update.
     */
    void handleProblemModelEvent(ProblemModelEvent Ev) {
	
		if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Handling Problem Model Event.");
	
		List<ProblemModelEvent> SubList;
		ProblemEdge InvolvedEdge;
		int Result;
		boolean Updated = false;
	
		/* First ensure that we have an edge to care about.*/
		if (this.MyEdge != null) {
		    
		    /* In the event that we have a compound event we 
		     * extract all edge related subevents from it and
		     * process them individually.  This code uses
		     * a flag to prevent multiple updates.  It uses 
		     * the handleSingle method for the basic events.
		     */
		    if (Ev.isCompoundEventP()) {
			ArrayList<Class> TypeList = new ArrayList(2);
			TypeList.add(EdgeEvent.class);
			//TypeList.add(NodeCreatedEvent.class);
			SubList = Ev.collectSubevents(null, TypeList, true, true, true);
	
			if (!SubList.isEmpty()) {
			    
			    for (ProblemModelEvent Ep : SubList) {
				Result = this.handleSingleEdgeEvent(Ep, Updated);
				if (Result == -1) { return; }
				else if (Result == 1) { Updated = true; }
			    }
			}
		    }
		    
		    /* If we have not returned by now and if the 
		     * event is an EdgeEvent that is relevant to 
		     * this edge then check for it as a singleton.  
		     * If it is also a delete edge then clear this 
		     * panel and return.  If it is an update then 
		     * handle it individually.
		     *
		     * If, however it affects a peer edge with the 
		     * same source then it is necessary to perform 
		     * an update as that change may have altered who
		     * is preferred.  
		     */
		    if (Ev instanceof EdgeEvent) { 
	
			/* Process the single EdgeEvent separately.  If 
			 * -1 is returned the edge was deleted if 1 it 
			 * was updated else, 0. */
			Result = this.handleSingleEdgeEvent(Ev, Updated);
			if (Result != 0) { return; }
		    } 
		    /* In the event that the user adds a node to the 
		     * graph it is possible that said node will produce
		     * a peer of the currently selected edge.  In that 
		     * event, it may be necessary to update the contents
		     * to reflect a change in the preferred status.  
		     * With that in mind I have a hack here that trips
		     * an update on any NodeCreated event.  This is a 
		     * Bit of a hack but a functional one. */
		    else if ((!Updated) && (Ev instanceof NodeCreatedEvent)) {  
			this.updateContents(null);
			return;
		    }
		}
    }


    /**
     * In the event that a single EdgeEvent is supplied this 
     * will process it checking whether it is of the right type.  
     * The supplied boolean Updated flags whether an update has 
     * already occurred and, if so does not do another one.  
     *
     * @param Ev:  The Edge event being handled.
     * @param Updated:  Flag indicating whether an update has 
     *          taken place already.
     *
     * @returns: -1 if the contents were cleared.
     *            1 if the contents were updated.
     *            0 otherwize.
     */
    private int handleSingleEdgeEvent(ProblemModelEvent Ev, boolean Updated) {
    	/*if (Ev.getSource().equals(this)){
    		System.out.println("Successs maybe");
    		return 0;
    	}*/
    if(Ev.getSource().equals(this))
    	return 0;
	if (Ev instanceof EdgeEvent) {
	    /* Extract the edge for later use. */
	    ProblemEdge Edg = ((EdgeEvent) Ev).getEdge();
	    // if(this.)
	    /* Handle directly affecting edges. */
	    if (Edg.getUniqueID() == this.MyEdge.getUniqueID()) {
			/* If this is an EdgeDeleted event then clear this. */
			if (Ev instanceof EdgeDeletedEvent) {
				if(((EdgeDeletedEvent)Ev).isEdgeBeingRewired())
					return 0;
			    this.clearEdge();
			    return -1;
			}else if (Ev instanceof EdgeCreatedEvent){
				return 0;
			}/* Else if it is an Update event reset. */
			else if (Ev instanceof EdgeRewiredEvent){
				if (Updated) 
					return 0;
				else{
					this.updateContents(((EdgeRewiredEvent) Ev));
					return 1;
				}
			}
			else if (Ev instanceof EdgeUpdatedEvent) {
			    if (Updated) { return 0; }
			    else {
				this.updateContents(((EdgeUpdatedEvent) Ev));
				return 1;
			    }
			}
	    }
	}
	/* In the event that the user adds a node to the 
	 * graph it is possible that said node will produce
	 * a peer of the currently selected edge.  In that 
	 * event, it may be necessary to update the contents
	 * to reflect a change in the preferred status.  
	 * With that in mind I have a hack here that trips
	 * an update on any NodeCreated event.  This is a 
	 * Bit of a hack but a functional one. */
	else if ((!Updated) && (Ev instanceof NodeCreatedEvent)) {  
	    this.updateContents(null);
	    return 1; 
	}

	/* Else no match so return 0. */
	return 0;
    }


    /* -------------------------------------------
     * MouseListener Interface.
     * ---------------------------------------- */
    
    
    /**
     * When the Mouse enters the panel we will need
     * to update the width of the panel in the event
     * that it has changed and then to pass that info
     * on to the subpanels if necessary.  This is 
     * needed to handle the dynamic resizing of 
     * the window components.  
     *
     * This will kick off a resize note to the 
     * subpanels as needed.
     */
    public void mouseEntered(MouseEvent e) { 	
	this.updateDimensionsLater();
    }

    public void mouseClicked(MouseEvent e) { 
	if (trace.getDebugCode("LI_Panel")) trace.outNT("LI_Panel", "Mouse Clicked.");
	this.MyManager.activateThisPanel(this);
	this.repaintLater();
    }

    public void mouseExited(MouseEvent e) { 
	this.updateDimensionsLater();
	//trace.outNT("LI_Panel", "Mouse Exited");
    }
    
    public void mousePressed(MouseEvent e) { 
	//trace.outNT("LI_Panel", "Mouse pressed");
    } 
    
    public void mouseReleased(MouseEvent e) {
	//trace.outNT("LI_Panel", "Mouse released");
    }

}
	
	

