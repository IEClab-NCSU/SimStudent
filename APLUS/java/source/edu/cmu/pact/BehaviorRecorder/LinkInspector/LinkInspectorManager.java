/****************************************
 * LinkInspectorManager.java
 * @author Collin Lynch
 * @date 06/15/2009
 * @copyright CTAT Project.
 * 
 *
 * Dynamic views require a serializer.  This code will 
 * need to be changed to include in the DockManager a
 * dynamic view serializer which will then handle the 
 * dynamic windows.  In this case the serializer would
 * have to handle the objects directly.  Which might 
 * not be worth the effort although a serparate window
 * for the items might be worthwhile.
 * <p>
 * Alternately they would all be empty.  So why serialize
 * at all in that re-opening would be conditioned on 
 * serializing the windows and so no need to save the
 * edge content.  At most we are concerned with the 
 * placement so the serialization can be quite simple
 * specifying the number alone.  
 * <p>
 * TODO:: On initialization make it sync with who is visible
 * and who is not.  Simple test.
 * Locking.
 * <p>
 *
 * NOTE:: As a general, and somewhat perverse, point when 
 * a window is closed the "hidden" event is what is called
 * not the "closeWindow" event.  Which makes some sense 
 * if this was on the view itself but not the same if the 
 * view is separate.  I take this to be a consequence of
 * their implementation as static views.
 *
 * NOTE:: For edge events look in the BehaviorRecorder/View/GraphInspector slot.  
 
 * Now figure out ctrl click.
 *
 * TODO:: Fix loop on reinsert.
 */

// Place it in the LinkInspector package for use.
package edu.cmu.pact.BehaviorRecorder.LinkInspector;

/* Necessary trace features. */

/* Access to the infonode libraries. */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import net.infonode.docking.View;

import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;

import edu.cmu.pact.BehaviorRecorder.Controller.BR_Controller;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.NewProblemEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModel;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelEvent;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.ProblemModelListener;
import edu.cmu.pact.BehaviorRecorder.ProblemModel.Graph.ProblemEdge;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.BR_JGraph;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.BR_JGraphEdge;
import edu.cmu.pact.BehaviorRecorder.jgraphwindow.JGraphPanel;
import edu.cmu.pact.Utilities.CTAT_Controller;
import edu.cmu.pact.Utilities.trace;
import edu.cmu.pact.ctatview.CtatviewException;
import edu.cmu.pact.ctatview.DockManager;

/** 
 * The LinkInspectorManager class manages the existing LinkInspectorPanels
 * handling the task of producing new panels, deleting old ones and 
 * maintaining recency.  When the DockManager is initialized it will
 * initialize a LinkInspectorManager instance and then use it for all
 * calls to the panels themselves.
 * <p>
 *
 * The panels themselves will be initialized at the beginning based upon
 * the static parameter in the LinkInspectorManager.  This can be overridden
 * by the argument specified to the manager itself.  They are initialized
 * at the beginning in order to ensure that the set can be properly 
 * serialized once closed.  
 * <p>
 *
 * At initialization all the panels will be blank and will be hidden from
 * view.  Commands to update the panel will set a specified panel while 
 * calls to hide them will put them away.
  * <p> 
 *
 * By default all panels start by residing in the bin.  Calls to produce 
 * a new panel will bring up new ones from the bin and then bring them 
 * into action.  It is up to the panels to behave accordingly.  
 * <p>
 *
 * Calls to the menu command to make a new panel will go here.
 * In essence the manager is responsible for calling new panels
 * into existence both blank panels and panels tied to a particular
 * edge.  The process of adding those items to the DockManager 
 * which will then handle tasks such as listening for panel events
 * and other items.  The panel registration process will be 
 * distinct from this.
 * <p>
 *
 * The LinkInspectorManager also acts as a listener for window
 * events on the root window.  This is done to ensure that the
 * events such as closing and opening of the active panels are 
 * trapped for processing and for changing of the panels 
 * themselves.  
 * <p>
 *
 * *NOTE::* With respect to the showing/hiding behavior the system is 
 *  slightly perverse.  In that when a window is closed a closingWindow
 *  message is not, apparently, sent through to the WindowListener
 *  on the root window.  Rather a series of events are sent tripping 
 *  the windowHidden and windowRemoved events.  Now the windowRemoved
 *  events are also tripped as is the windowHidden event when the 
 *  window is moved, manipulated, or otherwize not visible.  
 *
 *  Window closure is therefore rather difficult to deal with. 
 *  As a consequence this code will note when something is hidden 
 *  or removed.  
 * <p>
 *
 * More specifically the windowing system often sends events detailing 
 * removal or hiding when an window is hidden or merely moved about
 * on the screen.  Therefore windows will be binned when they are
 * removed, locked only when they are hidden and then unbinned 
 * when they are shown by the system.
 * <p>
 * Need to initialize manager on first load and then go from there.
 * <p>
 *
 * The LinkInspectorManager is a listener for problem model events
 * its primary purpose in listening is to recognize when the edge 
 * is updated so that the currently active panel can be updated.  
 * <p>
 * The currently active panel is the first panel in the active list
 * and will be first in the list to receive a new arc if a new arc 
 * is selected.  
 * <br>
 *
 * Problem Model listener:
 * The LinkInspectorManager acts as a ProblemModel Listener.  When 
 * instantiated it will connect to the problem model and will handle
 * problem model events.  If a new problem event is signalled this 
 * will reregister to the new problem model and will signal all of 
 * of the panels to clear their edge contents.  
 *
 * In the case of other events they will be passed onto the panels 
 * themselves who will sift for events affecting themselves.
 */
public class LinkInspectorManager 
    extends DockingWindowAdapter 
    implements ProblemModelListener, 
	       GraphSelectionListener,
	       GraphModelListener,
	       ComponentListener,
	       MouseListener{


    /** 
     * LinkInspectorPanelContainer class.
     * 
     * This class is used to maintain a singly
     * linked list of the LinkInspectorPanels.  When 
     * constructed they will be used as a pointer for
     * the items.    
     * <p>
     * This inner class also handles the window listening
     * events.  Because the library recommends that you subclass
     * DockingWindowAdapter class rather than implement the 
     * (possibly unstable) DockingWindowListener interface
     * that is employed as part of the code.  
     * <p>
     * This code manages responses to events so when each view is 
     * created it will be linked to the view as a listener.  Then
     * when the events are received it will either propagate 
     * the relevant events to the panel or manager.
     * <p>
     *
     * Due to the quirkiness of the windowing toolkit the response
     * to show/hide events will be somewhat different.  As noted 
     * above the LinkInspectorManager acts as a listener for 
     * show/hide events and will either lock or unlock a container
     * and the attendent panel for editing on each change.  The 
     * system does not sent close events when a stable view is 
     * "closed" and on occasions sends events not to a view but 
     * with reference to a containing window.  The view only appears
     * to get show/hide events directly when it is part of a tabbed 
     * layout.  Thus on a hide event for the panel or its ancsestor
     * it will be locked to prevent accidental edits.  On a shown 
     * for the panel it will be unlocked.
     * <p>
     *
     * I elected not to use the visible status as, perversely, a 
     * closed window still registers as visible under some cases.
     * This is also the reason that the removed event is not used
     * in and of itself because windows are often recorded as
     * "removed" when they are merely moved from one side to 
     * another.  
     * <p>
     *
     * Binning will thus occur when a window is removed but they will
     * be unbinned when shown.  Windows will also be locked when 
     * they are hidden (sometimes in a tab) and then unlocked when 
     * shown again.  
     */
    private class LinkInspectorPanelContainer {
	
	/** -----------------------------------------
	 * Members.
	 * --------------------------------------- */

	/** Storage for the panel itself. */
	private LinkInspectorPanel MyPanel;

	/** Storage for the manager. */
	private LinkInspectorManager MyManager;
	
	/** Storage for the view itself. */
	private View MyView;
	
	/** Reference to the next item. */
	private LinkInspectorPanelContainer Next;
	
	       
	/** ------------------------------------------
	 * Constructors.
	 * ----------------------------------------*/
	

	/** 
	 * LinkInspectorPanelContainer 
	 * <p>
	 * @param NewPanel  The basic panel for the system.
	 * @param NewView   The view for this panel.
	 * @param ViewCount  The View Count for this panel as an ID.
	 * <p>
	 * This constructor just sets the panel and the default next 
	 * panel to NULL.
	 */
	protected LinkInspectorPanelContainer(LinkInspectorPanel NewPanel,
					      LinkInspectorManager Manager,
					      View NewView,
					      long ViewCount) {
	    
	    // Set the basic argument members.
	    this.MyPanel = NewPanel;
	    this.MyManager = Manager;
	    this.MyView = NewView;
	    this.Next = null;

	    // Set the identifier for the panel.
	    NewPanel.setIdentifier(ViewCount);
	}
	
       	
	
	/** ---------------------------------------------
	 * Accessors/Settors
	 * -------------------------------------------*/

	/** Get the panel. */
	private JPanel getPanel() {
	    return this.MyPanel;
	}

	/** Get the View. */
	private View getView() {
	    return this.MyView;
	}
	 
	/** Retrieve the next item. */
	private LinkInspectorPanelContainer getNext() { 
	    return this.Next;
	}
	
	/** Set the next to a specific Container. */
	private void setNext(LinkInspectorPanelContainer NextContainer) {
	    this.Next = NextContainer;
	}
	
	/** Clear the next item setting it null. */
	private void clearNext() {
	    this.Next = null;
	}


	
	/* -----------------------------------------------
	 * Panel Changes and access
	 *
	 * Methods that manipulate or access the panel.
	 * -------------------------------------------- */

	/** Get the identifier from the panel. */
	private long getIdentifier() {
	    return this.MyPanel.getIdentifier();
	}

	/** Set the identifier of the panel. */
	private void setIdentifier(long Identifier) {
	    this.MyPanel.setIdentifier(Identifier);
	}

	/** Clear the edge connected to this Container. */
	private void clearEdge() { 
	    this.MyPanel.clearEdge(); 
	}
	

	/** 
	 * Set the edge for the underlying panel. */
	private boolean setEdge(ProblemEdge Edge) {
	    try { 
		this.MyPanel.setEdge(Edge); 
		this.MyView.repaint();
		return true;
	    }
	    catch (LinkInspectorException E) {
		trace.err("exception "+E+" on setEdge"+Edge+")");
		E.printStackTrace();
		return false;
	    }
	}

	/** Lock the panel from edits. */
	private void lockPanel() {
	    this.MyPanel.lock();
	}

	/** Unlock the panel from edits. */
	private void unlockPanel() {
	    this.MyPanel.unlock();
	}

	
	
	/** Pass ProblemModel Events to the panel. */
	private void handleProblemModelEvent(ProblemModelEvent Ev) {
	    this.MyPanel.handleProblemModelEvent(Ev);
	}


	
	/* -----------------------------------------
	 * Higher level commands to make the panel
	 * primary and secondary.  In furture these
	 * might subsume the lock commands above. 
	 *
	 * But those get called for different reasons.
	 * ---------------------------------------*/

	/** Make the panel a primary panel. */
	private void makePrimary() { 
	    this.MyPanel.makePrimary(); 
	    //this.MyView.setName(this.MyPanel.getName());
	}

	/** Make the panel a secondary panel. */
	private void makeSecondary() { 
	    this.MyPanel.makeSecondary(); 
	    //this.MyView.setName(this.MyPanel.getName());
	}

	/** Make the panel a binned panel. */
	private void makeBinned() { 
	    this.MyPanel.makeBinned(); 
	    //    this.MyView.setName(this.MyPanel.getName());
	}
	



	/* -----------------------------------------
	 * Showing/hiding
	 *
	 * Manipulating the visibility of the panel 
	 * and sending messages.
	 */

	/** 
	 * makeVisible
	 *
	 * Make this container visible to the user.
	 */
	private void makeVisible() {
	    // Send a restore command to the view.
	    this.MyView.restore();
	}
	    
	
	/**
	 * isClosable
	 * <p>
	 * Return true if the view can be closed, meaning it is open
	 * in the parlance of the infonode system.
	 */
	private boolean isClosable() {
	    return this.MyView.isClosable();
	}

	    
	/* -----------------------------------------
	 * Event Notification.
	 * -------------------------------------- */
	
	/**
	 * notifyShown
	 *
	 * @param Window:  The docking window that was hidden.
	 * @return boolean: True if this window is shown.
	 * 
	 * When a window is shown the LinkInspectorManager will
	 * receive a message which is then passed here.  if the 
	 * window in question is this Container's view or an 
	 * ancsestor of it then it will be unlocked.  
	 * <p>
	 * The loop continues iterating after a single item is 
	 * found as a single action may affect multiple panels.
	 */
	boolean notifyShown(DockingWindow Window) {

	    if (this.MyView == Window) {

		if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			    "I'm shown: " + this.MyView.toString());

		this.unlockPanel();
		return true;
	    }
	    else if (Window.isAncestorOf(this.MyView)) {

		if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			    "Ancestor shown: " + this.MyView.toString());

		this.unlockPanel();
		return true;
	    }
	    else { return false; }
	}


	/** 
	 * notifyHidden
	 *
	 * @param Window:  The docking window that was hidden.
	 * @return boolean: true if this window is hidden.
	 *
	 * When a window is hidden a message will be sent to 
	 * all active windows by the LinkInspectorManager.  This 
	 * method will test the window against this container 
	 * and will lock the window and return true if it matches
	 * the view or if the window is an ancsestor to this panel.
	 */
	boolean notifyHidden(DockingWindow Window) {
	    
	    if (this.MyView == Window) {

		if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			    "I'm hidden: " + this.MyView.toString());
		MyPanel.restoreEdgeColor();
		this.lockPanel();
		return true;
	    }
	    else if (Window.isAncestorOf(this.MyView)) {

		if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			    "Ancestor hidden: " + this.MyView.toString());

		this.lockPanel();
		return true;
	    }
	    else { return false; }
	}


	/**
	 * notifyRemoved
	 *
	 * @param Window: The window being removed from the list.
	 * @return boolean: True if this panel or an 
	 *                  ancsestor was removed.
	 *
	 * When a window is removed every active panel is notified
	 * by the LinkInspectorManager.  This code will test if the
	 * removed window was this window or an ancsestor of it and
	 * in either event will lock the panel and return true.
	 */
	boolean notifyRemoved(DockingWindow Window) {

	    if (this.MyView == Window) {

		if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			    "I'm removed: " + this.MyView.toString());

		this.lockPanel();
		return true;
	    }
	    else if (Window.isAncestorOf(this.MyView)) {

		if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			    "Ancestor removed: " + this.MyView.toString());

		this.lockPanel();
		return true;
	    }
	    else { return false; }
	}

	
	/* -------------------------------------------
	 * Status and Output.
	 * ---------------------------------------- */
	
	/** 
	 * displayStatus
	 * <p>
	 * Output status information for this item.
	 * This includes panel and view Status.
	 */
	private void displayStatus() {
	    String ViewStatus = "View Status:\n";
	    //MyView.setToolTipText("");
	    String Title = "Title: " + this.MyView.getTitle() + "\n";
	    String Minimized = "Minimized: " + this.MyView.isMinimized() + "\n";
	    String Maximized = "Maximized: " + this.MyView.isMaximized() + "\n";
	    String Restorable = "Restorabe: " + this.MyView.isRestorable() + "\n";
	    String Closable = "Closable: " + this.MyView.isClosable() + "\n";
	    //String  = "Restorabe: " + this.MyView.isRestorable() + "\n";

	    if (trace.getDebugCode("LI_Container")) trace.outNT("LI_Container", 
			ViewStatus + Title + Minimized 
			+ Maximized + Restorable
			+ Closable);
	}

	/**
	 * toString
	 *
	 * Get a string title for this item. 
	 */
	public String toString() {
	    
	    String VS = "LinkInspectorPanelContainer: " 
		+ this.MyView.getTitle();
	    return VS;
	}
    }
    
    /* -----------------------------------------------------
     * LinkInspectorManager Members.
     * ---------------------------------------------------*/


	private final Color[] colors = {Color.green, Color.ORANGE, Color.blue };
    
    /** The list of active panels to be made. */
    private LinkInspectorPanelContainer ActivePanelList = null;

    /** The list of binned or hidden panels. */
    private LinkInspectorPanelContainer BinnedPanelList = null;
    
    /** Long indicating the number of active panels. */
    private long ActivePanelCount = 0;

    /** Long indicating the number of binned panels. */
    private long BinnedPanelCount = 0;

    /** long indicating the number of panels total. */
    private long NumPanels = 0;
    
    /**
     * Constant value used for the default number of panels. 
     */
    public static long DEFAULT_PANEL_COUNT = 3;

    /** The text for the view menu. */
    private static String VIEWMENU_TEXT = "Link Inspector";

    /** 
     * Link to the Dock Manager used to handle creation
     * of and removal of views from the system.  
     */
    private DockManager CurrentDockManager;

    /** Link to the menu item placed into the view menu. */
    private JMenuItem ViewMenuItem = null;
    

    /**
     * BRController
     * This is access to the Behavior recorder controller 
     * for use in accessing the problem model.  At present
     * it is required to be a BR_Controller but could, in 
     * theory be replaced with a CTAT_Controller.
     */
    private BR_Controller Controller;

    // ///** 
    //      * The Current Problem Model is maintained here.  When 
    //      * the system is loaded this will be set.  On changes 
    //      * to the model through a new one being instantiated or
    //      * other change from the BR_Controller this will need to
    //      * be updated.
    //      */
    //     //private

    /**
     * JGraph
     * The Graph itself.  This will be registered as a listener
     * to it and will note change events.  
     * It may also be necessary to listen to the controller for 
     * graph edit events but hopefully this will suffice. 
     */
    private BR_JGraph JGraph;
    
    /* -----------------------------------------------------
     * Constructor
     * ---------------------------------------------------*/

    /** 
     * LinkInspectorManager
     * <p>
     * @param Manager The current Dock Manager.
     * <p>
     * Construct a new LinkInspectorManager with the 
     * supplied Manager and the default Panel count.
     */
    public LinkInspectorManager(DockManager Manager, 
				CTAT_Controller Controller) 
	throws LinkInspectorException {
	this(-1, Manager, Controller);	
    }
    

    /** 
     * LinkInspectorManager
     *
     * @param Manager     The current Dock Manager.
     * @param PanelCount  The number of panels to produce.
     * <p>
     * Construct a new LinkInspectorManager with the 
     * supplied DockManager as an arg.  This will produce
     * <PanelCount> binned panels and add them to the 
     * list as well as adding a menu item to the view 
     * menu that will show the first panel if none is 
     * visible.  
     */
    public LinkInspectorManager(long PanelCount, DockManager Manager,
				CTAT_Controller Controller) 
	throws LinkInspectorException {
	
	ProblemModel Problem;
	JGraphPanel JGPanel;

	if (trace.getDebugCode("linkinspectorManager")) trace.outNT("linkinspectorManager", "Generating LinkInspectorManager.");
	

	/* Confirm that the CTAT_Controller is a BR_Controller and
	 * if not, raise an exception. */
	if (!(Controller instanceof BR_Controller)) {
	    throw new LinkInspectorException("Non BR_Controller supplied.");
	}
       	/* Else move on as normal installing the controller for access
	 * to the internal componets, initializing the panels and other
	 * necessary changes.*/
	else {

	    // Set the panel count based upon the argument or defaults.
	    if (PanelCount > -1) { this.NumPanels = PanelCount; }
	    else { this.NumPanels = LinkInspectorManager.DEFAULT_PANEL_COUNT; }
	    
	    /* Set the DockManager and controller and add this as 
	     * a listener for changes to the problem model. */
	    this.CurrentDockManager = Manager;
	    this.Controller = (BR_Controller) Controller;
	    Problem = ((BR_Controller) Controller).getProblemModel();
	    Problem.addProblemModelListener(this);
	    /* Now obtain the JGraph and add this as a listener
	     * for graph selection changes. */
	    //JGPanel = this.Controller.getJGraphWindow();
	    JGPanel = this.Controller.getJGraphWindow();
	    //this.JGraph = JGPanel.getJGraph();
	    this.JGraph = JGPanel.getJGraph();
	    JGraph.addMouseListener(this);
	    this.JGraph.addGraphSelectionListener(this);
	    
	    /* Construct the Show Menu event and add it to the 
	     * menu item itself for later use. */
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
			"Generating: Installing ViewMenu Item.");
	    this.ViewMenuItem = new JMenuItem();
	    this.ViewMenuItem.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
				    "ViewMenu action called.");
			viewMenuEvent();
		    }
		});
	    this.CurrentDockManager.addExperimentalViewMenuItem(this.ViewMenuItem);
	    
	    /* Now generate the n binned panels and add them to 
	     * the Binned PanelList. */
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Generating: Adding Panels.");
	    for (int i = 0; i < this.NumPanels; i++) { this.addPanel(i); }
	    
	    /* Update the View Menu text appropriately. */
	    this.ViewMenuItem.setText(VIEWMENU_TEXT + this.BinnedPanelCount);
	    /* Finally display the status for assessment. */
	    this.displayStatus();		
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Generating: Done.");
	}
    }


    /* ----------------------------------------------------
     * Post root-window code
     * 
     * These methods are called once the root window has been
     * instantiated and defined to add listeners and other 
     * items. 
     * -------------------------------------------------- */

    /** 
     * postWindowingUpdate
     *
     * @throws Ctatviewexception:  Exception thrown when additions
     *            to the window listener are wrong.  E.g. when the
     *            window itself does not exist.
     *         @see edu.cmu.pact.ctatview.CtatviewException
     * <p>
     * 
     * Update the LinkInspectorManager after the windowing 
     * update has occrred.  This both updates the view status
     * for the individual paneles and adds this item as a 
     * listener to the root window.
     */
    public void postWindowingUpdate() throws CtatviewException {
	// Add this item as a root window listener.
	this.CurrentDockManager.addListenerToRootWindow(this);
	// Update the status of each view.
	this.checkViewStatus(); 
    }
    

    /** 
     * checkViewStatus
     * <p>
     * Once the system is loaded it is necessary to test 
     * the status of the views to see if they have been 
     * maximized or not.  This is necessary because the 
     * default layout loading occurrs after panel init.
     * <p>
     * This will iterate over the views checking who is 
     * visible and adding them to the visible list.
     */
    private void checkViewStatus() {

	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Checking view status.");
	
	LinkInspectorPanelContainer C = null;
	LinkInspectorPanelContainer Cp = null;
	LinkInspectorPanelContainer A;
	
	// Locate the active panel.
	if (this.ActivePanelList == null) { A = null; }
	else { A = this.getLastActivePanel(); }

	// Check the binned items moving items that are listed as closable
	// meaning they are visible to the active list.  This will do so
	// in a fcfs order placing the panels into the Active List.
	C = this.BinnedPanelList;
	while (C != null) { 
	    //trace.outNT("LI_Manager", "View: " + C.getView().toString());
	    C.displayStatus();

	    // If the status is closable then pop it off the
	    // binned panel list.
	    if (C.isClosable() == true) {

		// Append the end of the Active Panel List.
		if (A == null) { this.ActivePanelList = C; }
		else { A.setNext(C); }
		A = C;

		// Remove C from the Binned Panel List splicing it
		// out of the list.  If we are at the head of the 
		// list then rest the BinnedPanelList and move C
		// down the list.
		C = C.getNext();
		if (Cp == null) { this.BinnedPanelList = C; }
		else { Cp.setNext(C); }
		
		// Display some simple status information.
		if (C == null) { trace.out("End of list"); }
		else { trace.out(C.getView().getTitle()); }
		
		// Clear the next for A.
		A.setNext(null);

		// And lastly update the counts.
		this.ActivePanelCount++;
		this.BinnedPanelCount--;
	    }
	    // Else traverse down the list.
	    else {
		Cp = C;
		C = C.getNext();
	    }
	}

	// Output the status.
	this.displayStatus();
    }



	

    
    /* -----------------------------------------------------
     * Status Display Methods.  
     * ---------------------------------------------------*/
    
    /** Print out the relevant status information. */
    private void displayStatus() {

	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "LinkManager Status");
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
		    "Num Panels:        " + this.NumPanels);
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
		    "Num Active Panels: " + this.ActivePanelCount);
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
		    "Num Binned Panels: " + this.BinnedPanelCount);
    }


    /* ------------------------------------------------------
     * Panel Initialization.
     *
     * Adding new panels to the manager.
     * ----------------------------------------------------*/


    /**
     * addPanel
     * <p>
     * Add a new link inspector panel to the list for use.
     * This will produce the panel itself as a blank item,
     * add it as a view to the windowing system and then 
     * bin it.
     * <p>
     * Panels must be activated later for use.  Note that 
     * one of the issues with seralization is that some 
     * panels start as being visible so this will need to
     * go through the list and make active any of the panels
     * that are visible and then blank them.  
     */
    public void addPanel(int numPanel) {
		numPanel = numPanel%colors.length;
		if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Adding blank panel."); 
	
		// Retreive the DockManager for use.
		DockManager Manager = this.CurrentDockManager;
	
		// Generate the new panel as a blank item.
		LinkInspectorPanel NewPanel 
		    = new LinkInspectorPanel(this, this.Controller, colors[numPanel]);
		//NewPanel.setl
		NewPanel.addComponentListener(this);
		// Add it as a view to the list.
		String ViewName = "Link Inspector (beta): " + this.BinnedPanelCount;
		View NewView = Manager.addDynamicView(NewPanel, ViewName);
		
		// Generate a container for the panel.
		LinkInspectorPanelContainer Container = 
		    new LinkInspectorPanelContainer(NewPanel, this, NewView, numPanel);
	
		// Now add the container to the binned panel list
		// and update the panel.
		this.addToBin(Container);
		//NewView.
		
    }
    

    /* ---------------------------------------------------
     * Panel management.
     *
     * Managing the active/passive status of the panels.
     * -------------------------------------------------*/

    /** Get the last active panel. */
    private LinkInspectorPanelContainer getLastActivePanel() {
	LinkInspectorPanelContainer C = this.ActivePanelList;
	if (C == null) { return null; }
	else { while (C.getNext() != null) { C = C.getNext(); }	}
	return C;
    }


    /** Get the last binned panel. */
    private LinkInspectorPanelContainer getLastBinnedPanel() {
	LinkInspectorPanelContainer C = this.BinnedPanelList;
	if (C == null) { return null; }
	else { while (C.getNext() != null) { C = C.getNext(); }	}
	return C;
    }


    /** 
     * activatePanel
     * 
     * @return boolean:  True if a panel is activated.
     *
     * Activate the first panel in the binned panel list 
     * moving it to the active list.  This can take an 
     * optional Edge to fill the panel.
     */
    private boolean activateBlankPanel() {
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Activating blank panel.");
	return (this.addToActive() != null);
    }




    /** 
     * activatePanel
     * 
     * @param Edge:  The problem edge for which this panel should
     *               be activated.
     * @return boolean: true if an edge is successfully activated.
     *
     * Activate the first panel in the binned panel list 
     * moving it to the active list.  This can take an 
     * optional Edge to fill the panel.
     * 
     * This will also need to make the edge visible.
     */
    private boolean activateEdgePanel(ProblemEdge Edge) {
	// If the transition succeeds then update the panel.
	LinkInspectorPanelContainer C = this.addToActive();
	if (C == null) { return false; }
	else { return C.setEdge(Edge); }
    }
	

    /**
     * addToActive
     *
     * @return LinkInspectorPanelContainer: The container activated
     *           or null if none.
     * <p>
     * Pop the first panel available in the bin list to the active
     * list.  If the list is empty nothing is done and false is 
     * returned.  If it is possible to do so then a panel will be
     * popped off of the binnedPanelList, activated and then 
     * returned.  If not null will be returned.
     */
    private LinkInspectorPanelContainer addToActive() {
	
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Adding active panel.");
	
	// Get the first binned panel.
	LinkInspectorPanelContainer Container = this.BinnedPanelList;

	// If the panel is null then this is a problem
	// notify through a trace and return false.
	if (Container == null) { 
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
			"Warning: no more panels to activate.");
	    return null;
	}
	/* Else move the container, activate the panel and 
	 * deactivate the other later panels.
	 */
	else {
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Moving from Bin.");
	   
	    // Deactivate the current panel.
	    if (this.ActivePanelList != null) {
		this.ActivePanelList.makeSecondary();
	    }
	    
	    // Get the item to be activated.
	    this.BinnedPanelList = Container.getNext();
	    this.BinnedPanelCount--;
	    Container.setNext(this.ActivePanelList);
  	    this.ActivePanelList = Container;
	    this.ActivePanelCount++;
	    Container.makePrimary();

	    // Simple hack to make it viewable.
	    Container.makeVisible();
	    // Display the present status for use.
	    this.displayStatus();	
	    
	    /* At this point if the BinnedPanelCount 
	     * is 0 then we need to grey out the menu 
	     * item so no more selections can take place.
	     */
	    if (this.BinnedPanelCount == 0) {
		this.ViewMenuItem.setEnabled(false);
	    }
	    this.ViewMenuItem.setText(VIEWMENU_TEXT + this.BinnedPanelCount);
	       

	    return Container;
	}
    }      
	    

    /**
     * activateThisPanel
     * 
     * @param Panel:  The link inspector panel to activate.
     *
     * Given a specific Link Inspector panel activate it 
     * (if necessary) and then make it the primary.  This will
     * search through the list of panels as needed.
     *
     * Note, this does not throw an error in the event that 
     * the panel is not found although it should in the 
     * future. 
     */
    void activateThisPanel(LinkInspectorPanel Panel) {
	
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Activating supplied panel.");

	// Check to ensure we don't activate again.
	if (Panel != this.ActivePanelList.getPanel()) {
	    
	    /* Checl the Active Panel list first. */
	    if (this.ActivePanelCount > 0) {
		if (this.chainActivatePanel(Panel, this.ActivePanelList)) { 
		    return; 
		}
	    }
	    /* At this point the panel was not found so 
	     * make the same trip through the Binned panel List.
	     */
	    if (this.BinnedPanelCount > 0) {
		if (this.chainActivatePanel(Panel, this.BinnedPanelList)) {
		    return; 
		}
	    }
	}
    }

    /**
     * chainActivatePanel
     *
     * @param Panel: The Panel in question being activated.
     * @param Parent:  The first item on the list to activate.
     *
     * Chain down the supplied list starting with Parent and,
     * if a container containing the panel is found activate
     * it and return true.  Else return false at the end of
     * the chain. 
     */
    private boolean chainActivatePanel(LinkInspectorPanel Panel,
				       LinkInspectorPanelContainer Parent) {

	LinkInspectorPanelContainer Curr = null;
	LinkInspectorPanelContainer Next = null;

	// Set the current candidate based upon the parent.
	Curr = Parent.getNext();
	// Iterate over the list as a whole until Next is null.
	while (Curr != null) {
	    Next = Curr.getNext();

	    // If the panel matches then activate it.
	    if (Panel == Curr.getPanel()) {
		this.activatePanel(Parent, Curr, Next);
		return true;
	    }
	    else {
		Parent = Curr;
		Curr = Next;
	    }
	}

	// False if we reach here.
	return false;
    }
	


    /** 
     * activatePanel
     *
     * @param Container:  Given a specific container remove 
     *         it from the bin and activate it.
     * 
     * @throws LinkInspectorException: If the panel is not binned.
     *
     * Given a specific panel pop it off of the binned panel 
     * list and activate it.  Here Parent Container and Next
     * are from the binned panel list.  If Parent is null then
     * Container is the first item and it will be popped off.
     * else it is in the middle and it will be spliced out of 
     * the Binned Panel list.  
     */
    private void activatePanel(LinkInspectorPanelContainer Parent,
			       LinkInspectorPanelContainer Container,
			       LinkInspectorPanelContainer Next) {
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Activating from Bin: "
		    + Container.toString());
	
	/* Splice in the necessary panel items. 
	   If parent is null splice in Next. */
	if (Parent == null) { this.BinnedPanelList = Next; }
	else { Parent.setNext(Next); }
	this.BinnedPanelCount--;
	
	/* Now push the panel onto the active list. 
	 * Setting its predecessor as secondary and 
	 * it as primary. */
	if (this.ActivePanelList != null) {
	    this.ActivePanelList.makeSecondary();
	}
	Container.setNext(this.ActivePanelList);
	this.ActivePanelList = Container;
	this.ActivePanelCount++;
	Container.makePrimary();
	
	/* At this point if the BinnedPanelCount 
	 * is 0 then we need to grey out the menu 
	 * item so no more selections can take place.
	 */
	if (this.BinnedPanelCount == 0) {
	    this.ViewMenuItem.setEnabled(false);
	}
	this.ViewMenuItem.setText(VIEWMENU_TEXT + this.BinnedPanelCount);
	
	// Display it.
	this.displayStatus();
    }
    



    /**
     * binPanel
     * <p>
     * @param Panel    The panel to be added to the bin.
     * <p>
     * Given a panel remove it from the active list and
     * add it to the bin list resetting it to blank and 
     * minimizing the view.
     *
     * In the event that Parent null then this is the 
     * first item and will be stored as such.  Else the
     * parent will be used to link the relevant items.
     */
    private void binPanel(LinkInspectorPanelContainer Parent,
			  LinkInspectorPanelContainer Container,
			  LinkInspectorPanelContainer Next) {
	
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Binning active panel: "
		    + Container.toString());
	
	/* If parent is null then set Next to the item. 
	 * Else set the link splice.  At the same time
	 * update the ActivePanelList to signal who is
	 * primary.*/
	if (Parent == null) { this.ActivePanelList = Next; }
	else { Parent.setNext(Next); }
	this.ActivePanelCount--;

	/* Inform the panel of the status change if one exists. */
	if (this.ActivePanelList != null) {
	    this.ActivePanelList.makePrimary();
	}

	/* Store the panel in the binned list. */
	this.addToBin(Container);
    }
    

    /**
     * addToBin
     * <p>
     * @param Container  The container to be added to the bin.
     * <p>
     * Add the specified panel to the bin list making it blank
     * in the process.
     */
    private void addToBin(LinkInspectorPanelContainer Container) {

	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Adding panel to bin.");

	// Set the Panel container.
	Container.setNext(this.BinnedPanelList);
	
	// Push it to the beginning of the list.
	this.BinnedPanelList = Container;
	
	// Make it "binned" for internal status.
	Container.makeBinned();

	// Update the count.
	this.BinnedPanelCount++;

	/* At this point if the JMEnuItem is greyed out we 
	 * need to reactivate it.  If not we update the 
	 * text. */
	if (!this.ViewMenuItem.isEnabled()) {
	    this.ViewMenuItem.setEnabled(true);
	}
	
	this.ViewMenuItem.setText(VIEWMENU_TEXT + this.BinnedPanelCount);

	// Update display.
	this.displayStatus();
    }

    

    
    /* ---------------------------------------------
     * Current Panel mangement.
     *
     * Sending edges to penels and other controls.
     * ------------------------------------------ */

    /** 
     * setActivePanelEdge
     * 
     * @param Edge:  The problem edge for which 
     *         the panel should be set.
     *
     * Set the edge of the currently active panel.
     */
    private boolean setActivePanelEdge(ProblemEdge Edge) {

	if (this.ActivePanelList != null) {
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "setActivePanelEdge.");
	    this.ActivePanelList.setEdge(Edge);
	    //this.CurrentDockManager.repaintRootWindow();
	    return true;
	}
	else { return false; }
    }
	
    


    /* ---------------------------------------------------
     * Basic event methods. 
     *
     * These event methods are used to handle the panel 
     * controls including setting the active panel.
     * -------------------------------------------------*/

    /**
     * ViewMenu Event
     * 
     * When the View Menu event is called this will show a 
     * Window if none is present or do nothing.
     */
    public void viewMenuEvent() {
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "ViewMenu event called.->");
	//if (this.NumPanels == 0) { 
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "ViewMenu generating panel.");
	this.activateBlankPanel();
	//}
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "ViewMenu event called.<-");
    }


    /** 
     * ctrlClick event.
     *
     * Event to spawn a new panel for a window on a ctrl click.
     */

    

    /* -----------------------------------------------
     * Docking Window Adapter Notification Events.
     * -------------------------------------------- */

    /**
     * notifyShown
     *
     * @param Window:  The docking window that was shown.
     * 
     * When a window is shown this code will pass a shown 
     * message to all windows.  If a window is active but
     * locked then it will be unlocked and left as-is.
     * if it is binned then it will be activated and 
     * moved to the front of the list.
     * <p>
     *
     * The loop continues iterating after a single item is 
     * found as a single action may affect multiple panels.
     *
     * NOTE:: It is not necessary to suppress this event for
     *    commands to raise panels as they will already be 
     *    activated before the event is trapped.
     *
     * On a different note, the notifyShown will return 
     * true if the panel is activated and then, if it
     * is binned it will be restored.  
     *
     */
    public void windowShown(DockingWindow Window) {

	boolean Signal;
	LinkInspectorPanelContainer Parent, Container, Next;

	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
		    "Shown Event" + Window.toString());
	this.displayStatus();
	/* First iterate over the active panels merely unlocking
	 * any that are shown and leaving no other change done.
	 */
	Container = this.ActivePanelList;
	while (Container != null) { 
	    if (Container.notifyShown(Window) == true) {
		if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Active Panel Shown.");
	    }
	    Container = Container.getNext();
	}

	/* Now iterate over the binned panels noting any that
	 * are now shown and, if necessary unbinning them.
	 * 
	 * N is maintained to avoid pointer jumping problems.
	 * P i
	 */
	Parent = null;
	Container = this.BinnedPanelList;
	while (Container != null) { 
	
	    /* Set the items. */
	    Next = Container.getNext();

    
	    /* If shown then update the others leaving the 
	     * parent as-is. */
	    if (Container.notifyShown(Window) == true) {
		if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Binned Panel Shown.");
		
		// And activate the panel.
		this.activatePanel(Parent, Container, Next);

		// Move to the next items in the list.
		Container = Next;
	    }
	    /* Else update all three to step down the list. */
	    else {
		Parent = Container;
		Container = Next;
	    }
	}
    }


    /**
     * windowHidden
     *
     * @param Window:  The docking window that was hidden.
     *
     * When a window is hidden this code will pass hide 
     * events to the active windows.  If any window reports
     * itself as being hidden it will lock itself but take
     * no other action.  Hidden windows are not binned as 
     * they are often hidden and then shown later.
     */
    public void windowHidden(DockingWindow Window) {
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Hide Event:" + Window.toString());
	//trace.outNT("LI_Manager", 
	//"ChildCount: " + Window.getChildWindowCount());

	LinkInspectorPanelContainer C = this.ActivePanelList;
	
	while (C != null) { 
	    if (C.notifyHidden(Window) == true) {
		if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Panel Hidden.");
	    }
	    C = C.getNext();
	}
    }


    /**
     * windowRemoved
     *
     * @param Window:  The docking window that was removed.
     * @return boolean: returns true if a panel was binned 
     *           as a result of this move or false if not.  
     * 
     * When a window is removed this code will signal the 
     * active panels with the removed signal.  If a panel 
     * reports that it or an ancsestor of it has been 
     * removed then this will bin said panel.  The signal
     * is based upon the return.  
     * <p>
     *
     * The loop continues iterating after a single item is 
     * found as a single action may affect multiple panels.
     * <p>
     *
     * When the panel is removed or one of its ancsestors is 
     * removed then the panel itself needs to be binned. 
     * in this event the notifyRempoved call will return 
     * true if one of those events occurrs.  In that case
     * the panel will be binned.
     *
     * NOTE:: Under some circumstances a blank window will 
     *   be affected.  This will ignore those null cases.
     */
    public void windowRemoved(DockingWindow removedFromWindow, 
			      DockingWindow removedWindow) {

	// Necessary Variables for maintaining the three pass list.
     	LinkInspectorPanelContainer Parent, Container, Next;

	if (removedWindow != null) {
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
			"From: [" + removedFromWindow.toString() + "] "
			+ "Removed: [" + removedWindow.toString() + "]");
	    
	    /* Maintain the three item list while iterating. */
	    Parent = null;
	    Container = this.ActivePanelList;
	    while (Container  != null) { 
		
		// Get the next item from the list.
		Next = Container.getNext();
		
		/* If this panel is removed then bin it as is. */
		if (Container.notifyRemoved(removedWindow) == true) {
		    this.binPanel(Parent, Container, Next);
		    Container = Next;
		}
		/* Else handle the movement down the list. */
		else {
		    Parent = Container;
		    Container = Next;
		}
	    }
	}
    }

    //public void viewFocusChanged(View Previous, View Current) {
    // 	trace.outNT("LI_Manager", 
    //	    "Refocusing From: " + Previous.toString()
    //	    + "To: " + Current.toString());
    //}
    
    
    //     public void viewClosed(View ClosedWindow) {
    // 	trace.outNT("LI_Manager", 
    // 		    "Closed View: " + ClosedWindow.toString());
    //     }
    
    //     public void viewClosing(View ClosingWindow) {
    // 	trace.outNT("LI_Manager",  
    // 		    "Closing View: " + ClosingWindow.toString());
    //     }
    
    //     public void windowDocked(DockingWindow Window) {
    // 	trace.outNT("LI_Manager", "Docked "  + Window.toString());
    //trace.outNT("LI_Manager", 
    // "ChildCount: " + Window.getChildWindowCount());
    
    // 	DockingWindow W;
    
    // 	if (Window.getChildWindowCount() > 0) {
    // 	    DockingWindow Child = Window.getChildWindow(0);
    // 	    W = Child;
    // 	}
    // 	else { W = Window; }
    
    // 	LinkInspectorPanelContainer C = this.ActivePanelList;
    // 	while (C != null) { 
    // 	    //C.notifyDocked(Window); 
    // 	    C.notifyDocked(W);
    // 	    C = C.getNext();
    // 	}
    //     }
    
    //     public void windowDocking(DockingWindow Window) {
    // 	trace.outNT("LI_Manager", "Docking" + Window.toString());
    // trace.outNT("LI_Manager", 
    // "ChildCount: " + Window.getChildWindowCount());
    //     	LinkInspectorPanelContainer C = this.ActivePanelList;
    // 	while (C != null) { 
    // 	    C.notifyDocking(Window); 
    // 	    C = C.getNext();
    // 	}
    // }
    
    // public void windowMaximized(DockingWindow Window) {
    // 	//trace.outNT("LI_Manager", "Maximized" + Window.toString());
    //     }
    
    //     public void windowMaximizing(DockingWindow Window) {
    // 	//trace.outNT("LI_Manager", "Maximizing" + Window.toString());
    //     }
    
    //     public void windowMinimized(DockingWindow Window) {
    // 	//trace.outNT("LI_Manager", "Minimized" + Window.toString());
    //     }
    
    //     public void windowMinimizing(DockingWindow Window) {
    // 	//trace.outNT("LI_Manager", "Minimizing" + Window.toString());
    // }
    
    
    
    // public void windowRestored(DockingWindow Window) {
    // 	trace.outNT("LI_Manager", "Restored" + Window.toString());
    //     }            
    
    //     public void windowRestoring(DockingWindow Window) {
    // 	trace.outNT("LI_Manager", "Restoring" + Window.toString());
    // }
    
    // public void windowUndocked(DockingWindow Window) {
    // 	trace.outNT("LI_Manager", "Undocked" + Window.toString());
    // trace.outNT("LI_Manager", 
    //"ChildCount: " + Window.getChildWindowCount());
    // 	LinkInspectorPanelContainer C = this.ActivePanelList;
    // 	while (C != null) { 
    // 	    C.notifyUndocked(Window); 
    // 	    C = C.getNext();
    // 	}
    // }
    
    //     public void windowUndocking(DockingWindow Window) {
    // 	trace.outNT("LI_Manager", "Undocking" + Window.toString());
    // 	trace.outNT("LI_Manager", 
    // "ChildCount: " + Window.getChildWindowCount());
    // 	LinkInspectorPanelContainer C = this.ActivePanelList;
    // 	while (C != null) { 
    // 	    C.notifyUndocking(Window); 
    // 	    C = C.getNext();
    // 	}
    // }



    /* --------------------------------------------------
     * ProblemModelEventListener interface methods. 
     * ----------------------------------------------- */

    /**
     * problemModelEventOccrred
     *
     * @param E: The problem Model event itself.
     *
     * TODO::
     * This will need to listen for changes to the model in 
     * the event that a new model is made, a BRD file is 
     * loaded or a currently active arc is removed or 
     * otherwize modified by some external source.  
     */
    public void problemModelEventOccurred(ProblemModelEvent E) {
	
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
		    "ProblemModelEvent: " + E.toString());
	
       
	LinkInspectorPanelContainer C = this.ActivePanelList;


	/* If this is a New Problem Event then clear all of the 
	 * subpanels by signalling them to do so.  */
	if (E instanceof NewProblemEvent) {
	    while (C != null) {
		C.clearEdge();
		C = C.getNext();
	    }
	}
	/* Else for any other edge Events pass them down to 
	 * the underlying active panels. */
	else {
	    while (C != null) {
		C.handleProblemModelEvent(E);
		C = C.getNext();
	    }
	}
    }
    

    
    /* --------------------------------------------------
     * Graph Selection Listener Event Methods. 
     * ----------------------------------------------- */

    /**
     * valueChanged
     *
     * @param SelEvent: The JGraph selection event indicating
     *          what items were selected and deselected by
     *          the action as well as several other parameters.
     *
     * This valueChanged event will identify any newly selected
     * edges in the selection action.  For the items it will 
     * pass a call to the system for panel loading. 
     *
     * For the present we are only interested in edge selection 
     * events, not in node selection events or edge deselection
     * events.  It will also not react on multi-select events.
     */
    public void valueChanged(GraphSelectionEvent SelEvent) {
    if(true)
    	return;
	BR_JGraphEdge JGEdge = null;
	ProblemEdge PEdge = null;
	BR_JGraphEdge SelectionJEdge = null;
	ProblemEdge SelectionPEdge = null;
	//SelEvent.
	boolean SingleAddition = false;

	// Signal the selection process.
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager",
		    "GraphSelEvent: " + SelEvent.toString());
	
	/* Iterate over the arcs getting selected cells.  Test
	 * the cells and set the first one as an added cell.  
	 * if, however more than one added then break as we 
	 * are only interested in single selctions.
	 */
	for (Object SelObj : SelEvent.getCells()) {
	    if (SelObj instanceof BR_JGraphEdge) {
		
		JGEdge = (BR_JGraphEdge) SelObj;
		PEdge = JGEdge.getProblemEdge();
		
		/* If only one is selected or none other yet then
		 * then set the single selection route and move on.
		 */
		if ((SelectionJEdge == null) 
		    && (SelEvent.isAddedCell(SelObj))) {
		    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
				"Single Edge Selected: " + JGEdge.toString() 
				+ "Graph: " + PEdge.toString());

		    SelectionJEdge = JGEdge;
		    SelectionPEdge = PEdge;
		    SingleAddition = true;
		    			
		}
		/* If any other selection occurrs this will signal
		 * the fact than an error has arisen and break.
		 */
		else if ((SingleAddition == true)
			 && (SelEvent.isAddedCell(SelObj))) {
		    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", 
				"Multi Edge Selected: " + JGEdge.toString() 
				+ "Graph: " + PEdge.toString());
		    SingleAddition = false;
		    break;
		}
		/* Else for deselection just report it and 
		 * move on from there.
		 */
		else { 
		    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager",
				"Edge Deselected: " + JGEdge.toString()
				+ "Graph: " + PEdge.toString());
		}
	    }
	}

	/* Having performed the selection now if this is a single 
	 * selection update the active panel.
	 */
	if (SingleAddition == true) { 
	    if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "Setting Edge.");
	    this.setActivePanelEdge(SelectionPEdge);
	}
    }

    /* --------------------------------------------------
     * Graph Model Listener Event Methods. 
     * ----------------------------------------------- */

    /**
     * graphChanged
     * 
     * @param GraphEvent: The graph model event refelcting the change.
     *
     * This event will be fired when the graph itself is changed
     * returning a graph change event that indicates any alteration
     * the graph model.
     */
    public void graphChanged(GraphModelEvent GraphEvent) {
	
	if (trace.getDebugCode("LI_Manager")) trace.outNT("LI_Manager", "GraphChanged: " + GraphEvent.toString());

	//GraphModelEv
    }


	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void componentResized(ComponentEvent arg0) {
		Component temp = arg0.getComponent();
		temp.setPreferredSize(new Dimension(temp.getWidth(),temp.getHeight()));
		temp.validate();
		temp.repaint();
	}


	@Override
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		//arg0.
		if(BR_JGraph.wasRightClick(arg0)){
			return;
		}
		Object cell = JGraph.getFirstCellForLocation(arg0.getX(), arg0.getY());
		if(cell instanceof BR_JGraphEdge){
			this.setActivePanelEdge(((BR_JGraphEdge)cell).getProblemEdge());
		}
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


    /* Constructed as it may be needed to force redraws when
     * the link items are updated.
      public void repaintRootWindow() {
	this.CurrentDockManager.repaintRootWindow();
    }
    */
}	





